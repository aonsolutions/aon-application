package com.esferalia.aon.payroll.event;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.AonVersion;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.esferalia.aon.payroll.ContractData;

public class ContractDataVetoableBeanListener extends ManagerBeanVetoListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

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
	    // TODO chequear las palabras reservadas del script engine que se utilice.
		if (StringUtils.isEmpty( ce.getExpression())) {
			throw new ManagerBeanVetoListenerException("El valor de la variable es obligatorio.");
		}
	}

}
