package com.esferalia.aon.gwt.payroll.client;

import java.util.List;
import java.util.Map.Entry;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.payroll.client.EmployeeDialog.Callback;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class WorkplaceDialog extends CustomDialog {

	private class WorkplaceImplementation extends Workplace{

		@Override
		public void onWorkplaceDescriptionChange() {
			workplaceDialogObject.setWorkplaceDescription(workplaceDescription.getValue());
		}

		@Override
		public void onWorkplaceEconomicConcertChange() {
			workplaceDialogObject.setWorkplaceEconomicConcert(workplaceEconomicConcert.getSelectedIndex() - 1);
		}

		@Override
		public void onWorkplaceAgreementChange() {
			Integer agreementId = workplaceDialogObject.getAgreementId(this.workpalceAgreement.getSelectedItemText());
			workplaceDialogObject.setWorkplaceAgreement(agreementId);
		}
		
	}
	
	interface Callback {
		void onAccept(WorkplaceDialog dialog);
	}
	
	// -------------------------------------------------- UiBinder --------------------------------------------------
	
	interface WorkplaceDialogUiBinder extends UiBinder<Widget, WorkplaceDialog> {
	
	}
	
	private static WorkplaceDialogUiBinder binder = GWT.create(WorkplaceDialogUiBinder.class);
	
	@UiField (provided = true)
	Workplace workplace;
	
	@UiField
	Button cancelButton;
	
	@UiField
	Button acceptButton;
	
	// -------------------------------------------- Variables de la clase---------------------------------------------
	
	private Callback cb;
	private WorkplaceDialogObject workplaceDialogObject;
	
	// ------------------------------------------------- CONSTRUCTOR --------------------------------------------------

	public WorkplaceDialog() {	
		workplace = new WorkplaceImplementation();
		
		setCaption("Centro de trabajo");
		setWidget(binder.createAndBindUi(this));
	}
	
	public void show(Callback cb) {
		this.cb = cb;
		super.show();
	}
	
	public void setPopupPositionAndShow(PositionCallback positionCallback, Callback callback) {
		this.cb = callback;
		super.setPopupPositionAndShow(positionCallback);
	}
	
	// -------------------------------------------------- UiHandlers --------------------------------------------------
	
	@UiHandler("cancelButton")
	public void onCancelClick(ClickEvent event) {
		hide();
	}
	
	@UiHandler("acceptButton")
	public void onSaveClick(ClickEvent event) {
		if(canSaveWorkplace()) {
			if(workplaceDialogObject.getWorkplaceAddresses().size() > 1)
				onAddressChage();
			if(workplaceDialogObject.getWorkplaceScopes().size() > 1)
				onScopeChange();
			
			workplaceDialogObject.createWorkplace(
					s -> {
						hide();
						EmployeeTree.invokeRefreshEnterprise();
						cb.onAccept(this);
					},
					f -> {}
			);
		}else {
			WarningDialog warningDialog = new WarningDialog("Aviso", "Hay que rellenar los campos azules obligatoriamente.");
			warningDialog.center();
			warningDialog.show();
		}
	}

	// ----------------------------------------------- METODOS DE LA CLASE ------------------------------------------------

	private boolean canSaveWorkplace() {
		if("" != workplace.workplaceDescription.getValue())
			return true;
		else
			return false;
	}

	public void setWorkplaceDialogObject(WorkplaceDialogObject workplaceDialogObject) {
		this.workplaceDialogObject = workplaceDialogObject;
		this.workplaceDialogObject.getAgreements(
				s -> { 
						initializeView();
					 }
				, f -> {}
		);
	}
	
	private void initializeView() {
		workplace.generalDataTable.getRows().getItem(4).getStyle().clearDisplay();
		resetElements();
		initializeListBox();
	}
	
	private void resetElements() {	
		// Clear general elements
		workplace.workplaceDescription.setValue("");
		workplace.workplaceAddressPanel.clear();
		workplace.workplaceEconomicConcert.clear();
		workplace.workplaceScopePanel.clear();

		// Clear payroll elements
		workplace.workplaceCalendarPanel.clear();
		workplace.workpalceAgreement.clear();
		workplace.workplaceActivityPanel.clear();
		
	}

	private void initializeListBox() {
		//DIRECCION
		initializeAddressCell();
		
		//CONCIERTO ECONOMICO
		workplace.workplaceEconomicConcert.addItem("-");
		workplace.workplaceEconomicConcert.addItem(String.valueOf("\u00C1")+"lava");
		workplace.workplaceEconomicConcert.addItem("Bizkaia");
		workplace.workplaceEconomicConcert.addItem("Gipuzkoa");
		workplace.workplaceEconomicConcert.addItem("Navarra");
		workplace.workplaceEconomicConcert.addItem("Territorio Com"+ String.valueOf("\u00FA") +"n");
		
		//SCOPE
		initializeScopeCell();
		
		//CALENDARIO
		initializeCalendarCell();
		
		// CONVENIO
		workplace.workpalceAgreement.addItem("-");
		List<Agreement> agreements = workplaceDialogObject.getActiveAgreements();
		for (Agreement a : agreements) {
			workplace.workpalceAgreement.addItem(a.getDescription());
		}
		
		//ACTIVIDADES
		initializeActivityCell();

	}
	
	private void initializeAddressCell() {
		Widget workplaceAddressWidget;
		if(workplaceDialogObject.getWorkplaceAddresses().values().size() == 1){
			Integer addressId = (Integer) workplaceDialogObject.getWorkplaceAddresses().keySet().toArray()[0];
			workplaceAddressWidget = new Label((null == addressId) ? "" : workplaceDialogObject.getWorkplaceAddresses().get(addressId));
			workplaceAddressWidget.setStyleName("aon-inputText");
			workplaceAddressWidget.addStyleName(workplace.style.maxWidthTextBox());
			workplace.workplaceAddressPanel.add(workplaceAddressWidget);
			workplaceDialogObject.setWorkplaceAddress(addressId); //AutoSeleccion
		}else{
			workplaceAddressWidget = new ListBox();
			for(String address : workplaceDialogObject.getWorkplaceAddresses().values()){
				((ListBox) workplaceAddressWidget).addItem(address);
			}
			workplaceAddressWidget.setStyleName("aon-selectOneMenu");
			workplaceAddressWidget.addStyleName(workplace.style.maxWidth());
			((ListBox) workplaceAddressWidget).addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					onAddressChage();
					//save();
				}
			});
			workplace.workplaceAddressPanel.add(workplaceAddressWidget);
		}
	}
	
	private void onAddressChage() {
		String address = ((ListBox) workplace.workplaceAddressPanel.getWidget(0)).getSelectedItemText();
		Integer addressId = -1;
		for(Entry<Integer, String> entry : workplaceDialogObject.getWorkplaceAddresses().entrySet()){
			if(address == entry.getValue()){
				addressId = entry.getKey();
				break;
			}
		}
		workplaceDialogObject.setWorkplaceAddress(addressId);
	}
	
	private void initializeScopeCell() {
		Widget workplaceScopeWidget;
		if(workplaceDialogObject.getWorkplaceScopes().values().size() == 1){
			Integer scopeId = (Integer) workplaceDialogObject.getWorkplaceScopes().keySet().toArray()[0];
			workplaceScopeWidget = new Label((null == scopeId) ? "" : workplaceDialogObject.getWorkplaceScopes().get(scopeId));
			workplaceScopeWidget.setStyleName("aon-inputText");
			workplaceScopeWidget.addStyleName(workplace.style.maxWidthTextBox());
			workplace.workplaceScopePanel.add(workplaceScopeWidget);
			workplaceDialogObject.setWorkplaceScope(scopeId); //AutoSeleccion
		}else{
			workplaceScopeWidget = new ListBox();
			for(String scope : workplaceDialogObject.getWorkplaceScopes().values()){
				((ListBox) workplaceScopeWidget).addItem(scope);
			}
			workplaceScopeWidget.setStyleName("aon-selectOneMenu");
			workplaceScopeWidget.addStyleName(workplace.style.maxWidth());
			((ListBox) workplaceScopeWidget).addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					onScopeChange();
					//save();
				}
			});
			workplace.workplaceScopePanel.add(workplaceScopeWidget);
		}
	}
	
	private void onScopeChange() {
		String scope = ((ListBox) workplace.workplaceScopePanel.getWidget(0)).getSelectedItemText();
		Integer scopeId = -1;
		for(Entry<Integer, String> entry : workplaceDialogObject.getWorkplaceScopes().entrySet()){
			if(scope == entry.getValue()){
				scopeId = entry.getKey();
				break;
			}
		}
		workplaceDialogObject.setWorkplaceScope(scopeId);
	}
	
	private void initializeCalendarCell() {
		Widget workplaceCalendarWidget;
		if(workplaceDialogObject.getWorkplacesCalendars().values().size() == 0){
			workplaceCalendarWidget = new Label("No hay calendarios disponibles");
			workplaceCalendarWidget.setStyleName("aon-inputText");
			workplaceCalendarWidget.addStyleName(workplace.style.maxWidthTextBox());
			workplaceCalendarWidget.addStyleName(workplace.style.warningColor());
			workplaceCalendarWidget.addStyleName(workplace.style.borderNone());
			workplace.workplaceCalendarPanel.add(workplaceCalendarWidget);
		}else if(workplaceDialogObject.getWorkplacesCalendars().values().size() == 1){
			Integer calendarId = (Integer) workplaceDialogObject.getWorkplacesCalendars().keySet().toArray()[0];
			workplaceCalendarWidget = new Label(workplaceDialogObject.getWorkplacesCalendars().get(calendarId));
			workplaceCalendarWidget.setStyleName("aon-inputText");
			workplaceCalendarWidget.addStyleName(workplace.style.maxWidthTextBox());
			workplace.workplaceCalendarPanel.add(workplaceCalendarWidget);
			workplaceDialogObject.setWorkplaceCalendar(calendarId); //AutoSeleccion
		}else{
			workplaceCalendarWidget = new ListBox();
			for(String calendar : workplaceDialogObject.getWorkplacesCalendars().values()){
				if(null != calendar)
					((ListBox) workplaceCalendarWidget).addItem(calendar);
			}
			workplaceCalendarWidget.setStyleName("aon-selectOneMenu");
			workplaceCalendarWidget.addStyleName(workplace.style.maxWidth());
			((ListBox) workplaceCalendarWidget).addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					String calendar = ((ListBox) workplaceCalendarWidget).getSelectedItemText();
					Integer calendarId = -1;
					for(Entry<Integer, String> entry : workplaceDialogObject.getWorkplacesCalendars().entrySet()){
						if(calendar.equals(entry.getValue())){
							calendarId = entry.getKey();
							break;
						}
					}
					workplaceDialogObject.setWorkplaceCalendar(calendarId);
					//save();
				}
			});
			workplace.workplaceCalendarPanel.add(workplaceCalendarWidget);
		}	
	}

	private void initializeActivityCell() {
		Widget workplaceActivityWidget;
		if(workplaceDialogObject.getWorkplaceActivities().values().size() == 0){
			workplaceActivityWidget = new Label("No hay actividades disponibles");
			workplaceActivityWidget.setStyleName("aon-inputText");
			workplaceActivityWidget.addStyleName(workplace.style.maxWidthTextBox());
			workplaceActivityWidget.addStyleName(workplace.style.warningColor());
			workplaceActivityWidget.addStyleName(workplace.style.borderNone());
			workplace.workplaceActivityPanel.add(workplaceActivityWidget);
		}else{
			workplaceActivityWidget = new ListBox();
			((ListBox) workplaceActivityWidget).addItem("-");
			for(String activity : workplaceDialogObject.getWorkplaceActivities().values()){
				((ListBox) workplaceActivityWidget).addItem(activity);
			}
			workplaceActivityWidget.setStyleName("aon-selectOneMenu");
			workplaceActivityWidget.addStyleName(workplace.style.maxWidth());
			((ListBox) workplaceActivityWidget).addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					String activity = ((ListBox) workplaceActivityWidget).getSelectedItemText();
					Integer activityId = -1;
					for(Entry<Integer, String> entry : workplaceDialogObject.getWorkplaceActivities().entrySet()){
						if(activity.equals(entry.getValue())){
							activityId = entry.getKey();
							break;
						}
					}
					workplaceDialogObject.setWorkplaceActivity(activityId);
					//save();
				}
			});
			workplace.workplaceActivityPanel.add(workplaceActivityWidget);	
			if(((ListBox) workplaceActivityWidget).getItemCount() != 0){
				if(((ListBox) workplaceActivityWidget).getItemCount() == 2){
					((ListBox) workplaceActivityWidget).setSelectedIndex(1);
					DomEvent.fireNativeEvent(Document.get().createChangeEvent(), workplaceActivityWidget);
				}
			}
		}
		
	}
}
