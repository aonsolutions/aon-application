package com.code.aon.ui.finance.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class FBatchDetailController extends FBatchDetailListController implements IFinanceConstants {

	public void onLoadFinance(ActionEvent event) throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			Finance finance = ((FinanceBatchDetail)this.getModel().getRowData()).getFinance();
			BasicController financeController = (BasicController) AonUtil.getRegisteredBean(FINANCE_CONTROLLER_NAME);
			financeController.onLoad(event, finance.getId(), FINANCE_BATCH_FORM_NAME, FINANCE_BATCH_CONTROLLER_NAME + ".onBackFinanceBatch");
		}
	}

}