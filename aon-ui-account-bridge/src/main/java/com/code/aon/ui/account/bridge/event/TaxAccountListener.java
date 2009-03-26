package com.code.aon.ui.account.bridge.event;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.TaxAccount;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.bridge.enumeration.TaxAccountType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Tax;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.PageDataModel;
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
	@SuppressWarnings("unchecked")
	public void afterModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			IManagerBean taxAccountBean = BeanManager.getManagerBean(TaxAccount.class);
			Iterator iterator = ((List)event.getController().getModel().getWrappedData()).iterator();
			while (iterator.hasNext()) {
				Tax tax = (Tax)iterator.next();
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(taxAccountBean.getFieldName(IAccountBridgeAlias.TAX_ACCOUNT_TAX_ID), tax.getId());
				Iterator iter = taxAccountBean.getList(criteria).iterator();
				while (iter.hasNext()) {
					TaxAccount taxAccount = (TaxAccount)iter.next();
					if (TaxAccountType.SALES.equals(taxAccount.getType())) {
						tax.setSalesAccount(taxAccount.getAccount());
					} else if (TaxAccountType.PURCHASE.equals(taxAccount.getType())) {
						tax.setPurchaseAccount(taxAccount.getAccount());
					}
				}
				if (tax.getSalesAccount() == null) {
					tax.setSalesAccount(new Account());
				}
				if (tax.getPurchaseAccount() == null) {
					tax.setPurchaseAccount(new Account());
				}
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void afterBeanCanceled(ControllerEvent event) throws ControllerListenerException {
		Tax tax = (Tax)event.getController().getTo();
		try {
			IManagerBean taxAccountBean = BeanManager.getManagerBean(TaxAccount.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(taxAccountBean.getFieldName(IAccountBridgeAlias.TAX_ACCOUNT_TAX_ID), tax.getId());
			Iterator iterator = taxAccountBean.getList(criteria).iterator();
			while (iterator.hasNext()) {
				TaxAccount taxAccount = (TaxAccount)iterator.next();
				if (TaxAccountType.SALES.equals(taxAccount.getType())) {
					tax.setSalesAccount(taxAccount.getAccount());
				} else if (TaxAccountType.PURCHASE.equals(taxAccount.getType())) {
					tax.setPurchaseAccount(taxAccount.getAccount());
				}
			}
			if (tax.getSalesAccount() == null) {
				tax.setSalesAccount(new Account());
			}
			if (tax.getPurchaseAccount() == null) {
				tax.setPurchaseAccount(new Account());
			}

			BasicController controller = (BasicController)event.getController();
			((PageDataModel) controller.getModel()).setRowData(controller.getSelectedIndex(), tax);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Tax tax = (Tax)event.getController().getTo();
		if (tax.getSalesAccount() != null && !StringUtils.isEmpty(tax.getSalesAccount().getId())) {
			validateAccount(tax.getSalesAccount());
		}
		if (tax.getPurchaseAccount() != null && !StringUtils.isEmpty(tax.getPurchaseAccount().getId())) {
			validateAccount(tax.getPurchaseAccount());
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Tax tax = (Tax)event.getController().getTo();
		if (tax.getSalesAccount() != null && !StringUtils.isEmpty(tax.getSalesAccount().getId())) {
			insertTaxAccount(tax, tax.getSalesAccount(), TaxAccountType.SALES);
		}
		if (tax.getPurchaseAccount() != null && !StringUtils.isEmpty(tax.getPurchaseAccount().getId())) {
			insertTaxAccount(tax, tax.getPurchaseAccount(), TaxAccountType.PURCHASE);
		}
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		Tax tax = (Tax)event.getController().getTo();
		if (tax.getSalesAccount() != null && !StringUtils.isEmpty(tax.getSalesAccount().getId())) {
			validateAccount(tax.getSalesAccount());
		}
		if (tax.getPurchaseAccount() != null && !StringUtils.isEmpty(tax.getPurchaseAccount().getId())) {
			validateAccount(tax.getPurchaseAccount());
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		Tax tax = (Tax)event.getController().getTo();
		if (tax.getSalesAccount() != null && !StringUtils.isEmpty(tax.getSalesAccount().getId())) {
			updateTaxAccount(tax, tax.getSalesAccount(), TaxAccountType.SALES);
		} else {
			removeTaxAccount(tax, TaxAccountType.SALES);
		}
		if (tax.getPurchaseAccount() != null && !StringUtils.isEmpty(tax.getPurchaseAccount().getId())) {
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

	@SuppressWarnings("unchecked")
	private void updateTaxAccount(Tax tax, Account account, TaxAccountType type) throws ControllerListenerException {
		try {
			IManagerBean taxAccountBean = BeanManager.getManagerBean(TaxAccount.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(taxAccountBean.getFieldName(IAccountBridgeAlias.TAX_ACCOUNT_TAX_ID), tax.getId());
			criteria.addEqualExpression(taxAccountBean.getFieldName(IAccountBridgeAlias.TAX_ACCOUNT_TYPE), type);
			Iterator iterator = taxAccountBean.getList(criteria).iterator();
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

	@SuppressWarnings("unchecked")
	private void removeTaxAccount(Tax tax, TaxAccountType type) throws ControllerListenerException {
		try {
			IManagerBean taxAccountBean = BeanManager.getManagerBean(TaxAccount.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(taxAccountBean.getFieldName(IAccountBridgeAlias.TAX_ACCOUNT_TAX_ID), tax.getId());
			criteria.addEqualExpression(taxAccountBean.getFieldName(IAccountBridgeAlias.TAX_ACCOUNT_TYPE), type);
			Iterator iterator = taxAccountBean.getList(criteria).iterator();
			while (iterator.hasNext()) {
				TaxAccount taxAccount = (TaxAccount)iterator.next();
				taxAccountBean.remove(taxAccount);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}
}