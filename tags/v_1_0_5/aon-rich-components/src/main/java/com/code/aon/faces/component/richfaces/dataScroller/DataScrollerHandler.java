package com.code.aon.faces.component.richfaces.dataScroller;

import javax.el.ValueExpression;
import javax.el.VariableMapper;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UIViewRoot;

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

	private int getPageSize( FaceletContext ctx, UIComponent parent ) {
		int rows = 20;
		UIData table = (UIData) ComponentSupport.findChild( parent, dataTable.getValue(ctx) );
		if ( table != null ) {
			rows = table.getRows();
		}
		return rows;
	}
	
	private void insertTemplate(FaceletContext ctx, UIComponent component) {
		VariableMapper newMapper = new VariableMapperWrapper(ctx.getVariableMapper());
		newMapper.setVariable(MODEL, model.getValueExpression(ctx, Object.class));
		newMapper.setVariable(DATA_TABLE, dataTable.getValueExpression(ctx, String.class));		
		ValueExpression showNote = FaceletUtil.getBooleanValueExpression(ctx, getAttribute(SHOW_NOTE));
		newMapper.setVariable(SHOW_NOTE, showNote);
		TagAttribute pageSize = getAttribute(PAGE_SIZE);
		if (pageSize != null) {
			newMapper.setVariable(PAGE_SIZE, pageSize.getValueExpression(ctx, Integer.class));
		} else {
			int rows = getPageSize(ctx, component);
			newMapper.setVariable(PAGE_SIZE, ctx.getExpressionFactory()
					.createValueExpression(ctx, String.valueOf(rows), Integer.class));
		}
		
		FaceletUtil.insertTemplate(ctx, tag, component, FaceletUtil.getTemplate(TEMPLATE), newMapper);
	}

	@Override
	public void apply(FaceletContext ctx, UIComponent parent) {
		insertTemplate( ctx, parent );
	}

}