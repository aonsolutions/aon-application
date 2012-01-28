package com.esferalia.aon.pms.event;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationService;

public class ProjectReservationServiceBeanVetoListener extends ManagerBeanVetoListenerAdapter {

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
		IManagerBean reservationServiceBean = BeanManager.getManagerBean(ProjectReservationService.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(reservationServiceBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_PROJECT_RESERVATION_ID), reservation.getId());
		Projection projection = Projection.max(reservationServiceBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_SERVICE_INDEX));
		Object value = reservationServiceBean.getUniqueResult(projection, criteria);
		return (value != null) ? ((Integer)value) + 1 : 1;
	}

}
