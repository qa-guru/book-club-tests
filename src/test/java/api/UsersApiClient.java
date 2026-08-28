package api;

import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import models.registration.ExistingUserResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import models.users.CodeDetailErrorModel;
import models.users.DetailErrorModel;
import models.users.ProfileValidationErrorModel;
import models.users.UserModel;

import static io.restassured.RestAssured.given;
import static specs.registration.RegistrationSpec.existingUserRegistrationResponseSpec;
import static specs.registration.RegistrationSpec.registrationRequestSpec;
import static specs.registration.RegistrationSpec.successfulRegistrationResponseSpec;
import static specs.users.UsersMeSpec.detailUnauthorizedResponseSpec;
import static specs.users.UsersMeSpec.invalidTokenResponseSpec;
import static specs.users.UsersMeSpec.methodNotAllowedResponseSpec;
import static specs.users.UsersMeSpec.noContentResponseSpec;
import static specs.users.UsersMeSpec.parseErrorResponseSpec;
import static specs.users.UsersMeSpec.successfulOptionsResponseSpec;
import static specs.users.UsersMeSpec.successfulUserResponseSpec;
import static specs.users.UsersMeSpec.unauthorizedResponseSpec;
import static specs.users.UsersMeSpec.unsupportedMediaTypeResponseSpec;
import static specs.users.UsersMeSpec.userNotFoundResponseSpec;
import static specs.users.UsersMeSpec.usersMeRequestSpec;
import static specs.users.UsersMeSpec.validationErrorResponseSpec;

public class UsersApiClient {

    @Step("[API] Регистрация пользователя POST /users/register/")
    public SuccessfulRegistrationResponseModel register(RegistrationBodyModel body) {
        return given(registrationRequestSpec)
                .body(body)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract()
                .as(SuccessfulRegistrationResponseModel.class);
    }

    @Step("[API] Попытка повторной регистрации существующего пользователя POST /users/register/")
    public ExistingUserResponseModel registerExistingUser(RegistrationBodyModel body) {
        return given(registrationRequestSpec)
                .body(body)
                .when()
                .post("/users/register/")
                .then()
                .spec(existingUserRegistrationResponseSpec)
                .extract()
                .as(ExistingUserResponseModel.class);
    }

    @Step("[API] GET /users/me/ как JSON")
    public JsonPath getMeJson(String accessToken) {
        return meRequest(accessToken)
                .when()
                .get("/users/me/")
                .then()
                .spec(successfulUserResponseSpec)
                .extract()
                .jsonPath();
    }

    @Step("[API] GET /users/me/ без авторизации (полный ответ)")
    public Response getMeUnauthorizedResponse() {
        return given(usersMeRequestSpec)
                .when()
                .get("/users/me/")
                .then()
                .spec(unauthorizedResponseSpec)
                .extract()
                .response();
    }

    @Step("[API] HEAD /users/me/")
    public Response headMe(String accessToken) {
        return meRequest(accessToken)
                .when()
                .head("/users/me/")
                .then()
                .extract()
                .response();
    }

    @Step("[API] HEAD /users/me/ без авторизации")
    public Response headMeUnauthorized() {
        return given(usersMeRequestSpec)
                .when()
                .head("/users/me/")
                .then()
                .extract()
                .response();
    }

    @Step("[API] Получение текущего пользователя GET /users/me/")
    public UserModel getMe(String accessToken) {
        return meRequest(accessToken)
                .when()
                .get("/users/me/")
                .then()
                .spec(successfulUserResponseSpec)
                .extract()
                .as(UserModel.class);
    }

    @Step("[API] Полная замена профиля PUT /users/me/")
    public UserModel putMe(String accessToken, Object body) {
        return meRequest(accessToken)
                .body(body)
                .when()
                .put("/users/me/")
                .then()
                .spec(successfulUserResponseSpec)
                .extract()
                .as(UserModel.class);
    }

    @Step("[API] Частичное обновление профиля PATCH /users/me/")
    public UserModel patchMe(String accessToken, Object body) {
        return meRequest(accessToken)
                .body(body)
                .when()
                .patch("/users/me/")
                .then()
                .spec(successfulUserResponseSpec)
                .extract()
                .as(UserModel.class);
    }

    @Step("[API] Удаление текущего пользователя DELETE /users/me/")
    public void deleteMe(String accessToken) {
        meRequest(accessToken)
                .when()
                .delete("/users/me/")
                .then()
                .spec(noContentResponseSpec);
    }

    public void deleteMeSilently(String accessToken) {
        meRequest(accessToken)
                .when()
                .delete("/users/me/");
    }

    @Step("[API] Невалидный PUT /users/me/")
    public ProfileValidationErrorModel putMeInvalid(String accessToken, Object body) {
        return meRequest(accessToken)
                .body(body)
                .when()
                .put("/users/me/")
                .then()
                .spec(validationErrorResponseSpec)
                .extract()
                .as(ProfileValidationErrorModel.class);
    }

    @Step("[API] Невалидный PATCH /users/me/")
    public ProfileValidationErrorModel patchMeInvalid(String accessToken, Object body) {
        return meRequest(accessToken)
                .body(body)
                .when()
                .patch("/users/me/")
                .then()
                .spec(validationErrorResponseSpec)
                .extract()
                .as(ProfileValidationErrorModel.class);
    }

    @Step("[API] {method} /users/me/ без авторизации")
    public DetailErrorModel requestMeUnauthorized(String method) {
        return given(usersMeRequestSpec)
                .when()
                .request(method, "/users/me/")
                .then()
                .spec(unauthorizedResponseSpec)
                .extract()
                .as(DetailErrorModel.class);
    }

    @Step("[API] {method} /users/me/ с невалидным JWT")
    public CodeDetailErrorModel requestMeInvalidJwt(String method, String token) {
        return meRequest(token)
                .when()
                .request(method, "/users/me/")
                .then()
                .spec(invalidTokenResponseSpec)
                .extract()
                .as(CodeDetailErrorModel.class);
    }

    @Step("[API] {method} /users/me/ с заголовком Authorization={authorizationHeader}")
    public DetailErrorModel requestMeWithAuthorizationHeader(String method, String authorizationHeader) {
        return given(usersMeRequestSpec)
                .header("Authorization", authorizationHeader)
                .when()
                .request(method, "/users/me/")
                .then()
                .spec(detailUnauthorizedResponseSpec)
                .extract()
                .as(DetailErrorModel.class);
    }

    @Step("[API] {method} /users/me/ после удаления пользователя")
    public CodeDetailErrorModel requestMeAsDeletedUser(String method, String accessToken) {
        return meRequest(accessToken)
                .when()
                .request(method, "/users/me/")
                .then()
                .spec(userNotFoundResponseSpec)
                .extract()
                .as(CodeDetailErrorModel.class);
    }

    @Step("[API] PUT /users/me/ с невалидным JSON")
    public DetailErrorModel putMeMalformedJson(String accessToken) {
        return meRequest(accessToken)
                .body("{")
                .when()
                .put("/users/me/")
                .then()
                .spec(parseErrorResponseSpec)
                .extract()
                .as(DetailErrorModel.class);
    }

    @Step("[API] {method} /users/me/ с Content-Type {contentType}")
    public DetailErrorModel requestMeWithContentType(String method,
                                                     String accessToken,
                                                     String contentType,
                                                     String rawBody) {
        return meRequest(accessToken)
                .contentType(contentType)
                .body(rawBody)
                .when()
                .request(method, "/users/me/")
                .then()
                .spec(unsupportedMediaTypeResponseSpec)
                .extract()
                .as(DetailErrorModel.class);
    }

    @Step("[API] POST /users/me/ — метод не разрешён")
    public DetailErrorModel postMe(String accessToken, Object body) {
        return meRequest(accessToken)
                .body(body)
                .when()
                .post("/users/me/")
                .then()
                .spec(methodNotAllowedResponseSpec)
                .extract()
                .as(DetailErrorModel.class);
    }

    @Step("[API] OPTIONS /users/me/")
    public Response optionsMe(String accessToken) {
        return meRequest(accessToken)
                .when()
                .options("/users/me/")
                .then()
                .spec(successfulOptionsResponseSpec)
                .extract()
                .response();
    }

    @Step("[API] OPTIONS /users/me/ без авторизации")
    public DetailErrorModel optionsMeUnauthorized() {
        return given(usersMeRequestSpec)
                .when()
                .options("/users/me/")
                .then()
                .spec(unauthorizedResponseSpec)
                .extract()
                .as(DetailErrorModel.class);
    }

    private RequestSpecification meRequest(String accessToken) {
        return given(usersMeRequestSpec)
                .header("Authorization", "Bearer " + accessToken);
    }
}
