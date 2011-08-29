package com.code.aon.tas.event;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.tas.ProjectTas;

public class ProjectTasBeanVetoListener extends ManagerBeanVetoListenerAdapter {

    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	ProjectTas to = (ProjectTas)evt.getTo();
    	to.getProject().setName(obtainProjectTasName(to));
    	to.getProject().setRegistry(to.getTarget().getRegistry());
    	to.getProject().setTas(true);
    }

    private String obtainProjectTasName(ProjectTas to) {
    	String name = to.getReferenceCode() + " - " + to.getTasItem().getPublicCode() + " (" + to.getTasItem().getModel().getFullName() + ")";
    	return StringUtils.abbreviate(name, 64);
    }

}
