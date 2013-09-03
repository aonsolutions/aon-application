package com.esferalia.aon.ui.payroll.event;

import static com.code.aon.ui.common.ICommonMessages.FILE_UPLOAD_ELEMENT;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.common.util.MimeResolver;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.TrainingCenter;
import com.esferalia.aon.ui.payroll.controller.TrainingCenterController;

/**
 * Listener added to the TrainingCenterController.
 */
public class TrainingCenterLogoControllerListener extends ControllerAdapter {

	/** The LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(TrainingCenterLogoControllerListener.class.getName());
	
	private void checkAonFile( AonFile aonFile ) throws ControllerListenerException {
		if ( ArrayUtils.isEmpty(aonFile.getData()) ) {
			throw new ControllerListenerException( AonUtil.getMessage(FILE_UPLOAD_ELEMENT) );									
		} else if (aonFile.getSize() > ICompanyConstants.LOGO_MAX_SIZE) {
			String message = AonUtil.getMessage(ICompanyConstants.BUNDLE_NAME, ICompanyConstants.COMPANY_LOGO_MAX_SIZE_ERROR, ICompanyConstants.LOGO_MAX_SIZE);
			throw new ControllerListenerException(message);										
		}
	}	
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		TrainingCenterController trainingCenterController = (TrainingCenterController) event.getController();
		trainingCenterController.setLogoFile(null);
		trainingCenterController.setLogoAttach(null);
	}
	
	/**
	 * Adds the TrainingCenter logo as a RegistryAttach if it is uploaded
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
		if (trainingCenterController.getLogoFile() != null) {
			checkAonFile(trainingCenterController.getLogoFile());
			try {
				AonFile aonFile = trainingCenterController.getLogoFile();
				RegistryAttachment attach = new RegistryAttachment();
				attach.setRegistryAttachmentType(RegistryAttachmentType.LOGO);
				attach.setCategory(null);
				attach.setData(aonFile.getData());
				attach.setDescription("aon-logo");
				attach.setRegistry(((TrainingCenter) event.getController().getTo()).getRegistry());
				MimeType mt = getMimeType(aonFile.getFileName(), aonFile.getData());
				attach.setMimeType(mt);
				IManagerBean attachBean = BeanManager.getManagerBean(RegistryAttachment.class);
				trainingCenterController.setLogoAttach((RegistryAttachment) attachBean.insert(attach));
			} catch (ManagerBeanException e) {
				LOGGER.error("Error updating logo", e);
			}
		}
	}

	/**
	 * Updates the TrainingCenter logo
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
		if (trainingCenterController.getLogoFile() != null) {
			checkAonFile(trainingCenterController.getLogoFile());
			try {
				AonFile aonFile = trainingCenterController.getLogoFile();
				RegistryAttachment attach = trainingCenterController.obtainTrainingCenterLogo();				
				if (attach == null) {
					attach = new RegistryAttachment();
				}
				attach.setRegistryAttachmentType(RegistryAttachmentType.LOGO);
				attach.setCategory(null);
				attach.setData(aonFile.getData());
				attach.setDescription("aon-logo");
				attach.setRegistry(((TrainingCenter) event.getController().getTo()).getRegistry());
				MimeType mt = getMimeType(aonFile.getFileName(), aonFile.getData());
				attach.setMimeType(mt);
				IManagerBean attachBean = BeanManager.getManagerBean(RegistryAttachment.class);
				if (attach.getId() == null) {
					trainingCenterController.setLogoAttach((RegistryAttachment) attachBean.insert(attach));
				} else {
					trainingCenterController.setLogoAttach((RegistryAttachment) attachBean.update(attach));
				}
			} catch (ManagerBeanException e) {
				LOGGER.error("Error updating logo", e);
			}
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		TrainingCenterController trainingCenterController = (TrainingCenterController) event.getController();
		try {
			RegistryAttachment trainingCenterLogo = trainingCenterController.obtainTrainingCenterLogo();
			if (trainingCenterLogo != null) {
				trainingCenterController.setLogoAttach(trainingCenterLogo);

				AonFile f = new AonFile();
				f.setKey(trainingCenterLogo.getId());
				f.setData(trainingCenterLogo.getData());
				f.setFileName(trainingCenterLogo.getDescription());
				f.setMimeType(trainingCenterLogo.getMimeType());
				trainingCenterController.setLogoFile(f);
			} else {
				trainingCenterController.setLogoFile(null);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}
	
	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		TrainingCenterController trainingCenterController = (TrainingCenterController) event.getController();
		if (trainingCenterController.getLogoAttach() != null) {
			try {
				IManagerBean attachBean = BeanManager.getManagerBean(RegistryAttachment.class);
				attachBean.remove(trainingCenterController.getLogoAttach());
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
