package com.code.aon.ui.commercial.event;

import java.util.Date;

import com.code.aon.commercial.Commission;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class CommissionControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Date endDate = ((Commission) this.getController().getTo()).getEndDate();
		if (endDate != null) {
			Date startDate = ((Commission) this.getController().getTo()).getStartDate();
			if (endDate.before(startDate)) {
				((Commission)this.getController().getTo()).setEndDate(null);
				throw new ControllerListenerException(AonUtil.getMessage("commercialBundle", "commercial_commission_dates_error"));
			}
		}
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		Date endDate = ((Commission) this.getController().getTo()).getEndDate();
		if (endDate != null) {
			Date startDate = ((Commission) this.getController().getTo()).getStartDate();
			if (endDate.before(startDate)) {
				((Commission)this.getController().getTo()).setEndDate(null);
				throw new ControllerListenerException(AonUtil.getMessage("commercialBundle", "commercial_commission_dates_error"));
			}
		}
	}

}