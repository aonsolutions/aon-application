package com.esferalia.aon.pms;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.esferalia.aon.entity.master.ProjectReservationRoomDetailDB;

@Entity
@Table(name="project_reservation_room_detail")
public class ProjectReservationRoomDetail extends ProjectReservationRoomDetailDB {

	private static final long serialVersionUID = 1L;

    @Transient
    public Room getRoom() throws ManagerBeanException {
    	if (getAssetActivity() != null && getAssetActivity().getAsset() != null) {
        	return (Room)BeanManager.getManagerBean(Room.class).get(getAssetActivity().getAsset().getId());
    	}
    	return null;
    }

}