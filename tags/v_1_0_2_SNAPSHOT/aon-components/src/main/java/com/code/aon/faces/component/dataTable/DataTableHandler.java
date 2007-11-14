package com.code.aon.faces.component.dataTable;

import com.code.aon.faces.component.AonComponentHandler;
import com.code.aon.faces.component.myfaces.UIComponentTagUtils;
import com.icesoft.faces.component.ext.HtmlDataTable;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.jsf.ComponentConfig;

public class DataTableHandler extends AonComponentHandler {

	private static final String STYLE_CLASS_ATTR_NAME = "styleClass";
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
		HtmlDataTable table = (HtmlDataTable) instance;
		super.setAttributes(ctx, table);
		if (! hasValue(ctx, STYLE_CLASS_ATTR_NAME) ) {
			UIComponentTagUtils.setStringProperty(ctx.getFacesContext(), table, STYLE_CLASS_ATTR_NAME, TABLE_STYLE_CLASS );
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