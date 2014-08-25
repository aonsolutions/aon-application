package com.esferalia.aon.gwt.common.client.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.google.gwt.core.client.GWT;
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
		@SafeHtmlTemplates.Template("<span class=\"aon-nowrap {0}\" >{1}</span>")
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

	public void setSelectedMonth(final Date aMonth) {
		final int index = getIndex(aMonth);
		int length = getPageSize();
		int start = Math.max(0, index - length / 2);
		setVisibleRangeAndClearData(new Range(start, length), true);

		setSelected(getMonth(index), true);
		/*
		Scheduler.get().scheduleFinally(new ScheduledCommand() {
			@Override
			public void execute() {
				setSelected(getMonth(index), true);
			}
		});*/
	}


	public Date getSelectedMonth() {
		return getSelected();
	}

	public void setLastMonth(Date aMonth) {
		this.lastMonth = aMonth != null ? DateUtils
				.getFirstDayOfMonth(aMonth) : null;
	}

	public void setFirstMonth(Date aMonth) {
		this.firstMonth = DateUtils.getFirstDayOfMonth(aMonth);
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

	protected List<Date> getMonths(int start, int length) {
		List<Date> months = new ArrayList<Date>(length);

		for (int i = 0; i < length; i++) {
			Date month = getMonth(start + i);
			if (DateUtils.compare(month, lastMonth) > 0) {
				return months;
			}
			months.add(month);
		}
		return months;
	}

	protected Date getMonth(int index) {
		return DateUtils.addMonths2Date(CalendarUtil.copyDate(firstMonth),
				index);
	}

	protected int getIndex(Date month) {
		return DateUtils.getMonths(month, firstMonth);
	}

	// ------------------------------------------------------------------------
}
