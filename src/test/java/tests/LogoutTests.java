package tests;

import models.auth.login.LoginRequestModel;
import models.auth.login.LoginSuccessResponseModel;
import models.auth.logout.LogoutValidationErrorResponseModel;
import models.auth.logout.LogoutAuthErrorResponseModel;
import models.auth.logout.LogoutRequestModel;
import org.junit.jupiter.api.Test;

import static tests.TestData.*;
import static org.assertj.core.api.Assertions.assertThat;

public class LogoutTests extends TestBase {

    @Test
    public void successfulLogoutTest() {
        LoginRequestModel loginData = new LoginRequestModel(LOGIN_USERNAME, LOGIN_PASSWORD);
        String refreshToken = api.auth.loginAndGetRefreshToken(loginData);

        LogoutRequestModel logoutData = new LogoutRequestModel(refreshToken);
        api.auth.logout(logoutData);
    }

    @Test
    public void accessTokenLogoutErrorTest() {
        LoginRequestModel loginData = new LoginRequestModel(LOGIN_USERNAME, LOGIN_PASSWORD);

        LoginSuccessResponseModel loginResponse = api.auth.login(loginData);

        String accessToken = loginResponse.access();
        LogoutRequestModel logoutData = new LogoutRequestModel(accessToken);

        LogoutAuthErrorResponseModel logoutResponse = api.auth.error401Logout(logoutData);

        String expectedErrorDetail = WRONG_TOKEN_TYPE_ERROR;
        String expectedErrorCode = VALIDATION_TOKEN_ERROR;
        String actualErrorDetail = logoutResponse.detail();
        String actualErrorCode = logoutResponse.code();
        assertThat(actualErrorDetail).isEqualTo(expectedErrorDetail);
        assertThat(actualErrorCode).isEqualTo(expectedErrorCode);
    }

    @Test
    public void withoutTokenLogoutErrorTest() {
        LogoutRequestModel logoutData = new LogoutRequestModel("");

        LogoutValidationErrorResponseModel logoutResponse = api.auth.error400Logout(logoutData);

        String expectedError = EMPTY_FIELD_ERROR;
        String actualError = logoutResponse.refresh().get(0);
        assertThat(actualError).isEqualTo(expectedError);
    }
}

