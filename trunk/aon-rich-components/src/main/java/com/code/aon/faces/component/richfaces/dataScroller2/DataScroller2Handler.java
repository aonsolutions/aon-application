package com.code.aon.faces.component.richfaces.dataScroller2;

import javax.el.ValueExpression;
import javax.el.VariableMapper;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UIViewRoot;
import javax.faces.model.DataModel;

import com.code.aon.faces.component.myfaces.UIComponentTagUtils;
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
	
   	private TagAttribute modelTag;	

	/**
	 * The Constructor.
	 * 
	 * @param config
	 *            the config
	 */
	public DataScroller2Handler(TagConfig config) {
		super(config);
		forTag = getRequiredAttribute(FOR);
		modelTag = getRequiredAttribute(MODEL);
	}
	
	private UIData getDataTable( FaceletContext ctx, UIComponent parent ) {
		return (UIData) ComponentSupport.findChild( parent, forTag.getValue(ctx) );
	}
	
	private int getPageSize( FaceletContext ctx, UIComponent parent ) {
		int rows = 0;
		UIData table = getDataTable(ctx, parent);
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
	
	private ValueExpression getScrollerModelExpression( FaceletContext ctx, UIComponent component ) {
		UIViewRoot root = ComponentSupport.getViewRoot(ctx, component);
		String scrollerId = getScrollerModelId();
		ValueExpression model = modelTag.getValueExpression(ctx, DataModel.class);		
		ScrollerDataModel scrollerModel = (ScrollerDataModel) root.getAttributes().get(scrollerId);
		if ( scrollerModel == null ) {
			scrollerModel = new ScrollerDataModel( model );
			root.getAttributes().put( scrollerId, scrollerModel );
		} else {
			scrollerModel.setModel( model );
		}
		scrollerModel.setPageSize( getPageSize(ctx, component) );
		String expression = "#{" + getScrollerDataExpression()+  "}";
		return ctx.getExpressionFactory().createValueExpression( ctx, expression, Object.class);
	}
	
	private void insertTemplate(FaceletContext ctx, UIComponent component) {
		VariableMapper newMapper = new VariableMapperWrapper(ctx.getVariableMapper());
		newMapper.setVariable(DATA_TABLE, forTag.getValueExpression(ctx, String.class));
		newMapper.setVariable( MODEL, getScrollerModelExpression(ctx, component) );	
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
	
	private void updateDataTableFirst( FaceletContext ctx, UIComponent parent ) {
		UIData table = getDataTable(ctx, parent);
		if ( table != null ) {
			String expression = "#{" + getScrollerDataExpression()+  ".first}";
			UIComponentTagUtils.setStringProperty( ctx.getFacesContext(), table, FIRST, expression );
		}		
	}
	
	@Override
	public void apply(FaceletContext ctx, UIComponent parent) {
		if ( isRendered(ctx) ) {
			insertTemplate( ctx, parent );
			updateDataTableFirst( ctx, parent );
		}
	}

}