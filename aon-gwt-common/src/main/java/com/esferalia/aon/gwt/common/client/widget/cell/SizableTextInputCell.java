package com.esferalia.aon.gwt.common.client.widget.cell;

import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class SizableTextInputCell extends TextInputCell {

	interface CustomInputTemplate extends SafeHtmlTemplates {
		@Template("<input type=\"text\" value=\"{0}\" size=\"{1}\"></input>")
		SafeHtml input(String value, int size);
	}
	
	private static CustomInputTemplate template;
	private int size;

	public SizableTextInputCell(int size) {
		super();
		template = GWT.create(CustomInputTemplate.class);
		this.size = size;
	}

	@Override
	public void render(Context context, String value, SafeHtmlBuilder sb) {
		Object key = context.getKey();
		ViewData viewData = getViewData(key);
		if (viewData != null && viewData.getCurrentValue().equals(value)) {
			clearViewData(key);
			viewData = null;
		}
		String s = (viewData != null) ? viewData.getCurrentValue() : value;
		if (s==null) s = "";
		sb.append(template.input(s,size));
	}
}		
