package com.code.aon.faces.component.richfaces.dataTable;

import java.io.IOException;

import jakarta.el.ELException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UIViewRoot;

import com.code.aon.faces.component.AonComponentHandler;
import com.code.aon.faces.component.richfaces.form.FormHandler;
import com.code.aon.ui.form.ExtendedPageDataModel;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentSupport;

public class DataTableHandler extends AonComponentHandler {

	public DataTableHandler(ComponentConfig config) {
		super(config);
	}
	
	@Override
	protected void applyNextHandler(FaceletContext ctx, UIComponent c)
			throws IOException, FacesException, ELException {
		UIViewRoot root = ComponentSupport.getViewRoot(ctx, c);
		FormHandler.getDataTableMap(root).put( getId(ctx), (UIData) c );
		ExtendedPageDataModel model = DataTableHandler.getModel(c);
		if ( model != null ) {
			model.setSortable(false);
		}		
		super.applyNextHandler(ctx, c);
	}
	
	public static ExtendedPageDataModel getModel( UIComponent c ) {
		if (UIData.class.isAssignableFrom(c.getClass())) {
			UIData table = (UIData) c;
			Object model = table.getValue();
			if ( (model != null) && ExtendedPageDataModel.class.isAssignableFrom(model.getClass())) {
				return (ExtendedPageDataModel) model;
			}
		}
		return null;
	}
	
}
