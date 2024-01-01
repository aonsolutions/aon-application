package com.esferalia.aon.gwt.payroll.client;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.gwt.payroll.shared.Salary.Type;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfo;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfo.AlcatrazTerritory;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;

public abstract class SalaryTable extends ResizeComposite {
	
	// ------------------------------------------ UiBinder 

	private static SalaryTableUiBinder uiBinder = GWT.create(SalaryTableUiBinder.class);

	interface SalaryTableUiBinder extends UiBinder<Widget, SalaryTable> {}

	// ------------------------------------------ UiFields
	
	@UiField(provided = true)
	DataGrid<SalaryInfo> salaryDG;
	
	// ------------------------------------------ Variables
	
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	private MultiSelectionModel<SalaryInfo> selectionModel;
	
	private static final String EMPTYDOUBLEVALUE = "00,00";
	
//	private List<SalaryInfo> salaryInfoList;
	private List<SalaryInfo> salariesList;

	// ------------------------------------------ Constructor

	protected SalaryTable() {
		provideSalaryDataGrid();
		initWidget(uiBinder.createAndBindUi(this));
	    setGridHeight();
	}
	
	private void setGridHeight() {
		this.salaryDG.setHeight((Window.getClientHeight() - (250 + 75 )) + "px");
	}

	// ------------------------------------------ Provied DataGrid

	private void provideSalaryDataGrid() {
		salariesList = Collections.emptyList();
		
		// Resource Style CellTable
		salaryDG = new CustomDataGrid<>(Integer.MAX_VALUE, SalaryInfo.KEY_PROVIDER);
		
		//Do not refresh the headers every time the dataGrid is updated.
		salaryDG.setAutoHeaderRefreshDisabled(true);
		
		// Set the message to display when the table is empty.
		salaryDG.setEmptyTableWidget(new Label(("No existen n\u00F3minas").toUpperCase()));
		
		// Add a selection model so we can select cells.
	    this.selectionModel = new MultiSelectionModel<>(SalaryInfo.KEY_PROVIDER);
	    salaryDG.setSelectionModel(this.selectionModel, DefaultSelectionEventManager.<SalaryInfo> createCheckboxManager());
	    
	    // Initialize the columns.
	    addColumns(this.selectionModel);
	    
	    new ListDataProvider<SalaryInfo>(Collections.emptyList()).addDataDisplay(salaryDG);
	    
	}
	
	private void addColumns(MultiSelectionModel<SalaryInfo> selectionModel) {
		
		selectionModel.addSelectionChangeHandler(e -> onSelectionSalary(!selectionModel.getSelectedSet().isEmpty()));
	    
		Column<SalaryInfo, Boolean> checkColumn = new Column<SalaryInfo, Boolean>(new CheckboxCell(true, false)) {
			@Override
			public Boolean getValue(SalaryInfo salaryInfo) {
				return selectionModel.isSelected(salaryInfo);
			}
	    };
    
	    CheckboxCell selectAllHeaderCB = new CheckboxCell(true,true);
	    Header<Boolean> selectAllHeader = new Header<Boolean>(selectAllHeaderCB) {
	    	@Override
	    	public Boolean getValue() {
	    		if(null == salariesList) return false;
	    		return selectionModel.getSelectedSet().size() == salariesList.size();
	    	}
	    	
	    };
	    
	    selectAllHeader.setUpdater(value -> salariesList.forEach(salary -> selectionModel.setSelected(salary, value)));
	    
	    salaryDG.addColumn(checkColumn,selectAllHeader);
	    salaryDG.setColumnWidth(checkColumn, 5, Unit.PCT);
	    checkColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    
	    // Enterprise name column.
	    TextColumn<SalaryInfo> enterpriseNameColumn = new TextColumn<SalaryInfo>() {
	    	@Override
	    	public String getValue(SalaryInfo salaryInfo) {
	    		return salaryInfo.getEnterpriseName();
	    	}
	    };

	    enterpriseNameColumn.setSortable(true);
	    salaryDG.setColumnWidth(enterpriseNameColumn, 25, Unit.PCT);
	    
	    // Workplace name column.
	    TextColumn<SalaryInfo> workplaceNameColumn = new TextColumn<SalaryInfo>() {
	    	@Override
	    	public String getValue(SalaryInfo salaryInfo) {
	    		return salaryInfo.getWorkplaceName();
	    	}
	    };

	    workplaceNameColumn.setSortable(true);
	    salaryDG.setColumnWidth(workplaceNameColumn, 15, Unit.PCT);
	    
	    // Employee name column.
	    TextColumn<SalaryInfo> employeeNameColumn = new TextColumn<SalaryInfo>() {
	    	@Override
	    	public String getValue(SalaryInfo salaryInfo) {
	    		return salaryInfo.getEmployeeName();
	    	}
	    };

	    employeeNameColumn.setSortable(true);
	    salaryDG.setColumnWidth(employeeNameColumn, 25, Unit.PCT);
	    
	    // Start date column.
	    TextColumn<SalaryInfo> startDateColumn = new TextColumn<SalaryInfo>() {
	    	@Override
	    	public String getValue(SalaryInfo salaryInfo) {
	    		return formatDate.format(salaryInfo.getStartDate());
	    	}
	    };

	    startDateColumn.setSortable(true);
	    startDateColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    salaryDG.setColumnWidth(startDateColumn, 10, Unit.PCT);
	    
	    // End date column.
	    TextColumn<SalaryInfo> endDateColumn = new TextColumn<SalaryInfo>() {
	    	@Override
	    	public String getValue(SalaryInfo salaryInfo) {
	    		return formatDate.format(salaryInfo.getEndDate());
	    	}
	    };

	    endDateColumn.setSortable(true);
	    endDateColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    salaryDG.setColumnWidth(endDateColumn, 10, Unit.PCT);
	    
	    // Contract type column.
	    TextColumn<SalaryInfo> typeColumn = new TextColumn<SalaryInfo>() {
	    	@Override
	    	public String getValue(SalaryInfo salaryInfo) {
	    		return salaryInfo.getType().getDescription();
	    	}
	    };
	    
	    typeColumn.setSortable(true);
	    salaryDG.setColumnWidth(typeColumn, 10, Unit.PCT);
	    
	    // Total payment column.
	    TextColumn<SalaryInfo> totalPaymentColumn = new TextColumn<SalaryInfo>() {
	    	@Override
	    	public String getValue(SalaryInfo salaryInfo) {
	    		if(isEmptyDoubleValue(salaryInfo.getTotalPayment()))
	    			return EMPTYDOUBLEVALUE;
	    		return NumberFormat.getFormat("0.00").format(salaryInfo.getTotalPayment());
	    	}
	    };
	 
	    totalPaymentColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
	    salaryDG.setColumnWidth(totalPaymentColumn, 10, Unit.PCT);
	    
	    // Total deduction column.
	    TextColumn<SalaryInfo> totalDeductionColumn = new TextColumn<SalaryInfo>() {
	    	@Override
	    	public String getValue(SalaryInfo salaryInfo) {
	    		if(isEmptyDoubleValue(salaryInfo.getTotalDecuction()))
	    			return EMPTYDOUBLEVALUE;
	    		return NumberFormat.getFormat("0.00").format(salaryInfo.getTotalDecuction());
	    	}
	    };
	    
	    totalDeductionColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
	    salaryDG.setColumnWidth(totalDeductionColumn, 10, Unit.PCT);
	    
	    // Total liquid column.
	    TextColumn<SalaryInfo> totalLiquidColumn = new TextColumn<SalaryInfo>() {
	    	@Override
	    	public String getValue(SalaryInfo salaryInfo) {
	    		if(isEmptyDoubleValue(salaryInfo.getTotalLiquid()))
	    			return EMPTYDOUBLEVALUE;
	    		return NumberFormat.getFormat("0.00").format(salaryInfo.getTotalLiquid()) + " \u20AC";
	    	}
	    };
	    
	    totalLiquidColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
	    salaryDG.setColumnWidth(totalLiquidColumn, 10, Unit.PCT);
	    
	    ActionCell<SalaryInfo> draftActionCell = new ActionCell<>("", salaryInfo -> {
	    	if(!salaryInfo.getType().equals(Type.SETTLE))
		    	EmployeeTree.showSalaryDraft(
						salaryInfo.getContract(),
						salaryInfo.getWorkplaceId(), 
						salaryInfo.getStartDate(),
						salaryInfo.getEndDate());
	    });
	    
	    Column<SalaryInfo, SalaryInfo> aeatColumn = new Column<SalaryInfo, SalaryInfo>(draftActionCell) {

			@Override
			public SalaryInfo getValue(SalaryInfo salaryInfo) {
				return salaryInfo;
			}
			
			@Override
			public void render(Context context, SalaryInfo salaryInfo, SafeHtmlBuilder sb) {
				if(null != salaryInfo) {
					if(salaryInfo.isAlcatraz()) {
						String title = "Mod111 (" + salaryInfo.getAlcatrazYear() + ", " + salaryInfo.getAlcatrazPeriod().getDescription() + ")";
						String imageCss = getAeatIcon(salaryInfo.getAlcatrazTerritory());
						sb.appendHtmlConstant("<button type=\"button\" class=\"aon_button aon_table_button " + imageCss + "\" style=\"border: none !important; height: 20px;\" title=\"" + title + "\"></button>");
					}else 
						sb.appendHtmlConstant("");
				}
			}

			private String getAeatIcon(AlcatrazTerritory alcatrazTerritory) {
				switch (alcatrazTerritory) {
				case ARABA:
					return "aon-icon-araba";
				case BIZKAIA:
					return "aon-icon-bizkaia";
				case GIPUZKOA:
					return "aon-icon-gipuzkoa";
				case NAVARRA:
					return "aon-icon-navarra";
				default:
					return "aon-icon-aeat";
				}
			}
		};
		
		aeatColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		salaryDG.setColumnWidth(aeatColumn, 5, Unit.PCT);

		Column<SalaryInfo, SalaryInfo> draftColumn = new Column<SalaryInfo, SalaryInfo>(draftActionCell) {

			@Override
			public SalaryInfo getValue(SalaryInfo salaryInfo) {
				return salaryInfo;
			}
			
			@Override
			public void render(Context context, SalaryInfo salaryInfo, SafeHtmlBuilder sb) {
				if(null != salaryInfo) {
					if(!salaryInfo.getType().equals(Type.SETTLE))
						sb.appendHtmlConstant("<button type=\"button\" class=\"aon_button aon_table_button aon_icon_edit\" style=\"border: none !important; height: 20px;\" title=\"Ir al borrador\"></button>");
					else 
						sb.appendHtmlConstant("");
				}
			}
		};
		
		draftColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		salaryDG.setColumnWidth(draftColumn, 5, Unit.PCT);
		
	    // Add the columns.
		salaryDG.addColumn(enterpriseNameColumn, "Empresa");
		salaryDG.addColumn(workplaceNameColumn, "C. Trabajo");
		salaryDG.addColumn(employeeNameColumn, "Empleado");
		
		salaryDG.addColumn(typeColumn, "Tipo");
		salaryDG.addColumn(startDateColumn, "F. Inicio");
		salaryDG.addColumn(endDateColumn, "F. Fin");
	    
		salaryDG.addColumn(totalPaymentColumn, "Bruto");
		salaryDG.addColumn(totalDeductionColumn, "Deducciones");
		salaryDG.addColumn(totalLiquidColumn, "L\u00EDquido");
	    
		salaryDG.addColumn(aeatColumn, "");  
		salaryDG.addColumn(draftColumn, "");   
	}
	
	private boolean isEmptyDoubleValue(Double value) {
		return null == value || AonNumberUtils.equals(0, value);
	}
	
	// ------------------------------------------ Init SalaryDG
	
	public void initSalariesTable() {		
		//Reset Selection Model 
		selectionModel.clear();
		
		// Create a data provider.
	    ListDataProvider<SalaryInfo> dataProvider = new ListDataProvider<>();

	    // Connect the table to the data provider.
	    dataProvider.addDataDisplay(salaryDG);
	    
	    // Add the data to the data provider, which automatically pushes it to the widget.
	    List<SalaryInfo> salaryListProvider = dataProvider.getList();
	    salaryListProvider.clear();
	      
//	    this.salaryInfoList = salariesList;
	    
	    for (SalaryInfo salary : this.salariesList) {
	    	salaryListProvider.add(salary);
	    }   
	    
		addSortColums(salaryListProvider);
	    
		// Set page size
	    salaryDG.setPageSize(salariesList.size());
	}

	private void addSortColums(List<SalaryInfo> salaryInfoList) {
		ListHandler<SalaryInfo> columnSortHandler = new ListHandler<>(salaryInfoList);
		
		columnSortHandler.setComparator(salaryDG.getColumn(1),
		    		(o1, o2) -> compareString(o1, o2, o1.getEnterpriseName(), o2.getEnterpriseName()));
		
	    columnSortHandler.setComparator(salaryDG.getColumn(2),
	    		(o1, o2) -> compareString(o1, o2, o1.getEmployeeName(), o2.getEmployeeName()));
	    
	    columnSortHandler.setComparator(salaryDG.getColumn(3),
	    		(o1, o2) -> compareString(o1, o2, o1.getWorkplaceName(), o2.getWorkplaceName()));
	    
	    columnSortHandler.setComparator(salaryDG.getColumn(4),
	    		(o1, o2) -> compareDates(o1, o2, o1.getStartDate(), o2.getStartDate()));
	    
	    columnSortHandler.setComparator(salaryDG.getColumn(5),
	    		(o1, o2) -> compareDates(o1, o2, o1.getEndDate(), o2.getEndDate()));
	    
	    columnSortHandler.setComparator(salaryDG.getColumn(6),
	    		(o1, o2) -> compareString(o1, o2, o1.getType().getDescription(), o2.getType().getDescription()));
	    
	    salaryDG.addColumnSortHandler(columnSortHandler);

	    // We know that the data is sorted alphabetically by default.
	    salaryDG.getColumn(4).setDefaultSortAscending(false);
	    salaryDG.getColumnSortList().push(salaryDG.getColumn(5));   
	}
	
	private int compareString(Object o1, Object o2, String s1, String s2) {
		if (o1 == o2) return 0;
		else if (o1 == null) return -1;
		else if (o2 == null) return 1;
		else
        	return s1.compareTo(s2);
	}
	
	private int compareDates(Object o1, Object o2, Date d1, Date d2) {
		if (o1 == o2) return 0;
		else if (o1 == null) return -1;
		else if (o2 == null) return 1;
		else
        	return d1.compareTo(d2);
	}

	// ------------------------------------------ Auxiliar Methods

	public void sortTableByName() {
		salaryDG.getColumnSortList().push(salaryDG.getColumn(3));
		ColumnSortEvent.fire(salaryDG, salaryDG.getColumnSortList());
	}
	
	public void sortTableByStartDate() {
		salaryDG.getColumnSortList().push(salaryDG.getColumn(4));  
		ColumnSortEvent.fire(salaryDG, salaryDG.getColumnSortList());
	}
	
	public void setEnteprisesView() {
		salaryDG.removeColumn(11);
	}
	
	public void setEntepriseView() {
		salaryDG.removeColumn(1);
	}
	
	public void setWorkplaceView() {
		salaryDG.removeColumn(1);
		salaryDG.removeColumn(1);
	}
	
	public void setEmployeeView() {
		salaryDG.removeColumn(1);
		salaryDG.removeColumn(1);
		salaryDG.removeColumn(1);
	}
	
	// ------------------------------------------ Setter Methods
	
	public void setSalariesList(List<SalaryInfo> salariesListIn) {
		this.salariesList = salariesListIn;
	}
	
	public Set<SalaryInfo> getSelectedSalaries() {
		return this.selectionModel.getSelectedSet();
	}
	
	// ------------------------------------------ Abstract Methods
	
	private void onSelectionSalary(boolean isSomethingSelected) {
		Optional<SalaryInfo> settle = this.selectionModel.getSelectedSet().stream().filter(salary -> salary.getType().equals(Type.SETTLE)).findAny();
		onSelectionSalaryChange(isSomethingSelected, settle.isPresent());
	}

	protected abstract void onSelectionSalaryChange(boolean isSomethingSelected, boolean hasSettleSelected);
}
