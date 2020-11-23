package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map.Entry;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.payroll.shared.ContractJourneyDuration;
import com.esferalia.aon.gwt.payroll.shared.JourneyDuration;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class ContractJourneyDialog extends AonCustomDialog {

	interface Binder extends UiBinder<Widget, ContractJourneyDialog> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	MyStyle style;
	
	@UiField
	HTMLPanel north;
	
	@UiField
	DeckPanel mainDeckPanel;
	
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
	Grid contractJourneyDataTableHeader;
	
	@UiField
	ScrollPanel contractJourneyScrollPanel;
	
	@UiField
	DeckPanel deckPanel;
	
	@UiField
	Grid contractJourneyDataTable;
	
	@UiField
	HTMLPanel buttonsPanel;

	interface MyStyle extends CssResource {
		String bold();
		String widthTB();
		String hide();
		String headerLabelStyle();
		String backgroudGrey();
		String minWindth100();
	}

	private ContractJourneyDuration contractJourneyDuration;
	private Date contractStartDate;
	private Date contractEndDate;
	
	private Button closeBtnDialog;
	private Button acceptBtnDialog;
	
	private AonToolbar toolbar;
	private AonToolbarButton listIT;
	private AonToolbarButton backListIT;
	
	private DateTimeFormat formatFullDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	public ContractJourneyDialog(Date contractStartDate, Date contractEndDate, ContractJourneyDuration contractJourneyDuration) {
		setCaption("DURACION DE LA JORNADA");
		
		setWidget(binder.createAndBindUi(this));
		
		this.contractStartDate = contractStartDate;
		this.contractEndDate = contractEndDate;
		this.contractJourneyDuration = contractJourneyDuration;
		
//		this.setWidth("445px");
		
		mainDeckPanel.showWidget(0);
//		mainDeckPanel.setWidth("425px");
		deckPanel.showWidget(0);
//		deckPanel.setWidth("400px");
		
		getButtonsPanel();
		
		toolbar = getToolbarPanel();
		north.add(toolbar);
		north.setHeight("50px");
		
		showListJourney();
		initializeView();
		paintTable();
	}
	
	public ContractJourneyDialog(Date contractStartDate, Date contractEndDate) {
		setCaption("DURACION DE LA JORNADA");
		
		setWidget(binder.createAndBindUi(this));
		
		this.contractStartDate = contractStartDate;
		this.contractEndDate = contractEndDate;
		this.contractJourneyDuration = new ContractJourneyDuration();
		
		mainDeckPanel.showWidget(0);
//		mainDeckPanel.setWidth("425px");
		deckPanel.showWidget(0);
//		deckPanel.setWidth("400px");
		
		getButtonsPanel();
		
		toolbar = getToolbarPanel();
		north.add(toolbar);
		north.setHeight("50px");
		
		showListJourney();
		initializeView();
		paintTable();
	}
	
	private void initializeView() {
		initAttachmentsTable();
		paintHeaderContractBonusTable();
		
		setScrollPanelsHeight();
		setColumnsWidth();
	}
	
	private void initAttachmentsTable() {
		contractJourneyDataTableHeader.clear();
		contractJourneyDataTableHeader.resize(0, 0);
		contractJourneyDataTableHeader.resizeColumns(4);
		contractJourneyDataTable.clear();
		contractJourneyDataTable.resize(0, 0);
		contractJourneyDataTable.resizeColumns(4);
	}
	
	private void paintHeaderContractBonusTable() {
		int row = contractJourneyDataTableHeader.insertRow(contractJourneyDataTableHeader.getRowCount());
		
		Label startDate = new Label("F. INICIO");
		Label endDate = new Label("F. FIN");
		Label description = new Label("DESCRIPCI" + String.valueOf("\u00D3") + "N");
		Label blank = new Label("");
		
		description.addStyleName(style.headerLabelStyle());
		startDate.addStyleName(style.headerLabelStyle());
		endDate.addStyleName(style.headerLabelStyle());
		startDate.getElement().getStyle().setPaddingLeft(10, Unit.PX);
		
		contractJourneyDataTableHeader.setWidget(row, 0, startDate);
		contractJourneyDataTableHeader.setWidget(row, 1, endDate);
		contractJourneyDataTableHeader.setWidget(row, 2, description);
		contractJourneyDataTableHeader.setWidget(row, 3, blank);
	}
	
	private void setScrollPanelsHeight() {
		contractJourneyScrollPanel.setHeight(100 + "px");
	}
	
	private void setColumnsWidth() {
		contractJourneyDataTableHeader.getCellFormatter().getElement(0, 0).getStyle().setWidth(100, Unit.PX);
		contractJourneyDataTableHeader.getCellFormatter().getElement(0, 1).getStyle().setWidth(100, Unit.PX);
		contractJourneyDataTableHeader.getCellFormatter().getElement(0, 2).getStyle().setWidth(250, Unit.PX);
		contractJourneyDataTableHeader.getCellFormatter().getElement(0, 3).getStyle().setWidth(50, Unit.PX);
		
		contractJourneyDataTable.getColumnFormatter().addStyleName(0, style.minWindth100());
		contractJourneyDataTable.getColumnFormatter().addStyleName(1, style.minWindth100());
//		contractJourneyDataTable.getColumnFormatter().getElement(0).getStyle().setWidth(100, Unit.PX);
//		contractJourneyDataTable.getColumnFormatter().getElement(1).getStyle().setWidth(100, Unit.PX);
		contractJourneyDataTable.getColumnFormatter().getElement(2).getStyle().setWidth(250, Unit.PX);
		contractJourneyDataTable.getColumnFormatter().getElement(3).getStyle().setWidth(50, Unit.PX);
	}
	
	// ---------------------------------------------------- UI FIELD
	
	@UiHandler("startDatePeriod")
	void onStartDateChanged(ValueChangeEvent<Date> event) {
		if(null == startDatePeriod.getValue() || contractJourneyDuration.isOverlapDate(startDatePeriod.getValue())) {
			AonConfirmDialog dialog = new AonConfirmDialog();
			dialog.info("AVISO: Fecha solapada", "La fecha seleccionada solapa con algun tramo ya creado.");
			startDatePeriod.setValue(null);
		}else {
			contractJourneyDuration.setEndDatePreviusPeriod(startDatePeriod.getValue());
		}
	}
	
	@UiHandler("acceptJourney")
	void onAcceptJourneyClick(ClickEvent event) {
		if(null == startDatePeriod.getValue()) {
			AonConfirmDialog dialog = new AonConfirmDialog();
			dialog.info("AVISO: Fecha vacia", "La fecha seleccionada no puede estar vacia.");
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
			monday.setExpression((null == newMonday.getValue() || "" == newMonday.getValue()) ? null : newMonday.getValue());
			
			//HORAS_MARTES
			JourneyDuration tuesday = new JourneyDuration();
			tuesday.setStartDate(startDate);
			tuesday.setEndDate(endDate);
			tuesday.setName("HORAS_MARTES");
			tuesday.setExpression((null == newTuesday.getValue() || "" == newTuesday.getValue()) ? null : newTuesday.getValue());
			
			//HORAS_MIERCOLES
			JourneyDuration wednesday = new JourneyDuration();
			wednesday.setStartDate(startDate);
			wednesday.setEndDate(endDate);
			wednesday.setName("HORAS_MIERCOLES");
			wednesday.setExpression((null == newWednesday.getValue() || "" == newWednesday.getValue()) ? null : newWednesday.getValue());
			
			//HORAS_JUEVES
			JourneyDuration thursday = new JourneyDuration();
			thursday.setStartDate(startDate);
			thursday.setEndDate(endDate);
			thursday.setName("HORAS_JUEVES");
			thursday.setExpression((null == newThursday.getValue() || "" == newThursday.getValue()) ? null : newThursday.getValue());
			
			//HORAS_VIERNES
			JourneyDuration friday = new JourneyDuration();
			friday.setStartDate(startDate);
			friday.setEndDate(endDate);
			friday.setName("HORAS_VIERNES");
			friday.setExpression((null == newFriday.getValue() || "" == newFriday.getValue()) ? null : newFriday.getValue());
			
			//HORAS_SABADO
			JourneyDuration saturday = new JourneyDuration();
			saturday.setStartDate(startDate);
			saturday.setEndDate(endDate);
			saturday.setName("HORAS_SABADO");
			saturday.setExpression((null == newSaturday.getValue() || "" == newSaturday.getValue()) ? null : newSaturday.getValue());
			
			//HORAS_DOMINGO
			JourneyDuration sunday = new JourneyDuration();
			sunday.setStartDate(startDate);
			sunday.setEndDate(endDate);
			sunday.setName("HORAS_DOMINGO");
			sunday.setExpression((null == newSunday.getValue() || "" == newSunday.getValue()) ? null : newSunday.getValue());
			
			journies.add(monday);
			journies.add(tuesday);
			journies.add(wednesday);
			journies.add(thursday);
			journies.add(friday);
			journies.add(saturday);
			journies.add(sunday);
			
			contractJourneyDuration.setContractJourneyDuration(startDate, journies);
			
			showListJourney();
			resetContractJourneyDataTableStructure();
			paintTable();
		}
	}
	
	// ---------------------------------------------------- AUX METHDOS
	
	private void resetContractJourneyDataTableStructure() {
		contractJourneyDataTable.clear();
		contractJourneyDataTable.resize(0, 0);
		contractJourneyDataTable.resizeColumns(4);
		
		setColumnsWidth();
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
		showNewJourney();
	}
	
	private void paintTable() {
		if(contractJourneyDuration.getJourniesSize() == 0) {
			newJourneyBox();
			deckPanel.showWidget(0);
		}else {
			showListJourney();
			deckPanel.showWidget(1);
			for(Entry<Date, ArrayList<JourneyDuration>> entry : contractJourneyDuration.getContractJourneyDuration().descendingMap().entrySet()) {
				int newRow = contractJourneyDataTable.insertRow(contractJourneyDataTable.getRowCount());
				Label startDate = new Label(formatFullDate.format(entry.getKey()));
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
					if(null != journey.getEndDate()) endDate.setText(formatFullDate.format(journey.getEndDate()));
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
						contractJourneyDuration.delete(formatFullDate.parse(startDate.getText()));
						contractJourneyDuration.setEndDatePreviusPeriod(null);
						resetContractJourneyDataTableStructure();
						paintTable();
					}
				});
				
				hPanelButtons.add(deleteButton);
				
				contractJourneyDataTable.setWidget(newRow, 0, startDate);
				contractJourneyDataTable.setWidget(newRow, 1, endDate);
				contractJourneyDataTable.setWidget(newRow, 2, hPanel);
				contractJourneyDataTable.setWidget(newRow, 3, hPanelButtons);
			}
		}
	}
	
	public ContractJourneyDuration getContractJourneyDuration() {
		return this.contractJourneyDuration;
	}

	protected abstract void onSave();
	
	private void getButtonsPanel() {
		closeBtnDialog = new Button();
		closeBtnDialog.setStyleName(AON.CSS.aonCancelButtonSmall());
		closeBtnDialog.setText( AON.MSG.cancelAction());
		closeBtnDialog.setAccessKey('C');
		closeBtnDialog.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onCloseDialog(event);
			}
		});
		
		closeBtnDialog.getElement().getStyle().setMarginRight(10, Unit.PX);
		
		buttonsPanel.add(closeBtnDialog);
		
		acceptBtnDialog = new Button();
		acceptBtnDialog.setStyleName(AON.CSS.aonOkButtonSmall());
		acceptBtnDialog.setText( AON.MSG.accept());
		acceptBtnDialog.setAccessKey('A');
		acceptBtnDialog.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onAcceptDialog(event);
			}
		});
		
		buttonsPanel.add(acceptBtnDialog);
	}
	
	private void onCloseDialog(ClickEvent event) {
		hide();
	}
	
	private void onAcceptDialog(ClickEvent event) {
		hide();
		onSave();
	}
	
	private void showNewJourney() {
		mainDeckPanel.showWidget(0);
		this.setWidth("425px");
		listIT.setVisible(true);
		backListIT.setVisible(false);
	}
	
	private void showListJourney() {
		mainDeckPanel.showWidget(1);
		this.setWidth("625px");
		backListIT.setVisible(true);
		listIT.setVisible(false);
	}
	
	private AonToolbar getToolbarPanel() {
		AonToolbar toolbar = new AonToolbar("");
		
		listIT = new AonToolbarButton( "Listar Jornadas", AON.CSS.aonIconAdd() );
		listIT.setAccessKey('L');
		listIT.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onListIT(event);
			}
		});
		toolbar.add(listIT);
		
		backListIT = new AonToolbarButton( "Volver", AON.CSS.aonIconBack() );
		backListIT.setAccessKey('B');
		backListIT.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onBackListIT(event);
			}
		});
		toolbar.add(backListIT);

		return toolbar;

	}
	
	private void onListIT(ClickEvent event) {
		showListJourney();
	}
	
	private void onBackListIT(ClickEvent event) {
		showNewJourney();
	}

}
