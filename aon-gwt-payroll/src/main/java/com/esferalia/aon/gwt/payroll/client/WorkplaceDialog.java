package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class WorkplaceDialog extends AonCustomDialog {

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
		
	}
	
	// -------------------------------------------------- UiBinder --------------------------------------------------
	
	interface WorkplaceDialogUiBinder extends UiBinder<Widget, WorkplaceDialog> {}
	
	private static WorkplaceDialogUiBinder binder = GWT.create(WorkplaceDialogUiBinder.class);
	
	@UiField (provided = true)
	Workplace workplace;
	
	@UiField
	HTMLPanel buttonsPanel;
	
	// -------------------------------------------- Variables de la clase---------------------------------------------
	
	private WorkplaceDialogObject workplaceDialogObject;
	
	private Button closeBtnDialog;
	private Button acceptBtnDialog;
	
	// ------------------------------------------------- CONSTRUCTOR --------------------------------------------------

	public WorkplaceDialog() {	
		workplace = new WorkplaceImplementation();
		
		setCaption("Nuevo Centro de Trabajo");
		setWidget(binder.createAndBindUi(this));
		
		workplace.hideCalendarPanel();
		
		getButtonsPanel();	
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
		workplace.initializeAddressCell(workplaceDialogObject.getWorkplaceAddresses());
		
		// CONVENIO
		workplace.initializeAgreementCell(workplaceDialogObject.getWorkplacesAgreements());
		
		//ACTIVIDADES
		workplace.initializeActivityCell(workplaceDialogObject.getWorkplaceActivities());
	}
	
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
		if(canSaveWorkplace()) {
			workplaceDialogObject.createWorkplace(
					s -> {
						hide();
						EmployeeTree.invokeRefreshEnterprise();
					},
					f -> {}
			);
		}else {
			AonDialog dialog = new AonDialog("CUIDADO", new HTML("Hay que rellenar los campos azules obligatoriamente."));
			dialog.info();
		}
	}
	
	private boolean canSaveWorkplace() {
		if(AonStringUtils.isBlank(workplace.workplaceDescription.getValue()) || checkAddressWidget())
			return false;
		else
			return true;
	}

	private boolean checkAddressWidget() {
		ListBox addressListBox = (ListBox) workplace.workplaceAddressPanel.getWidget(0);
		return addressListBox.getSelectedIndex() == 0;
	}
}
