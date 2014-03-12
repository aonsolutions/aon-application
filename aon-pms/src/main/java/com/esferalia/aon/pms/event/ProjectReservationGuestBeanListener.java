package com.esferalia.aon.pms.event;

import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationGuest;

public class ProjectReservationGuestBeanListener extends ManagerBeanListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

    @Override
    public void beanInserted(ManagerBeanEvent evt) throws ManagerBeanException {
    	ProjectReservationGuest to = (ProjectReservationGuest)evt.getTo();
		if (to.getGuestIndex() == 1) {
			IManagerBean reservationBean = BeanManager.getManagerBean(ProjectReservation.class);
			reservationBean.update(to.getProjectReservation());
		}
    }

    @Override
    public void beanUpdated(ManagerBeanEvent evt) throws ManagerBeanException {
    	ProjectReservationGuest to = (ProjectReservationGuest)evt.getTo();
		if (to.getGuestIndex() == 1) {
			IManagerBean reservationBean = BeanManager.getManagerBean(ProjectReservation.class);
			reservationBean.update(to.getProjectReservation());
		}
    }

}
