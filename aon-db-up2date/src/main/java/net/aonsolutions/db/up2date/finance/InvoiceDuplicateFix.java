package net.aonsolutions.db.up2date.finance;

import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class InvoiceDuplicateFix implements Update {

	public static final InvoiceDuplicateFix INVOICE_DUPLICATE_FIX = new InvoiceDuplicateFix();

	private InvoiceDuplicateFix() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		System.out.println("[START]");
		
		getInvoiceDetails(dslContext).stream().forEach(invoiceDetailId -> {
			InvoiceDetail invoiceDetail = getInvoiceDetail(dslContext, invoiceDetailId);
			if(invoiceDetail.getDate() != null) {
				Integer invoice = getInvoice(dslContext, invoiceDetail);
				List<InvoiceTax> taxes = getInvoiceTaxes(dslContext, invoiceDetail);
				if(taxes.size() == 2 && invoice != null && invoice != invoiceDetail.getInvoice()) {
					save(dslContext, invoiceDetail, taxes.get(0), invoice);
				}
			}
		});
	}
	
	
	public List<Integer> getInvoiceDetails(DSLContext dslContext) {
		return dslContext.select(INVOICE_TAX.DOMAIN, INVOICE_TAX.INVOICE_DETAIL, DSL.count(INVOICE_TAX.INVOICE_DETAIL))
		.from(INVOICE_TAX)
		.groupBy(INVOICE_TAX.INVOICE_DETAIL, INVOICE_TAX.TAX_TYPE)
		.having(DSL.count(INVOICE_TAX.INVOICE_DETAIL).eq(2))
		.fetch().stream()
		.map(r -> r.getValue(INVOICE_TAX.INVOICE_DETAIL))
		.toList();
	}
	
	
	private InvoiceDetail getInvoiceDetail(DSLContext dslContext, Integer invoiceDetailId) {
		return dslContext.select()
			.from(INVOICE_DETAIL)
			.where(INVOICE_DETAIL.ID.eq(invoiceDetailId))
			.fetch().stream().map(r -> new InvoiceDetail()
					.setDate(r.getValue(INVOICE_DETAIL.CREATION_DATE))
					.setDescription(r.getValue(INVOICE_DETAIL.DESCRIPTION))
					.setDomain(r.getValue(INVOICE_DETAIL.DOMAIN))
					.setId(r.getValue(INVOICE_DETAIL.ID))
					.setItem(r.getValue(INVOICE_DETAIL.ITEM))
					.setWorkplace(r.getValue(INVOICE_DETAIL.WORKPLACE))
					.setInvoice(r.getValue(INVOICE_DETAIL.INVOICE))
					)
			.findFirst().orElse(null);
	}
	
	private Integer getInvoice(DSLContext dslContext, InvoiceDetail invoiceDetail) {
		Integer inv =  dslContext.select(INVOICE.ID)
			.from(INVOICE)
			.where(INVOICE.DOMAIN.eq(invoiceDetail.getDomain()))
			.and(INVOICE.CREATION_DATE.eq(invoiceDetail.getDate()))
			.fetch().stream().map(r -> r.getValue(INVOICE.ID))
			.findFirst().orElse(null);
		if(inv == null) {
			Timestamp date = invoiceDetail.getDate();   
			date.setSeconds(date.getSeconds()-1);
			inv =  dslContext.select(INVOICE.ID)
					.from(INVOICE)
					.where(INVOICE.DOMAIN.eq(invoiceDetail.getDomain()))
					.and(INVOICE.CREATION_DATE.eq(date))
					.fetch().stream().map(r -> r.getValue(INVOICE.ID))
					.findFirst().orElse(null);
		}
		return inv;
	}
	
	private List<InvoiceTax> getInvoiceTaxes(DSLContext dslContext, InvoiceDetail invoiceDetail) {
		return dslContext.select(INVOICE_TAX.ID, INVOICE_TAX.BASE)
			.from(INVOICE_TAX)
			.where(INVOICE_TAX.DOMAIN.eq(invoiceDetail.getDomain()))
			.and(INVOICE_TAX.INVOICE_DETAIL.eq(invoiceDetail.getId()))
			.fetch().stream().map(r -> new InvoiceTax()
					.setId(r.getValue(INVOICE_TAX.ID))
					.setBase(r.getValue(INVOICE_TAX.BASE)))
			.toList();
	}
	
	private void save(DSLContext dslContext, InvoiceDetail invoiceDetail, InvoiceTax invoiceTax, Integer invoice) {
		dslContext.insertInto(INVOICE_DETAIL)
		.set(INVOICE_DETAIL.DOMAIN, invoiceDetail.getDomain())
		.set(INVOICE_DETAIL.INVOICE, invoice)
		.set(INVOICE_DETAIL.LINE, (short) 0)
		.set(INVOICE_DETAIL.DESCRIPTION, invoiceDetail.getDescription())
		.set(INVOICE_DETAIL.QUANTITY, 1.0)
		.set(INVOICE_DETAIL.PRICE, invoiceTax.getBase())
		.set(INVOICE_DETAIL.DISCOUNT_EXPR, "0")
		.set(INVOICE_DETAIL.SOURCE, (byte) 10)
		.set(INVOICE_DETAIL.TAXABLE_BASE, invoiceTax.getBase())
		.set(INVOICE_DETAIL.TAXES, 0.0)
		.set(INVOICE_DETAIL.PREPAYMENT, (byte) 0)
		.set(INVOICE_DETAIL.WORKPLACE, invoiceDetail.getWorkplace())
		.set(INVOICE_DETAIL.CREATION_DATE, invoiceDetail.getDate())
		.execute();
		
		Integer id = dslContext.select(INVOICE_DETAIL.ID)
		.from(INVOICE_DETAIL)
		.where(INVOICE_DETAIL.DOMAIN.eq(invoiceDetail.getDomain()))
		.and(INVOICE_DETAIL.INVOICE.eq(invoice))
		.and(INVOICE_DETAIL.TAXABLE_BASE.eq(invoiceTax.getBase()))
		.orderBy(INVOICE_DETAIL.ID.desc())
		.fetch().stream().map(r -> r.getValue(INVOICE_DETAIL.ID))
		.findFirst().orElse(null);
		
		if(id != null) {
			dslContext.update(INVOICE_TAX)
			.set(INVOICE_TAX.INVOICE_DETAIL, id)
			.where(INVOICE_TAX.ID.eq(invoiceTax.getId()))
			.execute();
		}
	}
	
	class InvoiceDetail {

		private Integer id;
		private Integer domain;
		private String description;
		private Timestamp date;
		private Integer item;
		private Integer workplace;
		
		private Integer invoice;
		
		public InvoiceDetail() {
			
		}
		
		public Integer getId() {
			return id;
		}
		
		public InvoiceDetail setId(Integer id) {
			this.id = id;
			return this;
		}
		
		public Integer getDomain() {
			return domain;
		}
		
		public InvoiceDetail setDomain(Integer domain) {
			this.domain = domain;
			return this;
		}
		
		public Timestamp getDate() {
			return date;
		}
		
		public InvoiceDetail setDate(Timestamp date) {
			this.date = date;
			return this;
		}
		
		public String getDescription() {
			return description;
		}
		
		public InvoiceDetail setDescription(String description) {
			this.description = description;
			return this;
		}
		
		public Integer getItem() {
			return item;
		}
		
		public InvoiceDetail setItem(Integer item) {
			this.item = item;
			return this;
		}
		
		public Integer getWorkplace() {
			return workplace;
		}
		
		public InvoiceDetail setWorkplace(Integer workplace) {
			this.workplace = workplace;
			return this;
		}
		
		public Integer getInvoice() {
			return invoice;
		}
		
		public InvoiceDetail setInvoice(Integer invoice) {
			this.invoice = invoice;
			return this;
		}
	}
	
	class InvoiceTax {

		private Integer id;
		private double base;
		
		public InvoiceTax() {
			
		}
		
		public Integer getId() {
			return id;
		}
		
		public InvoiceTax setId(Integer id) {
			this.id = id;
			return this;
		}
		
		public double getBase() {
			return base;
		}
		
		public InvoiceTax setBase(double base) {
			this.base = base;
			return this;
		}
		
	}
	
	public static void main(String[] args) {
		Timestamp d = new Timestamp(new Date().getTime());
		System.out.println(d);
		d.setSeconds(d.getSeconds()-1);
		System.out.println(d);
	}
}
