package com.esferalia.aon.occam.test.registry.address;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryAddressDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.occam.test.faker.InvoiceFaker;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;


public class ValidationDeleteForeignKey extends AbstractOccamTest {
	
	@Test
	public void test() {
		
		Customer c  = AonRandom.getCustomer(ctx);
		
		if (c == null) {
			c = AonFaker.getCustomer(ctx);
			c = CustomerDAO.save(ctx, c);
		}
		
		CustomerFull customer = CustomerDAO.getFull(ctx, c.getId());
		
		if (!customer.hasAddresses()) {
			RegistryAddress registryAddress = AonFaker.getRegistryAddress(ctx);
			registryAddress.setRegistry(customer.getRegistry().getId());
			
			if (customer.getMainAddress() == null) {
				registryAddress.setMain(true);
			}
			
			registryAddress = RegistryAddressDAO.save(ctx, registryAddress);
			
			customer.addAddress(registryAddress);
			customer = CustomerDAO.save(ctx, customer);
		}
		
		RegistryAddress registryAddress = customer.getMainAddress(); 
		
		if (registryAddress != null) {
			Invoice invoice = InvoiceFaker.getSalesNational(ctx);
			invoice.setRegistry(customer.getRegistry().getId());
			invoice.setRegistryName(customer.getRegistry().getName());
			invoice.setRegistryDocument(customer.getRegistry().getDocument());
			invoice.setRegistryDocumentCountry(customer.getRegistry().getDocumentCountry());
			invoice.setRegistryDocumentType(customer.getRegistry().getDocumentType());
			invoice.setAddress(registryAddress);
			invoice.setRegistryAddress(registryAddress.getId());
			
			AON.insertInvoice(DOMAIN_NAME, DOMAIN_ID, USER, invoice);
			
			Integer addressId = registryAddress.getId();
			AonCoreException e = assertThrows(AonCoreException.class, () -> RegistryAddressDAO.delete(ctx, addressId));
			assertEquals(AonError.DELETE_RADDRESS_INVOICE.getMessage(), e.getMessage());
		}
	}
}