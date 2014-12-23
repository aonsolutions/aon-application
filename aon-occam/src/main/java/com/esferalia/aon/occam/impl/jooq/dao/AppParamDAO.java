package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jooq.Condition;

import com.esferalia.aon.jooq.tables.records.AppParamRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AppParamDAO {

	private static Logger LOGGER = Logger
			.getLogger(AppParamDAO.class.getName());

	@FunctionalInterface
	public static interface IFiscalParamsFiller {
		void fill(FiscalParameters params, String value);
	}

	private enum FiscalParamsItem implements Serializable {
		FS_DEFAULT_YEAR("FS_DEFAULT_YEAR", (params, value) -> params
				.setDefaultYear(Integer.parseInt(value))), FS_DEFAULT_ADMINISTRATION(
				"FS_DEFAULT_ADMINISTRATION", (params, value) -> params
						.setAdministration(Integer.parseInt(value))), FS_ADMINISTRATION_CODE(
				"FS_ADMINISTRATION_CODE", (params, value) -> params
						.setAdministrationCode(value)), FS_TAX_REFUND_REGISTRY(
				"FS_TAX_REFUND_REGISTRY", (params, value) -> params
						.setTaxRefundRegistry(Boolean.parseBoolean(value))), FS_TAX_REGIME(
				"FS_TAX_REGIME", (params, value) -> params.setTaxRegime(Integer
						.parseInt(value))), FS_ADMON_CREDITOR(
				"FS_ADMON_CREDITOR", (params, value) -> params
						.setAdmonCreditor(Integer.parseInt(value))), FS_PERM_ADDRESS_CHANGES(
				"FS_PERM_ADDRESS_CHANGES", (params, value) -> params
						.setPermAddressChanges(Boolean.parseBoolean(value))), FS_CONCTACT_PERSON(
				"FS_CONCTACT_PERSON", (params, value) -> params
						.setContactPerson(value)), FS_CONCTACT_PHONE(
				"FS_CONCTACT_PHONE", (params, value) -> params
						.setContactPhone(value)), FS_CONCTACT_CELLULAR(
				"FS_CONCTACT_CELLULAR", (params, value) -> params
						.setContactCellular(value)), FS_CONCTACT_MAIL(
				"FS_CONCTACT_MAIL", (params, value) -> params
						.setContactMail(value)), FS_MOD303_BY_DIFFERENCE_DISABLED(
				"FS_MOD303_BY_DIFFERENCE_DISABLED", (params, value) -> params
						.setMod303ByDifferenceDisabled(Boolean
								.parseBoolean(value)));

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
		Condition condition = APP_PARAM.DOMAIN.equal(ctx.getDomainId()).and(
				APP_PARAM.NAME.equal(param.getValue()));
		return populateRecord(ctx.getDslContext()
				.fetchOne(APP_PARAM, condition));
	}

	private static ApplicationParameter populateRecord(AppParamRecord record) {
		if (record == null)
			return null;

		ApplicationParameter app = new ApplicationParameter();
		app.setId(record.getId());
		app.setDomain(record.getDomain());
		app.setName(record.getName());
		app.setValue(record.getValue());
		return app;
	}

	public static FiscalParameters getFiscalParameters(AONContext ctx,
			int domain) {
		FiscalParameters params = new FiscalParameters();
		ctx.getDslContext()
				.select(APP_PARAM.NAME, APP_PARAM.VALUE)
				.from(APP_PARAM)
				.where(APP_PARAM.DOMAIN.equal(domain))
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
		Company company = CompanyDAO.getCompany(ctx, domain);
		params.setCompany(company.getId());
		Domain dom = DomainDAO.getDomain(ctx, domain);
		if (dom.isStandalone() || dom.isChild()) {
			params.setDocument(company.getDocument());
			params.setName(company.getName());
		}
		return params;
	}

}
