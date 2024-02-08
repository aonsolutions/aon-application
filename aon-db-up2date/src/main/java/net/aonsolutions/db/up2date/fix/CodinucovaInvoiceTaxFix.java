package net.aonsolutions.db.up2date.fix;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;
import static com.esferalia.aon.jooq.tables.InvoiceTaxAccount.INVOICE_TAX_ACCOUNT;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.Objects;

import org.jooq.AggregateFunction;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.InvoiceTaxRecord;

import net.aonsolutions.db.up2date.Update;

public class CodinucovaInvoiceTaxFix implements Update {

	private static final int DOMAIN_ID = 22229;
	private static final int WRONG_DOMAIN = 37515;
	
	
	private static class InvoiceTaxCount  {

		private int invoiceDetail;
		private byte taxType;
		private int count;
		private LinkedList<InvoiceTaxRecord> taxes = new LinkedList<>();
		
		public int getInvoiceDetail() {
			return invoiceDetail;
		}
		public InvoiceTaxCount setInvoiceDetail(int invoiceDetail) {
			this.invoiceDetail = invoiceDetail;
			return this;
		}
		public byte getTaxType() {
			return taxType;
		}
		public InvoiceTaxCount setTaxType(byte taxType) {
			this.taxType = taxType;
			return this;
		}
		public int getCount() {
			return count;
		}
		public InvoiceTaxCount setCount(int count) {
			this.count = count;
			return this;
		}
		public LinkedList<InvoiceTaxRecord> getTaxes() {
			return taxes;
		}
		public void addTax(InvoiceTaxRecord rec) {
			this.taxes.add(rec);
		}
	}
	
	
	private int invoiceLines;
	private int invoiceTaxDeleted;
	private int invoiceNoEquals;
	
	public static final CodinucovaInvoiceTaxFix INSTANCE= new CodinucovaInvoiceTaxFix();
	
	private CodinucovaInvoiceTaxFix() {
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		
		DSLContext dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		invoiceLines = 0;
		invoiceTaxDeleted = 0;
		invoiceNoEquals = 0;
		dslContext.transaction( config -> {
			boolean hasDomain = dslContext.select()
					.from(DOMAIN)
					.where(DOMAIN.ID.eq(DOMAIN_ID))
					.and(DOMAIN.NAME.like("codinucova%"))
					.fetch()
					.stream()
					.findAny()
					.isPresent();
				
				if (hasDomain) {
					fix(dslContext);		
				} else {
					System.out.println( "DOMINIO NO ENCONTRADO!");
				}
		});
		
	}

	
	private void fix(DSLContext dsl) {
		AggregateFunction<Integer> count = DSL.count();
		dsl.select( INVOICE_TAX.INVOICE_DETAIL,INVOICE_TAX.TAX_TYPE,count) 
			.from(INVOICE_TAX)
			.where(INVOICE_TAX.DOMAIN.eq(DOMAIN_ID))
			.groupBy( INVOICE_TAX.INVOICE_DETAIL,INVOICE_TAX.TAX_TYPE )
			.having(count.gt(1))
			.fetch()
			.stream()
			.map(rec -> new InvoiceTaxCount()
					.setInvoiceDetail( rec.get(INVOICE_TAX.INVOICE_DETAIL))
					.setTaxType( rec.get(INVOICE_TAX.TAX_TYPE))
					.setCount( rec.get(count))
			)
			.forEach( itc -> fixInvoiceTax(dsl, itc));
		System.out.println("Lineas de facturas con tax duplicados: " + invoiceLines);
		System.out.println("Taxes borrados: " + invoiceTaxDeleted);
		System.out.println("Taxes no iguales: " + invoiceNoEquals);
	}
	
	private void fixInvoiceTax(DSLContext dsl, InvoiceTaxCount itc) {
		dsl.select()
			.from(INVOICE_TAX)
			.where(INVOICE_TAX.DOMAIN.eq(DOMAIN_ID))
			.and( INVOICE_TAX.INVOICE_DETAIL.eq(itc.getInvoiceDetail()))
			.and( INVOICE_TAX.TAX_TYPE.eq(itc.getTaxType()))
			.fetchInto( InvoiceTaxRecord.class )
			.stream()
			.forEach( itc::addTax );
		if ( itc.getCount() != itc.getTaxes().size()) {
			System.out.println("ERROR NO CUADRA!");
		} else {
			check( dsl, itc );
		}
		++invoiceLines;
	}

	private void check(DSLContext dsl, InvoiceTaxCount itc) {
		InvoiceTaxRecord i0 = itc.getTaxes().get(0);
		InvoiceTaxRecord i1 = itc.getTaxes().get(1);
		if ( Objects.equals(i0.getDomain(),i1.getDomain())
		  && Objects.equals(i0.getInvoiceDetail(),i1.getInvoiceDetail())
		  && Objects.equals(i0.getTaxType(),i1.getTaxType())
		  && Objects.equals(i0.getBase(),i1.getBase())
		  && Objects.equals(i0.getPercentage(),i1.getPercentage())
		  && Objects.equals(i0.getSurcharge(),i1.getSurcharge())
		  && Objects.equals(i0.getQuota(),i1.getQuota())
		  && Objects.equals(i0.getSurchargeQuota(),i1.getSurchargeQuota())
		  && Objects.equals(i0.getVatDeductionType(),i1.getVatDeductionType())
		  && Objects.equals(i0.getWithholdingType(),i1.getWithholdingType())
		  && Objects.equals(i0.getDeductiblePercent(),i1.getDeductiblePercent())
		  && Objects.equals(i0.getDeductibleQuota(),i1.getDeductibleQuota())) {
			
			long i0Count = dsl.select( )
				.from(INVOICE_TAX_ACCOUNT)
				.where(INVOICE_TAX_ACCOUNT.DOMAIN.eq(WRONG_DOMAIN))
				.and( INVOICE_TAX_ACCOUNT.INVOICE_TAX.eq(i0.getId()))
				.stream()
				.count();
			long i1Count = dsl.select( )
				.from(INVOICE_TAX_ACCOUNT)
				.where(INVOICE_TAX_ACCOUNT.DOMAIN.eq(WRONG_DOMAIN))
				.and( INVOICE_TAX_ACCOUNT.INVOICE_TAX.eq(i1.getId()))
				.stream()
				.count();
			
			if ( i0Count > 0 && i1Count > 0) {
				System.out.println( "ERROR COUNT DOMAIN");
			} else if ( i0Count > 0) {
				delete(dsl, i0.getId());
			} else {
				delete(dsl, i1.getId());
			}
		} else {
			++invoiceNoEquals;
		}
		
	}

	private void delete(DSLContext dsl, Integer id) {
		dsl.delete( INVOICE_TAX_ACCOUNT )
			.where(INVOICE_TAX_ACCOUNT.INVOICE_TAX.eq(id))
			.and(INVOICE_TAX_ACCOUNT.DOMAIN.eq(WRONG_DOMAIN))
			.execute();
		int deleted = dsl.delete( INVOICE_TAX )
			.where(INVOICE_TAX.ID.eq(id))
			.and(INVOICE_TAX.DOMAIN.eq(DOMAIN_ID))
			.execute();
		invoiceTaxDeleted += deleted;			
	}

}
