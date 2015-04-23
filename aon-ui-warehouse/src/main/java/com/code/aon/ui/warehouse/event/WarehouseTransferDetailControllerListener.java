package com.code.aon.ui.warehouse.event;

import com.code.aon.AonVersion;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.warehouse.WarehouseTransfer;
import com.code.aon.warehouse.WarehouseTransferDetail;

public class WarehouseTransferDetailControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		LinesController detail = (LinesController) event.getController();
		IController master = detail.getMasterController(); 
		WarehouseTransfer wt  = (WarehouseTransfer) master.getTo();
		WarehouseTransferDetail wtd = (WarehouseTransferDetail) detail.getTo();
		wtd.setWarehouseTransfer(wt);
	}
}