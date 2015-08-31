package com.code.aon.warehouse.event;

import org.apache.commons.lang.ObjectUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.warehouse.WarehouseTransfer;
import com.code.aon.warehouse.enumeration.WarehouseTransferSource;

public class WarehouseTransferBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		WarehouseTransfer wt = (WarehouseTransfer) evt.getTo();
		check(wt);
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		WarehouseTransfer wt = (WarehouseTransfer) evt.getTo();
		check(wt);
	}

	private void check(WarehouseTransfer wt) throws ManagerBeanVetoListenerException{
		if (wt.getSource()==null) {
			wt.setSource(WarehouseTransferSource.DIRECT_TRANSFER);
		}
		if (wt.getSourceWarehouse() == null && wt.getTargetWarehouse() == null) {
			throw new ManagerBeanVetoListenerException("Debe indicar al menos un almacén origen y/o destino.");
		}
		Integer source = wt.getSourceWarehouse() == null?null:wt.getSourceWarehouse().getId(); 		
		Integer target = wt.getTargetWarehouse() == null?null:wt.getTargetWarehouse().getId();
		if (ObjectUtils.equals(source,target) ) {
			throw new ManagerBeanVetoListenerException("No se puede indicar el mismo almacén en origen y destino.");
		}
	}

}