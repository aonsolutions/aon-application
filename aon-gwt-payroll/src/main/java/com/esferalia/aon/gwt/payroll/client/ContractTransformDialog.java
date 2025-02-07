package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomCheckBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomSuggestBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.payroll.shared.CNO;
import com.esferalia.aon.gwt.payroll.shared.ContractTransform;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.occam.api.model.type.ContractType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;

public abstract class ContractTransformDialog extends AonCustomDialog {
	
	private HTMLPanel container = new HTMLPanel("");
	private HTMLPanel messagePanel = new HTMLPanel("");
	
	private AonCustomTextBox contractIde = new AonCustomTextBox("IDE Contrato");
	private AonCustomListBox tc2 = new AonCustomListBox("Tipo contrato (TC2)");
	private AonCustomDateBox startDateBx = new AonCustomDateBox("Fecha inicio");
	private AonCustomCheckBox discontinuosInd = new AonCustomCheckBox("Indicador discontinuidad");
	private AonCustomSuggestBox cno = new AonCustomSuggestBox("C.N.O.");
	
	private HTMLPanel buttonsPanel = new HTMLPanel("");
	
	// ------------------------------------------------- Variables
	
	final DomainEmployeesServiceAsync employeesService = DomainEmployeesServiceAsync.newInstance();
	final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	private EmployeeContractInfo contractEmployeeInfo;
	
	private Map<String, CNO> cnoMap = new HashMap<>();
	
	private Button acceptDialog;
	
	// ------------------------------------------------- Constructor
	
	protected ContractTransformDialog(EmployeeContractInfo contractEmployeeInfoIn) {
		
		setCaption("Transformaci\u00F3n");
		showCloseButton(true);
		
		contractEmployeeInfo = contractEmployeeInfoIn;
		
		getCNOs(end -> {
			initializeCNOSuggestions();
			
			initializeView();
			getButtonsPanel();
			
			showDialog();
		});
	}
	
	private void initializeCNOSuggestions() {
		List<String> cnoEntries = new ArrayList<String>();
		cnoMap.entrySet().forEach(entry -> cnoEntries.add(entry.getKey() + " - " + entry.getValue().getTitle()));
		
		List<String> cnoSuggest = new ArrayList<>();
		for(String cnoStr : cnoEntries) cnoSuggest.add(cnoStr);
		
		MultiWordSuggestOracle orclIbans = (MultiWordSuggestOracle) cno.getSuggestBox().getSuggestOracle();
		orclIbans.addAll(cnoSuggest);
		cno.setAutoSelectEnabled(true);
	}
	
	private void initializeView() {
		container.addStyleName(AON.CSS.aonItemFlex());
		container.addStyleName(AON.CSS.aonFlexColumn());
		container.getElement().getStyle().setProperty("padding", "1rem");
		
		container.add(messagePanel);
		
		contractIde.setValue(contractEmployeeInfo.getContractInfo().getSepeId());
		container.add(contractIde);
		
		initContractType();
		container.add(tc2);
		
		startDateBx.addValueChangeHandler(e -> acceptDialog.setEnabled(null != e.getValue()));
		container.add(startDateBx);
		
		container.add(discontinuosInd);
		
		CNO cnoObj = cnoMap.get(contractEmployeeInfo.getContractSpecificData().getCno());
		if(null != cnoObj) cno.setValue(cnoObj.getCode() + " - " + cnoObj.getTitle());
		container.add(cno);
	}
	
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

	private void getCNOs(Consumer<Map<String, CNO>> consumer) {
		impl.getCNOs(new AsyncCallback<Map<String, CNO>>() {

			@Override
			public void onFailure(Throwable caught) {
				// Not use here
			}

			@Override
			public void onSuccess(Map<String, CNO> result) {
				cnoMap = result;
				consumer.accept(result);
			}
		});	
	}
	
	private void initContractType() {
		// TIPO DE CONTRATO
		ContractType contractType = new ContractType();
		
		tc2.clearItems();
		tc2.addItem("-", "-1");
		
		contractType.getContractTypes().entrySet().stream()
			.filter(entry -> entry.getKey() < 400 && entry.getKey() % 10 == 9)
			.forEach(entry -> tc2.addItem(entry.getKey() + " - " + entry.getValue().getContractTypeDescription(), AonStringUtils.leftPad(entry.getKey().toString(), 3, '0')));
		
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
	
	private void onAcceptDialog() {
		acceptDialog.setEnabled(false);
		
		ContractTransform contractTransform = new ContractTransform();
		contractTransform.setContractId(contractEmployeeInfo.getContractInfo().getContractId())
						 .setDomainId(contractEmployeeInfo.getEmployeeInfo().getDomain())
						 .setTc2(tc2.getValue())
						 .setContractStartDate(startDateBx.getValue())
						 .setSepeId(contractIde.getValue())
						 .setDiscontinuosInd(discontinuosInd.getValue())
						 .setCno(AonStringUtils.isBlank(cno.getValue()) ? "" : cno.getValue().split(" -")[0]);
		
		employeesService.contractTransform(contractTransform, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				hide();
				onTransformDone();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error pr\u00F3rroga : " + caught.getMessage());
				acceptDialog.setEnabled(true);
			}
		});
	}
	
	// ------------------------------------------------- Abstract Methods
	
	protected abstract void onTransformDone();
}
