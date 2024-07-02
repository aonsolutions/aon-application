package com.esferalia.aon.gwt.payroll.client;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonExpandButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.ContractConceptCalc;
import com.esferalia.aon.gwt.payroll.shared.ContractConceptCalc.ContractConceptCalcType;
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Salary.Type;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class EmployeeContractPayments extends Composite {

	// ----------------------------------------------- UiBinder 
	
	private static EmployeeContractPaymentsUiBinder uiBinder = GWT.create(EmployeeContractPaymentsUiBinder.class);

	interface EmployeeContractPaymentsUiBinder extends UiBinder<Widget, EmployeeContractPayments> {}
	
	// ----------------------------------------------- ContractConceptCalcTypeCell 
	
	static class ContractConceptCalcTypeCell extends AbstractCell<String> {
	   
		
	    
	    @Override
	    public void render(Context context, String value, SafeHtmlBuilder sb) {
	      if (value == null)
	        return;
	      
	      sb.appendHtmlConstant("<b title=\"" + getContractConceptCalcType(value) + "\">" + value + "</b>");
	    }
	    
	    static String getContractConceptCalcType(String contractConceptCalcTypeShort) {
			if(AonStringUtils.isBlank(contractConceptCalcTypeShort))
				return "N/D";
	    	
	    	switch (contractConceptCalcTypeShort) {
				case "P":
					return "Pagos";
				case "D":
					return "Deducci\u00F3nes";
				case "B":
					return "Bonificaciones";
				case "C":
					return "Costes";
				case "E":
					return "Embargo";
				default:
					return "N/D";
			}
		}
	  }
	
	// ------------------------------------------------- ScheduledCommand (TGSS)
	
	class AddPaymentCommand implements ScheduledCommand {

		@Override
		public void execute() {
			onPayment();
		}
	}
	
	class AddDeductionCommand implements ScheduledCommand {

		@Override
		public void execute() {
			onDeduction();
		}
	}
	
	class AddEmbargoCommand implements ScheduledCommand {

		@Override
		public void execute() {
			onEmbargo();
		}
	}
	
	class AddBonusCommand implements ScheduledCommand {

		@Override
		public void execute() {
			onBonus();
		}
	}
	
	class AddCostCommand implements ScheduledCommand {

		@Override
		public void execute() {
			onCost();
		}
	}
	
	class AddContextMenu extends ContextMenu {
		
		private MenuItem payment;
		private MenuItem dedcution;
		private MenuItem embargo;
		private MenuItem bonus;
		private MenuItem cost;
		
		public AddContextMenu() {
			
			payment = addItem("A\u00f1adir pago", new AddPaymentCommand(), 
					AON.CSS.aonIconAddBlock(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			payment.ensureDebugId("payment");
			
			dedcution = addItem("A\u00f1adir deduci\u00f3n", new AddDeductionCommand(), 
					AON.CSS.aonIconAddBlock(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			dedcution.ensureDebugId("dedcution");
			
			embargo = addItem("A\u00f1adir embargo", new AddEmbargoCommand(), 
					AON.CSS.aonIconAddBlock(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			embargo.ensureDebugId("embargo");
			
			bonus = addItem("A\u00f1adir bonificaci\u00f3n", new AddBonusCommand(), 
					AON.CSS.aonIconAddBlock(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			bonus.ensureDebugId("bonus");
			
			cost = addItem("A\u00f1adir coste", new AddCostCommand(), 
					AON.CSS.aonIconAddBlock(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			cost.ensureDebugId("cost");
			
		}

	}
	
	// ----------------------------------------------- UiField 
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String cmdBtn();
	}
	
	@UiField
	DockLayoutPanel dockLayoutPanel;
	
	@UiField(provided = true)
	AonToolbar toolbar;

	@UiField
	HTMLPanel messagePanel;
	
	@UiField(provided = true)
	DataGrid<ContractConceptCalc> contractConceptCalcDG;
	
	// ----------------------------------------------- Variables 
	
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private EmployeeContractPaymentsObject employeeContractPaymentsObject;
	private List<ContractConceptCalc> contractConceptCalcList;
	private Set<Payment> availablePayments = Collections.emptySet();
	
	private ListBox paymentTypeLB;
	private ListBox yearLB;
	private ListBox monthLB;
	
	private AddContextMenu addContextMenu;
	
	// ----------------------------------------------- Constructor 
	
	public EmployeeContractPayments() {
		initializeToolbarPanel();
		provideContractConceptCalcDG();
		initWidget(uiBinder.createAndBindUi(this));
		this.getElement().getStyle().setHeight(100, Unit.PCT);
		setScrollPanelHeight();
		this.addContextMenu = new AddContextMenu();
	}
	
	public void setToolbarTitle(String title) {
		toolbar.setTitle(title );
	}

	// ----------------------------------------------- Auxiliar Methods (Constructor & DataGrid) 
	
	private void setScrollPanelHeight() {
		contractConceptCalcDG.setHeight((Window.getClientHeight() - 220) + "px");
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
			case EMBARGO:
				return "E";
			default:
				return "N/D";
		}
	}
	
	private String getParsedExpression(String expression) {
		return AonStringUtils.isBlank(expression) ? expression : expression.replaceAll("HIDE\\(.*\\); ", "");
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
	    
	}
	
	private void addContractConceptCalcColumns() {
		// Edit column.
	    ActionCell<ContractConceptCalc> editActionCell = new ActionCell<>("", selectedPayment -> openDialog(selectedPayment));
	    
	    Column<ContractConceptCalc, ContractConceptCalc> editColumn = new Column<ContractConceptCalc, ContractConceptCalc>(editActionCell) {

			@Override
			public ContractConceptCalc getValue(ContractConceptCalc contractPayment) {
				return contractPayment;
			}
			
			@Override
			public void render(Context context, ContractConceptCalc contractConceptCalc, SafeHtmlBuilder sb) {
				if(null != contractConceptCalc) {
					sb.appendHtmlConstant("<button type=\"button\" class=\"aon_button aon_table_button aon_icon_right\" style=\"border: none !important; height: 20px;\" title=\"Editar\"></button>");
				}
			}
		};
		
		editColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		contractConceptCalcDG.setColumnWidth(editColumn, 5, Unit.PCT);
		
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
				return null == contractConceptCalc.getType() ? "" : contractConceptCalc.getType().ordinal()+"";
	        }
		};

		codeColumn.setSortable(true);
		codeColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		contractConceptCalcDG.setColumnWidth(codeColumn, 10, Unit.PCT);

	    // Description column.
		Column<ContractConceptCalc, String> descriptionColumn = new Column<ContractConceptCalc, String>(new TextCell()) {
			@Override
			public String getValue(ContractConceptCalc contractConceptCalc) {
				return contractConceptCalc.getDescription();
			}
		};
		
		descriptionColumn.setSortable(true);
		contractConceptCalcDG.setColumnWidth(descriptionColumn, 20, Unit.PCT);
	    
	    // Expression column.
	    Column<ContractConceptCalc, String> expressionColumn = new Column<ContractConceptCalc, String>(new TextCell()) {
	    	@Override
			public String getValue(ContractConceptCalc contractPayment) {
	    		String parsedExpression = getParsedExpression(contractPayment.getExpression());
				return AonStringUtils.isNotBlank(parsedExpression) && parsedExpression.length() > 200 ? parsedExpression.substring(0, 199) : parsedExpression;
			}
			
			@Override
			public void render(Context context, ContractConceptCalc contractConceptCalc, SafeHtmlBuilder sb) {
				if(null != contractConceptCalc) {
					String parsedExpression = getParsedExpression(contractConceptCalc.getExpression());
					sb.appendHtmlConstant("<div style=\"outline-style:none;\" title=\"" + parsedExpression + "\">" + (AonStringUtils.isNotBlank(parsedExpression) && parsedExpression.length() > 80 ? parsedExpression.substring(0, 79) + "..." : parsedExpression)  + "</div>");
				}
			}
		};

	    expressionColumn.setSortable(true);
	    contractConceptCalcDG.setColumnWidth(expressionColumn, 30, Unit.PCT);
	    
	    // StartDate column.
	    Column<ContractConceptCalc, String> startDateColumn = new Column<ContractConceptCalc, String>(new TextCell()) {
	    	@Override
	        public String getValue(ContractConceptCalc contractConceptCalc) {
	    		return formatDate.format(contractConceptCalc.getStartDate());
	        }
		};
		
//	    Column<ContractConceptCalc, Date> startDateColumn = new Column<ContractConceptCalc, Date>(new DatePickerCell()) {
//	    	@Override
//	        public Date getValue(ContractConceptCalc contractConceptCalc) {
//	    		return contractConceptCalc.getStartDate();
//	        }
//		};
//		
//		startDateColumn.setFieldUpdater((index, contractConceptCalc, startDate) -> {
//			contractConceptCalc.setStartDate(startDate);
//	    	contractConceptCalc.setHasChange(true);
//	    	onSave();
//		});

	    startDateColumn.setSortable(true);
	    startDateColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    contractConceptCalcDG.setColumnWidth(startDateColumn, 10, Unit.PCT);
	    
	    // EndDate column.
	    Column<ContractConceptCalc, String> endDateColumn = new Column<ContractConceptCalc, String>(new TextCell()) {
			@Override
			public String getValue(ContractConceptCalc contractConceptCalc) {
				Date endDate = contractConceptCalc.getEndDate();
				return endDate == null ? "" : formatDate.format(endDate);
			}
		};
	    
//	    Column<ContractConceptCalc, Date> endDateColumn = new Column<ContractConceptCalc, Date>(new DatePickerCell()) {
//			@Override
//			public Date getValue(ContractConceptCalc contractConceptCalc) {
//				return contractConceptCalc.getEndDate();
//			}
//		};
//		
//		endDateColumn.setFieldUpdater((index, contractConceptCalc, endDate) -> {
//			contractConceptCalc.setEndDate(endDate);
//	    	contractConceptCalc.setHasChange(true);
//	    	onSave();
//		});

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
	    	AonDialog deleteDialog = new AonDialog("Eliminar concepto", new HTML("\u00BFDesea eliminar el concepto seleccionado\u003F"));
			deleteDialog.confirm(new AonAcceptDialogCallback() {
				
				@Override
				public void onCancel() {
					// Nothing to do here
				}
				
				@Override
				public void onAccept() {
			    	employeeContractPaymentsObject.deleteContractConceptCalc(contractConceptCalc);
			    	onSave();
				}
			});
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
		contractConceptCalcDG.addColumn(editColumn, "");
		contractConceptCalcDG.addColumn(typeColumn, "Tipo");
		contractConceptCalcDG.addColumn(codeColumn, "CRA");
		contractConceptCalcDG.addColumn(descriptionColumn, "Descripci\u00F3n");
	 
		contractConceptCalcDG.addColumn(expressionColumn, "Expresi\u00F3n");
		contractConceptCalcDG.addColumn(startDateColumn, "F. Inicio");
		contractConceptCalcDG.addColumn(endDateColumn, "F. Fin");
	    
		contractConceptCalcDG.addColumn(visibilityColumn, "");  
		contractConceptCalcDG.addColumn(deleteColumn, "");  
	}
	
	private void openDialog(ContractConceptCalc selectedPayment) {
		boolean isHide = AonStringUtils.isNotBlank(selectedPayment.getExpression()) && AonStringUtils.containsIgnoreCase(selectedPayment.getExpression(), "HIDE");
    	EmployeeContractPaymentEditor paymentEditor = new EmployeeContractPaymentEditor(selectedPayment.getContractConceptCalcType(), selectedPayment, employeeContractPaymentsObject.getContractStartDate(), employeeContractPaymentsObject.getContractEndDate()) {
			@Override
			protected void onAccept(ContractConceptCalc updatedPayment) {
				switch (updatedPayment.getContractConceptCalcType()) {
					case PAYMENT:
						updatePayment(isHide, selectedPayment, updatedPayment);
						break;
					case DEDUCTION:
						updateDeduction(isHide, selectedPayment, updatedPayment);
						break;
					case COST:
						updateCost(isHide, selectedPayment, updatedPayment);
						break;
					case BONUS:
						updateBonus(isHide, selectedPayment, updatedPayment);
						break;
					case EMBARGO:
						updateEmbargo(isHide, selectedPayment, updatedPayment);
						break;
					default:
						break;
				}
			}

			private void updatePayment(boolean isHide, ContractConceptCalc selectedPayment, ContractConceptCalc updatedPayment) {
				selectedPayment.setType(updatedPayment.getType());
				selectedPayment.setConceptId(updatedPayment.getConceptId());
				selectedPayment.setName(updatedPayment.getName());
				selectedPayment.setDescription(updatedPayment.getDescription());
				selectedPayment.setExpression(Boolean.TRUE.equals(isHide) ? showHideContractConceptCalc(updatedPayment.getDescription(), updatedPayment.getExpression(), updatedPayment.getContractConceptCalcType()) : updatedPayment.getExpression());
				selectedPayment.setIrpfExpression(updatedPayment.getIrpfExpression());
				selectedPayment.setQuoteExpression(updatedPayment.getQuoteExpression());
				selectedPayment.setMonth(updatedPayment.getMonth());
				selectedPayment.setStartDate(updatedPayment.getStartDate());
				selectedPayment.setEndDate(updatedPayment.getEndDate());
				selectedPayment.setContractConceptCalcType(ContractConceptCalcType.PAYMENT);
				selectedPayment.setScope(Scope.SALARY);
				selectedPayment.setSalaryType(Type.SALARY);
				selectedPayment.setHasChange(true);
				contractConceptCalcDG.redraw();
				onSave();
			}

			private void updateDeduction(boolean isHide, ContractConceptCalc selectedPayment, ContractConceptCalc updatedPayment) {
				selectedPayment.setConceptId(updatedPayment.getConceptId());
				selectedPayment.setCodeType(updatedPayment.getCodeType());
				selectedPayment.setName(updatedPayment.getName());
				selectedPayment.setDescription(updatedPayment.getDescription());
				selectedPayment.setExpression(Boolean.TRUE.equals(isHide) ? showHideContractConceptCalc(updatedPayment.getDescription(), updatedPayment.getExpression(), updatedPayment.getContractConceptCalcType()) : updatedPayment.getExpression());
				selectedPayment.setMonth(updatedPayment.getMonth());
				selectedPayment.setStartDate(updatedPayment.getStartDate());
				selectedPayment.setEndDate(updatedPayment.getEndDate());
				selectedPayment.setContractConceptCalcType(ContractConceptCalcType.DEDUCTION);
				selectedPayment.setScope(Scope.SALARY);
				selectedPayment.setSalaryType(Type.SALARY);
				selectedPayment.setHasChange(true);
				contractConceptCalcDG.redraw();
				onSave();
			}

			private void updateCost(boolean isHide, ContractConceptCalc selectedPayment, ContractConceptCalc updatedPayment) {
				selectedPayment.setCodeType(updatedPayment.getCodeType());
				selectedPayment.setName(updatedPayment.getName());
				selectedPayment.setDescription(updatedPayment.getDescription());
				selectedPayment.setExpression(Boolean.TRUE.equals(isHide) ? showHideContractConceptCalc(updatedPayment.getDescription(), updatedPayment.getExpression(), updatedPayment.getContractConceptCalcType()) : updatedPayment.getExpression());
				selectedPayment.setStartDate(updatedPayment.getStartDate());
				selectedPayment.setEndDate(updatedPayment.getEndDate());
				selectedPayment.setContractConceptCalcType(ContractConceptCalcType.COST);
				selectedPayment.setScope(Scope.SALARY);
				selectedPayment.setSalaryType(Type.SALARY);
				selectedPayment.setHasChange(true);
				contractConceptCalcDG.redraw();
				onSave();
			}

			private void updateBonus(boolean isHide, ContractConceptCalc selectedPayment, ContractConceptCalc updatedPayment) {
				selectedPayment.setConceptId(updatedPayment.getConceptId());
				selectedPayment.setCodeType(updatedPayment.getCodeType());
				selectedPayment.setDescription(updatedPayment.getDescription());
				selectedPayment.setExpression(Boolean.TRUE.equals(isHide) ? showHideContractConceptCalc(updatedPayment.getDescription(), updatedPayment.getExpression(), updatedPayment.getContractConceptCalcType()) : updatedPayment.getExpression());
				selectedPayment.setStartDate(updatedPayment.getStartDate());
				selectedPayment.setEndDate(updatedPayment.getEndDate());
				selectedPayment.setContractConceptCalcType(ContractConceptCalcType.BONUS);
				selectedPayment.setScope(Scope.SALARY);
				selectedPayment.setSalaryType(Type.SALARY);
				selectedPayment.setHasChange(true);
				contractConceptCalcDG.redraw();
				onSave();
			}
			
			private void updateEmbargo(boolean isHide, ContractConceptCalc selectedPayment, ContractConceptCalc updatedPayment) {
				selectedPayment.setDescription(updatedPayment.getDescription());
				selectedPayment.setExpression(Boolean.TRUE.equals(isHide) ? showHideContractConceptCalc(updatedPayment.getDescription(), updatedPayment.getExpression(), updatedPayment.getContractConceptCalcType()) : updatedPayment.getExpression());
				selectedPayment.setAmount(updatedPayment.getAmount());
				selectedPayment.setStartDate(updatedPayment.getStartDate());
				selectedPayment.setEndDate(updatedPayment.getEndDate());
				selectedPayment.setContractConceptCalcType(ContractConceptCalcType.EMBARGO);
				selectedPayment.setScope(Scope.SALARY);
				selectedPayment.setSalaryType(Type.SALARY);
				selectedPayment.setHasChange(true);
				contractConceptCalcDG.redraw();
				onSave();
			}
		};
		
		paymentEditor.setSaveButton();
	}
	
	public String showHideContractConceptCalc(String description, String expression, ContractConceptCalcType paymentType) {
		if(paymentType == ContractConceptCalcType.PAYMENT) {
			if(!AonStringUtils.isBlank(expression) && AonStringUtils.containsIgnoreCase(expression, "HIDE") && AonStringUtils.startsWithIgnoreCase(expression, "HIDE"))
				expression = expression.replaceAll("HIDE.*; ", "");
			else
				expression = "HIDE(\"<div>" + description + " oculto desde Conceptos de c\u00E1lculo</div><div>&nbsp;</div><div class='aon-text-right'><span class='aon-icon aon-icon-logo'/>aon Solutions</div>\"); " + expression;
		} else {
			if(!AonStringUtils.isBlank(expression) && AonStringUtils.containsIgnoreCase(expression, "HIDE") && AonStringUtils.startsWithIgnoreCase(expression, "HIDE"))
				expression = expression.replaceAll("HIDE.*; ", "");
			else
				expression = "HIDE(\"<div>Oculto desde Conceptos de c\u00E1lculo</div><div>&nbsp;</div>\"); " + expression;
		}
		
		
		return expression;
	}
	
	// ----------------------------------------------- InitContractConceptCalcs
	
	public void initContractConceptCalcsTable() {	
		hideMessage();
		
		// Create a data provider.
		ListDataProvider<ContractConceptCalc> contractConceptCalcDataProvider = new ListDataProvider<>();

	    // Connect the table to the data provider.
		contractConceptCalcDataProvider.addDataDisplay(contractConceptCalcDG);
	    
	    // Add the data to the data provider, which automatically pushes it to the widget.
	    List<ContractConceptCalc> contractConceptCalcListAux = contractConceptCalcDataProvider.getList();
	    contractConceptCalcListAux.clear();
	    
	    this.contractConceptCalcList = employeeContractPaymentsObject.getContractConceptCalcs(yearLB.getSelectedValue(), monthLB.getSelectedValue(), paymentTypeLB.getSelectedValue());
	    
	    for (ContractConceptCalc contractConceptCalc : this.contractConceptCalcList) {
	    	contractConceptCalcListAux.add(contractConceptCalc);
	    }   
		
		addSortColums(contractConceptCalcListAux);
	    
		// Set page size
		contractConceptCalcDG.setPageSize(contractConceptCalcListAux.size());
		
		setScrollPanelHeight();
	}

	private void addSortColums(List<ContractConceptCalc> contractConceptCalcList) {
		ListHandler<ContractConceptCalc> columnSortHandler = new ListHandler<>(contractConceptCalcList);
		
		columnSortHandler.setComparator(contractConceptCalcDG.getColumn(1),
			(o1, o2) -> compareString(o1, o2, getContractConceptCalcTypeShort(o1.getContractConceptCalcType()), getContractConceptCalcTypeShort(o2.getContractConceptCalcType())));
			
	    columnSortHandler.setComparator(contractConceptCalcDG.getColumn(2), 
	    	(o1, o2) -> compareString(o1, o2, o1.getType().ordinal()+"", o2.getType().ordinal()+""));
	    
	    columnSortHandler.setComparator(contractConceptCalcDG.getColumn(3), 
	    	(o1, o2) -> compareString(o1, o2, o1.getDescription(), o2.getDescription()));
	    
	    columnSortHandler.setComparator(contractConceptCalcDG.getColumn(4), 
	    	(o1, o2) -> compareString(o1, o2, o1.getExpression(), o2.getExpression()));
	    
	    columnSortHandler.setComparator(contractConceptCalcDG.getColumn(5), 
	    	(o1, o2) -> compareDates(o1, o2, o1.getStartDate(), o2.getStartDate()));
	    
	    columnSortHandler.setComparator(contractConceptCalcDG.getColumn(6),
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
		showLoading("Obteniendo conceptos de calculo ...");
		this.employeeContractPaymentsObject.getContractPayements(
				r -> {
					initializeYearLB(this.yearLB);
					initContractConceptCalcsTable();
				},t -> {});
	}
	
	// ----------------------------------------------- setEmployeeContractPaymentsObject.Methods
	
	public void initializePaymentTypeLB() {
		this.paymentTypeLB.clear();
		this.paymentTypeLB.addItem("Todos", "");
		this.paymentTypeLB.addItem("Pagos", "0");
		this.paymentTypeLB.addItem("Deducciones", "1");
		this.paymentTypeLB.addItem("Costes", "2");
		this.paymentTypeLB.addItem("Bonificaciones", "3");
		this.paymentTypeLB.addItem("Embargo", "4");
		
		this.paymentTypeLB.addChangeHandler(e -> changeYear());
		
		setSelectedValueLB(this.paymentTypeLB, "");
	}
	
	public void initializeYearLB(ListBox yearLB) {
		Integer startYear = DateUtils.getYear(employeeContractPaymentsObject.getContractStartDate());
		Integer endYear = null == employeeContractPaymentsObject.getContractEndDate() ? DateUtils.getYear() : DateUtils.getYear(employeeContractPaymentsObject.getContractEndDate());
		
		Integer auxYear = endYear;
		if(null == employeeContractPaymentsObject.getContractEndDate()) auxYear++;
		
		yearLB.clear();
		yearLB.addItem("-", "");
		
		while(auxYear >= startYear) {
			yearLB.addItem(auxYear.toString(), auxYear.toString());
			auxYear--;
		}
		
		yearLB.addChangeHandler(e -> {
			checkSelectedYear();
			changeYear();
		});
		
		setSelectedValueLB(yearLB, endYear.toString());
	}
	
	private void checkSelectedYear() {
		if(AonStringUtils.isBlank(yearLB.getSelectedValue())) {
			monthLB.setSelectedIndex(0);
			monthLB.setVisible(false);
		} else
			monthLB.setVisible(true);
	}

	public void initializeMonthLB() {
		this.monthLB.clear();
		this.monthLB.addItem("-", "");
		this.monthLB.addItem("Enero", "0");
		this.monthLB.addItem("Febrero", "1");
		this.monthLB.addItem("Marzo", "2");
		this.monthLB.addItem("Abril", "3");
		this.monthLB.addItem("Mayo", "4");
		this.monthLB.addItem("Junio", "5");
		this.monthLB.addItem("Julio", "6");
		this.monthLB.addItem("Agosto", "7");
		this.monthLB.addItem("Septiembre", "8");
		this.monthLB.addItem("Octubre", "9");
		this.monthLB.addItem("Noviembre", "10");
		this.monthLB.addItem("Diciembre", "11");
		
		this.monthLB.addChangeHandler(e -> changeYear());
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
		
		AonExpandButton addExpand = new AonExpandButton("A\u00f1adir pagos", AON.CSS.aonIconAddBlock()) {
			
			@Override
			public void onExpandClick(ClickEvent event) {
				NativeEvent nativeEvent = event.getNativeEvent();
				addContextMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
				addContextMenu.show();
			}
			
			@Override
			public void onDefaultClick(ClickEvent evet) {
				onPaymentWizard();
			}
		};
		toolbar.add(addExpand);
		
		this.paymentTypeLB = new ListBox();
		initializePaymentTypeLB();
		this.toolbar.add(this.paymentTypeLB);
		
		this.yearLB = new ListBox();
		this.toolbar.add(this.yearLB);
		
		this.monthLB = new ListBox();
		initializeMonthLB();
		this.toolbar.add(this.monthLB);
	}

	// ----------------------------------------------- Toolbar.Methods

	public void onSave() {
		showLoading("Guardando conceptos de calculo ...");
		employeeContractPaymentsObject.updateContractPayments(
				r -> {
					showSuccess("Conceptos Calculo", "Conceptos Calculo guardados correctamente");
					changeYear();
				}, t -> showError("Error Conceptos Calculo", t.getMessage()));
	}
	
	private void onPaymentWizard() {
		employeeContractPaymentsObject.getAvailablePayments(s -> {
			availablePayments = filterPayments(s);
			
			new SalaryPaymentWizard(availablePayments, null) {
				
				@Override
				protected void onGtzdoAccept(List<Payment> payments) {
					List<ContractConceptCalc> contractConceptCalcListAux = Collections.emptyList();
					for(Payment payment : payments) {
						ContractConceptCalc contractConceptCalc = (ContractConceptCalc)payment;
						contractConceptCalc.setContractConceptCalcType(ContractConceptCalcType.PAYMENT);
						contractConceptCalc.setCodeType(contractConceptCalc.getType().ordinal()+"");
						contractConceptCalcListAux.add(contractConceptCalc);
					}
					createAndGetPayments(contractConceptCalcListAux);
				}
				
				@Override
				protected void onExtraAccept(Payment payment, Extra extra) {
					ContractConceptCalc contractConceptCalc = (ContractConceptCalc)payment;
					contractConceptCalc.setContractConceptCalcType(ContractConceptCalcType.PAYMENT);
					contractConceptCalc.setCodeType(contractConceptCalc.getType().ordinal()+"");
					createAndGetPayments(contractConceptCalc);
				}
				
				@Override
				protected void onAccept(Payment payment) {
					ContractConceptCalc contractConceptCalc = new ContractConceptCalc(payment);
					contractConceptCalc.setContractConceptCalcType(ContractConceptCalcType.PAYMENT);
					contractConceptCalc.setCodeType(contractConceptCalc.getType().ordinal()+"");
					createAndGetPayments(contractConceptCalc);
				}
			};
			
		}, f -> {});
	}
	
	private Set<Payment> filterPayments(List<Payment> payments) {
		Set<String> names = 
				payments.stream()
				.map(p -> p.getName() )
				.filter ( n -> n != null)
				.collect(Collectors.toSet())
				;
		
		return  
		payments.stream()
		.filter(p -> AonStringUtils.isNotBlank(p.getName()) && names.contains(p.getName()))
		.collect(Collectors.toSet());
	}
	
	private void onPayment() {
		openEditor(ContractConceptCalcType.PAYMENT);
	}

	private void onDeduction() {
		openEditor(ContractConceptCalcType.DEDUCTION);
	}

	private void onCost() {
		openEditor(ContractConceptCalcType.COST);
	}

	private void onBonus() {
		openEditor(ContractConceptCalcType.BONUS);
	}
	
	private void onEmbargo() {
		openEditor(ContractConceptCalcType.EMBARGO);
	}
	
	public void openEditor(ContractConceptCalcType type) {
		EmployeeContractPaymentEditor paymentEditor = new EmployeeContractPaymentEditor(type, employeeContractPaymentsObject.getContractStartDate(), employeeContractPaymentsObject.getContractEndDate()) {
			@Override
			protected void onAccept(ContractConceptCalc contractConceptCalc) {
				createAndGetPayments(contractConceptCalc);
			}
		};
		
		paymentEditor.setSaveButton();
	}
	
	private void createAndGetPayments(ContractConceptCalc contractConceptCalc) {
		showLoading("Creando concepto de calculo ...");
		employeeContractPaymentsObject.createContractPayment(
				contractConceptCalc, 
				s -> {
					showLoading("Obteniendo conceptos de calculo ...");
					employeeContractPaymentsObject.getContractPayements(
							r -> initContractConceptCalcsTable()
							,t -> showError("Error Conceptos Calculo", t.getMessage()));
				}, 
				f -> showError("Error Conceptos Calculo", f.getMessage()));
	}
	
	private void createAndGetPayments(List<ContractConceptCalc> contractConceptCalcs) {
		showLoading("Creando conceptos de calculo ...");
		employeeContractPaymentsObject.createContractPayment(
				contractConceptCalcs, 
				s -> {
					showLoading("Obteniendo conceptos de calculo ...");
					employeeContractPaymentsObject.getContractPayements(
							r -> initContractConceptCalcsTable()
							,t -> showError("Error Conceptos Calculo", t.getMessage()));
				}, 
				f -> showError("Error Conceptos Calculo", f.getMessage()));
	}
	
	// -------------------------------------------------- ContrataEmployee.Methods
	
	public void hideToolbar(){
		dockLayoutPanel.remove(toolbar);
	}
	
	public void setVariableTypeLB(ListBox paymentTypeLB) {
		this.paymentTypeLB = paymentTypeLB;
	}
	
	public void setYearLB(ListBox yearLB) {
		this.yearLB = yearLB;
	}
	
	public void setMonthLB(ListBox monthLB) {
		this.monthLB = monthLB;
	}
	
	// ------------------------------------------------- Aon Messages panel

	private void showSuccess(String title, String message) {
		Map<String, String> successMap = new HashMap<>();
		successMap.put(title, message);
		AonMessagePanel.showSuccess(messagePanel, successMap);
	}

	private void showError(String title, String message) {
		Map<String, String> errorMap = new HashMap<>();
		errorMap.put(title, message);
		AonMessagePanel.showError(messagePanel, errorMap);
	}

	private void showLoading(String message) {
		AonMessagePanel.showLoading(messagePanel, message);
	}

	private void hideMessage() {
		AonMessagePanel.hideMessage(messagePanel);
	}
}
