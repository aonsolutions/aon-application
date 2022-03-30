package com.esferalia.aon.gwt.payroll.client;

import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.payroll.shared.SSPECData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class SSPECDraft extends Composite {
	
	private static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat("dd/MM/yyyy");

	// -------------------------------------------------- UiBinder
	
	interface SSPECDraftUiBinder extends UiBinder<Widget, SSPECDraft> {}
	
	private static SSPECDraftUiBinder uiBinder = GWT.create(SSPECDraftUiBinder.class);
	
	// -------------------------------------------------- UiFields
			
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String hide();
		String buttonTable();
		String bold();
		String formulaStyle();
		String descriptionStyle();
		String dateStyle();
		String selected();
		String selectedBG();
		String unSelected();
	}
	
	@UiField
	HTMLPanel toolbarPanel;
	
	@UiField
	HTMLPanel messagePanel;
	
	@UiField
	Grid listPECsTable;
	
	// -------------------------------------------------- Variables
		
	private SSPECObject ssPECObject;
	private AonToolbar toolbar;
	
	// -------------------------------------------------- Constructor

	public SSPECDraft() {
		
		// Inicializamos la vista de la actividad
		initWidget(uiBinder.createAndBindUi(this));
		createToolbar();
	}

	// -------------------------------------------------- SetContractSSPECObject
	
	public void setContractSSPECObject(SSPECObject ssPECObject) {
		showLoading("Obteniendo bonifinicaciones y peculiaridades...");
		this.ssPECObject = ssPECObject;
		this.ssPECObject.getSSPECData(
				s -> {
					hideMessage();
					showTable();
				}, 
				f -> showError("Error obtenci\u00f3n", f.getMessage()));
	}
	
	// -------------------------------------------------- Table methods
	
	private void showTable() {
		cleanSelected();
		showListPECs();
	}
	
	private void showListPECs() {
		clearListPECs();
		
		this.listPECsTable.resize(ssPECObject.getSSPECDataList().size() + 1, this.listPECsTable.getColumnCount());
		
		this.listPECsTable.setWidget(0, 0, new Label(""));
		this.listPECsTable.setWidget(0, 1, new Label("Fecha Incio"));
		this.listPECsTable.getWidget(0, 1).addStyleName(style.dateStyle());
		this.listPECsTable.setWidget(0, 2, new Label("Fecha Fin"));
		this.listPECsTable.getWidget(0, 2).addStyleName(style.dateStyle());
		this.listPECsTable.setWidget(0, 3, new Label("Descripci"+String.valueOf("\u00F3")+"n"));
		this.listPECsTable.getWidget(0, 3).addStyleName(style.descriptionStyle());
		this.listPECsTable.setWidget(0, 4, new Label(""));
		
		for(int i = 0; i < this.listPECsTable.getColumnCount(); i++)
			this.listPECsTable.getWidget(0, i).addStyleName(style.bold());
		
		int row = 1;
		
		for(SSPECData pec : ssPECObject.getSSPECDataList()){
			Label system = new Label();
			if(Boolean.TRUE.equals(pec.isSystem()))
				system.setStyleName("aon-icon-rowSelector-S aon-editDataTable-button");
			else
				system.setStyleName("aon-icon-rowSelector aon-editDataTable-button");
			listPECsTable.setWidget(row, 0, system);
			listPECsTable.setWidget(row, 1, new Label((null == pec.getStartDate()) ? "" : DATE_FORMAT.format(pec.getStartDate())));
			listPECsTable.setWidget(row, 2, new Label((null == pec.getEndDate()) ? "" : DATE_FORMAT.format(pec.getEndDate())));
			listPECsTable.setWidget(row, 3, new Label( pec.getDescription()));
						
			row++;
		}
		cleanSelected();
	}
	
	private void clearListPECs() {
		this.listPECsTable.clear();
	}
	
	private void cleanSelected() {
		//Set Unselected
		int rows = this.listPECsTable.getRowCount();
		for(int itRow = 0; itRow < rows; itRow++){
			this.listPECsTable.getRowFormatter().removeStyleName(itRow, style.selected());
		}
	}

	// -------------------------------------------------- Toolbar
	
	private void createToolbar() {
		toolbarPanel.clear();
		
		toolbar = new AonToolbar();
		
		AonToolbarButton addBtn = new AonToolbarButton("Nueva peculiaridad", AON.CSS.aonIconAdd());
		addBtn.addClickHandler(e -> new EmployeePeculiaritiesDialog(ssPECObject.getContractId(), ssPECObject.getContractStartDate()));
		toolbar.add(addBtn);
		
		AonToolbarButton syncBtn = new AonToolbarButton("Sincronizaci\u00f3n TGSS", AON.CSS.aonIconTgss());
		syncBtn.addClickHandler(e -> {
			showLoading("Sincronizando bonifinicaciones y peculiaridades TGSS...");
			this.ssPECObject.syncSSPECData(
					s -> {
						showSuccess("Sincronizaci\u00f3n TGSS", "Bonificaciones y peculiaridades sincronizadas correctamente");
						showTable();
					}, 
					f -> showError("Error sincronizaci\u00f3n TGSS", f.getMessage()));
		});
		toolbar.add(syncBtn);
		
		toolbarPanel.add(toolbar);
	}

	public void setToolbarTitle(String employeeName) {
		toolbar.setTitle(employeeName);
	}
	
	// ------------------------------------------------- Aon Messages panel

	private void showSuccess(String title, String message) {
		Map<String, String> successMap = new HashMap<>();
		successMap.put(title, message);
		AonMessagePanel.showSuccess(messagePanel, successMap);
	}
	
	private void showError(String title, String message) {
		Map<String, String> errorMap = new HashMap<>();
		errorMap.put(title, message);
		AonMessagePanel.showError(messagePanel, errorMap);
	}
	
	private void showLoading(String message) {
		AonMessagePanel.showLoading(messagePanel, message);
	}
	
	private void hideMessage() {
		AonMessagePanel.hideMessage(messagePanel);
	}
}
