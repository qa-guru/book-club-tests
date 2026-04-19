package tests;

import models.registration.ErrorFieldRegistrationResponseModel;
import models.registration.ExistingUserResponseModel;
import models.registration.RegistrationRequestModel;
import models.registration.SuccessfulRegistrationResponseModel;
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

        SuccessfulRegistrationResponseModel registrationResponse =
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

        SuccessfulRegistrationResponseModel firstRegistrationResponse =
                api.users.register(registrationData);

        assertThat(firstRegistrationResponse.username()).isEqualTo(username);

        ExistingUserResponseModel secondRegistrationResponse =
                api.users.registerExistingUser(registrationData);

        String expectedError = REGISTRATION_EXISTING_USER_ERROR;
        String actualError = secondRegistrationResponse.username().get(0);
        assertThat(actualError).isEqualTo(expectedError);
    }

    @Test
    public void emptyPasswordRegistrationTest() {
        RegistrationRequestModel registrationData = new RegistrationRequestModel(username, "");

        ErrorFieldRegistrationResponseModel registrationResponse =
                api.users.registerEmptyField(registrationData);

        String expectedError = EMPTY_FIELD_ERROR;
        String actualError = registrationResponse.getError();
        assertThat(actualError).isEqualTo(expectedError);
    }

    @Test
    public void emptyUsernameRegistrationTest() {
        RegistrationRequestModel registrationData = new RegistrationRequestModel("", password);

        ErrorFieldRegistrationResponseModel registrationResponse =
                api.users.registerEmptyField(registrationData);

        String expectedError = EMPTY_FIELD_ERROR;
        String actualError = registrationResponse.getError();
        assertThat(actualError).isEqualTo(expectedError);
    }

    @Test
    public void longUsernameRegistrationTest() {
        String longName = LONG_USERNAME;

        RegistrationRequestModel registrationData = new RegistrationRequestModel(longName, password);

        ErrorFieldRegistrationResponseModel registrationResponse =
                api.users.registerEmptyField(registrationData);

        String expectedError = LONG_USERNAME_ERROR;
        String actualError = registrationResponse.getError();
        assertThat(actualError).isEqualTo(expectedError);
    }

    @Test
    public void longPasswordRegistrationTest() {
        String longPassword = LONG_PASSWORD;

        RegistrationRequestModel registrationData = new RegistrationRequestModel(username, longPassword);

        ErrorFieldRegistrationResponseModel registrationResponse =
                api.users.registerEmptyField(registrationData);

        String expectedError = LONG_PASSWORD_ERROR;
        String actualError = registrationResponse.getError();
        assertThat(actualError).isEqualTo(expectedError);
    }

}
