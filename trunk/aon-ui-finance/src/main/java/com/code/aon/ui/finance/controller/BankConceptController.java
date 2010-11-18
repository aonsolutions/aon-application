package com.code.aon.ui.finance.controller;

import javax.faces.event.AbortProcessingException;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.BankConceptAccount;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.BankConcept;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;

public class BankConceptController extends BasicController {

	public Account getAccount() {
		try {
			BankConcept concept = (BankConcept)this.getModel().getRowData();
			IManagerBean conceptAccBean = BeanManager.getManagerBean(BankConceptAccount.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(conceptAccBean.getFieldName(IAccountBridgeAlias.BANK_CONCEPT_ACCOUNT_BANK_CONCEPT_ID), concept.getId());
			for (ITransferObject to : conceptAccBean.getList(criteria)) {
				BankConceptAccount bankConceptAccount = (BankConceptAccount)to;
				return bankConceptAccount.getAccount();
			}
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
		return null;
	}

}