package com.esferalia.aon.ui.pms.controller;

import java.util.Collection;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.FormUtil;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationServiceDetail;



public class ReservationInvoicePrinter {
	
	public ReservationInvoicePrinter getInstance() {
		return new ReservationInvoicePrinter();
	}
	
	@SuppressWarnings("rawtypes")
	public Collection getCollection() throws ManagerBeanException {
		ProjectReservationController reservationController = (ProjectReservationController) FormUtil.getController("reservation");
		if(reservationController.getTo()!=null){
			ProjectReservationServiceController reservationServiceController = (ProjectReservationServiceController) FormUtil.getController("reservationService");
			return reservationServiceController.getServiceDetailList();
		}
		return null;
	}
	
	public ProjectReservation getProjectReservation() throws ManagerBeanException {
		return ((ProjectReservationServiceDetail) (getCollection().isEmpty()?null:getCollection().toArray()[0])).getProjectReservationService().getProjectReservation();
	}


}