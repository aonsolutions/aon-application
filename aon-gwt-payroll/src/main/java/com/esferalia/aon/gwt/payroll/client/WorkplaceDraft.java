package com.esferalia.aon.gwt.payroll.client;

import java.util.List;
import java.util.Map.Entry;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class WorkplaceDraft extends Composite implements ContextMenuHandler {

	// -------------------------------------------------- UiBinder --------------------------------------------------

	private static EmployeeDraftUiBinder uiBinder = GWT.create(EmployeeDraftUiBinder.class);

	interface EmployeeDraftUiBinder extends UiBinder<Widget, WorkplaceDraft> {
	}

	// -------------------------------------------------- UiFields --------------------------------------------------

	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String hide();
		String paddingEnableDisable();
		String maxWidth();
		String fontDisableStyle();
		String fontEnableStyle();
		String warningColor();
		String maxWidthTextBox();
		String borderNone();
	}
	
	@UiField
	Label saveStatus;
	
	@UiField
	HorizontalPanel enableWorkplacePanel;
	
	@UiField
	Button enableWorkplaceButton;
	
	@UiField
	HorizontalPanel disableWorkplacePanel;
	
	@UiField
	Button disableWorkplaceButton;

	// TABLA DATOS EMPLEADO

	@UiField
	TableElement generalDataTable;
	
	@UiField
	TextBox workplaceDescription;
	
	@UiField
	HorizontalPanel workplaceAddressPanel;
	
	@UiField
	ListBox workplaceEconomicConcert;

	// TABLA DATOS CONTRATO

	@UiField
	Label labelPayrollDataTable;

	@UiField
	TableElement payrollDataTable;
	
	@UiField
	HorizontalPanel workplaceCalendarPanel;

	@UiField
	ListBox workpalceAgreement;

	@UiField
	HorizontalPanel workplaceActivityPanel;

	// ------------------------------------------------------ VARIABLES DE LA CLASE --------------------------------------------------

	private WorkplaceDraftObject workplaceDraftObject;

	// ------------------------------------------------ CONSTRUCTOR ------------------------------------------------------

	public WorkplaceDraft() {
		
		// Inicializamos la vista del empleado
		initWidget(uiBinder.createAndBindUi(this));
		
	}

	// ------------------------------------------------- UiHandlers ------------------------------------------------------

	@UiHandler("enableWorkplaceButton")
	void onEnableWorkplaceClick(ClickEvent event) {
		workplaceDraftObject.setWorkplaceActive(true);
		save();
	}

	@UiHandler("disableWorkplaceButton")
	void onDisableWorkplaceClick(ClickEvent event) {
		workplaceDraftObject.setWorkplaceActive(false);
		save();
	}
	
	@UiHandler("workplaceDescription")
	void onWorkplaceDescriptionChangeValue(ChangeEvent event) {
		workplaceDraftObject.setWorkplaceDescription(workplaceDescription.getValue());
		save();
	}

	@UiHandler("workplaceEconomicConcert")
	void onWorkplaceEconomicConcertChangeValue(ChangeEvent event) {
		workplaceDraftObject.setWorkplaceEconomicConcert(workplaceEconomicConcert.getSelectedIndex() - 1);
		save();
	}
	
	@UiHandler("workpalceAgreement")
	void onContractAgreementChangeValue(ChangeEvent event) {
		Integer agreementId = workplaceDraftObject.getAgreementId(this.workpalceAgreement.getSelectedItemText());
		workplaceDraftObject.setWorkplaceAgreement(agreementId);
		save();
	}
	
	private void save() {
		saveStatus.setText("Guardando...");
		workplaceDraftObject.updateWorkplace(
				r -> { setWorkplaceDraftObject(workplaceDraftObject);
					   saveStatus.setText("Guardado");
					 }, 
				t -> {}
		);
	}

	// ------------------------------------------------------ METODOS DE LA CLASE --------------------------------------------------

	public void setWorkplaceDraftObject(WorkplaceDraftObject workplaceDraftObject) {
		this.workplaceDraftObject = workplaceDraftObject;
		this.workplaceDraftObject.initializeWorkplace(
				s -> { initializeView();}
				, f -> {}
		);
		
	}

	private void initializeView() {
		enableWorkplaceButton.addStyleName(AON.AON_ICON_DISABLE);
		disableWorkplaceButton.addStyleName(AON.AON_ICON_ENABLE);
		enableWorkplaceButton.addStyleName(style.paddingEnableDisable());
		disableWorkplaceButton.addStyleName(style.paddingEnableDisable());
		if(workplaceDraftObject.getWorkplaceInfo().isActive() == 1){
			enableWorkplacePanel.addStyleName(style.hide());
			disableWorkplacePanel.removeStyleName(style.hide());
			disableWorkplacePanel.addStyleName(style.fontDisableStyle());
		}else{
			enableWorkplacePanel.removeStyleName(style.hide());
			enableWorkplacePanel.addStyleName(style.fontEnableStyle());
			disableWorkplacePanel.addStyleName(style.hide());
		}
		
		resetElements();
		initializeListBox();
		fillWorkplaceInfo();
	}

	private void resetElements() {
		
		// Clear general elements
		this.workplaceDescription.setValue("");
		this.workplaceAddressPanel.clear();
		this.workplaceEconomicConcert.clear();

		// Clear payroll elements
		this.workplaceCalendarPanel.clear();
		this.workpalceAgreement.clear();
		this.workplaceActivityPanel.clear();
		
	}

	private void initializeListBox() {

		//DIRECCION
		initializeAddressCell();
		
		//CONCIERTO ECONOMICO
		this.workplaceEconomicConcert.addItem("-");
		this.workplaceEconomicConcert.addItem(String.valueOf("\u00C1")+"lava");
		this.workplaceEconomicConcert.addItem("Bizkaia");
		this.workplaceEconomicConcert.addItem("Gipuzkoa");
		this.workplaceEconomicConcert.addItem("Navarra");
		this.workplaceEconomicConcert.addItem("Territorio Com"+ String.valueOf("\u00FA") +"n");
		
		
		//CALENDARIO
		initializeCalendarCell();
		
		// CONVENIO
		this.workpalceAgreement.addItem("-");
		List<Agreement> agreements = workplaceDraftObject.getActiveAgreements();
		for (Agreement a : agreements) {
			this.workpalceAgreement.addItem(a.getDescription());
		}
		
		//ACTIVIDADES
		initializeActivityCell();

	}

	private void initializeAddressCell() {
		Widget workplaceAddressWidget;
		if(workplaceDraftObject.getWorkplaceAddresses().values().size() == 1){
			Integer addressId = workplaceDraftObject.getWorkplaceAddressId();
			workplaceAddressWidget = new Label((null == addressId) ? "" : workplaceDraftObject.getWorkplaceAddresses().get(addressId));
			workplaceAddressWidget.setStyleName("aon-inputText");
			workplaceAddressWidget.addStyleName(style.maxWidthTextBox());
			workplaceAddressPanel.add(workplaceAddressWidget);
		}else{
			workplaceAddressWidget = new ListBox();
			for(String address : workplaceDraftObject.getWorkplaceAddresses().values()){
				((ListBox) workplaceAddressWidget).addItem(address);
			}
			workplaceAddressWidget.setStyleName("aon-selectOneMenu");
			workplaceAddressWidget.addStyleName(style.maxWidth());
			((ListBox) workplaceAddressWidget).addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					String address = ((ListBox) workplaceAddressWidget).getSelectedItemText();
					Integer addressId = -1;
					for(Entry<Integer, String> entry : workplaceDraftObject.getWorkplaceAddresses().entrySet()){
						if(address == entry.getValue()){
							addressId = entry.getKey();
							break;
						}
					}
					workplaceDraftObject.setWorkplaceAddress(addressId);
					save();
				}
			});
			workplaceAddressPanel.add(workplaceAddressWidget);
			if(((ListBox) workplaceAddressWidget).getItemCount() != 0){
				((ListBox) workplaceAddressWidget).setSelectedIndex(workplaceDraftObject.getWorkplaceAddressIndex());
			}
		}
	}
	
	private void initializeCalendarCell() {
		Widget workplaceCalendarWidget;
		if(workplaceDraftObject.getWorkplacesCalendars().values().size() == 0){
			workplaceCalendarWidget = new Label("No hay calendarios disponibles");
			workplaceCalendarWidget.setStyleName("aon-inputText");
			workplaceCalendarWidget.addStyleName(style.maxWidthTextBox());
			workplaceCalendarWidget.addStyleName(style.warningColor());
			workplaceCalendarWidget.addStyleName(style.borderNone());
			workplaceCalendarPanel.add(workplaceCalendarWidget);
		}else if(workplaceDraftObject.getWorkplacesCalendars().values().size() == 1){
			Integer calendarId = workplaceDraftObject.getWorkplaceInfo().getCalendarId();
			workplaceCalendarWidget = new Label(workplaceDraftObject.getWorkplacesCalendars().get(calendarId));
			workplaceCalendarWidget.setStyleName("aon-inputText");
			workplaceCalendarWidget.addStyleName(style.maxWidthTextBox());
			workplaceCalendarPanel.add(workplaceCalendarWidget);
		}else{
			workplaceCalendarWidget = new ListBox();
			for(String calendar : workplaceDraftObject.getWorkplacesCalendars().values()){
				if(null != calendar)
					((ListBox) workplaceCalendarWidget).addItem(calendar);
			}
			workplaceCalendarWidget.setStyleName("aon-selectOneMenu");
			workplaceCalendarWidget.addStyleName(style.maxWidth());
			((ListBox) workplaceCalendarWidget).addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					String calendar = ((ListBox) workplaceCalendarWidget).getSelectedItemText();
					Integer calendarId = -1;
					for(Entry<Integer, String> entry : workplaceDraftObject.getWorkplacesCalendars().entrySet()){
						if(calendar.equals(entry.getValue())){
							calendarId = entry.getKey();
							break;
						}
					}
					workplaceDraftObject.setWorkplaceCalendar(calendarId);
					save();
					
				}
			});
			workplaceCalendarPanel.add(workplaceCalendarWidget);
			if(((ListBox) workplaceCalendarWidget).getItemCount() != 0){
				((ListBox) workplaceCalendarWidget).setSelectedIndex(workplaceDraftObject.getWorkplaceCalendarIndex());
			}
		}	
	}

	private void initializeActivityCell() {
		Widget workplaceActivityWidget;
		if(workplaceDraftObject.getWorkplaceActivities().values().size() == 0){
			workplaceActivityWidget = new Label("No hay actividades disponibles");
			workplaceActivityWidget.setStyleName("aon-inputText");
			workplaceActivityWidget.addStyleName(style.maxWidthTextBox());
			workplaceActivityWidget.addStyleName(style.warningColor());
			workplaceActivityWidget.addStyleName(style.borderNone());
			workplaceActivityPanel.add(workplaceActivityWidget);
//		}else if(workplaceDraftObject.getWorkplaceActivities().values().size() == 1){
//			Integer activityId = workplaceDraftObject.getWorkplaceInfo().getActivityId();
//			workplaceActivityWidget = new Label(workplaceDraftObject.getWorkplaceActivities().get(activityId));
//			workplaceActivityPanel.add(workplaceActivityWidget);	
		}else{
			workplaceActivityWidget = new ListBox();
			((ListBox) workplaceActivityWidget).addItem("-");
			for(String activity : workplaceDraftObject.getWorkplaceActivities().values()){
				((ListBox) workplaceActivityWidget).addItem(activity);
			}
			workplaceActivityWidget.setStyleName("aon-selectOneMenu");
			workplaceActivityWidget.addStyleName(style.maxWidth());
			((ListBox) workplaceActivityWidget).addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					String activity = ((ListBox) workplaceActivityWidget).getSelectedItemText();
					Integer activityId = -1;
					for(Entry<Integer, String> entry : workplaceDraftObject.getWorkplaceActivities().entrySet()){
						if(activity.equals(entry.getValue())){
							activityId = entry.getKey();
							break;
						}
					}
					workplaceDraftObject.setWorkplaceActivity(activityId);
					save();
					
				}
			});
			workplaceActivityPanel.add(workplaceActivityWidget);	
			if(((ListBox) workplaceActivityWidget).getItemCount() != 0){
				if(((ListBox) workplaceActivityWidget).getItemCount() == 2){
					((ListBox) workplaceActivityWidget).setSelectedIndex(1);
					DomEvent.fireNativeEvent(Document.get().createChangeEvent(), workplaceActivityWidget);
				}else
					((ListBox) workplaceActivityWidget).setSelectedIndex(workplaceDraftObject.getWorkplaceActivityIndex()+1);
			}
		}
		
	}

	private void fillWorkplaceInfo() {
		this.workplaceDescription.setValue(workplaceDraftObject.getWorkplaceInfo().getDescription());
		
		if(this.workplaceEconomicConcert.getItemCount() != 0){
			this.workplaceEconomicConcert.setSelectedIndex(workplaceDraftObject.getWorkplaceEconomicConcert());
		}
		
		String workplaceAgreement = workplaceDraftObject.getWorkplaceAgreementDescription();
		if(this.workpalceAgreement.getItemCount() != 0){
			this.workpalceAgreement.setSelectedIndex(workplaceDraftObject.getAgreementIndex(workplaceAgreement) + 1);
		}
			
	}

	@Override
	public void onContextMenu(ContextMenuEvent event) {
		// TODO Auto-generated method stub
	}

}
