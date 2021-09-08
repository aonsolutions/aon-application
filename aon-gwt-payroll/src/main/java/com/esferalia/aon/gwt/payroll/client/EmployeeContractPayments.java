package com.esferalia.aon.gwt.payroll.client;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.ContractConceptCalc;
import com.esferalia.aon.gwt.payroll.shared.ContractConceptCalc.ContractConceptCalcType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class EmployeeContractPayments extends Composite {

	// ----------------------------------------------- UiBinder 
	
	private static EmployeeContractPaymentsUiBinder uiBinder = GWT.create(EmployeeContractPaymentsUiBinder.class);

	interface EmployeeContractPaymentsUiBinder extends UiBinder<Widget, EmployeeContractPayments> {}
	
	// ----------------------------------------------- ContractConceptCalcTypeCell 
	
	static class ContractConceptCalcTypeCell extends AbstractCell<String> {
	   
		interface Templates extends SafeHtmlTemplates {
	      @SafeHtmlTemplates.Template("<div style=\"{0}\">{1}</div>")
	      SafeHtml cell(SafeStyles styles, SafeHtml value);
	    }
	    
	    @Override
	    public void render(Context context, String value, SafeHtmlBuilder sb) {
	      if (value == null)
	        return;
	      
	      sb.appendHtmlConstant("<b title=\"" + getContractConceptCalcType(value) + "\">" + value + "</b>");
	    }
	    
	    static String getContractConceptCalcType(String contractConceptCalcTypeShort) {
			switch (contractConceptCalcTypeShort) {
				case "P":
					return "Pagos";
				case "D":
					return "Deducci\u00F3nes";
				case "B":
					return "Bonificaciones";
				case "C":
					return "Costes";
				default:
					return "N/D";
			}
		}
	  }
	
	// ----------------------------------------------- UiField 
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {}
	
	@UiField
	DockLayoutPanel dockLayoutPanel;
	
	@UiField
	HTMLPanel mainPanel;
	
	@UiField(provided = true)
	DataGrid<ContractConceptCalc> contractConceptCalcDG;
	
	// ----------------------------------------------- Variables 
	
	private EmployeeContractPaymentsObject employeeContractPaymentsObject;
	private List<ContractConceptCalc> contractConceptCalcList;
	
	private AonToolbar toolbar;
	private AonToolbarButton saveButton;
	private ListBox yearLB;
	
	// ----------------------------------------------- Constructor 
	
	public EmployeeContractPayments() {
		initializeToolbarPanel();
		provideContractConceptCalcDG();
		initWidget(uiBinder.createAndBindUi(this));
		this.getElement().getStyle().setHeight(100, Unit.PCT);
		dockLayoutPanel.addNorth(toolbar, AonToolbar.HEIGTH);
		setScrollPanelHeight();
		saveButton.setEnabled(false);
	}
	
	// ----------------------------------------------- Auxiliar Methods (Constructor & DataGrid) 
	
	private void setScrollPanelHeight() {
		contractConceptCalcDG.setHeight(90 + "%");
		contractConceptCalcDG.setWidth(100 + "%");
	}
	
	private boolean isHideExpression(ContractConceptCalc contractConceptCalc) {
		String expression = contractConceptCalc.getExpression();
		return !AonStringUtils.isBlank(expression) && AonStringUtils.containsIgnoreCase(expression, "HIDE") && AonStringUtils.startsWithIgnoreCase(expression, "HIDE");
	}
	
	private String getContractConceptCalcTypeShort(ContractConceptCalcType contractConceptCalcType) {
		switch (contractConceptCalcType) {
			case PAYMENT:
				return "P";
			case DEDUCTION:
				return "D";
			case BONUS:
				return "B";
			case COST:
				return "C";
			default:
				return "N/D";
		}
	}
	
	private String getParsedExpression(String expression) {
		return expression.replaceAll("HIDE\\(.*\\); ", "");
	}
	
	// ----------------------------------------------- ProvideContractConceptCalcDG
	
	private void provideContractConceptCalcDG() {
		contractConceptCalcList = Collections.emptyList();
		
		// Resource Style CellTable
		contractConceptCalcDG = new CustomDataGrid<>(Integer.MAX_VALUE, ContractConceptCalc.KEY_PROVIDER);
		
		//Do not refresh the headers every time the dataGrid is updated.
		contractConceptCalcDG.setAutoHeaderRefreshDisabled(true);
		
		// Set the message to display when the table is empty.
		contractConceptCalcDG.setEmptyTableWidget(new Label(("No existen datos").toUpperCase()));
		
		// Initialize the columns.
	    addContractConceptCalcColumns();
	    
	    new ListDataProvider<ContractConceptCalc>(Collections.emptyList()).addDataDisplay(contractConceptCalcDG);
	    
	    // Add style to table header
	    addStyleToHeader();
	}
	
	public void addStyleToHeader() {
		String headerStyle = "rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header";
		contractConceptCalcDG.getHeader(0).setHeaderStyleNames(headerStyle);
		contractConceptCalcDG.getHeader(1).setHeaderStyleNames(headerStyle);
		contractConceptCalcDG.getHeader(2).setHeaderStyleNames(headerStyle);
		contractConceptCalcDG.getHeader(3).setHeaderStyleNames(headerStyle);
		contractConceptCalcDG.getHeader(4).setHeaderStyleNames(headerStyle);
		contractConceptCalcDG.getHeader(5).setHeaderStyleNames(headerStyle);
		contractConceptCalcDG.getHeader(6).setHeaderStyleNames(headerStyle);
		contractConceptCalcDG.getHeader(7).setHeaderStyleNames(headerStyle);
	}
	
	private void addContractConceptCalcColumns() {
		
		// Type columns.
		Column<ContractConceptCalc, String> typeColumn = new Column<ContractConceptCalc, String>(new ContractConceptCalcTypeCell()) {
			@Override
	        public String getValue(ContractConceptCalc contractConceptCalc) {
				return getContractConceptCalcTypeShort(contractConceptCalc.getContractConceptCalcType());
	        }
		};

		typeColumn.setSortable(true);
		typeColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		contractConceptCalcDG.setColumnWidth(typeColumn, 5, Unit.PCT);
		
		// Code columns.
		Column<ContractConceptCalc, String> codeColumn = new Column<ContractConceptCalc, String>(new TextCell()) {
			@Override
	        public String getValue(ContractConceptCalc contractConceptCalc) {
				return contractConceptCalc.getCode();
	        }
		};

		codeColumn.setSortable(true);
		codeColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		contractConceptCalcDG.setColumnWidth(codeColumn, 10, Unit.PCT);

	    // Description column.
		Column<ContractConceptCalc, String> descriptionColumn = new Column<ContractConceptCalc, String>(new EditTextCell()) {
			@Override
			public String getValue(ContractConceptCalc contractConceptCalc) {
				return contractConceptCalc.getDescription();
			}
		};
		
		descriptionColumn.setFieldUpdater((index, contractConceptCalc, description) -> {
			contractConceptCalc.setDescription(description);
	    	contractConceptCalc.setHasChange(true);
		});
		
		descriptionColumn.setSortable(true);
		contractConceptCalcDG.setColumnWidth(descriptionColumn, 20, Unit.PCT);
	    
	    // Expression column.
	    Column<ContractConceptCalc, String> expressionColumn = new Column<ContractConceptCalc, String>(new EditTextCell()) {
	    	@Override
	        public String getValue(ContractConceptCalc contractConceptCalc) {
	    		return getParsedExpression(contractConceptCalc.getExpression());
	        }
		};
		
		expressionColumn.setFieldUpdater((index, contractConceptCalc, expression) -> {
			contractConceptCalc.setExpression(expression);
	    	contractConceptCalc.setHasChange(true);
		});

	    expressionColumn.setSortable(true);
	    contractConceptCalcDG.setColumnWidth(expressionColumn, 35, Unit.PCT);
	    
	    // StartDate column.
	    Column<ContractConceptCalc, Date> startDateColumn = new Column<ContractConceptCalc, Date>(new DatePickerCell()) {
	    	@Override
	        public Date getValue(ContractConceptCalc contractConceptCalc) {
	    		return contractConceptCalc.getStartDate();
	        }
		};
		
		startDateColumn.setFieldUpdater((index, contractConceptCalc, startDate) -> {
			contractConceptCalc.setStartDate(startDate);
	    	contractConceptCalc.setHasChange(true);
	    	contractConceptCalcDG.redrawRow(index);
		});

	    startDateColumn.setSortable(true);
	    startDateColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    contractConceptCalcDG.setColumnWidth(startDateColumn, 10, Unit.PCT);
	    
	    // EndDate column.	    
	    Column<ContractConceptCalc, Date> endDateColumn = new Column<ContractConceptCalc, Date>(new DatePickerCell()) {
			@Override
			public Date getValue(ContractConceptCalc contractConceptCalc) {
				return contractConceptCalc.getEndDate();
			}
		};
		
		endDateColumn.setFieldUpdater((index, contractConceptCalc, endDate) -> {
			contractConceptCalc.setEndDate(endDate);
	    	contractConceptCalc.setHasChange(true);
	    	contractConceptCalcDG.redrawRow(index);
		});

	    endDateColumn.setSortable(true);
	    endDateColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    contractConceptCalcDG.setColumnWidth(endDateColumn, 10, Unit.PCT);
	    
	    // Visibility column.
	    ActionCell<ContractConceptCalc> visibilityActionCell = new ActionCell<>("", contractConceptCalc -> {
	    	employeeContractPaymentsObject.showHideContractConceptCalc(contractConceptCalc);
    		contractConceptCalc.setHasChange(true);
    		onSave();
	    });
	    
	    Column<ContractConceptCalc, ContractConceptCalc> visibilityColumn = new Column<ContractConceptCalc, ContractConceptCalc>(visibilityActionCell) {

			@Override
			public ContractConceptCalc getValue(ContractConceptCalc contractPayment) {
				return contractPayment;
			}
			
			@Override
			public void render(Context context, ContractConceptCalc contractConceptCalc, SafeHtmlBuilder sb) {
				if(null != contractConceptCalc) {
					if(!isHideExpression(contractConceptCalc))
						sb.appendHtmlConstant("<button type=\"button\" class=\"aon_button aon_table_button aon_icon_visibility\" style=\"border: none !important; height: 20px;\" title=\"Visible\"></button>");
					else
						sb.appendHtmlConstant("<button type=\"button\" class=\"aon_button aon_table_button aon_icon_visibility_off\" style=\"border: none !important; height: 20px;\" title=\"Oculto\"></button>");
				}
			}
		};
		
		visibilityColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		contractConceptCalcDG.setColumnWidth(visibilityColumn, 5, Unit.PCT);
	    
	    // Delete column.
	    ActionCell<ContractConceptCalc> deleteActionCell = new ActionCell<>("", contractConceptCalc -> {
	    	employeeContractPaymentsObject.deleteContractConceptCalc(contractConceptCalc);
	    	onSave();
	    }); 
	    
	    Column<ContractConceptCalc, ContractConceptCalc> deleteColumn = new Column<ContractConceptCalc, ContractConceptCalc>(deleteActionCell) {

			@Override
			public ContractConceptCalc getValue(ContractConceptCalc contractPayment) {
				return contractPayment;
			}
			
			@Override
			public void render(Context context, ContractConceptCalc contractConceptCalc, SafeHtmlBuilder sb) {
				if(null != contractConceptCalc) {
					sb.appendHtmlConstant("<button type=\"button\" class=\"aon_button aon_table_button aon_icon_delete\" style=\"border: none !important; height: 20px;\" title=\"Eliminar\"></button>");
				}
			}
		};
		
		deleteColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		contractConceptCalcDG.setColumnWidth(deleteColumn, 5, Unit.PCT);
		
	    // Add the columns.
		contractConceptCalcDG.addColumn(typeColumn, "Tipo");
		contractConceptCalcDG.addColumn(codeColumn, "C\u00F3digo");
		contractConceptCalcDG.addColumn(descriptionColumn, "Descripci\u00F3n");
	 
		contractConceptCalcDG.addColumn(expressionColumn, "Expresi\u00F3n");
		contractConceptCalcDG.addColumn(startDateColumn, "F. Inicio");
		contractConceptCalcDG.addColumn(endDateColumn, "F. Fin");
	    
		contractConceptCalcDG.addColumn(visibilityColumn, "");  
		contractConceptCalcDG.addColumn(deleteColumn, "");  
	}
	
	// ----------------------------------------------- InitContractConceptCalcs
	
	public void initContractConceptCalcsTable() {		
		// Create a data provider.
		ListDataProvider<ContractConceptCalc> contractConceptCalcDataProvider = new ListDataProvider<>();

	    // Connect the table to the data provider.
		contractConceptCalcDataProvider.addDataDisplay(contractConceptCalcDG);
	    
	    // Add the data to the data provider, which automatically pushes it to the widget.
	    List<ContractConceptCalc> contractConceptCalcListAux = contractConceptCalcDataProvider.getList();
	    contractConceptCalcListAux.clear();
	    
	    this.contractConceptCalcList = employeeContractPaymentsObject.getContractConceptCalcs(Integer.parseInt(yearLB.getSelectedValue()));
	    
	    for (ContractConceptCalc contractConceptCalc : this.contractConceptCalcList) {
	    	contractConceptCalcListAux.add(contractConceptCalc);
	    }   
		
		addSortColums(contractConceptCalcListAux);
	    
		// Set page size
		contractConceptCalcDG.setPageSize(contractConceptCalcListAux.size());
	}

	private void addSortColums(List<ContractConceptCalc> contractConceptCalcList) {
		ListHandler<ContractConceptCalc> columnSortHandler = new ListHandler<>(contractConceptCalcList);
		
		columnSortHandler.setComparator(contractConceptCalcDG.getColumn(0),
			(o1, o2) -> compareString(o1, o2, getContractConceptCalcTypeShort(o1.getContractConceptCalcType()), getContractConceptCalcTypeShort(o2.getContractConceptCalcType())));
			
	    columnSortHandler.setComparator(contractConceptCalcDG.getColumn(1), 
	    	(o1, o2) -> compareString(o1, o2, o1.getCode(), o2.getCode()));
	    
	    columnSortHandler.setComparator(contractConceptCalcDG.getColumn(2), 
	    	(o1, o2) -> compareString(o1, o2, o1.getDescription(), o2.getDescription()));
	    
	    columnSortHandler.setComparator(contractConceptCalcDG.getColumn(3), 
	    	(o1, o2) -> compareString(o1, o2, o1.getExpression(), o2.getExpression()));
	    
	    columnSortHandler.setComparator(contractConceptCalcDG.getColumn(4), 
	    	(o1, o2) -> compareDates(o1, o2, o1.getStartDate(), o2.getStartDate()));
	    
	    columnSortHandler.setComparator(contractConceptCalcDG.getColumn(5),
	    	(o1, o2) -> compareDates(o1, o2, o1.getEndDate(), o2.getEndDate()));
	    
	    contractConceptCalcDG.addColumnSortHandler(columnSortHandler);

	    // We know that the data is sorted alphabetically by default.
	    contractConceptCalcDG.getColumn(0).setDefaultSortAscending(false);
	    contractConceptCalcDG.getColumnSortList().push(contractConceptCalcDG.getColumn(0));   
	}
	
	private int compareString(Object o1, Object o2, String s1, String s2) {
		if (o1 == o2) return 0;
		else if (o1 == null) return -1;
		else if (o2 == null) return 1;
		else
        	return s1.compareTo(s2);
	}
	
	private int compareDates(Object o1, Object o2, Date d1, Date d2) {
		if (o1 == o2) return 0;
		else if (o1 == null) return -1;
		else if (o2 == null) return 1;
		else
        	return d1.compareTo(d2);
	}
		
	// ----------------------------------------------- setEmployeeContractPaymentsObject 
	
	public void setEmployeeContractPaymentsObject(EmployeeContractPaymentsObject employeeContractPaymentsObject) {
		this.employeeContractPaymentsObject = employeeContractPaymentsObject;
		this.employeeContractPaymentsObject.getContractPayements(
				r -> initContractConceptCalcsTable()
				,t -> {});
	}
	
	// ----------------------------------------------- setEmployeeContractPaymentsObject.Methods
	
	public void initializeYearLB(ListBox yearLB) {
		Integer year = DateUtils.getYear();
		Integer yearAux = DateUtils.getYear();
		Integer previusYear = year - 1;
		Integer nextYear = year + 1;
		
		yearLB.clear();
		yearLB.addItem(nextYear.toString(), nextYear.toString());
		yearLB.addItem(yearAux.toString(), yearAux.toString());
		yearLB.addItem(previusYear.toString(), previusYear.toString());
		
		yearLB.addChangeHandler(e -> changeYear());
		
		setSelectedValueLB(yearLB, year.toString());
	}
	
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
	
	private void changeYear() {
		initContractConceptCalcsTable();
	}

	// ----------------------------------------------- Toolbar
	
	private void initializeToolbarPanel() {
		
		this.toolbar = new AonToolbar("Conceptos Calculo");
		
		saveButton = new AonToolbarButton( AON.MSG.saveAction(), AON.CSS.aonIconSave() );
		saveButton.addClickHandler(e -> onSave());
		toolbar.add(saveButton);
		
		AonToolbarButton addButton = new AonToolbarButton( AON.MSG.newAction(), AON.CSS.aonIconAdd() );
		addButton.addClickHandler(e -> openEditor());
		toolbar.add(addButton);
		
		this.yearLB = new ListBox();
		this.toolbar.add(this.yearLB);
	}

	// ----------------------------------------------- Toolbar.Methods

	public void onSave() {
		employeeContractPaymentsObject.updateContractPayments(
				r -> changeYear(), 
				t -> {});
	}
	
	public void openEditor() {
		new EmployeeContractPaymentEditor() {
			@Override
			protected void onAccept(ContractConceptCalc contractConceptCalc) {
				employeeContractPaymentsObject.createContractPayment(
						contractConceptCalc, 
						s ->
							employeeContractPaymentsObject.getContractPayements(
									r -> initContractConceptCalcsTable()
									,t -> {})
						, 
						f -> {});
			}
		};
	}
	
	// -------------------------------------------------- ContrataEmployee.Methods
	
	public void hideToolbar(){
		dockLayoutPanel.remove(toolbar);
		mainPanel.getElement().getStyle().setMarginTop(0, Unit.PX);
	}
	
	public void setYearLB(ListBox yearLB) {
		this.yearLB = yearLB;
	}
	
}
