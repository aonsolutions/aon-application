package com.code.aon.faces.component.richfaces.dataPaginator;

import javax.faces.component.UIComponent;

import org.richfaces.component.UIDatascroller;
import org.richfaces.taglib.DataScrollerTagHandler;

import com.code.aon.faces.component.ComponentManager;
import com.code.aon.faces.component.myfaces.UIComponentTagUtils;
import com.code.aon.faces.component.util.FaceletUtil;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.jsf.ComponentConfig;

public class DataPaginatorHandler extends DataScrollerTagHandler {

	private static final String PAGINATOR_ATTRIBUTE = "paginator";
	
	private static final String FOR_ATTRIBUTE = "for";
	
	private static final String AUTO_VALUE = "auto";
	
	private static final String HIDE_VALUE = "hide";

	private static final String BOUNDARY_CONTROLS_ATTRIBUTE = "boundaryControls";
	
	private static final String FAST_CONTROLS_ATTRIBUTE = "fastControls";
	
	private static final String STEP_CONTROLS_ATTRIBUTE = "stepControls";
	
	public DataPaginatorHandler(ComponentConfig config) {
		super(config);
	}

	private boolean isPaginator(FaceletContext ctx) {
		TagAttribute tag = getAttribute(PAGINATOR_ATTRIBUTE);
		if (tag != null) {
			return tag.getBoolean(ctx);
		}
		return false;
	}
	
	private void setControlsValue( FaceletContext ctx, UIComponent component, String value ) {
		if (! FaceletUtil.hasValue(ctx, tag, BOUNDARY_CONTROLS_ATTRIBUTE) ) {
			UIComponentTagUtils.setStringProperty( ctx.getFacesContext(), component, BOUNDARY_CONTROLS_ATTRIBUTE, value );			
		}
		if (! FaceletUtil.hasValue(ctx, tag, STEP_CONTROLS_ATTRIBUTE) ) {		
			UIComponentTagUtils.setStringProperty( ctx.getFacesContext(), component, STEP_CONTROLS_ATTRIBUTE, value );
		}
		if (! FaceletUtil.hasValue(ctx, tag, FAST_CONTROLS_ATTRIBUTE) ) {
			UIComponentTagUtils.setStringProperty( ctx.getFacesContext(), component, FAST_CONTROLS_ATTRIBUTE, value );
		}
	}
	
	@Override
	protected MetaRuleset createMetaRuleset(Class type) {
		MetaRuleset set = super.createMetaRuleset(type);
		ComponentManager.getInstance().updateMetaRuleset( tag, set );
		return set;
	}
	
	/**
	 * Chapuza necesaria porque el componente de Rich Faces no devuelve correctamente. El getter deberia
	 * devolver el valor en función de un ValueExpression pero no lo hace, porque lo que hay que resolver
	 * el valor y llamar al setter de For. En la version 3.2.0.SR1.
	 * 
	 * @param ctx the ctx
	 * @param component the component
	 */
	private void updateForAttribute( FaceletContext ctx, UIComponent component ) {
		TagAttribute tag = getAttribute(FOR_ATTRIBUTE);
		if (tag != null) {
			String value = tag.getValue(ctx);
			((UIDatascroller) component).setFor(value);
		}
	}

	@Override
	protected void setAttributes(FaceletContext ctx, Object instance) {
		super.setAttributes(ctx, instance);
		ComponentManager.getInstance().setAttributes( tag, ctx, (UIComponent) instance );
		UIComponent component = (UIComponent) instance;
		updateForAttribute(ctx, component);
		if ( isPaginator(ctx) ) {
			setControlsValue(ctx, component, AUTO_VALUE);
		} else {
			setControlsValue(ctx, component, HIDE_VALUE);			
		}
	}
	
}
