package com.code.aon.faces.component.richfaces.editDataTable;

import java.io.IOException;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UIViewRoot;

import com.code.aon.faces.component.AonComponentHandler;
import com.code.aon.faces.component.richfaces.dataTable.DataTableHandler;
import com.code.aon.faces.component.richfaces.form.FormHandler;
import com.code.aon.ui.form.ExtendedPageDataModel;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentSupport;

public class EditDataTableHandler extends AonComponentHandler {

	public static final String EDIT_DATA_TABLE_ID = "com.code.aon.faces.EditDataTable.id";
	
	public EditDataTableHandler(ComponentConfig config) {
		super(config);
	}
	
	@Override
	protected void applyNextHandler(FaceletContext ctx, UIComponent c)
			throws IOException, FacesException, ELException {
		UIViewRoot root = ComponentSupport.getViewRoot(ctx, c);
		root.getAttributes().put( EDIT_DATA_TABLE_ID, getId(ctx) );
		FormHandler.getDataTableMap(root).put( getId(ctx), (UIData) c );
		ExtendedPageDataModel model = DataTableHandler.getModel(c);
		if ( model != null ) {
			model.setSortable(false);
		}				
		super.applyNextHandler(ctx, c);
	}

	@Override
	protected void onComponentPopulated(FaceletContext ctx, UIComponent c,
			UIComponent parent) {
		UIViewRoot root = ComponentSupport.getViewRoot(ctx, c);
		root.getAttributes().remove( EDIT_DATA_TABLE_ID );
	}
	
}
