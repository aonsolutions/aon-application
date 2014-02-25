package com.code.aon.faces.component.richfaces.dataScroller;

import static com.code.aon.faces.component.richfaces.IRichFacesTags.HIDE_PAGE_SIZE_SELECTOR;
import static com.code.aon.faces.component.richfaces.IRichFacesTags.ON_COMPLETE;
import static com.code.aon.faces.component.richfaces.IRichFacesTags.PAGE;

import javax.el.ValueExpression;
import javax.el.VariableMapper;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.faces.component.param.ParamHandler;
import com.code.aon.faces.component.richfaces.IRichFacesTags;
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
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ParamHandler.class);

	private static final String TEMPLATE_PATH = "com/code/aon/faces/component/richfaces/dataScroller/";

	private static final String TEMPLATE = TEMPLATE_PATH + "dataScroller.xhtml";
	
	private static final String DATA_TABLE = "dataTable";
	
	private static final String FOR = "for";
	
	private static final String PAGE_SIZE = "pageSize";
	
   	private static final String SHOW_NOTE = "showNote";
   	
   	private static final String ROW_COUNT = "rowCount";
   	
   	private static final String ACTION = "action";
   	
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
	
	private ValueExpression getPageSize( FaceletContext ctx, UIComponent parent ) {
		UIData table = getDataTable(ctx, parent);
		if ( table != null ) {
			return table.getValueExpression(IRichFacesTags.ROWS);
		}
		return null;
	}
	
	private ValueExpression getMethodExpression(FaceletContext ctx, String name,
			Class type, Class[] paramTypes) {
		return FaceletUtil.getMethodExpression(ctx, getAttribute(name), type,
				paramTypes);
	}
	
	private void insertTemplate(FaceletContext ctx, UIComponent component) {
		VariableMapper newMapper = new VariableMapperWrapper(ctx.getVariableMapper());
		newMapper.setVariable(DATA_TABLE, forTag.getValueExpression(ctx, String.class));		
		newMapper.setVariable(ROW_COUNT, rowCount.getValueExpression(ctx, Integer.class));
		ValueExpression showNote = FaceletUtil.getBooleanValueExpression(ctx, getAttribute(SHOW_NOTE));
		newMapper.setVariable(SHOW_NOTE, showNote);
		TagAttribute pageTag = getAttribute(PAGE);
		if (pageTag != null) {
			ValueExpression pageVE = pageTag.getValueExpression(ctx, Integer.class);
			try {
				if (! pageVE.isReadOnly(ctx) ) {
					newMapper.setVariable(PAGE, pageVE);	
				}				
			} catch (Throwable th) {
				LOGGER.debug( th.getMessage(), th );
			}
		}
		boolean forceHide = true;
		TagAttribute pageSize = getAttribute(PAGE_SIZE);
		if (pageSize != null) {
			ValueExpression pageSizeVE = pageSize.getValueExpression(ctx, Integer.class);
			forceHide = pageSizeVE.isReadOnly(ctx);
			newMapper.setVariable(PAGE_SIZE, pageSizeVE);
		} else {
			ValueExpression rowsVE = getPageSize(ctx, component);
			if ( rowsVE != null ) {
				forceHide = rowsVE.isReadOnly(ctx); 
				newMapper.setVariable(PAGE_SIZE, rowsVE);				
			}
		}
		ValueExpression action = getMethodExpression(ctx, ACTION, String.class, FaceletUtil.ACTION_SIG);
		if (action == null) {
			action = FaceletUtil.getMethodEmptyExpression(ctx, ACTION,
					String.class, FaceletUtil.ACTION_SIG);
		}
		newMapper.setVariable(ACTION, action);		
		TagAttribute onCompleteTag = getAttribute(ON_COMPLETE);
		if ( onCompleteTag != null ) {
			newMapper.setVariable(ON_COMPLETE, FaceletUtil.getStringValueExpression(ctx, onCompleteTag));			
		}		
		if ( forceHide ) {
			newMapper.setVariable(HIDE_PAGE_SIZE_SELECTOR, FaceletUtil.getValueExpression(ctx, "#{true}", Boolean.class));
		} else {
			TagAttribute hideTag = getAttribute(HIDE_PAGE_SIZE_SELECTOR);
			if ( hideTag != null ) {
				newMapper.setVariable(HIDE_PAGE_SIZE_SELECTOR, hideTag.getValueExpression(ctx, Boolean.class));
			}				
		}
		FaceletUtil.insertTemplate(ctx, tag, component, FaceletUtil.getTemplate(TEMPLATE), newMapper);
	}
	
	@Override
	public void apply(FaceletContext ctx, UIComponent parent) {
		if ( FaceletUtil.isRendered(ctx, tag) && parent.isRendered() ) {
			insertTemplate( ctx, parent );
		}
	}

}