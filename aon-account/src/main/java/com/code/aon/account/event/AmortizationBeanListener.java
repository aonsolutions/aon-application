package com.code.aon.account.event;

import com.code.aon.account.Amortization;
import com.code.aon.account.amortization.AmortizationManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;

public class AmortizationBeanListener extends ManagerBeanListenerAdapter {


	@Override
    public void beanInserted(ManagerBeanEvent evt) throws ManagerBeanException {
    	Amortization to = (Amortization) evt.getTo();
    	AmortizationManager am = new AmortizationManager();
    	am.generateDetails(to);
    }

	@Override
	public void beanUpdated(ManagerBeanEvent evt) throws ManagerBeanException {
    	Amortization to = (Amortization) evt.getTo();
    	AmortizationManager am = new AmortizationManager();
    	am.deleteDetails(to);
    	am.generateDetails(to);
	}
}