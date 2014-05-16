package com.esferalia.aon.gwt.payroll.client;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.esferalia.aon.gwt.payroll.shared.DateUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

public class DateListBox extends ComboBox<Date> {
	private static DateTimeFormat DEFAULT_DATETIME_FORMAT = DateTimeFormat
			.getFormat(PredefinedFormat.DATE_LONG);

	private static class DateFormat implements Format<Date> {
		private DateTimeFormat dateTimeFormat;

		public DateFormat(DateTimeFormat dateTimeFormat) {
			this.dateTimeFormat = dateTimeFormat;
		}

		@Override
		public String format(Date date) {
			return date != null ? dateTimeFormat.format(date) : "";
		}
	}

	interface Template extends SafeHtmlTemplates {
		@SafeHtmlTemplates.Template("<span class=\"aon-nowrap {0}\">{1}</span>")
		SafeHtml span(String styles, SafeHtml str);
	}

	private static final Template template = GWT.create(Template.class);

	private static class DateFormatSafeHtmlRenderer extends
			AbstractFormatSafeHtmlRenderer<Date> {
		
		private Format<Date> format;

		private String normalStyles = "";
		private String highlightStyles = "";
		private Set<Date> highlightDates = Collections.emptySet();
		
		public DateFormatSafeHtmlRenderer(Format<Date> format) {
			this.format = format;
		}

		@Override
		public Format<Date> getFormat() {
			return format;
		}

		@Override
		public SafeHtml render(Date date) {
			if (highlightDates.contains(date))
				return template.span(highlightStyles,
						SafeHtmlUtils.fromString(getFormat().format(date)));

			return template.span(normalStyles,
					SafeHtmlUtils.fromString(getFormat().format(date)));
		}

	}

	public DateListBox() {
		this(DEFAULT_DATETIME_FORMAT);
	}

	public DateListBox(DateTimeFormat dateTimeFormat) {
		super(new DateFormatSafeHtmlRenderer(new DateFormat(dateTimeFormat)));
	}

	public Date getSelectedDate() {
		return getSelected();
	}

	public void setNormalStyles(String monthStyles) {
		((DateFormatSafeHtmlRenderer) getFormatSafeHtmlRenderer()).normalStyles = monthStyles;
	}

	public void setHighlightStyles(String highlightStyles) {
		((DateFormatSafeHtmlRenderer) getFormatSafeHtmlRenderer()).highlightStyles = highlightStyles;
	}

	public void setHighLightDates(Set<Date> highlightDates) {
		((DateFormatSafeHtmlRenderer) getFormatSafeHtmlRenderer()).highlightDates = new HashSet<Date>();
		for (Date date : highlightDates) {
			((DateFormatSafeHtmlRenderer) getFormatSafeHtmlRenderer()).highlightDates
					.add(DateUtils.getFirstDayOfMonth(date));
		}
	}

	// ------------------------------------------------------------------------

}
