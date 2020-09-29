package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.IT;
import com.esferalia.aon.gwt.payroll.shared.ITEmployee;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;

public class MainContrataIT extends MainEntryPoint {
	
	interface Binder extends UiBinder<Widget, MainContrataIT> {}
	
	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String suggestBox();
	}
	
	@UiField
	SuggestBox employeeSB;
	
	@UiField
	CheckBox inactiveContractsCB;
	
	@UiField
	HTMLPanel mainTablePanel;
	
	@UiField
	HTMLPanel mainContainer;
	
	@UiField(provided = true)
	DataGrid<ITEmployee> employeeDataGrid;
	
	// --------------------------------------------------------------------------------------------
	// 										VARIABLES
	// --------------------------------------------------------------------------------------------
		
	private MainContrataITObject mainContrataITObject;
	private NoSelectionModel<ITEmployee> selectionCCCInfoModel;
	private DateTimeFormat formatFullDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	private List<ITEmployee> employeesList = Collections.emptyList();
	
	public MainContrataIT() {
		provideEmployeesDataGrid();
		
		// Add style to table header
	    addStyleToHeader();
		
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
		AON.ensureInjected();
	
		Widget ui = binder.createAndBindUi(this);
		RootLayoutPanel.get("rootPanel").add(ui);
	}

	// --------------------------------------------------------------------------------------------
	// 									PROVIDE SALARY DATA GRID
	// --------------------------------------------------------------------------------------------

	private void provideEmployeesDataGrid() {
		employeesList  = Collections.emptyList();
		
		// Resource Style CellTable
		employeeDataGrid = new CustomDataGrid<ITEmployee>(Integer.MAX_VALUE, ITEmployee.KEY_PROVIDER);
		employeeDataGrid.setWidth("100%");
		
		//Do not refresh the headers every time the dataGrid is updated.
		employeeDataGrid.setAutoHeaderRefreshDisabled(true);
		
		// Set the message to display when the table is empty.
		employeeDataGrid.setEmptyTableWidget(new Label("No existen contratos".toUpperCase()));
		
		// Add a selection model so we can select cells.
	    this.selectionCCCInfoModel = new NoSelectionModel<ITEmployee>(ITEmployee.KEY_PROVIDER);
	    employeeDataGrid.setSelectionModel(this.selectionCCCInfoModel);
		
	    // Initialize the columns.
	    addEmployeeInfoColumns(this.selectionCCCInfoModel);
	    
	    new ListDataProvider<ITEmployee>(Collections.emptyList()).addDataDisplay(employeeDataGrid);

	}
	
	private void addEmployeeInfoColumns(NoSelectionModel<ITEmployee> selectionCCCInfoModel) {
		selectionCCCInfoModel.addSelectionChangeHandler(new Handler() {
	        
	        @Override
	        public void onSelectionChange(SelectionChangeEvent event) {
	        	ITEmployee employeeITInfo = selectionCCCInfoModel.getLastSelectedObject();
	        	String fullName = employeeITInfo.getEmployeeInfo().getFullName();
	        	
	        	ITDialog itDialog = new ITDialog(fullName, true) {

					@Override
					protected void onDelete(IT it) {
						mainContrataITObject.deleteIT(it,
								s -> {
									WarningDialog dialog = new WarningDialog("AVISO", "El parte IT ha sido borrado correctamente.");
									dialog.setModal(true);
									dialog.setAnimationEnabled(true);
									dialog.show();
									dialog.center();
									
									mainContrataITObject.getEmployeesInfo(false,
											t -> {
												initContractTable();
												setTableHeights();
											},
											f -> {}
									);
								},
								f -> {});
					}

					@Override
					protected void onAccept() {
						mainContrataITObject.createUpdateITEmployee(employeeITInfo,
								s -> {
									WarningDialog dialog = new WarningDialog("AVISO", s);
									dialog.setModal(true);
									dialog.setAnimationEnabled(true);
									dialog.show();
									dialog.center();
									
									mainContrataITObject.getEmployeesInfo(false,
											t -> {
												initContractTable();
												setTableHeights();
											},
											f -> {}
									);
								},
								f -> {});
					}
	        		
	        	};
	        	
	        	ITDialogObject itDialogObject = new ITDialogObject(employeeITInfo);
	        	itDialog.setITDialogObject(itDialogObject);
	        	
	        	itDialog.setModal(true);
	        	itDialog.setAnimationEnabled(true);
	        	itDialog.show();
	        	itDialog.center();
	        }
	    });
	    
	    // Add Selection Column to table
	    employeeDataGrid.setSelectionModel(selectionCCCInfoModel);
		
		//----------------------------------------------------------------------
	    //							CREATE COLUMNS
	    //----------------------------------------------------------------------
		
		TextColumn<ITEmployee> employeeNameColumn = new TextColumn<ITEmployee>() {
	      @Override
	      public String getValue(ITEmployee employeeContractInfo) {
	        return employeeContractInfo.getEmployeeInfo().getFullName();
	      }
	    };

	    employeeNameColumn.setSortable(true);
	    
	    TextColumn<ITEmployee> statusColumn = new TextColumn<ITEmployee>() {
	      @Override
	      public String getValue(ITEmployee employeeContractInfo) {
	        return employeeContractInfo.getStatus() == (byte)0 ? "ALTA" : "BAJA";
	      }
	    };

	    statusColumn.setSortable(true);
	    statusColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    employeeDataGrid.setColumnWidth(statusColumn, 10, Unit.PCT);
	     
	    TextColumn<ITEmployee> documentColumn = new TextColumn<ITEmployee>() {
	      @Override
	      public String getValue(ITEmployee employeeContractInfo) {
	        return employeeContractInfo.getEmployeeInfo().getDocument();
	      }
	    };

	    documentColumn.setSortable(true);
	    documentColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    employeeDataGrid.setColumnWidth(documentColumn, 10, Unit.PCT);
	    
	    TextColumn<ITEmployee> ssNumberColumn = new TextColumn<ITEmployee>() {
	      @Override
	      public String getValue(ITEmployee employeeContractInfo) {
	        return employeeContractInfo.getEmployeeInfo().getSsNumber();
	      }
	    };

	    ssNumberColumn.setSortable(true);
	    ssNumberColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    employeeDataGrid.setColumnWidth(ssNumberColumn, 10, Unit.PCT);
	    
	    TextColumn<ITEmployee> contractTypeColumn = new TextColumn<ITEmployee>() {
	      @Override
	      public String getValue(ITEmployee employeeContractInfo) {
	    	  if((byte)3 == employeeContractInfo.getContractInfo().getSsRegimen())
	    		  return "RETA";
	    	  if("000" == employeeContractInfo.getContractInfo().getContractType())
	    		  return "BECARIO";
	    	  return employeeContractInfo.getContractInfo().getContractType();
	      }

	    };

	    contractTypeColumn.setSortable(true);
	    contractTypeColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    employeeDataGrid.setColumnWidth(contractTypeColumn, 10, Unit.PCT);
	    
	    TextColumn<ITEmployee> startDateColumn = new TextColumn<ITEmployee>() {
	      @Override
	      public String getValue(ITEmployee employeeContractInfo) {
	    	  return formatFullDate.format(employeeContractInfo.getContractInfo().getStartDate());
	      }
	    };

	    startDateColumn.setSortable(true);
	    startDateColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    employeeDataGrid.setColumnWidth(startDateColumn, 10, Unit.PCT);
	    
	    TextColumn<ITEmployee> endDateColumn = new TextColumn<ITEmployee>() {
		      @Override
		      public String getValue(ITEmployee employeeContractInfo) {
		    	  if(null != employeeContractInfo.getContractInfo().getEndDate())
		    		  return formatFullDate.format(employeeContractInfo.getContractInfo().getEndDate());
		    	  
		    	  return "";
		      }
		    };

		endDateColumn.setSortable(true);
		endDateColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		employeeDataGrid.setColumnWidth(endDateColumn, 10, Unit.PCT);
	    
	    // Add the columns.
	    employeeDataGrid.addColumn(employeeNameColumn, "Nobre Completo");
	    employeeDataGrid.addColumn(statusColumn, "Estado SS");
	    employeeDataGrid.addColumn(documentColumn, "Documento");
	    employeeDataGrid.addColumn(ssNumberColumn, "N" + String.valueOf("\u00B0") + " SS");
	    employeeDataGrid.addColumn(contractTypeColumn, "Tipo Contrato");
	    employeeDataGrid.addColumn(startDateColumn, "Fecha Inicio");
	    employeeDataGrid.addColumn(endDateColumn, "Fecha Fin");
	      
	}

	// --------------------------------------------------------------------------------------------
	// 									HEADER STYLES
	// --------------------------------------------------------------------------------------------
	
	public void addStyleToHeader() {
		employeeDataGrid.getHeader(0).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		employeeDataGrid.getHeader(1).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		employeeDataGrid.getHeader(2).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		employeeDataGrid.getHeader(3).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		employeeDataGrid.getHeader(4).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		employeeDataGrid.getHeader(5).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		employeeDataGrid.getHeader(6).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
	}
	
	// --------------------------------------------------------------------------------------------
	// 										ON MODULE LOAD
	// --------------------------------------------------------------------------------------------
	
	public void onModuleLoad(MainContrataITObject mainContrataContractObject) {
		this.mainContrataITObject = mainContrataContractObject;
		
		this.mainContrataITObject.getEmployeesInfo(false,
				s -> {
					initEnterpriseSB();
					initContractTable();
					setTableHeights();
				},
				f -> {}
		);
	}
	
	private void initEnterpriseSB() {
		// Enteprise List
		
		List<String> enterprises = new ArrayList<>(mainContrataITObject.getEmployeesMap().keySet());
		
		List<String> enterprisesSuggest = new ArrayList<String>();
		for(String enterprise : enterprises)
			enterprisesSuggest.add(enterprise+"");
		
		MultiWordSuggestOracle orclEnterprise = (MultiWordSuggestOracle) employeeSB.getSuggestOracle();
		orclEnterprise.addAll(enterprisesSuggest);
		employeeSB.setAutoSelectEnabled(false);
		
		employeeSB.addKeyUpHandler(e-> {
			String value = employeeSB.getValue();
			if(StringUtils.isBlank(value) || value.length() < 3) {
				mainContrataITObject.resetEmployeesList();
			} else {
				List<Integer> employeesContractIds = mainContrataITObject.getEmployeesContractIds(value);
				mainContrataITObject.filterEmployeesList(employeesContractIds);
			}
			
			initContractTable();
		});
		
		employeeSB.addSelectionHandler(e -> {
			String value = employeeSB.getValue();
			List<Integer> employeesContractIds = mainContrataITObject.getEmployeesContractIds(value);
			mainContrataITObject.filterEmployeesList(employeesContractIds);
			
			initContractTable();
			employeeDataGrid.redraw();
		});
	}

	private void setTableHeights() {
		employeeDataGrid.getElement().getStyle().setHeight(100, Unit.PCT);
		mainTablePanel.getElement().getStyle().setHeight((Window.getClientHeight() - 255), Unit.PX);
	}

	// --------------------------------------------------------------------------------------------
	// 										INIT CCCs TABLE
	// --------------------------------------------------------------------------------------------

	private void initContractTable() {		
		// Create a data provider.
	    ListDataProvider<ITEmployee> dataProvider = new ListDataProvider<ITEmployee>();

	    // Connect the table to the data provider.
	    dataProvider.addDataDisplay(employeeDataGrid);
	    
	    // Add the data to the data provider, which automatically pushes it to the
	    // widget.
	    List<ITEmployee> employeeContractInfoList = dataProvider.getList();
	    employeeContractInfoList.clear();
	    
	    this.employeesList = mainContrataITObject.getEmployeesList();
	    
	    for (ITEmployee employeeContractInfo : this.employeesList) {
	    	employeeContractInfoList.add(employeeContractInfo);
	    } 
	    
	    // Set page size
	    employeeDataGrid.setPageSize(employeesList.size());
	    
	    // Add style to table header
	    addStyleToHeader();
	    
	    addSortColums(employeeContractInfoList); 
		
	}
	
	private void addSortColums(List<ITEmployee> employeeContractInfoList) {
		ListHandler<ITEmployee> columnSortHandler = new ListHandler<ITEmployee>(employeeContractInfoList);
		
	    columnSortHandler.setComparator(employeeDataGrid.getColumn(0), new Comparator<ITEmployee>() {
	          public int compare(ITEmployee o1, ITEmployee o2) {
		            if (o1 == o2) {
		              return 0;
		            }
	
		            if (o1 != null) {
		              return (o2 != null) ? o1.getEmployeeInfo().getFullName().compareTo(o2.getEmployeeInfo().getFullName()) : 1;
		            }
		            
		            return -1;
	          }
	    });
	    
	    columnSortHandler.setComparator(employeeDataGrid.getColumn(1), new Comparator<ITEmployee>() {
	          public int compare(ITEmployee o1, ITEmployee o2) {
		            if (o1 == o2) {
		              return 0;
		            }
	
		            if (o1 != null) {
		              return (o2 != null) ? o1.getStatus().compareTo(o2.getStatus()) : 1;
		            }
		            
		            return -1;
	          }
	    });
	    
	    columnSortHandler.setComparator(employeeDataGrid.getColumn(2), new Comparator<ITEmployee>() {
	          public int compare(ITEmployee o1, ITEmployee o2) {
		            if (o1 == o2) {
		              return 0;
		            }
	
		            if (o1 != null) {
		              return (o2 != null) ? o1.getEmployeeInfo().getDocument().compareTo(o2.getEmployeeInfo().getDocument()) : 1;
		            }
		            
		            return -1;
	          }
	    });
	    
	    columnSortHandler.setComparator(employeeDataGrid.getColumn(3), new Comparator<ITEmployee>() {
	          public int compare(ITEmployee o1, ITEmployee o2) {
		            if (o1 == o2) {
		              return 0;
		            }
	
		            if (o1 != null) {
		              return (o2 != null) ? o1.getEmployeeInfo().getSsNumber().compareTo(o2.getEmployeeInfo().getSsNumber()) : 1;
		            }
		            
		            return -1;
	          }
	    });
	    
	    
	    columnSortHandler.setComparator(employeeDataGrid.getColumn(4), new Comparator<ITEmployee>() {
	          public int compare(ITEmployee o1, ITEmployee o2) {
		            if (o1 == o2) {
		              return 0;
		            }
	
		            if (o1 != null) {
		              return (o2 != null) ? o1.getContractInfo().getContractType().compareTo(o2.getContractInfo().getContractType()) : 1;
		            }
		            
		            return -1;
	          }
	    });
	    
	    columnSortHandler.setComparator(employeeDataGrid.getColumn(5), new Comparator<ITEmployee>() {
	          public int compare(ITEmployee o1, ITEmployee o2) {
		            if (o1 == o2) {
		              return 0;
		            }
	
		            if (o1 != null) {
		              return (o2 != null) ? o1.getContractInfo().getStartDate().compareTo(o2.getContractInfo().getStartDate()) : 1;
		            }
		            
		            return -1;
	         }
	    });
	    
	    columnSortHandler.setComparator(employeeDataGrid.getColumn(6), new Comparator<ITEmployee>() {
	          public int compare(ITEmployee o1, ITEmployee o2) {
		            if (o1.getContractInfo().getEndDate() == o2.getContractInfo().getEndDate()) {
		              return 0;
		            }
	
		            if (o1.getContractInfo().getEndDate() != null) {
		              return (o2.getContractInfo().getEndDate() != null) ? o1.getContractInfo().getEndDate().compareTo(o2.getContractInfo().getEndDate()) : 1;
		            }
		            
		            return -1;
	         }
	   });
	    
	    
	    employeeDataGrid.addColumnSortHandler(columnSortHandler);

	    // We know that the data is sorted alphabetically by default.
	    employeeDataGrid.getColumn(0).setDefaultSortAscending(false);
	    employeeDataGrid.getColumnSortList().push(employeeDataGrid.getColumn(0));   
	}
	
	// --------------------------------------------------------------------------------------------
	// 										UI HANDLERS
	// --------------------------------------------------------------------------------------------
	
	@UiHandler("inactiveContractsCB")
	public void onInactiveContractsCBValueChange(ValueChangeEvent<Boolean> event) {
		this.mainContrataITObject.getEmployeesInfo(event.getValue(),
				s -> {
					initEnterpriseSB();
					initContractTable();
					setTableHeights();
				},
				f -> {}
		);
	}
	
}
