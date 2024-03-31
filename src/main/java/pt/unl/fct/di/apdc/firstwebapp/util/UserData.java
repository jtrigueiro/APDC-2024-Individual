package pt.unl.fct.di.apdc.firstwebapp.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserData {
    public String username;
    public String targetUsername;
    public AuthToken token;

    public UserData() {
    }

    @JsonCreator
    public UserData(@JsonProperty("username") String username,
            @JsonProperty("targetUsername") String targetUsername, @JsonProperty("token") AuthToken token) {
        this.username = username;
        this.targetUsername = targetUsername;
        this.token = token;
    }
}
