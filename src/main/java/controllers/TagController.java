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
@Consumes(MediaType.TEXT_PLAIN)
@Produces(MediaType.APPLICATION_JSON)
public class TagController {

    final ReceiptDao receipts;

    public TagController(ReceiptDao receipts) {
        this.receipts = receipts;
    }

    @PUT
    public void toggleTag(@PathParam("tag") String tagName, @NotNull String receiptNumber) {
        System.out.print("ELena tagname " + tagName  + " sup ");
        System.out.print("ELena tagname " + receiptNumber  + " sup ");

        int number = Integer.parseInt(receiptNumber);
        receipts.tag(tagName, number);
    }

    @GET
    public List<ReceiptResponse> getTaggedReceipts(@PathParam("tag") String tagName) {
        List<ReceiptsRecord> receiptRecords = receipts.getAllTaggedReceipts(tagName);
        System.out.print("ELena receiptRecords " + receiptRecords.size()  + " sup ");
        return receiptRecords.stream().map(ReceiptResponse::new).collect(toList());
    }
}
