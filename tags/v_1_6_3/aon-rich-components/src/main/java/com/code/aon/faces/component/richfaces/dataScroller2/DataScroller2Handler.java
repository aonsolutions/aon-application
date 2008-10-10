package com.code.aon.faces.component.richfaces.dataScroller2;

import java.util.Map;

import javax.el.ValueExpression;
import javax.el.VariableMapper;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UIViewRoot;
import javax.faces.model.DataModel;

import com.code.aon.faces.component.myfaces.UIComponentTagUtils;
import com.code.aon.faces.component.richfaces.form.FormHandler;
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
public class DataScroller2Handler extends TagHandler {

	private static final String TEMPLATE_PATH = "com/code/aon/faces/component/richfaces/dataScroller2/";

	private static final String TEMPLATE = TEMPLATE_PATH + "dataScroller2.xhtml";
	
	private static final String RENDERED = "rendered";
	
	private static final String DATA_TABLE = "dataTable";
	
	private static final String FOR = "for";

	private static final String FIRST = "first";
	
   	private static final String SHOW_NOTE = "showNote";
   	
   	private static final String MODEL = "model";
   	
   	private static final String PAGE = "page";
   	
	private TagAttribute forTag;

	/**
	 * The Constructor.
	 * 
	 * @param config
	 *            the config
	 */
	public DataScroller2Handler(TagConfig config) {
		super(config);
		forTag = getRequiredAttribute(FOR);
	}
	
	@SuppressWarnings("unchecked")
	private UIData getDataTable( FaceletContext ctx, UIComponent parent ) {
		String id = forTag.getValue(ctx);
		UIData table = (UIData) ComponentSupport.findChild( parent, id );
		if ( table == null ) {
			UIViewRoot root = ComponentSupport.getViewRoot(ctx, parent);
			Map<String,UIData> dataTableMap = (Map<String, UIData>) root.getAttributes().get( FormHandler.CURRENT_FORM_DATA_TABLE_MAP );
			table = dataTableMap.get( id );
		}
		return table;
	}
	
	private int getPageSize( FaceletContext ctx, UIData table ) {
		int rows = 0;
		if ( table != null ) {
			rows = table.getRows();
		}
		return rows;
	}
	
	private String getScrollerModelId() {
		String id = forTag.getValue() + "Model";
		return id;
	}
	
	private String getScrollerDataExpression() {
		return "view.attributes['" + getScrollerModelId() +  "']";
	}
	
	private DataModel getDataModel( FaceletContext ctx, UIData table ) {
		DataModel model = (DataModel) table.getValue();
		if ( model == null ) {
			TagAttribute modelTag = getAttribute(MODEL);
			model = (DataModel) modelTag.getObject(ctx, DataModel.class);
		}
		return model;
	}
	
	private ValueExpression getScrollerModelExpression( FaceletContext ctx, UIData table ) {
		UIViewRoot root = ComponentSupport.getViewRoot(ctx, table);
		String scrollerId = getScrollerModelId();
		DataModel model = getDataModel(ctx, table);		
		ScrollerDataModel scrollerModel = (ScrollerDataModel) root.getAttributes().get(scrollerId);
		if ( scrollerModel == null ) {
			scrollerModel = new ScrollerDataModel( model );
			root.getAttributes().put( scrollerId, scrollerModel );
		} else {
			scrollerModel.setModel( model );
		}
		scrollerModel.setPageSize( getPageSize(ctx, table) );
		String expression = "#{" + getScrollerDataExpression()+  "}";
		return ctx.getExpressionFactory().createValueExpression( ctx, expression, Object.class);
	}
	
	private void insertTemplate(FaceletContext ctx, UIComponent component, UIData table ) {
		VariableMapper newMapper = new VariableMapperWrapper(ctx.getVariableMapper());
		newMapper.setVariable(DATA_TABLE, forTag.getValueExpression(ctx, String.class));
		newMapper.setVariable( MODEL, getScrollerModelExpression(ctx, table) );	
		ValueExpression showNote = FaceletUtil.getBooleanValueExpression(ctx, getAttribute(SHOW_NOTE));
		newMapper.setVariable(SHOW_NOTE, showNote);
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
	
	private void updateDataTableFirst( FaceletContext ctx, UIComponent table ) {
		if ( table != null ) {
			String expression = "#{" + getScrollerDataExpression()+  ".first}";
			UIComponentTagUtils.setStringProperty( ctx.getFacesContext(), table, FIRST, expression );
		}		
	}
	
	@Override
	public void apply(FaceletContext ctx, UIComponent parent) {
		if ( isRendered(ctx) && parent.isRendered() ) {
			UIData table = getDataTable(ctx, parent);
			insertTemplate( ctx, parent, table );
			updateDataTableFirst( ctx, table );
		}
	}

}