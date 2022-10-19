package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.ProgressPanel;
import com.esferalia.aon.gwt.common.client.widget.ProgressPanel.Task;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.payroll.shared.SSPECData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.formatters.DateFormat;

public class SSPECDialog extends AonCustomDialog {
	
	private static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat("dd/MM/yyyy");

	// -------------------------------------------------- UiBinder --------------------------------------------------
	
	interface Binder extends UiBinder<Widget, SSPECDialog> {}

	private static final Binder binder = GWT.create(Binder.class);
	

	// -------------------------------------------------- UiFields --------------------------------------------------
			
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
	Grid listPECsTable;
	
	@UiField
	HTMLPanel buttonsPanel;
	
	@UiField
	ProgressPanel progressPanel;
	
// ------------------------------------------------------------ VARIABLES DE LA CLASE ----------------------------------------------------
		
	private Integer contractId;
	private List<SSPECData> ssPECs;
	
	private Button closeBtnDialog;

// ---------------------------------------------------------------- CONSTRUCTOR ----------------------------------------------------------

	private final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	
	public SSPECDialog(Integer contractId, Date startDate, Date endDate) {
		
		
		setCaption(
		"Peculiaridades de Cotizaci\u00F3n (SISTEMA RED) " 
		+ DATE_FORMAT.format(startDate) + "..." + ( endDate != null ? DATE_FORMAT.format(endDate) : "" ) );
		
		setWidget(binder.createAndBindUi(this));
		
		getButtonsPanel();
		

		
		this.ssPECs = new ArrayList<>();
		this.contractId = contractId;

		HandlerRegistration handlerRegistration [] = new HandlerRegistration[1];
		handlerRegistration[0] = progressPanel.addAttachHandler(e -> {
			// Synchronize cret@ messages.
			Task syncTask = new Task();
			syncTask.setDescription("Comprobando peculiaridades...");
			progressPanel.showTask(syncTask);
			syncTask.messageChanged("SISTEMA RED...");
			handlerRegistration[0].removeHandler();
		});

		showDialog();
		
		impl.syncEmployeeSSPECs(this.contractId, startDate, endDate, new AsyncCallback<List<SSPECData>>() {
			
			@Override
			public void onSuccess(List<SSPECData> result) {
				hideProgressPanel();
				ssPECs = result;
				showTable();
			}

			@Override
			public void onFailure(Throwable caught) {
				hideProgressPanel();
			}

			protected void hideProgressPanel() {
				progressPanel.setVisible(false);
			}
		});

	}
	
	private void showTable() {
		cleanSelected();
		showListPECs();
	}
	
	
// ----------------------------------------------------------------- UiHandlers ----------------------------------------------------------
	
	private void cleanSelected() {
		//Set Unselected
		int rows = this.listPECsTable.getRowCount();
		for(int itRow = 0; itRow < rows; itRow++){
			this.listPECsTable.getRowFormatter().removeStyleName(itRow, style.selected());
		}
	}

	private void clearListPECs() {
		this.listPECsTable.clear();
	}
	

	private void showListPECs() {
		clearListPECs();
		
		this.listPECsTable.resize(getPECs().size() + 1, this.listPECsTable.getColumnCount());
		
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
		
		for(SSPECData pec : getPECs()){
			Label system = new Label();
			if(pec.isSystem())
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

	

	
	// ---------------------------------------------------------------------------------------------------------------------------------------------
	// 														PRIVATE METHODS
	// ---------------------------------------------------------------------------------------------------------------------------------------------
	
	public List<SSPECData> getPECs(){
		return this.ssPECs;
	}

	private void getButtonsPanel() {
		closeBtnDialog = new Button();
		closeBtnDialog.setStyleName(AON.CSS.aonCancelButtonSmall());
		closeBtnDialog.setText( "Cerrar");
		closeBtnDialog.setAccessKey('C');
		closeBtnDialog.addClickHandler(e -> hide());
		
		buttonsPanel.add(closeBtnDialog);
	}
	
	public void showDialog() {
		// Show center
		Scheduler.get().scheduleDeferred(() -> {
			center();
			show();
		});
	}
}
