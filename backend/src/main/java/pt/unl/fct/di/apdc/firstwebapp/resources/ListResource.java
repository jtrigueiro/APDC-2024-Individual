package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.datastore.*;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.firstwebapp.util.ListData;
import pt.unl.fct.di.apdc.firstwebapp.util.UserData;
import pt.unl.fct.di.apdc.firstwebapp.resources.PermissionsResource.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/list")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ListResource {

    private static final Logger LOG = Logger.getLogger(ListResource.class.getName());
    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private final KeyFactory userKeyFactory = datastore.newKeyFactory().setKind("User");
    private final KeyFactory tokenKeyFactory = datastore.newKeyFactory().setKind("Token");
    private final Gson g = new Gson();

    public ListResource() {
    }

    @POST
    @Path("/users")
    @JsonCreator
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response listUsers(@JsonProperty("data") ListData data) {
        Key userKey = userKeyFactory.newKey(data.token.username);
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

        if (user == null) {
            LOG.warning("User not found");
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        if (user.getString("user_state").equals(State.DISABLED.toString())) {
            LOG.warning("User account is disabled");
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        
        Query<Entity> query = Query.newEntityQueryBuilder()
                .setKind("User")
                .build();
        QueryResults<Entity> results = datastore.run(query);
        if (!results.hasNext()) {
            LOG.warning("No users found");
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            List<UserData> usersList = new ArrayList<>();
            while (results.hasNext()) {
                Entity targetUserEntity = results.next();
                if (PermissionsResource.canListUsers(user, targetUserEntity)) {
                    if (user.getString("user_role").equals(Role.USER.toString())) {
                        usersList.add(new UserData(targetUserEntity.getString("user_username"),
                                targetUserEntity.getString("user_email"), targetUserEntity.getString("user_name")));
                    } else {
                        usersList.add(new UserData(targetUserEntity.getString("user_username"),
                                targetUserEntity.getString("user_name"),
                                targetUserEntity.getString("user_phone_number"),
                                targetUserEntity.getString("user_email"),
                                targetUserEntity.getString("user_job"),
                                targetUserEntity.getString("user_work_place"),
                                targetUserEntity.getString("user_address"),
                                targetUserEntity.getString("user_postal_code"),
                                targetUserEntity.getString("user_NIF"),
                                targetUserEntity.getBoolean("user_is_private"),
                                targetUserEntity.getString("user_role"),
                                targetUserEntity.getString("user_state")));
                    }
                }
            }
            LOG.info("User " + data.token.username + " listed the users");
            return Response.ok(g.toJson(usersList)).build();
        }
    }
}
