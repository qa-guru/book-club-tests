package specs.auth.logout;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.filter.log.LogDetail.ALL;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static specs.BaseSpec.baseRequestSpec;

public class LogoutSpec {

    public static RequestSpecification logoutRequestSpec = baseRequestSpec;

    public static ResponseSpecification successfulLogoutResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(200)
            .build();

    public static ResponseSpecification error401LogoutResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(401)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/auth/logout/error_401_logout_response_schema.json"))
            .build();

    public static ResponseSpecification error400LogoutResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(400)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/auth/logout/error_400_logout_response_schema.json"))
            .build();
}

