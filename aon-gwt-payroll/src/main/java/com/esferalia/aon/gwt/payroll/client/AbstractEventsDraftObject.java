package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.esferalia.aon.gwt.payroll.client.AbstractEventsDraft.DateField;
import com.esferalia.aon.gwt.payroll.shared.Events.Event;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.i18n.client.constants.NumberConstants;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

public class AbstractEventsDraftObject {

	protected static class EventMetaData {
	
			private String name;
			private String label;
			private String description;
	
			private Cell<Event> editCell;
			private Cell<Event> displayCell;
			
			private DateField [] dateFields ;
	
			public EventMetaData(String name, DateField... dateFields) {
				this(name, name, dateFields);
			}
	
			public EventMetaData(String name, String description, DateField... dateFields) {
				this(name, name, description, new EventTextCell(),
						new EventInputTextCell(), dateFields);
			}
	
			public EventMetaData(String name, String label, String description, DateField... dateFields) {
				this(name, label, description, new EventTextCell(),
						new EventInputTextCell(), dateFields);
			}
	
			protected EventMetaData(String name, String label, String description,
					Cell<Event> displayCell, Cell<Event> editCell, DateField... dateFields) {
				this.name = name;
				this.label = label;
				this.description = description;
				this.displayCell = displayCell;
				this.editCell = editCell;
				this.dateFields = dateFields;
			}
	
			public String getName() {
				return name;
			}
	
			public String getLabel() {
				return label;
			}
	
			public String getDescription() {
				return description;
			}
	
			public Cell<Event> getEditCell() {
				return editCell;
			}
	
			public Cell<Event> getDisplayCell() {
				return displayCell;
			}
			
			public boolean accept (DateField dateField) {
				
				for (DateField field : dateFields)
					if (field == dateField)
						return true;
				
				return false;
			}
		}

	protected static class EnumEventMetaData extends EventMetaData {
	
			public EnumEventMetaData(String name, String label, String description,
					String nullOption, String  options [], DateField ...dateFields) {
				this(name, label, description, Arrays.asList(options), nullOption, dateFields);
			}
	
			public EnumEventMetaData(String name, String label, String description,
					List<String> options, String nullOption , DateField ...dateFields) {
				super(name, label, description, new EventTextCell(),
						new EventSelectionCell(options, nullOption),dateFields);
			}
	
		}

	protected static class DecimalEventMetaData extends EventMetaData {
	
			private static final NumberConstants NUMBER_CONSTANTS = LocaleInfo
					.getCurrentLocale().getNumberConstants();
	
			public DecimalEventMetaData(String name, DateField... dateFields) {
				this(name, null, dateFields);
			}
	
			public DecimalEventMetaData(String name, String description, DateField... dateFields) {
				this(name, name, description, dateFields);
			}
	
			public DecimalEventMetaData(String name, String label,
					String description, DateField... dateFields) {
				super(name, label, description, new EventNumberCell(
						NUMBER_CONSTANTS), new EventInputNumberCell(
						NUMBER_CONSTANTS), dateFields);
			}
	
		}

	protected static class BooleanEventMetaData extends EventMetaData {
	
			private static final NumberConstants NUMBER_CONSTANTS = LocaleInfo
					.getCurrentLocale().getNumberConstants();
	
			public BooleanEventMetaData(String name, DateField... dateFields) {
				this(name, null, dateFields);
			}
	
			public BooleanEventMetaData(String name, String description, DateField... dateFields) {
				this(name, name, description, dateFields);
			}
	
			public BooleanEventMetaData(String name, String label,
					String description, DateField... dateFields) {
				super(name, label, description, new EventBooleanCell(),
						new EventInputCheckCell(), dateFields);
			}
	
		}

	protected static class ConstantEventMetaData extends EventMetaData {
	
			public ConstantEventMetaData(String name, DateField... dateFields) {
				this(name, null, dateFields);
			}
	
			public ConstantEventMetaData(String name, String description, DateField... dateFields) {
				this(name, name, description, dateFields);
			}
	
			public ConstantEventMetaData(String name, String label,
					String description, DateField... dateFields) {
				super(name, label, description, new EventTextCell(),
						new EventTextCell(), dateFields);
			}
	
		}

	/**
		 * A custom {@link Cell} used to render the value of a event {@link Event}
		 * as a string.
		 */
	
		protected static class EventTextCell extends AbstractCell<Event> {
	
			interface Templates extends SafeHtmlTemplates {
				@SafeHtmlTemplates.Template("<span style=\"{0}\">{1}</span>")
				SafeHtml cell(SafeStyles styles, SafeHtml value);
			}
	
			private static final int DEFAULT_SIZE = 6;
	
			private static final SafeStyles DEFAULT_STYLES = SafeStylesUtils
					.forWhiteSpace(WhiteSpace.NOWRAP);
	
			private static final Templates templates = GWT.create(Templates.class);
	
			private int size;
			private SafeStyles styles;
	
			public EventTextCell() {
				this(DEFAULT_STYLES, DEFAULT_SIZE);
			}
	
			public EventTextCell(SafeStyles styles, int size) {
				this.styles = styles;
			}
	
			@Override
			public void render(Context context, Event event, SafeHtmlBuilder sb) {
				if (event == null) {
					return;
				}
	
				render(context, event.getValue(), sb);
	
			}
	
			@Override
			public Set<String> getConsumedEvents() {
				return Collections.emptySet();
			}
	
			protected void render(Context context, String value, SafeHtmlBuilder sb) {
				if (value == null) {
					return;
				}
	
				String display = value.substring(0, Math.max(size, value.length()));
	
				// If the value comes from the user, we escape it to avoid XSS
				// attacks.
				SafeHtml safeValue = SafeHtmlUtils.fromString(display);
	
				SafeHtml rendered = templates.cell(styles, safeValue);
				sb.append(rendered);
			}
	
		}

	protected static class EventNumberCell extends EventTextCell {
	
			private NumberFormat format;
			private NumberConstants numberConstants;
	
			public EventNumberCell(NumberConstants numberConstants) {
				this.numberConstants = numberConstants;
				this.format = NumberFormat.getFormat(numberConstants
						.decimalPattern());
			}
	
			@Override
			protected void render(Context context, String value, SafeHtmlBuilder sb) {
				try {
					super.render(context, format.format(Double.parseDouble(value)),
							sb);
				} catch (NumberFormatException e) {
					super.render(context, numberConstants.notANumber(), sb);
				}
			}
		}

	protected static class EventBooleanCell extends AbstractCell<Event> {
	
			interface Templates extends SafeHtmlTemplates {
				@SafeHtmlTemplates.Template("<div class=\"aon-check\" />")
				SafeHtml checked();
			}
	
			private static final Templates templates = GWT.create(Templates.class);
	
			@Override
			public void render(Context context, Event event, SafeHtmlBuilder sb) {
				if (event == null) {
					return;
				}
	
				render(context, event.getValue(), sb);
	
			}
	
			protected void render(Context context, String value, SafeHtmlBuilder sb) {
				if (value == null || !Boolean.valueOf(value)) {
					return;
				}
				sb.append(templates.checked());
			}
	
		}

	/**
		 * 
		 */
		protected static class EventInputTextCell extends AbstractCell<Event> {
	
			interface Templates extends SafeHtmlTemplates {
				@SafeHtmlTemplates.Template("<input class=\"aon-inputText\" style=\"{0}\" type=\"text\" size=\"{1}\" />")
				SafeHtml empty(SafeStyles styles, int size);
	
				@SafeHtmlTemplates.Template("<input class=\"aon-inputText\" style=\"{0} \"type=\"text\" size=\"{1}\" value=\"{2}\" />")
				SafeHtml input(SafeStyles styles, int size, String value);
	
			}
	
			private static final int DEFAULT_SIZE = 5;
	
			private static final SafeStyles DEFAULT_STYLES = SafeStylesUtils
					.forWhiteSpace(WhiteSpace.NOWRAP);
	
			private static final Templates templates = GWT.create(Templates.class);
	
			private int size;
			private SafeStyles styles;
	
			public EventInputTextCell() {
				this(DEFAULT_STYLES, DEFAULT_SIZE);
			}
	
			public EventInputTextCell(String... consumedEvents) {
				this(DEFAULT_STYLES, DEFAULT_SIZE, consumedEvents);
			}
	
			public EventInputTextCell(SafeStyles styles, int size) {
				this(styles, size, BrowserEvents.CHANGE, BrowserEvents.DBLCLICK);
			}
	
			public EventInputTextCell(SafeStyles styles, int size,
					String... consumedEvents) {
				super(consumedEvents);
				this.styles = styles;
				this.size = size;
			}
	
			@Override
			public boolean isEditing(
					com.google.gwt.cell.client.Cell.Context context,
					Element parent, Event value) {
				return true;
			}
	
			@Override
			public void onBrowserEvent(Context context, Element parent,
					Event value, NativeEvent event, ValueUpdater<Event> valueUpdater) {
	
				super.onBrowserEvent(context, parent, value, event, valueUpdater);
				String type = event.getType();
	
				if (BrowserEvents.CHANGE.equals(type)) {
					InputElement input = getInputElement(parent);
					value.setValue(input.getValue());
					valueUpdater.update(value);
				} else if (BrowserEvents.DBLCLICK.equals(type)) {
					InputDialog inputDialog = new InputDialog("", "");
					inputDialog.center();
					inputDialog.show();
				}
			}
	
			@Override
			public boolean resetFocus(
					com.google.gwt.cell.client.Cell.Context context,
					Element parent, Event value) {
				getInputElement(parent).focus();
				return true;
			}
	
			@Override
			public void render(Context context, Event event, SafeHtmlBuilder sb) {
				render(context, event != null ? event.getValue() : null, sb);
	
			}
	
			private void render(Context context, String value, SafeHtmlBuilder sb) {
				if (value == null) {
					sb.append(templates.empty(styles, size));
				} else {
					sb.append(templates.input(styles, size, value));
				}
	
			}
	
			protected InputElement getInputElement(Element parent) {
				return parent.getFirstChild().cast();
			}
	
		}

	protected static class EventInputNumberCell extends EventInputTextCell {
	
			private NumberConstants numberConstants;
	
			public EventInputNumberCell(NumberConstants numberConstants) {
				super(BrowserEvents.CHANGE, BrowserEvents.KEYPRESS);
				this.numberConstants = numberConstants;
	
			}
	
			@Override
			public void onBrowserEvent(Context context, Element parent,
					Event value, NativeEvent event, ValueUpdater<Event> valueUpdater) {
	
				if (BrowserEvents.KEYPRESS.equals(event.getType())) {
					int keyCode = event.getKeyCode();
					if (keyCode == KeyCodes.KEY_BACKSPACE
							|| keyCode == KeyCodes.KEY_DELETE
							|| keyCode == KeyCodes.KEY_LEFT
							|| keyCode == KeyCodes.KEY_RIGHT
							|| keyCode == KeyCodes.KEY_TAB) {
						super.onBrowserEvent(context, parent, value, event,
								valueUpdater);
						return;
					}
	
					int charCode = event.getCharCode();
					if (isDigit(charCode) || isDecimalSep(charCode)
							|| isMinus(charCode) || isPlus(charCode)) {
						super.onBrowserEvent(context, parent, value, event,
								valueUpdater);
						return;
					}
	
					event.preventDefault();
	
				}
				super.onBrowserEvent(context, parent, value, event, valueUpdater);
	
			}
	
			private boolean isDigit(int charCode) {
				return (charCode >= 48 && charCode <= 57);
			}
	
			private boolean isPlus(int charCode) {
				return isAt(charCode, numberConstants.plusSign());
			}
	
			private boolean isMinus(int charCode) {
				return isAt(charCode, numberConstants.minusSign());
			}
	
			private boolean isDecimalSep(int charCode) {
				return charCode == Character.codePointAt(".", 0);
			}
	
			private static boolean isAt(int charCode, String str) {
				for (int i = 0; i < str.length(); i++)
					if (charCode == Character.codePointAt(str, i))
						return true;
				return false;
			}
		}

	/**
		 * 
		 */
		protected static class EventInputCheckCell extends AbstractCell<Event> {
	
			interface Templates extends SafeHtmlTemplates {
				@SafeHtmlTemplates.Template("<input style=\"{0} \"type=\"checkbox\" checked />")
				SafeHtml checked(SafeStyles styles);
	
				@SafeHtmlTemplates.Template("<input style=\"{0} \"type=\"checkbox\" />")
				SafeHtml unchecked(SafeStyles styles);
	
			}
	
			private static final SafeStyles DEFAULT_STYLES = SafeStylesUtils
					.forWhiteSpace(WhiteSpace.NOWRAP);
	
			private static final Templates templates = GWT.create(Templates.class);
	
			private SafeStyles styles;
	
			public EventInputCheckCell() {
				this(DEFAULT_STYLES);
			}
	
			public EventInputCheckCell(String... consumedEvents) {
				this(DEFAULT_STYLES, consumedEvents);
			}
	
			public EventInputCheckCell(SafeStyles styles) {
				this(styles, BrowserEvents.CHANGE);
			}
	
			public EventInputCheckCell(SafeStyles styles, String... consumedEvents) {
				super(consumedEvents);
				this.styles = styles;
			}
	
			@Override
			public boolean isEditing(
					com.google.gwt.cell.client.Cell.Context context,
					Element parent, Event value) {
				return true;
			}
	
			@Override
			public void onBrowserEvent(Context context, Element parent,
					Event value, NativeEvent event, ValueUpdater<Event> valueUpdater) {
	
				super.onBrowserEvent(context, parent, value, event, valueUpdater);
				String type = event.getType();
	
				if (BrowserEvents.CHANGE.equals(type)) {
					InputElement input = getInputElement(parent);
					value.setValue(Boolean.toString(input.isChecked()));
					valueUpdater.update(value);
				}
			}
	
			@Override
			public boolean resetFocus(
					com.google.gwt.cell.client.Cell.Context context,
					Element parent, Event value) {
				getInputElement(parent).focus();
				return true;
			}
	
			@Override
			public void render(Context context, Event event, SafeHtmlBuilder sb) {
				render(context, event != null ? event.getValue() : null, sb);
	
			}
	
			private void render(Context context, String value, SafeHtmlBuilder sb) {
				if (value != null && Boolean.valueOf(value))
					sb.append(templates.checked(styles));
				else
					sb.append(templates.unchecked(styles));
	
			}
	
			protected InputElement getInputElement(Element parent) {
				return parent.getFirstChild().cast();
			}
	
		}

	/**
		 * 
		 */
		protected static class EventSelectionCell extends AbstractCell<Event> {
	
			interface Templates extends SafeHtmlTemplates {
				@Template("<option value=\"{0}\">{0}</option>")
				SafeHtml deselected(String option);
	
				@Template("<option value=\"{0}\" selected=\"selected\">{0}</option>")
				SafeHtml selected(String option);
			}
	
			private static Templates template = GWT.create(Templates.class);
	
			private String nullOption = null;
			private List<String> options;
			private HashMap<String, Integer> indexForOption;
	
			public EventSelectionCell(List<String> options, String nullOption) {
				super(BrowserEvents.CHANGE);
				this.indexForOption = new HashMap<String, Integer>();
				this.options = new ArrayList<String>(options.size() + 1);
				if (nullOption != null) {
					this.options.add(nullOption);
					this.indexForOption.put(nullOption, 0);
				}
				for (int i = 0; i < options.size(); i++) {
					String option = options.get(i);
					this.options.add(option);
					this.indexForOption.put(option, i + 1);
				}
	
			}
	
			@Override
			public boolean resetFocus(
					com.google.gwt.cell.client.Cell.Context context,
					Element parent, Event value) {
				getSelectElement(parent).focus();
				return true;
			}
	
			@Override
			public void render(Context context, Event event, SafeHtmlBuilder sb) {
				render(context, event != null ? event.getValue() : null, sb);
	
			}
	
			@Override
			public void onBrowserEvent(
					com.google.gwt.cell.client.Cell.Context context,
					Element parent, Event value, NativeEvent event,
					ValueUpdater<Event> valueUpdater) {
	
				super.onBrowserEvent(context, parent, value, event, valueUpdater);
	
				String type = event.getType();
	
				if (BrowserEvents.CHANGE.equals(type)) {
	
					SelectElement select = getSelectElement(parent);
	
					String option = options.get(select.getSelectedIndex());
					if (option == nullOption)
						option = null;
	
					value.setValue(option);
	
					valueUpdater.update(value);
				}
			}
	
			@Override
			public boolean isEditing(
					com.google.gwt.cell.client.Cell.Context context,
					Element parent, Event value) {
				return true;
			}
	
			// ---------------------------------------------------------------------
			//
			// ---------------------------------------------------------------------
	
			private SelectElement getSelectElement(Element parent) {
				return parent.getFirstChild().cast();
			}
	
			private int getSelectedIndex(String value) {
				Integer index = indexForOption.get(value);
				if (index == null) {
					return -1;
				}
				return index.intValue();
			}
	
			private void render(Context context, String value, SafeHtmlBuilder sb) {
	
				// TODO: Get the view data.
	
				int selectedIndex = getSelectedIndex(value != null ? value
						: nullOption);
				sb.appendHtmlConstant("<select tabindex=\"-1\">");
				int index = 0;
				
				for (String option : options) {
					if (index++ == selectedIndex) {
						sb.append(template.selected(option));
					} else {
						sb.append(template.deselected(option));
					}
				}
				
				sb.appendHtmlConstant("</select>");
	
			}
	
		}

	public AbstractEventsDraftObject() {
		super();
	}

}