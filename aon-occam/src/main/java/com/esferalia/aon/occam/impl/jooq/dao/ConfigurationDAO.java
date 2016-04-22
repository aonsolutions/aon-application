package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Series.SERIES;

import java.util.Date;
import java.util.LinkedList;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class ConfigurationDAO {

	public static AonConfiguration getConfiguration(final AONContext ctx, Date atDate) {
		ctx.checkRead();
		AonConfiguration conf = new AonConfiguration()
				.setCompany(CompanyDAO.getCompany(ctx, ctx.getDomainId()))
				.setUser(SecurityDAO.getUser(ctx))
				.setPeriods(AccountPeriodDAO.getPeriods(ctx, 
						p -> p.getDomainProperty().eq(ctx.getDomainId())
							.and(p.getStatusProperty().in( new Byte[] {
									 AccountPeriodStatus.ACTIVE.getValue()
									,AccountPeriodStatus.OPENING.getValue()
									,AccountPeriodStatus.OPERATING.getValue()} )))
						.collect(Collectors.toCollection(LinkedList::new)))
				.setInvoiceSalesSeries(ctx.getDslContext()
						.select(SERIES.CODE)
						.from(SERIES)
						.where(SERIES.DOMAIN.eq(ctx.getDomainId())
								.and(SERIES.ACTIVE.eq((byte) 1))
								.and(SERIES.INVOICE.eq((byte) 1)))
						.fetch()
						.stream() 
						.map(rec -> rec.getValue(SERIES.CODE))
						.collect(Collectors.toCollection(LinkedList::new)))
				.setEnterpriseActivities( CompanyDAO.getEnterpriseActivities(ctx,ctx.getDomainId(),atDate)
						.collect(Collectors.toCollection(LinkedList::new)))
				.setWorkplaces( WorkplaceDAO.getWorkplaceList(ctx, 
						p -> p.getDomainProperty().eq(ctx.getDomainId())
						.and(p.getActiveProperty().eq( (byte) 1 ))
						.and(p.getScopeProperty().in( SecurityDAO.getUserScopes(ctx) ))))
				.setVatTaxes( TaxDAO.getVatTaxs(ctx,atDate).collect(Collectors.toCollection(LinkedList::new)))
				.setDefaultVatPercent(AppParamDAO.fetchIntValue(ctx, AppParam.ACC_DEFAULT_VAT_PERCENT))
				.setWithholdingTaxes( TaxDAO.getWithholdingTaxs(ctx,atDate).collect(Collectors.toCollection(LinkedList::new)))
				.setDefaultWithholdingPercent(AppParamDAO.fetchIntValue(ctx, AppParam.ACC_DEFAULT_RETENTION_PERCENT))
				.setDefaultInvoiceSeries(AppParamDAO.fetchValue(ctx, AppParam.ACC_DEFAULT_INVOICE_SERIES))
				.setDefaultSalesAccount( getAccount(ctx, AppParam.ACC_DEFAULT_SALES_ACC) )
				.setDefaultPurchaseAccount( getAccount(ctx, AppParam.ACC_DEFAULT_PURCHASE_ACC) )
				.setDefaultChargedVatAccount( getAccount(ctx, AppParam.ACC_DEFAULT_CHARGED_VAT_ACC) )
				.setDefaultPaidVatAccount( getAccount(ctx, AppParam.ACC_DEFAULT_PAID_VAT_ACC) )
				.setDefaultChargedRetAccount( getAccount(ctx, AppParam.ACC_DEFAULT_CHARGED_RET_ACC) )
				.setDefaultPaidRetAccount( getAccount(ctx, AppParam.ACC_DEFAULT_PAID_RET_ACC) )
		;
		return conf;
	}
	
	private static Account getAccount(AONContext ctx, AppParam param ) {
		String value = AppParamDAO.fetchValue(ctx, param);
		if (AonStringUtils.isNotBlank(value)) {
			Integer id = AonNumberUtils.toInteger(value);
			if (id != null) return AccountDAO.get(ctx, id);
		}
		return null;
	}
}
