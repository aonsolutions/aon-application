package com.code.aon.ui.registry.controller.event;


import javax.faces.application.FacesMessage;
import javax.faces.component.UIInput;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.MimeResolver;
import com.code.aon.person.Person;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.common.io.AonFile;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.registry.controller.PersonController;
import com.code.aon.ui.util.AonUtil;
import com.sun.faces.util.MessageFactory;

public class PersonLogoControllerListener extends ControllerAdapter  {

	private static int LOGO_MAX_SIZE = 256 * 1024;
	private static String BUNDLE_NAME = "companyBundle";
	private static String PERSON_LOGO_MAX_SIZE_ERROR = "company_logo_max_size_error";

	private static final Logger LOGGER = LoggerFactory.getLogger(PersonLogoControllerListener.class.getName());

	private void checkAonFile( PersonController personController ) throws ControllerListenerException {
		AonFile aonFile = personController.getAonFile();
		if ( ArrayUtils.isEmpty(aonFile.getData()) ) {
			FacesMessage message = MessageFactory.getMessage( UIInput.REQUIRED_MESSAGE_ID, AonUtil.getMessage("aon_fileupload_element") );
			throw new ControllerListenerException( message.getSummary() );									
		} else if (aonFile.getSize() > LOGO_MAX_SIZE) {
			String message = AonUtil.getMessage(BUNDLE_NAME, PERSON_LOGO_MAX_SIZE_ERROR, LOGO_MAX_SIZE);
			throw new ControllerListenerException(message);										
		}
	}	
	
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		PersonController personController = (PersonController) event.getController();
		if (personController.getAonFile() != null) {
			checkAonFile(personController);
			try {
				AonFile aonFile = personController.getAonFile();
				RegistryAttachment attach = new RegistryAttachment();
				attach.setRegistryAttachmentType(RegistryAttachmentType.LOGO);
				attach.setCategory(null);
				attach.setData(aonFile.getData());
				attach.setDescription("aon-logo");
				attach.setRegistry(((Person) event.getController().getTo()).getRegistry());
				MimeType mt = getMimeType(aonFile.getFileName(), aonFile.getData());
				attach.setMimeType(mt);
				IManagerBean attachBean = BeanManager.getManagerBean(RegistryAttachment.class);
				personController.setAttach((RegistryAttachment) attachBean.insert(attach));
			} catch (ManagerBeanException e) {
				LOGGER.error("Error updating logo", e);
			}
		}
	}

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		PersonController personController = (PersonController) event.getController();
		personController.setAonFile(null);
		personController.setAttach(null);
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		PersonController personController = (PersonController) event.getController();
		if (personController.getAonFile() != null) {
			checkAonFile(personController);
			try {
				AonFile aonFile = personController.getAonFile();
				RegistryAttachment attach = personController.obtainPersonLogo();				
				if (attach == null) {
					attach = new RegistryAttachment();
				}
				attach.setRegistryAttachmentType(RegistryAttachmentType.LOGO);
				attach.setCategory(null);
				attach.setData(aonFile.getData());
				attach.setDescription("aon-logo");
				attach.setRegistry(((Person) event.getController().getTo()).getRegistry());
				MimeType mt = getMimeType(aonFile.getFileName(), aonFile.getData());
				attach.setMimeType(mt);
				IManagerBean attachBean = BeanManager.getManagerBean(RegistryAttachment.class);
				if (attach.getId() == null) {
					personController.setAttach((RegistryAttachment) attachBean.insert(attach));
				} else {
					personController.setAttach((RegistryAttachment) attachBean.update(attach));
				}
			} catch (ManagerBeanException e) {
				LOGGER.error("Error updating logo", e);
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

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		PersonController personController = (PersonController) event.getController();
		try {
			RegistryAttachment companyLogo = personController.obtainPersonLogo();
			if (companyLogo != null) {
				personController.setAttach(companyLogo);
				AonFile f = new AonFile();
				f.setKey(companyLogo.getId());
				f.setData(companyLogo.getData());
				f.setFileName(companyLogo.getDescription());
				f.setMimeType(companyLogo.getMimeType());
				personController.setAonFile(f);
			} else {
				personController.setAonFile(null);
				personController.setAttach(null);
			}
			
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}

}
