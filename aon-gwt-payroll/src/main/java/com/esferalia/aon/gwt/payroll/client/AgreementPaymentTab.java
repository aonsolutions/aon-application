package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.gwt.payroll.shared.AgreementInfo;
import com.esferalia.aon.gwt.payroll.shared.AgreementInfo.AgreementExtra;
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Salary.Type;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;
import com.esferalia.aon.gwt.payroll.shared.SpecialExpresion;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DisclosurePanel;
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
	
	class AddAonPaymentCommand implements ScheduledCommand {

		@Override
		public void execute() {
			new AgreementSuggestPaymentDialog() {
				
				@Override
				protected void onAccept(Payment payment) {
					payment.setModify(true);
					agreement.addPayment(payment);
					setAgreementPayment(agreement);
					setHasChange(true);
				}

				@Override
				protected void onManualEdition() {
					hide();
					
					new AgreementPaymentWizard(agreement.getPayments(), null) {
						
						@Override
						protected void onAccept(Payment payment) {
							payment.setModify(true);
							agreement.addPayment(payment);
							setAgreementPayment(agreement);
							setHasChange(true);
						}

						@Override
						protected void onExtraAccept(Payment payment, Extra extra) {
							payment.setModify(true);
							agreement.addPayment(payment);
							
							if(null != extra)
								agreement.addExtra(extra);
							
							checkPairExtras(payment, extra);
							
							setAgreementPayment(agreement);
							setHasChange(true);
							
						}

						private void checkPairExtras(Payment payment, Extra extra) {
							if(payment.getType().equals(Payment.Type.CRA_0004)) {
								Optional<Payment> searchPayment = agreement.getPayments().stream().filter(paymentIt -> !paymentIt.equals(payment) && paymentIt.getType().equals(Payment.Type.CRA_0004)).findAny();
								if(!searchPayment.isPresent()) {
									Payment associatedPayment = new Payment();
									Random rand = new Random();
									int newPaymentId = rand.nextInt(1000) * -1;
									if(newPaymentId > 0) newPaymentId = newPaymentId * -1;
									associatedPayment.setId(newPaymentId);
									associatedPayment.setDomain(payment.getDomain());
									associatedPayment.setModify(true);
									
									associatedPayment.setType(Payment.Type.CRA_0004);
									associatedPayment.setConceptId(payment.getConceptId());
									associatedPayment.setName(payment.getName());
									
									associatedPayment.setDescription(AonStringUtils.containsIgnoreCase(payment.getDescription(), "verano") ? "PAGA NAVIDAD" : "PAGA VERNAO");
									associatedPayment.setExpression(payment.getExpression());
									associatedPayment.setIrpfExpression(payment.getIrpfExpression());
									associatedPayment.setQuoteExpression(payment.getQuoteExpression());
									associatedPayment.setMonth(null);
									
									agreement.addPayment(associatedPayment);
									
									if(null != extra) {
										int newExtraId = rand.nextInt(1000) * -1;
										AgreementExtra associatedExtra = new AgreementExtra();
										associatedExtra.setId(newExtraId);
										
										associatedExtra.setDomain(payment.getDomain());
										associatedExtra.setAgreementPayment(associatedPayment.getId());
									
										if(AonStringUtils.containsIgnoreCase(extra.getIssueDate(), "06") || AonStringUtils.containsIgnoreCase(extra.getIssueDate(), "07")) {
											associatedExtra.setIssueDate("31/12");
											if(AonStringUtils.containsIgnoreCase(extra.getStartDate(), "-1")) {
												associatedExtra.setStartDate("01/01");
												associatedExtra.setEndDate("31/12");
											} else {
												associatedExtra.setStartDate("01/07");
												associatedExtra.setEndDate("31/12");
											}
										} else if(AonStringUtils.containsIgnoreCase(extra.getIssueDate(), "12")) {
											associatedExtra.setIssueDate("30/06");
											if(AonStringUtils.containsIgnoreCase(extra.getStartDate(), "01")) {
												associatedExtra.setStartDate("01/07 -1");
												associatedExtra.setEndDate("30/06");
											} else {
												associatedExtra.setStartDate("01/01");
												associatedExtra.setEndDate("30/06");
											}
										}
										

										agreement.addExtra(associatedExtra);
									}
								}
							}
						}

						@Override
						protected void onGtzdoAccept(List<Payment> payments) {
							if(!payments.isEmpty()) {
								for(Payment payment : payments) {
									payment.setModify(true);
									agreement.addPayment(payment);
								}
							}
							setAgreementPayment(agreement);
							setHasChange(true);
						}
					};
				}
				
			};
		}
	}
	
	class AddBasicSalaryCommand implements ScheduledCommand {

		@Override
		public void execute() {
			AgreementPaymentDialog dialog = new AgreementPaymentDialog(0) {
				
				@Override
				protected void onAccept(List<Payment> payments) {
					for(Payment payment : payments) {
						payment.setModify(true);
						agreement.addPayment(payment);
					}
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
					for(Payment payment : payments) {
						payment.setModify(true);
						agreement.addPayment(payment);
					}
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
					for(Payment payment : payments) {
						payment.setModify(true);
						agreement.addPayment(payment);
					}
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
					for(Payment payment : payments) {
						payment.setModify(true);
						agreement.addPayment(payment);
					}
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
					for(Payment payment : payments) {
						payment.setModify(true);
						agreement.addPayment(payment);
					}
					
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
				
		private MenuItem addAonPayment = null;
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
		String modify();
	}
	
	@UiField
	HTMLPanel messagePanel;
	
	@UiField(provided = true)
	AonToolbar toolbar;
	
	@UiField
	DisclosurePanel paymentDiscPanel;
	
	@UiField(provided = true)
	AonToolbarButton paymentDiscBtn;
	
	@UiField
	HTMLPanel paymentDiscPanelContent;
	
	@UiField(provided = true)
	DataGrid<Payment> agreementPaymentDG;
	
	@UiField
	DisclosurePanel extraDiscPanel;
	
	@UiField(provided = true)
	AonToolbarButton extraDiscBtn;
	
	@UiField
	HTMLPanel extraDiscPanelContent;
	
	@UiField(provided = true)
	DataGrid<Payment> agreementExtraPaymentDG;
	
	// ------------------------------------------ Variables
	
	private AgreementServiceAsyncDecorator agreementServiceAsync;
	private AgreementInfo agreement;
	private List<Payment> paymentList;
	private List<Payment> paymentExtraList;
	
	private boolean hasChange = false;
	private AonToolbarSmallButton saveBtn;
	
	private int collapseWidth = 0;
	private int disclouroseHeight = 0;
	
	// ------------------------------------------ Constructor

	protected AgreementPaymentTab() {
		createToolbar();
		provideAgreementPaymentDG();
		provideAgreementExtraPaymentDG();
		createDiscPanelButtons();
		initWidget(uiBinder.createAndBindUi(this));
		
		AgreementServiceAsync agreementServiceRaw = GWT.create(AgreementService.class);
		agreementServiceAsync = new AgreementServiceAsyncDecorator(agreementServiceRaw);
		
		initDiscPanels();
	}
	
	public void setScrollDGHeight() {
//		Window.alert("disclouroseHeight : " + disclouroseHeight +
//				"\ncollapseWidth : " + collapseWidth);
		agreementPaymentDG.setHeight(disclouroseHeight + "px");
		agreementPaymentDG.setWidth(collapseWidth + "px");
		agreementExtraPaymentDG.setHeight(disclouroseHeight + "px");
		agreementExtraPaymentDG.setWidth(collapseWidth + "px");
	}
	
	public void setOpenCollapseScrollDGHeight() {
//		Window.alert("paymentDiscPanelContent Height : " + paymentDiscPanelContent.getOffsetHeight() +
//		"\npaymentDiscPanelContent Width : " + paymentDiscPanelContent.getOffsetWidth() +
//		"\nextraDiscPanelContent Height : " + extraDiscPanelContent.getOffsetHeight() + 
//		"\nextraDiscPanelContent Width : " + extraDiscPanelContent.getOffsetWidth());
		agreementPaymentDG.setHeight((paymentDiscPanelContent.getOffsetHeight() - 15) + "px");
		agreementPaymentDG.setWidth((paymentDiscPanelContent.getOffsetWidth() - 30) + "px");
		agreementExtraPaymentDG.setHeight((extraDiscPanelContent.getOffsetHeight() - 10) + "px");
		agreementExtraPaymentDG.setWidth((extraDiscPanelContent.getOffsetWidth() - 30) + "px");
	}
	
	// ------------------------------------------ DisclosurePanel

	private void initDiscPanels() {
		paymentDiscPanel.setAnimationEnabled(true);
		paymentDiscPanel.addOpenHandler(e -> {
			handleIcon(paymentDiscBtn, true);
			
			extraDiscPanel.setOpen(false);
			handleIcon(extraDiscBtn, false);
			
			initAgreementPaymentDG();
			setScrollDGHeight();
		});
		paymentDiscPanel.addCloseHandler(e -> handleIcon(paymentDiscBtn, false));
		paymentDiscPanelContent.setHeight((Window.getClientHeight() - 375) + "px");
	
		extraDiscPanel.setAnimationEnabled(true);
		extraDiscPanel.addOpenHandler(e -> {
			handleIcon(extraDiscBtn, true);
			
			paymentDiscPanel.setOpen(false);
			handleIcon(paymentDiscBtn, false);
			
			initAgreementExtraPaymentDG();
			setScrollDGHeight();
		});
		extraDiscPanel.addCloseHandler(e -> handleIcon(extraDiscBtn, false));
		extraDiscPanelContent.setHeight((Window.getClientHeight() - 375) + "px");
	}

	private void createDiscPanelButtons() {
		paymentDiscBtn = new AonToolbarButton("Desplegar Devengos", AON.CSS.aonIconRight());
		extraDiscBtn = new AonToolbarButton("Desplegar Extras", AON.CSS.aonIconRight());
		
		paymentDiscBtn.addClickHandler(e -> handleIcon(paymentDiscBtn, paymentDiscPanel.isOpen()));
		extraDiscBtn.addClickHandler(e -> handleIcon(extraDiscBtn, extraDiscPanel.isOpen()));
	}

	private void handleIcon(AonToolbarButton button, boolean open) {
		if(open) {
			button.removeStyleName(AON.CSS.aonIconRight());
			button.addStyleName(AON.CSS.aonIconDown());
			
			if(button.equals(paymentDiscBtn)) button.setTitle("Colapsar Devengos");
			if(button.equals(extraDiscBtn)) button.setTitle("Colapsar Extras");
		} else {
			button.removeStyleName(AON.CSS.aonIconDown());
			button.addStyleName(AON.CSS.aonIconRight());
			
			if(button.equals(paymentDiscBtn)) button.setTitle("Desplegar Devengos");
			if(button.equals(extraDiscBtn)) button.setTitle("Desplegar Extras");
		}
	}
	
	// ----------------------------------------------- ProvideContractConceptCalcDG
	
	private void provideAgreementExtraPaymentDG() {
		paymentExtraList = Collections.emptyList();
		
		// Resource Style CellTable
		agreementExtraPaymentDG = new CustomDataGrid<>(Integer.MAX_VALUE, Payment.KEY_PROVIDER);
		
		//Do not refresh the headers every time the dataGrid is updated.
		agreementExtraPaymentDG.setAutoHeaderRefreshDisabled(true);
		
		// Set the message to display when the table is empty.
		agreementExtraPaymentDG.setEmptyTableWidget(new Label(("No existen extras").toUpperCase()));
		
		// Initialize the columns.
	    addPaymentsExtraDGColumns();
	    
	    new ListDataProvider<Payment>(Collections.emptyList()).addDataDisplay(agreementExtraPaymentDG);
	    
	}
	
	private void addPaymentsExtraDGColumns() {
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
					if(payment.isModify())
						sb.appendHtmlConstant("<button type=\"button\" class=\"aon_button aon_table_button aon_icon_arrow_right_modify\" style=\"border: none !important; height: 20px;\" title=\"Editar\"></button>");
					else
						sb.appendHtmlConstant("<button type=\"button\" class=\"aon_button aon_table_button aon_icon_right\" style=\"border: none !important; height: 20px;\" title=\"Editar\"></button>");
				}
			}
		};
		
		editColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		agreementExtraPaymentDG.setColumnWidth(editColumn, 5, Unit.PCT);
		
		// Code columns.
		Column<Payment, String> codeColumn = new Column<Payment, String>(new TextCell()) {
			@Override
	        public String getValue(Payment payment) {
				return null == payment.getType() ? "" : AonStringUtils.leftPad(payment.getType().getCode() + "", 4, '0');
	        }
		};

		codeColumn.setSortable(true);
		codeColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		agreementExtraPaymentDG.setColumnWidth(codeColumn, 10, Unit.PCT);

	    // Description column.
		Column<Payment, String> descriptionColumn = new Column<Payment, String>(new TextCell()) {
			@Override
			public String getValue(Payment payment) {
				return payment.getDescription();
			}
		};
		
		descriptionColumn.setSortable(true);
		agreementExtraPaymentDG.setColumnWidth(descriptionColumn, 30, Unit.PCT);
		
		// Pay column.
		TextColumn<Payment> payColumn = new TextColumn<Payment>() {
			@Override
			public String getValue(Payment payment) {
				return getPayType(payment);
			}
			
			@Override
			public void render(Context context, Payment payment, SafeHtmlBuilder sb) {
				sb.appendHtmlConstant("<div title=\"" + getPayDescription(payment) + "\">" + getPayType(payment) + "</div>");
			}
		};
		
		payColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		agreementExtraPaymentDG.setColumnWidth(payColumn, 5, Unit.PCT);
		
		// Taxed column.
		TextColumn<Payment> taxedColumn = new TextColumn<Payment>() {
			@Override
			public String getValue(Payment payment) {
				return getTaxedType(payment.getIrpfExpression());
			}
			
			@Override
			public void render(Context context, Payment payment, SafeHtmlBuilder sb) {
				sb.appendHtmlConstant("<div title=\"" + getTaxedDescription(payment.getIrpfExpression()) + "\">" + getTaxedType(payment.getIrpfExpression()) + "</div>");
			}
		};
		
		taxedColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		agreementExtraPaymentDG.setColumnWidth(taxedColumn, 5, Unit.PCT);
		 
		// Quote column.
		TextColumn<Payment> quoteColumn = new TextColumn<Payment>() {
			@Override
			public String getValue(Payment payment) {
				return getQuoteType(payment.getQuoteExpression());
			}
			
			@Override
			public void render(Context context, Payment payment, SafeHtmlBuilder sb) {
				sb.appendHtmlConstant("<div title=\"" + getQuoteDescription(payment.getQuoteExpression()) + "\">" + getQuoteType(payment.getQuoteExpression()) + "</div>");
			}
		};
		
		quoteColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		agreementExtraPaymentDG.setColumnWidth(quoteColumn, 5, Unit.PCT);
	    
	    // Expression column.
	    Column<Payment, String> expressionColumn = new Column<Payment, String>(new TextCell()) {
	    	@Override
	        public String getValue(Payment payment) {
	    		return getParsedExpression(payment.getExpression());
	        }
		};

	    expressionColumn.setSortable(true);
	    agreementExtraPaymentDG.setColumnWidth(expressionColumn, 30, Unit.PCT);
	    
	    // Visibility column.
	    ActionCell<Payment> visibilityActionCell = new ActionCell<>("", payment -> {
	    	showHidePayment(payment);
	    	payment.setModify(true);
	    	agreementExtraPaymentDG.redraw();
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
		agreementExtraPaymentDG.setColumnWidth(visibilityColumn, 5, Unit.PCT);
	    
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
					sb.appendHtmlConstant("<button type=\"button\" id=\"gwt-debug-deletePaymentTabButton-" + context.getIndex() + "\" class=\"aon_button aon_table_button aon_icon_delete\" style=\"border: none !important; height: 20px;\" title=\"Eliminar\"></button>");
				}
			}
		};
		
		deleteColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		agreementExtraPaymentDG.setColumnWidth(deleteColumn, 5, Unit.PCT);
		
		agreementExtraPaymentDG.setRowStyles((payment, rowIdx) -> {
			if(payment.isModify()) return style.modify();
			else return null;
		});
		
	    // Add the columns.
		agreementExtraPaymentDG.addColumn(editColumn, "");
		agreementExtraPaymentDG.addColumn(codeColumn, "CRA");
		agreementExtraPaymentDG.addColumn(descriptionColumn, "Descripci\u00F3n");
		agreementExtraPaymentDG.addColumn(payColumn, "Pago");
		agreementExtraPaymentDG.addColumn(taxedColumn, "Tributa");
		agreementExtraPaymentDG.addColumn(quoteColumn, "Cotiza");
		agreementExtraPaymentDG.addColumn(expressionColumn, "Expresi\u00F3n");
		
		agreementExtraPaymentDG.addColumn(visibilityColumn, "Estado");  
		agreementExtraPaymentDG.addColumn(deleteColumn, "");  
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
					if(payment.isModify())
						sb.appendHtmlConstant("<button type=\"button\" class=\"aon_button aon_table_button aon_icon_arrow_right_modify\" style=\"border: none !important; height: 20px;\" title=\"Editar\"></button>");
					else
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
		agreementPaymentDG.setColumnWidth(descriptionColumn, 30, Unit.PCT);
		
		// Pay column.
		TextColumn<Payment> payColumn = new TextColumn<Payment>() {
			@Override
			public String getValue(Payment payment) {
				return getPayType(payment);
			}
			
			@Override
			public void render(Context context, Payment payment, SafeHtmlBuilder sb) {
				sb.appendHtmlConstant("<div title=\"" + getPayDescription(payment) + "\">" + getPayType(payment) + "</div>");
			}
		};
		
		payColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		agreementPaymentDG.setColumnWidth(payColumn, 5, Unit.PCT);
		
		// Taxed column.
		TextColumn<Payment> taxedColumn = new TextColumn<Payment>() {
			@Override
			public String getValue(Payment payment) {
				return getTaxedType(payment.getIrpfExpression());
			}
			
			@Override
			public void render(Context context, Payment payment, SafeHtmlBuilder sb) {
				sb.appendHtmlConstant("<div title=\"" + getTaxedDescription(payment.getIrpfExpression()) + "\">" + getTaxedType(payment.getIrpfExpression()) + "</div>");
			}
		};
		
		taxedColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		agreementPaymentDG.setColumnWidth(taxedColumn, 5, Unit.PCT);
		 
		// Quote column.
		TextColumn<Payment> quoteColumn = new TextColumn<Payment>() {
			@Override
			public String getValue(Payment payment) {
				return getQuoteType(payment.getQuoteExpression());
			}
			
			@Override
			public void render(Context context, Payment payment, SafeHtmlBuilder sb) {
				sb.appendHtmlConstant("<div title=\"" + getQuoteDescription(payment.getQuoteExpression()) + "\">" + getQuoteType(payment.getQuoteExpression()) + "</div>");
			}
		};
		
		quoteColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		agreementPaymentDG.setColumnWidth(quoteColumn, 5, Unit.PCT);
	    
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
	    	payment.setModify(true);
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
					sb.appendHtmlConstant("<button type=\"button\" id=\"gwt-debug-deletePaymentTabButton-" + context.getIndex() + "\" class=\"aon_button aon_table_button aon_icon_delete\" style=\"border: none !important; height: 20px;\" title=\"Eliminar\"></button>");
				}
			}
		};
		
		deleteColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		agreementPaymentDG.setColumnWidth(deleteColumn, 5, Unit.PCT);
		
		agreementPaymentDG.setRowStyles((payment, rowIdx) -> {
			if(payment.isModify()) return style.modify();
			else return null;
		});
		
	    // Add the columns.
		agreementPaymentDG.addColumn(editColumn, "");
		agreementPaymentDG.addColumn(codeColumn, "CRA");
		agreementPaymentDG.addColumn(descriptionColumn, "Descripci\u00F3n");
		agreementPaymentDG.addColumn(payColumn, "Pago");
		agreementPaymentDG.addColumn(taxedColumn, "Tributa");
		agreementPaymentDG.addColumn(quoteColumn, "Cotiza");
		agreementPaymentDG.addColumn(expressionColumn, "Expresi\u00F3n");
		
		agreementPaymentDG.addColumn(visibilityColumn, "Estado");  
		agreementPaymentDG.addColumn(deleteColumn, "");  
	}
	
	private void openDialog(Payment payment) {
		boolean isHide = AonStringUtils.isNotBlank(payment.getExpression()) && AonStringUtils.containsIgnoreCase(payment.getExpression(), "HIDE");
    	AgreementPaymentEditor editor = new AgreementPaymentEditor(payment, agreement.getExtraPayment(payment.getId()), agreement.getPayments(), agreement.getExtras()) {
			@Override
			protected void onAccept(Payment updatedPayment, AgreementExtra extra, Payment associatedPayment, AgreementExtra associatedExtra) {
//				Window.alert(null == extra ? "---- Extra NULL ----" : "---- Extra ----\nId : " + extra.getId() + "\nisDeleted : " + extra.isDeleted()
//				 + "\nstartDate : " + extra.getStartDate() + "\nendDate : " + extra.getEndDate() + "\nissueDate : " + extra.getIssueDate() + "\nagreementPayment : " + extra.getAgreementPayment());
//				
//				Window.alert(null == associatedExtra ? "---- Associated Extra NULL ----" : "---- Associated Extra ----\nId : " + associatedExtra.getId() + "\nisDeleted : " + associatedExtra.isDeleted()
//				 + "\nstartDate : " + associatedExtra.getStartDate() + "\nendDate : " + associatedExtra.getEndDate() + "\nissueDate : " + associatedExtra.getIssueDate() + "\nagreementPayment : " + associatedExtra.getAgreementPayment());
				
				updatePaymentExpresion(isHide, payment, updatedPayment);
				agreement.replacePayment(payment);
				
				if(null != extra && null != extra.getId() && extra.getId() > 0) agreement.replaceExtra(extra); 
				if(null != extra && null != extra.getId()) agreement.addExtra(extra);
				
				if(null != associatedExtra && null != associatedExtra.getId() && associatedExtra.getId() > 0) agreement.replaceExtra(associatedExtra); 
				
				if(null != associatedPayment) agreement.addPayment(associatedPayment);
				if(null != associatedExtra && (null == associatedExtra.getId() || associatedExtra.getId() < 0)) agreement.addExtra(associatedExtra);
				
//				agreement.getPayments().forEach(paymentIt -> Window.alert("---- Payment ----\nId : " + paymentIt.getId() + "\nisDeleted : " + paymentIt.isDeleted()
//				 + "\ndescription : " + paymentIt.getDescription() + "\nexpression : " + paymentIt.getExpression() + "\nmonth : " + paymentIt.getMonth()));
//				
//				agreement.getExtras().forEach(extraIt -> Window.alert("---- Extra ----\nId : " + extraIt.getId() + "\nisDeleted : " + extraIt.isDeleted()
//				 + "\nstartDate : " + extraIt.getStartDate() + "\nendDate : " + extraIt.getEndDate() + "\nissueDate : " + extraIt.getIssueDate()));
				
				setAgreementPayment(agreement);
				setHasChange(true);
			}

			private void updatePaymentExpresion(boolean isHide, Payment selectedPayment, Payment updatedPayment) {
				selectedPayment.setExpression(Boolean.TRUE.equals(isHide) ? showHidePayment(updatedPayment.getDescription(), updatedPayment.getExpression()) : updatedPayment.getExpression());
				selectedPayment.setScope(Scope.SALARY);
				selectedPayment.setSalaryType(Type.SALARY);
			}

			@Override
			protected void onSeniority(String seniorityExpression) {
				agreement.addSeniority(seniorityExpression);
			}

			@Override
			protected String getSeniority() {
				return agreement.getSeniority();
			}

		};
		
		editor.setContextProvider(agreement);
		
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
	
	private String getPayType(Payment payment) {
		AgreementExtra extra = agreement.getExtraPayment(payment.getId());
		
		if(	null == extra && 
				!payment.getType().equals(Payment.Type.CRA_0004) && 
				!payment.getType().equals(Payment.Type.CRA_0005)) return "";
			
		if(	null == extra && 
				(payment.getType().equals(Payment.Type.CRA_0004) || 
				payment.getType().equals(Payment.Type.CRA_0005))) return "P";
		
		return (extra == null || extra.isDeleted()) ? "P" : getExtraPeriod(extra);
	}
	
	private String getExtraPeriod(AgreementExtra extra) {
		if(AonStringUtils.containsIgnoreCase(extra.getStartDate(), "-1")) return "A";
		
		try {
			int startMonth = Integer.parseInt(extra.getStartDate().split("/")[1]);
			int endMonth = Integer.parseInt(extra.getEndDate().split("/")[1]);
		
			switch (endMonth - startMonth) {
			case 11:
				return "A";
			case 5:
				return "S";
			default:
				return (endMonth - startMonth) + "";
			}
		} catch (Exception e) {
			return "Revisar esta extra!!";
		}
	}
	
	private String getPayDescription(Payment payment) {
		AgreementExtra extra = agreement.getExtraPayment(payment.getId());
		
		if(	null == extra && 
				!payment.getType().equals(Payment.Type.CRA_0004) && 
				!payment.getType().equals(Payment.Type.CRA_0005)) return "";
			
		if(	null == extra && 
				(payment.getType().equals(Payment.Type.CRA_0004) || 
				payment.getType().equals(Payment.Type.CRA_0005))) return "Prorrateado";
		
		return (extra == null || extra.isDeleted()) ? "Prorrateado" : getExtraPeriodTitle(extra);
	}
	
	private String getExtraPeriodTitle(AgreementExtra extra) {
		if(AonStringUtils.containsIgnoreCase(extra.getStartDate(), "-1")) return "Anual";
		
		try {
			int startMonth = Integer.parseInt(extra.getStartDate().split("/")[1]);
			int endMonth = Integer.parseInt(extra.getEndDate().split("/")[1]);
			
			switch (endMonth - startMonth) {
			case 11:
				return "Anual";
			case 5:
				return "Semestral";
			default:
				return (endMonth - startMonth) + " meses";
			}
		} catch (Exception e) {
			return "Revisar esta extra!!";
		}
	}

	private String getTaxedType(String irpfExpression) {
		if(AonStringUtils.equalsIgnoreCase(irpfExpression, "_P")) return "T";
		if(AonStringUtils.equalsIgnoreCase(irpfExpression, "0.00")) return "E";
		if(AonStringUtils.containsIgnoreCase(irpfExpression, "BASE_CTA_ESP")) return "C";
		if(AonStringUtils.isNotBlank(irpfExpression)) return "P";
		return "N/D";
	}

	private String getTaxedDescription(String irpfExpression) {
		if(AonStringUtils.equalsIgnoreCase(irpfExpression, "_P")) return "Importe \u00cdntegro";
		if(AonStringUtils.equalsIgnoreCase(irpfExpression, "0.00")) return "Exento";
		if(AonStringUtils.containsIgnoreCase(irpfExpression, "BASE_CTA_ESP")) return "Ingreso a Cuenta";
		if(AonStringUtils.isNotBlank(irpfExpression)) return "Personalizado";
		return "No definido";
	}

	private String getQuoteType(String quoteExpression) {
		if(AonStringUtils.equalsIgnoreCase(quoteExpression, "_P")) return "T";
		if(AonStringUtils.equalsIgnoreCase(quoteExpression, "0.00")) return "E";
		if(AonStringUtils.containsIgnoreCase(quoteExpression, "PRORRATEAR")) return "PR";
		if(AonStringUtils.isNotBlank(quoteExpression)) return "P";
		return "N/D";
	}

	private String getQuoteDescription(String quoteExpression) {
		if(AonStringUtils.equalsIgnoreCase(quoteExpression, "_P")) return "Importe \u00cdntegro";
		if(AonStringUtils.equalsIgnoreCase(quoteExpression, "0.00")) return "Exento";
		if(AonStringUtils.containsIgnoreCase(quoteExpression, "PRORRATEAR")) return "Prorrateado";
		if(AonStringUtils.isNotBlank(quoteExpression)) return "Personalizado";
		return "No definido";
	}
	
	// ------------------------------------------ setAgreementPayment
	
	public void setAgreementPayment(AgreementInfo agreementIn) {
		agreement = agreementIn;
		toolbar.setTitle(agreement.getDescription());
		
		collapseWidth = Window.getClientWidth() - 415;
		disclouroseHeight = Window.getClientHeight() - 370;
		
		if(paymentDiscPanel.isOpen()) { 
			paymentDiscPanel.setOpen(false);
			paymentDiscPanel.setOpen(true);
		} else if(extraDiscPanel.isOpen()) {
			extraDiscPanel.setOpen(false);
			extraDiscPanel.setOpen(true);
		} else {
			paymentDiscPanel.setOpen(false);
			paymentDiscPanel.setOpen(true);
		}
	}
	
	// ----------------------------------------------- InitContractConceptCalcs
	
	public void initAgreementExtraPaymentDG() {	
		// Create a data provider.
		ListDataProvider<Payment> paymentDataProvider = new ListDataProvider<>();
	
	    // Connect the table to the data provider.
		paymentDataProvider.addDataDisplay(agreementExtraPaymentDG);
	    
	    // Add the data to the data provider, which automatically pushes it to the widget.
	    List<Payment> paymentListAux = paymentDataProvider.getList();
	    paymentListAux.clear();
	    
	    this.paymentExtraList = new ArrayList<>(agreement.getPaymentsExtraAndHides());
	    
	    for (Payment payment : this.paymentExtraList) {
	    	paymentListAux.add(payment);
	    }   
		
		addSortExtraColums(paymentListAux);
	    
		// Set page size
		agreementExtraPaymentDG.setPageSize(paymentListAux.size());
		
		agreementExtraPaymentDG.redraw();
	}
	
	private void addSortExtraColums(List<Payment> paymentList) {
		ListHandler<Payment> columnSortHandler = new ListHandler<>(paymentList);
		
		columnSortHandler.setComparator(agreementExtraPaymentDG.getColumn(1), 
	    	(o1, o2) -> compareString(o1, o2, o1.getType().ordinal()+"", o2.getType().ordinal()+""));
	    
	    columnSortHandler.setComparator(agreementExtraPaymentDG.getColumn(2), 
	    	(o1, o2) -> compareString(o1, o2, o1.getDescription(), o2.getDescription()));
	    
	    columnSortHandler.setComparator(agreementExtraPaymentDG.getColumn(3), 
	    	(o1, o2) -> compareString(o1, o2, o1.getExpression(), o2.getExpression()));
	    
	    agreementExtraPaymentDG.addColumnSortHandler(columnSortHandler);
	
	    // We know that the data is sorted alphabetically by default.
	    agreementExtraPaymentDG.getColumn(1).setDefaultSortAscending(false);
	    agreementExtraPaymentDG.getColumnSortList().push(agreementExtraPaymentDG.getColumn(1));   
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
		saveBtn.ensureDebugId("savePaymentButton");
		saveBtn.addClickHandler(e -> {
			showLoading("Guardando convenio " + toolbar.getTitle() + " ...");
			setHasChange(false);
			onSaved();
		});
		
		AonToolbarSmallButton addPaymentButton = new AonToolbarSmallButton("A\u00F1adir Devengo", AON.CSS.aonIconAdd());
		addPaymentButton.ensureDebugId("newPaymentButton");
		addPaymentButton.addClickHandler(e -> new AddAonPaymentCommand().execute());
		
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
