package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfo;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfoFilter;
import com.esferalia.aon.gwt.payroll.shared.StringUtils;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.SimplePager;
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
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;

public class WorkplaceSalary extends Composite implements ContextMenuHandler {

	private static EmployeeSalaryUiBinder uiBinder = GWT.create(EmployeeSalaryUiBinder.class);

	interface EmployeeSalaryUiBinder extends UiBinder<Widget, WorkplaceSalary> {
	}
	
	//Listener to Publish Salaries
	static interface Listener {
		void onPublishSalaries(SalaryInfo salary);
	}
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String tableStyle();
		String mAuto();
		String hide();
		String suggestBox();
	}
	
	@UiField
	HTMLPanel mainTablePanel;
	
	@UiField
	Button deleteButton;
	
	@UiField
	Button saveButton;
	
	@UiField
	Button publishButton;
	
	@UiField
	HTMLPanel mainContainer;
	
	@UiField
	Label workplaceNameLabel;

	@UiField
	DisclosurePanel collapsePanel;
	
	@UiField
	SuggestBox employeeSB;
	
	@UiField
	RadioButton noDateRB;
	
	@UiField
	RadioButton dateMYRB;
	
	@UiField
	ListBox monthMY;
	
	@UiField
	ListBox yearMY;
	
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

	
	public WorkplaceSalary() {
				
		//Inicializamos la vista del calendario
		initWidget(uiBinder.createAndBindUi(this)); 
		
		listeners = new LinkedList<Listener>();
		
	}
	
	private void initListBox() {
		// Clear listboxies
		monthMY.clear();
		yearMY.clear();
		monthTillT.clear();
		yearTillT.clear();
		monthTTo.clear();
		yearTTo.clear();
		
		// Add months to listboxes
		String[] monthList = new String[] {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
		for(String month : monthList) {
			monthMY.addItem(month);
			monthTillT.addItem(month);
			monthTTo.addItem(month);
		}
		
		// Add year to listboxes
		Integer actualYear = new Date().getYear() + 1900;
		Integer firstPayroll = this.workplaceSalaryObject.getWorkplaceSalaries().get(this.workplaceSalaryObject.getWorkplaceSalaries().size()-1).getStartDate().getYear() + 1900;
		Integer diffYears = actualYear - firstPayroll;
		for(int i = 0; i <= diffYears; i++) {
			yearMY.addItem((actualYear - i)+"");
			yearTillT.addItem((actualYear - i)+"");
			yearTTo.addItem((actualYear - i)+"");
		}
	}

	private WorkplaceSalaryObject workplaceSalaryObject;
	private MultiSelectionModel<SalaryInfo> selectionModel;
	private List<Listener> listeners;
	
	public void setWorkplaceSalaryObject(WorkplaceSalaryObject workplaceSalaryObject) {
		this.workplaceSalaryObject = workplaceSalaryObject;
		this.workplaceSalaryObject.getWorkplaceSalariesDB(
				s -> {
					resetPage();
					if(!this.workplaceSalaryObject.getWorkplaceSalaries().isEmpty()) {
						initListBox();
						initSuggestBox();
						this.noDateRB.setValue(true, true);
						initSalariesTable();
					} else {
						// Hide everything
						mainContainer.addStyleName(style.hide());
						
						//Hide buttons
						this.deleteButton.setVisible(false);
						this.saveButton.setVisible(false);
						this.publishButton.setVisible(false);
						
						// Show warning dialog
						WarningDialog warningDialog = new WarningDialog("Aviso", "No existen nominas para este empleado.");
						warningDialog.center();
						warningDialog.show();
						return;
					}
				}, 
				f -> {}
		);
	}

	private void initSuggestBox() {
		//NAMES
		List<String> employeesNames = this.workplaceSalaryObject.getWorkplaceEmployees().getWorkplaceEmployeesName();
		List<String> employeesNamesSuggest = new ArrayList<String>();
		for(String name : employeesNames)
			employeesNamesSuggest.add(name+"");
		MultiWordSuggestOracle orclNames = (MultiWordSuggestOracle) this.employeeSB.getSuggestOracle();
		orclNames.addAll(employeesNamesSuggest);
		this.employeeSB.setAutoSelectEnabled(false);
		this.employeeSB.addStyleName(style.suggestBox());
		this.employeeSB.setValue("");
	}

	private void resetPage() {
		if(!this.workplaceSalaryObject.getWorkplaceSalaries().isEmpty()) {
			this.collapsePanel.setOpen(false);
			mainTablePanel.clear();
		}
	}

	private void initSalariesTable() {
		if(this.workplaceSalaryObject.getWorkplaceSalaries().isEmpty()) {
			//Hide buttons
			this.deleteButton.setVisible(false);
			this.saveButton.setVisible(false);
			this.publishButton.setVisible(false);
			
			// Show warning dialog
			WarningDialog warningDialog = new WarningDialog("Aviso", "No existen nominas para este empleado. Si ha filtrado la informacion por favor revise los parametros.");
			warningDialog.center();
			warningDialog.show();
			return;
		}
		
		// Show everything
		mainContainer.removeStyleName(style.hide());
		
		//Show buttons
		this.deleteButton.setVisible(true);
		this.saveButton.setVisible(true);
		this.publishButton.setVisible(true);
		
		//Disable buttons till any salary selected
		deleteButton.setEnabled(false);
		saveButton.setEnabled(false);
		publishButton.setEnabled(false);
		
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
	    for (SalaryInfo salary : this.workplaceSalaryObject.getWorkplaceSalaries()) {
	    	salaryList.add(salary);
	    }
	    
	    // Add rest of columns and activate sorteable if its needed
	    addColumns(table, salaryList);
	    
	    // Create a SimplePager.
	    SimplePager pager = new SimplePager();

	    // Set the cellList as the display.
	    pager.setDisplay(table);
	    
	    //Workplace Name Title
	    workplaceNameLabel.setText(this.workplaceSalaryObject.getWorkplaceSalaries().get(0).getWorkplaceName());
	    
	    // Add the pager and list to the page.
	    VerticalPanel vPanel = new VerticalPanel();
	    vPanel.add(table);
	    vPanel.add(pager);
	    
	    // Add Styles
	    vPanel.addStyleName(style.tableStyle());
	    pager.addStyleName(style.mAuto());
		
	    mainTablePanel.add(vPanel);
	}

	private void addSelectionModel(CellTable<SalaryInfo> table) {
		
		this.selectionModel = new MultiSelectionModel<SalaryInfo>(SalaryInfo.KEY_PROVIDER);
	    table.setSelectionModel(selectionModel, DefaultSelectionEventManager.<SalaryInfo> createCheckboxManager());
	    
	    this.selectionModel.addSelectionChangeHandler(new Handler() {
	        
	        @Override
	        public void onSelectionChange(SelectionChangeEvent event) {
	            if(selectionModel.getSelectedSet().size() > 0) {
	            	deleteButton.setEnabled(true);
	            	saveButton.setEnabled(true);
	            	publishButton.setEnabled(true);
	            }else {
	            	deleteButton.setEnabled(false);
	            	saveButton.setEnabled(false);
	            	publishButton.setEnabled(false);
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
	        boolean value = selectionModel.getSelectedSet().size() == workplaceSalaryObject.getWorkplaceSalaries().size();
	        return value; 
	      }
	    };
	    
	    selectAllHeader.setUpdater(new ValueUpdater<Boolean>(){
	      @Override
	      public void update(Boolean value)
	      {
	        // Select/deselect all persons
	        for (SalaryInfo person : workplaceSalaryObject.getWorkplaceSalaries())
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
	    
//	    // Create workplace name column.
//	    TextColumn<SalaryInfo> workplaceNameColumn = new TextColumn<SalaryInfo>() {
//	      @Override
//	      public String getValue(SalaryInfo salaryInfo) {
//	        return salaryInfo.getWorkplaceName();
//	      }
//	    };
//
//	    // Make the workplace name column sortable.
//	    workplaceNameColumn.setSortable(true);
//	    
//	    // Create enterprise name column.
//	    TextColumn<SalaryInfo> enterpriseNameColumn = new TextColumn<SalaryInfo>() {
//	      @Override
//	      public String getValue(SalaryInfo salaryInfo) {
//	        return salaryInfo.getEnterpriseName();
//	      }
//	    };
	    
	    // Create start date column.
	    TextColumn<SalaryInfo> startDateColumn = new TextColumn<SalaryInfo>() {
	      @Override
	      public String getValue(SalaryInfo salaryInfo) {
	    	  Date endDate = salaryInfo.getStartDate();
	    	  String year = (endDate.getYear() + 1900) + "";
	    	  String month = StringUtils.leftPad((endDate.getMonth() + 1) + "", 2, '0');
	    	  String date = StringUtils.leftPad((endDate.getDate()) + "", 2, '0');
	    	  return date+"/"+month+"/"+year;
	      }
	    };

	    // Make the  start date column sortable.
	    startDateColumn.setSortable(true);
	    
	    // Create end date column.
	    TextColumn<SalaryInfo> endtDateColumn = new TextColumn<SalaryInfo>() {
	      @Override
	      public String getValue(SalaryInfo salaryInfo) {
	    	  Date endDate = salaryInfo.getEndDate();
	    	  String year = (endDate.getYear() + 1900) + "";
	    	  String month = StringUtils.leftPad((endDate.getMonth() + 1) + "", 2, '0');
	    	  String date = StringUtils.leftPad((endDate.getDate()) + "", 2, '0');
	    	  return date+"/"+month+"/"+year;
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
	    
	    // Make the type column sortable.
	    typeColumn.setSortable(true);
	    
	    // Create total payment column.
	    TextColumn<SalaryInfo> totalPaymentColumn = new TextColumn<SalaryInfo>() {
	      @Override
	      public String getValue(SalaryInfo salaryInfo) {
	        return (Math.round(salaryInfo.getTotalPayment() * 100d) / 100d)+"";
	      }
	    };
	    
	    totalPaymentColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
	    
	    // Create total deduction column.
	    TextColumn<SalaryInfo> totalDeductionColumn = new TextColumn<SalaryInfo>() {
	      @Override
	      public String getValue(SalaryInfo salaryInfo) {
	        return (Math.round(salaryInfo.getTotalDecuction() * 100d) / 100d)+"";
	      }
	    };
	    
	    totalDeductionColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
	    
	    // Create total liquid column.
	    TextColumn<SalaryInfo> totalLiquidColumn = new TextColumn<SalaryInfo>() {
	      @Override
	      public String getValue(SalaryInfo salaryInfo) {
	        return (Math.round(salaryInfo.getTotalLiquid() * 100d) / 100d)+" "+String.valueOf("\u20AC");
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
					sb.appendHtmlConstant("<button type=\"button\" class=\"aon-finding-toolbar-item aon-icon-edit\" style=\"border: none !important;\"></button>");
				}
			}
		};
		
		draftColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
	    
	    // Add the columns.
	    table.addColumn(employeeNameColumn, "Empleado");
//	    table.addColumn(enterpriseNameColumn, "Empresa");
//	    table.addColumn(workplaceNameColumn, "C. Trabajo");
	    
	    table.addColumn(typeColumn, "Tipo");
	    
	    table.addColumn(startDateColumn, "F. Inicio");
	    table.addColumn(endtDateColumn, "F. Fin");
	    
	    table.addColumn(totalPaymentColumn, "Bruto");
	    table.addColumn(totalDeductionColumn, "Deducciones");
	    table.addColumn(totalLiquidColumn, "L"+String.valueOf("\u00ED")+"quido");
	    
	    table.addColumn(draftColumn, "Borrador");
	      
	    // Add a ColumnSortEvent.ListHandler to connect sorting to the java.util.List.
	    ListHandler<SalaryInfo> columnSortHandler = new ListHandler<SalaryInfo>(salaryList);
	    columnSortHandler.setComparator(employeeNameColumn, new Comparator<SalaryInfo>() {
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
	    
//	    columnSortHandler.setComparator(workplaceNameColumn, new Comparator<SalaryInfo>() {
//	          public int compare(SalaryInfo o1, SalaryInfo o2) {
//	            if (o1 == o2) {
//	              return 0;
//	            }
//
//	            if (o1 != null) {
//	              return (o2 != null) ? o1.getWorkplaceName().compareTo(o2.getWorkplaceName()) : 1;
//	            }
//	            return -1;
//	          }
//	        });
	    
	    columnSortHandler.setComparator(startDateColumn, new Comparator<SalaryInfo>() {
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
	    
	    columnSortHandler.setComparator(endtDateColumn, new Comparator<SalaryInfo>() {
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
	    
	    columnSortHandler.setComparator(typeColumn, new Comparator<SalaryInfo>() {
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
	    
	    table.addColumnSortHandler(columnSortHandler);

	    // We know that the data is sorted alphabetically by default.
	    endtDateColumn.setDefaultSortAscending(false);
	    table.getColumnSortList().push(endtDateColumn);
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
		String fileDownloadURL = GWT.getModuleBaseURL()+ "salary/"
	            + "?selectedSalaries=" + selectionModel.getSelectedSet().size()
	            + "&enterprise=" + ((SalaryInfo)selectionModel.getSelectedSet().toArray()[0]).getEnterpriseId()
		        ;
			
		for(int i=0; i<selectionModel.getSelectedSet().size(); i++) {
			fileDownloadURL += "&salary"+i+"Id=" + ((SalaryInfo)selectionModel.getSelectedSet().toArray()[i]).getId();
		}
		
		fileDownloadURL += "&name=salaries.pdf";
		
//		Window.alert(fileDownloadURL);
		
		Window.open(fileDownloadURL, "_blank", null);
	}
	
	@UiHandler("publishButton")
	public void onPublichalary(ClickEvent event) {
		onPublish();
	}
	
	@UiHandler("noDateRB")
	public void onNoDateRBCahnge(ValueChangeEvent<Boolean> event) {
		if(event.getValue()) {
			monthMY.setEnabled(false);
			yearMY.setEnabled(false);
			monthTillT.setEnabled(false);
			yearTillT.setEnabled(false);
			monthTTo.setEnabled(false);
			yearTTo.setEnabled(false);
		}
	}
	
	@UiHandler("dateMYRB")
	public void onDateMYRBCahnge(ValueChangeEvent<Boolean> event) {
		if(event.getValue()) {
			monthMY.setEnabled(true);
			yearMY.setEnabled(true);
			monthTillT.setEnabled(false);
			yearTillT.setEnabled(false);
			monthTTo.setEnabled(false);
			yearTTo.setEnabled(false);
		}
	}
	
	@UiHandler("dateTTRB")
	public void onDateTTRBCahnge(ValueChangeEvent<Boolean> event) {
		if(event.getValue()) {
			monthMY.setEnabled(false);
			yearMY.setEnabled(false);
			monthTillT.setEnabled(true);
			yearTillT.setEnabled(true);
			monthTTo.setEnabled(true);
			yearTTo.setEnabled(true);
		}
	}
	
	@UiHandler("filterButton")
	public void onFilterButtonClick(ClickEvent event) {
		SalaryInfoFilter filter = this.workplaceSalaryObject.getFilter();
		if(noDateRB.getValue()) {
			filter.setNoDateFilter(true);
			filter.setDateMYFilter(false);
			filter.setDateTTFilter(false);
		} else if(dateMYRB.getValue()) {
			filter.setNoDateFilter(false);
			filter.setDateMYFilter(true);
			Integer year = Integer.parseInt(yearMY.getSelectedValue()) - 1900;
			Integer month = monthMY.getSelectedIndex();
			filter.setDateMY(new Date(year, month, 1));
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
		
		//Check if exist employee filter
		String nameSurname = this.employeeSB.getValue();
		String name = nameSurname.split(", ")[0];
		String surname = nameSurname.split(", ")[1];
		EmployeeInfo employeeInfo = null;
		if(nameSurname.length() > 0)
			employeeInfo = this.workplaceSalaryObject.getEmployeeDataByNameSurname(name, surname);
		
		// Choose method filter
		if(null == employeeInfo){
			this.workplaceSalaryObject.getFilterWorkplaceSalariesDB(
					s -> {
						resetPage();
						initSalariesTable();
					}, f -> { }
			);
		} else{
			this.workplaceSalaryObject.getFilterWorkplaceEmployeeSalariesDB(
					employeeInfo.getEmployeeId(), // ContractId in this case
					s -> {
						resetPage();
						initSalariesTable();
					}, f -> { }
			);
		}
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

}
