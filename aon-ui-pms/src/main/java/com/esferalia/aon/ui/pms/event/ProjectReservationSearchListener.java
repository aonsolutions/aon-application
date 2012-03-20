package com.esferalia.aon.ui.pms.event;

import java.util.Date;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.enumeration.ReservationStatus;

public class ProjectReservationSearchListener extends ControllerSearchListener {

	private Hotel hotel;
	private Customer agency;
	private ReservationStatus[] reservationStatuses;
	private String guestName;
	private String guestSurname;
	private Date insideDate;

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
	
	public String getGuestName() {
		return guestName;
	}

	public void setGuestName(String guestName) {
		this.guestName = guestName;
	}

	public String getGuestSurname() {
		return guestSurname;
	}

	public void setGuestSurname(String guestSurname) {
		this.guestSurname = guestSurname;
	}
	
	public Date getInsideDate() {
		return insideDate;
	}

	public void setInsideDate(Date insideDate) {
		this.insideDate = insideDate;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setHotel((Hotel)BeanManager.getManagerBean(Hotel.class).createNewTo());
		setAgency((Customer)BeanManager.getManagerBean(Customer.class).createNewTo());
		ReservationStatus[] defaultReservationStatus = {ReservationStatus.ACTIVE, ReservationStatus.INVOICED};
		setReservationStatuses(defaultReservationStatus);
		setGuestName(null);
		setGuestSurname(null);
		setInsideDate(null);
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
		if (StringUtils.isNotEmpty(getGuestName())) {
			criteria.addExpression(ExpressionUtilities.getLikeExpression(getController().resolveAlias("ProjectReservation.guests.name"), "%"+getGuestName()+"%"));
		}
		if (StringUtils.isNotEmpty(getGuestSurname())) {
			criteria.addExpression(ExpressionUtilities.getLikeExpression(getController().resolveAlias("ProjectReservation.guests.surname"), "%"+getGuestSurname()+"%"));
		}
		if (getInsideDate() != null) {
			criteria.addLessThanOrEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_START_DATE), getInsideDate());			
			criteria.addGreaterThanOrEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_END_DATE), getInsideDate());			
		}
		
	}

}