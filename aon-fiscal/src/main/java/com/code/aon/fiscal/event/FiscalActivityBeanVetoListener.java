package com.code.aon.fiscal.event;

import com.code.aon.AonVersion;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.fiscal.FiscalActivity;

public class FiscalActivityBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		FiscalActivity fiscalModel = (FiscalActivity) evt.getTo();
		if ( fiscalModel.getYear() > 2017) {
			throw new ManagerBeanVetoListenerException("A partir del ejercicio 2018, las datos de actividades se introducen directamente en los modelos fiscales.");
		}
	}


}
