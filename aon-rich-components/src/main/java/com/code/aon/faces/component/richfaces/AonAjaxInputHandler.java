package com.code.aon.faces.component.richfaces;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.el.ELException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import org.ajax4jsf.taglib.html.facelets.AjaxSupportHandler;
import org.apache.commons.lang.StringUtils;

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
	
	private static final String AON_STATUS = "aonStatus";
	
	private TagAttribute partialSubmit;
	
	private TagAttribute reRender;
	
	private TagAttribute ajaxSingle;
	
	private TagAttribute actionListener;
	
	private boolean ajaxNeeded;
	
	public AonAjaxInputHandler(ComponentConfig config) {
		super( config );
		partialSubmit = getAttribute(PARTIAL_SUBMIT);
		reRender = getAttribute(RERENDER);
		ajaxSingle = getAttribute(AJAX_SINGLE);
		actionListener = getAttribute(ACTION_LISTENER);
		ajaxNeeded = isTrueValue(partialSubmit) || isTrueValue(ajaxSingle)
			|| (reRender != null) || (actionListener != null);
	}
	
	private boolean isTrueValue( TagAttribute tag ) {
		if ( tag != null ) {
			String value = tag.getValue();
			return StringUtils.equals( value, Boolean.TRUE.toString() );
		}
		return false;
	}
	
	@Override
	protected MetaRuleset createMetaRuleset(Class type) {
		MetaRuleset set = super.createMetaRuleset(type);
		set.ignore(RERENDER).ignore(PARTIAL_SUBMIT).ignore(STATUS);
		set.ignore(AJAX_SINGLE).ignore(FOCUS).ignore(ACTION_LISTENER);
		return set;
	}

	protected void setReRender(TagAttribute reRender) {
		this.reRender = reRender;
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
	
	protected void initAjaxSupport( FaceletContext ctx, List<TagAttribute> attributes ) {
	}
	
	@Override
	protected void applyNextHandler(FaceletContext ctx, UIComponent c)
			throws IOException, FacesException, ELException {
		super.applyNextHandler(ctx, c);
		if ( isAjaxNeeded() ) {
			List<TagAttribute> attributes = new ArrayList<TagAttribute>();
			initAjaxSupport(ctx, attributes);
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
				ajaxSingleValue = ajaxSingle.getValue();
			}
			attributes.add( BasicComponentConfig.newAttribute(tag, AJAX_SINGLE, ajaxSingleValue) );
			if ( reRender != null ) {
				String value = reRender.getValue();
				attributes.add( BasicComponentConfig.newAttribute(tag, RERENDER, value) );
			}
			TagAttribute focus = getAttribute(FOCUS);
			if ( focus != null ) {
				String value = focus.getValue();
				attributes.add( BasicComponentConfig.newAttribute(tag, FOCUS, value) );					
			}
			TagAttribute process = getAttribute(PROCESS);
			if ( process != null ) {
				String value = process.getValue();
				attributes.add( BasicComponentConfig.newAttribute(tag, PROCESS, value) );					
			}
			TagAttribute onsubmit = getAttribute(ON_SUBMIT);
			if ( onsubmit != null ) {
				String value = onsubmit.getValue();
				attributes.add( BasicComponentConfig.newAttribute(tag, ON_SUBMIT, value) );	
			}
			TagAttribute oncomplete = getAttribute(ON_COMPLETE);
			if ( oncomplete != null ) {
				String value = oncomplete.getValue();
				attributes.add( BasicComponentConfig.newAttribute(tag, ON_COMPLETE, value) );	
			}
			if ( actionListener != null ) {
				String value = actionListener.getValue();
				attributes.add( BasicComponentConfig.newAttribute(tag, ACTION_LISTENER, value) );					
			}
			String statusValue = AON_STATUS;
			TagAttribute status = getAttribute(STATUS);
			if ( status != null ) {
				statusValue = focus.getValue();
			}
			attributes.add( BasicComponentConfig.newAttribute(tag, STATUS, statusValue) );				
			BasicComponentConfig config = new BasicComponentConfig(getConfig(), attributes );
			config.setComponentType(SUPPORT_COMPONENT_TYPE);
			config.setRendererType(SUPPORT_RENDERER_TYPE);
			config.setNextHandler(FaceletUtil.LEAF_HANDLER);
			TagHandler ajaxSupportHandler = new AjaxSupportHandler(config);
			ajaxSupportHandler.apply(ctx, c);
		}
	}
	
}
