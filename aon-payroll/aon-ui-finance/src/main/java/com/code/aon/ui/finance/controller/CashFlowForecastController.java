package com.code.aon.ui.finance.controller;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class CashFlowForecastController extends BasicController {

	private CashFlowForecastParams params;
	
	public CashFlowForecastParams getParams() {
		if (params == null) {
			setParams( new CashFlowForecastParams() );
		}
		return params;
	}
	public void setParams(CashFlowForecastParams params) {
		this.params = params;
	}

	public int getAvailableRegistryBanks() throws ManagerBeanException {
		String companyControllerName = ICompanyConstants.COLLECTIONS_CONTROLLER_NAME;
		CompanyCollectionsController companyCollections = (CompanyCollectionsController)AonUtil.getRegisteredBean(companyControllerName);
		return companyCollections.getCompanyBanks().size();
	}

}
