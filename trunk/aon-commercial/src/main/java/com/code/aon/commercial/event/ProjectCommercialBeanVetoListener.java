package com.code.aon.commercial.event;

import com.code.aon.commercial.ProjectCommercial;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;

public class ProjectCommercialBeanVetoListener extends ManagerBeanVetoListenerAdapter {

    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	ProjectCommercial to = (ProjectCommercial)evt.getTo();
    	to.getProject().setRegistry(to.getTarget().getRegistry());
    	to.getProject().setProjectType(null);
    	to.getProject().setCommercial(true);
    }

}
