package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfo;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfoFilter;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;

public class WorkplaceSalary extends Composite implements ContextMenuHandler {

	private static EmployeeSalaryUiBinder uiBinder = GWT.create(EmployeeSalaryUiBinder.class);

	interface EmployeeSalaryUiBinder extends UiBinder<Widget, WorkplaceSalary> {}
	
	//Listener to Publish Salaries
	static interface Listener {
		void onPublishSalaries(SalaryInfo salary);
	}
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String suggestBox();
	}
	
	@UiField
	Button deleteButton;
	
	@UiField
	Button saveButton;
	
	@UiField
	Button publishButton;
	
	@UiField
	Button emailEnterpriseButton;
	
	@UiField
	Button emailEmployeesButton;
	
	@UiField
	HTMLPanel mainContainer;
	
	@UiField
	Label workplaceNameLabel;

	@UiField
	DisclosurePanel collapsePanel;
	
	@UiField
	SuggestBox employeeSB;
	
	@UiField
	ListBox typeList;
	
	@UiField
	RadioButton noDateRB;
	
	@UiField
	RadioButton dateTTRB;
	
	@UiField
	ListBox monthTillT;
	
	@UiField
	ListBox yearTillT;
	
	@UiField
	ListBox monthTTo;
	
	@UiField
	ListBox yearTTo;
	
	@UiField
	Button filterButton;
	
	@UiField
	HTMLPanel mainTablePanel;
	
	@UiField(provided = true)
	DataGrid<SalaryInfo> salaryDataGrid;

	private List<SalaryInfo> salaries = Collections.emptyList();
	
	public WorkplaceSalary() {
		provideSalaryDataGrid();
		
		//Inicializamos la vista del calendario
		initWidget(uiBinder.createAndBindUi(this)); 
		
		listeners = new LinkedList<Listener>();
		
		initCollapsePanel();
	}
	
	private void provideSalaryDataGrid() {
		salaries  = Collections.emptyList();
		
		// Resource Style CellTable
		salaryDataGrid = new CustomDataGrid<SalaryInfo>(Integer.MAX_VALUE, SalaryInfo.KEY_PROVIDER);
		salaryDataGrid.setWidth("100%");
		
		//Do not refresh the headers every time the dataGrid is updated.
		salaryDataGrid.setAutoHeaderRefreshDisabled(true);
		
		// Set the message to display when the table is empty.
		salaryDataGrid.setEmptyTableWidget(new Label("No existen nominas".toUpperCase()));
		
		// Add a selection model so we can select cells.
	    this.selectionModel = new MultiSelectionModel<SalaryInfo>(SalaryInfo.KEY_PROVIDER);
	    salaryDataGrid.setSelectionModel(this.selectionModel, DefaultSelectionEventManager.<SalaryInfo> createCheckboxManager());
		
	    // Initialize the columns.
	    addColumns(this.selectionModel);
	    
	    new ListDataProvider<SalaryInfo>(Collections.emptyList()).addDataDisplay(salaryDataGrid);
	    
	    // Add style to table header
	    addStyleToHeader();
	}
	
	public void addStyleToHeader() {
		salaryDataGrid.getHeader(0).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
	    salaryDataGrid.getHeader(1).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
	    salaryDataGrid.getHeader(2).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
	    salaryDataGrid.getHeader(3).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
	    salaryDataGrid.getHeader(4).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
	    salaryDataGrid.getHeader(5).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
	    salaryDataGrid.getHeader(6).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
	    salaryDataGrid.getHeader(7).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
	    salaryDataGrid.getHeader(8).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
	    salaryDataGrid.getHeader(9).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
	}
	
	
	private void initCollapsePanel() {
		collapsePanel.setOpen(false);
		monthTillT.setEnabled(false);
		yearTillT.setEnabled(false);
		monthTTo.setEnabled(false);
		yearTTo.setEnabled(false);
	}
	
	private void addColumns(MultiSelectionModel<SalaryInfo> selectionModel) {
		selectionModel.addSelectionChangeHandler(new Handler() {
	        
	        @Override
	        public void onSelectionChange(SelectionChangeEvent event) {
	            if(selectionModel.getSelectedSet().size() > 0) {
	            	deleteButton.setEnabled(true);
	            	saveButton.setEnabled(true);
	            	publishButton.setEnabled(true);
	            	emailEnterpriseButton.setEnabled(true);
	            	emailEmployeesButton.setEnabled(true);
	            }else {
	            	deleteButton.setEnabled(false);
	            	saveButton.setEnabled(false);
	            	publishButton.setEnabled(false);
	            	emailEnterpriseButton.setEnabled(false);
	            	emailEmployeesButton.setEnabled(false);
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
	    	boolean value = false;
	    	
	    	if(null != workplaceSalaryObject.getWorkplaceSalaries())
	    		value = selectionModel.getSelectedSet().size() == workplaceSalaryObject.getWorkplaceSalaries().size();
	        
	    	return value; 
	      }
	    };
	    
	    selectAllHeader.setUpdater(new ValueUpdater<Boolean>(){
	      @Override
	      public void update(Boolean value)
	      {
	        // Select/deselect all persons
	    	if(null != workplaceSalaryObject.getWorkplaceSalaries())
		        for (SalaryInfo person : workplaceSalaryObject.getWorkplaceSalaries())
		          selectionModel.setSelected(person, value);
	        
	      }
	    });
	    
	    // Add Selection Column to table
	    salaryDataGrid.addColumn(checkColumn,selectAllHeader);
	    salaryDataGrid.setColumnWidth(checkColumn, 60, Unit.PX);
	    checkColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		//----------------------------------------------------------------------
	    //							CREATE COLUMNS
	    //----------------------------------------------------------------------
		
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
	    
	    // Create start date column.
	    TextColumn<SalaryInfo> startDateColumn = new TextColumn<SalaryInfo>() {
	      @Override
	      public String getValue(SalaryInfo salaryInfo) {
	    	  return formatFullDate.format(salaryInfo.getStartDate());
	      }
	    };

	    // Make the  start date column sortable.
	    startDateColumn.setSortable(true);
	    
	    // Create end date column.
	    TextColumn<SalaryInfo> endDateColumn = new TextColumn<SalaryInfo>() {
	      @Override
	      public String getValue(SalaryInfo salaryInfo) {
	    	  return formatFullDate.format(salaryInfo.getEndDate());
	      }
	    };

	    // Make the end date column sortable.
	    endDateColumn.setSortable(true);
	    
	    // Create type column.
	    TextColumn<SalaryInfo> typeColumn = new TextColumn<SalaryInfo>() {
	      @Override
	      public String getValue(SalaryInfo salaryInfo) {
	        return salaryInfo.getType().getDescription();
	      }
	    };
	    
	    // Make the type column sortable.
	    typeColumn.setSortable(true);
	    
	    // Create total payment column.
	    TextColumn<SalaryInfo> totalPaymentColumn = new TextColumn<SalaryInfo>() {
	      @Override
	      public String getValue(SalaryInfo salaryInfo) {
	    	  if(null == salaryInfo.getTotalPayment() || 0 == salaryInfo.getTotalPayment())
	    		  return "00,00";
	    	  return NumberFormat.getFormat("#.00").format(salaryInfo.getTotalPayment());
	      }
	    };
	 
	    totalPaymentColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
	    
	    // Create total deduction column.
	    TextColumn<SalaryInfo> totalDeductionColumn = new TextColumn<SalaryInfo>() {
	      @Override
	      public String getValue(SalaryInfo salaryInfo) {
	    	  if(null == salaryInfo.getTotalDecuction() || 0 == salaryInfo.getTotalDecuction())
	    		  return "00,00";
	    	  return NumberFormat.getFormat("#.00").format(salaryInfo.getTotalDecuction());
	      }
	    };
	    
	    totalDeductionColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
	    
	    // Create total liquid column.
	    TextColumn<SalaryInfo> totalLiquidColumn = new TextColumn<SalaryInfo>() {
	      @Override
	      public String getValue(SalaryInfo salaryInfo) {
	    	  if(null == salaryInfo.getTotalLiquid() || 0 == salaryInfo.getTotalLiquid())
	    		  return "00,00";
	    	  return NumberFormat.getFormat("#.00").format(salaryInfo.getTotalLiquid())+" "+String.valueOf("\u20AC");
	      }
	    };
	    
	    totalLiquidColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
	    
	    ActionCell<SalaryInfo> draftActionCell = new ActionCell<SalaryInfo>("Borrador", new ActionCell.Delegate<SalaryInfo>() {

			@Override
			public void execute(SalaryInfo salary) {
				EmployeeTree.showSalaryDraft(salary.getContract(),
						salary.getWorkplaceId(), salary.getStartDate(),
						salary.getEndDate());
			}
			
		});
	    
	    Column<SalaryInfo, SalaryInfo> draftColumn = new Column<SalaryInfo, SalaryInfo>(draftActionCell) {

			@Override
			public SalaryInfo getValue(SalaryInfo object) {
				return object;
			}
			
			@Override
			public void render(Context context, SalaryInfo object, SafeHtmlBuilder sb) {
				if(null != object) {
					sb.appendHtmlConstant("<button type=\"button\" class=\"aon-finding-toolbar-item aon-icon-edit\" style=\"border: none !important; height: 20px;\"></button>");
				}
			}
		};
		
		draftColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		salaryDataGrid.setColumnWidth(draftColumn, 100, Unit.PX);
		
	    // Add the columns.
		salaryDataGrid.addColumn(employeeNameColumn, "Empleado");
		salaryDataGrid.addColumn(workplaceNameColumn, "C. Trabajo");
	    
		salaryDataGrid.addColumn(typeColumn, "Tipo");
		salaryDataGrid.addColumn(startDateColumn, "F. Inicio");
		salaryDataGrid.addColumn(endDateColumn, "F. Fin");
	    
		salaryDataGrid.addColumn(totalPaymentColumn, "Bruto");
		salaryDataGrid.addColumn(totalDeductionColumn, "Deducciones");
		salaryDataGrid.addColumn(totalLiquidColumn, "L"+String.valueOf("\u00ED")+"quido");
	    
		salaryDataGrid.addColumn(draftColumn, "Borrador");
	      
	}
	
	// --------------------------------------------------
	//				setEnterpriseSalaryObject
	// --------------------------------------------------

	private WorkplaceSalaryObject workplaceSalaryObject;
	private MultiSelectionModel<SalaryInfo> selectionModel;
	private List<Listener> listeners;
	private DateTimeFormat formatFullDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	public void setWorkplaceSalaryObject(WorkplaceSalaryObject workplaceSalaryObject) {
		this.workplaceSalaryObject = workplaceSalaryObject;
		this.workplaceSalaryObject.getWorkplaceSalariesDB(
				s -> {
					//Workplace Name Title
					if(!this.workplaceSalaryObject.getWorkplaceSalaries().isEmpty())
						workplaceNameLabel.setText(this.workplaceSalaryObject.getWorkplaceSalaries().get(0).getWorkplaceName());
				    
					initListBox();
					initSuggestBox();
					this.noDateRB.setValue(true, false);
					initSalariesTable();
					
					salaryDataGrid.getElement().getStyle().setHeight(100, Unit.PCT);
					mainTablePanel.getElement().getStyle().setHeight(725, Unit.PX);
				}, 
				f -> {}
		);
	}
	
	private void initListBox() {
		// Clear listboxies
		typeList.clear();
		monthTillT.clear();
		yearTillT.clear();
		monthTTo.clear();
		yearTTo.clear();
		
		// Add types to typeList
		typeList.addItem("Todas");
		typeList.addItem("Nomina");
		typeList.addItem("Extra");
		typeList.addItem("Atraso");
		typeList.addItem("Finiquito");
		
		// Add months to listboxes
		String[] monthList = new String[] {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
		for(String month : monthList) {
			monthTillT.addItem(month);
			monthTTo.addItem(month);
		}
		
		// Add year to listboxes
		Integer actualYear = new Date().getYear() + 1900;
		Integer firstPayroll = (null == this.workplaceSalaryObject.getWorkplaceSalaries() || this.workplaceSalaryObject.getWorkplaceSalaries().isEmpty()) ? new Date().getYear() + 1900 : this.workplaceSalaryObject.getWorkplaceSalaries().get(this.workplaceSalaryObject.getWorkplaceSalaries().size()-1).getStartDate().getYear() + 1900;
		Integer diffYears = actualYear - firstPayroll;
		for(int i = 0; i <= diffYears; i++) {
			yearTillT.addItem((actualYear - i)+"");
			yearTTo.addItem((actualYear - i)+"");
		}
	}

	private void initSuggestBox() {
		//NAMES
		List<String> employeesNames = this.workplaceSalaryObject.getWorkplaceEmployees().getWorkplaceEmployeesName();
		List<String> employeesNamesSuggest = new ArrayList<String>();
		for(String name : employeesNames)
			employeesNamesSuggest.add(name+"");
		MultiWordSuggestOracle orclNames = (MultiWordSuggestOracle) this.employeeSB.getSuggestOracle();
		orclNames.addAll(employeesNamesSuggest);
		this.employeeSB.setAutoSelectEnabled(true);
		this.employeeSB.addStyleName(style.suggestBox());
		this.employeeSB.setValue("");
	}

	private void initSalariesTable() {		
		//Reset Selection Model 
		selectionModel.clear();
		
		//Show buttons
		this.saveButton.setVisible(true);
		this.publishButton.setVisible(true);
		this.emailEmployeesButton.setVisible(true);
		
		//Disable buttons till any salary selected
		deleteButton.setEnabled(false);
		saveButton.setEnabled(false);
		publishButton.setEnabled(false);
		emailEnterpriseButton.setEnabled(false);
		emailEmployeesButton.setEnabled(false);
		
		// Create a data provider.
	    ListDataProvider<SalaryInfo> dataProvider = new ListDataProvider<SalaryInfo>();

	    // Connect the table to the data provider.
	    dataProvider.addDataDisplay(salaryDataGrid);
	    
	    // Add the data to the data provider, which automatically pushes it to the
	    // widget.
	    List<SalaryInfo> salaryList = dataProvider.getList();
	    salaryList.clear();
	    
	    this.salaries = this.workplaceSalaryObject.getWorkplaceSalaries();
	    
	    for (SalaryInfo salary : this.salaries) {
	    	salaryList.add(salary);
	    }   
		
		addSortColums(salaryList);
	    
		// Set page size
	    salaryDataGrid.setPageSize(salaries.size());
	    
	    // Add style to table header
	    addStyleToHeader();
	}

	private void addSortColums(List<SalaryInfo> salaryList) {
		ListHandler<SalaryInfo> columnSortHandler = new ListHandler<SalaryInfo>(salaryList);
	    columnSortHandler.setComparator(salaryDataGrid.getColumn(1), new Comparator<SalaryInfo>() {
	          public int compare(SalaryInfo o1, SalaryInfo o2) {
	            if (o1 == o2) {
	              return 0;
	            }

	            if (o1 != null) {
	              return (o2 != null) ? o1.getEmployeeName().compareTo(o2.getEmployeeName()) : 1;
	            }
	            return -1;
	          }
	        });
	    
	    columnSortHandler.setComparator(salaryDataGrid.getColumn(2), new Comparator<SalaryInfo>() {
	          public int compare(SalaryInfo o1, SalaryInfo o2) {
	            if (o1 == o2) {
	              return 0;
	            }

	            if (o1 != null) {
	              return (o2 != null) ? o1.getWorkplaceName().compareTo(o2.getWorkplaceName()) : 1;
	            }
	            return -1;
	          }
	        });
	    
	    columnSortHandler.setComparator(salaryDataGrid.getColumn(4), new Comparator<SalaryInfo>() {
	          public int compare(SalaryInfo o1, SalaryInfo o2) {
	            if (o1 == o2) {
	              return 0;
	            }

	            if (o1 != null) {
	              return (o2 != null) ? o1.getStartDate().compareTo(o2.getStartDate()) : 1;
	            }
	            return -1;
	          }
	        });
	    
	    columnSortHandler.setComparator(salaryDataGrid.getColumn(5), new Comparator<SalaryInfo>() {
	          public int compare(SalaryInfo o1, SalaryInfo o2) {
	            if (o1 == o2) {
	              return 0;
	            }

	            if (o1 != null) {
	              return (o2 != null) ? o1.getEndDate().compareTo(o2.getEndDate()) : 1;
	            }
	            return -1;
	          }
	        });
	    
	    columnSortHandler.setComparator(salaryDataGrid.getColumn(3), new Comparator<SalaryInfo>() {
	          public int compare(SalaryInfo o1, SalaryInfo o2) {
	            if (o1 == o2) {
	              return 0;
	            }

	            if (o1 != null) {
	              return (o2 != null) ? o1.getType().getDescription().compareTo(o2.getType().getDescription()) : 1;
	            }
	            return -1;
	          }
	        });
	    
	    salaryDataGrid.addColumnSortHandler(columnSortHandler);

	    // We know that the data is sorted alphabetically by default.
	    salaryDataGrid.getColumn(5).setDefaultSortAscending(false);
	    salaryDataGrid.getColumnSortList().push(salaryDataGrid.getColumn(5));   
	}
	
	// --------------------------------------------------
	//					UiHandlers
	// --------------------------------------------------
	
	@UiHandler("deleteButton")
	public void onDeleteSalary(ClickEvent event) {
		workplaceSalaryObject.delete(
				selectionModel.getSelectedSet(), 
				s -> {
					setWorkplaceSalaryObject(workplaceSalaryObject);
				}, 
				f -> {}
		);
	}
	
	@UiHandler("saveButton")
	public void onPrintSalary(ClickEvent event) {
		String fileDownloadURL = GWT.getModuleBaseURL()+ "salary_exporter/";
		String query = "?type=salary&selectedSalaries=" + selectionModel.getSelectedSet().size()
	            + "&enterprise=" + ((SalaryInfo)selectionModel.getSelectedSet().toArray()[0]).getEnterpriseId()
		        ;
			
		for(int i=0; i<selectionModel.getSelectedSet().size(); i++) {
			query += "&salary"+i+"Id=" + ((SalaryInfo)selectionModel.getSelectedSet().toArray()[i]).getId();
		}
		
		query += "&name=salaries.pdf";
		
		String paramsBase64 = b64decode(query);
		
		Window.open(fileDownloadURL+paramsBase64, "_blank", null);
	}

	private static native String b64decode(String a) /*-{
	  return window.btoa(a);
	}-*/;
	
	@UiHandler("publishButton")
	public void onPublichalary(ClickEvent event) {
		onPublish();
	}
	
	@UiHandler("emailEnterpriseButton")
	public void onEmailSalary(ClickEvent event) {
		Integer enterpriseID = ((SalaryInfo)selectionModel.getSelectedSet().toArray()[0]).getEnterpriseId();
		
		// PARAMS TO DOWNLOAD PAYROLLS
		String url = GWT.getModuleBaseURL()+ "salary_exporter/";
		String query = "?type=salary&selectedSalaries=" + selectionModel.getSelectedSet().size()
	            + "&enterprise=" + ((SalaryInfo)selectionModel.getSelectedSet().toArray()[0]).getEnterpriseId();
			
		for(int i=0; i<selectionModel.getSelectedSet().size(); i++) {
			query += "&salary"+i+"Id=" + ((SalaryInfo)selectionModel.getSelectedSet().toArray()[i]).getId();
		}
		
		query += "&name=salaries.pdf";
		
		String paramsBase64 = b64decode(query);
		
		//Complete URL
		url += paramsBase64;
		
		// DIALOG TO SEND EMAIL
		PayrollEmailToEnterpriseDialog dialog = new PayrollEmailToEnterpriseDialog(enterpriseID, url) {
			
			@Override
			protected void onAccept() {
				if(null == this.getFromMAilAccount()) {
					WarningDialog warning = new WarningDialog("AVISO", "No existe cuenta de correo desde la que enviar este mensaje.");
					warning.center();
					warning.show();
				} else {
					String from = this.getFromMAilAccount().getId().toString();
					String to = this.getSendTo();
					String cc = this.getCC();
					String cco = this.getCCO();
					String bodyHTML = this.getBody();
					
					workplaceSalaryObject.sendPayrollEmail(from, to, cc, cco, bodyHTML,
						s -> {
							WarningDialog warning = new WarningDialog("AVISO", workplaceSalaryObject.getEmailStatus());
							warning.center();
							warning.show();
							hide();
						},f -> {}
					);
				}
			}
		};
		
		dialog.center();
		dialog.show();
	}
	
	@UiHandler("emailEmployeesButton")
	public void onEmailEmployeesSalary(ClickEvent event) {
		Integer enterpriseID = ((SalaryInfo)selectionModel.getSelectedSet().toArray()[0]).getEnterpriseId();
		
		// PARAMS TO DOWNLOAD PAYROLLS
		String query = "?type=salary&selectedSalaries=" + selectionModel.getSelectedSet().size()
	            + "&enterprise=" + ((SalaryInfo)selectionModel.getSelectedSet().toArray()[0]).getEnterpriseId();
			
		for(int i=0; i<selectionModel.getSelectedSet().size(); i++) {
			query += "&salary"+i+"Id=" + ((SalaryInfo)selectionModel.getSelectedSet().toArray()[i]).getId();
		}
		
		query += "&name=salaries.pdf";
		
		String paramsBase64 = b64decode(query);
		
		final String url = GWT.getModuleBaseURL()+ "salary_exporter/" + paramsBase64;
		
		// CHECK SELECTED EMPLOYEES EMAILS
		workplaceSalaryObject.checkEmployeesEmails(
				selectionModel.getSelectedSet(), 
				s -> {
					if(workplaceSalaryObject.getCheckEmailEmployeesStatus().length() != 0) {
						WarningDialog warningDialog = new WarningDialog("REVISAR EMAILS", workplaceSalaryObject.getCheckEmailEmployeesStatus());
						warningDialog.center();
						warningDialog.show();
					} else {
						PayrollEmailToEmployeesDialog dialog = new PayrollEmailToEmployeesDialog(enterpriseID, url) {
							
							@Override
							protected void onAccept() {
								
								if(null == this.getFromMAilAccount()) {
									WarningDialog warning = new WarningDialog("AVISO", "No existe cuenta de correo desde la que enviar este mensaje.");
									warning.center();
									warning.show();
								} else {
									String from = this.getFromMAilAccount().getId().toString();
									String cc = this.getCC();
									String cco = this.getCCO();
									String bodyHTML = this.getBody();
									
									workplaceSalaryObject.sendPayrollEmailToEmployees(from, cc, cco, bodyHTML, url,
										s -> {
											WarningDialog warning = new WarningDialog("AVISO", workplaceSalaryObject.getEmailStatus());
											warning.center();
											warning.show();
											hide();
										},f -> {}
									);
								}
							}
						}; 
						
						dialog.center();
						dialog.show();
					}
				}, 
				f -> {}
		);
		
	}
	
	@UiHandler("collapsePanel")
	public void onOpenPanel(OpenEvent<DisclosurePanel> event) {
		mainTablePanel.getElement().getStyle().setHeight(575, Unit.PX);
		salaryDataGrid.redraw();
	}
	
	@UiHandler("collapsePanel")
	public void onClosePanel(CloseEvent<DisclosurePanel> event) {
		mainTablePanel.getElement().getStyle().setHeight(725, Unit.PX);
		salaryDataGrid.redraw();
	}
	
	@UiHandler("employeeSB")
	public void onFilterEmployee(ValueChangeEvent<String> event) {
		if(!StringUtils.isBlank(event.getValue()) || event.getValue().length() == 0)
			filterButton.click();
	}
	
	@UiHandler("typeList")
	public void onFilterTypeChange(ChangeEvent event) {
		filterButton.click();
	}
	
	@UiHandler("noDateRB")
	public void onNoDateRBCahnge(ValueChangeEvent<Boolean> event) {
		if(event.getValue()) {
			monthTillT.setEnabled(false);
			yearTillT.setEnabled(false);
			monthTTo.setEnabled(false);
			yearTTo.setEnabled(false);
			filterButton.click();
		}
	}
	
	@UiHandler("dateTTRB")
	public void onDateTTRBCahnge(ValueChangeEvent<Boolean> event) {
		if(event.getValue()) {
			monthTillT.setEnabled(true);
			yearTillT.setEnabled(true);
			monthTTo.setEnabled(true);
			yearTTo.setEnabled(true);
			filterButton.click();
		}
	}
	
	@UiHandler({"monthTillT", "yearTillT", "monthTTo", "yearTTo"})
	public void onFilterDatesChange(ChangeEvent event) {
		filterButton.click();
	}
	
	@UiHandler("filterButton")
	public void onFilterButtonClick(ClickEvent event) {
		SalaryInfoFilter filter = this.workplaceSalaryObject.getFilter();
		if(noDateRB.getValue()) {
			filter.setNoDateFilter(true);
			filter.setDateMYFilter(false);
			filter.setDateTTFilter(false);
		} else if(dateTTRB.getValue()) {
			filter.setNoDateFilter(false);
			filter.setDateMYFilter(false);
			filter.setDateTTFilter(true);
			Integer yearTillTValue = Integer.parseInt(yearTillT.getSelectedValue()) - 1900;
			Integer monthTillTValue = monthTillT.getSelectedIndex();
			filter.setDateTillT(new Date(yearTillTValue, monthTillTValue, 1));
			Integer yearTToValue = Integer.parseInt(yearTTo.getSelectedValue()) - 1900;
			Integer monthTToValue = monthTTo.getSelectedIndex();
			filter.setDateTTo(new Date(yearTToValue, monthTToValue, 1));
		}
		
		// Salary Type
		Integer salaryType = getSalaryType(this.typeList.getSelectedIndex());
		filter.setSalaryType(salaryType);
		
		//Check if exist employee filter
		String nameSurname = this.employeeSB.getValue();
		String name = nameSurname.split(", ")[0];
		String surname = nameSurname.split(", ")[1];
		if(nameSurname.length() > 0) {
			EmployeeInfo employeeInfo = this.workplaceSalaryObject.getEmployeeDataByNameSurname(name, surname);
			filter.setEmployeeId(employeeInfo.getEmployeeId());
			filter.setWorkplaceId(null);
			filter.setEnterpriseId(null);
		}else
			filter.setEmployeeId(null);
		
		
		this.workplaceSalaryObject.getFilterSalariesDB(
				s -> {
					initSalariesTable();
				}, f -> { }
		);

	}
	
	// --------------------------------------------------
	//					Aux Methods
	// --------------------------------------------------
	
	@Override
	public void onContextMenu(ContextMenuEvent event) {
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}
	
	void onPublish() {
		for (Listener listener : listeners)
			for(SalaryInfo salary : selectionModel.getSelectedSet())
			listener.onPublishSalaries(salary);
	}
	
	private Integer getSalaryType(int selectedIndex) {
		switch (selectedIndex) {
		case 1:
			return 0;
		case 2:
			return 1;
		case 3:
			return 3;
		case 4:
			return 2;
		default:
			return null;
		}
	}

	public void hideEnterpriseSiteButtons() {
		deleteButton.getElement().getStyle().setDisplay(Display.NONE);
		emailEnterpriseButton.getElement().getStyle().setDisplay(Display.NONE);
	}

}
