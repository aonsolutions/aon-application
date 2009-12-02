package com.code.aon.faces.component.richfaces.lookup.inputText;

import javax.faces.component.UIInput;

import com.code.aon.faces.component.myfaces.UIComponentTagUtils;
import com.code.aon.faces.component.richfaces.AonAjaxInputHandler;
import com.code.aon.faces.component.richfaces.lookup.ILookupTags;
import com.code.aon.faces.component.sandbox.ValueChangeNotifierHandler;
import com.code.aon.faces.component.util.FaceletUtil;
import com.code.aon.faces.component.util.HTML;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.jsf.ComponentConfig;

/**
 * The Class TabbedPaneComponentHandler.
 * 
 * @author atellitu
 */
public class LookupInputTextHandler extends AonAjaxInputHandler implements ILookupTags {

    private static final String VALUE_CHANGE_LISTENER = "lookupChanged";
    
    private static final String DISABLED_METHOD = "showWindow";
	
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

	private void setDisabled( FaceletContext ctx, UIInput text ) {
		String lookup = getRequiredAttribute(LOOKUP).getValue();
		String disabled = FaceletUtil.appendExpression( lookup, DISABLED_METHOD);
		UIComponentTagUtils.setStringProperty(ctx.getFacesContext(), text, HTML.DISABLED_ATTR, disabled );
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
		UIInput text = (UIInput) instance;
		setDisabled(ctx, text);
		setValueChangeNotifier(ctx, text);
	}

}