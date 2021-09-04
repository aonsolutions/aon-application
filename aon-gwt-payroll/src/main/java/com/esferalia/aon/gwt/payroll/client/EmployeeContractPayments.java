package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.ContractDeduction;
import com.esferalia.aon.gwt.payroll.shared.ContractPayment;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class EmployeeContractPayments extends Composite {

	// ----------------------------------------------- UiBinder 
	
	private static EmployeeContractPaymentsUiBinder uiBinder = GWT.create(EmployeeContractPaymentsUiBinder.class);

	interface EmployeeContractPaymentsUiBinder extends UiBinder<Widget, EmployeeContractPayments> {}
	
	// ----------------------------------------------- UiField 
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String widthAll();
		String cellTypeList();
		String dateCell();
	}
	
	@UiField
	DockLayoutPanel dockLayoutPanel;
	
	@UiField
	HTMLPanel mainPanel;
	
	@UiField(provided = true)
	DataGrid<ContractPayment> contractPaymentDG;
	
	@UiField(provided = true)
	DataGrid<ContractDeduction> contractDeductionDG;
	
	// ----------------------------------------------- Variables 
	
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private EmployeeContractPaymentsObject employeeContractPaymentsObject;
	private List<ContractPayment> contractPaymentList;
	private List<ContractDeduction> contractDeductionList;
	
	private ListDataProvider<ContractPayment> contractPaymentDataProvider;
	private ListDataProvider<ContractDeduction> contractDeductionDataProvider;
	
	private AonToolbar toolbar;
	private AonToolbarButton saveButton;
	private ListBox yearLB;
	
	// ----------------------------------------------- Constructor 
	
	public EmployeeContractPayments() {
		toolbar = getToolbarPanel();
		provideContractPaymentDG();
		provideContractDeductionDG();
		initWidget(uiBinder.createAndBindUi(this));
		this.getElement().getStyle().setHeight(100, Unit.PCT);
		dockLayoutPanel.addNorth(toolbar, AonToolbar.HEIGTH);
		setScrollPanelHeight();
		saveButton.setEnabled(false);
	}
	
	private void setScrollPanelHeight() {
		contractPaymentDG.setHeight(90 + "%");
		contractDeductionDG.setHeight(90 + "%");
		contractPaymentDG.setWidth(100 + "%");
		contractDeductionDG.setWidth(100 + "%");
	}
	
	@SuppressWarnings("deprecation")
	private Date parseDate(String date) {
		if(AonStringUtils.isBlank(date))
			return null;
		
		if(date.length() == 8 && !AonStringUtils.containsIgnoreCase(date, "/")) {
			Integer year = Integer.parseInt(date.substring(4, 8)) - 1900;
			Integer month = Integer.parseInt(date.substring(2, 4)) - 1;
			Integer day = Integer.parseInt(date.substring(0, 2));
			
			return new Date(year, month, day);
		} else if(AonStringUtils.containsIgnoreCase(date, "/")) {
			Integer year = Integer.parseInt(date.substring(6, 10)) - 1900;
			Integer month = Integer.parseInt(date.substring(3, 5)) - 1;
			Integer day = Integer.parseInt(date.substring(0, 2));
			
			return new Date(year, month, day);
		}
		
		return null;
	}
	
	private void refreshContractPayments() {
		contractPaymentDG.redraw();
		contractPaymentDataProvider.refresh();
	}
	
	private void refreshContractDeductions() {
		contractDeductionDG.redraw();
		contractDeductionDataProvider.refresh();
	}
	
	private void provideContractPaymentDG() {
		contractPaymentList = Collections.emptyList();
		
		// Resource Style CellTable
		contractPaymentDG = new CustomDataGrid<ContractPayment>(Integer.MAX_VALUE, ContractPayment.KEY_PROVIDER);
		
		//Do not refresh the headers every time the dataGrid is updated.
		contractPaymentDG.setAutoHeaderRefreshDisabled(true);
		
		// Set the message to display when the table is empty.
		contractPaymentDG.setEmptyTableWidget(new Label(("No existen datos").toUpperCase()));
		
		// Initialize the columns.
	    addContractPaymentColumns();
	    
	    new ListDataProvider<ContractPayment>(Collections.emptyList()).addDataDisplay(contractPaymentDG);
	    
	    // Add style to table header
	    addStyleToHeader();
	}
	
	public void addStyleToHeader() {
		contractPaymentDG.getHeader(0).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		contractPaymentDG.getHeader(1).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		contractPaymentDG.getHeader(2).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		contractPaymentDG.getHeader(3).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		contractPaymentDG.getHeader(4).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		contractPaymentDG.getHeader(5).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
	}
	
	private void addContractPaymentColumns() {
		
		// Type columns.
		final Payment.Type[] craTypes = Payment.Type.values();
	    List<String> craTypeNames = new ArrayList<String>();
	    for (Payment.Type type : craTypes) {
	    	craTypeNames.add(type.getDescription());
	    }
	    SelectionCell craTypesCell = new SelectionCell(craTypeNames);
	    
	    Column<ContractPayment, String> craTypesColumn = new Column<ContractPayment, String>(craTypesCell) {
	      @Override
	      public String getValue(ContractPayment contractPayment) {
	        return Payment.Type.values()[((int)contractPayment.getType())].getDescription();
	      }
	    };
	    
	    craTypesColumn.setFieldUpdater(new FieldUpdater<ContractPayment, String>() {
	      @Override
	      public void update(int index, ContractPayment contractPayment, String craTypeStr) {
	    	  for(Payment.Type craType : craTypes) {
	    		  if(AonStringUtils.equalsIgnoreCase(craType.getDescription(), craTypeStr)) {
	    			  contractPayment.setType((byte)craType.getCode());
	    			  break;
	    		  }
	    	  }
	      }
	    });

	    craTypesColumn.setCellStyleNames(style.cellTypeList());
	    contractPaymentDG.setColumnWidth(craTypesColumn, 20, Unit.PCT);

	    // Description column.
		Column<ContractPayment, String> descriptionColumn = new Column<ContractPayment, String>(new EditTextCell()) {
		          @Override
		          public String getValue(ContractPayment contractPayment) {
		            return contractPayment.getDescription();
		          }
		};
		
		descriptionColumn.setFieldUpdater(new FieldUpdater<ContractPayment, String>() {
		      @Override
		      public void update(int index, ContractPayment contractPayment, String description) {
		        // Called when the user changes the value.
		    	contractPayment.setDescription(description);
		    	contractPayment.setHasChange(true);
		      }
		});
		
		descriptionColumn.setSortable(true);
		descriptionColumn.setCellStyleNames(style.widthAll());
	    contractPaymentDG.setColumnWidth(descriptionColumn, 25, Unit.PCT);
	    
	    // Expression column.
	    Column<ContractPayment, String> expressionColumn = new Column<ContractPayment, String>(new EditTextCell()) {
	          @Override
	          public String getValue(ContractPayment contractPayment) {
	            return contractPayment.getExpression();
	          }
		};
		
		expressionColumn.setFieldUpdater(new FieldUpdater<ContractPayment, String>() {
		      @Override
		      public void update(int index, ContractPayment contractPayment, String expression) {
		        // Called when the user changes the value.
		    	contractPayment.setExpression(expression);
		    	contractPayment.setHasChange(true);
		      }
		});

	    expressionColumn.setSortable(true);
	    expressionColumn.setCellStyleNames(style.widthAll());
	    contractPaymentDG.setColumnWidth(expressionColumn, 30, Unit.PCT);
	    
	    // StartDate column.
	    Column<ContractPayment, String> startDateColumn = new Column<ContractPayment, String>(new EditTextCell()) {
	          @Override
	          public String getValue(ContractPayment contractPayment) {
	            return formatDate.format(contractPayment.getStartDate());
	          }
		};
		
		startDateColumn.setFieldUpdater(new FieldUpdater<ContractPayment, String>() {
		      @Override
		      public void update(int index, ContractPayment contractPayment, String startDateIn) {
		        // Called when the user changes the value.
		    	contractPayment.setStartDate(parseDate(startDateIn));
		    	contractPayment.setHasChange(true);
		    	refreshContractPayments();
		      }
		});

	    startDateColumn.setSortable(true);
	    startDateColumn.setCellStyleNames(style.dateCell());
	    contractPaymentDG.setColumnWidth(startDateColumn, 10, Unit.PCT);
	    
	    // EndDate column.
	    Column<ContractPayment, String> endDateColumn = new Column<ContractPayment, String>(new EditTextCell()) {
	          @Override
	          public String getValue(ContractPayment contractPayment) {
	            return contractPayment.getEndDate() == null ? "" : formatDate.format(contractPayment.getEndDate());
	          }
		};
		
		endDateColumn.setFieldUpdater(new FieldUpdater<ContractPayment, String>() {
		      @Override
		      public void update(int index, ContractPayment contractPayment, String endDateIn) {
		    	  contractPayment.setEndDate(parseDate(endDateIn));
		    	  contractPayment.setHasChange(true);
		    	  refreshContractPayments();
		    }
		});

	    endDateColumn.setSortable(true);
	    endDateColumn.setCellStyleNames(style.dateCell());
	    contractPaymentDG.setColumnWidth(endDateColumn, 10, Unit.PCT);
	    
	    // Delete column.
	    ActionCell<ContractPayment> deleteActionCell = new ActionCell<ContractPayment>("", new ActionCell.Delegate<ContractPayment>() {
			@Override
			public void execute(ContractPayment contractPayment) {
				employeeContractPaymentsObject.deletePayment(contractPayment);
				changeYear();
			}
		});
	    
	    Column<ContractPayment, ContractPayment> deleteColumn = new Column<ContractPayment, ContractPayment>(deleteActionCell) {

			@Override
			public ContractPayment getValue(ContractPayment contractPayment) {
				return contractPayment;
			}
			
			@Override
			public void render(Context context, ContractPayment contractPayment, SafeHtmlBuilder sb) {
				if(null != contractPayment) {
					sb.appendHtmlConstant("<button type=\"button\" class=\"aon_button aon_table_button aon_icon_delete\" style=\"border: none !important; height: 20px;\" title=\"Eliminar\"></button>");
				}
			}
		};
		
		deleteColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		contractPaymentDG.setColumnWidth(deleteColumn, 5, Unit.PCT);
		
	    // Add the columns.
		contractPaymentDG.addColumn(craTypesColumn, "Tipo CRA");
		contractPaymentDG.addColumn(descriptionColumn, "Descripci\u00F3n");
	 
		contractPaymentDG.addColumn(expressionColumn, "Expresi\u00F3n");
		contractPaymentDG.addColumn(startDateColumn, "F. Inicio");
		contractPaymentDG.addColumn(endDateColumn, "F. Fin");
	    
		contractPaymentDG.addColumn(deleteColumn, "");   
	}
	
	// ----------------------------------------------- Provide Data Grids (ContractPayment)
	
	private void provideContractDeductionDG() {
		contractDeductionList = Collections.emptyList();
		
		// Resource Style CellTable
		contractDeductionDG = new CustomDataGrid<ContractDeduction>(Integer.MAX_VALUE, ContractDeduction.KEY_PROVIDER);
		
		//Do not refresh the headers every time the dataGrid is updated.
		contractDeductionDG.setAutoHeaderRefreshDisabled(true);
		
		// Set the message to display when the table is empty.
		contractDeductionDG.setEmptyTableWidget(new Label(("No existen datos").toUpperCase()));
		
		// Initialize the columns.
	    addContractDeductionColumns();
	    
	    new ListDataProvider<ContractDeduction>(Collections.emptyList()).addDataDisplay(contractDeductionDG);
	    
	    // Add style to table header
	    addStyleToHeaderDeduction();
	}
	
	public void addStyleToHeaderDeduction() {
		contractDeductionDG.getHeader(0).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		contractDeductionDG.getHeader(1).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		contractDeductionDG.getHeader(2).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		contractDeductionDG.getHeader(3).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		contractDeductionDG.getHeader(4).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		contractDeductionDG.getHeader(5).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
	}
	
	private void addContractDeductionColumns() {
		
		// Type columns.
		final Payment.Type[] craTypes = Payment.Type.values();
	    List<String> craTypeNames = new ArrayList<String>();
	    for (Payment.Type type : craTypes) {
	    	craTypeNames.add(type.getDescription());
	    }
	    SelectionCell craTypesCell = new SelectionCell(craTypeNames);
	    
	    Column<ContractDeduction, String> craTypesColumn = new Column<ContractDeduction, String>(craTypesCell) {
	      @Override
	      public String getValue(ContractDeduction contractDeduction) {
	        return Payment.Type.values()[((int)contractDeduction.getType())].getDescription();
	      }
	    };
	    
	    craTypesColumn.setFieldUpdater(new FieldUpdater<ContractDeduction, String>() {
	      @Override
	      public void update(int index, ContractDeduction contractDeduction, String craTypeStr) {
	    	  for(Payment.Type craType : craTypes) {
	    		  if(AonStringUtils.equalsIgnoreCase(craType.getDescription(), craTypeStr)) {
	    			  contractDeduction.setType((byte)craType.getCode());
	    			  break;
	    		  }
	    	  }
	      }
	    });

	    craTypesColumn.setCellStyleNames(style.cellTypeList());
	    contractDeductionDG.setColumnWidth(craTypesColumn, 20, Unit.PCT);
	    
	    // Description column.
		Column<ContractDeduction, String> descriptionColumn = new Column<ContractDeduction, String>(new EditTextCell()) {
		          @Override
		          public String getValue(ContractDeduction contractDeduction) {
		            return contractDeduction.getDescription();
		          }
		};
		
		descriptionColumn.setFieldUpdater(new FieldUpdater<ContractDeduction, String>() {
		      @Override
		      public void update(int index, ContractDeduction contractDeduction, String description) {
		        // Called when the user changes the value.
		    	  contractDeduction.setDescription(description);
		    	  contractDeduction.setHasChange(true);
		      }
		});
		
		descriptionColumn.setSortable(true);
		descriptionColumn.setCellStyleNames(style.widthAll());
	    contractDeductionDG.setColumnWidth(descriptionColumn, 25, Unit.PCT);
	    
	    // Expression column.
	    Column<ContractDeduction, String> expressionColumn = new Column<ContractDeduction, String>(new EditTextCell()) {
	          @Override
	          public String getValue(ContractDeduction contractDeduction) {
	            return contractDeduction.getExpression();
	          }
		};
		
		expressionColumn.setFieldUpdater(new FieldUpdater<ContractDeduction, String>() {
		      @Override
		      public void update(int index, ContractDeduction contractDeduction, String expression) {
		        // Called when the user changes the value.
		    	  contractDeduction.setExpression(expression);
		    	  contractDeduction.setHasChange(true);
		      }
		});

	    expressionColumn.setSortable(true);
	    expressionColumn.setCellStyleNames(style.widthAll());
	    contractDeductionDG.setColumnWidth(expressionColumn, 30, Unit.PCT);
	    
	    // StartDate column.
	    Column<ContractDeduction, String> startDateColumn = new Column<ContractDeduction, String>(new EditTextCell()) {
	          @Override
	          public String getValue(ContractDeduction contractDeduction) {
	            return formatDate.format(contractDeduction.getStartDate());
	          }
		};
		
		startDateColumn.setFieldUpdater(new FieldUpdater<ContractDeduction, String>() {
		      @Override
		      public void update(int index, ContractDeduction contractDeduction, String startDateIn) {
		        // Called when the user changes the value.
		    	  contractDeduction.setStartDate(parseDate(startDateIn));
		    	  contractDeduction.setHasChange(true);
		    	  refreshContractDeductions();
		      }
		});

	    startDateColumn.setSortable(true);
	    startDateColumn.setCellStyleNames(style.dateCell());
	    contractDeductionDG.setColumnWidth(startDateColumn, 10, Unit.PCT);
	    
	    // EndDate column.
	    Column<ContractDeduction, String> endDateColumn = new Column<ContractDeduction, String>(new EditTextCell()) {
	          @Override
	          public String getValue(ContractDeduction contractDeduction) {
	            return contractDeduction.getEndDate() == null ? "" : formatDate.format(contractDeduction.getEndDate());
	          }
		};
		
		endDateColumn.setFieldUpdater(new FieldUpdater<ContractDeduction, String>() {
		      @Override
		      public void update(int index, ContractDeduction contractDeduction, String endDateIn) {
		    	  contractDeduction.setEndDate(parseDate(endDateIn));
		    	  contractDeduction.setHasChange(true);
		    	  refreshContractDeductions();
		      }
		});

	    endDateColumn.setSortable(true);
	    endDateColumn.setCellStyleNames(style.dateCell());
	    contractDeductionDG.setColumnWidth(endDateColumn, 10, Unit.PCT);
	    
	    // Delete column.
	    
	    ActionCell<ContractDeduction> deleteActionCell = new ActionCell<ContractDeduction>("", new ActionCell.Delegate<ContractDeduction>() {
			@Override
			public void execute(ContractDeduction contractDeduction) {
				employeeContractPaymentsObject.deleteDeduction(contractDeduction);
				changeYear();
			}
		});
	    
	    Column<ContractDeduction, ContractDeduction> deleteColumn = new Column<ContractDeduction, ContractDeduction>(deleteActionCell) {

			@Override
			public ContractDeduction getValue(ContractDeduction contractDeduction) {
				return contractDeduction;
			}
			
			@Override
			public void render(Context context, ContractDeduction contractDeduction, SafeHtmlBuilder sb) {
				if(null != contractDeduction) {
					sb.appendHtmlConstant("<button type=\"button\" class=\"aon_button aon_table_button aon_icon_delete\" style=\"border: none !important; height: 20px;\" title=\"Eliminar\"></button>");
				}
			}
		};
		
		deleteColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		contractDeductionDG.setColumnWidth(deleteColumn, 5, Unit.PCT);
		
	    // Add the columns.
		contractDeductionDG.addColumn(craTypesColumn, "Tipo CRA");
		contractDeductionDG.addColumn(descriptionColumn, "Descripci\u00F3n");
	 
		contractDeductionDG.addColumn(expressionColumn, "Expresi\u00F3n");
		contractDeductionDG.addColumn(startDateColumn, "F. Inicio");
		contractDeductionDG.addColumn(endDateColumn, "F. Fin");
	    
		contractDeductionDG.addColumn(deleteColumn, "");   
	}

	// ----------------------------------------------- InitContractPayments
	
	public void initContractPaymentsTable() {		
		// Create a data provider.
		contractPaymentDataProvider = new ListDataProvider<ContractPayment>();

	    // Connect the table to the data provider.
	    contractPaymentDataProvider.addDataDisplay(contractPaymentDG);
	    
	    // Add the data to the data provider, which automatically pushes it to the widget.
	    List<ContractPayment> contractPaymentList = contractPaymentDataProvider.getList();
	    contractPaymentList.clear();
	    
	    this.contractPaymentList = employeeContractPaymentsObject.getContractPayments(Integer.parseInt(yearLB.getSelectedValue()));
	    
	    for (ContractPayment contractPayment : this.contractPaymentList) {
	    	contractPaymentList.add(contractPayment);
	    }   
		
		addSortColums(contractPaymentList);
	    
		// Set page size
	    contractPaymentDG.setPageSize(contractPaymentList.size());
	}

	
	private void addSortColums(List<ContractPayment> contractPaymentList) {
		ListHandler<ContractPayment> columnSortHandler = new ListHandler<ContractPayment>(contractPaymentList);
		
	    columnSortHandler.setComparator(contractPaymentDG.getColumn(1), new Comparator<ContractPayment>() {
	    	public int compare(ContractPayment o1, ContractPayment o2) {        
	    		if (o1 == o2) return 0;

	            if (o1 != null)
	            	return (o2 != null) ? o1.getDescription().compareTo(o2.getDescription()) : 1;
	            
	            return -1;
	    	}
	    });
	    
	    columnSortHandler.setComparator(contractPaymentDG.getColumn(2), new Comparator<ContractPayment>() {
	    	public int compare(ContractPayment o1, ContractPayment o2) {        
	    		if (o1 == o2) return 0;

	            if (o1 != null)
	            	return (o2 != null) ? o1.getExpression().compareTo(o2.getExpression()) : 1;
	            
	            return -1;
	    	}
	    });
	    
	    columnSortHandler.setComparator(contractPaymentDG.getColumn(3), new Comparator<ContractPayment>() {
	    	public int compare(ContractPayment o1, ContractPayment o2) {        
	    		if (o1 == o2) return 0;

	            if (o1 != null)
	            	return (o2 != null) ? o1.getStartDate().compareTo(o2.getStartDate()) : 1;
	            
	            return -1;
	    	}
	    });
	    
	    columnSortHandler.setComparator(contractPaymentDG.getColumn(4), new Comparator<ContractPayment>() {
	    	public int compare(ContractPayment o1, ContractPayment o2) {        
	    		if (o1 == o2) return 0;

	            if (o1 != null)
	            	return (o2 != null) ? o1.getEndDate().compareTo(o2.getEndDate()) : 1;
	            
	            return -1;
	    	}
	    });
	    
	    contractPaymentDG.addColumnSortHandler(columnSortHandler);

	    // We know that the data is sorted alphabetically by default.
	    contractPaymentDG.getColumn(1).setDefaultSortAscending(false);
	    contractPaymentDG.getColumnSortList().push(contractPaymentDG.getColumn(1));   
	}
	
	// ----------------------------------------------- InitContractPayments
	
	
	public void initContractDeductionsTable() {
		// Create a data provider.
		contractDeductionDataProvider = new ListDataProvider<ContractDeduction>();

	    // Connect the table to the data provider.
		contractDeductionDataProvider.addDataDisplay(contractDeductionDG);
	    
	    // Add the data to the data provider, which automatically pushes it to the widget.
	    List<ContractDeduction> contractDeductionList = contractDeductionDataProvider.getList();
	    contractDeductionList.clear();
	    
	    this.contractDeductionList = employeeContractPaymentsObject.getContractDeductions(Integer.parseInt(yearLB.getSelectedValue()));
	    
	    for (ContractDeduction contractDeduction : this.contractDeductionList) {
	    	contractDeductionList.add(contractDeduction);
	    }   
		
		addSortColumsDeduction(contractDeductionList);
	    
		// Set page size
		contractDeductionDG.setPageSize(contractDeductionList.size());
	}

	
	private void addSortColumsDeduction(List<ContractDeduction> contractDeductionList) {
		ListHandler<ContractDeduction> columnSortHandler = new ListHandler<ContractDeduction>(contractDeductionList);
		
	    columnSortHandler.setComparator(contractDeductionDG.getColumn(1), new Comparator<ContractDeduction>() {
	    	public int compare(ContractDeduction o1, ContractDeduction o2) {        
	    		if (o1 == o2) return 0;

	            if (o1 != null)
	            	return (o2 != null) ? o1.getDescription().compareTo(o2.getDescription()) : 1;
	            
	            return -1;
	    	}
	    });
	    
	    columnSortHandler.setComparator(contractDeductionDG.getColumn(2), new Comparator<ContractDeduction>() {
	    	public int compare(ContractDeduction o1, ContractDeduction o2) {        
	    		if (o1 == o2) return 0;

	            if (o1 != null)
	            	return (o2 != null) ? o1.getExpression().compareTo(o2.getExpression()) : 1;
	            
	            return -1;
	    	}
	    });
	    
	    columnSortHandler.setComparator(contractDeductionDG.getColumn(3), new Comparator<ContractDeduction>() {
	    	public int compare(ContractDeduction o1, ContractDeduction o2) {        
	    		if (o1 == o2) return 0;

	            if (o1 != null)
	            	return (o2 != null) ? o1.getStartDate().compareTo(o2.getStartDate()) : 1;
	            
	            return -1;
	    	}
	    });
	    
	    columnSortHandler.setComparator(contractDeductionDG.getColumn(4), new Comparator<ContractDeduction>() {
	    	public int compare(ContractDeduction o1, ContractDeduction o2) {        
	    		if (o1 == o2) return 0;

	            if (o1 != null)
	            	return (o2 != null) ? o1.getEndDate().compareTo(o2.getEndDate()) : 1;
	            
	            return -1;
	    	}
	    });
	    
	    contractDeductionDG.addColumnSortHandler(columnSortHandler);

	    // We know that the data is sorted alphabetically by default.
	    contractDeductionDG.getColumn(1).setDefaultSortAscending(false);
	    contractDeductionDG.getColumnSortList().push(contractDeductionDG.getColumn(1));   
	}
	
	
	// ----------------------------------------------- setEmployeeContractPaymentsObject 
	
	public void setEmployeeContractPaymentsObject(EmployeeContractPaymentsObject employeeContractPaymentsObject) {
		this.employeeContractPaymentsObject = employeeContractPaymentsObject;
		this.employeeContractPaymentsObject.getContractPayements(r -> {
					initContractPaymentsTable();
					initContractDeductionsTable();
				},t -> {});
	}
	
	// ----------------------------------------------- setEmployeeContractPaymentsObject.Methods
	
	public void initializeYearLB(ListBox yearLB) {
		Integer year = DateUtils.getYear();
		Integer yearAux = DateUtils.getYear();
		Integer previusYear = year - 1;
		Integer nextYear = year + 1;
		
		yearLB.clear();
		yearLB.addItem(nextYear.toString(), nextYear.toString());
		yearLB.addItem(yearAux.toString(), yearAux.toString());
		yearLB.addItem(previusYear.toString(), previusYear.toString());
		
		yearLB.addChangeHandler(e -> {
			changeYear();
		});
		
		setSelectedValueLB(yearLB, year.toString());
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
		initContractPaymentsTable();
		initContractDeductionsTable();
	}

	// ----------------------------------------------- Toolbar
	
	private AonToolbar getToolbarPanel() {
		
		AonToolbar toolbar = new AonToolbar("Conceptos Calculo");
		
		saveButton = new AonToolbarButton( AON.MSG.saveAction(), AON.CSS.aonIconSave() );
		saveButton.addClickHandler(e -> {
			onSave(e);
		});
		toolbar.add(saveButton);
		
		this.yearLB = new ListBox();
		toolbar.add(this.yearLB);
		
		return toolbar;
	}

	// ----------------------------------------------- Toolbar.Methods

	public void onSave(ClickEvent e) {
		employeeContractPaymentsObject.updateContractPayments(
				r -> {}, 
				t -> {});
	}
	
	// -------------------------------------------------- ContrataEmployee.Methods
	
	public void hideToolbar(){
		dockLayoutPanel.remove(toolbar);
		mainPanel.getElement().getStyle().setMarginTop(0, Unit.PX);
	}
	
	public void setYearLB(ListBox yearLB) {
		this.yearLB = yearLB;
	}
	
}
