package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.payroll.shared.CNO;
import com.esferalia.aon.gwt.payroll.shared.ContractTransform;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.occam.api.model.type.ContractType;
import com.esferalia.aon.occam.api.model.type.ContractType.ContractTypeRecord;
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
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class ContractTransformDialog extends AonCustomDialog {
	
	// ------------------------------------------------- UIBinder
	
	interface Certifica2DialogUIBinder extends UiBinder<Widget, ContractTransformDialog> {}

	private static final Certifica2DialogUIBinder binder = GWT.create(Certifica2DialogUIBinder.class);
	
	// ------------------------------------------------- UIFileds
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {}
	
	@UiField
	TextBox contractIde;
	
	@UiField
	ListBox tc2;
	
	@UiField
	DateBoxEx startDateBx;
	
	@UiField
	Button discontinuosInd;
	
	@UiField
	SuggestBox cno;
	
	@UiField
	HTMLPanel buttonsPanel;
	
	// ------------------------------------------------- Variables
	
	final DomainEmployeesServiceAsync employeesService = DomainEmployeesServiceAsync.newInstance();
	final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	private EmployeeContractInfo contractEmployeeInfo;
	
	private Map<String, CNO> cnoMap = new HashMap<>();
	
	private Button acceptDialog;
	
	// ------------------------------------------------- Constructor
	
	protected ContractTransformDialog(EmployeeContractInfo contractEmployeeInfoIn) {
		
		setCaption("Transformaci\u00F3n");
		
		setWidget(binder.createAndBindUi(this));
		
		this.showCloseButton(true);
		
		contractEmployeeInfo = contractEmployeeInfoIn;
		
		impl.getCNOs(new AsyncCallback<Map<String, CNO>>() {

			@Override
			public void onFailure(Throwable caught) {
				// Not use here
			}

			@Override
			public void onSuccess(Map<String, CNO> result) {
				cnoMap = result;
				
				List<String> cnoEntry = new ArrayList<>();
				for(Entry<String, CNO> entry : cnoMap.entrySet())
					cnoEntry.add(entry.getKey() + " - " + entry.getValue().getTitle());
				
				List<String> cnoSuggest = new ArrayList<>();
				for(String cnoStr : cnoEntry)
					cnoSuggest.add(cnoStr);
				
				MultiWordSuggestOracle orclIbans = (MultiWordSuggestOracle) cno.getSuggestOracle();
				orclIbans.addAll(cnoSuggest);
				cno.setAutoSelectEnabled(true);
				
				getButtonsPanel();
				initializeView();
				showDialog();
			}});	
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
		this.contractIde.setValue(contractEmployeeInfo.getContractInfo().getSepeId());
		getEnableDisableButton(discontinuosInd, false);
		initContractType();
		initCNO();
		
		acceptDialog.setEnabled(false);
		startDateBx.addValueChangeHandler(e -> acceptDialog.setEnabled(null != e.getValue()));
	}

	private void initCNO() {
		String codeCNO = contractEmployeeInfo.getContractSpecificData().getCno();
		CNO cnoObj = cnoMap.get(codeCNO);
		if(null != cnoObj)
			cno.setText(codeCNO + " - " + cnoObj.getTitle());
	}

	private void initContractType() {
		// TIPO DE CONTRATO
		ContractType contractType = new ContractType();
		tc2.addItem("-", "-1");
		for (Entry<Integer, ContractTypeRecord> entry : contractType.getContractTypes().entrySet()) {
			if(entry.getKey() < 400 && entry.getKey() % 10 == 9)
				tc2.addItem(entry.getKey() + " - " + entry.getValue().getContractTypeDescription(), AonStringUtils.leftPad(entry.getKey().toString(), 3, '0'));
		}
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
	
	private String getCNO() {
		String cnoStr = cno.getValue();
		String cnoValue = "";
		if(!AonStringUtils.isBlank(cnoStr))
			cnoValue = cnoStr.split(" -")[0];
		return cnoValue;
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
		contractTransform.setContractId(contractEmployeeInfo.getContractInfo().getContractId())
						 .setDomainId(contractEmployeeInfo.getEmployeeInfo().getDomain())
						 .setTc2(tc2.getSelectedValue())
						 .setContractStartDate(startDateBx.getValue())
						 .setSepeId(contractIde.getValue())
						 .setDiscontinuosInd(isActiveToggleButton(discontinuosInd))
						 .setCno(getCNO());
		
		employeesService.contractTransform(contractTransform, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				hide();
				onTransformDone();
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
	
	protected abstract void onTransformDone();
	protected abstract void fireError(Map<String, String> errorMap);
}
