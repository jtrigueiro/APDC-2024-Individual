package pt.unl.fct.di.apdc.firstwebapp.resources;

import com.google.cloud.datastore.Entity;

public class PermissionsResource {

    public PermissionsResource() {
    }

    public enum Role {
        SU(4), // Super-User
        GA(3), // Gestao da Aplicacao
        GBO(2), // Gestao Back Office
        USER(1); // Utilizador normal

        int permissionLevel;

        Role(int permissionLevel) {
            this.permissionLevel = permissionLevel;
        }
    }

    public enum State {
        ENABLED,
        DISABLED
    }

    public static boolean canChangeRole(Entity user, Entity targetUser) {
        try {
            Role role = Role.valueOf(user.getString("user_role"));
            Role targetRole = Role.valueOf(targetUser.getString("user_role"));

            switch (role) {
                case SU:
                    return true;
                case GA:
                    if (role.permissionLevel > targetRole.permissionLevel)
                        return true;
                    break;
                default:
                    return false;
            }
            return false;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean canChangeState(Entity user, Entity targetUser) {
        try {
            Role role = Role.valueOf(user.getString("user_role"));
            Role targetRole = Role.valueOf(targetUser.getString("user_role"));

            switch (role) {
                case SU:
                    return true;
                case GA:
                    if (role.permissionLevel > targetRole.permissionLevel)
                        return true;
                    break;
                case GBO:
                    if (role.permissionLevel > targetRole.permissionLevel)
                        return true;
                    break;
                default:
                    return false;
            }
            return false;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public State changeState(Entity targetUser) {
        try {
            State state = State.valueOf(targetUser.getString("user_state"));
            if (state == State.ENABLED)
                return State.DISABLED;
            else
                return State.ENABLED;
        } catch (IllegalArgumentException e) {
            return State.DISABLED;
        }
    }

    public boolean canRemoveUser(Entity user, Entity targetUser) {
        try {
            Role role = Role.valueOf(user.getString("user_role"));
            Role targetRole = Role.valueOf(targetUser.getString("user_role"));

            switch (role) {
                case SU:
                    return true;
                case GA:
                    if (role.permissionLevel > targetRole.permissionLevel)
                        return true;
                    break;
                case USER:
                    if (user == targetUser)
                        return true;
                default:
                    return false;
            }
            return false;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean canEditUser(Entity user, Entity targetUser) {
        try {
            Role role = Role.valueOf(user.getString("user_role"));
            Role targetRole = Role.valueOf(targetUser.getString("user_role"));

            switch (role) {
                case SU:
                    return true;
                case GA:
                    if (role.permissionLevel > targetRole.permissionLevel)
                        return true;
                    break;
                case GBO:
                    if (role.permissionLevel > targetRole.permissionLevel)
                        return true;
                    break;
                case USER:
                    if (user == targetUser)
                        return true;
                default:
                    return false;
            }
            return false;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
