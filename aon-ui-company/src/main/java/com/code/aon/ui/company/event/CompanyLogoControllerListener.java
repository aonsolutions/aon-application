package com.code.aon.ui.company.event;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.Company;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.common.io.AonFile;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.CompanyImagesController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

/**
 * Listener added to the CompanyController.
 */
public class CompanyLogoControllerListener extends ControllerAdapter implements ICompanyConstants {

	/** The LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(CompanyLogoControllerListener.class
			.getName());

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
		if (companyController.getAonFile() != null) {
			AonFile aonFile = companyController.getAonFile();
			try {
				if (aonFile.getSize() > LOGO_MAX_SIZE) {
					String message = AonUtil.getMessage(BUNDLE_NAME, COMPANY_LOGO_MAX_SIZE_ERROR);
					throw new ControllerListenerException(message);
				}
				RegistryAttachment attach = new RegistryAttachment();
				attach.setRegistryAttachmentType(RegistryAttachmentType.LOGO);
				attach.setCategory(null);
				attach.setData(aonFile.getData());
				attach.setDescription("aon-logo");
				attach.setRegistry((Company) event.getController().getTo());
				MimeType mt = CompanyImagesController.getMimeType(aonFile.getFileName(), aonFile.getData());
				attach.setMimeType(mt);
				IManagerBean attachBean = BeanManager.getManagerBean(RegistryAttachment.class);
				companyController.setAttach((RegistryAttachment) attachBean.insert(attach));
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error updating logo", e);
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
		if (companyController.getAonFile() != null) {
			AonFile aonFile = companyController.getAonFile();
			try {
				RegistryAttachment attach = companyController.obtainCompanyLogo();				
				if (aonFile.getSize() > LOGO_MAX_SIZE) {
					String message = AonUtil.getMessage(BUNDLE_NAME, COMPANY_LOGO_MAX_SIZE_ERROR);
					throw new ControllerListenerException(message);
				}
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
					companyController.setAttach((RegistryAttachment) attachBean.insert(attach));
				} else {
					companyController.setAttach((RegistryAttachment) attachBean.update(attach));
				}
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error updating logo", e);
			}
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		CompanyController companyController = (CompanyController) event.getController();
		try {
			RegistryAttachment companyLogo = companyController.obtainCompanyLogo();
			if (companyLogo != null) {
				companyController.setAttach(companyLogo);

				AonFile f = new AonFile();
				f.setData(companyLogo.getData());
				f.setFileName(companyLogo.getDescription());
				companyController.setAonFile(f);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}

}
