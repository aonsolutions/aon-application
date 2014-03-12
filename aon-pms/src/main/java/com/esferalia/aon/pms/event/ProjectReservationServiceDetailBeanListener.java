package com.esferalia.aon.pms.event;

import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.esferalia.aon.pms.ProjectReservationRoom;
import com.esferalia.aon.pms.ProjectReservationService;
import com.esferalia.aon.pms.ProjectReservationServiceDetail;

public class ProjectReservationServiceDetailBeanListener extends ManagerBeanListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

    @Override
    public void beanInserted(ManagerBeanEvent evt) throws ManagerBeanException {
    	ProjectReservationServiceDetail to = (ProjectReservationServiceDetail)evt.getTo();
    	if (to.getProjectReservationRoomDetail() != null && to.getProjectReservationRoomDetail().getId() != null) {
    		updateLinkedServiceRoom(to.getProjectReservationService(), to.getProjectReservationRoomDetail().getProjectReservationRoom());
    	}
    }

    @Override
    public void beanUpdated(ManagerBeanEvent evt) throws ManagerBeanException {
    	ProjectReservationServiceDetail to = (ProjectReservationServiceDetail)evt.getTo();
    	if (to.getProjectReservationRoomDetail() != null && to.getProjectReservationRoomDetail().getId() != null) {
    		updateLinkedServiceRoom(to.getProjectReservationService(), to.getProjectReservationRoomDetail().getProjectReservationRoom());
    	}
    }

    private void updateLinkedServiceRoom(ProjectReservationService reservationService, ProjectReservationRoom reservationRoom) throws ManagerBeanException {
    	if (reservationService.getProjectReservationRoom() == null || reservationService.getProjectReservationRoom() != reservationRoom.getId()) {
    		reservationService.setProjectReservationRoom(reservationRoom.getId());
    		BeanManager.getManagerBean(ProjectReservationService.class).update(reservationService);
    	}
    }

}
