package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;

public class WorkplaceDialog extends AonCustomDialog {
	
	// ------------------------------------------------- Workplace

	private class WorkplaceImplementation extends Workplace{

		@Override
		public void onWorkplaceDescriptionChange(String description) {
			workplaceDialogObject.setWorkplaceDescription(description);
		}
		
		@Override
		public void onWorkplaceAddressChange(Integer addressId) {
			workplaceDialogObject.setWorkplaceAddress(AonNumberUtils.equals(-1, addressId) ? null : addressId);
		}

		@Override
		public void onWorkplaceEconomicConcertChange(Byte economicConcert) {
			workplaceDialogObject.setWorkplaceEconomicConcert(economicConcert);
		}

		@Override
		public void onWorkplaceAgreementChange(Integer agreementId) {
			workplaceDialogObject.setWorkplaceAgreement(agreementId);
		}

		@Override
		public void onWorkplaceActivityChange(Integer activityId) {
			workplaceDialogObject.setWorkplaceActivity(AonNumberUtils.equals(-1, activityId) ? null : activityId);
		}
		
	}
	
	
	// ------------------------------------------------- UiField
	
	private HTMLPanel content = new HTMLPanel(AonStringUtils.EMPTY);
	private Workplace workplace;
	private HTMLPanel buttonsPanel = new HTMLPanel(AonStringUtils.EMPTY);
	
	// ------------------------------------------------- Variables
	
	private WorkplaceDialogObject workplaceDialogObject;
	
	// ------------------------------------------------- Constructor

	public WorkplaceDialog() {	
		setCaption("Nuevo Centro de Trabajo");
		
		content.addStyleName(AON.CSS.aonFlexColumn());
		
		workplace = new WorkplaceImplementation();
		workplace.hideCalendarPanel();
		
		getButtonsPanel();
		
		content.add(workplace);
		content.add(buttonsPanel);
		
		setWidget(content);
	}

	// ------------------------------------------------- setWorkplaceDialogObject

	public void setWorkplaceDialogObject(WorkplaceDialogObject workplaceDialogObject) {
		this.workplaceDialogObject = workplaceDialogObject;
		this.workplaceDialogObject.getAgreements(
				s -> {
					initializeListBox();
					showDialog();
				}, f -> {}
		);
	}
	
	public void showDialog() {
		// Show center
		Scheduler.get().scheduleDeferred(() -> {
			center();
			show();
		});
	}

	private void initializeListBox() {
		//DIRECCION
		workplace.initializeAddressCell(workplaceDialogObject.getWorkplaceAddresses());
		
		// CONVENIO
		workplace.initializeAgreementCell(workplaceDialogObject.getWorkplacesAgreements());
		
		//ACTIVIDADES
		workplace.initializeActivityCell(workplaceDialogObject.getWorkplaceActivities());
	}
	
	// ------------------------------------------------- Buttons Panel
	
	private void getButtonsPanel() {
		buttonsPanel.addStyleName(AON.CSS.aonDisplayFlexEnd());
		buttonsPanel.getElement().getStyle().setProperty("margin", "1rem");
		
		Button closeBtnDialog = createButton("Cancelar");
		closeBtnDialog.addClickHandler(e -> onCloseDialog());
		
		buttonsPanel.add(closeBtnDialog);
		
		Button acceptBtnDialog = createButton("Crear");
		acceptBtnDialog.getElement().getStyle().setProperty("color", "green");
		acceptBtnDialog.addClickHandler(e -> onAcceptDialog());
		
		buttonsPanel.add(acceptBtnDialog);
	}
	
	private Button createButton(String text) {
		Button button = new Button(text);
		button.getElement().getStyle().setProperty("background", "none");
		button.getElement().getStyle().setProperty("background-color", "#fafafa");
		button.getElement().getStyle().setProperty("padding", "5px");
		button.getElement().getStyle().setProperty("height", "auto");
		button.getElement().getStyle().setProperty("font-size", "12px");
//		button.getElement().getStyle().setProperty("font-family", "Arial Unicode MS, Arial, sans-serif");
		button.getElement().getStyle().setProperty("text-transform", "inherit");
		button.getElement().getStyle().setProperty("font-weight", "bold");
		button.getElement().getStyle().setProperty("border", "1px solid #d0d0d0");
		button.getElement().getStyle().setProperty("border-radius", "5px");
		
		return button;
	}
	
	private void onCloseDialog() {
		hide();
	}
	
	private void onAcceptDialog() {
		if(canSaveWorkplace()) 
			workplaceDialogObject.createWorkplace(
					s -> {
						hide();
						EmployeeTree.invokeRefreshEnterprise();
					},
					f -> {}
			);
		else
			workplace.showError("Los campos azules son obligatorios");
	}
	
	private boolean canSaveWorkplace() {
		return !AonStringUtils.isBlank(workplace.getWorkplaceDescription().getValue()) && !checkAddressWidget();
	}

	private boolean checkAddressWidget() {
		AonCustomListBox addressListBox = (AonCustomListBox) workplace.getWorkplaceAddressPanel().getWidget(0);
		return addressListBox.getListBox().getSelectedIndex() == 0;
	}
}
