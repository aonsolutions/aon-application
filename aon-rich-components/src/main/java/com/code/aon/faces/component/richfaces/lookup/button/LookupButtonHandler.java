package com.code.aon.faces.component.richfaces.lookup.button;

import java.io.IOException;

import jakarta.el.ELException;
import jakarta.el.MethodExpression;
import jakarta.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.event.MethodExpressionActionListener;

import com.code.aon.faces.component.myfaces.UIComponentTagUtils;
import com.code.aon.faces.component.richfaces.AonAjaxComponentHandler;
import com.code.aon.faces.component.richfaces.IRichFacesTags;
import com.code.aon.faces.component.richfaces.lookup.ILookupComponent;
import com.code.aon.faces.component.richfaces.lookup.ILookupConstants;
import com.code.aon.faces.component.util.FaceletUtil;
import com.code.aon.faces.component.util.HTML;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.Tag;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.jsf.ComponentConfig;

/**
 * The Class TabbedPaneComponentHandler.
 * 
 * @author atellitu
 */
public class LookupButtonHandler extends AonAjaxComponentHandler implements ILookupConstants, IRichFacesTags {

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

	private void updateType(FaceletContext ctx, HtmlLookupButton button) {
		LookupButtonType type = LookupButtonType.LIST;
		TagAttribute typeTag = getAttribute(ACTION_TYPE);
		if ( typeTag != null ) {
			String value = typeTag.getValue(ctx);
			type = LookupButtonType.get(value);
		}
		button.setButtonType(type);
	}
	
	private String getStyleClass( HtmlLookupButton button ) {
		if ( button.isDisabled() ) {
			return button.getButtonType().getDisabledStyleClass();
		}
		return button.getButtonType().getStyleClass();
	}
	
	private void setActionListener( FaceletContext ctx, HtmlLookupButton button ) {
		String actionListener = button.getButtonType().getActionListener(lookup.getValue());
		MethodExpression me = FaceletUtil.getMethodExpression(ctx, actionListener, null, FaceletUtil.ACTION_LISTENER_SIG);
		button.addActionListener( new MethodExpressionActionListener(me) );
	}

	public static String getLookupBeanName(FaceletContext ctx, TagAttribute lookupTag) {
		String value = FaceletUtil.appendExpression(lookupTag.getValue(), "beanName" );
		ValueExpression name = ctx.getExpressionFactory().createValueExpression(
				ctx, value, String.class);		
		return (String) name.getValue(ctx);
	}
	
	public static String getModalPanelId(FaceletContext ctx, TagAttribute lookupTag) {
		return getLookupBeanName(ctx, lookupTag) + "ModalPanel";
	}
	
	public static void updateProperty( FaceletContext ctx, Tag tag, ILookupComponent component ) {
		TagAttribute tagProperty = FaceletUtil.getAttribute(tag, PROPERTY);
		if ( tagProperty != null ) {
			ValueExpression ve = tagProperty.getValueExpression(ctx, Object.class);
			component.setProperty( ve );
		}		
	}

	public static void setLookupChangeListener( FaceletContext ctx, Tag tag, ILookupComponent component ) {	
		TagAttribute vcl = FaceletUtil.getAttribute(tag, LOOKUP_CHANGE_LISTENER);
		if ( vcl != null ) {
			MethodExpression me = vcl.getMethodExpression(ctx, null, FaceletUtil.LOOKUP_CHANGE_LISTENER_SIG);
			component.setLookupChangeListener( me );
		}
	}
	
	private void setReRender( FaceletContext ctx, HtmlLookupButton button) {
		if ( button.getButtonType() == LookupButtonType.CLEAR ) {
			TagAttribute tagSRR = getAttribute(SELECT_RE_RENDER);
			if ( tagSRR != null ) {
				String value = FaceletUtil.updateList(ctx, getAttribute(RERENDER), tagSRR.getValue(ctx));
				UIComponentTagUtils.setStringProperty(ctx.getFacesContext(), button, RERENDER, value);							
			}
		} else {
			String id = getModalPanelId(ctx, lookup) + "ReRender";
			String reRender = FaceletUtil.updateList(ctx, getAttribute(RERENDER), id);
			UIComponentTagUtils.setStringProperty(ctx.getFacesContext(), button, RERENDER, reRender);
		}
	}
	
	private void updateRendered( FaceletContext ctx, HtmlLookupButton button, boolean resolved ) {
		TagAttribute renderedTag = getAttribute(RENDERED);
		if ( renderedTag == null ) {
			String rendered = null;
			if ( button.getButtonType() == LookupButtonType.CLEAR ) {
				rendered = Boolean.toString(resolved && !button.isDisabled());
			} else {
				rendered = Boolean.toString(!resolved);
			}
			UIComponentTagUtils.setBooleanProperty(ctx.getFacesContext(), button, RENDERED, rendered);
		}
	}	

	private void updateStyleClass( FaceletContext ctx, HtmlLookupButton button) {
		if (! FaceletUtil.hasValue(ctx, tag, HTML.STYLE_CLASS_ATTR) ) {
			UIComponentTagUtils.setStringProperty(ctx.getFacesContext(), button, HTML.STYLE_CLASS_ATTR, getStyleClass(button) );
		}
	}	
	
	private void setLookupAction( FaceletContext ctx, HtmlLookupButton button ) {
		TagAttribute lookupActionTag = getAttribute(LOOKUP_ACTION);
		if ( lookupActionTag != null ) {
			MethodExpression lookupAction = lookupActionTag.getMethodExpression(ctx, 
					String.class, FaceletUtil.ACTION_SIG);
			button.setLookupAction(lookupAction);
		} 
	}
	
	private void configureButton( FaceletContext ctx, HtmlLookupButton button) {
		setActionListener(ctx, button);		
		if (! FaceletUtil.hasValue(ctx, tag, HTML.TITLE_ATTR) ) {
			String title = button.getButtonType().getTitle();
			UIComponentTagUtils.setStringProperty(ctx.getFacesContext(), button, HTML.TITLE_ATTR, title );			
		}		
		setReRender(ctx, button);
		setLookupAction(ctx, button);
	}
	
	private void updateDisabled( FaceletContext ctx, HtmlLookupButton button, boolean resolved ) {
		LookupButtonType type = button.getButtonType();
		if ( (type == LookupButtonType.SEARCH) || (type == LookupButtonType.LIST) ) {
			if (! FaceletUtil.hasValue(ctx, tag, HTML.DISABLED_ATTR) ) {
				button.setDisabled(resolved);
			}
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
		HtmlLookupButton button = (HtmlLookupButton) instance;
		updateProperty(ctx, tag, button);
		setLookupChangeListener(ctx, tag, button);
		updateType(ctx, button); 
		configureButton(ctx, button);
	}

	@Override
	protected void applyNextHandler(FaceletContext ctx, UIComponent c)
			throws IOException, FacesException, ELException {
		HtmlLookupButton button = (HtmlLookupButton) c;
		String buttonId = button.getClientId(ctx.getFacesContext());
		button.setWindowCloseFocus( buttonId );
		boolean resolved = button.isResolved();
		updateDisabled(ctx, button, resolved);		
		updateRendered(ctx, button, resolved);
		updateStyleClass(ctx, button);
		super.applyNextHandler(ctx, c);
	}
	
}