package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.CCCInfo;
import com.esferalia.aon.gwt.payroll.shared.CRA;
import com.esferalia.aon.gwt.payroll.shared.ProvinceContract;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;

public class MainCRANew extends MainEntryPoint {

	interface Binder extends UiBinder<Widget, MainCRANew> {}
	
	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String suggestBox();
	}
	
	@UiField
	Button exportButton;
	
	@UiField
	Button listButton;
	
	@UiField
	Button newCRAButton;
	
	@UiField
	ListBox month;
	
	@UiField
	ListBox year;
	
	@UiField
	CheckBox allCCCsCB;
	
	@UiField
	CheckBox emitCCCsCB;
	
	@UiField
	CheckBox peddingCCCsCB;
	
	@UiField
	TableElement filterTable;
	
	@UiField
	ListBox geozoneList;

	@UiField
	DisclosurePanel collapsePanel;
	
	@UiField
	SuggestBox enterpriseSB;
	
	@UiField
	Label enterprisesSelected;
	
	@UiField
	ListBox typeList;
	
	@UiField
	ListBox monthTillT;
	
	@UiField
	ListBox yearTillT;
	
	@UiField
	ListBox monthTTo;
	
	@UiField
	ListBox yearTTo;
	
	@UiField
	HTMLPanel mainTablePanel;
	
	@UiField
	HTMLPanel mainContainer;
	
	@UiField
	HTMLPanel crasContainer;
	
	@UiField(provided = true)
	DataGrid<CCCInfo> cccDataGrid;
	
	@UiField
	HTMLPanel crasPanel;
	
	@UiField(provided = true)
	DataGrid<CRA> crasDataGrid;
	
	private List<CCCInfo> cccs = Collections.emptyList();
	private List<CRA> cras = Collections.emptyList();
	private Integer enterprisesSelectedCount = 0;
	
	public MainCRANew() {
		provideCCCDataGrid();
		provideCRAsDataGrid();
		
		// Add style to table header
	    addStyleToHeader();
		
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
		AON.ensureInjected();
	
		Widget ui = binder.createAndBindUi(this);
		RootLayoutPanel.get("rootPanel").add(ui);
		
		// Init view and listboxes
		initPreView();
		initListBoxes();
		
	}
	
	private void initListBoxes() {
		// Set list box for filter by dates
		month.clear();
		monthTillT.clear();
		monthTTo.clear();
		year.clear();
		yearTillT.clear();
		yearTTo.clear();
		
		String[] months = new String[]{"Enero", "Frebero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
		for(int i=0; i<months.length; i++) {
			month.addItem(months[i], i+"");
			monthTillT.addItem(months[i], i+"");
			monthTTo.addItem(months[i], i+"");
		}
		
		Integer yearInt = new Date().getYear();
		
		year.addItem((yearInt + 1900) + "", yearInt + "");
		year.addItem((yearInt + 1900 - 1) + "", (yearInt - 1) + "");
		
		yearTillT.addItem((yearInt + 1900) + "", yearInt + "");
		yearTillT.addItem((yearInt + 1900 - 1) + "", (yearInt - 1) + "");
		
		yearTTo.addItem((yearInt + 1900) + "", year + "");
		yearTTo.addItem((yearInt + 1900 - 1) + "", (yearInt - 1) + "");
		
		// Type List
		typeList.clear();
		typeList.addItem("-", "-1");
		typeList.addItem("Principal", "0");
		typeList.addItem("Formacion y aprendizaje", "1");
		typeList.addItem("Aprendizaje", "2");
		typeList.addItem("Representantes de comercio", "3");
		typeList.addItem("Asimilados R.General", "4");
		typeList.addItem("Becarios", "5");
		typeList.addItem("Emploead@s de hogar", "6");
		typeList.addItem("Trabajadores cuenta ajena agrarios", "7");
		typeList.addItem("Artistas", "8");
		typeList.getElement().getElementsByTagName("option").getItem(2).setAttribute("disabled", "disabled");
		typeList.addStyleName("aon-selectOneMenu");
		
		//Geozone
		geozoneList.clear();
		geozoneList.addItem("-", "-1");
		
		for(Entry<String, String> province : ProvinceContract.getProvinces().entrySet()) {
			geozoneList.addItem(province.getValue(), province.getKey());
		}
		
		
	}

	// --------------------------------------------------------------------------------------------
	// 									PROVIDE SALARY DATA GRID
	// --------------------------------------------------------------------------------------------

	private void provideCCCDataGrid() {
		cccs  = Collections.emptyList();
		
		// Resource Style CellTable
		cccDataGrid = new CustomDataGrid<CCCInfo>(Integer.MAX_VALUE, CCCInfo.KEY_PROVIDER);
		cccDataGrid.setWidth("100%");
		
		//Do not refresh the headers every time the dataGrid is updated.
		cccDataGrid.setAutoHeaderRefreshDisabled(true);
		
		// Set the message to display when the table is empty.
		cccDataGrid.setEmptyTableWidget(new Label("No existen cuentas de cotizacion con nominas para este periodo".toUpperCase()));
		
		// Add a selection model so we can select cells.
	    this.selectionCCCInfoModel = new MultiSelectionModel<CCCInfo>(CCCInfo.KEY_PROVIDER);
	    cccDataGrid.setSelectionModel(this.selectionCCCInfoModel, DefaultSelectionEventManager.<CCCInfo> createCheckboxManager());
		
	    // Initialize the columns.
	    addCCCInfoColumns(this.selectionCCCInfoModel);
	    
	    new ListDataProvider<CCCInfo>(Collections.emptyList()).addDataDisplay(cccDataGrid);

	}
	
	private void addCCCInfoColumns(MultiSelectionModel<CCCInfo> selectionCCCInfoModel) {
		selectionCCCInfoModel.addSelectionChangeHandler(new Handler() {
	        
	        @Override
	        public void onSelectionChange(SelectionChangeEvent event) {
	        	enterprisesSelectedCount = selectionCCCInfoModel.getSelectedSet().size();
	        	enterprisesSelected.setText(enterprisesSelectedCount.toString());
	            if(selectionCCCInfoModel.getSelectedSet().size() > 0) {
	            	exportButton.setEnabled(true);
	            }else {
	            	exportButton.setEnabled(false);
	            }
	            
	        }
	    });
	    
	    Column<CCCInfo, Boolean> checkColumn = new Column<CCCInfo, Boolean>(
	        new CheckboxCell(true, false)) {
	      @Override
	      public Boolean getValue(CCCInfo object) {
	        // Get the value from the selection model.
	        return selectionCCCInfoModel.isSelected(object);
	      }
	    };
    
	    CheckboxCell selectAllHeaderCB = new CheckboxCell(true,true); //the checkbox is true true for dependsOnSelection and handlesSelection for it to work
	    Header<Boolean> selectAllHeader = new Header<Boolean>(selectAllHeaderCB)
	    {
	      @Override
	      public Boolean getValue()
	      {
	        //return true only when all items are selected
	    	boolean value = false;
	    	
	    	if(null != mainCRAObjectNew && null != mainCRAObjectNew.getEnterpriseCCCs())
	    		value = selectionCCCInfoModel.getSelectedSet().size() == mainCRAObjectNew.getEnterpriseCCCs().size();
	        
	    	return value; 
	      }
	    };
	    
	    selectAllHeader.setUpdater(new ValueUpdater<Boolean>(){
	      @Override
	      public void update(Boolean value)
	      {
	        // Select/deselect all persons
	    	if(null != mainCRAObjectNew && null != mainCRAObjectNew.getEnterpriseCCCs())
		        for (CCCInfo cccInfo : mainCRAObjectNew.getEnterpriseCCCs())
		          selectionCCCInfoModel.setSelected(cccInfo, value);
	        
	      }
	    });
	    
	    // Add Selection Column to table
	    cccDataGrid.addColumn(checkColumn,selectAllHeader);
	    cccDataGrid.setColumnWidth(checkColumn, 5, Unit.PCT);
	    checkColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		//----------------------------------------------------------------------
	    //							CREATE COLUMNS
	    //----------------------------------------------------------------------
		
		TextColumn<CCCInfo> enterpriseNameColumn = new TextColumn<CCCInfo>() {
	      @Override
	      public String getValue(CCCInfo cccInfo) {
	        return cccInfo.getEnterpriseDesciption();
	      }
	    };

	    enterpriseNameColumn.setSortable(true);
	    cccDataGrid.setColumnWidth(enterpriseNameColumn, 25, Unit.PCT);
	    
	    TextColumn<CCCInfo> activityNameColumn = new TextColumn<CCCInfo>() {
	      @Override
	      public String getValue(CCCInfo cccInfo) {
	        return cccInfo.getActivityDescription();
	      }
	    };

	    activityNameColumn.setSortable(true);
	    cccDataGrid.setColumnWidth(activityNameColumn, 20, Unit.PCT);
	    
	    TextColumn<CCCInfo> geozoneColumn = new TextColumn<CCCInfo>() {
	      @Override
	      public String getValue(CCCInfo cccInfo) {
	        return ProvinceContract.getName(cccInfo.getGeozone());
	      }
	    };

	    geozoneColumn.setSortable(true);
	    cccDataGrid.setColumnWidth(geozoneColumn, 10, Unit.PCT);
	    
	    TextColumn<CCCInfo> typeColumn = new TextColumn<CCCInfo>() {
	      @Override
	      public String getValue(CCCInfo cccInfo) {
	    	  return getCCCType(cccInfo.getType());
//	    	  return parseCCCType(cccInfo.getTypeStr());
	      }

	    };

	    typeColumn.setSortable(true);
	    cccDataGrid.setColumnWidth(typeColumn, 20, Unit.PCT);
	    
	    TextColumn<CCCInfo> cccColumn = new TextColumn<CCCInfo>() {
	      @Override
	      public String getValue(CCCInfo cccInfo) {
	    	  return cccInfo.getCcc();
	      }
	    };

	    cccColumn.setSortable(true);
	    cccDataGrid.setColumnWidth(cccColumn, 15, Unit.PCT);
		
	    // Add the columns.
	    cccDataGrid.addColumn(enterpriseNameColumn, "Empresa");
	    cccDataGrid.addColumn(activityNameColumn, "Actividad");
	    
	    cccDataGrid.addColumn(geozoneColumn, "Provincia");
	    
	    cccDataGrid.addColumn(typeColumn, "Tipo CCC");
	    cccDataGrid.addColumn(cccColumn, "CCC");
	      
	}

	// --------------------------------------------------------------------------------------------
	// 									PROVIDE CRA DATA GRID
	// --------------------------------------------------------------------------------------------

	private void provideCRAsDataGrid() {
		cras  = Collections.emptyList();
		
		// Resource Style CellTable
		crasDataGrid = new CustomDataGrid<CRA>(Integer.MAX_VALUE, CRA.KEY_PROVIDER);
		crasDataGrid.setWidth("100%");
		
		//Do not refresh the headers every time the dataGrid is updated.
		crasDataGrid.setAutoHeaderRefreshDisabled(true);
		
		// Set the message to display when the table is empty.
		crasDataGrid.setEmptyTableWidget(new Label("No existen CRAs".toUpperCase()));
		
		// Add a selection model so we can select cells.
	    this.selectionCraModel = new MultiSelectionModel<CRA>(CRA.KEY_PROVIDER);
	    crasDataGrid.setSelectionModel(this.selectionCraModel, DefaultSelectionEventManager.<CRA> createCheckboxManager());
		
	    // Initialize the columns.
	    addCraColumns(this.selectionCraModel);
	    
	    new ListDataProvider<CRA>(Collections.emptyList()).addDataDisplay(crasDataGrid);
	    
	}
	
	private void addCraColumns(MultiSelectionModel<CRA> selectionCraModel) {
		
		//----------------------------------------------------------------------
	    //							CREATE COLUMNS
	    //----------------------------------------------------------------------
		
		TextColumn<CRA> creationColumn = new TextColumn<CRA>() {
	      @Override
	      public String getValue(CRA cra) {
	    	  if(null == cra.getDate())
	    		  return "";
	    	  return formatFullDateHour.format(cra.getDate());
	      }
	    };

	    creationColumn.setSortable(true);
	    creationColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    crasDataGrid.setColumnWidth(creationColumn, 15, Unit.PCT);
	    
	    TextColumn<CRA> periodColumn = new TextColumn<CRA>() {
		      @Override
		      public String getValue(CRA cra) {
		    	  if(null == cra.getCreationDate())
		    		  return "";
		    	  return formatFullDate.format(cra.getCreationDate());
		      }
		    };

	    periodColumn.setSortable(true);
	    periodColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    crasDataGrid.setColumnWidth(periodColumn, 10, Unit.PCT);
	    
	    TextColumn<CRA> activityNameColumn = new TextColumn<CRA>() {
	      @Override
	      public String getValue(CRA cra) {
	        return cra.getActivityName();
	      }
	    };

	    activityNameColumn.setSortable(true);
	    crasDataGrid.setColumnWidth(activityNameColumn, 25, Unit.PCT);
	    
	    TextColumn<CRA> rectificativeColumn = new TextColumn<CRA>() {
	      @Override
	      public String getValue(CRA cra) {
	        return parseCRAType(cra.getType());
	      }
	    };

	    rectificativeColumn.setSortable(true);
	    crasDataGrid.setColumnWidth(rectificativeColumn, 10, Unit.PCT);
	    
	    TextColumn<CRA> geozoneColumn = new TextColumn<CRA>() {
	      @Override
	      public String getValue(CRA cra) {
	    	  return cra.getCccProvince();
	      }
	    };

	    geozoneColumn.setSortable(true);
	    crasDataGrid.setColumnWidth(geozoneColumn, 10, Unit.PCT);
	    
	    TextColumn<CRA> typeColumn = new TextColumn<CRA>() {
	      @Override
	      public String getValue(CRA cra) {
	    	  return getCCCType(cra.getCccType());
	      }

	    };

	    typeColumn.setSortable(true);
	    crasDataGrid.setColumnWidth(typeColumn, 15, Unit.PCT);
	    
	    TextColumn<CRA> cccColumn = new TextColumn<CRA>() {
	      @Override
	      public String getValue(CRA cra) {
	    	  return cra.getCcc();
	      }
	    };

	    cccColumn.setSortable(true);
	    crasDataGrid.setColumnWidth(cccColumn, 10, Unit.PCT);
	    
	    ActionCell<CRA> infoActionCell = new ActionCell<CRA>("", new ActionCell.Delegate<CRA>() {

			@Override
			public void execute(CRA cra) {
				if(cra.getIsConsignment()) {
					String message = "";
					for(CCCInfo cccInfo : cra.getIncludeCCCs()) {
						message += cccInfo.toString() + "\n";
					}
					WarningDialog info = new WarningDialog("CCCs contenidas", message);
					info.center();
					info.show();
				}
					
			}
			
		});
	    
	    Column<CRA, CRA> infoColumn = new Column<CRA, CRA>(infoActionCell) {

			@Override
			public CRA getValue(CRA object) {
				return object;
			}
			
			@Override
			public void render(Context context, CRA object, SafeHtmlBuilder sb) {
				if(null != object) {
					if(object.getIncludeCCCs().size() > 1)
						sb.appendHtmlConstant("<button type=\"button\" title=\"Ver CCCs\" class=\"aon-finding-toolbar-item aon-icon-info\" style=\"border: none !important;\"></button>");
				}
			}
		};
		
		infoColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		crasDataGrid.setColumnWidth(infoColumn, 5, Unit.PCT);
	    
	    ActionCell<CRA> downloadActionCell = new ActionCell<CRA>("", new ActionCell.Delegate<CRA>() {

			@Override
			public void execute(CRA cra) {
				String fileDownloadURL = GWT.getModuleBaseURL()+ "/download_cra/"
			            + "?craBatchId=" + cra.getCode();
				
				Window.open(fileDownloadURL, "_blank", null);
			}
			
		});
	    
	    Column<CRA, CRA> downloadColumn = new Column<CRA, CRA>(downloadActionCell) {

			@Override
			public CRA getValue(CRA object) {
				return object;
			}
			
			@Override
			public void render(Context context, CRA object, SafeHtmlBuilder sb) {
				if(null != object) {
					sb.appendHtmlConstant("<button type=\"button\" class=\"aon-finding-toolbar-item aon-icon-mail-save\" style=\"border: none !important; height: 20px;\"></button>");
				}
			}
		};
		
		downloadColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		crasDataGrid.setColumnWidth(downloadColumn, 5, Unit.PCT);
	    
	    ActionCell<CRA> deleteActionCell = new ActionCell<CRA>("", new ActionCell.Delegate<CRA>() {

			@Override
			public void execute(CRA cra) {
				mainCRAObjectNew.deteleCRA(cra.getCode(), 
						s -> {
							mainCRAObjectNew.removeCCCCRADate(cra);
							initCRATable();
						}, 
						f -> {});
			}
			
		});
	    
	    Column<CRA, CRA> deleteColumn = new Column<CRA, CRA>(deleteActionCell) {

			@Override
			public CRA getValue(CRA object) {
				return object;
			}
			
			@Override
			public void render(Context context, CRA object, SafeHtmlBuilder sb) {
				if(null != object) {
//					if(object.getDomain() == mainCRAObjectNew.getDomainId() || object.getDomain().equals(mainCRAObjectNew.getDomainId()))
						sb.appendHtmlConstant("<button type=\"button\" class=\"aon-finding-toolbar-item aon-icon-delete\" style=\"border: none !important; height: 20px;\"></button>");
				}
			}
		};
		
		deleteColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		crasDataGrid.setColumnWidth(deleteColumn, 5, Unit.PCT);
		
	    // Add the columns.
		crasDataGrid.addColumn(creationColumn, "F. Creacion");
	    crasDataGrid.addColumn(periodColumn, "P. Liquidacion");
	    crasDataGrid.addColumn(activityNameColumn, "Actividad");
	    
	    crasDataGrid.addColumn(rectificativeColumn, "Tipo CRA");
	    
	    crasDataGrid.addColumn(geozoneColumn, "Provincia");
	    
	    crasDataGrid.addColumn(typeColumn, "Tipo CCC");
	    crasDataGrid.addColumn(cccColumn, "CCC");
	    
	    crasDataGrid.addColumn(infoColumn, "");
	    crasDataGrid.addColumn(downloadColumn, "");
	    crasDataGrid.addColumn(deleteColumn, "");
	      
	}
	
	// --------------------------------------------------------------------------------------------
	// 									HEADER STYLES
	// --------------------------------------------------------------------------------------------
	
	public void addStyleToHeader() {
		cccDataGrid.getHeader(0).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		cccDataGrid.getHeader(1).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		cccDataGrid.getHeader(2).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		cccDataGrid.getHeader(3).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		cccDataGrid.getHeader(4).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		cccDataGrid.getHeader(5).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		
		crasDataGrid.getHeader(0).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		crasDataGrid.getHeader(1).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		crasDataGrid.getHeader(2).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		crasDataGrid.getHeader(3).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		crasDataGrid.getHeader(4).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		crasDataGrid.getHeader(5).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		crasDataGrid.getHeader(6).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		crasDataGrid.getHeader(7).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		crasDataGrid.getHeader(8).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		crasDataGrid.getHeader(9).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
	}
	
	private void initPreView() {
		collapsePanel.setOpen(false);
		filterTable.getRows().getItem(2).getStyle().setDisplay(Display.NONE);
		enterprisesSelected.setText(enterprisesSelectedCount.toString());
	}
	
	// --------------------------------------------------------------------------------------------
	// 										VARIABLES
	// --------------------------------------------------------------------------------------------
		
	private MainCRAObjectNew mainCRAObjectNew;
	private MultiSelectionModel<CCCInfo> selectionCCCInfoModel;
	private MultiSelectionModel<CRA> selectionCraModel;
	private DateTimeFormat formatFullDate = DateTimeFormat.getFormat("MM/yyyy");
	private DateTimeFormat formatFullDateHour = DateTimeFormat.getFormat("dd/MM/yyyy HH:mm");
	
	// --------------------------------------------------------------------------------------------
	// 										ON MODULE LOAD
	// --------------------------------------------------------------------------------------------
	
	public void onModuleLoad(MainCRAObjectNew mainCRAObjectNew) {
		this.mainCRAObjectNew = mainCRAObjectNew;
		
		// Show CCCs on start
		showCCCs();
		
		// Create findPeriod, first day of previus month
		Date initialDate = createInitialDate();
		
		Integer initialYear = initialDate.getYear();
		Integer initialMonth = initialDate.getMonth();
		
		Date findPeriod = DateUtils.copyDateOnly(initialDate);

		this.mainCRAObjectNew.getEnterprisesCCCInfo(findPeriod.getTime(),
				s -> {
					initEnterpriseSB();
					clearSelectionModel();
//					initListBox();
					initCCCsTable();
//					initCRATable();
					setInitialLBAndCBSelected(initialYear, initialMonth);
					setTableHeights();		
					
				}, 
				f -> {}
		);
	}

	private Date createInitialDate() {
		// Get first day of previus month
		Date actualDate = new Date();
		actualDate.setDate(1);
		actualDate = DateUtils.addMonths2Date(actualDate, -1);
		return actualDate;
	}
	
	private void initEnterpriseSB() {
		// Enteprise List
		List<String> enterprises = new ArrayList<>(mainCRAObjectNew.getEnterprisesMap().values());
		List<String> enterprisesSuggest = new ArrayList<String>();
		for(String enterprise : enterprises)
			enterprisesSuggest.add(enterprise+"");
		
		MultiWordSuggestOracle orclEnterprise = (MultiWordSuggestOracle) enterpriseSB.getSuggestOracle();
		orclEnterprise.addAll(enterprisesSuggest);
		enterpriseSB.setAutoSelectEnabled(false);
		
		enterpriseSB.addKeyUpHandler(e-> {
			String value = enterpriseSB.getValue();
			if(StringUtils.isBlank(value) || value.length() < 3) {
				mainCRAObjectNew.resetEnterpriseCCCList();
			} else {
				List<Integer> enterprisesIds = mainCRAObjectNew.getEnterprisesIds(value);
				mainCRAObjectNew.filterEnterpriseCCCListByEnterprise(enterprisesIds);
			}
			initCCCsTable();
		});
		
		enterpriseSB.addSelectionHandler(e -> {
			String value = enterpriseSB.getValue();
			List<Integer> enterprisesIds = mainCRAObjectNew.getEnterprisesIds(value);
			mainCRAObjectNew.filterEnterpriseCCCListByEnterprise(enterprisesIds);
			initCCCsTable();
		});
	}
	
	private void setInitialLBAndCBSelected(Integer initialYear, Integer initialMonth) {
		setSelectedValueLB(this.year, initialYear+"");
		setSelectedValueLB(this.month, initialMonth+"");
		
		this.allCCCsCB.setValue(true);
		this.emitCCCsCB.setValue(false);
		this.peddingCCCsCB.setValue(false);
	}
	
	private void setTableHeights() {
		cccDataGrid.getElement().getStyle().setHeight(100, Unit.PCT);
		mainTablePanel.getElement().getStyle().setHeight((Window.getClientHeight() - 255), Unit.PX);
		
		crasDataGrid.getElement().getStyle().setHeight(100, Unit.PCT);
		crasPanel.getElement().getStyle().setHeight((Window.getClientHeight() - 250), Unit.PX);
	}

	// --------------------------------------------------------------------------------------------
	// 										INIT CCCs TABLE
	// --------------------------------------------------------------------------------------------

	private void initCCCsTable() {		
		//Reset Selection Model 
		//selectionCCCInfoModel.clear();
				
		//Show buttons
		this.exportButton.setVisible(true);
		
		// Create a data provider.
	    ListDataProvider<CCCInfo> dataProvider = new ListDataProvider<CCCInfo>();

	    // Connect the table to the data provider.
	    dataProvider.addDataDisplay(cccDataGrid);
	    
	    // Add the data to the data provider, which automatically pushes it to the
	    // widget.
	    dataProvider.getList().clear();
	    List<CCCInfo> cccList = dataProvider.getList();
	    
	    this.cccs = this.mainCRAObjectNew.getEnterpriseCCCs();
	    
	    for (CCCInfo cccInfo : this.cccs) {
	    	cccList.add(cccInfo);
	    } 
	    
	    // Set page size
	    cccDataGrid.setPageSize(cccs.size());
	    
	    // Add style to table header
	    addStyleToHeader();
	    
	    addSortColums(cccList); 
		
	}
	
	private void addSortColums(List<CCCInfo> cccInfoList) {
		ListHandler<CCCInfo> columnSortHandler = new ListHandler<CCCInfo>(cccInfoList);
	    columnSortHandler.setComparator(cccDataGrid.getColumn(1), new Comparator<CCCInfo>() {
	          public int compare(CCCInfo o1, CCCInfo o2) {
	            if (o1 == o2) {
	              return 0;
	            }

	            if (o1 != null) {
	              return (o2 != null) ? o1.getEnterpriseDesciption().compareTo(o2.getEnterpriseDesciption()) : 1;
	            }
	            return -1;
	          }
	        });
	    
	    columnSortHandler.setComparator(cccDataGrid.getColumn(2), new Comparator<CCCInfo>() {
	          public int compare(CCCInfo o1, CCCInfo o2) {
	            if (o1 == o2) {
	              return 0;
	            }

	            if (o1 != null) {
	              return (o2 != null) ? o1.getActivityDescription().compareTo(o2.getActivityDescription()) : 1;
	            }
	            return -1;
	          }
	        });
	    
	    columnSortHandler.setComparator(cccDataGrid.getColumn(3), new Comparator<CCCInfo>() {
	          public int compare(CCCInfo o1, CCCInfo o2) {
	            if (o1 == o2) {
	              return 0;
	            }

	            if (o1 != null) {
	              return (o2 != null) ? o1.getGeozone().compareTo(o2.getGeozone()) : 1;
	            }
	            return -1;
	          }
	        });
	    
	    
	    columnSortHandler.setComparator(cccDataGrid.getColumn(4), new Comparator<CCCInfo>() {
	          public int compare(CCCInfo o1, CCCInfo o2) {
	            if (o1 == o2) {
	              return 0;
	            }

	            if (o1 != null) {
	              return (o2 != null) ? o1.getTypeStr().compareTo(o2.getTypeStr()) : 1;
	            }
	            return -1;
	          }
	        });
	    
	    columnSortHandler.setComparator(cccDataGrid.getColumn(5), new Comparator<CCCInfo>() {
	          public int compare(CCCInfo o1, CCCInfo o2) {
	            if (o1 == o2) {
	              return 0;
	            }

	            if (o1 != null) {
	              return (o2 != null) ? o1.getCcc().compareTo(o2.getCcc()) : 1;
	            }
	            return -1;
	          }
	        });
	    
	    
	    cccDataGrid.addColumnSortHandler(columnSortHandler);

	    // We know that the data is sorted alphabetically by default.
	    cccDataGrid.getColumn(2).setDefaultSortAscending(false);
	    cccDataGrid.getColumnSortList().push(cccDataGrid.getColumn(2));   
	}
		
	// --------------------------------------------------------------------------------------------
	// 										INIT CRA TABLE
	// --------------------------------------------------------------------------------------------

	private void initCRATable() {		
		//Reset Selection Model 
		//selectionCraModel.clear();
				
		// Create a data provider.
	    ListDataProvider<CRA> dataProvider = new ListDataProvider<CRA>();

	    // Connect the table to the data provider.
	    dataProvider.addDataDisplay(crasDataGrid);
	    
	    // Add the data to the data provider, which automatically pushes it to the
	    // widget.
	    List<CRA> crasList = dataProvider.getList();
	    crasList.clear();
	    
	    this.cras = this.mainCRAObjectNew.getFilteredCRAs();
	    
	    for (CRA cra : this.cras) {
	    	crasList.add(cra);
	    }
	    
	    crasList.sort(new Comparator<CRA>() {
			@Override
			public int compare(CRA o1, CRA o2) {
				if(null == o2.getDate())
					return -1;
				
				if(null == o1.getDate())
					return -1;
				
				return o2.getDate().compareTo(o1.getDate());
			}
		});
	    
		// Set page size
	    crasDataGrid.setPageSize(cras.size());
	    
	    // Add style to table header
	    addStyleToHeader();
	    
	    addSortCRAColums(crasList);
	}
	
	private void addSortCRAColums(List<CRA> crasList) {
		ListHandler<CRA> columnSortHandler = new ListHandler<CRA>(crasList);
		
	    columnSortHandler.setComparator(crasDataGrid.getColumn(0), new Comparator<CRA>() {
	          public int compare(CRA o1, CRA o2) {
	            if (o1 == o2) {
	              return 0;
	            }

	            if (o1 != null) {
	              return (o2 != null) ? o1.getDate().compareTo(o2.getDate()) : 1;
	            }
	            return -1;
	          }
	        });
	    
	    columnSortHandler.setComparator(crasDataGrid.getColumn(1), new Comparator<CRA>() {
	          public int compare(CRA o1, CRA o2) {
	            if (o1 == o2) {
	              return 0;
	            }

	            if (o1 != null) {
	              return (o2 != null) ? o1.getCreationDate().compareTo(o2.getCreationDate()) : 1;
	            }
	            return -1;
	          }
	        });
	    
	    columnSortHandler.setComparator(crasDataGrid.getColumn(2), new Comparator<CRA>() {
	          public int compare(CRA o1, CRA o2) {
	            if (o1 == o2) {
	              return 0;
	            }

	            if (o1 != null) {
	              return (o2 != null) ? o1.getActivityName().compareTo(o2.getActivityName()) : 1;
	            }
	            return -1;
	          }
	        });
	    
	    columnSortHandler.setComparator(crasDataGrid.getColumn(3), new Comparator<CRA>() {
	          public int compare(CRA o1, CRA o2) {
	            if (o1 == o2) {
	              return 0;
	            }

	            if (o1 != null) {
	              return (o2 != null) ? o1.getCccProvince().compareTo(o2.getCccProvince()) : 1;
	            }
	            return -1;
	          }
	        });
	    
	    
	    columnSortHandler.setComparator(crasDataGrid.getColumn(4), new Comparator<CRA>() {
	          public int compare(CRA o1, CRA o2) {
	            if (o1 == o2) {
	              return 0;
	            }

	            if (o1 != null) {
	              return (o2 != null) ? o1.getCccType().compareTo(o2.getCccType()) : 1;
	            }
	            return -1;
	          }
	        });
	    
	    columnSortHandler.setComparator(crasDataGrid.getColumn(5), new Comparator<CRA>() {
	          public int compare(CRA o1, CRA o2) {
	            if (o1 == o2) {
	              return 0;
	            }

	            if (o1 != null) {
	              return (o2 != null) ? o1.getCcc().compareTo(o2.getCcc()) : 1;
	            }
	            return -1;
	          }
	        });
	    
	    
	    crasDataGrid.addColumnSortHandler(columnSortHandler);
	    
	    // We know that the data is sorted alphabetically by default.
//	    Column<CRA, ?> creationColumn = crasDataGrid.getColumn(0);
//	    creationColumn.setDefaultSortAscending(false);
//	    crasDataGrid.getColumnSortList().push(creationColumn);
//	    crasDataGrid.getColumnSortList().push(creationColumn);
	}
	
	// --------------------------------------------------------------------------------------------
	// 										UI HANDLERS
	// --------------------------------------------------------------------------------------------
	
	@UiHandler("listButton")
	public void onListButton(ClickEvent event) {
//		mainCRAObjectNew.getCRAs(
//				s -> {
//					showCRAS();
//					initCRATable();
//				}, 
//				f -> {}
//		);
		showCRAS();
		initCRATable();
	}
	
	@UiHandler("allCCCsCB")
	public void onAllCCCsCB(ValueChangeEvent<Boolean> event) {
		if(event.getValue()) {
			this.emitCCCsCB.setValue(false);
			this.peddingCCCsCB.setValue(false);
			
			this.mainCRAObjectNew.resetEnterpriseCCCList();
			enterpriseSB.setText("");
			
//			showCCCs();
			clearSelectionModel();
			initCCCsTable();
//			initCRATable();
		}
	}
	
	@UiHandler("emitCCCsCB")
	public void onEmitCCCsCB(ValueChangeEvent<Boolean> event) {
		if(event.getValue()) {
			this.allCCCsCB.setValue(false);
			this.peddingCCCsCB.setValue(false);
			
			Date findPeriod = new Date(Integer.parseInt(year.getSelectedValue()), Integer.parseInt(month.getSelectedValue()), 1);
			this.mainCRAObjectNew.filterEmitedCCC(findPeriod);
			enterpriseSB.setText("");
			
//			showCCCs();
			clearSelectionModel();
			initCCCsTable();
//			initCRATable();
		}
		
	}
	
	@UiHandler("peddingCCCsCB")
	public void onPenddingCCCsCB(ValueChangeEvent<Boolean> event) {
		if(event.getValue()) {
			this.allCCCsCB.setValue(false);
			this.emitCCCsCB.setValue(false);
			enterpriseSB.setText("");
			
			Date findPeriod = new Date(Integer.parseInt(year.getSelectedValue()), Integer.parseInt(month.getSelectedValue()), 1);
			this.mainCRAObjectNew.filterPenddingCCC(findPeriod);
			
//			showCCCs();
			clearSelectionModel();
			initCCCsTable();
//			initCRATable();
		}
	}
	
	@UiHandler("month")
	public void onMonthChange(ChangeEvent event) {
//		showCCCs();
		
		Date findPeriod = new Date(Integer.parseInt(year.getSelectedValue()), Integer.parseInt(month.getSelectedValue()), 1);
		
		this.mainCRAObjectNew.getEnterprisesCCCInfo(findPeriod.getTime(),
				s -> {
					clearSelectionModel();
//					initListBox();
					initCCCsTable();
//					initCRATable();
					
					int initialYear = findPeriod.getYear();
					int initialMonth = findPeriod.getMonth();
					setInitialLBAndCBSelected(initialYear, initialMonth);
					setTableHeights();		
					
				}, 
				f -> {}
		);

	}
	
	@UiHandler("year")
	public void onYearChange(ChangeEvent event) {
//		showCCCs();
		
		Date findPeriod = new Date(Integer.parseInt(year.getSelectedValue()), Integer.parseInt(month.getSelectedValue()), 1);
		
		this.mainCRAObjectNew.getEnterprisesCCCInfo(findPeriod.getTime(),
				s -> {
					clearSelectionModel();
//					initListBox();
					initCCCsTable();
//					initCRATable();
					
					int initialYear = findPeriod.getYear();
					int initialMonth = findPeriod.getMonth();
					setInitialLBAndCBSelected(initialYear, initialMonth);
					setTableHeights();
				}, 
				f -> {}
		);

	}
	
	@UiHandler("newCRAButton")
	public void onNewCRAButton(ClickEvent event) {
//		this.mainCRAObjectNew.resetEnterpriseCCCList();
		
		showCCCs();
		clearSelectionModel();
		initCCCsTable();
//		initCRATable();
	}
	
	@UiHandler("exportButton")
	public void onExportButton(ClickEvent event) {
		if(selectionCCCInfoModel.getSelectedSet().size() > 0) {
			ArrayList<String> cccList = new ArrayList<String>();
			ArrayList<Integer> cccIdList = new ArrayList<Integer>();
			
			for(CCCInfo cccInfo : selectionCCCInfoModel.getSelectedSet()) {
				cccList.add(cccInfo.getCcc());
				cccIdList.add(cccInfo.getCccId());
			}
			
			ArrayList<CCCInfo>  cccsSelected = new ArrayList<CCCInfo>();
			cccsSelected.addAll(selectionCCCInfoModel.getSelectedSet());
			Integer cccId = cccsSelected.get(0).getCccId();
			
			Date findingDate = new Date(Integer.parseInt(year.getSelectedValue()), Integer.parseInt(month.getSelectedValue()), 1);
			DateUtils.resetTime(findingDate);
			
			if(checkRectificavo()) {
				mainCRAObjectNew.checkCreateNewCRA(findingDate, cccIdList,
						p -> {
							if(StringUtils.isBlank(p)) {
								mainCRAObjectNew.createNewCRA(findingDate, cccList, cccIdList, cccId, "N",
										v -> {
											for(CCCInfo cccInfo : cccsSelected) {
												cccInfo.getCRADates().add(findingDate);
											}
											showCRAS();
											initCRATable();
										}, 
										f -> {});
							}else {
								AcceptCancelDialog dialog = new AcceptCancelDialog("AVISO", p) {
									
									@Override
									protected void onAccept() {
										mainCRAObjectNew.createNewCRA(findingDate, cccList, cccIdList, cccId, "N",
												v -> {
													for(CCCInfo cccInfo : cccsSelected) {
														cccInfo.getCRADates().add(findingDate);
													}
													showCRAS();
													initCRATable();
												}, 
												f -> {});
									}
								};
								dialog.center();
								dialog.show();
							}
						}, f ->{});
			} else {
				AcceptCancelDialog dialog = new AcceptCancelDialog("AVISO", "Ya existe un fichero CRA para esta cuenta de cotizaci"+String.valueOf("\u00F3")+"n en este periodo. Recuerde que puede eliminar de la tabla dicho fichero CRA. Si por lo contrario quiere generar un fichero CRA rectificativo puede acepte esta ventana." + String.valueOf("\u00BF")+"Desea generar un fichero rectificativo?") {
					
					@Override
					protected void onAccept() {
						mainCRAObjectNew.createNewCRA(findingDate, cccList, cccIdList, cccId, "R",
								v -> {
									WarningDialog warning = new WarningDialog("INTRUCCIONES", "Para poder llevar a cabo la rectificaci"+String.valueOf("\u00F3")+"n del fichero "
											+ "CRA, deber"+String.valueOf("\u00E1")+" seguir las siguientes instrucciones : \n\n 1- Enviar el CRA Rectificativo que se ha generado en el historial de CRAs rectificativos. ");
									warning.center();
									warning.show();
									
									for(CCCInfo cccInfo : cccsSelected) {
										cccInfo.getCRADates().add(findingDate);
									}
									
									showCRAS();
									initCRATable();
								}, 
								f -> {});
					}
				};
				
				dialog.center();
				dialog.show();
			}
		}
	}
	
	@UiHandler("typeList")
	public void onTypeListChange(ChangeEvent event) {
		if(0 == typeList.getSelectedIndex())
			this.mainCRAObjectNew.resetCRAsList();
		else {
			Byte type = Byte.parseByte(typeList.getSelectedValue());
			mainCRAObjectNew.filterCRAsListByType(type);
		}
		initCRATable();
	}
	
	@UiHandler("geozoneList")
	public void onGeozoneListChange(ChangeEvent event) {
		if(0 == geozoneList.getSelectedIndex())
			this.mainCRAObjectNew.resetCRAsList();
		else {
			String geozoneCode = geozoneList.getSelectedItemText();
			mainCRAObjectNew.filterCRAListByGeozone(geozoneCode);
		}
		initCRATable();
	}
//	
//	@UiHandler({"monthTillT", "yearTillT", "monthTTo", "yearTTo"})
//	public void onFilterDatesChange(ChangeEvent event) {
//		Date date1 = new Date(Integer.parseInt(yearTillT.getSelectedValue()), Integer.parseInt(monthTillT.getSelectedValue()), 1);
//		Date date2 = new Date(Integer.parseInt(yearTTo.getSelectedValue()), Integer.parseInt(monthTTo.getSelectedValue()), 1);
//		
//		Date startDate = null;
//		Date endDate = null;
//		
//		if(date1.before(date2) || date1.equals(date2)) {
//			startDate = DateUtils.copyDateOnly(date1);
//			
//			endDate = DateUtils.copyDateOnly(date2);
//			endDate = DateUtils.getLastDayOfMonth(endDate);
//		} else {
//			startDate = DateUtils.copyDateOnly(date2);
//			
//			endDate = DateUtils.copyDateOnly(date1);
//			endDate = DateUtils.getLastDayOfMonth(endDate);
//		}
//		
//		mainCRAObjectNew.filterCrasByDates(startDate, endDate);
//		initCRATable();
//	}
	
	@UiHandler({"monthTillT", "yearTillT", "monthTTo", "yearTTo", "geozoneList", "typeList"})
	public void onFilterChange(ChangeEvent event) {
		// DATES
		Date date1 = new Date(Integer.parseInt(yearTillT.getSelectedValue()), Integer.parseInt(monthTillT.getSelectedValue()), 1);
		Date date2 = new Date(Integer.parseInt(yearTTo.getSelectedValue()), Integer.parseInt(monthTTo.getSelectedValue()), 1);
		
		Date startDate = null;
		Date endDate = null;
		
		if(date1.before(date2) || date1.equals(date2)) {
			startDate = DateUtils.copyDateOnly(date1);
			
			endDate = DateUtils.copyDateOnly(date2);
			endDate = DateUtils.getLastDayOfMonth(endDate);
		} else {
			startDate = DateUtils.copyDateOnly(date2);
			
			endDate = DateUtils.copyDateOnly(date1);
			endDate = DateUtils.getLastDayOfMonth(endDate);
		}
		
		// GEOZONE
		String geozoneName = geozoneList.getSelectedItemText();
		
		// CCC TYPE
		Byte cccType = null;
		if(0 == typeList.getSelectedIndex())
			cccType = Byte.parseByte("-1");
		else
			cccType = Byte.parseByte(typeList.getSelectedValue());
		
		mainCRAObjectNew.filterCras(startDate, endDate, geozoneName, cccType);
		initCRATable();
	}
	
	@UiHandler("collapsePanel")
	public void onOpenPanel(OpenEvent<DisclosurePanel> event) {
		crasPanel.getElement().getStyle().setHeight((Window.getClientHeight() - 310), Unit.PX);
		crasDataGrid.redraw();
	}
	
	@UiHandler("collapsePanel")
	public void onClosePanel(CloseEvent<DisclosurePanel> event) {
		crasPanel.getElement().getStyle().setHeight((Window.getClientHeight() - 250), Unit.PX);
		crasDataGrid.redraw();
	}
	
	// --------------------------------------------------------------------------------------------
	// 										AUX METHODS
	// --------------------------------------------------------------------------------------------
	
//	private void initListBox() {
//		//Month
//		month.clear();
//		month.addItem("Enero", "0");
//		month.addItem("Febrero", "1");
//		month.addItem("Marzo", "2");
//		month.addItem("Abril", "3");
//		month.addItem("Mayo", "4");
//		month.addItem("Junio", "5");
//		month.addItem("Julio", "6");
//		month.addItem("Agosto", "7");
//		month.addItem("Septiembre", "8");
//		month.addItem("Octubre", "9");
//		month.addItem("Noviembre", "10");
//		month.addItem("Diciembre", "11");
//		
//		
//		// Year
//		Integer actualYear = new Date().getYear() + 1900;
//		year.clear();
//		year.addItem((actualYear)+"", (actualYear-1900)+"");
//		year.addItem((actualYear-1)+"", (actualYear-1-1900)+"");
//		year.addItem((actualYear-2)+"", (actualYear-2-1900)+"");
//		
//		// Type List
//		typeList.clear();
//		typeList.addItem("-", "-1");
//		typeList.addItem("Principal", "0");
//		typeList.addItem("Formacion y aprendizaje", "1");
//		typeList.addItem("Aprendizaje", "2");
//		typeList.addItem("Representantes de comercio", "3");
//		typeList.addItem("Asimilados R.General", "4");
//		typeList.addItem("Becarios", "5");
//		typeList.addItem("Emploead@s de hogar", "6");
//		typeList.addItem("Trabajadores cuenta ajena agrarios", "7");
//		typeList.addItem("Artistas", "8");
//		typeList.getElement().getElementsByTagName("option").getItem(2).setAttribute("disabled", "disabled");
//		typeList.addStyleName("aon-selectOneMenu");
//		
//		// Enteprise List
//		List<String> enterprises = new ArrayList<>(mainCRAObjectNew.getEnterprisesMap().values());
//		List<String> enterprisesSuggest = new ArrayList<String>();
//		for(String enterprise : enterprises)
//			enterprisesSuggest.add(enterprise+"");
//		
////		MultiWordSuggestOracle orclEnterprise = (MultiWordSuggestOracle) enterpriseSB.getSuggestOracle();
////		orclEnterprise.addAll(enterprisesSuggest);
////		enterpriseSB.setAutoSelectEnabled(false);
////		
////		enterpriseSB.addKeyUpHandler(e-> {
////			String value = enterpriseSB.getValue();
////			if(StringUtils.isBlank(value) || value.length() < 3) {
////				mainCRAObjectNew.resetEnterpriseCCCList();
////			} else {
////				List<Integer> enterprisesIds = mainCRAObjectNew.getEnterprisesIds(value);
////				mainCRAObjectNew.filterEnterpriseCCCListByEnterprise(enterprisesIds);
////			}
////			initCCCsTable();
////		});
////		
////		enterpriseSB.addSelectionHandler(e -> {
////			String value = enterpriseSB.getValue();
////			List<Integer> enterprisesIds = mainCRAObjectNew.getEnterprisesIds(value);
////			mainCRAObjectNew.filterEnterpriseCCCListByEnterprise(enterprisesIds);
////			initCCCsTable();
////		});
//		
//		//Geozone
//		geozoneList.clear();
//		geozoneList.addItem("-", "-1");
//		for(Entry<String, String> province : ProvinceContract.getProvinces().entrySet()) {
//			geozoneList.addItem(province.getValue(), province.getKey());
//		}
//		
//	}

//	private String parseCCCType(String typeStr) {
//		switch (typeStr) {
//			case "0111":
//				return "Principal";
//			case "0138":
//				return "Emploead@s de hogar";
//			case "0163":
//				return "Trabajadores cuenta ajena agrarios";
//			case "0112":
//				return "Artistas";
//			default:
//				return "Principal";
//		}
//	}
	
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
	
	private void showCCCs() {
		mainContainer.getElement().getStyle().clearDisplay();
		crasContainer.getElement().getStyle().setDisplay(Display.NONE);
		
		exportButton.setEnabled(false);
		exportButton.setVisible(true);
		listButton.setVisible(true);
		newCRAButton.setVisible(false);
		
		this.allCCCsCB.setValue(true);
		this.emitCCCsCB.setValue(false);
		this.peddingCCCsCB.setValue(false);
	}
	
	private void showCRAS() {
		crasContainer.getElement().getStyle().clearDisplay();
		mainContainer.getElement().getStyle().setDisplay(Display.NONE);
		
		exportButton.setEnabled(false);
		exportButton.setVisible(false);
		listButton.setVisible(false);
		newCRAButton.setVisible(true);
	}
	
	private String parseCRAType(String type) {
		switch (type) {
		case "N":
			return "-";
		case "R":
			return "RECTIFICATIVO";
		default:
			return "-";
		}
	}
	
	public void clearSelectionModel() {
		this.selectionCCCInfoModel.clear();
		this.selectionCraModel.clear();
	}
	
	private boolean checkRectificavo() {
		Integer yearInt = Integer.parseInt(year.getSelectedValue());
		Integer monthInt = Integer.parseInt(month.getSelectedValue());
		Date findingDate = new Date(yearInt, monthInt, 1);
		DateUtils.resetTime(findingDate);
		
		ArrayList<Integer> cccIdList = new ArrayList<Integer>();
		for(CCCInfo cccInfo : selectionCCCInfoModel.getSelectedSet()) {
			cccIdList.add(cccInfo.getCccId());
		}
		
		for(CRA cra : mainCRAObjectNew.getAllCRAs()) {
			if(null == cra.getCreationDate())
				continue;
			
			Date creationDate = DateUtils.copyDateOnly(cra.getCreationDate());
			DateUtils.resetTime(creationDate);
			
			for(CCCInfo ccc : cra.getIncludeCCCs()) {
				if(cccIdList.contains(ccc.getCccId()) && (creationDate.equals(findingDate) || creationDate.getTime() == findingDate.getTime()))
					return false;
			}
			
//			if(cccList.contains(cra.getCcc().substring(4)) && creationDate.equals(findDate)) {
//				return false;
//			}
			
		}
		return true;
	}
	
	private String getCCCType(Byte type) {
		switch (type) {
			case (byte) 0:
				return "PRINCIPAL";
			case (byte) 1:
				return "FORMACION Y APRENDIZAJE";
			case (byte) 3:
				return "REPRESENTANTES DE COMERCIO";
			case (byte) 4:
				return "ASIMILADOS R.GENERAL";
			case (byte) 5:
				return "BECARIOS";
			case (byte) 6:
				return "EMPLEADOS DE HOGAR";
			case (byte) 7:
				return "TRABAJADOR CUENTA AJENA";
			case (byte) 8:
				return "ARTISTA";
			default:
				return "-";
		}
	}

}
