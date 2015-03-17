package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.payroll.client.CalendarDraftObjectData.MyHolidayDraft;
import com.esferalia.aon.gwt.payroll.shared.HolidayDraft;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
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
		CalendarDraftObjectData.Listener {

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
	@UiField
	Button saveButton;

	private Date datePickerDateSelected;
	private String nameValueListBoxSelected;

	private CalendarDraftObjectData calendarDraftObjectData;

	public CalendarDraft() {
		initWidget(uiBinder.createAndBindUi(this));		
	}

	// --------------------------------------------- UiHandlers

	@UiHandler("calendarPanel")
	void onCalendarPanelAttach(AttachEvent event) {
		scrollPanel.getParent().getElement().getStyle()
				.setOverflow(Overflow.VISIBLE);
		scrollPanel.getParent().getElement().getStyle()
				.setPosition(Position.STATIC);
	}

	@UiHandler("holidayList")
	void onListChangeHandler(ChangeEvent event) {
		if(holidayList.getSelectedIndex() > 0) {
			saveButton.setEnabled(true);
			loadCalendarPanel(holidayList.getSelectedValue(),
					calendarDraftObjectData);
		}
	}
	
	@UiHandler("addEvent")
	void onAddEventClick(ClickEvent event) {
		
		FilterDialog filterDialog = new FilterDialog() {
			
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
				
				if (getName() != null && getDateFrom() != null) {
					String name = getName();
					Date date = getDateFrom();
					calendarDraftObjectData.addHoliday(date, name);
					initializeLegendPanel();
				}
			}
		};
		
		filterDialog.setDateFrom(getDateSelected());
		filterDialog.center();
		filterDialog.show();
		filterDialog.setFocusOnNameTextBox(true);
	}
	
	@UiHandler("saveButton")
	void onSaveButtonClick(ClickEvent event) {
		calendarDraftObjectData.saveHolidayDraft(nameValueListBoxSelected, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(Void result) {
				loadCalendarPanel(null, calendarDraftObjectData);
			}
		});
		
		
	}

	// --------------------------------------------- Listeners

	@Override
	public void onValueChangeEvent(ValueChangeEvent<Date> event) {
		this.datePickerDateSelected = event.getValue();
	}

	@Override
	public void onInsertHoliday() {
		saveButton.setEnabled(!calendarDraftObjectData.insertIsEmpy());
	}

	// --------------------------------------------- ---------

	public void setCalendarDraftObject(String pattern,
			CalendarDraftObjectData calendarDraftObjectData) {

		calendarDraftObjectData
				.loadListBoxItems(new AsyncCallback<List<String>>() {

					@Override
					public void onFailure(Throwable caught) {

					}

					@Override
					public void onSuccess(List<String> result) {
						CalendarDraft.this.holidayList.addItem("-", "-");
						for (String item : result)
							CalendarDraft.this.addItem2DraftMap(item);
					}
				});

		loadCalendarPanel(pattern, calendarDraftObjectData);
	}

	private void addItem2DraftMap(String item) {

		int start = item.indexOf(" ");
		String aux = item.substring(start + 1);

		holidayList.addItem(aux, item);
	}

	private void loadCalendarPanel(String pattern,
			CalendarDraftObjectData calendarDraftData) {

		calendarDraftData.getHolidayCalendar(pattern,
				new AsyncCallback<CalendarDraftObjectData>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(CalendarDraftObjectData result) {
						CalendarDraft.this.calendarPanel.clear();
						CalendarDraft.this.calendarDraftObjectData = result;
						CalendarDraft.this.calendarPanel.add(result
								.getCalendar());
						CalendarDraft.this.calendarDraftObjectData
								.addCalendarListener(CalendarDraft.this);
						CalendarDraft.this.calendarDraftObjectData
								.insertHolidays();
						CalendarDraft.this.getItemLoadIndex();
						CalendarDraft.this.initializeLegendPanel();
					}
				});
	}

	private void getItemLoadIndex() {
		
		nameValueListBoxSelected = "-";
		
		for (int x = 0; x < holidayList.getItemCount(); x++) {
			if (holidayList.getValue(x).compareTo(
					calendarDraftObjectData.getHolidayDescription()) == 0) {
				holidayList.setItemSelected(x, true);
				nameValueListBoxSelected = holidayList.getValue(x);
			}
		}
	}

	private void initializeLegendPanel() {

		List<HolidayDraft> list = calendarDraftObjectData.getListHolidayDraft();
		ListIterator<HolidayDraft> iterator = list.listIterator(list.size());
		legendVerticalPanel.clear();
		int contador = 0;
		while (iterator.hasPrevious()) {
			HolidayDraft draft = iterator.previous();

			legendVerticalPanel.add(addHolidaysLegend(false,
					draft.getDescription(), draft.getHolidaysMap(),
					getStyle(contador++)));
		}

		List<MyHolidayDraft> myDrafts = calendarDraftObjectData.getMyDrafts();
		ListIterator<MyHolidayDraft> draftIterator = myDrafts.listIterator();

		while (draftIterator.hasNext()) {
			MyHolidayDraft draft = draftIterator.next();

			legendVerticalPanel.add(addHolidaysLegend(true,
					draft.getDescription(), draft.getGeneralMap(),
					getStyle(contador++)));
		}
	}

	private DisclosurePanel addHolidaysLegend(boolean open, String title,
			Map<Date, String> map, String pStyle) {

		DisclosurePanel statalDisclosurePanel = createDisclosurePanel(open,
				title, pStyle);
		VerticalPanel vPanel = new VerticalPanel();

		String auxMonth = "";

		for (Date key : map.keySet()) {

			String descr = map.get(key);
			String month = calendarDraftObjectData.getMonth(key.getMonth());

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

	private DisclosurePanel createDisclosurePanel(boolean open, String title,
			String pStyle) {
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
		disclosurePanel.setOpen(open);
		return disclosurePanel;
	}

	private Integer getDay(Date date) {
		return Integer.parseInt(DateTimeFormat.getFormat("dd-MM-yyyy")
				.format(date).split("-")[0]);
	}

	private Date getDateSelected() {
		return this.datePickerDateSelected;
	}

	private static String getStyle(Integer contador) {

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
