package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map.Entry;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.payroll.shared.ContractJourneyDuration;
import com.esferalia.aon.gwt.payroll.shared.JourneyDuration;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class ContractJourneyDialog extends CustomDialog {

	interface Binder extends UiBinder<Widget, ContractJourneyDialog> {

	}

	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	MyStyle style;
	
	@UiField
	Label newJourney;
	
	@UiField
	Label saveJourney;
	
	@UiField
	VerticalPanel newJourneyTable;
	
	@UiField
	DateBoxEx startDatePeriod;
	
	@UiField
	TextBox newMonday;
	
	@UiField
	TextBox newTuesday;
	
	@UiField
	TextBox newWednesday;
	
	@UiField
	TextBox newThursday;
	
	@UiField
	TextBox newFriday;
	
	@UiField
	TextBox newSaturday;
	
	@UiField
	TextBox newSunday;
	
	@UiField
	Button acceptJourney;
	
	@UiField
	Grid journeyTable;

	interface MyStyle extends CssResource {
		String bold();
		String widthTB();
		String hide();
		String backgroudGrey();
	}
	
	@UiHandler("newJourney")
	void onNewJourneyCleck(ClickEvent event) {
		newJourneyBox();
	}
	
	@UiHandler("startDatePeriod")
	void onStartDateChanged(ValueChangeEvent<Date> event) {
		if(null == startDatePeriod.getValue() || contractJourneyDuration.isOverlapDate(startDatePeriod.getValue())) {
			WarningDialog warning = new WarningDialog("AVISO", "La fecha seleccionada solapa con algun tramo ya creado.");
			warning.center();
			warning.show();
			startDatePeriod.setValue(null);
		}else {
			contractJourneyDuration.setEndDatePreviusPeriod(startDatePeriod.getValue());
		}
	}
	
	@UiHandler("acceptJourney")
	void onAcceptJourneyClick(ClickEvent event) {
		if(null == startDatePeriod.getValue()) {
			WarningDialog warning = new WarningDialog("AVISO", "La fecha seleccionada no puede estar vacia.");
			warning.center();
			warning.show();
			startDatePeriod.setValue(null);
		}else {
			Date startDate = startDatePeriod.getValue();
			Date endDate = null;
			ArrayList<JourneyDuration> journies = new ArrayList<>();
			
			//HORAS_LUNES
			JourneyDuration monday = new JourneyDuration();
			monday.setStartDate(startDate);
			monday.setEndDate(endDate);
			monday.setName("HORAS_LUNES");
			monday.setExpression((null == newMonday.getValue() || "" == newMonday.getValue()) ? "0" : newMonday.getValue());
			
			//HORAS_MARTES
			JourneyDuration tuesday = new JourneyDuration();
			tuesday.setStartDate(startDate);
			tuesday.setEndDate(endDate);
			tuesday.setName("HORAS_MARTES");
			tuesday.setExpression((null == newTuesday.getValue() || "" == newTuesday.getValue()) ? "0" : newTuesday.getValue());
			
			//HORAS_MIERCOLES
			JourneyDuration wednesday = new JourneyDuration();
			wednesday.setStartDate(startDate);
			wednesday.setEndDate(endDate);
			wednesday.setName("HORAS_MIERCOLES");
			wednesday.setExpression((null == newWednesday.getValue() || "" == newWednesday.getValue()) ? "0" : newWednesday.getValue());
			
			//HORAS_JUEVES
			JourneyDuration thursday = new JourneyDuration();
			thursday.setStartDate(startDate);
			thursday.setEndDate(endDate);
			thursday.setName("HORAS_JUEVES");
			thursday.setExpression((null == newThursday.getValue() || "" == newThursday.getValue()) ? "0" : newThursday.getValue());
			
			//HORAS_VIERNES
			JourneyDuration friday = new JourneyDuration();
			friday.setStartDate(startDate);
			friday.setEndDate(endDate);
			friday.setName("HORAS_VIERNES");
			friday.setExpression((null == newFriday.getValue() || "" == newFriday.getValue()) ? "0" : newFriday.getValue());
			
			//HORAS_SABADO
			JourneyDuration saturday = new JourneyDuration();
			saturday.setStartDate(startDate);
			saturday.setEndDate(endDate);
			saturday.setName("HORAS_SABADO");
			saturday.setExpression((null == newSaturday.getValue() || "" == newSaturday.getValue()) ? "0" : newSaturday.getValue());
			
			//HORAS_DOMINGO
			JourneyDuration sunday = new JourneyDuration();
			sunday.setStartDate(startDate);
			sunday.setEndDate(endDate);
			sunday.setName("HORAS_DOMINGO");
			sunday.setExpression((null == newSunday.getValue() || "" == newSunday.getValue()) ? "0" : newSunday.getValue());
			
			journies.add(monday);
			journies.add(tuesday);
			journies.add(wednesday);
			journies.add(thursday);
			journies.add(friday);
			journies.add(saturday);
			journies.add(sunday);
			
			contractJourneyDuration.setContractJourneyDuration(startDate, journies);
			newJourneyTable.addStyleName(style.hide());
			clearTable();
			paintTable();
		}
	}

	private ContractJourneyDuration contractJourneyDuration;
	private Date contractStartDate;
	private Date contractEndDate;
	
	public ContractJourneyDialog(Date contractStartDate, Date contractEndDate, ContractJourneyDuration contractJourneyDuration) {
		setCaption("DURACION DE LA JORNADA");
		
		setWidget(binder.createAndBindUi(this));
		
		this.contractStartDate = contractStartDate;
		this.contractEndDate = contractEndDate;
		this.contractJourneyDuration = contractJourneyDuration;
		
		newJourneyTable.addStyleName(style.hide());
		journeyTable.getRowFormatter().addStyleName(0, style.backgroudGrey());
		clearTable();
		paintTable();
		
		this.saveJourney.addClickHandler(new ClickHandler() {	
			@Override
			public void onClick(ClickEvent event) {
				hide();
				onSave();
			}
		});
	}
	
	private void clearTable() {
		for(int i = journeyTable.getRowCount()-1; i > 0; i--)
			journeyTable.removeRow(i);
	}
	
	public void newJourneyBox() {
		if(contractJourneyDuration.getJourniesSize() == 0) {
			startDatePeriod.setValue(contractStartDate);
			startDatePeriod.setEnabled(false);
		}else {
			startDatePeriod.setEnabled(true);
			startDatePeriod.setValue(null);
		}
		newMonday.setValue(null);
		newTuesday.setValue(null);
		newWednesday.setValue(null);
		newThursday.setValue(null);
		newFriday.setValue(null);
		newSaturday.setValue(null);
		newSunday.setValue(null);
		newJourneyTable.removeStyleName(style.hide());
	}
	
	private void paintTable() {
		if(contractJourneyDuration.getJourniesSize() == 0) {
			journeyTable.addStyleName(style.hide());
			newJourneyBox();
		}else {
			journeyTable.removeStyleName(style.hide());
		
			for(Entry<Date, ArrayList<JourneyDuration>> entry : contractJourneyDuration.getContractJourneyDuration().descendingMap().entrySet()) {
				int newRow = journeyTable.insertRow(journeyTable.getRowCount());
				Label startDate = new Label(formatDate(entry.getKey()));
				Label endDate = new Label();
				HorizontalPanel hPanel = new HorizontalPanel();;
				Label monday = new Label("L");
				monday.addStyleName(style.bold());
				TextBox mondayTB = new TextBox();
				mondayTB.setStyleName("aon-inputText", true);
				mondayTB.addStyleName(style.widthTB());
				Label tuesday = new Label("M");
				tuesday.addStyleName(style.bold());
				TextBox tuesdayTB = new TextBox();
				tuesdayTB.setStyleName("aon-inputText", true);
				tuesdayTB.addStyleName(style.widthTB());
				Label wednesday = new Label("X");
				wednesday.addStyleName(style.bold());
				TextBox wednesdayTB = new TextBox();
				wednesdayTB.setStyleName("aon-inputText", true);
				wednesdayTB.addStyleName(style.widthTB());
				Label thursday = new Label("J");
				thursday.addStyleName(style.bold());
				TextBox thursdayTB = new TextBox();
				thursdayTB.setStyleName("aon-inputText", true);
				thursdayTB.addStyleName(style.widthTB());
				Label friday = new Label("V");
				friday.addStyleName(style.bold());
				TextBox fridayTB = new TextBox();
				fridayTB.setStyleName("aon-inputText", true);
				fridayTB.addStyleName(style.widthTB());
				Label saturday = new Label("S");
				saturday.addStyleName(style.bold());
				TextBox saturdayTB = new TextBox();
				saturdayTB.setStyleName("aon-inputText", true);
				saturdayTB.addStyleName(style.widthTB());
				Label sunday = new Label("D");
				sunday.addStyleName(style.bold());
				TextBox sundayTB = new TextBox();
				sundayTB.setStyleName("aon-inputText", true);
				sundayTB.addStyleName(style.widthTB());
				
				for(JourneyDuration journey : entry.getValue()) {
					if(null != journey.getEndDate()) endDate.setText(formatDate(journey.getEndDate()));
					if("HORAS_LUNES" == journey.getName()) mondayTB.setValue(journey.getExpression());
					if("HORAS_MARTES" == journey.getName()) tuesdayTB.setValue(journey.getExpression());
					if("HORAS_MIERCOLES" == journey.getName()) wednesdayTB.setValue(journey.getExpression());
					if("HORAS_JUEVES" == journey.getName()) thursdayTB.setValue(journey.getExpression());
					if("HORAS_VIERNES" == journey.getName()) fridayTB.setValue(journey.getExpression());
					if("HORAS_SABADO" == journey.getName()) saturdayTB.setValue(journey.getExpression());
					if("HORAS_DOMINGO" == journey.getName()) sundayTB.setValue(journey.getExpression());
				}
				
				hPanel.add(monday);
				hPanel.add(mondayTB);
				hPanel.add(tuesday);
				hPanel.add(tuesdayTB);
				hPanel.add(wednesday);
				hPanel.add(wednesdayTB);
				hPanel.add(thursday);
				hPanel.add(thursdayTB);
				hPanel.add(friday);
				hPanel.add(fridayTB);
				hPanel.add(saturday);
				hPanel.add(saturdayTB);
				hPanel.add(sunday);
				hPanel.add(sundayTB);
				
				HorizontalPanel hPanelButtons = new HorizontalPanel();
				Button deleteButton = new Button();
				deleteButton.setStyleName("aon-editDataTable-button aon-icon-delete");
				deleteButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						contractJourneyDuration.delete(parseStringToDate(startDate.getText()));
						contractJourneyDuration.setEndDatePreviusPeriod(null);
						clearTable();
						paintTable();
					}
				});
				
				hPanelButtons.add(deleteButton);
				hPanelButtons.addStyleName(style.hide());
				
				journeyTable.setWidget(newRow, 0, startDate);
				journeyTable.setWidget(newRow, 1, endDate);
				journeyTable.setWidget(newRow, 2, hPanel);
				journeyTable.setWidget(newRow, 3, hPanelButtons);
			}
		
			journeyTable.getWidget(1, 3).removeStyleName(style.hide());
		
		}
	}
	
	private Date parseStringToDate(String dateText) {
		String dayStr = dateText.split("/")[0];
		String monthStr = dateText.split("/")[1];
		String yearStr = dateText.split("/")[2];
		
		Integer day = Integer.parseInt(dayStr);
		Integer month = Integer.parseInt(monthStr) -1 ;
		Integer year = Integer.parseInt(yearStr) - 1900;
		
		return new Date(year, month, day);
	}
	

	private String formatDate(Date date) {
		return date.getDate() + "/" + (date.getMonth()+1) + "/" + (date.getYear()+1900);
	}
	
	public ContractJourneyDuration getContractJourneyDuration() {
		return this.contractJourneyDuration;
	}

	protected abstract void onSave();

}
