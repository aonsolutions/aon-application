package com.esferalia.aon.pms.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.reservation.ReservationUtils;

public class ProjectReservationBeanVetoListener extends ManagerBeanVetoListenerAdapter {

    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	ProjectReservation to = (ProjectReservation)evt.getTo();
    	ReservationUtils reservationUtils = new ReservationUtils();
    	try {
    		reservationUtils.fillProject(to);
    		calculateReservationTotals(reservationUtils, to);
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
    		calculateReservationTotals(reservationUtils, to);
    	} catch (ManagerBeanException ex) {
    		throw new ManagerBeanVetoListenerException(ex.getMessage(), ex);
    	}
    }

	private void calculateReservationTotals(ReservationUtils reservationUtils, ProjectReservation reservation) throws ManagerBeanException {
		if (!reservation.isCrs() || reservation.isForceCalculateTotals()) {
			double taxableBase = reservationUtils.getReservationCalculatedTaxableBase(reservation);
			double vatQuota = reservationUtils.getReservationCalculatedVatQuota(reservation);

			reservation.setTaxableBase(taxableBase);
			reservation.setVatQuota(vatQuota);
			reservation.setTotal(CommonUtil.round(taxableBase + vatQuota));
		}
	}

}
