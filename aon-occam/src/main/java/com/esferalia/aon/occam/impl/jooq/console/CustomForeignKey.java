package com.esferalia.aon.occam.impl.jooq.console;

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
import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.BankStatementLink.BANK_STATEMENT_LINK;
import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.PurchaseDetail.PURCHASE_DETAIL;
import static com.esferalia.aon.jooq.tables.WarehouseTransfer.WAREHOUSE_TRANSFER;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Record;
import org.jooq.Table;

import com.esferalia.aon.jooq.tables.records.AccountRecord;
import com.esferalia.aon.jooq.tables.records.AlarmRecord;
import com.esferalia.aon.jooq.tables.records.AppParamRecord;
import com.esferalia.aon.jooq.tables.records.BankConceptRecord;
import com.esferalia.aon.jooq.tables.records.BankStatementLinkRecord;
import com.esferalia.aon.jooq.tables.records.CommercialTrackingRecord;
import com.esferalia.aon.jooq.tables.records.DeliveryDetailRecord;
import com.esferalia.aon.jooq.tables.records.FbatchRecord;
import com.esferalia.aon.jooq.tables.records.FinanceRecord;
import com.esferalia.aon.jooq.tables.records.FinanceTrackingRecord;
import com.esferalia.aon.jooq.tables.records.IncomeDetailRecord;
import com.esferalia.aon.jooq.tables.records.InventoryDetailRecord;
import com.esferalia.aon.jooq.tables.records.InvoiceDetailRecord;
import com.esferalia.aon.jooq.tables.records.MkActionTargetRecord;
import com.esferalia.aon.jooq.tables.records.NoticeRecord;
import com.esferalia.aon.jooq.tables.records.OfferDetailRecord;
import com.esferalia.aon.jooq.tables.records.PrepaymentRecord;
import com.esferalia.aon.jooq.tables.records.ProjectReservationRecord;
import com.esferalia.aon.jooq.tables.records.ProposalDetailRecord;
import com.esferalia.aon.jooq.tables.records.PurchaseDetailRecord;
import com.esferalia.aon.jooq.tables.records.SalaryRecord;
import com.esferalia.aon.jooq.tables.records.SalesDetailRecord;
import com.esferalia.aon.jooq.tables.records.TaskRecord;
import com.esferalia.aon.jooq.tables.records.WarehouseTransferRecord;

enum CustomForeignKey {
	
	INVOICE_DETAIL_PURCHASE_DETAIL {
		
		@Override
		public boolean accept(Table<?> table, Record rec) {
			return sameByte(rec,INVOICE_DETAIL.SOURCE,1);
		}
		
		@Override
		public Field<?>[] getInvolvedColumns() {
			return new Field<?>[]{ INVOICE_DETAIL.SOURCE };
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public ForeignKey<InvoiceDetailRecord, PurchaseDetailRecord> getForeignKey() {
			return new WeakForeignKey<>(KEY_PURCHASE_DETAIL_PRIMARY
				,INVOICE_DETAIL
				,"WEAK_FK_INVOICE_DETAIL_PURCHASE_DETAIL"
				,INVOICE_DETAIL.SOURCE_ID); 	
		}
	},
	INVOICE_DETAIL_SALES_DETAIL{
		
		@Override
		public boolean accept(Table<?> table, Record rec) {
			return sameByte(rec,INVOICE_DETAIL.SOURCE,2);
		}
		
		@Override
		public Field<?>[] getInvolvedColumns() {
			return new Field<?>[]{ INVOICE_DETAIL.SOURCE };
		}

		@Override
		@SuppressWarnings("unchecked")
		public ForeignKey<InvoiceDetailRecord,SalesDetailRecord> getForeignKey() {
			return new WeakForeignKey<>(KEY_SALES_DETAIL_PRIMARY
				,INVOICE_DETAIL
				,"WEAK_FK_INVOICE_DETAIL_SALES_DETAIL"
				,INVOICE_DETAIL.SOURCE_ID);
		}
	},
	INVOICE_DETAIL_DELIVERY_DETAIL{
		@Override
		public boolean accept(Table<?> table, Record rec) {
			return sameByte(rec,INVOICE_DETAIL.SOURCE,3);
		}
		
		@Override
		public Field<?>[] getInvolvedColumns() {
			return new Field<?>[]{ INVOICE_DETAIL.SOURCE };
		}

		@Override
		@SuppressWarnings("unchecked")
		public ForeignKey<InvoiceDetailRecord,DeliveryDetailRecord> getForeignKey() {
			return new WeakForeignKey<>(KEY_DELIVERY_DETAIL_PRIMARY
				,INVOICE_DETAIL
				,"WEAK_FK_INVOICE_DETAIL_DELIVERY_DETAIL"
				,INVOICE_DETAIL.SOURCE_ID);
		}
	},
	INVOICE_DETAIL_INCOME_DETAIL{
		@Override
		public boolean accept(Table<?> table, Record rec) {
			return sameByte(rec,INVOICE_DETAIL.SOURCE,4);
		}
		
		@Override
		public Field<?>[] getInvolvedColumns() {
			return new Field<?>[]{ INVOICE_DETAIL.SOURCE };
		}

		@Override
		@SuppressWarnings("unchecked")
		public ForeignKey<InvoiceDetailRecord,IncomeDetailRecord> getForeignKey() {
			return new WeakForeignKey<>(KEY_INCOME_DETAIL_PRIMARY
				,INVOICE_DETAIL
				,"WEAK_FK_INVOICE_DETAIL_INCOME_DETAIL"
				,INVOICE_DETAIL.SOURCE_ID);
		}
	},
	INVOICE_DETAIL_OFFER_DETAIL{
		@Override
		public boolean accept(Table<?> table, Record rec) {
			return sameByte(rec,INVOICE_DETAIL.SOURCE,8);
		}
		
		@Override
		public Field<?>[] getInvolvedColumns() {
			return new Field<?>[]{ INVOICE_DETAIL.SOURCE };
		}

		@Override
		@SuppressWarnings("unchecked")
		public ForeignKey<InvoiceDetailRecord,OfferDetailRecord> getForeignKey() {
			return new WeakForeignKey<>(KEY_OFFER_DETAIL_PRIMARY
				,INVOICE_DETAIL
				,"WEAK_FK_INVOICE_DETAIL_OFFER_DETAIL"
				,INVOICE_DETAIL.SOURCE_ID);
		}
		
	},
	INVOICE_DETAIL_PROJECT_RESERVATION {
		@Override
		public boolean accept(Table<?> table, Record rec) {
			return sameByte(rec,INVOICE_DETAIL.SOURCE,9);
		}
		
		@Override
		public Field<?>[] getInvolvedColumns() {
			return new Field<?>[]{ INVOICE_DETAIL.SOURCE };
		}

		@Override
		@SuppressWarnings("unchecked")
		public ForeignKey<InvoiceDetailRecord,ProjectReservationRecord> getForeignKey() {
			return new WeakForeignKey<>(KEY_PROJECT_RESERVATION_PRIMARY
				,INVOICE_DETAIL
				,"WEAK_FK_INVOICE_DETAIL_PROJECT_RESERVATION"
				,INVOICE_DETAIL.SOURCE_ID);
		}
		
	},
	BANK_STATEMENT_LINK_FINANCE_TRACKING {
		@Override
		public boolean accept(Table<?> table, Record rec) {
			return sameByte(rec,BANK_STATEMENT_LINK.SOURCE,0);
		}
		
		@Override
		public Field<?>[] getInvolvedColumns() {
			return new Field<?>[]{ BANK_STATEMENT_LINK.SOURCE };
		}

		@Override
		@SuppressWarnings("unchecked")
		public ForeignKey<BankStatementLinkRecord,FinanceTrackingRecord> getForeignKey() {
			return new WeakForeignKey<>(KEY_FINANCE_TRACKING_PRIMARY
				,BANK_STATEMENT_LINK
				,"WEAK_FK_BANK_STATEMENT_LINK_FINANCE_TRACKING"
				,BANK_STATEMENT_LINK.SOURCE_ID);
		}
		
	},
	BANK_STATEMENT_LINK_FBATCH {
		@Override
		public boolean accept(Table<?> table, Record rec) {
			return sameByte(rec,BANK_STATEMENT_LINK.SOURCE,1);
		}
		
		@Override
		public Field<?>[] getInvolvedColumns() {
			return new Field<?>[]{ BANK_STATEMENT_LINK.SOURCE };
		}

		@Override
		@SuppressWarnings("unchecked")
		public ForeignKey<BankStatementLinkRecord,FbatchRecord> getForeignKey() {
			return new WeakForeignKey<>(KEY_FBATCH_PRIMARY
				,BANK_STATEMENT_LINK
				,"WEAK_FK_BANK_STATEMENT_LINK_FBATCH"
				,BANK_STATEMENT_LINK.SOURCE_ID);
		}
		
	},
	BANK_STATEMENT_LINK_BANK_CONCEPT {
		@Override
		public boolean accept(Table<?> table, Record rec) {
			return sameByte(rec,BANK_STATEMENT_LINK.SOURCE,2);
		}
		
		@Override
		public Field<?>[] getInvolvedColumns() {
			return new Field<?>[]{ BANK_STATEMENT_LINK.SOURCE };
		}

		@Override
		@SuppressWarnings("unchecked")
		public ForeignKey<BankStatementLinkRecord, BankConceptRecord> getForeignKey() {
			return new WeakForeignKey<>(KEY_BANK_CONCEPT_PRIMARY
				,BANK_STATEMENT_LINK
				,"WEAK_FK_BANK_STATEMENT_LINK_BANK_CONCEPT"
				,BANK_STATEMENT_LINK.SOURCE_ID);
		}
	},
	BANK_STATEMENT_LINK_ACCOUNT {
		@Override
		public boolean accept(Table<?> table, Record rec) {
			return sameByte(rec,BANK_STATEMENT_LINK.SOURCE,3);
		}
		
		@Override
		public Field<?>[] getInvolvedColumns() {
			return new Field<?>[]{ BANK_STATEMENT_LINK.SOURCE };
		}

		@Override
		@SuppressWarnings("unchecked")
		public ForeignKey<BankStatementLinkRecord,AccountRecord> getForeignKey() {
			return new WeakForeignKey<>(KEY_ACCOUNT_PRIMARY
				,BANK_STATEMENT_LINK
				,"WEAK_FK_BANK_STATEMENT_LINK_ACCOUNT"
				,BANK_STATEMENT_LINK.SOURCE_ID);
		}
		
	},
	FINANCE_SALARY {
		@Override
		public boolean accept(Table<?> table, Record rec) {
			return sameByte(rec, FINANCE.PAYROLL , 1);
		}
		
		@Override
		public Field<?>[] getInvolvedColumns() {
			return new Field<?>[]{ FINANCE.PAYROLL };
		}

		@Override
		@SuppressWarnings("unchecked")
		public ForeignKey<FinanceRecord,SalaryRecord> getForeignKey() {
			return new WeakForeignKey<>(KEY_SALARY_PRIMARY
				,FINANCE
				,"WEAK_FK_FINANCE_SALARY"
				,FINANCE.SOURCE_ID);
		}
					
	},
	FINANCE_PREPAYMENT {
		@Override
		public boolean accept(Table<?> table, Record rec) {
			return sameByte(rec,FINANCE.PREPAYMENT, 1);
		}
		
		@Override
		public Field<?>[] getInvolvedColumns() {
			return new Field<?>[]{ FINANCE.PREPAYMENT };
		}

		@Override
		@SuppressWarnings("unchecked")
		public ForeignKey<FinanceRecord,PrepaymentRecord> getForeignKey() {
			return new WeakForeignKey<>(KEY_PREPAYMENT_PRIMARY
				,FINANCE
				,"WEAK_FK_FINANCE_PREPAYMENT"
				,FINANCE.SOURCE_ID);
		}
					
	},
	ALARM_NOTICE {
		@Override
		public boolean accept(Table<?> table, Record rec) {
			return sameByte(rec,ALARM.SOURCE,0);
		}
		
		@Override
		public Field<?>[] getInvolvedColumns() {
			return new Field<?>[]{ ALARM.SOURCE };
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public ForeignKey<AlarmRecord,NoticeRecord> getForeignKey() {
			return new WeakForeignKey<>(KEY_NOTICE_PRIMARY
				,ALARM
				,"WEAK_FK_ALARM_NOTICE"
				, ALARM.SOURCE_ID);
		}
		
	},
	ALARM_TASK {
		@Override
		public boolean accept(Table<?> table, Record rec) {
			return sameByte(rec,ALARM.SOURCE,1);
		}
		
		@Override
		public Field<?>[] getInvolvedColumns() {
			return new Field<?>[]{ ALARM.SOURCE };
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public ForeignKey<AlarmRecord, TaskRecord> getForeignKey() {
			return new WeakForeignKey<>(KEY_TASK_PRIMARY
				,ALARM
				,"WEAK_FK_ALARM_TASK"
				,ALARM.SOURCE_ID);
		}
		
	},
	ALARM_COMMERCIAL_TRACKING {
		@Override
		public boolean accept(Table<?> table, Record rec) {
			return sameByte(rec,ALARM.SOURCE,3);
		}
		
		@Override
		public Field<?>[] getInvolvedColumns() {
			return new Field<?>[]{ ALARM.SOURCE };
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public ForeignKey<AlarmRecord, CommercialTrackingRecord> getForeignKey() {
			return new WeakForeignKey<>(KEY_COMMERCIAL_TRACKING_PRIMARY
				,ALARM
				,"WEAK_FK_ALARM_COMMERCIAL_TRACKING"
				,ALARM.SOURCE_ID);
		}
		
	},
	ALARM_MK_ACTION_TARGET {
		@Override
		public boolean accept(Table<?> table, Record rec) {
			return sameByte(rec,ALARM.SOURCE,4);
		}
		
		@Override
		public Field<?>[] getInvolvedColumns() {
			return new Field<?>[]{ ALARM.SOURCE };
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public ForeignKey<AlarmRecord,MkActionTargetRecord> getForeignKey() {
			return new WeakForeignKey<>(KEY_MK_ACTION_TARGET_PRIMARY
				,ALARM
				,"WEAK_FK_ALARM_MK_ACTION_TARGET"
				,ALARM.SOURCE_ID);
		}
		
	},
	PURCHASE_DETAIL_PROPOSAL_DETAIL {
		@Override
		public boolean accept(Table<?> table, Record rec) {
			return sameByte(rec,PURCHASE_DETAIL.SOURCE,0);
		}
		
		@Override
		public Field<?>[] getInvolvedColumns() {
			return new Field<?>[]{ PURCHASE_DETAIL.SOURCE };
		}

		@Override
		@SuppressWarnings("unchecked")
		public ForeignKey<PurchaseDetailRecord,ProposalDetailRecord> getForeignKey() {
			return new WeakForeignKey<>(KEY_PROPOSAL_DETAIL_PRIMARY
				,PURCHASE_DETAIL
				,"WEAK_FK_PURCHASE_DETAIL_PROPOSAL_DETAIL"
				,PURCHASE_DETAIL.SOURCE_ID);
		}

	},
	PURCHASE_DETAIL_PURCHASE_DETAIL {
		@Override
		public boolean accept(Table<?> table, Record rec) {
			return sameByte(rec,PURCHASE_DETAIL.SOURCE,1);
		}

		@Override
		public Field<?>[] getInvolvedColumns() {
			return new Field<?>[]{ PURCHASE_DETAIL.SOURCE };
		}

		@Override
		@SuppressWarnings("unchecked")
		public ForeignKey<PurchaseDetailRecord,PurchaseDetailRecord> getForeignKey() {
			return new WeakForeignKey<>(KEY_PURCHASE_DETAIL_PRIMARY
				,PURCHASE_DETAIL
				,"WEAK_FK_PURCHASE_DETAIL_PURCHASE_DETAIL"
				,PURCHASE_DETAIL.SOURCE_ID);
		}
		
	},
	PURCHASE_DETAIL_SALES_DETAIL {
		@Override
		public boolean accept(Table<?> table, Record rec) {
			return sameByte(rec,PURCHASE_DETAIL.SOURCE,2);
		}
		
		@Override
		public Field<?>[] getInvolvedColumns() {
			return new Field<?>[]{ PURCHASE_DETAIL.SOURCE };
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public ForeignKey<PurchaseDetailRecord,SalesDetailRecord> getForeignKey() {
			return new WeakForeignKey<>(KEY_SALES_DETAIL_PRIMARY
				,PURCHASE_DETAIL
				,"WEAK_FK_PURCHASE_DETAIL_SALES_DETAIL"
				,PURCHASE_DETAIL.SOURCE_ID);
		}
		
	},
	WAREHOUSE_TRANSFER_INVENTORY_DETAIL {
		@Override
		public boolean accept(Table<?> table, Record rec) {
			return sameByte(rec,WAREHOUSE_TRANSFER.SOURCE,1);
		}
		
		@Override
		public Field<?>[] getInvolvedColumns() {
			return new Field<?>[]{ WAREHOUSE_TRANSFER.SOURCE };
		}

		@Override
		@SuppressWarnings("unchecked")
		public ForeignKey<WarehouseTransferRecord,InventoryDetailRecord> getForeignKey() {
			return new WeakForeignKey<>(KEY_INVENTORY_DETAIL_PRIMARY
				,WAREHOUSE_TRANSFER
				,"WEAK_FK_WAREHOUSE_TRANSFER_INVENTORY_DETAIL"
				,WAREHOUSE_TRANSFER.SOURCE_ID);
		}
		
	},
	WAREHOUSE_TRANSFER_PURCHASE_DETAIL {
		@Override
		public boolean accept(Table<?> table, Record rec) {
			return sameByte(rec,WAREHOUSE_TRANSFER.SOURCE,2);
		}
		
		@Override
		public Field<?>[] getInvolvedColumns() {
			return new Field<?>[]{ WAREHOUSE_TRANSFER.SOURCE };
		}

		@Override
		@SuppressWarnings("unchecked")
		public ForeignKey<WarehouseTransferRecord,PurchaseDetailRecord> getForeignKey() {
			return new WeakForeignKey<>(KEY_PURCHASE_DETAIL_PRIMARY
				,WAREHOUSE_TRANSFER
				,"WEAK_FK_WAREHOUSE_TRANSFER_PURCHASE_DETAIL"
				, WAREHOUSE_TRANSFER.SOURCE_ID);
		}
		
	},
	WAREHOUSE_TRANSFER_INVENTORY_DETAIL_INIT {
		@Override
		public boolean accept(Table<?> table, Record rec) {
			return sameByte(rec,WAREHOUSE_TRANSFER.SOURCE,3);
		}
		
		@Override
		public Field<?>[] getInvolvedColumns() {
			return new Field<?>[]{ WAREHOUSE_TRANSFER.SOURCE };
		}

		@Override
		@SuppressWarnings("unchecked")
		public ForeignKey<WarehouseTransferRecord,InventoryDetailRecord> getForeignKey() {
			return new WeakForeignKey<>(
				KEY_INVENTORY_DETAIL_PRIMARY
				,WAREHOUSE_TRANSFER
				,"WEAK_FK_WAREHOUSE_TRANSFER_INVENTORY_DETAIL_INIT"
				,WAREHOUSE_TRANSFER.SOURCE_ID);
		}
		
	},
	ACC_APP_PARAM {
		@Override
		public boolean accept(Table<?> table, Record rec) {
			String paramName = rec.getValue( APP_PARAM.NAME ); 
			return paramName != null 
				&& paramName.startsWith("ACC_")
				&& paramName.endsWith("_ACC");
		}

		@Override
		public Field<?>[] getInvolvedColumns() {
			return new Field<?>[]{ APP_PARAM.NAME };
		}

		@Override
		@SuppressWarnings("unchecked")
		public ForeignKey<AppParamRecord,AccountRecord> getForeignKey() {
			return new WeakForeignKey<>(KEY_ACCOUNT_PRIMARY
				,APP_PARAM
				,"WEAK_FK_APP_PARAM_ACCOUNT"
				,APP_PARAM.VALUE);
		}
	},
	;
	
	public abstract boolean accept(Table<?> table, Record rec);
	public abstract <R extends Record, U extends Record> ForeignKey<R,U> getForeignKey();
	public abstract Field<?>[] getInvolvedColumns();
	
	private static boolean sameByte(Record rec, Field<Byte> field, int value) {
		Byte b = rec.getValue( field );
		return b != null && (b.byteValue() == Integer.valueOf(value).byteValue());
	}
	
}
