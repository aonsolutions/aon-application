package com.code.aon.ui.finance.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.ui.util.AonUtil;

public class FBatchDetailController extends FBatchDetailListController implements IFinanceConstants {

	public void onLoadFinance(ActionEvent event) throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			FinanceBatchDetail fBatchDetail = (FinanceBatchDetail)this.getModel().getRowData();

			FinanceController financeController = (FinanceController) AonUtil.getRegisteredBean(FINANCE_CONTROLLER_NAME);
			financeController.setPayment(fBatchDetail.getFinance().isPayment());
			financeController.onLoadFinance(event, fBatchDetail.getFinance(), FINANCE_BATCH_FORM_NAME, FINANCE_CONTROLLER_NAME + ".onBack");
		}
	}

}