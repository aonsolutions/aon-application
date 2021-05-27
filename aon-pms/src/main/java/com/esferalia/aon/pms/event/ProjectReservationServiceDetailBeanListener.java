package com.esferalia.aon.pms.event;

import org.hibernate.SQLQuery;
import org.hibernate.Session;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
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
    	if (isUpdateServiceNeeded(reservationService, reservationRoom)) {
    		reservationService.setProjectReservationRoom(reservationRoom.getId());
    		String stmt = "UPDATE project_reservation_service" +
    						" SET project_reservation_room = :reservationRoom" +
    						" WHERE id = :id";
    		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
    		SQLQuery query = session.createSQLQuery(stmt);
    		query.setInteger("id", reservationService.getId());
    		query.setInteger("reservationRoom", reservationRoom.getId());
    		query.executeUpdate();
    	}
    }

	private boolean isUpdateServiceNeeded(ProjectReservationService reservationService, ProjectReservationRoom reservationRoom) throws ManagerBeanException {
		String stmt = "SELECT 1" +
						" FROM project_reservation_service as project_reservation_service" +
    					" WHERE project_reservation_service.id = :id" +
    					" AND project_reservation_service.project_reservation_room IS NOT NULL " +
    					" AND project_reservation_service.project_reservation_room = :reservationRoom";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		SQLQuery query = session.createSQLQuery(stmt);
		query.setInteger("id", reservationService.getId());
		query.setInteger("reservationRoom", reservationRoom.getId());
        return query.list().isEmpty();
	}

}
