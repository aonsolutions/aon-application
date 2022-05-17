package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.payroll.shared.ContractClause;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;

public abstract class ImportClauseDialog extends AonCustomDialog {
	
	// ------------------------------------------------- UIBinder
	
	interface ImportClauseDialogUIBinder extends UiBinder<Widget, ImportClauseDialog> {}

	private static final ImportClauseDialogUIBinder binder = GWT.create(ImportClauseDialogUIBinder.class);
	
	// ------------------------------------------------- UIFileds
	
	@UiField
	HTMLPanel messagePanel;
	
	@UiField(provided = true)
	DataGrid<ContractClause> clausesDG;
	
	@UiField
	HTMLPanel buttonsPanel;
	
	// ------------------------------------------------- Variables
	
	final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	private MultiSelectionModel<ContractClause> selectionModel;
	private List<ContractClause> domainClauses = new ArrayList<>();
	
	private Button importBtnDialog;
	
	// ------------------------------------------------- Constructor
	
	protected ImportClauseDialog(List<ContractClause> existingClauses) {
		
		setCaption("Clausulas");
		provideClausesDataGrid();
		setWidget(binder.createAndBindUi(this));
		setGridHeight();
		getButtonsPanel();
		enableAcceptButton(false);
		
		impl.getDomainClauses(new AsyncCallback<List<ContractClause>>() {

			@Override
			public void onFailure(Throwable caught) {
				showError("Clausulas", caught.getMessage());
			}

			@Override
			public void onSuccess(List<ContractClause> clausesListDB) {
				List<ContractClause> clausesList = clausesListDB;
				// Solo se filtra si existe una lista que filtrar
				if(null != existingClauses && !existingClauses.isEmpty())
					clausesList = filterClauses(clausesListDB, existingClauses);
				
				domainClauses = clausesList;
				initClausesTable();
				showDialog();
			}

			private List<ContractClause> filterClauses(List<ContractClause> clausesListDB, List<ContractClause> existingClauses) {
				List<ContractClause> clausesList = new ArrayList<>();
				for(ContractClause contractClause : clausesListDB)
					if(!existClause(contractClause, existingClauses))
						clausesList.add(contractClause);
				
						return clausesList;
			}

			private boolean existClause(ContractClause contractClause, List<ContractClause> existingClauses) {
				for(ContractClause existingClause : existingClauses)
					if(AonStringUtils.equalsIgnoreCase(contractClause.getName(), existingClause.getName()))
						return true;
				return false;
			}
		});
		
	}

	private void setGridHeight() {
		this.clausesDG.setHeight("300px");
	}

	// ------------------------------------------ Provied DataGrid

	private void provideClausesDataGrid() {
		domainClauses = Collections.emptyList();
		
		// Resource Style CellTable
		clausesDG = new CustomDataGrid<>(Integer.MAX_VALUE, ContractClause.KEY_PROVIDER);
		
		//Do not refresh the headers every time the dataGrid is updated.
		clausesDG.setAutoHeaderRefreshDisabled(true);
		
		// Set the message to display when the table is empty.
		clausesDG.setEmptyTableWidget(new Label(("No existen clausulas para importar").toUpperCase()));
		
		// Add a selection model so we can select cells.
	    this.selectionModel = new MultiSelectionModel<>(ContractClause.KEY_PROVIDER);
	    clausesDG.setSelectionModel(this.selectionModel, DefaultSelectionEventManager.<ContractClause> createCheckboxManager());
	    
	    // Initialize the columns.
	    addColumns(this.selectionModel);
	    
	    new ListDataProvider<ContractClause>(Collections.emptyList()).addDataDisplay(clausesDG);
	    
	}
	
	private void addColumns(MultiSelectionModel<ContractClause> selectionModel) {
		
		selectionModel.addSelectionChangeHandler(e -> enableAcceptButton(!selectionModel.getSelectedSet().isEmpty()));
	    
		Column<ContractClause, Boolean> checkColumn = new Column<ContractClause, Boolean>(new CheckboxCell(true, false)) {
			@Override
			public Boolean getValue(ContractClause contractClause) {
				return selectionModel.isSelected(contractClause);
			}
	    };
    
	    CheckboxCell selectAllHeaderCB = new CheckboxCell(true,true);
	    Header<Boolean> selectAllHeader = new Header<Boolean>(selectAllHeaderCB) {
	    	@Override
	    	public Boolean getValue() {
	    		if(null == domainClauses) return false;
	    		return selectionModel.getSelectedSet().size() == domainClauses.size();
	    	}
	    	
	    };
	    
	    selectAllHeader.setUpdater(value -> domainClauses.forEach(salary -> selectionModel.setSelected(salary, value)));
	    
	    clausesDG.addColumn(checkColumn,selectAllHeader);
	    clausesDG.setColumnWidth(checkColumn, 5, Unit.PCT);
	    checkColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		// Line column.
	    TextColumn<ContractClause> lineColumn = new TextColumn<ContractClause>() {
	    	@Override
	    	public String getValue(ContractClause contractClause) {
	    		return contractClause.getLineNumber().toString();
	    	}
	    };

	    lineColumn.setSortable(true);
	    lineColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    clausesDG.setColumnWidth(lineColumn, 15, Unit.PCT);
	    
	    // Name column.
	    TextColumn<ContractClause> nameColumn = new TextColumn<ContractClause>() {
	    	@Override
	    	public String getValue(ContractClause contractClause) {
	    		return contractClause.getName();
	    	}
	    };

	    nameColumn.setSortable(true);
	    clausesDG.setColumnWidth(nameColumn, 25, Unit.PCT);
	    
	    // Description column.
	    TextColumn<ContractClause> descriptionColumn = new TextColumn<ContractClause>() {
	    	@Override
	    	public String getValue(ContractClause contractClause) {
	    		return contractClause.getDescription();
	    	}
	    };

	    descriptionColumn.setSortable(true);
	    
	    // General column.
	    ActionCell<ContractClause> generalActionCell = new ActionCell<>("", contractClause -> {});
	    Column<ContractClause, ContractClause> generalColumn = new Column<ContractClause, ContractClause>(generalActionCell) {

			@Override
			public ContractClause getValue(ContractClause contractClause) {
				return contractClause;
			}
			
			@Override
			public void render(Context context, ContractClause object, SafeHtmlBuilder sb) {
				if(null != object) {
					if(object.getGeneral() == (byte) 0)
						sb.appendHtmlConstant("<button type=\"button\" class=\"aon_button aon_table_button aon_icon_lock\" style=\"border: none !important; height: 20px;\" title=\"No general\"></button>");
					else
						sb.appendHtmlConstant("<button type=\"button\" class=\"aon_button aon_table_button aon_icon_unlock\" style=\"border: none !important; height: 20px;\" title=\"General\"></button>");
			
				}
			}
		};
		
		generalColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		clausesDG.setColumnWidth(generalColumn, 5, Unit.PCT);
		
	    // Add the columns.
		clausesDG.addColumn(lineColumn, "Linea");
		clausesDG.addColumn(nameColumn, "Nombre");
		clausesDG.addColumn(descriptionColumn, "Descripci\u00f3n");
		clausesDG.addColumn(generalColumn, "");
	}
	
	// ------------------------------------------ Init ClausesDG
	
	public void initClausesTable() {		
		//Reset Selection Model 
		selectionModel.clear();
		
		// Create a data provider.
	    ListDataProvider<ContractClause> dataProvider = new ListDataProvider<>();

	    // Connect the table to the data provider.
	    dataProvider.addDataDisplay(clausesDG);
	    
	    // Add the data to the data provider, which automatically pushes it to the widget.
	    List<ContractClause> clausesAuxList = dataProvider.getList();
	    clausesAuxList.clear();
	    
	    for (ContractClause contractClause : this.domainClauses) {
	    	clausesAuxList.add(contractClause);
	    }   
		
		addSortColums(clausesAuxList);
	    
		// Set page size
		clausesDG.setPageSize(clausesAuxList.size());
	}

	private void addSortColums(List<ContractClause> domainClauses) {
		ListHandler<ContractClause> columnSortHandler = new ListHandler<>(domainClauses);
		
	    columnSortHandler.setComparator(clausesDG.getColumn(1),
	    		(o1, o2) -> compareString(o1, o2, o1.getLineNumber()+"", o2.getLineNumber()+""));
	    
	    columnSortHandler.setComparator(clausesDG.getColumn(2),
	    		(o1, o2) -> compareString(o1, o2, o1.getName(), o2.getName()));
	    
	    columnSortHandler.setComparator(clausesDG.getColumn(3),
	    		(o1, o2) -> compareString(o1, o2, o1.getDescription(), o2.getDescription()));
	    
	    clausesDG.addColumnSortHandler(columnSortHandler);

	    // We know that the data is sorted alphabetically by default.
	    clausesDG.getColumn(1).setDefaultSortAscending(false);
	    clausesDG.getColumnSortList().push(clausesDG.getColumn(1));   
	}
	
	private int compareString(Object o1, Object o2, String s1, String s2) {
		if (o1 == o2) return 0;
		else if (o1 == null) return -1;
		else if (o2 == null) return 1;
		else
        	return s1.compareTo(s2);
	}

	// ------------------------------------------------- ShowDialog
	
	public void showDialog() {
		// Show center
		Scheduler.get().scheduleDeferred(() -> {
			center();
			show();
		});
	}
	
	// ------------------------------------------------- ButtonsPanel
	
	private void getButtonsPanel() {
		Button closeBtnDialog = new Button();
		closeBtnDialog.setStyleName(AON.CSS.aonCancelButtonSmall());
		closeBtnDialog.setText("Cerrar");
		closeBtnDialog.addClickHandler(e -> hide());
		
		buttonsPanel.add(closeBtnDialog);
		
		importBtnDialog = new Button();
		importBtnDialog.setStyleName(AON.CSS.aonOkButtonSmall());
		importBtnDialog.setText("Importar Clausulas");
		importBtnDialog.addClickHandler(e -> onClausesImport(getClausesIds()));
		
		buttonsPanel.add(importBtnDialog);
	}
	
	private List<Integer> getClausesIds() {
		return selectionModel.getSelectedSet().stream()
				.map(ContractClause::getId)
	            .collect(Collectors.toList());
	}

	private void enableAcceptButton(boolean enabled) {
		importBtnDialog.setEnabled(enabled);
		importBtnDialog.setTitle(enabled ? "Importar clausulas" : "Seleccione al menos una clausula para importar");
	}
	
	// ------------------------------------------------- AbstractMethods
	
	protected abstract void onClausesImport(List<Integer> clausesIds);

	// ------------------------------------------------- MessagePanel
	
	private void showError(String title, String message) {
		Map<String, String> errorMap = new HashMap<>();
		errorMap.put(title, message);
		AonMessagePanel.showError(messagePanel, errorMap);
	}
	
}
