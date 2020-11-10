package com.esferalia.aon.gwt.payroll.client;

import static com.esferalia.aon.gwt.payroll.shared.EnterpriseStatus.ifSistemaREDEnabled;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.ResultsPanel;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseStatus;
import com.esferalia.aon.gwt.payroll.shared.SistemaREDService;
import com.esferalia.aon.gwt.payroll.shared.SistemaREDService.JsSistemaREDResults;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
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
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;

import net.aonsolutions.gwt.pdfjs.client.Viewer;

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
	DockLayoutPanel splitLayoutPanel;
	
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
	
	@UiField
	MinimizePanel footPanel;
	
	@UiField
	TabLayoutPanel footTabPanel;
	
	@UiField
	PDFViewer pdfViewer;
	
	@UiField
	Panel sistemaREDPanel;
	
	
	ResultsPanel resultsPanel;
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
		RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel").add(ui);
		
		// Show table
		deckPanel.showWidget(0);
		
		initFootPanel();
		initResultsPanel();
		initPDFViewer();
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
		
		checkStatus(this.mainContrataContractObject);
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
	
	@UiHandler("up2DateSSButton")
	void onClickUp2DateSSButton(ClickEvent e) {
		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.open("POST", SistemaREDService.SISTEMA_RED_URL+ "/" + SistemaREDService.UP2DATE_REPORT);
		xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		xhr.setOnReadyStateChange(new ReadyStateChangeHandler() {
			@Override
			public void onReadyStateChange(XMLHttpRequest xhr) {
				int state = xhr.getReadyState();
				if (state != XMLHttpRequest.DONE)
					return;
				try {
					String dataURI = xhr.getResponseText();
					showPFDF(dataURI);
					AON.stop();
				} catch ( Throwable t ) {
					AON.fail();
				}
			}
		});

		StringBuffer requestDataBuffer = new StringBuffer();

		requestDataBuffer
		.append(SistemaREDService.Parameter.DOMAIN.name() + "=" + Wnd.getCurrentDomainNameURL())
		.append("&" +SistemaREDService.Parameter.USER.name() + "=" + Wnd.getCurrentUser() )
		;
		
		xhr.send(requestDataBuffer.toString());
		AON.start();
		
	}
	
	protected void showPFDF(String dataURI) {
		pdfViewer.setTitle("CERTI. ESTAR AL CORRIENTE EN OBLIGAC. DE S.S.");
		pdfViewer.setDocument(dataURI, Constants.DEFAULT_ZOOM / 100.00 );
		deckPanel.showWidget(2);
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
	
	private void initFootPanel() {
		footPanel.addMaximizeHandler((e) -> {
			splitLayoutPanel.setWidgetSize(footPanel, 150);
		});
		
		footPanel.addMinimizeHandler((e) -> {
			splitLayoutPanel.setWidgetSize(footPanel, 25);
		});
		
	}
	
	private void initResultsPanel () {
		resultsPanel = new ResultsPanel();		
	}
	
	private void initPDFViewer () {
		Button closeButton = new Button("Cerrar");
		closeButton.setStylePrimaryName(AON.AON_ICON_CANCEL);
		closeButton.addClickHandler( e -> deckPanel.showWidget(0));
		pdfViewer.addCustomToolBarButton(closeButton);
	}

	private void checkStatus(MainContrataContractObject mainContrataContractObject) {
		mainContrataContractObject.checkStatus(enterpriseStatus -> {
			SistemaREDResults sistemaREDResults = new SistemaREDResults() {
				
				@Override
				public void up2Date() {
				}

				@Override
				public void run() {
					mainContrataContractObject.checkStatus(enterpriseStatus -> {
						removeAll();
						enterpriseStatus.visit(this);
					}, throwable -> {
					});
				}
				
				@Override
				protected void newAffiliated(JsSistemaREDResults jsSaltraResults) {
					MainContrataContract.this.mainContrataContractObject.getEmployeesInfo(false,
							s -> {
								MainContrataContract.this.initEnterpriseSB();
								MainContrataContract.this.initContractTable();
								MainContrataContract.this.setTableHeights();
							},
							f -> {}
					);	
					run();
				}
				
				@Override
				protected void newAffiliated(JsArray<JsSistemaREDResults> jsSaltraResults ) {
					MainContrataContract.this.mainContrataContractObject.getEmployeesInfo(false,
							s -> {
								MainContrataContract.this.initEnterpriseSB();
								MainContrataContract.this.initContractTable();
								MainContrataContract.this.setTableHeights();
							},
							f -> {}
					);	
					run();
				}
				

				@Override
				protected void saltraCredentialsFound() {
					mainContrataContractObject.checkStatus(enterpriseStatus -> {
						removeAll();
						enterpriseStatus.visit(this);
						selectResultsPanel();
						EnterpriseStatus.ifSistemaREDEnabled(enterpriseStatus, () -> {
							showFootPanel();
							MainContrataContract.this.setSistemaREDVisible(true);
							//ContrataEmployee.this.setOnSaved(e -> run());
						}, () -> {
							closeFootPanel();
							MainContrataContract.this.setSistemaREDVisible(false);

						});
					}, throwable -> {
						closeFootPanel();
						MainContrataContract.this.setSistemaREDVisible(false);

					});
				}
			};

			enterpriseStatus.visit(sistemaREDResults);
			resultsPanel.setWidget(sistemaREDResults);
			selectResultsPanel();

			ifSistemaREDEnabled(enterpriseStatus, () -> {
				showFootPanel();
				MainContrataContract.this.setSistemaREDVisible(true);
				//ContrataEmployee.this.setOnSaved(e -> sistemaREDResults.run());
			}, () -> {
				closeFootPanel();
				MainContrataContract.this.setSistemaREDVisible(false);
			});

		}, throwable -> {
			closeFootPanel();
			MainContrataContract.this.setSistemaREDVisible(false);
		});
	}
	
	private void closeFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, 0);
	}

	private void maximizeFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, 0);
	}

	private void showFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 4);
	}
	
	private void selectResultsPanel() {

		InlineLabel tab = new InlineLabel("Resultados");
		tab.addStyleName(AON.AON_ICON_TIME);
		tab.addStyleName(AON.AON_ICON_CMD_BUTTON);
		footTabPanel.add(resultsPanel, tab);
		footTabPanel.selectTab(resultsPanel);

	}

	private void setSistemaREDVisible( boolean visible ){
		sistemaREDPanel.setVisible(visible);
	}

}
