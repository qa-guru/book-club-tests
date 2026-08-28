package specs.users;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.filter.log.LogDetail.ALL;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static specs.BaseSpec.baseRequestSpec;

public class UsersMeSpec {

    public static RequestSpecification usersMeRequestSpec = baseRequestSpec;

    public static ResponseSpecification successfulUserResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(200)
            .expectBody(matchesJsonSchemaInClasspath("schemas/users/user_response_schema.json"))
            .expectBody("id", notNullValue())
            .expectBody("username", notNullValue())
            .expectBody("remoteAddr", notNullValue())
            .build();

    public static ResponseSpecification noContentResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(204)
            .build();

    public static ResponseSpecification unauthorizedResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(401)
            .expectBody(matchesJsonSchemaInClasspath("schemas/users/detail_error_schema.json"))
            .build();

    public static ResponseSpecification invalidTokenResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(401)
            .expectBody(matchesJsonSchemaInClasspath("schemas/users/invalid_token_schema.json"))
            .expectBody("code", equalTo("token_not_valid"))
            .build();

    public static ResponseSpecification userNotFoundResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(401)
            .expectBody(matchesJsonSchemaInClasspath("schemas/users/user_not_found_schema.json"))
            .expectBody("code", equalTo("user_not_found"))
            .build();

    public static ResponseSpecification detailUnauthorizedResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(401)
            .expectBody(matchesJsonSchemaInClasspath("schemas/users/detail_error_schema.json"))
            .expectBody("detail", notNullValue())
            .build();

    public static ResponseSpecification validationErrorResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(400)
            .expectBody(matchesJsonSchemaInClasspath("schemas/users/validation_error_schema.json"))
            .build();

    public static ResponseSpecification parseErrorResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(400)
            .expectBody(matchesJsonSchemaInClasspath("schemas/users/detail_error_schema.json"))
            .build();

    public static ResponseSpecification unsupportedMediaTypeResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(415)
            .expectBody(matchesJsonSchemaInClasspath("schemas/users/detail_error_schema.json"))
            .build();

    public static ResponseSpecification methodNotAllowedResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(405)
            .expectBody(matchesJsonSchemaInClasspath("schemas/users/detail_error_schema.json"))
            .build();

    public static ResponseSpecification successfulOptionsResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(200)
            .build();
}
