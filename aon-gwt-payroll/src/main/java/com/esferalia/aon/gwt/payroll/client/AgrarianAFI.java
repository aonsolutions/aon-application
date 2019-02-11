package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.AgrarianJourney;
import com.esferalia.aon.gwt.payroll.shared.CCC;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class AgrarianAFI extends MainEntryPoint {
	
	interface Binder extends UiBinder<Widget, AgrarianAFI> {
	}
	
	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String bold();
		String hide();
		String paddingDays();
		String widthDays();
		String widthName();
		String textCenter();
		String widthFirstColumn();
		String paddingText();
	}
	
	@UiField
	TableElement dataTable;
	
	@UiField
	Label enterprise;
	
	@UiField
	ListBox cccs;
	
	@UiField
	ListBox monthList;
	
	@UiField
	ListBox yearList;
	
	@UiField
	Button searchAgrarian;
	
	@UiField
	VerticalPanel employeePanel;
	
	@UiField
	Grid employeeTable;
	
	private AgrarianAFIObject agrarianAFIObject;
	
	@Override
	public void onModuleLoad() {
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
		AON.ensureInjected();
	
		Widget ui = binder.createAndBindUi(this);
		RootLayoutPanel.get("rootPanel").add(ui);
		
		//Add Months
		monthList.addItem("Enero");
		monthList.addItem("Febrero");
		monthList.addItem("Marzo");
		monthList.addItem("Abril");
		monthList.addItem("Mayo");
		monthList.addItem("Junio");
		monthList.addItem("Julio");
		monthList.addItem("Agosto");
		monthList.addItem("Septiembre");
		monthList.addItem("Octubre");
		monthList.addItem("Noviembre");
		monthList.addItem("Diciembre");
		
		//Add Years
		Date currentDate = new Date();
		Integer currentYear = currentDate.getYear();
		Integer currentParseYear = currentYear + 1900;
		Integer previusYear = currentParseYear -1;
		
		yearList.addItem(currentParseYear+"");
		yearList.addItem(previusYear+"");
		
		//Style Data Table
		dataTable.getStyle().setMargin(5, Unit.PX);
		
		//Hide EmployeePanel
		employeePanel.addStyleName(style.hide());
				
	}

	private HorizontalPanel createMonthPanel() {
		HorizontalPanel hPanel = new HorizontalPanel();
		Integer maxDays = DateUtils.getLastDayOfMonth(new Date(Integer.parseInt(yearList.getSelectedItemText())-1900, monthList.getSelectedIndex(), 1)).getDate();
		for(int i = 0; i < maxDays; i++) {
			Label day = new Label((i+1)+"");
			day.addStyleName(style.widthDays());
			day.addStyleName(style.paddingDays());
			day.addStyleName(style.bold());
			day.addStyleName(style.textCenter());
			hPanel.add(day);
		}
		return hPanel;
	}
	
	private Label createTotalDays(Integer contractId) {
		Label newLabel = new Label();
		Integer totalDays = agrarianAFIObject.getTotalDaysByContract(contractId);
		newLabel.setText(totalDays+"");
		return newLabel;
	}
	
	private HorizontalPanel createAgrarianMonthPanel(Integer contractId) {
		HorizontalPanel hPanel = new HorizontalPanel();
		Integer maxDays = DateUtils.getLastDayOfMonth(new Date(Integer.parseInt(yearList.getSelectedItemText())-1900, monthList.getSelectedIndex(), 1)).getDate();
		for(int i = 0; i < maxDays; i++) {
			Label day = new Label();
			if(agrarianAFIObject.checkDateAgraria(contractId, new Date(Integer.parseInt(yearList.getSelectedItemText())-1900, monthList.getSelectedIndex(), i+1)))
				day.setText("S");
			else
				day.setText("-");
			
			day.addStyleName(style.widthDays());
			day.addStyleName(style.paddingDays());
			day.addStyleName(style.textCenter());
			hPanel.add(day);
		}
		return hPanel;
	}
	
	public void setAgrarianAFIDialogObject() {

	}

	// ------------------------------------------------------------------------
	//						Initialize Logic Window
	// ------------------------------------------------------------------------
	
	

	// ------------------------------------------------------------------------
	//							UiHandler Accept/Cancel
	// ------------------------------------------------------------------------
	
	@UiHandler("searchAgrarian")
	void onSearchAgrariantButtonClick(ClickEvent clickEvent) {
		//CheckDate for searching
		if(checkDate()){
			//Set Dates to find
			Integer selectedMonth = this.monthList.getSelectedIndex();
			Integer selectedYear = Integer.parseInt(this.yearList.getSelectedItemText()) - 1900;
			Date selectedDate = new Date(selectedYear, selectedMonth, 1);
			setFindingDates(selectedDate);
			
			//Set CCC to find
			String selectedCCC = this.cccs.getSelectedItemText().split("- ")[1];
			setFindingCCC(selectedCCC);
			
			//Get Journies
			agrarianAFIObject.getAgrarianJourney(
					s -> {
						initializeTableJourney();
					}, f->{});
			
		}else{
			WarningDialog warning = new WarningDialog("Error", "No se puede generar el fichero AFI para el mismo mes o posteriores.");
			warning.center();
			warning.show();
		}
	}
	
	private void initializeTableJourney() {
		if(agrarianAFIObject.getAgrarianJourney().entrySet().size() == 0){
			WarningDialog warning = new WarningDialog("Aviso", "No hay contratos con peonadas para estas fechas.");
			warning.center();
			warning.show();
		}else{
			initializeHeader();
			for(Entry<Integer, List<AgrarianJourney>> entry : agrarianAFIObject.getAgrarianJourney().entrySet()){
				//Fill Practice Row
				Integer newRow = employeeTable.insertRow(employeeTable.getRowCount());
				CheckBox select = new CheckBox();
				employeeTable.setWidget(newRow, 0, select);
				Label employeeName = new Label(entry.getValue().get(0).getSurname() + ", " + entry.getValue().get(0).getName());
				employeeName.addStyleName(style.widthName());
				employeeTable.setWidget(newRow, 1, employeeName);
				Label totalDays = createTotalDays(entry.getKey());
				totalDays.addStyleName(style.textCenter());
				employeeTable.setWidget(newRow, 2, totalDays);
				HorizontalPanel agrarianMonth = createAgrarianMonthPanel(entry.getKey());
				employeeTable.setWidget(newRow, 3, agrarianMonth);
			}
		}	
	}

	private void initializeHeader() {
		//Remove hide
		employeePanel.removeStyleName(style.hide());
		
		//Header Grid
		employeeTable.resize(0,4);
		Integer newRow = employeeTable.insertRow(employeeTable.getRowCount());
		CheckBox selectAll = new CheckBox();
		selectAll.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(selectAll.isChecked()){
					Integer rows = employeeTable.getRowCount();
					for(int i=1; i<rows; i++){
						CheckBox checkBox = (CheckBox) employeeTable.getWidget(i, 0);
						checkBox.setChecked(true);
					}
				}else{
					Integer rows = employeeTable.getRowCount();
					for(int i=1; i<rows; i++){
						CheckBox checkBox = (CheckBox) employeeTable.getWidget(i, 0);
						checkBox.setChecked(false);
					}
				}
			}
		});
		employeeTable.setWidget(newRow, 0, selectAll);
		Label employeeLabel = new Label("Empleado");
		employeeLabel.addStyleName(style.bold());
		employeeTable.setWidget(newRow, 1, employeeLabel);
		employeeTable.getWidget(newRow, 1).addStyleName(style.widthFirstColumn());
		Label totalDaysLabel = new Label("D"+String.valueOf("\u00ED")+"as");
		totalDaysLabel.addStyleName(style.bold());
		employeeTable.setWidget(newRow, 2, totalDaysLabel);
		HorizontalPanel month = createMonthPanel();
		employeeTable.setWidget(newRow, 3, month);
	}

	private void setFindingCCC(String selectedCCC) {
		agrarianAFIObject.setFindingCCC(selectedCCC);
	}

	private void setFindingDates(Date selectedDate) {
		Date startDate = selectedDate;
		Date endDate = DateUtils.getLastDayOfMonth(selectedDate);
		
		agrarianAFIObject.setFindingDates(startDate, endDate);
	}

	// ------------------------------------------------------------------------
	//
	// ------------------------------------------------------------------------
	
	public void setAgrariaAFIObject(AgrarianAFIObject agrarianAFIObject) {
		this.agrarianAFIObject = agrarianAFIObject;
		
		this.agrarianAFIObject.getEnterprises(
				s -> {
					//Enterprise Name
					enterprise.setText(this.agrarianAFIObject.getEnterpriseName());
					enterprise.addStyleName(style.bold());
					enterprise.addStyleName(style.paddingText());
					
					if(this.agrarianAFIObject.getAgrarianCCCs().isEmpty()){
						this.cccs.setEnabled(false);
						this.monthList.setEnabled(false);
						this.yearList.setEnabled(false);
						this.searchAgrarian.setEnabled(false);
						WarningDialog warning = new WarningDialog("Aviso", "No existe ninguna cuenta de cotizaci"+String.valueOf("\u00F3")+"n de tipo agrario.");
						warning.center();
						warning.show();
					}else{
						for(Entry<String, CCC> entry : this.agrarianAFIObject.getAgrarianCCCs().entrySet()){
							this.cccs.addItem(entry.getKey() + " - " + entry.getValue().getCode());
						}
					}
				}, 
				f -> {}
		);	
	}
	
	private boolean checkDate() {
		Date currentDate = new Date(new Date().getYear(), new Date().getMonth(), 1);
		
		Integer selectedMonth = this.monthList.getSelectedIndex();
		Integer selectedYear = Integer.parseInt(this.yearList.getSelectedItemText()) - 1900;
		Date selectedDate = new Date(selectedYear, selectedMonth, 1);
		
		if (currentDate.after(selectedDate))
			return true;
		else
			return false;
	}

}
