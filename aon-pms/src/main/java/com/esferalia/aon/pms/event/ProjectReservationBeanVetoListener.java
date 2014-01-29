package com.esferalia.aon.pms.event;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
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

        	if (!to.isForceRefreshBooking()) {
        		to.setForceRefreshBooking(to.isCancelled() || to.isNoShow() || isRefreshBookingNeeded(to));
        	}
    	} catch (ManagerBeanException ex) {
    		throw new ManagerBeanVetoListenerException(ex.getMessage(), ex);
    	}
    }

	private void calculateReservationTotals(ReservationUtils reservationUtils, ProjectReservation reservation) throws ManagerBeanException {
		if (StringUtils.isEmpty(reservation.getCrsCode()) || reservation.isForceCalculateTotals()) {
			double taxableBase = reservationUtils.getReservationCalculatedTaxableBase(reservation);
			double vatQuota = reservationUtils.getReservationCalculatedVatQuota(reservation);

			reservation.setTaxableBase(taxableBase);
			reservation.setVatQuota(vatQuota);
			reservation.setTotal(CommonUtil.round(taxableBase + vatQuota));
		}
	}

	private boolean isRefreshBookingNeeded(ProjectReservation reservation) {
		Integer agency = (reservation.getAgency() != null && reservation.getAgency().getId() != null) ? reservation.getAgency().getId() : null;
		String stmt = "SELECT 1" +
						" FROM project_reservation as project_reservation" +
    					" WHERE project_reservation.project = :project" +
    					" AND project_reservation.hotel = :hotel" +
    					" AND project_reservation.agency " + ((agency != null) ? "= :agency" : "IS NULL") +
    					" AND project_reservation.start_date = :start_date" + 
    					" AND project_reservation.end_date = :end_date";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		SQLQuery query = session.createSQLQuery(stmt);
		query.setInteger("project", reservation.getId());
		query.setInteger("hotel", reservation.getHotel().getId());
		if (agency != null) {
			query.setInteger("agency", agency);
		}
		query.setDate("start_date", reservation.getStartDate());
		query.setDate("end_date", reservation.getEndDate());
        return query.list().isEmpty();
	}

}
