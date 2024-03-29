package pt.unl.fct.di.apdc.firstwebapp.resources;

public class PermissionsResource {

    public PermissionsResource() {
    }

    public enum Role {
        SU, // Super-User
        GA, // Gestao da Aplicacao
        GBO, // Gestao Back Office
        USER // Utilizador normal
    }

    public enum State {
        ENABLED,
        DISABLED
    }

}
