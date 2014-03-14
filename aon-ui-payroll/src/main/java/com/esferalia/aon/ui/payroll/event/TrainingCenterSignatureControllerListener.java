package com.esferalia.aon.ui.payroll.event;

import static com.code.aon.ui.common.ICommonMessages.COMPANY_LOGO_MAX_SIZE_ERROR;
import static com.code.aon.ui.common.ICommonMessages.FILE_UPLOAD_ELEMENT;
import static com.code.aon.ui.company.controller.ICompanyConstants.LOGO_MAX_SIZE;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.common.util.MimeResolver;
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
		if ( ArrayUtils.isEmpty(aonFile.getData()) ) {
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
		if (trainingCenterController.getSignatureFile() != null) {
			checkAonFile(trainingCenterController.getSignatureFile());
			try {
				AonFile aonFile = trainingCenterController.getSignatureFile();
				RegistryAttachment attach = new RegistryAttachment();
				attach.setRegistryAttachmentType(RegistryAttachmentType.SIGNATURE);
				attach.setCategory(null);
				attach.setData(aonFile.getData());
				attach.setDescription("aon-signature");
				attach.setRegistry(((TrainingCenter) event.getController().getTo()).getRegistry());
				MimeType mt = getMimeType(aonFile.getFileName(), aonFile.getData());
				attach.setMimeType(mt);
				IManagerBean attachBean = BeanManager.getManagerBean(RegistryAttachment.class);
				trainingCenterController.setSignatureAttach((RegistryAttachment) attachBean.insert(attach));
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
		if (trainingCenterController.getSignatureFile() != null) {
			checkAonFile(trainingCenterController.getSignatureFile());
			try {
				AonFile aonFile = trainingCenterController.getSignatureFile();
				RegistryAttachment attach = trainingCenterController.obtainTrainingCenterSignature();				
				if (attach == null) {
					attach = new RegistryAttachment();
				}
				attach.setRegistryAttachmentType(RegistryAttachmentType.SIGNATURE);
				attach.setCategory(null);
				attach.setData(aonFile.getData());
				attach.setDescription("aon-signature");
				attach.setRegistry(((TrainingCenter) event.getController().getTo()).getRegistry());
				MimeType mt = getMimeType(aonFile.getFileName(), aonFile.getData());
				attach.setMimeType(mt);
				IManagerBean attachBean = BeanManager.getManagerBean(RegistryAttachment.class);
				if (attach.getId() == null) {
					trainingCenterController.setSignatureAttach((RegistryAttachment) attachBean.insert(attach));
				} else {
					trainingCenterController.setSignatureAttach((RegistryAttachment) attachBean.update(attach));
				}
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
				f.setKey(trainingCenterSignature.getId());
				f.setData(trainingCenterSignature.getData());
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

	private MimeType getMimeType(String resource, byte[] data) {
		MimeType mt = MimeResolver.getMimeTypeByExtension(resource);
		if ( mt == null ) {
			mt = MimeResolver.getMimeType(data);
		}
		return mt;
	}
	
}
