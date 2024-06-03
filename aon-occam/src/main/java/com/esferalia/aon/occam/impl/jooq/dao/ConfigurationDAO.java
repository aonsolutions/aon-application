package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.AutoConcept;
import com.esferalia.aon.occam.api.model.config.ConfigBlock;
import com.esferalia.aon.occam.api.model.config.ConfigParams;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.ApplicationParameterFiller;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class ConfigurationDAO {
	
	
	private static final Logger LOGGER = Logger.getLogger(ConfigurationDAO.class.getName());
	
	private ConfigurationDAO() {
	}
	
	public static AonConfiguration getConfiguration(final AONContext ctx) {
		return getConfiguration(ctx, new Date());
	}

	public static AonConfiguration getConfiguration(final AONContext ctx, Date atDate) {
		return getConfiguration(ctx, new ConfigParams().setAtDate(atDate).setBlocks(ConfigBlock.ALL) );
	}
	
	public static AonConfiguration getAccountingConfiguration(AONContext ctx) {
		return getAccountingConfiguration(ctx, new Date());
	}
	public static AonConfiguration getAccountingConfiguration(AONContext ctx, Date atDate) {
		return getConfiguration(ctx, new ConfigParams().setAtDate(atDate).setBlocks(ConfigBlock.ACCOUNTING) );
	}
	
	public static AonConfiguration getConfiguration(final AONContext ctx, ConfigParams params) {
		ctx.checkRead();
		if (params == null) {
			throw new IllegalArgumentException("Params can not be null");
		}
		if (params.getAtDate() == null) {
			params.setAtDate(new Date());
		}
		
		AonConfiguration conf = getBasicConfiguration(ctx,params);
		
		Integer operator = AON.getTaskHolder(conf.getDomain().getName(), conf.getDomain().getId(), conf.getUser().getLogin(), 
				f -> f.getDomainProperty().eq(conf.getDomain().getId())
				.and(f.getUserIdProperty().eq(conf.getUser().getId()))).getId();

		conf.setDur(SecurityDAO.getDomainUserRoles(ctx, conf.getUser().getId()));
		
		int defaultVatPercent = AppParamDAO.fetchIntValue(ctx, AppParam.ACC_DEFAULT_VAT_PERCENT);
		int defaultWithholdingPercent = AppParamDAO.fetchIntValue(ctx, AppParam.ACC_DEFAULT_RETENTION_PERCENT);
		Integer[] userScopes = SecurityDAO.getUserScopes(ctx, conf.getUser().getId());
		conf.setMd5(getMd5(conf.getUser().getLogin()+conf.getDomain().getName()))
			.setUserOperator(operator)
			.setEnterpriseActivities( CompanyDAO.getEnterpriseActivities(ctx,ctx.getDomainId(), params.getAtDate()).collect(Collectors.toCollection(LinkedList::new)))
			.setAllEnterpriseActivities( CompanyDAO.getEnterpriseActivities(ctx,ctx.getDomainId()).collect(Collectors.toCollection(LinkedList::new)))
			.setInvestAsset( CompanyDAO.getInvestAssets(ctx,ctx.getDomainId(), params.getAtDate()).collect(Collectors.toCollection(LinkedList::new)))
			.setWorkplaces( WorkplaceDAO.getWorkplaceList(ctx, 
					p -> (userScopes == null) 
						? p.getDomainProperty().eq(ctx.getDomainId())
							.and(p.getActiveProperty().eq( (byte) 1 ))
						: p.getDomainProperty().eq(ctx.getDomainId())
							.and(p.getActiveProperty().eq( (byte) 1 ))
							.and(p.getScopeProperty().in( userScopes ))
					
					))
			.setVatTaxes( TaxDAO.getVatTaxs(ctx,params.getAtDate()).collect(Collectors.toCollection(LinkedList::new)))
			.setGeozones( GeoZoneDAO.getStream(ctx, null).collect(Collectors.toCollection(LinkedList::new)))
			.setAvailableScopes(SecurityDAO.getAvailableScopes (ctx))
			.setPayMethods(PayMethodDAO.getOrderByNames(ctx))
			.setPayMethodTypeDetails(PayMethodDAO.getPayMethodTypeDetails(ctx))
			.setDefaultVatPercent(defaultVatPercent == 0
				?null
				:TaxDAO.getTax(ctx, filter -> filter.getIdProperty().eq(defaultVatPercent)))
			.setWithholdingTaxes( TaxDAO.getWithholdingTaxs(ctx,params.getAtDate()).collect(Collectors.toCollection(LinkedList::new)))
			.setSegments( RegistrySegmentDAO.getSegments(ctx, p-> p.getDomainProperty().eq( ctx.getDomainId())).collect(Collectors.toCollection(LinkedList::new)))
			.setDefaultWithholdingPercent(defaultWithholdingPercent== 0
				?null
				:TaxDAO.getTax(ctx, filter -> filter.getIdProperty().eq(defaultWithholdingPercent)))
			.setDefaultInvoiceSeries(AppParamDAO.fetchValue(ctx, AppParam.ACC_DEFAULT_INVOICE_SERIES))
			.setOperationsDeadline( AppParamDAO.fetchDateValue(ctx, AppParam.ACC_OPERATIONS_DEADLINE))
			.setChildDomains(DomainDAO.getActiveChildDomains(ctx))
			.setDefaultCreditor(getDefaultCreditor(ctx))
			.setOCRActive(SecurityDAO.isOCRActive(ctx, ctx.getDomainId()))
			.setOcrDefaultItem( getOcrDefaultItem(ctx) )
			.setBetaEnabled(AonEnumUtils.getAonBoolean(AppParamDAO.fetchValue(ctx, AppParam.AON_BETA_ENABLED)))
			.setAlphaEnabled(AonEnumUtils.getAonBoolean(AppParamDAO.fetchValue(ctx, AppParam.AON_ALPHA_ENABLED)))
		;
		if (params.hasAccounting() ) {
			fillAccountingParameters(ctx, conf);
		}
		if (params.hasFiscal() ) {
			fillFiscalParameters(ctx, conf);
		}

		SeriesDAO.getSeries(ctx, p -> p.getDomainProperty().in( SecurityDAO.getInheritanceDomainIds(ctx) )
						.and(p.getActiveProperty().eq((byte) 1)  )
						.and(p.getInvoiceProperty().eq((byte) 1)  
							.or(p.getRectificationProperty().eq((byte) 1) )))
		.forEach(series -> {
			conf.addInvoiceSalesSeries(series.getCode());
			if (series.isRectification()) {
				conf.addInvoiceRectificationSalesSeries(series.getCode());
			} 
		});
		if (conf.getCompany() != null && conf.getCompany().getId() != null) {
			conf.getCompany().setMainAddress( RegistryOldDAO.getRAddressStream(ctx, 
				p -> p.getRegistryProperty().eq(conf.getCompany().getId()))
				.findFirst().orElse(null));
		}
			
		return conf;
	}
	
	private static AonConfiguration getBasicConfiguration(AONContext ctx, ConfigParams params) {
		AonConfiguration conf = new AonConfiguration()
				.setDomain( DomainDAO.getDomain(ctx, ctx.getDomainId()) )
				.setCompany(CompanyDAO.getCompany(ctx, ctx.getDomainId()));
		if (params.isByToken()) {
			conf.setUser(AON_SOLUTIONS.getUser(conf.getDomain(), params.getToken()))
				.setAonSolutions(true);
		} 
		if(conf.getUser() == null || conf.getUser().isEmpty()) {
			conf.setUser(AON.getUser(conf.getDomain().getName(), conf.getDomain().getId(), ctx.getUser()))
			.setAonSolutions(false);			
		}
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
	
	// ********************************************************************************************
	// ********************************************************************************************
	// ********************************************************************************************
	
	public static Item getOcrDefaultItem(AONContext ctx) {
		int itemId = AppParamDAO.fetchIntValue(ctx, AppParam.OCR_DEFAULT_ITEM);
		if (AonMathUtils.isNotZero(itemId)) {
			return ItemDAO.get(ctx, itemId);
		}
		return null;
	}

	public static AccountingRegistry getDefaultCreditor(AONContext ctx) {
		return AccountingRegistryDAO.getAccountingRegistries(ctx, f -> 
				(f.getDocumentProperty().isNull().or(f.getDocumentProperty().eq(" ")).or(f.getDocumentProperty().eq("")))
				.and(f.getNameProperty().like("%vario%")))
				.filter( ar -> ar.getType() == AccountingRegistryType.CREDITOR)
				.findFirst().orElse(null);
	}

	public static interface IAppParamFiller {
		void fill(AONContext ctx, AonConfiguration config, String value);
	}
	private static final EnumMap<AppParam, IAppParamFiller> APM = new EnumMap<>(AppParam.class);
	static {
		
		// ****************************************************** [FISCAL VALUES]
		APM.put(AppParam.FS_DEFAULT_YEAR, 
			(ctx, config, value) -> config.fiscal().setDefaultYear(AonNumberUtils.toInteger(value)));
		APM.put(AppParam.FS_DEFAULT_ADMINISTRATION, 
			(ctx, config, value) -> config.fiscal().setAdministration(AonNumberUtils.toInteger(value)));
		APM.put(AppParam.FS_ADMINISTRATION_CODE, 
			(ctx, config, value) -> config.fiscal().setAdministrationCode(value));
		APM.put(AppParam.FS_TAX_REFUND_REGISTRY,
			(ctx, config, value) -> config.fiscal().setTaxRefundRegistry( AonEnumUtils.getAonBoolean(value) ));
		APM.put(AppParam.FS_TAX_REGIME, 
			(ctx, config, value) -> config.fiscal().setTaxRegime(AonNumberUtils.toInteger(value)));
		APM.put(AppParam.FS_ADMON_CREDITOR,
			(ctx, config, value) -> config.fiscal().setAdmonCreditor( getCreditor(ctx,value) ));
		APM.put(AppParam.FS_ADMON_VAT_CREDITOR,
			(ctx, config, value) -> config.fiscal().setAdmonVatCreditor(getCreditor(ctx,value) ));
		APM.put(AppParam.FS_ADMON_RETENTION_CREDITOR,
			(ctx, config, value) -> config.fiscal().setAdmonRetentionCreditor(getCreditor(ctx,value) ));
		APM.put(AppParam.FS_PERM_ADDRESS_CHANGES, 
			(ctx, config, value) -> config.fiscal().setPermAddressChanges(AonEnumUtils.getAonBoolean(value) ));
		APM.put(AppParam.FS_CONCTACT_PERSON, 
			(ctx, config, value) -> config.fiscal().setContactPerson(value));
		APM.put(AppParam.FS_CONCTACT_PHONE, 
			(ctx, config, value) -> config.fiscal().setContactPhone(value));
		APM.put(AppParam.FS_CONCTACT_CELLULAR,
			(ctx, config, value) -> config.fiscal().setContactCellular(value));
		APM.put(AppParam.FS_CONCTACT_MAIL, 
			(ctx, config, value) -> config.fiscal().setContactMail(value));
		APM.put(AppParam.FS_MOD303_BY_DIFFERENCE_DISABLED, 
			(ctx, config, value) -> config.fiscal().setMod303ByDifferenceDisabled( AonEnumUtils.getAonBoolean(value) ));
		APM.put(AppParam.FS_CUSTOMER_CHECK_ENABLED, 
			(ctx, config, value) -> config.fiscal().setCustomerCheckEnabled(AonEnumUtils.getAonBoolean(value) ));
		APM.put(AppParam.FS_PRES_MODEL_AUTO_ENABLED, 
				(ctx, config, value) -> config.fiscal().setPresModelAutoEnabled(AonEnumUtils.getAonBoolean(value) ));
		APM.put(AppParam.FS_CERT_DOCUMENT, 
				(ctx, config, value) -> config.fiscal().setCertificateDocument(value));
		APM.put(AppParam.FS_CERT_NAME, 
				(ctx, config, value) -> config.fiscal().setCertificateName(value));
		APM.put(AppParam.FS_AEAT_TEST_ENV, 
				(ctx, config, value) -> config.fiscal().setTestEnvironment(AonEnumUtils.getAonBoolean(value) ));
	}

	private static void fillParam( AONContext ctx, final AonConfiguration config, final ApplicationParameter ap) {
		if (ap != null && AonStringUtils.isNotBlank(ap.getName()) && AonStringUtils.isNotBlank(ap.getValue())) {
			try {
				AppParam param = AppParam.valueOf(ap.getName());
				if (APM.containsKey(param)) {
					APM.get(param).fill(ctx, config, ap.getValue());
				}
			} catch (IllegalArgumentException e) {
				LOGGER.log(Level.WARNING
					, "INCORRECT APP PARAM VALUE ( name: [{0}] , value: [{1}] )"
					,new Object[]{ap.getName(),ap.getValue()});
			}
		}
	}
	
	private static Creditor getCreditor(AONContext ctx, String value) {
		Integer id = AonNumberUtils.toInteger(value);
		Creditor creditor = null;
		if (id != null) {
			creditor = CreditorDAO.get(ctx, id);
		}
		return creditor;
	}

	private static void fillAccountingParameters(AONContext ctx, final AonConfiguration config) {
		config.accounting()
			.setPeriods(AccountPeriodDAO.getPeriods(ctx, p -> p.getDomainProperty().eq(ctx.getDomainId())).collect(Collectors.toCollection(LinkedList::new)))
			.setAutoConcepts(
				AccountEntryDAO.getAutoConcepts(ctx)
					.map(AutoConcept::getDescription)
					.collect(Collectors.toCollection(LinkedList::new)))
			.setCostCenters(
				AppParamDAO.getApplicationParameterStream(ctx
					, p -> p.getDomainProperty().eq(ctx.getDomainId())
					.and(p.getNameProperty().like(AppParam.ACC_COST_CENTER_.toString() + "%")))
				.map( ApplicationParameter::getValue )
				.collect(Collectors.toCollection(LinkedList::new)))
			.setDefaultSalesAccount( getAccount(ctx, AppParam.ACC_DEFAULT_SALES_ACC) )
			.setDefaultPurchaseAccount( getAccount(ctx, AppParam.ACC_DEFAULT_PURCHASE_ACC) )
			.setDefaultChargedVatAccount( getAccount(ctx, AppParam.ACC_DEFAULT_CHARGED_VAT_ACC) )
			.setDefaultPaidVatAccount( getAccount(ctx, AppParam.ACC_DEFAULT_PAID_VAT_ACC) )
			.setDefaultChargedRetAccount( getAccount(ctx, AppParam.ACC_DEFAULT_CHARGED_RET_ACC) )
			.setDefaultPaidRetAccount( getAccount(ctx, AppParam.ACC_DEFAULT_PAID_RET_ACC) )
			.setDefaultCashAccount( getAccount(ctx, AppParam.ACC_DEFAULT_CASH_ACC) )
			.setVatNegativeAdjustAccount( getAccount(ctx, AppParam.ACC_VAT_NEGATIVE_ADJUST_ACC) )
			.setDirectTaxAdjustAccount( getAccount(ctx, AppParam.ACC_DIRECT_TAX_ADJUST_ACC) )
			.setDefaultDUAVatAccount( getAccount(ctx, AppParam.ACC_DEF_DUA_VAT_ACC) )
			.setDefaultDUADutyAccount( getAccount(ctx, AppParam.ACC_DEF_DUA_DUTY_ACC) )
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
			.setDefaultPrepayment(getAccount(ctx, AppParam.ACC_DEFAULT_PREPAYMENT_ACC))
		;
	}

	private static void fillFiscalParameters(AONContext ctx, final AonConfiguration config) {
		ctx.getDslContext()
			.select()
			.from(APP_PARAM)
			.where(APP_PARAM.DOMAIN.equal(ctx.getDomainId()))
			.and(APP_PARAM.NAME.like("FS_%"))
			.and(APP_PARAM.VALUE.isNotNull())
			.fetch()
			.stream()
			.map( new ApplicationParameterFiller() )
			.forEach(param -> fillParam(ctx, config, param));
		
		if (!config.fiscal().isCustomerCheckEnabled() && config.getDomain().getParentId() != null) {
			ctx.getDslContext()
				.select()
				.from(APP_PARAM)
				.where(APP_PARAM.DOMAIN.equal(config.getDomain().getParentId()))
				.and(APP_PARAM.NAME.eq(AppParam.FS_CUSTOMER_CHECK_ENABLED.toString()))
				.and(APP_PARAM.VALUE.isNotNull())
				.fetch()
				.stream()
				.map( new ApplicationParameterFiller() )
				.forEach(param -> fillParam(ctx, config, param));
		}
	}
	
	private static String getMd5(String str){
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	    md.update(str.getBytes());
	    byte byteData[] = md.digest();
	    //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < byteData.length; i++) {
	     	sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
	    }       
        return sb.toString();
	}
}
