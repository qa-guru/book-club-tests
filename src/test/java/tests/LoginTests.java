package tests;

import models.login.EmptyFieldLoginResponseModel;
import models.login.LoginRequestModel;
import models.login.SuccessfulLoginResponseModel;
import models.login.WrongCredentialsLoginResponseModel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.*;

public class LoginTests extends TestBase {

    @Test
    public void successfulLoginTest() {
        LoginRequestModel loginData = new LoginRequestModel(LOGIN_USERNAME, LOGIN_PASSWORD);

        SuccessfulLoginResponseModel loginResponse = api.auth.login(loginData);

        String actualAccess = loginResponse.access();
        String actualRefresh = loginResponse.refresh();
        assertThat(actualAccess).startsWith(LOGIN_TOKEN_PREFIX);
        assertThat(actualRefresh).startsWith(LOGIN_TOKEN_PREFIX);
        assertThat(actualAccess).isNotEqualTo(actualRefresh);
    }

    @Test
    public void wrongPasswordLoginTest() {
        LoginRequestModel loginData = new LoginRequestModel(LOGIN_USERNAME, LOGIN_WRONG_PASSWORD);

        WrongCredentialsLoginResponseModel loginResponse = api.auth.loginWrongCredentials(loginData);

        String expectedError = LOGIN_WRONG_CREDENTIALS_ERROR;
        String actualError = loginResponse.detail();
        assertThat(actualError).isEqualTo(expectedError);
    }

    @Test
    public void wrongUsernameLoginTest() {
        LoginRequestModel loginData = new LoginRequestModel(LOGIN_WRONG_USERNAME, LOGIN_PASSWORD);

        WrongCredentialsLoginResponseModel loginResponse = api.auth.loginWrongCredentials(loginData);

        String expectedError = LOGIN_WRONG_CREDENTIALS_ERROR;
        String actualError = loginResponse.detail();
        assertThat(actualError).isEqualTo(expectedError);
    }

    @Test
    public void emptyUsernameLoginTest() {
        LoginRequestModel loginData = new LoginRequestModel("", LOGIN_PASSWORD);

        EmptyFieldLoginResponseModel loginResponse = api.auth.loginEmptyField(loginData);

        String expectedError = EMPTY_FIELD_ERROR;
        String actualError = loginResponse.getError();
        assertThat(actualError).isEqualTo(expectedError);
    }

    @Test
    public void emptyPasswordLoginTest() {
        LoginRequestModel loginData = new LoginRequestModel(LOGIN_USERNAME, "");

        EmptyFieldLoginResponseModel loginResponse = api.auth.loginEmptyField(loginData);

        String expectedError = EMPTY_FIELD_ERROR;
        String actualError = loginResponse.getError();
        assertThat(actualError).isEqualTo(expectedError);
    }

    @Test
    public void emptyFieldsLoginTest() {
        LoginRequestModel loginData = new LoginRequestModel("", "");

        EmptyFieldLoginResponseModel loginResponse = api.auth.loginEmptyField(loginData);

        String expectedError = EMPTY_FIELD_ERROR;
        String actualError = loginResponse.getError();
        assertThat(actualError).isEqualTo(expectedError);
    }

}
