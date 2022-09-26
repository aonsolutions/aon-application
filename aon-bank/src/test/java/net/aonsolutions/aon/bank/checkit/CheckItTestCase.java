package net.aonsolutions.aon.bank.checkit;

import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Date;

import org.junit.Ignore;
import org.junit.Test;

import net.aonsolutions.aon.bank.checkit.CheckItAPI;
import net.aonsolutions.aon.bank.checkit.CheckItException;

public class CheckItTestCase {
	
	private static final Integer AON_ID = 11413;
	private static final Integer RAYSON_ID = 11414;
	

	@Test
	public void testGetBanks() {
		try {
			CheckItAPI.getBanks();
		} catch (Exception e) {
			if (!e.getMessage().equals(CheckItException.NO_CONNECTION_MSG))
				fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetLogins() {
		try {
			CheckItAPI.getLogins(44);
		} catch (Exception e) {
			if (!e.getMessage().equals(CheckItException.NO_CONNECTION_MSG))
				fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetLoginFields() {
		try {
			CheckItAPI.getLoginFields(20);
		} catch (Exception e) {
			if (!e.getMessage().equals(CheckItException.NO_CONNECTION_MSG))
				fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetCredentials() {
		try {
			CheckItAPI.getCredentials(AON_ID, 10);
		} catch (Exception e) {
			if (!e.getMessage().equals(CheckItException.NO_CONNECTION_MSG))
				fail(e.getMessage());
		}
	}
	
	@Test
	public void testAddCredentials() {
		try {
			CheckItAPI.addCredentials(AON_ID, 7, "54321", "123API", "test2016");
		} catch (Exception e) {
			if (!e.getMessage().equals(CheckItException.NO_CONNECTION_MSG))
				fail(e.getMessage());
		}
	}
	
	@Test
	public void testAddExtraField() {
		try {
			CheckItAPI.addExtraField(RAYSON_ID, 39, "17");
		} catch (Exception e) {
			if (!e.getMessage().equals(CheckItException.NO_CONNECTION_MSG))
				fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetAccounts() {
		try {
			CheckItAPI.getAccounts(AON_ID, 1, null);
		} catch (Exception e) {
			if (!e.getMessage().equals(CheckItException.NO_CONNECTION_MSG))
				fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetTranslogiaAccounts() {
		try {
			System.out.println(CheckItAPI.getAccounts(11423, 1, null));
		} catch (Exception e) {
			if (!e.getMessage().equals(CheckItException.NO_CONNECTION_MSG))
				fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetEnterprise() {
		try {
			System.out.println(CheckItAPI.getEnterprise("B01487271"));
		} catch (Exception e) {
			if (!e.getMessage().equals(CheckItException.NO_CONNECTION_MSG))
				fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetLogs() {
		try {
			System.out.println(CheckItAPI.getLogs(11413, 33631));
		} catch (Exception e) {
			if (!e.getMessage().equals(CheckItException.NO_CONNECTION_MSG))
				fail(e.getMessage());
		}
	}
	
	
	@Ignore("Ignored because it inserts a new account each time is called and they cannot be deleted for now")
	@Test
	public void testAddAccount() {
		try {
			System.out.println(CheckItAPI.addAccount(RAYSON_ID, 79, 1, "ES0614650100936000306237", 1));
		} catch (Exception e) {
			if (!e.getMessage().equals(CheckItException.NO_CONNECTION_MSG))
				fail(e.getMessage());
		}
	}

	@Test
	public void testAddAccountWrongLogin() {
		try {
			System.out.println(CheckItAPI.addAccount(RAYSON_ID, 57, 39, "ES9121000418450200051332", 1));
		} catch (Exception e) {
			if (!e.getMessage().equals(CheckItException.NO_CONNECTION_MSG))
				fail(e.getMessage());
		}
	}
	
	@Ignore("Ignored because it inserts a new account each time is called and they cannot be deleted for now")
	@Test
	public void testAddAccountApi() {
		try {
			System.out.println(CheckItAPI.addAccountApi(RAYSON_ID, 79, "ES0614650100936000306237", -326360.34, -326360.34, new Date(), "ES0614650100936000306237", 1));
		} catch (Exception e) {
			if (!e.getMessage().equals(CheckItException.NO_CONNECTION_MSG))
				fail(e.getMessage());
		}
	}
	
	@Ignore("Ignored because it inserts fake data")
	@Test
	public void testAddEnterprise() {
		try {
			System.out.println(CheckItAPI.addEnterprise("TRANSLOGIA DEVELOPMENT, S.L.", "B66941873", "jgarcia@translogia.es", "TRANSLOGIA DEVELOPMENT", null, null, null));
		} catch (Exception e) {
			if (!e.getMessage().equals(CheckItException.NO_CONNECTION_MSG))
				fail(e.getMessage());
		}
	}//C37354826
	@Ignore("Ignored because it inserts fake data")
	@Test
	public void testAddEnterpriseNoMail() {
		try {
			System.out.println(CheckItAPI.addEnterprise("Empresa HIJA", "B66600000", null, "Empresa HIJA", "empresa-parent.aonSolutions.net", null, null));
		} catch (Exception e) {
			if (!e.getMessage().equals(CheckItException.NO_CONNECTION_MSG))
				fail(e.getMessage());
		}
	}
	@Ignore("Ignored because it inserts fake data")
	@Test
	public void testAddEnterpriseNoMailParent() {
		try {
			System.out.println(CheckItAPI.addEnterprise("Empresa MADRE", "B66600666", null, "Empresa MADRE", "empresa.aonSolutions.net", null, null));
		} catch (Exception e) {
			if (!e.getMessage().equals(CheckItException.NO_CONNECTION_MSG))
				fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetTransactions() {
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
		try {
			CheckItAPI.getTransactions(AON_ID, from, to, "33631");
		} catch (Exception e) {
			if (!e.getMessage().equals(CheckItException.NO_CONNECTION_MSG))
				fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetTranslogiaTransactions() {
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
		try {
			System.out.println(CheckItAPI.getTransactions(11423, from, to, "33661"));
		} catch (Exception e) {
			if (!e.getMessage().equals(CheckItException.NO_CONNECTION_MSG))
				fail(e.getMessage());
		}
	}

}
