package com.esferalia.aon.pms.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.reservation.ReservationUtils;

public class ProjectReservationBeanVetoListener extends ManagerBeanVetoListenerAdapter {

    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	ProjectReservation to = (ProjectReservation)evt.getTo();
    	ReservationUtils reservationUtils = new ReservationUtils();
    	try {
    		reservationUtils.fillProject(to);
    	} catch (ManagerBeanException ex) {
    		throw new ManagerBeanVetoListenerException(ex.getMessage(), ex);
    	}
    }

    @Override
    public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	ProjectReservation to = (ProjectReservation)evt.getTo();
    	ReservationUtils reservationUtils = new ReservationUtils();
    	try {
    		reservationUtils.fillProject(to);
    	} catch (ManagerBeanException ex) {
    		throw new ManagerBeanVetoListenerException(ex.getMessage(), ex);
    	}
    }

}
