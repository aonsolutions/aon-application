package com.code.aon.faces.component.richfaces.mediaOutput;

import javax.faces.component.UIComponent;

import com.code.aon.faces.component.ComponentManager;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.jsf.ComponentConfig;

public class MediaOutputHandler extends org.ajax4jsf.taglib.html.facelets.MediaOutputHandler {
	
	public MediaOutputHandler(ComponentConfig config) {
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

}
