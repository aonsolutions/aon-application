package com.esferalia.aon.ui.sepe.event;

import java.util.Date;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Certifica2Batch;
import com.esferalia.aon.payroll.enumeration.FileStatus;
import com.esferalia.aon.ui.sepe.controller.CertificadosController;
import com.esferalia.aon.ui.sepe.controller.ISepeConstants;
import com.esferalia.aon.ui.sepe.controller.batch.Certifica2BatchController;
import com.esferalia.aon.ui.sepe.utils.SEPEUtils;

/**
 * Listener added to the Certifica2BatchController
 * 
 */
public class Certifica2BatchControllerListener extends ControllerAdapter {
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		SEPEUtils utils = new SEPEUtils();
		Certifica2BatchController controller = (Certifica2BatchController) this.getController();
		controller.setRecorded(false);
		Certifica2Batch batch = (Certifica2Batch) controller.getTo();
		batch.setStatus(FileStatus.PENDING);
		batch.setDate(new Date());
		batch.setEnterprise(utils.getCurrentDomainEnterprise());
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		Certifica2BatchController controller = (Certifica2BatchController) this.getController();
		try {
			controller.onSearchContracts(null);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("error on onAccept ["+e.getMessage()+"]");
		}
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		Certifica2BatchController controller = (Certifica2BatchController) this.getController();
		controller.onInit(null);
		
		Certifica2Batch batch =  (Certifica2Batch) controller.getTo();
		if(batch.getStatus() == FileStatus.GENERATED){
			CertificadosController certificadosController = (CertificadosController) AonUtil.getRegisteredBean(ISepeConstants.CONTRACT_CERTIFICADOS_CONTROLLER_NAME);
			certificadosController.initialize(batch);
		}
		
	}
	
}
