package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Series;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
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
				invoice.setVatQuota(tb.getVatQuota(invoice) + tb.getSurchargeQuota(invoice));
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
			fixSalesInvoiceSeries(ctx, domain);
		}
	}

	private static void fixSalesInvoiceSeries(AONContext ctx, int domain) {
		List<Series> seriesList = SeriesDAO.stream(ctx, domain).toList();
		Integer scope = ctx.getDslContext().selectDistinct(INVOICE.SCOPE).from(INVOICE)
		.where(INVOICE.DOMAIN.eq(domain))
		.and(INVOICE.TYPE.eq(InvoiceType.SALES.value()))
		.and(INVOICE.RECTIFICATION_TYPE.eq(RectificationType.NONE.value()))
		.and(INVOICE.ISSUE_DATE.ge(AonDateUtils.toSql(AonDateUtils.getYearFirstDay(new Date()))))
		.fetch().stream().map(r -> r.getValue(INVOICE.SCOPE)).findFirst().orElse(null);
		
		if(scope != null) {
			ctx.getDslContext().selectDistinct(INVOICE.SERIES).from(INVOICE)
			.where(INVOICE.DOMAIN.eq(domain))
			.and(INVOICE.TYPE.eq(InvoiceType.SALES.value()))
			.and(INVOICE.RECTIFICATION_TYPE.eq(RectificationType.NONE.value()))
			.and(INVOICE.ISSUE_DATE.ge(AonDateUtils.toSql(AonDateUtils.getYearFirstDay(new Date()))))
			.fetch().stream().map(r -> r.getValue(INVOICE.SERIES)).forEach(series -> {
				if(seriesList.stream().filter(s -> s.isInvoice() && s.isActive() && s.getCode().equals(series)).count() == 0) {
					SeriesDAO.save(ctx, new Series()
							.setDomain(domain)
							.setScope(scope)
							.setCode(series)
							.setSecurityLevel(SecurityLevel.OFFICIAL)
							.setDescription(series)
							.setInvoice(true)
							.setActive(true));
				}
			});
			
			ctx.getDslContext().selectDistinct(INVOICE.SERIES).from(INVOICE)
			.where(INVOICE.DOMAIN.eq(domain))
			.and(INVOICE.TYPE.eq(InvoiceType.SALES.value()))
			.and(INVOICE.RECTIFICATION_TYPE.ne(RectificationType.NONE.value()))
			.and(INVOICE.ISSUE_DATE.ge(AonDateUtils.toSql(AonDateUtils.getYearFirstDay(new Date()))))
			.fetch().stream().map(r -> r.getValue(INVOICE.SERIES)).forEach(series -> {
				if(seriesList.stream().filter(s -> s.isRectification() && s.isActive() && s.getCode().equals(series)).count() == 0) {
					SeriesDAO.save(ctx, new Series()
							.setDomain(domain)
							.setScope(scope)
							.setCode(series)
							.setSecurityLevel(SecurityLevel.OFFICIAL)
							.setDescription(series)
							.setRectification(true)
							.setActive(true));
				}
			});
			
			AppParamDAO.save(ctx, domain, AppParam.INVOICE_FIX_SERIES, "true");	
		}
	}
	
	public static boolean isInvoiceFixSeriesApplied(AONContext ctx, int domain) {
		Optional<ApplicationParameter> param = AppParamDAO.get(ctx, domain, AppParam.INVOICE_FIX_SERIES);
		return param.isPresent() && "true".equals(param.get().getValue());
	}
}
