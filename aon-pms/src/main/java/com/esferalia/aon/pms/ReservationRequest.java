package com.esferalia.aon.pms;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.audit.IAuditable;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.ReservationRequestDB;
import com.esferalia.aon.pms.enumeration.BookingHolder;

@Entity
@Table(name="reservation_request")
public class ReservationRequest extends ReservationRequestDB implements IAuditable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	private Set<ReservationRequestRoom> rooms = new HashSet<ReservationRequestRoom>();

	public ReservationRequest() {
		setActive(true);
	}

	@OneToMany(mappedBy = "reservationRequest", cascade={CascadeType.REMOVE})
	@OrderBy()
	public Set<ReservationRequestRoom> getRooms() {
		return this.rooms;
	}
	public void setRooms(Set<ReservationRequestRoom> rooms) {
		this.rooms = rooms;
	}

	@Transient
	public int getNights() {
		if (getStartDate() != null && getEndDate() != null && getStartDate().compareTo(getEndDate()) < 0) {
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
	public String getGuestFullName() throws ManagerBeanException {
		IManagerBean requestGuestBean = BeanManager.getManagerBean(ReservationRequestGuest.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(requestGuestBean.getFieldName(IEntityAlias.RESERVATION_REQUEST_GUEST_RESERVATION_REQUEST_ID), getId());
		criteria.addOrder(requestGuestBean.getFieldName(IEntityAlias.RESERVATION_REQUEST_GUEST_GUEST_INDEX));
		Projection prjName = Projection.property(requestGuestBean.getFieldName(IEntityAlias.RESERVATION_REQUEST_GUEST_NAME));
		Projection prjSurname = Projection.property(requestGuestBean.getFieldName(IEntityAlias.RESERVATION_REQUEST_GUEST_SURNAME));
		List<?> resultList = requestGuestBean.getList(new ProjectionList(prjName, prjSurname), criteria);
		if (resultList.size() > 0 && resultList.get(0) != null) {
			Object[] result = (Object[])resultList.get(0);
	    	String guestName = (result[0] == null) ? "" : result[0].toString() + " ";
	    	guestName += (result[1] == null) ? "" : result[1].toString();
			return guestName;
		}
		return null;
	}

}