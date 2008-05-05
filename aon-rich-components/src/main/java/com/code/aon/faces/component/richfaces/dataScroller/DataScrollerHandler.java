package com.code.aon.faces.component.richfaces.dataScroller;

import javax.el.ValueExpression;
import javax.el.VariableMapper;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;

import com.code.aon.faces.component.util.FaceletUtil;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.el.VariableMapperWrapper;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagHandler;
import com.sun.facelets.tag.jsf.ComponentSupport;

/**
 * The Class TabbedPaneComponentHandler.
 * 
 * @author atellitu
 */
public class DataScrollerHandler extends TagHandler {

	private static final String TEMPLATE_PATH = "com/code/aon/faces/component/richfaces/dataScroller/";

	private static final String TEMPLATE = TEMPLATE_PATH + "dataScroller.xhtml";
	
	private static final String DATA_TABLE = "dataTable";
	
	private static final String MODEL = "model";
	
	private static final String PAGE_SIZE = "pageSize";
	
   	private static final String SHOW_NOTE = "showNote";
   	
   	private static final String ROW_COUNT = "rowCount";
   	
   	private static final String PAGE = "page";
   	
	private TagAttribute dataTable;
	
   	private TagAttribute model;	

	/**
	 * The Constructor.
	 * 
	 * @param config
	 *            the config
	 */
	public DataScrollerHandler(TagConfig config) {
		super(config);
		dataTable = getRequiredAttribute(DATA_TABLE);
		model = getRequiredAttribute(MODEL);
	}

	private UIData getDataTable( FaceletContext ctx, UIComponent parent ) {
		return (UIData) ComponentSupport.findChild( parent, dataTable.getValue(ctx) );
	}
	
	private int getPageSize( FaceletContext ctx, UIComponent parent ) {
		int rows = 20;
		UIData table = getDataTable(ctx, parent);
		if ( table != null ) {
			rows = table.getRows();
		}
		return rows;
	}

	private int getRowCount( FaceletContext ctx, UIComponent parent ) {
		int rowCount = 0;
		UIData table = getDataTable(ctx, parent);
		if ( table != null ) {
			rowCount = table.getRowCount();
		}
		return rowCount;
	}
	
	private void insertTemplate(FaceletContext ctx, UIComponent component) {
		VariableMapper newMapper = new VariableMapperWrapper(ctx.getVariableMapper());
		ValueExpression modelExpression = model.getValueExpression(ctx, Object.class);
		newMapper.setVariable(MODEL, modelExpression);
		newMapper.setVariable(DATA_TABLE, dataTable.getValueExpression(ctx, String.class));		
		ValueExpression showNote = FaceletUtil.getBooleanValueExpression(ctx, getAttribute(SHOW_NOTE));
		newMapper.setVariable(SHOW_NOTE, showNote);
		TagAttribute page = getAttribute(PAGE);
		if (page != null) {
			newMapper.setVariable(PAGE, page.getValueExpression(ctx, Object.class));
		}
		TagAttribute pageSize = getAttribute(PAGE_SIZE);
		if (pageSize != null) {
			newMapper.setVariable(PAGE_SIZE, pageSize.getValueExpression(ctx, Integer.class));
		} else {
			int rows = getPageSize(ctx, component);
			newMapper.setVariable(PAGE_SIZE, ctx.getExpressionFactory()
					.createValueExpression(ctx, String.valueOf(rows), Integer.class));
		}
		TagAttribute rowCount = getAttribute(ROW_COUNT);
		if (rowCount != null) {
			newMapper.setVariable(ROW_COUNT, rowCount.getValueExpression(ctx, Integer.class));
		} else {
			int count = getRowCount(ctx, component);
			newMapper.setVariable(ROW_COUNT, ctx.getExpressionFactory()
					.createValueExpression(ctx, String.valueOf(count), Integer.class));
		}
		
		FaceletUtil.insertTemplate(ctx, tag, component, FaceletUtil.getTemplate(TEMPLATE), newMapper);
	}

	@Override
	public void apply(FaceletContext ctx, UIComponent parent) {
		insertTemplate( ctx, parent );
	}

}