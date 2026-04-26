package tests;

import models.auth.login.LoginRequestModel;
import models.users.UserSuccessResponseModel;
import models.users.put_user.PutUserRequestModel;
import models.users.put_user.PutUserValidationErrorResponseModel;
import models.users.registration.RegistrationRequestModel;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.REQUIRED_FIELD_ERROR;

public class UpdateUserTests extends TestBase {

    Faker faker = new Faker();
    String username;
    String password;
    String firstname;
    String lastName;
    String email;

    @BeforeEach
    public void prepareTestData() {

        username = "user_" + System.currentTimeMillis();
        password = "pass_" + System.currentTimeMillis();
        firstname = faker.name().firstName();
        lastName = faker.name().lastName();
        email = faker.internet().emailAddress();
    }

    @Test
    public void successfulUpdateUserTest() {
        RegistrationRequestModel registrationData = new RegistrationRequestModel(username, password);
        api.users.register(registrationData);

        LoginRequestModel loginData = new LoginRequestModel(registrationData.username(), registrationData.password());
        String accessToken = api.auth.loginAndGetAccessToken(loginData);

        PutUserRequestModel updateData = new PutUserRequestModel(username, firstname,  lastName, email);
        UserSuccessResponseModel updateResponse = api.users.putUser(accessToken, updateData);

        String userNameData = updateData.username();
        String userNameResponse = updateResponse.username();
        assertThat(userNameData).isEqualTo(userNameResponse);
    }

    @Test
    public void withoutFirstnameFailedUpdateUserTest() {
        RegistrationRequestModel registrationData = new RegistrationRequestModel(username, password);
        api.users.register(registrationData);

        LoginRequestModel loginData = new LoginRequestModel(registrationData.username(), registrationData.password());
        String accessToken = api.auth.loginAndGetAccessToken(loginData);

        PutUserRequestModel updateData = PutUserRequestModel.withoutFirstName(username, lastName, email);

        PutUserValidationErrorResponseModel response = api.users.validationErrorPutUser(accessToken, updateData);
        String expectedErrorMessage = REQUIRED_FIELD_ERROR;

        assertThat(response.firstName()).containsExactly(expectedErrorMessage);
        assertThat(response.username()).isNull();
        assertThat(response.lastName()).isNull();
        assertThat(response.email()).isNull();
    }

    @Test
    public void withoutAllFieldsFailedUpdateUserTest() {
        RegistrationRequestModel registrationData = new RegistrationRequestModel(username, password);
        api.users.register(registrationData);

        LoginRequestModel loginData = new LoginRequestModel(registrationData.username(), registrationData.password());
        String accessToken = api.auth.loginAndGetAccessToken(loginData);

        PutUserRequestModel updateData = PutUserRequestModel.withoutAllFields();

        PutUserValidationErrorResponseModel response = api.users.validationErrorPutUser(accessToken, updateData);
        String expectedErrorMessage = REQUIRED_FIELD_ERROR;

        assertThat(response.firstName()).containsExactly(expectedErrorMessage);
        assertThat(response.username()).containsExactly(expectedErrorMessage);
        assertThat(response.lastName()).containsExactly(expectedErrorMessage);
        assertThat(response.email()).containsExactly(expectedErrorMessage);
    }
}
