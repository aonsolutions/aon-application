package com.code.aon.ui.product.controller;

import javax.faces.event.AbortProcessingException;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.ProductAccount;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.bridge.enumeration.ProductAccountType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;

public class ExpenseItemController extends BasicController {

	public Account getAccount() {
		try {
			Item item = (Item)this.getModel().getRowData();
			IManagerBean productAccountBean = BeanManager.getManagerBean(ProductAccount.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(productAccountBean.getFieldName(IAccountBridgeAlias.PRODUCT_ACCOUNT_PRODUCT_ID), item.getProduct().getId());
			criteria.addEqualExpression(productAccountBean.getFieldName(IAccountBridgeAlias.PRODUCT_ACCOUNT_TYPE), ProductAccountType.PURCHASE);
			for (ITransferObject to : productAccountBean.getList(criteria)) {
				ProductAccount productAccount = (ProductAccount)to;
				return productAccount.getAccount();
			}
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
		return null;
	}

}