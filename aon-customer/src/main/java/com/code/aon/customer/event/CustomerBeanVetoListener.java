package com.code.aon.customer.event;

import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.customer.Customer;
import com.code.aon.customer.enumeration.CustomerStatus;

public class CustomerBeanVetoListener extends ManagerBeanVetoListenerAdapter {

    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	Customer to = (Customer)evt.getTo();
    	if (to.getStatus() == null) {
    		to.setStatus(CustomerStatus.ACTIVE);
    	}
    	if (to.getTransaction() == null) {
    		to.setTransaction(InvoiceTransactionType.NATIONAL);
    	}
    }

}
