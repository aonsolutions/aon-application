package com.esferalia.aon.pms.event;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.registry.Registry;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.enumeration.BookingHolder;
import com.esferalia.aon.pms.enumeration.ReservationStatus;

public class ProjectReservationBeanVetoListener extends ManagerBeanVetoListenerAdapter {

    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	ProjectReservation to = (ProjectReservation)evt.getTo();
    	to.getProject().setProjectType(null);
    	to.getProject().setDate(to.getStartDate());
    	to.getProject().setRegistry(obtainProjectReservationRegistry(to));
	    to.getProject().setName(obtainProjectReservationName(to));
    	to.getProject().setReservation(true);
    	to.getProject().setActive(to.getStatus() == ReservationStatus.ACTIVE);
    }

    @Override
    public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	ProjectReservation to = (ProjectReservation)evt.getTo();
    	to.getProject().setDate(to.getStartDate());
    	to.getProject().setRegistry(obtainProjectReservationRegistry(to));
	    to.getProject().setName(obtainProjectReservationName(to));
    	to.getProject().setReservation(true);
    	to.getProject().setActive(to.getStatus() == ReservationStatus.ACTIVE);
    }

    private Registry obtainProjectReservationRegistry(ProjectReservation to) {
    	if (to.getBookingHolder() == BookingHolder.AGENCY && to.getAgency() != null && to.getAgency().getId() != null) {
    		return to.getAgency().getRegistry();
    	} else if (to.getBookingHolder() == BookingHolder.COMPANY && to.getCompany() != null && to.getCompany().getId() != null) {
    		return to.getCompany().getRegistry();
    	}
    	return to.getHotel().getCustomer().getRegistry();
    }

    private String obtainProjectReservationName(ProjectReservation to) {
    	DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    	String dates = formatter.format(to.getStartDate()) + "-" + formatter.format(to.getEndDate());
    	String sellerName = (to.getSeller() != null && to.getSeller().getId() != null) ? " - " + to.getSeller().getRegistry().getFullName() : "";
    	return StringUtils.abbreviate(dates + " " + to.getCode() + sellerName, 64);
    }

}
