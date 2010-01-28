package com.code.aon.faces.component.richfaces.form;

import java.io.IOException;
import java.util.HashMap;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UIViewRoot;

import com.code.aon.faces.component.richfaces.AonAjaxComponentHandler;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentSupport;

public class FormHandler extends AonAjaxComponentHandler {

	public static final String CURRENT_FORM = "com.code.aon.faces.Form.current";
	
	public static final String CURRENT_FORM_DATA_TABLE_MAP = "com.code.aon.faces.Form.current.DataTable.map";
	
	public FormHandler(ComponentConfig config) {
		super(config);
	}
	
	@Override
	protected void applyNextHandler(FaceletContext ctx, UIComponent component)
			throws IOException, FacesException, ELException {
		UIViewRoot root = ComponentSupport.getViewRoot(ctx, component);
		root.getAttributes().put( CURRENT_FORM, component );
		root.getAttributes().put( CURRENT_FORM_DATA_TABLE_MAP, new HashMap<String, UIData>() );
		super.applyNextHandler(ctx, component);
	}

	@Override
	protected void onComponentPopulated(FaceletContext ctx, UIComponent c,
			UIComponent parent) {
		UIViewRoot root = ComponentSupport.getViewRoot(ctx, c);
		root.getAttributes().remove( CURRENT_FORM );
		root.getAttributes().remove( CURRENT_FORM_DATA_TABLE_MAP );
	}
	
}
