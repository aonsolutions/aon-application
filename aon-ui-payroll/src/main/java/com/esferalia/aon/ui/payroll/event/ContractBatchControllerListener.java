package com.esferalia.aon.ui.payroll.event;

import java.util.Date;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.ContractBatch;
import com.esferalia.aon.payroll.enumeration.FileStatus;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.batch.ContractBatchController;
import com.esferalia.aon.ui.payroll.controller.batch.ContractListController;

/**
 * Listener added to the ContractBatchController
 * 
 */
public class ContractBatchControllerListener extends ControllerAdapter {
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		ContractBatchController controller = (ContractBatchController) this.getController();
		controller.setRecorded(false);
		ContractBatch batch = (ContractBatch) controller.getTo();
		batch.setStatus(FileStatus.PENDING);
		batch.setDate(new Date());
		controller.setNewBatchWizard( null );
		controller.getNewBatchWizard().init();
		ContractListController list = (ContractListController) FormUtil.getController(IPayrollConstants.CONTRACT_LIST_CONTROLLER_NAME);
		list.setSearchPanelExpanded(true);
		try {
			controller.onSearchContracts(null);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("No se ha podido recargar la lista de contratos");
		}
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		ContractBatchController controller = (ContractBatchController) this.getController();
		try {
			controller.onSearchContracts(null);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("No se ha podido recargar la lista de contratos");
		}
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		ContractBatchController controller = (ContractBatchController) this.getController();
		controller.onInit(null);
	}

}
