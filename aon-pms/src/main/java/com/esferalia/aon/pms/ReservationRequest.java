package com.esferalia.aon.pms;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.entity.master.ReservationRequestDB;
import com.esferalia.aon.pms.enumeration.BookingHolder;

@Entity
@Table(name="reservation_request")
public class ReservationRequest extends ReservationRequestDB {

	private static final long serialVersionUID = 1L;

	public ReservationRequest() {
		setActive(true);
	}

	@Transient
	public int getNights() {
		if (getStartDate() != null && getEndDate() != null) {
			return (int)CommonUtil.getDaysBetweenDates(getStartDate(), getEndDate());
		}
		return 0;
	}

	@Transient
	public boolean isGuestHolder() {
		return getBookingHolder() == BookingHolder.GUEST;
	}
	@Transient
	public boolean isAgencyHolder() {
		return getBookingHolder() == BookingHolder.AGENCY;
	}
	@Transient
	public boolean isCompanyHolder() {
		return getBookingHolder() == BookingHolder.COMPANY;
	}

	@Transient
	public String getGuestFullName() {
		return "";
	}

}