package com.code.aon.ui.accounting.check.modules.account.entry;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.accounting.AccountEntry;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.accounting.IAccountingConstants;
import com.code.aon.ui.accounting.check.AonCheckException;
import com.code.aon.ui.accounting.check.CheckEntryAdapter;
import com.code.aon.ui.accounting.controller.entry.AccountEntryController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class UnbalancedAccountEntryCheckEntry extends CheckEntryAdapter {

	@Override
	public boolean isFixAvailable() {
		return true;
	}

	@Override
	public boolean isFixed() {
		return false;
	}

	@Override
	public String getMessage() {
		AccountEntry entry = (AccountEntry) getTo();
		return super.getMessage() + " ( Id: " + entry.getId() + " )";
	}

	@Override
	public String getFixActionLabel() {
		return "Ver";
	}

	@Override
	public void onFix(ActionEvent event) throws AonCheckException{
		AccountEntry entry = (AccountEntry) getTo();
		try {
			AccountEntryController entryController = (AccountEntryController) FormUtil
					.getController(IAccountingConstants.ACCOUNT_ENTRY_CONTROLLER_NAME);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(entryController.getManagerBean().getFieldName(IEntityAlias.ACCOUNT_ENTRY_ID), entry.getId());
			entryController.setCriteria(criteria);
			entryController.onSearch(null);
			entryController.getModel().setRowIndex(0);
			entryController.onSelect(null);
			entryController.setBackAction("check_list");
		} catch (ManagerBeanException e) {
			String msg = "Error al cargar el apunte.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		
	}
	
	@Override
	public String fixAction() throws AonCheckException {
		return IAccountingConstants.ACCOUNT_ENTRY_FORM_NAVKEY;
	}

}
