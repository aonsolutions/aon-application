package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.gwt.common.client.widget.MultiFileUpload;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.ContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.FIEService.JsContractInfo;
import com.esferalia.aon.gwt.payroll.shared.FIEService.JsEmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.FIEService.JsIT;
import com.esferalia.aon.gwt.payroll.shared.FIEService.JsITEmployee;
import com.esferalia.aon.gwt.payroll.shared.IT;
import com.esferalia.aon.gwt.payroll.shared.ITEmployee;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
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
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Hidden;
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
	Button backContractButton;
	
	@UiField
	Button listITsButton;
	
	@UiField
	DeckPanel deckPanel;
	
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
	
	@UiField
	SuggestBox itSB;
	
	@UiField
	CheckBox inactiveITsCB;
	
	@UiField
	HTMLPanel mainITTablePanel;
	
	@UiField
	HTMLPanel mainITContainer;
	
	@UiField(provided = true)
	DataGrid<IT> itDataGrid;
	
	@UiField
	Button msjFIEButton;	
	@UiField
	FormPanel msjFIEFormPanel;
	@UiField
	Hidden userNameHidden;
	@UiField
	Hidden domainNameHidden;
	@UiField
	MultiFileUpload msjFIEFileUpload;
	
	@UiField
	HTMLPanel sldToolbarPanel;
	
	// --------------------------------------------------------------------------------------------
	// 										VARIABLES
	// --------------------------------------------------------------------------------------------
		
	private MainContrataITObject mainContrataITObject;
	private NoSelectionModel<ITEmployee> selectionCCCInfoModel;
	private List<ITEmployee> employeesList = Collections.emptyList();
	private NoSelectionModel<IT> selectionITModel;
	private List<IT> itsList = Collections.emptyList();
	private DateTimeFormat formatFullDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private ListDataProvider<ITEmployee> dataProvider ;
	
	
	public MainContrataIT() {
		
		
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
		AON.ensureInjected();

		provideEmployeesDataGrid();
		provideITsDataGrid();

		// Add style to table header
	    addStyleToHeader();
	    addStyleToITHeader();
	
		Widget ui = binder.createAndBindUi(this);
		RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel").add(ui);
		
		deckPanel.showWidget(0);
		inactiveITsCB.setValue(true);
		backContractButton.getElement().getStyle().setDisplay(Display.NONE);
		
		userNameHidden.setValue(Wnd.getCurrentUser());
		domainNameHidden.setValue(Wnd.getCurrentDomainNameURL());
		
				
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
	    
		// Create a data provider.
	    dataProvider = new ListDataProvider<ITEmployee>();
	    // Connect the table to the data provider.
	    dataProvider.addDataDisplay(employeeDataGrid);
	    // Add style to table header
	    addStyleToHeader();

	    // new ListDataProvider<ITEmployee>(Collections.emptyList()).addDataDisplay(employeeDataGrid);

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
			public String getValue(ITEmployee itEmployee) {
				IT it = checkIfIsOpenIt(itEmployee);
		        return null != it ? parseShortLowCauseByte(it.getTypeLowPart()) + " (" + formatFullDate.format(it.getStartDate()) + ")" : "ALTA";
			}
			
			@Override
			public void render(Context context, ITEmployee itEmployee, SafeHtmlBuilder sb) {
				if(null != itEmployee) {
					IT it = checkIfIsOpenIt(itEmployee);
			        String description = null != it ? parseShortLowCauseByte(it.getTypeLowPart()) + " (" + formatFullDate.format(it.getStartDate()) + ")" : "ALTA";
					sb.appendHtmlConstant("<span title=\"" + (null != it ? parseLowCauseByte(it.getTypeLowPart()) : "") + "\">" + description + "</span>");
				}
			}
		};

	    statusColumn.setSortable(true);
	    statusColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    employeeDataGrid.setColumnWidth(statusColumn, 15, Unit.PCT);
	     
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
	    employeeDataGrid.addColumn(employeeNameColumn, "Trabajador");
	    employeeDataGrid.addColumn(statusColumn, "Estado SS");
	    employeeDataGrid.addColumn(documentColumn, "Documento");
	    employeeDataGrid.addColumn(ssNumberColumn, "N" + String.valueOf("\u00B0") + " SS");
	    employeeDataGrid.addColumn(contractTypeColumn, "Tipo Contrato");
	    employeeDataGrid.addColumn(startDateColumn, "Fecha Inicio");
	    employeeDataGrid.addColumn(endDateColumn, "Fecha Fin");
	      
	}

	// --------------------------------------------------------------------------------------------
	// 									PROVIDE SALARY DATA GRID
	// --------------------------------------------------------------------------------------------

	private void provideITsDataGrid() {
		itsList  = Collections.emptyList();
		
		// Resource Style CellTable
		itDataGrid = new CustomDataGrid<IT>(Integer.MAX_VALUE, IT.KEY_PROVIDER);
		itDataGrid.setWidth("100%");
		
		//Do not refresh the headers every time the dataGrid is updated.
		itDataGrid.setAutoHeaderRefreshDisabled(true);
		
		// Set the message to display when the table is empty.
		itDataGrid.setEmptyTableWidget(new Label("No existen ITs activos".toUpperCase()));
		
		// Add a selection model so we can select cells.
	    this.selectionITModel = new NoSelectionModel<IT>(IT.KEY_PROVIDER);
	    itDataGrid.setSelectionModel(this.selectionITModel);
		
	    // Initialize the columns.
	    addITInfoColumns(this.selectionITModel);
	    
	    new ListDataProvider<IT>(Collections.emptyList()).addDataDisplay(itDataGrid);

	}
	
	private void addITInfoColumns(NoSelectionModel<IT> selectionITModel) {
		selectionITModel.addSelectionChangeHandler(new Handler() {
	        
	        @Override
	        public void onSelectionChange(SelectionChangeEvent event) {
	        	IT itInfo = selectionITModel.getLastSelectedObject();
	        	String fullName = itInfo.getFullName();
	        	
	        	ITEmployee itEmployee = mainContrataITObject.getEmployeeITInfo(itInfo.getId());
	        	
	        	ITDialog itDialog = new ITDialog(fullName, false) {

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
												initITTable();
												setTableHeights();
												inactiveITsCB.setValue(true);
											},
											f -> {}
									);
								},
								f -> {});
					}

					@Override
					protected void onAccept() {
						mainContrataITObject.createUpdateITEmployee(itEmployee,
								s -> {
									WarningDialog dialog = new WarningDialog("AVISO", s);
									dialog.setModal(true);
									dialog.setAnimationEnabled(true);
									dialog.show();
									dialog.center();
									
									mainContrataITObject.getEmployeesInfo(false,
											t -> {
												initContractTable();
												initITTable();
												setTableHeights();
												inactiveITsCB.setValue(true);
											},
											f -> {}
									);
								},
								f -> {});
					}
	        		
	        	};
	        	
	        	ITDialogObject itDialogObject = new ITDialogObject(itEmployee);
	        	itDialog.setITDialogObject(itDialogObject, itInfo, true);
	        	
	        	itDialog.setModal(true);
	        	itDialog.setAnimationEnabled(true);
	        	itDialog.show();
	        	itDialog.center();
	        }
	    });
	    
	    // Add Selection Column to table
	    itDataGrid.setSelectionModel(selectionITModel);
		
		//----------------------------------------------------------------------
	    //							CREATE COLUMNS
	    //----------------------------------------------------------------------
		
		TextColumn<IT> employeeNameColumn = new TextColumn<IT>() {
	      @Override
	      public String getValue(IT itInfo) {
	        return itInfo.getFullName();
	      }
	    };

	    employeeNameColumn.setSortable(true);
	    
	    TextColumn<IT> lowDateColumn = new TextColumn<IT>() {
		      @Override
		      public String getValue(IT it) {
		        return formatFullDate.format(it.getStartDate());
		      }
		    };

	    lowDateColumn.setSortable(true);
	    lowDateColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    itDataGrid.setColumnWidth(lowDateColumn, 100, Unit.PX);
	    
	    TextColumn<IT> lowCauseColumn = new TextColumn<IT>() {

			@Override
			public String getValue(IT it) {
				return parseShortLowCauseByte(it.getTypeLowPart());
			}
			
			@Override
			public void render(Context context, IT it, SafeHtmlBuilder sb) {
				if(null != it) {
					sb.appendHtmlConstant("<span title=\"" + parseLowCauseByte(it.getTypeLowPart()) + "\">" + parseShortLowCauseByte(it.getTypeLowPart()) + "</span>");
				}
			}
		};
	    
	    lowCauseColumn.setSortable(true);
	    lowCauseColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    itDataGrid.setColumnWidth(lowCauseColumn, 100, Unit.PX);
	    
	    TextColumn<IT> highDateColumn = new TextColumn<IT>() {
	      @Override
	      public String getValue(IT it) {
	        return null == it.getEndDate() ? "-" : formatFullDate.format(it.getEndDate());
	      }
	    };

	    highDateColumn.setSortable(true);
	    highDateColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    itDataGrid.setColumnWidth(highDateColumn, 100, Unit.PX);
		    
	    TextColumn<IT> highCauseColumn = new TextColumn<IT>() {
	      @Override
	      public String getValue(IT it) {
	        return parseHighCauseByte(it.getTypeHighPart());
	      }
	    };

	    highCauseColumn.setSortable(true);
	    highCauseColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    
	   TextColumn<IT> rechargeColumn = new TextColumn<IT>() {
	      @Override
	      public String getValue(IT it) {
	    	  return it.getParent() == 0 ? "NO" : "SI";
	      }

	    };

	    rechargeColumn.setSortable(true);
	    rechargeColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    itDataGrid.setColumnWidth(rechargeColumn, 70, Unit.PX);
	    
	    // Add the columns.
	    itDataGrid.addColumn(employeeNameColumn, "Trabajador");
	    itDataGrid.addColumn(lowDateColumn, "F. Baja");
	    itDataGrid.addColumn(lowCauseColumn, "Causa Baja");
	    itDataGrid.addColumn(highDateColumn, "F. Alta");
	    itDataGrid.addColumn(highCauseColumn, "Causa Alta");
	    itDataGrid.addColumn(rechargeColumn, "Recaida");
	      
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
	
	public void addStyleToITHeader() {
		itDataGrid.getHeader(0).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		itDataGrid.getHeader(1).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		itDataGrid.getHeader(2).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		itDataGrid.getHeader(3).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		itDataGrid.getHeader(4).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		itDataGrid.getHeader(5).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
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
	
	private void initITSB() {
		List<String> its = new ArrayList<>(mainContrataITObject.getITsMap().keySet());
		
		List<String> itsSuggest = new ArrayList<String>();
		for(String enterprise : its)
			itsSuggest.add(enterprise+"");
		
		MultiWordSuggestOracle orclIT = (MultiWordSuggestOracle) itSB.getSuggestOracle();
		orclIT.addAll(itsSuggest);
		itSB.setAutoSelectEnabled(false);
		
		itSB.addKeyUpHandler(e-> {
			String value = itSB.getValue();
			if(StringUtils.isBlank(value) || value.length() < 3) {
				mainContrataITObject.resetITsList();
			} else {
				List<Integer> itIds = mainContrataITObject.getITsContractIds(value);
				mainContrataITObject.filterITsList(itIds);
			}
			
			initContractTable();
		});
		
		itSB.addSelectionHandler(e -> {
			String value = itSB.getValue();
			List<Integer> itIds = mainContrataITObject.getITsContractIds(value);
			mainContrataITObject.filterEmployeesList(itIds);
			
			initITTable();
			itDataGrid.redraw();
		});
	}

	private void setTableHeights() {
		employeeDataGrid.getElement().getStyle().setHeight(100, Unit.PCT);
		mainTablePanel.getElement().getStyle().setHeight((Window.getClientHeight() - 255), Unit.PX);
		
		itDataGrid.getElement().getStyle().setHeight(100, Unit.PCT);
		mainITTablePanel.getElement().getStyle().setHeight((Window.getClientHeight() - 255), Unit.PX);
	}

	// --------------------------------------------------------------------------------------------
	// 										INIT CONTRACTs TABLE
	// --------------------------------------------------------------------------------------------

	private void initContractTable() {		
	    
	    // Add the data to the data provider, which automatically pushes it to the
	    // widget.
	    List<ITEmployee> employeeContractInfoList = dataProvider.getList();
	    employeeContractInfoList.clear();
	    
	    this.employeesList = mainContrataITObject.getEmployeesList();
	    
	    for (ITEmployee employeeContractInfo : this.employeesList) {
	    	employeeContractInfoList.add(employeeContractInfo);
	    } 
	    
	    addSortColums(employeeContractInfoList); 

	    // Set page size
	    employeeDataGrid.setPageSize(employeesList.size());
	    employeeDataGrid.setVisibleRange(0, employeesList.size());
	    
		
	    dataProvider.refresh();
	    employeeDataGrid.redraw();
	    
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
	// 										INIT ITs TABLE
	// --------------------------------------------------------------------------------------------

	private void initITTable() {		
		// Create a data provider.
	    ListDataProvider<IT> dataProvider = new ListDataProvider<IT>();

	    // Connect the table to the data provider.
	    dataProvider.addDataDisplay(itDataGrid);
	    
	    // Add the data to the data provider, which automatically pushes it to the
	    // widget.
	    List<IT> itInfoList = dataProvider.getList();
	    itInfoList.clear();
	    
	    this.itsList = mainContrataITObject.getITsList();
	    
	    for (IT itInfo : this.itsList) {
	    	itInfoList.add(itInfo);
	    } 
	    
	    // Set page size
	    employeeDataGrid.setPageSize(employeesList.size());
	    
	    // Add style to table header
	    addStyleToHeader();
	    
	    addSortITColums(itInfoList); 
		
	}
	
	private void addSortITColums(List<IT> itInfoList) {
		ListHandler<IT> columnSortHandler = new ListHandler<IT>(itInfoList);
		
	    columnSortHandler.setComparator(itDataGrid.getColumn(0), new Comparator<IT>() {
	          public int compare(IT o1, IT o2) {
		            if (o1 == o2) {
		              return 0;
		            }
	
		            if (o1 != null) {
		              return (o2 != null) ? o1.getFullName().compareTo(o2.getFullName()) : 1;
		            }
		            
		            return -1;
	          }
	    });
	    
	    columnSortHandler.setComparator(itDataGrid.getColumn(1), new Comparator<IT>() {
	          public int compare(IT o1, IT o2) {
		            if (o1 == o2) {
		              return 0;
		            }
	
		            if (o1 != null) {
		              return (o2 != null) ? o1.getStartDate().compareTo(o2.getStartDate()) : 1;
		            }
		            
		            return -1;
	          }
	    });
		
	    columnSortHandler.setComparator(itDataGrid.getColumn(2), new Comparator<IT>() {
	          public int compare(IT o1, IT o2) {
		            if (o1 == o2) {
		              return 0;
		            }
	
		            if (o1 != null) {
		              return (o2 != null) ? o1.getTypeLowPart().compareTo(o2.getTypeLowPart()) : 1;
		            }
		            
		            return -1;
	          }
	    });
	    
	    columnSortHandler.setComparator(itDataGrid.getColumn(3), new Comparator<IT>() {
	          public int compare(IT o1, IT o2) {
		            if (o1 == o2) {
		              return 0;
		            }
	
		            if (o1 != null) {
		              return (o2 != null) ? o1.getEndDate().compareTo(o2.getEndDate()) : 1;
		            }
		            
		            return -1;
	          }
	    });
	    
	    columnSortHandler.setComparator(itDataGrid.getColumn(4), new Comparator<IT>() {
	          public int compare(IT o1, IT o2) {
		            if (o1 == o2) {
		              return 0;
		            }
	
		            if (o1 != null) {
		              return (o2 != null) ? o1.getTypeHighPart().compareTo(o2.getTypeHighPart()) : 1;
		            }
		            
		            return -1;
	          }
	    });
	    
	    
	    columnSortHandler.setComparator(itDataGrid.getColumn(5), new Comparator<IT>() {
	          public int compare(IT o1, IT o2) {
		            if (o1 == o2) {
		              return 0;
		            }
	
		            if (o1 != null) {
		              return (o2 != null) ? o1.getParent().compareTo(o2.getParent()) : 1;
		            }
		            
		            return -1;
	          }
	    });
	    
	    
	    itDataGrid.addColumnSortHandler(columnSortHandler);

	    // We know that the data is sorted alphabetically by default.
	    itDataGrid.getColumn(1).setDefaultSortAscending(false);
	    itDataGrid.getColumnSortList().push(itDataGrid.getColumn(1));    
	}

	
	// --------------------------------------------------------------------------------------------
	// 										UI HANDLERS
	// --------------------------------------------------------------------------------------------
	
	@UiHandler("inactiveContractsCB")
	public void onInactiveContractsCBValueChange(ValueChangeEvent<Boolean> event) {
		this.mainContrataITObject.getEmployeesInfo(event.getValue(),
				s -> {
					initEnterpriseSB();
					initITSB();
					initContractTable();
					setTableHeights();
				},
				f -> {}
		);
	}
	
	@UiHandler("inactiveITsCB")
	public void onInactiveITsCBValueChange(ValueChangeEvent<Boolean> event) {
		mainContrataITObject.setITsList(!event.getValue());
		initEnterpriseSB();
		initITSB();
		initITTable();
		setTableHeights();
	}
	
	@UiHandler("backContractButton")
	public void onBackContractButtonClick(ClickEvent event) {
		deckPanel.showWidget(0);
		backContractButton.getElement().getStyle().setDisplay(Display.NONE);
		listITsButton.getElement().getStyle().clearDisplay();
		initContractTable();
	}
	
	@UiHandler("listITsButton")
	public void onListITsButtonClick(ClickEvent event) {
		deckPanel.showWidget(1);
		inactiveITsCB.setValue(true);
		listITsButton.getElement().getStyle().setDisplay(Display.NONE);
		backContractButton.getElement().getStyle().clearDisplay();
		initITTable();
	}
	
	@UiHandler("msjFIEButton")
	public void onMsjFIEButtonClick(ClickEvent event) {
		msjFIEFileUpload.click();
	}
	
	@UiHandler("msjFIEFileUpload")
	public void  onMsjFIEFileUploadChange(ChangeEvent e) {
		msjFIEFormPanel.submit();
	}
	
	@UiHandler("msjFIEFormPanel") 
	public void onMsjFIEFormPanelSubmitComplete(SubmitCompleteEvent e) {
		String json = e.getResults();
		
		JsArray<JsITEmployee> jsITEmployees = eval("(" + json + ")");
		
		List<ITEmployee> itEmployees = new ArrayList<ITEmployee>(jsITEmployees.length());
		
		for (int i = 0; i < jsITEmployees.length(); i++ ) {
			JsITEmployee jsITEmployee = jsITEmployees.get(i);			
			ITEmployee itEmployee = fromJsITEmployee(jsITEmployee);
			itEmployees.add(itEmployee); 		
		}
				
		mainContrataITObject.setEmployeesInfo(itEmployees,
			s -> {
				initEnterpriseSB();
				initITSB();
				initContractTable();
				setTableHeights();
			},
			f -> {}
		);
		
	}
	// --------------------------------------------------------------------------------------------
	// 										AUXILIAR METHODS
	// --------------------------------------------------------------------------------------------
	
	private IT checkIfIsOpenIt(ITEmployee itEmployee) {
  		for(IT it : itEmployee.getIts()) {
  			if(null == it.getEndDate())
  				return it;
  		}
  		return null;
  	  }
      
	private String parseLowCauseByte(Byte typeLowPart) {
		switch (typeLowPart) {
			case (byte)0:
				return "Enfermedad Com" + String.valueOf("\u00FA") + "n";
			case (byte)1:
				return "Accidente de trabajo";
			case (byte)2:
				return "Maternidad";
			case (byte)3:
				return "Paternidad";
			case (byte)4:
				return "Riesgo para el embarazo";
			case (byte)5:
				return "Riesgo durante la lactancia";
			case (byte)6:
				return "Accidente no laboral";
			case (byte)7:
				return "Enfermedad com" + String.valueOf("\u00FA") + "n periodo de carencia";
			case (byte)8:
				return "Enfermedad com" + String.valueOf("\u00FA") + "n, prestaci" + String.valueOf("\u00F3") + "n profesional (COVID-19)";
			default:
				return "-";
		}
	}
	
	private String parseShortLowCauseByte(Byte typeLowPart) {
		switch (typeLowPart) {
			case (byte)0:
				return "ECC";
			case (byte)1:
				return "ATT";
			case (byte)2:
				return "MAT";
			case (byte)3:
				return "PAT";
			case (byte)4:
				return "REM";
			case (byte)5:
				return "RLA";
			case (byte)6:
				return "ANL";
			case (byte)7:
				return "ECC";
			case (byte)8:
				return "COV";
			default:
				return "-";
		}
	}
	
	private String parseHighCauseByte(Byte typeHighPart) {
		if(null == typeHighPart)
			return "-";
		
		switch (typeHighPart) {
			case (byte)0:
				return "Curaci" + String.valueOf("\u00F3") + "n";
			case (byte)1:
				return "Fallecimiento";
			case (byte)2:
				return "Inspecci" + String.valueOf("\u00F3") + "n m" + String.valueOf("\u00E9") + "dica";
			case (byte)3:
				return "Propuesta incapacidad";
			case (byte)4:
				return "Agotamiento de plazo";
			case (byte)5:
				return "Mejor" + String.valueOf("\u00ED") + "a que permite realizar el trabajo habitual";
			case (byte)6:
				return "Incomparecencia";
			case (byte)7:
				return "Control INSS duraci" + String.valueOf("\u00F3") + "n 12 meses";
			case (byte)8:
				return "Recuperaci" + String.valueOf("\u00F3") + "n capacidad profesional";
			case (byte)9:
				return "Incomparecencia contratos de formaci" + String.valueOf("\u00F3") + "n";
			default:
				return "-";
		}
	}
	
	private static ITEmployee fromJsITEmployee(JsITEmployee jsITEmployee) {
		ITEmployee itEmployee = new ITEmployee();
		
		JsEmployeeInfo jsEmployeeInfo = jsITEmployee.getEmployeeInfo();
		EmployeeInfo employeeInfo = fromJsEmployeeInfo(jsEmployeeInfo);
		
		JsContractInfo jsContractInfo = jsITEmployee.getContractInfo();
		ContractInfo contractInfo = fromJsContractInfo(jsContractInfo);
		
		JsArray<JsIT> jsITs = jsITEmployee.getITs();
		List<IT> its = new ArrayList<IT>();
		for ( int i = 0; i < jsITs.length(); i++ ) {
			its.add(fromJsIT(jsITs.get(i)));
		}

		itEmployee.setEmployeeInfo(employeeInfo);
		itEmployee.setContractInfo(contractInfo);
		itEmployee.setIts(its);
		
		itEmployee.setStatus(jsITEmployee.getStatus());

		return itEmployee;
	}
	
	
	
	private static Date parseDate(String str) {
		if ( str == null )
			return null;
		if (str.trim().length() == 0 )
			return null;
		try {
			return DateTimeFormat.getFormat("yyyy-MM-dd").parse(str);
		} catch ( IllegalArgumentException e ) {
			return null;
		}
	}

	private static IT fromJsIT(JsIT jsIT) {
		
		IT it = new IT();
		it.setContract(jsIT.getContract());
		it.setDailyCGCBase(jsIT.getDailyCGCBase());
		it.setDailyCGPBase(jsIT.getDailyCGPBase());
		it.setDailyREGBase(jsIT.getDailyREGBase());
		it.setDescription(jsIT.getDescription());
		it.setDomain(jsIT.getDomain());
		it.setEndDate(parseDate(jsIT.getEndDate()));
		it.setFullName(jsIT.getFullName());
		it.setMaternityReason(jsIT.getMaternityReason());
		it.setMaternityType(jsIT.getMaternityType());
		it.setId(jsIT.getId());
		it.setIsParent(jsIT.isParent());
		//it.setITParts(jsIT.getITParts());
		it.setParent(jsIT.getParent());
		it.setStartDate(parseDate(jsIT.getStartDate()));
		it.setTypeHighPart(jsIT.getTypeHighPart());
		it.setTypeLowPart(jsIT.getTypeLowPart());
		return it;
	}
	
	private static ContractInfo fromJsContractInfo(JsContractInfo jsContractInfo) {
		ContractInfo contractInfo = new ContractInfo();	
		contractInfo.setActivityId(jsContractInfo.getActivityId());
		contractInfo.setEnterpriseCIF(jsContractInfo.getEnterpriseCIF());
		contractInfo.setCccId(jsContractInfo.getCccId());
		contractInfo.setCompleteCCC(jsContractInfo.getCompleteCCC());
		contractInfo.setCccType(jsContractInfo.getCccType());
		contractInfo.setWorkplaceId(jsContractInfo.getWorkplaceId());
		contractInfo.setWorkplaceZIP(jsContractInfo.getWorkplaceZIP());
		contractInfo.setWorkplaceFullAddress(jsContractInfo.getWorkplaceFullAddress());
		contractInfo.setContractType(jsContractInfo.getContractType());
		contractInfo.setContractModel(jsContractInfo.getContractModel());
		contractInfo.setStartDate(parseDate(jsContractInfo.getStartDate()));
		contractInfo.setEndDate(parseDate(jsContractInfo.getEndDate()));
		contractInfo.setSeniorityDate(parseDate(jsContractInfo.getSeniorityDate()));
		contractInfo.setAgreementId(jsContractInfo.getAgreementId());
		contractInfo.setAgreementLevelId(jsContractInfo.getAgreementLevelId());
		contractInfo.setAgreementCategory(jsContractInfo.getAgreementCategory());
		contractInfo.setQuoteGroup(jsContractInfo.getQuoteGroup());
		contractInfo.setOcupation(jsContractInfo.getOcupation());
		contractInfo.setJourneyType(jsContractInfo.getJourneyType());
		contractInfo.setSsRegimen(jsContractInfo.getSsRegimen());
		contractInfo.setContractId(jsContractInfo.getContractId());
		contractInfo.setContracttypeId(jsContractInfo.getContracttypeId());
		contractInfo.setQuotegroupId(jsContractInfo.getQuotegroupId());
		contractInfo.setOcupationId(jsContractInfo.getOcupationId());
		contractInfo.setJourneytypeId(jsContractInfo.getJourneytypeId());
		contractInfo.setContractmodelId(jsContractInfo.getContractmodelId());
		contractInfo.setRetaId(jsContractInfo.getRetaId());
//		contractInfo.setContractJourneyDuration(jsContractInfo.getContractJourneyDuration());
		contractInfo.setOldStartDate(parseDate(jsContractInfo.getOldStartDate()));
		contractInfo.setOldEndDate(parseDate(jsContractInfo.getOldEndDate()));
		contractInfo.setHasPayroll(jsContractInfo.getHasPayroll());
		contractInfo.setPayrollDate(parseDate(jsContractInfo.getPayrollDate()));
		return contractInfo;
	}

	private static EmployeeInfo fromJsEmployeeInfo(JsEmployeeInfo jsEmployeeInfo) {		
		EmployeeInfo employeeInfo = new EmployeeInfo();
		
		employeeInfo.setAccount(jsEmployeeInfo.getAccount());
		employeeInfo.setAddresNum(jsEmployeeInfo.getAddresNum());
		employeeInfo.setAddress(jsEmployeeInfo.getAddress());
		employeeInfo.setAddressCity(jsEmployeeInfo.getAddressCity());
		employeeInfo.setAddressInfo(jsEmployeeInfo.getAddressInfo());
		employeeInfo.setAddressProvinces(jsEmployeeInfo.getAddressProvinces());
		employeeInfo.setAddressZip(jsEmployeeInfo.getAddressZip());
		employeeInfo.setBic(jsEmployeeInfo.getBic());
		employeeInfo.setBirthdate(parseDate(jsEmployeeInfo.getBirthdate()));
		employeeInfo.setCivilStatus(jsEmployeeInfo.getCivilStatus());
		employeeInfo.setContractActive(jsEmployeeInfo.getContractActive());
		employeeInfo.setContractId(jsEmployeeInfo.getContractId());
		employeeInfo.setDocument(jsEmployeeInfo.getDocument());
		employeeInfo.setDocumentType(jsEmployeeInfo.getDocumentType());
		employeeInfo.setDomain(jsEmployeeInfo.getDomain());
		employeeInfo.setEmail(jsEmployeeInfo.getEmail());
		employeeInfo.setEmailId(jsEmployeeInfo.getEmailId());
		employeeInfo.setEmployeeId(jsEmployeeInfo.getEmployeeId());
		employeeInfo.setGender(jsEmployeeInfo.getGender());
		employeeInfo.setGeozoneId(jsEmployeeInfo.getGeozoneId());
		employeeInfo.setIsFullTime(jsEmployeeInfo.getIsFullTime());
		employeeInfo.setMobile(jsEmployeeInfo.getMobile());
		employeeInfo.setMobileId(jsEmployeeInfo.getMobileId());
		employeeInfo.setName(jsEmployeeInfo.getName());
		employeeInfo.setNationality(jsEmployeeInfo.getNationality());
		employeeInfo.setPaymethodId(jsEmployeeInfo.getPaymethodId());
		employeeInfo.setPayMethodType(jsEmployeeInfo.getPayMethodType());
		employeeInfo.setPayMethodTypeB(jsEmployeeInfo.getPayMethodTypeB());
		employeeInfo.setPhone(jsEmployeeInfo.getPhone());
		employeeInfo.setPhoneId(jsEmployeeInfo.getPhoneId());
		employeeInfo.setRaddressId(jsEmployeeInfo.getRaddressId());
		employeeInfo.setRbankId(jsEmployeeInfo.getRbankId());
		employeeInfo.setRpaymethodId(jsEmployeeInfo.getRpaymethodId());
		employeeInfo.setSecondSurName(jsEmployeeInfo.getSecondSurName());
		employeeInfo.setSsNumber(jsEmployeeInfo.getSsNumber());
		employeeInfo.setStreetType(jsEmployeeInfo.getStreetType());
		employeeInfo.setSurName(jsEmployeeInfo.getSurName());
		
		return employeeInfo;
	}

	private static native <T extends JavaScriptObject> T eval(String javascript)
	/*-{
		return eval(javascript);
	}-*/;


}
