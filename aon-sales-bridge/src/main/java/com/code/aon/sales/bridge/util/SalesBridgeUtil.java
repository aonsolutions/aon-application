package com.code.aon.sales.bridge.util;

import java.util.Iterator;

import com.code.aon.commercial.Offer;
import com.code.aon.commercial.Target;
import com.code.aon.commercial.enumeration.TargetStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class SalesBridgeUtil {

	public Customer obtainCustomer(Offer offer) throws ManagerBeanException {
		IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(customerBean.getFieldName(IEntityAlias.CUSTOMER_REGISTRY_ID), offer.getTarget().getRegistry().getId());
		Iterator<?> iterator = customerBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			return (Customer)iterator.next();
		} else {
			return createCustomer(offer.getTarget());
		}
	}

	public Customer createCustomer(Target target) throws ManagerBeanException {
		IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
		Customer customer = new Customer();
		customer.setRegistry(target.getRegistry());
		customer.setTariff((target.getTariff()!=null && target.getTariff().getId()!=null) ? target.getTariff() : null);
		customer.setSurcharge(target.isSurcharge());
		customer.setWithholding(target.isWithholding());
		customer.setTransaction(target.getTransaction());
		customer.setStatus((target.getStatus() == TargetStatus.ACTIVE) ? CustomerStatus.ACTIVE : CustomerStatus.INACTIVE);
		customer.setScope(target.getScope());
		return (Customer)customerBean.insert(customer);
	}

}
