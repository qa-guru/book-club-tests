package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import models.login.LoginBodyModel;
import models.logout.LogoutBodyModel;
import models.registration.RegistrationBodyModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static tests.TestData.LOGIN_PASSWORD;
import static tests.TestData.LOGIN_USERNAME;

@Feature("Выход из аккаунта")
public class LogoutTests extends TestBase {

    @Test
    @Tag("api")
    @Description("Логин по API, из ответа берётся refresh-токен; POST logout с этим токеном выполняется без ошибок (завершение сессии на сервере).")
    @DisplayName("[API] Успешный выход из системы (старый пользователь)")
    @Severity(SeverityLevel.CRITICAL)
    public void successfulLogoutTest() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD);
        String refreshToken = api.auth.loginAndGetRefreshToken(loginData);

        LogoutBodyModel logoutData = new LogoutBodyModel(refreshToken);
        api.auth.logout(logoutData);
    }

    @Test
    @Tag("api")
    @Description("Регистрация нового пользователя по API, логин, из ответа берётся refresh-токен; POST logout с этим токеном выполняется без ошибок (завершение сессии на сервере).")
    @DisplayName("[API] Успешный выход из системы (новый пользователь)")
    @Severity(SeverityLevel.CRITICAL)
    public void successfulLogoutAsNewUserApiTest() {
        String username = "user_" + System.currentTimeMillis();
        String password = "pass_" + System.currentTimeMillis();

        api.users.register(new RegistrationBodyModel(username, password));

        LoginBodyModel loginData = new LoginBodyModel(username, password);
        String refreshToken = api.auth.loginAndGetRefreshToken(loginData);

        LogoutBodyModel logoutData = new LogoutBodyModel(refreshToken);
        api.auth.logout(logoutData);
    }

    @Test
    @Tag("ui")
    @Description("Создаётся новый пользователь, открывается /profile, нажимается выход; проверяется отображение страницы входа.")
    @DisplayName("[UI] Успешный выход из системы (новый пользователь)")
    @Severity(SeverityLevel.CRITICAL)
    public void successfulLogoutAsNewUserTest() {
        profilePage.openBlankPageWithNewUser();
        profilePage.openPage()
                .pressLogoutButton()
                .loginPageShouldBeOpened();
    }

    @Test
    @Tag("ui")
    @Description("Создаётся новый пользователь, открывается /profile, нажимается выход; проверяется отображение страницы входа.")
    @DisplayName("[UI] Успешный выход из системы (старый пользователь)")
    @Severity(SeverityLevel.CRITICAL)
    public void successfulLogoutAsExistingUserTest() {
        profilePage.openBlankPageWithExistingUser();
        profilePage.openPage()
                .pressLogoutButton()
                .loginPageShouldBeOpened();
    }

    // todo add more negative tests
}

