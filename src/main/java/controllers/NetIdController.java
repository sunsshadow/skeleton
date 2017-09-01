package controllers;

import api.ReceiptResponse;
import generated.tables.records.ReceiptsRecord;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Path("/netid")
@Produces(MediaType.TEXT_PLAIN)
public class NetIdController {

    @GET
    public String getReceipts() {
        return "ez256";
    }
}
