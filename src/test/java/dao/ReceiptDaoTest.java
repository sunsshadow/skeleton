package dao;

import generated.tables.records.ReceiptsRecord;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class ReceiptDaoTest {

    @Test
    public void testRemoveElementFromReceiptRecordTag() {
        ReceiptsRecord receiptsRecord = new ReceiptsRecord(12, null,
                "merchant", new BigDecimal(33.44), "tag1,tag2", 1);
        String list = ReceiptDao.removeElementFromReceiptRecordTag(receiptsRecord, "tag2");
        assertEquals("tag1", list);
    }
}
