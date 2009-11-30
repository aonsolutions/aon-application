package com.code.aon.ui.accounting.controller;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class AmortizationController extends BasicController {

	private static final String AMORTIZATION_DETAIL_CONTROLLER = "amortizationDetail";

	public boolean isUpdatable() {
		try {
			if (isNew()) {
				return true;
			}

			AmortizationDetailController adc = (AmortizationDetailController) AonUtil
					.getRegisteredBean(AMORTIZATION_DETAIL_CONTROLLER);
			return adc.hasScoredOrBlockedDetails();
		} catch (ManagerBeanException e) {
			return true;
		}
	}

}
