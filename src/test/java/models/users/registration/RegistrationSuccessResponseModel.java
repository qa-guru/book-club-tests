package models.users.registration;


public record RegistrationSuccessResponseModel(Integer id, String username, String firstName,
                                               String lastName, String email, String remoteAddr) {}
