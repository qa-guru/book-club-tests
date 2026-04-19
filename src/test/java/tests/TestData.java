package tests;

public class TestData {

    public static final String LOGIN_USERNAME = "user8";
    public static final String LOGIN_PASSWORD = "user8";
    public static final String LOGIN_WRONG_USERNAME = "user_" + System.currentTimeMillis();
    public static final String LOGIN_WRONG_PASSWORD = "qaguru1234";

    public static final String LONG_USERNAME = "A".repeat(151);
    public static final String LONG_PASSWORD = "A".repeat(129);

    public static final String LOGIN_TOKEN_PREFIX = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";

    // Errors
    public static final String LOGIN_WRONG_CREDENTIALS_ERROR = "Invalid username or password.";
    public static final String EMPTY_FIELD_ERROR = "This field may not be blank.";
    public static final String REGISTRATION_EXISTING_USER_ERROR = "A user with that username already exists.";
    public static final String LONG_USERNAME_ERROR = "Ensure this field has no more than 150 characters.";
    public static final String LONG_PASSWORD_ERROR = "Ensure this field has no more than 128 characters.";
    public static final String WRONG_TOKEN_TYPE_ERROR = "Token has wrong type";
    public static final String VALIDATION_TOKEN_ERROR = "token_not_valid";

    public static final String REGISTRATION_IP_REGEXP =
            "^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}"
                    + "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$";
}

