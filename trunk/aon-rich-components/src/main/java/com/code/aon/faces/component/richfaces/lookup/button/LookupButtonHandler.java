package com.code.aon.faces.component.richfaces.lookup.button;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.faces.component.myfaces.UIComponentTagUtils;
import com.code.aon.faces.component.richfaces.AonAjaxComponentHandler;
import com.code.aon.faces.component.richfaces.IRichFacesTags;
import com.code.aon.faces.component.richfaces.lookup.ILookupTags;
import com.code.aon.faces.component.util.FaceletUtil;
import com.code.aon.faces.component.util.HTML;
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
public class LookupButtonHandler extends AonAjaxComponentHandler implements ILookupTags, IRichFacesTags {

   	private static final String LIST_STYLE_CLASS = "aon-lookupButton";

   	private static final String NEW_STYLE_CLASS = "aon-lookupButton-new";

   	private static final String LIST_TITLE = "#{bundle.aon_open_select_window}";
   	
   	private static final String NEW_TITLE = "#{bundle.aon_open_new_window}";
   	
    private static final String LIST_ACTION_LISTENER = "onShowListWindow";
	
   	private static final String SEARCH_ACTION_LISTENER = "onShowSearchWindow";
   	
   	private static final String NEW_ACTION_LISTENER = "onShowNewWindow";
   	
   	private static final Class[] VALUE_LISTENER_ARGS = {ValueChangeEvent.class};
   	
   	private TagAttribute lookup;
   	
   	/**
	 * The Constructor.
	 * 
	 * @param config the config
	 */
	public LookupButtonHandler(ComponentConfig config) {
		super( config );
		lookup = getRequiredAttribute(LOOKUP);		
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
		String value = lookup.getValue();
		switch ( button.getActionType() ) {
			case LIST:
				actionListener = FaceletUtil.appendExpression( value, LIST_ACTION_LISTENER);
				break;
			case NEW:
				actionListener = FaceletUtil.appendExpression( value, NEW_ACTION_LISTENER);				
				break;
			case SEARCH:
				actionListener = FaceletUtil.appendExpression( value, SEARCH_ACTION_LISTENER);				
				break;
		}
		UIComponentTagUtils.setActionListenerProperty( ctx.getFacesContext(), button, actionListener);
	}
	
	public static String getModalPanelId(FaceletContext ctx, TagAttribute lookupTag) {
		String value = FaceletUtil.appendExpression(lookupTag.getValue(), "beanName" );
		ValueExpression id = ctx.getExpressionFactory().createValueExpression(
				ctx, value, String.class);		
		return id.getValue(ctx) + "ModalPanel";
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
		button.setValue("");
		LookupButtonType type = getType(ctx); 
		button.setActionType( type );
		if (! FaceletUtil.hasValue(ctx, tag, HTML.STYLE_CLASS_ATTR) ) {
			UIComponentTagUtils.setStringProperty(ctx.getFacesContext(), button, HTML.STYLE_CLASS_ATTR, getStyleClass(type) );
		}
		if (! FaceletUtil.hasValue(ctx, tag, HTML.TITLE_ATTR) ) {
			UIComponentTagUtils.setStringProperty(ctx.getFacesContext(), button, HTML.TITLE_ATTR, getTitle(type) );			
		}
		TagAttribute vcl = getAttribute("valueChangeListener");
		if ( vcl != null ) {
			MethodExpression me = vcl.getMethodExpression(ctx, null, VALUE_LISTENER_ARGS);
			button.setValueChangeListener( new LegacyMethodBinding(me) );
		}
		TagAttribute windowTitle = getAttribute(WINDOW_TITLE);
		if ( windowTitle != null ) {
			String value = windowTitle.getValue(ctx);
			UIComponentTagUtils.setStringProperty(ctx.getFacesContext(), button, WINDOW_TITLE, value );
		}
		TagAttribute selectReRender = getAttribute(SELECT_RE_RENDER);
		if ( selectReRender != null ) {
			String value = selectReRender.getValue(ctx);
			UIComponentTagUtils.setStringProperty(ctx.getFacesContext(), button, SELECT_RE_RENDER, value );
		}
		TagAttribute minHeight = getAttribute(MIN_HEIGHT);
		if ( minHeight != null ) {
			String value = minHeight.getValue(ctx);
			UIComponentTagUtils.setStringProperty(ctx.getFacesContext(), button, MIN_HEIGHT, value );
		}
		TagAttribute minWidth = getAttribute(MIN_WIDTH);
		if ( minWidth != null ) {
			String value = minWidth.getValue(ctx);
			UIComponentTagUtils.setStringProperty(ctx.getFacesContext(), button, MIN_WIDTH, value );
		}
		setActionListener(ctx, button);
		String id = getModalPanelId(ctx, lookup) + "ReRender";
		String value = FaceletUtil.updateList(ctx, getAttribute(RERENDER), id);
		UIComponentTagUtils.setStringProperty(ctx.getFacesContext(), button, RERENDER, value);
	}
	
}