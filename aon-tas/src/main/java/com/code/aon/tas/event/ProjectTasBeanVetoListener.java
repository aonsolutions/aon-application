package com.code.aon.tas.event;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.tas.ProjectTas;
import com.code.aon.tas.enumeration.ProjectStatus;

public class ProjectTasBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	ProjectTas to = (ProjectTas)evt.getTo();
    	to.getProject().setRegistry(to.getTarget().getRegistry());
    	to.getProject().setProjectType(null);
	    to.getProject().setName(obtainProjectTasName(to));
    	to.getProject().setTas(true);
    	to.getProject().setActive(to.getStatus() == ProjectStatus.PENDING);
    }

    @Override
    public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	ProjectTas to = (ProjectTas)evt.getTo();
    	to.getProject().setRegistry(to.getTarget().getRegistry());
    	to.getProject().setName(obtainProjectTasName(to));
    	to.getProject().setTas(true);
    	to.getProject().setActive(to.getStatus() == ProjectStatus.PENDING);
    }

    private String obtainProjectTasName(ProjectTas to) {
    	String name = to.getReferenceCode() + " - " + to.getTasItem().getPublicCode() + " (" + to.getTasItem().getModel().getFullName() + ")";
    	return StringUtils.abbreviate(name, 64);
    }

}
