package com.code.aon.ui.config.event;


import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ClassUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Scope;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.config.controller.ConfigCollectionsController;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.form.event.ControllerSearchListenerEx;
import com.code.aon.ui.util.AonUtil;

public class ScopeSearchListener extends ControllerSearchListenerEx {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Scope[] scopes;
	
	public Scope[] getScopes() {
		if (ArrayUtils.isEmpty(scopes)) {
			scopes = new Scope[]{getEmptyScope()};
		}
		return scopes;
	}

	public void setScopes(Scope[] scopes) {
		this.scopes = scopes;
	}

	public int getScopesSize() {
		return ArrayUtils.getLength(scopes);
	}
	
	public List<Integer> getScopesIds() {
		List<Integer> ids = new LinkedList<Integer>();
		for( Scope scope : getScopes() ) {
			if ((scope != null) && (scope.getId() != null)) {
				ids.add(scope.getId());
			}
		}
		return ids;
	}			
	
	@Override
	protected void init() throws ManagerBeanException {
		setScopes( new Scope[]{getEmptyScope()} );
	}

	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		if ( getScopesSize() > 0 ) {
			String preffix = ClassUtils.getShortClassName(getController().getManagerBean().getPOJOClass());
			addEnumToCriteria(criteria, preffix + ".scope<id", getScopesIds().toArray());	
		}				
	}
	
	public void onAddScope(ActionEvent event) {
		this.scopes = (Scope[]) ArrayUtils.add(this.scopes, getEmptyScope());
	}
	
	public void onRemoveScope(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
		int index = Integer.valueOf(context.getExternalContext().getRequestParameterMap().get("index"));		
		this.scopes = (Scope[]) ArrayUtils.remove(this.scopes, index);
		if ( ArrayUtils.isEmpty(this.scopes) ) {
			setScopes(new Scope[]{getEmptyScope()});
		}
	}		
 
	public ConfigCollectionsController getCollectionsController() {
		return (ConfigCollectionsController)AonUtil.getRegisteredBean(ConfigConstants.CONFIG_COLLECTIONS);
	}
	
	private Scope getEmptyScope() {
		return getCollectionsController().getEmptyScope();
	}	
	
}