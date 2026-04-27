package tests;

import models.auth.login.LoginValidationErrorResponseModel;
import models.auth.login.LoginRequestModel;
import models.auth.login.LoginSuccessResponseModel;
import models.auth.login.LoginAuthErrorResponseModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.*;

@DisplayName("Тесты на логин")
public class LoginTests extends TestBase {

    @Test
    @DisplayName("Успешный логин")
    public void successfulLoginTest() {
        LoginRequestModel loginData = new LoginRequestModel(LOGIN_USERNAME, LOGIN_PASSWORD);

        step("Запрос логина", () -> {
            LoginSuccessResponseModel loginResponse = api.auth.login(loginData);

            String actualAccess = loginResponse.access();
            String actualRefresh = loginResponse.refresh();
            assertThat(actualAccess).startsWith(LOGIN_TOKEN_PREFIX);
            assertThat(actualRefresh).startsWith(LOGIN_TOKEN_PREFIX);
            assertThat(actualAccess).isNotEqualTo(actualRefresh);
        });
    }

    @Test
    @DisplayName("Логин с неверным паролем")
    public void wrongPasswordLoginTest() {
        LoginRequestModel loginData = new LoginRequestModel(LOGIN_USERNAME, LOGIN_WRONG_PASSWORD);

        step("Запрос логина с неверным паролем", () -> {
            LoginAuthErrorResponseModel loginResponse = api.auth.loginWrongCredentials(loginData);

            String expectedError = LOGIN_WRONG_CREDENTIALS_ERROR;
            String actualError = loginResponse.detail();
            assertThat(actualError).isEqualTo(expectedError);
        });
    }

    // 401 WrongCredentials
    @Test
    @DisplayName("Логин с неверным именем")
    public void wrongUsernameLoginTest() {
        LoginRequestModel loginData = new LoginRequestModel(LOGIN_WRONG_USERNAME, LOGIN_PASSWORD);

        step("Запрос логина с неверным именем", () -> {
            LoginAuthErrorResponseModel loginResponse = api.auth.loginWrongCredentials(loginData);

            String expectedError = LOGIN_WRONG_CREDENTIALS_ERROR;
            String actualError = loginResponse.detail();
            assertThat(actualError).isEqualTo(expectedError);
        });
    }

    // 400 EmptyField
    @Test
    @DisplayName("Логин без имени")
    public void emptyUsernameLoginTest() {
        LoginRequestModel loginData = new LoginRequestModel("", LOGIN_PASSWORD);

        LoginValidationErrorResponseModel loginResponse = api.auth.loginEmptyField(loginData);

        String expectedError = EMPTY_FIELD_ERROR;
        String actualError = loginResponse.username().get(0);
        assertThat(actualError).isEqualTo(expectedError);
    }

    @Test
    @DisplayName("Логин без пароля")
    public void emptyPasswordLoginTest() {
        LoginRequestModel loginData = new LoginRequestModel(LOGIN_USERNAME, "");

        LoginValidationErrorResponseModel loginResponse = api.auth.loginEmptyField(loginData);

        String expectedError = EMPTY_FIELD_ERROR;
        String actualError = loginResponse.password().get(0);
        assertThat(actualError).isEqualTo(expectedError);
    }

    @Test
    @DisplayName("Логин с пустыми полями имени и пароля")
    public void emptyFieldsLoginTest() {
        LoginRequestModel loginData = new LoginRequestModel("", "");

        LoginValidationErrorResponseModel loginResponse = api.auth.loginEmptyField(loginData);

        String expectedError = EMPTY_FIELD_ERROR;
        String actualUsernameError = loginResponse.username().get(0);
        String actualPasswordError = loginResponse.password().get(0);
        assertThat(actualUsernameError).isEqualTo(expectedError);
        assertThat(actualPasswordError).isEqualTo(expectedError);
    }

}
