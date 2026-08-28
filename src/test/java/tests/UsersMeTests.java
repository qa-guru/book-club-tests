package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import models.login.LoginBodyModel;
import models.registration.RegistrationBodyModel;
import models.users.DetailErrorModel;
import models.users.ProfileValidationErrorModel;
import models.users.UserModel;
import models.users.UserUpdateBodyModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.AUTH_CREDENTIALS_NOT_PROVIDED;
import static tests.TestData.FIELD_MAX_150_ERROR;
import static tests.TestData.FIELD_MAX_254_ERROR;
import static tests.TestData.FIELD_MAY_NOT_BE_BLANK;
import static tests.TestData.FIELD_MAY_NOT_BE_NULL;
import static tests.TestData.FIELD_REQUIRED;
import static tests.TestData.INVALID_TOKEN;
import static tests.TestData.JSON_PARSE_ERROR_PREFIX;
import static tests.TestData.LOGIN_WRONG_CREDENTIALS_ERROR;
import static tests.TestData.METHOD_POST_NOT_ALLOWED;
import static tests.TestData.REGISTRATION_EXISTING_USER_ERROR;
import static tests.TestData.TOKEN_NOT_VALID;
import static tests.TestData.TOKEN_NOT_VALID_CODE;
import static tests.TestData.USERNAME_INVALID_CHARS_ERROR;
import static tests.TestData.USERS_ME_ALLOWED_METHODS;
import static tests.TestData.USER_NOT_FOUND;
import static tests.TestData.USER_NOT_FOUND_CODE;
import static tests.TestData.VALID_EMAIL_ERROR;

@Epic("Пользователи")
@Feature("Профиль пользователя")
@Tag("api")
public class UsersMeTests extends ApiTestBase {

    private static final String SEED_FIRST = "KeepFirst";
    private static final String SEED_LAST = "KeepLast";

    private final List<String> tokensToCleanup = new ArrayList<>();
    private UserSession session;

    @AfterEach
    void deleteCreatedUsers() {
        for (String token : tokensToCleanup) {
            api.users.deleteMeSilently(token);
        }
        tokensToCleanup.clear();
        session = null;
    }

    @Test
    @Tag("positive")
    @Tag("smoke")
    @Description("GET /users/me/: id и username совпадают с регистрацией, имена и email пустые, remoteAddr не пустой.")
    @DisplayName("[API] GET /users/me/ — свой профиль после регистрации")
    @Severity(SeverityLevel.CRITICAL)
    void getMeReturnsOwnEmptyProfile() {
        UserSession user = session();

        UserModel me = api.users.getMe(user.accessToken());

        assertProfile(me, user, user.username(), "", "", "");
    }

    @Test
    @Tag("positive")
    @Description("Два access-токена: каждый GET /users/me/ возвращает id и username своего пользователя, не чужого.")
    @DisplayName("[API] GET /users/me/ — каждый токен видит только себя")
    @Severity(SeverityLevel.CRITICAL)
    void getMeReturnsOnlyTheAuthenticatedUser() {
        UserSession first = newUser();
        UserSession second = newUser();

        UserModel meFirst = api.users.getMe(first.accessToken());
        UserModel meSecond = api.users.getMe(second.accessToken());

        assertThat(meFirst.id()).isEqualTo(first.id()).isNotEqualTo(meSecond.id());
        assertThat(meFirst.username()).isEqualTo(first.username());
        assertThat(meSecond.id()).isEqualTo(second.id());
        assertThat(meSecond.username()).isEqualTo(second.username());
    }

    @Test
    @Tag("positive")
    @Description("Неавторизованный GET: 401, WWW-Authenticate Bearer, Allow = набор методов синглтона.")
    @DisplayName("[API] GET /users/me/ без токена — 401, Allow и WWW-Authenticate")
    @Severity(SeverityLevel.CRITICAL)
    void getMeWithoutTokenExposesAllowAndWwwAuthenticate() {
        Response response = api.users.getMeUnauthorizedResponse();

        assertThat(response.as(DetailErrorModel.class).detail())
                .isEqualTo(AUTH_CREDENTIALS_NOT_PROVIDED);
        assertThat(response.getHeader("WWW-Authenticate")).contains("Bearer");
        assertAllowHeader(response.getHeader("Allow"));
    }

    @Test
    @Tag("positive")
    @Description("HEAD /users/me/ с токеном: 200, тот же Allow, без разбора JSON-тела.")
    @DisplayName("[API] HEAD /users/me/ — 200 и Allow")
    @Severity(SeverityLevel.NORMAL)
    void headMeReturns200AndAllow() {
        Response response = api.users.headMe(session().accessToken());

        assertThat(response.statusCode()).isEqualTo(200);
        assertAllowHeader(response.getHeader("Allow"));
    }

    @Test
    @Tag("negative")
    @Description("HEAD /users/me/ без токена: 401 и Allow. Тело не разбираем — у HEAD его может не быть.")
    @DisplayName("[API] HEAD /users/me/ без токена → 401")
    @Severity(SeverityLevel.NORMAL)
    void headMeWithoutTokenIsUnauthorized() {
        Response response = api.users.headMeUnauthorized();

        assertThat(response.statusCode()).isEqualTo(401);
        assertAllowHeader(response.getHeader("Allow"));
    }

    @Test
    @Tag("positive")
    @Description("PUT со всеми полями: ответ и последующий GET совпадают с отправленным профилем, id не меняется.")
    @DisplayName("[API] PUT /users/me/ — полная замена, проверяем GET")
    @Severity(SeverityLevel.CRITICAL)
    void putMeReplacesFullProfileAndPersists() {
        UserSession user = session();
        String email = emailOf(user);

        UserModel updated = api.users.putMe(user.accessToken(),
                new UserUpdateBodyModel(user.username(), "Alice", "Reader", email));
        UserModel fromGet = api.users.getMe(user.accessToken());

        assertProfile(updated, user, user.username(), "Alice", "Reader", email);
        assertPersistedIgnoringRemoteAddr(updated, fromGet);
    }

    @Test
    @Tag("positive")
    @Description("Повторный PUT с тем же телом: ответы совпадают (кроме remoteAddr), GET читает тот же профиль.")
    @DisplayName("[API] PUT /users/me/ — идемпотентен, проверяем GET")
    @Severity(SeverityLevel.CRITICAL)
    void putMeIsIdempotentAndVisibleViaGet() {
        UserSession user = session();
        UserUpdateBodyModel body = new UserUpdateBodyModel(
                user.username(), "Alice", "Reader", emailOf(user));

        UserModel first = api.users.putMe(user.accessToken(), body);
        UserModel second = api.users.putMe(user.accessToken(), body);
        UserModel fromGet = api.users.getMe(user.accessToken());

        assertPersistedIgnoringRemoteAddr(first, second);
        assertPersistedIgnoringRemoteAddr(first, fromGet);
    }

    @Test
    @Tag("positive")
    @Description("Сначала PUT с заполненным профилем, затем PUT с пустыми именами/email: GET показывает пустые строки.")
    @DisplayName("[API] PUT /users/me/ — очищает имена и email")
    @Severity(SeverityLevel.NORMAL)
    void putMeClearsNamesAndEmailWhenSentEmpty() {
        UserSession user = session();
        api.users.putMe(user.accessToken(),
                new UserUpdateBodyModel(user.username(), "Alice", "Reader", emailOf(user)));

        UserModel cleared = api.users.putMe(user.accessToken(),
                new UserUpdateBodyModel(user.username(), "", "", ""));
        UserModel fromGet = api.users.getMe(user.accessToken());

        assertProfile(cleared, user, user.username(), "", "", "");
        assertPersistedIgnoringRemoteAddr(cleared, fromGet);
    }

    @Test
    @Tag("positive")
    @Description("PUT с лишними id/password/isStaff: PK не меняется, пароль тот же, в JSON ответа только поля User.")
    @DisplayName("[API] PUT /users/me/ — id и password из тела не применяются")
    @Severity(SeverityLevel.NORMAL)
    void putMeIgnoresIdAndPasswordInBody() {
        UserSession user = session();
        String email = emailOf(user);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("username", user.username());
        body.put("firstName", "X");
        body.put("lastName", "Y");
        body.put("email", email);
        body.put("id", 1);
        body.put("password", "hacked");
        body.put("isStaff", true);

        api.users.putMe(user.accessToken(), body);

        UserModel fromGet = api.users.getMe(user.accessToken());
        assertProfile(fromGet, user, user.username(), "X", "Y", email);
        assertThat(api.users.getMeJson(user.accessToken()).getMap(".").keySet())
                .containsExactlyInAnyOrder("id", "username", "firstName", "lastName", "email", "remoteAddr");

        var login = api.auth.login(new LoginBodyModel(user.username(), user.password()));
        assertThat(login.access()).isNotBlank();
        tokensToCleanup.add(login.access());

        var hackedLogin = api.auth.loginWrongCredentials(
                new LoginBodyModel(user.username(), "hacked"));
        assertThat(hackedLogin.detail()).isEqualTo(LOGIN_WRONG_CREDENTIALS_ERROR);
    }

    @ParameterizedTest(name = "[API] PUT /users/me/ — без поля {0} → 400")
    @ValueSource(strings = {"username", "firstName", "lastName", "email"})
    @Tag("negative")
    @Description("PUT без одного обязательного поля: 400 только по этому полю, required.")
    @Severity(SeverityLevel.NORMAL)
    void putMeRejectsWhenSingleRequiredFieldMissing(String omitted) {
        UserSession user = session();
        Map<String, String> body = fullProfileMap(user, "A", "B", emailOf(user));
        body.remove(omitted);

        ProfileValidationErrorModel error = api.users.putMeInvalid(user.accessToken(), body);

        assertThat(errorsOn(error, omitted)).containsExactly(FIELD_REQUIRED);
        for (String field : List.of("username", "firstName", "lastName", "email")) {
            if (!field.equals(omitted)) {
                assertThat(errorsOn(error, field)).isNull();
            }
        }
    }

    @Test
    @Tag("negative")
    @Description("PUT с пустым объектом: 400, required по всем четырём полям.")
    @DisplayName("[API] PUT /users/me/ — пустое тело отклоняется")
    @Severity(SeverityLevel.NORMAL)
    void putMeRejectsEmptyBody() {
        ProfileValidationErrorModel error = api.users.putMeInvalid(session().accessToken(), Map.of());

        assertThat(error.username()).containsExactly(FIELD_REQUIRED);
        assertThat(error.firstName()).containsExactly(FIELD_REQUIRED);
        assertThat(error.lastName()).containsExactly(FIELD_REQUIRED);
        assertThat(error.email()).containsExactly(FIELD_REQUIRED);
    }

    @Test
    @Tag("negative")
    @Description("PUT с невалидным email: 400, ошибка по полю email.")
    @DisplayName("[API] PUT /users/me/ — невалидный email")
    @Severity(SeverityLevel.NORMAL)
    void putMeRejectsInvalidEmail() {
        UserSession user = session();
        ProfileValidationErrorModel error = api.users.putMeInvalid(user.accessToken(),
                new UserUpdateBodyModel(user.username(), "A", "B", "not-an-email"));

        assertThat(error.email()).containsExactly(VALID_EMAIL_ERROR);
    }

    @Test
    @Tag("negative")
    @Description("PUT с username другого пользователя: 400, already exists.")
    @DisplayName("[API] PUT /users/me/ — занятый username")
    @Severity(SeverityLevel.NORMAL)
    void putMeRejectsDuplicateUsername() {
        UserSession user = session();
        UserSession other = newUser();

        ProfileValidationErrorModel error = api.users.putMeInvalid(user.accessToken(),
                new UserUpdateBodyModel(other.username(), "A", "B", emailOf(user)));

        assertThat(error.username()).containsExactly(REGISTRATION_EXISTING_USER_ERROR);
    }

    @Test
    @Tag("negative")
    @Description("PUT с пробелом и ! в username: 400, сообщение о допустимых символах.")
    @DisplayName("[API] PUT /users/me/ — недопустимые символы в username")
    @Severity(SeverityLevel.NORMAL)
    void putMeRejectsInvalidUsernameCharacters() {
        UserSession user = session();

        ProfileValidationErrorModel error = api.users.putMeInvalid(user.accessToken(),
                new UserUpdateBodyModel("bad user!", "A", "B", emailOf(user)));

        assertThat(error.username()).containsExactly(USERNAME_INVALID_CHARS_ERROR);
    }

    @ParameterizedTest(name = "[API] PUT /users/me/ — {0} длиннее 150 → 400")
    @MethodSource("fieldsLongerThan150")
    @Tag("negative")
    @Description("PUT: username/firstName/lastName длиннее 150 символов — 400, max length.")
    @Severity(SeverityLevel.NORMAL)
    void putMeRejectsFieldsLongerThan150(String field, String tooLongValue) {
        UserSession user = session();
        Map<String, String> body = fullProfileMap(user, "A", "B", emailOf(user));
        body.put(field, tooLongValue);

        ProfileValidationErrorModel error = api.users.putMeInvalid(user.accessToken(), body);

        assertThat(errorsOn(error, field)).containsExactly(FIELD_MAX_150_ERROR);
    }

    @ParameterizedTest(name = "[API] PUT /users/me/ — {0} ровно 150 символов → 200 и GET")
    @ValueSource(strings = {"firstName", "lastName"})
    @Tag("positive")
    @Description("Граница 150: PUT firstName или lastName длины 150 сохраняется, GET совпадает.")
    @Severity(SeverityLevel.NORMAL)
    void putMeAcceptsNameOfExactly150(String field) {
        UserSession user = session();
        String value = "A".repeat(150);
        Map<String, String> body = fullProfileMap(user, "A", "B", emailOf(user));
        body.put(field, value);

        api.users.putMe(user.accessToken(), body);
        UserModel fromGet = api.users.getMe(user.accessToken());

        assertThat(field.equals("firstName") ? fromGet.firstName() : fromGet.lastName()).isEqualTo(value);
        assertThat(fromGet.username()).isEqualTo(user.username());
    }

    @Test
    @Tag("negative")
    @Description("PUT email длины 255: 400, max 254.")
    @DisplayName("[API] PUT /users/me/ — email длиннее 254")
    @Severity(SeverityLevel.NORMAL)
    void putMeRejectsEmailLongerThan254() {
        UserSession user = session();
        String email = "a".repeat(243) + "@example.com";
        assertThat(email).hasSize(255);

        ProfileValidationErrorModel error = api.users.putMeInvalid(user.accessToken(),
                new UserUpdateBodyModel(user.username(), "A", "B", email));

        assertThat(error.email()).containsExactly(FIELD_MAX_254_ERROR);
    }

    @Test
    @Tag("positive")
    @Description("Граница 254: PUT email длины 254 проходит, GET возвращает тот же email.")
    @DisplayName("[API] PUT /users/me/ — email ровно 254")
    @Severity(SeverityLevel.NORMAL)
    void putMeAcceptsEmailOfExactly254() {
        UserSession user = session();
        String email = "a".repeat(242) + "@example.com";
        assertThat(email).hasSize(254);

        api.users.putMe(user.accessToken(),
                new UserUpdateBodyModel(user.username(), "A", "B", email));

        assertThat(api.users.getMe(user.accessToken()).email()).isEqualTo(email);
    }

    @Test
    @Tag("negative")
    @Description("PUT с пустым username: 400, may not be blank.")
    @DisplayName("[API] PUT /users/me/ — пустой username")
    @Severity(SeverityLevel.NORMAL)
    void putMeRejectsBlankUsername() {
        UserSession user = session();

        ProfileValidationErrorModel error = api.users.putMeInvalid(user.accessToken(),
                new UserUpdateBodyModel("", "A", "B", emailOf(user)));

        assertThat(error.username()).containsExactly(FIELD_MAY_NOT_BE_BLANK);
    }

    @Test
    @Tag("negative")
    @Description("PUT с обрезанным JSON: 400, detail начинается с JSON parse error.")
    @DisplayName("[API] PUT /users/me/ — сломанный JSON")
    @Severity(SeverityLevel.NORMAL)
    void putMeRejectsMalformedJson() {
        var error = api.users.putMeMalformedJson(session().accessToken());

        assertThat(error.detail()).startsWith(JSON_PARSE_ERROR_PREFIX);
    }

    @ParameterizedTest(name = "[API] PATCH /users/me/ — только {0}, проверяем GET")
    @CsvSource({
            "firstName, Patched",
            "lastName, OnlyLast",
            "email, patched@example.com"
    })
    @Tag("positive")
    @Description("PATCH одного поля после заполненного PUT: ответ и GET меняют только его.")
    @Severity(SeverityLevel.CRITICAL)
    void patchMeUpdatesSingleFieldAndPersistsViaGet(String field, String newValue) {
        UserSession user = session();
        String email = emailOf(user);
        api.users.putMe(user.accessToken(),
                new UserUpdateBodyModel(user.username(), SEED_FIRST, SEED_LAST, email));

        UserModel patched = api.users.patchMe(user.accessToken(), Map.of(field, newValue));
        UserModel fromGet = api.users.getMe(user.accessToken());

        String expectedFirst = "firstName".equals(field) ? newValue : SEED_FIRST;
        String expectedLast = "lastName".equals(field) ? newValue : SEED_LAST;
        String expectedEmail = "email".equals(field) ? newValue : email;

        assertProfile(patched, user, user.username(), expectedFirst, expectedLast, expectedEmail);
        assertPersistedIgnoringRemoteAddr(patched, fromGet);
    }

    @Test
    @Tag("positive")
    @Description("PATCH {}: ответ и GET совпадают с профилем до запроса (кроме remoteAddr).")
    @DisplayName("[API] PATCH /users/me/ — пустой объект ничего не меняет, проверяем GET")
    @Severity(SeverityLevel.NORMAL)
    void patchMeEmptyObjectIsNoOpAndPersists() {
        UserSession user = session();
        UserModel before = api.users.putMe(user.accessToken(),
                new UserUpdateBodyModel(user.username(), "Patched", "Y", emailOf(user)));

        UserModel patched = api.users.patchMe(user.accessToken(), Map.of());
        UserModel fromGet = api.users.getMe(user.accessToken());

        assertPersistedIgnoringRemoteAddr(before, patched);
        assertPersistedIgnoringRemoteAddr(before, fromGet);
    }

    @Test
    @Tag("positive")
    @Description("PATCH username: GET показывает новое имя, логин с новым проходит, со старым — нет.")
    @DisplayName("[API] PATCH /users/me/ — смена username, проверяем GET и логин")
    @Severity(SeverityLevel.CRITICAL)
    void patchMeUsernameAllowsLoginWithNewName() {
        UserSession user = session();
        String newUsername = user.username() + "z";

        api.users.patchMe(user.accessToken(), Map.of("username", newUsername));

        assertThat(api.users.getMe(user.accessToken()).username()).isEqualTo(newUsername);

        var login = api.auth.login(new LoginBodyModel(newUsername, user.password()));
        assertThat(login.access()).isNotBlank();
        tokensToCleanup.add(login.access());

        var oldLogin = api.auth.loginWrongCredentials(
                new LoginBodyModel(user.username(), user.password()));
        assertThat(oldLogin.detail()).isEqualTo(LOGIN_WRONG_CREDENTIALS_ERROR);
    }

    @Test
    @Tag("negative")
    @Description("PATCH firstName: null — 400, may not be null.")
    @DisplayName("[API] PATCH /users/me/ — null в firstName")
    @Severity(SeverityLevel.NORMAL)
    void patchMeRejectsNullFirstName() {
        Map<String, Object> body = new HashMap<>();
        body.put("firstName", null);

        ProfileValidationErrorModel error = api.users.patchMeInvalid(session().accessToken(), body);

        assertThat(error.firstName()).containsExactly(FIELD_MAY_NOT_BE_NULL);
    }

    @Test
    @Tag("negative")
    @Description("PATCH с невалидным email: 400.")
    @DisplayName("[API] PATCH /users/me/ — невалидный email")
    @Severity(SeverityLevel.NORMAL)
    void patchMeRejectsInvalidEmail() {
        ProfileValidationErrorModel error = api.users.patchMeInvalid(
                session().accessToken(), Map.of("email", "nope"));

        assertThat(error.email()).containsExactly(VALID_EMAIL_ERROR);
    }

    @Test
    @Tag("negative")
    @Description("PATCH username на занятый: 400, already exists.")
    @DisplayName("[API] PATCH /users/me/ — занятый username")
    @Severity(SeverityLevel.NORMAL)
    void patchMeRejectsDuplicateUsername() {
        UserSession other = newUser();

        ProfileValidationErrorModel error = api.users.patchMeInvalid(
                session().accessToken(), Map.of("username", other.username()));

        assertThat(error.username()).containsExactly(REGISTRATION_EXISTING_USER_ERROR);
    }

    @Test
    @Tag("negative")
    @Description("PATCH с application/merge-patch+json: 415. Стенд принимает application/json.")
    @DisplayName("[API] PATCH /users/me/ — merge-patch+json не принимается")
    @Severity(SeverityLevel.NORMAL)
    void patchMeRejectsMergePatchContentType() {
        var error = api.users.requestMeWithContentType(
                "PATCH",
                session().accessToken(),
                "application/merge-patch+json",
                "{\"firstName\":\"Merge\"}");

        assertThat(error.detail())
                .startsWith("Unsupported media type \"")
                .contains("application/merge-patch+json");
    }

    @ParameterizedTest(name = "[API] {0} /users/me/ с Content-Type text/plain → 415")
    @ValueSource(strings = {"PUT", "PATCH"})
    @Tag("negative")
    @Description("PUT/PATCH с text/plain: 415 Unsupported media type.")
    @Severity(SeverityLevel.NORMAL)
    void writeMeRejectsPlainText(String method) {
        var error = api.users.requestMeWithContentType(
                method, session().accessToken(), "text/plain", "not json");

        assertThat(error.detail())
                .startsWith("Unsupported media type \"")
                .contains("text/plain");
    }

    @Test
    @Tag("positive")
    @Description("DELETE: 204; GET тем же JWT — 401 user_not_found; логин теми же кредами не проходит.")
    @DisplayName("[API] DELETE /users/me/ — 204, токен и логин больше не работают")
    @Severity(SeverityLevel.CRITICAL)
    void deleteMeRemovesProfileTokenAndLogin() {
        UserSession user = session();

        api.users.deleteMe(user.accessToken());

        var meError = api.users.requestMeAsDeletedUser("GET", user.accessToken());
        assertThat(meError.detail()).isEqualTo(USER_NOT_FOUND);
        assertThat(meError.code()).isEqualTo(USER_NOT_FOUND_CODE);

        var loginError = api.auth.loginWrongCredentials(
                new LoginBodyModel(user.username(), user.password()));
        assertThat(loginError.detail()).isEqualTo(LOGIN_WRONG_CREDENTIALS_ERROR);
    }

    @ParameterizedTest(name = "[API] {0} /users/me/ после DELETE → 401 user_not_found")
    @ValueSource(strings = {"PUT", "PATCH", "DELETE"})
    @Tag("negative")
    @Description("После DELETE мутирующие глаголы с тем же JWT дают 401 user_not_found (GET проверен в сценарии удаления).")
    @Severity(SeverityLevel.NORMAL)
    void afterDeleteMutatingMethodsReturnUserNotFound(String method) {
        UserSession user = newUser();
        api.users.deleteMe(user.accessToken());

        var error = api.users.requestMeAsDeletedUser(method, user.accessToken());

        assertThat(error.detail()).isEqualTo(USER_NOT_FOUND);
        assertThat(error.code()).isEqualTo(USER_NOT_FOUND_CODE);
    }

    @Test
    @Tag("negative")
    @Description("HEAD после DELETE: 401. Тело не разбираем.")
    @DisplayName("[API] HEAD /users/me/ после DELETE → 401")
    @Severity(SeverityLevel.NORMAL)
    void headMeAfterDeleteIsUnauthorized() {
        UserSession user = newUser();
        api.users.deleteMe(user.accessToken());

        assertThat(api.users.headMe(user.accessToken()).statusCode()).isEqualTo(401);
    }

    @Test
    @Tag("negative")
    @Description("POST на синглтон /users/me/: 405 Method POST not allowed.")
    @DisplayName("[API] POST /users/me/ — метод не разрешён")
    @Severity(SeverityLevel.NORMAL)
    void postMeIsNotAllowed() {
        var error = api.users.postMe(session().accessToken(), Map.of("username", session().username()));

        assertThat(error.detail()).isEqualTo(METHOD_POST_NOT_ALLOWED);
    }

    @Test
    @Tag("positive")
    @Description("OPTIONS: name=Self, parses JSON, PUT-поля required, PATCH в actions нет, Allow полный.")
    @DisplayName("[API] OPTIONS /users/me/ — контракт PUT, без PATCH в actions")
    @Severity(SeverityLevel.MINOR)
    void optionsMeDescribesPutContract() {
        Response response = api.users.optionsMe(session().accessToken());
        var json = response.jsonPath();

        assertAllowHeader(response.getHeader("Allow"));
        assertThat(json.getString("name")).isEqualTo("Self");
        assertThat(json.getList("parses")).contains("application/json");
        assertThat(json.getBoolean("actions.PUT.username.required")).isTrue();
        assertThat(json.getBoolean("actions.PUT.firstName.required")).isTrue();
        assertThat(json.getBoolean("actions.PUT.lastName.required")).isTrue();
        assertThat(json.getBoolean("actions.PUT.email.required")).isTrue();
        assertThat(json.getMap("actions")).doesNotContainKey("PATCH");
    }

    @Test
    @Tag("negative")
    @Description("OPTIONS без токена: 401, credentials were not provided.")
    @DisplayName("[API] OPTIONS /users/me/ без токена → 401")
    @Severity(SeverityLevel.NORMAL)
    void optionsMeWithoutTokenIsUnauthorized() {
        var error = api.users.optionsMeUnauthorized();

        assertThat(error.detail()).isEqualTo(AUTH_CREDENTIALS_NOT_PROVIDED);
    }

    @ParameterizedTest(name = "[API] {0} /users/me/ без токена → 401")
    @ValueSource(strings = {"PUT", "PATCH", "DELETE"})
    @Tag("negative")
    @Description("PUT/PATCH/DELETE без Authorization: 401. GET/HEAD/OPTIONS покрыты отдельными тестами с заголовками.")
    @Severity(SeverityLevel.CRITICAL)
    void meWithoutTokenIsUnauthorized(String method) {
        var error = api.users.requestMeUnauthorized(method);

        assertThat(error.detail()).isEqualTo(AUTH_CREDENTIALS_NOT_PROVIDED);
    }

    @Test
    @Tag("negative")
    @Description("GET с мусорным JWT: 401 token_not_valid, Token is invalid. Формат токена общий для всех глаголов.")
    @DisplayName("[API] GET /users/me/ с мусорным JWT → 401")
    @Severity(SeverityLevel.CRITICAL)
    void getMeWithGarbageJwtIsUnauthorized() {
        var error = api.users.requestMeInvalidJwt("GET", "not-a-jwt");

        assertThat(error.detail()).isEqualTo(TOKEN_NOT_VALID);
        assertThat(error.code()).isEqualTo(TOKEN_NOT_VALID_CODE);
        assertThat(error.messages()).isNotEmpty();
        assertThat(error.messages().getFirst().tokenType()).isEqualTo("access");
        assertThat(error.messages().getFirst().message()).isEqualTo("Token is invalid");
    }

    @Test
    @Tag("negative")
    @Description("GET с refresh вместо access: 401 token_not_valid, Token has wrong type.")
    @DisplayName("[API] GET /users/me/ с refresh вместо access → 401")
    @Severity(SeverityLevel.CRITICAL)
    void getMeWithRefreshTokenIsUnauthorized() {
        var error = api.users.requestMeInvalidJwt("GET", session().refreshToken());

        assertThat(error.detail()).isEqualTo(TOKEN_NOT_VALID);
        assertThat(error.messages().getFirst().message()).isEqualTo("Token has wrong type");
    }

    @Test
    @Tag("negative")
    @Description("GET без префикса Bearer: 401, credentials were not provided.")
    @DisplayName("[API] GET /users/me/ без префикса Bearer → 401")
    @Severity(SeverityLevel.NORMAL)
    void getMeWithoutBearerPrefixIsUnauthorized() {
        var error = api.users.requestMeWithAuthorizationHeader("GET", session().accessToken());

        assertThat(error.detail()).isEqualTo(AUTH_CREDENTIALS_NOT_PROVIDED);
    }

    @Test
    @Tag("negative")
    @Description("GET Authorization Token <jwt>: 401 Invalid token. Нужна схема Bearer.")
    @DisplayName("[API] GET /users/me/ со схемой Token → 401")
    @Severity(SeverityLevel.NORMAL)
    void getMeWithTokenSchemeIsUnauthorized() {
        var error = api.users.requestMeWithAuthorizationHeader(
                "GET", "Token " + session().accessToken());

        assertThat(error.detail()).isEqualTo(INVALID_TOKEN);
    }

    static Stream<Arguments> fieldsLongerThan150() {
        return Stream.of(
                Arguments.of("username", "u".repeat(151)),
                Arguments.of("firstName", "A".repeat(151)),
                Arguments.of("lastName", "A".repeat(151))
        );
    }

    private UserSession session() {
        if (session == null) {
            session = newUser();
        }
        return session;
    }

    private UserSession newUser() {
        String username = "me_" + faker.internet().uuid().replace("-", "").substring(0, 16);
        String password = "pass_" + faker.internet().uuid().replace("-", "").substring(0, 12);

        var registered = api.users.register(new RegistrationBodyModel(username, password));
        var login = api.auth.login(new LoginBodyModel(username, password));
        tokensToCleanup.add(login.access());

        return new UserSession(
                registered.id(),
                username,
                password,
                login.access(),
                login.refresh());
    }

    private static String emailOf(UserSession user) {
        return user.username() + "@example.com";
    }

    private static Map<String, String> fullProfileMap(UserSession user,
                                                      String firstName,
                                                      String lastName,
                                                      String email) {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("username", user.username());
        body.put("firstName", firstName);
        body.put("lastName", lastName);
        body.put("email", email);
        return body;
    }

    private static void assertProfile(UserModel actual,
                                      UserSession user,
                                      String username,
                                      String firstName,
                                      String lastName,
                                      String email) {
        assertThat(actual.id()).isEqualTo(user.id());
        assertThat(actual.username()).isEqualTo(username);
        assertThat(actual.firstName()).isEqualTo(firstName);
        assertThat(actual.lastName()).isEqualTo(lastName);
        assertThat(actual.email()).isEqualTo(email);
        assertThat(actual.remoteAddr()).isNotBlank();
    }

    private static void assertPersistedIgnoringRemoteAddr(UserModel expected, UserModel actual) {
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("remoteAddr")
                .isEqualTo(expected);
    }

    private static void assertAllowHeader(String allow) {
        assertThat(allow).isNotBlank();
        List<String> methods = Arrays.stream(allow.split(","))
                .map(String::trim)
                .toList();
        assertThat(methods).containsExactlyInAnyOrder(USERS_ME_ALLOWED_METHODS);
    }

    private static List<String> errorsOn(ProfileValidationErrorModel error, String field) {
        return switch (field) {
            case "username" -> error.username();
            case "firstName" -> error.firstName();
            case "lastName" -> error.lastName();
            case "email" -> error.email();
            default -> throw new IllegalArgumentException(field);
        };
    }

    private record UserSession(Integer id,
                               String username,
                               String password,
                               String accessToken,
                               String refreshToken) {}
}
