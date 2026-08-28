package tests;

import api.ApiClient;
import io.restassured.RestAssured;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeAll;

/**
 * База для API-слоя: без Selenide. URL как в {@link TestBase} этого репозитория.
 */
public class ApiTestBase {

    protected final Faker faker = new Faker();
    protected static final ApiClient api = new ApiClient();

    @BeforeAll
    public static void setUpApi() {
        RestAssured.baseURI = "https://book-club.qa.guru";
        RestAssured.basePath = "/api/v1";
    }
}
