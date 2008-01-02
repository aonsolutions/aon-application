package com.code.aon.faces.component.richfaces;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;

import com.code.aon.faces.component.myfaces.UIComponentTagUtils;
import com.code.aon.faces.component.richfaces.editDataTable.EditDataTableHandler;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentSupport;

public class AonAjaxCommandHandler extends AonAjaxComponentHandler {

	public AonAjaxCommandHandler(ComponentConfig config) {
		super(config);
	}
	
	@Override
	protected void setAttributes(FaceletContext ctx, Object instance) {
		super.setAttributes(ctx, instance);
		UIComponent component = (UIComponent) instance;
		UIViewRoot root = ComponentSupport.getViewRoot(ctx, component);
		String id = (String) root.getAttributes().get( EditDataTableHandler.EDIT_DATA_TABLE_ID );
		if ( id != null ) {
			UIComponentTagUtils.setStringProperty( ctx.getFacesContext(), component, "reRender", id );			
		}
	}

}
