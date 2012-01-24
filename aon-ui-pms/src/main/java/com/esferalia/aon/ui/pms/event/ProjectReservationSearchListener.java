package com.esferalia.aon.ui.pms.event;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.enumeration.ReservationStatus;

public class ProjectReservationSearchListener extends ControllerSearchListener {

	private Hotel hotel;
	private Customer agency;
	private ReservationStatus[] reservationStatuses;

	public Hotel getHotel() {
		return hotel;
	}

	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}

	public Customer getAgency() {
		return agency;
	}

	public void setAgency(Customer agency) {
		this.agency = agency;
	}

	public ReservationStatus[] getReservationStatuses() {
		return reservationStatuses;
	}

	public void setReservationStatuses(ReservationStatus[] reservationStatuses) {
		this.reservationStatuses = reservationStatuses;
	}
	
	@Override
	protected void init() throws ManagerBeanException {
		setHotel((Hotel)BeanManager.getManagerBean(Hotel.class).createNewTo());
		setAgency((Customer)BeanManager.getManagerBean(Customer.class).createNewTo());
		ReservationStatus[] defaultReservationStatus = {ReservationStatus.ACTIVE};
		setReservationStatuses(defaultReservationStatus);
	}
	
	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		if (getHotel() != null && getHotel().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_HOTEL_ID), getHotel().getId());			
		}
		if (getAgency() != null && getAgency().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_AGENCY_ID), getAgency().getId());			
		}
		if (!ArrayUtils.isEmpty(getReservationStatuses())) {
			String status = getController().resolveAlias(IEntityAlias.PROJECT_RESERVATION_STATUS);
			addEnumToCriteria(criteria, status, getReservationStatuses());
		}
	}

}