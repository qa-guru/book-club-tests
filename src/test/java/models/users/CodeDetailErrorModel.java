package models.users;

import java.util.List;

public record CodeDetailErrorModel(String detail,
                                   String code,
                                   List<TokenMessageModel> messages) {}
