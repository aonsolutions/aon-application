package com.code.aon.ui.company.event;

import static com.code.aon.ui.common.ICommonMessages.COMPANY_LOGO_MAX_SIZE_ERROR;
import static com.code.aon.ui.common.ICommonMessages.FILE_UPLOAD_ELEMENT;
import static com.code.aon.ui.company.controller.ICompanyConstants.IMAGE_MAX_SIZE;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIInput;

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
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.sun.faces.util.MessageFactory;

public class CompanyReportBackgroundControllerListener extends ControllerAdapter {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CompanyReportBackgroundControllerListener.class.getName());
	
	private void checkAonFile( AonFile aonFile ) throws ControllerListenerException {
		if ( aonFile.getSize() <= 0 ) {
			FacesMessage message = MessageFactory.getMessage( UIInput.REQUIRED_MESSAGE_ID, AonUtil.getMessage(FILE_UPLOAD_ELEMENT) );
			throw new ControllerListenerException( message.getSummary() );									
		} else if (aonFile.getSize() > IMAGE_MAX_SIZE) {
			String message = AonUtil.getMessage( COMPANY_LOGO_MAX_SIZE_ERROR, IMAGE_MAX_SIZE);
			throw new ControllerListenerException(message);										
		}
	}	
	
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		CompanyController companyController = (CompanyController) event.getController();
		Company company = (Company) event.getController().getTo();

		AonFile aonFile = companyController.getSaleInvoiceBackgroundFile();
		insertAttach(aonFile, company, ICompanyConstants.SALE_INVOICE_REPORT_KEY);
		
		aonFile = companyController.getOfferBackgroundFile();
		insertAttach(aonFile, company, ICompanyConstants.OFFER_REPORT_KEY);
		
		aonFile = companyController.getDeliveryBackgroundFile();
		insertAttach(aonFile, company, ICompanyConstants.DELIVERY_REPORT_KEY);
		
		aonFile = companyController.getSalesBackgroundFile();
		insertAttach(aonFile, company, ICompanyConstants.SALES_REPORT_KEY);
	}
	
	private void insertAttach(AonFile aonFile, Company company, String name) throws ControllerListenerException{
		if ((aonFile != null) && aonFile.isDirty() ) {
			checkAonFile(aonFile);
			try {
				RegistryAttachment attach = new RegistryAttachment();
				attach.setRegistryAttachmentType(RegistryAttachmentType.REPORT_BACKGROUND);
				attach.setCategory(null);
				attach.setData(aonFile.getData());
				attach.setDescription(name);
				attach.setRegistry(company);
				attach.setMimeType(aonFile.getMimeType());
				IManagerBean attachBean = BeanManager.getManagerBean(RegistryAttachment.class);
				attach = (RegistryAttachment) attachBean.insert(attach);
				aonFile.setAttachment(attach);
			} catch (ManagerBeanException e) {
				LOGGER.error("Error updating report background image", e);
			}
		}
	}
	
	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		CompanyController companyController = (CompanyController) event.getController();
		Company company = (Company) event.getController().getTo();
		try {
			AonFile aonFile = companyController.getSaleInvoiceBackgroundFile();
			RegistryAttachment attach = companyController.obtainSaleInvoiceBackground();				
			updateAttach(aonFile, company, attach, ICompanyConstants.SALE_INVOICE_REPORT_KEY);

			aonFile = companyController.getOfferBackgroundFile();
			attach = companyController.obtainOfferBackground();				
			updateAttach(aonFile, company, attach, ICompanyConstants.OFFER_REPORT_KEY);
			
			aonFile = companyController.getDeliveryBackgroundFile();
			attach = companyController.obtainDeliveryBackground();				
			updateAttach(aonFile, company, attach, ICompanyConstants.DELIVERY_REPORT_KEY);
			
			aonFile = companyController.getSalesBackgroundFile();
			attach = companyController.obtainSalesBackground();				
			updateAttach(aonFile, company, attach, ICompanyConstants.SALES_REPORT_KEY);
		} catch (ManagerBeanException e) {
			LOGGER.error("Error updating report background image", e);
		}
	}
	
	private void updateAttach(AonFile aonFile, Company company, RegistryAttachment attach, String name) throws ControllerListenerException {
		if ((aonFile != null) && aonFile.isDirty() ) {
			try {
				if (attach == null) {
					attach = new RegistryAttachment();
				}
				attach.setRegistryAttachmentType(RegistryAttachmentType.REPORT_BACKGROUND);
				attach.setCategory(null);
				attach.setData(aonFile.getData());
				attach.setDescription(name);
				attach.setRegistry(company);
				attach.setMimeType(aonFile.getMimeType());
				IManagerBean attachBean = BeanManager.getManagerBean(RegistryAttachment.class);
				attach = (RegistryAttachment) attachBean.insertOrUpdate(attach);
				aonFile.setAttachment(attach);
			} catch (ManagerBeanException e) {
				LOGGER.error("Error updating report background image", e);
			}
		} else if (aonFile == null) {
			try {
				if (attach != null && attach.getId() != null) {
					IManagerBean attachBean = BeanManager.getManagerBean(RegistryAttachment.class);
					attachBean.remove(attach);
				}
			} catch (ManagerBeanException e) {
				LOGGER.error("Error updating report background image", e);
			}
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		CompanyController companyController = (CompanyController) event.getController();
		try {
			RegistryAttachment attach = companyController.obtainSaleInvoiceBackground();
			AonFile file = selecteAttach(attach);
			companyController.setSaleInvoiceBackgroundFile(file);
			
			attach = companyController.obtainOfferBackground();
			file = selecteAttach(attach);
			companyController.setOfferBackgroundFile(file);
			
			attach = companyController.obtainDeliveryBackground();
			file = selecteAttach(attach);
			companyController.setDeliveryBackgroundFile(file);
			
			attach = companyController.obtainSalesBackground();
			file = selecteAttach(attach);
			companyController.setSalesBackgroundFile(file);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}

	private AonFile selecteAttach(RegistryAttachment attach) {
		if (attach != null) {
			AonFile file = new AonFile();
			file.setAttachment(attach);
			file.setFileName(attach.getDescription());
			file.setMimeType(attach.getMimeType());
			return file;
		}
		return null;
	}

}
