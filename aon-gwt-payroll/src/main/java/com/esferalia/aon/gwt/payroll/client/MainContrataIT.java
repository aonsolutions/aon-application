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
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.payroll.shared.ContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.FIEService;
import com.esferalia.aon.gwt.payroll.shared.FIEService.JsContractInfo;
import com.esferalia.aon.gwt.payroll.shared.FIEService.JsEmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.FIEService.JsIT;
import com.esferalia.aon.gwt.payroll.shared.FIEService.JsITEmployee;
import com.esferalia.aon.gwt.payroll.shared.IT;
import com.esferalia.aon.gwt.payroll.shared.ITEmployee;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
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
		String filterPanel();
		String flexPanel();
	}
	
	@UiField
	DockLayoutPanel dockLayoutPanel;
	
	@UiField
	HTMLPanel filterITListPanel;
	
	@UiField
	HTMLPanel mainITTablePanel;
	
	@UiField
	HTMLPanel mainITContainer;
	
	@UiField(provided = true)
	DataGrid<IT> itDataGrid;
	
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
	
	private AonToolbar toolbar;
	private AonToolbarButton addIT;
	
	private AonToolbarButton msjFIE;
	private FormPanel msjFIEFormPanel;
	private Hidden userNameHidden;
	private Hidden domainNameHidden;
	private MultiFileUpload msjFIEFileUpload;
	
	private SuggestBox itSB;
	private CheckBox inactiveITsCB;
	
	public MainContrataIT() {		
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
		AON.ensureInjected();

		provideITsDataGrid();

		// Add style to table header
	    addStyleToITHeader();
	
		Widget ui = binder.createAndBindUi(this);
		RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel").add(ui);
		
		toolbar = getToolbarPanel();
		dockLayoutPanel.addNorth( toolbar , AonToolbar.HEIGTH );
		
		getFilterITListPanel();
		
		inactiveITsCB.setValue(true);		
	}
	
	// --------------------------------------------------------------------------------------------
	// 									PROVIDE IT DATA GRID
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
	        	ITEmployee itEmployee = mainContrataITObject.getEmployeeITInfo(itInfo.getId());
	  
	        	String suggestionStr = itEmployee.getEmployeeInfo().getFullName() + " (" + itEmployee.getContractInfo().getContractId() + ")";
	        	
	        	ITDialog itDialog = new ITDialog(suggestionStr, false) {
	        		
	        		@Override
					protected void onAccept() {
						mainContrataITObject.createUpdateITEmployee(itEmployee,
								s -> {
									AonConfirmDialog dialog = new AonConfirmDialog();
									dialog.info("AVISO: Creaci" + String.valueOf("\u00F3") + "n", s);
									
									refreshITTable();
								},
								f -> {});
					}
	        		
	        		@Override
					protected void onAcceptIT(IT it) {}

					@Override
					protected void onAcceptPaternityIT(IT it) {}
					
					@Override
					protected void onDelete(IT it) {
						mainContrataITObject.removeIT(itEmployee, it,
								s -> {
									AonConfirmDialog dialog = new AonConfirmDialog();
									dialog.info("AVISO: Borrado", "El parte IT ha sido borrado correctamente.");
									
									if(it.isComunicate() && mainContrataITObject.isUserComunica())
										mainContrataITObject.deleteComunicateIT(itEmployee, it, t -> {
											refreshITTable();
										}, d -> {});
									else
										refreshITTable();
								},
								f -> {});
					}

					@Override
					protected void onDeletePaternity(IT it) {
						mainContrataITObject.deleteIT(it,
								s -> {
									AonConfirmDialog dialog = new AonConfirmDialog();
									dialog.info("AVISO: Borrado", "El parte IT ha sido borrado correctamente.");
									
									if(it.isComunicate() && mainContrataITObject.isUserComunica())
										mainContrataITObject.deleteComunicateIT(itEmployee, it, t -> {
											refreshITTable();
										}, d -> {});
									else
										refreshITTable();
								},
								f -> {});
					}
					
					@Override
					protected void onShowCertitificateIT(IT it) {
						getITCertificatePDF(itEmployee, it);
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
	
	public void addStyleToITHeader() {
		itDataGrid.getHeader(0).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		itDataGrid.getHeader(1).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		itDataGrid.getHeader(2).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		itDataGrid.getHeader(3).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		itDataGrid.getHeader(4).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		itDataGrid.getHeader(5).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
	}
	
	private void setTableHeights() {
		itDataGrid.getElement().getStyle().setHeight(100, Unit.PCT);
		mainITTablePanel.getElement().getStyle().setHeight((Window.getClientHeight() - 255), Unit.PX);
	}
	
	// --------------------------------------------------------------------------------------------
	// 										ON MODULE LOAD
	// --------------------------------------------------------------------------------------------
	
	public void onModuleLoad(MainContrataITObject mainContrataContractObject) {
		this.mainContrataITObject = mainContrataContractObject;
		
		this.mainContrataITObject.getEmployeesInfo(false,
				s -> {
					initITSB();
					initITTable();
					setTableHeights();
				},
				f -> {}
		);
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
			if(AonStringUtils.isBlank(value) || value.length() < 3) {
				mainContrataITObject.resetITsList();
			} else {
				List<Integer> itIds = mainContrataITObject.getITsContractIds(value);
				mainContrataITObject.filterITsList(itIds);
			}
			
			mainContrataITObject.setITsList(!inactiveITsCB.getValue());
			initITTable();
		});
		
		itSB.addSelectionHandler(e -> {
			String value = itSB.getValue();
			List<Integer> itIds = mainContrataITObject.getITsContractIds(value);
			mainContrataITObject.filterITsList(itIds);
			
			initITTable();
			itDataGrid.redraw();
		});
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
	    itDataGrid.setPageSize(itsList.size());
	    
	    // Add style to table header
	    addStyleToITHeader();
	    
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
	
	private void refreshITTable() {
		boolean intactivesIT = this.inactiveITsCB.getValue();
		mainContrataITObject.getEmployeesInfo(false,
				t -> {
					mainContrataITObject.setITsList(!intactivesIT);
					initITTable();
					setTableHeights();
				},
				d -> {}
		);
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

	private AonToolbar getToolbarPanel() {
		AonToolbar toolbar = new AonToolbar("Partes IT");
		
		// FORM
		msjFIEFormPanel = new FormPanel();
		msjFIEFormPanel.setMethod(FormPanel.METHOD_POST);
		msjFIEFormPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
		msjFIEFormPanel.setAction(FIEService.FIE_URL);
		
		userNameHidden = new Hidden(FIEService.Parameter.USER.name(), Wnd.getCurrentUser());
		domainNameHidden = new Hidden(FIEService.Parameter.DOMAIN.name(), Wnd.getCurrentDomainNameURL());
		
		msjFIEFileUpload = new MultiFileUpload();
		msjFIEFileUpload.setName(FIEService.Parameter.FILE.name());
		msjFIEFileUpload.setVisible(false);
		msjFIEFileUpload.setAccept(".msj");
		msjFIEFileUpload.addChangeHandler(e -> {
			msjFIEFormPanel.submit();
		});
		msjFIEFormPanel.addSubmitCompleteHandler(e -> {
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
					initITSB();
					initITTable();
					setTableHeights();
				},
				f -> {}
			);
		});
		
		FlowPanel formFlowPanel = new FlowPanel();
		formFlowPanel.add(userNameHidden);
		formFlowPanel.add(domainNameHidden);
		formFlowPanel.add(msjFIEFileUpload);
		
		msjFIEFormPanel.add(formFlowPanel);
		toolbar.add(msjFIEFormPanel);
		
		addIT = new AonToolbarButton( "Nueva IT", AON.CSS.aonIconAdd() );
		addIT.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onAddIT(event);
			}
		});
		toolbar.add(addIT);
		
		msjFIE = new AonToolbarButton( "Mensaje del INSS Empresa (FIE)", AON.CSS.aonIconTgssFie() );
		msjFIE.setAccessKey('F');
		msjFIE.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onFIE(event);
			}
		});
		toolbar.add(msjFIE);

		return toolbar;

	}
	
	private void onAddIT(ClickEvent event) {
		ITDialog itDialog = new ITDialog(null, true) {

    		@Override
			protected void onAccept() {
				mainContrataITObject.createUpdateITEmployee(getITEmployee(),
						s -> {
							AonConfirmDialog dialog = new AonConfirmDialog();
							dialog.info("AVISO: Creaci" + String.valueOf("\u00F3") + "n", s);
							
							refreshContractITTable();
						},
						f -> {});
			}

			@Override
			protected void onAcceptIT(IT it) {
				mainContrataITObject.createUpdateITEmployee(getITEmployee(),
						s -> {
							AonConfirmDialog dialog = new AonConfirmDialog();
							dialog.info("AVISO: Creaci" + String.valueOf("\u00F3") + "n", s);
							
							if(mainContrataITObject.isUserComunica()) {
								AonConfirmDialog comunicateDialog = new AonConfirmDialog();
								comunicateDialog.confirm(
										"COMUNIC" + String.valueOf("\u0040"), 
										String.valueOf("\u00BF") + "Desea comunicar el parte IT?",
										new AonConfirmDialogCallback() {
					
											@Override
											public void onAccept() {
												mainContrataITObject.comunicateITBaja(getITEmployee(), it, t -> {
													AonConfirmDialog dialog = new AonConfirmDialog();
													dialog.info("AVISO: COMUNICA", "El parte IT ha sido comunicado correctamente");
													
													getITCertificatePDF(getITEmployee(), it);
													
													refreshContractITTable();
													
												}, d -> {});
											}

											@Override
											public void onCancel() {
												refreshContractITTable();
											}});
							} else {
								refreshContractITTable();
							}},
							f -> {});
							
			}
    		
    		@Override
			protected void onAcceptPaternityIT(IT it) {
				mainContrataITObject.createUpdateITEmployee(getITEmployee(),
						s -> {
							AonConfirmDialog dialog = new AonConfirmDialog();
							dialog.info("AVISO: Creaci" + String.valueOf("\u00F3") + "n", s);
							
							if(mainContrataITObject.isUserComunica()) {
								AonConfirmDialog comunicateDialog = new AonConfirmDialog();
								comunicateDialog.confirm(
										"COMUNIC" + String.valueOf("\u0040"), 
										String.valueOf("\u00BF") + "Desea comunicar el parte IT?",
										new AonConfirmDialogCallback() {
					
											@Override
											public void onAccept() {
												mainContrataITObject.comunicatePaternityIT(getITEmployee(), it, t -> {
													AonConfirmDialog dialog = new AonConfirmDialog();
													dialog.info("AVISO: COMUNICA", "El parte IT ha sido comunicado correctamente");
													
													getITCertificatePDF(getITEmployee(), it);
													
													refreshContractITTable();
												}, d -> {});
											}
					
											@Override
											public void onCancel() {
												refreshContractITTable();
											}});
							} else {
								refreshContractITTable();
							}},
						f -> {});
			}
    		
    		@Override
			protected void onDelete(IT it) {
				mainContrataITObject.removeIT(getITEmployee(), it,
						s -> {
							AonConfirmDialog dialog = new AonConfirmDialog();
							dialog.info("AVISO: Borrado", "El parte IT ha sido borrado correctamente.");
							
							if(it.isComunicate() && mainContrataITObject.isUserComunica())
								mainContrataITObject.deleteComunicateIT(getITEmployee(), it, t -> {
									refreshContractITTable();
								}, d -> {});
							else
								refreshContractITTable();
						},
						f -> {});
			}
    		
			@Override
			protected void onDeletePaternity(IT it) {
				mainContrataITObject.deleteIT(it,
						s -> {
							AonConfirmDialog dialog = new AonConfirmDialog();
							dialog.info("AVISO: Borrado", "El parte IT ha sido borrado correctamente.");
							
							if(it.isComunicate() && mainContrataITObject.isUserComunica())
								mainContrataITObject.deleteComunicateIT(getITEmployee(), it, t -> {
									refreshContractITTable();
								}, d -> {});
							else
								refreshContractITTable();
						},
						f -> {});
			}
			
			@Override
			protected void onShowCertitificateIT(IT it) {
				getITCertificatePDF(getITEmployee(), it);
			}
    		
    	};
    	
    	itDialog.setEmployeesList(mainContrataITObject.getEmployeesList());
		itDialog.setModal(true);
    	itDialog.setAnimationEnabled(true);
		itDialog.center();
		itDialog.show();
	}
	
	private void refreshContractITTable() {
		mainContrataITObject.getEmployeesInfo(false,
				t -> {
					initITTable();
					setTableHeights();
				},
				d -> {}
		);
	}
	
	private void onFIE(ClickEvent event) {
		msjFIEFileUpload.click();
	}
	
	private void getFilterITListPanel() {
		filterITListPanel.setStyleName(AON.CSS.aonSearchPanel());
		filterITListPanel.addStyleName(AON.CSS.aonScrollArea());
		filterITListPanel.addStyleName(AON.CSS.aonMarginBottom());
		filterITListPanel.addStyleName(AON.CSS.aonMarginLeft());
		filterITListPanel.addStyleName(AON.CSS.aonMarginRight());
		filterITListPanel.addStyleName(AON.CSS.aonBlockCenter());
		
		HTMLPanel filterPanel = new HTMLPanel("");
		filterPanel.addStyleName(style.filterPanel());
		
		HTMLPanel itPanel = new HTMLPanel("");
		itPanel.addStyleName(style.flexPanel());
		Label itL = new Label("Trabajador : ");
		itL.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		itL.getElement().getStyle().setMarginRight(10, Unit.PX);
		itSB = new SuggestBox();
		itSB.getElement().getStyle().setWidth(300, Unit.PX);
		itPanel.add(itL);
		itPanel.add(itSB);
		
		HTMLPanel showPanel = new HTMLPanel("");
		showPanel.addStyleName(style.flexPanel());
		Label showL = new Label("Mostrar ITs : ");
		showL.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		showL.getElement().getStyle().setMarginRight(5, Unit.PX);
		inactiveITsCB = new CheckBox();
		inactiveITsCB.addValueChangeHandler(e -> {
			mainContrataITObject.setITsList(!e.getValue());
			initITSB();
			initITTable();
			setTableHeights();
		});
		Label inactiveL = new Label("En vigor");
		inactiveL.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		inactiveL.getElement().getStyle().setMarginLeft(5, Unit.PX);
		showPanel.add(showL);
		showPanel.add(inactiveITsCB);
		showPanel.add(inactiveL);
		
		filterPanel.add(itPanel);
		filterPanel.add(showPanel);
		
		filterITListPanel.add(filterPanel);
	}

	private void getITCertificatePDF(ITEmployee itEmployee, IT it) {
		mainContrataITObject.getNafxIpf(itEmployee, s -> {
			String affiliationNumber = s.getNss();
			String regime = itEmployee.getContractInfo().getCompleteCCC().substring(0, 4);
			String contributionAccount = itEmployee.getContractInfo().getCompleteCCC().substring(4, itEmployee.getContractInfo().getCompleteCCC().length());
			String dateFromStr = formatFullDate.format(new Date());
			String dateToStr = formatFullDate.format(new Date());
			String startDateStr = formatFullDate.format(it.getStartDate());
			
			String fileDownloadURL = GWT.getModuleBaseURL()+ "it_export/";
			String query = "?domainName=" + Wnd.getCurrentDomainNameURL()
			 		+ "&userLogin=" + Wnd.getCurrentUser()
		            + "&affiliationNumber=" + affiliationNumber
		            + "&regime=" + regime
		            + "&contributionAccount=" + contributionAccount
		            + "&dateFromStr=" + dateFromStr
					+ "&dateToStr=" + dateToStr
					+ "&startDateStr=" + startDateStr
					+ "&itType=" + it.getTypeLowPart();
			
			Window.open(fileDownloadURL+query, "ITExporter", "resizable=yes,scrollbars=yes,status=yes");
		}, f -> {});
		
	
	}
}
