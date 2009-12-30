package com.code.aon.ui.account.controller;

import java.util.Collection;

import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class AccountMonthlyReportController extends BasicController {

    private static final String ACCOUNT_SUMMARY_REPORT_CONTROLLER_NAME = "accountSummaryReport";

    @SuppressWarnings("unchecked")
	public Collection getCollection(){
        AccountSummaryReportController controller = (AccountSummaryReportController)AonUtil.getController(ACCOUNT_SUMMARY_REPORT_CONTROLLER_NAME);
        return controller.getMonthlyCollection();
    }

}
