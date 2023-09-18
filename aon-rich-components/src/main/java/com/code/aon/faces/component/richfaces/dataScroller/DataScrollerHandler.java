package com.code.aon.faces.component.richfaces.dataScroller;

import static com.code.aon.faces.component.richfaces.IRichFacesTags.HIDE_PAGE_SIZE_SELECTOR;
import static com.code.aon.faces.component.richfaces.IRichFacesTags.ON_COMPLETE;
import static com.code.aon.faces.component.richfaces.IRichFacesTags.PAGE;

import jakarta.el.ValueExpression;
import jakarta.el.VariableMapper;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UIViewRoot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.faces.component.param.ParamHandler;
import com.code.aon.faces.component.richfaces.IRichFacesTags;
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
public class DataScrollerHandler extends TagHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ParamHandler.class);

	private static final String TEMPLATE_PATH = "com/code/aon/faces/component/richfaces/dataScroller/";

	private static final String TEMPLATE = TEMPLATE_PATH + "dataScroller.xhtml";
	
	private static final String DATA_TABLE = "dataTable";
	
	private static final String FOR = "for";
	
	private static final String PAGE_SIZE = "pageSize";
	
   	private static final String SHOW_NOTE = "showNote";
   	
   	private static final String ROW_COUNT = "rowCount";
   	
	private TagAttribute forTag;

	/**
	 * The Constructor.
	 * 
	 * @param config
	 *            the config
	 */
	public DataScrollerHandler(TagConfig config) {
		super(config);
		forTag = getRequiredAttribute(FOR);
	}

	public static UIData getDataTable( FaceletContext ctx, UIComponent parent, TagAttribute forTag ) {
		String id = forTag.getValue(ctx);
		UIData table = (UIData) ComponentSupport.findChild( parent, id );
		if ( table == null ) {
			UIViewRoot root = ComponentSupport.getViewRoot(ctx, parent);
			table = FormHandler.getDataTableMap(root).get( id );
		}
		return table;
	}
		
	private ValueExpression getPageSize( FaceletContext ctx, UIComponent parent ) {
		ValueExpression rowsVE = null;
		UIData table = getDataTable(ctx, parent, forTag);
		if ( table != null ) {
			rowsVE = table.getValueExpression(IRichFacesTags.ROWS);
			if ( rowsVE == null ) {
				int rows = Math.max(table.getRows(), 0);
				rowsVE = FaceletUtil.getValueExpression(ctx, "#{"+rows+"}", Integer.class);	
			}
		}
		return rowsVE;
	}

	private ValueExpression getRowCount( FaceletContext ctx, UIComponent parent ) {
		ValueExpression rowsCountVE = null;
		TagAttribute rowCountTag = getAttribute(ROW_COUNT);
		if ( rowCountTag != null ) {
			rowsCountVE = rowCountTag.getValueExpression(ctx, Integer.class);	
		}
		if ( rowsCountVE == null ) {
			UIData table = getDataTable(ctx, parent, forTag);
			if ( table != null ) {
				ValueExpression modelVE = table.getValueExpression(IRichFacesTags.VALUE);
				if ( modelVE != null ) {
					String modelExpression = modelVE.getExpressionString();
					String rowCountExpression = FaceletUtil.appendExpression( modelExpression, IRichFacesTags.ROW_COUNT);
					rowsCountVE = FaceletUtil.getValueExpression(ctx, rowCountExpression, Integer.class);
				}
			}			
		}
		return rowsCountVE;
	}
	
	private void insertTemplate(FaceletContext ctx, UIComponent component) {
		VariableMapper newMapper = new VariableMapperWrapper(ctx.getVariableMapper());
		newMapper.setVariable(DATA_TABLE, forTag.getValueExpression(ctx, String.class));		
		newMapper.setVariable(ROW_COUNT, getRowCount(ctx, component));
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