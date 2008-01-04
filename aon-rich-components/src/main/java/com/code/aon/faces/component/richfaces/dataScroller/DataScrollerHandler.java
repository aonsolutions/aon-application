package com.code.aon.faces.component.richfaces.dataScroller;

import javax.el.ValueExpression;
import javax.el.VariableMapper;
import javax.faces.component.UIComponent;

import com.code.aon.faces.component.util.FaceletUtil;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.el.VariableMapperWrapper;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagHandler;

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

	private void insertTemplate(FaceletContext ctx, UIComponent component) {
		VariableMapper newMapper = new VariableMapperWrapper(ctx.getVariableMapper());
		newMapper.setVariable(MODEL, model.getValueExpression(ctx, Object.class));
		newMapper.setVariable(DATA_TABLE, dataTable.getValueExpression(ctx, String.class));		
		ValueExpression showNote = FaceletUtil.getBooleanValueExpression(ctx, getAttribute(SHOW_NOTE));
		newMapper.setVariable(SHOW_NOTE, showNote);
		FaceletUtil.insertTemplate(ctx, tag, component, FaceletUtil.getTemplate(TEMPLATE), newMapper);
	}

	@Override
	public void apply(FaceletContext ctx, UIComponent parent) {
		insertTemplate( ctx, parent );
	}

}