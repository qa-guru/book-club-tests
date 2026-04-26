package models.auth.logout;

import java.util.List;

public record LogoutValidationErrorResponseModel(List<String> refresh) {
}
