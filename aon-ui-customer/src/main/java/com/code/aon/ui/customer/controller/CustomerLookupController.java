package com.code.aon.ui.customer.controller;

import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.form.LookupController;

public class CustomerLookupController extends LookupController {

	/** The LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(CustomerLookupController.class.getName());

	/** The Constant SUPPLIER_FULL_NAME used to retrieve the complete name of the supplier using the lookup. */
	private static final String CUSTOMER_FULL_NAME = "Customer_full_name";

	/** REGISTRY_ADDRESS_ID. */
	private static final String REGISTRY_ADDRESS_ID = "raddress_id";

	/** REGISTRY_ADDRESS. */
	private static final String REGISTRY_ADDRESS = "raddress";

	/** REGISTRY_ADDRESS_CITY. */
	private static final String REGISTRY_ADDRESS_CITY = "city";
	
	/** REGISTRY_ADDRESS_INFO. */
	private static final String REGISTRY_ADDRESS_INFO = "raddress_info";


	@Override
	protected void customizeLookupMap(ITransferObject to, Map<String, Object> map) {
		Customer customer = (Customer)to;
		map.put(CUSTOMER_FULL_NAME, customer.getRegistry().getName() + " " + ((customer.getRegistry().getSurname() == null) ? "" : customer.getRegistry().getSurname()) );

		try {
			IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_REGISTRY_ID),map.get("Customer_id"));
			Iterator iter = rAddressBean.getList(criteria).iterator();
			if (iter.hasNext()) {
				RegistryAddress rAddress = (RegistryAddress) iter.next();
				map.put(REGISTRY_ADDRESS_ID, rAddress.getId());
				map.put(REGISTRY_ADDRESS,((rAddress.getAddress() != null) ? rAddress.getAddress() : ""));
				map.put(REGISTRY_ADDRESS_CITY, rAddress.getCity());
				StringBuilder sb = new StringBuilder();
				sb.append((rAddress.getZip() != null) ? rAddress.getZip() + " ": "");
				sb.append((rAddress.getCity() != null) ? rAddress.getCity() + " ": "");
				sb.append((rAddress.getGeozone()!= null && rAddress.getGeozone().getName() != null) ? rAddress.getGeozone().getName() + " " : "");
				map.put(REGISTRY_ADDRESS_INFO, sb.toString());
			} else {
				map.put(REGISTRY_ADDRESS_ID, null);
				map.put(REGISTRY_ADDRESS, "");
				map.put(REGISTRY_ADDRESS_CITY, "");
				map.put(REGISTRY_ADDRESS_INFO, "");
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error customizing lookup map", e);
		}
	}
}
