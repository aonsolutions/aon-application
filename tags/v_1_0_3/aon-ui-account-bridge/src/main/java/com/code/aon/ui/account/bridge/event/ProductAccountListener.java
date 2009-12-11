package com.code.aon.ui.account.bridge.event;

import java.util.Iterator;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.ProductAccount;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.bridge.enumeration.ProductAccountType;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class ProductAccountListener extends ControllerAdapter {

	@Override
	@SuppressWarnings("unchecked")
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		Item item = (Item)event.getController().getTo();
		try {
			IManagerBean productAccountBean = BeanManager.getManagerBean(ProductAccount.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(productAccountBean.getFieldName(IAccountBridgeAlias.PRODUCT_ACCOUNT_PRODUCT_ID), item.getProduct().getId());
			Iterator iterator = productAccountBean.getList(criteria).iterator();
			while (iterator.hasNext()) {
				ProductAccount productAccount = (ProductAccount)iterator.next();
				if (ProductAccountType.SALES.equals(productAccount.getType())) {
					item.getProduct().setSalesAccount(productAccount.getAccount().getId());
				} else if (ProductAccountType.PURCHASE.equals(productAccount.getType())) {
					item.getProduct().setPurchaseAccount(productAccount.getAccount().getId());
				}
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Item item = (Item)event.getController().getTo();
		if (item.getProduct().getSalesAccount() != null && !item.getProduct().getSalesAccount().equals("")) {
			validateAccount(item.getProduct().getSalesAccount());
		}
		if (item.getProduct().getPurchaseAccount() != null && !item.getProduct().getPurchaseAccount().equals("")) {
			validateAccount(item.getProduct().getPurchaseAccount());
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Item item = (Item)event.getController().getTo();
		if (item.getProduct().getSalesAccount() != null && !item.getProduct().getSalesAccount().equals("")) {
			Account salesAccount = new Account();
			salesAccount.setId(item.getProduct().getSalesAccount());
			insertProductAccount(item.getProduct(), salesAccount, ProductAccountType.SALES);
		}
		if (item.getProduct().getPurchaseAccount() != null && !item.getProduct().getPurchaseAccount().equals("")) {
			Account purchaseAccount = new Account();
			purchaseAccount.setId(item.getProduct().getPurchaseAccount());
			insertProductAccount(item.getProduct(), purchaseAccount, ProductAccountType.PURCHASE);
		}
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		Item item = (Item)event.getController().getTo();
		if (item.getProduct().getSalesAccount() != null && !item.getProduct().getSalesAccount().equals("")) {
			validateAccount(item.getProduct().getSalesAccount());
		}
		if (item.getProduct().getPurchaseAccount() != null && !item.getProduct().getPurchaseAccount().equals("")) {
			validateAccount(item.getProduct().getPurchaseAccount());
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		Item item = (Item)event.getController().getTo();
		if (item.getProduct().getSalesAccount() != null && !item.getProduct().getSalesAccount().equals("")) {
			Account salesAccount = new Account();
			salesAccount.setId(item.getProduct().getSalesAccount());
			updateProductAccount(item.getProduct(), salesAccount, ProductAccountType.SALES);
		} else {
			removeProductAccount(item.getProduct(), ProductAccountType.SALES);
		}
		if (item.getProduct().getPurchaseAccount() != null && !item.getProduct().getPurchaseAccount().equals("")) {
			Account purchaseAccount = new Account();
			purchaseAccount.setId(item.getProduct().getPurchaseAccount());
			updateProductAccount(item.getProduct(), purchaseAccount, ProductAccountType.PURCHASE);
		} else {
			removeProductAccount(item.getProduct(), ProductAccountType.PURCHASE);
		}
	}

	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		Item item = (Item)event.getController().getTo();
		removeProductAccount(item.getProduct(), ProductAccountType.SALES);
		removeProductAccount(item.getProduct(), ProductAccountType.PURCHASE);
	}

	@SuppressWarnings("unchecked")
	private void validateAccount(String accountId) throws ControllerListenerException {
		try {
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), accountId);
			Iterator iterator = accountBean.getList(criteria).iterator();
			if (iterator.hasNext()) {
				Account account = (Account)iterator.next();
				if (!account.isEntryEnabled()) {
					throw new ControllerListenerException("La Cuenta Contable " + accountId + " no permite apuntes.");
				}
			} else {
				throw new ControllerListenerException("La Cuenta Contable " + accountId + " no existe.");
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	private void insertProductAccount(Product product, Account account, ProductAccountType type) throws ControllerListenerException {
		ProductAccount productAccount = new ProductAccount();
		productAccount.setProduct(product);
		productAccount.setAccount(account);
		productAccount.setType(type);
		try {
			IManagerBean productAccountBean = BeanManager.getManagerBean(ProductAccount.class);
			productAccountBean.insert(productAccount);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	private void updateProductAccount(Product product, Account account, ProductAccountType type) throws ControllerListenerException {
		try {
			IManagerBean productAccountBean = BeanManager.getManagerBean(ProductAccount.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(productAccountBean.getFieldName(IAccountBridgeAlias.PRODUCT_ACCOUNT_PRODUCT_ID), product.getId());
			criteria.addEqualExpression(productAccountBean.getFieldName(IAccountBridgeAlias.PRODUCT_ACCOUNT_TYPE), type);
			Iterator iterator = productAccountBean.getList(criteria).iterator();
			if (iterator.hasNext()) {
				ProductAccount productAccount = (ProductAccount)iterator.next();
				productAccount.setAccount(account);
				productAccountBean.update(productAccount);
			} else {
				insertProductAccount(product, account, type);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
		
	}

	@SuppressWarnings("unchecked")
	private void removeProductAccount(Product product, ProductAccountType type) throws ControllerListenerException {
		try {
			IManagerBean productAccountBean = BeanManager.getManagerBean(ProductAccount.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(productAccountBean.getFieldName(IAccountBridgeAlias.PRODUCT_ACCOUNT_PRODUCT_ID), product.getId());
			criteria.addEqualExpression(productAccountBean.getFieldName(IAccountBridgeAlias.PRODUCT_ACCOUNT_TYPE), type);
			Iterator iterator = productAccountBean.getList(criteria).iterator();
			while (iterator.hasNext()) {
				ProductAccount productAccount = (ProductAccount)iterator.next();
				productAccountBean.remove(productAccount);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}
}