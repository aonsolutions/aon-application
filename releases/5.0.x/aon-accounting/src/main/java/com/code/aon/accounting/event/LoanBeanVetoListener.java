package com.code.aon.accounting.event;

import com.code.aon.accounting.Loan;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;

/**
 * @author Consulting & Development
 *
 */
public class LoanBeanVetoListener extends ManagerBeanVetoListenerAdapter {

    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	check(evt);
    }
	
	@Override
    public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		check(evt);
    }

	private void check(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException{
		try {
			Loan loan = (Loan) evt.getTo();
			Integer.parseInt( loan.getTerm() );
		} catch (NumberFormatException e) {
			String msg = "El plazo de la operación no es un valor numérico válido";
			throw new ManagerBeanVetoListenerException(msg);
		}
		
	}
	
}
