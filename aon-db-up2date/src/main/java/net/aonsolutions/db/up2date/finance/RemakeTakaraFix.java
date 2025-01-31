package net.aonsolutions.db.up2date.finance;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;

import java.sql.Connection;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class RemakeTakaraFix implements Update {

	public static RemakeTakaraFix REMAKETAKARAFIX = new RemakeTakaraFix();

	private static final int REMAKETAKARA_ARUBA_DOMAIN = 9044;
	private static final String REMAKETAKARA_ARUBA = "remaketakara-aruba";
	private static final int REMAKETAKARA_DOMAIN = 41933;
	private static final String REMAKETAKARA = "remaketakara";

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		// Establish context
		DSLContext dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		dslContext.transaction( config -> {
			if ( 
				dslContext
					.select(DOMAIN.ID)
					.from(DOMAIN)
					.where(DOMAIN.ID.equal(REMAKETAKARA_ARUBA_DOMAIN))
					.and(DOMAIN.NAME.like(REMAKETAKARA_ARUBA + "%"))
					.fetch()
					.stream()
					.findFirst()
					.isPresent()
				&& dslContext
					.select(DOMAIN.ID)
					.from(DOMAIN)
					.where(DOMAIN.ID.equal(REMAKETAKARA_DOMAIN))
					.and(DOMAIN.NAME.like(REMAKETAKARA + "%"))
					.fetch()
					.stream()
					.findFirst()
					.isPresent()) {
				
				fixInvoices( dslContext ); 
			} else {
				System.out.println( "DOMAIN REMAKETAKARA NOT FOUND!");
			}
		});
	}

	private static class InvoiceMin {
		private  Integer id;
		private Date issueDate;
		private  Byte type;
		private  String series;
		private  Integer number;
		private Double total;
		
		
		public Integer getId() {
			return id;
		}
		public InvoiceMin setId(Integer id) {
			this.id = id;
			return this;
		}
		public Date getIssueDate() {
			return issueDate;
		}
		public InvoiceMin setIssueDate(Date issueDate) {
			this.issueDate = issueDate;
			return this;
		}
		public Byte getType() {
			return type;
		}
		public InvoiceMin setType(Byte type) {
			this.type = type;
			return this;
		}
		public String getSeries() {
			return series;
		}
		public InvoiceMin setSeries(String series) {
			this.series = series;
			return this;
		}
		public Integer getNumber() {
			return number;
		}
		public InvoiceMin setNumber(Integer number) {
			this.number = number;
			return this;
		}
		public Double getTotal() {
			return total;
		}
		public InvoiceMin setTotal(Double total) {
			this.total = total;
			return this;
		}
	}
	private static class InvoiceMinFiller implements Function<Record,InvoiceMin> {
		@Override
		public InvoiceMin apply(Record r) {
			return new InvoiceMin()
				.setId(r.getValue(INVOICE.ID))
				.setIssueDate(r.getValue(INVOICE.ISSUE_DATE))
				.setType(r.getValue(INVOICE.TYPE))
				.setSeries(r.getValue(INVOICE.SERIES))
				.setNumber(r.getValue(INVOICE.NUMBER))
				.setTotal(r.getValue(INVOICE.TOTAL));
		}
	}
	
	private static class InvoiceDetailMin {
		private InvoiceMin invoice;
		private Integer id;
		private Integer item;
		private Short line;
		private String description;
		private Double quantity;
		private Double price;
		private Double taxableBase;
		
		public InvoiceMin getInvoice() {
			return invoice;
		}
		public InvoiceDetailMin setInvoice(InvoiceMin invoice) {
			this.invoice = invoice;
			return this;
		}
		public Integer getId() {
			return id;
		}
		public InvoiceDetailMin setId(Integer id) {
			this.id = id;
			return this;
		}
		public Integer getItem() {
			return item;
		}
		public InvoiceDetailMin setItem(Integer item) {
			this.item = item;
			return this;
		}
		public Short getLine() {
			return line;
		}
		public InvoiceDetailMin setLine(Short line) {
			this.line = line;
			return this;
		}
		public String getDescription() {
			return description;
		}
		public InvoiceDetailMin setDescription(String description) {
			this.description = description;
			return this;
		}
		public Double getQuantity() {
			return quantity;
		}
		public InvoiceDetailMin setQuantity(Double quantity) {
			this.quantity = quantity;
			return this;
		}
		public Double getPrice() {
			return price;
		}
		public InvoiceDetailMin setPrice(Double price) {
			this.price = price;
			return this;
		}
		public Double getTaxableBase() {
			return taxableBase;
		}
		public InvoiceDetailMin setTaxableBase(Double taxableBase) {
			this.taxableBase = taxableBase;
			return this;
		}
		
		
	}
	
	private static class InvoiceDetailMinFiller implements Function<Record,InvoiceDetailMin> {
		@Override
		public InvoiceDetailMin apply(Record r) {
			return new InvoiceDetailMin()
				.setId(r.getValue(INVOICE_DETAIL.ID))
				.setItem(r.getValue(INVOICE_DETAIL.ITEM))
				.setLine(r.getValue(INVOICE_DETAIL.LINE))
				.setDescription(r.getValue(INVOICE_DETAIL.DESCRIPTION))
				.setQuantity(r.getValue(INVOICE_DETAIL.QUANTITY))
				.setPrice(r.getValue(INVOICE_DETAIL.PRICE))
				.setTaxableBase(r.getValue(INVOICE_DETAIL.TAXABLE_BASE));
		}
	}
	
	private static Stream<InvoiceDetailMin> detailStream(DSLContext dslContext, InvoiceMin invoice) {
		return dslContext.select(
			 INVOICE_DETAIL.ID,INVOICE_DETAIL.ITEM,INVOICE_DETAIL.LINE
			,INVOICE_DETAIL.DESCRIPTION,INVOICE_DETAIL.QUANTITY,INVOICE_DETAIL.PRICE
			,INVOICE_DETAIL.TAXABLE_BASE)
		.from(INVOICE_DETAIL)
		.where(INVOICE_DETAIL.INVOICE.eq(invoice.getId()))
		.orderBy( INVOICE_DETAIL.LINE)
		.fetch()
		.stream()
		.map( r -> new InvoiceDetailMinFiller().apply(r) )
		.map( oldId -> oldId.setInvoice(invoice))
		;
	}
	
	private static void fixInvoices(DSLContext dslContext) {
		dslContext
			.select(INVOICE.ID,INVOICE.ISSUE_DATE,INVOICE.TYPE,INVOICE.SERIES,INVOICE.NUMBER,INVOICE.TOTAL)
			.from(INVOICE)
			.where(INVOICE.DOMAIN.equal(REMAKETAKARA_ARUBA_DOMAIN))
			.orderBy( INVOICE.ISSUE_DATE )
			.fetch()
			.stream()
			.map( r -> new InvoiceMinFiller().apply(r) )
			.forEach( i -> {
				dslContext
					.select(INVOICE.ID,INVOICE.ISSUE_DATE,INVOICE.TYPE,INVOICE.SERIES,INVOICE.NUMBER,INVOICE.TOTAL)
					.from(INVOICE)
					.where(INVOICE.DOMAIN.equal(REMAKETAKARA_DOMAIN))
					 .and(INVOICE.TYPE.eq(i.getType()))
					 .and(i.getSeries() == null?INVOICE.SERIES.isNull():INVOICE.SERIES.eq(i.getSeries()))
					 .and(INVOICE.NUMBER.eq(i.getNumber()))
					.fetch()
					.stream()
					.map( r -> new InvoiceMinFiller().apply(r) )
					.findFirst()
					.ifPresentOrElse( 
						im -> fixInvoiceDetails(dslContext, i,im)
						,() -> System.out.println(" FACTURA NO ENCONTRADA " + i.getType() + " " + i.getSeries() + " " + i.getNumber() + " " + i.getTotal())
					);
		});
	}

	private static void fixInvoiceDetails(DSLContext dslContext, InvoiceMin oldInvoice, InvoiceMin newInvoice) {
		detailStream(dslContext,oldInvoice)
			.forEach( oldId -> {
				dslContext
				.select(INVOICE_DETAIL.ID,INVOICE_DETAIL.ITEM,INVOICE_DETAIL.LINE
					,INVOICE_DETAIL.DESCRIPTION,INVOICE_DETAIL.QUANTITY,INVOICE_DETAIL.PRICE
					,INVOICE_DETAIL.TAXABLE_BASE)
				.from(INVOICE_DETAIL)
				.where(INVOICE_DETAIL.DOMAIN.equal(REMAKETAKARA_DOMAIN))
				 .and(INVOICE_DETAIL.INVOICE.eq(newInvoice.getId()))	
				 //.and(oldId.getItem() == null?INVOICE_DETAIL.ITEM.isNull():INVOICE_DETAIL.ITEM.eq(oldId.getItem()))
				 .and(oldId.getLine() == null?INVOICE_DETAIL.LINE.isNull():INVOICE_DETAIL.LINE.eq(oldId.getLine()))
				 .and(oldId.getDescription() == null?INVOICE_DETAIL.DESCRIPTION.isNull():INVOICE_DETAIL.DESCRIPTION.eq(oldId.getDescription()))
				 .and(INVOICE_DETAIL.QUANTITY.eq(oldId.getQuantity()))
				 .and(INVOICE_DETAIL.PRICE.eq(oldId.getPrice()))
				 .and(INVOICE_DETAIL.TAXABLE_BASE.eq(oldId.getTaxableBase()))
				.fetch()
				.stream()
				.map( r -> new InvoiceDetailMinFiller().apply(r).setInvoice(newInvoice))
				.findFirst()
				.ifPresentOrElse( 
					im -> fixInvoiceTaxs(dslContext, oldId, im)
					,() -> System.out.println(" DETALLE FACTURA NO ENCONTRADA " + oldId.getInvoice().getId() + " ("  + oldId.getLine() + ") ----- " + newInvoice.getId())
				);

			}
			);
		;
		
	}
	private static void fixInvoiceTaxs(DSLContext dslContext, InvoiceDetailMin oldDetail, InvoiceDetailMin newDetail) {
		long count = dslContext.select(INVOICE_TAX.ID)
			.from( INVOICE_TAX )
			.where(INVOICE_TAX.INVOICE_DETAIL.eq(newDetail.getId()))
			.fetch()
			.stream()
			.count();
		if (count > 0) {
			System.out.println(" DETALLE CON TAXES " + oldDetail.getInvoice().getId()
				+ " ("
				+ oldDetail.getId()
				+ ") "
				+ newDetail.getInvoice().getId()
				+ " ("
				+ newDetail.getId()
				+ ")");
			return;
		}
		Set<Byte> taxTypes = new HashSet<Byte>();
		dslContext.select()
			.from( INVOICE_TAX )
			.where(INVOICE_TAX.INVOICE_DETAIL.eq(oldDetail.getId()))
			.fetch()
			.stream()
			.forEach( r -> {
				Byte taxType = r.getValue(INVOICE_TAX.TAX_TYPE);
				if (!taxTypes.contains(taxType)) {
					taxTypes.add(taxType);
					dslContext.insertInto(INVOICE_TAX)
						.set(INVOICE_TAX.DOMAIN, REMAKETAKARA_DOMAIN)
						.set(INVOICE_TAX.INVOICE_DETAIL, newDetail.getId())
						.set(INVOICE_TAX.TAX_TYPE, r.getValue(INVOICE_TAX.TAX_TYPE))
						.set(INVOICE_TAX.BASE, r.getValue(INVOICE_TAX.BASE))
						.set(INVOICE_TAX.PERCENTAGE, r.getValue(INVOICE_TAX.PERCENTAGE))
						.set(INVOICE_TAX.QUOTA, r.getValue(INVOICE_TAX.QUOTA))
						.set(INVOICE_TAX.SURCHARGE, r.getValue(INVOICE_TAX.SURCHARGE))
						.set(INVOICE_TAX.SURCHARGE_QUOTA,r.getValue(INVOICE_TAX.SURCHARGE_QUOTA))
						.set(INVOICE_TAX.VAT_DEDUCTION_TYPE,r.getValue(INVOICE_TAX.VAT_DEDUCTION_TYPE))
						.set(INVOICE_TAX.WITHHOLDING_TYPE,r.getValue(INVOICE_TAX.WITHHOLDING_TYPE))
						.set(INVOICE_TAX.DEDUCTIBLE_PERCENT,r.getValue(INVOICE_TAX.DEDUCTIBLE_PERCENT))
						.set(INVOICE_TAX.DEDUCTIBLE_QUOTA , r.getValue(INVOICE_TAX.DEDUCTIBLE_QUOTA))
						.execute()
						;
				}
			});
		
	}
	
}
