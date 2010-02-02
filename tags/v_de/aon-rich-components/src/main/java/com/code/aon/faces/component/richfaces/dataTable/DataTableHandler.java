package com.code.aon.faces.component.richfaces.dataTable;

import java.io.IOException;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UIViewRoot;

import com.code.aon.faces.component.AonComponentHandler;
import com.code.aon.faces.component.richfaces.form.FormHandler;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentSupport;

public class DataTableHandler extends AonComponentHandler {

	public DataTableHandler(ComponentConfig config) {
		super(config);
	}
	
	@Override
	@SuppressWarnings("unchecked")	
	protected void applyNextHandler(FaceletContext ctx, UIComponent c)
			throws IOException, FacesException, ELException {
		UIViewRoot root = ComponentSupport.getViewRoot(ctx, c);
		FormHandler.getDataTableMap(root).put( getId(ctx), (UIData) c );
		super.applyNextHandler(ctx, c);
	}
	
}
