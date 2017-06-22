package com.esferalia.aon.pms.event;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationService;

public class ProjectReservationServiceBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	ProjectReservationService to = (ProjectReservationService)evt.getTo();
    	try {
    		if (to.getServiceIndex() == 0) {
        		to.setServiceIndex(calculateNextIndex(to.getProjectReservation()));
    		}
    		if (StringUtils.isEmpty(to.getDescription())) {
    			to.setDescription(to.getItem().getProduct().getName());
    		}
    	} catch (ManagerBeanException ex) {
    		throw new ManagerBeanVetoListenerException(ex);
    	}
    }

    @Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	ProjectReservationService to = (ProjectReservationService)evt.getTo();
		if (StringUtils.isEmpty(to.getDescription())) {
			to.setDescription(to.getItem().getProduct().getName());
		}
	}

    private	Integer calculateNextIndex(ProjectReservation reservation) throws ManagerBeanException {
		String stmt = "SELECT MAX(service_index)" +
						" FROM project_reservation_service as project_reservation_service" +
						" WHERE project_reservation_service.project_reservation = :project" +
						" AND project_reservation_service.removed = 0";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		SQLQuery query = session.createSQLQuery(stmt);
		query.setInteger("project", reservation.getId());
		List<?> list = query.list();
		return !list.isEmpty() && !list.contains(null) ? ((Byte)list.get(0)).intValue() + 1 : 1; 
	}

}
