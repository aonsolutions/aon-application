package com.code.aon.faces.component.richfaces.rowSelector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;

import org.ajax4jsf.taglib.html.facelets.AjaxSupportHandler;
import org.richfaces.component.html.HtmlDataTable;

import com.code.aon.faces.component.AttributeInfo;
import com.code.aon.faces.component.richfaces.IRichFacesTags;
import com.code.aon.faces.component.richfaces.editDataTable.EditDataTableHandler;
import com.code.aon.faces.component.util.AonComponentConfig;
import com.code.aon.faces.component.util.FaceletUtil;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagException;
import com.sun.facelets.tag.TagHandler;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentSupport;

/**
 * The Class ConfirmButtonHandler.
 * 
 * @author atellitu
 */
public class RowSelectorHandler extends TagHandler implements IRichFacesTags {

	private static final String MOUSE_OVER_CLASS_ATTRIBUTE = "mouseOverClass";
	
	private static final String MOUSE_OVER_CLASS_VALUE = "aon-table-row-hover";	
	
	private static final String SELECTED_CLASS_ATTRIBUTE = "selectedClass";
	
	private static final String SELECTED_CLASS_VALUE = "aon-table-row-selected";
	
	private static final String ON_SUBMIT_ATTRIBUTE = "onsubmit";
	
	private static final String ON_SUBMIT_VALUE = "if (isRowSelectorDisabled()){return true}";
	
	private ComponentConfig config;
	
	/**
	 * The Constructor.
	 * 
	 * @param config
	 *            the config
	 */
	public RowSelectorHandler(ComponentConfig config) {
		super( config );
		this.config = config;
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
	
	private TagHandler getSupportHandler(FaceletContext ctx, UIComponent component) {
		List<AttributeInfo> attributes = new ArrayList<AttributeInfo>();
		UIViewRoot root = ComponentSupport.getViewRoot(ctx, component);
		String id = (String) root.getAttributes().get( EditDataTableHandler.EDIT_DATA_TABLE_ID );
		if ( id != null ) {
			String value = FaceletUtil.updateList(ctx, getAttribute(RERENDER), id);
			AttributeInfo reRender = new AttributeInfo(RERENDER, value);
			reRender.setForce(true);
			attributes.add(reRender);
		}
		AttributeInfo onSubmit = new AttributeInfo(ON_SUBMIT_ATTRIBUTE, ON_SUBMIT_VALUE);
		attributes.add(onSubmit);
		AonComponentConfig aonConfig = new AonComponentConfig(config, attributes); 
		return new AjaxSupportHandler(aonConfig);
	}
	
	@Override
	public void apply(FaceletContext ctx, UIComponent parent) throws IOException {
		if (parent instanceof HtmlDataTable) {
			HtmlDataTable dataTable = (HtmlDataTable) parent;
			if (ComponentSupport.isNew(parent)) {
				addRowClasses( ctx, dataTable );
			}
		} else {
			throw new TagException(this.tag,
					"Parent is not of type org.richfaces.component.html.HtmlDataTable, type is: " + parent);
		}
		getSupportHandler(ctx, parent).apply(ctx, parent);
	}

}