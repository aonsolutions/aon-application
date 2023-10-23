package net.aonsolutions.aon.bank.nordigen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Date;

import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage;
import com.esferalia.aon.occam.api.model.finance.nordigen.NORDIGEN_ACCESS_SCOPES;
import com.esferalia.aon.occam.api.model.type.Country;

import net.aonsolutions.aon.bank.nordigen.NordigenAPIAbstract.CreateRequisitionParams;


public class NordigenAPITestCase {
	
	private static String accessToken;
	private static String refreshAccessToken;
	private static NORDIGEN_ACCESS_SCOPES[] ALL_SCOPES = {NORDIGEN_ACCESS_SCOPES.DETAILS, NORDIGEN_ACCESS_SCOPES.BALANCES, NORDIGEN_ACCESS_SCOPES.TRANSACTIONS};
	
	@BeforeClass
	public static void initialize() throws NordigenException {
		JSONObject tokenJson = NordigenAPI.newAccessToken();
		accessToken = tokenJson.getString("access");
		refreshAccessToken = tokenJson.getString("refresh");
	}
	
	@Test
	public void testRefreshAccessToken() {
		try {			
			JSONObject newTokenJson = NordigenAPI.refreshAccessToken(refreshAccessToken);
			accessToken = newTokenJson.getString("access");
		} catch (NordigenException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testInvalidRefreshAccessToken() {
		try {			
			NordigenAPI.refreshAccessToken(refreshAccessToken + "123");
			fail("Refresh token shouldn't be valid");
		} catch (NordigenException e) {
			if (e.getStatusCode() != 401) {
				fail(e.getMessage());
			}
		}
	}
	
	//AGREEMENTS
	
	@Test
	@Ignore //IGNORADO MIENTRAS NO OBTENGA LOS SCOPES AL CREARSE
	public void testCRDAgreements() throws InterruptedException {
		try {
		//CREATE THE AGREEMENT
		JSONObject agreement = NordigenAPI.createEndUserAgreement(accessToken, 90, 1, ALL_SCOPES, "CAIXABANK_CAIXESBB");
		String agreementId = agreement.getString("id");
		//GET THE CREATED AGREEMENT AND COMPARE TO THE ORIGINAL
		JSONObject retrievedAgreement = NordigenAPI.getEndUserAgreement(accessToken, agreementId);
		assertEquals(retrievedAgreement.toString(), agreement.toString());
		//DELETE THE AGREEMENT
		NordigenAPI.deleteEndUserAgreement(accessToken, agreementId);
		} catch (NordigenException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testCreateAgreementUnknownInstitution() {
		try {
			NordigenAPI.createEndUserAgreement(accessToken, 70, 1, ALL_SCOPES, "MOGAMBO_BANK");
			fail("Shouldn't have created the agreement");
		} catch (NordigenException e) {
			if (e.getStatusCode() != 400) {
				fail(e.getMessage());
			}
		}
	}
	
	@Test
	public void testCreateAgreementIncorrectHistoricalDays() {
		try {
			NordigenAPI.createEndUserAgreement(accessToken, 1000000, 1, ALL_SCOPES, "CAIXABANK_CAIXESBB");
			fail("Shouldn't have created the agreement");
		} catch (NordigenException e) {
			if (e.getStatusCode() != 400) {
				fail(e.getMessage());
			}
		}
	}
	
	@Test
	public void testCreateAgreementIncorrectAccessDays() {
		try {
			NordigenAPI.createEndUserAgreement(accessToken, 1, 1000000, ALL_SCOPES, "CAIXABANK_CAIXESBB");
			fail("Shouldn't have created the agreement");
		} catch (NordigenException e) {
			if (e.getStatusCode() != 400) {
				fail(e.getMessage());
			}
		}
	}
	
	@Test
	public void testCreateAgreementInvalidToken() {
		try {			
			NordigenAPI.createEndUserAgreement(accessToken + "123", 50, 50, ALL_SCOPES, "CAIXABANK_CAIXESBB");
			fail("Access token shouldn't be valid");
		} catch (NordigenException e) {
			if (e.getStatusCode() != 401) {
				fail(e.getMessage());
			}
		}
	}
	
	@Test
	public void testGetAgreements() {
		try {			
			NordigenAPI.getEndUserAgreements(accessToken, 10, 0);
		} catch (NordigenException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetAgreementsInvalidToken() {
		try {			
			NordigenAPI.getEndUserAgreements(accessToken + "123", 10, 0);
			fail("Access token shouldn't be valid");
		} catch (NordigenException e) {
			if (e.getStatusCode() != 401) {
				fail(e.getMessage());
			}
		}
	}
	
	@Test
	public void testGetAgreementNullId() {
		try {			
			NordigenAPI.getEndUserAgreement(accessToken, null);
			fail("Shouldn't get anything");
		} catch (NordigenException e) {
			if (e.getStatusCode() != 400 && e.getStatusCode() != 404 ) {
				fail(e.getMessage());
			}
		}
	}
	
	@Test
	public void testGetAgreementInvalidId() {
		try {			
			NordigenAPI.getEndUserAgreement(accessToken, "chimbo-de45-4efb-aa1a-f8157ffa94");
			fail("Shouldn't get anything");
		} catch (NordigenException e) {
			if (e.getStatusCode() != 400 && e.getStatusCode() != 404) {
				fail(e.getMessage());
			}
		}
	}
	
	@Test
	public void testGetAgreementInvalidToken() {
		try {			
			NordigenAPI.getEndUserAgreement(accessToken + "123", "inventado");
			fail("Access token shouldn't be valid");
		} catch (NordigenException e) {
			if (e.getStatusCode() != 401) {
				fail(e.getMessage());
			}
		}
	}
	
	@Test
	public void testDeleteAgreementInvalidToken() {
		try {			
			NordigenAPI.deleteEndUserAgreement(accessToken + "123", "inventado");
			fail("Access token shouldn't be valid");
		} catch (NordigenException e) {
			if (e.getStatusCode() != 401) {
				fail(e.getMessage());
			}
		}
	}
	
	@Test
	public void testDeleteAgreementInvalidId() {
		try {			
			NordigenAPI.deleteEndUserAgreement(accessToken, "inventado");
			fail("Shouldn't delete anything");
		} catch (NordigenException e) {
			if (e.getStatusCode() != 400 && e.getStatusCode() != 404) {
				fail(e.getMessage());
			}
		}
	}
	
	//------------------------
	//INSTITUTIONS
	
	@Test
	public void testGetInstitutions() {
		try {			
			NordigenAPI.getInstitutions(accessToken, null, null);
		} catch (NordigenException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetInstitutionsFromSpain() {
		try {			
			NordigenAPI.getInstitutions(accessToken, Country.ES, null);
		} catch (NordigenException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetInstitutionsFromDPRK() {
		try {			
			NordigenAPI.getInstitutions(accessToken, Country.KP, null);
			fail("North Korea shouldn't be contemplated");
		} catch (NordigenException e) {
			if (e.getStatusCode() != 400) {
				fail(e.getMessage());
			}
		}
	}
	
	@Test
	public void testGetInstitutionsFromSpainWithPayments() {
		try {
			NordigenAPI.getInstitutions(accessToken, Country.ES, true);
		} catch (NordigenException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetInstitutionsInvalidToken() {
		try {
			NordigenAPI.getInstitutions(accessToken + "123", null, null);
			fail("Access token shouldn't be valid");
		} catch (NordigenException e) {
			if (e.getStatusCode() != 401) {
				fail(e.getMessage());
			}
		}
	}
	
	@Test
	public void testGetInstitution() {
		try {			
			NordigenAPI.getInstitution(accessToken, "CAIXABANK_CAIXESBB");
		} catch (NordigenException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetInstitutionInvalidToken() {
		try {
			NordigenAPI.getInstitution(accessToken + "123", "CAIXABANK_CAIXESBB");
			fail("Access token shouldn't be valid");
		} catch (NordigenException e) {
			if (e.getStatusCode() != 401) {
				fail(e.getMessage());
			}
		}
	}
	
	@Test
	public void testGetInstitutionInvalidBank() {
		try {
			NordigenAPI.getInstitution(accessToken, "MOGAMBO_BANK");
			fail("Bank shouldn't be valid");
		} catch (NordigenException e) {
			if (e.getStatusCode() != 404) {
				fail(e.getMessage());
			}
		}
	}
	
	@Test
	public void testGetInstitutionNullBank() {
		try {
			NordigenAPI.getInstitution(accessToken, null);
			fail("Null bank should throw an exception");
		} catch (NordigenException e) {
			if (e.getStatusCode() != 404) {
				fail(e.getMessage());
			}
		}
	}
	//------------------------
	
	//REQUISITIONS
	
	@Test
	@Ignore //A VECES NO OBTIENE A TIEMPO EL ID DE AGREEMENT
	public void testCRDRequisitions() {
		try {
			String bankInstitution = "CAIXABANK_CAIXESBB";
			JSONObject agreement;
			agreement = NordigenAPI.createEndUserAgreement(accessToken, 90, 1, ALL_SCOPES, bankInstitution);
			String agreementId = agreement.getString("id");
			CreateRequisitionParams params = new CreateRequisitionParams()
					.setAgreement(agreementId)
					.setUserLanguage(AonLanguage.SPANISH)
					.setInstitutionId(bankInstitution)
					.setRedirect("https://aonsolutions.org/");
			JSONObject requisition = NordigenAPI.createRequisition(accessToken, params);
			String requisitionId = requisition.getString("id");
			JSONObject retrievedRequisition = NordigenAPI.getRequisition(accessToken, requisitionId);
			assertEquals(requisition.toString(), retrievedRequisition.toString());
			NordigenAPI.deleteRequisition(accessToken, requisitionId);
		} catch (NordigenException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetRequisitions() {
		try {
			NordigenAPI.getRequisitions(accessToken, 10, 0);
		} catch (NordigenException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetRequisitionsInvalidToken() {
		try {
			NordigenAPI.getRequisitions(accessToken + "123", 10, 0);
			fail("Access token shouldn't be valid");
		} catch (NordigenException e) {
			if (e.getStatusCode() != 401) {
				fail(e.getMessage());
			}
		}
	}
	
	@Test
	public void testCreateRequisitionInvalidToken() {
		try {
			CreateRequisitionParams params = new CreateRequisitionParams()
					.setAgreement("MOGAMBO")
					.setUserLanguage(AonLanguage.SPANISH)
					.setInstitutionId("CAIXABANK_CAIXESBB")
					.setRedirect("https://aonsolutions.org/");
			NordigenAPI.createRequisition(accessToken + "123", params);
			fail("Access token shouldn't be valid");
		} catch (NordigenException e) {
			if (e.getStatusCode() != 401) {
				fail(e.getMessage());
			}
		}
	}
	
	@Test
	public void testCreateRequisitionInvalidAgreement() {
		try {
			CreateRequisitionParams params = new CreateRequisitionParams()
					.setAgreement("MOGAMBO")
					.setUserLanguage(AonLanguage.SPANISH)
					.setInstitutionId("CAIXABANK_CAIXESBB")
					.setRedirect("https://aonsolutions.org/");
			NordigenAPI.createRequisition(accessToken, params);
			fail("Agreement shouldn't be valid");
		} catch (NordigenException e) {
			if (e.getStatusCode() != 400) {
				fail(e.getMessage());
			}
		}
	}
	
	@Test
	public void testGetRequisitionInvalidId() {
		try {
			NordigenAPI.getRequisition(accessToken, "MOGAMBO");
			fail("Requisition ID shouldn't be valid");
		} catch (NordigenException e) {
			if (e.getStatusCode() != 400 && e.getStatusCode() != 404) {
				fail(e.getMessage());
			}
		}
	}
	
	@Test
	public void testGetRequisitionNullId() {
		try {
			NordigenAPI.getRequisition(accessToken, null);
			fail("Requisition ID shouldn't be valid");
		} catch (NordigenException e) {
			if (e.getStatusCode() != 400 && e.getStatusCode() != 404) {
				fail(e.getMessage());
			}
		}
	}
	
	@Test
	public void testGetRequisitionInvalidToken() {
		try {
			NordigenAPI.getRequisition(accessToken + "123", "MOGAMBO");
			fail("Access token shouldn't be valid");
		} catch (NordigenException e) {
			if (e.getStatusCode() != 401) {
				fail(e.getMessage());
			}
		}
	}
	
	@Test
	public void testDeleteRequisitionInvalidToken() {
		try {
			NordigenAPI.deleteRequisition(accessToken + "123", "MOGAMBO");
			fail("Access token shouldn't be valid");
		} catch (NordigenException e) {
			if (e.getStatusCode() != 401) {
				fail(e.getMessage());
			}
		}
	}
	
	@Test
	public void testDeleteRequisitionInvalidId() {
		try {
			NordigenAPI.deleteRequisition(accessToken, "MOGAMBO");
			fail("ID shouldn't be valid");
		} catch (NordigenException e) {
			if (e.getStatusCode() != 400 && e.getStatusCode() != 404) {
				fail(e.getMessage());
			}
		}
	}
	
	@Test
	public void testDeleteRequisitionNullId() {
		try {
			NordigenAPI.deleteRequisition(accessToken, null);
			fail("Null ID shouldn't be valid");
		} catch (NordigenException e) {
			if (e.getStatusCode() != 400 && e.getStatusCode() != 404) {
				fail(e.getMessage());
			}
		}
	}
	
	//------------------------
	//ACCOUNTS
	
	@Test
	public void testGetAccountInvalidId() {
		try {
			NordigenAPI.getAccount(accessToken, "MOGAMBO");
			fail("ID shouldn't be valid");
		} catch (NordigenException e) {
			if (e.getStatusCode() != 400 && e.getStatusCode() != 404) {
				fail(e.getMessage());
			}
		}
	}
	
	@Test
	public void testGetAccountNullId() {
		try {
			NordigenAPI.getAccount(accessToken, null);
			fail("ID shouldn't be valid");
		} catch (NordigenException e) {
			if (e.getStatusCode() != 400 && e.getStatusCode() != 404) {
				fail(e.getMessage());
			}
		}
	}
	
	@Test
	public void testGetAccountInvalidToken() {
		try {
			NordigenAPI.getAccount(accessToken + "123", "MOGAMBO");
			fail("Access token shouldn't be valid");
		} catch (NordigenException e) {
			if (e.getStatusCode() != 401) {
				fail(e.getMessage());
			}
		}
	}
	
	@Test
	public void testGetAccountBalancesInvalidId() {
		try {
			NordigenAPI.getBalances(accessToken, "MOGAMBO");
			fail("ID shouldn't be valid");
		} catch (NordigenException e) {
			if (e.getStatusCode() != 400 && e.getStatusCode() != 404) {
				fail(e.getMessage());
			}
		}
	}
	
	@Test
	public void testGetAccountBalancesNullId() {
		try {
			NordigenAPI.getBalances(accessToken, null);
			fail("ID shouldn't be valid");
		} catch (NordigenException e) {
			if (e.getStatusCode() != 400 && e.getStatusCode() != 404) {
				fail(e.getMessage());
			}
		}
	}
	
	@Test
	public void testGetAccountBalancesInvalidToken() {
		try {
			NordigenAPI.getAccount(accessToken + "123", "MOGAMBO");
			fail("Access token shouldn't be valid");
		} catch (NordigenException e) {
			if (e.getStatusCode() != 401) {
				fail(e.getMessage());
			}
		}
	}
	
	@Test
	public void testGetAccountDetailsInvalidId() {
		try {
			NordigenAPI.getDetails(accessToken, "MOGAMBO");
			fail("ID shouldn't be valid");
		} catch (NordigenException e) {
			if (e.getStatusCode() != 400 && e.getStatusCode() != 404) {
				fail(e.getMessage());
			}
		}
	}
	
	@Test
	public void testGetAccountDetailsNullId() {
		try {
			NordigenAPI.getDetails(accessToken, null);
			fail("ID shouldn't be valid");
		} catch (NordigenException e) {
			if (e.getStatusCode() != 400 && e.getStatusCode() != 404) {
				fail(e.getMessage());
			}
		}
	}
	
	@Test
	public void testGetAccountDetailsInvalidToken() {
		try {
			NordigenAPI.getDetails(accessToken + "123", "MOGAMBO");
			fail("Access token shouldn't be valid");
		} catch (NordigenException e) {
			if (e.getStatusCode() != 401) {
				fail(e.getMessage());
			}
		}
	}
	
	@Test
	public void testGetAccountTransactionsInvalidId() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, -90);
		try {
			NordigenAPI.getTransactions(accessToken, "MOGAMBO", cal.getTime(), new Date());
			fail("ID shouldn't be valid");
		} catch (NordigenException e) {
			if (e.getStatusCode() != 400 && e.getStatusCode() != 404) {
				fail(e.getMessage());
			}
		}
	}
	
	@Test
	public void testGetAccountTransactionsNullId() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, -90);
		try {
			NordigenAPI.getTransactions(accessToken, null, cal.getTime(), new Date());
			fail("ID shouldn't be valid");
		} catch (NordigenException e) {
			if (e.getStatusCode() != 400 && e.getStatusCode() != 404) {
				fail(e.getMessage());
			}
		}
	}
	
	@Test
	public void testGetAccountTransactionsInvalidToken() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, -90);
		try {
			NordigenAPI.getTransactions(accessToken + "123", "MOGAMBO", cal.getTime(), new Date());
			fail("Access token shouldn't be valid");
		} catch (NordigenException e) {
			if (e.getStatusCode() != 401) {
				fail(e.getMessage());
			}
		}
	}
	
	//------------------------
}
