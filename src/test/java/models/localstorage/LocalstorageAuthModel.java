package models.localstorage;

/**
 * Модель данных локального хранилища для ключа {@code book_club_auth}.
 */
// TODO IMPROVE / REFACTOR
public record LocalstorageAuthModel(BookClubAuth book_club_auth) {

    public record BookClubAuth(
            User user,
            String accessToken,
            String refreshToken,
            boolean isAuthenticated
    ) {}

    public record User(
            Integer id,
            String username,
            String firstName,
            String lastName,
            String email,
            String remoteAddr
    ) {}
}
