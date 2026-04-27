package tests;

import models.users.registration.RegistrationValidationErrorResponseModel;
import models.users.registration.RegistrationRequestModel;
import models.users.UserSuccessResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.*;

@DisplayName("Тесты на регистрацию")
public class RegistrationTests extends TestBase {

    String username;
    String password;

    @BeforeEach
    public void prepareTestData() {
        // оставляем генерацию данных в тесте, чтобы каждый запуск был с новыми пользователями
        username = "user_" + System.currentTimeMillis();
        password = "pass_" + System.currentTimeMillis();
    }

    @Test
    @DisplayName("Успешная регистрация нового пользователя")
    public void successfulRegistrationTest() {
        RegistrationRequestModel registrationData = new RegistrationRequestModel(username, password);

        step("Запрос создания пользователя", () -> {
            UserSuccessResponseModel registrationResponse =
                    api.users.register(registrationData);

            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);
            assertThat(registrationResponse.firstName()).isEqualTo("");
            assertThat(registrationResponse.lastName()).isEqualTo("");
            assertThat(registrationResponse.email()).isEqualTo("");

            assertThat(registrationResponse.remoteAddr()).matches(REGISTRATION_IP_REGEXP);
        });
    }

    @Test
    @DisplayName("Создание уже существующего пользователя")
    public void existingUserWrongRegistrationTest() {
        RegistrationRequestModel registrationData = new RegistrationRequestModel(username, password);

        step("Запрос создания нового пользователя", () -> {
            UserSuccessResponseModel firstRegistrationResponse =
                            api.users.register(registrationData);
            assertThat(firstRegistrationResponse.username()).isEqualTo(username);
        });

        step("Повторный запрос создания пользователя", () -> {
            RegistrationValidationErrorResponseModel secondRegistrationResponse =
                    api.users.validationErrorRegister(registrationData);

            String expectedError = REGISTRATION_EXISTING_USER_ERROR;
            String actualError = secondRegistrationResponse.username().get(0);
            assertThat(actualError).isEqualTo(expectedError);
        });
    }

    @Test
    @DisplayName("Создание пользователя без пароля")
    public void emptyPasswordRegistrationTest() {
        RegistrationRequestModel registrationData = new RegistrationRequestModel(username, "");

        step("Запрос создания пользователя без пароля", () -> {
            RegistrationValidationErrorResponseModel registrationResponse =
                            api.users.validationErrorRegister(registrationData);


            String expectedError = EMPTY_FIELD_ERROR;
            String actualError = registrationResponse.getError();
            assertThat(actualError).isEqualTo(expectedError);
        });
    }

    @Test
    @DisplayName("Создание пользователя без имени")
    public void emptyUsernameRegistrationTest() {
        RegistrationRequestModel registrationData = new RegistrationRequestModel("", password);


        step("Запрос создания пользователя без имени", () -> {
            RegistrationValidationErrorResponseModel registrationResponse =
                    api.users.validationErrorRegister(registrationData);

            String expectedError = EMPTY_FIELD_ERROR;
            String actualError = registrationResponse.getError();
            assertThat(actualError).isEqualTo(expectedError);
        });
    }

    @Test
    @DisplayName("Создание пользователя с длинным именем")
    public void longUsernameRegistrationTest() {
        String longName = LONG_USERNAME;

        step("Запрос создания пользователя с именем в " + longName.length() + " символов", () -> {
            RegistrationRequestModel registrationData = new RegistrationRequestModel(longName, password);

            RegistrationValidationErrorResponseModel registrationResponse =
                    api.users.validationErrorRegister(registrationData);

            String expectedError = LONG_USERNAME_ERROR;
            String actualError = registrationResponse.getError();
            assertThat(actualError).isEqualTo(expectedError);
        });
    }

    @Test
    @DisplayName("Создание пользователя с длинным паролем")
    public void longPasswordRegistrationTest() {
        String longPassword = LONG_PASSWORD;

        step("Запрос создания пользователя с паролем в " + longPassword.length() + " символов", () -> {
            RegistrationRequestModel registrationData = new RegistrationRequestModel(username, longPassword);

            RegistrationValidationErrorResponseModel registrationResponse =
                    api.users.validationErrorRegister(registrationData);

            String expectedError = LONG_PASSWORD_ERROR;
            String actualError = registrationResponse.getError();
            assertThat(actualError).isEqualTo(expectedError);
        });
    }

}
