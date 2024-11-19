package com.code.aon.faces.component.richfaces.dataTable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UIViewRoot;

import org.richfaces.component.html.HtmlColumn;
import org.richfaces.component.html.HtmlDataTable;

import com.code.aon.faces.component.AonComponentHandler;
import com.code.aon.faces.component.richfaces.form.FormHandler;
import com.code.aon.faces.component.richfaces.lookup.ILookupConstants;
import com.code.aon.ui.form.ExtendedPageDataModel;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentSupport;

import jakarta.el.ELException;

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
	
	public static Collection<HtmlColumn> getVisibleColumns(HtmlDataTable htmlDataTable) {
		List<HtmlColumn> visibleColumns = new ArrayList<>();
		for (Iterator<UIComponent> columns = htmlDataTable.columns(); columns.hasNext();) {
			HtmlColumn column = ( HtmlColumn ) columns.next();
			if ( column.isRendered(true)) {
				visibleColumns.add(column);
			}
		}
		return visibleColumns;
	}

	public static void setVisibleColumns(FaceletContext ctx, HtmlDataTable htmlDataTable) {
		Collection<HtmlColumn> visibleColumns = getVisibleColumns(htmlDataTable);
		String[] visibleFields = visibleColumns.stream().map(col -> getAlias(ctx, col)).filter(Objects::nonNull).toArray(String[]::new);
		ExtendedPageDataModel model = getModel(htmlDataTable);
		if ( model != null ) {
			model.setVisibleFields(visibleFields);
		}
	}
	
	public static String getAlias(FaceletContext ctx,  HtmlColumn htmlColumn) {
		String alias = (String) htmlColumn.getAttributes().get(ILookupConstants.ALIAS);
		if ( alias != null )
			return alias;
		
		return null;
	}

	@Override
	protected void onComponentPopulated(FaceletContext ctx, UIComponent c, UIComponent parent) {
		super.onComponentPopulated(ctx, c, parent);
		try {
			if ( c instanceof HtmlDataTable htmlDataTable ) {
				setVisibleColumns(ctx, htmlDataTable);
			}
		} catch ( Exception e ) {
			//TODO: Remove this :-(
		}
	}

	
	
	
}
