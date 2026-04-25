package api;

import io.qameta.allure.Step;
import models.auth.login.LoginValidationErrorResponseModel;
import models.auth.login.LoginRequestModel;
import models.auth.login.LoginSuccessResponseModel;
import models.auth.login.LoginAuthErrorResponseModel;
import models.auth.logout.LogoutValidationErrorResponseModel;
import models.auth.logout.LogoutAuthErrorResponseModel;
import models.auth.logout.LogoutRequestModel;

import static io.restassured.RestAssured.given;
import static specs.auth.login.LoginSpec.*;
import static specs.auth.logout.LogoutSpec.*;

public class AuthApiClient {

    public LoginSuccessResponseModel login(LoginRequestModel loginBody) {
        return given(loginRequestSpec)
                .body(loginBody)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract()
                .as(LoginSuccessResponseModel.class);
    }

    @Step("Авторизация и получение рефреш-токена")
    public String loginAndGetRefreshToken(LoginRequestModel loginBody) {
        return given(loginRequestSpec)
                .body(loginBody)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract()
                .path("refresh");
    }

    @Step("Авторизация и получение аксес-токена")
    public String loginAndGetAccessToken(LoginRequestModel loginBody) {
        return given(loginRequestSpec)
                .body(loginBody)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract()
                .path("access");
    }

    public LoginAuthErrorResponseModel loginWrongCredentials(LoginRequestModel loginBody) {
        return given(loginRequestSpec)
                .body(loginBody)
                .when()
                .post("/auth/token/")
                .then()
                .spec(wrongCredentialsLoginResponseSpec)
                .extract()
                .as(LoginAuthErrorResponseModel.class);
    }

    public LoginValidationErrorResponseModel loginEmptyField(LoginRequestModel loginBody) {
        return  given(loginRequestSpec)
                .body(loginBody)
                .when()
                .post("/auth/token/")
                .then()
                .spec(emptyFieldLoginResponseSpec)
                .extract()
                .as(LoginValidationErrorResponseModel.class);
    }

    @Step("Отправка запроса logout")
    public void logout(LogoutRequestModel logoutBody) {
        given(logoutRequestSpec)
                .body(logoutBody)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(successfulLogoutResponseSpec);
    }

    public LogoutValidationErrorResponseModel validationErrorLogout(LogoutRequestModel logoutBody) {
        return given(logoutRequestSpec)
                .body(logoutBody)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(validationErrorLogoutResponseSpec)
                .extract()
                .as(LogoutValidationErrorResponseModel.class);
    }

    public LogoutAuthErrorResponseModel authErrorLogout(LogoutRequestModel logoutBody) {
        return given(logoutRequestSpec)
                .body(logoutBody)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(authErrorLogoutResponseSpec)
                .extract()
                .as(LogoutAuthErrorResponseModel.class);
    }
}

