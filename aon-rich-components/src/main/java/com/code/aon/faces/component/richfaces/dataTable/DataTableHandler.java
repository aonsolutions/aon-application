package com.code.aon.faces.component.richfaces.dataTable;

import javax.faces.component.UIComponent;

import com.code.aon.faces.component.AonComponentHandler;
import com.code.aon.faces.component.HTML;
import com.code.aon.faces.component.myfaces.UIComponentTagUtils;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.jsf.ComponentConfig;

public class DataTableHandler extends AonComponentHandler {

	private static final String HEADER_STYLE_CLASS_ATTR_NAME = "headerClass";
	private static final String FOOTER_STYLE_CLASS_ATTR_NAME = "footerClass";
	private static final String ROW_CLASSES_ATTR_NAME = "rowClasses";

	private static final String TABLE_STYLE_CLASS = "aon-table";
	private static final String HEADER_STYLE_CLASS = "aon-table-header";
	private static final String FOOTER_STYLE_CLASS = "aon-table-footer";
	private static final String ROW_STYLE_CLASSES = "aon-table-row-odd, aon-table-row-even";

	public DataTableHandler(ComponentConfig config) {
		super(config);
	}
	
	@Override
	protected void setAttributes(FaceletContext ctx, Object instance) {
		UIComponent table = (UIComponent) instance;
		super.setAttributes(ctx, table);
		if (! hasValue(ctx, HTML.STYLE_CLASS_ATTR) ) {
			UIComponentTagUtils.setStringProperty(ctx.getFacesContext(), table, HTML.STYLE_CLASS_ATTR, TABLE_STYLE_CLASS );
		}
		if (! hasValue(ctx, HEADER_STYLE_CLASS_ATTR_NAME) ) {
			UIComponentTagUtils.setStringProperty(ctx.getFacesContext(), table, HEADER_STYLE_CLASS_ATTR_NAME, HEADER_STYLE_CLASS );
		}
		if (! hasValue(ctx, FOOTER_STYLE_CLASS_ATTR_NAME) ) {
			UIComponentTagUtils.setStringProperty(ctx.getFacesContext(), table, FOOTER_STYLE_CLASS_ATTR_NAME, FOOTER_STYLE_CLASS );
		}
		if (! hasValue(ctx, ROW_CLASSES_ATTR_NAME) ) {
			UIComponentTagUtils.setStringProperty(ctx.getFacesContext(), table, ROW_CLASSES_ATTR_NAME, ROW_STYLE_CLASSES );
		}
	}

}