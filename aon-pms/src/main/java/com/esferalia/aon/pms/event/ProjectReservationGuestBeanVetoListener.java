package com.esferalia.aon.pms.event;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationGuest;

public class ProjectReservationGuestBeanVetoListener extends ManagerBeanVetoListenerAdapter {

    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	ProjectReservationGuest to = (ProjectReservationGuest)evt.getTo();
    	try {
    		if (to.getGuestIndex() == 0) {
        		to.setGuestIndex(calculateNextIndex(to.getProjectReservation()));
    		}
    	} catch (ManagerBeanException ex) {
    		throw new ManagerBeanVetoListenerException(ex);
    	}
    }

    private	Integer calculateNextIndex(ProjectReservation reservation) throws ManagerBeanException {
		String stmt = "SELECT MAX(guest_index)" +
				" FROM project_reservation_guest as project_reservation_guest" +
				" WHERE project_reservation_guest.project_reservation = :project";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		SQLQuery query = session.createSQLQuery(stmt);
		query.setInteger("project", reservation.getId());
		List<?> list = query.list();
		return !list.isEmpty() ? ((Byte)list.get(0)).intValue() + 1 : 1; 
	}

}
