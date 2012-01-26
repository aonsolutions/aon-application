package com.esferalia.aon.ui.pms.event;

import java.util.Date;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;

public class ReservationIOSearchListener extends ControllerSearchListener {

	private Hotel hotel;
	private boolean checkin;
	private Date fromDate;
	private Date toDate;
	
	public Hotel getHotel() {
		return hotel;
	}

	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}
		
	public boolean isCheckin() {
		return checkin;
	}

	public void setCheckin(boolean checkin) {
		this.checkin = checkin;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setHotel(null);
		setCheckin(false);
		setFromDate(null);
		setToDate(null);
	}
	
	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		if (getHotel() != null && getHotel().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_HOTEL_ID), getHotel().getId());			
		}
		if(isCheckin()){
			if (getFromDate() != null ) {
				criteria.addGreaterThanOrEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_START_DATE), getFromDate());
			}
			if (getToDate() != null ) {
				criteria.addLessThanOrEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_START_DATE), getToDate());
			}
		} else {
			if (getFromDate() != null ) {
				criteria.addGreaterThanOrEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_END_DATE), getFromDate());
			}
			if (getToDate() != null ) {
				criteria.addLessThanOrEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_END_DATE), getToDate());
			}
		}
	}

}