package pt.unl.fct.di.apdc.firstwebapp.util;

public class ChangeRoleData {
    public String username;
    public String targetUsername;
    public String newRole;
    public AuthToken token;

    public ChangeRoleData() {
    }

    public ChangeRoleData(String username, String targetUsername, String newRole, AuthToken token) {
        this.username = username;
        this.targetUsername = targetUsername;
        this.newRole = newRole;
        this.token = token;
    }
}