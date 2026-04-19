package models.login;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EmptyFieldLoginResponseModel(List<String> username,
                                           List<String> password) {

    public String getError() {
        if (username != null && !username.isEmpty()) return username.get(0);
        if (password != null && !password.isEmpty()) return password.get(0);
        return "";
    }
}
