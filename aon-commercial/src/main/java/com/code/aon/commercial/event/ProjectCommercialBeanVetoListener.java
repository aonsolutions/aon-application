package com.code.aon.commercial.event;

import com.code.aon.commercial.ProjectCommercial;
import com.code.aon.commercial.enumeration.ProjectStatus;
import com.code.aon.AonVersion;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;

public class ProjectCommercialBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	ProjectCommercial to = (ProjectCommercial)evt.getTo();
    	to.getProject().setRegistry(to.getTarget().getRegistry());
    	to.getProject().setCommercial(true);
    	to.getProject().setActive(to.getStatus() == ProjectStatus.PENDING || to.getStatus() == ProjectStatus.APPROVED);
    }

    @Override
    public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	ProjectCommercial to = (ProjectCommercial)evt.getTo();
    	to.getProject().setRegistry(to.getTarget().getRegistry());
    	to.getProject().setCommercial(true);
    	to.getProject().setActive(to.getStatus() == ProjectStatus.PENDING || to.getStatus() == ProjectStatus.APPROVED);
    }

}
