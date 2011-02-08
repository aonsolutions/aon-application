package com.code.aon.faces.component.icefaces;

import javax.faces.component.UIComponent;

import com.code.aon.faces.component.ComponentManager;
import com.icesoft.faces.component.facelets.IceComponentHandler;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.Tag;
import com.sun.facelets.tag.jsf.ComponentConfig;

public class AonIceComponentHandler extends IceComponentHandler {

	private Tag aonTag;
	
	public AonIceComponentHandler(ComponentConfig config) {
		super(new IceComponentConfig(config));
		this.aonTag = config.getTag();
	}

	@Override
	protected MetaRuleset createMetaRuleset(Class type) {
		MetaRuleset set = super.createMetaRuleset(type);
		ComponentManager.getInstance().updateMetaRuleset( aonTag, set );
		return set;
	}

	@Override
	protected void setAttributes( FaceletContext ctx, Object instance ) {
		super.setAttributes(ctx, instance);
		ComponentManager.getInstance().setAttributes( aonTag, ctx, (UIComponent) instance );
	}

}
