package api;

import models.users.put_user.PutUserRequestModel;
import models.users.put_user.PutUserValidationErrorResponseModel;
import models.users.registration.RegistrationValidationErrorResponseModel;
import models.users.registration.RegistrationRequestModel;
import models.users.UserSuccessResponseModel;

import static io.restassured.RestAssured.given;
import static specs.users.put.PutUserSpec.*;
import static specs.users.registration.RegistrationSpec.*;

public class UsersApiClient {

    public UserSuccessResponseModel register(RegistrationRequestModel body) {
        return given(registrationRequestSpec)
                .body(body)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract()
                .as(UserSuccessResponseModel.class);
    }

    public RegistrationValidationErrorResponseModel validationErrorRegister(RegistrationRequestModel body) {
        return given(registrationRequestSpec)
                .body(body)
                .when()
                .post("/users/register/")
                .then()
                .spec(validationErrorRegistrationResponseSpec)
                .extract()
                .as(RegistrationValidationErrorResponseModel.class);
    }

    public UserSuccessResponseModel putUser(String accessToken,PutUserRequestModel body) {
        return given(putUserRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .when()
                .put("/users/me/")
                .then()
                .spec(successfulPutUserResponseSpec)
                .extract()
                .as(UserSuccessResponseModel.class);
    }

    public PutUserValidationErrorResponseModel validationErrorPutUser(String accessToken,PutUserRequestModel body) {
        return given(putUserRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .when()
                .put("/users/me/")
                .then()
                .spec(validationErrorPutUserResponseSpec)
                .extract()
                .as(PutUserValidationErrorResponseModel.class);
    }
}
