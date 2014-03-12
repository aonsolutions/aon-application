package com.esferalia.aon.pms;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
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

}