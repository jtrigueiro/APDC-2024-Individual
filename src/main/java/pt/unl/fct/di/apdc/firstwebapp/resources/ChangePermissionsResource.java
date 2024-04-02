package pt.unl.fct.di.apdc.firstwebapp.resources;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.datastore.*;
import pt.unl.fct.di.apdc.firstwebapp.util.RoleData;
import pt.unl.fct.di.apdc.firstwebapp.util.StateData;
import pt.unl.fct.di.apdc.firstwebapp.resources.PermissionsResource.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("/changepersmissions")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ChangePermissionsResource {

    private static final Logger LOG = Logger.getLogger(ChangePermissionsResource.class.getName());
    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private final KeyFactory userKeyFactory = datastore.newKeyFactory().setKind("User");
    private final KeyFactory tokenKeyFactory = datastore.newKeyFactory().setKind("Token");

    public ChangePermissionsResource() {
    }

    @PUT
    @Path("/role")
    @JsonCreator
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response changeRole(@JsonProperty("data") RoleData data) {
        Key userKey = userKeyFactory.newKey(data.token.username);
        Key targetUserKey = userKeyFactory.newKey(data.targetUsername);
        Key tokenKey = tokenKeyFactory.newKey(data.token.username);

        Entity userToken = datastore.get(tokenKey);
        if (userToken == null) {
            LOG.warning("Token not found, no login made");
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        if (!data.token.tokenID.equals(userToken.getString("token_id"))) {
            LOG.warning("User has an invalid token");
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        if (System.currentTimeMillis() > userToken.getLong("token_expirationData")) {
            LOG.warning("Token time has expired");
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        Entity user = datastore.get(userKey);
        Entity targetUser = datastore.get(targetUserKey);
        if (targetUser == null) {
            LOG.warning("Target user not found");
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        if (user == null) {
            LOG.warning("User not found");
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        if (user.getString("user_state").equals(State.DISABLED.toString())) {
            LOG.warning("User account is disabled");
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        if (!PermissionsResource.canChangeRole(user, targetUser)) {
            LOG.warning("User doesn't have permition to change target's role");
            return Response.status(Response.Status.FORBIDDEN).build();
        } else {
            Transaction txn = datastore.newTransaction();

            try {
                targetUser = Entity.newBuilder(targetUserKey, targetUser)
                        .set("user_role", data.newRole)
                        .build();
                txn.update(targetUser);
                txn.commit();
                LOG.info("User " + data.token.username + " changed user " + data.targetUsername + " role to "
                        + data.newRole);
                return Response.ok().build();

            } catch (Exception e) {
                txn.rollback();
                LOG.severe(e.getMessage());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            } finally {
                if (txn.isActive()) {
                    txn.rollback();
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
                }
            }
        }
    }

    @PUT
    @Path("/state")
    @JsonCreator
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response changeState(@JsonProperty("data") StateData data) {
        Key userKey = userKeyFactory.newKey(data.token.username);
        Key targetKey = userKeyFactory.newKey(data.targetUsername);
        Key tokenKey = tokenKeyFactory.newKey(data.token.username);

        Entity userToken = datastore.get(tokenKey);
        if (userToken == null) {
            LOG.warning("Token not found, no login made");
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        if (!data.token.tokenID.equals(userToken.getString("token_id"))) {
            LOG.warning("User has an invalid token");
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        if (System.currentTimeMillis() > userToken.getLong("token_expirationData")) {
            LOG.warning("Token time has expired");
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        Entity user = datastore.get(userKey);
        Entity targetUser = datastore.get(targetKey);
        if (targetUser == null) {
            LOG.warning("Target user not found");
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        if (user == null) {
            LOG.warning("User not found");
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        if (user.getString("user_state").equals(State.DISABLED.toString())) {
            LOG.warning("User account is disabled");
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        if (!PermissionsResource.canChangeState(user, targetUser)) {
            LOG.warning("User doesn't have permition to change target's state");
            return Response.status(Response.Status.FORBIDDEN).build();
        } else {
            Transaction txn = datastore.newTransaction();

            try {
                String targetUserNewState = PermissionsResource.changeState(targetUser).toString();
                targetUser = Entity.newBuilder(targetKey, targetUser)
                        .set("user_state", targetUserNewState)
                        .build();
                txn.update(targetUser);
                txn.commit();
                LOG.info(
                        "User " + data.token.username + " changed user " + data.targetUsername + " state to "
                                + targetUserNewState);
                return Response.ok().build();

            } catch (Exception e) {
                txn.rollback();
                LOG.severe(e.getMessage());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            } finally {
                if (txn.isActive()) {
                    txn.rollback();
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
                }
            }
        }
    }

}
