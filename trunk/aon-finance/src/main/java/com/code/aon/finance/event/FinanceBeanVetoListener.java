package com.code.aon.finance.event;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.config.BankAccount;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.ql.Criteria;

public class FinanceBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Finance finance = (Finance)evt.getTo();
		checkFinance(finance);
		fillConcept(finance);
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Finance finance = (Finance)evt.getTo();
		checkFinance(finance);
		fillConcept(finance);
	}

	@Override
	public void vetoableBeanRemoved(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Finance finance = (Finance)evt.getTo();
		removeFractionTracking(finance);
	}

	private void checkFinance(Finance finance) throws ManagerBeanVetoListenerException {
		if(finance.getAmount() == 0){
			throw new ManagerBeanVetoListenerException("El importe del vencimiento no puede ser 0.0");
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
			finance.setSecurityLevel(finance.getInvoice().getSecurityLevel());
		}
	}

	private void fillConcept(Finance finance) {
		if (StringUtils.isEmpty(finance.getConcept())) {
			Invoice invoice = finance.getInvoice();
			String concept = invoice.getReferenceCode();
			if (invoice.getRegistryName() != null && !invoice.getRegistryName().equals(invoice.getRegistry().getFullName())) {
				concept = StringUtils.abbreviate(invoice.getReferenceCode() + " - " + invoice.getRegistryName(), 64);
			}
	        finance.setConcept(concept); 
		}
	}

	private void removeFractionTracking(Finance finance) throws ManagerBeanVetoListenerException {
		if (finance.getFinanceStatus() == FinanceStatus.PENDING) {
			try {
				IManagerBean trackingBean = BeanManager.getManagerBean(FinanceTracking.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(trackingBean.getFieldName(IFinanceAlias.FINANCE_TRACKING_FINANCE_ID), finance.getId());
				criteria.addEqualExpression(trackingBean.getFieldName(IFinanceAlias.FINANCE_TRACKING_TYPE), FinanceTrackingType.FRACTIONED);
				for (ITransferObject to : trackingBean.getList(criteria)) {
					trackingBean.remove(to);
				}
			} catch (ManagerBeanException e) {
				throw new ManagerBeanVetoListenerException(e.getMessage(), e);
			}
		}
	}

}