package com.esferalia.aon.gwt.payroll.client;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.ContractVariable;
import com.esferalia.aon.gwt.payroll.shared.ContractVariable.VariableType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class EmployeeContractVariables extends Composite {

	// ----------------------------------------------- UiBinder 
	
	private static EmployeeContractVariablesUiBinder uiBinder = GWT.create(EmployeeContractVariablesUiBinder.class);

	interface EmployeeContractVariablesUiBinder extends UiBinder<Widget, EmployeeContractVariables> {}
	
	// ----------------------------------------------- ContractConceptCalcTypeCell 
	
	static class ContractVariablesTypeCell extends AbstractCell<String> {
	   
		
	    
	    @Override
	    public void render(Context context, String value, SafeHtmlBuilder sb) {
	      if (value == null)
	        return;
	      
	      sb.appendHtmlConstant("<b title=\"" + getContractVariableType(value) + "\">" + value + "</b>");
	    }
	    
	    static String getContractVariableType(String shortType) {
			if(AonStringUtils.isBlank(shortType))
				return "N/D";
	    	
	    	switch (shortType) {
				case "D":
					return "Contract Data";
				case "I":
					return "Contract Info";
				default:
					return "N/D";
			}
		}
	  }
	
	// ----------------------------------------------- UiField 
	
	@UiField
	DockLayoutPanel dockLayoutPanel;
	
	@UiField (provided = true)
	AonToolbar toolbar;
	
	@UiField(provided = true)
	DataGrid<ContractVariable> contractVariablesDG;
	
	// ----------------------------------------------- Variables 
	
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private EmployeeContractVariablesObject employeeContractVariablesObject;
	private List<ContractVariable> contractVariableList;
	
	private ListBox variableTypeLB;
	private ListBox yearLB;
	private ListBox monthLB;
	
	// ----------------------------------------------- Constructor 
	
	public EmployeeContractVariables() {
		initializeToolbarPanel();
		provideContractVariablesDG();
		initWidget(uiBinder.createAndBindUi(this));
		this.getElement().getStyle().setHeight(100, Unit.PCT);
		setScrollPanelHeight();
	}
	
	// ----------------------------------------------- Auxiliar Methods (Constructor & DataGrid) 
	
	private void setScrollPanelHeight() {
		contractVariablesDG.setHeight((Window.getClientHeight() - 230) + "px");
	}
	
	private String getVariableTypeShort(VariableType variableType) {
		switch (variableType) {
			case CONTRACT_DATA:
				return "D";
			case CONTRACT_INFO:
				return "I";
			default:
				return "N/D";
		}
	}
	
	// ----------------------------------------------- ProvideContractVariablesDG
	
	private void provideContractVariablesDG() {
		contractVariableList = Collections.emptyList();
		
		// Resource Style CellTable
		contractVariablesDG = new CustomDataGrid<>(Integer.MAX_VALUE, ContractVariable.KEY_PROVIDER);
		
		//Do not refresh the headers every time the dataGrid is updated.
		contractVariablesDG.setAutoHeaderRefreshDisabled(true);
		
		// Set the message to display when the table is empty.
		contractVariablesDG.setEmptyTableWidget(new Label(("No existen datos").toUpperCase()));
		
		// Initialize the columns.
	    addContractVariableColumns();
	    
	    new ListDataProvider<ContractVariable>(Collections.emptyList()).addDataDisplay(contractVariablesDG);   
	}
	
	private void addContractVariableColumns() {
		// Edit column.
	    ActionCell<ContractVariable> editActionCell = new ActionCell<>("", selectedContractVariable -> openContractVariableDialog(selectedContractVariable));
	    
	    Column<ContractVariable, ContractVariable> editColumn = new Column<ContractVariable, ContractVariable>(editActionCell) {

			@Override
			public ContractVariable getValue(ContractVariable contractVariable) {
				return contractVariable;
			}
			
			@Override
			public void render(Context context, ContractVariable contractVariable, SafeHtmlBuilder sb) {
				if(null != contractVariable) {
					sb.appendHtmlConstant("<button type=\"button\" class=\"aon_button aon_table_button aon_icon_right\" style=\"border: none !important; height: 20px;\" title=\"Editar\"></button>");
				}
			}
		};
		
		editColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		contractVariablesDG.setColumnWidth(editColumn, 5, Unit.PCT);
		
		// Type columns.
		Column<ContractVariable, String> typeColumn = new Column<ContractVariable, String>(new ContractVariablesTypeCell()) {
			@Override
	        public String getValue(ContractVariable contractVariable) {
				return getVariableTypeShort(contractVariable.getVariableType());
	        }
		};

		typeColumn.setSortable(true);
		typeColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		contractVariablesDG.setColumnWidth(typeColumn, 5, Unit.PCT);
		
		// Description column.
		Column<ContractVariable, String> descriptionColumn = new Column<ContractVariable, String>(new TextCell()) {
			@Override
			public String getValue(ContractVariable contractVariable) {
				return contractVariable.getDescription();
			}
		};
		
		descriptionColumn.setSortable(true);
		contractVariablesDG.setColumnWidth(descriptionColumn, 20, Unit.PCT);
	    
	    // Expression column.
	    Column<ContractVariable, String> expressionColumn = new Column<ContractVariable, String>(new TextCell()) {
	    	@Override
	        public String getValue(ContractVariable contractVariable) {
	    		return contractVariable.getExpression();
	        }
		};

	    expressionColumn.setSortable(true);
	    contractVariablesDG.setColumnWidth(expressionColumn, 35, Unit.PCT);
	    
	    // StartDate column.
	    Column<ContractVariable, String> startDateColumn = new Column<ContractVariable, String>(new TextCell()) {
	    	@Override
	        public String getValue(ContractVariable contractVariable) {
	    		return formatDate.format(contractVariable.getStartDate());
	        }
		};

	    startDateColumn.setSortable(true);
	    startDateColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    contractVariablesDG.setColumnWidth(startDateColumn, 10, Unit.PCT);
	    
	    // EndDate column.	    
	    Column<ContractVariable, String> endDateColumn = new Column<ContractVariable, String>(new TextCell()) {
			@Override
			public String getValue(ContractVariable contractVariable) {
				Date endDate = contractVariable.getEndDate();
				return endDate == null ? "" : formatDate.format(endDate);
			}
		};

	    endDateColumn.setSortable(true);
	    endDateColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    contractVariablesDG.setColumnWidth(endDateColumn, 10, Unit.PCT);
	    
	    // Delete column.
	    ActionCell<ContractVariable> deleteActionCell = new ActionCell<>("", contractVariable -> {
	    	AonDialog deleteDialog = new AonDialog("Eliminar variable", new HTML("\u00BFDesea eliminar la variable seleccionada\u003F"));
			deleteDialog.confirm(new AonAcceptDialogCallback() {
				
				@Override
				public void onCancel() {
					// Nothing to do here
				}
				
				@Override
				public void onAccept() {
					employeeContractVariablesObject.deleteContractVariable(contractVariable);
			    	onSave();
				}
			});
	    }); 
	    
	    Column<ContractVariable, ContractVariable> deleteColumn = new Column<ContractVariable, ContractVariable>(deleteActionCell) {

			@Override
			public ContractVariable getValue(ContractVariable contractVariable) {
				return contractVariable;
			}
			
			@Override
			public void render(Context context, ContractVariable contractVariable, SafeHtmlBuilder sb) {
				if(null != contractVariable) {
					sb.appendHtmlConstant("<button type=\"button\" class=\"aon_button aon_table_button aon_icon_delete\" style=\"border: none !important; height: 20px;\" title=\"Eliminar\"></button>");
				}
			}
		};
		
		deleteColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		contractVariablesDG.setColumnWidth(deleteColumn, 5, Unit.PCT);
		
	    // Add the columns.
		contractVariablesDG.addColumn(editColumn, "");
		contractVariablesDG.addColumn(typeColumn, "Tipo");
		contractVariablesDG.addColumn(descriptionColumn, "Descripci\u00F3n");
		contractVariablesDG.addColumn(expressionColumn, "Expresi\u00F3n");
		contractVariablesDG.addColumn(startDateColumn, "F. Inicio");
		contractVariablesDG.addColumn(endDateColumn, "F. Fin");
	    contractVariablesDG.addColumn(deleteColumn, "");  
	}
	
	// ----------------------------------------------- InitContractVariables
	
	public void initContractVariablesTable() {		
		// Create a data provider.
		ListDataProvider<ContractVariable> contractVariableDataProvider = new ListDataProvider<>();

	    // Connect the table to the data provider.
		contractVariableDataProvider.addDataDisplay(contractVariablesDG);
	    
	    // Add the data to the data provider, which automatically pushes it to the widget.
	    List<ContractVariable> contractVariableListAux = contractVariableDataProvider.getList();
	    contractVariableListAux.clear();
	    
	    this.contractVariableList = employeeContractVariablesObject.getContractVariables(yearLB.getSelectedValue(), monthLB.getSelectedValue(), variableTypeLB.getSelectedValue());
	    
	    for (ContractVariable contractVariable : this.contractVariableList) {
	    	contractVariableListAux.add(contractVariable);
	    }   
		
		addSortColums(contractVariableListAux);
	    
		// Set page size
		contractVariablesDG.setPageSize(contractVariableListAux.size());
		
		setScrollPanelHeight();
	}

	private void addSortColums(List<ContractVariable> contractVariableList) {
		ListHandler<ContractVariable> columnSortHandler = new ListHandler<>(contractVariableList);
		
		columnSortHandler.setComparator(contractVariablesDG.getColumn(1),
			(o1, o2) -> compareString(o1, o2, getVariableTypeShort(o1.getVariableType()), getVariableTypeShort(o2.getVariableType())));
			
	    columnSortHandler.setComparator(contractVariablesDG.getColumn(2), 
	    	(o1, o2) -> compareString(o1, o2, o1.getDescription(), o2.getDescription()));
	    
	    columnSortHandler.setComparator(contractVariablesDG.getColumn(3), 
	    	(o1, o2) -> compareString(o1, o2, o1.getExpression(), o2.getExpression()));
	    
	    columnSortHandler.setComparator(contractVariablesDG.getColumn(4), 
	    	(o1, o2) -> compareDates(o1, o2, o1.getStartDate(), o2.getStartDate()));
	    
	    columnSortHandler.setComparator(contractVariablesDG.getColumn(5),
	    	(o1, o2) -> compareDates(o1, o2, o1.getEndDate(), o2.getEndDate()));
	    
	    contractVariablesDG.addColumnSortHandler(columnSortHandler);

	    // We know that the data is sorted alphabetically by default.
	    contractVariablesDG.getColumn(1).setDefaultSortAscending(false);
	    contractVariablesDG.getColumnSortList().push(contractVariablesDG.getColumn(1));   
	}
	
	private int compareString(Object o1, Object o2, String s1, String s2) {
		if (o1 == o2) return 0;
		else if (o1 == null || AonStringUtils.isBlank(s1)) return -1;
		else if (o2 == null || AonStringUtils.isBlank(s2)) return 1;
		else
        	return s1.compareTo(s2);
	}
	
	private int compareDates(Object o1, Object o2, Date d1, Date d2) {
		if (o1 == o2) return 0;
		else if (o1 == null || d1 == null) return -1;
		else if (o2 == null || d2 == null) return 1;
		else
        	return d1.compareTo(d2);
	}
		
	// ----------------------------------------------- setEmployeeContractVariablesObject 
	
	public void setEmployeeContractVariablesObject(EmployeeContractVariablesObject employeeContractVariablesObject) {
		this.employeeContractVariablesObject = employeeContractVariablesObject;
		this.employeeContractVariablesObject.getContractVariables(
				r -> {
					initializeYearLB();
					initContractVariablesTable();
				},t -> {});
	}
	
	// ----------------------------------------------- setEmployeeContractVariablesObject.Methods
	
	public void initializeVariableTypeLB() {
		this.variableTypeLB.clear();
		this.variableTypeLB.addItem("Contract Data", "0");
		this.variableTypeLB.addItem("Contract Info", "1");
		this.variableTypeLB.addItem("Todas", "2");
		
		this.variableTypeLB.addChangeHandler(e -> changeYear());
		
		setSelectedValueLB(this.variableTypeLB, "0");
	}
	
	public void initializeYearLB() {
		Integer startYear = DateUtils.getYear(employeeContractVariablesObject.getContractStartDate());
		Integer endYear = null == employeeContractVariablesObject.getContractEndDate() ? DateUtils.getYear() : DateUtils.getYear(employeeContractVariablesObject.getContractEndDate());
		
		Integer auxYear = endYear;
		if(null == employeeContractVariablesObject.getContractEndDate()) auxYear++;
		
		this.yearLB.clear();
		this.yearLB.addItem("-", "");
		
		while(auxYear >= startYear) {
			this.yearLB.addItem(auxYear.toString(), auxYear.toString());
			auxYear--;
		}
		
		this.yearLB.addChangeHandler(e -> {
			checkSelectedYear();
			changeYear();
		});
		
		setSelectedValueLB(this.yearLB, endYear.toString());
	}
	
	private void checkSelectedYear() {
		if(AonStringUtils.isBlank(yearLB.getSelectedValue())) {
			monthLB.setSelectedIndex(0);
			monthLB.setVisible(false);
		} else
			monthLB.setVisible(true);
	}

	public void initializeMonthLB() {
		this.monthLB.clear();
		this.monthLB.addItem("-", "");
		this.monthLB.addItem("Enero", "0");
		this.monthLB.addItem("Febrero", "1");
		this.monthLB.addItem("Marzo", "2");
		this.monthLB.addItem("Abril", "3");
		this.monthLB.addItem("Mayo", "4");
		this.monthLB.addItem("Junio", "5");
		this.monthLB.addItem("Julio", "6");
		this.monthLB.addItem("Agosto", "7");
		this.monthLB.addItem("Septiembre", "8");
		this.monthLB.addItem("Octubre", "9");
		this.monthLB.addItem("Noviembre", "10");
		this.monthLB.addItem("Diciembre", "11");
		
		this.monthLB.addChangeHandler(e -> changeYear());
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
	
	private void changeYear() {
		initContractVariablesTable();
	}

	// ----------------------------------------------- Toolbar
	
	private void initializeToolbarPanel() {
		
		this.toolbar = new AonToolbar("Variables Contrato");
		
		AonToolbarButton addVariable = new AonToolbarButton( AON.MSG.newAction(), AON.CSS.aonIconAdd());
		addVariable.addClickHandler(e -> openContractVariableDialog(null));
		toolbar.add(addVariable);
		
		this.variableTypeLB = new ListBox();
		initializeVariableTypeLB();
		this.toolbar.add(this.variableTypeLB);
		
		this.yearLB = new ListBox();
		this.toolbar.add(this.yearLB);
		
		this.monthLB = new ListBox();
		initializeMonthLB();
		this.toolbar.add(this.monthLB);
		
	}

	// ----------------------------------------------- Toolbar.Methods

	public void onSave() {
		employeeContractVariablesObject.updateContractVariables(
				r -> changeYear(), 
				t -> {});
	}
	
	public void openContractVariableDialog(ContractVariable selectedContractVariable) {
		new ContractVariableDialog(selectedContractVariable, employeeContractVariablesObject.getContractStartDate(), employeeContractVariablesObject.getContractEndDate()) {
			@Override
			protected void onAcceptDialog(ContractVariable contractVariable) {
				// Create new variable
				if(contractVariable.getId() == null) {
					employeeContractVariablesObject.createContractVariable(
						contractVariable, 
						s ->
							employeeContractVariablesObject.getContractVariables(
									r -> initContractVariablesTable(),
									t -> {}
							), 
						f -> {});	
				// Update variable
				} else {
					contractVariable.setHasChange(true);
					onSave();
				}
			}};
	}
	
	// -------------------------------------------------- ContrataEmployee.Methods
	
	public void hideToolbar(){
		dockLayoutPanel.remove(toolbar);
	}
	
	public void setVariableTypeLB(ListBox variableTypeLB) {
		this.variableTypeLB = variableTypeLB;
	}
	
	public void setYearLB(ListBox yearLB) {
		this.yearLB = yearLB;
	}
	
	public void setMonthLB(ListBox monthLB) {
		this.monthLB = monthLB;
	}

	public void setToolbarTitle(String title) {
		toolbar.setTitle(title);
	}
	
}
