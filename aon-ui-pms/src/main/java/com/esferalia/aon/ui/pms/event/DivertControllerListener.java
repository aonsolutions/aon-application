package com.esferalia.aon.ui.pms.event;

import java.util.Date;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationDivert;
import com.esferalia.aon.pms.enumeration.ReservationDivertStatus;

public class DivertControllerListener extends ControllerAdapter {
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		ProjectReservationDivert divert = (ProjectReservationDivert) event.getController().getTo();
		try {
			IManagerBean reservationBean = BeanManager.getManagerBean(ProjectReservation.class);
			divert.setProjectReservation((ProjectReservation) reservationBean.createNewTo());
		
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
		divert.setDivertDate(new Date());
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		ProjectReservationDivert divert = (ProjectReservationDivert) event.getController().getTo();
		divert.setRequestHotel(divert.getProjectReservation().getHotel());
		divert.setStatus(ReservationDivertStatus.PENDING);
	}
	
}