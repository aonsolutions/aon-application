package com.code.aon.ui.company.event;

import static com.code.aon.ui.common.ICommonMessages.COMPANY_SIGNATURE_MAX_SIZE_ERROR;
import static com.code.aon.ui.company.controller.ICompanyConstants.SIGNATURE_MAX_SIZE;

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
public class CompanySignatureControllerListener extends ControllerAdapter {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	/** The LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(CompanySignatureControllerListener.class.getName());

	private void checkAonFile( CompanyController companyController ) throws ControllerListenerException {
		AonFile aonFile = companyController.getSignatureFile();
		if (aonFile.getSize() > SIGNATURE_MAX_SIZE) {
			String message = AonUtil.getMessage(COMPANY_SIGNATURE_MAX_SIZE_ERROR, SIGNATURE_MAX_SIZE);
			throw new ControllerListenerException(message);										
		}
	}	
	
	/**
	 * Adds the company signature as a RegistryAttach if it is uploaded
	 * 
	 * @param event
	 *            the event
	 * 
	 * @throws ControllerListenerException
	 *             the controller listener exception
	 */
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		updateSignatureAttach(event);
	}

	/**
	 * Updates the company signature
	 * 
	 * @param event
	 *            the event
	 * 
	 * @throws ControllerListenerException
	 *             the controller listener exception
	 */
	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		updateSignatureAttach(event);
	}
	
	
	private void updateSignatureAttach(ControllerEvent event) throws ControllerListenerException {
		CompanyController companyController = (CompanyController) event.getController();
		AonFile aonFile = companyController.getSignatureFile();
		if ((aonFile != null) && aonFile.isDirty() ) {
			try {
				if ( aonFile.getSize() > 0 ) {				
					checkAonFile(companyController);
					RegistryAttachment attach = companyController.obtainCompanySignature();				
					if (attach == null) {
						attach = new RegistryAttachment();
					}
					attach.setRegistryAttachmentType(RegistryAttachmentType.SIGNATURE);
					attach.setCategory(null);
					attach.setData(aonFile.getData());
					attach.setDescription("aon-signature");
					attach.setRegistry((Company) event.getController().getTo());
					attach.setMimeType(aonFile.getMimeType());
					IManagerBean attachBean = BeanManager.getManagerBean(RegistryAttachment.class);
					attachBean.insertOrUpdate(attach);
					companyController.setSignatureAttach(attach);
					aonFile.setAttachment(attach);
				} else {
					RegistryAttachment attach = companyController.obtainCompanySignature();				
					if (attach != null) {
						IManagerBean attachBean = BeanManager.getManagerBean(RegistryAttachment.class);
						attachBean.remove(attach);
					}
				}
			} catch (ManagerBeanException e) {
				LOGGER.error("Error updating signature", e);
			}
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		CompanyController companyController = (CompanyController) event.getController();
		try {
			RegistryAttachment companySignature = companyController.obtainCompanySignature();
			if (companySignature != null) {
				companyController.setSignatureAttach(companySignature);
				
				AonFile f = new AonFile();
				f.setAttachment(companySignature);
				f.setFileName(companySignature.getDescription());
				f.setMimeType(companySignature.getMimeType());
				companyController.setSignatureFile(f);
			} else {
				companyController.setSignatureFile(null);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}

}
