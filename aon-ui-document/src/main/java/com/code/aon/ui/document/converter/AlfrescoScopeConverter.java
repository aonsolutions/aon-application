package com.code.aon.ui.document.converter;

import static com.code.aon.ui.document.controller.IDocumentConstants.MANAGER_CONTROLLER_NAME;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;

import com.code.aon.document.AlfrescoGroup;
import com.code.aon.ui.document.controller.ManagerController;
import com.code.aon.ui.util.AonUtil;

/**
 * 
 *
 */
public class AlfrescoScopeConverter implements Converter {
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value != null) {
			ManagerController mc = (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
			for( SelectItem item : mc.getUserScopes() ) {
				AlfrescoGroup ag = (AlfrescoGroup) item.getValue();
				if ( ag.getName().equals(value) ) {
					return ag;
				}
			}
		}
		return null;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null) {
			return null;
		}
		AlfrescoGroup ag = (AlfrescoGroup) value;
		return ag.getName();
	}

}