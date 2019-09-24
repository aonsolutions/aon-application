package com.esferalia.aon.gwt.payroll.client;

import java.util.Comparator;
import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.SalaryInfo;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;

public class EmployeeSalary extends Composite implements ContextMenuHandler {

	private static EmployeeSalaryUiBinder uiBinder = GWT.create(EmployeeSalaryUiBinder.class);

	interface EmployeeSalaryUiBinder extends UiBinder<Widget, EmployeeSalary> {
	}
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String tableStyle();
		String mAuto();
	}
	
	@UiField
	HTMLPanel mainContainer;
	
	@UiField
	Button deleteButton;
	
	@UiField
	Button saveButton;
	
	@UiField
	Button publishButton;

	
	public EmployeeSalary() {
				
		//Inicializamos la vista del calendario
		initWidget(uiBinder.createAndBindUi(this));
		
		deleteButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				employeeSalaryObject.delete(
						selectionModel.getSelectedSet(), 
						s -> {
							setEmployeeSalaryObject(employeeSalaryObject);
						}, 
						f -> {}
				);
			}
		});
		
	}

	private EmployeeSalaryObject employeeSalaryObject;
	private MultiSelectionModel<SalaryInfo> selectionModel;
	
	public void setEmployeeSalaryObject(EmployeeSalaryObject employeeSalaryObject) {
		this.employeeSalaryObject = employeeSalaryObject;
		this.employeeSalaryObject.getEmployeeSalariesDB(
				s -> {
					resetPage();
					initSalariesTable();
				}, 
				f -> {}
		);
	}

	private void resetPage() {
		mainContainer.clear();
		this.saveButton.setVisible(false);
		this.publishButton.setVisible(false);
	}

	private void initSalariesTable() {
		if(this.employeeSalaryObject.getEmployeeSalaries().isEmpty()) {
			this.deleteButton.setVisible(false);
			WarningDialog warningDialog = new WarningDialog("Aviso", "No existen nominas para este empleado.");
			warningDialog.center();
			warningDialog.show();
			return;
		}
		
		deleteButton.setVisible(true);
		deleteButton.setEnabled(false);
		
		// Create a CellTable.
	    CellTable<SalaryInfo> table = new CellTable<SalaryInfo>(SalaryInfo.KEY_PROVIDER);
	   
	    // Do not refresh the headers and footers every time the data is updated.
	    table.setAutoHeaderRefreshDisabled(true);
	    table.setAutoFooterRefreshDisabled(true);
	    
	    // Add a selection model so we can select cells.
	    addSelectionModel(table);
	    
	    // Create a data provider.
	    ListDataProvider<SalaryInfo> dataProvider = new ListDataProvider<SalaryInfo>();

	    // Connect the table to the data provider.
	    dataProvider.addDataDisplay(table);

	    // Add the data to the data provider, which automatically pushes it to the
	    // widget.
	    List<SalaryInfo> salaryList = dataProvider.getList();;
	    for (SalaryInfo salary : this.employeeSalaryObject.getEmployeeSalaries()) {
	    	salaryList.add(salary);
	    }
	    
	    // Add rest of columns and activate sorteable if its needed
	    addColumns(table, salaryList);
	    
	    // Create a SimplePager.
	    SimplePager pager = new SimplePager();

	    // Set the cellList as the display.
	    pager.setDisplay(table);
	    
	    // Add the pager and list to the page.
	    VerticalPanel vPanel = new VerticalPanel();
	    vPanel.add(table);
	    vPanel.add(pager);
	    
	    // Add Styles
	    vPanel.addStyleName(style.tableStyle());
	    pager.addStyleName(style.mAuto());
		
		mainContainer.add(vPanel);
	}

	private void addSelectionModel(CellTable<SalaryInfo> table) {
		
		this.selectionModel = new MultiSelectionModel<SalaryInfo>(SalaryInfo.KEY_PROVIDER);
	    table.setSelectionModel(selectionModel, DefaultSelectionEventManager.<SalaryInfo> createCheckboxManager());
	    
	    this.selectionModel.addSelectionChangeHandler(new Handler() {
	        
	        @Override
	        public void onSelectionChange(SelectionChangeEvent event) {
	            if(selectionModel.getSelectedSet().size() > 0) {
	            	deleteButton.setEnabled(true);
	            }else {
	            	deleteButton.setEnabled(false);
	            }
	            
	        }
	    });
	    
	    // Checkbox column. This table will uses a checkbox column for selection.
	    // Alternatively, you can call cellTable.setSelectionEnabled(true) to enable
	    // mouse selection.
	    Column<SalaryInfo, Boolean> checkColumn = new Column<SalaryInfo, Boolean>(
	        new CheckboxCell(true, false)) {
	      @Override
	      public Boolean getValue(SalaryInfo object) {
	        // Get the value from the selection model.
	        return selectionModel.isSelected(object);
	      }
	    };
    
	    // Checkbox at the header row to select/deselect all persons
	    CheckboxCell selectAllHeaderCB = new CheckboxCell(true,true); //the checkbox is true true for dependsOnSelection and handlesSelection for it to work
	    Header<Boolean> selectAllHeader = new Header<Boolean>(selectAllHeaderCB)
	    {
	      @Override
	      public Boolean getValue()
	      {
	        //return true only when all items are selected
	        boolean value = selectionModel.getSelectedSet().size() == employeeSalaryObject.getEmployeeSalaries().size();
	        return value; 
	      }
	    };
	    
	    selectAllHeader.setUpdater(new ValueUpdater<Boolean>()
	    {
	      @Override
	      public void update(Boolean value)
	      {
	        // Select/deselect all persons
	        for (SalaryInfo person : employeeSalaryObject.getEmployeeSalaries())
	        {
	          selectionModel.setSelected(person, value);
	        }
	      }
	    });
	    
	    // Add Selection Column to table
	    table.addColumn(checkColumn,selectAllHeader);
	}
	
	private void addColumns(CellTable<SalaryInfo> table, List<SalaryInfo> salaryList) {
		// Create employee name column.
	    TextColumn<SalaryInfo> employeeNameColumn = new TextColumn<SalaryInfo>() {
	      @Override
	      public String getValue(SalaryInfo salaryInfo) {
	        return salaryInfo.getEmployeeName();
	      }
	    };

	    // Make the employee name column sortable.
	    employeeNameColumn.setSortable(true);
	    
	    // Create workplace name column.
	    TextColumn<SalaryInfo> workplaceNameColumn = new TextColumn<SalaryInfo>() {
	      @Override
	      public String getValue(SalaryInfo salaryInfo) {
	        return salaryInfo.getWorkplaceName();
	      }
	    };

	    // Make the workplace name column sortable.
	    workplaceNameColumn.setSortable(true);
	    
	    // Create enterprise name column.
	    TextColumn<SalaryInfo> enterpriseNameColumn = new TextColumn<SalaryInfo>() {
	      @Override
	      public String getValue(SalaryInfo salaryInfo) {
	        return salaryInfo.getEnterpriseName();
	      }
	    };
	    
	    // Create start date column.
	    TextColumn<SalaryInfo> startDateColumn = new TextColumn<SalaryInfo>() {
	      @Override
	      public String getValue(SalaryInfo salaryInfo) {
	        return salaryInfo.getStartDate()+"";
	      }
	    };

	    // Make the  start date column sortable.
	    startDateColumn.setSortable(true);
	    
	    // Create end date column.
	    TextColumn<SalaryInfo> endtDateColumn = new TextColumn<SalaryInfo>() {
	      @Override
	      public String getValue(SalaryInfo salaryInfo) {
	        return salaryInfo.getEndDate()+"";
	      }
	    };

	    // Make the end date column sortable.
	    endtDateColumn.setSortable(true);
	    
	    // Create type column.
	    TextColumn<SalaryInfo> typeColumn = new TextColumn<SalaryInfo>() {
	      @Override
	      public String getValue(SalaryInfo salaryInfo) {
	        return salaryInfo.getType().getDescription();
	      }
	    };
	    
	    // Create total payment column.
	    TextColumn<SalaryInfo> totalPaymentColumn = new TextColumn<SalaryInfo>() {
	      @Override
	      public String getValue(SalaryInfo salaryInfo) {
	        return salaryInfo.getTotalPayment()+"";
	      }
	    };
	    
	    // Create total deduction column.
	    TextColumn<SalaryInfo> totalDeductionColumn = new TextColumn<SalaryInfo>() {
	      @Override
	      public String getValue(SalaryInfo salaryInfo) {
	        return salaryInfo.getTotalDecuction()+"";
	      }
	    };
	    
	    // Create total liquid column.
	    TextColumn<SalaryInfo> totalLiquidColumn = new TextColumn<SalaryInfo>() {
	      @Override
	      public String getValue(SalaryInfo salaryInfo) {
	        return salaryInfo.getTotalLiquid()+"";
	      }
	    };
	    
//	    ActionCell<SalaryInfo> previewCell = new ActionCell<SalaryInfo>("Preview", new ActionCell.Delegate<SalaryInfo>() {
//
//			@Override
//			public void execute(SalaryInfo salary) {
//				Window.alert("Salary ID : " + salary.getId());
//				new SalaryPreviewDialog(salary, "pdf");
//			}
//			
//		});
//	    
//	    Column<SalaryInfo, SalaryInfo> previewColumn = new Column<SalaryInfo, SalaryInfo>(previewCell) {
//
//			@Override
//			public SalaryInfo getValue(SalaryInfo object) {
//				return object;
//			}
//			
//			@Override
//			public void render(Context context, SalaryInfo object, SafeHtmlBuilder sb) {
//				if(null != object) {
//					sb.appendHtmlConstant("<button type=\"button\" class=\"aon-finding-toolbar-item aon-icon-page\" style=\"border: none !important;\"></button>");
//				}
//			}
//		};
		
		ActionCell<SalaryInfo> downloadCell = new ActionCell<SalaryInfo>("Download", new ActionCell.Delegate<SalaryInfo>() {

			@Override
			public void execute(SalaryInfo salary) {
				String printURL = URL.encode(GWT.getModuleBaseURL() + "salary/"
						+ salary.getId() + ".pdf?" + salary.getType().name() );
				
				Window.open(printURL, "_blank", null);
			}
			
		});
	    
	    Column<SalaryInfo, SalaryInfo> downloadColumn = new Column<SalaryInfo, SalaryInfo>(downloadCell) {

			@Override
			public SalaryInfo getValue(SalaryInfo object) {
				return object;
			}
			
			@Override
			public void render(Context context, SalaryInfo object, SafeHtmlBuilder sb) {
				if(null != object) {
					sb.appendHtmlConstant("<button type=\"button\" class=\"aon-finding-toolbar-item aon-icon-mail-save\" style=\"border: none !important;\"></button>");
				}
			}
		};

	    // Add the columns.
	    table.addColumn(employeeNameColumn, "Empleado");
	    table.addColumn(enterpriseNameColumn, "Empresa");
	    table.addColumn(workplaceNameColumn, "C. Trabajo");
	    
	    table.addColumn(startDateColumn, "F. Inicio");
	    table.addColumn(endtDateColumn, "F. Fin");
	    
	    table.addColumn(typeColumn, "Tipo");
	    table.addColumn(totalPaymentColumn, "Pago Total");
	    table.addColumn(totalDeductionColumn, "Deduccion Total");
	    table.addColumn(totalLiquidColumn, "Liquido Total");
	    
//	    table.addColumn(previewColumn, "Previsualizar");
	    table.addColumn(downloadColumn, "Descargar / Previsualizar");
	      
	    
	    // Add a ColumnSortEvent.ListHandler to connect sorting to the java.util.List.
	    ListHandler<SalaryInfo> columnSortHandler = new ListHandler<SalaryInfo>(salaryList);
	    columnSortHandler.setComparator(employeeNameColumn, new Comparator<SalaryInfo>() {
	          public int compare(SalaryInfo o1, SalaryInfo o2) {
	            if (o1 == o2) {
	              return 0;
	            }

	            // Compare the name columns.
	            if (o1 != null) {
	              return (o2 != null) ? o1.getEmployeeName().compareTo(o2.getEmployeeName()) : 1;
	            }
	            return -1;
	          }
	        });
	    
	    columnSortHandler.setComparator(workplaceNameColumn, new Comparator<SalaryInfo>() {
	          public int compare(SalaryInfo o1, SalaryInfo o2) {
	            if (o1 == o2) {
	              return 0;
	            }

	            // Compare the name columns.
	            if (o1 != null) {
	              return (o2 != null) ? o1.getWorkplaceName().compareTo(o2.getWorkplaceName()) : 1;
	            }
	            return -1;
	          }
	        });
	    
	    columnSortHandler.setComparator(startDateColumn, new Comparator<SalaryInfo>() {
	          public int compare(SalaryInfo o1, SalaryInfo o2) {
	            if (o1 == o2) {
	              return 0;
	            }

	            // Compare the name columns.
	            if (o1 != null) {
	              return (o2 != null) ? o1.getStartDate().compareTo(o2.getStartDate()) : 1;
	            }
	            return -1;
	          }
	        });
	    
	    columnSortHandler.setComparator(endtDateColumn, new Comparator<SalaryInfo>() {
	          public int compare(SalaryInfo o1, SalaryInfo o2) {
	            if (o1 == o2) {
	              return 0;
	            }

	            // Compare the name columns.
	            if (o1 != null) {
	              return (o2 != null) ? o1.getEndDate().compareTo(o2.getEndDate()) : 1;
	            }
	            return -1;
	          }
	        });
	    
	    table.addColumnSortHandler(columnSortHandler);

	    // We know that the data is sorted alphabetically by default.
	    table.getColumnSortList().push(employeeNameColumn);
	}
	
	@Override
	public void onContextMenu(ContextMenuEvent event) {
	}
}
