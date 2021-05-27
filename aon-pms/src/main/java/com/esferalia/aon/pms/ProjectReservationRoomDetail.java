package com.esferalia.aon.pms;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.ProjectReservationRoomDetailDB;

@Entity
@Table(name="project_reservation_room_detail")
public class ProjectReservationRoomDetail extends ProjectReservationRoomDetailDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

    @Transient
    public Room getRoom() throws ManagerBeanException {
    	if (getAssetActivity() != null && getAssetActivity().getAsset() != null) {
        	return (Room)BeanManager.getManagerBean(Room.class).get(getAssetActivity().getAsset().getId());
    	}
    	return null;
    }

    @Transient
    public boolean isFirstNight() {
    	if (getAssetActivity() != null && getAssetActivity().getAsset() != null) {
    		return getProjectReservationRoom().getProjectReservation().getStartDate().equals(getAssetActivity().getDate());
    	}
    	return false;
    }

    @Transient
    public boolean isLastNight() {
    	if (getAssetActivity() != null && getAssetActivity().getAsset() != null) {
    		return DateUtils.addDays(getProjectReservationRoom().getProjectReservation().getEndDate(), -1).equals(getAssetActivity().getDate());
    	}
    	return false;
    }

    @Transient
    public boolean isInvoiced() {
    	return (getProjectReservationRoom() != null && getProjectReservationRoom().getProjectReservation().isInvoiced());
    }

    @Transient
    public String getRoomNumber(Date effectiveDate) throws ManagerBeanException {
    	if (getAssetActivity() != null && getAssetActivity().getAsset() != null) {
			IManagerBean reservationRoomDetailBean = BeanManager.getManagerBean(ProjectReservationRoomDetail.class);
			Criteria criteria = new Criteria();
			String alias = reservationRoomDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_PROJECT_RESERVATION_ROOM_ID);
			criteria.addEqualExpression(alias, getProjectReservationRoom().getId());
			criteria.addEqualExpression(reservationRoomDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_ASSET_ACTIVITY_DATE), effectiveDate);
			Projection prjName = Projection.property(reservationRoomDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_ASSET_ACTIVITY_ASSET_NAME));
			for (Object obj : reservationRoomDetailBean.getList(new ProjectionList(prjName), criteria)) {
				return (String)obj;
			}
    	}
		return null;
    }

}