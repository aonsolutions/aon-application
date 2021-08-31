package com.esferalia.aon.gwt.payroll.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class AgreementVariablesDialog extends AonCustomDialog {

	// --------------------------------------------------- UiBinder
	
	interface AgreementVariablesDialogUiBinder extends UiBinder<Widget, AgreementVariablesDialog> {}
	
	private static AgreementVariablesDialogUiBinder binder = GWT.create(AgreementVariablesDialogUiBinder.class);
	
	// --------------------------------------------------- UiFields
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String flex();
		String flexColumn();
	}
	
	@UiField
	HTMLPanel container;
	
	@UiField
	HTMLPanel buttonsPanel;
	
	// --------------------------------------------------- Variables
	
	private Set<String> variables;
	private Set<String> shownVariables;
	private ListBox variablesTypeLB;
	private HTMLPanel variablesCBPanel = new HTMLPanel("");
	private Map<String, CheckBox> variablesMap = new HashMap<String, CheckBox>();
	
	// --------------------------------------------------- Variables.Footer
	
	private Button closeBtnDialog;
	private Button acceptBtnDialog;
	
	// --------------------------------------------------- Constructor
	
	public AgreementVariablesDialog(Set<String> variables, Set<String> shownVariables) {
		setCaption("Variables");
		setWidget(binder.createAndBindUi(this));
		this.variables = variables;
		this.shownVariables = shownVariables;
		createContainer();
		createFooterButtons();
		showDialog();
	}
	
	private void createContainer() {
		initVariablesTypePanel();
		initVariablesCheckBoxes();
		hideVariablesCBPanel();
	}

	private void initVariablesTypePanel() {
		HTMLPanel variablesTypePanel = new HTMLPanel("");
		variablesTypePanel.addStyleName(style.flex());
		Label variablesTypeL = new Label("VISTA VARIABLES :");
		initVariablesTypeLB();
		variablesTypePanel.add(variablesTypeL);
		variablesTypePanel.add(variablesTypeLB);
		container.add(variablesTypePanel);
	}
	
	private void initVariablesCheckBoxes() {
		variablesMap.clear();
		variablesCBPanel.clear();
		variablesCBPanel.addStyleName(style.flexColumn());
		variablesCBPanel.getElement().getStyle().setMarginTop(10, Unit.PX);
		
		for(String var : variables) {
			HTMLPanel flexPanel = new HTMLPanel("");
			flexPanel.addStyleName(style.flex());
			CheckBox varCB = new CheckBox();
			varCB.setValue(shownVariables.contains(var));
			Label varL = new Label(var);
			flexPanel.add(varCB);
			flexPanel.add(varL);
			variablesMap.put(var, varCB);
			variablesCBPanel.add(flexPanel);
		}
		container.add(variablesCBPanel);
	}

	private void initVariablesTypeLB() {
		variablesTypeLB = new ListBox();
		variablesTypeLB.addItem("CON VALOR", "VALUES");
		variablesTypeLB.addItem("SIN VALOR", "NO_VALUES");
		variablesTypeLB.addItem("TODAS", "ALL");
		variablesTypeLB.addItem("MANUAL", "MANUAL");
		variablesTypeLB.addChangeHandler(e -> {
			if(variablesTypeLB.getSelectedIndex() == 3)
				showVariablesCBPanel();
			else
				hideVariablesCBPanel();
		});
	}
	
	private void showVariablesCBPanel() {
		variablesCBPanel.getElement().getStyle().clearDisplay();
	}
	
	private void hideVariablesCBPanel() {
		variablesCBPanel.getElement().getStyle().setDisplay(Display.NONE);
	}

	// --------------------------------------------------- Footer
	
	private void createFooterButtons() {
		buttonsPanel.clear();
		
		closeBtnDialog = new Button();
		closeBtnDialog.setStyleName(AON.CSS.aonCancelButtonSmall());
		closeBtnDialog.setText( AON.MSG.cancelAction());
		closeBtnDialog.addClickHandler(e -> {
			hide();
		});
		
		acceptBtnDialog = new Button();
		acceptBtnDialog.setStyleName(AON.CSS.aonOkButtonSmall());
		acceptBtnDialog.setText( AON.MSG.accept());
		acceptBtnDialog.addClickHandler(e -> {
			onAcceptDialog();
		});
		
		buttonsPanel.add(closeBtnDialog);
		buttonsPanel.add(acceptBtnDialog);
	}
	
	private void onAcceptDialog() {
		String variablesType = this.variablesTypeLB.getSelectedValue();
		Set<String> variables = new HashSet<String>();
		
		for(Entry<String, CheckBox> entry : variablesMap.entrySet())
			if(entry.getValue().getValue())
				variables.add(entry.getKey());
		
		onAccept(variablesType, variables);
		
		hide();
	}
	
	// --------------------------------------------------- Abstract Methods
	
	protected abstract void onAccept(String variablesType, Set<String> variables);
	
	// --------------------------------------------------- Show Dialog
	
	public void showDialog() {
		// Show center
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				center();
				show();
			}
		});
	}
}
