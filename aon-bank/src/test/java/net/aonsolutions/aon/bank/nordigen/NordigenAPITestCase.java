package net.aonsolutions.aon.bank.nordigen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccessScope;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccessToken;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAgreement;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAgreements;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenException;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenInstitution;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisition;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisitions;
import com.esferalia.aon.occam.api.model.type.Country;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


public class NordigenAPITestCase {
	
	//private static String accessToken;
	private static String refreshAccessToken;
	private static NordigenAccessScope[] ALL_SCOPES = {NordigenAccessScope.DETAILS, NordigenAccessScope.BALANCES, NordigenAccessScope.TRANSACTIONS};
	private static final String CAIXABANK_CAIXESBB = "CAIXABANK_CAIXESBB";
	
	public static void initialize() throws NordigenException {
		NordigenAccessToken tokenJson = NordigenAPI.newAccessToken();
//		accessToken = tokenJson.getAccess();
		refreshAccessToken = tokenJson.getRefresh();
	}
	
	private static synchronized NordigenAccessToken newAccessToken() throws NordigenException {
		return NordigenAPI.newAccessToken();
	}
	private static synchronized String getAccessToken() throws NordigenException {
		NordigenAccessToken token = newAccessToken();
		return token.getAccess();
	}
	private static synchronized String getRefreshAccessToken() throws NordigenException {
		NordigenAccessToken token = newAccessToken();
		return token.getRefresh();
	}
	
	@BeforeEach
	public void beforeTest(TestInfo testInfo) throws NordigenException {
		System.out.println( "Running test against Nordigen API .... " + testInfo.getDisplayName());
	}
	
	@Test
	@SkipWhenNordigenUnavailable
	void testRefreshAccessToken() {
		NordigenAccessToken newTokenJson = NordigenAPI.refreshAccessToken(getRefreshAccessToken());
		assertNotNull(newTokenJson);
		String accessToken = newTokenJson.getAccess();
		assertNotNull(accessToken);
	}
	
	@Test
	@SkipWhenNordigenUnavailable
	void testInvalidRefreshAccessToken() {
		NordigenException e = assertThrows(NordigenException.class, () -> NordigenAPI.refreshAccessToken(refreshAccessToken + "123"));
		assertNotNull( e.getResponse() );
		assertEquals(401, e.getResponse().getStatusCode());
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testGetInstitution() {
		String institutionId = CAIXABANK_CAIXESBB;
		NordigenInstitution institution = NordigenAPI.getInstitution(getAccessToken(), institutionId );
		assertNotNull(institution);
		assertNotNull(institution.getId());
		assertEquals( institutionId, institution.getId());
	}
	
	@Test
	@SkipWhenNordigenUnavailable
	void testGetInstitutionInvalidToken() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getInstitution(getAccessToken() + "123", CAIXABANK_CAIXESBB));
		assertNotNull( e.getResponse() );
		assertEquals(401, e.getResponse().getStatusCode());
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testGetInstitutionInvalidBank() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getInstitution(getAccessToken(), "MOGAMBO_BANK"));
		assertNotNull( e.getResponse() );
		assertEquals(404, e.getResponse().getStatusCode());
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testGetInstitutionNullBank() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getInstitution(getAccessToken(), null));
		assertNotNull( e.getResponse() );
		assertEquals(404, e.getResponse().getStatusCode());
	}

	@Test
	@SkipWhenNordigenUnavailable
	@Disabled("HTTP 500 ????")
	void testCreateAgreementUnknownInstitution() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.createEndUserAgreement(getAccessToken(), 70, 1, ALL_SCOPES, "MOGAMBO_BANK"));
		assertNotNull( e.getResponse() );
		assertEquals(400, e.getResponse().getStatusCode());
	}
	
	@Test
	@SkipWhenNordigenUnavailable
//	@Disabled("IGNORADO MIENTRAS NO OBTENGA LOS SCOPES AL CREARSE")
	void testCRDAgreements() throws InterruptedException {
		NordigenAgreement agreement = NordigenAPI.createEndUserAgreement(getAccessToken(), 90, 1, ALL_SCOPES, CAIXABANK_CAIXESBB);
		String agreementId = agreement.getId();
		// GET THE CREATED AGREEMENT AND COMPARE TO THE ORIGINAL
		NordigenAgreement retrievedAgreement = NordigenAPI.getEndUserAgreement(getAccessToken(), agreementId);
		assertEquals(retrievedAgreement.getId(), agreement.getId());
		//DELETE THE AGREEMENT
		NordigenAPI.deleteEndUserAgreement(getAccessToken(), agreementId);
	}

	@Test
	@SkipWhenNordigenUnavailable
	@Disabled("A VECES NO OBTIENE A TIEMPO EL ID DE AGREEMENT")
	void testCRDRequisitions() {
		NordigenAgreement agreement = NordigenAPI.createEndUserAgreement(getAccessToken(), 90, 1, ALL_SCOPES, CAIXABANK_CAIXESBB);
		RequisitionParams params = new RequisitionParams()
			.setAgreement(agreement.getId())
			.setUserLanguage(AonLanguage.SPANISH)
			.setInstitutionId(agreement.getInstitutionId())
			.setRedirect("https://aonsolutions.org/");
		NordigenRequisition requisition = NordigenAPI.createRequisition(getAccessToken(), params);
		String requisitionId = requisition.getId();
		NordigenRequisition retrievedRequisition = NordigenAPI.getRequisition(getAccessToken(), requisitionId);
		assertEquals(requisition.toString(), retrievedRequisition.toString());
		NordigenAPI.deleteRequisition(getAccessToken(), requisitionId);
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testCreateAgreementIncorrectHistoricalDays() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.createEndUserAgreement(getAccessToken(), 1000000, 1, ALL_SCOPES, CAIXABANK_CAIXESBB));
		assertNotNull( e.getResponse() );
		assertEquals(400, e.getResponse().getStatusCode());
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testCreateAgreementIncorrectAccessDays() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.createEndUserAgreement(getAccessToken(), 1, 1000000, ALL_SCOPES, CAIXABANK_CAIXESBB));
		assertNotNull( e.getResponse() );
		assertEquals(400, e.getResponse().getStatusCode());
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testCreateAgreementInvalidToken() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.createEndUserAgreement(getAccessToken() + "123", 50, 50, ALL_SCOPES, CAIXABANK_CAIXESBB));
		assertNotNull( e.getResponse() );
		assertEquals(401, e.getResponse().getStatusCode());
	}
	
	@Test
	@SkipWhenNordigenUnavailable
	void testGetInstitutions() {
		List<NordigenInstitution> institutions = NordigenAPI.getInstitutionsByCountry(getAccessToken(), null);
		assertNotNull(institutions);
		assertTrue( institutions.size() > 0, "No Institutions!");
	}
	
	@Test
	@SkipWhenNordigenUnavailable
	void testGetInstitutionsFromSpain() {
		List<NordigenInstitution> institutions = NordigenAPI.getInstitutionsByCountry(getAccessToken(), Country.ES);
		assertNotNull(institutions);
		assertTrue( institutions.size() > 0, "No Institutions!");
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testGetInstitutionsFromDPRK() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getInstitutionsByCountry(getAccessToken(), Country.KP));
		assertNotNull( e.getResponse() );
		assertEquals(400, e.getResponse().getStatusCode());
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testGetInstitutionsInvalidToken() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getInstitutions(getAccessToken() + "123", null));
		assertNotNull( e.getResponse() );
		assertEquals(401, e.getResponse().getStatusCode());
	}
	
	@Test
	@SkipWhenNordigenUnavailable
	void testGetInstitutionsFromSpainWithPayments() {
		List<NordigenInstitution> institutions = NordigenAPI.getInstitutionsByCountry(getAccessToken(), Country.ES);
		assertNotNull(institutions);
		assertTrue( institutions.size() > 0, "No Institutions!");
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testGetRequisitions() {
		NordigenRequisitions requisitions = NordigenAPI.getRequisitions(getAccessToken(), 100, 0);
		assertNotNull( requisitions );
		assertNotNull( requisitions.getCount() );
		assertNotNull( requisitions.getResult() );
		assertTrue( requisitions.getResult().size() > 0, "No Requisitions!");
	}
	
	@Test
	@SkipWhenNordigenUnavailable
	void testDeleteRequisitionInvalidToken() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.deleteRequisition(getAccessToken() + "123", "MOGAMBO"));
		assertNotNull( e.getResponse() );
		assertEquals(401, e.getResponse().getStatusCode());
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testDeleteRequisitionInvalidId() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.deleteRequisition(getAccessToken(), "MOGAMBO"));
		assertNotNull( e.getResponse() );
		assertTrue(e.getResponse().getStatusCode() == 400 || e.getResponse().getStatusCode() == 404);
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testCreateRequisitionInvalidToken() {
		RequisitionParams params = new RequisitionParams()
			.setAgreement("MOGAMBO")
			.setUserLanguage(AonLanguage.SPANISH)
			.setInstitutionId(CAIXABANK_CAIXESBB)
			.setRedirect("https://aonsolutions.org/");
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.createRequisition(getAccessToken() + "123", params));
		assertNotNull( e.getResponse() );
		assertEquals(401, e.getResponse().getStatusCode());
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testCreateRequisitionInvalidAgreement() {
		RequisitionParams params = new RequisitionParams()
			.setAgreement("MOGAMBO")
			.setUserLanguage(AonLanguage.SPANISH)
			.setInstitutionId(CAIXABANK_CAIXESBB)
			.setRedirect("https://aonsolutions.org/");
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.createRequisition(getAccessToken(), params));
		assertNotNull( e.getResponse() );
		assertEquals(400, e.getResponse().getStatusCode());
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testGetRequisitionInvalidId() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getRequisition(getAccessToken(), "MOGAMBO"));
		assertNotNull( e.getResponse() );
		assertTrue(e.getResponse().getStatusCode() == 400 || e.getResponse().getStatusCode() == 404);
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testGetRequisitionNullId() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getRequisition(getAccessToken(), null));
		assertNotNull( e.getResponse() );
		assertTrue(e.getResponse().getStatusCode() == 400 || e.getResponse().getStatusCode() == 404);
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testGetRequisitionInvalidToken() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getRequisition(getAccessToken() + "123", "MOGAMBO"));
		assertNotNull( e.getResponse() );
		assertEquals(401, e.getResponse().getStatusCode());
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testDeleteRequisitionNullId() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.deleteRequisition(getAccessToken(), null));
		assertNotNull( e.getResponse() );
		assertTrue(e.getResponse().getStatusCode() == 400 || e.getResponse().getStatusCode() == 404);
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testGetRequisitionsInvalidToken() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getRequisitions(getAccessToken() + "123", 10, 0));
		assertNotNull( e.getResponse() );
		assertEquals(401, e.getResponse().getStatusCode());
	}
	
	@Test
	@SkipWhenNordigenUnavailable
	void testGetAgreements() {
		NordigenAgreements agreements = NordigenAPI.getEndUserAgreements(getAccessToken(), 10, 0);
		assertNotNull( agreements );
		assertNotNull( agreements.getCount() );
		assertNotNull( agreements.getResult() );
		assertTrue( agreements.getResult().size() > 0, "No Agreements!");
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testGetAgreementsInvalidToken() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getEndUserAgreements(getAccessToken() + "123", 10, 0));
		assertNotNull( e.getResponse() );
		assertEquals(401, e.getResponse().getStatusCode());
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testGetAgreementNullId() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getEndUserAgreement(getAccessToken(), null));
		assertNotNull( e.getResponse() );
		assertTrue(e.getResponse().getStatusCode() == 400 || e.getResponse().getStatusCode() == 404);
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testGetAgreementInvalidId() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getEndUserAgreement(getAccessToken(), "chimbo-de45-4efb-aa1a-f8157ffa94"));
		assertNotNull( e.getResponse() );
		assertTrue(e.getResponse().getStatusCode() == 400 || e.getResponse().getStatusCode() == 404);
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testGetAgreementInvalidToken() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getEndUserAgreement(getAccessToken() + "123", "inventado"));
		assertNotNull( e.getResponse() );
		assertEquals(401, e.getResponse().getStatusCode());
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testDeleteAgreementInvalidToken() {
		NordigenException e = assertThrows(NordigenException.class
				, () -> NordigenAPI.deleteEndUserAgreement(getAccessToken() + "123", "inventado"));
		assertNotNull( e.getResponse() );
		assertEquals(401, e.getResponse().getStatusCode());
	}
	
	@Test
	@SkipWhenNordigenUnavailable
	void testDeleteAgreementInvalidId() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.deleteEndUserAgreement(getAccessToken(), "inventado"));
		assertNotNull( e.getResponse() );
		assertTrue(e.getResponse().getStatusCode() == 400 || e.getResponse().getStatusCode() == 404);
	}
	
	@Test
	@SkipWhenNordigenUnavailable
	void testGetAccountInvalidId() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getAccountMetadata(getAccessToken(), "MOGAMBO"));
		assertNotNull( e.getResponse() );
		assertTrue(e.getResponse().getStatusCode() == 400 || e.getResponse().getStatusCode() == 404);
	}
	
	@Test
	@SkipWhenNordigenUnavailable
	void testGetAccountNullId() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getAccountMetadata(getAccessToken(), null));
		assertNotNull( e.getResponse() );
		assertTrue(e.getResponse().getStatusCode() == 400 || e.getResponse().getStatusCode() == 404);
	}
	
	@Test
	@SkipWhenNordigenUnavailable
	void testGetAccountInvalidToken() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getAccountMetadata(getAccessToken() + "123", "MOGAMBO"));
		assertNotNull( e.getResponse() );
		assertEquals(401, e.getResponse().getStatusCode());
	}
	
	@Test
	@SkipWhenNordigenUnavailable
	void testGetAccountBalancesInvalidToken() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getAccountMetadata(getAccessToken() + "123", "MOGAMBO"));
		assertNotNull( e.getResponse() );
		assertEquals(401, e.getResponse().getStatusCode());
	}
	
	@Test
	@SkipWhenNordigenUnavailable
	void testGetAccountBalancesInvalidId() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getBalances(getAccessToken(), "MOGAMBO"));
		assertNotNull( e.getResponse() );
		assertTrue(e.getResponse().getStatusCode() == 400 || e.getResponse().getStatusCode() == 404);
	}

	@Test
	@SkipWhenNordigenUnavailable
	void testGetAccountBalancesNullId() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getBalances(getAccessToken(), null));
		assertNotNull( e.getResponse() );
		assertTrue(e.getResponse().getStatusCode() == 400 || e.getResponse().getStatusCode() == 404);
	}
	
	
	@Test
	@SkipWhenNordigenUnavailable
	void testGetAccountDetailsInvalidId() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getDetail(getAccessToken(), "MOGAMBO"));
		assertNotNull( e.getResponse() );
		assertTrue(e.getResponse().getStatusCode() == 400 || e.getResponse().getStatusCode() == 404);
	}
	
	@Test
	@SkipWhenNordigenUnavailable
	void testGetAccountDetailsNullId() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getDetail(getAccessToken(), null));
		assertNotNull( e.getResponse() );
		assertTrue(e.getResponse().getStatusCode() == 400 || e.getResponse().getStatusCode() == 404);
	}
	
	@Test
	@SkipWhenNordigenUnavailable
	void testGetAccountDetailsInvalidToken() {
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getDetail(getAccessToken() + "123", "MOGAMBO"));
		assertNotNull( e.getResponse() );
		assertEquals(401, e.getResponse().getStatusCode());
	}
	
	@Test
	@SkipWhenNordigenUnavailable
	void testGetAccountTransactionsInvalidId() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, -90);
		Date dateFrom = cal.getTime();
		Date dateTo = new Date(); 
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getTransactions(getAccessToken(), "MOGAMBO", dateFrom, dateTo));
		assertNotNull( e.getResponse() );
		assertTrue(e.getResponse().getStatusCode() == 400 || e.getResponse().getStatusCode() == 404);
	}

	// *********************************************************************
	// *********************************************************************
	// *********************************************************************
	// *********************************************************************
	// *********************************************************************


	@Test
	@SkipWhenNordigenUnavailable
	@Disabled
	void testGetAccountTransactionsNullId() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, -90);
		Date dateFrom = cal.getTime();
		Date dateTo = new Date(); 
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getTransactions(getAccessToken(), null, dateFrom, dateTo));
		assertNotNull( e.getResponse() );
		assertTrue(e.getResponse().getStatusCode() == 400 || e.getResponse().getStatusCode() == 404);
	}

	@Test
	@SkipWhenNordigenUnavailable
	@Disabled
	void testGetAccountTransactionsInvalidToken() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, -90);
		Date dateFrom = cal.getTime();
		Date dateTo = new Date(); 
		NordigenException e = assertThrows(NordigenException.class
			, () -> NordigenAPI.getTransactions(getAccessToken() + "123", "MOGAMBO", dateFrom, dateTo));
		assertEquals(401, e.getResponse().getStatusCode());
	}
}
