package com.code.aon.ui.accounting.event;

import com.code.aon.accounting.AmortizationDetail;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class AmortizationDetailListener extends ControllerAdapter {

	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		AmortizationDetail detail = (AmortizationDetail) event.getController()
				.getTo();
		detail.setCoefficient(CommonUtil.round(detail.getAllocation() * 100
				/ detail.getAmortization().getAmount()));
	}

}
