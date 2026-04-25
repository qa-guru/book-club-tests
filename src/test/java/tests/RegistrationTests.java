package tests;

import models.users.registration.RegistrationValidationErrorResponseModel;
import models.users.registration.RegistrationRequestModel;
import models.users.registration.RegistrationSuccessResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.*;

public class RegistrationTests extends TestBase {

    String username;
    String password;

    @BeforeEach
    public void prepareTestData() {
        // оставляем генерацию данных в тесте, чтобы каждый запуск был с новыми пользователями
        username = "user_" + System.currentTimeMillis();
        password = "pass_" + System.currentTimeMillis();
    }

    @Test
    public void successfulRegistrationTest() {
        RegistrationRequestModel registrationData = new RegistrationRequestModel(username, password);

        RegistrationSuccessResponseModel registrationResponse =
                api.users.register(registrationData);

        assertThat(registrationResponse.id()).isGreaterThan(0);
        assertThat(registrationResponse.username()).isEqualTo(username);
        assertThat(registrationResponse.firstName()).isEqualTo("");
        assertThat(registrationResponse.lastName()).isEqualTo("");
        assertThat(registrationResponse.email()).isEqualTo("");

        assertThat(registrationResponse.remoteAddr()).matches(REGISTRATION_IP_REGEXP);
    }

    @Test
    public void existingUserWrongRegistrationTest() {
        RegistrationRequestModel registrationData = new RegistrationRequestModel(username, password);

        RegistrationSuccessResponseModel firstRegistrationResponse =
                api.users.register(registrationData);

        assertThat(firstRegistrationResponse.username()).isEqualTo(username);

        RegistrationValidationErrorResponseModel secondRegistrationResponse =
                api.users.registerExistingUser(registrationData);

        String expectedError = REGISTRATION_EXISTING_USER_ERROR;
        String actualError = secondRegistrationResponse.username().get(0);
        assertThat(actualError).isEqualTo(expectedError);
    }

    @Test
    public void emptyPasswordRegistrationTest() {
        RegistrationRequestModel registrationData = new RegistrationRequestModel(username, "");

        RegistrationValidationErrorResponseModel registrationResponse =
                api.users.registerEmptyField(registrationData);

        String expectedError = EMPTY_FIELD_ERROR;
        String actualError = registrationResponse.getError();
        assertThat(actualError).isEqualTo(expectedError);
    }

    @Test
    public void emptyUsernameRegistrationTest() {
        RegistrationRequestModel registrationData = new RegistrationRequestModel("", password);

        RegistrationValidationErrorResponseModel registrationResponse =
                api.users.registerEmptyField(registrationData);

        String expectedError = EMPTY_FIELD_ERROR;
        String actualError = registrationResponse.getError();
        assertThat(actualError).isEqualTo(expectedError);
    }

    @Test
    public void longUsernameRegistrationTest() {
        String longName = LONG_USERNAME;

        RegistrationRequestModel registrationData = new RegistrationRequestModel(longName, password);

        RegistrationValidationErrorResponseModel registrationResponse =
                api.users.registerEmptyField(registrationData);

        String expectedError = LONG_USERNAME_ERROR;
        String actualError = registrationResponse.getError();
        assertThat(actualError).isEqualTo(expectedError);
    }

    @Test
    public void longPasswordRegistrationTest() {
        String longPassword = LONG_PASSWORD;

        RegistrationRequestModel registrationData = new RegistrationRequestModel(username, longPassword);

        RegistrationValidationErrorResponseModel registrationResponse =
                api.users.registerEmptyField(registrationData);

        String expectedError = LONG_PASSWORD_ERROR;
        String actualError = registrationResponse.getError();
        assertThat(actualError).isEqualTo(expectedError);
    }

}
