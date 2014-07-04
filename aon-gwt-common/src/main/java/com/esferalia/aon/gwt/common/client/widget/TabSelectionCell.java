package com.esferalia.aon.gwt.common.client.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class TabSelectionCell extends SelectionCell {

	interface SelectTemplate extends SafeHtmlTemplates {
		@Template("<select style=\"{0}\">")
		SafeHtml renderStart(SafeStyles style);
		@Template("</select>")
		SafeHtml renderEnd();
	}

	interface OptionTemplate extends SafeHtmlTemplates {
		@Template("<option value=\"{0}\">{0}</option>")
		SafeHtml deselected(String option);

		@Template("<option value=\"{0}\" selected=\"selected\">{0}</option>")
		SafeHtml selected(String option);
	}
	private static final int DEFAULT_WIDTH = 150;
	private static SelectTemplate selectTemplate;
	private static OptionTemplate optionTemplate;

	private HashMap<String, Integer> indexForOption = new HashMap<String, Integer>();

	private final List<String> options;
	private int width;

	public TabSelectionCell(List<String> options,int width) {
		super(options);
		
		this.width = width;
		if (selectTemplate == null) {
			selectTemplate = GWT.create(SelectTemplate.class);
		}
		if (optionTemplate == null) {
			optionTemplate = GWT.create(OptionTemplate.class);
		}
		this.options = new ArrayList<String>(options);
		int index = 0;
		for (String option : options) {
			indexForOption.put(option, index++);
		}
	}
	
	public TabSelectionCell(List<String> options) {
		this(options, DEFAULT_WIDTH);
	}

	@Override
	public void render(Context context, String value, SafeHtmlBuilder sb) {
		// Get the view data.
		Object key = context.getKey();
		String viewData = getViewData(key);
		if (viewData != null && viewData.equals(value)) {
			clearViewData(key);
			viewData = null;
		}

		int selectedIndex = getSelectedIndex(viewData == null ? value
				: viewData);
		sb.append(selectTemplate.renderStart(SafeStylesUtils.forWidth(width, Unit.PX)) );
		int index = 0;
		for (String option : options) {
			if (index++ == selectedIndex) {
				sb.append(optionTemplate.selected(option));
			} else {
				sb.append(optionTemplate.deselected(option));
			}
		}
		sb.append(selectTemplate.renderEnd() );
	}

	private int getSelectedIndex(String value) {
		Integer index = indexForOption.get(value);
		if (index == null) {
			return -1;
		}
		return index.intValue();
	}
}
