package com.code.aon.ui.company.event;

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
import com.code.aon.ui.company.controller.ICompanyController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

/**
 * Listener added to the CompanyController.
 */
public class CompanyLogoParentControllerListener extends ControllerAdapter {
	
	/** The LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(CompanyLogoParentControllerListener.class.getName());

	/**
	 * Loads the company logo
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		ICompanyController companyController = (ICompanyController)event.getController();
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
	protected RegistryAttachment obtainRegistryAttachment(Integer id) {
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
