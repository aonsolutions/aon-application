package net.aonsolutions.aon.hibernateToOccam.registry;

import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.finance.enumeration.CreditorStatus;
import com.code.aon.supplier.enumeration.SupplierStatus;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;

public class OccamEnumerations {

	
	public static Country toOccamCountry(com.code.aon.common.enumeration.Country documentCountry) {
		return Country.safeValueOf( documentCountry.getValue() );
	}
	
	public static RegistryStatus customerStatusToRegistryStatus( CustomerStatus cs ) {
		if(cs== null) {
			return null;
		}
		return RegistryStatus.valueOf( cs.toString() );
	}
	
	public static RegistryStatus supplierStatusToRegistryStatus( SupplierStatus cs ) {
		if(cs== null) {
			return null;
		}
		return RegistryStatus.valueOf( cs.toString() );
	}
	
	public static RegistryStatus creditorStatusToRegistryStatus( CreditorStatus cs ) {
		if(cs== null) {
			return null;
		}
		return RegistryStatus.valueOf( cs.toString() );
	}
	
	
	
}
