package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class CalendarDraft extends Composite {

	interface Style extends CssResource {

		@ClassName("disclosure-panel")
		String disclosurePanel();

		@ClassName("legend-icon")
		String legendIcon();

		@ClassName("legend-caption")
		String legendCaption();

		@ClassName("typeHoliday")
		String typeHoliday();

		@ClassName("month")
		String month();

		@ClassName("holiday")
		String holiday();

		@ClassName("statal")
		String statal();
		
		@ClassName("autonomic")
		String autonomic();
		
		@ClassName("local")
		String local();
	}

	private static CalendarDraftUiBinder uiBinder = GWT
			.create(CalendarDraftUiBinder.class);

	interface CalendarDraftUiBinder extends UiBinder<Widget, CalendarDraft> {
	}

	@UiField
	DockLayoutPanel mainPanel;
	@UiField
	Style style;
	@UiField
	ScrollPanel scrollPanel;
	@UiField
	SimplePanel calendarPanel;
	@UiField
	VerticalPanel legendVerticalPanel;
	@UiField
	ListBox yearList;

	private CalendarDraftObject calendarDraftObject;

	public CalendarDraft() {
		initWidget(uiBinder.createAndBindUi(this));

		scrollPanel.getParent().getElement().getStyle()
				.setOverflow(Overflow.VISIBLE);
		scrollPanel.getParent().getElement().getStyle()
				.setPosition(Position.STATIC);
/*		scrollPanel.getWidget().getElement().getStyle()
				.setOverflowX(Overflow.HIDDEN);*/
		
	}

	public void setCalendarDraftObject(CalendarDraftObject calendarDraftObject) {
		calendarDraftObject.load(new AsyncCallback<CalendarDraftObject>() {

			@Override
			public void onSuccess(CalendarDraftObject calendarDraftObject) {
				CalendarDraft.this.calendarDraftObject = calendarDraftObject;
				CalendarDraft.this.calendarPanel.add(calendarDraftObject
						.getCalendar());
				CalendarDraft.this.initializeLegendPanel();
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}
		});
	}
	
	private void initializeLegendPanel() {
		
		if ( calendarDraftObject.getStatalHolidays().size() > 0) {
			
			String statalTitle = calendarDraftObject.getStatalTitle();
			Map<Date, String> mapStatal = calendarDraftObject.getStatalHolidays();
			
			legendVerticalPanel.add(addHolidaysLegend(statalTitle, mapStatal, style.statal()));
		}
		
		if ( calendarDraftObject.getAutonomiHolidays().size() > 0) {
			
			String title = calendarDraftObject.getAutonomiTitle();
			Map<Date, String> map = calendarDraftObject.getAutonomiHolidays();
			
			legendVerticalPanel.add(addHolidaysLegend(title, map, style.autonomic()));
		}
		
		if ( calendarDraftObject.getLocalHolidays().size() > 0) {
			
			String title = calendarDraftObject.getLocalTitle();
			Map<Date, String> map = calendarDraftObject.getLocalHolidays();
			
			legendVerticalPanel.add(addHolidaysLegend(title, map, style.local()));
		}			


	}
	
	private DisclosurePanel addHolidaysLegend (String title, Map<Date, String> map, String pStyle) {
		
		DisclosurePanel statalDisclosurePanel = createDisclosurePanel(title, pStyle);
		VerticalPanel vPanel = new VerticalPanel();
		
		String auxMonth = "";
		
		for (Date key : map.keySet()) {
			
			String descr = map.get(key);
			String month = calendarDraftObject.getMonth(key.getMonth());
			
			if ( !auxMonth.equals(month)) {
				auxMonth = month;
				Label monthLabel = new Label(month);
				monthLabel.setStylePrimaryName(style.month());
				vPanel.add(monthLabel);
			}
			
			Label descrLabel = new Label(getDay(key) + " - " + descr);
			descrLabel.setStylePrimaryName(style.holiday());
			vPanel.add(descrLabel);
		}
		
		statalDisclosurePanel.setContent(vPanel);
		
		return statalDisclosurePanel;
	}

	private DisclosurePanel createDisclosurePanel(String title, String pStyle) {
		DisclosurePanel disclosurePanel = new DisclosurePanel();
		disclosurePanel.setStylePrimaryName(style.disclosurePanel());
		disclosurePanel.setAnimationEnabled(true);

		HorizontalPanel headerPanel = new HorizontalPanel();
		HTML statalIcon = new HTML();
		statalIcon.addStyleName(style.legendIcon());
		statalIcon.addStyleName(pStyle);
		headerPanel.add(statalIcon);

		InlineLabel holidayLabel = new InlineLabel(title);
		holidayLabel.addStyleName(style.legendCaption());
		holidayLabel.addStyleName(style.typeHoliday());
		headerPanel.add(holidayLabel);

		disclosurePanel.setHeader(headerPanel);

		return disclosurePanel;
	}

	private Integer getDay(Date date) {

		return Integer.parseInt(DateTimeFormat.getFormat("dd-MM-yyyy")
				.format(date).split("-")[0]);
	}
}
