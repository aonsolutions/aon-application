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
		Button closeBtnDialog = new Button();
		closeBtnDialog.setStyleName(AON.CSS.aonCancelButtonSmall());
		closeBtnDialog.setText( AON.MSG.cancelAction());
		closeBtnDialog.addClickHandler(e -> onCloseDialog());
		
		buttonsPanel.add(closeBtnDialog);
		
		Button acceptBtnDialog = new Button();
		acceptBtnDialog.setStyleName(AON.CSS.aonOkButtonSmall());
		acceptBtnDialog.setText( AON.MSG.accept());
		acceptBtnDialog.addClickHandler(e -> onAcceptDialog());
		
		buttonsPanel.add(acceptBtnDialog);
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
