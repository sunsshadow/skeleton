package api;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.tables.records.ReceiptsRecord;
import org.jvnet.hk2.annotations.Optional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * This is an API Object.  Its purpose is to model the JSON API that we expose.
 * This class is NOT used for storing in the Database.
 *
 * This ReceiptResponse in particular is the model of a Receipt that we expose to users of our API
 *
 * Any properties that you want exposed when this class is translated to JSON must be
 * annotated with {@link JsonProperty}
 */
public class ReceiptResponse {
    @JsonProperty
    Integer id;

    @JsonProperty
    String merchant;

    @JsonProperty
    BigDecimal amount;

    @JsonProperty
    long time;

    @JsonProperty
    List<String> tags;

    @JsonProperty
    @Optional
    String image;

    public ReceiptResponse(ReceiptsRecord dbRecord) {
        this.merchant = dbRecord.getMerchant();
        this.amount = dbRecord.getAmount();
        this.time = dbRecord.getUploaded().getTime();
        this.id = dbRecord.getId();
        this.tags = toStringArray(dbRecord.getTag());
        this.image = dbRecord.getImage();
    }

    private List<String> toStringArray(final String toBeArr) {
        List<String> list = new ArrayList<>();

        if (!"".equals(toBeArr) && toBeArr != null) {
            String[] tags = toBeArr.split(",");

            for (String tag : tags) {
                if (!"".equals(tag)) {
                    list.add(tag);
                }
            }
        }
        return list;
    }
}
