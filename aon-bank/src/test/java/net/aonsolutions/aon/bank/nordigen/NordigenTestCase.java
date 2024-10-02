package net.aonsolutions.aon.bank.nordigen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccessScope;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisitionStatus;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccessToken;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAgreement;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenException;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenInstitution;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisition;
import com.esferalia.aon.occam.api.model.type.Country;

import net.aonsolutions.aon.bank.nordigen.utils.NordigenAgreementUtils;
import net.aonsolutions.aon.bank.nordigen.utils.NordigenInstitutionUtils;
import net.aonsolutions.aon.bank.nordigen.utils.NordigenRequisitionUtils;
import net.aonsolutions.aon.bank.nordigen.utils.NordigenTokenUtils;

public class NordigenTestCase {
	
	private static NordigenAccessToken nordigenToken;
	
	private void assertAgreement(NordigenAgreement agreement) {
		assertNotNull(agreement, "Null agreement");			
		NordigenAccessScope[] accessScopes = agreement.getAccessScope();
		assertNotNull(agreement.getAccessScope(),"Null agreement access scope");
		Arrays.stream(NordigenAccessScope.values())
			.forEach(scope -> assertTrue(Arrays.stream(accessScopes).anyMatch(s -> s.equals(scope)), scope + " missing"));
		assertNotNull(agreement.getAccessValidForDays(), "Null agreement access valid for days");
		assertNotNull(agreement.getCreated(), "Null agreement creation date");
		assertNotNull(agreement.getId(), "Null agreement id");
		assertNotNull(agreement.getInstitutionId(), "Null agreement institution id");
		assertNotNull(agreement.getMaxHistoricalDays(), "Null agreement max historical days");
	}
	
	private void assertRequisition(NordigenRequisition requisition) {
		assertNotNull(requisition, "Null requisition");			
		assertEquals(AonLanguage.SPANISH.getLanguage(), requisition.getUserLanguage());
		assertNotNull(requisition.getAgreement(), "Null requisition agreement");			
		assertEquals(NordigenRequisitionStatus.CREATED, requisition.getStatus());			
		assertNotNull(requisition.getRedirect(), "Null requisition redirect");
		assertNotNull(requisition.getRedirectImmediate(), "Null requisition redirect inmediate");
		assertNotNull(requisition.getCreated(), "Null requisition created date");
		assertNotNull(requisition.getAccountSelection(), "Null requisition account selection");
		assertNotNull(requisition.getId(), "Null requisition ID");
		assertNotNull(requisition.getInstitutionId(), "Null requisition institution ID");
		assertNotNull(requisition.getLink(), "Null requisition link");
		assertNull(requisition.getSsn(), "Requisition SSN should be null");
		assertEquals(requisition.getId(), requisition.getReference());
		assertTrue(requisition.getAccounts().isEmpty());
	}	
	
	@BeforeAll
	public static void initialize() throws Exception {
		nordigenToken = NordigenTokenUtils.getNewAccessToken();
	}
	
	@BeforeEach
	public void beforeTest(TestInfo testInfo) throws NordigenException {
		System.out.println( "Running test against AonNordigen .... " + testInfo.getDisplayName());
	}
	
	
	@Test
	void testObtainNewAccessToken() {
		try {
			NordigenAccessToken token = NordigenTokenUtils.getNewAccessToken();
			assertNotNull(token, "Null token");
			assertNotNull(token.getAccess(), "Null access");
			assertNotNull(token.getAccessExpires(), "Null access expires");
			assertNotNull(token.getRefresh(), "Null refresh");
			assertNotNull(token.getRefreshExpires(), "Null refresh expires");
			assertNotNull(token.getCreationDate(), "Null creation date");
			assertNotNull(token.getRefreshDate(), "Null refresh date");
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testRefreshAccessToken() {
		String originalAccess = nordigenToken.getAccess();
		Date originalRefreshDate = nordigenToken.getRefreshDate();
		
		NordigenTokenUtils.refreshToken(nordigenToken);
		
		assertNotNull(nordigenToken, "Null token");
		assertNotNull(nordigenToken.getAccess(), "Null access");
		assertNotNull(nordigenToken.getAccessExpires(), "Null access expires");
		assertNotNull(nordigenToken.getRefresh(), "Null refresh");
		assertNotNull(nordigenToken.getRefreshExpires(), "Null refresh expires");
		assertNotNull(nordigenToken.getCreationDate(), "Null creation date");
		assertNotNull(nordigenToken.getRefreshDate(), "Null refresh date");
		assertNotEquals(originalRefreshDate, nordigenToken.getRefreshDate());
		assertNotEquals(nordigenToken.getCreationDate(), nordigenToken.getRefreshDate());
		assertNotEquals(originalAccess, nordigenToken.getAccess());
	}
	
	@Test
	void testGetAllInstitutions() {
		try {
			List<NordigenInstitution> allInstitutions = AonNordigen.getInstitutions(nordigenToken, null, null);
			assertTrue(allInstitutions != null && allInstitutions.size() > 2000);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testGetAllSpanishInstitutions() {
		try {
			List<NordigenInstitution> allInstitutions = AonNordigen.getInstitutions(nordigenToken, Country.ES, null);
			assertNotNull(allInstitutions);
			assertTrue(allInstitutions.stream().allMatch(inst -> inst.getCountries().contains(Country.ES)));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	void testGetCaixabankInstitution() {
		try {
			NordigenInstitution caixaBank = NordigenInstitutionUtils.getInstitution(nordigenToken, "CAIXABANK_CAIXESBB");
			assertNotNull(caixaBank);
			assertNotNull(caixaBank.getCountries(), "Null countries");
			assertNotNull(caixaBank.getId(), "Null ID");
			assertNotNull(caixaBank.getLogo(), "Null logo");
			assertNotNull(caixaBank.getName(),"Null name");
			assertNotNull(caixaBank.getTransactionTotalDays(), "Null transaction total days");
			assertEquals("CAIXESBB", caixaBank.getBic());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	void testCrdAgreements() {
		try {
			NordigenAgreement agreement = NordigenAgreementUtils.createAgreement(nordigenToken, "CAIXABANK_CAIXESBB");
			//COMENTADO PORQUE LA API NO OBTIENE LOS "SCOPES" CUANDO SE CREA
//			assertAgreement(agreement);
			Integer expectedHistoricalDays = 90;
			assertEquals(expectedHistoricalDays, agreement.getMaxHistoricalDays());
			NordigenAgreement gottenAgreement = NordigenAgreementUtils.getAgreement(nordigenToken, agreement.getId());
			assertAgreement(gottenAgreement);
			AonNordigen.deleteAgreement(nordigenToken, agreement);
			
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	void testCrdRequisitions() {
		try {
			NordigenAgreement agreement = NordigenAgreementUtils.createAgreement(nordigenToken, "CAIXABANK_CAIXESBB");
			NordigenRequisition requisition = AonNordigen.createRequisition(nordigenToken, agreement, "https://aonsolutions.org/");
			assertRequisition(requisition);
			AonNordigen.getRequisition(nordigenToken, requisition.getId());
			assertRequisition(requisition);
			NordigenRequisitionUtils.deleteRequisition(nordigenToken, requisition);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	void testAllRequisitionss() {
		List<NordigenRequisition> requisitions = NordigenRequisitionUtils.getAllRequisitions(nordigenToken);
		assertNotNull(requisitions);
		assertTrue(requisitions.size() > 0 );
	}
	
	
}
