package models.users;

import java.util.List;

public record ProfileValidationErrorModel(List<String> username,
                                          List<String> firstName,
                                          List<String> lastName,
                                          List<String> email) {}
