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
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
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
	private ScrollPanel variablesScroll = new ScrollPanel();
	private Map<String, CheckBox> variablesMap = new HashMap<String, CheckBox>();
	
	// --------------------------------------------------- Variables.Footer
	
	private Button acceptBtnDialog;
	
	// --------------------------------------------------- Constructor
	
	protected AgreementVariablesDialog(Set<String> variables, Set<String> shownVariables) {
		setCaption("Selecci\u00D3n Variables");
		setWidget(binder.createAndBindUi(this));
		this.showCloseButton(true);
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
		Label variablesTypeL = new Label("VER :");
		initVariablesTypeLB();
		variablesTypePanel.add(variablesTypeL);
		variablesTypePanel.add(variablesTypeLB);
		container.add(variablesTypePanel);
	}
	
	private void initVariablesCheckBoxes() {
		variablesMap.clear();
		
		variablesScroll.setHeight(variables.size() > 7 ? "200px" : (variables.size() * 28.5) + "px");
		
		HTMLPanel variablesCBPanel = new HTMLPanel("");
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
		
		variablesScroll.add(variablesCBPanel);
		container.add(variablesScroll);
	}

	private void initVariablesTypeLB() {
		variablesTypeLB = new ListBox();
		variablesTypeLB.addItem("CON VALOR ASIGNADO", "VALUES");
		variablesTypeLB.addItem("SIN VALOR", "NO_VALUES");
		variablesTypeLB.addItem("TODAS LAS DEFINIDAS", "ALL");
		variablesTypeLB.addItem("SELECCI\u00D3N PERSONALIZADA", "MANUAL");
		variablesTypeLB.addChangeHandler(e -> {
			if(variablesTypeLB.getSelectedIndex() == 3) {
				showVariablesCBPanel();
				showDialog();
			} else
				hideVariablesCBPanel();
		});
	}
	
	private void showVariablesCBPanel() {
		variablesScroll.getElement().getStyle().clearDisplay();
	}
	
	private void hideVariablesCBPanel() {
		variablesScroll.getElement().getStyle().setDisplay(Display.NONE);
	}

	// --------------------------------------------------- Footer
	
	private void createFooterButtons() {
		buttonsPanel.clear();
		
		acceptBtnDialog = new Button();
		acceptBtnDialog.setStyleName(AON.CSS.aonOkButtonSmall());
		acceptBtnDialog.setText( AON.MSG.accept());
		acceptBtnDialog.addClickHandler(e -> {
			onAcceptDialog();
		});
		
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

	public void setShowVariables(String shownVariables) {
		switch (shownVariables) {
			case "VALUES":
				variablesTypeLB.setSelectedIndex(0);
				break;
			case "NO_VALUES":
				variablesTypeLB.setSelectedIndex(1);
				break;
			case "ALL":
				variablesTypeLB.setSelectedIndex(2);
				break;
			default:
				variablesTypeLB.setSelectedIndex(3);
				break;
		}
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), variablesTypeLB);
	}

}
