package com.code.aon.ui.finance.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.common.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.finance.CashFlowForecast;
import com.code.aon.finance.enumeration.BillingPeriod;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class CashFlowForecastController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private CashFlowForecastParams params;
	private Month startMonth;
	private BillingPeriod billingPeriod;
	
	public CashFlowForecastParams getParams() {
		if (params == null) {
			setParams( new CashFlowForecastParams() );
		}
		return params;
	}
	public void setParams(CashFlowForecastParams params) {
		this.params = params;
	}
	
	public Month getStartMonth() {
		return startMonth;
	}
	public void setStartMonth(Month startMonth) {
		this.startMonth = startMonth;
	}
	public BillingPeriod getBillingPeriod() {
		return billingPeriod;
	}
	public void setBillingPeriod(BillingPeriod billingPeriod) {
		this.billingPeriod = billingPeriod;
	}
	
	public int getAvailableRegistryBanks() throws ManagerBeanException {
		String companyControllerName = ICompanyConstants.COLLECTIONS_CONTROLLER_NAME;
		CompanyCollectionsController companyCollections = (CompanyCollectionsController)AonUtil.getRegisteredBean(companyControllerName);
		return companyCollections.getAllCompanyBanks().size();
	}

	public void onUpdateChecks(ActionEvent event) {
		CashFlowForecast cff = (CashFlowForecast) getTo();
		cff.initializeMonths();
		boolean[] checks = cff.getMonths();
		if (getBillingPeriod() != BillingPeriod.NO_PERIOD) {
			int step = getBillingPeriod().getValue();
			int offset = getStartMonth().getValue();
			for (int i = 0;i<12;i=i+step) {
				int x = i + offset;
				checks[x>11?(x-12):x] = true;
			}
		}
		cff.setMonths(checks);
	}
}
