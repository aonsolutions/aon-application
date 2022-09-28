package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonExpandButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.gwt.payroll.shared.AgreementInfo;
import com.esferalia.aon.gwt.payroll.shared.AgreementInfo.AgreementExtra;
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.SpecialExpresion;
import com.esferalia.aon.gwt.payroll.shared.Salary.Type;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public abstract class AgreementPaymentTab extends ResizeComposite {
	
	// ------------------------------------------ UiBinder 

	private static AgreementPaymentTabUiBinder uiBinder = GWT.create(AgreementPaymentTabUiBinder.class);

	interface AgreementPaymentTabUiBinder extends UiBinder<Widget, AgreementPaymentTab> {}
	
	// ------------------------------------------ ScheduledCommand
	
	class AddBasicSalaryCommand implements ScheduledCommand {

		@Override
		public void execute() {
			AgreementPaymentDialog dialog = new AgreementPaymentDialog(0) {
				
				@Override
				protected void onAccept(List<Payment> payments) {
					for(Payment payment : payments)
						agreement.addPayment(payment);
					setAgreementPayment(agreement);
					setHasChange(true);
				}

				@Override
				protected void onAcceptExtra(List<Payment> paymentResultList, List<Extra> extraResultList) {
					// Nothing to do here
				}
				
			};
			
			getAvailablePayments(s -> dialog.setAvailablePaymens(s));
			dialog.center();
			dialog.show();
		}
	}
	
	class AddPlusesSalaryCommand implements ScheduledCommand {

		@Override
		public void execute() {
			AgreementPaymentDialog dialog = new AgreementPaymentDialog(1) {
				
				@Override
				protected void onAccept(List<Payment> payments) {
					for(Payment payment : payments)
						agreement.addPayment(payment);
					setAgreementPayment(agreement);
					setHasChange(true);
				}
				
				@Override
				protected void onAcceptExtra(List<Payment> paymentResultList, List<Extra> extraResultList) {
					// Nothing to do here
				}
				
			};
			
			getAvailablePayments(s -> dialog.setAvailablePaymens(s));
			dialog.center();
			dialog.show();
		}
	}
	
	class AddPlusesExtraSalaryCommand implements ScheduledCommand {

		@Override
		public void execute() {
			AgreementPaymentDialog dialog = new AgreementPaymentDialog(2) {
				
				@Override
				protected void onAccept(List<Payment> payments) {
					for(Payment payment : payments)
						agreement.addPayment(payment);
					setAgreementPayment(agreement);
					setHasChange(true);
				}
				
				@Override
				protected void onAcceptExtra(List<Payment> paymentResultList, List<Extra> extraResultList) {
					// Nothing to do here
				}
				
			};
			
			getAvailablePayments(s -> dialog.setAvailablePaymens(s));
			dialog.center();
			dialog.show();
		}
	}
	
	class AddComplementsSalaryCommand implements ScheduledCommand {

		@Override
		public void execute() {
			AgreementPaymentDialog dialog = new AgreementPaymentDialog(3) {
				
				@Override
				protected void onAccept(List<Payment> payments) {
					for(Payment payment : payments)
						agreement.addPayment(payment);
					setAgreementPayment(agreement);
					setHasChange(true);
				}
				
				@Override
				protected void onAcceptExtra(List<Payment> paymentResultList, List<Extra> extraResultList) {
					// Nothing to do here
				}
				
			};
			
			getAvailablePayments(s -> dialog.setAvailablePaymens(s));
			dialog.center();
			dialog.show();
		}
	}
	
	class AddExtrasSalaryCommand implements ScheduledCommand {

		@Override
		public void execute() {
			AgreementPaymentDialog dialog = new AgreementPaymentDialog(4) {
				
				@Override
				protected void onAccept(List<Payment> payments) {
					// Nothing to do here
				}
				
				@Override
				protected void onAcceptExtra(List<Payment> payments, List<Extra> extras) {
					for(Payment payment : payments)
						agreement.addPayment(payment);
					
					for(Extra extra : extras)
						agreement.addExtra(extra);
					
					setAgreementPayment(agreement);
					setHasChange(true);
				}
				
			};
			
			getAvailablePayments(s -> dialog.setAvailablePaymens(s));
			dialog.center();
			dialog.show();
		}
	}
	
	private void getAvailablePayments(Consumer<List<Payment>> success) {
		agreementServiceAsync.getAvailablePayments(Wnd.getCurrentDomainNameURL(), Integer.MIN_VALUE, new AsyncCallback<List<Payment>>() {

			@Override
			public void onFailure(Throwable caught) {
				// Nothing to do here
			}

			@Override
			public void onSuccess(List<Payment> aviablePayments) {
				success.accept(aviablePayments);
			}});
	}
	
	class AddPaymentContextMenu extends ContextMenu {
				
		private MenuItem addBasicSalary = null;
		private MenuItem addPlusesSalary = null;
		private MenuItem addPlusesExtraSalary = null;
		private MenuItem addComplementsSalary = null;
		private MenuItem addExtrasSalary = null;
		
		public AddPaymentContextMenu() {
			
			addBasicSalary = addItem("Salario Base", new AddBasicSalaryCommand(), 
					AON.CSS.aonIconAddBlock(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			addBasicSalary.ensureDebugId("addBasicSalary");
			
			addPlusesSalary = addItem("Plus Salarial", new AddPlusesSalaryCommand(), 
					AON.CSS.aonIconAddBlock(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			addPlusesSalary.ensureDebugId("addPlusesSalary");
			
			addPlusesExtraSalary = addItem("Plus Extra Salarial", new AddPlusesExtraSalaryCommand(), 
					AON.CSS.aonIconAddBlock(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			addPlusesExtraSalary.ensureDebugId("addPlusesExtraSalary");
			
			addComplementsSalary = addItem("Complementos y gastos", new AddComplementsSalaryCommand(), 
					AON.CSS.aonIconAddBlock(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			addComplementsSalary.ensureDebugId("addComplementsSalary");
			
			addExtrasSalary = addItem("Pagas Extras", new AddExtrasSalaryCommand(), 
					AON.CSS.aonIconAddBlock(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			addExtrasSalary.ensureDebugId("addExtrasSalary");
		}
	}

	// ------------------------------------------ UiFields
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String cmdBtn();
	}
	
	@UiField
	HTMLPanel messagePanel;
	
	@UiField(provided = true)
	AonToolbar toolbar;
	
	@UiField(provided = true)
	DataGrid<Payment> agreementPaymentDG;
	
	// ------------------------------------------ Variables
	
	private AgreementServiceAsyncDecorator agreementServiceAsync;
	private AgreementInfo agreement;
	private AddPaymentContextMenu contextMenu;
	private List<Payment> paymentList;
	
	private boolean hasChange = false;
	private AonToolbarSmallButton saveBtn;
	
	// ------------------------------------------ Constructor

	protected AgreementPaymentTab() {
		createToolbar();
		provideAgreementPaymentDG();
		initWidget(uiBinder.createAndBindUi(this));
		
		AgreementServiceAsync agreementServiceRaw = GWT.create(AgreementService.class);
		agreementServiceAsync = new AgreementServiceAsyncDecorator(agreementServiceRaw);
		
		contextMenu = new AddPaymentContextMenu();
		setScrollDGHeight();
	}
	
	private void setScrollDGHeight() {
		agreementPaymentDG.setHeight((Window.getClientHeight() - 280) + "px");
	}
	
	// ----------------------------------------------- ProvideContractConceptCalcDG
	
	private void provideAgreementPaymentDG() {
		paymentList = Collections.emptyList();
		
		// Resource Style CellTable
		agreementPaymentDG = new CustomDataGrid<>(Integer.MAX_VALUE, Payment.KEY_PROVIDER);
		
		//Do not refresh the headers every time the dataGrid is updated.
		agreementPaymentDG.setAutoHeaderRefreshDisabled(true);
		
		// Set the message to display when the table is empty.
		agreementPaymentDG.setEmptyTableWidget(new Label(("No existen devengos").toUpperCase()));
		
		// Initialize the columns.
	    addPaymentsDGColumns();
	    
	    new ListDataProvider<Payment>(Collections.emptyList()).addDataDisplay(agreementPaymentDG);
	    
	}
	
	private void addPaymentsDGColumns() {
		// Edit column.
	    ActionCell<Payment> editActionCell = new ActionCell<>("", selectedPayment -> openDialog(selectedPayment));
	    
	    Column<Payment, Payment> editColumn = new Column<Payment, Payment>(editActionCell) {

			@Override
			public Payment getValue(Payment payment) {
				return payment;
			}
			
			@Override
			public void render(Context context, Payment payment, SafeHtmlBuilder sb) {
				if(null != payment) {
					sb.appendHtmlConstant("<button type=\"button\" class=\"aon_button aon_table_button aon_icon_right\" style=\"border: none !important; height: 20px;\" title=\"Editar\"></button>");
				}
			}
		};
		
		editColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		agreementPaymentDG.setColumnWidth(editColumn, 5, Unit.PCT);
		
		// Code columns.
		Column<Payment, String> codeColumn = new Column<Payment, String>(new TextCell()) {
			@Override
	        public String getValue(Payment payment) {
				return null == payment.getType() ? "" : AonStringUtils.leftPad(payment.getType().getCode() + "", 4, '0');
	        }
		};

		codeColumn.setSortable(true);
		codeColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		agreementPaymentDG.setColumnWidth(codeColumn, 10, Unit.PCT);

	    // Description column.
		Column<Payment, String> descriptionColumn = new Column<Payment, String>(new TextCell()) {
			@Override
			public String getValue(Payment payment) {
				return payment.getDescription();
			}
		};
		
		descriptionColumn.setSortable(true);
		agreementPaymentDG.setColumnWidth(descriptionColumn, 20, Unit.PCT);
	    
	    // Expression column.
	    Column<Payment, String> expressionColumn = new Column<Payment, String>(new TextCell()) {
	    	@Override
	        public String getValue(Payment payment) {
	    		return getParsedExpression(payment.getExpression());
	        }
		};

	    expressionColumn.setSortable(true);
	    agreementPaymentDG.setColumnWidth(expressionColumn, 30, Unit.PCT);
	    
	    // Visibility column.
	    ActionCell<Payment> visibilityActionCell = new ActionCell<>("", payment -> {
	    	showHidePayment(payment);
	    	agreementPaymentDG.redraw();
			setHasChange(true);
	    });
	    
	    Column<Payment, Payment> visibilityColumn = new Column<Payment, Payment>(visibilityActionCell) {

			@Override
			public Payment getValue(Payment payment) {
				return payment;
			}
			
			@Override
			public void render(Context context, Payment payment, SafeHtmlBuilder sb) {
				if(null != payment) {
					if(isHideExpression(payment))
						sb.appendHtmlConstant("<button type=\"button\" class=\"aon-editDataTable-button aon-no-margin aon-icon-disable\" title=\"Inactivo\"></button>");
					else
						sb.appendHtmlConstant("<button type=\"button\" class=\"aon-editDataTable-button aon-no-margin aon-icon-enable\" title=\"Activo\"></button>");
				}
			}
		};
		
		visibilityColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		agreementPaymentDG.setColumnWidth(visibilityColumn, 5, Unit.PCT);
	    
	    // Delete column.
	    ActionCell<Payment> deleteActionCell = new ActionCell<>("", payment -> {
	    	AonDialog deleteDialog = new AonDialog("Eliminar concepto", new HTML("\u00BFDesea eliminar el concepto seleccionado\u003F"));
			deleteDialog.confirm(new AonAcceptDialogCallback() {
				
				@Override
				public void onCancel() {
					// Nothing to do here
				}
				
				@Override
				public void onAccept() {
					agreement.deletePayment(payment);
					setAgreementPayment(agreement);
					setHasChange(true);
				}
			});
	    }); 
	    
	    Column<Payment, Payment> deleteColumn = new Column<Payment, Payment>(deleteActionCell) {

			@Override
			public Payment getValue(Payment payment) {
				return payment;
			}
			
			@Override
			public void render(Context context, Payment payment, SafeHtmlBuilder sb) {
				if(null != payment) {
					sb.appendHtmlConstant("<button type=\"button\" class=\"aon_button aon_table_button aon_icon_delete\" style=\"border: none !important; height: 20px;\" title=\"Eliminar\"></button>");
				}
			}
		};
		
		deleteColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		agreementPaymentDG.setColumnWidth(deleteColumn, 5, Unit.PCT);
		
	    // Add the columns.
		agreementPaymentDG.addColumn(editColumn, "");
		agreementPaymentDG.addColumn(codeColumn, "CRA");
		agreementPaymentDG.addColumn(descriptionColumn, "Descripci\u00F3n");
		agreementPaymentDG.addColumn(expressionColumn, "Expresi\u00F3n");
		
		agreementPaymentDG.addColumn(visibilityColumn, "");  
		agreementPaymentDG.addColumn(deleteColumn, "");  
	}
	
	private void openDialog(Payment payment) {
		boolean isHide = AonStringUtils.isNotBlank(payment.getExpression()) && AonStringUtils.containsIgnoreCase(payment.getExpression(), "HIDE");
    	AgreementPaymentEditor paymentEditor = new AgreementPaymentEditor(payment, agreement.getExtraPayment(payment.getId())) {
			@Override
			protected void onAccept(Payment updatedPayment, AgreementExtra extra) {
				updatePaymentExpresion(isHide, payment, updatedPayment);
				agreement.replacePayment(payment);
				if(null != extra) agreement.replaceExtra(extra);
				setAgreementPayment(agreement);
				setHasChange(true);
			}

			private void updatePaymentExpresion(boolean isHide, Payment selectedPayment, Payment updatedPayment) {
				selectedPayment.setExpression(Boolean.TRUE.equals(isHide) ? showHidePayment(updatedPayment.getDescription(), updatedPayment.getExpression()) : updatedPayment.getExpression());
				selectedPayment.setScope(Scope.SALARY);
				selectedPayment.setSalaryType(Type.SALARY);
			}

		};
		
		paymentEditor.setSaveButton();
	}

	private String getParsedExpression(String expression) {
		expression = AonStringUtils.isBlank(expression) ? expression : expression.replaceAll("HIDE\\(.*\\); ", "");
		return SpecialExpresion.parse(expression).getInput();
	}
	
	private boolean isHideExpression(Payment payment) {
		String expression = payment.getExpression();
		return !AonStringUtils.isBlank(expression) && AonStringUtils.containsIgnoreCase(expression, "HIDE") && AonStringUtils.startsWithIgnoreCase(expression, "HIDE");
	}
	
	public String showHidePayment(String description, String expression) {
		if(!AonStringUtils.isBlank(expression) && AonStringUtils.containsIgnoreCase(expression, "HIDE") && AonStringUtils.startsWithIgnoreCase(expression, "HIDE"))
			expression = expression.replaceAll("HIDE.*; ", "");
		else
			expression = "HIDE(\"<div>" + description + " oculto desde Convenio</div><div>&nbsp;</div><div class='aon-text-right'><span class='aon-icon aon-icon-logo'/>aon Solutions</div>\"); " + expression;
		
		return expression;
	}
	
	public void showHidePayment(Payment payment) {
		String expression = payment.getExpression();
		
		expression = !AonStringUtils.isBlank(expression) && AonStringUtils.containsIgnoreCase(expression, "HIDE") ?
				expression.replaceAll("HIDE.*; ", "") :
				"HIDE(\"<div>" + payment.getDescription() + " oculto desde Convenio</div><div>&nbsp;</div><div class='aon-text-right'><span class='aon-icon aon-icon-logo'/>aon Solutions</div>\"); " + expression;
		
		payment.setExpression(expression);
	}

	// ------------------------------------------ setAgreementPayment
	
	public void setAgreementPayment(AgreementInfo agreementIn) {
		agreement = agreementIn;
		toolbar.setTitle(agreement.getDescription());
		initAgreementPaymentDG();
	}
	
	// ----------------------------------------------- InitContractConceptCalcs
	
	public void initAgreementPaymentDG() {	
		// Create a data provider.
		ListDataProvider<Payment> paymentDataProvider = new ListDataProvider<>();

	    // Connect the table to the data provider.
		paymentDataProvider.addDataDisplay(agreementPaymentDG);
	    
	    // Add the data to the data provider, which automatically pushes it to the widget.
	    List<Payment> paymentListAux = paymentDataProvider.getList();
	    paymentListAux.clear();
	    
	    this.paymentList = new ArrayList<>(agreement.getPaymentsAndHides());
	    
	    for (Payment payment : this.paymentList) {
	    	paymentListAux.add(payment);
	    }   
		
		addSortColums(paymentListAux);
	    
		// Set page size
		agreementPaymentDG.setPageSize(paymentListAux.size());
		
		setScrollDGHeight();
		agreementPaymentDG.redraw();
	}

	private void addSortColums(List<Payment> paymentList) {
		ListHandler<Payment> columnSortHandler = new ListHandler<>(paymentList);
		
		columnSortHandler.setComparator(agreementPaymentDG.getColumn(1), 
	    	(o1, o2) -> compareString(o1, o2, o1.getType().ordinal()+"", o2.getType().ordinal()+""));
	    
	    columnSortHandler.setComparator(agreementPaymentDG.getColumn(2), 
	    	(o1, o2) -> compareString(o1, o2, o1.getDescription(), o2.getDescription()));
	    
	    columnSortHandler.setComparator(agreementPaymentDG.getColumn(3), 
	    	(o1, o2) -> compareString(o1, o2, o1.getExpression(), o2.getExpression()));
	    
	    agreementPaymentDG.addColumnSortHandler(columnSortHandler);

	    // We know that the data is sorted alphabetically by default.
	    agreementPaymentDG.getColumn(1).setDefaultSortAscending(false);
	    agreementPaymentDG.getColumnSortList().push(agreementPaymentDG.getColumn(1));   
	}
	
	private int compareString(Object o1, Object o2, String s1, String s2) {
		if (o1 == o2) return 0;
		else if (o1 == null) return -1;
		else if (o2 == null) return 1;
		else
        	return s1.compareTo(s2);
	}
	
	// ------------------------------------------ toolbar

	private void createToolbar() {
		toolbar = new AonToolbar("Devengos");
		
		saveBtn = new AonToolbarSmallButton(AON.MSG.saveAction(), AON.CSS.aonIconSave());
		saveBtn.addClickHandler(e -> {
			showLoading("Guardando convenio " + toolbar.getTitle() + " ...");
			setHasChange(false);
			onSaved();
		});
		
		AonExpandButton addPaymentButton = new AonExpandButton("A\u00F1adir Pago", AON.CSS.aonIconAddBlock()) {
			
			@Override
			public void onExpandClick(ClickEvent event) {
				NativeEvent nativeEvent = event.getNativeEvent();
				contextMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
				contextMenu.show();
			}
			
			@Override
			public void onDefaultClick(ClickEvent evet) {
				new AgreementPaymentWizard(agreement.getPayments(), null) {
					
					@Override
					protected void onAccept(Payment payment) {
						agreement.addPayment(payment);
						setAgreementPayment(agreement);
						setHasChange(true);
					}

					@Override
					protected void onExtraAccept(Payment payment, Extra extra) {
						agreement.addPayment(payment);
						
						if(null != extra)
							agreement.addExtra(extra);
						
						setAgreementPayment(agreement);
						setHasChange(true);
						
					}

					@Override
					protected void onGtzdoAccept(List<Payment> payments) {
						if(!payments.isEmpty()) {
							for(Payment payment : payments)
								agreement.addPayment(payment);
						}
						setAgreementPayment(agreement);
						setHasChange(true);
					}
				};
			}
		};
		
		toolbar.add(saveBtn);
		toolbar.add(addPaymentButton);
		
	}
	
	// ------------------------------------------ HasChange
	
	public boolean hasChange() {
		return hasChange;
	}

	public void setHasChange(boolean hasChange) {
		this.hasChange = hasChange;
		saveBtn.setEnabled(hasChange());
		if(!hasChange()) {
			saveBtn.getElement().getStyle().setDisplay(Display.BLOCK);
			saveBtn.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		}
	}
	
	// ------------------------------------------ Abstract methods
	
	public abstract void onSaved();	
	
	// ------------------------------------------------- Aon Messages panel

	public void showSuccess(String title, String message) {
		Map<String, String> successMap = new HashMap<>();
		successMap.put(title, message);
		AonMessagePanel.showSuccess(messagePanel, successMap);
	}

	public void showError(String title, String message) {
		Map<String, String> errorMap = new HashMap<>();
		errorMap.put(title, message);
		AonMessagePanel.showError(messagePanel, errorMap);
	}

	public void showLoading(String message) {
		AonMessagePanel.showLoading(messagePanel, message);
	}
	
}
