package com.code.aon.faces.component.icefaces.rowSelector;

import javax.el.MethodExpression;

import com.code.aon.faces.component.icefaces.AonIceComponentHandler;
import com.code.aon.faces.component.util.FaceletUtil;
import com.icesoft.faces.component.ext.RowSelector;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.jsf.ComponentConfig;

public class RowSelectorHandler extends AonIceComponentHandler {

	private static final String ACTION = "action";
	
	private static final String ACTION_LISTENER = "actionListener";
	
	public RowSelectorHandler(ComponentConfig config) {
		super( config );
	}

	@Override
	protected MetaRuleset createMetaRuleset(Class type) {
		MetaRuleset set = super.createMetaRuleset(type);
		set.ignore(ACTION).ignore(ACTION_LISTENER);
		return set;
	}
	
	@Override
	protected void setAttributes( FaceletContext ctx, Object instance ) {
		super.setAttributes(ctx, instance);
		RowSelector rowSelector = (RowSelector) instance;
		TagAttribute actionListener = getAttribute(ACTION_LISTENER);
		if ( actionListener != null ) {
			MethodExpression me = actionListener.getMethodExpression(ctx, null, FaceletUtil.ACTION_LISTENER_SIG);
			rowSelector.setSelectionListener(new ActionListenerMethodBinding(me));
		}
		TagAttribute action = getAttribute(ACTION);
		if ( action != null ) {
			MethodExpression me = action.getMethodExpression(ctx, String.class, FaceletUtil.ACTION_SIG);
			rowSelector.setSelectionAction(new ActionMethodBinding(me));
		}
	}
	
}
