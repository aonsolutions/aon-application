package com.code.aon.ui.company.event;

import static com.code.aon.ui.common.ICommonMessages.COMPANY_LOGO_MAX_SIZE_ERROR;
import static com.code.aon.ui.company.controller.ICompanyConstants.LOGO_MAX_SIZE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.AonFile;
import com.code.aon.company.Company;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

/**
 * Listener added to the CompanyController.
 */
public class CompanyLogoControllerListener extends ControllerAdapter {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	/** The LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(CompanyLogoControllerListener.class.getName());

	private void checkAonFile( CompanyController companyController ) throws ControllerListenerException {
		AonFile aonFile = companyController.getLogoFile();
		if (aonFile.getSize() > LOGO_MAX_SIZE) {
			String message = AonUtil.getMessage( COMPANY_LOGO_MAX_SIZE_ERROR, LOGO_MAX_SIZE);
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
		updateLogoAttach(event);
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
		updateLogoAttach(event);
	}
	
	
	private void updateLogoAttach(ControllerEvent event) throws ControllerListenerException {
		CompanyController companyController = (CompanyController) event.getController();
		AonFile aonFile = companyController.getLogoFile();
		if ((aonFile != null) && aonFile.isDirty() ) {
			try {
				if ( aonFile.getSize() > 0 ) {				
					checkAonFile(companyController);
					RegistryAttachment attach = companyController.obtainCompanyLogo();				
					if (attach == null) {
						attach = new RegistryAttachment();
					}
					attach.setRegistryAttachmentType(RegistryAttachmentType.LOGO);
					attach.setCategory(null);
					attach.setData(aonFile.getData());
					attach.setDescription("aon-logo");
					attach.setRegistry((Company) event.getController().getTo());
					attach.setMimeType(aonFile.getMimeType());
					IManagerBean attachBean = BeanManager.getManagerBean(RegistryAttachment.class);
					attachBean.insertOrUpdate(attach);
					companyController.setLogoAttach(attach);
					aonFile.setAttachment(attach);
				} else {
					RegistryAttachment attach = companyController.obtainCompanyLogo();				
					if (attach != null) {
						IManagerBean attachBean = BeanManager.getManagerBean(RegistryAttachment.class);
						attachBean.remove(attach);
					}
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
				f.setAttachment(companyLogo);
				f.setFileName(companyLogo.getDescription());
				f.setMimeType(companyLogo.getMimeType());
				companyController.setLogoFile(f);
			} else {
				companyController.setLogoFile(null);				
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}

}
