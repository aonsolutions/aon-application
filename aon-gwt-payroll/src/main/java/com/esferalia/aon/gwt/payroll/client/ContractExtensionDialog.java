package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.ContractExtension;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class ContractExtensionDialog extends AonCustomDialog {
	
	// ------------------------------------------------- UIBinder
	
	interface Certifica2DialogUIBinder extends UiBinder<Widget, ContractExtensionDialog> {}

	private static final Certifica2DialogUIBinder binder = GWT.create(Certifica2DialogUIBinder.class);
	
	// ------------------------------------------------- UIFileds
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {}
	
	@UiField
	TextBox contractIde;
	
	@UiField
	DateBoxEx newEndDateBx;
	
	@UiField
	Button discontinuosInd;
	
	@UiField
	Button enterpriseInd;
	
	@UiField
	TextBox freeEnterprise;
	
	@UiField
	HTMLPanel buttonsPanel;
	
	// ------------------------------------------------- Variables
	
	final DomainEmployeesServiceAsync employeesService = DomainEmployeesServiceAsync.newInstance();
	private EmployeeContractInfo contractEmployeeInfo;
	
	private Button acceptDialog;
	
	// ------------------------------------------------- Constructor
	
	protected ContractExtensionDialog(EmployeeContractInfo contractEmployeeInfoIn) {
		
		setCaption("Pr\u00F3rroga");
		
		setWidget(binder.createAndBindUi(this));
		
		this.showCloseButton(true);
		
		contractEmployeeInfo = contractEmployeeInfoIn;
		
		getButtonsPanel();
		initializeView();
		showDialog();
	}
	
	// ------------------------------------------------- UIHandlers
	
	@UiHandler("discontinuosInd")
	void onDiscontinuosIndClick(ClickEvent event) {
		Boolean oldValue = isActiveToggleButton(discontinuosInd);
		Boolean value = !oldValue;
		getEnableDisableButton(discontinuosInd, value);
	}
	
	@UiHandler("enterpriseInd")
	void onEnterpriseIndClick(ClickEvent event) {
		Boolean oldValue = isActiveToggleButton(enterpriseInd);
		Boolean value = !oldValue;
		getEnableDisableButton(enterpriseInd, value);
	}
	
	
	// ------------------------------------------------- InitializeView
	
	private void initializeView() {
		this.contractIde.setValue(contractEmployeeInfo.getContractInfo().getSepeId());
		getEnableDisableButton(discontinuosInd, false);
		getEnableDisableButton(enterpriseInd, false);
		acceptDialog.setEnabled(false);
		
		newEndDateBx.addValueChangeHandler(e -> acceptDialog.setEnabled(null != e.getValue()));
	}
	
	// ------------------------------------------------- ToggleButton
	
	private void getEnableDisableButton(Button button, boolean disabled) {
		button.removeStyleName(disabled ? AON.AON_ICON_DISABLE : AON.AON_ICON_ENABLE);
		button.removeStyleName(AON.AON_NO_MARGIN);
		button.removeStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON);
		
		button.setStyleName(!disabled ? AON.AON_ICON_DISABLE : AON.AON_ICON_ENABLE );
		button.setStyleName(AON.AON_NO_MARGIN, true);
		button.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
	}
	
	private boolean isActiveToggleButton(Button button) {
		return AonStringUtils.containsIgnoreCase(button.getStyleName(), AON.AON_ICON_ENABLE);
	}

	// ------------------------------------------------- Auxiliar Methods
	
	public void showDialog() {
		Scheduler.get().scheduleDeferred(() -> {
			center();
			show();
		});
	}

	// ------------------------------------------------- ButtonsPanel
	
	private void getButtonsPanel() {
		acceptDialog = new Button();
		acceptDialog.setStyleName(AON.CSS.aonOkButtonSmall());
		acceptDialog.setText("Aceptar");
		acceptDialog.addClickHandler(e -> onAcceptDialog());
		
		buttonsPanel.add(acceptDialog);
	}
	
	private void onAcceptDialog() {
		Date newStartDate = DateUtils.copyDateOnly(contractEmployeeInfo.getContractInfo().getEndDate());
		DateUtils.addDays2Date(newStartDate, 1);
		
		ContractExtension contractExtension = new ContractExtension();
		contractExtension.setContractId(contractEmployeeInfo.getContractInfo().getContractId())
						 .setContractStartDate(contractEmployeeInfo.getContractInfo().getStartDate())
						 .setNewContractStartDate(newStartDate)
						 .setNewContractEndDate(newEndDateBx.getValue())
						 .setSepeId(contractIde.getValue())
						 .setDiscontinuosInd(isActiveToggleButton(discontinuosInd))
						 .setEnterpriseInd(isActiveToggleButton(enterpriseInd))
						 .setFreeEnterprise(freeEnterprise.getValue());
		
		employeesService.contractExtension(contractExtension, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				hide();
				onExtensionDone();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Map<String, String> errorMap = new HashMap<>();
				errorMap.put("Error pr\u00F3rroga", "No se ha podido realizar la pr\u00F3rroga correctamente");
				fireError(errorMap);
				hide();
			}
		});
	}
	
	// ------------------------------------------------- Abstract Methods
	
	protected abstract void onExtensionDone();
	protected abstract void fireError(Map<String, String> errorMap);
}
