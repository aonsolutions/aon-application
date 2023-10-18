package com.esferalia.aon.occam.test.registry.bank;

import static com.esferalia.aon.jooq.tables.Rbank.RBANK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryBankDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

/**
 * Tests the methods of the class RegistryBankDAO
 */
public class RegistryBankDAOTest extends AbstractOccamTest {
	
	@Test
	public void crudeTest() {
		// create
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		RegistryBank registryBankreceived = RegistryBankDAO.save(ctx, registryBank);
		Integer registryBankId = registryBankreceived.getId();
		RegistryBank inserted = RegistryBankDAO.get(ctx, f -> f.getIdProperty().eq(registryBankId));
		
		//assertNotNull(inserted.getId());
		//assertNull(inserted.getDomain());
		//assertNull(inserted.getBic());
		
		Asserts.assertEqualsRegistryBank(registryBank, registryBankreceived);
		Asserts.assertEqualsRegistryBank(registryBank, inserted);
		
		// update
		registryBank = RegistryBankDAO.save(ctx, registryBank);
		RegistryBank updated = RegistryBankDAO.get(ctx, f -> f.getIdProperty().eq(registryBankId));
		Asserts.assertEqualsRegistryBank(registryBank, updated);
		
		// delete
		RegistryBankDAO.delete(ctx, registryBank.getId());
		RegistryBank deleted = RegistryBankDAO.get(ctx, f -> f.getIdProperty().eq(registryBankId));
		assertNull(deleted);
	}
	
	@Test
	public void saveNullRegistryBank() {
		AonCoreException e = assertThrows(AonCoreException.class, () -> RegistryBankDAO.save(ctx, null));
		assertEquals(AonError.REGISTRY_BANK_NULL.getMessage(), e.getMessage());
	}
	
	@Test
	public void saveEmptyRegistryBank() {
		RegistryBank registryBank = new RegistryBank();
		AonCoreException e = assertThrows(AonCoreException.class, () -> RegistryBankDAO.save(ctx, registryBank));
		assertEquals(AonError.REGISTRY_BANK_EMPTY.getMessage(), e.getMessage());
	}
	
	@Test
	public void saveNullDomainRegistryBank() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		registryBank.setDomain(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> RegistryBankDAO.save(ctx, registryBank));
		assertEquals(AonError.REGISTRY_BANK_NULL_DOMAIN.getMessage(), e.getMessage());
	}
	
	@Test
	public void saveNullRegistryRegistryBank() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		registryBank.setRegistry(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> RegistryBankDAO.save(ctx, registryBank));
		assertEquals(AonError.REGISTRY_BANK_NULL_REGISTRY.getMessage(), e.getMessage());
	}
	
	@Test
	public void saveNullBankAccountRegistryBank() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		registryBank.setBankAccount(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> RegistryBankDAO.save(ctx, registryBank));
		assertEquals(AonError.REGISTRY_BANK_NULL_BANK_ACCOUNT.getMessage(), e.getMessage());
	}
	
	@Test
	public void saveInvalidBicSizeRegistryBank() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		String bic = AonStringUtils.repeat("h", RBANK.BIC.getDataType().length() + 1);
		registryBank.setBic(bic);
		AonCoreException e = assertThrows(AonCoreException.class, () -> RegistryBankDAO.save(ctx, registryBank));
		assertEquals(AonError.REGISTRY_BANK_INVALID_BIC_SIZE.getMessage(), e.getMessage());
	}
	
	@Test
	public void saveInvalidSuffixSizeRegistryBank() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		String suffix = AonStringUtils.repeat("h", RBANK.SUFIX.getDataType().length() + 1);
		registryBank.setSuffix(suffix);
		AonCoreException e = assertThrows(AonCoreException.class, () -> RegistryBankDAO.save(ctx, registryBank));
		assertEquals(AonError.REGISTRY_BANK_INVALID_SUFFIX_SIZE.getMessage(), e.getMessage());
	}
	
	@Test
	public void saveInvalidRequisitionSizeRegistryBank() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		String requisition = AonStringUtils.repeat("h", RBANK.REQUISITION.getDataType().length() + 1);
		registryBank.setRequisition(requisition);
		AonCoreException e = assertThrows(AonCoreException.class, () -> RegistryBankDAO.save(ctx, registryBank));
		assertEquals(AonError.REGISTRY_BANK_INVALID_REQUISITION_SIZE.getMessage(), e.getMessage());
	}
	
	@Test
	public void saveInvalidSepaMandateRefSizeRegistryBank() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		String sepaMandateRef = AonStringUtils.repeat("h", RBANK.SEPA_MANDATE_REF.getDataType().length() + 1);
		registryBank.setSepaMandateRef(sepaMandateRef);
		AonCoreException e = assertThrows(AonCoreException.class, () -> RegistryBankDAO.save(ctx, registryBank));
		assertEquals(AonError.REGISTRY_BANK_INVALID_SEPA_MANDATE_REF_SIZE.getMessage(), e.getMessage());
	}
}



