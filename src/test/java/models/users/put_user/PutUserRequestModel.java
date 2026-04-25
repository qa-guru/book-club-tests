package models.users.put_user;

import com.fasterxml.jackson.annotation.JsonInclude;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public record PutUserRequestModel(String username,
                                  String firstName,
                                  String lastName,
                                  String email) {

    public static PutUserRequestModel withoutFirstName(String username, String lastName, String email) {
        return new PutUserRequestModel(username, null, lastName, email);
    }

    public static PutUserRequestModel withoutAllFields() {
        return new PutUserRequestModel(null, null, null, null);
    }
}
