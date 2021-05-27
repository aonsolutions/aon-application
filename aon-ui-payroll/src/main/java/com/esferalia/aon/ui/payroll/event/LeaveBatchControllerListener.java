package com.esferalia.aon.ui.payroll.event;

import java.util.Date;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.LeaveBatch;
import com.esferalia.aon.payroll.enumeration.FileStatus;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.batch.LeaveBatchController;
import com.esferalia.aon.ui.payroll.controller.batch.LeaveListController;

/**
 * Listener added to the ContractBatchController
 * 
 */
public class LeaveBatchControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		LeaveBatchController controller = (LeaveBatchController) this.getController();
		LeaveBatch batch = (LeaveBatch) controller.getTo();
		batch.setStatus(FileStatus.PENDING);
		batch.setDate(new Date());
		controller.setNewBatchWizard( null );
		controller.getNewBatchWizard().init();
		LeaveListController list = (LeaveListController) FormUtil.getController(IPayrollConstants.LEAVE_LIST_CONTROLLER_NAME);
		list.setSearchPanelExpanded(true);
		try {
			controller.onSearchLeaves(null);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("No se ha podido recargar la lista de partes de IT");
		}
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		LeaveBatchController controller = (LeaveBatchController) this.getController();
		try {
			controller.onSearchLeaves(null);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("No se ha podido recargar la lista de partes de IT");
		}
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		LeaveBatchController controller = (LeaveBatchController) this.getController();
		controller.onInit(null);
	}

}
