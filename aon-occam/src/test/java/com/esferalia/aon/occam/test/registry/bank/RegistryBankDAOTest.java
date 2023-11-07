package com.esferalia.aon.occam.test.registry.bank;

import static com.esferalia.aon.jooq.tables.Rbank.RBANK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.Account;
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

	/**
	 * Test create, update and delete
	 */
	@Test
	public void crudeTest() {
		// create
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		registryBank = RegistryBankDAO.save(ctx, registryBank);
		Integer registryBankId = registryBank.getId();
		RegistryBank inserted = RegistryBankDAO.get(ctx, f -> f.getIdProperty().eq(registryBankId));
		Asserts.assertEqualsRegistryBank(registryBank, inserted);
		
		// update
		registryBank.setActive(!registryBank.isActive());
		registryBank = RegistryBankDAO.save(ctx, registryBank);
		RegistryBank updated = RegistryBankDAO.get(ctx, f -> f.getIdProperty().eq(registryBankId));
		Asserts.assertEqualsRegistryBank(registryBank, updated);
		
		// delete
		RegistryBankDAO.delete(ctx, registryBank.getId());
		RegistryBank deleted = RegistryBankDAO.get(ctx, f -> f.getIdProperty().eq(registryBankId));
		assertTrue(deleted.isEmpty());
	}
	
	/**
	 * Test that it throws an exception if we save a null registryBank
	 */
	@Test
	public void saveNullRegistryBank() {
		AonCoreException e = assertThrows(AonCoreException.class, () -> RegistryBankDAO.save(ctx, null));
		assertEquals(AonError.REGISTRY_BANK_NULL.getMessage(), e.getMessage());
	}
	
	/**
	 * Test that it throws an exception if we save an empty registryBank
	 */
	@Test
	public void saveEmptyRegistryBank() {
		RegistryBank registryBank = new RegistryBank();
		AonCoreException e = assertThrows(AonCoreException.class, () -> RegistryBankDAO.save(ctx, registryBank));
		assertEquals(AonError.REGISTRY_BANK_EMPTY.getMessage(), e.getMessage());
	}
	
	/**
	 * Test that it throws an exception if we save a null domain
	 */
	@Test
	public void saveNullDomainRegistryBank() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		registryBank.setDomain(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> RegistryBankDAO.save(ctx, registryBank));
		assertEquals(AonError.REGISTRY_BANK_NULL_DOMAIN.getMessage(), e.getMessage());
		RegistryBankDAO.delete(ctx, registryBank.getId());
	}
	
	/**
	 * Test that it throws an exception if we save a null registry
	 */
	@Test
	public void saveNullRegistryRegistryBank() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		registryBank.setRegistry(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> RegistryBankDAO.save(ctx, registryBank));
		assertEquals(AonError.REGISTRY_BANK_NULL_REGISTRY.getMessage(), e.getMessage());
		RegistryBankDAO.delete(ctx, registryBank.getId());
	}
	
	/**
	 * Test that it throws an exception if we save a null bank account
	 */
	@Test
	public void saveNullBankAccountRegistryBank() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		registryBank.setBankAccount(null);
		registryBank = RegistryBankDAO.save(ctx, registryBank);
		Asserts.assertEqualsBankAccount(new BankAccount(), registryBank.getBankAccount());
		RegistryBankDAO.delete(ctx, registryBank.getId());
	}
	
	/**
	 * Test that it throws an exception if we save a bic with and invalid size
	 */
	@Test
	public void saveInvalidBicSizeRegistryBank() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		String bic = AonStringUtils.repeat("h", RBANK.BIC.getDataType().length() + 1);
		registryBank.setBic(bic);
		AonCoreException e = assertThrows(AonCoreException.class, () -> RegistryBankDAO.save(ctx, registryBank));
		assertEquals(AonError.REGISTRY_BANK_INVALID_BIC_SIZE.getMessage(), e.getMessage());
		RegistryBankDAO.delete(ctx, registryBank.getId());
	}
	
	/**
	 * Test that it throws an exception if we save a suffix with and invalid size
	 */
	@Test
	public void saveInvalidSuffixSizeRegistryBank() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		String suffix = AonStringUtils.repeat("h", RBANK.SUFIX.getDataType().length() + 1);
		registryBank.setSuffix(suffix);
		AonCoreException e = assertThrows(AonCoreException.class, () -> RegistryBankDAO.save(ctx, registryBank));
		assertEquals(AonError.REGISTRY_BANK_INVALID_SUFFIX_SIZE.getMessage(), e.getMessage());
		RegistryBankDAO.delete(ctx, registryBank.getId());
	}
	
	/**
	 * Test that it throws an exception if we save a requisition with and invalid size
	 */
	@Test
	public void saveInvalidRequisitionSizeRegistryBank() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		String requisition = AonStringUtils.repeat("h", RBANK.REQUISITION.getDataType().length() + 1);
		registryBank.setRequisition(requisition);
		AonCoreException e = assertThrows(AonCoreException.class, () -> RegistryBankDAO.save(ctx, registryBank));
		assertEquals(AonError.REGISTRY_BANK_INVALID_REQUISITION_SIZE.getMessage(), e.getMessage());
		RegistryBankDAO.delete(ctx, registryBank.getId());
	}
	
	/**
	 * Test that it throws an exception if we save a SepaMandateRef with and invalid size
	 */
	@Test
	public void saveInvalidSepaMandateRefSizeRegistryBank() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		String sepaMandateRef = AonStringUtils.repeat("h", RBANK.SEPA_MANDATE_REF.getDataType().length() + 1);
		registryBank.setSepaMandateRef(sepaMandateRef);
		AonCoreException e = assertThrows(AonCoreException.class, () -> RegistryBankDAO.save(ctx, registryBank));
		assertEquals(AonError.REGISTRY_BANK_INVALID_SEPA_MANDATE_REF_SIZE.getMessage(), e.getMessage());
		RegistryBankDAO.delete(ctx, registryBank.getId());
	}
	
	/**
	 * Tests the method getByDomain
	 */
	@Test
	public void getByDomainRegistryBank() {
		RegistryBank registryBank1 = AonFaker.getRegistryBank(ctx);
		RegistryBank registryBank2 = AonFaker.getRegistryBank(ctx);
		Integer domain = registryBank1.getDomain();
		registryBank2.setDomain(domain);
		
		registryBank1 = RegistryBankDAO.save(ctx, registryBank1);
		registryBank2 = RegistryBankDAO.save(ctx, registryBank2);
		
		List<RegistryBank> registryBankList = RegistryBankDAO.getByDomain(ctx, domain).collect(Collectors.toList());

		registryBankList.forEach(f -> assertEquals(domain, f.getDomain()));
		registryBankList.contains(registryBank1);
		registryBankList.contains(registryBank2);
				
		RegistryBankDAO.delete(ctx, registryBank1.getId());
		RegistryBankDAO.delete(ctx, registryBank1.getId());
	}
	
	/**
	 * Tests the method getByRegistry
	 */
	@Test
	public void getByRegistryRegistryBank() {
		RegistryBank registryBank1 = AonFaker.getRegistryBank(ctx);
		RegistryBank registryBank2 = AonFaker.getRegistryBank(ctx);
		Integer registry = registryBank1.getRegistry();
		registryBank2.setRegistry(registry);
		
		registryBank1 = RegistryBankDAO.save(ctx, registryBank1);
		registryBank2 = RegistryBankDAO.save(ctx, registryBank2);
		
		List<RegistryBank> registryBankList = RegistryBankDAO.getByRegistry(ctx, registry).collect(Collectors.toList());
		
		registryBankList.forEach(f -> assertEquals(registry, f.getRegistry()));
		registryBankList.contains(registryBank1);
		registryBankList.contains(registryBank2);
		
		RegistryBankDAO.delete(ctx, registryBank1.getId());
		RegistryBankDAO.delete(ctx, registryBank2.getId());
	}
	
	/**
	 * Tests the method getByAccount
	 */
	@Test
	public void getByBankAccount() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		String bankAccount = registryBank.getBankAccount().getIban();
		registryBank = RegistryBankDAO.save(ctx, registryBank);
		
		List<RegistryBank> registryBankList = RegistryBankDAO.getByBankAccount(ctx, bankAccount).collect(Collectors.toList());
		
		registryBankList.forEach(f -> assertEquals(bankAccount, f.getBankAccount().getIban()));
		registryBankList.contains(registryBank);
		
		RegistryBankDAO.delete(ctx, registryBank.getId());
	}
	
	/**
	 * Tests the method getByBic
	 */
	@Test
	public void getByBicRegistryBank() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		String bic = registryBank.getBic();
		registryBank = RegistryBankDAO.save(ctx, registryBank);
		
		List<RegistryBank> registryBankList = RegistryBankDAO.getByBic(ctx, bic).collect(Collectors.toList());
		
		registryBankList.forEach(f -> assertEquals(bic, f.getBic()));
		registryBankList.contains(registryBank);
		
		RegistryBankDAO.delete(ctx, registryBank.getId());
	}
	
	/**
	 * Tests the method getBySuffix
	 */
	@Test
	public void getBySuffixRegistryBank() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		String suffix = registryBank.getSuffix();
		registryBank = RegistryBankDAO.save(ctx, registryBank);
		
		List<RegistryBank> registryBankList = RegistryBankDAO.getBySuffix(ctx, suffix).collect(Collectors.toList());
		
		registryBankList.forEach(f -> assertEquals(suffix, f.getSuffix()));
		registryBankList.contains(registryBank);
		
		RegistryBankDAO.delete(ctx, registryBank.getId());
	}
	
	/**
	 * Tests the method getByAlias
	 */
	@Test
	public void getByAliasRegistryBank() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		String alias = registryBank.getAlias();
		registryBank = RegistryBankDAO.save(ctx, registryBank);
		
		List<RegistryBank> registryBankList = RegistryBankDAO.getByAlias(ctx, alias).collect(Collectors.toList());
		
		registryBankList.forEach(f -> assertEquals(alias, f.getAlias()));
		registryBankList.contains(registryBank);
		
		RegistryBankDAO.delete(ctx, registryBank.getId());
	}
	
	/**
	 * Tests the method getByActive
	 */
	@Test
	public void getByActiveRegistryBank() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		Byte active = registryBank.getActive();
		registryBank = RegistryBankDAO.save(ctx, registryBank);
		
		List<RegistryBank> registryBankList = RegistryBankDAO.getByActive(ctx, active).collect(Collectors.toList());
		
		registryBankList.forEach(f -> assertEquals(active, f.getActive()));
		registryBankList.contains(registryBank);
		
		RegistryBankDAO.delete(ctx, registryBank.getId());
	}
	
	/**
	 * Tests the method getByAccount
	 */
	@Test
	public void getByAccountRegistryBank() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		Account account = registryBank.getAccount();
		registryBank = RegistryBankDAO.save(ctx, registryBank);
		
		List<RegistryBank> registryBankList = RegistryBankDAO.getByAccount(ctx, account.getId()).collect(Collectors.toList());
		
		registryBankList.forEach(f -> Asserts.assertEqualsAccount(account, f.getAccount()));
		registryBankList.contains(registryBank);
		
		RegistryBankDAO.delete(ctx, registryBank.getId());
	}
	
	/**
	 * Tests the method getByRequisition
	 */
	@Test
	public void getByRequisitionRegistryBank() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		String requisition = registryBank.getRequisition();
		registryBank = RegistryBankDAO.save(ctx, registryBank);
		
		List<RegistryBank> registryBankList = RegistryBankDAO.getByRequisition(ctx, requisition).collect(Collectors.toList());
		
		registryBankList.forEach(f -> assertEquals(requisition, f.getRequisition()));
		registryBankList.contains(registryBank);
		
		RegistryBankDAO.delete(ctx, registryBank.getId());
	}
	
	/**
	 * Tests the method getBySepaMandateRef
	 */
	@Test
	public void getSepaMandateRefFilterRegistryBank() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		String sepaMandateRef = registryBank.getSepaMandateRef();
		registryBank = RegistryBankDAO.save(ctx, registryBank);
		
		List<RegistryBank> registryBankList = RegistryBankDAO.getBySepaMandateRef(ctx, sepaMandateRef).collect(Collectors.toList());
		
		registryBankList.forEach(f -> assertEquals(sepaMandateRef, f.getSepaMandateRef()));
		registryBankList.contains(registryBank);
		
		RegistryBankDAO.delete(ctx, registryBank.getId());
	}
	
	/**
	 * Tests the method deleteByRegistry
	 */
	@Test
	public void deleteByRegistryRegistryBank() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		Integer registry = registryBank.getRegistry();
		RegistryBank inserted = RegistryBankDAO.save(ctx, registryBank);
		
		Asserts.assertEqualsRegistryBank(registryBank, inserted);
		
		RegistryBankDAO.deleteByRegistry(ctx, registry);
		RegistryBank deleted = RegistryBankDAO.get(ctx, inserted.getId());
		assertTrue(deleted.isEmpty());
	}
}



