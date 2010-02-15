package com.code.aon.faces.component.richfaces.panelTab;

import javax.faces.component.UIComponent;

import org.richfaces.taglib.TabPanelTagHandler;

import com.code.aon.faces.component.ComponentManager;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.jsf.ComponentConfig;

public class PanelTabSetHandler extends TabPanelTagHandler {

	public PanelTabSetHandler(ComponentConfig config) {
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
	
}
