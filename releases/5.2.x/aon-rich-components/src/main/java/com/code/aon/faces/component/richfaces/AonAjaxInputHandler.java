package com.code.aon.faces.component.richfaces;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import org.ajax4jsf.taglib.html.facelets.AjaxSupportHandler;

import com.code.aon.faces.component.AonComponentHandler;
import com.code.aon.faces.component.AttributeInfo;
import com.code.aon.faces.component.ComponentInfo;
import com.code.aon.faces.component.ComponentManager;
import com.code.aon.faces.component.util.BasicComponentConfig;
import com.code.aon.faces.component.util.FaceletUtil;
import com.code.aon.faces.component.util.HTML;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagHandler;
import com.sun.facelets.tag.jsf.ComponentConfig;

public class AonAjaxInputHandler extends AonComponentHandler implements IRichFacesTags {

	private static final String SUPPORT_RENDERER_TYPE = "org.ajax4jsf.components.AjaxSupportRenderer";

	private static final String SUPPORT_COMPONENT_TYPE = "org.ajax4jsf.Support";
	
	private static final String PARTIAL_SUBMIT = "partialSubmit";
	
	private TagHandler ajaxSupportHandler; 
	
	private TagAttribute partialSubmit;
	
	private TagAttribute reRender;
	
	private TagAttribute ajaxSingle;
	
	private boolean ajaxNeeded;
	
	public AonAjaxInputHandler(ComponentConfig config) {
		super(config);
		partialSubmit = getAttribute(PARTIAL_SUBMIT);
		reRender = getAttribute(RERENDER);
		ajaxSingle = getAttribute(AJAX_SINGLE);
		ajaxNeeded = (partialSubmit != null) || (reRender != null) || (ajaxSingle != null);
	}
	
	@Override
	protected MetaRuleset createMetaRuleset(Class type) {
		MetaRuleset set = super.createMetaRuleset(type);
		set.ignore(RERENDER).ignore(PARTIAL_SUBMIT);
		set.ignore(AJAX_SINGLE).ignore(FOCUS);
		return set;
	}

	@Override
	protected void setAttributes( FaceletContext ctx, Object instance ) {
		super.setAttributes(ctx, instance);
		UIComponent component = (UIComponent) instance;
	}

	public boolean isAjaxNeeded() {
		return ajaxNeeded;
	}

	public void setAjaxNeeded(boolean ajaxNeeded) {
		this.ajaxNeeded = ajaxNeeded;
	}
	
	public TagAttribute getRendered() {
		return getAttribute(RENDERED);
	}

	private String getAjaxEvent() {
		ComponentInfo info = ComponentManager.getInstance().getComponentInfo(this);
		if ( info != null ) {
			AttributeInfo ainfo = info.getAttributeInfo(PARTIAL_SUBMIT);
			if ( (ainfo != null) && (ainfo.getValue() != null) ) {
				return ainfo.getValue();
			}
		}
		return HTML.ONCHANGE_ATTR;
	}
	
	@Override
	protected void applyNextHandler(FaceletContext ctx, UIComponent c)
			throws IOException, FacesException, ELException {
		super.applyNextHandler(ctx, c);
		if ( isAjaxNeeded() ) {
			if ( this.ajaxSupportHandler == null ) {
				List<TagAttribute> attributes = new ArrayList<TagAttribute>();
				String event = getAjaxEvent();
				if ( partialSubmit != null ) {
					String value = partialSubmit.getValue(ctx);
					if (! "true".equals(value) ) {
						event = value;
					}
				}
				attributes.add( BasicComponentConfig.newAttribute(tag, EVENT, event) );
				String ajaxSingleValue = "true";
				if ( ajaxSingle != null ) {
					ajaxSingleValue = ajaxSingle.getValue(ctx);
				}
				attributes.add( BasicComponentConfig.newAttribute(tag, AJAX_SINGLE, ajaxSingleValue) );
				if ( reRender != null ) {
					String value = reRender.getValue(ctx);
					attributes.add( BasicComponentConfig.newAttribute(tag, RERENDER, value) );
				}
				TagAttribute focus = getAttribute(FOCUS);
				if ( focus != null ) {
					String value = focus.getValue(ctx);
					attributes.add( BasicComponentConfig.newAttribute(tag, FOCUS, value) );					
				}
				BasicComponentConfig config = new BasicComponentConfig(getConfig(), attributes );
				config.setComponentType(SUPPORT_COMPONENT_TYPE);
				config.setRendererType(SUPPORT_RENDERER_TYPE);
				config.setNextHandler(FaceletUtil.LEAF_HANDLER);
				ajaxSupportHandler = new AjaxSupportHandler(config);
			}
			ajaxSupportHandler.apply(ctx, c);
		}
	}
	
}
