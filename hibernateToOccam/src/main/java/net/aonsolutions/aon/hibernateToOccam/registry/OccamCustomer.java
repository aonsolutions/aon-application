package net.aonsolutions.aon.hibernateToOccam.registry;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.type.MediaType;

public class OccamCustomer {

	public static CustomerFull from (Customer customer) {
		CustomerFull customerFull = new CustomerFull();
		com.esferalia.aon.occam.api.model.Customer occamCustomer = new com.esferalia.aon.occam.api.model.Customer();
		
		occamCustomer.setDocument(customer.getRegistry().getDocument());
		occamCustomer.setAlias(customer.getRegistry().getAlias());
		occamCustomer.setDocumentCountry(OccamEnumerations.toOccamCountry(customer.getRegistry().getDocumentCountry()));
		occamCustomer.setId(customer.getRegistry().getId());
		occamCustomer.setName(customer.getRegistry().getName());
		customerFull.setRegistry(occamCustomer);
		
		try {
			com.code.aon.registry.RegistryMedia phone = customer.getRegistry().getPhone();
			if (phone != null) {
				customerFull.addMedia(new RegistryMedia().setMedia(MediaType.FIXED_PHONE).setValue(phone.getValue()));
			}
		} catch (ManagerBeanException e) {
			// Sin telefono
		}
		
		occamCustomer.setStatus(OccamEnumerations.customerStatusToRegistryStatus(customer.getStatus()));
		
		return customerFull;
	}
	
}
