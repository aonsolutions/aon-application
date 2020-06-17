package com.esferalia.aon.gwt.payroll.client;

import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
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
			Byte economicConcert = Byte.valueOf(this.workplaceEconomicConcert.getSelectedValue());
			workplaceDialogObject.setWorkplaceEconomicConcert(economicConcert);
		}

		@Override
		public void onWorkplaceAgreementChange() {
			if (this.workpalceAgreement.getSelectedIndex() == 0 ) {
				workplaceDialogObject.setWorkplaceAgreement(null);
				return;
			}
			
			Integer agreementId = Integer.valueOf(this.workpalceAgreement.getSelectedValue());
			workplaceDialogObject.setWorkplaceAgreement(agreementId);
		}
		
	}
	
	// -------------------------------------------------- UiBinder --------------------------------------------------
	
	interface WorkplaceDialogUiBinder extends UiBinder<Widget, WorkplaceDialog> {}
	
	private static WorkplaceDialogUiBinder binder = GWT.create(WorkplaceDialogUiBinder.class);
	
	@UiField (provided = true)
	Workplace workplace;
	
	@UiField
	Button cancelButton;
	
	@UiField
	Button acceptButton;
	
	// -------------------------------------------- Variables de la clase---------------------------------------------
	
	private WorkplaceDialogObject workplaceDialogObject;
	private Consumer<WorkplaceDialog> onSaved ;
	
	// ------------------------------------------------- CONSTRUCTOR --------------------------------------------------

	public WorkplaceDialog() {	
		workplace = new WorkplaceImplementation();
		
		setCaption("Centro de trabajo");
		setWidget(binder.createAndBindUi(this));
		
		//SCOPE HIDE
		workplace.generalDataTable.getRows().getItem(4).getStyle().setDisplay(Display.NONE);
		
		onSaved = this::onSavedNoop;		
	}
	
	// -------------------------------------------------- UiHandlers --------------------------------------------------
	
	@UiHandler("cancelButton")
	public void onCancelClick(ClickEvent event) {
		hide();
	}
	
	@UiHandler("acceptButton")
	public void onSaveClick(ClickEvent event) {
		if(canSaveWorkplace()) {
			workplaceDialogObject.createWorkplace(
					s -> {
						hide();
						saved();
					},
					f -> {}
			);
		}else {
			WarningDialog warningDialog = new WarningDialog("Aviso", "Hay que rellenar los campos azules obligatoriamente.");
			warningDialog.center();
			warningDialog.show();
		}
	}
	
	private boolean canSaveWorkplace() {
		if(StringUtils.isBlank(workplace.workplaceDescription.getValue()) || checkAddressWidget())
			return false;
		else
			return true;
	}

	private boolean checkAddressWidget() {
		ListBox addressListBox = (ListBox) workplace.workplaceAddressPanel.getWidget(0);
		return addressListBox.getSelectedIndex() == 0;
	}

	// ----------------------------------------------- METODOS DE LA CLASE ------------------------------------------------

	public void setWorkplaceDialogObject(WorkplaceDialogObject workplaceDialogObject) {
		this.workplaceDialogObject = workplaceDialogObject;
		this.workplaceDialogObject.getAgreements(
				s -> { 
					initializeListBox();
					 }
				, f -> {}
		);
	}

	private void initializeListBox() {
		//DIRECCION
		initializeAddressCell();
		
		// CONVENIO
		initializeAgreementCell();
		
		//ACTIVIDADES
		initializeActivityCell();
	}

	private void initializeAddressCell() {
		Widget workplaceAddressWidget;
		
		if(workplaceDialogObject.getWorkplaceAddresses().values().size() == 0)
			workplaceAddressWidget = createEmptyListLabel();
		else{
			ListBox addressListBox = new ListBox();
			addressListBox.addItem("-", "-1");
			for(Entry<Integer, String> entry : workplaceDialogObject.getWorkplaceAddresses().entrySet())
				addressListBox.addItem(entry.getValue(), entry.getKey().toString());
			
			addressListBox.setStyleName("aon-selectOneMenu");
			addressListBox.getElement().getStyle().setWidth(100.00, Unit.PCT);
			
			addressListBox.addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					Integer activityId = Integer.valueOf(addressListBox.getSelectedValue());
					workplaceDialogObject.setWorkplaceAddress(activityId);
				}
			});
			
			// If only one activity, selected it and fire event
			if(addressListBox.getItemCount() != 0 && addressListBox.getItemCount() == 1){
				addressListBox.setSelectedIndex(1);
				DomEvent.fireNativeEvent(Document.get().createChangeEvent(), addressListBox);
			}
			
			workplaceAddressWidget = addressListBox;
		}
		
		workplace.workplaceAddressPanel.add(workplaceAddressWidget);
	}
	
	private void initializeAgreementCell() {
		this.workplace.workpalceAgreement.addItem("-", "-1");
		List<Agreement> agreements = workplaceDialogObject.getWorkplacesAgreements();
		for (Agreement agreement : agreements)
			this.workplace.workpalceAgreement.addItem(agreement.getDescription(), String.valueOf(agreement.getId()));
	}

	private void initializeActivityCell() {
		Widget workplaceActivityWidget;
		
		if(workplaceDialogObject.getWorkplaceActivities().values().size() == 0)
			workplaceActivityWidget = createEmptyListLabel();
		else{
			ListBox activityListBox = new ListBox();
			activityListBox.addItem("-", "-1");
			for(Entry<Integer, String> entry : workplaceDialogObject.getWorkplaceActivities().entrySet())
				activityListBox.addItem(entry.getValue(), entry.getKey().toString());
			
			activityListBox.setStyleName("aon-selectOneMenu");
			activityListBox.getElement().getStyle().setWidth(100.00, Unit.PCT);
			
			activityListBox.addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					Integer activityId = Integer.valueOf(activityListBox.getSelectedValue());
					workplaceDialogObject.setWorkplaceActivity(activityId);
				}
			});
			
			// If only one activity, selected it and fire event
			if(activityListBox.getItemCount() != 0 && activityListBox.getItemCount() == 2){
				activityListBox.setSelectedIndex(1);
				DomEvent.fireNativeEvent(Document.get().createChangeEvent(), activityListBox);
			}
			
			workplaceActivityWidget = activityListBox;
		}
		
		workplace.workplaceActivityPanel.add(workplaceActivityWidget);
	}
	
	public Label createEmptyListLabel() {
		Label label = new Label();
		
		label.setText("No hay entradas disponibles");
		label.setStyleName("aon-inputText");
		label.addStyleName(workplace.style.warningColor());
		label.getElement().getStyle().setWidth(99.7, Unit.PCT);	
		label.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		
		return label;
	}
	
	public Label createEmptyListLabel(String labelMessage) {
		Label label = new Label();
		
		label.setText(labelMessage);
		label.setStyleName("aon-inputText");
		label.addStyleName(workplace.style.warningColor());
		label.getElement().getStyle().setWidth(99.7, Unit.PCT);	
		label.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		
		return label;
	}
	
	// ----------------------------------------------- CALLBACK TO SAVE ------------------------------------------------
	//TOO: ¿Como puedo recargar todo el arbol de laboral? (Esto iria en EmployeeTree)
	
	public WorkplaceDialog setOnSaved(Consumer<WorkplaceDialog> onSaved) {
		this.onSaved = onSaved;
		return this;
	}
	
	private void saved() {
		onSaved.accept(this);
	}
	
	protected void onSavedNoop(WorkplaceDialog workplaceDialog) {}
}
