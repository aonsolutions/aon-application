package com.code.aon.faces.component.richfaces.panelCollapsible;

import javax.faces.component.UIComponent;

import org.richfaces.taglib.SimpleTogglePanelTagHandler;

import com.code.aon.faces.component.ComponentManager;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.jsf.ComponentConfig;

public class PanelCollapsibleHandler extends SimpleTogglePanelTagHandler {

	public PanelCollapsibleHandler(ComponentConfig config) {
		super(config);
	}

	@Override
	protected MetaRuleset createMetaRuleset(Class type) {
		MetaRuleset set = super.createMetaRuleset(type);
		ComponentManager.getInstance().updateMetaRuleset( tag, set );
		return set;
	}

	@Override
	protected void setAttributes( FaceletContext ctx, Object instance ) {
		super.setAttributes(ctx, instance);
		ComponentManager.getInstance().setAttributes( tag, ctx, (UIComponent) instance );
	}

	@Override
	protected void onComponentCreated(FaceletContext ctx, UIComponent c,
			UIComponent parent) {
		ComponentManager.getInstance().onComponentCreated( ctx, c, parent );
	}	
	
	@Override
	protected void onComponentPopulated(FaceletContext ctx, UIComponent c,
			UIComponent parent) {
		ComponentManager.getInstance().onComponentPopulated( tag, ctx, c, parent );
	}	
	
}
