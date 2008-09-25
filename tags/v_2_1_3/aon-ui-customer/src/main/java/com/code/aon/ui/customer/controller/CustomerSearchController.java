package com.code.aon.ui.customer.controller;

import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ILookupObject;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.form.SelectWindowController;

/**
 * Controller used in the select window of <code>Customer</code>.
 */
public class CustomerSearchController extends SelectWindowController {

	/** The LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(CustomerController.class.getName());
	
	/** REGISTRY_ADDRESS_ID. */
	private static final String REGISTRY_ADDRESS_ID = "raddress_id";
	
	/** REGISTRY_ADDRESS. */
	private static final String REGISTRY_ADDRESS = "raddress";
	
	/** REGISTRY_ADDRESS_CITY. */
	private static final String REGISTRY_ADDRESS_CITY = "city";

	/** REGISTRY_ADDRESS_INFO. */
	private static final String REGISTRY_ADDRESS_INFO = "raddress_info";

	/**
	 * Adds custom entries into the lookup map.
	 * 
	 * @param ito the ILookupObject
	 * @param map the map
	 */
    @Override
	protected void customizeLookupMap(ILookupObject ito, Map<String, Object> map) {
		try {
			IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_REGISTRY_ID),map.get("Customer_id"));
            criteria.addOrder(rAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_ADDRESS_TYPE));
            Iterator iter = rAddressBean.getList(criteria).iterator();
			if(iter.hasNext()){
				RegistryAddress rAddress = (RegistryAddress)iter.next();
				map.put(REGISTRY_ADDRESS_ID, rAddress.getId());
				map.put(REGISTRY_ADDRESS,((rAddress.getAddress() != null)?rAddress.getAddress():"") + " " + ((rAddress.getAddress2()!= null)?rAddress.getAddress2():"") + " " + ((rAddress.getAddress3()!=null)?rAddress.getAddress3():"") );
				map.put(REGISTRY_ADDRESS_CITY, rAddress.getCity() + " " + rAddress.getZip());
				map.put(REGISTRY_ADDRESS_INFO, ((rAddress.getZip() != null) ? rAddress.getZip() + " ": "") + ((rAddress.getCity() != null) ? rAddress.getCity() + " ": "") + ((rAddress.getGeozone().getName() != null) ? rAddress.getGeozone().getName() + " " : ""));
			}else{
				map.put(REGISTRY_ADDRESS_ID, null);
				map.put(REGISTRY_ADDRESS, "" );
				map.put(REGISTRY_ADDRESS_CITY, "");
				map.put(REGISTRY_ADDRESS_INFO, "");
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error customizing lookup map",e);
		}
	}
}