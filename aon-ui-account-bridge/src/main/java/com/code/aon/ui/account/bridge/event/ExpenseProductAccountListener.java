package com.code.aon.ui.account.bridge.event;

import java.util.Iterator;
import java.util.List;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.ProductAccount;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.bridge.enumeration.ProductAccountType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.ExtendedPageDataModel;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class ExpenseProductAccountListener extends ProductAccountListener {

	@Override
	@SuppressWarnings("unchecked")
	public void afterModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			IManagerBean productAccountBean = BeanManager.getManagerBean(ProductAccount.class);
			Iterator iterator = ((List)event.getController().getModel().getWrappedData()).iterator();
			while (iterator.hasNext()) {
				Item item = (Item)iterator.next();
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(productAccountBean.getFieldName(IAccountBridgeAlias.PRODUCT_ACCOUNT_PRODUCT_ID), item.getProduct().getId());
				Iterator iter = productAccountBean.getList(criteria).iterator();
				while (iter.hasNext()) {
					ProductAccount productAccount = (ProductAccount)iter.next();
					if (ProductAccountType.SALES.equals(productAccount.getType())) {
						item.getProduct().setSalesAccount(productAccount.getAccount());
					} else if (ProductAccountType.PURCHASE.equals(productAccount.getType())) {
						item.getProduct().setPurchaseAccount(productAccount.getAccount());
					}
				}
				if (item.getProduct().getSalesAccount() == null) {
					item.getProduct().setSalesAccount(new Account());
				}
				if (item.getProduct().getPurchaseAccount() == null) {
					item.getProduct().setPurchaseAccount(new Account());
				}
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
	}

	@Override
	@SuppressWarnings("unchecked")
	public void afterBeanCanceled(ControllerEvent event) throws ControllerListenerException {
		if (!event.getController().isNew()) {
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
				if (item.getProduct().getSalesAccount() == null) {
					item.getProduct().setSalesAccount(new Account());
				}
				if (item.getProduct().getPurchaseAccount() == null) {
					item.getProduct().setPurchaseAccount(new Account());
				}
	
				BasicController controller = (BasicController)event.getController();
				((ExtendedPageDataModel) controller.getModel()).setRowData(controller.getSelectedIndex(), item);
			} catch (ManagerBeanException e) {
				throw new ControllerListenerException(e.getMessage(), e);
			}
		}
	}

}