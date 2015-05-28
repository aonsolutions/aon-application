package com.code.aon.ui.warehouse.controller;

import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.config.controller.ConfigCollectionsController;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.config.controller.HeaderObjectController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.WarehouseTransfer;

public class WarehouseTransferController extends HeaderObjectController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void onReset(ActionEvent event) {
		super.onReset(event);
		initSeries();
	}

	@Override
	public List<SelectItem> getSeriesCodes() throws ManagerBeanException {
		ConfigCollectionsController ccc = (ConfigCollectionsController) AonUtil.getRegisteredBean(ConfigConstants.CONFIG_COLLECTIONS);
		return ccc.getDeliverySeriesIds();
	}		
	
	public void onGoToInventory( ActionEvent event ) throws ManagerBeanException {
		WarehouseTransfer wt = (WarehouseTransfer) getTo();
		InventoryController ic = (InventoryController) AonUtil.getRegisteredBean(IWarehouseConstants.INVENTORY_CONTROLLER_NAME);
		ic.select(event, wt.getInventory());
		ic.setBackAction(formAction());
	}
	
	public boolean isReadOnly() {
		WarehouseTransfer wt = (WarehouseTransfer) getTo();
		return (wt.getInventory() != null) && (wt.getInventory().getId() != null);
	}
	
}