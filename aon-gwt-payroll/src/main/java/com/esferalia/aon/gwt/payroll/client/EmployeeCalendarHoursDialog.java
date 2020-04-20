package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class EmployeeCalendarHoursDialog extends CustomDialog {

	interface Binder extends UiBinder<Widget, EmployeeCalendarHoursDialog> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	DateBoxEx startDateDB;
	
	@UiField
	DateBoxEx endDateDB;
	
	@UiField
	HTMLPanel mondayBlock;
	
	@UiField
	TextBox mondayHoursOpt;
	
	@UiField
	HTMLPanel tuesdayBlock;
	
	@UiField
	TextBox tuesdayHoursOpt;
	
	@UiField
	HTMLPanel wednesdayBlock;
	
	@UiField
	TextBox wednesdayHoursOpt;
	
	@UiField
	HTMLPanel thursdayBlock;
	
	@UiField
	TextBox thursdayHoursOpt;
	
	@UiField
	HTMLPanel fridayBlock;
	
	@UiField
	TextBox fridayHoursOpt;
	
	@UiField
	HTMLPanel saturdayBlock;
	
	@UiField
	TextBox saturdayHoursOpt;
	
	@UiField
	HTMLPanel sundayBlock;
	
	@UiField
	TextBox sundayHoursOpt;
	
	@UiField
	Button cancelButton;
	
	@UiField
	Button acceptButton;
	
	// -------------------------------------------------------------------------------
	// --------------------------------- MAIN CLASS ----------------------------------
	// -------------------------------------------------------------------------------
	
	private final HTMLPanel blockDays[] = new HTMLPanel[7];
	private Double listOldHours[] = new Double[7];
	private Double listNewHours[] = new Double[7];
	private Boolean showDays[] = new Boolean[7];
	
	
	private EmployeeCalendarDraftObject employeeCalendarDraftObject;
	private List<Date> selectedDates;
	
	private Date contractStartDate;
	private Date contractEndDate;
	
	public EmployeeCalendarHoursDialog(List<Date> selectedDates, Date contractStartDate, Date contractEndDate, EmployeeCalendarDraftObject employeeCalendarDraftObject) {
		setCaption("HORAS");
		
		setWidget(binder.createAndBindUi(this));
		
		this.contractStartDate = contractStartDate;
		this.contractEndDate = contractEndDate;
		
		if(!selectedDates.isEmpty()) {
			selectedDates.sort(null);
			startDateDB.setValue(selectedDates.get(0));
			endDateDB.setValue(selectedDates.get(selectedDates.size() - 1));
		} else {
			this.startDateDB.setValue(contractStartDate);
			this.endDateDB.setValue(contractEndDate);
		}
		
		this.selectedDates = selectedDates;
		this.employeeCalendarDraftObject = employeeCalendarDraftObject;
		
		for(int i=0; i<7; i++)
			showDays[i] = false;
		
		initBlockDays();	
		checkShowingUpDays();
		initDefaultValuesTB();
		
		startDateDB.getTextBox().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				startDateDB.getDatePicker().getElement().setAttribute("style", "visibility: visible; overflow: visible; position: absolute; left: 0px; z-index: 108; ");
			}
		});
		
		endDateDB.getTextBox().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				endDateDB.getDatePicker().getElement().setAttribute("style", "visibility: visible; overflow: visible; position: absolute; left: 0px; z-index: 108; ");
			}
		});
		
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		
		acceptButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(null != startDateDB.getValue()) {
					accept();
					onAccept();
				}
				hide();
			}

			private void accept() {
				// Initialice listNewHours
				listNewHours[0] = getSundayHours();
				listNewHours[1] = getMondayHours();
				listNewHours[2] = getTuesdayHours();
				listNewHours[3] = getWednesdayHours();
				listNewHours[4] = getThursdayHours();
				listNewHours[5] = getFridayHours();
				listNewHours[6] = getSaturdayHours();
				
				for(int day=0; day<7; day++) {
					if(showDays[day]) {
						Date startDate = getStartDate();
						Date endDate = getEndDate();
//						Window.alert("ADDED -> StartDate : " + startDate + " EndDate : " + endDate + " Day : " + day + " Value : "+ listNewHours[day]);
						employeeCalendarDraftObject.addDayHour(startDate, endDate, day, listNewHours[day]);
					}
				}
			}
		});	
	}
	
	// -------------------------------------------------------------------------------
	// ----------------------------- ABSTRACT METHOD ---------------------------------
	// -------------------------------------------------------------------------------
	
	protected abstract void onAccept();
	
	// -------------------------------------------------------------------------------
	// -------------------------------- UI HANDLERS ----------------------------------
	// -------------------------------------------------------------------------------
	
	@UiHandler("startDateDB")
	public void onStartDateDBChange(ValueChangeEvent<Date> event) {
		Date date = event.getValue();
		if(null != date) {
			if(date.before(contractStartDate))
				startDateDB.setValue(contractStartDate);
		}
	}
	
	@UiHandler("endDateDB")
	public void onEndDateDBChange(ValueChangeEvent<Date> event) {
		Date date = event.getValue();
		if(null != date && null != contractEndDate) {
			if(date.after(contractEndDate))
				endDateDB.setValue(contractEndDate);
		}
	}
	
	// -------------------------------------------------------------------------------
	// -------------------------------- AUX METHODS ----------------------------------
	// -------------------------------------------------------------------------------

	private void initDefaultValuesTB() {
		for(int i = 0; i<7; i++) {
			Double hour = getListOldHours()[i];
			switch (i) {
				case 0:
					this.sundayHoursOpt.setValue(null == hour ? "" : Double.toString(hour));
					break;
				case 1:
					this.mondayHoursOpt.setValue(null == hour ? "" : Double.toString(hour));
					break;
				case 2:
					this.tuesdayHoursOpt.setValue(null == hour ? "" : Double.toString(hour));
					break;
				case 3:
					this.wednesdayHoursOpt.setValue(null == hour ? "" : Double.toString(hour));
					break;
				case 4:
					this.thursdayHoursOpt.setValue(null == hour ? "" : Double.toString(hour));
					break;
				case 5:
					this.fridayHoursOpt.setValue(null == hour ? "" : Double.toString(hour));
					break;	
				case 6:
					this.saturdayHoursOpt.setValue(null == hour ? "" : Double.toString(hour));
					break;
				default:
					break;
			}
		}
	}
			
	private void initBlockDays() {
		blockDays[0] = sundayBlock;
		blockDays[1] = mondayBlock;
		blockDays[2] = tuesdayBlock;
		blockDays[3] = wednesdayBlock;
		blockDays[4] = thursdayBlock;
		blockDays[5] = fridayBlock;
		blockDays[6] = saturdayBlock;
	}
	
	private void checkShowingUpDays() {
		if(!this.selectedDates.isEmpty()) {
			//Ocultar todos los dias
			for (int i=0; i<7; i++)
				blockDays[i].getElement().getStyle().setDisplay(Display.NONE);
			
			//Gestionar los dias seleccionados (mostrar y actualizar valor)
			for (int i = 0; i < 7; i++){
				final int c =i; 
				try{
					Double minValue = Double.MIN_VALUE;
					
					@SuppressWarnings("deprecation")
					Double value = this.selectedDates.stream()
							.filter(d -> d.getDay() == c)
							.map(d-> employeeCalendarDraftObject.getHourByDate(d))
							.peek(p -> showDays[c] = true)
							.peek(p -> blockDays[c].getElement().getStyle().clearDisplay())
							.collect(Collectors.reducing(Double.MIN_VALUE, (h1,h2) -> minValue.equals(h1) || h2.equals(h1) ? h2: null ));
	
					
					if(value != null && value != Double.MIN_VALUE){
						listOldHours[c] = value;
					}else{
						listOldHours[c] = null;
					}	
				} catch (Exception e) {
//					Window.alert("Formato incorrecto");
				}
			}
		} else {
			for (int i=0; i<7; i++)
				showDays[i] = true;
		}
	}
	
	// -------------------------------------------------------------------------------
	// ---------------------------------- GET HOURS ----------------------------------
	// -------------------------------------------------------------------------------
	
	public Double[] getListOldHours() {
		return listOldHours;
	}
	
	public Date getStartDate() {
		return this.startDateDB.getValue();
	}
	
	public Date getEndDate() {
		return this.endDateDB.getValue();
	}
	
	public Double getMondayHours(){
		Double hour = null;
		String hourStr = mondayHoursOpt.getValue();
		try{
			if(StringUtils.isEmpty(hourStr))
				return null;
			hour = Double.parseDouble(hourStr);
		}catch (NumberFormatException e) {}
		
		return hour;
	}
	
	public Double getTuesdayHours(){
		Double hour = null;
		String hourStr = tuesdayHoursOpt.getValue();
		try{
			if(StringUtils.isEmpty(hourStr))
				return null;
			hour = Double.parseDouble(hourStr);
		}catch (NumberFormatException e) {}
		
		return hour;
	}
	
	public Double getWednesdayHours(){
		Double hour = null;
		String hourStr = wednesdayHoursOpt.getValue();
		try{
			if(StringUtils.isEmpty(hourStr))
				return null;
			hour = Double.parseDouble(hourStr);
		}catch (NumberFormatException e) {}
		
		return hour;
	}
	
	public Double getThursdayHours(){
		Double hour = null;
		String hourStr = thursdayHoursOpt.getValue();
		try{
			if(StringUtils.isEmpty(hourStr))
				return null;
			hour = Double.parseDouble(hourStr);
		}catch (NumberFormatException e) {}
		
		return hour;
	}
	
	public Double getFridayHours(){
		Double hour = null;
		String hourStr = fridayHoursOpt.getValue();
		try{
			if(StringUtils.isEmpty(hourStr))
				return null;
			hour = Double.parseDouble(hourStr);
		}catch (NumberFormatException e) {}
		
		return hour;
	}
	
	public Double getSaturdayHours(){
		Double hour = null;
		String hourStr = saturdayHoursOpt.getValue();
		try{
			if(StringUtils.isEmpty(hourStr))
				return null;
			hour = Double.parseDouble(hourStr);
		}catch (NumberFormatException e) {}
		
		return hour;
	}
	
	public Double getSundayHours(){
		Double hour = null;
		String hourStr = sundayHoursOpt.getValue();
		try{
			if(StringUtils.isEmpty(hourStr))
				return null;
			hour = Double.parseDouble(hourStr);
		}catch (NumberFormatException e) {}
		
		return hour;
	}
	
}
