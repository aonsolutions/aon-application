package com.esferalia.aon.ui.payroll.event;

import java.util.Calendar;
import java.util.Date;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.FanBatch;
import com.esferalia.aon.payroll.enumeration.FileStatus;
import com.esferalia.aon.payroll.enumeration.LiquidationType;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.batch.FanBatchController;
import com.esferalia.aon.ui.payroll.controller.batch.FanListController;

/**
 * Listener added to the ContractBatchController
 * 
 */
public class FanBatchControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		FanBatchController controller = (FanBatchController) this.getController();
		FanBatch batch = (FanBatch) controller.getTo();
		batch.setStatus(FileStatus.PENDING);
		batch.setLiquidationType(LiquidationType.L00);
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.MONTH, -1);
		batch.setDate(cal.getTime());
		controller.setNewBatchWizard( null );
		controller.getNewBatchWizard().init();
		FanListController list = (FanListController) FormUtil.getController(IPayrollConstants.FAN_LIST_CONTROLLER_NAME);
		list.setSearchPanelExpanded(false);
		try {
			controller.onSearchCCCs(null);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("No se ha podido recargar la lista de contratos");
		}
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		FanBatchController controller = (FanBatchController) this.getController();
		try {
			controller.onSearchCCCs(null);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("No se ha podido recargar la lista de contratos");
		}
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		FanBatchController controller = (FanBatchController) this.getController();
		controller.onInit(null);
	}

}
