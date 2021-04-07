package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.Filter.ApplicationParameterFilter;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.IRPFRegime;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.ApplicationParameterFiller;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.ApplicationParameterPropertiesDAO;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AppParamDAO {
	
	private static final String DATE_PATTERN = "dd/MM/yyyy";
	private static final String[] DATE_PATTERNS = new String[]{DATE_PATTERN}; 
	private static final ApplicationParameterPropertiesDAO APPLICATION_PARAMETER_PROPERTIES = new ApplicationParameterPropertiesDAO();
	private static Logger LOGGER = Logger
			.getLogger(AppParamDAO.class.getName());

	@FunctionalInterface
	public static interface IFiscalParamsFiller {
		void fill(FiscalParameters params, String value);
	}

	private enum FiscalParamsItem implements Serializable {
		 FS_DEFAULT_YEAR("FS_DEFAULT_YEAR", 
			(params, value) -> params.setDefaultYear(Integer.parseInt(value)))
		,FS_DEFAULT_ADMINISTRATION("FS_DEFAULT_ADMINISTRATION", 
			(params, value) -> params.setAdministration(Integer.parseInt(value)))
		,FS_ADMINISTRATION_CODE("FS_ADMINISTRATION_CODE", 
			(params, value) -> params.setAdministrationCode(value))
		,FS_TAX_REFUND_REGISTRY("FS_TAX_REFUND_REGISTRY",
			(params, value) -> params.setTaxRefundRegistry( Boolean.parseBoolean(value) || AonStringUtils.equals(value, AonStringUtils.ONE)))
		,FS_TAX_REGIME("FS_TAX_REGIME", 
			(params, value) -> params.setTaxRegime(Integer.parseInt(value)))
		,FS_ADMON_CREDITOR("FS_ADMON_CREDITOR",
			(params, value) -> params.setAdmonCreditor(Integer.parseInt(value)))
		,FS_ADMON_VAT_CREDITOR("FS_ADMON_VAT_CREDITOR",
			(params, value) -> params.setAdmonVatCreditor(Integer.parseInt(value)))
		,FS_ADMON_RETENTION_CREDITOR("FS_ADMON_RETENTION_CREDITOR",
			(params, value) -> params.setAdmonRetentionCreditor(Integer.parseInt(value)))
		,FS_PERM_ADDRESS_CHANGES("FS_PERM_ADDRESS_CHANGES", 
			(params, value) -> params.setPermAddressChanges(Boolean.parseBoolean(value) || AonStringUtils.equals(value, AonStringUtils.ONE)))
		,FS_CONCTACT_PERSON("FS_CONCTACT_PERSON", 
			(params, value) -> params.setContactPerson(value))
		,FS_CONCTACT_PHONE("FS_CONCTACT_PHONE", 
			(params, value) -> params.setContactPhone(value))
		,FS_CONCTACT_CELLULAR("FS_CONCTACT_CELLULAR",
			(params, value) -> params.setContactCellular(value))
		,FS_CONCTACT_MAIL("FS_CONCTACT_MAIL", 
			(params, value) -> params.setContactMail(value))
		,FS_MOD303_BY_DIFFERENCE_DISABLED("FS_MOD303_BY_DIFFERENCE_DISABLED", 
			(params, value) -> params.setMod303ByDifferenceDisabled(Boolean.parseBoolean(value) || AonStringUtils.equals(value, AonStringUtils.ONE)))
		,FS_CUSTOMER_CHECK_ENABLED("FS_CUSTOMER_CHECK_ENABLED", 
				(params, value) -> params.setCustomerCheckEnabled(Boolean.parseBoolean(value) || AonStringUtils.equals(value, AonStringUtils.ONE)));
		;

		private String name;
		private IFiscalParamsFiller filler;

		private FiscalParamsItem(String name, IFiscalParamsFiller filler) {
			this.name = name;
			this.filler = filler;
		}

		public static FiscalParamsItem getItemByName(String name) {
			for (FiscalParamsItem item : FiscalParamsItem.values()) {
				if (AonStringUtils.equals(name, item.name)) {
					return item;
				}
			}
			return null;
		}

		public void fill(FiscalParameters params, String value) {
			try {
				filler.fill(params, value);
			} catch (NumberFormatException e) {
				LOGGER.log(Level.WARNING, "INCORRECT APP PARAM VALUE "
						+ " ( name:" + name + ", value: " + value + ")");
			}
		}
	}
	public static String fetchValue(AONContext ctx, AppParam param) {
		ApplicationParameter ap = fetchOne(ctx, param.getValue());
		return ap == null ? null : ap.getValue(); 
	}
	public static double fetchDoubleValue(AONContext ctx, AppParam param) {
		ApplicationParameter ap = fetchOne(ctx, param.getValue());
		return ap == null ? 0.0 : AonNumberUtils.todouble( ap.getValue()); 
	}
	public static int fetchIntValue(AONContext ctx, AppParam param) {
		ApplicationParameter ap = fetchOne(ctx, param.getValue());
		return ap == null ? 0 : AonNumberUtils.toint( ap.getValue()); 
	}
	public static Date fetchDateValue(AONContext ctx, AppParam param) {
		ApplicationParameter ap = fetchOne(ctx, param.getValue());
		if ( ap != null && AonStringUtils.isNotBlank(ap.getValue())) {
			try {
				return AonDateUtils.parseDateStrictly(ap.getValue(), DATE_PATTERNS);
			} catch ( ParseException e ) {
				ctx.log().error( e.getMessage() );
			}
		}
		return null;
	}
	
	public static Stream<ApplicationParameter> getApplicationParameterStream(AONContext ctx, ApplicationParameterFilter filter) {
		return APPLICATION_PARAMETER_PROPERTIES.build(ctx.getDslContext()
				.select()
				.from(APP_PARAM), filter)
				.fetch().stream().map(r -> new ApplicationParameter()
						.setDomain(r.getValue(APP_PARAM.DOMAIN))
						.setId(r.getValue(APP_PARAM.ID))
						.setName(r.getValue(APP_PARAM.NAME))
						.setValue(r.getValue(APP_PARAM.VALUE)));
	}
	
	public static void deleteApplicationParameter(AONContext ctx, ApplicationParameterFilter filter) {
		ctx.getDslContext()
			.delete(APP_PARAM)
			.where(APPLICATION_PARAMETER_PROPERTIES.getConditions(filter))
			.execute();
	}
	
	
	public static ApplicationParameter fetchOne(AONContext ctx, AppParam param) {
		return fetchOne(ctx, param.getValue());
	}
	
	public static ApplicationParameter fetchOne(AONContext ctx, String param) {
		ctx.checkRead();
		final ApplicationParameter ap = new ApplicationParameter();
		ctx.getDslContext()
			.select(APP_PARAM.ID,APP_PARAM.DOMAIN,APP_PARAM.NAME,APP_PARAM.VALUE)
			.from(APP_PARAM)
			.where(APP_PARAM.DOMAIN.eq(ctx.getDomainId())
				.and(APP_PARAM.NAME.eq(param)))
			.fetch()
			.stream()
			.findFirst()
			.ifPresent( record  -> ap
					.setId(record.getValue(APP_PARAM.ID))
					.setDomain(record.getValue(APP_PARAM.DOMAIN))
					.setName(record.getValue(APP_PARAM.NAME))
					.setValue(record.getValue(APP_PARAM.VALUE)) );
			;
		return ap.getId() != null ? ap : null;
	}
	

	public static FiscalParameters getFiscalParameters(AONContext ctx) {
		FiscalParameters params = new FiscalParameters();
		ctx.getDslContext()
				.select(APP_PARAM.NAME, APP_PARAM.VALUE)
				.from(APP_PARAM)
				.where(APP_PARAM.DOMAIN.equal(ctx.getDomainId()))
				.and(APP_PARAM.NAME.like("FS_%"))
				.and(APP_PARAM.VALUE.isNotNull())
				.fetch()
				.forEach(
						rec -> {
							if (AonStringUtils.isNotBlank(rec.getValue(APP_PARAM.VALUE))) {
								FiscalParamsItem item = FiscalParamsItem.getItemByName(rec.getValue(APP_PARAM.NAME));
								if (item != null) {
									item.fill(params,rec.getValue(APP_PARAM.VALUE));
								}
							}
						});
		// En el caso de un dominio hijo o standalone, se
		// rellenan los datos de document y name con los de company.
		Company company = CompanyDAO.getCompany(ctx, ctx.getDomainId());
		params.setCompany(company.getId());
		Domain dom = DomainDAO.getDomain(ctx, ctx.getDomainId());
		if (dom.isStandalone() || dom.isChild()) {
			params.setDocument(company.getDocument());
			params.setName(company.getName());
		}
		return params;
	}
	
	public static IRPFRegime getDefaultIRPFRegime(AONContext ctx) {
		ApplicationParameter ap  = AppParamDAO.fetchOne(ctx, AppParam.FS_TAX_REGIME.getValue());
		if (ap == null || AonStringUtils.isBlank(ap.getValue())) return null;
		int i = AonNumberUtils.toInteger( ap.getValue() );
		return IRPFRegime.safeValueOf(i);
	}
	public static boolean isPermAddressChanges(AONContext ctx) {
		ApplicationParameter ap  = AppParamDAO.fetchOne(ctx, AppParam.FS_PERM_ADDRESS_CHANGES.getValue());
		if (ap == null || AonStringUtils.isBlank(ap.getValue())) return false;
		return (AonNumberUtils.toInteger( ap.getValue() )==1);
	}
	
	public static ApplicationParameter insertApplicationParameter(AONContext ctx, String param, String value){
		if(ctx.getDslContext().select().from(APP_PARAM).where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
			.and(APP_PARAM.NAME.eq(param)).fetch().isEmpty())
			ctx.getDslContext()
				.insertInto(APP_PARAM, APP_PARAM.DOMAIN, APP_PARAM.NAME, APP_PARAM.VALUE)
				.values(ctx.getDomainId(), param, value).execute();
		else ctx.getDslContext()
				.update(APP_PARAM)
				.set(APP_PARAM.VALUE, value)
				.where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
				.and(APP_PARAM.NAME.eq(param)).execute();
		return fetchOne(ctx, param);
	}
	
	public static ApplicationParameter insertApplicationParameter(AONContext ctx, ApplicationParameter applicationParameter) {
		return ctx.getDslContext()
			.insertInto(APP_PARAM, APP_PARAM.DOMAIN, APP_PARAM.NAME, APP_PARAM.VALUE)
			.values(ctx.getDomainId(), applicationParameter.getName(), applicationParameter.getValue())
			.returning().fetch().stream().map(new ApplicationParameterFiller()).findFirst().orElse(new ApplicationParameter());
	}
	
	public static ApplicationParameter updateApplicationParameter(AONContext ctx, ApplicationParameter applicationParameter, ApplicationParameterFilter filter) {
		return ctx.getDslContext()
			.update(APP_PARAM)
			.set(APP_PARAM.DOMAIN, applicationParameter.getDomain())
			.set(APP_PARAM.NAME, applicationParameter.getName())
			.set(APP_PARAM.VALUE, applicationParameter.getValue())
			.where(APPLICATION_PARAMETER_PROPERTIES.getConditions(filter))
			.returning().fetch().stream().map(new ApplicationParameterFiller()).findFirst().orElse(new ApplicationParameter());
	}
	
}
