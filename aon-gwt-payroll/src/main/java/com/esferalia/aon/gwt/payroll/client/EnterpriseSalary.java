package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfo;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfoFilter;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
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
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
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

public class EnterpriseSalary extends Composite implements ContextMenuHandler {

	private static EmployeeSalaryUiBinder uiBinder = GWT.create(EmployeeSalaryUiBinder.class);

	interface EmployeeSalaryUiBinder extends UiBinder<Widget, EnterpriseSalary> {
	}
	
	// Cell Table Resource for redifine Style
	public interface CellTableResource extends CellTable.Resources
	{
	   public interface CellTableStyle extends CellTable.Style {};

	   @Source({"SalaryCellTable.css"})
	   CellTableStyle cellTableStyle();
	}; 
	
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
	Button emailButton;
	
	@UiField
	HTMLPanel mainContainer;
	
	@UiField
	Label workplaceNameLabel;

	@UiField
	DisclosurePanel collapsePanel;
	
	@UiField
	RadioButton allRB;
	
	@UiField
	RadioButton workplaceRB;
	
	@UiField
	SuggestBox workplaceSB;
	
	@UiField
	RadioButton employeeRB;
	
	@UiField
	SuggestBox employeeSB;
	
	@UiField
	ListBox typeList;
	
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

	
	public EnterpriseSalary() {
				
		//Inicializamos la vista del calendario
		initWidget(uiBinder.createAndBindUi(this)); 
		
		listeners = new LinkedList<Listener>();
		
	}
	
	private void initListBox() {
		// Clear listboxies
		typeList.clear();
		monthMY.clear();
		yearMY.clear();
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
			monthMY.addItem(month);
			monthTillT.addItem(month);
			monthTTo.addItem(month);
		}
		
		// Add year to listboxes
		Integer actualYear = new Date().getYear() + 1900;
		Integer firstPayroll = this.enterpriseSalaryObject.getEnterpriseSalaries().get(this.enterpriseSalaryObject.getEnterpriseSalaries().size()-1).getStartDate().getYear() + 1900;
		Integer diffYears = actualYear - firstPayroll;
		for(int i = 0; i <= diffYears; i++) {
			yearMY.addItem((actualYear - i)+"");
			yearTillT.addItem((actualYear - i)+"");
			yearTTo.addItem((actualYear - i)+"");
		}
	}

	private EnterpriseSalaryObject enterpriseSalaryObject;
	private MultiSelectionModel<SalaryInfo> selectionModel;
	private List<Listener> listeners;
	private DateTimeFormat formatFullDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	public void setEnterpriseSalaryObject(EnterpriseSalaryObject enterpriseSalaryObject) {
		this.enterpriseSalaryObject = enterpriseSalaryObject;
		this.enterpriseSalaryObject.getEnterpriseSalariesDB(
				s -> {
					resetPage();
					if(!this.enterpriseSalaryObject.getEnterpriseSalaries().isEmpty()) {
						initListBox();
						initSuggestBox();
						this.noDateRB.setValue(true, true);
						initSalariesTable();
					} else {
						// Hide everything
						mainContainer.addStyleName(style.hide());
						this.emailButton.removeStyleName(style.hide());
						
						//Hide buttons
						this.deleteButton.setVisible(false);
						this.saveButton.setVisible(false);
						this.publishButton.setVisible(false);
						this.emailButton.setVisible(false);
						
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
		List<String> employeesNames = this.enterpriseSalaryObject.getEnterpriseEmployeesName();
		List<String> employeesNamesSuggest = new ArrayList<String>();
		for(String name : employeesNames)
			employeesNamesSuggest.add(name+"");
		MultiWordSuggestOracle orclNames = (MultiWordSuggestOracle) this.employeeSB.getSuggestOracle();
		orclNames.clear();
		orclNames.addAll(employeesNamesSuggest);
		this.employeeSB.setAutoSelectEnabled(false);
		this.employeeSB.addStyleName(style.suggestBox());
		this.employeeSB.setEnabled(false);
		this.employeeSB.setValue("");
		this.employeeRB.setValue(false);
		
		//NAMES
		List<String> workplaceNames = this.enterpriseSalaryObject.getWorkplacesNames();
		List<String> workplaceNamesSuggest = new ArrayList<String>();
		for(String name : workplaceNames)
			workplaceNamesSuggest.add(name+"");
		MultiWordSuggestOracle orclWorkplaceNames = (MultiWordSuggestOracle) this.workplaceSB.getSuggestOracle();
		orclWorkplaceNames.clear();
		orclWorkplaceNames.addAll(workplaceNamesSuggest);
		this.workplaceSB.setAutoSelectEnabled(false);
		this.workplaceSB.addStyleName(style.suggestBox());
		this.workplaceSB.setEnabled(false);
		this.workplaceSB.setValue("");
		this.workplaceRB.setValue(false);
	}

	private void resetPage() {
		if(!this.enterpriseSalaryObject.getEnterpriseSalaries().isEmpty()) {
			this.collapsePanel.setOpen(false);
			mainTablePanel.clear();
		}
	}

	private void initSalariesTable() {
		if(this.enterpriseSalaryObject.getEnterpriseSalaries().isEmpty()) {
			//Hide buttons
			this.deleteButton.setVisible(false);
			this.saveButton.setVisible(false);
			this.publishButton.setVisible(false);
			this.emailButton.setVisible(false);
			
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
		this.emailButton.setVisible(true);
		
		//Disable buttons till any salary selected
		deleteButton.setEnabled(false);
		saveButton.setEnabled(false);
		publishButton.setEnabled(false);
		emailButton.setEnabled(false);
		
		// Resource Style CellTable
		CellTableResource resource = GWT.create(CellTableResource.class);
		
		// Create a CellTable.
	    CellTable<SalaryInfo> table = new CellTable<SalaryInfo>(20, resource, SalaryInfo.KEY_PROVIDER);
	   
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
	    for (SalaryInfo salary : this.enterpriseSalaryObject.getEnterpriseSalaries()) {
	    	salaryList.add(salary);
	    }
	    
	    // Add rest of columns and activate sorteable if its needed
	    addColumns(table, salaryList);
	    
	    // Create a SimplePager.
	    SimplePager pager = new SimplePager(TextLocation.CENTER, false, false);

	    // Set the cellList as the display.
	    pager.setDisplay(table);
	    
	    //Workplace Name Title
	    workplaceNameLabel.setText(this.enterpriseSalaryObject.getEnterpriseSalaries().get(0).getEnterpriseName());
	    
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
	            	emailButton.setEnabled(true);
	            }else {
	            	deleteButton.setEnabled(false);
	            	saveButton.setEnabled(false);
	            	publishButton.setEnabled(false);
	            	emailButton.setEnabled(false);
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
	        boolean value = selectionModel.getSelectedSet().size() == enterpriseSalaryObject.getEnterpriseSalaries().size();
	        return value; 
	      }
	    };
	    
	    selectAllHeader.setUpdater(new ValueUpdater<Boolean>(){
	      @Override
	      public void update(Boolean value)
	      {
	        // Select/deselect all persons
	        for (SalaryInfo person : enterpriseSalaryObject.getEnterpriseSalaries())
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
	    TextColumn<SalaryInfo> endtDateColumn = new TextColumn<SalaryInfo>() {
	      @Override
	      public String getValue(SalaryInfo salaryInfo) {
	    	  return formatFullDate.format(salaryInfo.getEndDate());
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
					sb.appendHtmlConstant("<button type=\"button\" class=\"aon-finding-toolbar-item aon-icon-edit\" style=\"border: none !important;\"></button>");
				}
			}
		};
		
		draftColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
	    
	    // Add the columns.
	    table.addColumn(employeeNameColumn, "Empleado");
	    table.addColumn(workplaceNameColumn, "C. Trabajo");
	    
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
	    
	    columnSortHandler.setComparator(workplaceNameColumn, new Comparator<SalaryInfo>() {
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
		enterpriseSalaryObject.delete(
				selectionModel.getSelectedSet(), 
				s -> {
					setEnterpriseSalaryObject(enterpriseSalaryObject);
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
	
	@UiHandler("emailButton")
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
		
		url += paramsBase64;
		
		// DIALOG TO SEND EMAIL
		PayrollEmailDialog dialog = new PayrollEmailDialog(enterpriseID, url) {
			
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
					
					enterpriseSalaryObject.sendPayrollEmail(from, to, cc, cco, bodyHTML,
						s -> {
							WarningDialog warning = new WarningDialog("AVISO", enterpriseSalaryObject.getEmailStatus());
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
	
	@UiHandler("allRB")
	public void onAllRBCahnge(ValueChangeEvent<Boolean> event) {
		if(event.getValue()) {
			workplaceSB.setEnabled(false);
			employeeSB.setEnabled(false);
			employeeSB.setValue("");
			workplaceSB.setValue("");
		}
	}
	
	@UiHandler("workplaceRB")
	public void onworkplaceRBCahnge(ValueChangeEvent<Boolean> event) {
		if(event.getValue()) {
			workplaceSB.setEnabled(true);
			employeeSB.setEnabled(false);
			employeeSB.setValue("");
		}
	}
	
	@UiHandler("employeeRB")
	public void onEmployeeRBCahnge(ValueChangeEvent<Boolean> event) {
		if(event.getValue()) {
			employeeSB.setEnabled(true);
			workplaceSB.setEnabled(false);
			workplaceSB.setValue("");
		}
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
		SalaryInfoFilter filter = this.enterpriseSalaryObject.getFilter();
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
		
		// Salary Type
		Integer salaryType = getSalaryType(this.typeList.getSelectedIndex());
		filter.setSalaryType(salaryType);
		
		EmployeeInfo employeeInfo = null;
		Workplace workplaceInfo = null;
		if(allRB.getValue()) {
			filter.setEmployeeId(null);
			filter.setWorkplaceId(null);
			filter.setEnterpriseId(this.enterpriseSalaryObject.getEnterpriseId());
		} else if(employeeRB.getValue()) {
			//Check if exist employee filter
			String nameSurname = this.employeeSB.getValue();
			String name = nameSurname.split(", ")[0];
			String surname = nameSurname.split(", ")[1];
			if(nameSurname.length() > 0) {
				employeeInfo = this.enterpriseSalaryObject.getEmployeeDataByNameSurname(name, surname);
				filter.setEmployeeId(employeeInfo.getEmployeeId());
				filter.setWorkplaceId(null);
				filter.setEnterpriseId(null);
			}else
				filter.setEmployeeId(null);
		} else if (workplaceRB.getValue()) {
			String workplaceDescription = this.workplaceSB.getValue();
			if(workplaceDescription.length() > 0) {
				workplaceInfo = this.enterpriseSalaryObject.getWorkplaceByDescription(workplaceDescription);
				filter.setWorkplaceId(workplaceInfo.getId());
				filter.setEmployeeId(null);
				filter.setEnterpriseId(null);
			}else
				filter.setWorkplaceId(null);
		}
		
		this.enterpriseSalaryObject.getFilterSalariesDB(
				s -> {
					resetPage();
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

}
