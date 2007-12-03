package com.code.aon.faces.component.richfaces.rowSelector;

import java.io.IOException;

import javax.faces.component.UIComponent;

import org.ajax4jsf.taglib.html.facelets.AjaxSupportHandler;
import org.richfaces.component.html.HtmlDataTable;

import com.code.aon.faces.component.util.AonComponentConfig;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagException;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentSupport;

/**
 * The Class ConfirmButtonHandler.
 * 
 * @author atellitu
 */
public class RowSelectorHandler extends AjaxSupportHandler {

	private static final String MOUSE_OVER_CLASS_ATTRIBUTE = "mouseOverClass";
	
	private static final String MOUSE_OVER_CLASS_VALUE = "aon-table-row-hover";	
	
	private static final String SELECTED_CLASS_ATTRIBUTE = "selectedClass";
	
	private static final String SELECTED_CLASS_VALUE = "aon-table-row-selected";	
	
	private ComponentConfig originalConfig;
	
	/**
	 * The Constructor.
	 * 
	 * @param config
	 *            the config
	 */
	public RowSelectorHandler(ComponentConfig config) {
		super( new AonComponentConfig(config) );
		this.originalConfig = config;
	}

	private String getValue(FaceletContext ctx, String name, String _default ) {
		String value = _default;
		TagAttribute tag = originalConfig.getTag().getAttributes().get(name);
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
		super.apply(ctx, parent);
	}

}