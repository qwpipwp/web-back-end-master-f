package org.fatmansoft.teach.payload.request;

import java.util.Set;

import javax.validation.constraints.*;

public class IdentifyRoleRequest {
    private String username;



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
