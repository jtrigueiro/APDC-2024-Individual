package pt.unl.fct.di.apdc.firstwebapp.util;

import pt.unl.fct.di.apdc.firstwebapp.resources.PermissionsResource.Role;
import pt.unl.fct.di.apdc.firstwebapp.resources.PermissionsResource.State;

public class UserData {
    public String username, name, phoneNumber, email, job, workPlace, address, postalCode, NIF;
    public String role;
    public String state;
    public boolean isPrivate;

    public UserData() {

    }

    public UserData(String username, String name, String phoneNumber, String email, String job,
            String workPlace, String address, String postalCode, String NIF, boolean isPrivate, String role,
            String state) {
        this.username = username;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.job = job;
        this.workPlace = workPlace;
        this.address = address;
        this.postalCode = postalCode;
        this.NIF = NIF;
        this.isPrivate = isPrivate;
        this.role = role;
        this.state = state;

    }

    public UserData(String username, String email, String name) {
        this.username = username;
        this.email = email;
        this.name = name;

        this.phoneNumber = "";
        this.job = "";
        this.workPlace = "";
        this.address = "";
        this.postalCode = "";
        this.NIF = "";
        this.isPrivate = false;
        this.role = "";
        this.state = "";
    }
}
