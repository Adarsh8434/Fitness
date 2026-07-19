package com.example.gateway.user;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    private String id;
    private String email;
    private String password;
    private String firstName;
    private String LastName;
    private String keyCloakId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public void setkeyCloakId(String keyCloakId) {
     this.keyCloakId=keyCloakId;
    }
}
