package models.auth.login;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public record LoginValidationErrorResponseModel(List<String> username,
                                                List<String> password) {}
