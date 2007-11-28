package com.code.aon.faces.component.richfaces.rowSelector;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.el.VariableMapper;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import org.richfaces.component.html.HtmlDataTable;

import com.code.aon.faces.component.util.FaceletUtil;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.el.VariableMapperWrapper;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagException;
import com.sun.facelets.tag.TagHandler;
import com.sun.facelets.tag.jsf.ComponentSupport;

/**
 * The Class ConfirmButtonHandler.
 * 
 * @author atellitu
 */
public class CopyOfRowSelectorHandler extends TagHandler {

	private static final String TEMPLATE_INSERTED = "com.code.aon.faces.RowSelector.templateInserted";

	private static final String TOGGLE_ON_CLICK_ATTRIBUTE = "toggleOnClick";

	private static final String ACTION = "action";

	private static final String ACTION_LISTENER = "actionListener";
	
	private static final String MOUSE_OVER_CLASS_ATTRIBUTE = "mouseOverClass";
	
	private static final String MOUSE_OVER_CLASS_VALUE = "aon-table-row-hover";	
	
	private static final String SELECTED_CLASS_ATTRIBUTE = "selectedClass";
	
	private static final String SELECTED_CLASS_VALUE = "aon-table-row-selected";	

	private static final String TEMPLATE = "template.xhtml";

	private static final String ROW_SELECTOR_ID = "rowSelectorId";
	
	private static final String RENDERED = "rowSelectorRendered";
	
	private static final String RE_RENDER = "reRender";
	
	/**
	 * The Constructor.
	 * 
	 * @param config
	 *            the config
	 */
	public CopyOfRowSelectorHandler(TagConfig config) {
		super(config);
	}

	private String getValue(FaceletContext ctx, String name, String _default ) {
		String value = _default;
		TagAttribute tag = getAttribute(name);
		if (tag != null) {
			value = tag.getValue(ctx);
		}
		return value;
	}

	private void addRowClasses(FaceletContext ctx, HtmlDataTable dataTable) {
		String selectedClass = getValue(ctx, SELECTED_CLASS_ATTRIBUTE, SELECTED_CLASS_VALUE);
		StringBuffer mouseOut = new StringBuffer();
		mouseOut.append("onDataTableOut(this,'").append(selectedClass)
				.append("');");
		if (dataTable.getOnRowMouseOut() != null) {
			mouseOut.append(dataTable.getOnRowMouseOut());
		}
		dataTable.setOnRowMouseOut(mouseOut.toString());

		String mouseOverClass = getValue(ctx, MOUSE_OVER_CLASS_ATTRIBUTE, MOUSE_OVER_CLASS_VALUE);
		StringBuffer mouseOver = new StringBuffer();
		mouseOver.append("onDataTableOver(this,'").append(selectedClass)
				.append("','");
		mouseOver.append(mouseOverClass).append("');");
		if (dataTable.getOnRowMouseOver() != null) {
			mouseOver.append(dataTable.getOnRowMouseOver());
		}
		dataTable.setOnRowMouseOver(mouseOver.toString());
	}
	
    private String getId(FaceletContext ctx) {
    	TagAttribute tag = getAttribute("id");
        if (tag != null) {
            return tag.getValue(ctx);
        }
        return ctx.generateUniqueId( "rowSelector" + tagId );
    }
	
	private VariableMapper getVariableMapper( FaceletContext ctx ) {
		VariableMapper newMapper = new VariableMapperWrapper(ctx.getVariableMapper());
		
		// ValueExpression id = ctx.getExpressionFactory().createValueExpression(ctx, getId(ctx), String.class);
		// newMapper.setVariable(ROW_SELECTOR_ID, id);
		newMapper.setVariable(RENDERED, FaceletUtil.getBooleanValueExpression(ctx, getAttribute(TOGGLE_ON_CLICK_ATTRIBUTE)) );
		newMapper.setVariable(RE_RENDER, FaceletUtil.getStringValueExpression(ctx, getAttribute(RE_RENDER)) );
		ValueExpression action = FaceletUtil.getMethodExpression(ctx, getAttribute(ACTION), String.class, FaceletUtil.ACTION_SIG);
		if ( action == null ) {
			action = FaceletUtil.getMethodEmptyExpression(ctx, ACTION, String.class, FaceletUtil.ACTION_SIG);
		}
		newMapper.setVariable( ACTION, action );
		ValueExpression al = FaceletUtil.getMethodExpression(ctx, getAttribute(ACTION_LISTENER), null, FaceletUtil.ACTION_LISTENER_SIG);
		if ( al != null ) {
			newMapper.setVariable( ACTION_LISTENER, al );				
		}
		return newMapper;
	}

	private void insertTemplate(FaceletContext ctx, UIComponent parent) {
		Map<String, Object> map = ctx.getFacesContext().getExternalContext().getRequestMap();
		if (!map.containsKey(TEMPLATE_INSERTED)) {
			VariableMapper newMapper = getVariableMapper(ctx);
			URL template = CopyOfRowSelectorHandler.class.getResource(TEMPLATE);
			FaceletUtil.insertTemplate(ctx, this.tag, parent, template, newMapper);
			map.put(TEMPLATE_INSERTED, "true");
		}
	}

	@Override
	public void apply(FaceletContext ctx, UIComponent parent)
			throws IOException, FacesException, FaceletException, ELException {
		if (parent instanceof HtmlDataTable) {
			HtmlDataTable dataTable = (HtmlDataTable) parent;
			if (ComponentSupport.isNew(parent)) {
				addRowClasses( ctx, dataTable );
				insertTemplate( ctx, dataTable );
			}
		} else {
			throw new TagException(this.tag,
					"Parent is not of type org.richfaces.component.html.HtmlDataTable, type is: " + parent);
		}
		this.nextHandler.apply(ctx, parent);		
	}

}