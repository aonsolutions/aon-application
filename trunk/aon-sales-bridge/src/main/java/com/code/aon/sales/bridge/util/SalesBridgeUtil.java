package com.code.aon.sales.bridge.util;

import java.util.Iterator;

import com.code.aon.commercial.Offer;
import com.code.aon.commercial.Target;
import com.code.aon.commercial.enumeration.TargetStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.customer.dao.ICustomerAlias;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.RegistryPayMethod;

public class SalesBridgeUtil {

	public Customer obtainCustomer(Offer offer) throws ManagerBeanException {
		IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(customerBean.getFieldName(ICustomerAlias.CUSTOMER_REGISTRY_ID), offer.getTarget().getRegistry().getId());
		Iterator<?> iterator = customerBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			return (Customer)iterator.next();
		} else {
			RegistryBank rBank = null;
			if (offer.getBank() != null && offer.getBank().getId() != null) {
				rBank = createRegistryBank(offer);
			}
			if (offer.getPayMethod() != null && offer.getPayMethod().getId() != null) {
				createRegistryPayMethod(offer, rBank);
			}
			if (offer.getTariff() != null && offer.getTariff().getId() != null) {
				updateTargetTariff(offer);
			}
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

	private RegistryBank createRegistryBank(Offer offer) throws ManagerBeanException {
		IManagerBean rBankBean = BeanManager.getManagerBean(RegistryBank.class);
		RegistryBank rBank = new RegistryBank();
		rBank.setRegistry(offer.getTarget().getRegistry());
		rBank.setBank(offer.getBank());
		rBank.setBankAccount(offer.getBankAccount());
		return (RegistryBank)rBankBean.insert(rBank);
	}

	private RegistryPayMethod createRegistryPayMethod(Offer offer, RegistryBank rBank) throws ManagerBeanException {
		IManagerBean rPayMethodBean = BeanManager.getManagerBean(RegistryPayMethod.class);
		RegistryPayMethod rPayMethod = new RegistryPayMethod();
		rPayMethod.setRegistry(offer.getTarget().getRegistry());
		rPayMethod.setPayment(offer.getPayMethod());
		rPayMethod.setNumberOfPayments((offer.getNumberOfPayments() == 0) ? 1 : offer.getNumberOfPayments());
		rPayMethod.setDaysToFirstPayment(offer.getDaysToFirstPayment());
		rPayMethod.setDaysBetweenPayments(offer.getDaysBetweenPayments());
		rPayMethod.setPaymentDays(offer.getPaymentDays());
		rPayMethod.setRegistryBank(rBank);
		return (RegistryPayMethod)rPayMethodBean.insert(rPayMethod);
	}

	private void updateTargetTariff(Offer offer) throws ManagerBeanException {
		if (offer.getTarget().getTariff() == null || offer.getTarget().getTariff().getId() == null) {
			IManagerBean targetBean = BeanManager.getManagerBean(Target.class);
			offer.getTarget().setTariff(offer.getTariff());
			targetBean.update(offer.getTarget());
		}
	}

}
