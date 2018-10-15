package com.esferalia.aon.gwt.payroll.client;

import java.util.List;
import java.util.Map.Entry;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
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
	}
	
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
	ListBox workplaceCalendar;

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

	@UiHandler("workplaceCalendar")
	void onWorkplaceCalendarChangeValue(ChangeEvent event) {
		String calendar = workplaceCalendar.getSelectedItemText();
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
	
	@UiHandler("workpalceAgreement")
	void onContractAgreementChangeValue(ChangeEvent event) {
		Integer agreementId = workplaceDraftObject.getAgreementId(this.workpalceAgreement.getSelectedItemText());
		workplaceDraftObject.setWorkplaceAgreement(agreementId);
		save();
	}
	
	private void save() {
		workplaceDraftObject.updateWorkplace(
				r -> { setWorkplaceDraftObject(workplaceDraftObject); }, 
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
		this.workplaceCalendar.clear();
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
		for(String calendar : workplaceDraftObject.getWorkplacesCalendars().values()){
			if(null != calendar)
				this.workplaceCalendar.addItem(calendar);
		}
		
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
			workplaceAddressPanel.add(workplaceAddressWidget);
		}else{
			workplaceAddressWidget = new ListBox();
			for(String address : workplaceDraftObject.getWorkplaceAddresses().values()){
				((ListBox) workplaceAddressWidget).addItem(address);
			}
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

	private void initializeActivityCell() {
		Widget workplaceActivityWidget;
		if(workplaceDraftObject.getWorkplaceActivities().values().size() == 1){
			Integer activityId = workplaceDraftObject.getWorkplaceInfo().getActivityId();
			workplaceActivityWidget = new Label(workplaceDraftObject.getWorkplaceActivities().get(activityId));
			workplaceActivityPanel.add(workplaceActivityWidget);	
		}else{
			workplaceActivityWidget = new ListBox();
			for(String activity : workplaceDraftObject.getWorkplaceActivities().values()){
				((ListBox) workplaceActivityWidget).addItem(activity);
			}
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
				((ListBox) workplaceActivityWidget).setSelectedIndex(workplaceDraftObject.getWorkplaceActivityIndex());
			}
		}
		
	}

	private void fillWorkplaceInfo() {
		this.workplaceDescription.setValue(workplaceDraftObject.getWorkplaceInfo().getDescription());
		
		if(this.workplaceEconomicConcert.getItemCount() != 0){
			this.workplaceEconomicConcert.setSelectedIndex(workplaceDraftObject.getWorkplaceEconomicConcert());
		}
		
		if(this.workplaceCalendar.getItemCount() != 0){
			this.workplaceCalendar.setSelectedIndex(workplaceDraftObject.getWorkplaceCalendarIndex());
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
