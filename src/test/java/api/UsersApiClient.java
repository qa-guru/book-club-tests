package api;

import models.users.registration.RegistrationValidationErrorResponseModel;
import models.users.registration.RegistrationRequestModel;
import models.users.registration.RegistrationSuccessResponseModel;

import static io.restassured.RestAssured.given;
import static specs.users.registration.RegistrationSpec.*;

public class UsersApiClient {

    public RegistrationSuccessResponseModel register(RegistrationRequestModel body) {
        return given(registrationRequestSpec)
                .body(body)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract()
                .as(RegistrationSuccessResponseModel.class);
    }

    public RegistrationValidationErrorResponseModel registerExistingUser(RegistrationRequestModel body) {
        return given(registrationRequestSpec)
                .body(body)
                .when()
                .post("/users/register/")
                .then()
                .spec(existingUserRegistrationResponseSpec)
                .extract()
                .as(RegistrationValidationErrorResponseModel.class);
    }

    public RegistrationValidationErrorResponseModel registerEmptyField(RegistrationRequestModel body) {
        return given(registrationRequestSpec)
                .body(body)
                .when()
                .post("/users/register/")
                .then()
                .spec(emptyFieldRegistrationResponseSpec)
                .extract()
                .as(RegistrationValidationErrorResponseModel.class);
    }
}
