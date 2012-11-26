package com.code.aon.finance.event;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.config.BankAccount;
import com.code.aon.config.IScopable;
import com.code.aon.config.Scope;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Creditor;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.supplier.Supplier;
import com.esferalia.aon.entity.IEntityAlias;

public class FinanceBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Finance finance = (Finance)evt.getTo();
		checkFinance(finance);
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Finance finance = (Finance)evt.getTo();
		checkFinance(finance);
	}

	@Override
	public void vetoableBeanRemoved(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Finance finance = (Finance)evt.getTo();
		removeFractionTracking(finance);
	}

	private void checkFinance(Finance finance) throws ManagerBeanVetoListenerException {
		if (finance.getAmount() == 0) {
			throw new ManagerBeanVetoListenerException("El importe del vencimiento no puede ser 0.0");
		}
		if (StringUtils.isEmpty(finance.getRegistryName())) {
			finance.setRegistryName((!finance.isEmptyInvoice()) ? finance.getInvoice().getRegistryName() : finance.getRegistry().getFullName());
		}
		if (StringUtils.isEmpty(finance.getRegistryDocument())) {
			finance.setRegistryDocument((!finance.isEmptyInvoice()) ? finance.getInvoice().getRegistryDocument() : finance.getRegistry().getDocument());
			finance.setRegistryDocumentType((!finance.isEmptyInvoice()) ? finance.getInvoice().getRegistryDocumentType() : finance.getRegistry().getDocumentType());
			finance.setRegistryDocumentCountry((!finance.isEmptyInvoice()) ? finance.getInvoice().getRegistryDocumentCountry() : finance.getRegistry().getDocumentCountry());
		}
		if (!finance.isEmptyInvoice()) {
	        finance.setConcept(finance.getInvoice().getDocumentNumber()); 
		}
		BankAccount bankAccount = finance.getBankAccount();
		if (bankAccount != null) {
			if (StringUtils.isWhitespace(bankAccount.getOffice())) bankAccount.setOffice(null);
			if (StringUtils.isWhitespace(bankAccount.getEntity())) bankAccount.setEntity(null);
			if (StringUtils.isWhitespace(bankAccount.getControl())) bankAccount.setControl(null);
			if (StringUtils.isWhitespace(bankAccount.getAccount())) bankAccount.setAccount(null);
			if ((bankAccount.getOffice() != null ||
				bankAccount.getEntity() != null ||
				bankAccount.getControl() != null ||
				bankAccount.getAccount() != null) &&
				!bankAccount.isValid()) {
				throw new ManagerBeanVetoListenerException("La cuenta bancaria del vencimiento no es válida. Los digitos de control no coinciden.");
			}
		}
		if (finance.getBank() != null && finance.getBank().getId() == null) {
			finance.setBank(null);
		}
		if (finance.getSecurityLevel() == null) {
			if (!finance.isEmptyInvoice()) {
				finance.setSecurityLevel(finance.getInvoice().getSecurityLevel());
			} else {
				finance.setSecurityLevel(SecurityLevel.OFFICIAL);
			}
		}
		if (finance.getScope() == null || finance.getScope().getId() == null) {
			if (!finance.isEmptyInvoice()) {
				finance.setScope(finance.getInvoice().getScope());
			} else {
				finance.setScope(obtainRegistryScope(finance.isPayment(), finance.getRegistry()));
			}
		}
	}

	private Scope obtainRegistryScope(boolean payment, Registry registry) throws ManagerBeanVetoListenerException {
		try {
			IManagerBean bean;
			if (payment) {
				bean = BeanManager.getManagerBean(Supplier.class);
				if (bean.get(registry.getId()) == null) {
					bean = BeanManager.getManagerBean(Creditor.class);
				}
			} else {
				bean = BeanManager.getManagerBean(Customer.class);
			}
			IScopable scopable = (IScopable)bean.get(registry.getId());
			return scopable.getScope();
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage(), e);
		}
	}

	private void removeFractionTracking(Finance finance) throws ManagerBeanVetoListenerException {
		if (finance.getFinanceStatus() == FinanceStatus.PENDING) {
			try {
				IManagerBean trackingBean = BeanManager.getManagerBean(FinanceTracking.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(trackingBean.getFieldName(IEntityAlias.FINANCE_TRACKING_FINANCE_ID), finance.getId());
				criteria.addEqualExpression(trackingBean.getFieldName(IEntityAlias.FINANCE_TRACKING_TYPE), FinanceTrackingType.FRACTIONED);
				for (ITransferObject to : trackingBean.getList(criteria)) {
					trackingBean.remove(to);
				}
			} catch (ManagerBeanException e) {
				throw new ManagerBeanVetoListenerException(e.getMessage(), e);
			}
		}
	}

}