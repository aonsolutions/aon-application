package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.IRPFRegime;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AppParamDAO {

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
			(params, value) -> params.setTaxRefundRegistry(Boolean.parseBoolean(value)))
		,FS_TAX_REGIME("FS_TAX_REGIME", 
			(params, value) -> params.setTaxRegime(Integer.parseInt(value)))
		,FS_ADMON_CREDITOR("FS_ADMON_CREDITOR",
			(params, value) -> params.setAdmonCreditor(Integer.parseInt(value)))
		,FS_PERM_ADDRESS_CHANGES("FS_PERM_ADDRESS_CHANGES", 
			(params, value) -> params.setPermAddressChanges(Boolean.parseBoolean(value)))
		,FS_CONCTACT_PERSON("FS_CONCTACT_PERSON", 
			(params, value) -> params.setContactPerson(value))
		,FS_CONCTACT_PHONE("FS_CONCTACT_PHONE", 
			(params, value) -> params.setContactPhone(value))
		,FS_CONCTACT_CELLULAR("FS_CONCTACT_CELLULAR",
			(params, value) -> params.setContactCellular(value))
		,FS_CONCTACT_MAIL("FS_CONCTACT_MAIL", 
			(params, value) -> params.setContactMail(value))
		,FS_MOD303_BY_DIFFERENCE_DISABLED("FS_MOD303_BY_DIFFERENCE_DISABLED", 
			(params, value) -> params.setMod303ByDifferenceDisabled(Boolean.parseBoolean(value)));

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
	
	public static ApplicationParameter fetchOne(AONContext ctx, AppParam param) {
		ctx.checkRead();
		final ApplicationParameter ap = new ApplicationParameter();
		ctx.getDslContext()
			.select(APP_PARAM.ID,APP_PARAM.DOMAIN,APP_PARAM.NAME,APP_PARAM.VALUE)
			.from(APP_PARAM)
			.where(APP_PARAM.DOMAIN.eq(ctx.getDomainId())
				.and(APP_PARAM.NAME.eq(param.getValue())))
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
							if (AonStringUtils.isNotBlank(rec
									.getValue(APP_PARAM.VALUE))) {
								FiscalParamsItem item = FiscalParamsItem
										.getItemByName(rec
												.getValue(APP_PARAM.NAME));
								if (item != null) {
									item.fill(params,
											rec.getValue(APP_PARAM.VALUE));
								}
							}
						});
		// En el caso de un dominio hijo o standalone, se
		// rellenan los datos de document y name con los de company.
		Company company = CompanyDAO.getCompany(ctx, ctx.getDomainId());
		params.setCompany(company.getId());
		Domain dom = SecurityDAO.getDomain(ctx, ctx.getDomainId());
		if (dom.isStandalone() || dom.isChild()) {
			params.setDocument(company.getDocument());
			params.setName(company.getName());
		}
		return params;
	}
	
	public static IRPFRegime getDefaultIRPFRegime(AONContext ctx) {
		ApplicationParameter ap  = AppParamDAO.fetchOne(ctx, AppParam.FS_TAX_REGIME);
		if (ap == null || AonStringUtils.isBlank(ap.getValue())) return null;
		int i = AonNumberUtils.toInteger( ap.getValue() );
		return IRPFRegime.safeValueOf(i);
	}
	public static boolean isPermAddressChanges(AONContext ctx) {
		ApplicationParameter ap  = AppParamDAO.fetchOne(ctx, AppParam.FS_PERM_ADDRESS_CHANGES);
		if (ap == null || AonStringUtils.isBlank(ap.getValue())) return false;
		return (AonNumberUtils.toInteger( ap.getValue() )==1);
	}

}
