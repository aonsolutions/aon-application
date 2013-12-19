package com.esferalia.aon.gwt.fiscal.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.pool.AonConnectionException;
import com.code.aon.pool.AonDataSource;
import com.esferalia.aon.gwt.fiscal.shared.AonSQLException;
import com.esferalia.aon.gwt.fiscal.shared.Company;
import com.esferalia.aon.gwt.fiscal.shared.Domain;
import com.esferalia.aon.gwt.fiscal.shared.FiscalParameters;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.AppParamColumns;

public class SQLParams {
	private static Logger LOGGER = Logger.getLogger(SQLParams.class.getName());

	private static String FS_DEFAULT_YEAR = "FS_DEFAULT_YEAR";
	private static String FS_DEFAULT_ADMINISTRATION = "FS_DEFAULT_ADMINISTRATION";
	private static String FS_ADMINISTRATION_CODE = "FS_ADMINISTRATION_CODE";
	private static String FS_TAX_REFUND_REGISTRY = "FS_TAX_REFUND_REGISTRY";
	private static String FS_TAX_REGIME = "FS_TAX_REGIME";
	private static String FS_ADMON_CREDITOR = "FS_ADMON_CREDITOR";
	private static String FS_PERM_ADDRESS_CHANGES = "FS_PERM_ADDRESS_CHANGES";
	private static String FS_CONCTACT_PERSON = "FS_CONCTACT_PERSON";
	private static String FS_CONCTACT_PHONE = "FS_CONCTACT_PHONE";
	private static String FS_CONCTACT_CELLULAR = "FS_CONCTACT_CELLULAR";
	private static String FS_CONCTACT_MAIL = "FS_CONCTACT_MAIL";
	private static String FS_MOD303_BY_DIFFERENCE_DISABLED = "FS_MOD303_BY_DIFFERENCE_DISABLED";

	private static String[] NAMES = { FS_ADMINISTRATION_CODE,
			FS_ADMON_CREDITOR, FS_CONCTACT_CELLULAR, FS_CONCTACT_MAIL,
			FS_CONCTACT_PERSON, FS_CONCTACT_PHONE, FS_DEFAULT_ADMINISTRATION,
			FS_DEFAULT_YEAR, FS_MOD303_BY_DIFFERENCE_DISABLED,
			FS_PERM_ADDRESS_CHANGES, FS_TAX_REFUND_REGISTRY, FS_TAX_REGIME };
	static {
		// Se previene el orden para el correcto funcionamiento de
		// binarySearch.
		Arrays.sort(NAMES);
	}

	//@formatter:off
	private static final String SELECT_FISCAL_PARAMS = "SELECT "
		+ SQLConstants.APP_PARAM + "." + AppParamColumns.NAME + " " + AppParamColumns.NAME + ","	
		+ SQLConstants.APP_PARAM + "." + AppParamColumns.VALUE	+ " " + AppParamColumns.VALUE
		+ " FROM " + SQLConstants.APP_PARAM 
		+ " WHERE " + SQLConstants.APP_PARAM + "." + AppParamColumns.DOMAIN + " = ?"
		+ " AND " + SQLConstants.APP_PARAM + "." + AppParamColumns.NAME + " LIKE 'FS_%'"
		+ " AND " + SQLConstants.APP_PARAM + "." + AppParamColumns.VALUE + " IS NOT NULL"
		+ " AND NULLIF(TRIM(" + SQLConstants.APP_PARAM + "." + AppParamColumns.VALUE + "),'') IS NOT NULL";
	
	//private static final String SELECT_DOMAIN = "SELECT "
	//@formatter:on

	public static FiscalParameters getFiscalParameters(int domain,
			Connection conn) throws AonSQLException {
		String select = SELECT_FISCAL_PARAMS;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(select, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, domain);
			LOGGER.log(Level.INFO, "APP PARAM SELECT( dom: " + domain + ")");
			rs = stmt.executeQuery();
			FiscalParameters params = new FiscalParameters();

			while (rs.next()) {
				String name = rs.getString(AppParamColumns.NAME);
				int i = Arrays.binarySearch(NAMES, name);
				if (i >= 0) {
					String value = rs.getString(AppParamColumns.VALUE);
					try {
						if (NAMES[i] == FS_DEFAULT_YEAR) {
							params.setDefaultYear(Integer.parseInt(value));
						} else if (NAMES[i] == FS_DEFAULT_ADMINISTRATION) {
							params.setAdministration(Integer.parseInt(value));
						} else if (NAMES[i] == FS_ADMINISTRATION_CODE) {
							params.setAdministrationCode(value);
						} else if (NAMES[i] == FS_TAX_REFUND_REGISTRY) {
							params.setTaxRefundRegistry(Boolean
									.parseBoolean(value));
						} else if (NAMES[i] == FS_TAX_REGIME) {
							params.setTaxRegime(Integer.parseInt(value));
						} else if (NAMES[i] == FS_ADMON_CREDITOR) {
							params.setAdmonCreditor(Integer.parseInt(value));
						} else if (NAMES[i] == FS_PERM_ADDRESS_CHANGES) {
							params.setPermAddressChanges(Boolean
									.parseBoolean(value));
						} else if (NAMES[i] == FS_CONCTACT_PERSON) {
							params.setContactPerson(value);
						} else if (NAMES[i] == FS_CONCTACT_PHONE) {
							params.setContactPhone(value);
						} else if (NAMES[i] == FS_CONCTACT_CELLULAR) {
							params.setContactCellular(value);
						} else if (NAMES[i] == FS_CONCTACT_MAIL) {
							params.setContactMail(value);
						} else if (NAMES[i] == FS_MOD303_BY_DIFFERENCE_DISABLED) {
							params.setMod303ByDifferenceDisabled(Boolean
									.parseBoolean(value));
						} else {
							// Nada. Parametro no soportado.
						}
					} catch (NumberFormatException e) {
						LOGGER.log(Level.WARNING, "INCORRECT APP PARAM VALUE "
								+ " ( dom: " + domain + ", name:" + NAMES[i]
								+ ", value: " + value + ")");
					}
				}
			}
			// En el caso de un dominio hijo o standalone, se
			// rellenan los datos de document y name con los de company.
			Company company = SQLCompany.getCompany(domain, conn);
			params.setCompany(company.getId());
			Domain dom = SQLDomain.getDomain(domain, conn);
			if (dom.isStandalone() || dom.isChild()) {
				params.setDocument(company.getDocument());
				params.setName(company.getName());
			}
			return params;
		} catch (Throwable e) {
			throw new AonSQLException(e);
		} finally {
			SQLUtils.closeQuietly(rs);
			SQLUtils.closeQuietly(stmt);
		}
	}

}
