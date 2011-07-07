package com.code.aon.tas.event;

import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.tas.ProjectTas;

public class ProjectTasBeanVetoListener extends ManagerBeanVetoListenerAdapter {

    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	ProjectTas to = (ProjectTas)evt.getTo();
    	to.getProject().setName(to.getReferenceCode() + " - " + to.getTasItem().getModel().getFullName());
    	to.getProject().setAlias(to.getTasItem().getPublicCode());
    	to.getProject().setTas(true);
    }

}
