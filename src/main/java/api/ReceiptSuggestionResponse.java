package api;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

/**
 * Represents the result of an OCR parse
 */
public class ReceiptSuggestionResponse {
    @JsonProperty
    public final String merchantName;

    @JsonProperty
    public final BigDecimal amount;

    @JsonProperty
    public final int[] boundingBox;


    public ReceiptSuggestionResponse(String merchantName, BigDecimal amount, int[] box) {
        this.merchantName = merchantName;
        this.amount = amount;
        this.boundingBox = box;
    }
}
