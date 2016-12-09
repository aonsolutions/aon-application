package net.aonsolutions.dump;

import static com.esferalia.aon.jooq.Keys.KEY_ACCOUNT_ENTRY_PRIMARY;
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
import static com.esferalia.aon.jooq.tables.FinanceTracking.FINANCE_TRACKING;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.PurchaseDetail.PURCHASE_DETAIL;
import static com.esferalia.aon.jooq.tables.WarehouseTransfer.WAREHOUSE_TRANSFER;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jooq.Constraint;
import org.jooq.ForeignKey;
import org.jooq.Key;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.InvoiceDetail;
import com.esferalia.aon.jooq.tables.records.InvoiceDetailRecord;

public class WeakForeignKeyProvider {

    protected static <R extends Record, U extends Record> ForeignKey<R, U> createForeignKey(UniqueKey<U> key,
            Table<R> table, String name, TableField<R, ?>... fields) {
        ForeignKey<R, U> result = new ReferenceImpl<R, U>(key, table, name, fields);
        return result;
    }

    abstract static class AbstractKey<R extends Record> implements Key<R> {

        /**
         * Generated UID
         */
        private static final long serialVersionUID = 8176874459141379340L;

        private final String name;
        private final Table<R> table;
        private final TableField<R, ?>[] fields;

        @SafeVarargs

        AbstractKey(Table<R> table, TableField<R, ?>... fields) {
            this(table, null, fields);
        }

        @SafeVarargs

        AbstractKey(Table<R> table, String name, TableField<R, ?>... fields) {
            this.table = table;
            this.name = name;
            this.fields = fields;
        }

        @Override
        public final String getName() {
            return name;
        }

        @Override
        public final Table<R> getTable() {
            return table;
        }

        @Override
        public final List<TableField<R, ?>> getFields() {
            return Arrays.asList(fields);
        }

        @Override
        public final TableField<R, ?>[] getFieldsArray() {
            return fields;
        }
    }

    final static class ReferenceImpl<R extends Record, O extends Record> extends AbstractKey<R>
            implements ForeignKey<R, O> {

        private static final long serialVersionUID = 3636724364192618701L;

        private final UniqueKey<O> key;

        @SafeVarargs

        ReferenceImpl(UniqueKey<O> key, Table<R> table, TableField<R, ?>... fields) {
            this(key, table, null, fields);
        }

        @SafeVarargs

        ReferenceImpl(UniqueKey<O> key, Table<R> table, String name, TableField<R, ?>... fields) {
            super(table, name, fields);

            this.key = key;
        }

        @Override
        public final UniqueKey<O> getKey() {
            return key;
        }

        @Override
        public final O fetchParent(R record) {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override

        @SafeVarargs

        public final Result<O> fetchParents(R... records) {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public final Result<R> fetchChildren(O record) {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override

        @SafeVarargs

        public final Result<R> fetchChildren(O... records) {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public final Result<O> fetchParents(Collection<? extends R> records) {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public final Result<R> fetchChildren(Collection<? extends O> records) {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public Constraint constraint() {
            return DSL.constraint(getName()).foreignKey(getFieldsArray()).references(key.getTable(),
                    key.getFieldsArray());
        }
    }

    final class UniqueKeyImpl<R extends Record> extends AbstractKey<R> implements UniqueKey<R> {

        /**
         * Generated UID
         */
        private static final long    serialVersionUID = 162853300137140844L;

        final List<ForeignKey<?, R>> references;


        @SafeVarargs

        UniqueKeyImpl(Table<R> table, TableField<R, ?>... fields) {
            this(table, null, fields);
        }


        @SafeVarargs

        UniqueKeyImpl(Table<R> table, String name, TableField<R, ?>... fields) {
            super(table, name, fields);

            this.references = new ArrayList<ForeignKey<?, R>>();
        }

        @Override
        public final boolean isPrimary() {
            return equals(getTable().getPrimaryKey());
        }

        @Override
        public final List<ForeignKey<?, R>> getReferences() {
            return Collections.unmodifiableList(references);
        }

        @Override
        public Constraint constraint() {
            if (isPrimary())
                return DSL.constraint(getName()).primaryKey(getFieldsArray());
            else
                return DSL.constraint(getName()).unique(getFieldsArray());
        }
    }
        

    public static final List INVOICE_DETAIL_WEAK_FK = new LinkedList();
    
    static {
        INVOICE_DETAIL_WEAK_FK.add(createForeignKey(KEY_PURCHASE_DETAIL_PRIMARY, INVOICE_DETAIL, "WEAK_FK_INVOICE_DETAIL_PURCHASE_DETAIL", INVOICE_DETAIL.SOURCE_ID));
        INVOICE_DETAIL_WEAK_FK.add(createForeignKey(KEY_SALES_DETAIL_PRIMARY, INVOICE_DETAIL, "WEAK_FK_INVOICE_DETAIL_SALES_DETAIL", INVOICE_DETAIL.SOURCE_ID));
        INVOICE_DETAIL_WEAK_FK.add(createForeignKey(KEY_DELIVERY_DETAIL_PRIMARY, INVOICE_DETAIL, "WEAK_FK_INVOICE_DETAIL_DELIVERY_DETAIL", INVOICE_DETAIL.SOURCE_ID));
        INVOICE_DETAIL_WEAK_FK.add(createForeignKey(KEY_INCOME_DETAIL_PRIMARY, INVOICE_DETAIL, "WEAK_FK_INVOICE_DETAIL_INCOME_DETAIL", INVOICE_DETAIL.SOURCE_ID));
        INVOICE_DETAIL_WEAK_FK.add(createForeignKey(KEY_OFFER_DETAIL_PRIMARY, INVOICE_DETAIL, "WEAK_FK_INVOICE_DETAIL_OFFER_DETAIL", INVOICE_DETAIL.SOURCE_ID));
        INVOICE_DETAIL_WEAK_FK.add(createForeignKey(KEY_PROJECT_RESERVATION_PRIMARY, INVOICE_DETAIL, "WEAK_FK_INVOICE_DETAIL_PROJECT_RESERVATION", INVOICE_DETAIL.SOURCE_ID));
    }
    
	public static final List BANK_STATEMENT_LINK_WEAK_FK = new LinkedList();
	    
	    static {
	    	BANK_STATEMENT_LINK_WEAK_FK.add(createForeignKey(KEY_FINANCE_TRACKING_PRIMARY, BANK_STATEMENT_LINK, "WEAK_FK_BANK_STATEMENT_LINK_FINANCE_TRACKING", BANK_STATEMENT_LINK.SOURCE_ID));
	    	BANK_STATEMENT_LINK_WEAK_FK.add(createForeignKey(KEY_FBATCH_PRIMARY, BANK_STATEMENT_LINK, "WEAK_FK_BANK_STATEMENT_LINK_FBATCH", BANK_STATEMENT_LINK.SOURCE_ID));
	    	BANK_STATEMENT_LINK_WEAK_FK.add(createForeignKey(KEY_BANK_CONCEPT_PRIMARY, BANK_STATEMENT_LINK, "WEAK_FK_BANK_STATEMENT_LINK_BANK_CONCEPT", BANK_STATEMENT_LINK.SOURCE_ID));
	    	BANK_STATEMENT_LINK_WEAK_FK.add(createForeignKey(KEY_ACCOUNT_PRIMARY, BANK_STATEMENT_LINK, "WEAK_FK_BANK_STATEMENT_LINK_ACCOUNT", BANK_STATEMENT_LINK.SOURCE_ID));
	    }
	    
	public static final List FINANCE_WEAK_FK = new LinkedList();
		    
		    static {
		    	FINANCE_WEAK_FK.add(createForeignKey(KEY_SALARY_PRIMARY, FINANCE, "WEAK_FK_FINANCE_SALARY", FINANCE.SOURCE_ID));
		    	FINANCE_WEAK_FK.add(createForeignKey(KEY_PREPAYMENT_PRIMARY, FINANCE, "WEAK_FK_FINANCE_PREPAYMENT", FINANCE.SOURCE_ID));
		    }
		    
	public static final List FINANCE_TRACKING_WEAK_FK = new LinkedList();
		    
		    static {
		    	FINANCE_TRACKING_WEAK_FK.add(createForeignKey(KEY_ACCOUNT_ENTRY_PRIMARY, FINANCE_TRACKING, "WEAK_FK_FINANCE_TRACKING_ACCOUNT_ENTRY", FINANCE_TRACKING.DESCRIPTION));
		    }
	    
	public static final List ALARM_WEAK_FK = new LinkedList();
		    
		    static {
		    	ALARM_WEAK_FK.add(createForeignKey(KEY_NOTICE_PRIMARY, ALARM, "WEAK_FK_ALARM_NOTICE", ALARM.SOURCE_ID));
		    	ALARM_WEAK_FK.add(createForeignKey(KEY_TASK_PRIMARY, ALARM, "WEAK_FK_ALARM_TASK", ALARM.SOURCE_ID));
		    	ALARM_WEAK_FK.add(createForeignKey(KEY_COMMERCIAL_TRACKING_PRIMARY, ALARM, "WEAK_FK_ALARM_COMMERCIAL_TRACKING", ALARM.SOURCE_ID));
		    	ALARM_WEAK_FK.add(createForeignKey(KEY_MK_ACTION_TARGET_PRIMARY, ALARM, "WEAK_FK_ALARM_MK_ACTION_TARGET", ALARM.SOURCE_ID));
		    }
	
	public static final List PURCHASE_DETAIL_WEAK_FK = new LinkedList();
		    
		    static {
		    	PURCHASE_DETAIL_WEAK_FK.add(createForeignKey(KEY_PROPOSAL_DETAIL_PRIMARY, PURCHASE_DETAIL, "WEAK_FK_PURCHASE_DETAIL_PROPOSAL_DETAIL", PURCHASE_DETAIL.SOURCE_ID));
		    	PURCHASE_DETAIL_WEAK_FK.add(createForeignKey(KEY_PURCHASE_DETAIL_PRIMARY, PURCHASE_DETAIL, "WEAK_FK_PURCHASE_DETAIL_PURCHASE_DETAIL", PURCHASE_DETAIL.SOURCE_ID));
		    	PURCHASE_DETAIL_WEAK_FK.add(createForeignKey(KEY_SALES_DETAIL_PRIMARY, PURCHASE_DETAIL, "WEAK_FK_PURCHASE_DETAIL_SALES_DETAIL", PURCHASE_DETAIL.SOURCE_ID));
		    }
		    
	public static final List WAREHOUSE_TRANSFER_WEAK_FK = new LinkedList();
		    
		    static {
		    	WAREHOUSE_TRANSFER_WEAK_FK.add(createForeignKey(KEY_INVENTORY_DETAIL_PRIMARY, WAREHOUSE_TRANSFER, "WEAK_FK_WAREHOUSE_TRANSFER_INVENTORY_DETAIL", WAREHOUSE_TRANSFER.SOURCE_ID));
		    	WAREHOUSE_TRANSFER_WEAK_FK.add(createForeignKey(KEY_PURCHASE_DETAIL_PRIMARY, WAREHOUSE_TRANSFER, "WEAK_FK_WAREHOUSE_TRANSFER_PURCHASE_DETAIL", WAREHOUSE_TRANSFER.SOURCE_ID));
		    	WAREHOUSE_TRANSFER_WEAK_FK.add(createForeignKey(KEY_INVENTORY_DETAIL_PRIMARY, WAREHOUSE_TRANSFER, "WEAK_FK_WAREHOUSE_TRANSFER_INVENTORY_DETAIL", WAREHOUSE_TRANSFER.SOURCE_ID));
		    }


    
    public static void main(String[] args) {
        
        List<ForeignKey<InvoiceDetailRecord,?>> references = new LinkedList<ForeignKey<InvoiceDetailRecord,?>>();
        references.addAll(InvoiceDetail.INVOICE_DETAIL.getReferences());
        for (Object o : INVOICE_DETAIL_WEAK_FK) {
            ForeignKey<InvoiceDetailRecord, ?> fk = (ForeignKey<InvoiceDetailRecord, ?>) o;
            references.add(fk);
        }
        
        for(ForeignKey<InvoiceDetailRecord, ?> fk : references) {
            System.out.println( "****************" );
            System.out.println( fk.getName() );
            System.out.println( fk.getFields() + " ---> " + fk.getKey() );
            
            System.out.println( );
            
            
        }
        
        
    }
}