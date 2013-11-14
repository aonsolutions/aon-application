package com.esferalia.aon.ui.pms.event;

import java.util.Date;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.seller.Seller;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.enumeration.ReservationCheckStatus;
import com.esferalia.aon.pms.enumeration.ReservationStatus;

public class ProjectReservationSearchListener extends ControllerSearchListener {

	private Hotel hotel;
	private String invoiceNumber;
	private Date creationDateFrom;
	private Date creationDateTo;
	private Date insideDateFrom;
	private Date insideDateTo;
	private Customer agency;
	private Seller seller;
	private ReservationCheckStatus[] reservationCheckStatuses;
	private ReservationStatus[] reservationStatuses;
	private String guestName;
	private String guestSurname;

	public Hotel getHotel() {
		return hotel;
	}

	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public Date getCreationDateFrom() {
		return creationDateFrom;
	}

	public void setCreationDateFrom(Date creationDateFrom) {
		this.creationDateFrom = creationDateFrom;
	}

	public Date getCreationDateTo() {
		return creationDateTo;
	}

	public void setCreationDateTo(Date creationDateTo) {
		this.creationDateTo = creationDateTo;
	}

	public Date getInsideDateFrom() {
		return insideDateFrom;
	}

	public void setInsideDateFrom(Date insideDateFrom) {
		this.insideDateFrom = insideDateFrom;
	}

	public Date getInsideDateTo() {
		return insideDateTo;
	}

	public void setInsideDateTo(Date insideDateTo) {
		this.insideDateTo = insideDateTo;
	}

	public Customer getAgency() {
		return agency;
	}

	public void setAgency(Customer agency) {
		this.agency = agency;
	}

	public Seller getSeller() {
		return seller;
	}

	public void setSeller(Seller seller) {
		this.seller = seller;
	}

	public ReservationCheckStatus[] getReservationCheckStatuses() {
		return reservationCheckStatuses;
	}

	public void setReservationCheckStatuses(ReservationCheckStatus[] reservationCheckStatuses) {
		this.reservationCheckStatuses = reservationCheckStatuses;
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
	
	@Override
	protected void init() throws ManagerBeanException {
		setHotel((Hotel)BeanManager.getManagerBean(Hotel.class).createNewTo());
		setInvoiceNumber(null);
		setCreationDateFrom(null);
		setCreationDateTo(null);
		setInsideDateFrom(null);
		setInsideDateTo(null);
		setAgency((Customer)BeanManager.getManagerBean(Customer.class).createNewTo());
		setSeller((Seller)BeanManager.getManagerBean(Seller.class).createNewTo());
		setReservationCheckStatuses(null);
		setReservationStatuses(null);
		setGuestName(null);
		setGuestSurname(null);
	}
	
	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		if (getHotel() != null && getHotel().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_HOTEL_ID), getHotel().getId());			
		}
		if (StringUtils.isNotEmpty(getInvoiceNumber())) {
			criteria.addEqualExpression(getController().resolveAlias("ProjectReservation.invoices.referenceCode"), getInvoiceNumber());
		}
		if (getCreationDateFrom() != null) {
			criteria.addGreaterThanOrEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_CREATION_DATE), getCreationDateFrom());
		}
		if (getCreationDateTo() != null) {
			Date creationDateTo = DateUtils.addMilliseconds(getCreationDateTo(), (int)(DateUtils.MILLIS_PER_DAY - DateUtils.MILLIS_PER_SECOND));
			criteria.addLessThanOrEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_CREATION_DATE), creationDateTo);
		}
		if (getInsideDateFrom() != null) {
			Date insideDateTo = (getInsideDateTo() != null) ? getInsideDateTo() : getInsideDateFrom();
			criteria.addLessThanOrEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_START_DATE), insideDateTo);			
			criteria.addGreaterThanOrEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_END_DATE), getInsideDateFrom());			
		}
		if (getAgency() != null && getAgency().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_AGENCY_ID), getAgency().getId());			
		}
		if (getSeller() != null && getSeller().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_SELLER_ID), getSeller().getId());			
		}
		if (!ArrayUtils.isEmpty(getReservationCheckStatuses())) {
			String status = getController().resolveAlias(IEntityAlias.PROJECT_RESERVATION_CHECK_STATUS);
			addEnumToCriteria(criteria, status, getReservationCheckStatuses());
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
	}

}