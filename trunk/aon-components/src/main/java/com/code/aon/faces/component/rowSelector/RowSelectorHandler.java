package com.code.aon.faces.component.rowSelector;

import com.code.aon.faces.component.AonComponentHandler;
import com.code.aon.faces.component.myfaces.UIComponentTagUtils;
import com.icesoft.faces.component.ext.RowSelector;
import com.icesoft.faces.component.ext.RowSelectorEvent;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.MethodRule;
import com.sun.facelets.tag.jsf.ComponentConfig;

public class RowSelectorHandler extends AonComponentHandler {

	private static final String MOUSE_OVER_ATTR_NAME = "mouseOverClass";
	
	private static final String SELECTED_ATTR_NAME = "selectedClass";
	
	private static final String MOUSE_OVER_CLASS = "aon-table-row-hover";
	
	private static final String SELECTED_CLASS = "aon-table-row-selected";
	
	public RowSelectorHandler(ComponentConfig config) {
		super(config);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected MetaRuleset createMetaRuleset(Class type) {
		MetaRuleset m = super.createMetaRuleset(type);
		m.addRule( new MethodRule("selectionListener", null, new Class[] {RowSelectorEvent.class}) );
        m.addRule( new MethodRule("selectionAction", null, new Class[0]) );
		return m;
	}
	
	@Override
	protected void setAttributes(FaceletContext ctx, Object instance) {
		RowSelector selector = (RowSelector) instance;
		super.setAttributes(ctx, instance);
		if (! hasValue(ctx, MOUSE_OVER_ATTR_NAME) ) {
			UIComponentTagUtils.setStringProperty(ctx.getFacesContext(), selector, MOUSE_OVER_ATTR_NAME, MOUSE_OVER_CLASS );
		}
		if (! hasValue(ctx, SELECTED_ATTR_NAME) ) {
			UIComponentTagUtils.setStringProperty(ctx.getFacesContext(), selector, SELECTED_ATTR_NAME, SELECTED_CLASS );
		}
	}
}