package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.OrderedMultiSelectionModel;

public abstract class EmployeeCalendarHoursDialog extends CustomDialog {

	interface Binder extends UiBinder<Widget, EmployeeCalendarHoursDialog> {

	}

	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String bottonExpandStyle();
		String hidePanel();
	}
	
	@UiField
	HorizontalPanel mondayBlock;
	
	@UiField(provided = true)
	SuggestBox mondayHoursOpt;
	
	@UiField
	Button expandMondayHours;
	
	@UiField
	HorizontalPanel tuesdayBlock;
	
	@UiField(provided = true)
	SuggestBox tuesdayHoursOpt;
	
	@UiField
	Button expandTuesdayHours;
	
	@UiField
	HorizontalPanel wednesdayBlock;
	
	@UiField(provided = true)
	SuggestBox wednesdayHoursOpt;
	
	@UiField
	Button expandWednesdayHours;
	
	@UiField
	HorizontalPanel thursdayBlock;
	
	@UiField(provided = true)
	SuggestBox thursdayHoursOpt;
	
	@UiField
	Button expandThursdayHours;
	
	@UiField
	HorizontalPanel fridayBlock;
	
	@UiField(provided = true)
	SuggestBox fridayHoursOpt;
	
	@UiField
	Button expandFridayHours;
	
	@UiField
	HorizontalPanel saturdayBlock;
	
	@UiField(provided = true)
	SuggestBox saturdayHoursOpt;
	
	@UiField
	Button expandSaturdayHours;
	
	@UiField
	HorizontalPanel sundayBlock;
	
	@UiField(provided = true)
	SuggestBox sundayHoursOpt;
	
	@UiField
	Button expandSundayHours;
	
	@UiField
	Button cancelButton;
	
	@UiField
	Button acceptButton;
	
	private final static int MONDAY = 0;
	private final static int TUESDAY = 1;
	private final static int WEDNESDAY = 2;
	private final static int THURSDAY = 3;
	private final static int FRIDAY = 4;
	private final static int SATURDAY = 5;
	private final static int SUNDAY = 6;

	private final SuggestBox suggestOpts[] = new SuggestBox[7];
	private final HorizontalPanel blockDays[] = new HorizontalPanel[7];
	private Double listOldHours[] = new Double[7];
	private OrderedMultiSelectionModel<Date> selectedDates;
	private EmployeeCalendarDraftObjectData calendarEmployeeInfo;
	
	public EmployeeCalendarHoursDialog(String caption, OrderedMultiSelectionModel<Date> selectedDates,
			EmployeeCalendarDraftObjectData calendarEmployeeInfo) {
		this.selectedDates = selectedDates;
		this.calendarEmployeeInfo = calendarEmployeeInfo;
		
		setCaption(caption);
		
		initSuggestBox();
		
		setWidget(binder.createAndBindUi(this));
		
		initBlockDays();
		
		clickEventOnExpandOpt(expandMondayHours, mondayHoursOpt);
		clickEventOnExpandOpt(expandTuesdayHours, tuesdayHoursOpt);
		clickEventOnExpandOpt(expandWednesdayHours, wednesdayHoursOpt);
		clickEventOnExpandOpt(expandThursdayHours, thursdayHoursOpt);
		clickEventOnExpandOpt(expandFridayHours, fridayHoursOpt);
		clickEventOnExpandOpt(expandSaturdayHours, saturdayHoursOpt);
		clickEventOnExpandOpt(expandSundayHours, sundayHoursOpt);
		
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
				
			}
		});
		
		acceptButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
				onAccept();
			}
		});	
		
		checkShowingUpDays();
		
	}

	private void initSuggestBox() {
		//Inicializamos todos los SuggestBox para insertar horas nuevas
		ArrayList<String> suggestHours = new ArrayList<String>();
		MultiWordSuggestOracle oracleL = new MultiWordSuggestOracle();
		MultiWordSuggestOracle oracleM = new MultiWordSuggestOracle();
		MultiWordSuggestOracle oracleX = new MultiWordSuggestOracle();
		MultiWordSuggestOracle oracleJ = new MultiWordSuggestOracle();
		MultiWordSuggestOracle oracleV = new MultiWordSuggestOracle();
		MultiWordSuggestOracle oracleS = new MultiWordSuggestOracle();
		MultiWordSuggestOracle oracleD = new MultiWordSuggestOracle();
		suggestHours.add("2");
		suggestHours.add("4");
		suggestHours.add("6");
		suggestHours.add("8");
		oracleL.setDefaultSuggestionsFromText(suggestHours);
		oracleM.setDefaultSuggestionsFromText(suggestHours);
		oracleX.setDefaultSuggestionsFromText(suggestHours);
		oracleJ.setDefaultSuggestionsFromText(suggestHours);
		oracleV.setDefaultSuggestionsFromText(suggestHours);
		oracleS.setDefaultSuggestionsFromText(suggestHours);
		oracleD.setDefaultSuggestionsFromText(suggestHours);
		
		suggestOpts[MONDAY] = new SuggestBox(oracleL);
		suggestOpts[TUESDAY] = new SuggestBox(oracleM);
		suggestOpts[WEDNESDAY] = new SuggestBox(oracleX);
		suggestOpts[THURSDAY] = new SuggestBox(oracleJ);
		suggestOpts[FRIDAY] = new SuggestBox(oracleV);
		suggestOpts[SATURDAY] = new SuggestBox(oracleS);
		suggestOpts[SUNDAY] = new SuggestBox(oracleD);
		
		mondayHoursOpt = suggestOpts[MONDAY];
		tuesdayHoursOpt = suggestOpts[TUESDAY];
		wednesdayHoursOpt = suggestOpts[WEDNESDAY];
		thursdayHoursOpt = suggestOpts[THURSDAY];
		fridayHoursOpt = suggestOpts[FRIDAY];
		saturdayHoursOpt = suggestOpts[SATURDAY];
		sundayHoursOpt = suggestOpts[SUNDAY];
	}
	
	private void initBlockDays() {
		blockDays[0] = mondayBlock;
		blockDays[1] = tuesdayBlock;
		blockDays[2] = wednesdayBlock;
		blockDays[3] = thursdayBlock;
		blockDays[4] = fridayBlock;
		blockDays[5] = saturdayBlock;
		blockDays[6] = sundayBlock;
		
	}
	
	private void clickEventOnExpandOpt(Button expandButton, SuggestBox suggestBox) {
		expandButton.setStyleName("aon-icon-down-arrow");
		expandButton.addStyleName(style.bottonExpandStyle());
		
		expandButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				suggestBox.showSuggestionList();	
			}
		});
	}
	
	private void checkShowingUpDays() {
		//Ocultar todos los dias
		for (int i=0; i<7; i++)
			blockDays[i].addStyleName(style.hidePanel());
		
		//Gestionar los dias seleccionados (mostrar y actualizar valor)
		for (int i = 0; i < 7; i++){
			final int c =i; 
			try{
				
				Double minValue = Double.MIN_VALUE;
				int days [] = {1,2,3,4,5,6,0};
				
				@SuppressWarnings("deprecation")
				Double value = selectedDates.getSelectedList().stream()
						.filter(d -> d.getDay() == days[c])
						.map(d-> calendarEmployeeInfo.getHourByDay(d))
						.peek(p -> blockDays[c].removeStyleName(style.hidePanel()))
						.collect(Collectors.reducing(Double.MIN_VALUE,(h1,h2) -> minValue.equals(h1) || h2.equals(h1) ? h2: null ))
						;

				
				if(value != null && value != Double.MAX_VALUE){
					suggestOpts[c].setValue(value+"");
					listOldHours[c] = value;
				}else{
					suggestOpts[c].setValue("null");
					listOldHours[c] = null;
				}
				
			} catch (Exception e) {
				//Window.alert("Fallo!"+", "+c + "," + e.getMessage());
			}
		}
	}

	protected abstract void onAccept();
	
	public Double[] getListOldHours() {
		return listOldHours;
	}
	
	public Double getMondayHours(){
		Double hour = null;
		try{
			hour = Double.parseDouble(mondayHoursOpt.getValue());
		}catch (NumberFormatException e) {
			
		}
		return hour;
	}
	
	public Double getTuesdayHours(){
		Double hour = null;
		try{
			hour = Double.parseDouble(tuesdayHoursOpt.getValue());
		}catch (NumberFormatException e) {
			
		}
		return hour;
	}
	
	public Double getWednesdayHours(){
		Double hour = null;
		try{
			hour = Double.parseDouble(wednesdayHoursOpt.getValue());
		}catch (NumberFormatException e) {
			
		}
		return hour;
	}
	
	public Double getThursdayHours(){
		Double hour = null;
		try{
			hour = Double.parseDouble(thursdayHoursOpt.getValue());
		}catch (NumberFormatException e) {
			
		}
		return hour;
	}
	
	public Double getFridayHours(){
		Double hour = null;
		try{
			hour = Double.parseDouble(fridayHoursOpt.getValue());
		}catch (NumberFormatException e) {
			
		}
		return hour;
	}
	
	public Double getSaturdayHours(){
		Double hour = null;
		try{
			hour = Double.parseDouble(saturdayHoursOpt.getValue());
		}catch (NumberFormatException e) {
			
		}
		return hour;
	}
	
	public Double getSundayHours(){
		Double hour = null;
		try{
			hour = Double.parseDouble(sundayHoursOpt.getValue());
		}catch (NumberFormatException e) {
			
		}
		return hour;
	}
}
