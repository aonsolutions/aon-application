package com.code.aon.accounting.event;

import com.code.aon.account.Account;
import com.code.aon.accounting.AmortizationType;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;

public class AmortizationTypeVetoableBeanListener extends ManagerBeanVetoListenerAdapter  {


	@Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	AmortizationType to = (AmortizationType) evt.getTo();
    	check(to);
    }

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	AmortizationType to = (AmortizationType) evt.getTo();
    	check(to);
	}


	private void check(AmortizationType to) throws ManagerBeanVetoListenerException {
		double p = to.getPercentage();
		if (p < 0 || p > 100) {
			throw new ManagerBeanVetoListenerException("El porcentaje no puede ser menor de cero ni mayor de cien.");
		}
		checkAccount(to.getAccumulatedAccount(),"Cuenta de acumulado");
		checkAccount(to.getAllocationAccount(),"Cuenta de dotación");
		checkAccount(to.getFixedAssetAccount(),"Cuenta de inmovilizado");
		
	}

	private void checkAccount(Account account, String text) throws ManagerBeanVetoListenerException {
		if (account == null) {
			throw new ManagerBeanVetoListenerException("La "+ text + " es un dato requerido.");
		}
		if (account.getLevel() != 4) {
			throw new ManagerBeanVetoListenerException("La "+ text + " debe ser un cuenta de cuatro dígitos.");
		}
	}
}