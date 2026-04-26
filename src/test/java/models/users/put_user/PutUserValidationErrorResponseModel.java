package models.users.put_user;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public record PutUserValidationErrorResponseModel(List<String> username,
                                                  List<String> firstName,
                                                  List<String> lastName,
                                                  List<String> email) {
    public String getError() {
        if (username != null && !username.isEmpty()) return username.get(0);
        if (firstName != null && !firstName.isEmpty()) return firstName.get(0);
        if (lastName != null && !lastName.isEmpty()) return lastName.get(0);
        if (email != null && !email.isEmpty()) return email.get(0);
        return "";
    }
}
