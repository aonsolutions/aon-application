package com.code.aon.finance.event;

import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.finance.Creditor;
import com.code.aon.finance.enumeration.CreditorStatus;

public class CreditorBeanVetoListener extends ManagerBeanVetoListenerAdapter {

    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	Creditor to = (Creditor)evt.getTo();
    	if (to.getStatus() == null) {
    		to.setStatus(CreditorStatus.ACTIVE);
    	}
    	if (to.getTransaction() == null) {
    		to.setTransaction(InvoiceTransactionType.NATIONAL);
    	}
    }

}
