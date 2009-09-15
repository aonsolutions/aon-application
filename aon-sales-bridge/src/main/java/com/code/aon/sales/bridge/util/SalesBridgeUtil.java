package com.code.aon.sales.bridge.util;

import java.util.Iterator;

import com.code.aon.commercial.Offer;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.customer.dao.ICustomerAlias;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.ql.Criteria;

public class SalesBridgeUtil {

	public Customer obtainCustomer(Offer offer) throws ManagerBeanException {
		IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(customerBean.getFieldName(ICustomerAlias.CUSTOMER_REGISTRY_ID), offer.getTarget().getRegistry().getId());
		Iterator<?> iterator = customerBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			return (Customer)iterator.next();
		} else {
			Customer customer = new Customer();
			customer.setRegistry(offer.getTarget().getRegistry());
			customer.setTariff(offer.getTariff());
			customer.setStatus(CustomerStatus.ACTIVE);
			customer.setScope(offer.getScope());
			return (Customer)customerBean.insert(customer);
		}
	}

}
