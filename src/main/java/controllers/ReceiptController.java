package controllers;

import api.CreateReceiptRequest;
import api.ReceiptResponse;
import dao.ReceiptDao;
import generated.tables.records.ReceiptsRecord;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static java.util.stream.Collectors.toList;


/**
 * Sample json:
 * {"merchant":"foo","amount": 22.45}
 */
@Path("/receipts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ReceiptController {
    final ReceiptDao receipts;

    public ReceiptController(ReceiptDao receipts) {
        this.receipts = receipts;
    }

    @POST
    public ReceiptResponse createReceipt(@Valid @NotNull CreateReceiptRequest receipt) {
        List<ReceiptsRecord> receiptRecords;
        if (receipt.base64EncodedImage == null) {
            receiptRecords = receipts.insert(receipt.merchant, receipt.amount);
        } else {
            receiptRecords = receipts.insert(receipt.merchant, receipt.amount, receipt.base64EncodedImage);
        }
        return receiptRecords.stream().map(ReceiptResponse::new).collect(toList()).get(0);
    }

    @GET
    public List<ReceiptResponse> getReceipts() {
        List<ReceiptsRecord> receiptRecords = receipts.getAllReceipts();

        if (receiptRecords.size() > 0) {
            System.out.print("receiptRecords " + receiptRecords.stream().map(ReceiptResponse::new).collect(toList()).get(0).toString()+ "image over");
        }
        return receiptRecords.stream().map(ReceiptResponse::new).collect(toList());
    }
}
