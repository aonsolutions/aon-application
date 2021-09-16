package com.esferalia.aon.gwt.payroll.client;

import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class WorkplaceDialog extends AonCustomDialog {
	
	// ------------------------------------------------- Workplace

	private class WorkplaceImplementation extends Workplace{

		@Override
		public void onWorkplaceDescriptionChange() {
			workplaceDialogObject.setWorkplaceDescription(workplaceDescription.getValue());
		}
		
		@Override
		public void onWorkplaceAddressChange(Integer addressId) {
			workplaceDialogObject.setWorkplaceAddress(AonNumberUtils.equals(-1, addressId) ? null : addressId);
		}

		@Override
		public void onWorkplaceEconomicConcertChange() {
			Byte economicConcert = Byte.valueOf(this.workplaceEconomicConcert.getSelectedValue());
			workplaceDialogObject.setWorkplaceEconomicConcert(economicConcert);
		}

		@Override
		public void onWorkplaceAgreementChange(Integer agreementId) {
			workplaceDialogObject.setWorkplaceAgreement(AonNumberUtils.equals(-1, agreementId) ? null : agreementId);
		}

		@Override
		public void onWorkplaceActivityChange(Integer activityId) {
			workplaceDialogObject.setWorkplaceActivity(AonNumberUtils.equals(-1, activityId) ? null : activityId);
		}

		@Override
		public void fireWarningMessage(Map<String, String> warningMap) {
			AonMessagePanel.showWarning(messagePanel, warningMap);
		}
		
	}
	
	// ------------------------------------------------- UiBinder
	
	interface WorkplaceDialogUiBinder extends UiBinder<Widget, WorkplaceDialog> {}
	
	private static WorkplaceDialogUiBinder binder = GWT.create(WorkplaceDialogUiBinder.class);
	
	// ------------------------------------------------- UiField
	
	@UiField
	HTMLPanel messagePanel;
	
	@UiField (provided = true)
	Workplace workplace;
	
	@UiField
	HTMLPanel buttonsPanel;
	
	// ------------------------------------------------- Variables
	
	private WorkplaceDialogObject workplaceDialogObject;
	
	// ------------------------------------------------- Constructor

	public WorkplaceDialog() {	
		workplace = new WorkplaceImplementation();
		
		setCaption("Nuevo Centro de Trabajo");
		setWidget(binder.createAndBindUi(this));
		
		workplace.hideCalendarPanel();
		
		getButtonsPanel();	
	}

	// ------------------------------------------------- setWorkplaceDialogObject

	public void setWorkplaceDialogObject(WorkplaceDialogObject workplaceDialogObject) {
		this.workplaceDialogObject = workplaceDialogObject;
		this.workplaceDialogObject.getAgreements(
				s -> initializeListBox(),
				f -> {}
		);
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
		if(canSaveWorkplace()) {
			workplaceDialogObject.createWorkplace(
					s -> {
						hide();
						EmployeeTree.invokeRefreshEnterprise();
					},
					f -> {}
			);
		}else {
			Map<String, String> warningMap = new HashMap<>();
			warningMap.put("Campos obligatorios", "Los campos azules son obligatorios");
			AonMessagePanel.showWarning(messagePanel, warningMap);
		}
	}
	
	private boolean canSaveWorkplace() {
		return !AonStringUtils.isBlank(workplace.workplaceDescription.getValue()) && !checkAddressWidget();
	}

	private boolean checkAddressWidget() {
		ListBox addressListBox = (ListBox) workplace.workplaceAddressPanel.getWidget(0);
		return addressListBox.getSelectedIndex() == 0;
	}
}
