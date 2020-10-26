package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DeckPanel;
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

public class MainContrataContract extends MainEntryPoint {

	private class ContrataEmployeeImpl extends ContrataEmployee{

		@Override
		protected void onListShow(boolean reloadEmployees) {
			if(reloadEmployees)
				redrawTable();
			else 
				employeeDataGrid.redraw();
			
			deckPanel.showWidget(0);
		}
		
	}
	
	interface Binder extends UiBinder<Widget, MainContrataContract> {}
	
	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String suggestBox();
	}
	
	@UiField
	Button newContractButton;
	
	@UiField
	SuggestBox employeeSB;
	
	@UiField
	CheckBox inactiveContractsCB;
	
	@UiField
	HTMLPanel mainTablePanel;
	
	@UiField
	HTMLPanel mainContainer;
	
	@UiField
	DeckPanel deckPanel;
	
	@UiField(provided = true)
	DataGrid<EmployeeContractInfo> employeeDataGrid;
	
	@UiField(provided = true)
	ContrataEmployee contrataEmployee;
	
	// --------------------------------------------------------------------------------------------
	// 										VARIABLES
	// --------------------------------------------------------------------------------------------
		
	private MainContrataContractObject mainContrataContractObject;
	private NoSelectionModel<EmployeeContractInfo> selectionCCCInfoModel;
	private DateTimeFormat formatFullDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	private List<EmployeeContractInfo> employeesList = Collections.emptyList();
	
	public MainContrataContract() {
		contrataEmployee = new ContrataEmployeeImpl();
		
		provideEmployeesDataGrid();
		
		// Add style to table header
	    addStyleToHeader();
		
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
		AON.ensureInjected();
	
		Widget ui = binder.createAndBindUi(this);
		RootLayoutPanel.get("rootPanel").add(ui);
		
		// Show table
		deckPanel.showWidget(0);
	}

	// --------------------------------------------------------------------------------------------
	// 									PROVIDE SALARY DATA GRID
	// --------------------------------------------------------------------------------------------

	private void provideEmployeesDataGrid() {
		employeesList  = Collections.emptyList();
		
		// Resource Style CellTable
		employeeDataGrid = new CustomDataGrid<EmployeeContractInfo>(Integer.MAX_VALUE, EmployeeContractInfo.KEY_PROVIDER);
		employeeDataGrid.setWidth("100%");
		
		//Do not refresh the headers every time the dataGrid is updated.
		employeeDataGrid.setAutoHeaderRefreshDisabled(true);
		
		// Set the message to display when the table is empty.
		employeeDataGrid.setEmptyTableWidget(new Label("No existen contratos".toUpperCase()));
		
		// Add a selection model so we can select cells.
	    this.selectionCCCInfoModel = new NoSelectionModel<EmployeeContractInfo>(EmployeeContractInfo.KEY_PROVIDER);
	    employeeDataGrid.setSelectionModel(this.selectionCCCInfoModel);
		
	    // Initialize the columns.
	    addEmployeeInfoColumns(this.selectionCCCInfoModel);
	    
	    new ListDataProvider<EmployeeContractInfo>(Collections.emptyList()).addDataDisplay(employeeDataGrid);

	}
	
	private void addEmployeeInfoColumns(NoSelectionModel<EmployeeContractInfo> selectionCCCInfoModel) {
		selectionCCCInfoModel.addSelectionChangeHandler(new Handler() {
	        
	        @Override
	        public void onSelectionChange(SelectionChangeEvent event) {
	        	EmployeeContractInfo employeeContractInfo = selectionCCCInfoModel.getLastSelectedObject();
	        	String fullName = employeeContractInfo.getEmployeeInfo().getFullName();
	        	
	        	DomainEmployeesServiceAsync employeesService = DomainEmployeesServiceAsync.newInstance();
	    		DomainEnterprisesServiceAsync enterprisesService = DomainEnterprisesServiceAsync.newInstance();
	    		
//	    		contrataEmployee = new ContrataEmployee();
	    		
	    		ContrataEmployeeObject contrataEmployeeDialogObject = new ContrataEmployeeObject(null, employeesService, enterprisesService);
	    		contrataEmployee.setContrataEmployeeObject(contrataEmployeeDialogObject, employeeContractInfo);
	        	
	    		deckPanel.showWidget(1);
	        }
	    });
	    
	    // Add Selection Column to table
	    employeeDataGrid.setSelectionModel(selectionCCCInfoModel);
		
		//----------------------------------------------------------------------
	    //							CREATE COLUMNS
	    //----------------------------------------------------------------------
		
		TextColumn<EmployeeContractInfo> employeeNameColumn = new TextColumn<EmployeeContractInfo>() {
	      @Override
	      public String getValue(EmployeeContractInfo employeeContractInfo) {
	        return employeeContractInfo.getEmployeeInfo().getFullName();
	      }
	    };

	    employeeNameColumn.setSortable(true);
	     
	    TextColumn<EmployeeContractInfo> documentColumn = new TextColumn<EmployeeContractInfo>() {
	      @Override
	      public String getValue(EmployeeContractInfo employeeContractInfo) {
	        return employeeContractInfo.getEmployeeInfo().getDocument();
	      }
	    };

	    documentColumn.setSortable(true);
	    documentColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    employeeDataGrid.setColumnWidth(documentColumn, 10, Unit.PCT);
	    
	    TextColumn<EmployeeContractInfo> ssNumberColumn = new TextColumn<EmployeeContractInfo>() {
	      @Override
	      public String getValue(EmployeeContractInfo employeeContractInfo) {
	        return employeeContractInfo.getEmployeeInfo().getSsNumber();
	      }
	    };

	    ssNumberColumn.setSortable(true);
	    ssNumberColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    employeeDataGrid.setColumnWidth(ssNumberColumn, 10, Unit.PCT);
	    
	    TextColumn<EmployeeContractInfo> contractTypeColumn = new TextColumn<EmployeeContractInfo>() {
	      @Override
	      public String getValue(EmployeeContractInfo employeeContractInfo) {
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
	    
	    TextColumn<EmployeeContractInfo> startDateColumn = new TextColumn<EmployeeContractInfo>() {
	      @Override
	      public String getValue(EmployeeContractInfo employeeContractInfo) {
	    	  return formatFullDate.format(employeeContractInfo.getContractInfo().getStartDate());
	      }
	    };

	    startDateColumn.setSortable(true);
	    startDateColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    employeeDataGrid.setColumnWidth(startDateColumn, 10, Unit.PCT);
	    
	    TextColumn<EmployeeContractInfo> endDateColumn = new TextColumn<EmployeeContractInfo>() {
		      @Override
		      public String getValue(EmployeeContractInfo employeeContractInfo) {
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
	}
	
	// --------------------------------------------------------------------------------------------
	// 										ON MODULE LOAD
	// --------------------------------------------------------------------------------------------
	
	public void onModuleLoad(MainContrataContractObject mainContrataContractObject) {
		this.mainContrataContractObject = mainContrataContractObject;
		
		this.mainContrataContractObject.getEmployeesInfo(false,
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
		
		List<String> enterprises = new ArrayList<>(mainContrataContractObject.getEmployeesMap().keySet());
		
		List<String> enterprisesSuggest = new ArrayList<String>();
		for(String enterprise : enterprises)
			enterprisesSuggest.add(enterprise+"");
		
		MultiWordSuggestOracle orclEnterprise = (MultiWordSuggestOracle) employeeSB.getSuggestOracle();
		orclEnterprise.addAll(enterprisesSuggest);
		employeeSB.setAutoSelectEnabled(false);
		
		employeeSB.addKeyUpHandler(e-> {
			String value = employeeSB.getValue();
			if(StringUtils.isBlank(value) || value.length() < 3) {
				mainContrataContractObject.resetEmployeesList();
			} else {
				List<Integer> employeesContractIds = mainContrataContractObject.getEmployeesContractIds(value);
				mainContrataContractObject.filterEmployeesList(employeesContractIds);
			}
			
			initContractTable();
		});
		
		employeeSB.addSelectionHandler(e -> {
			String value = employeeSB.getValue();
			List<Integer> employeesContractIds = mainContrataContractObject.getEmployeesContractIds(value);
			mainContrataContractObject.filterEmployeesList(employeesContractIds);
			
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
	    ListDataProvider<EmployeeContractInfo> dataProvider = new ListDataProvider<EmployeeContractInfo>();

	    // Connect the table to the data provider.
	    dataProvider.addDataDisplay(employeeDataGrid);
	    
	    // Add the data to the data provider, which automatically pushes it to the
	    // widget.
	    List<EmployeeContractInfo> employeeContractInfoList = dataProvider.getList();
	    employeeContractInfoList.clear();
	    
	    this.employeesList = mainContrataContractObject.getEmployeesList();
	    
	    for (EmployeeContractInfo employeeContractInfo : this.employeesList) {
	    	employeeContractInfoList.add(employeeContractInfo);
	    } 
	    
	    // Set page size
	    employeeDataGrid.setPageSize(employeesList.size());
	    
	    // Add style to table header
	    addStyleToHeader();
	    
	    addSortColums(employeeContractInfoList); 
		
	}
	
	private void addSortColums(List<EmployeeContractInfo> employeeContractInfoList) {
		ListHandler<EmployeeContractInfo> columnSortHandler = new ListHandler<EmployeeContractInfo>(employeeContractInfoList);
		
	    columnSortHandler.setComparator(employeeDataGrid.getColumn(0), new Comparator<EmployeeContractInfo>() {
	          public int compare(EmployeeContractInfo o1, EmployeeContractInfo o2) {
		            if (o1 == o2) {
		              return 0;
		            }
	
		            if (o1 != null) {
		              return (o2 != null) ? o1.getEmployeeInfo().getFullName().compareTo(o2.getEmployeeInfo().getFullName()) : 1;
		            }
		            
		            return -1;
	          }
	    });
	    
	    columnSortHandler.setComparator(employeeDataGrid.getColumn(1), new Comparator<EmployeeContractInfo>() {
	          public int compare(EmployeeContractInfo o1, EmployeeContractInfo o2) {
		            if (o1 == o2) {
		              return 0;
		            }
	
		            if (o1 != null) {
		              return (o2 != null) ? o1.getEmployeeInfo().getDocument().compareTo(o2.getEmployeeInfo().getDocument()) : 1;
		            }
		            
		            return -1;
	          }
	    });
	    
	    columnSortHandler.setComparator(employeeDataGrid.getColumn(2), new Comparator<EmployeeContractInfo>() {
	          public int compare(EmployeeContractInfo o1, EmployeeContractInfo o2) {
		            if (o1 == o2) {
		              return 0;
		            }
	
		            if (o1 != null) {
		              return (o2 != null) ? o1.getEmployeeInfo().getSsNumber().compareTo(o2.getEmployeeInfo().getSsNumber()) : 1;
		            }
		            
		            return -1;
	          }
	    });
	    
	    
	    columnSortHandler.setComparator(employeeDataGrid.getColumn(3), new Comparator<EmployeeContractInfo>() {
	          public int compare(EmployeeContractInfo o1, EmployeeContractInfo o2) {
		            if (o1 == o2) {
		              return 0;
		            }
	
		            if (o1 != null) {
		              return (o2 != null) ? o1.getContractInfo().getContractType().compareTo(o2.getContractInfo().getContractType()) : 1;
		            }
		            
		            return -1;
	          }
	    });
	    
	    columnSortHandler.setComparator(employeeDataGrid.getColumn(4), new Comparator<EmployeeContractInfo>() {
	          public int compare(EmployeeContractInfo o1, EmployeeContractInfo o2) {
		            if (o1 == o2) {
		              return 0;
		            }
	
		            if (o1 != null) {
		              return (o2 != null) ? o1.getContractInfo().getStartDate().compareTo(o2.getContractInfo().getStartDate()) : 1;
		            }
		            
		            return -1;
	         }
	    });
	    
	    columnSortHandler.setComparator(employeeDataGrid.getColumn(5), new Comparator<EmployeeContractInfo>() {
	          public int compare(EmployeeContractInfo o1, EmployeeContractInfo o2) {
		            if (o1.getContractInfo().getEndDate() == o2.getContractInfo().getEndDate()) {
		              return 0;
		            }
	
		            if (o1.getContractInfo().getEndDate() != null) {
		              return (o2.getContractInfo().getEndDate() != null) ? o1.getContractInfo().getEndDate().compareTo(o2.getContractInfo().getEndDate()) : 1;
		            }
		            
		            return -1;
	         }
	   });
	    
	    
	    // We know that the data is sorted alphabetically by default.
	    employeeDataGrid.getColumn(0).setDefaultSortAscending(false);
	    employeeDataGrid.getColumnSortList().push(employeeDataGrid.getColumn(0));   
	    
	    employeeDataGrid.addColumnSortHandler(columnSortHandler);

	}
	
	// --------------------------------------------------------------------------------------------
	// 										UI HANDLERS
	// --------------------------------------------------------------------------------------------
	
	@UiHandler("inactiveContractsCB")
	public void onInactiveContractsCBValueChange(ValueChangeEvent<Boolean> event) {
		this.mainContrataContractObject.getEmployeesInfo(event.getValue(),
				s -> {
					initEnterpriseSB();
					initContractTable();
					setTableHeights();
				},
				f -> {}
		);
	}
	
	@UiHandler("newContractButton")
	public void onNewContractButton(ClickEvent event) {
		DomainEmployeesServiceAsync employeesService = DomainEmployeesServiceAsync.newInstance();
		DomainEnterprisesServiceAsync enterprisesService = DomainEnterprisesServiceAsync.newInstance();
		
		EmployeeDialog employeeDialog = new EmployeeDialog(true) {
			@Override
			protected void onAccept() {
				WarningDialog dialog = new WarningDialog("AVISO", "Desea dar de alta el contrato?");
				dialog.setModal(true);
				dialog.setAnimationEnabled(true);
				dialog.center();
				dialog.show();
				redrawTable();
			}
		};
		
		EmployeeDialogObject employeeDialogObject = new EmployeeDialogObject(null, employeesService, enterprisesService);
		employeeDialog.setEmployeeDialogObject(employeeDialogObject);
		employeeDialog.setModal(true);
		employeeDialog.setAnimationEnabled(true);
		employeeDialog.center();
		employeeDialog.show();
	}
	
	private void redrawTable() {
		this.inactiveContractsCB.setValue(false);
		this.mainContrataContractObject.getEmployeesInfo(false,
				s -> {
					initContractTable();
					setTableHeights();
				},
				f -> {}
		);
	}
	
	protected void onListShow(boolean reloadEmployees) {
		if(reloadEmployees)
			redrawTable();
		else 
			this.employeeDataGrid.redraw();
		
		this.deckPanel.showWidget(0);
		
	}
	

}
