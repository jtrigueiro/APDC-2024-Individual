package pt.unl.fct.di.apdc.firstwebapp.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RoleData {
    public String username;
    public String targetUsername;
    public String newRole;
    public AuthToken token;

    public RoleData() {
    }

    @JsonCreator
    public RoleData(@JsonProperty("username") String username,
            @JsonProperty("targetUsername") String targetUsername, @JsonProperty("newRole") String newRole,
            @JsonProperty("token") AuthToken token) {
        this.username = username;
        this.targetUsername = targetUsername;
        this.newRole = newRole;
        this.token = token;
    }
}