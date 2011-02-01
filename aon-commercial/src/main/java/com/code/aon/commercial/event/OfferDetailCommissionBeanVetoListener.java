package com.code.aon.commercial.event;

import java.util.Date;

import com.code.aon.commercial.OfferDetailCommission;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;

public class OfferDetailCommissionBeanVetoListener extends ManagerBeanVetoListenerAdapter {

    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	OfferDetailCommission odc = (OfferDetailCommission) evt.getTo();
    	initializePayDate(odc);
    }

    @Override
    public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	OfferDetailCommission odc = (OfferDetailCommission) evt.getTo();
    	initializePayDate(odc);
    }

	private void initializePayDate(OfferDetailCommission odc) {
		if (odc.getPayDate() == null) {
			odc.setPayDate( new Date() );
		}
	}
    
}
