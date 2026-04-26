package models.users.registration;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public record RegistrationValidationErrorResponseModel(List<String> username,
                                                       List<String> password) {

    public String getError() {
        if (username != null && !username.isEmpty()) return username.get(0);
        if (password != null && !password.isEmpty()) return password.get(0);
        return "";
    }
}
