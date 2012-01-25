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
import com.esferalia.aon.entity.master.ProjectReservationServiceDB;

@Entity
@Table(name="project_reservation_service")
public class ProjectReservationService extends ProjectReservationServiceDB {

	private static final long serialVersionUID = 1L;

	private boolean showServiceDetail;
	private String roomNumber;

	@Transient
	public boolean isShowServiceDetail() {
		return showServiceDetail;
	}
	public void setShowServiceDetail(boolean value) {
		this.showServiceDetail = value;
	}

	@Transient
	public String getRoomNumber() throws ManagerBeanException {
		if (roomNumber == null) {
			List<String> roomList = new LinkedList<String>();
			IManagerBean reservationServiceDetailBean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
			Criteria criteria = new Criteria();
			String alias = reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_ID);
			criteria.addEqualExpression(alias, getId());
			for (ITransferObject ito : reservationServiceDetailBean.getList(criteria)) {
				ProjectReservationServiceDetail reservationServiceDetail = (ProjectReservationServiceDetail)ito;
				if (reservationServiceDetail.getProjectReservationRoomDetail() != null) {
					String roomNumber = reservationServiceDetail.getProjectReservationRoomDetail().getRoom().getAsset().getName();
					if (!roomList.contains(roomNumber)) {
						roomList.add(roomNumber);
					}
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
	public ProjectReservationRoom getProjectReservationRoom() throws ManagerBeanException {
		IManagerBean reservationServiceDetailBean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
		Criteria criteria = new Criteria();
		String alias = reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_ID);
		criteria.addEqualExpression(alias, getId());
		for (ITransferObject ito : reservationServiceDetailBean.getList(criteria)) {
			ProjectReservationServiceDetail reservationServiceDetail = (ProjectReservationServiceDetail)ito;
			if (reservationServiceDetail.getProjectReservationRoomDetail() != null) {
				return reservationServiceDetail.getProjectReservationRoomDetail().getProjectReservationRoom();
			}
		}
		return null;
	}

}