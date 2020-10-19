package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.payroll.shared.ContractAttach;
import com.esferalia.aon.gwt.payroll.shared.ContractClause;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ContractClauseUI extends ResizeComposite {

	// -------------------------------------------------- UiBinder --------------------------------------------------

	private static ContractClauseUIBinder uiBinder = GWT.create(ContractClauseUIBinder.class);

	interface ContractClauseUIBinder extends UiBinder<Widget, ContractClauseUI> {}

	// -------------------------------------------------- UiFields --------------------------------------------------

	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String headerLabelStyle();
		String maxWidthTB();
		String maxWidthLB();
		String clauseTD();
	}
	
	@UiField
	VerticalPanel clausesTable;

	@UiField
	Grid clausesDataTableHeader;
	
	@UiField
	ScrollPanel clausesScrollPanel;
	
	@UiField
	Grid clausesDataTable;

	@UiField
	Label newClause;
	
	// ------------------------------------------------------ Constructor ---------------------------------------------------------

	private DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	private EmployeeContractInfo employeeContractInfo;
	
	public ContractClauseUI() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public void setEmployeeContractInfo(EmployeeContractInfo employeeContractInfoIn) {
		this.employeeContractInfo = employeeContractInfoIn;
		initializeView();
		
		for(ContractClause contractClause : employeeContractInfo.getContractClauses())
			paintContractClause(contractClause);
	}
	
	// --------------------------------------------------------- UiHandlers --------------------------------------------------------
	
	@UiHandler("newClause")
	public void onNewClauseClick(ClickEvent event) {
		ContractClause contractClause = new ContractClause();
		contractClause.setDomain(employeeContractInfo.getEmployeeInfo().getDomain());
		contractClause.setContract(employeeContractInfo.getContractInfo().getContractId());
		contractClause.setLineNumber((short)(employeeContractInfo.getContractClauses().size()+1));
		contractClause.setDescription("");
		
		createContractClause(contractClause,
				s -> {
					
					resetClauseDataTableStructure();
					
					for(ContractClause contractClauseIn : employeeContractInfo.getContractClauses())
						paintContractClause(contractClauseIn);
					
				}, f -> {});
	}
	
	// --------------------------------------------------- UiHandlers (Aux Methods) -------------------------------------------------
	
	private void resetClauseDataTableStructure() {
		clausesDataTable.clear();
		clausesDataTable.resize(0, 0);
		clausesDataTable.resizeColumns(4);
		
		setColumnsWidth();
	}
	
	// -------------------------------------------------- Paint Table Header Methods --------------------------------------------------

	private void paintHeaderClausesTable() {
		int row = clausesDataTableHeader.insertRow(clausesDataTableHeader.getRowCount());
		
		Label line = new Label("L" + String.valueOf("\u00CD") + "NEA");
		Label name = new Label("NOMBRE");
		Label description = new Label("DESCRIPCI" + String.valueOf("\u00D3") + "N");
		Label action = new Label("");
		
		line.addStyleName(style.headerLabelStyle());
		name.addStyleName(style.headerLabelStyle());
		description.addStyleName(style.headerLabelStyle());
		action.addStyleName(style.headerLabelStyle());
		
		clausesDataTableHeader.setWidget(row, 0, line);
		clausesDataTableHeader.setWidget(row, 1, name);
		clausesDataTableHeader.setWidget(row, 2, description);
		clausesDataTableHeader.setWidget(row, 3, action);
	}
	
	// ------------------------------------------------------ Paint Table Methods -----------------------------------------------------

	private void paintContractClause(ContractClause contractClause) {
		// Insert new row
		int row = this.clausesDataTable.insertRow(clausesDataTable.getRowCount());
		
		// Line TextBox
		TextBox lineTB = new TextBox();
		lineTB.addValueChangeHandler((e) -> {
			contractClause.setLineNumber(Short.parseShort(e.getValue()));
		});
		
		// Name TextBox
		TextBox nameTB = new TextBox();
		nameTB.addValueChangeHandler((e) -> {
			contractClause.setName(e.getValue());
		});
		
		// Description TextBox
		TextArea descriptionTA = new TextArea();
		descriptionTA.addValueChangeHandler((e) -> {
			contractClause.setDescription(e.getValue());
		});
		
		// Delete Button
		Button deleteBTN = new Button();
		deleteBTN.addClickHandler((e) -> {
			deleteContractClause(contractClause, 
					s -> {
						resetClauseDataTableStructure();
						for(ContractClause contractClauseIn : employeeContractInfo.getContractClauses())
							paintContractClause(contractClauseIn);
					}, f -> {});
		});
		
		// Add Styles
		lineTB.addStyleName(style.maxWidthTB());
		nameTB.addStyleName(style.maxWidthTB());
		descriptionTA.addStyleName(style.maxWidthTB());
		descriptionTA.setHeight("95px");
		deleteBTN.setStyleName("aon-editDataTable-button aon-icon-delete");
		
		// If id != null exists then fill the fields
		if(null != contractClause.getId()) {
			lineTB.setText(contractClause.getLineNumber() + "");
			nameTB.setText(contractClause.getName());
			descriptionTA.setValue(contractClause.getDescription());
		}
		
		// It null == contract ? global_attachs cant be deleted
		if(null == contractClause.getContract()) {
			lineTB.setReadOnly(true);
			nameTB.setReadOnly(true);
			descriptionTA.setReadOnly(true);
		}
		
		clausesDataTable.setWidget(row, 0, lineTB);
		clausesDataTable.setWidget(row, 1, nameTB);
		clausesDataTable.setWidget(row, 2, descriptionTA);
		if(null != contractClause.getContract())
			clausesDataTable.setWidget(row, 3, deleteBTN);
		else
			clausesDataTable.setWidget(row, 3, new Label());
		
		clausesDataTable.getCellFormatter().addStyleName(row, 0, style.clauseTD());
		clausesDataTable.getCellFormatter().addStyleName(row, 1, style.clauseTD());
		clausesDataTable.getCellFormatter().addStyleName(row, 3, style.clauseTD());
	}	
	
	// ------------------------------------------------------ Auxiliar Methods ----------------------------------------------------
	
	private void initializeView() {
		initClausesTable();
		paintHeaderClausesTable();
		
		setScrollPanelsHeight();
		setColumnsWidth();
	}

	private void initClausesTable() {
		clausesDataTableHeader.clear();
		clausesDataTableHeader.resize(0, 0);
		clausesDataTableHeader.resizeColumns(4);
		clausesDataTable.clear();
		clausesDataTable.resize(0, 0);
		clausesDataTable.resizeColumns(4);
	}
	
	private void setScrollPanelsHeight() {
		clausesScrollPanel.setHeight((Window.getClientHeight() - 340) + "px");
	}
	
	private void setColumnsWidth() {
		clausesDataTableHeader.getCellFormatter().getElement(0, 0).getStyle().setWidth(100, Unit.PX);
		clausesDataTableHeader.getCellFormatter().getElement(0, 1).getStyle().setWidth(200, Unit.PX);
		clausesDataTableHeader.getCellFormatter().getElement(0, 2).getStyle().setWidth(595, Unit.PX);
		clausesDataTableHeader.getCellFormatter().getElement(0, 3).getStyle().setWidth(50, Unit.PX);
		
		clausesDataTable.getColumnFormatter().getElement(0).getStyle().setWidth(100, Unit.PX);
		clausesDataTable.getColumnFormatter().getElement(1).getStyle().setWidth(200, Unit.PX);
		clausesDataTable.getColumnFormatter().getElement(2).getStyle().setWidth(595, Unit.PX);
		clausesDataTable.getColumnFormatter().getElement(3).getStyle().setWidth(50, Unit.PX);
		
		clausesDataTable.getColumnFormatter().addStyleName(0, style.clauseTD());
		clausesDataTable.getColumnFormatter().addStyleName(1, style.clauseTD());
		clausesDataTable.getColumnFormatter().addStyleName(3, style.clauseTD());	
	}

	// --------------------------------------------------------------------------------------------------------
	
	private void createContractClause(ContractClause contractClause, Consumer<List<ContractClause>> success, Consumer<Throwable> failure) {
		impl.createContractClause(contractClause, new AsyncCallback<List<ContractClause>>() {
			
			@Override
			public void onSuccess(List<ContractClause> result) {
				employeeContractInfo.setContractClauses(result);
				success.accept(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
		
	}

	private void deleteContractClause(ContractClause contractClause, Consumer<List<ContractClause>> success, Consumer<Throwable> failure) {
		impl.deleteContractClause(contractClause, new AsyncCallback<List<ContractClause>>() {
			
			@Override
			public void onSuccess(List<ContractClause> result) {
				employeeContractInfo.setContractClauses(result);
				success.accept(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
		
	}

}
