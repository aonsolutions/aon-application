package com.code.aon.ui.webinfo.event;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.webinfo.controller.CompanyController;

/**
 * Listener added to the CompanyController.
 */
public class CompanyLogoControllerListener extends ControllerAdapter {
	
	/** The LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(CompanyLogoControllerListener.class.getName());


	
	/**
	 * Create empty Registry Attachment
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		CompanyController companyController = (CompanyController)event.getController();
		Company company =(Company) companyController.getTo();
		companyController.setAttachment( newLogo(company) );
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
		RegistryAttachment logo = obtainCompanyLogo( (Company)companyController.getTo() );
		companyController.setAttachment(logo);
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		try {
			CompanyController companyController = (CompanyController)event.getController();
			RegistryAttachment attachment = companyController.getAttachment();
			if ( attachment.getData() != null ) {
				IManagerBean attachBean = BeanManager.getManagerBean(RegistryAttachment.class);
				attachBean.insert(attachment);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error inserting logo for company", e);
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		try {
			CompanyController companyController = (CompanyController)event.getController();
			RegistryAttachment attachment = companyController.getAttachment();
			IManagerBean attachBean = BeanManager.getManagerBean(RegistryAttachment.class);
			if ( attachment.getData() != null ) {
				attachBean.update(attachment);
			} else {
				if ( attachment.getId() != null ) {
					attachBean.remove(attachment);
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error updating logo for company", e);
		}
	}

	/**
	 * Obtains the RegistryAttachemnt with the id passed as parameter
	 * 
	 * @param id the id
	 * 
	 * @return the registry attachment
	 */
	@SuppressWarnings("unchecked")
	private RegistryAttachment obtainCompanyLogo( Company company ) {
		try {
			IManagerBean attachBean = BeanManager.getManagerBean(RegistryAttachment.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(attachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ID), company.getId());
			criteria.addEqualExpression(attachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE), RegistryAttachmentType.LOGO);
			Iterator iter = attachBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return (RegistryAttachment)iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining logo for company", e);
		}
		return newLogo(company);
	}

	private RegistryAttachment newLogo( Company company ) {
		RegistryAttachment newAttach = new RegistryAttachment();
		newAttach.setDescription("");
		newAttach.setRegistryAttachmentType(RegistryAttachmentType.LOGO);
		newAttach.setRegistry( company );
		return newAttach;
	}
	
}