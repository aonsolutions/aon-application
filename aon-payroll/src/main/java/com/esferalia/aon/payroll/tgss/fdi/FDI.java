package com.esferalia.aon.payroll.tgss.fdi;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractLeave.CONTRACT_LEAVE;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.EnterpriseData.ENTERPRISE_DATA;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.tools.json.JSONObject;

import com.esferalia.aon.jooq.tables.records.ContractLeaveRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.tgss.cra.StringUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class FDI {

	private static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
	private static DateFormat DATE_FORMAT_FULL = new SimpleDateFormat("yyyyMMddhhmm");

	// ********************************************************************************************************************************************
	// GENERATE FDI
	// ********************************************************************************************************************************************

	@SuppressWarnings("unchecked")
	public static String getFDI(Connection connection, Integer contractId, Integer contractLeaveId, String quoteDays, String fileName) throws SQLException {

		// Get dslContext for given connection
		AONContext ctx = new AONContext(connection);
		DSLContext dslContext = ctx.getDslContext();

		// Get info DB
		Record contract = dslContext.select().from(CONTRACT).join(REGISTRY).on(REGISTRY.ID.eq(CONTRACT.PERSON))
				.join(PERSON).on(PERSON.REGISTRY.eq(CONTRACT.PERSON)).join(ENTERPRISE_CCC)
				.on(ENTERPRISE_CCC.ID.eq(CONTRACT.ENTERPRISE_CCC)).join(GEOZONE)
				.on(GEOZONE.ID.eq(ENTERPRISE_CCC.GEOZONE)).where(CONTRACT.ID.eq(contractId)).fetchOne();

		Record enterprise = dslContext.select().from(ENTERPRISE).join(REGISTRY).on(REGISTRY.ID.eq(ENTERPRISE.REGISTRY))
				.where(ENTERPRISE.DOMAIN.eq(contract.get(CONTRACT.DOMAIN))).fetchOne();
		
		ContractLeaveRecord contractLeave = dslContext.selectFrom(CONTRACT_LEAVE).where(CONTRACT_LEAVE.ID.eq(contractLeaveId)).fetchOne();

		String cccRegimeCode = getCCCRegimeCode(contract.get(ENTERPRISE_CCC.TYPE));
		String ccc = contract.get(ENTERPRISE_CCC.CCC);
		
		Double dailyCC = contractLeave.getDailyCgcBase();
		Double baseCc = (Math.round((dailyCC * Integer.parseInt(quoteDays)) *100.0 ) / 100.0);
        
		// GET AuthKey from DB
		String authKey = getAuthKeyFromDomain(dslContext, contract.get(CONTRACT.DOMAIN));

		// Create JSONObject FIE JSON
		JSONObject fieJson = new JSONObject();
		
		// ETI
		fieJson.put("ETI", "ETIFDI61     " + StringUtils.leftPad(authKey, 8, '0') + "        "
				+ DATE_FORMAT_FULL.format(new java.util.Date()) + fileName + "FDIN 00000000000000  ");


		// EMP
		fieJson.put("EMP",
				"EMP" + cccRegimeCode + ccc + checkIdentificationNumber(enterprise.get(REGISTRY.DOCUMENT)) + "   "
						+ StringUtils.leftPad(enterprise.get(REGISTRY.DOCUMENT), 14, '0') + "  " + cccRegimeCode + ccc
						+ "                 ");

		// TRA
		fieJson.put("TRA",
				"TRA" + contract.get(PERSON.SOCIAL_SECURITY_NUM)
						+ checkIdentificationNumber(contract.get(REGISTRY.DOCUMENT)) + "   "
						+ StringUtils.leftPad(contract.get(REGISTRY.DOCUMENT), 14, '0')
						+ "                            724      ");

		// ODT
		fieJson.put("ODT",
				"ODT" + StringUtils.rightPad(contract.get(CONTRACT.CATEGORY_DESCRIPTION).toUpperCase(), 50, ' ')
						+ "                 ");

		// ODT
		fieJson.put("FUN",
				"FUN" + StringUtils.rightPad("Propias de " + contract.get(CONTRACT.CATEGORY_DESCRIPTION), 67, ' '));

		// DIT
		fieJson.put("DIT",
				"DITDE 00" + checkLowReasonIdent(contractLeave.getType())
						+ DATE_FORMAT.format(contractLeave.getStartDate())
						+ "00000000000000000000000000           000  0000000N   ");

		String parteEntera;
		String parteDecimal;
		try {
			parteEntera = baseCc.toString().split("\\.")[0];
			parteDecimal = baseCc.toString().split("\\.")[1];
		} catch (Exception e) {
			parteEntera = baseCc.toString();
			parteDecimal = "00";
		}

		// DEC
		fieJson.put("DEC", "DEC" + StringUtils.leftPad(parteEntera, 6, '0') +  StringUtils.leftPad(parteDecimal, 2, '0') + "000000000000000000000000 " + quoteDays
				+ "000000000                       ");

		// ETF
		fieJson.put("ETF", "ETFFDI61     " + StringUtils.leftPad(authKey, 8, '0') + "        " + DATE_FORMAT_FULL.format(new java.util.Date()) + fileName + "FDIN 0000100000008   ");

		System.out.println(fieJson);
		
		String fieFile = "";
		fieFile += fieJson.get("ETI") + "\r\n";
		fieFile += fieJson.get("EMP") + "\r\n";
		fieFile += fieJson.get("TRA") + "\r\n";
		fieFile += fieJson.get("ODT") + "\r\n";
		fieFile += fieJson.get("FUN") + "\r\n";
		fieFile += fieJson.get("DIT") + "\r\n";
		fieFile += fieJson.get("DEC") + "\r\n";
		fieFile += fieJson.get("ETF") + "\r\n";

		return fieFile;
	}

	private static String getCCCRegimeCode(Byte cccRegime) {
		switch (cccRegime) {
		case 0:
			return "0111";
		case 1:
			return "0111";
		case 2:
			return "0111";
		case 3:
			return "0111";
		case 4:
			return "0111";
		case 5:
			return "0111";
		case 6:
			return "0138";
		case 7:
			return "0163";
		default:
			return "0111";
		}
	}

	private static String getAuthKeyFromDomain(DSLContext dslContext, Integer domainId) {

		// Prepare aunthKey
		String authKey = "";

		Record domainRecord = dslContext.select().from(DOMAIN).where(DOMAIN.ID.eq(domainId)).fetchOne();

		Integer parentDomainId = domainRecord.get(DOMAIN.PARENT);

		Record enterpriseDataRecord = dslContext.select().from(ENTERPRISE_DATA)
				.where(ENTERPRISE_DATA.NAME.eq("PAY_authorization_key_PAY")).and(ENTERPRISE_DATA.DOMAIN.eq(domainId))
				.fetchOne();

		if (null != enterpriseDataRecord
				&& AonStringUtils.isNotBlank(enterpriseDataRecord.get(ENTERPRISE_DATA.EXPRESSION)))
			return enterpriseDataRecord.get(ENTERPRISE_DATA.EXPRESSION);
		else {
			Record appParamRecord = dslContext.select().from(APP_PARAM)
					.where(APP_PARAM.NAME.eq("PAY_authorization_key_PAY")).and(APP_PARAM.DOMAIN.eq(domainId))
					.fetchOne();

			if (null == appParamRecord || null == appParamRecord.get(APP_PARAM.VALUE)) {

				// Find Authkey from parent
				appParamRecord = dslContext.select().from(APP_PARAM)
						.where(APP_PARAM.NAME.eq("PAY_authorization_key_PAY")).and(APP_PARAM.DOMAIN.eq(parentDomainId))
						.fetchOne();

				if (null == appParamRecord || null == appParamRecord.get(APP_PARAM.VALUE))
					authKey = "00000";
				else
					authKey = appParamRecord.get(APP_PARAM.VALUE);

			} else {
				authKey = appParamRecord.get(APP_PARAM.VALUE);
			}

			return authKey;
		}

	}

	public static String checkIdentificationNumber(String input) {
		// Regular expression patterns for DNI, NIF, NIE, and CIF
		String dniPattern = "^[0-9]{8}[A-Z]$";
		String nifPattern = "^[A-Z]{1}[0-9]{7}$";
		String niePattern = "^[X-Z]{1}[0-9]{7}[A-Z]$";
		String cifPattern = "^[A-Z]{1}[0-9]{8}$";

		// Check if the input matches any of the patterns

		if (input.matches(dniPattern) || input.matches(nifPattern)) {
			return "1";
		} else if (input.matches(niePattern)) {
			return "6";
		} else if (input.matches(cifPattern)) {
			return "9";
		} else {
			return "0"; // Return 0 if the input is invalid
		}
	}
	
	private static String checkLowReasonIdent(Byte type) {
		switch (type) {
		case 0:
			return "1"; // Enfermedad comun
		case 6:
			return "2"; // Accidente no laboral
		case 1:
			return "3"; // Accidente de trabajo
		case 7:
			return "4"; // Enfermedad profesional
		default:
			return "1";
		}
	}
	
}
