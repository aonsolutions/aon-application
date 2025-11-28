package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;

import java.util.Date;
import java.util.Optional;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.watson.server.AonDateUtils;

public class InvoiceFixDAO {

	private InvoiceFixDAO() {

	}
	
	public static void invoiceTaxableBase0Fix(AONContext ctx, int invoiceId) {
		Invoice invoice = InvoiceDAO.getFullInvoice(ctx, invoiceId);
		if(invoice.getTaxableBase() == 0.0 || (invoice.getVatQuota() == 0.0 && invoice.getTotal() != invoice.getTaxableBase())) {
			invoice.refreshTaxBreakdown();
			invoice.getTaxBreakdown().ifPresent( tb -> {
				invoice.setTaxableBase(tb.getVatBase());
				invoice.setVatQuota(tb.getVatQuota() + tb.getSurchargeQuota());
				invoice.setRetentionQuota(tb.getRetentionQuota());
				
				ctx.getDslContext().update(INVOICE)
					.set(INVOICE.TAXABLE_BASE, invoice.getTaxableBase())
					.set(INVOICE.VAT_QUOTA, invoice.getVatQuota())
					.set(INVOICE.RETENTION_QUOTA, invoice.getRetentionQuota())
					.where(INVOICE.ID.eq(invoice.getId()))
					.execute();
			});
		}
	}
	
	public static void fixInvoice(AONContext ctx, int domain) {
		if(!isInvoiceFixSeriesApplied(ctx, domain)) {
			// SeriesDAO.fixSalesInvoiceSeries(ctx, domain, AonDateUtils.getYear(new Date()));
			// AppParamDAO.save(ctx, domain, AppParam.INVOICE_FIX_SERIES, "true");
		}
	}
	
	public static boolean isInvoiceFixSeriesApplied(AONContext ctx, int domain) {
		Optional<ApplicationParameter> param = AppParamDAO.get(ctx, domain, AppParam.INVOICE_FIX_SERIES);
		return param.isPresent() && "true".equals(param.get().getValue());
	}
}
