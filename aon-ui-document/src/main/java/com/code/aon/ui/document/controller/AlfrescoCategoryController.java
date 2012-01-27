package com.code.aon.ui.document.controller;

import static com.code.aon.ui.document.controller.IDocumentConstants.MANAGER_CONTROLLER_NAME;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import com.code.aon.common.BasicManagerBean;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.EnterpriseUser;
import com.code.aon.document.AlfrescoCategory;
import com.code.aon.document.dao.AlfrescoCategoryDAO;
import com.code.aon.document.dao.AlfrescoDAO;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.registry.controller.IRegistryConstants;
import com.code.aon.ui.util.AonUtil;
import com.sun.faces.util.MessageFactory;

public class AlfrescoCategoryController extends BasicController {

	private AlfrescoDAO alfrescoDAO;
	
	private BasicManagerBean alfrescoManagerBean;

	@Override
	public IManagerBean getManagerBean() throws ManagerBeanException {
		if ( this.alfrescoManagerBean == null ) {
			ManagerController mc = (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
			EnterpriseUser user = mc.getLoggedUser();
			this.alfrescoDAO = new AlfrescoCategoryDAO(user.getLogin(), user.getPassword());
			this.alfrescoManagerBean = new BasicManagerBean(this.alfrescoDAO);			
		}
		return this.alfrescoManagerBean;
	}
	
	@Override
	protected String getIdAlias() throws ManagerBeanException {
		return "ID";
	}
	
	public AlfrescoDAO getAlfrescoDAO() {
		return alfrescoDAO;
	}

	public void categoryCheck(FacesContext context, UIComponent component, Object value) {
		AlfrescoCategory category = (AlfrescoCategory) value;
		if ( (value == null) || (category == AlfrescoCategoryDAO.EMPTY_CATEGORY) ) {
			UIInput input = (UIInput) component;
			if ( input.isRequired() ) {
				String label = AonUtil.getMessage(IRegistryConstants.BUNDLE_NAME, IRegistryConstants.REGISTRY_CATEGORY);
				FacesMessage message = MessageFactory.getMessage( UIInput.REQUIRED_MESSAGE_ID, label );
				throw new ValidatorException(message);				
			}
		}		
	}
	
}
