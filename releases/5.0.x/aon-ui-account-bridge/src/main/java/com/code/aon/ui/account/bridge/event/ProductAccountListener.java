package com.code.aon.ui.account.bridge.event;

import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.ProductAccount;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.bridge.enumeration.ProductAccountType;
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
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		Item item = (Item)event.getController().getTo();
		item.getProduct().setSalesAccount(new Account());
		item.getProduct().setPurchaseAccount(new Account());
	}

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
					item.getProduct().setSalesAccount(productAccount.getAccount());
				} else if (ProductAccountType.PURCHASE.equals(productAccount.getType())) {
					item.getProduct().setPurchaseAccount(productAccount.getAccount());
				}
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}

		if (item.getProduct().getSalesAccount() == null) {
			item.getProduct().setSalesAccount(new Account());
		}
		if (item.getProduct().getPurchaseAccount() == null) {
			item.getProduct().setPurchaseAccount(new Account());
		}
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Item item = (Item)event.getController().getTo();
		if (item.getProduct().getSalesAccount() != null && !StringUtils.isEmpty(item.getProduct().getSalesAccount().getId())) {
			validateAccount(item.getProduct().getSalesAccount());
		}
		if (item.getProduct().getPurchaseAccount() != null && !StringUtils.isEmpty(item.getProduct().getPurchaseAccount().getId())) {
			validateAccount(item.getProduct().getPurchaseAccount());
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Item item = (Item)event.getController().getTo();
		if (item.getProduct().getSalesAccount() != null && !StringUtils.isEmpty(item.getProduct().getSalesAccount().getId())) {
			insertProductAccount(item.getProduct(), item.getProduct().getSalesAccount(), ProductAccountType.SALES);
		}
		if (item.getProduct().getPurchaseAccount() != null && !StringUtils.isEmpty(item.getProduct().getPurchaseAccount().getId())) {
			insertProductAccount(item.getProduct(), item.getProduct().getPurchaseAccount(), ProductAccountType.PURCHASE);
		}
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		Item item = (Item)event.getController().getTo();
		if (item.getProduct().getSalesAccount() != null && !StringUtils.isEmpty(item.getProduct().getSalesAccount().getId())) {
			validateAccount(item.getProduct().getSalesAccount());
		}
		if (item.getProduct().getPurchaseAccount() != null && !StringUtils.isEmpty(item.getProduct().getPurchaseAccount().getId())) {
			validateAccount(item.getProduct().getPurchaseAccount());
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		Item item = (Item)event.getController().getTo();
		if (item.getProduct().getSalesAccount() != null && !StringUtils.isEmpty(item.getProduct().getSalesAccount().getId())) {
			updateProductAccount(item.getProduct(), item.getProduct().getSalesAccount(), ProductAccountType.SALES);
		} else {
			removeProductAccount(item.getProduct(), ProductAccountType.SALES);
		}
		if (item.getProduct().getPurchaseAccount() != null && !StringUtils.isEmpty(item.getProduct().getPurchaseAccount().getId())) {
			updateProductAccount(item.getProduct(), item.getProduct().getPurchaseAccount(), ProductAccountType.PURCHASE);
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

	private void validateAccount(Account account) throws ControllerListenerException {
		if (account == null) {
			throw new ControllerListenerException("La Cuenta Contable no es válida.");
		}
		if (!account.isEntryEnabled()) {
			throw new ControllerListenerException("La Cuenta Contable " + account.getId() + " no permite apuntes.");
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