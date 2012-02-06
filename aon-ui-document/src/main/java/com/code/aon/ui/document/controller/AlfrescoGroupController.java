package com.code.aon.ui.document.controller;

import static com.code.aon.ui.document.controller.IDocumentConstants.MANAGER_CONTROLLER_NAME;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BUNDLE_NAME;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.INVALID_NAME;

import java.util.LinkedList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BasicManagerBean;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.company.EnterpriseUser;
import com.code.aon.document.AlfrescoUserManager;
import com.code.aon.document.IAlfrescoConstants;
import com.code.aon.document.dao.AlfrescoScopeDAO;
import com.code.aon.ui.document.converter.AlfrescoScopeConverter;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class AlfrescoGroupController extends BasicController {

	private AlfrescoScopeDAO alfrescoGroupDAO;
	
	private BasicManagerBean alfrescoManagerBean;
	
	private Converter converter;
	
	private List<SelectItem> scopes;
	
	private List<String> selectScopes;
	
	public AlfrescoGroupController() {
		this.converter = new AlfrescoScopeConverter();
	}

	@Override
	public IManagerBean getManagerBean() throws ManagerBeanException {
		if ( this.alfrescoManagerBean == null ) {
			ManagerController mc = (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
			this.alfrescoGroupDAO = new AlfrescoScopeDAO(mc.getUserManager());
			this.alfrescoManagerBean = new BasicManagerBean(this.alfrescoGroupDAO);			
		}
		return this.alfrescoManagerBean;
	}
	
	public Converter getConverter() {
		return converter;
	}

	public void groupCheck(FacesContext context, UIComponent component, Object value) {
		String name = (String) value;
		if ( StringUtils.startsWith(name, IAlfrescoConstants.ENTERPRISE_PREFFIX) ) {
			String label = AonUtil.getMessage(BUNDLE_NAME, INVALID_NAME, name);	
			FacesMessage message = new FacesMessage(label);
			throw new ValidatorException(message);
		}
	}
	
	public void initScopes( EnterpriseUser user ) {
		loadScopes();
		if ( user.getName() != null ) {
			ManagerController mc = (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
			List<String> scopes = mc.getUserManager().getUserScopes(user.getLogin());
			setSelectScopes(scopes);
		}
		if ( this.selectScopes == null ) {
			this.selectScopes = new LinkedList<String>();
		}
		if ( this.selectScopes.isEmpty() ) {
			this.selectScopes.add(null);
		}
	}

	public void updateScopes( EnterpriseUser user ) throws DAOException {
		ManagerController mc = (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
		AlfrescoUserManager um = mc.getUserManager();
		List<String> userScopes = um.getUserScopes(user.getLogin());
		for( String scope : userScopes ) {
			if (! this.selectScopes.contains(scope) ) {
				um.removeUserFromGroup(user.getLogin(), scope);
			}
		}
		this.selectScopes.remove(null);
		for( String scope : selectScopes ) {
			if (! userScopes.contains(scope) ) {
				um.addUserToGroup(user.getLogin(), scope);
			}			
		}
	}
	
	public List<String> getSelectScopes() {
		return selectScopes;
	}

	public int getSelectScopesSize() {
		return (selectScopes != null) ? selectScopes.size() : 0;
	}
	
	public void setSelectScopes(List<String> selectScopes) {
		this.selectScopes = selectScopes;
	}

	public List<SelectItem> getScopes() {
		return scopes;
	}

	public void loadScopes() {
		this.scopes = new LinkedList<SelectItem>();
		ManagerController mc = (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);		
		for( String scope : mc.getUserManager().getScopes() ) {
			SelectItem item = new SelectItem(scope);
			this.scopes.add( item );
		}
	}

	public void onAddScope(ActionEvent event) {
		selectScopes.add(null);
	}
	
	public void onRemoveScope(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
		int index = Integer.valueOf(context.getExternalContext().getRequestParameterMap().get("index"));		
		selectScopes.remove(index);
		if (selectScopes.isEmpty()) {
			selectScopes.add(null);
		}
	}		
	
}
