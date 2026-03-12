package api;

import io.qameta.allure.Step;
import models.clubs.ClubModel;
import models.clubs.ClubsListResponseModel;
import models.clubs.CreateClubBodyModel;

import static io.restassured.RestAssured.given;
import static specs.clubs.ClubsSpec.clubsRequestSpec;
import static specs.clubs.ClubsSpec.successfulClubsListResponseSpec;
import static specs.clubs.ClubsSpec.successfulCreateClubResponseSpec;
import static specs.clubs.ClubsSpec.unauthorizedCreateClubResponseSpec;

public class ClubsApiClient {

    @Step("Получение списка клубов GET /clubs/")
    public ClubsListResponseModel getClubs() {
        return given(clubsRequestSpec)
                .when()
                .get("/clubs/")
                .then()
                .spec(successfulClubsListResponseSpec)
                .extract()
                .as(ClubsListResponseModel.class);
    }

    @Step("Создание клуба POST /clubs/")
    public ClubModel createClub(String accessToken, CreateClubBodyModel body) {
        return given(clubsRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .when()
                .post("/clubs/")
                .then()
                .spec(successfulCreateClubResponseSpec)
                .extract()
                .as(ClubModel.class);
    }

    @Step("Попытка создать клуб без авторизации POST /clubs/")
    public void createClubWithoutAuth(CreateClubBodyModel body) {
        given(clubsRequestSpec)
                .body(body)
                .when()
                .post("/clubs/")
                .then()
                .spec(unauthorizedCreateClubResponseSpec);
    }
}
