package com.esferalia.aon.gwt.payroll.client;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfo;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.ValueUpdater;
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
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;

public abstract class SalaryTable extends ResizeComposite {
	
	// -------------------------------------------------- UiBinder --------------------------------------------------

	private static SalaryTableUiBinder uiBinder = GWT.create(SalaryTableUiBinder.class);

	interface SalaryTableUiBinder extends UiBinder<Widget, SalaryTable> {}

	// -------------------------------------------------- UiFields --------------------------------------------------

	@UiField
	ScrollPanel scrollPanel;
	
	@UiField(provided = true)
	DataGrid<SalaryInfo> salaryDG;
	
	// -------------------------------------------------- Variables -------------------------------------------------
	
	private List<SalaryInfo> salaryInfoList;
	private List<SalaryInfo> salariesList;
	private MultiSelectionModel<SalaryInfo> selectionModel;
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");

	// ------------------------------------------------- Constructor ------------------------------------------------

	public SalaryTable() {
		provideSalaryDataGrid();
		initWidget(uiBinder.createAndBindUi(this));
		salariesList = Collections.emptyList();
		// Set scrollPanel height
	    setScrollPanelHeight();
	}
	
	// ----------------------------------------------- Provied DataGrid ---------------------------------------------

	private void provideSalaryDataGrid() {
		salaryInfoList = Collections.emptyList();
		
		// Resource Style CellTable
		salaryDG = new CustomDataGrid<SalaryInfo>(Integer.MAX_VALUE, SalaryInfo.KEY_PROVIDER);
		
		//Do not refresh the headers every time the dataGrid is updated.
		salaryDG.setAutoHeaderRefreshDisabled(true);
		
		// Set the message to display when the table is empty.
		salaryDG.setEmptyTableWidget(new Label(("No existen n" + String.valueOf("\u00F3") + "minas").toUpperCase()));
		
		// Add a selection model so we can select cells.
	    this.selectionModel = new MultiSelectionModel<SalaryInfo>(SalaryInfo.KEY_PROVIDER);
	    salaryDG.setSelectionModel(this.selectionModel, DefaultSelectionEventManager.<SalaryInfo> createCheckboxManager());
	    
	    // Initialize the columns.
	    addColumns(this.selectionModel);
	    
	    new ListDataProvider<SalaryInfo>(Collections.emptyList()).addDataDisplay(salaryDG);
	    
	    // Add style to table header
	    addStyleToHeader();
	}
	
	public void addStyleToHeader() {
		salaryDG.getHeader(0).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		salaryDG.getHeader(1).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
	    salaryDG.getHeader(2).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
	    salaryDG.getHeader(3).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
	    salaryDG.getHeader(4).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
	    salaryDG.getHeader(5).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
	    salaryDG.getHeader(6).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
	    salaryDG.getHeader(7).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
	    salaryDG.getHeader(8).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
	    salaryDG.getHeader(9).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
	}
	
	private void addColumns(MultiSelectionModel<SalaryInfo> selectionModel) {
		selectionModel.addSelectionChangeHandler(new Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				onSelectionSalaryChange(selectionModel.getSelectedSet().size() > 0);
			}
	    });
	    
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
	    		boolean value = false;
	    	
	    		if(null != salariesList)
	    			value = selectionModel.getSelectedSet().size() == salariesList.size();
	        
	    		return value; 
	    	}
	    	
//	    	@Override
//	    	public void render(Context context, SafeHtmlBuilder sb) {
//	    		sb.appendHtmlConstant("<input type=\"checkbox\" tabindex=\"-1\" style=\"margin-left: 0;\">");
//	    	}
	    };
	    
	    selectAllHeader.setUpdater(new ValueUpdater<Boolean>() {
	    	@Override
	    	public void update(Boolean value) {
		        for (SalaryInfo salary : salariesList)
		          selectionModel.setSelected(salary, value);
	    	}
	    });
	    
	    // Add Selection Column to table
	    salaryDG.addColumn(checkColumn,selectAllHeader);
	    salaryDG.setColumnWidth(checkColumn, 5, Unit.PCT);
	    checkColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		// Employee name column.
	    TextColumn<SalaryInfo> employeeNameColumn = new TextColumn<SalaryInfo>() {
	    	@Override
	    	public String getValue(SalaryInfo salaryInfo) {
	    		return salaryInfo.getEmployeeName();
	    	}
	    };

	    employeeNameColumn.setSortable(true);
	    salaryDG.setColumnWidth(employeeNameColumn, 25, Unit.PCT);
	    
	    // Workplace name column.
	    TextColumn<SalaryInfo> workplaceNameColumn = new TextColumn<SalaryInfo>() {
	    	@Override
	    	public String getValue(SalaryInfo salaryInfo) {
	    		return salaryInfo.getWorkplaceName();
	    	}
	    };

	    workplaceNameColumn.setSortable(true);
	    salaryDG.setColumnWidth(workplaceNameColumn, 15, Unit.PCT);
	    
	    // Start date column.
	    TextColumn<SalaryInfo> startDateColumn = new TextColumn<SalaryInfo>() {
	    	@Override
	    	public String getValue(SalaryInfo salaryInfo) {
	    		return formatDate.format(salaryInfo.getStartDate());
	    	}
	    };

	    // Make the  start date column sortable.
	    startDateColumn.setSortable(true);
	    salaryDG.setColumnWidth(startDateColumn, 10, Unit.PCT);
	    
	    // End date column.
	    TextColumn<SalaryInfo> endDateColumn = new TextColumn<SalaryInfo>() {
	    	@Override
	    	public String getValue(SalaryInfo salaryInfo) {
	    		return formatDate.format(salaryInfo.getEndDate());
	    	}
	    };

	    endDateColumn.setSortable(true);
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
	    		if(null == salaryInfo.getTotalPayment() || AonNumberUtils.equals(0, salaryInfo.getTotalPayment()))
	    			return "00,00";
	    		return NumberFormat.getFormat("#.00").format(salaryInfo.getTotalPayment());
	    	}
	    };
	 
	    totalPaymentColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
	    salaryDG.setColumnWidth(totalPaymentColumn, 10, Unit.PCT);
	    
	    // Total deduction column.
	    TextColumn<SalaryInfo> totalDeductionColumn = new TextColumn<SalaryInfo>() {
	    	@Override
	    	public String getValue(SalaryInfo salaryInfo) {
	    		if(null == salaryInfo.getTotalDecuction() || AonNumberUtils.equals(0, salaryInfo.getTotalPayment()))
	    			return "00,00";
	    		return NumberFormat.getFormat("#.00").format(salaryInfo.getTotalDecuction());
	    	}
	    };
	    
	    totalDeductionColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
	    salaryDG.setColumnWidth(totalDeductionColumn, 10, Unit.PCT);
	    
	    // Total liquid column.
	    TextColumn<SalaryInfo> totalLiquidColumn = new TextColumn<SalaryInfo>() {
	    	@Override
	    	public String getValue(SalaryInfo salaryInfo) {
	    		if(null == salaryInfo.getTotalLiquid() || AonNumberUtils.equals(0, salaryInfo.getTotalPayment()))
	    			return "00,00";
	    		return NumberFormat.getFormat("#.00").format(salaryInfo.getTotalLiquid())+" "+String.valueOf("\u20AC");
	    	}
	    };
	    
	    totalLiquidColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
	    salaryDG.setColumnWidth(totalLiquidColumn, 10, Unit.PCT);
	    
	    ActionCell<SalaryInfo> draftActionCell = new ActionCell<SalaryInfo>("", new ActionCell.Delegate<SalaryInfo>() {
			@Override
			public void execute(SalaryInfo salaryInfo) {
				EmployeeTree.showSalaryDraft(
						salaryInfo.getContract(),
						salaryInfo.getWorkplaceId(), 
						salaryInfo.getStartDate(),
						salaryInfo.getEndDate());
			}
			
		});
	    
	    Column<SalaryInfo, SalaryInfo> draftColumn = new Column<SalaryInfo, SalaryInfo>(draftActionCell) {

			@Override
			public SalaryInfo getValue(SalaryInfo salaryInfo) {
				return salaryInfo;
			}
			
			@Override
			public void render(Context context, SalaryInfo object, SafeHtmlBuilder sb) {
				if(null != object) {
					sb.appendHtmlConstant("<button type=\"button\" class=\"aon_button aon_table_button aon_icon_edit\" style=\"border: none !important; height: 20px;\" title=\"Ir al borrador\"></button>");
				}
			}
		};
		
		draftColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		salaryDG.setColumnWidth(draftColumn, 5, Unit.PCT);
		
	    // Add the columns.
		salaryDG.addColumn(employeeNameColumn, "Empleado");
		salaryDG.addColumn(workplaceNameColumn, "C. Trabajo");
	    
		salaryDG.addColumn(typeColumn, "Tipo");
		salaryDG.addColumn(startDateColumn, "F. Inicio");
		salaryDG.addColumn(endDateColumn, "F. Fin");
	    
		salaryDG.addColumn(totalPaymentColumn, "Bruto");
		salaryDG.addColumn(totalDeductionColumn, "Deducciones");
		salaryDG.addColumn(totalLiquidColumn, "L"+String.valueOf("\u00ED")+"quido");
	    
		salaryDG.addColumn(draftColumn, "");   
	}
	
	// ------------------------------------------------ Init SalaryDG -----------------------------------------------
	
	public void initSalariesTable() {		
		//Reset Selection Model 
		selectionModel.clear();
		
		// Create a data provider.
	    ListDataProvider<SalaryInfo> dataProvider = new ListDataProvider<SalaryInfo>();

	    // Connect the table to the data provider.
	    dataProvider.addDataDisplay(salaryDG);
	    
	    // Add the data to the data provider, which automatically pushes it to the widget.
	    List<SalaryInfo> salaryList = dataProvider.getList();
	    salaryList.clear();
	    
	    this.salaryInfoList = salariesList;
	    
	    for (SalaryInfo salary : this.salaryInfoList) {
	    	salaryList.add(salary);
	    }   
		
		addSortColums(salaryList);
	    
		// Set page size
	    salaryDG.setPageSize(salaryInfoList.size());
	}

	private void addSortColums(List<SalaryInfo> salaryInfoList) {
		ListHandler<SalaryInfo> columnSortHandler = new ListHandler<SalaryInfo>(salaryInfoList);
		
	    columnSortHandler.setComparator(salaryDG.getColumn(1), new Comparator<SalaryInfo>() {
	    	public int compare(SalaryInfo o1, SalaryInfo o2) {        
	    		if (o1 == o2) return 0;

	            if (o1 != null)
	            	return (o2 != null) ? o1.getEmployeeName().compareTo(o2.getEmployeeName()) : 1;
	            
	            return -1;
	    	}
	    });
	    
	    columnSortHandler.setComparator(salaryDG.getColumn(2), new Comparator<SalaryInfo>() {
	    	public int compare(SalaryInfo o1, SalaryInfo o2) {
	    		if (o1 == o2) return 0;

	            if (o1 != null)
	            	return (o2 != null) ? o1.getWorkplaceName().compareTo(o2.getWorkplaceName()) : 1;
	            
	            return -1;
	    	}
	    });
	    
	    columnSortHandler.setComparator(salaryDG.getColumn(3), new Comparator<SalaryInfo>() {
	    	public int compare(SalaryInfo o1, SalaryInfo o2) {
	    		if (o1 == o2) return 0;

	            if (o1 != null)
	            	return (o2 != null) ? o1.getStartDate().compareTo(o2.getStartDate()) : 1;
	            
	            return -1;
	    	}
	    });
	    
	    columnSortHandler.setComparator(salaryDG.getColumn(4), new Comparator<SalaryInfo>() {
	    	public int compare(SalaryInfo o1, SalaryInfo o2) {
	    		if (o1 == o2) return 0;
	            
	        	if (o1 != null)
	        		return (o2 != null) ? o1.getEndDate().compareTo(o2.getEndDate()) : 1;
	            
	  	        return -1;
	        }
	    });
	    
	    columnSortHandler.setComparator(salaryDG.getColumn(5), new Comparator<SalaryInfo>() {
	    	public int compare(SalaryInfo o1, SalaryInfo o2) {
	    		if (o1 == o2) return 0;

	        	if (o1 != null)
	        		return (o2 != null) ? o1.getType().getDescription().compareTo(o2.getType().getDescription()) : 1;
	            
	  	        return -1;
	        }
	    });
	    
	    salaryDG.addColumnSortHandler(columnSortHandler);

	    // We know that the data is sorted alphabetically by default.
	    salaryDG.getColumn(5).setDefaultSortAscending(false);
	    salaryDG.getColumnSortList().push(salaryDG.getColumn(1));   
	}

	// ----------------------------------------------- Aux Methods ------------------------------------------------

	public void sortTableByName() {
		salaryDG.getColumnSortList().push(salaryDG.getColumn(1));
		ColumnSortEvent.fire(salaryDG, salaryDG.getColumnSortList());
	}
	
	public void sortTableByStartDate() {
		salaryDG.getColumnSortList().push(salaryDG.getColumn(5));  
		ColumnSortEvent.fire(salaryDG, salaryDG.getColumnSortList());
	}
	
	private void setScrollPanelHeight() {
		scrollPanel.setHeight((Window.getClientHeight() - 235) + "px");
		salaryDG.setHeight((Window.getClientHeight() - 245) + "px");
	}
	
	public void setWorkplaceView() {
		salaryDG.removeColumn(2);
	}
	
	public void setEmployeeView() {
		salaryDG.removeColumn(1);
		salaryDG.removeColumn(1);
	}
	
	// ---------------------------------------------- Setter Methods ----------------------------------------------
	
	public void setSalariesList(List<SalaryInfo> salariesList) {
		this.salariesList = salariesList;
	}
	
	public Set<SalaryInfo> getSelectedSalaries() {
		return this.selectionModel.getSelectedSet();
	}
	
	// ---------------------------------------------- Abstract Methods ----------------------------------------------

	protected abstract void onSelectionSalaryChange(boolean isSomethingSelected);
}
