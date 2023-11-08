package com.esferalia.aon.occam.test.registry.address;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Ignore;
import org.junit.Test;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryAddressDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.github.javafaker.Faker;


public class ValidationDeleteForeignKey extends AbstractOccamTest {
	
	@Ignore
	@Test
	public void test() {
		Registry registry = AonRandom.getRegistry(ctx);
		RegistryAddress registryAddress = AonFaker.getRegistryAddress(ctx);
		registryAddress.setRegistry(registry.getId());
		
		Scope scope = new Scope()
			.setId(AonRandom.integer(0, 10))
			.setDomain(ctx.getDomainId())
			.setDescription(Faker.instance().letterify("????????"));
		
		Invoice invoice = new Invoice()
			.setDomain(ctx.getDomainId())
			.setAddress(registryAddress)
			.setIssueDate(AonRandom.getPastDate(0))
			.setType(InvoiceType.SALES)
			.setRegistry(registry.getId())
			.setScope(scope)
			.setTransaction(AonRandom.getRandomInvoiceTransactionType());
		
		RegistryAddressDAO.save(ctx, registryAddress);
		InvoiceDAO.save(ctx, invoice);
		
		AonCoreException e = assertThrows(AonCoreException.class, () -> RegistryAddressDAO.delete(ctx, registryAddress.getId()));
		
		assertEquals(AonError.DELETE_RADDRESS_INVOICE.getMessage(), e.getMessage());
	}
}
