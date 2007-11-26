package com.code.aon.faces.component.richfaces.dataPaginator;

import javax.faces.component.UIComponent;

import com.code.aon.faces.component.AonComponentHandler;
import com.code.aon.faces.component.myfaces.UIComponentTagUtils;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.jsf.ComponentConfig;

public class DataPaginatorHandler extends AonComponentHandler {

	private static final String PAGINATOR_ATTRIBUTE = "paginator";
	
	private static final String AUTO_VALUE = "auto";
	
	private static final String HIDE_VALUE = "hide";

	private static final String BOUNDARY_CONTROLS_ATTRIBUTE = "boundaryControls";
	
	private static final String FAST_CONTROLS_ATTRIBUTE = "fastControls";
	
	private static final String STEP_CONTROLS_ATTRIBUTE = "stepControls";
	
	public DataPaginatorHandler(ComponentConfig config) {
		super(config);
	}

	private boolean isPaginator(FaceletContext ctx) {
		TagAttribute tag = getAttribute(PAGINATOR_ATTRIBUTE);
		if (tag != null) {
			return tag.getBoolean(ctx);
		}
		return false;
	}
	
	private void setControlsValue( FaceletContext ctx, UIComponent component, String value ) {
		if (! hasValue(ctx, BOUNDARY_CONTROLS_ATTRIBUTE) ) {
			UIComponentTagUtils.setStringProperty( ctx.getFacesContext(), component, BOUNDARY_CONTROLS_ATTRIBUTE, value );			
		}
		if (! hasValue(ctx, STEP_CONTROLS_ATTRIBUTE) ) {		
			UIComponentTagUtils.setStringProperty( ctx.getFacesContext(), component, STEP_CONTROLS_ATTRIBUTE, value );
		}
		if (! hasValue(ctx, FAST_CONTROLS_ATTRIBUTE) ) {
			UIComponentTagUtils.setStringProperty( ctx.getFacesContext(), component, FAST_CONTROLS_ATTRIBUTE, value );
		}
	}
	
	@Override
	protected void setAttributes(FaceletContext ctx, Object instance) {
		super.setAttributes(ctx, instance);
		UIComponent component = (UIComponent) instance;
		if ( isPaginator(ctx) ) {
			setControlsValue(ctx, component, AUTO_VALUE);
		} else {
			setControlsValue(ctx, component, HIDE_VALUE);			
		}
	}
	
}
