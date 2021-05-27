package com.code.aon.accounting.event;

import com.code.aon.accounting.Loan;
import com.code.aon.AonVersion;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;

/**
 * @author Consulting & Development
 *
 */
public class LoanBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	check(evt);
    }
	
	@Override
    public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		check(evt);
    }

	private void check(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException{
		Loan loan = (Loan) evt.getTo();
		try {
			Integer.parseInt( loan.getTerm() );
		} catch (NumberFormatException e) {
			String msg = "El plazo de la operación no es un valor numérico válido";
			throw new ManagerBeanVetoListenerException(msg);
		}
		if (loan.getSecurityLevel() == null) {
			loan.setSecurityLevel(SecurityLevel.OFFICIAL);
		}
	}
	
}
