package com.esferalia.aon.occam.test.registry.bank;

import static com.esferalia.aon.jooq.tables.Rbank.RBANK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.finance.BankAccount;
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
		registryBank = RegistryBankDAO.save(ctx, registryBank);
		Integer registryBankId = registryBank.getId();
		RegistryBank inserted = RegistryBankDAO.get(ctx, f -> f.getIdProperty().eq(registryBankId));
		Asserts.assertEqualsRegistryBank(registryBank, inserted);
		
		// update
		registryBank = RegistryBankDAO.save(ctx, registryBank);
		RegistryBank updated = RegistryBankDAO.get(ctx, f -> f.getIdProperty().eq(registryBankId));
		Asserts.assertEqualsRegistryBank(registryBank, updated);
		
		// delete
		RegistryBankDAO.delete(ctx, registryBank.getId());
		RegistryBank deleted = RegistryBankDAO.get(ctx, f -> f.getIdProperty().eq(registryBankId));
		assertTrue(deleted.isEmpty());
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
		RegistryBankDAO.delete(ctx, registryBank.getId());
	}
	
	@Test
	public void saveNullRegistryRegistryBank() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		registryBank.setRegistry(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> RegistryBankDAO.save(ctx, registryBank));
		assertEquals(AonError.REGISTRY_BANK_NULL_REGISTRY.getMessage(), e.getMessage());
		RegistryBankDAO.delete(ctx, registryBank.getId());
	}
	
	@Test
	public void saveNullBankAccountRegistryBank() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		registryBank.setBankAccount(null);
		registryBank = RegistryBankDAO.save(ctx, registryBank);
		Asserts.assertEqualsBankAccount(new BankAccount(), registryBank.getBankAccount());
		RegistryBankDAO.delete(ctx, registryBank.getId());
	}
	
	@Test
	public void saveInvalidBicSizeRegistryBank() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		String bic = AonStringUtils.repeat("h", RBANK.BIC.getDataType().length() + 1);
		registryBank.setBic(bic);
		AonCoreException e = assertThrows(AonCoreException.class, () -> RegistryBankDAO.save(ctx, registryBank));
		assertEquals(AonError.REGISTRY_BANK_INVALID_BIC_SIZE.getMessage(), e.getMessage());
		RegistryBankDAO.delete(ctx, registryBank.getId());
	}
	
	@Test
	public void saveInvalidSuffixSizeRegistryBank() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		String suffix = AonStringUtils.repeat("h", RBANK.SUFIX.getDataType().length() + 1);
		registryBank.setSuffix(suffix);
		AonCoreException e = assertThrows(AonCoreException.class, () -> RegistryBankDAO.save(ctx, registryBank));
		assertEquals(AonError.REGISTRY_BANK_INVALID_SUFFIX_SIZE.getMessage(), e.getMessage());
		RegistryBankDAO.delete(ctx, registryBank.getId());
	}
	
	@Test
	public void saveInvalidRequisitionSizeRegistryBank() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		String requisition = AonStringUtils.repeat("h", RBANK.REQUISITION.getDataType().length() + 1);
		registryBank.setRequisition(requisition);
		AonCoreException e = assertThrows(AonCoreException.class, () -> RegistryBankDAO.save(ctx, registryBank));
		assertEquals(AonError.REGISTRY_BANK_INVALID_REQUISITION_SIZE.getMessage(), e.getMessage());
		RegistryBankDAO.delete(ctx, registryBank.getId());
	}
	
	@Test
	public void saveInvalidSepaMandateRefSizeRegistryBank() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		String sepaMandateRef = AonStringUtils.repeat("h", RBANK.SEPA_MANDATE_REF.getDataType().length() + 1);
		registryBank.setSepaMandateRef(sepaMandateRef);
		AonCoreException e = assertThrows(AonCoreException.class, () -> RegistryBankDAO.save(ctx, registryBank));
		assertEquals(AonError.REGISTRY_BANK_INVALID_SEPA_MANDATE_REF_SIZE.getMessage(), e.getMessage());
		RegistryBankDAO.delete(ctx, registryBank.getId());
	}
	
	@Test
	public void getDomainFilterRegistryBank() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		int domain = registryBank.getDomain();
		registryBank = RegistryBankDAO.save(ctx, registryBank);
		
		RegistryBank inserted = RegistryBankDAO.get(ctx, f -> f.getDomainProperty().eq(domain));
		assertEquals(registryBank.getDomain(), inserted.getDomain());
		RegistryBankDAO.delete(ctx, registryBank.getId());
	}
	
	@Test
	public void getRegistryFilterRegistryBank() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		int registry = registryBank.getRegistry();
		registryBank = RegistryBankDAO.save(ctx, registryBank);
		
		RegistryBank inserted = RegistryBankDAO.get(ctx, f -> f.getRegistryProperty().eq(registry));
		assertEquals(registryBank.getRegistry(), inserted.getRegistry());
		RegistryBankDAO.delete(ctx, registryBank.getId());
	}
	
	@Test
	public void getBankAccountFilterRegistryBank() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		String bankAccount = registryBank.getBankAccount().getIban();
		registryBank = RegistryBankDAO.save(ctx, registryBank);
		
		RegistryBank inserted = RegistryBankDAO.get(ctx, f -> f.getBankAccountProperty().eq(bankAccount));
		Asserts.assertEqualsBankAccount(registryBank.getBankAccount(), inserted.getBankAccount());
		RegistryBankDAO.delete(ctx, registryBank.getId());
	}
	
	@Test
	public void getBicFilterRegistryBank() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		String bic = registryBank.getBic();
		registryBank = RegistryBankDAO.save(ctx, registryBank);
		
		RegistryBank inserted = RegistryBankDAO.get(ctx, f -> f.getBicProperty().eq(bic));
		assertEquals(registryBank.getBic(), inserted.getBic());
		RegistryBankDAO.delete(ctx, registryBank.getId());
	}
	
	@Test
	public void getSuffixFilterRegistryBank() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		String suffix = registryBank.getSuffix();
		registryBank = RegistryBankDAO.save(ctx, registryBank);
		
		RegistryBank inserted = RegistryBankDAO.get(ctx, f -> f.getSufixProperty().eq(suffix));
		assertEquals(registryBank.getSuffix(), inserted.getSuffix());
		RegistryBankDAO.delete(ctx, registryBank.getId());
	}
	
	@Test
	public void getAliasFilterRegistryBank() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		String alias = registryBank.getAlias();
		registryBank = RegistryBankDAO.save(ctx, registryBank);
		
		RegistryBank inserted = RegistryBankDAO.get(ctx, f -> f.getAliasProperty().eq(alias));
		assertEquals(registryBank.getAlias(), inserted.getAlias());
		RegistryBankDAO.delete(ctx, registryBank.getId());
	}
	
	@Test
	public void getActiveFilterRegistryBank() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		Byte active = registryBank.getActive();
		registryBank = RegistryBankDAO.save(ctx, registryBank);
		
		RegistryBank inserted = RegistryBankDAO.get(ctx, f -> f.getActiveProperty().eq(active));
		assertEquals(registryBank.getActive(), inserted.getActive());
		RegistryBankDAO.delete(ctx, registryBank.getId());
	}
	
	@Test
	public void getAccountFilterRegistryBank() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		int account = registryBank.getAccount().getId();
		registryBank = RegistryBankDAO.save(ctx, registryBank);
		
		RegistryBank inserted = RegistryBankDAO.get(ctx, f -> f.getAccountProperty().eq(account));
		Asserts.assertEqualsAccount(registryBank.getAccount(), inserted.getAccount());
		RegistryBankDAO.delete(ctx, registryBank.getId());
	}
	
	@Test
	public void getRequisitionFilterRegistryBank() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		String requisition = registryBank.getRequisition();
		registryBank = RegistryBankDAO.save(ctx, registryBank);
		
		RegistryBank inserted = RegistryBankDAO.get(ctx, f -> f.getRequisitionProperty().eq(requisition));
		assertEquals(registryBank.getRequisition(), inserted.getRequisition());
		RegistryBankDAO.delete(ctx, registryBank.getId());
	}
	
	@Test
	public void getSepaMandateRefFilterRegistryBank() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		String sepaMandateRef = registryBank.getSepaMandateRef();
		registryBank = RegistryBankDAO.save(ctx, registryBank);
		
		RegistryBank inserted = RegistryBankDAO.get(ctx, f -> f.getSepaMandateRefProperty().eq(sepaMandateRef));
		assertEquals(registryBank.getSepaMandateRef(), inserted.getSepaMandateRef());
		RegistryBankDAO.delete(ctx, registryBank.getId());
	}
}



