package tests;

public class TestData {

    public static final String LOGIN_ID = "2";
    public static final String LOGIN_USERNAME = "user8";
    public static final String LOGIN_PASSWORD = "user8";
    public static final String LOGIN_WRONG_PASSWORD = "qaguru1234";

    public static final String LOGIN_TOKEN_PREFIX = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
    public static final String LOGIN_WRONG_CREDENTIALS_ERROR = "Invalid username or password.";

    public static final String REGISTRATION_EXISTING_USER_ERROR =
            "A user with that username already exists.";

    public static final String REGISTRATION_IP_REGEXP =
            "^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}"
                    + "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$";

    public static final String AUTH_CREDENTIALS_NOT_PROVIDED =
            "Authentication credentials were not provided.";
    public static final String TOKEN_NOT_VALID =
            "Given token not valid for any token type";
    public static final String TOKEN_NOT_VALID_CODE = "token_not_valid";
    public static final String INVALID_TOKEN = "Invalid token.";
    public static final String USER_NOT_FOUND = "User not found";
    public static final String USER_NOT_FOUND_CODE = "user_not_found";
    public static final String FIELD_REQUIRED = "This field is required.";
    public static final String FIELD_MAY_NOT_BE_BLANK = "This field may not be blank.";
    public static final String FIELD_MAY_NOT_BE_NULL = "This field may not be null.";
    public static final String VALID_EMAIL_ERROR = "Enter a valid email address.";
    public static final String USERNAME_INVALID_CHARS_ERROR =
            "Enter a valid username. This value may contain only letters, numbers, and @/./+/-/_ characters.";
    public static final String FIELD_MAX_150_ERROR =
            "Ensure this field has no more than 150 characters.";
    public static final String FIELD_MAX_254_ERROR =
            "Ensure this field has no more than 254 characters.";
    public static final String METHOD_POST_NOT_ALLOWED = "Method \"POST\" not allowed.";
    public static final String JSON_PARSE_ERROR_PREFIX = "JSON parse error";
    public static final String[] USERS_ME_ALLOWED_METHODS = {
            "GET", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS"
    };
}

