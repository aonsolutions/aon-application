package com.code.aon.faces.component;

import static com.code.aon.faces.controller.IRichConstants.LABELS_MAP;

import java.net.URL;
import java.util.Map;

import jakarta.el.VariableMapper;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UIOutput;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.faces.component.myfaces.UIComponentTagUtils;
import com.code.aon.faces.component.util.FaceletUtil;
import com.code.aon.faces.component.util.HTML;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentHandler;

public class AonComponentHandler extends ComponentHandler {

	private static final String INPUT_REQUIRED_STYLE_CLASS = "aon-input-required";
	
    private ComponentConfig config;
    
    /**
	 * The Constructor.
	 * 
	 * @param config
	 *            the config
	 */
	public AonComponentHandler( ComponentConfig config) {
		super(config);
		this.config = config;
	}
	
	public ComponentConfig getConfig() {
		return config;
	}

	protected void insertTemplate(FaceletContext ctx, UIComponent parent, URL template, VariableMapper newMapper ) {
		FaceletUtil.insertTemplate(ctx, this.tag, parent, template, newMapper);
	}
	
	public boolean hasValue(FaceletContext ctx, String name) {
		return FaceletUtil.hasValue(ctx, tag, name);
	}	

	@Override
	protected MetaRuleset createMetaRuleset(Class type) {
		MetaRuleset set = super.createMetaRuleset(type);
		ComponentManager.getInstance().updateMetaRuleset( tag, set );
		return set;
	}

	@Override
	protected void setAttributes( FaceletContext ctx, Object instance ) {
		super.setAttributes(ctx, instance);
		ComponentManager.getInstance().setAttributes( tag, ctx, (UIComponent) instance );
		if ( instance instanceof UIInput ) {
			updateLabel(ctx, (UIInput) instance);	
		}
	}

	@Override
	protected void onComponentCreated(FaceletContext ctx, UIComponent c,
			UIComponent parent) {
		ComponentManager.getInstance().onComponentCreated( ctx, c, parent );
	}	
	
	@Override
	protected void onComponentPopulated(FaceletContext ctx, UIComponent c,
			UIComponent parent) {
		ComponentManager.getInstance().onComponentPopulated( tag, ctx, c, parent );
	}

	private void updateLabelStyleClass(FaceletContext ctx, UIOutput label) {
		String styleClassAttribute = ComponentManager.getInputStyleClass(label);
		FaceletUtil.addStyleClass(ctx.getFacesContext(), label, styleClassAttribute, INPUT_REQUIRED_STYLE_CLASS);
	}	
	
	private void updateLabel(FaceletContext ctx, UIInput c) {
		Map map = (Map) FaceletUtil.getRequestValue(ctx, LABELS_MAP);
		if (map != null) {
			String id = StringUtils.substringBefore(getId(ctx), "-");
			UIOutput label = (UIOutput) map.get(id);
			if ( label != null ) {
				String value = ObjectUtils.toString(label.getValue());
				if (! StringUtils.isEmpty(value) ) {
					UIComponentTagUtils.setStringProperty(ctx.getFacesContext(), c, HTML.LABEL_ATTR, value.toString());	
				}
				if ( (c instanceof EditableValueHolder)  ) {
					EditableValueHolder evh = (EditableValueHolder) c;
					if ( evh.isRequired() ) {
						updateLabelStyleClass(ctx, label);
					}
				}
			}
		}
	}
	
}
