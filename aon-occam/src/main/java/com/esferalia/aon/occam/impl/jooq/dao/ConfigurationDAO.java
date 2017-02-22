package com.esferalia.aon.occam.impl.jooq.dao;

import java.util.Date;
import java.util.LinkedList;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class ConfigurationDAO {
	public static AonConfiguration getConfiguration(final AONContext ctx) {
		return getConfiguration(ctx, new Date());
	}
	
	public static AonConfiguration getConfiguration(final AONContext ctx, Date atDate) {
		ctx.checkRead();
		int defaultVatPercent = AppParamDAO.fetchIntValue(ctx, AppParam.ACC_DEFAULT_VAT_PERCENT);
		int defaultWithholdingPercent = AppParamDAO.fetchIntValue(ctx, AppParam.ACC_DEFAULT_RETENTION_PERCENT);
		AonConfiguration conf = new AonConfiguration()
				.setCompany(CompanyDAO.getCompany(ctx, ctx.getDomainId()))
				.setUser(SecurityDAO.getUser(ctx))
				.setPeriods(AccountPeriodDAO.getPeriods(ctx, 
						p -> p.getDomainProperty().eq(ctx.getDomainId())
//							.and(p.getStatusProperty().in( new Byte[] {
//									 AccountPeriodStatus.ACTIVE.getValue()
//									,AccountPeriodStatus.OPENING.getValue()
//									,AccountPeriodStatus.OPERATING.getValue()} ))
							)
						.collect(Collectors.toCollection(LinkedList::new)))
				.setEnterpriseActivities( CompanyDAO.getEnterpriseActivities(ctx,ctx.getDomainId(),atDate)
						.collect(Collectors.toCollection(LinkedList::new)))
				.setInvestAsset( CompanyDAO.getInvestAssets(ctx,ctx.getDomainId(),atDate)
						.collect(Collectors.toCollection(LinkedList::new)))
				.setWorkplaces( WorkplaceDAO.getWorkplaceList(ctx, 
						p -> {
							Integer[] userScopes = SecurityDAO.getUserScopes(ctx);
							return (userScopes == null) 
								? p.getDomainProperty().eq(ctx.getDomainId())
									.and(p.getActiveProperty().eq( (byte) 1 ))
								: p.getDomainProperty().eq(ctx.getDomainId())
									.and(p.getActiveProperty().eq( (byte) 1 ))
									.and(p.getScopeProperty().in( userScopes ));
						}
						))
				.setVatTaxes( TaxDAO.getVatTaxs(ctx,atDate).collect(Collectors.toCollection(LinkedList::new)))
				.setGeozones( GeoZoneDAO.getGeoZones(ctx, null).collect(Collectors.toCollection(LinkedList::new)))
				.setAvailableScopes(SecurityDAO.getAvailableScopes (ctx))
				.setPayMethods(FinanceDAO.getPayMethods(ctx))
				.setDefaultVatPercent(defaultVatPercent == 0
					?null
					:TaxDAO.getTax(ctx, filter -> filter.getIdProperty().eq(defaultVatPercent)))
				.setWithholdingTaxes( TaxDAO.getWithholdingTaxs(ctx,atDate).collect(Collectors.toCollection(LinkedList::new)))
				.setDefaultWithholdingPercent(defaultWithholdingPercent== 0
					?null
					:TaxDAO.getTax(ctx, filter -> filter.getIdProperty().eq(defaultWithholdingPercent)))
				.setDefaultInvoiceSeries(AppParamDAO.fetchValue(ctx, AppParam.ACC_DEFAULT_INVOICE_SERIES))
				.setDefaultSalesAccount( getAccount(ctx, AppParam.ACC_DEFAULT_SALES_ACC) )
				.setDefaultPurchaseAccount( getAccount(ctx, AppParam.ACC_DEFAULT_PURCHASE_ACC) )
				.setDefaultChargedVatAccount( getAccount(ctx, AppParam.ACC_DEFAULT_CHARGED_VAT_ACC) )
				.setDefaultPaidVatAccount( getAccount(ctx, AppParam.ACC_DEFAULT_PAID_VAT_ACC) )
				.setDefaultChargedRetAccount( getAccount(ctx, AppParam.ACC_DEFAULT_CHARGED_RET_ACC) )
				.setDefaultPaidRetAccount( getAccount(ctx, AppParam.ACC_DEFAULT_PAID_RET_ACC) )
				.setVatNegativeAdjustAccount( getAccount(ctx, AppParam.ACC_VAT_NEGATIVE_ADJUST_ACC) )
				.setOperationsDeadline( AppParamDAO.fetchDateValue(ctx, AppParam.ACC_OPERATIONS_DEADLINE))
				.setDefaultSalary( getAccount(ctx, AppParam.ACC_DEFAULT_SALARY_ACC ) )
				.setDefaultSalaryInKind( getAccount(ctx, AppParam.ACC_DEFAULT_SALARY_IK_ACC ) )
				.setDefaultAllowance(getAccount(ctx, AppParam.ACC_DEFAULT_ALLOWANCE_ACC))
				.setDefaultCompensation(getAccount(ctx, AppParam.ACC_DEFAULT_COMPENSATION_ACC))
				.setDefaultCompanySocIns(getAccount(ctx, AppParam.ACC_DEFAULT_COMPANY_SOC_INS_ACC ))
				.setSalaryChargedRet(getAccount(ctx, AppParam.ACC_SALARY_CHARGED_RET_ACC ))
				.setSalaryChargedRetInKind(getAccount(ctx, AppParam.ACC_SALARY_CHARGED_RET_IK_ACC ))
				.setDefaultSocialInsurance(getAccount(ctx, AppParam.ACC_DEFAULT_SOCIAL_INSURANCE_ACC ))
				.setDefaultPendingSalary(getAccount(ctx, AppParam.ACC_DEFAULT_PENDING_SALARY_ACC ))
				.setSalaryDedAdvPayment(getAccount(ctx, AppParam.ACC_SALARY_DED_ADV_PAYMENT_ACC))
				.setSalaryOtherDeductions(getAccount(ctx, AppParam.ACC_SALARY_DED_OTHER_ACC))
				.setSalaryDedSeize(getAccount(ctx, AppParam.ACC_SALARY_DED_SEIZE_ACC))
		;
		SeriesDAO
			.getSeries(ctx,
					p -> 
					p.getDomainProperty().in( SecurityDAO.getInheritanceDomainIds(ctx) )
					.and(p.getActiveProperty().eq((byte) 1)  )
					.and(p.getInvoiceProperty().eq((byte) 1)  
					.or(p.getRectificationProperty().eq((byte) 1) )))
			.forEach(series -> {
				conf.addInvoiceSalesSeries(series.getCode());
				if (series.isRectification()) {
					conf.addInvoiceRectificationSalesSeries(series.getCode());
				} 
				});
		return conf;
	}
	
	private static Account getAccount(AONContext ctx, AppParam param ) {
		String value = AppParamDAO.fetchValue(ctx, param);
		if (AonStringUtils.isNotBlank(value)) {
			Integer id =AonStringUtils.equals("{null}", value)
				?null
				:AonNumberUtils.toInteger(value);
			if (id != null) return AccountDAO.get(ctx, id);
		}
		return null;
	}
}
