package com.code.aon.finance.event;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.config.IBankAccountContainer;
import com.code.aon.config.IScopable;
import com.code.aon.config.Scope;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Creditor;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.supplier.Supplier;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.Contract;

public class FinanceBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

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
		if (!finance.isEmptyInvoice() && (finance.getRegistry() == null || finance.getRegistry().getId() == null)) {
			finance.setRegistry(finance.getInvoice().getRegistry());
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
	        if ((finance.getInvoice().isSales() && finance.isPayment()) || (!finance.getInvoice().isSales() && !finance.isPayment())) {
	        	finance.setPayment(!finance.isPayment());
	        }
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
				if (!finance.isPayroll()) {
					finance.setScope(obtainRegistryScope(finance.isPayment(), finance.getRegistry()));
				} else {
					finance.setScope(obtainContractScope(finance.getDueDate(), finance.getRegistry()));
				}
			}

			if (finance.getScope() == null || finance.getScope().getId() == null) {
				throw new ManagerBeanVetoListenerException("No es posible encontrar un Ambito valido para el Vencimiento.");
			}
		}
		checkBankAccount(finance);
	}

	public static void checkBankAccount(IBankAccountContainer bac) throws ManagerBeanVetoListenerException {
		if (bac.getBankAccount() == null || StringUtils.isEmpty(bac.getBankAccount().getBban())) {
			bac.setBankAccount(null);
			bac.setBankAlias(null);
			bac.setBic(null);
		}

		if (bac.getBankAccount() != null && !bac.getBankAccount().isValidBankAccount()) {
			if (!bac.getBankAccount().isValidIbanLength()) {
				throw new ManagerBeanVetoListenerException("Longitud de IBAN incorrecta.");
			} else if (!bac.getBankAccount().isValidBban()) {
				throw new ManagerBeanVetoListenerException("Cuenta Bancaria incorrecta.");
			} else {
				throw new ManagerBeanVetoListenerException("IBAN incorrecto.");
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

	private Scope obtainContractScope(Date dueDate, Registry registry) throws ManagerBeanVetoListenerException {
		try {
			IManagerBean contractBean = BeanManager.getManagerBean(Contract.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(contractBean.getFieldName(IEntityAlias.CONTRACT_PERSON_ID), registry.getId());
			criteria.addNullExpression(contractBean.getFieldName(IEntityAlias.CONTRACT_END_DATE));
			criteria.addOrder(contractBean.getFieldName(IEntityAlias.CONTRACT_START_DATE), false);
			for (ITransferObject ito : contractBean.getList(criteria)) {
				return ((Contract)ito).getWorkPlace().getScope();
			}

			criteria = new Criteria();
			criteria.addEqualExpression(contractBean.getFieldName(IEntityAlias.CONTRACT_PERSON_ID), registry.getId());
			criteria.addOrder(contractBean.getFieldName(IEntityAlias.CONTRACT_END_DATE), false);
			criteria.addOrder(contractBean.getFieldName(IEntityAlias.CONTRACT_START_DATE), false);
			for (ITransferObject ito : contractBean.getList(criteria)) {
				return ((Contract)ito).getWorkPlace().getScope();
			}
			throw new ManagerBeanVetoListenerException("El Trabajador no tiene Contrato.");
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage(), e);
		}
	}

	private void removeFractionTracking(Finance finance) throws ManagerBeanVetoListenerException {
		if (finance.isPending()) {
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