package com.code.aon.supplier.event;

import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.supplier.Supplier;
import com.code.aon.supplier.enumeration.SupplierStatus;

public class SupplierBeanVetoListener extends ManagerBeanVetoListenerAdapter {

    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	Supplier to = (Supplier)evt.getTo();
    	if (to.getStatus() == null) {
    		to.setStatus(SupplierStatus.ACTIVE);
    	}
    	if (to.getTransaction() == null) {
    		to.setTransaction(InvoiceTransactionType.NATIONAL);
    	}
    }

}
