package tests;

import models.clubs.ClubModel;
import models.clubs.CreateClubBodyModel;
import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;

public class ClubTests extends TestBase {

    String username;
    String password;

    @BeforeEach
    public void prepareTestData() {
        // оставляем генерацию данных в тесте, чтобы каждый запуск был с новыми пользователями
        username = faker.name().firstName();
        password = "12345";
    }

    @Test
    public void cantLeaveClubAsOwnerTest(){
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

        // create club
        String bookTitle = faker.book().title();
        String bookAuthors = faker.book().author();
        Integer publicationYear = 2009;
        String description = faker.lorem().sentence();
        String telegramChatLink = "https://t.me/qa_guru";

        CreateClubBodyModel createClubBody = new CreateClubBodyModel(
                bookTitle,
                bookAuthors,
                publicationYear,
                description,
                telegramChatLink
        );

        ClubModel createdClub = api.clubs.createClub(accessToken, createClubBody);
        String clubId = createdClub.id().toString();

        // open club
        open("/favicon.ico");
        localStorage().setItem("book_club_auth", localStorageAuthBody);
        open("/clubs/" + clubId);

        // cant leave club as owner
        $(".club-content").shouldBe(visible);
        $(".leave-btn").click();
        confirm();
        $(".error").shouldHave(text("Не удалось покинуть клуб"));
    }

    @Test
//    @WithNewUser
//    @WithNewClub
    public void cantLeaveClubAsOwnerTest_with_extensions (){
        // cant leave club as owner
        $(".club-content").shouldBe(visible);
        $(".leave-btn").click();
        confirm();
        $(".error").shouldHave(text("Не удалось покинуть клуб"));
    }

    @Test
    public void createClub_byApiTest() {
        String bookTitle = "API Club " + faker.book().title();
        String bookAuthors = faker.book().author();
        Integer publicationYear = 2020;
        String description = faker.lorem().sentence();
        String telegramChatLink = "https://t.me/qa_guru_" + faker.internet().uuid();

        // register user
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
        SuccessfulRegistrationResponseModel registrationResponse = api.users.register(registrationData);

        // login user
        LoginBodyModel loginData = new LoginBodyModel(username, password);
        SuccessfulLoginResponseModel loginResponse = api.auth.login(loginData);

        String accessToken = loginResponse.access();

        // create club by API
        CreateClubBodyModel createClubBody = new CreateClubBodyModel(
                bookTitle,
                bookAuthors,
                publicationYear,
                description,
                telegramChatLink
        );

        ClubModel createdClub = api.clubs.createClub(accessToken, createClubBody);

        // assertions
        org.assertj.core.api.Assertions.assertThat(createdClub.bookTitle()).isEqualTo(bookTitle);
        org.assertj.core.api.Assertions.assertThat(createdClub.bookAuthors()).isEqualTo(bookAuthors);
        org.assertj.core.api.Assertions.assertThat(createdClub.publicationYear()).isEqualTo(publicationYear);
        org.assertj.core.api.Assertions.assertThat(createdClub.description()).isEqualTo(description);
        org.assertj.core.api.Assertions.assertThat(createdClub.telegramChatLink()).isEqualTo(telegramChatLink);
        org.assertj.core.api.Assertions.assertThat(createdClub.owner()).isEqualTo(registrationResponse.id());
    }

    @Test
    @Disabled
    public void cantLeaveClubAsAdminTest_with_login_by_api(){
        // register user
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
        SuccessfulRegistrationResponseModel registrationResponse = api.users.register(registrationData);
        // login user
        LoginBodyModel loginData = new LoginBodyModel(username, password);
        SuccessfulLoginResponseModel loginResponse = api.auth.login(loginData);

        String accessToken = loginResponse.access();
        String refreshToken = loginResponse.refresh();

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

        open("/favicon.ico");
        localStorage().setItem("book_club_auth", localStorageAuthBody);
//        open("/");

        // create club
        // todo create test for navigation from Main page to Create club page
        open("https://book-club.qa.guru/clubs/create");
//        $("[data-testid=create-club-link]").click();
        $("#bookTitle").setValue(username);
        $("#bookAuthors").setValue(username);
        $("#publicationYear").setValue("2020");
        $("#description").setValue(username);
        $("#telegramChatLink").setValue("https://t.me/qa_guru" + username).pressEnter();

        // open club
        $(".clubs-list").$(byText(username))
                .parent().parent().$(".open-btn").click();

        $(".clubs-list").$(byText(username))
                .parent().parent().$(".open-btn").click();

        // wrong leave club
        $(".club-content").shouldBe(visible);
        $(".leave-btn").click();
        confirm();
        $(".error").shouldHave(text("Не удалось покинуть клуб"));
    }

    @Test
    @Disabled
    public void cantLeaveClubAsAdminTest_without_api(){
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
