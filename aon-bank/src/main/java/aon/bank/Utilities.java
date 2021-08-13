package aon.bank;

import static com.esferalia.aon.jooq.tables.BankStatement.BANK_STATEMENT;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.jooq.Record2;
import org.jooq.impl.DSL;
import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;

import aon.bank.exceptions.CheckItException;

public class Utilities {

	private static final DateFormat DF = new SimpleDateFormat("yyyy-MM-dd", new Locale("es", "ES"));
	private static final DateFormat DF_TIME = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("es", "ES"));

	public static String getAccountIdByIBAN(String claveApi, Integer empresaId, String iban) throws CheckItException {
		JSONArray accountsJson = CheckItAPI.getAccounts(claveApi, empresaId, null, iban);
		JSONObject accountJson = accountsJson.optJSONObject(0);
		if (accountJson == null)
			throw new CheckItException("The requested account could not be found");

		String accountId = accountJson.optString("id_cuentabancaria");
		if (accountId == null || accountId.isEmpty())
			throw new CheckItException("The requested account could not be found");
		return accountId;
	}

	public static String formatDateForTransactions(Date date) {
		try {
			return DF.format(date);
		} catch (Exception e) {
			return null;
		}
	}

	public static String formatDateForJSON(Date date) {
		try {
			return DF_TIME.format(date);
		} catch (Exception e) {
			return null;
		}
	}

	public static Date parseTZDate(String dateStr) throws CheckItException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", new Locale("es", "ES"));
		format.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			return format.parse(dateStr);
		} catch (ParseException e) {
			throw new CheckItException("Date could not be parsed: " + dateStr);
		}
	}
	
	public static RegistryBank getRbankByIban(AONContext aonContext, String iban) {
		RegistryBank rbank = AON.getRBank(aonContext.getDomainName()
				, aonContext.getDomainId()
				, aonContext.getUser()
				, f -> f.getDomainProperty().eq(aonContext.getDomainId()).and(f.getBankAccountProperty().eq(iban))
		);
		return rbank;	
	}
	
	public static int getNextLotNumber(AONContext aonContext, RegistryBank rbank) {
		Integer lot = (Integer) aonContext.getDslContext()
		.select(DSL.max(BANK_STATEMENT.LOT_NUMBER).as("lot"))
		.from(BANK_STATEMENT)
		.where(BANK_STATEMENT.RBANK.eq(rbank.getId()))
		.and(BANK_STATEMENT.DOMAIN.eq(aonContext.getDomainId()))
		.fetchSingle().getValue("lot");
		return lot != null ? lot + 1 : 1;
	}
	
	public static Date getLastOperationDate(AONContext aonContext, RegistryBank rbank) {
		java.sql.Date date = (java.sql.Date) aonContext.getDslContext()
		.select(DSL.max(BANK_STATEMENT.OPERATION_DATE).as("date"))
		.from(BANK_STATEMENT)
		.where(BANK_STATEMENT.DOMAIN.eq(aonContext.getDomainId()))
		.and(BANK_STATEMENT.RBANK.eq(rbank.getId()))
		.fetchSingle().get("date");
		if (date == null)
			 return null;
		else
			return new Date(date.getTime());
	}
	
	public static java.sql.Date toSqlDate (Date date) {
		if (date == null)
			return null;
		else
			return new java.sql.Date(date.getTime());
					
	}
	
	public static String leadingZeros(Integer id, int fieldSize) {
		if (id != null) {
			String str = String.valueOf(id);
			while (str.length() < fieldSize) {
				str = "0" + str;
			}
			return str;
		} else
			return null;
	}
	
	public static Object[] getMaxMovementIdAndDate(AONContext aonContext, RegistryBank rbank) {
		Record2<String, java.sql.Date> result = aonContext.getDslContext()
		.select(DSL.max(BANK_STATEMENT.REFERENCE2).as("maximum"), BANK_STATEMENT.OPERATION_DATE)
		.from(BANK_STATEMENT)
		.where(BANK_STATEMENT.REFERENCE1.eq(CheckItJooq.CHECKIT_R1))
		.fetchSingle();
		
		String id = (String) result.get("maximum");
		java.sql.Date date = result.get(BANK_STATEMENT.OPERATION_DATE);
		
		
		
		if (id == null || id.isEmpty()) {
			return null;
		} else
			return new Object[] {id, date};
	}
	
	public static Date cleanDate(int day, int month, int year) {
		try {
			Calendar calendar = Calendar.getInstance(new Locale("es", "ES"));
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.HOUR, 0);
			calendar.set(Calendar.DAY_OF_MONTH, day);
			calendar.set(Calendar.MONTH, month);
			calendar.set(Calendar.YEAR, year);
			return calendar.getTime();
		} catch (Exception e) {
			return null;
		}
	}

//	public static Date getDateJson(JSONObject json, String field) throws Exception {
//		//Fri Jan 01 12:00:00 CET 2021
//		String dateStr = json.optString(field);
//		JsonUtils.getDate(json, key)
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		try {
//			Date date = sdf.parse(dateStr);
//			return date;
//		} catch (ParseException e) {
//			throw new Exception("Date could not be parsed");
//		}
//	}
}
