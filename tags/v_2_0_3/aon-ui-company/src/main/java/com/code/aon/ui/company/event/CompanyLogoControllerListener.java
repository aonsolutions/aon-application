package com.code.aon.ui.company.event;

import java.io.IOException;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.Company;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

/**
 * Listener added to the CompanyController.
 */
public class CompanyLogoControllerListener extends ControllerAdapter {
	
	/** The LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(CompanyLogoControllerListener.class.getName());
	
    /** BASE_NAME. */
    private static final String BASE_NAME = "com.code.aon.ui.registry.i18n.messages";

	/**
	 * Adds the company logo as a RegistryAttach if it is uploaded
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		CompanyController companyController = (CompanyController)event.getController();
		if(companyController.getFile() != null){
			UploadedFile file = companyController.getFile();
			if (file.getSize()>100000){
		        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME); 
				throw new ControllerListenerException(bundle.getString("aon_company_logo_max_size_error"));
			}
			try {
				RegistryAttachment attach = new RegistryAttachment();
				attach.setRegistryAttachmentType(RegistryAttachmentType.LOGO);
				attach.setCategory(null);
				attach.setData(file.getBytes());
				attach.setDescription("");
				attach.setRegistry((Company)event.getController().getTo());
				attach.setMimeType(MimeType.getByExtension(file.getName().substring(file.getName().lastIndexOf(".") + 1)));
				IManagerBean attachBean = BeanManager.getManagerBean(RegistryAttachment.class);
				companyController.setAttach((RegistryAttachment)attachBean.insert(attach));
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, "Error updating logo", e);
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error updating logo", e);
			}
		}
	}

	/**
	 * Updates the company logo
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		CompanyController companyController = (CompanyController)event.getController();
		if(companyController.getFile() != null){
			UploadedFile file = companyController.getFile();
			if (file.getSize()>100000){
		        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME); 
				throw new ControllerListenerException(bundle.getString("aon_company_logo_max_size_error"));
			}
			RegistryAttachment attach = obtainRegistryAttachment(((Company)event.getController().getTo()).getId());
			try {
				if(attach == null){
					attach = new RegistryAttachment();
				}
				attach.setRegistryAttachmentType(RegistryAttachmentType.LOGO);
				attach.setCategory(null);
				attach.setData(file.getBytes());
				attach.setDescription("");
				attach.setRegistry((Company)event.getController().getTo());
				attach.setMimeType(MimeType.getByExtension(file.getName().substring(file.getName().lastIndexOf(".") + 1)));
				IManagerBean attachBean = BeanManager.getManagerBean(RegistryAttachment.class);
				if(attach.getId() == null){
					companyController.setAttach((RegistryAttachment)attachBean.insert(attach));
				}else{
					companyController.setAttach((RegistryAttachment)attachBean.update(attach));
				}
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, "Error updating logo", e);
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error updating logo", e);
			}
		}
	}

	/**
	 * Loads the company logo
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		CompanyController companyController = (CompanyController)event.getController();
		companyController.setAttach(obtainRegistryAttachment(((Company)companyController.getTo()).getId()));
	}
	
	/**
	 * Obtains the RegistryAttachemnt with the id passed as parameter
	 * 
	 * @param id the id
	 * 
	 * @return the registry attachment
	 */
	@SuppressWarnings("unchecked")
	private RegistryAttachment obtainRegistryAttachment(Integer id) {
		try {
			IManagerBean attachBean = BeanManager.getManagerBean(RegistryAttachment.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(attachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ID), id);
			criteria.addEqualExpression(attachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE), RegistryAttachmentType.LOGO);
			Iterator iter = attachBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return (RegistryAttachment)iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining attach for company", e);
		}
		return null;
	}
}
