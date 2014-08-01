package com.esferalia.aon.ui.payroll.event;

import static com.code.aon.ui.common.ICommonMessages.COMPANY_LOGO_MAX_SIZE_ERROR;
import static com.code.aon.ui.common.ICommonMessages.FILE_UPLOAD_ELEMENT;
import static com.code.aon.ui.company.controller.ICompanyConstants.LOGO_MAX_SIZE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.AonFile;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.TrainingCenter;
import com.esferalia.aon.ui.payroll.controller.TrainingCenterController;

/**
 * Listener added to the TrainingCenterController.
 */
public class TrainingCenterSignatureControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	/** The LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(TrainingCenterSignatureControllerListener.class.getName());
	
	private void checkAonFile( AonFile aonFile ) throws ControllerListenerException {
		if ( aonFile.getSize() <= 0 ) {			
			throw new ControllerListenerException( AonUtil.getMessage(FILE_UPLOAD_ELEMENT) );									
		} else if (aonFile.getSize() > LOGO_MAX_SIZE) {
			String message = AonUtil.getMessage(COMPANY_LOGO_MAX_SIZE_ERROR, LOGO_MAX_SIZE);
			throw new ControllerListenerException(message);										
		}
	}	
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		TrainingCenterController trainingCenterController = (TrainingCenterController) event.getController();
		trainingCenterController.setSignatureFile(null);
		trainingCenterController.setSignatureAttach(null);
	}
	
	/**
	 * Adds the TrainingCenter signature as a RegistryAttach if it is uploaded
	 * 
	 * @param event
	 *            the event
	 * 
	 * @throws ControllerListenerException
	 *             the controller listener exception
	 */
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		TrainingCenterController trainingCenterController = (TrainingCenterController) event.getController();
		AonFile aonFile = trainingCenterController.getSignatureFile();
		if ((aonFile != null) && aonFile.isDirty() ) {
			checkAonFile(aonFile);
			try {
				RegistryAttachment attach = new RegistryAttachment();
				attach.setRegistryAttachmentType(RegistryAttachmentType.SIGNATURE);
				attach.setCategory(null);
				attach.setData(aonFile.getData());
				attach.setDescription("aon-signature");
				attach.setRegistry(((TrainingCenter) event.getController().getTo()).getRegistry());
				attach.setMimeType(aonFile.getMimeType());
				IManagerBean attachBean = BeanManager.getManagerBean(RegistryAttachment.class);
				attachBean.insert(attach);
				trainingCenterController.setSignatureAttach(attach);
				aonFile.setAttachment(attach);				
			} catch (ManagerBeanException e) {
				LOGGER.error("Error updating signature", e);
			}
		}
	}

	/**
	 * Updates the TrainingCenter signature
	 * 
	 * @param event
	 *            the event
	 * 
	 * @throws ControllerListenerException
	 *             the controller listener exception
	 */
	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		TrainingCenterController trainingCenterController = (TrainingCenterController) event.getController();
		AonFile aonFile = trainingCenterController.getSignatureFile();
		if ((aonFile != null) && aonFile.isDirty() ) {
			checkAonFile(aonFile);
			try {
				RegistryAttachment attach = trainingCenterController.obtainTrainingCenterSignature();				
				if (attach == null) {
					attach = new RegistryAttachment();
				}
				attach.setRegistryAttachmentType(RegistryAttachmentType.SIGNATURE);
				attach.setCategory(null);
				attach.setData(aonFile.getData());
				attach.setDescription("aon-signature");
				attach.setRegistry(((TrainingCenter) event.getController().getTo()).getRegistry());
				attach.setMimeType(aonFile.getMimeType());
				IManagerBean attachBean = BeanManager.getManagerBean(RegistryAttachment.class);
				attachBean.insertOrUpdate(attach);
				trainingCenterController.setSignatureAttach(attach);
				aonFile.setAttachment(attach);
			} catch (ManagerBeanException e) {
				LOGGER.error("Error updating signature", e);
			}
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		TrainingCenterController trainingCenterController = (TrainingCenterController) event.getController();
		try {
			RegistryAttachment trainingCenterSignature = trainingCenterController.obtainTrainingCenterSignature();
			if (trainingCenterSignature != null) {
				trainingCenterController.setSignatureAttach(trainingCenterSignature);
				
				AonFile f = new AonFile();
				f.setAttachment(trainingCenterSignature);
				f.setFileName(trainingCenterSignature.getDescription());
				f.setMimeType(trainingCenterSignature.getMimeType());
				trainingCenterController.setSignatureFile(f);
			} else {
				trainingCenterController.setSignatureFile(null);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}
	
	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		TrainingCenterController trainingCenterController = (TrainingCenterController) event.getController();
		if (trainingCenterController.getSignatureAttach() != null) {
			try {
				IManagerBean attachBean = BeanManager.getManagerBean(RegistryAttachment.class);
				attachBean.remove(trainingCenterController.getSignatureAttach());
			} catch (ManagerBeanException e) {
				LOGGER.error("Error updating signature", e);
			}
		}
	}

}
