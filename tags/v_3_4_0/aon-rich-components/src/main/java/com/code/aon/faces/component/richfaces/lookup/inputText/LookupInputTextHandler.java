package com.code.aon.faces.component.richfaces.lookup.inputText;

import javax.el.MethodExpression;
import javax.faces.component.UIInput;

import com.code.aon.faces.component.richfaces.AonAjaxInputHandler;
import com.code.aon.faces.component.richfaces.lookup.ILookupTags;
import com.code.aon.faces.component.sandbox.ValueChangeNotifierHandler;
import com.code.aon.faces.component.util.FaceletUtil;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.el.LegacyMethodBinding;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.jsf.ComponentConfig;

/**
 * The Class TabbedPaneComponentHandler.
 * 
 * @author atellitu
 */
public class LookupInputTextHandler extends AonAjaxInputHandler implements ILookupTags {

    private static final String VALUE_CHANGE_LISTENER = "lookupChanged";
	
   	/**
	 * The Constructor.
	 * 
	 * @param config the config
	 */
	public LookupInputTextHandler(ComponentConfig config) {
		super( config );
		setAjaxNeeded( true );
	}

	private void setValueChangeNotifier( FaceletContext ctx, UIInput text ) {
		String lookup = getRequiredAttribute(LOOKUP).getValue();
		String valueChangeListener = FaceletUtil.appendExpression( lookup, VALUE_CHANGE_LISTENER);
		ValueChangeNotifierHandler.setupClassListener(ctx, text, valueChangeListener);
	}
	
	private void setLookupChangeListener( FaceletContext ctx, HtmlLookupInputText text ) {	
		TagAttribute vcl = getAttribute(LOOKUP_CHANGE_LISTENER);
		if ( vcl != null ) {
			MethodExpression me = vcl.getMethodExpression(ctx, null, FaceletUtil.LOOKUP_CHANGE_LISTENER_SIG);
			text.setLookupChangeListener( new LegacyMethodBinding(me) );
		}
	}
	
	/**
	 * Sets the attributes.
	 * 
	 * @param instance the instance
	 * @param ctx the ctx
	 */
	@Override
	protected void setAttributes(FaceletContext ctx, Object instance) {
		super.setAttributes(ctx, instance);
		HtmlLookupInputText text = (HtmlLookupInputText) instance;
		setValueChangeNotifier(ctx, text);
		setLookupChangeListener(ctx, text);
	}

}