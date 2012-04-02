package com.esferalia.aon.ui.pms.event;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.pms.ReservationRequest;
import com.esferalia.aon.ui.pms.controller.ReservationRequestController;

public class ReservationRequestControllerListener extends ControllerAdapter {
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		ReservationRequestController controller = (ReservationRequestController)event.getController();
		controller.resetNights();

		ReservationRequest request = (ReservationRequest)controller.getTo();
		request.setStartDate(new Date());
		request.setEndDate(DateUtils.addDays(new Date(), 1));
		request.setRequestCounter(0);
		request.setActive(true);
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		ReservationRequestController controller = (ReservationRequestController)event.getController();
		controller.resetNights();
	}

}