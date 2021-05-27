package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.esferalia.aon.gwt.common.client.widget.ComboBox;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.view.client.ListDataProvider;

public class EnumComboBox<T> extends ComboBox<T> {

	private static class DefFormat<T> implements Format<T> {

		@Override
		public String format(T t) {
			return t == null ? "" : t.toString();
		}
	}

	public static interface Filter<T> {
		public boolean accept(T t);
	}

	private static class BlankFilter<T> implements Filter<T> {
		public boolean accept(T t) {
			return t != null && !StringUtils.isBlank(t.toString());
		}
	}
	
	private int maxLength = Integer.MAX_VALUE;

	public EnumComboBox(Class<T> type) {
		this(type, new BlankFilter<T>());
	}

	public EnumComboBox(Class<T> type, Filter<T> filter) {
		this(getEnumConstants(type, filter), new DefFormat<T>());
	}

	public EnumComboBox(Class<T> type, Format<T> format) {
		this(type.getEnumConstants(), format);
	}

	public EnumComboBox(T values[], Format<T> format) {
		this(Arrays.asList(values), format);
	}

	public EnumComboBox(List<T> values, Format<T> format) {
		super(format);
		new ListDataProvider<T>(values).addDataDisplay(this);
	}

	public void setSelected(T t) {
		super.setSelected(t, true);
	}
	
	
	@Override
	protected void setOptionText(OptionElement option, String text,
			Direction dir) {
		super.setOptionText(option, text.substring(0, Math.min(maxLength, text.length() )), dir);
	}
	
	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}
	
	// ------------------------------------------------------------------------
	
	@Override
	protected void onResizeDropDownList(int dropDownListWidth) {
	}
	
	// ------------------------------------------------------------------------

	private static <T> List<T> getEnumConstants(Class<T> type, Filter<T> filter) {
		return filterList(filter, type.getEnumConstants());
	}

	private static <T> List<T> filterList(Filter<T> filter, T... ts) {
		ArrayList<T> filteredList = new ArrayList<T>(ts.length);
		for (T t : ts)
			if (filter.accept(t))
				filteredList.add(t);
		return filteredList;
	}

}
