package net.aonsolutions.aon.bank.nordigen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage;
import com.esferalia.aon.occam.api.model.finance.nordigen.NORDIGEN_ACCESS_SCOPES;
import com.esferalia.aon.occam.api.model.finance.nordigen.NORDIGEN_REQUISITION_STATUS;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccessToken;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAgreement;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenInstitution;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisition;
import com.esferalia.aon.occam.api.model.type.Country;

public class NordigenTestCase {
	
	private static NordigenAccessToken nordigenToken;
	
	private void assertAgreement(NordigenAgreement agreement) {
		assertNotNull("Null agreement", agreement);			
		NORDIGEN_ACCESS_SCOPES[] accessScopes = agreement.getAccessScope();
		assertNotNull("Null agreement access scope", agreement.getAccessScope());
		Arrays.stream(NORDIGEN_ACCESS_SCOPES.values()).forEach(scope -> assertTrue(scope + " missing", Arrays.stream(accessScopes).anyMatch(s -> s.equals(scope))));
		assertNotNull("Null agreement access valid for days", agreement.getAccessValidForDays());
		assertNotNull("Null agreement creation date", agreement.getCreated());
		assertNotNull("Null agreement id", agreement.getId());
		assertNotNull("Null agreement institution id", agreement.getInstitutionId());
		assertNotNull("Null agreement max historical days", agreement.getMaxHistoricalDays());
	}
	
	private void assertRequisition(NordigenRequisition requisition) {
		assertNotNull("Null requisition", requisition);			
		assertEquals(AonLanguage.SPANISH.getLanguage(), requisition.getUserLanguage());
		assertNotNull("Null requisition agreement", requisition.getAgreement());			
		assertEquals(NORDIGEN_REQUISITION_STATUS.CR, requisition.getStatus());			
		assertNotNull("Null requisition redirect", requisition.getRedirect());
		assertNotNull("Null requisition redirect inmediate", requisition.getRedirectImmediate());
		assertNotNull("Null requisition created date", requisition.getCreated());
		assertNotNull("Null requisition account selection", requisition.getAccountSelection());
		assertNotNull("Null requisition ID", requisition.getId());
		assertNotNull("Null requisition institution ID", requisition.getInstitutionId());
		assertNotNull("Null requisition link", requisition.getLink());
		assertNull("Requisition SSN should be null", requisition.getSsn());
		assertEquals(requisition.getId(), requisition.getReference());
		assertTrue(requisition.getAccounts().isEmpty());
	}	
	
	@BeforeClass
	public static void initialize() throws Exception {
		nordigenToken = AonNordigen.getNewAccessToken();
	}
	
	@Test
	public void testObtainNewAccessToken() {
		try {
			NordigenAccessToken token = AonNordigen.getNewAccessToken();
			assertNotNull("Null token", token);
			assertNotNull("Null access", token.getAccess());
			assertNotNull("Null access expires", token.getAccessExpires());
			assertNotNull("Null refresh", token.getRefresh());
			assertNotNull("Null refresh expires", token.getRefreshExpires());
			assertNotNull("Null creation date", token.getCreationDate());
			assertNotNull("Null refresh date", token.getRefreshDate());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testRefreshAccessToken() {
		try {
			
			String originalAccess = nordigenToken.getAccess();
			Date originalRefreshDate = nordigenToken.getRefreshDate();
			
			AonNordigen.refreshToken(nordigenToken);
			
			assertNotNull("Null token", nordigenToken);
			assertNotNull("Null access", nordigenToken.getAccess());
			assertNotNull("Null access expires", nordigenToken.getAccessExpires());
			assertNotNull("Null refresh", nordigenToken.getRefresh());
			assertNotNull("Null refresh expires", nordigenToken.getRefreshExpires());
			assertNotNull("Null creation date", nordigenToken.getCreationDate());
			assertNotNull("Null refresh date", nordigenToken.getRefreshDate());
			assertNotEquals(originalRefreshDate, nordigenToken.getRefreshDate());
			assertNotEquals(nordigenToken.getCreationDate(), nordigenToken.getRefreshDate());
			assertNotEquals(originalAccess, nordigenToken.getAccess());
			
			
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetAllInstitutions() {
		try {
			List<NordigenInstitution> allInstitutions = AonNordigen.getInstitutions(nordigenToken, null, null);
			assertTrue(allInstitutions != null && allInstitutions.size() > 2000);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetAllSpanishInstitutions() {
		try {
			List<NordigenInstitution> allInstitutions = AonNordigen.getInstitutions(nordigenToken, Country.ES, null);
			assertNotNull(allInstitutions);
			assertTrue(allInstitutions.stream().allMatch(inst -> inst.getCountries().contains(Country.ES)));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetCaixabankInstitution() {
		try {
			NordigenInstitution caixaBank = AonNordigen.getInstitution(nordigenToken, "CAIXABANK_CAIXESBB");
			assertNotNull(caixaBank);
			assertNotNull("Null countries", caixaBank.getCountries());
			assertNotNull("Null ID", caixaBank.getId());
			assertNotNull("Null logo", caixaBank.getLogo());
			assertNotNull("Null name", caixaBank.getName());
			assertNotNull("Null transaction total days", caixaBank.getTransactionTotalDays());
			assertEquals("CAIXESBB", caixaBank.getBic());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testCrdAgreements() {
		try {
			NordigenInstitution caixaBank = AonNordigen.getInstitution(nordigenToken, "CAIXABANK_CAIXESBB");
			NordigenAgreement agreement = AonNordigen.createAgreement(nordigenToken, "CAIXABANK_CAIXESBB");
			//COMENTADO PORQUE LA API NO OBTIENE LOS "SCOPES" CUANDO SE CREA
//			assertAgreement(agreement);
			Integer expectedHistoricalDays = 90;
			assertEquals(expectedHistoricalDays, agreement.getMaxHistoricalDays());
			NordigenAgreement gottenAgreement = AonNordigen.getAgreement(nordigenToken, agreement.getId());
			assertAgreement(gottenAgreement);
			AonNordigen.deleteAgreement(nordigenToken, agreement);
			
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testCrdRequisitions() {
		try {
			NordigenAgreement agreement = AonNordigen.createAgreement(nordigenToken, "CAIXABANK_CAIXESBB");
			NordigenRequisition requisition = AonNordigen.createRequisition(nordigenToken, agreement, "https://aonsolutions.org/");
			assertRequisition(requisition);
			AonNordigen.getRequisition(nordigenToken, requisition.getId());
			assertRequisition(requisition);
			AonNordigen.deleteRequisition(nordigenToken, requisition);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	
}
