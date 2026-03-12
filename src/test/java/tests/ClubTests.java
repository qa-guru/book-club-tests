package tests;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;

public class ClubTests {

    String username;
    String password;

    @BeforeAll
    static void beforeAll() {
        Configuration.browserSize = "1920x1080";
    }

    @BeforeEach
    public void prepareTestData() {

        // оставляем генерацию данных в тесте, чтобы каждый запуск был с новыми пользователями
        Faker faker = new Faker();
        username = faker.name().firstName();
        password = "12345";
    }

    @Test
    public void cantLeaveClubAsAdminTest(){
        // register user
        open("https://book-club.qa.guru/signup");
        $("[data-testid=username-input]").setValue(username);
        $("[data-testid=password-input]").setValue(password);
        $("[data-testid=confirm-password-input]").setValue(password).pressEnter();
        $("[data-testid=signup-button]").should(disappear);

//        // login user
//        $("[data-testid=username-input]").setValue(username);
//        $("[data-testid=password-input]").setValue(password).pressEnter();
//        $("[data-testid=signin-button]").should(disappear);

        // create club
        // todo create test for navigation from Main page to Create club page
        // open("https://book-club.qa.guru/clubs/create");
        $("[data-testid=create-club-link]").click();
        $("#bookTitle").setValue(username);
        $("#bookAuthors").setValue(username);
        $("#publicationYear").setValue("2020");
        $("#description").setValue(username);
        $("#telegramChatLink").setValue("https://t.me/qa_guru" + username).pressEnter();

        // open club
        $(".clubs-list").$(byText(username))
                .parent().parent().$(".open-btn").click();

        // wrong leave club
        $(".club-content").shouldBe(visible);
        $(".leave-btn").click();
        confirm();
        $(".error").shouldHave(text("Не удалось покинуть клуб"));
    }
}
