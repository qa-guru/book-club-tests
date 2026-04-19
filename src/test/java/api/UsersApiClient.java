package api;

import models.registration.ErrorFieldRegistrationResponseModel;
import models.registration.ExistingUserResponseModel;
import models.registration.RegistrationRequestModel;
import models.registration.SuccessfulRegistrationResponseModel;

import static io.restassured.RestAssured.given;
import static specs.registration.RegistrationSpec.*;

public class UsersApiClient {

    public SuccessfulRegistrationResponseModel register(RegistrationRequestModel body) {
        return given(registrationRequestSpec)
                .body(body)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract()
                .as(SuccessfulRegistrationResponseModel.class);
    }

    public ExistingUserResponseModel registerExistingUser(RegistrationRequestModel body) {
        return given(registrationRequestSpec)
                .body(body)
                .when()
                .post("/users/register/")
                .then()
                .spec(existingUserRegistrationResponseSpec)
                .extract()
                .as(ExistingUserResponseModel.class);
    }

    public ErrorFieldRegistrationResponseModel registerEmptyField(RegistrationRequestModel body) {
        return given(registrationRequestSpec)
                .body(body)
                .when()
                .post("/users/register/")
                .then()
                .spec(emptyFieldRegistrationResponseSpec)
                .extract()
                .as(ErrorFieldRegistrationResponseModel.class);
    }
}
