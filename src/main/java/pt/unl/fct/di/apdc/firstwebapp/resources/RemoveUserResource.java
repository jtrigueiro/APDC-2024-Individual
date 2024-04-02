package pt.unl.fct.di.apdc.firstwebapp.resources;

import com.google.cloud.datastore.*;
import pt.unl.fct.di.apdc.firstwebapp.util.DeleteData;
import pt.unl.fct.di.apdc.firstwebapp.resources.PermissionsResource.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("/remove")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RemoveUserResource {

    private static final Logger LOG = Logger.getLogger(RemoveUserResource.class.getName());
    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private final KeyFactory userKeyFactory = datastore.newKeyFactory().setKind("User");
    private final KeyFactory tokenKeyFactory = datastore.newKeyFactory().setKind("Token");

    public RemoveUserResource() {
    }

    @DELETE
    @Path("/user")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response removeUser(DeleteData data) {

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

        if (!PermissionsResource.canRemoveUser(user, targetUser)) {
            LOG.warning("User doesn't have permition to delete target user");
            return Response.status(Response.Status.FORBIDDEN).build();
        } else {
            Transaction txn = datastore.newTransaction();
            try {
                txn.delete(targetUserKey);
                txn.delete(tokenKeyFactory.newKey(targetUser.getString("user_username")));
                LOG.info("User " + data.token.username + " was removed by " + data.targetUsername);
                txn.commit();
                return Response.status(Response.Status.OK).build();
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
