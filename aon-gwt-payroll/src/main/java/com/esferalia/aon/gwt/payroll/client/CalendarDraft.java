package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.payroll.shared.HolidayDraft;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
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

public class CalendarDraft extends Composite implements
		CalendarDraftObject.Listener {

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

		@ClassName("other")
		String other();

	}

	private static CalendarDraftUiBinder uiBinder = GWT
			.create(CalendarDraftUiBinder.class);

	interface CalendarDraftUiBinder extends UiBinder<Widget, CalendarDraft> {
	}

	@UiField
	ListBox holidayList;
	@UiField
	DockLayoutPanel mainPanel;
	@UiField
	static Style style;
	@UiField
	ScrollPanel scrollPanel;
	@UiField
	SimplePanel calendarPanel;
	@UiField
	VerticalPanel legendVerticalPanel;
	@UiField
	ListBox yearList;
	@UiField
	Button addEvent;
	
	private FilterDialog filterDialog;
	private Date dateSeleted;

	private CalendarDraftObject calendarDraftObject;

	private static final DateTimeFormat END_DATE_FORMAT = DateTimeFormat
			.getFormat(PredefinedFormat.DATE_SHORT);

	public CalendarDraft() {
		initWidget(uiBinder.createAndBindUi(this));

		scrollPanel.getParent().getElement().getStyle()
				.setOverflow(Overflow.VISIBLE);
		scrollPanel.getParent().getElement().getStyle()
				.setPosition(Position.STATIC);
		
		initAddEventButton(addEvent);
	}

	// --------------------------------------------- UiHandlers
	
	private void initAddEventButton(final Button addEvent) {
		
		addEvent.addClickHandler(new ClickHandler() {
			
			{
				filterDialog = new FilterDialog() {
				
					{
						setCaption("Nuevo Festivo");
						setFilterLabel("Datos de la festividad ...");
						setNameLabel("Descripci\u00F3n");
						setDateLabel("Fecha");
						setDateTimeFormat(AON.DATE_FORMAT);
						setVisibleDatePatternLabel(false);
					}

					@Override
					protected void onAccept() {
						// TODO Auto-generated method stub
						
					}
				};
			}
			
			@Override
			public void onClick(ClickEvent event) {
				filterDialog.setDateFrom(getDateSelected());
				filterDialog.center();
				filterDialog.show();
			}
		});
	}
	
	// --------------------------------------------- Listeners

	@Override
	public void onValueChangeEvent(ValueChangeEvent<Date> event) {
		this.dateSeleted = event.getValue();
	}

	// --------------------------------------------- ---------

	public void setCalendarDraftObject(CalendarDraftObject calendarDraftObject) {

		calendarDraftObject.loadListBoxItems(new AsyncCallback<List<String>>() {

			@Override
			public void onFailure(Throwable caught) {

			}

			@Override
			public void onSuccess(List<String> result) {
				CalendarDraft.this.holidayList.addItem("-");
				for (String item : result)
					CalendarDraft.this.holidayList.addItem(item);
			}
		});

		calendarDraftObject.load(new AsyncCallback<CalendarDraftObject>() {

			@Override
			public void onSuccess(CalendarDraftObject calendarDraftObject) {
				CalendarDraft.this.calendarDraftObject = calendarDraftObject;
				CalendarDraft.this.calendarDraftObject
						.addListener(CalendarDraft.this);
				CalendarDraft.this.calendarPanel.add(calendarDraftObject
						.getCalendar());
				CalendarDraft.this.calendarDraftObject.insertHolidays();
				CalendarDraft.this.initializeLegendPanel();
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}
		});
	}

	private void initializeLegendPanel() {

		List<HolidayDraft> list = calendarDraftObject.getListHolidayDraft();

		ListIterator<HolidayDraft> iterator = list.listIterator(list.size());

		int contador = 0;
		while (iterator.hasPrevious()) {
			HolidayDraft draft = iterator.previous();
			legendVerticalPanel.add(addHolidaysLegend(draft.getDescription(),
					draft.getHolidaysMap(),
					getStyle(contador++)));
		}
	}

	private DisclosurePanel addHolidaysLegend(String title,
			Map<Date, String> map, String pStyle) {

		DisclosurePanel statalDisclosurePanel = createDisclosurePanel(title,
				pStyle);
		VerticalPanel vPanel = new VerticalPanel();

		String auxMonth = "";

		for (Date key : map.keySet()) {

			String descr = map.get(key);
			String month = calendarDraftObject.getMonth(key.getMonth());

			if (!auxMonth.equals(month)) {
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
		HTML icon = new HTML();
		icon.addStyleName(style.legendIcon());
		icon.addStyleName(pStyle);
		headerPanel.add(icon);

		InlineLabel holidayLabel = new InlineLabel(title);
		holidayLabel.addStyleName(style.legendCaption());
		holidayLabel.addStyleName(style.typeHoliday());
		headerPanel.add(holidayLabel);

		disclosurePanel.setHeader(headerPanel);
		disclosurePanel.setOpen(true);

		return disclosurePanel;
	}

	private Integer getDay(Date date) {
		return Integer.parseInt(DateTimeFormat.getFormat("dd-MM-yyyy")
				.format(date).split("-")[0]);
	}

	private Date getDateSelected() {
		return this.dateSeleted;
	}
	
	private static String getStyle (Integer contador) {
		
		switch (contador) {
		case 0:
			return style.statal();
		case 1:
			return style.autonomic();
		case 2: 
			return style.local();
		case 3: 
			return style.other();
			
		default:
				return style.other();
		}
	}
}
