package com.kyawgyi.role.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kyawgyi.role.model.Users;
import lombok.Data;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReqRes {

    private int statusCode;
    private String error;
    private String message;
    private String token;
    private String refreshToken;
    private String expirationTime;
    private String name;
    private String email;
    private String city;
    private String role;
    private String password;
    private Users users;
    private List<Users> usersList;
    private UserInfo user;

    @Data
    public static class UserInfo {
        private int id;
        private String name;
        private String email;
        private String city;
        private String role;
    }


}
