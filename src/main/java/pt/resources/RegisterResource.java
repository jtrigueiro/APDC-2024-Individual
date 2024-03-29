package pt.resources;

import java.util.logging.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.*;

import org.apache.commons.codec.digest.DigestUtils;

import pt.unl.fct.di.apdc.firstwebapp.resources.util.RegisterData;

@Path("/register")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RegisterResource {
    /**
     * Logger Object
     */
    private static final Logger LOG = Logger.getLogger(ComputationResource.class.getName());

    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    public RegisterResource() {
    }

    @POST
    @Path("/v1")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response doRegistrationV1(RegisterData data) {
        LOG.fine("Attempt to register user:" + data.username);
        if (!data.validRegistration())
            return Response.status(Response.Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
        Transaction txn = datastore.newTransaction();
        try {
            // Creates an entity user form the data. THe key is username
            Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
            Entity user = txn.get(userKey);
            if (user != null) {
                txn.rollback();
                return Response.status(Response.Status.BAD_REQUEST).entity("User already eists.").build();
            } else {
                // Check the number of existing users
                Query<Entity> query = Query.newEntityQueryBuilder()
                        .setKind("User")
                        .build();
                QueryResults<Entity> results = datastore.run(query);
                int userCount = 0;
                while (results.hasNext()) {
                    results.next();
                    userCount++;
                }

                if (userCount > 4) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("Maximum of 4 users reached.")
                            .build();
                }
                user = Entity.newBuilder(userKey)
                        .set("user_name", data.name)
                        .set("user_pwd", DigestUtils.sha512Hex(data.password))
                        .set("user_email", data.email)
                        .set("user_creation_time", Timestamp.now())
                        .build();

                txn.add(user);
                LOG.info("User registered " + data.username);
                txn.commit();
                return Response.ok("{}").build();
            }

        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }

    }

}
