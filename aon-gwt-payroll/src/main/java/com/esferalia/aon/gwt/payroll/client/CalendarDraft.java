package com.esferalia.aon.gwt.payroll.client;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.FilterDialog;
import com.esferalia.aon.gwt.payroll.client.CalendarDraftObjectData.MyHolidayDraft;
import com.esferalia.aon.gwt.payroll.shared.HolidayDraft;
import com.esferalia.aon.watson.util.AonWordUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
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
import com.google.gwt.user.datepicker.client.CalendarUtil;

public class CalendarDraft extends Composite implements
		CalendarDraftObjectData.CalendarDraftListener {

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

		@ClassName("not-work")
		String notWork();
	}
	

	private static CalendarDraftUiBinder uiBinder = GWT
			.create(CalendarDraftUiBinder.class);

	interface CalendarDraftUiBinder extends UiBinder<Widget, CalendarDraft> {
	}
	
	class EnableButtons implements CalendarDraftObjectData.CalendarEvents {
		
		private CalendarDraftObjectData calendarDraftObjectData;
		
		public EnableButtons(CalendarDraftObjectData calendarDraftObjectData) {
			this.calendarDraftObjectData = calendarDraftObjectData;
			this.calendarDraftObjectData.addCalendarEvent(this);
		}

		@Override
		public void onInsertHoliday() {
			setEnableSaveButton();
		}

		@Override
		public void onUpdateHoliday() {
			setEnableSaveButton();			
		}
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
	Button deleteButton;
	@UiField
	Button addEvent;
	@UiField
	Button saveButton;
	@UiField
	Label yearLabel;
	@UiField
	Button lastYearButton;
	@UiField
	Button nextYearButton;

	private Date datePickerDateSelected;
	private Integer nameValueListBoxSelected;

	private EnableButtons enableButtons;
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

		yearLabel.setText(String.valueOf(getYear(new Date())));
	}

	@UiHandler("holidayList")
	void onListChangeHandler(ChangeEvent event) {
		saveButton.setEnabled(true);		
		Integer value = Integer.parseInt(holidayList.getSelectedValue());
		
		calendarDraftObjectData.assignHoliday2Draft(value);
		
		loadCalendarPanel(value, Integer.parseInt(yearLabel.getText()),
				calendarDraftObjectData);
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

				if (!getName().isEmpty() && getDateFrom() != null) {
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
	
	@UiHandler("deleteButton")
	void onClickDeleteButton(ClickEvent event) {
		calendarDraftObjectData.deleteHoliday(getDateSelected());
		deleteButton.setVisible(false);
	}

	@UiHandler("saveButton")
	void onSaveButtonClick(ClickEvent event) {
		Integer id = Integer.parseInt(holidayList.getSelectedValue());
		calendarDraftObjectData.saveHolidayDraft(id, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(Void result) {
				saveButton.setEnabled(false);
				loadCalendarPanel(null, Integer.parseInt(yearLabel.getText()),
						calendarDraftObjectData);
			}
		});
	}

	@UiHandler("nextYearButton")
	void onClickNextYearButton(ClickEvent event) {

		Integer year = Integer.parseInt(yearLabel.getText());
		yearLabel.setText(String.valueOf(++year));

		if (saveButton.isEnabled()) {
			calendarDraftObjectData.saveHolidayDraft(
					Integer.parseInt(holidayList.getSelectedValue()),
					new AsyncCallback<Void>() {

						@Override
						public void onFailure(Throwable caught) {

						}

						@Override
						public void onSuccess(Void result) {
							saveButton.setEnabled(false);
							loadCalendarPanelWithYearChange(null,
									Integer.parseInt(yearLabel.getText()),
									calendarDraftObjectData);
						}
					});
		} else
			loadCalendarPanelWithYearChange(null,
					Integer.parseInt(yearLabel.getText()),
					calendarDraftObjectData);

	}

	@UiHandler("lastYearButton")
	void onClickLastYearButton(ClickEvent event) {

		Integer year = Integer.parseInt(yearLabel.getText());
		yearLabel.setText(String.valueOf(--year));

		if (saveButton.isEnabled()) {
			calendarDraftObjectData.saveHolidayDraft(
					Integer.parseInt(holidayList.getSelectedValue()),
					new AsyncCallback<Void>() {

						@Override
						public void onFailure(Throwable caught) {
							
						}

						@Override
						public void onSuccess(Void result) {							
							loadCalendarPanelWithYearChange(null,
									Integer.parseInt(yearLabel.getText()),
									calendarDraftObjectData);
						}
					});
		} else
			loadCalendarPanelWithYearChange(null,
					Integer.parseInt(yearLabel.getText()),
					calendarDraftObjectData);
	}
	
	private void setEnableSaveButton() {
		saveButton.setEnabled(!calendarDraftObjectData.insertIsEmpy());
	}

	// --------------------------------------------- Listeners

	@Override
	public void onValueChangeEvent(Date date) {
		this.datePickerDateSelected = date;	
		deleteButton.setVisible(calendarDraftObjectData.canDeleteMyHoliday(date));
	}
	
	@Override
	public void onChangeEvent() {		
		initOnSuccess(calendarDraftObjectData);
	}
	
	@Override
	public void onEnterKeyPress(final Date date) {
		onEnterKeyPressAction(date);
	}
	
	@Override
	public void onSuprKeyPress(Date date) {
		onSuprKeyPressAction(date);
	}
	
	protected void onEnterKeyPressAction (final Date date) {
		FilterDialog filterDialog = new FilterDialog() {

			{
				setCaption("Descripci\u00F3n de festividad");
				setFilterLabel("Datos de la festividad " + AON.DATE_FORMAT.format(date));
				setNameLabel("Descripci\u00F3n");
				setVisibleDatePatternLabel(false);
				setVisibleDateLabel(false);
				setVisibleDateBox(false);
			}

			@Override
			protected void onAccept() {

				if (!getName().isEmpty()) {
					String name = getName();					
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
	
	protected void onSuprKeyPressAction(final Date date) {
		calendarDraftObjectData.deleteHoliday(date);
	}

	// --------------------------------------------- ---------

	public void setCalendarDraftObject(Integer pattern,
			CalendarDraftObjectData calendarDraftObjectData) {
		calendarDraftObjectData
				.loadListBoxItems(new AsyncCallback<Map<Integer, String>>() {

					@Override
					public void onFailure(Throwable caught) {

					}

					@Override
					public void onSuccess(Map<Integer, String> map) {
						
						CalendarDraft.this.holidayList.clear();

						CalendarDraft.this.holidayList.addItem("-", "-50");

						Map<Integer, String> sortedMap = sortedByComparator(map);

						for (Integer id : sortedMap.keySet())
							CalendarDraft.this.addItem2DraftMap(id, map.get(id));
					}
				});

		loadCalendarPanel(pattern, Integer.parseInt(yearLabel.getText()),
				calendarDraftObjectData);
	}

	private void addItem2DraftMap(Integer id, String item) {

		int start = item.indexOf(" ");
		String aux = item.substring(start + 1);

		holidayList.addItem(aux, "" + id);
	}

	private void loadCalendarPanel(Integer pattern, Integer year,
			CalendarDraftObjectData calendarDraftData) {

		calendarDraftData.getHolidayCalendar(pattern, year,
				new AsyncCallback<CalendarDraftObjectData>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(CalendarDraftObjectData result) {
						CalendarDraft.this.enableButtons = new EnableButtons(result);
						CalendarDraft.this.calendarDraftObjectData = result;
						CalendarDraft.this.calendarDraftObjectData
							.addCalendarListener(CalendarDraft.this);
						initOnSuccess(result);
					}
				});
	}

	private void loadCalendarPanelWithYearChange(Integer pattern, Integer year,
			CalendarDraftObjectData calendarDraftData) {

		calendarDraftData.getHolidayCalendarWithYearChange(pattern, year,
				new AsyncCallback<CalendarDraftObjectData>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(CalendarDraftObjectData result) {
						CalendarDraft.this.enableButtons = new EnableButtons(result);
						CalendarDraft.this.calendarDraftObjectData = result;
						CalendarDraft.this.calendarDraftObjectData.addCalendarListener(CalendarDraft.this);
						initOnSuccess(result);
					}
				});
	}
	
	protected void initOnSuccess(CalendarDraftObjectData result) {
		
		CalendarDraft.this.calendarPanel.clear();
		CalendarDraft.this.calendarDraftObjectData = result;
		CalendarDraft.this.calendarPanel.add(result
				.getCalendar());
		CalendarDraft.this.calendarDraftObjectData
				.insertHolidays();
		CalendarDraft.this.getItemLoadIndex();
		CalendarDraft.this.initializeLegendPanel();
		
	}
	
	protected void deshabilitGestionCalendar() {
		
		holidayList.setEnabled(false);
		addEvent.setEnabled(false);
		saveButton.setVisible(false);				
	}
 
	private void getItemLoadIndex() {
		nameValueListBoxSelected = calendarDraftObjectData
				.getHolidayDescription();
		
		for (int x = 0; x < holidayList.getItemCount(); x++) {
			if (Integer.parseInt(holidayList.getValue(x)) == nameValueListBoxSelected)
				holidayList.setItemSelected(x, true);
		}
	}

	private void initializeLegendPanel() {

		List<HolidayDraft> list = calendarDraftObjectData.getListHolidayDraft();
		ListIterator<HolidayDraft> iterator = list.listIterator(list.size());
		legendVerticalPanel.clear();
		
		legendVerticalPanel.add(
				addNotWorkLegend(false,
				"No Laborables", 
				style.notWork()));

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

	private DisclosurePanel addNotWorkLegend(boolean open, String title, String pStyle) {

		DisclosurePanel disclosurePanel = createDisclosurePanel(open,
				title, pStyle);
		VerticalPanel vPanel = new VerticalPanel();
		
		DateTimeFormat dayOfWeekFormat = DateTimeFormat.getFormat("EEEE");
		
		Date date = new Date();
		while ( date.getDay() != 0 )
			CalendarUtil.addDaysToDate(date, 1);
		
		for ( int i = 1; i < 8 ; i++) {
			CalendarUtil.addDaysToDate(date, 1);
			String label = dayOfWeekFormat.format(date);
			
			final Button dayOfWeekButton = new Button(AonWordUtils.capitalize(label));
			dayOfWeekButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON);
			dayOfWeekButton.addStyleName(style.holiday());
			
			final int dayOfWeek = date.getDay();
			final boolean isNotWorkDay = calendarDraftObjectData.isNotWorkDay(date.getDay()); 
			
			if ( isNotWorkDay ){
				CalendarDraft.this.calendarDraftObjectData.setNonWorkingDay(dayOfWeek);
				dayOfWeekButton.addStyleName(AON.AON_ICON_CHECK_YES);
			}
			else { 
				CalendarDraft.this.calendarDraftObjectData.setWorkingDay(dayOfWeek);
				dayOfWeekButton.addStyleName(AON.AON_ICON_CHECK_NO);
			}
				
			dayOfWeekButton.addClickHandler( new ClickHandler() {
				
				boolean checked = isNotWorkDay;
				
				@Override
				public void onClick(ClickEvent event) {
					if ( checked ) 
						unCheck();
					else 
						check();
					
					checked = !checked;
					setEnableSaveButton();
				}
				
				private void check() {
					dayOfWeekButton.removeStyleName(AON.AON_ICON_CHECK_NO);
					dayOfWeekButton.addStyleName(AON.AON_ICON_CHECK_YES);
					CalendarDraft.this.calendarDraftObjectData.setNonWorkingDay(dayOfWeek);
				}

				private void unCheck() {
					dayOfWeekButton.removeStyleName(AON.AON_ICON_CHECK_YES);
					dayOfWeekButton.addStyleName(AON.AON_ICON_CHECK_NO);
					CalendarDraft.this.calendarDraftObjectData.setWorkingDay(dayOfWeek);
				}
			});
			
			vPanel.add(dayOfWeekButton);
		}
		

		disclosurePanel.setContent(vPanel);

		return disclosurePanel;
	}

	private DisclosurePanel addHolidaysLegend(boolean open, String title,
			Map<Date, String> map, String pStyle) {

		DisclosurePanel statalDisclosurePanel = createDisclosurePanel(open,
				title, pStyle);
		VerticalPanel vPanel = new VerticalPanel();

		String auxMonth = "";

		for (Date key : map.keySet()) {

			String descr = map.get(key);
			//Integer monthAux = getMonth(key);
			String month = calendarDraftObjectData.getMonth(getMonth(key));

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

	private Integer getYear(Date date) {
		return Integer.parseInt(DateTimeFormat.getFormat("dd-MM-yyyy")
				.format(date).split("-")[2]);
	}

	private Integer getMonth(Date date) {
		
		//Example: January is Month 1 *******
		return Integer.parseInt(DateTimeFormat.getFormat("dd-MM-yyyy")
				.format(date).split("-")[1]) - 1;
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

	private static Map<Integer, String> sortedByComparator(
			Map<Integer, String> unsortMap) {

		List<Map.Entry<Integer, String>> list = new LinkedList<Map.Entry<Integer, String>>(
				unsortMap.entrySet());

		Collections.sort(list, new Comparator<Map.Entry<Integer, String>>() {

			@Override
			public int compare(Entry<Integer, String> o1,
					Entry<Integer, String> o2) {
				return (o1.getValue().compareTo(o2.getValue()));
			}
		});

		Map<Integer, String> sortedMap = new LinkedHashMap<Integer, String>();
		for (Iterator<Map.Entry<Integer, String>> it = list.iterator(); it
				.hasNext();) {
			Map.Entry<Integer, String> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}
}
