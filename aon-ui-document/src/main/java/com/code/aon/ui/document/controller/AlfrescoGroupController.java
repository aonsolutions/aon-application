package com.code.aon.ui.document.controller;

import static com.code.aon.ui.document.controller.IDocumentConstants.MANAGER_CONTROLLER_NAME;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BUNDLE_NAME;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.INVALID_NAME;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BasicManagerBean;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.EnterpriseUser;
import com.code.aon.document.IAlfrescoConstants;
import com.code.aon.document.dao.AlfrescoGroupDAO;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class AlfrescoGroupController extends BasicController {

	private AlfrescoGroupDAO alfrescoGroupDAO;
	
	private BasicManagerBean alfrescoManagerBean;

	@Override
	public IManagerBean getManagerBean() throws ManagerBeanException {
		if ( this.alfrescoManagerBean == null ) {
			ManagerController mc = (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
			EnterpriseUser user = mc.getLoggedUser();
			this.alfrescoGroupDAO = new AlfrescoGroupDAO(user.getLogin(), user.getPassword());
			this.alfrescoManagerBean = new BasicManagerBean(this.alfrescoGroupDAO);			
		}
		return this.alfrescoManagerBean;
	}

	public void groupCheck(FacesContext context, UIComponent component, Object value) {
		String name = (String) value;
		if ( StringUtils.startsWith(name, IAlfrescoConstants.ENTERPRISE_PREFFIX) ) {
			String label = AonUtil.getMessage(BUNDLE_NAME, INVALID_NAME, name);	
			FacesMessage message = new FacesMessage(label);
			throw new ValidatorException(message);
		}
	}
	
}
