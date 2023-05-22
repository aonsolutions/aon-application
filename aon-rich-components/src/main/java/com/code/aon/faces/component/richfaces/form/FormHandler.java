package com.code.aon.faces.component.richfaces.form;

import static com.code.aon.faces.controller.IRichConstants.CURRENT_FORM;
import static com.code.aon.faces.controller.IRichConstants.CURRENT_FORM_DATA_TABLE_MAP;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jakarta.el.ELException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UIViewRoot;

import com.code.aon.faces.component.richfaces.AonAjaxComponentHandler;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentSupport;

public class FormHandler extends AonAjaxComponentHandler {

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
	
	@SuppressWarnings("unchecked")
	public static Map<String,UIData> getDataTableMap( UIViewRoot root ) {
		Map<String,UIData> dataTableMap = (Map<String, UIData>) root.getAttributes().get( CURRENT_FORM_DATA_TABLE_MAP );
		if ( dataTableMap == null ) {
			dataTableMap = new HashMap<String, UIData>();
			root.getAttributes().put( CURRENT_FORM_DATA_TABLE_MAP, dataTableMap );
		}
		return dataTableMap;
	}
	
}
