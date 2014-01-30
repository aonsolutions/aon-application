package com.esferalia.aon.pms;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.audit.IAuditable;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.ProjectReservationRoomDB;

@Entity
@Table(name="project_reservation_room")
public class ProjectReservationRoom extends ProjectReservationRoomDB implements IAuditable {

	private static final long serialVersionUID = 1L;

	private boolean forceRefreshBooking;
	private boolean showRoomDetail;
	private String roomNumber;
	private String firstRoomNumber;

	public ProjectReservationRoom() {
		setForceRefreshBooking(false);
	}

	@Transient
	public boolean isForceRefreshBooking() {
		return forceRefreshBooking;
	}
	public void setForceRefreshBooking(boolean forceRefreshBooking) {
		this.forceRefreshBooking = forceRefreshBooking;
	}

	@Transient
	public boolean isShowRoomDetail() {
		return showRoomDetail;
	}
	public void setShowRoomDetail(boolean showRoomDetail) {
		this.showRoomDetail = showRoomDetail;
	}

	@Transient
	public String getRoomNumber() throws ManagerBeanException {
		if (roomNumber == null) {
			roomNumber = obtainRoomNumber(false);
		}
		return roomNumber;
	}
	public void setRoomNumber(String roomNumber) {
		this.roomNumber = roomNumber;
	}
	
	@Transient
	public String getFirstRoomNumber() throws ManagerBeanException {
		if (firstRoomNumber == null) {
			firstRoomNumber = obtainRoomNumber(true);
		}
		return firstRoomNumber;
	}
	public void setFirstRoomNumber(String firstRoomNumber) {
		this.firstRoomNumber = firstRoomNumber;
	}
	
	private String obtainRoomNumber(boolean first) throws ManagerBeanException{
		IManagerBean reservationRoomDetailBean = BeanManager.getManagerBean(ProjectReservationRoomDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(reservationRoomDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_PROJECT_RESERVATION_ROOM_ID), getId());
		criteria.addOrder(reservationRoomDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_ASSET_ACTIVITY_DATE), first);
		for (ITransferObject ito : reservationRoomDetailBean.getList(criteria)) {
			return ((ProjectReservationRoomDetail)ito).getRoom().getAsset().getName();
		}
		return null;
	}

}