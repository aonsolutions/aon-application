package net.aonsolutions.aon.bank.checkit;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.finance.checkit.CheckItBank;
import com.esferalia.aon.watson.server.AonDateUtils;

@Ignore("ignore until CheckIt is aviable")
class CheckItTestCase {
	
	private static final Integer AON_ID = 11413;
	private static final Integer RAYSON_ID = 11414;
	
	@Test
	@SkipWhenCheckItUnavailable
	void testGetBanks() {
		JSONArray banks = CheckItAPI.getBanks();
		assertTrue( banks instanceof JSONArray);
		assertTrue( banks.length() > 0, "No banks!");
	}
	
	@Test
	@SkipWhenCheckItUnavailable
	void testGetBankList() {
		List<CheckItBank> banks = CheckItAPI.getBankList();
		assertNotNull( banks );
		assertTrue( banks.size() > 0, "No banks!");
	}

	@Test
	@SkipWhenCheckItUnavailable
	void testGetLogins() {
		JSONArray logins = CheckItAPI.getLogins(44);
		assertTrue( logins instanceof JSONArray);
		assertTrue( logins.length() > 0, "No logins!");
	}
	
	@Test
	@SkipWhenCheckItUnavailable
	void testGetLoginFields() {
		JSONArray loginFields = CheckItAPI.getLoginFields(20);
		assertTrue( loginFields instanceof JSONArray);
		assertTrue( loginFields.length() > 0, "No login Fields!");
	}
	
	@Test
	@SkipWhenCheckItUnavailable
	void testGetCredentials() {
		JSONObject credentials = CheckItAPI.getCredentials(AON_ID, 10);
		assertTrue( credentials instanceof JSONObject);
	}
	
	@Test
	@SkipWhenCheckItUnavailable
	void testAddCredentials() {
		JSONObject credentials = CheckItAPI.addCredentials(AON_ID, 7, "54321", "123API", "test2016");
		assertTrue( credentials instanceof JSONObject);
	}
	
	@Test
	@SkipWhenCheckItUnavailable
	void testAddExtraField() {
		JSONObject result = CheckItAPI.addExtraField(RAYSON_ID, 39, "17");
		assertTrue( result instanceof JSONObject);
	}
	
	@Test
	@SkipWhenCheckItUnavailable
	void testGetAccounts() {
		JSONArray accounts = CheckItAPI.getAccounts(AON_ID, 1, null);
		assertTrue( accounts instanceof JSONArray);
		assertTrue( accounts.length() > 0, "No accounts!");
	}
	
	@Test
	@SkipWhenCheckItUnavailable
	void testGetTranslogiaAccounts() {
		JSONArray accounts = CheckItAPI.getAccounts(11423, 1, null);
		assertTrue( accounts instanceof JSONArray);
		assertTrue( accounts.length() > 0, "No accounts!");
	}
	
	@Test
	@SkipWhenCheckItUnavailable
	void testGetEnterprise() {
		JSONArray enterprises = CheckItAPI.getEnterprise("B01487271");
		assertTrue( enterprises instanceof JSONArray);
		assertTrue( enterprises.length() > 0, "No enterprises!");
	}
	
	@Test
	@SkipWhenCheckItUnavailable
	void testGetLogs() {
		JSONArray logs = CheckItAPI.getLogs(11413, 33638);
		assertTrue( logs instanceof JSONArray);
	}
	
	
	@Disabled("Ignored because it inserts a new account each time is called and they cannot be deleted for now")
	@Test
	@SkipWhenCheckItUnavailable
	void testAddAccount() {
		JSONObject result = CheckItAPI.addAccount(RAYSON_ID, 79, 1, "ES0614650100936000306237", 1);
		assertTrue( result instanceof JSONObject);
	}

//	@Test
//	@SkipWhenCheckItUnavailable
//	void testAddAccountWrongLogin() {
//		JSONObject result = CheckItAPI.addAccount(RAYSON_ID, 57, 39, "ES9121000418450200051332", 1);
//		assertTrue( result instanceof JSONObject);
//		
//	}
	
	@Disabled("Ignored because it inserts a new account each time is called and they cannot be deleted for now")
	@Test
	@SkipWhenCheckItUnavailable
	void testAddAccountApi() {
		JSONObject result = CheckItAPI.addAccountApi(RAYSON_ID, 79, "ES0614650100936000306237", -326360.34, -326360.34, new Date(), "ES0614650100936000306237", 1);
		assertTrue( result instanceof JSONObject);
	}
	
	@Disabled("Ignored because it inserts fake data")
	@Test
	@SkipWhenCheckItUnavailable
	void testAddEnterprise() {
		JSONObject result = CheckItAPI.addEnterprise("TRANSLOGIA DEVELOPMENT, S.L.", "B66941873", "jgarcia@translogia.es", "TRANSLOGIA DEVELOPMENT", null, null, null);
		assertTrue( result instanceof JSONObject);
	}
	
	@Test
	@SkipWhenCheckItUnavailable
	void testGetTransactions() {
		Date from = AonDateUtils.getYearFirstDay( 2022 );
		Date to = AonDateUtils.getYearLastDay( 2022 );
		JSONArray transactions = CheckItAPI.getTransactions(AON_ID, from, to, "33631");
		assertTrue( transactions instanceof JSONArray);
		assertTrue( transactions.length() > 0, "No transactions!");
	}
	
	@Test
	@SkipWhenCheckItUnavailable
	void testGetTranslogiaTransactions() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2021);
		Date from = calendar.getTime();
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		Date to = calendar.getTime();
		JSONArray transactions = CheckItAPI.getTransactions(11423, from, to, "33661");
		assertTrue( transactions instanceof JSONArray);
		assertTrue( transactions.length() > 0, "No Transactions!");
	}

}
