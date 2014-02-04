package com.esferalia.aon.master.sql.fiscal;

import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.pool.AonConnectionException;
import com.esferalia.aon.entity.jooq.tables.Invoice;
import com.esferalia.aon.entity.jooq.tables.InvoiceDetail;
import com.esferalia.aon.entity.jooq.tables.InvoiceTax;

public class VatTaxManager {
	
	private String domainName;
	
	public VatTaxManager(String domainName) {
		this.domainName = domainName;
	}
	public String getDomainName() {
		return domainName;
	}

	public static void main(String[] args) throws AonConnectionException {
		String domainName = "mac.ecastellano.dev";
		VatTaxManager manager = new VatTaxManager(domainName);
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, 2013);
		c.set(Calendar.MONTH, 1);
		c.set(Calendar.DAY_OF_MONTH, 1);
		Date fromDate = c.getTime();
		
		c.set(Calendar.MONTH, 11);
		c.set(Calendar.DAY_OF_MONTH, 31);
		Date toDate = c.getTime();
		
		manager.test(400,fromDate,toDate); // inelco
	}
	
	private static final InvoiceTax it = InvoiceTax.INVOICE_TAX.as("it"); 
	private static final InvoiceDetail id = InvoiceDetail.INVOICE_DETAIL.as("id");
	private static final Invoice i = Invoice.INVOICE.as("i");
	private static final Field<Double> taxableBaseSum = DSL.field("SUM({0})"
			,Double.class
			,id.TAXABLE_BASE
		);
	private static final Field<Double> quotaSum = DSL.field(
			"IF({0} != 0,{0},ROUND({1} * {2} / 100, 2) )"
			,Double.class
			,it.QUOTA
			,id.TAXABLE_BASE
			,it.PERCENTAGE
			);
	private static final Field<Double> reSum = DSL.field(
			"SUM( IF({0} != 0,{0},ROUND({1} * {2} / 100, 2) ) )"
			,Double.class
			,it.SURCHARGE_QUOTA
			,id.TAXABLE_BASE
			,it.SURCHARGE
			);
	private static final Field<Double> dedQuotaSum = DSL.field(
			"SUM( ROUND(IF({0} != 0,{0},IF({1} != 0,{1},ROUND({2} * {3} / 100, 2) )),2))"
			,Double.class
			,it.DEDUCTIBLE_QUOTA
			,it.QUOTA
			,id.TAXABLE_BASE
			,it.PERCENTAGE
			);

	public void test(int domain, Date fromDate, Date toDate) throws AonConnectionException {
		java.sql.Date dateFrom = new java.sql.Date(fromDate.getTime());
		java.sql.Date dateTo = new java.sql.Date(toDate.getTime());
		
		Settings settings = new Settings().withRenderSchema(false);
		
		Connection c = DatabaseUtil.getConnection(getDomainName());
		DSLContext create = DSL.using(c,SQLDialect.MYSQL,settings);
		List<InvoiceSelect> result =
			create.select(
				i.TYPE
				,i.RNAME
				,i.RECTIFICATION_TYPE
				,i.SERVICE
				,it.PERCENTAGE
				,it.SURCHARGE
				,it.VAT_DEDUCTION_TYPE
				,i.TRANSACTION
				,i.INVESTMENT
				,i.WITHHOLDING_FARMER
				,taxableBaseSum
				,quotaSum
				,reSum
				,dedQuotaSum
				)
				.from(it)
				.join(id).on( it.INVOICE_DETAIL.equal(id.ID) )
				.join(i).on( id.INVOICE.equal(i.ID) )
				.where(it.DOMAIN.equal(domain))
				.and(it.TAX_TYPE.equal((byte) 1))
				.and(i.TAX_DATE.between(dateFrom, dateTo))
				.groupBy(
					i.TYPE
					,i.RECTIFICATION_TYPE
					,i.SERVICE
					,it.PERCENTAGE
					,it.SURCHARGE
					,it.VAT_DEDUCTION_TYPE
					,i.TRANSACTION
					,i.INVESTMENT
					,i.WITHHOLDING_FARMER)
					.fetch(new InvoiceMapper())
				;
			
		for (InvoiceSelect is : result) {
			System.out.println(""
			+" - " + is.isRectification()
			+" - " + is.isService()
			+" - " + is.getPercent()
			+" - " + is.getVatDeductionType()
			+" - " + is.getTransaction()
			+" - " + is.isWithHoldingFarmer()
			+" - " + is.isInvestment()
			+" - " + is.getSurchargePercent()
			+" - " + is.getTaxableBase()
			+" - " + is.getQuota()
			+" - " + is.getSurchargeQuota()
			+" - " + is.getDeductibleQuota()
			);			
		}
	}
	public static class InvoiceSelect {
		private boolean rectification;
		private boolean service;
		private double percent;
		private int vatDeductionType;
		private int transaction;
		private boolean withHoldingFarmer;
		private boolean investment;
		private double surchargePercent;
		private double taxableBase;
		private double quota;
		private double surchargeQuota;
		private double deductibleQuota;
		
		public boolean isRectification() {
			return rectification;
		}
		public void setRectification(boolean rectification) {
			this.rectification = rectification;
		}
		public boolean isService() {
			return service;
		}
		public void setService(boolean service) {
			this.service = service;
		}
		public double getPercent() {
			return percent;
		}
		public void setPercent(double percent) {
			this.percent = percent;
		}
		public int getVatDeductionType() {
			return vatDeductionType;
		}
		public void setVatDeductionType(int vatDeductionType) {
			this.vatDeductionType = vatDeductionType;
		}
		public int getTransaction() {
			return transaction;
		}
		public void setTransaction(int transaction) {
			this.transaction = transaction;
		}
		public boolean isWithHoldingFarmer() {
			return withHoldingFarmer;
		}
		public void setWithHoldingFarmer(boolean withHoldingFarmer) {
			this.withHoldingFarmer = withHoldingFarmer;
		}
		public boolean isInvestment() {
			return investment;
		}
		public void setInvestment(boolean investment) {
			this.investment = investment;
		}
		public double getSurchargePercent() {
			return surchargePercent;
		}
		public void setSurchargePercent(double surchargePercent) {
			this.surchargePercent = surchargePercent;
		}
		public double getTaxableBase() {
			return taxableBase;
		}
		public void setTaxableBase(double taxableBase) {
			this.taxableBase = taxableBase;
		}
		public double getQuota() {
			return quota;
		}
		public void setQuota(double quota) {
			this.quota = quota;
		}
		public double getSurchargeQuota() {
			return surchargeQuota;
		}
		public void setSurchargeQuota(double surchargeQuota) {
			this.surchargeQuota = surchargeQuota;
		}
		public double getDeductibleQuota() {
			return deductibleQuota;
		}
		public void setDeductibleQuota(double deductibleQuota) {
			this.deductibleQuota = deductibleQuota;
		}
		
	}
	
	public static class InvoiceMapper implements RecordMapper<Record,InvoiceSelect>  {
		
		@Override
		public InvoiceSelect map(Record record) {
			InvoiceSelect is = new InvoiceSelect();
			is.setRectification(record.getValue(i.RECTIFICATION_TYPE) == 2);
			is.setService(record.getValue(i.SERVICE) == 1);
			is.setPercent(record.getValue(it.PERCENTAGE));
			is.setVatDeductionType(record.getValue(it.VAT_DEDUCTION_TYPE));
			is.setTransaction(record.getValue(i.TRANSACTION));
			is.setWithHoldingFarmer(record.getValue(i.WITHHOLDING_FARMER) == 1);
			is.setInvestment(record.getValue(i.INVESTMENT) == 1);
			is.setSurchargePercent(record.getValue(it.SURCHARGE));
			is.setTaxableBase(record.getValue(taxableBaseSum));
			is.setQuota(record.getValue(quotaSum));
			is.setSurchargeQuota(record.getValue(reSum));
			is.setDeductibleQuota(record.getValue(dedQuotaSum));
			return is;
		}

	}
	  
}
