package pages;

import api.ApiClient;
import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import io.qameta.allure.Step;
import net.datafaker.Faker;

import static com.codeborne.selenide.Selenide.localStorage;
import static com.codeborne.selenide.Selenide.open;

public class BasePage {
    ApiClient api = new ApiClient();

    @Step("[UI] Регистрация пользователя, установка сессии и открытие страницы: {url}")
    public void openPageWithNewUser(String url) {
        String username = new Faker().name().firstName();
        String password = "12345";

        // register user
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
        SuccessfulRegistrationResponseModel registrationResponse = api.users.register(registrationData);

        // login user
        LoginBodyModel loginData = new LoginBodyModel(username, password);
        SuccessfulLoginResponseModel loginResponse = api.auth.login(loginData);

        String accessToken = loginResponse.access();
        String refreshToken = loginResponse.refresh();

        // todo move to model
        String localStorageAuthBody = """
                {
                  "user": {
                    "id": %d,
                    "username": "%s",
                    "firstName": "%s",
                    "lastName": "%s",
                    "email": "%s",
                    "remoteAddr": "%s"
                  },
                  "accessToken": "%s",
                  "refreshToken": "%s",
                  "isAuthenticated": true
                }
                """.formatted(
                registrationResponse.id(),
                registrationResponse.username(),
                registrationResponse.firstName(),
                registrationResponse.lastName(),
                registrationResponse.email(),
                registrationResponse.remoteAddr(),
                accessToken,
                refreshToken
        );

        // open logout page
        open("/favicon.ico");
        setLocalStorage("book_club_auth", localStorageAuthBody);
        open(url);
    }


    @Step("[UI] Открытие страницы с существующим пользователем")
    public void openPageWithExistingUser() {} // todo implement

    @Step("[UI] Установка данных в localstorage")
    public void setLocalStorage(String key, String value) {
        localStorage().setItem(key, value);
    }
}
