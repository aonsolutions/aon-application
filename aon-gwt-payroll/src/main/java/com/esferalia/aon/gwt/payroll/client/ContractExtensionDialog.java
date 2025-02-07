package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomCheckBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.ContractExtension;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;

public abstract class ContractExtensionDialog extends AonCustomDialog {
	
	private HTMLPanel container = new HTMLPanel("");
	private HTMLPanel messagePanel = new HTMLPanel("");
	
	private AonCustomTextBox contractIde = new AonCustomTextBox("IDE Contrato");
	private AonCustomDateBox newEndDateBx = new AonCustomDateBox("Fecha Fin Pr\u00f3rroga");
	private AonCustomCheckBox discontinuosInd = new AonCustomCheckBox("Indicador Discontinuidad");
	private AonCustomCheckBox enterpriseInd = new AonCustomCheckBox("Indicador Empresa");
	private AonCustomTextBox freeEnterprise = new AonCustomTextBox("Uso Libre Empresa");
	
	private HTMLPanel buttonsPanel = new HTMLPanel("");
	
	// ------------------------------------------------- Variables
	
	final DomainEmployeesServiceAsync employeesService = DomainEmployeesServiceAsync.newInstance();
	private EmployeeContractInfo contractEmployeeInfo;
	
	private Button acceptDialog;
	
	// ------------------------------------------------- Constructor
	
	protected ContractExtensionDialog(EmployeeContractInfo contractEmployeeInfoIn) {
		
		setCaption("Pr\u00F3rroga");
		showCloseButton(true);
		setWidth("20rem");
		
		contractEmployeeInfo = contractEmployeeInfoIn;
		
		initializeView();
		
		getButtonsPanel();
		showDialog();
	}
	
	private void initializeView() {
		container.addStyleName(AON.CSS.aonItemFlex());
		container.addStyleName(AON.CSS.aonFlexColumn());
		container.getElement().getStyle().setProperty("padding", "1rem");
		
		container.add(messagePanel);
		
		contractIde.setValue(contractEmployeeInfo.getContractInfo().getSepeId());
		container.add(contractIde);
		
		newEndDateBx.addValueChangeHandler(e -> acceptDialog.setEnabled(null != e.getValue()));
		container.add(newEndDateBx);
		
		container.add(discontinuosInd);
		
		container.add(enterpriseInd);
		
		container.add(freeEnterprise);
	}

	// ------------------------------------------------- Auxiliar Methods
	
	public void showDialog() {
		Scheduler.get().scheduleDeferred(() -> {
			setWidget(container);
			center();
			show();
		});
	}

	// ------------------------------------------------- ButtonsPanel
	
	private void getButtonsPanel() {
		buttonsPanel.addStyleName(AON.CSS.aonItemFlex());
		
		acceptDialog = new Button();
		acceptDialog.setStyleName(AON.CSS.aonOkButtonSmall());
		acceptDialog.setText("Aceptar");
		acceptDialog.setEnabled(false);
		acceptDialog.addClickHandler(e -> onAcceptDialog());
		
		buttonsPanel.add(acceptDialog);
		container.add(buttonsPanel);
	}
	
	private void onAcceptDialog() {
		acceptDialog.setEnabled(false);
		
		Date newStartDate = DateUtils.copyDateOnly(contractEmployeeInfo.getContractInfo().getEndDate());
		DateUtils.addDays2Date(newStartDate, 1);
		
		ContractExtension contractExtension = new ContractExtension();
		contractExtension.setContractId(contractEmployeeInfo.getContractInfo().getContractId())
						 .setContractStartDate(contractEmployeeInfo.getContractInfo().getStartDate())
						 .setNewContractStartDate(newStartDate)
						 .setNewContractEndDate(newEndDateBx.getValue())
						 .setSepeId(contractIde.getValue())
						 .setDiscontinuosInd(discontinuosInd.getValue())
						 .setEnterpriseInd(enterpriseInd.getValue())
						 .setFreeEnterprise(freeEnterprise.getValue());
		
		employeesService.contractExtension(contractExtension, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				hide();
				onExtensionDone();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error pr\u00F3rroga : " + caught.getMessage());
				acceptDialog.setEnabled(true);
			}
		});
	}
	
	// ------------------------------------------------- Abstract Methods
	
	protected abstract void onExtensionDone();
}
