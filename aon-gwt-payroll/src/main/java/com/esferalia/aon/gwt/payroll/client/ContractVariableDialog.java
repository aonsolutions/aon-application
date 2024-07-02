package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.payroll.shared.ContractVariable;
import com.esferalia.aon.gwt.payroll.shared.ContractVariable.VariableType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class ContractVariableDialog extends AonCustomDialog {
	
	// ------------------------------------------------- UIBinder
	
	interface Certifica2DialogUIBinder extends UiBinder<Widget, ContractVariableDialog> {}

	private static final Certifica2DialogUIBinder binder = GWT.create(Certifica2DialogUIBinder.class);
	
	// ------------------------------------------------- UIFileds
	
	@UiField
	HTMLPanel messagePanel;
	
	@UiField
	ListBox variableType;
	
	@UiField
	TextBox variableName;
	
	@UiField
	TextBox variableValue;
	
	@UiField
	DateBoxEx startDateBx;
	
	@UiField
	DateBoxEx endDateBx;
	
	@UiField
	HTMLPanel buttonsPanel;
	
	// ------------------------------------------------- Variables
	
	private ContractVariable contractVariable;
	
	private Date contractStartDate;
	private Date contractEndDate;
	
	// ------------------------------------------------- Constructor
	
	protected ContractVariableDialog(ContractVariable selectedContractVariable, Date contractStartDate, Date contractEndDate) {
		setCaption("Variables contrato");
		setWidget(binder.createAndBindUi(this));
		this.contractVariable = selectedContractVariable;
		
		this.contractStartDate = contractStartDate;
		this.contractEndDate = contractEndDate;
		
		initializeView();
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this.variableType);
	}

	// ------------------------------------------------- Constructor Methods
	
	private void initializeView() {
		initListBox();
		initHandlers();
		getButtonsPanel();
		if(null != this.contractVariable)
			fillContractVariable();
		showDialog();
	}

	private void initListBox() {
		this.variableType.clear();
		this.variableType.addItem("Contract Data", "CONTRACT_DATA");
		this.variableType.addItem("Contract Info", "CONTRACT_INFO");
	}

	private void initHandlers() {
		variableType.addChangeHandler(e -> {
			checkIfExistContractVariable();
			contractVariable.setVariableType(VariableType.valueOf(variableType.getSelectedValue()));
		});
		
		variableName.addValueChangeHandler(e -> {
			checkIfExistContractVariable();
			contractVariable.setDescription(e.getValue());
		});
		
		variableValue.addValueChangeHandler(e -> {
			checkIfExistContractVariable();
			contractVariable.setExpression(e.getValue());
		});
		
		startDateBx.addValueChangeHandler(e -> {
			checkIfExistContractVariable();
			contractVariable.setStartDate(e.getValue());
		});
		
		endDateBx.addValueChangeHandler(e -> {
			checkIfExistContractVariable();
			contractVariable.setEndDate(e.getValue());
		});
	}

	private void checkIfExistContractVariable() {
		if(null == this.contractVariable)
			this.contractVariable = new ContractVariable();
	}

	private void fillContractVariable() {
		setSelectedValueLB(variableType, contractVariable.getVariableType().name());
		this.variableName.setValue(contractVariable.getDescription());
		this.variableValue.setValue(contractVariable.getExpression());
		this.startDateBx.setValue(contractVariable.getStartDate());
		this.endDateBx.setValue(contractVariable.getEndDate());
	}

	// ------------------------------------------------- Auxiliar Methods
	
	public void showDialog() {
		// Show center
		Scheduler.get().scheduleDeferred(() -> {
			center();
			show();
		});
	}
	
	private void setSelectedValueLB(ListBox lBox, String str) {
	    String text = str;
	    int indexToFind = 0;
	    for (int i = 0; i < lBox.getItemCount(); i++) {
	        if (lBox.getValue(i).equals(text)) {
	            indexToFind = i;
	            break;
	        }
	    }
	    lBox.setSelectedIndex(indexToFind);
	}
	
	// ------------------------------------------------- ButtonsPanel
	
	private void getButtonsPanel() {
		Button closeBtnDialog = new Button();
		closeBtnDialog.setStyleName(AON.CSS.aonCancelButtonSmall());
		closeBtnDialog.setText("Cancelar");
		closeBtnDialog.addClickHandler(e -> hide());
		
		buttonsPanel.add(closeBtnDialog);
		
		Button acceptBtnDialog = new Button();
		acceptBtnDialog.setStyleName(AON.CSS.aonOkButtonSmall());
		acceptBtnDialog.setText("Grabar");
		acceptBtnDialog.addClickHandler(e -> accept());
		
		buttonsPanel.add(acceptBtnDialog);
	}

	private void accept() {
		Map<String, String> saveMessage = canSave();
		if(saveMessage.isEmpty()) {
			
			if(null != startDateBx.getValue() && startDateBx.getValue().before(contractStartDate)) {
				AonDialog warningDialog = new AonDialog("Fecha inicio", new HTMLPanel("La fecha de inicio de la variable es anterior a la fecha de inicio del contrato. \u00BFDesea continuar igualmente?"));
				warningDialog.confirm(new AonAcceptDialogCallback() {
					
					@Override
					public void onCancel() { 
						saveMessage.put("Fecha inicio", "La fecha de inicio de la variable es anterior a la fecha de inicio del contrato"); 
						AonMessagePanel.showError(messagePanel, saveMessage);
					}
					
					@Override
					public void onAccept() {
						checkContractVariableValue();
						onAcceptDialog(contractVariable);
						hide();
					}
				});
			}
			
			else if(null != endDateBx.getValue() && null != contractEndDate && endDateBx.getValue().after(contractEndDate)) {
				AonDialog warningDialog = new AonDialog("Fecha fin", new HTMLPanel("La fecha fin de la variable es posterior a la fecha fin del contrato. \u00BFDesea continuar igualmente?"));
				warningDialog.confirm(new AonAcceptDialogCallback() {
					
					@Override
					public void onCancel() { 
						saveMessage.put("Fecha fin", "La fecha fin de la variable es posterior a la fecha fin del contrato"); 
						AonMessagePanel.showError(messagePanel, saveMessage);
					}
					
					@Override
					public void onAccept() {
						checkContractVariableValue();
						onAcceptDialog(contractVariable);
						hide();
					}
				});
			}
			
			else {
				checkContractVariableValue();
				onAcceptDialog(contractVariable);
				hide();
			}
			
			
		} else
			AonMessagePanel.showError(messagePanel, saveMessage);
	}

	private void checkContractVariableValue() {
		ArrayList<String> quoteVariables = new ArrayList<>();
		quoteVariables.add("CNO");
		quoteVariables.add("OCUPACION");
		quoteVariables.add("TC2");
		quoteVariables.add("GRUPO_COTIZACION");
		quoteVariables.add("OPCION_CONTRATO");
		
		if( quoteVariables.contains(contractVariable.getDescription()) && 
			AonStringUtils.isNotBlank(contractVariable.getExpression()) && 
			!contractVariable.getExpression().contains("\"")) {
			
			contractVariable.setExpression("\"" + contractVariable.getExpression() + "\"");
		}
	}

	private Map<String, String> canSave() {
		Map<String, String> saveMessage = new HashMap<>();
		
		if(AonStringUtils.isBlank(variableName.getValue())) saveMessage.put("Nombre", "El nombre de la variable es obligatorio");
		if(AonStringUtils.isBlank(variableValue.getValue())) saveMessage.put("Valor", "El valor de la variable es obligatorio");
		if(null == startDateBx.getValue()) saveMessage.put("Fecha inicio", "La fecha de inicio de la variable es obligatoria");
		
		if(null != startDateBx.getValue() && null != endDateBx.getValue() && endDateBx.getValue().before(startDateBx.getValue())) saveMessage.put("Fecha fin", "La fecha fin de la variable es anterior a la fecha de inicio de la variable");
		
		return saveMessage;
	}
	
	// ------------------------------------------------- AbstractMethods
	
	protected abstract void onAcceptDialog(ContractVariable createVariable);
	
}
