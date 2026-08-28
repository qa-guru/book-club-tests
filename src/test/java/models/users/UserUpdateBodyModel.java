package models.users;

public record UserUpdateBodyModel(String username,
                                  String firstName,
                                  String lastName,
                                  String email) {}
