package com.esferalia.aon.occam.impl.jooq.dao.invoiceduplicatefix;

import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;
import static com.esferalia.aon.jooq.tables.InvoiceTaxAccount.INVOICE_TAX_ACCOUNT;

import java.sql.Timestamp;
import java.util.List;

import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDetailDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceTaxDAO;

public class InvoiceDuplicateFixDAO {
	
	private InvoiceDuplicateFixDAO() {

	}

	public static void invoiceTaxDuplicateFix(AONContext ctx, Integer invoiceId) {
		getInvoiceDetails(ctx, invoiceId).stream().forEach(invoiceDetailId -> {
			deleteDuplicateInvoiceVatTax(ctx, invoiceDetailId);
			deleteDuplicateInvoiceRetentionTax(ctx, invoiceDetailId);
		});
		deleteDuplicateInvoiceDetail(ctx, invoiceId);
	}
	
	private static void deleteDuplicateInvoiceVatTax(AONContext ctx, Integer invoiceDetailId) {
		List<InvoiceTax> taxes = InvoiceTaxDAO.getList(ctx, f -> f.getInvoiceDetailProperty().eq(invoiceDetailId)
				.and(f.getTaxTypeProperty().eq(TaxType.VAT.value())));
		if(taxes.size() > 1) {
			InvoiceTax vatTax = null;
			for (InvoiceTax tax : taxes) {
				if(tax.isVatType() && vatTax == null && !hasInvoiceTaxAccount(ctx, tax.getId())) 
					vatTax = tax;
			}
			if(vatTax == null) vatTax = taxes.get(0);
			InvoiceTaxDAO.delete(ctx, vatTax.getId());				
		}
	}
	
	private static void deleteDuplicateInvoiceRetentionTax(AONContext ctx, Integer invoiceDetailId) {
		List<InvoiceTax> taxes = InvoiceTaxDAO.getList(ctx, f -> f.getInvoiceDetailProperty().eq(invoiceDetailId)
				.and(f.getTaxTypeProperty().eq(TaxType.RETENTION.value())));
		if(taxes.size() > 1) {
			InvoiceTax retentionTax = null;
			for (InvoiceTax tax : taxes) {
				if (retentionTax == null && !hasInvoiceTaxAccount(ctx, tax.getId())) 
					retentionTax = tax;
			}
			if(retentionTax == null) retentionTax = taxes.get(0);
			InvoiceTaxDAO.delete(ctx, retentionTax.getId());				
		}
	}
	
	private static boolean hasInvoiceTaxAccount(AONContext ctx, Integer invoiceTax) {
		return ctx.getDslContext().select().from(INVOICE_TAX_ACCOUNT).where(INVOICE_TAX_ACCOUNT.INVOICE_TAX.eq(invoiceTax)).fetch().stream().findFirst().isPresent();
	}
	
	public static void invoiceIrpfDuplicateFix(AONContext ctx) {
		getInvoiceIrpfDetails(ctx).stream().forEach(invoiceDetailId -> {
			IDFInvoiceDetail invoiceDetail = getInvoiceDetail(ctx, invoiceDetailId);
			Integer invoice = getInvoice(ctx, invoiceDetail);
			List<IDFInvoiceTax > taxes = getInvoiceTaxes(ctx, invoiceDetail);
			long vatCont = taxes.stream().filter(f -> TaxType.VAT.equals(f.getType())).count();
			long retentionCont = taxes.stream().filter(f -> TaxType.RETENTION.equals(f.getType())).count();
			if(taxes.size() == 4 && vatCont ==2 && retentionCont == 2 && invoice != null && invoice != invoiceDetail.getInvoice()) {
				IDFInvoiceTax vat = taxes.stream().filter(f -> TaxType.VAT.equals(f.getType())).findFirst().orElse(null);
				IDFInvoiceTax retention = taxes.stream().filter(f -> TaxType.RETENTION.equals(f.getType())).findFirst().orElse(null);
				saveIrpf(ctx, invoiceDetail, vat, retention, invoice);
			}
		});
	}
	
	private static List<Integer> getInvoiceIrpfDetails(AONContext ctx) {
		return ctx.getDslContext().select(INVOICE_TAX.DOMAIN, INVOICE_TAX.INVOICE_DETAIL, DSL.count(INVOICE_TAX.INVOICE_DETAIL))
		.from(INVOICE_TAX)
		.where(INVOICE_TAX.DOMAIN.eq(ctx.getDomainId()))
		.groupBy(INVOICE_TAX.INVOICE_DETAIL)
		.having(DSL.count(INVOICE_TAX.INVOICE_DETAIL).eq(4))
		.fetch().stream()
		.map(r -> r.getValue(INVOICE_TAX.INVOICE_DETAIL))
		.toList();
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
	
	private static List<Integer> getInvoiceDetails(AONContext ctx, Integer invoice) {
		return ctx.getDslContext().select(INVOICE_TAX.DOMAIN, INVOICE_TAX.INVOICE_DETAIL, DSL.count(INVOICE_TAX.INVOICE_DETAIL))
		.from(INVOICE_TAX)
		.where(INVOICE_TAX.DOMAIN.eq(ctx.getDomainId()))
		.and(INVOICE_TAX.INVOICE_DETAIL.in(
				ctx.getDslContext()
				.select(INVOICE_DETAIL.ID)
				.from(INVOICE_DETAIL)
				.where(INVOICE_DETAIL.INVOICE.eq(invoice))))
		.groupBy(INVOICE_TAX.INVOICE_DETAIL, INVOICE_TAX.TAX_TYPE)
		.having(DSL.count(INVOICE_TAX.INVOICE_DETAIL).eq(2))
		.fetch().stream()
		.map(r -> r.getValue(INVOICE_TAX.INVOICE_DETAIL))
		.toList();
	}
	
	private static void deleteDuplicateInvoiceDetail(AONContext ctx, Integer invoice) {
		ctx.getDslContext().select()
		.from(INVOICE_DETAIL)
		.leftOuterJoin(INVOICE_TAX).on(INVOICE_TAX.INVOICE_DETAIL.eq(INVOICE_DETAIL.ID))
		.where(INVOICE_DETAIL.INVOICE.eq(invoice))
		.and(INVOICE_TAX.ID.isNull())
		.and(INVOICE_DETAIL.LINE.eq((short) 0))
		.and(INVOICE_DETAIL.SOURCE.eq((byte) 10))
		.and(INVOICE_DETAIL.CREATION_USER.isNull())
		.fetch()
		.stream()
		.map(r -> r.getValue(INVOICE_DETAIL.ID))
		.forEach(id -> {
			ctx.getDslContext().delete(INVOICE_DETAIL).where(INVOICE_DETAIL.ID.eq(id)).execute();	
		});
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
		return ctx.getDslContext().select(INVOICE_TAX.ID, INVOICE_TAX.BASE, INVOICE_TAX.TAX_TYPE)
			.from(INVOICE_TAX)
			.where(INVOICE_TAX.DOMAIN.eq(invoiceDetail.getDomain()))
			.and(INVOICE_TAX.INVOICE_DETAIL.eq(invoiceDetail.getId()))
			.fetch().stream().map(r -> new IDFInvoiceTax ()
					.setId(r.getValue(INVOICE_TAX.ID))
					.setType(TaxType.safeValueOf(r.getValue(INVOICE_TAX.TAX_TYPE)))
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
	
	private static void saveIrpf(AONContext ctx, IDFInvoiceDetail invoiceDetail, IDFInvoiceTax vat, IDFInvoiceTax retention, Integer invoice) {
		ctx.getDslContext().insertInto(INVOICE_DETAIL)
		.set(INVOICE_DETAIL.DOMAIN, invoiceDetail.getDomain())
		.set(INVOICE_DETAIL.INVOICE, invoice)
		.set(INVOICE_DETAIL.LINE, (short) 0)
		.set(INVOICE_DETAIL.DESCRIPTION, invoiceDetail.getDescription())
		.set(INVOICE_DETAIL.QUANTITY, 1.0)
		.set(INVOICE_DETAIL.PRICE, vat.getBase())
		.set(INVOICE_DETAIL.DISCOUNT_EXPR, "0")
		.set(INVOICE_DETAIL.SOURCE, (byte) 10)
		.set(INVOICE_DETAIL.TAXABLE_BASE, vat.getBase())
		.set(INVOICE_DETAIL.TAXES, 0.0)
		.set(INVOICE_DETAIL.PREPAYMENT, (byte) 0)
		.set(INVOICE_DETAIL.WORKPLACE, invoiceDetail.getWorkplace())
		.execute();
		
		Integer id = ctx.getDslContext().select(INVOICE_DETAIL.ID)
		.from(INVOICE_DETAIL)
		.where(INVOICE_DETAIL.DOMAIN.eq(invoiceDetail.getDomain()))
		.and(INVOICE_DETAIL.INVOICE.eq(invoice))
		.and(INVOICE_DETAIL.TAXABLE_BASE.eq(vat.getBase()))
		.orderBy(INVOICE_DETAIL.ID.desc())
		.fetch().stream().map(r -> r.getValue(INVOICE_DETAIL.ID))
		.findFirst().orElse(null);
		
		if(id != null) {
			ctx.getDslContext().update(INVOICE_TAX)
			.set(INVOICE_TAX.INVOICE_DETAIL, id)
			.where(INVOICE_TAX.ID.eq(vat.getId()))
			.execute();
			
			ctx.getDslContext().update(INVOICE_TAX)
			.set(INVOICE_TAX.INVOICE_DETAIL, id)
			.where(INVOICE_TAX.ID.eq(retention.getId()))
			.execute();
		}
	}	
}
