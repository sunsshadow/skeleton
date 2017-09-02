package controllers;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/netid")
@Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
public class NetIdController {

    @GET
    public String getReceipts() {
        return "ez256";
    }
}
