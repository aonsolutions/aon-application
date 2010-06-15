package com.code.aon.faces.component.icefaces.lookup.button;

import javax.el.MethodExpression;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.faces.component.AonComponentHandler;
import com.code.aon.faces.component.icefaces.lookup.ILookupTags;
import com.code.aon.faces.component.myfaces.UIComponentTagUtils;
import com.icesoft.faces.renderkit.dom_html_basic.HTML;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.el.LegacyMethodBinding;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.jsf.ComponentConfig;

/**
 * The Class TabbedPaneComponentHandler.
 * 
 * @author atellitu
 */
public class LookupButtonHandler extends AonComponentHandler implements ILookupTags {

   	private static final String LIST_STYLE_CLASS = "aon-form-lookup-button";

   	private static final String NEW_STYLE_CLASS = "aon-form-new-button";

   	private static final String LIST_TITLE = "#{bundle.aon_open_select_window}";
   	
   	private static final String NEW_TITLE = "#{bundle.aon_open_new_window}";
   	
    private static final String LIST_ACTION_LISTENER = "onShowListWindow";
	
   	private static final String SEARCH_ACTION_LISTENER = "onShowSearchWindow";
   	
   	private static final String NEW_ACTION_LISTENER = "onShowNewWindow";
   	
   	private static final String WINDOW_TITLE = "windowTitle";
   	
   	private static final String TEMPLATE = "template";
   	
   	private static final String DEFAULT_TEMPLATE = "/facelet/lookup/panelPopup.xhtml";
   	
   	private static final Class[] VALUE_LISTENER_ARGS = {ValueChangeEvent.class};
   	
   	private String lookup;
   	
   	private String windowTitle;
   	
   	/**
	 * The Constructor.
	 * 
	 * @param config the config
	 */
	public LookupButtonHandler(ComponentConfig config) {
		super( config );
		lookup = getRequiredAttribute(LOOKUP).getValue();		
		windowTitle = getRequiredAttribute(WINDOW_TITLE).getValue();
	}

	@Override
	protected MetaRuleset createMetaRuleset(Class type) {
		MetaRuleset mrs = super.createMetaRuleset(type);
		mrs.ignore(ACTION_TYPE);
		return mrs;
	}

	private LookupButtonType getType(FaceletContext ctx) {
		LookupButtonType type = LookupButtonType.LIST;
		TagAttribute typeTag = getAttribute(ACTION_TYPE);
		if ( typeTag != null ) {
			String value = typeTag.getValue(ctx);
			type = LookupButtonType.get(value);
		}
		return type;
	}
	
	private String getStyleClass( LookupButtonType type ) {
		if ( type == LookupButtonType.NEW ) {
			return NEW_STYLE_CLASS;
		}
		return LIST_STYLE_CLASS;
	}

	private String getTitle( LookupButtonType type ) {
		if ( type == LookupButtonType.NEW ) {
			return NEW_TITLE;
		}
		return LIST_TITLE;
	}
	
	private void setActionListener( FaceletContext ctx, HtmlLookupButton button ) {
		String actionListener = null;
		switch ( button.getActionType() ) {
			case LIST:
				actionListener = appendExpression( lookup, LIST_ACTION_LISTENER);
				break;
			case NEW:
				actionListener = appendExpression( lookup, NEW_ACTION_LISTENER);				
				break;
			case SEARCH:
				actionListener = appendExpression( lookup, SEARCH_ACTION_LISTENER);				
				break;
		}
		UIComponentTagUtils.setActionListenerProperty( ctx.getFacesContext(), button, actionListener);
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
		HtmlLookupButton button = (HtmlLookupButton) instance;
		button.setPartialSubmit( true );
		button.setValue("");
		LookupButtonType type = getType(ctx); 
		button.setActionType( type );
		if (! hasValue(ctx, HTML.STYLE_CLASS_ATTR) ) {
			UIComponentTagUtils.setStringProperty(ctx.getFacesContext(), button, HTML.STYLE_CLASS_ATTR, getStyleClass(type) );
		}
		if (! hasValue(ctx, HTML.TITLE_ATTR) ) {
			UIComponentTagUtils.setStringProperty(ctx.getFacesContext(), button, HTML.TITLE_ATTR, getTitle(type) );			
		}
		TagAttribute vcl = getAttribute("valueChangeListener");
		if ( vcl != null ) {
			MethodExpression me = vcl.getMethodExpression(ctx, null, VALUE_LISTENER_ARGS);
			button.setValueChangeListener( new LegacyMethodBinding(me) );
		}
		setActionListener(ctx, button);
	}
}