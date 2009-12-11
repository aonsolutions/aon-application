package com.code.aon.faces.component.richfaces.form;

import java.io.IOException;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;

import com.code.aon.faces.component.richfaces.AonAjaxComponentHandler;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentSupport;

public class FormHandler extends AonAjaxComponentHandler {

	public static final String CURRENT_FORM = "com.code.aon.faces.Form.current";
	
	public FormHandler(ComponentConfig config) {
		super(config);
	}
	
	@Override
	protected void applyNextHandler(FaceletContext ctx, UIComponent component)
			throws IOException, FacesException, ELException {
		UIViewRoot root = ComponentSupport.getViewRoot(ctx, component);
		root.getAttributes().put( CURRENT_FORM, component );
		super.applyNextHandler(ctx, component);
	}

	@Override
	protected void onComponentPopulated(FaceletContext ctx, UIComponent c,
			UIComponent parent) {
		UIViewRoot root = ComponentSupport.getViewRoot(ctx, c);
		root.getAttributes().remove( CURRENT_FORM );
	}
	
}
