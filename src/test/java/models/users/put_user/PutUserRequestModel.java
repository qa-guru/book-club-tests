package models.users.put_user;

public record PutUserRequestModel(String username,
                                  String firstName,
                                  String lastName,
                                  String email) {}
