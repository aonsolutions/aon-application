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
	
	private static final String RENDERED = "rendered";
	
	private static final String DATA_TABLE = "dataTable";
	
	private static final String FOR = "for";
	
	private static final String PAGE_SIZE = "pageSize";
	
   	private static final String SHOW_NOTE = "showNote";
   	
   	private static final String ROW_COUNT = "rowCount";
   	
   	private static final String PAGE = "page";
   	
	private TagAttribute forTag;
	
   	private TagAttribute rowCount;	

	/**
	 * The Constructor.
	 * 
	 * @param config
	 *            the config
	 */
	public DataScrollerHandler(TagConfig config) {
		super(config);
		forTag = getRequiredAttribute(FOR);
		rowCount = getRequiredAttribute(ROW_COUNT);
	}

	private UIData getDataTable( FaceletContext ctx, UIComponent parent ) {
		return (UIData) ComponentSupport.findChild( parent, forTag.getValue(ctx) );
	}
	
	private int getPageSize( FaceletContext ctx, UIComponent parent ) {
		int rows = 20;
		UIData table = getDataTable(ctx, parent);
		if ( table != null ) {
			rows = table.getRows();
		}
		return rows;
	}
	
	private void insertTemplate(FaceletContext ctx, UIComponent component) {
		VariableMapper newMapper = new VariableMapperWrapper(ctx.getVariableMapper());
		newMapper.setVariable(DATA_TABLE, forTag.getValueExpression(ctx, String.class));		
		newMapper.setVariable(ROW_COUNT, rowCount.getValueExpression(ctx, Integer.class));
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
		FaceletUtil.insertTemplate(ctx, tag, component, FaceletUtil.getTemplate(TEMPLATE), newMapper);
	}

	private boolean isRendered( FaceletContext ctx ) {
		boolean rendered = true;
		TagAttribute renderedTag = getAttribute(RENDERED);
		if ( renderedTag != null ) {
			rendered = renderedTag.getBoolean(ctx);
		}
		return rendered;
	}
	
	@Override
	public void apply(FaceletContext ctx, UIComponent parent) {
		if ( isRendered(ctx) ) {
			insertTemplate( ctx, parent );
		}
	}

}