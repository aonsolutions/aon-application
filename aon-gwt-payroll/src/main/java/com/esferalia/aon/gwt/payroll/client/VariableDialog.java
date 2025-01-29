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
import com.esferalia.aon.gwt.payroll.client.Variables.Variable;
import com.esferalia.aon.gwt.payroll.shared.ContractVariable;
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

public abstract class VariableDialog extends AonCustomDialog {
	
	// ------------------------------------------------- UIBinder
	
	interface VariableDialogUIBinder extends UiBinder<Widget, VariableDialog> {}

	private static final VariableDialogUIBinder binder = GWT.create(VariableDialogUIBinder.class);
	
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
	
	private Date startDate;
	private Date endDate;
	
	private Variable<?> variable;
	
	// ------------------------------------------------- Constructor
	
	protected VariableDialog(Variable<?> selectedVariable, Date startDate, Date endDate) {
		setCaption("Variables contrato");
		setWidget(binder.createAndBindUi(this));
		this.variable = selectedVariable;
		
		this.startDate = startDate;
		this.endDate = endDate;
		
		initializeView();
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this.variableType);
	}

	// ------------------------------------------------- Constructor Methods
	
	private void initializeView() {
		initListBox(this.variableType);
		initHandlers();
		getButtonsPanel();
		if(null != this.variable)
			fillContractVariable();
		showDialog();
	}

	private void initHandlers() {
		variableType.addChangeHandler(e -> {
			checkIfExistContractVariable();
			variable.setVariableType(getVariableType(variableType.getSelectedValue()));
		});
		
		variableName.addValueChangeHandler(e -> {
			checkIfExistContractVariable();
			variable.setDescription(e.getValue());
		});
		
		variableValue.addValueChangeHandler(e -> {
			checkIfExistContractVariable();
			variable.setExpression(e.getValue());
		});
		
		startDateBx.addValueChangeHandler(e -> {
			checkIfExistContractVariable();
			variable.setStartDate(e.getValue());
		});
		
		endDateBx.addValueChangeHandler(e -> {
			checkIfExistContractVariable();
			variable.setEndDate(e.getValue());
		});
	}

	private void checkIfExistContractVariable() {
		if(null == this.variable)
			this.variable = newVariable();
	}

	private void fillContractVariable() {
		setSelectedValueLB(variableType, variable.getVariableType().name());
		this.variableName.setValue(variable.getDescription());
		this.variableValue.setValue(variable.getExpression());
		this.startDateBx.setValue(variable.getStartDate());
		this.endDateBx.setValue(variable.getEndDate());
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
			
			if(null != startDateBx.getValue() && startDateBx.getValue().before(startDate)) {
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
						onAcceptDialog(variable);
						hide();
					}
				});
			}
			
			else if(null != endDateBx.getValue() && null != endDate && endDateBx.getValue().after(endDate)) {
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
						onAcceptDialog(variable);
						hide();
					}
				});
			}
			
			else {
				checkContractVariableValue();
				onAcceptDialog(variable);
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
		
		if( quoteVariables.contains(variable.getDescription()) && 
			AonStringUtils.isNotBlank(variable.getExpression()) && 
			!variable.getExpression().contains("\"")) {
			
			variable.setExpression("\"" + variable.getExpression() + "\"");
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
	protected abstract void initListBox(ListBox variableTypeListBox);
	
	protected abstract Variable<?> newVariable();
	
	protected abstract void onAcceptDialog(Variable<?> variable);
	
	protected abstract <T extends Enum<?>> T getVariableType(String value) ;

	
}
