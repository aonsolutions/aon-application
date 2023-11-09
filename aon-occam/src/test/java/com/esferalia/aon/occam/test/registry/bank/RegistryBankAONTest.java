package com.esferalia.aon.occam.test.registry.bank;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;

/**
 * Tests the methods of the class RegistryBankDAO
 */
public class RegistryBankAONTest extends AbstractOccamTest {
	
	/**
	 * Test create, update and delete
	 */
	@Test
	public void crudeTest() {
		// create
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		Domain domain = new Domain().setId(ctx.getDomainId()).setName(ctx.getDomainName());
		registryBank = AON.saveRegistryBank(domain, ctx.getDomainName(), registryBank);
		final Integer idInserted = registryBank.getId();
		
		RegistryBank inserted = AON.getRegistryBank(domain, ctx.getDomainName(), f -> f.getIdProperty().eq(idInserted));
		Asserts.assertEqualsRegistryBank(inserted, registryBank);
		
		// update
		registryBank.setActive(!registryBank.isActive());
		registryBank = AON.saveRegistryBank(domain, ctx.getDomainName(), registryBank);
		final Integer idUpdated = registryBank.getId();

		RegistryBank updated = AON.getRegistryBank(domain, ctx.getDomainName(), f -> f.getIdProperty().eq(idUpdated));
		Asserts.assertEqualsRegistryBank(updated, registryBank);
		
		// delete
		AON.deleteRegistryBank(domain, ctx.getDomainName(), idUpdated);
		RegistryBank deleted = AON.getRegistryBank(domain, ctx.getDomainName(), f -> f.getIdProperty().eq(idUpdated));
		assertTrue(deleted.isEmpty());
	}
	
	/**
	 * Test the method getRegistryBankStream if the filter is getIdProperty
	 */
	@Test
	public void getStreamIdPropertyTest() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		Domain domain = new Domain().setId(ctx.getDomainId()).setName(ctx.getDomainName());
		registryBank = AON.saveRegistryBank(domain, ctx.getDomainName(), registryBank);
		final Integer id = registryBank.getId();
				
		List<RegistryBank> registryBankList = AON.getRegistryBankStream(domain, ctx.getDomainName(), f -> f.getIdProperty().eq(id)).collect(Collectors.toList());
		registryBankList.forEach(f -> assertEquals(id, f.getId()));
		registryBankList.contains(registryBank);	
	}
	
	/**
	 * Test the method getRegistryBankStream if the filter is getDomainProperty
	 */
	@Test
	public void saveStreamDomainPropertyTest() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		Domain domain = new Domain().setId(ctx.getDomainId()).setName(ctx.getDomainName());
		registryBank = AON.saveRegistryBank(domain, ctx.getDomainName(), registryBank);
		Integer domainRegistryBank = registryBank.getDomain();
				
		List<RegistryBank> registryBankList = AON.getRegistryBankStream(domain, ctx.getDomainName(), f -> f.getDomainProperty().eq(domainRegistryBank)).collect(Collectors.toList());
		registryBankList.forEach(f -> assertEquals(domainRegistryBank, f.getDomain()));
		registryBankList.contains(registryBank);	
	}
	
	/**
	 * Test the method getRegistryBankStream if the filter is getRegistryProperty
	 */
	@Test
	public void saveStreamRegistryPropertyTest() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		Domain domain = new Domain().setId(ctx.getDomainId()).setName(ctx.getDomainName());
		registryBank = AON.saveRegistryBank(domain, ctx.getDomainName(), registryBank);
		Integer registry = registryBank.getRegistry();
				
		List<RegistryBank> registryBankList = AON.getRegistryBankStream(domain, ctx.getDomainName(), f -> f.getRegistryProperty().eq(registry)).collect(Collectors.toList());
		registryBankList.forEach(f -> assertEquals(registry, f.getRegistry()));
		registryBankList.contains(registryBank);	
	}
	
	/**
	 * Test the method getRegistryBankStream if the filter is getBankAccountProperty
	 */
	@Test
	public void saveStreamBankAccountPropertyTest() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		Domain domain = new Domain().setId(ctx.getDomainId()).setName(ctx.getDomainName());
		registryBank = AON.saveRegistryBank(domain, ctx.getDomainName(), registryBank);
		BankAccount bankAccount = registryBank.getBankAccount();
				
		List<RegistryBank> registryBankList = AON.getRegistryBankStream(domain, ctx.getDomainName(), f -> f.getBankAccountProperty().eq(bankAccount.getIban())).collect(Collectors.toList());
		registryBankList.forEach(f -> Asserts.assertEqualsBankAccount(bankAccount, f.getBankAccount()));
		registryBankList.contains(registryBank);	
	}
	
	/**
	 * Test the method getRegistryBankStream if the filter is getBicProperty
	 */
	@Test
	public void saveStreamBicPropertyTest() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		Domain domain = new Domain().setId(ctx.getDomainId()).setName(ctx.getDomainName());
		registryBank = AON.saveRegistryBank(domain, ctx.getDomainName(), registryBank);
		String bic = registryBank.getBic();
				
		List<RegistryBank> registryBankList = AON.getRegistryBankStream(domain, ctx.getDomainName(), f -> f.getBicProperty().eq(bic)).collect(Collectors.toList());
		registryBankList.forEach(f -> assertEquals(bic, f.getBic()));
		registryBankList.contains(registryBank);	
	}
	
	/**
	 * Test the method getRegistryBankStream if the filter is getSuffixProperty
	 */
	@Test
	public void saveStreamSuffixPropertyTest() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		Domain domain = new Domain().setId(ctx.getDomainId()).setName(ctx.getDomainName());
		registryBank = AON.saveRegistryBank(domain, ctx.getDomainName(), registryBank);
		String suffix = registryBank.getSuffix();
				
		List<RegistryBank> registryBankList = AON.getRegistryBankStream(domain, ctx.getDomainName(), f -> f.getSufixProperty().eq(suffix)).collect(Collectors.toList());
		registryBankList.forEach(f -> assertEquals(suffix, f.getSuffix()));
		registryBankList.contains(registryBank);	
	}
	
	/**
	 * Test the method getRegistryBankStream if the filter is getAliasProperty
	 */
	@Test
	public void saveStreamAliasPropertyTest() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		Domain domain = new Domain().setId(ctx.getDomainId()).setName(ctx.getDomainName());
		registryBank = AON.saveRegistryBank(domain, ctx.getDomainName(), registryBank);
		String alias = registryBank.getAlias();
				
		List<RegistryBank> registryBankList = AON.getRegistryBankStream(domain, ctx.getDomainName(), f -> f.getAliasProperty().eq(alias)).collect(Collectors.toList());
		registryBankList.forEach(f -> assertEquals(alias, f.getAlias()));
		registryBankList.contains(registryBank);	
	}
	
	/**
	 * Test the method getRegistryBankStream if the filter is getActiveProperty
	 */
	@Test
	public void saveStreamActivePropertyTest() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		Domain domain = new Domain().setId(ctx.getDomainId()).setName(ctx.getDomainName());
		registryBank = AON.saveRegistryBank(domain, ctx.getDomainName(), registryBank);
		Byte active = registryBank.getActive();
				
		List<RegistryBank> registryBankList = AON.getRegistryBankStream(domain, ctx.getDomainName(), f -> f.getActiveProperty().eq(active)).collect(Collectors.toList());
		registryBankList.forEach(f -> assertEquals(active, f.getActive()));
		registryBankList.contains(registryBank);	
	}
	
	/**
	 * Test the method getRegistryBankStream if the filter is getAccountProperty
	 */
	@Test
	public void saveStreamAccountPropertyTest() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		Domain domain = new Domain().setId(ctx.getDomainId()).setName(ctx.getDomainName());
		registryBank = AON.saveRegistryBank(domain, ctx.getDomainName(), registryBank);
		Account account = registryBank.getAccount();
				
		List<RegistryBank> registryBankList = AON.getRegistryBankStream(domain, ctx.getDomainName(), f -> f.getAccountProperty().eq(account.getId())).collect(Collectors.toList());
		registryBankList.forEach(f -> Asserts.assertEqualsAccount(account, f.getAccount()));
		registryBankList.contains(registryBank);	
	}
	
	/**
	 * Test the method getRegistryBankStream if the filter is getRequisitionProperty
	 */
	@Test
	public void saveStreamRequisitionPropertyTest() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		Domain domain = new Domain().setId(ctx.getDomainId()).setName(ctx.getDomainName());
		registryBank = AON.saveRegistryBank(domain, ctx.getDomainName(), registryBank);
		String requisition = registryBank.getRequisition();
				
		List<RegistryBank> registryBankList = AON.getRegistryBankStream(domain, ctx.getDomainName(), f -> f.getRequisitionProperty().eq(requisition)).collect(Collectors.toList());
		registryBankList.forEach(f -> assertEquals(requisition, f.getRequisition()));
		registryBankList.contains(registryBank);	
	}
	
	/**
	 * Test the method getRegistryBankStream if the filter is getSepaMandateRefProperty
	 */
	@Test
	public void saveStreamSepaMandateRefPropertyTest() {
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		Domain domain = new Domain().setId(ctx.getDomainId()).setName(ctx.getDomainName());
		registryBank = AON.saveRegistryBank(domain, ctx.getDomainName(), registryBank);
		String sepaMandateRef = registryBank.getSepaMandateRef();
				
		List<RegistryBank> registryBankList = AON.getRegistryBankStream(domain, ctx.getDomainName(), f -> f.getSepaMandateRefProperty().eq(sepaMandateRef)).collect(Collectors.toList());
		registryBankList.forEach(f -> assertEquals(sepaMandateRef, f.getSepaMandateRef()));
		registryBankList.contains(registryBank);	
	}
}
