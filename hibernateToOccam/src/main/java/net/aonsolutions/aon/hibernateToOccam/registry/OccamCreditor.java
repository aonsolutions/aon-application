package net.aonsolutions.aon.hibernateToOccam.registry;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Creditor;
import com.esferalia.aon.occam.api.model.registry.CreditorFull;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.type.MediaType;

public class OccamCreditor {

	
	public static CreditorFull from(Creditor creditor) {
		CreditorFull creditorFull = new CreditorFull();
		com.esferalia.aon.occam.api.model.registry.Creditor occamCreditor = new com.esferalia.aon.occam.api.model.registry.Creditor();
		
		occamCreditor.setDocument(creditor.getRegistry().getDocument());
		occamCreditor.setAlias(creditor.getRegistry().getAlias());
		occamCreditor.setDocumentCountry(OccamEnumerations.toOccamCountry(creditor.getRegistry().getDocumentCountry()));
		occamCreditor.setId(creditor.getRegistry().getId());
		occamCreditor.setName(creditor.getRegistry().getName());
		creditorFull.setRegistry(occamCreditor);
		
		try {
			com.code.aon.registry.RegistryMedia phone = creditor.getRegistry().getPhone();
			if (phone != null) {
				creditorFull.addMedia(new RegistryMedia().setMedia(MediaType.FIXED_PHONE).setValue(phone.getValue()));
			}
		} catch (ManagerBeanException e) {
			// Sin telefono
		}
		
		occamCreditor.setStatus(OccamEnumerations.creditorStatusToRegistryStatus(creditor.getStatus()));
		
		return creditorFull;

	}
	
	
}
