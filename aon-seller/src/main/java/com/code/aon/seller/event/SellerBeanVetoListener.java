package com.code.aon.seller.event;

import com.code.aon.AonVersion;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.seller.Seller;
import com.code.aon.seller.enumeration.SellerStatus;

public class SellerBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	Seller to = (Seller)evt.getTo();
    	if (to.getStatus() == null) {
    		to.setStatus(SellerStatus.ACTIVE);
    	}
    }

}
