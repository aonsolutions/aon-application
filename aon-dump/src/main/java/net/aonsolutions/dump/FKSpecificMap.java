package net.aonsolutions.dump;

import static com.esferalia.aon.jooq.Keys.KEY_ACCOUNT_PRIMARY;
import static com.esferalia.aon.jooq.Keys.KEY_BANK_CONCEPT_PRIMARY;
import static com.esferalia.aon.jooq.Keys.KEY_COMMERCIAL_TRACKING_PRIMARY;
import static com.esferalia.aon.jooq.Keys.KEY_DELIVERY_DETAIL_PRIMARY;
import static com.esferalia.aon.jooq.Keys.KEY_FBATCH_PRIMARY;
import static com.esferalia.aon.jooq.Keys.KEY_FINANCE_TRACKING_PRIMARY;
import static com.esferalia.aon.jooq.Keys.KEY_INCOME_DETAIL_PRIMARY;
import static com.esferalia.aon.jooq.Keys.KEY_INVENTORY_DETAIL_PRIMARY;
import static com.esferalia.aon.jooq.Keys.KEY_MK_ACTION_TARGET_PRIMARY;
import static com.esferalia.aon.jooq.Keys.KEY_NOTICE_PRIMARY;
import static com.esferalia.aon.jooq.Keys.KEY_OFFER_DETAIL_PRIMARY;
import static com.esferalia.aon.jooq.Keys.KEY_PREPAYMENT_PRIMARY;
import static com.esferalia.aon.jooq.Keys.KEY_PROJECT_RESERVATION_PRIMARY;
import static com.esferalia.aon.jooq.Keys.KEY_PROPOSAL_DETAIL_PRIMARY;
import static com.esferalia.aon.jooq.Keys.KEY_PURCHASE_DETAIL_PRIMARY;
import static com.esferalia.aon.jooq.Keys.KEY_SALARY_PRIMARY;
import static com.esferalia.aon.jooq.Keys.KEY_SALES_DETAIL_PRIMARY;
import static com.esferalia.aon.jooq.Keys.KEY_TASK_PRIMARY;
import static com.esferalia.aon.jooq.tables.Alarm.ALARM;
import static com.esferalia.aon.jooq.tables.BankStatementLink.BANK_STATEMENT_LINK;
import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.PurchaseDetail.PURCHASE_DETAIL;
import static com.esferalia.aon.jooq.tables.WarehouseTransfer.WAREHOUSE_TRANSFER;

import java.util.HashMap;

import org.jooq.Condition;
import org.jooq.ForeignKey;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;

import net.aonsolutions.dump.WeakForeignKeyProvider.ReferenceImpl;

public class FKSpecificMap {

    HashMap<String, HashMap<Condition, ForeignKey<? extends Record,?>>> fkMap;
   
    public FKSpecificMap(){
    	
    	fkMap = new HashMap<String, HashMap<Condition, ForeignKey<? extends Record,?>>>();
    	
    	rellenarFKMpa (fkMap);
    }

	private void rellenarFKMpa(HashMap<String, HashMap<Condition, ForeignKey<? extends Record, ?>>> fkMap) {
		// TODO Auto-generated method stub
		fkMap.put("INVOICE_DETAIL", new HashMap<Condition, ForeignKey<?,?>>());
		fkMap.put("BANK_STATEMENT_LINK", new HashMap<Condition, ForeignKey<?,?>>());
		fkMap.put("FINACE", new HashMap<Condition, ForeignKey<?,?>>());
//		fkMap.put("FINANCE_TRACKING", new HashMap<String, ForeignKey<?,?>>());
		fkMap.put("ALARM", new HashMap<Condition, ForeignKey<?,?>>());
		fkMap.put("PURCHASE_DETAIL", new HashMap<Condition, ForeignKey<?,?>>());
		fkMap.put("WAREHOUSE_TRANSFER", new HashMap<Condition, ForeignKey<?,?>>());
	
		fkMap.get("INVOICE_DETAIL").put((DSL.field("source")).equal(1), createForeignKey(KEY_PURCHASE_DETAIL_PRIMARY, INVOICE_DETAIL, "WEAK_FK_INVOICE_DETAIL_PURCHASE_DETAIL", INVOICE_DETAIL.SOURCE_ID));
		fkMap.get("INVOICE_DETAIL").put((DSL.field("source")).equal(2), createForeignKey(KEY_SALES_DETAIL_PRIMARY, INVOICE_DETAIL, "WEAK_FK_INVOICE_DETAIL_SALES_DETAIL", INVOICE_DETAIL.SOURCE_ID));
		fkMap.get("INVOICE_DETAIL").put((DSL.field("source")).equal(3), createForeignKey(KEY_DELIVERY_DETAIL_PRIMARY, INVOICE_DETAIL, "WEAK_FK_INVOICE_DETAIL_DELIVERY_DETAIL", INVOICE_DETAIL.SOURCE_ID));
		fkMap.get("INVOICE_DETAIL").put((DSL.field("source")).equal(4), createForeignKey(KEY_INCOME_DETAIL_PRIMARY, INVOICE_DETAIL, "WEAK_FK_INVOICE_DETAIL_INCOME_DETAIL", INVOICE_DETAIL.SOURCE_ID));
		fkMap.get("INVOICE_DETAIL").put((DSL.field("source")).equal(8), createForeignKey(KEY_OFFER_DETAIL_PRIMARY, INVOICE_DETAIL, "WEAK_FK_INVOICE_DETAIL_OFFER_DETAIL", INVOICE_DETAIL.SOURCE_ID));
		fkMap.get("INVOICE_DETAIL").put((DSL.field("source")).equal(9), createForeignKey(KEY_PROJECT_RESERVATION_PRIMARY, INVOICE_DETAIL, "WEAK_FK_INVOICE_DETAIL_PROJECT_RESERVATION", INVOICE_DETAIL.SOURCE_ID));
		
		fkMap.get("BANK_STATEMENT_LINK").put((DSL.field("source")).equal(0), createForeignKey(KEY_FINANCE_TRACKING_PRIMARY, BANK_STATEMENT_LINK, "WEAK_FK_BANK_STATEMENT_LINK_FINANCE_TRACKING", BANK_STATEMENT_LINK.SOURCE_ID));
		fkMap.get("BANK_STATEMENT_LINK").put((DSL.field("source")).equal(1), createForeignKey(KEY_FBATCH_PRIMARY, BANK_STATEMENT_LINK, "WEAK_FK_BANK_STATEMENT_LINK_FBATCH", BANK_STATEMENT_LINK.SOURCE_ID));
		fkMap.get("BANK_STATEMENT_LINK").put((DSL.field("source")).equal(2), createForeignKey(KEY_BANK_CONCEPT_PRIMARY, BANK_STATEMENT_LINK, "WEAK_FK_BANK_STATEMENT_LINK_BANK_CONCEPT", BANK_STATEMENT_LINK.SOURCE_ID));
		fkMap.get("BANK_STATEMENT_LINK").put((DSL.field("source")).equal(3), createForeignKey(KEY_ACCOUNT_PRIMARY, BANK_STATEMENT_LINK, "WEAK_FK_BANK_STATEMENT_LINK_ACCOUNT", BANK_STATEMENT_LINK.SOURCE_ID));
		
		fkMap.get("FINACE").put((DSL.field("payroll")).equal(1), createForeignKey(KEY_SALARY_PRIMARY, FINANCE, "WEAK_FK_FINANCE_SALARY", FINANCE.SOURCE_ID));
		fkMap.get("FINACE").put((DSL.field("prepayment")).equal(1), createForeignKey(KEY_PREPAYMENT_PRIMARY, FINANCE, "WEAK_FK_FINANCE_PREPAYMENT", FINANCE.SOURCE_ID));
		
		fkMap.get("ALARM").put((DSL.field("source")).equal(0), createForeignKey(KEY_NOTICE_PRIMARY, ALARM, "WEAK_FK_ALARM_NOTICE", ALARM.SOURCE_ID));
		fkMap.get("ALARM").put((DSL.field("source")).equal(1), createForeignKey(KEY_TASK_PRIMARY, ALARM, "WEAK_FK_ALARM_TASK", ALARM.SOURCE_ID));
		fkMap.get("ALARM").put((DSL.field("source")).equal(3), createForeignKey(KEY_COMMERCIAL_TRACKING_PRIMARY, ALARM, "WEAK_FK_ALARM_COMMERCIAL_TRACKING", ALARM.SOURCE_ID));
		fkMap.get("ALARM").put((DSL.field("source")).equal(4), createForeignKey(KEY_MK_ACTION_TARGET_PRIMARY, ALARM, "WEAK_FK_ALARM_MK_ACTION_TARGET", ALARM.SOURCE_ID));
		
		fkMap.get("PURCHASE_DETAIL").put((DSL.field("source")).equal(0), createForeignKey(KEY_PROPOSAL_DETAIL_PRIMARY, PURCHASE_DETAIL, "WEAK_FK_PURCHASE_DETAIL_PROPOSAL_DETAIL", PURCHASE_DETAIL.SOURCE_ID));
		fkMap.get("PURCHASE_DETAIL").put((DSL.field("source")).equal(1), createForeignKey(KEY_PURCHASE_DETAIL_PRIMARY, PURCHASE_DETAIL, "WEAK_FK_PURCHASE_DETAIL_PURCHASE_DETAIL", PURCHASE_DETAIL.SOURCE_ID));
		fkMap.get("PURCHASE_DETAIL").put((DSL.field("source")).equal(2), createForeignKey(KEY_SALES_DETAIL_PRIMARY, PURCHASE_DETAIL, "WEAK_FK_PURCHASE_DETAIL_SALES_DETAIL", PURCHASE_DETAIL.SOURCE_ID));
		
		fkMap.get("WAREHOUSE_TRANSFER").put((DSL.field("source")).equal(1), createForeignKey(KEY_INVENTORY_DETAIL_PRIMARY, WAREHOUSE_TRANSFER, "WEAK_FK_WAREHOUSE_TRANSFER_INVENTORY_DETAIL", WAREHOUSE_TRANSFER.SOURCE_ID));
		fkMap.get("WAREHOUSE_TRANSFER").put((DSL.field("source")).equal(2), createForeignKey(KEY_PURCHASE_DETAIL_PRIMARY, WAREHOUSE_TRANSFER, "WEAK_FK_WAREHOUSE_TRANSFER_PURCHASE_DETAIL", WAREHOUSE_TRANSFER.SOURCE_ID));
		fkMap.get("WAREHOUSE_TRANSFER").put((DSL.field("source")).equal(3), createForeignKey(KEY_INVENTORY_DETAIL_PRIMARY, WAREHOUSE_TRANSFER, "WEAK_FK_WAREHOUSE_TRANSFER_INVENTORY_DETAIL", WAREHOUSE_TRANSFER.SOURCE_ID));
			
	}

	protected static <R extends Record, U extends Record> ForeignKey<R, U> createForeignKey(UniqueKey<U> key,
            Table<R> table, String name, TableField<R, ?>... fields) {
        ForeignKey<R, U> result = new ReferenceImpl<R, U>(key, table, name, fields);
        return result;
    }
}