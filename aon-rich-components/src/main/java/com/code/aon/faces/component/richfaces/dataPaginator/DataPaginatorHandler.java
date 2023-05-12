package com.code.aon.faces.component.richfaces.dataPaginator;

import java.io.IOException;

import jakarta.el.ELException;
import jakarta.el.VariableMapper;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import org.richfaces.taglib.DataScrollerTagHandler;

import com.code.aon.faces.component.ComponentManager;
import com.code.aon.faces.component.myfaces.UIComponentTagUtils;
import com.code.aon.faces.component.util.FaceletUtil;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.jsf.ComponentConfig;

public class DataPaginatorHandler extends DataScrollerTagHandler {

	private static final String TEMPLATE_PATH = "com/code/aon/faces/component/richfaces/dataPaginator/";

	private static final String TEMPLATE = TEMPLATE_PATH + "dataPaginator.xhtml";
	
	private static final String PAGINATOR_ATTRIBUTE = "paginator";
	
	private static final String SHOW_VALUE = "show";
	
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
			UIComponentTagUtils.setStringProperty( ctx.getFacesContext(), component, FAST_CONTROLS_ATTRIBUTE, HIDE_VALUE );
		}
	}
	
	@Override
	protected MetaRuleset createMetaRuleset(Class type) {
		MetaRuleset set = super.createMetaRuleset(type);
		ComponentManager.getInstance().updateMetaRuleset( tag, set );
		return set;
	}
	
	@Override
	protected void setAttributes(FaceletContext ctx, Object instance) {
		super.setAttributes(ctx, instance);
		ComponentManager.getInstance().setAttributes( tag, ctx, (UIComponent) instance );
		UIComponent component = (UIComponent) instance;
		if ( isPaginator(ctx) ) {
			setControlsValue(ctx, component, SHOW_VALUE);
		} else {
			setControlsValue(ctx, component, HIDE_VALUE);			
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
	
	@Override
	protected void applyNextHandler(FaceletContext ctx, UIComponent c)
			throws IOException, FacesException, ELException {
		if ( isPaginator(ctx) ) {
			VariableMapper mapper = ctx.getVariableMapper();
			FaceletUtil.insertTemplate(ctx, tag, c, FaceletUtil.getTemplate(TEMPLATE), mapper);
		}
		super.applyNextHandler(ctx, c);
	}
	
}
