package api;

import io.qameta.allure.Step;
import models.login.EmptyFieldLoginResponseModel;
import models.login.LoginRequestModel;
import models.login.SuccessfulLoginResponseModel;
import models.login.WrongCredentialsLoginResponseModel;
import models.logout.Error400LogoutResponseModel;
import models.logout.Error401LogoutResponseModel;
import models.logout.LogoutRequestModel;

import static io.restassured.RestAssured.given;
import static specs.login.LoginSpec.*;
import static specs.logout.LogoutSpec.*;

public class AuthApiClient {

    public SuccessfulLoginResponseModel login(LoginRequestModel loginBody) {
        return given(loginRequestSpec)
                .body(loginBody)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract()
                .as(SuccessfulLoginResponseModel.class);
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

    public WrongCredentialsLoginResponseModel loginWrongCredentials(LoginRequestModel loginBody) {
        return given(loginRequestSpec)
                .body(loginBody)
                .when()
                .post("/auth/token/")
                .then()
                .spec(wrongCredentialsLoginResponseSpec)
                .extract()
                .as(WrongCredentialsLoginResponseModel.class);
    }

    public EmptyFieldLoginResponseModel loginEmptyField(LoginRequestModel loginBody) {
        return  given(loginRequestSpec)
                .body(loginBody)
                .when()
                .post("/auth/token/")
                .then()
                .spec(emptyFieldLoginResponseSpec)
                .extract()
                .as(EmptyFieldLoginResponseModel.class);
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

    public Error400LogoutResponseModel error400Logout(LogoutRequestModel logoutBody) {
        return given(logoutRequestSpec)
                .body(logoutBody)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(error400LogoutResponseSpec)
                .extract()
                .as(Error400LogoutResponseModel.class);
    }

    public Error401LogoutResponseModel error401Logout(LogoutRequestModel logoutBody) {
        return given(logoutRequestSpec)
                .body(logoutBody)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(error401LogoutResponseSpec)
                .extract()
                .as(Error401LogoutResponseModel.class);
    }
}

