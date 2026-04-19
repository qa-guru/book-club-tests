package models.login;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public record EmptyFieldLoginResponseModel(List<String> username,
                                           List<String> password) {}
