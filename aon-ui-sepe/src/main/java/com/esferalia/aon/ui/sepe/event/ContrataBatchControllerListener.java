package com.esferalia.aon.ui.sepe.event;

import java.util.Date;

import com.code.aon.AonVersion;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.ContrataBatch;
import com.esferalia.aon.payroll.enumeration.ContrataFileType;
import com.esferalia.aon.payroll.enumeration.FileStatus;
import com.esferalia.aon.ui.sepe.controller.ContrataContratosController;
import com.esferalia.aon.ui.sepe.controller.ContrataProrrogasController;
import com.esferalia.aon.ui.sepe.controller.ContrataTransformacionesController;
import com.esferalia.aon.ui.sepe.controller.ISepeConstants;
import com.esferalia.aon.ui.sepe.controller.batch.ContrataBatchController;
import com.esferalia.aon.ui.sepe.controller.batch.ContrataListController;

/**
 * Listener added to the ContrataBatchController
 * 
 */
public class ContrataBatchControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		ContrataBatchController controller = (ContrataBatchController) this.getController();
		controller.setRecorded(false);
		ContrataBatch batch = (ContrataBatch) controller.getTo();
		batch.setDate(new Date());
		batch.setStatus(FileStatus.PENDING);
		controller.setNewBatchWizard( null );
		controller.getNewBatchWizard().init();
		ContrataListController list = (ContrataListController) FormUtil.getController(ISepeConstants.CONTRATA_LIST_CONTROLLER_NAME);
		list.setSearchPanelExpanded(true);
		controller.onSearchContracts(null);
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		ContrataListController list = (ContrataListController) FormUtil.getController(ISepeConstants.CONTRATA_LIST_CONTROLLER_NAME);
		list.onSearch(null);
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		ContrataBatchController controller = (ContrataBatchController) this.getController();
		controller.onInit(null);
		ContrataBatch batch = (ContrataBatch) controller.getTo();
		if(batch.getStatus() == FileStatus.GENERATED){
			if(batch.getType()==ContrataFileType.CONTRACT){
				ContrataContratosController contrataController = (ContrataContratosController) AonUtil.getRegisteredBean(ISepeConstants.CONTRACT_CONTRATA_CONTROLLER_NAME);
				contrataController.initialize(batch);
			} else if(batch.getType()==ContrataFileType.EXTENSION){
				ContrataProrrogasController contrataController = (ContrataProrrogasController) AonUtil.getRegisteredBean(ISepeConstants.EXTENSION_CONTRATA_CONTROLLER_NAME);
				contrataController.initialize(batch);
			} else if(batch.getType()==ContrataFileType.TRANSFORMATION){
				ContrataTransformacionesController contrataController = (ContrataTransformacionesController) AonUtil.getRegisteredBean(ISepeConstants.TRANSFORM_CONTRATA_CONTROLLER_NAME);
				contrataController.initialize(batch);
			}
		}
	}
	
}
