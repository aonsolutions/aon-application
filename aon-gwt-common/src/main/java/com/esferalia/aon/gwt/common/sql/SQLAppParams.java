package com.esferalia.aon.gwt.common.sql;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jooq.DSLContext;
import org.jooq.Record2;

import com.esferalia.aon.gwt.common.shared.AonSQLException;
import com.esferalia.aon.gwt.common.shared.Company;
import com.esferalia.aon.gwt.common.shared.Domain;
import com.esferalia.aon.gwt.common.shared.FiscalParameters;

public class SQLAppParams {
	private static Logger LOGGER = Logger.getLogger(SQLAppParams.class.getName());

	public static String FS_DEFAULT_YEAR = "FS_DEFAULT_YEAR";
	public static String FS_DEFAULT_ADMINISTRATION = "FS_DEFAULT_ADMINISTRATION";
	public static String FS_ADMINISTRATION_CODE = "FS_ADMINISTRATION_CODE";
	public static String FS_TAX_REFUND_REGISTRY = "FS_TAX_REFUND_REGISTRY";
	public static String FS_TAX_REGIME = "FS_TAX_REGIME";
	public static String FS_ADMON_CREDITOR = "FS_ADMON_CREDITOR";
	public static String FS_PERM_ADDRESS_CHANGES = "FS_PERM_ADDRESS_CHANGES";
	public static String FS_CONCTACT_PERSON = "FS_CONCTACT_PERSON";
	public static String FS_CONCTACT_PHONE = "FS_CONCTACT_PHONE";
	public static String FS_CONCTACT_CELLULAR = "FS_CONCTACT_CELLULAR";
	public static String FS_CONCTACT_MAIL = "FS_CONCTACT_MAIL";
	public static String FS_MOD303_BY_DIFFERENCE_DISABLED = "FS_MOD303_BY_DIFFERENCE_DISABLED";

	private static String[] FS_NAMES = { FS_ADMINISTRATION_CODE,
			FS_ADMON_CREDITOR, FS_CONCTACT_CELLULAR, FS_CONCTACT_MAIL,
			FS_CONCTACT_PERSON, FS_CONCTACT_PHONE, FS_DEFAULT_ADMINISTRATION,
			FS_DEFAULT_YEAR, FS_MOD303_BY_DIFFERENCE_DISABLED,
			FS_PERM_ADDRESS_CHANGES, FS_TAX_REFUND_REGISTRY, FS_TAX_REGIME };
	static {
		// Se previene el orden para el correcto funcionamiento de
		// binarySearch.
		Arrays.sort(FS_NAMES);
	}

	public static FiscalParameters getFiscalParameters(DSLContext dsl,int domain) throws AonSQLException {
		LOGGER.log(Level.INFO, "APP PARAM SELECT( dom: " + domain + ")");
		FiscalParameters params = new FiscalParameters();

		List<Record2<String,String>> record = 
				dsl.select(APP_PARAM.NAME,APP_PARAM.VALUE)
					.from(APP_PARAM)
					.where(APP_PARAM.DOMAIN.equal(domain))
					.and(APP_PARAM.NAME.like("FS_%"))
					.and(APP_PARAM.VALUE.isNotNull())
					.fetch();
		String name = null;
		String value = null;
		for (Record2<String,String> rec : record) {
			name = rec.getValue(APP_PARAM.NAME);
			value = rec.getValue(APP_PARAM.VALUE);
			if (value != null && value.trim() != "") {
				int i = Arrays.binarySearch(FS_NAMES, name);
				if (i >= 0) {
					try {
						if (FS_NAMES[i] == FS_DEFAULT_YEAR) {
							params.setDefaultYear(Integer.parseInt(value));
						} else if (FS_NAMES[i] == FS_DEFAULT_ADMINISTRATION) {
							params.setAdministration(Integer.parseInt(value));
						} else if (FS_NAMES[i] == FS_ADMINISTRATION_CODE) {
							params.setAdministrationCode(value);
						} else if (FS_NAMES[i] == FS_TAX_REFUND_REGISTRY) {
							params.setTaxRefundRegistry(Boolean
									.parseBoolean(value));
						} else if (FS_NAMES[i] == FS_TAX_REGIME) {
							params.setTaxRegime(Integer.parseInt(value));
						} else if (FS_NAMES[i] == FS_ADMON_CREDITOR) {
							params.setAdmonCreditor(Integer.parseInt(value));
						} else if (FS_NAMES[i] == FS_PERM_ADDRESS_CHANGES) {
							params.setPermAddressChanges(Boolean
									.parseBoolean(value));
						} else if (FS_NAMES[i] == FS_CONCTACT_PERSON) {
							params.setContactPerson(value);
						} else if (FS_NAMES[i] == FS_CONCTACT_PHONE) {
							params.setContactPhone(value);
						} else if (FS_NAMES[i] == FS_CONCTACT_CELLULAR) {
							params.setContactCellular(value);
						} else if (FS_NAMES[i] == FS_CONCTACT_MAIL) {
							params.setContactMail(value);
						} else if (FS_NAMES[i] == FS_MOD303_BY_DIFFERENCE_DISABLED) {
							params.setMod303ByDifferenceDisabled(Boolean
									.parseBoolean(value));
						} else {
							// Nada. Parámetro no soportado.
						}
					} catch (NumberFormatException e) {
						LOGGER.log(Level.WARNING, "INCORRECT APP PARAM VALUE "
								+ " ( dom: " + domain + ", name:" + FS_NAMES[i]
								+ ", value: " + value + ")");
					}
				}
			}
		}
		// En el caso de un dominio hijo o standalone, se
		// rellenan los datos de document y name con los de company.
		Company company = SQLCompany.getCompany(dsl,domain);
		params.setCompany(company.getId());
		Domain dom = SQLDomain.getDomain(dsl,domain);
		if (dom.isStandalone() || dom.isChild()) {
			params.setDocument(company.getDocument());
			params.setName(company.getName());
		}
		return params;
	}

}
