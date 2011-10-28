package com.code.aon.tas.event;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.company.Enterprise;
import com.code.aon.company.util.CompanyUtil;
import com.code.aon.tas.ProjectTas;
import com.code.aon.tas.enumeration.ProjectStatus;

public class ProjectTasBeanVetoListener extends ManagerBeanVetoListenerAdapter {

    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	ProjectTas to = (ProjectTas)evt.getTo();
    	to.getProject().setEnterprise(obtainEnterprise());
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

    private Enterprise obtainEnterprise() throws ManagerBeanVetoListenerException {
    	try {
	    	CompanyUtil companyUtil = new CompanyUtil();
	    	return companyUtil.getActiveEnterprise();
    	} catch (ManagerBeanException e) {
    		throw new ManagerBeanVetoListenerException(e.getMessage(), e);
    	}
    }

    private String obtainProjectTasName(ProjectTas to) {
    	String name = to.getReferenceCode() + " - " + to.getTasItem().getPublicCode() + " (" + to.getTasItem().getModel().getFullName() + ")";
    	return StringUtils.abbreviate(name, 64);
    }

}
