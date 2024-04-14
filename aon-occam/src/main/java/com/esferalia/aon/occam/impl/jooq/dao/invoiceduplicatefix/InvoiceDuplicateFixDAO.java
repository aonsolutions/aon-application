package com.esferalia.aon.occam.impl.jooq.dao.invoiceduplicatefix;

import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;

import java.sql.Timestamp;
import java.util.List;

import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;


public class InvoiceDuplicateFixDAO {
	
	private InvoiceDuplicateFixDAO() {

	}

	public static void invoiceDuplicateFix(AONContext ctx) {
		getInvoiceDetails(ctx).stream().forEach(invoiceDetailId -> {
			IDFInvoiceDetail invoiceDetail = getInvoiceDetail(ctx, invoiceDetailId);
			Integer invoice = getInvoice(ctx, invoiceDetail);
			List<IDFInvoiceTax > taxes = getInvoiceTaxes(ctx, invoiceDetail);
			if(taxes.size() == 2 && invoice != null && invoice != invoiceDetail.getInvoice()) {
				save(ctx, invoiceDetail, taxes.get(0), invoice);
			}
		});
	}
	
	private static List<Integer> getInvoiceDetails(AONContext ctx) {
		return ctx.getDslContext().select(INVOICE_TAX.DOMAIN, INVOICE_TAX.INVOICE_DETAIL, DSL.count(INVOICE_TAX.INVOICE_DETAIL))
		.from(INVOICE_TAX)
		.where(INVOICE_TAX.DOMAIN.eq(ctx.getDomainId()))
		.groupBy(INVOICE_TAX.INVOICE_DETAIL, INVOICE_TAX.TAX_TYPE)
		.having(DSL.count(INVOICE_TAX.INVOICE_DETAIL).eq(2))
		.fetch().stream()
		.map(r -> r.getValue(INVOICE_TAX.INVOICE_DETAIL))
		.toList();
	}
	
	private static IDFInvoiceDetail getInvoiceDetail(AONContext ctx, Integer invoiceDetailId) {
		return ctx.getDslContext().select()
			.from(INVOICE_DETAIL)
			.where(INVOICE_DETAIL.ID.eq(invoiceDetailId))
			.fetch().stream().map(r -> new IDFInvoiceDetail()
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
	
	private static Integer getInvoice(AONContext ctx, IDFInvoiceDetail invoiceDetail) {
		Integer inv =  ctx.getDslContext().select(INVOICE.ID)
			.from(INVOICE)
			.where(INVOICE.DOMAIN.eq(invoiceDetail.getDomain()))
			.and(INVOICE.CREATION_DATE.eq(invoiceDetail.getDate()))
			.fetch().stream().map(r -> r.getValue(INVOICE.ID))
			.findFirst().orElse(null);
		if(inv == null) {
			Timestamp date = invoiceDetail.getDate();   
			date.setSeconds(date.getSeconds()-1);
			inv =  ctx.getDslContext().select(INVOICE.ID)
					.from(INVOICE)
					.where(INVOICE.DOMAIN.eq(invoiceDetail.getDomain()))
					.and(INVOICE.CREATION_DATE.eq(date))
					.fetch().stream().map(r -> r.getValue(INVOICE.ID))
					.findFirst().orElse(null);
		}
		return inv;
	}
	
	private static List<IDFInvoiceTax > getInvoiceTaxes(AONContext ctx, IDFInvoiceDetail invoiceDetail) {
		return ctx.getDslContext().select(INVOICE_TAX.ID, INVOICE_TAX.BASE)
			.from(INVOICE_TAX)
			.where(INVOICE_TAX.DOMAIN.eq(invoiceDetail.getDomain()))
			.and(INVOICE_TAX.INVOICE_DETAIL.eq(invoiceDetail.getId()))
			.fetch().stream().map(r -> new IDFInvoiceTax ()
					.setId(r.getValue(INVOICE_TAX.ID))
					.setBase(r.getValue(INVOICE_TAX.BASE)))
			.toList();
	}
	
	private static void save(AONContext ctx, IDFInvoiceDetail invoiceDetail, IDFInvoiceTax invoiceTax, Integer invoice) {
		ctx.getDslContext().insertInto(INVOICE_DETAIL)
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
		.execute();
		
		Integer id = ctx.getDslContext().select(INVOICE_DETAIL.ID)
		.from(INVOICE_DETAIL)
		.where(INVOICE_DETAIL.DOMAIN.eq(invoiceDetail.getDomain()))
		.and(INVOICE_DETAIL.INVOICE.eq(invoice))
		.and(INVOICE_DETAIL.TAXABLE_BASE.eq(invoiceTax.getBase()))
		.orderBy(INVOICE_DETAIL.ID.desc())
		.fetch().stream().map(r -> r.getValue(INVOICE_DETAIL.ID))
		.findFirst().orElse(null);
		
		if(id != null) {
			ctx.getDslContext().update(INVOICE_TAX)
			.set(INVOICE_TAX.INVOICE_DETAIL, id)
			.where(INVOICE_TAX.ID.eq(invoiceTax.getId()))
			.execute();
		}
	}	
}
