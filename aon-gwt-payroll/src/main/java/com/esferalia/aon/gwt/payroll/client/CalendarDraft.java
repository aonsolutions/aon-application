package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.widget.Calendar;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class CalendarDraft extends Composite {

	private static CalendarDraftUiBinder uiBinder = GWT
			.create(CalendarDraftUiBinder.class);

	interface CalendarDraftUiBinder extends UiBinder<Widget, CalendarDraft> {
	}

	@UiField
	ListBox dateListBox;
	@UiField
	ScrollPanel scrollPanel;
	@UiField
	SimplePanel calendarPanel;

	public CalendarDraft() {
		initWidget(uiBinder.createAndBindUi(this));
		scrollPanel.getParent().getElement().getStyle()
				.setOverflow(Overflow.VISIBLE);
		scrollPanel.getParent().getElement().getStyle()
				.setPosition(Position.STATIC);
	}

	public void setCalendarDraftObject(CalendarDraftObject calendarDraftObject) {

		Calendar calendar = new Calendar(4);
		calendar.setFirstDate(DateUtils.getFirstDayOfYear(new Date()));
		calendar.setLastDate(DateUtils.getLastDayOfYear(new Date()));
		calendar.setVisible(true);

		calendarPanel.add(calendar);

	}

}
