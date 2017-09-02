package controllers;

import api.ReceiptResponse;
import dao.ReceiptDao;
import generated.tables.records.ReceiptsRecord;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * /tags/:tag
 */
@Path("/tags/{tag}")
@Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
@Produces(MediaType.APPLICATION_JSON)
public class TagController {

    final ReceiptDao receipts;

    public TagController(ReceiptDao receipts) {
        this.receipts = receipts;
    }

    @PUT
    public void toggleTag(@PathParam("tag") String tagName, @NotNull String receiptNumber) {
        int number = Integer.parseInt(receiptNumber);
        receipts.tag(tagName, number);
    }

    @GET
    public List<ReceiptResponse> getTaggedReceipts(@PathParam("tag") String tagName) {
        List<ReceiptsRecord> receiptRecords = receipts.getAllTaggedReceipts(tagName);
        return receiptRecords.stream().map(ReceiptResponse::new).collect(toList());
    }
}
