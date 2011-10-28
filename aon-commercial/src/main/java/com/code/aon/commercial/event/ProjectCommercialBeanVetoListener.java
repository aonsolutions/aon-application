package com.code.aon.commercial.event;

import com.code.aon.commercial.ProjectCommercial;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.company.Enterprise;
import com.code.aon.company.util.CompanyUtil;

public class ProjectCommercialBeanVetoListener extends ManagerBeanVetoListenerAdapter {

    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	ProjectCommercial to = (ProjectCommercial)evt.getTo();
    	to.getProject().setEnterprise(obtainEnterprise());
    	to.getProject().setRegistry(to.getTarget().getRegistry());
    	to.getProject().setProjectType(null);
    	to.getProject().setCommercial(true);
    }

    private Enterprise obtainEnterprise() throws ManagerBeanVetoListenerException {
    	try {
	    	CompanyUtil companyUtil = new CompanyUtil();
	    	return companyUtil.getActiveEnterprise();
    	} catch (ManagerBeanException e) {
    		throw new ManagerBeanVetoListenerException(e.getMessage(), e);
    	}
    }

}
