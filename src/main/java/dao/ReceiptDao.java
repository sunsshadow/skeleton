package dao;

import generated.tables.records.ReceiptsRecord;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;
import static generated.Tables.RECEIPTS;

public class ReceiptDao {
    DSLContext dsl;

    public ReceiptDao(Configuration jooqConfig) {
        this.dsl = DSL.using(jooqConfig);
    }

    public int insert(String merchantName, BigDecimal amount) {
        ReceiptsRecord receiptsRecord = dsl
                .insertInto(RECEIPTS, RECEIPTS.MERCHANT, RECEIPTS.AMOUNT, RECEIPTS.TAG)
                .values(merchantName, amount, "")
                .returning(RECEIPTS.ID)
                .fetchOne();

        checkState(receiptsRecord != null && receiptsRecord.getId() != null, "Insert failed");

        return receiptsRecord.getId();
    }

    public void tag(String tagName, int receiptNumber) {
        final ReceiptsRecord receiptsRecord = dsl.selectFrom(RECEIPTS)
                .where(RECEIPTS.ID.eq(receiptNumber))
                .fetchOne();

        String newList = removeElementFromReceiptRecordTag(receiptsRecord, tagName);
        if (newList != null) {
            dsl.update(RECEIPTS)
                    .set(RECEIPTS.TAG, newList)
                    .where(RECEIPTS.ID.eq(receiptNumber))
                    .execute();
        } else {
            dsl.update(RECEIPTS)
                    .set(RECEIPTS.TAG, receiptsRecord.getTag().concat("," + tagName))
                    .where(RECEIPTS.ID.eq(receiptNumber))
                    .execute();
        }
    }

    public List<ReceiptsRecord> getAllTaggedReceipts(String tag) {
        return dsl.selectFrom(RECEIPTS)
                .where(RECEIPTS.TAG.contains(tag))
                .fetch();
    }

    public List<ReceiptsRecord> getAllReceipts() {
        return dsl.selectFrom(RECEIPTS).fetch();
    }

    public static String removeElementFromReceiptRecordTag(ReceiptsRecord receiptsRecord, String tagToRemove) {
        if (receiptsRecord != null && receiptsRecord.getTag() != null) {
            String[] tags = receiptsRecord.getTag().split(",");
            List<String> tagList = new LinkedList<String>(Arrays.asList(tags));
            if (tagList.contains(tagToRemove)) {
                tagList.remove(tagToRemove);
                String idList = tagList.toString();
                return idList.substring(1, idList.length() - 1).replace(", ", ",");
            }
        }

        return null;
    }

}
