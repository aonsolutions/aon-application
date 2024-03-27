package net.aonsolutions.aon.hibernateToOccam.registry;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.supplier.Supplier;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.SupplierFull;
import com.esferalia.aon.occam.api.model.type.MediaType;

public class OccamSupplier {

	
	public static SupplierFull from (Supplier supplier) {
		SupplierFull supplierFull = new SupplierFull();
		com.esferalia.aon.occam.api.model.registry.Supplier occamSupplier = new com.esferalia.aon.occam.api.model.registry.Supplier();
		
		occamSupplier.setDocument(supplier.getRegistry().getDocument());
		occamSupplier.setAlias(supplier.getRegistry().getAlias());
		occamSupplier.setDocumentCountry(OccamEnumerations.toOccamCountry(supplier.getRegistry().getDocumentCountry()));
		occamSupplier.setId(supplier.getRegistry().getId());
		occamSupplier.setName(supplier.getRegistry().getName());
		supplierFull.setRegistry(occamSupplier);
		try {
			com.code.aon.registry.RegistryMedia phone = supplier.getRegistry().getPhone();
			if (phone != null) {
				supplierFull.addMedia(new RegistryMedia().setMedia(MediaType.FIXED_PHONE).setValue(phone.getValue()));
			}
		} catch (ManagerBeanException e) {
			// Sin telefono
		}
		
		occamSupplier.setStatus(OccamEnumerations.supplierStatusToRegistryStatus(supplier.getStatus()));
		
		return supplierFull;

	}
	
	
}
