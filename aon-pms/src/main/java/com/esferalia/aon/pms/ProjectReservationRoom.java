package com.esferalia.aon.pms;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.ProjectReservationRoomDB;

@Entity
@Table(name="project_reservation_room")
public class ProjectReservationRoom extends ProjectReservationRoomDB {

	private static final long serialVersionUID = 1L;

	private boolean showRoomDetail;
	private String roomNumber;
	private String roomCustomer;

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
			List<String> roomList = new LinkedList<String>();
			IManagerBean reservationRoomDetailBean = BeanManager.getManagerBean(ProjectReservationRoomDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(reservationRoomDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_PROJECT_RESERVATION_ROOM_ID), getId());
			for (ITransferObject ito : reservationRoomDetailBean.getList(criteria)) {
				ProjectReservationRoomDetail reservationRoomDetail = (ProjectReservationRoomDetail)ito;
				String roomNumber = reservationRoomDetail.getRoom().getAsset().getName();
				if (!roomList.contains(roomNumber)) {
					roomList.add(roomNumber);
				}
			}

			for (int i=0; i<roomList.size(); i++) {
				roomNumber = (roomNumber == null) ? "" : roomNumber + ", ";
				roomNumber += roomList.get(i);
			}
		}
		return roomNumber;
	}
	public void setRoomNumber(String roomNumber) {
		this.roomNumber = roomNumber;
	}
	
	@Transient
	public String getRoomCustomer() throws ManagerBeanException {
		if (roomCustomer == null) {
			IManagerBean bean = BeanManager.getManagerBean(ProjectReservationGuest.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROJECT_RESERVATION_GUEST_PROJECT_RESERVATION_ID), getProjectReservation().getId());
			List<ITransferObject> list = bean.getList(criteria);
			if(!list.isEmpty()){
				roomCustomer = ((ProjectReservationGuest)list.get(0)).getFullName();
			}
			
		}
		return roomCustomer;
	}
	public void setRoomCustomer(String roomCustomer) {
		this.roomCustomer = roomCustomer;
	}

}