package com.code.aon.ui.customer.servlet;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ILookupObject;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.customer.dao.ICustomerAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.common.lookup.LookupServlet;

/**
 * Servlet used with the lookup of <code>Customer</code>.
 */
public class CustomerLookupServlet extends LookupServlet {

	/** The LOGGER. */
	private static final Logger LOGGER = Logger
			.getLogger(CustomerLookupServlet.class.getName());

	/** IDS_PARAMETER. */
	private static final String IDS_PARAMETER = "ids";
	
	/** REGISTRY_ADDRESS_ID. */
	private static final String REGISTRY_ADDRESS_ID = "raddress_id";

	/** REGISTRY_ADDRESS. */
	private static final String REGISTRY_ADDRESS = "raddress";

	/** REGISTRY_ADDRESS_CITY. */
	private static final String REGISTRY_ADDRESS_CITY = "city";
	
	/** REGISTRY_ADDRESS_INFO. */
	private static final String REGISTRY_ADDRESS_INFO = "raddress_info";

	/**
	 * Do get. Retrieves the data required by the lookup from the database
	 * 
	 * @param req the request
	 * @param res the response
	 * 
	 * @throws IOException the IO exception
	 * @throws ServletException the servlet exception
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		String ids = req.getParameter(IDS_PARAMETER);
		Integer integer = isInteger(req.getParameter(VALUE_PARAMETER));
		List<ITransferObject> list = new LinkedList<ITransferObject>();
		Criteria criteria;
		try {
			IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
			if (integer != null) {
				criteria = new Criteria();
				criteria.addEqualExpression(customerBean.getFieldName(ICustomerAlias.CUSTOMER_ID), integer);
				list = customerBean.getList(criteria);
				if (list.size() > 0) {
					ILookupObject ito = (ILookupObject) list.get(0);
					renderResponse(res, ito, ids);
				}
				if (list.size() == 0) {
					criteria = new Criteria();
					criteria.addExpression(customerBean.getFieldName(ICustomerAlias.CUSTOMER_REGISTRY_ALIAS),req.getParameter(VALUE_PARAMETER));
					list = customerBean.getList(criteria);
					if (list.size() > 0) {
						ILookupObject ito = (ILookupObject) list.get(0);
						renderResponse(res, ito, ids);
					}
				}
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
			throw new ServletException(e.getMessage(), e);
		} catch (ExpressionException e) {
			e.printStackTrace();
			throw new ServletException(e.getMessage(), e);
		}
	}

	/**
	 * Checks if a String is an integer.
	 * 
	 * @param parameter the parameter
	 * 
	 * @return the integer
	 */
	private Integer isInteger(String parameter) {
		try {
			return Integer.valueOf(parameter);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * Adds custom entries into the lookup map.
	 * 
	 * @param ito the ito
	 * @param map the map
	 */
	@Override
	protected void customizeLookupMap(ILookupObject ito, Map<String, Object> map) {
		try {
			IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_REGISTRY_ID),map.get("Customer_Lookup_id"));
			Iterator iter = rAddressBean.getList(criteria).iterator();
			if (iter.hasNext()) {
				RegistryAddress rAddress = (RegistryAddress) iter.next();
				map.put(REGISTRY_ADDRESS_ID, rAddress.getId());
				map.put(REGISTRY_ADDRESS,((rAddress.getAddress() != null) ? rAddress.getAddress() : ""));
				map.put(REGISTRY_ADDRESS_CITY, rAddress.getCity());
				map.put(REGISTRY_ADDRESS_INFO, ((rAddress.getZip() != null) ? rAddress.getZip() + " ": "") + ((rAddress.getCity() != null) ? rAddress.getCity() + " ": "") + ((rAddress.getGeozone().getName() != null) ? rAddress.getGeozone().getName() + " " : ""));
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