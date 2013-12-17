package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.esferalia.aon.gwt.payroll.shared.DateUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.view.client.AbstractDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;

public class MonthListBox extends ComboBox<Date> {
	private static DateTimeFormat MONTH_DATE_TIME_FORMAT = DateTimeFormat
			.getFormat(PredefinedFormat.YEAR_MONTH);

	private static Format<Date> MONTH_FORMAT = new DateFormat(
			MONTH_DATE_TIME_FORMAT);

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

	private class MonthProvider extends AbstractDataProvider<Date> {

		public MonthProvider() {
			addDataDisplay(MonthListBox.this);
		}

		@Override
		protected void onRangeChanged(HasData<Date> display) {
			Range range = display.getVisibleRange();
			int start = range.getStart();
			int length = range.getLength();
			updateRowData(display, start,
					MonthListBox.this.getMonths(start, length));
		}

	}

	interface Template extends SafeHtmlTemplates {
		@SafeHtmlTemplates.Template("<span class=\"{0}\">{1}</span>")
		SafeHtml span(String styles, SafeHtml str);
	}

	private static final Template template = GWT.create(Template.class);

	private static class MonthFormatSafeHtmlRenderer extends
			AbstractFormatSafeHtmlRenderer<Date> {

		private String oldStyles = "";
		private String monthStyles = "";
		private String highLightStyles = "";
		private Set<Date> highLightMonths = Collections.emptySet();

		@Override
		public Format<Date> getFormat() {
			return MONTH_FORMAT;
		}

		@Override
		public SafeHtml render(Date month) {
			if (highLightMonths.contains(month))
				return template.span(highLightStyles,
						SafeHtmlUtils.fromString(getFormat().format(month)));

			return template.span(monthStyles,
					SafeHtmlUtils.fromString(getFormat().format(month)));
		}

	}

	private Date lastMonth;
	private Date firstMonth;

	public MonthListBox() {
		this(MONTH_DATE_TIME_FORMAT);
	}

	public MonthListBox(DateTimeFormat format) {
		super(new MonthFormatSafeHtmlRenderer());
		firstMonth = DateUtils.getFirstDayOfMonth(new Date(0));
		new MonthProvider();
	}

	public void setSelectedMonth(final Date month) {

		final int index = getIndex(month);
		int length = getPageSize();
		int start = Math.max(0, index - length / 2);

		setVisibleRangeAndClearData(new Range(start, length), true);

		Scheduler.get().scheduleFinally(new ScheduledCommand() {
			@Override
			public void execute() {
				setSelected(getMonth(index), true);
			}
		});
	}

	public Date getSelectedMonth() {
		return getSelected();
	}

	public void setLastMonth(Date lastMonth) {
		this.lastMonth = lastMonth != null ? DateUtils
				.getFirstDayOfMonth(lastMonth) : null;
	}

	public void setFirstMonth(Date firstMonth) {
		this.firstMonth = DateUtils.getFirstDayOfMonth(firstMonth);
	}

	public void setOldStyles(String oldStyles) {
		((MonthFormatSafeHtmlRenderer) getFormatSafeHtmlRenderer()).oldStyles = oldStyles;
	}

	public void setMonthStyles(String monthStyles) {
		((MonthFormatSafeHtmlRenderer) getFormatSafeHtmlRenderer()).monthStyles = monthStyles;
	}

	public void setHighLightStyles(String highLightStyles) {
		((MonthFormatSafeHtmlRenderer) getFormatSafeHtmlRenderer()).highLightStyles = highLightStyles;
	}

	public void setHighLightMonths(Set<Date> highLightMonths) {
		((MonthFormatSafeHtmlRenderer) getFormatSafeHtmlRenderer()).highLightMonths = new HashSet<Date>();
		for (Date date : highLightMonths) {
			((MonthFormatSafeHtmlRenderer) getFormatSafeHtmlRenderer()).highLightMonths
					.add(DateUtils.getFirstDayOfMonth(date));
		}
	}

	// ------------------------------------------------------------------------

	private List<Date> getMonths(int start, int length) {
		List<Date> months = new ArrayList<Date>(length);
		for (int i = 0; i < length; i++) {
			months.add(getMonth(start + i));
		}
		return months;
	}

	private Date getMonth(int index) {
		return DateUtils.addMonths2Date(CalendarUtil.copyDate(firstMonth),
				index);
	}

	private int getIndex(Date month) {
		return DateUtils.getMonths(month, firstMonth);
	}
}
