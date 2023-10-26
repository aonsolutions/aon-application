package net.aonsolutions.aon.bank.nordigen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage;
import com.esferalia.aon.occam.api.model.finance.nordigen.NORDIGEN_ACCESS_SCOPES;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenException;
import com.esferalia.aon.occam.api.model.type.Country;

public class NordigenAPITestCase {
	
	private static String accessToken;
	private static String refreshAccessToken;
	private static NORDIGEN_ACCESS_SCOPES[] ALL_SCOPES = {NORDIGEN_ACCESS_SCOPES.DETAILS, NORDIGEN_ACCESS_SCOPES.BALANCES, NORDIGEN_ACCESS_SCOPES.TRANSACTIONS};
	
	@BeforeAll
	public static void initialize() throws NordigenException {
		System.out.println( "Getting access token ....");
		JSONObject tokenJson = NordigenAPI.newAccessToken();
		accessToken = tokenJson.getString("access");
		refreshAccessToken = tokenJson.getString("refresh");
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testRefreshAccessToken() {
		System.out.println( "Access Token ..: " + accessToken);
		JSONObject newTokenJson = NordigenAPI.refreshAccessToken(refreshAccessToken);
		assertNotNull(newTokenJson);
		System.out.println( "New Access Token ..: " + newTokenJson);
		accessToken = newTokenJson.getString("access");
		assertNotNull(accessToken);
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testInvalidRefreshAccessToken() {
		NordigenException e = assertThrows(NordigenException.class, () -> NordigenAPI.refreshAccessToken(refreshAccessToken + "123"));
		assertEquals(401, e.getStatusCode());
	}

	@Test
	@SkipWhenNordigenUnavailable
	@Disabled("IGNORADO MIENTRAS NO OBTENGA LOS SCOPES AL CREARSE")
	void testCRDAgreements() throws InterruptedException {
		JSONObject agreement = NordigenAPI.createEndUserAgreement(accessToken, 90, 1, ALL_SCOPES, "CAIXABANK_CAIXESBB");
		String agreementId = agreement.getString("id");
		// GET THE CREATED AGREEMENT AND COMPARE TO THE ORIGINAL
		JSONObject retrievedAgreement = NordigenAPI.getEndUserAgreement(accessToken, agreementId);
		assertEquals(retrievedAgreement.toString(), agreement.toString());
		//DELETE THE AGREEMENT
		NordigenAPI.deleteEndUserAgreement(accessToken, agreementId);
	}
	
	@Test
	@SkipWhenNordigenUnavailable
	void testCreateAgreementUnknownInstitution() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.createEndUserAgreement(accessToken, 70, 1, ALL_SCOPES, "MOGAMBO_BANK"));
		assertEquals(400, e.getStatusCode());
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testCreateAgreementIncorrectHistoricalDays() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.createEndUserAgreement(accessToken, 1000000, 1, ALL_SCOPES, "CAIXABANK_CAIXESBB"));
		assertEquals(400, e.getStatusCode());
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testCreateAgreementIncorrectAccessDays() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.createEndUserAgreement(accessToken, 1, 1000000, ALL_SCOPES, "CAIXABANK_CAIXESBB"));
		assertEquals(400, e.getStatusCode());
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testCreateAgreementInvalidToken() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.createEndUserAgreement(accessToken + "123", 50, 50, ALL_SCOPES, "CAIXABANK_CAIXESBB"));
		assertEquals(401, e.getStatusCode());
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testGetAgreements() {
		JSONObject agreement = NordigenAPI.getEndUserAgreements(accessToken, 10, 0);
		assertNotNull(agreement);
	}
	
	@Test
	@SkipWhenNordigenUnavailable
	void testGetAgreementsInvalidToken() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getEndUserAgreements(accessToken + "123", 10, 0));
		assertEquals(401, e.getStatusCode());
	}
	
	@Test
	@SkipWhenNordigenUnavailable
	void testGetAgreementNullId() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getEndUserAgreement(accessToken, null));
		assertTrue(e.getStatusCode() == 400 || e.getStatusCode() == 404);
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testGetAgreementInvalidId() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getEndUserAgreement(accessToken, "chimbo-de45-4efb-aa1a-f8157ffa94"));
		assertTrue(e.getStatusCode() == 400 || e.getStatusCode() == 404);
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testGetAgreementInvalidToken() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getEndUserAgreement(accessToken + "123", "inventado"));
		assertEquals(401, e.getStatusCode());
	}
	
	@Test
	@SkipWhenNordigenUnavailable
	void testDeleteAgreementInvalidToken() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.deleteEndUserAgreement(accessToken + "123", "inventado"));
		assertEquals(401, e.getStatusCode());
	}
	
	@Test
	@SkipWhenNordigenUnavailable
	void testDeleteAgreementInvalidId() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.deleteEndUserAgreement(accessToken, "inventado"));
		assertTrue(e.getStatusCode() == 400 || e.getStatusCode() == 404);
	}
	
	@Test
	@SkipWhenNordigenUnavailable
	void testGetInstitutions() {
		JSONArray institutions = NordigenAPI.getInstitutions(accessToken, null, null);
		assertTrue( institutions instanceof JSONArray);
		assertTrue( institutions.length() > 0, "No Institutions!");
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testGetInstitutionsFromSpain() {
		JSONArray institutions = NordigenAPI.getInstitutions(accessToken, Country.ES, null);
		assertTrue( institutions instanceof JSONArray);
		assertTrue( institutions.length() > 0, "No Institutions!");
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testGetInstitutionsFromDPRK() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getInstitutions(accessToken, Country.KP, null));
		assertEquals(400, e.getStatusCode());
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testGetInstitutionsFromSpainWithPayments() {
		JSONArray institutions = NordigenAPI.getInstitutions(accessToken, Country.ES, true);
		assertTrue( institutions instanceof JSONArray);
		assertTrue( institutions.length() > 0, "No Institutions!");
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testGetInstitutionsInvalidToken() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getInstitutions(accessToken + "123", null, null));
		assertEquals(401, e.getStatusCode());
	}
	
	@Test
	@SkipWhenNordigenUnavailable
	void testGetInstitution() {
		JSONObject institutions = NordigenAPI.getInstitution(accessToken, "CAIXABANK_CAIXESBB");
		assertTrue( institutions instanceof JSONObject);
		assertTrue( institutions.length() > 0, "No Institutions!");
	}
	
	@Test
	@SkipWhenNordigenUnavailable
	void testGetInstitutionInvalidToken() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getInstitution(accessToken + "123", "CAIXABANK_CAIXESBB"));
		assertEquals(401, e.getStatusCode());
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testGetInstitutionInvalidBank() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getInstitution(accessToken, "MOGAMBO_BANK"));
		assertEquals(404, e.getStatusCode());
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testGetInstitutionNullBank() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getInstitution(accessToken, null));
		assertEquals(404, e.getStatusCode());
	}

	@Test
	@SkipWhenNordigenUnavailable
	@Disabled("A VECES NO OBTIENE A TIEMPO EL ID DE AGREEMENT")
	void testCRDRequisitions() {
		String bankInstitution = "CAIXABANK_CAIXESBB";
		JSONObject agreement;
		agreement = NordigenAPI.createEndUserAgreement(accessToken, 90, 1, ALL_SCOPES, bankInstitution);
		String agreementId = agreement.getString("id");
		RequisitionParams params = new RequisitionParams()
			.setAgreement(agreementId)
			.setUserLanguage(AonLanguage.SPANISH)
			.setInstitutionId(bankInstitution)
			.setRedirect("https://aonsolutions.org/");
		JSONObject requisition = NordigenAPI.createRequisition(accessToken, params);
		String requisitionId = requisition.getString("id");
		JSONObject retrievedRequisition = NordigenAPI.getRequisition(accessToken, requisitionId);
		assertEquals(requisition.toString(), retrievedRequisition.toString());
		NordigenAPI.deleteRequisition(accessToken, requisitionId);
	}
	
	@Test
	@SkipWhenNordigenUnavailable
	void testGetRequisitions() {
		JSONObject requisitions = NordigenAPI.getRequisitions(accessToken, 10, 0);
		assertTrue( requisitions instanceof JSONObject);
		assertTrue( requisitions.length() > 0, "No Requisitions!");
	}
	
	@Test
	@SkipWhenNordigenUnavailable
	void testGetRequisitionsInvalidToken() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getRequisitions(accessToken + "123", 10, 0));
		assertEquals(401, e.getStatusCode());
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testCreateRequisitionInvalidToken() {
		RequisitionParams params = new RequisitionParams()
			.setAgreement("MOGAMBO")
			.setUserLanguage(AonLanguage.SPANISH)
			.setInstitutionId("CAIXABANK_CAIXESBB")
			.setRedirect("https://aonsolutions.org/");
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.createRequisition(accessToken + "123", params));
		assertEquals(401, e.getStatusCode());
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testCreateRequisitionInvalidAgreement() {
		RequisitionParams params = new RequisitionParams()
			.setAgreement("MOGAMBO")
			.setUserLanguage(AonLanguage.SPANISH)
			.setInstitutionId("CAIXABANK_CAIXESBB")
			.setRedirect("https://aonsolutions.org/");
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.createRequisition(accessToken, params));
		assertEquals(400, e.getStatusCode());
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testGetRequisitionInvalidId() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getRequisition(accessToken, "MOGAMBO"));
		assertTrue(e.getStatusCode() == 400 || e.getStatusCode() == 404);
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testGetRequisitionNullId() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getRequisition(accessToken, null));
		assertTrue(e.getStatusCode() == 400 || e.getStatusCode() == 404);
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testGetRequisitionInvalidToken() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getRequisition(accessToken + "123", "MOGAMBO"));
		assertEquals(401, e.getStatusCode());
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testDeleteRequisitionInvalidToken() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.deleteRequisition(accessToken + "123", "MOGAMBO"));
		assertEquals(401, e.getStatusCode());
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testDeleteRequisitionInvalidId() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.deleteRequisition(accessToken, "MOGAMBO"));
		assertTrue(e.getStatusCode() == 400 || e.getStatusCode() == 404);
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testDeleteRequisitionNullId() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.deleteRequisition(accessToken, null));
		assertTrue(e.getStatusCode() == 400 || e.getStatusCode() == 404);
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testGetAccountInvalidId() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getAccount(accessToken, "MOGAMBO"));
		assertTrue(e.getStatusCode() == 400 || e.getStatusCode() == 404);
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testGetAccountNullId() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getAccount(accessToken, null));
		assertTrue(e.getStatusCode() == 400 || e.getStatusCode() == 404);
	}
	
	@Test
	@SkipWhenNordigenUnavailable
	void testGetAccountInvalidToken() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getAccount(accessToken + "123", "MOGAMBO"));
		assertEquals(401, e.getStatusCode());
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testGetAccountBalancesInvalidId() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getBalances(accessToken, "MOGAMBO"));
		assertTrue(e.getStatusCode() == 400 || e.getStatusCode() == 404);
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testGetAccountBalancesNullId() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getBalances(accessToken, null));
		assertTrue(e.getStatusCode() == 400 || e.getStatusCode() == 404);
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testGetAccountBalancesInvalidToken() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getAccount(accessToken + "123", "MOGAMBO"));
		assertEquals(401, e.getStatusCode());
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testGetAccountDetailsInvalidId() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getDetails(accessToken, "MOGAMBO"));
		assertTrue(e.getStatusCode() == 400 || e.getStatusCode() == 404);
	}
	
	@Test
	@SkipWhenNordigenUnavailable
	void testGetAccountDetailsNullId() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getDetails(accessToken, null));
		assertTrue(e.getStatusCode() == 400 || e.getStatusCode() == 404);
	}
	
	@Test
	@SkipWhenNordigenUnavailable
	void testGetAccountDetailsInvalidToken() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getDetails(accessToken + "123", "MOGAMBO"));
		assertEquals(401, e.getStatusCode());
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testGetAccountTransactionsInvalidId() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, -90);
		Date dateFrom = cal.getTime();
		Date dateTo = new Date(); 
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getTransactions(accessToken, "MOGAMBO", dateFrom, dateTo));
		assertTrue(e.getStatusCode() == 400 || e.getStatusCode() == 404);
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testGetAccountTransactionsNullId() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, -90);
		Date dateFrom = cal.getTime();
		Date dateTo = new Date(); 
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getTransactions(accessToken, null, dateFrom, dateTo));
		assertTrue(e.getStatusCode() == 400 || e.getStatusCode() == 404);
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testGetAccountTransactionsInvalidToken() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, -90);
		Date dateFrom = cal.getTime();
		Date dateTo = new Date(); 
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getTransactions(accessToken + "123", "MOGAMBO", dateFrom, dateTo));
		assertEquals(401, e.getStatusCode());
	}
}
