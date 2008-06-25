package com.code.aon.ui.supplier.controller;

import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.form.LookupController;

public class SupplierLookupController extends LookupController {
	
	private static final Logger LOGGER = Logger.getLogger(SupplierLookupController.class.getName());

	/** REGISTRY_ADDRESS. */
	private static final String REGISTRY_ADDRESS = "raddress";
	
	/** REGISTRY_ADDRESS_INFO. */
	private static final String REGISTRY_ADDRESS_INFO = "raddress_info";


	/** 
	 * The Constant SUPPLIER_FULL_NAME used to retrieve the complete name of the supplier 
	 * using the lookup. 
	 */
	private static final String SUPPLIER_FULL_NAME = "Supplier_full_name";
	
	@Override
	protected void customizeLookupMap(ITransferObject to, Map<String, Object> map) {
		try {
			Supplier supplier = (Supplier)to;
			RegistryAddress rAddress = obtainRAddress((Integer)map.get("Supplier_id"));
			map.put(SUPPLIER_FULL_NAME, supplier.getRegistry().getName() + " " + ((supplier.getRegistry().getSurname() == null) ? "" : supplier.getRegistry().getSurname()) );
			if(rAddress != null){
				map.put(REGISTRY_ADDRESS,((rAddress.getAddress() != null)?rAddress.getAddress():""));
				map.put(REGISTRY_ADDRESS_INFO, ((rAddress.getZip() != null) ? rAddress.getZip() + " ": "") + ((rAddress.getCity() != null) ? rAddress.getCity() + " ": "") + ((rAddress.getGeozone().getName() != null) ? rAddress.getGeozone().getName() + " " : ""));
			}else{
				map.put(REGISTRY_ADDRESS, "" );
				map.put(REGISTRY_ADDRESS_INFO, "");
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error customizing lookup map", e);
		}
	}

	@SuppressWarnings("unchecked")
	private RegistryAddress obtainRAddress(Integer id) throws ManagerBeanException {
		IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_REGISTRY_ID),id);
		Iterator iter = rAddressBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return (RegistryAddress)iter.next();
		}
		return null;
	}
}
