package models.records;


public record RegistrationResponseRecordsModel(Integer id, String username, String firstName,
                                               String lastName, String email, String remoteAddr) {}
