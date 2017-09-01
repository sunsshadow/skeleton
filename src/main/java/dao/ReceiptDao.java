package dao;

import api.ReceiptResponse;
import generated.tables.records.ReceiptsRecord;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.math.BigDecimal;
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

        dsl.update(RECEIPTS)
                .set(RECEIPTS.TAG, receiptsRecord.getTag().concat("," + tagName))
                .where(RECEIPTS.ID.eq(receiptNumber))
                .execute();
    }

    public List<ReceiptsRecord> getAllTaggedReceipts(String tag) {
        return dsl.selectFrom(RECEIPTS)
                .where(RECEIPTS.TAG.contains(tag))
                .fetch();
    }

    public List<ReceiptsRecord> getAllReceipts() {
        return dsl.selectFrom(RECEIPTS).fetch();
    }
}
