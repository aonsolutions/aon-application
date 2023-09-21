package com.code.aon.faces.component.richfaces.dataScroller2;

import static com.code.aon.faces.controller.IRichConstants.ATTRIBUTE_PREFFIX;

import jakarta.el.ValueExpression;
import jakarta.el.VariableMapper;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UIViewRoot;
import javax.faces.model.DataModel;

import com.code.aon.faces.component.myfaces.UIComponentTagUtils;
import com.code.aon.faces.component.richfaces.IRichFacesTags;
import com.code.aon.faces.component.richfaces.dataScroller.DataScrollerHandler;
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
public class DataScroller2Handler extends TagHandler implements IRichFacesTags {

	private static final String TEMPLATE_PATH = "com/code/aon/faces/component/richfaces/dataScroller2/";

	private static final String TEMPLATE = TEMPLATE_PATH + "dataScroller2.xhtml";
	
	private static final String DATA_TABLE = "dataTable";
	
	private static final String FOR = "for";

	private static final String FIRST = "first";
	
   	private static final String SHOW_NOTE = "showNote";
   	
   	private static final String MODEL = "model";
   	
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
	
	private String getScrollerModelId( FaceletContext ctx ) {
		String id = ATTRIBUTE_PREFFIX + forTag.getValue(ctx) + "Model";
		return id;
	}
	
	private String getScrollerDataExpression( FaceletContext ctx ) {
		return "view.attributes['" + getScrollerModelId(ctx) +  "']";
	}
	
	private DataModel getDataModel( FaceletContext ctx, UIData table ) {
		DataModel model = (DataModel) table.getValue();
		if ( model == null ) {
			TagAttribute modelTag = getAttribute(MODEL);
			model = (DataModel) modelTag.getObject(ctx, DataModel.class);
		}
		return model;
	}
	
	private ScrollerDataModel getScrollerDataModel( FaceletContext ctx, UIData table ) {
		UIViewRoot root = ComponentSupport.getViewRoot(ctx, table);
		String scrollerId = getScrollerModelId(ctx);
		DataModel model = getDataModel(ctx, table);		
		ScrollerDataModel scrollerModel = (ScrollerDataModel) root.getAttributes().get(scrollerId);
		if ( scrollerModel == null ) {
			scrollerModel = new ScrollerDataModel( model );
			root.getAttributes().put( scrollerId, scrollerModel );
		} else {
			scrollerModel.setModel( model );
		}		
		scrollerModel.setTable( table );		
		TagAttribute hideTag = getAttribute(HIDE_PAGE_SIZE_SELECTOR);
		if ( hideTag != null ) {
			scrollerModel.setHidePageSizeSelector(hideTag.getBoolean(ctx));
		}		
		return scrollerModel;
	}
	
	private ValueExpression getScrollerModelExpression( FaceletContext ctx ) {
		String expression = "#{" + getScrollerDataExpression(ctx)+  "}";
		return ctx.getExpressionFactory().createValueExpression( ctx, expression, Object.class);
	}
	
	private void insertTemplate(FaceletContext ctx, UIComponent component, UIData table ) {
		VariableMapper newMapper = new VariableMapperWrapper(ctx.getVariableMapper());
		newMapper.setVariable(DATA_TABLE, forTag.getValueExpression(ctx, String.class));
		newMapper.setVariable( MODEL, getScrollerModelExpression(ctx) );	
		ValueExpression showNote = FaceletUtil.getBooleanValueExpression(ctx, getAttribute(SHOW_NOTE));
		newMapper.setVariable(SHOW_NOTE, showNote);
		ValueExpression disableHotKeys = FaceletUtil.getBooleanValueExpression(ctx, getAttribute(DISABLE_HOT_KEYS));
		newMapper.setVariable(DISABLE_HOT_KEYS, disableHotKeys);
		TagAttribute onCompleteTag = getAttribute(ON_COMPLETE);
		if ( onCompleteTag != null ) {
			newMapper.setVariable(ON_COMPLETE, FaceletUtil.getStringValueExpression(ctx, onCompleteTag));			
		}
		FaceletUtil.insertTemplate(ctx, tag, component, FaceletUtil.getTemplate(TEMPLATE), newMapper);
	}

	private void updateDataTableFirst( FaceletContext ctx, UIComponent table ) {
		if ( table != null ) {
			String expression = "#{" + getScrollerDataExpression(ctx)+  ".first}";
			UIComponentTagUtils.setStringProperty( ctx.getFacesContext(), table, FIRST, expression );
		}		
	}

	private void updatePage( FaceletContext ctx, ScrollerDataModel scrollerModel ) {
		TagAttribute pageTag = getAttribute(PAGE);
		if ( pageTag != null ) {
			ValueExpression ve = pageTag.getValueExpression(ctx, Integer.class);
			scrollerModel.setPage( ve );
		}
	}
	
	@Override
	public void apply(FaceletContext ctx, UIComponent parent) {
		if ( FaceletUtil.isRendered(ctx, tag) && parent.isRendered() ) {
			UIData table = DataScrollerHandler.getDataTable(ctx, parent, forTag);
			ScrollerDataModel scrollerModel = getScrollerDataModel( ctx, table );
			updatePage(ctx, scrollerModel);
			insertTemplate( ctx, parent, table );
			updateDataTableFirst( ctx, table );
		}
	}

}