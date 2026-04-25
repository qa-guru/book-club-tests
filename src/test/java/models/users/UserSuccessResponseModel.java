package models.users;


public record UserSuccessResponseModel(Integer id, String username, String firstName,
                                       String lastName, String email, String remoteAddr) {}
