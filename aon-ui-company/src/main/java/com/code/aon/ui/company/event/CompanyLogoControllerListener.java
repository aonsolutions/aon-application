package com.code.aon.ui.company.event;

import static com.code.aon.ui.common.ICommonMessages.COMPANY_BUNDLE;
import static com.code.aon.ui.common.ICommonMessages.COMPANY_LOGO_MAX_SIZE_ERROR;
import static com.code.aon.ui.common.ICommonMessages.FILE_UPLOAD_ELEMENT;
import static com.code.aon.ui.company.controller.ICompanyConstants.LOGO_MAX_SIZE;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIInput;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.company.Company;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.CompanyImagesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.sun.faces.util.MessageFactory;

/**
 * Listener added to the CompanyController.
 */
public class CompanyLogoControllerListener extends ControllerAdapter {

	/** The LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(CompanyLogoControllerListener.class.getName());

	private void checkAonFile( CompanyController companyController ) throws ControllerListenerException {
		AonFile aonFile = companyController.getLogoFile();
		if ( ArrayUtils.isEmpty(aonFile.getData()) ) {
			FacesMessage message = MessageFactory.getMessage( UIInput.REQUIRED_MESSAGE_ID, AonUtil.getMessage(FILE_UPLOAD_ELEMENT) );
			throw new ControllerListenerException( message.getSummary() );									
		} else if (aonFile.getSize() > LOGO_MAX_SIZE) {
			String message = AonUtil.getMessage(COMPANY_BUNDLE, COMPANY_LOGO_MAX_SIZE_ERROR, LOGO_MAX_SIZE);
			throw new ControllerListenerException(message);										
		}
	}	
	/**
	 * Adds the company logo as a RegistryAttach if it is uploaded
	 * 
	 * @param event
	 *            the event
	 * 
	 * @throws ControllerListenerException
	 *             the controller listener exception
	 */
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		CompanyController companyController = (CompanyController) event.getController();
		if (companyController.getLogoFile() != null) {
			checkAonFile(companyController);
			try {
				AonFile aonFile = companyController.getLogoFile();
				RegistryAttachment attach = new RegistryAttachment();
				attach.setRegistryAttachmentType(RegistryAttachmentType.LOGO);
				attach.setCategory(null);
				attach.setData(aonFile.getData());
				attach.setDescription("aon-logo");
				attach.setRegistry((Company) event.getController().getTo());
				MimeType mt = CompanyImagesController.getMimeType(aonFile.getFileName(), aonFile.getData());
				attach.setMimeType(mt);
				IManagerBean attachBean = BeanManager.getManagerBean(RegistryAttachment.class);
				companyController.setLogoAttach((RegistryAttachment) attachBean.insert(attach));
			} catch (ManagerBeanException e) {
				LOGGER.error("Error updating logo", e);
			}
		}
	}

	/**
	 * Updates the company logo
	 * 
	 * @param event
	 *            the event
	 * 
	 * @throws ControllerListenerException
	 *             the controller listener exception
	 */
	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		CompanyController companyController = (CompanyController) event.getController();
		if (companyController.getLogoFile() != null) {
			checkAonFile(companyController);
			try {
				AonFile aonFile = companyController.getLogoFile();
				RegistryAttachment attach = companyController.obtainCompanyLogo();				
				if (attach == null) {
					attach = new RegistryAttachment();
				}
				attach.setRegistryAttachmentType(RegistryAttachmentType.LOGO);
				attach.setCategory(null);
				attach.setData(aonFile.getData());
				attach.setDescription("aon-logo");
				attach.setRegistry((Company) event.getController().getTo());
				MimeType mt = CompanyImagesController.getMimeType(aonFile.getFileName(), aonFile.getData());
				attach.setMimeType(mt);
				IManagerBean attachBean = BeanManager.getManagerBean(RegistryAttachment.class);
				if (attach.getId() == null) {
					companyController.setLogoAttach((RegistryAttachment) attachBean.insert(attach));
				} else {
					companyController.setLogoAttach((RegistryAttachment) attachBean.update(attach));
				}
			} catch (ManagerBeanException e) {
				LOGGER.error("Error updating logo", e);
			}
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		CompanyController companyController = (CompanyController) event.getController();
		try {
			RegistryAttachment companyLogo = companyController.obtainCompanyLogo();
			if (companyLogo != null) {
				companyController.setLogoAttach(companyLogo);

				AonFile f = new AonFile();
				f.setKey(companyLogo.getId());
				f.setData(companyLogo.getData());
				f.setFileName(companyLogo.getDescription());
				f.setMimeType(companyLogo.getMimeType());
				companyController.setLogoFile(f);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}

}
