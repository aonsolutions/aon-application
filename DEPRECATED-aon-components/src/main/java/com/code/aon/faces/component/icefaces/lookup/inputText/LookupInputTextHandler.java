package com.code.aon.faces.component.icefaces.lookup.inputText;

import com.code.aon.faces.component.AonComponentHandler;
import com.code.aon.faces.component.icefaces.lookup.ILookupTags;
import com.code.aon.faces.component.myfaces.UIComponentTagUtils;
import com.code.aon.faces.component.sandbox.ValueChangeNotifierHandler;
import com.icesoft.faces.renderkit.dom_html_basic.HTML;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.jsf.ComponentConfig;

/**
 * The Class TabbedPaneComponentHandler.
 * 
 * @author atellitu
 */
public class LookupInputTextHandler extends AonComponentHandler implements ILookupTags {

    private static final String VALUE_CHANGE_LISTENER = "lookupChanged";
    
    private static final String DISABLED_METHOD = "showWindow";
    
    private static final String STYLE_CLASS = "aon-form-input-text";
	
   	/**
	 * The Constructor.
	 * 
	 * @param config the config
	 */
	public LookupInputTextHandler(ComponentConfig config) {
		super( config );
	}

	private void setValueChangeNotifier( FaceletContext ctx, HtmlLookupInputText text ) {
		String lookup = getRequiredAttribute(LOOKUP).getValue();
		String valueChangeListener = appendExpression( lookup, VALUE_CHANGE_LISTENER);
		ValueChangeNotifierHandler.setupClassListener(ctx, text, valueChangeListener);
	}

	private void setDisabled( FaceletContext ctx, HtmlLookupInputText text ) {
		String lookup = getRequiredAttribute(LOOKUP).getValue();
		String disabled = appendExpression( lookup, DISABLED_METHOD);
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
		HtmlLookupInputText text = (HtmlLookupInputText) instance;
		text.setPartialSubmit( true );
		if (! hasValue(ctx, HTML.STYLE_CLASS_ATTR) ) {
			UIComponentTagUtils.setStringProperty(ctx.getFacesContext(), text, HTML.STYLE_CLASS_ATTR, STYLE_CLASS );
		}
		setDisabled(ctx, text);
		setValueChangeNotifier(ctx, text);
	}

}