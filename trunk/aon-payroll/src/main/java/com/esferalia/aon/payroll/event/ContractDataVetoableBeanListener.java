package com.esferalia.aon.payroll.event;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.esferalia.aon.payroll.ContractData;

public class ContractDataVetoableBeanListener extends ManagerBeanVetoListenerAdapter {

	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		ContractData ce = (ContractData) evt.getTo();
		validate(ce);
	}

	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		ContractData ce = (ContractData) evt.getTo();
		validate(ce);
	}

	private void validate(ContractData ce) throws ManagerBeanVetoListenerException {
		if (StringUtils.isEmpty( ce.getName())) {
			throw new ManagerBeanVetoListenerException("El nombre de la variable es obligatorio.");
		}
	    String regex = "^[a-zA-Z_][a-zA-Z0-9_]*$";
	    if (!ce.getName().matches(regex)) {
	    	throw new ManagerBeanVetoListenerException("El nombre de la variable no es un nombre válido.");
	    }
	    // TODO chequear las palabras reservadas del script engine que se utilice.
		if (StringUtils.isEmpty( ce.getExpression())) {
			throw new ManagerBeanVetoListenerException("El valor de la variable es obligatorio.");
		}
	}

}
