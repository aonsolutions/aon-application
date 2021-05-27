package com.code.aon.ui.registry.controller.event;

import static com.code.aon.ui.common.ICommonMessages.COMPANY_LOGO_MAX_SIZE_ERROR;
import static com.code.aon.ui.common.ICommonMessages.FILE_UPLOAD_ELEMENT;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.AonFile;
import com.code.aon.person.Person;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.registry.controller.PersonController;
import com.code.aon.ui.util.AonUtil;

public class PersonLogoControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public static final int LOGO_MAX_SIZE = 262144;

	/** The LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(PersonLogoControllerListener.class.getName());
	
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
		PersonController controller = (PersonController) event.getController();
		controller.setLogoFile(null);
		controller.setLogoAttach(null);
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
		PersonController controller = (PersonController) event.getController();
		AonFile aonFile = controller.getLogoFile(); 
		if ((aonFile != null) && aonFile.isDirty() ) {
			checkAonFile(aonFile);
			try {
				RegistryAttachment attach = new RegistryAttachment();
				attach.setRegistryAttachmentType(RegistryAttachmentType.LOGO);
				attach.setCategory(null);
				attach.setData(aonFile.getData());
				attach.setDescription("aon-logo");
				attach.setRegistry(((Person) event.getController().getTo()).getRegistry());
				attach.setMimeType(aonFile.getMimeType());
				IManagerBean attachBean = BeanManager.getManagerBean(RegistryAttachment.class);
				attachBean.insert(attach);
				controller.setLogoAttach(attach);
				aonFile.setAttachment(attach);				
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
		PersonController controller = (PersonController) event.getController();
		AonFile aonFile = controller.getLogoFile();		
		if ((aonFile != null) && aonFile.isDirty() ) {
			checkAonFile(aonFile);
			try {
				RegistryAttachment attach = controller.obtainPersonLogo();				
				if (attach == null) {
					attach = new RegistryAttachment();
				}
				attach.setRegistryAttachmentType(RegistryAttachmentType.LOGO);
				attach.setCategory(null);
				attach.setData(aonFile.getData());
				attach.setDescription("aon-logo");
				attach.setRegistry(((Person) event.getController().getTo()).getRegistry());
				attach.setMimeType(aonFile.getMimeType());
				IManagerBean attachBean = BeanManager.getManagerBean(RegistryAttachment.class);
				attachBean.insertOrUpdate(attach);
				controller.setLogoAttach(attach);
				aonFile.setAttachment(attach);
			} catch (ManagerBeanException e) {
				LOGGER.error("Error updating logo", e);
			}
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		PersonController controller = (PersonController) event.getController();
		try {
			RegistryAttachment trainingCenterLogo = controller.obtainPersonLogo();
			if (trainingCenterLogo != null) {
				controller.setLogoAttach(trainingCenterLogo);

				AonFile f = new AonFile();
				f.setAttachment(trainingCenterLogo);
				f.setFileName(trainingCenterLogo.getDescription());
				f.setMimeType(trainingCenterLogo.getMimeType());
				controller.setLogoFile(f);
			} else {
				controller.setLogoFile(null);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}
	
	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		PersonController controller = (PersonController) event.getController();
		if (controller.getLogoAttach() != null) {
			try {
				IManagerBean attachBean = BeanManager.getManagerBean(RegistryAttachment.class);
				attachBean.remove(controller.getLogoAttach());
			} catch (ManagerBeanException e) {
				LOGGER.error("Error updating signature", e);
			}
		}
	}
}
