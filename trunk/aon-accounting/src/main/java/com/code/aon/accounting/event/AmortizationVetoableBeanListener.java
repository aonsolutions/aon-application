package com.code.aon.accounting.event;

import com.code.aon.accounting.Amortization;
import com.code.aon.accounting.amortization.AmortizationManager;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;

public class AmortizationVetoableBeanListener extends ManagerBeanVetoListenerAdapter  {


	@Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	Amortization to = (Amortization) evt.getTo();
    	AmortizationManager am = new AmortizationManager();
    	am.generateDetails(to);
    }

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	Amortization to = (Amortization) evt.getTo();
    	AmortizationManager am = new AmortizationManager();
    	am.deleteDetails(to);
    	am.generateDetails(to);
	}
}