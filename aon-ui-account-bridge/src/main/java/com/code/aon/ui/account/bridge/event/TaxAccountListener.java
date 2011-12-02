package com.code.aon.ui.account.bridge.event;

import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.TaxAccount;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.bridge.enumeration.TaxAccountType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Tax;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class TaxAccountListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		Tax tax = (Tax)event.getController().getTo();
		tax.setSalesAccount(new Account());
		tax.setPurchaseAccount(new Account());
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		Tax tax = (Tax)event.getController().getTo();
		try {
			IManagerBean taxAccountBean = BeanManager.getManagerBean(TaxAccount.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(taxAccountBean.getFieldName(IAccountBridgeAlias.TAX_ACCOUNT_TAX_ID), tax.getId());
			Iterator<ITransferObject> iterator = taxAccountBean.getList(criteria).iterator();
			while (iterator.hasNext()) {
				TaxAccount taxAccount = (TaxAccount)iterator.next();
				if (TaxAccountType.SALES.equals(taxAccount.getType())) {
					tax.setSalesAccount(taxAccount.getAccount());
				} else if (TaxAccountType.PURCHASE.equals(taxAccount.getType())) {
					tax.setPurchaseAccount(taxAccount.getAccount());
				}
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}

		if (tax.getSalesAccount() == null) {
			tax.setSalesAccount(new Account());
		}
		if (tax.getPurchaseAccount() == null) {
			tax.setPurchaseAccount(new Account());
		}
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Tax tax = (Tax)event.getController().getTo();
		if (tax.getSalesAccount() != null && !StringUtils.isEmpty(tax.getSalesAccount().getCode())) {
			validateAccount(tax.getSalesAccount());
		}
		if (tax.getPurchaseAccount() != null && !StringUtils.isEmpty(tax.getPurchaseAccount().getCode())) {
			validateAccount(tax.getPurchaseAccount());
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Tax tax = (Tax)event.getController().getTo();
		if (tax.getSalesAccount() != null && !StringUtils.isEmpty(tax.getSalesAccount().getCode())) {
			insertTaxAccount(tax, tax.getSalesAccount(), TaxAccountType.SALES);
		}
		if (tax.getPurchaseAccount() != null && !StringUtils.isEmpty(tax.getPurchaseAccount().getCode())) {
			insertTaxAccount(tax, tax.getPurchaseAccount(), TaxAccountType.PURCHASE);
		}
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		Tax tax = (Tax)event.getController().getTo();
		if (tax.getSalesAccount() != null && !StringUtils.isEmpty(tax.getSalesAccount().getCode())) {
			validateAccount(tax.getSalesAccount());
		}
		if (tax.getPurchaseAccount() != null && !StringUtils.isEmpty(tax.getPurchaseAccount().getCode())) {
			validateAccount(tax.getPurchaseAccount());
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		Tax tax = (Tax)event.getController().getTo();
		if (tax.getSalesAccount() != null && !StringUtils.isEmpty(tax.getSalesAccount().getCode())) {
			updateTaxAccount(tax, tax.getSalesAccount(), TaxAccountType.SALES);
		} else {
			removeTaxAccount(tax, TaxAccountType.SALES);
		}
		if (tax.getPurchaseAccount() != null && !StringUtils.isEmpty(tax.getPurchaseAccount().getCode())) {
			updateTaxAccount(tax, tax.getPurchaseAccount(), TaxAccountType.PURCHASE);
		} else {
			removeTaxAccount(tax, TaxAccountType.PURCHASE);
		}
	}

	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		Tax tax = (Tax)event.getController().getTo();
		removeTaxAccount(tax, TaxAccountType.SALES);
		removeTaxAccount(tax, TaxAccountType.PURCHASE);
	}

	private void validateAccount(Account account) throws ControllerListenerException {
		if (account == null) {
			throw new ControllerListenerException("La Cuenta Contable no es válida.");
		}
		if (!account.isEntryEnabled()) {
			throw new ControllerListenerException("La Cuenta Contable " + account.getId() + " no permite apuntes.");
		}
	}

	private void insertTaxAccount(Tax tax, Account account, TaxAccountType type) throws ControllerListenerException {
		TaxAccount taxAccount = new TaxAccount();
		taxAccount.setTax(tax);
		taxAccount.setAccount(account);
		taxAccount.setType(type);
		try {
			IManagerBean taxAccountBean = BeanManager.getManagerBean(TaxAccount.class);
			taxAccountBean.insert(taxAccount);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	private void updateTaxAccount(Tax tax, Account account, TaxAccountType type) throws ControllerListenerException {
		try {
			IManagerBean taxAccountBean = BeanManager.getManagerBean(TaxAccount.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(taxAccountBean.getFieldName(IAccountBridgeAlias.TAX_ACCOUNT_TAX_ID), tax.getId());
			criteria.addEqualExpression(taxAccountBean.getFieldName(IAccountBridgeAlias.TAX_ACCOUNT_TYPE), type);
			Iterator<ITransferObject> iterator = taxAccountBean.getList(criteria).iterator();
			if (iterator.hasNext()) {
				TaxAccount taxAccount = (TaxAccount)iterator.next();
				taxAccount.setAccount(account);
				taxAccountBean.update(taxAccount);
			} else {
				insertTaxAccount(tax, account, type);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
		
	}

	private void removeTaxAccount(Tax tax, TaxAccountType type) throws ControllerListenerException {
		try {
			IManagerBean taxAccountBean = BeanManager.getManagerBean(TaxAccount.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(taxAccountBean.getFieldName(IAccountBridgeAlias.TAX_ACCOUNT_TAX_ID), tax.getId());
			criteria.addEqualExpression(taxAccountBean.getFieldName(IAccountBridgeAlias.TAX_ACCOUNT_TYPE), type);
			Iterator<ITransferObject> iterator = taxAccountBean.getList(criteria).iterator();
			while (iterator.hasNext()) {
				TaxAccount taxAccount = (TaxAccount)iterator.next();
				taxAccountBean.remove(taxAccount);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}
}