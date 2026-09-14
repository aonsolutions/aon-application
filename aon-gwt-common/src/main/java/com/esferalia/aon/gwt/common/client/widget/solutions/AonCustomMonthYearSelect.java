package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;

/**
 * Selector de mes + a\u00f1o que se comporta como un \u00fanico campo de tipo {@link Date}.
 * El valor devuelto siempre es el primer d\u00eda del mes seleccionado, o <code>null</code>
 * si falta el mes o el a\u00f1o.
 */
public class AonCustomMonthYearSelect extends HTMLPanel implements HasValue<Date> {

	private static final String EMPTY_STRING = "";

	private static final String[] MONTHS = { "Ene.", "Feb.", "Mar.", "Abr.", "May.", "Jun.", "Jul.", "Ago.", "Sep.",
			"Oct.", "Nov.", "Dic." };

	private HTMLPanel inputPanel = new HTMLPanel(EMPTY_STRING);
	private ListBox monthListBox;
	private TextBox yearTextBox;

	public AonCustomMonthYearSelect(String title) {
		super(EMPTY_STRING);
		addStyleName(AON.CSS.aonFlexColumn2());
		addStyleName(AON.CSS.aonCustomTextBox());

		if (AonStringUtils.isNotEmpty(title))
			createTitle(title);

		createInput();
	}

	private void createTitle(String title) {
		HTMLPanel titleLabel = new HTMLPanel(title);
		titleLabel.addStyleName(AON.CSS.aonCustomTextBoxTitle());
		add(titleLabel);
	}

	private void createInput() {
		inputPanel.addStyleName(AON.CSS.aonItemFlex());
		inputPanel.setWidth("100%");

		monthListBox = new ListBox();
		monthListBox.setStyleName(AON.CSS.aonCustomTextBoxInput());
		monthListBox.addItem("-", EMPTY_STRING);
		for (int i = 0; i < MONTHS.length; i++)
			monthListBox.addItem(MONTHS[i], String.valueOf(i));
		monthListBox.addChangeHandler(e -> fireValueChange());

		yearTextBox = new TextBox();
		yearTextBox.setStyleName(AON.CSS.aonCustomTextBoxInput());
		yearTextBox.setMaxLength(4);
		yearTextBox.setAlignment(TextAlignment.CENTER);
		yearTextBox.getElement().setPropertyString("placeholder", "aaaa");
		yearTextBox.getElement().getStyle().setProperty("max-width", "5rem");
		yearTextBox.addValueChangeHandler(e -> fireValueChange());

		inputPanel.add(monthListBox);
		inputPanel.add(yearTextBox);
		add(inputPanel);
	}

	// ------------------------------------------------- Value

	@Override
	public Date getValue() {
		Integer month = getMonth();
		Integer year = getYear();

		if (null == month || null == year)
			return null;

		return new Date(year - 1900, month, 1);
	}

	@Override
	public void setValue(Date value) {
		setValue(value, false);
	}

	@Override
	public void setValue(Date value, boolean fireEvents) {
		if (null == value) {
			monthListBox.setSelectedIndex(0);
			yearTextBox.setValue(EMPTY_STRING);
		} else {
			selectMonth(value.getMonth());
			yearTextBox.setValue(String.valueOf(value.getYear() + 1900));
		}

		if (fireEvents)
			fireValueChange();
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Date> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	private void fireValueChange() {
		ValueChangeEvent.fire(this, getValue());
	}

	/** \u00cdndice de mes (0-11) o <code>null</code> si no hay mes seleccionado. */
	public Integer getMonth() {
		String selected = monthListBox.getSelectedValue();
		return AonStringUtils.isBlank(selected) ? null : Integer.valueOf(selected);
	}

	/** A\u00f1o (4 d\u00edgitos) o <code>null</code> si est\u00e1 vac\u00edo o no es num\u00e9rico. */
	public Integer getYear() {
		String yearValue = yearTextBox.getValue();
		if (AonStringUtils.isBlank(yearValue))
			return null;

		try {
			return Integer.valueOf(yearValue.trim());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private void selectMonth(int month) {
		String monthValue = String.valueOf(month);
		for (int i = 0; i < monthListBox.getItemCount(); i++)
			if (AonStringUtils.equals(monthListBox.getValue(i), monthValue)) {
				monthListBox.setSelectedIndex(i);
				return;
			}

		monthListBox.setSelectedIndex(0);
	}

	// ------------------------------------------------- Accessors

	public ListBox getMonthListBox() {
		return this.monthListBox;
	}

	public TextBox getYearTextBox() {
		return this.yearTextBox;
	}

	public void setEnable(boolean enabled) {
		monthListBox.setEnabled(enabled);
		yearTextBox.setEnabled(enabled);
	}

	public boolean isEnable() {
		return monthListBox.isEnabled();
	}

	public void setFocus(boolean focused) {
		monthListBox.setFocus(focused);
	}

	// ------------------------------------------------- Styles

	public void addError() {
		addStyleName(AON.CSS.aonCustomError());
	}

	public void removeError() {
		removeStyleName(AON.CSS.aonCustomError());
	}

	public void addWarning() {
		addStyleName(AON.CSS.aonCustomWarning());
	}

	public void removeWarning() {
		removeStyleName(AON.CSS.aonCustomWarning());
	}

	public void setMaxWidth(String maxWidth) {
		getElement().getStyle().setProperty("max-width", maxWidth);
	}

	public void setMinWidth(String minWidth) {
		getElement().getStyle().setProperty("min-width", minWidth);
	}

	@Override
	protected void onEnsureDebugId(String baseID) {
		super.onEnsureDebugId(baseID);
		this.monthListBox.ensureDebugId(baseID + "Month");
		this.yearTextBox.ensureDebugId(baseID + "Year");
	}

}