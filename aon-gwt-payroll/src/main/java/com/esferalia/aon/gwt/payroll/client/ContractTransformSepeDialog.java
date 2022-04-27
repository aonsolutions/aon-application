package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.payroll.shared.ContractTransform;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class ContractTransformSepeDialog extends AonCustomDialog {
	
	// ------------------------------------------------- UIBinder
	
	interface ContractTransformSepeDialogUIBinder extends UiBinder<Widget, ContractTransformSepeDialog> {}

	private static final ContractTransformSepeDialogUIBinder binder = GWT.create(ContractTransformSepeDialogUIBinder.class);
	
	// ------------------------------------------------- UIFileds
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {}
	
	@UiField
	ListBox signBasicCopyLB;
	
	@UiField
	TextBox useEnterpriseFreeTB;
	
	@UiField
	Button discontinuosInd;
	
	@UiField
	HTMLPanel buttonsPanel;
	
	// ------------------------------------------------- Variables
	
	private Button acceptDialog;
	
	// ------------------------------------------------- Constructor
	
	protected ContractTransformSepeDialog() {
		
		setCaption("Datos Comununicaci\u00f3n Transformaci\u00F3n");
		
		setWidget(binder.createAndBindUi(this));
		
		getButtonsPanel();
		
		this.showCloseButton(true);
		acceptDialog.setEnabled(false);
		
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
	
	// ------------------------------------------------- InitializeView
	
	private void initializeView() {
		getEnableDisableButton(discontinuosInd, false);
		initSignBasicCopy();
		addChangeHandlers();
	}

	private void initSignBasicCopy() {
		signBasicCopyLB.clear();
		signBasicCopyLB.addItem("FIRMADAS POR LOS REPRESENTANTES LEGALES", "1");
		signBasicCopyLB.addItem("NO EXISTE REPRESENTACION LEGAL", "2");
		signBasicCopyLB.addItem("NO SE HA FACILITADO COPIA", "3");
		signBasicCopyLB.addItem("REHUSA FIRMAR", "4");
	}
	
	private void addChangeHandlers() {
		signBasicCopyLB.addChangeHandler(e -> checkAcceptBtn());
		useEnterpriseFreeTB.addChangeHandler(e -> checkAcceptBtn());
	}



	private void checkAcceptBtn() {
		String signBasicCopyValue = signBasicCopyLB.getSelectedValue();
		String useEnterpriseFree = useEnterpriseFreeTB.getValue();
		acceptDialog.setEnabled(AonStringUtils.isNotBlank(signBasicCopyValue) && AonStringUtils.isNotBlank(useEnterpriseFree));
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
		ContractTransform contractTransform = new ContractTransform();
		contractTransform.setSignBasicCopy(signBasicCopyLB.getSelectedValue())
						 .setBasicCopy(useEnterpriseFreeTB.getValue())
						 .setDiscontinuosInd(isActiveToggleButton(discontinuosInd));
		
		onTransformAccept(contractTransform);
	}
	
	// ------------------------------------------------- Abstract Methods
	
	protected abstract void onTransformAccept(ContractTransform contractTransform);
}
