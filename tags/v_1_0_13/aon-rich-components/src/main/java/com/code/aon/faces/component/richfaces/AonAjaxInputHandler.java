package com.code.aon.faces.component.richfaces;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;

import org.ajax4jsf.taglib.html.facelets.AjaxSupportHandler;

import com.code.aon.faces.component.AonComponentHandler;
import com.code.aon.faces.component.AttributeInfo;
import com.code.aon.faces.component.ComponentInfo;
import com.code.aon.faces.component.ComponentManager;
import com.code.aon.faces.component.myfaces.UIComponentTagUtils;
import com.code.aon.faces.component.richfaces.outputLabel.OutputLabelHandler;
import com.code.aon.faces.component.util.BasicComponentConfig;
import com.code.aon.faces.component.util.HTML;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagHandler;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentSupport;

public class AonAjaxInputHandler extends AonComponentHandler implements IRichFacesTags, HTML {

	private static final String SUPPORT_RENDERER_TYPE = "org.ajax4jsf.components.AjaxSupportRenderer";

	private static final String SUPPORT_COMPONENT_TYPE = "org.ajax4jsf.Support";

	private static final String DISABLED_STYLE_CLASS = "disabledStyleClass";
	
	private static final String PARTIAL_SUBMIT = "partialSubmit";
	
	private TagHandler ajaxSupportHandler; 
	
	private TagAttribute partialSubmit;
	
	private TagAttribute reRender;
	
	private boolean ajaxNeeded;
	
	public AonAjaxInputHandler(ComponentConfig config) {
		super(config);
		partialSubmit = getAttribute(PARTIAL_SUBMIT);
		reRender = getAttribute(RERENDER);
		ajaxNeeded = (partialSubmit != null) || (reRender != null);
	}
	
	@Override
	protected MetaRuleset createMetaRuleset(Class type) {
		MetaRuleset set = super.createMetaRuleset(type);
		set.ignore(DISABLED_STYLE_CLASS);
		set.ignore(RERENDER).ignore(PARTIAL_SUBMIT);
		return set;
	}

	@Override
	protected void setAttributes( FaceletContext ctx, Object instance ) {
		super.setAttributes(ctx, instance);
		UIComponent component = (UIComponent) instance;
		updateDisabledStyleClass(ctx, component);
		updateLabel(ctx, component);
	}
	
	protected String getInputStyleClass() {
		return STYLE_CLASS_ATTR;
	}
	
	private void updateDisabledStyleClass(FaceletContext ctx, UIComponent c) {
		TagAttribute disabledClass = getAttribute(DISABLED_STYLE_CLASS);
		if ( disabledClass != null ) {
			TagAttribute disabled = getAttribute(DISABLED_ATTR);
			if ( (disabled != null) && disabled.getBoolean(ctx) ) {
				String value = disabledClass.getValue(ctx);
				UIComponentTagUtils.setStringProperty(ctx.getFacesContext(), c, getInputStyleClass(), value);
			}
		}
	}
	
	private void updateLabel(FaceletContext ctx, UIComponent c) {
		UIViewRoot root = ComponentSupport.getViewRoot(ctx, c);		
		Map map = (Map) root.getAttributes().get(OutputLabelHandler.LABELS_MAP);
		if (map != null) {
			Object value = map.get(getId(ctx));
			if ( value != null ) {
				UIComponentTagUtils.setStringProperty(ctx.getFacesContext(), c, LABEL_ATTR, value.toString());				
			}
		}
	}

	public boolean isAjaxNeeded() {
		return ajaxNeeded;
	}

	public void setAjaxNeeded(boolean ajaxNeeded) {
		this.ajaxNeeded = ajaxNeeded;
	}
	
	public TagAttribute getRendered() {
		return getAttribute("rendered");
	}

	private String getAjaxEvent() {
		ComponentInfo info = ComponentManager.getInstance().getComponentInfo(this);
		if ( info != null ) {
			AttributeInfo ainfo = info.getAttributeInfo(PARTIAL_SUBMIT);
			if ( (ainfo != null) && (ainfo.getValue() != null) ) {
				return ainfo.getValue();
			}
		}
		return ONCHANGE_ATTR;
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
				if ( reRender != null ) {
					String value = reRender.getValue(ctx);
					attributes.add( BasicComponentConfig.newAttribute(tag, RERENDER, value) );
				}
				BasicComponentConfig config = new BasicComponentConfig(getConfig(), attributes );
				config.setComponentType(SUPPORT_COMPONENT_TYPE);
				config.setRendererType(SUPPORT_RENDERER_TYPE);
				ajaxSupportHandler = new AjaxSupportHandler(config);
			}
			ajaxSupportHandler.apply(ctx, c);
		}
	}
	
}
