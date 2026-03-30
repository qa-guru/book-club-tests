package pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;

public class LogoutPage extends BasePage {
    private SelenideElement logoutButton = $(".logout-btn");

    String pagePath = "/profile";

    public LogoutPage openPageWithNewUser() {
        openPageWithNewUser(pagePath);

        return this;
    }

    @Step("[UI] Нажатие кнопки выхода из аккаунта")
    public LoginPage pressLogoutButton() {
        logoutButton.click();

        return new LoginPage();
    }
}
