package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

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
import com.esferalia.aon.gwt.payroll.shared.AgreementInfo.AgreementOwner;
import com.esferalia.aon.gwt.payroll.shared.AgreementInfo.Level;
import com.esferalia.aon.gwt.payroll.shared.AgreementInfo.LevelData;
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.NumberVariable;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Salary.Type;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;
import com.esferalia.aon.gwt.payroll.shared.SpecialExpresion;
import com.esferalia.aon.gwt.payroll.shared.StringVariable;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.type.ContractType;
import com.esferalia.aon.occam.api.model.type.ContractType.ContractTypeRecord;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;
import com.google.gwt.view.client.ListDataProvider;

import net.aonsolutions.gwt.pdfjs.client.FullViewer;

public abstract class AgreementPreview extends Composite {
	
	// ------------------------------------------ UiBinder 

	private static AgreementPreviewUiBinder uiBinder = GWT.create(AgreementPreviewUiBinder.class);

	interface AgreementPreviewUiBinder extends UiBinder<Widget, AgreementPreview> {}
	
	// ------------------------------------------ ScheduledCommand
	
	class AddAonPaymentCommand implements ScheduledCommand {

		@Override
		public void execute() {
			new AgreementSuggestPaymentDialog() {
				
				@Override
				protected void onAccept(Payment payment) {
					payment.setModify(true);
					agreement.addPayment(payment);
					setAgreementPreview(agreement);
					setHasChange(true);
				}

				@Override
				protected void onManualEdition() {
					new AgreementPaymentWizard(agreement.getPayments(), null) {
						
						@Override
						protected void onAccept(Payment payment) {
							payment.setModify(true);
							agreement.addPayment(payment);
							setAgreementPreview(agreement);
							setHasChange(true);
						}

						@Override
						protected void onExtraAccept(Payment payment, Extra extra) {
							payment.setModify(true);
							agreement.addPayment(payment);
							
							if(null != extra)
								agreement.addExtra(extra);
							
							checkPairExtras(payment, extra);
							
							setAgreementPreview(agreement);
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
							setAgreementPreview(agreement);
							setHasChange(true);
						}
					};
				}
				
			};
		}
	}

	// ------------------------------------------ UiFields
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String categoryWidth();
		String cellWidth();
		String columnBorder();
		String dateNoSelected();
		String dateSelected();
		String datePanel();
		String dialogGlass();
		String dialogZIndex();
		String displayNone();
		String extraCellHeight();
		String flex();
		String gridCell();
		String gridTitle();
		String headerColor();
		String headerFixed();
		String headerFSize();
		String headerLevelFixed();
		String levelCell();
		String levelColumn();
		String levelDefaultValue();
		String levelFixed();
		String modify();
		String oddRow();
		String overflowEllipsis();
		String textCenter();
		String valueCell();
		String widthAll();
		
		String textBoxSalary();
		String datePickerPanel();
		String headerDeleteFixed();
		String deleteFixed();
		String elipsis();
	}
	
	@UiField
	HTMLPanel messagePanel;
	
	@UiField
	DeckLayoutPanel deckLayoutPanel;
	
	@UiField(provided = true)
	AonToolbar toolbar;
	
	@UiField
	TextBox description;
	
	@UiField
	TextBox ssNumber;
	
	@UiField
	HTMLPanel serviAgreementPanel;
	
	// LEVEL / CATEGORY
	
	@UiField
	DisclosurePanel levelDiscPanel;
	
	@UiField(provided = true)
	AonToolbarButton levelDiscBtn;
	
	@UiField
	HTMLPanel levelDiscPanelContent;
	
	@UiField
	ScrollPanel levelScrollPanel;
	
	@UiField
	Grid levelGrid;
	
	@UiField
	HTMLPanel agreementLevelMessage;
	
	// SALARY TABLE
	
	@UiField
	DisclosurePanel salaryDiscPanel;
	
	@UiField(provided = true)
	AonToolbarButton salaryDiscBtn;
	
	@UiField
	HTMLPanel salaryDiscPanelContent;
	
	@UiField
	ScrollPanel salaryScrollPanel;
	
	@UiField
	Grid salaryGrid;
	
	@UiField
	HTMLPanel agreementSalaryTableMessage;
	
	// PAYMENT
	
	@UiField
	DisclosurePanel paymentDiscPanel;
	
	@UiField(provided = true)
	AonToolbarButton paymentDiscBtn;
	
	@UiField
	HTMLPanel paymentDiscPanelContent;
	
	@UiField(provided = true)
	DataGrid<Payment> agreementPaymentDG;
	
	// EXTRA
	
	@UiField
	DisclosurePanel paymentExtraDiscPanel;
	
	@UiField(provided = true)
	AonToolbarButton paymentExtraDiscBtn;
	
	@UiField
	HTMLPanel extraDiscPanelContent;
	
	@UiField(provided = true)
	DataGrid<Payment> agreementExtraPaymentDG;
	
	@UiField(provided = true)
	AonToolbar toolbarSimulator;
	
	@UiField
	FullViewer printPreviewViewer;
	
	// ------------------------------------------ Variables
	
	private DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	private AgreementInfo agreement;
	
	private ListBox categoryLB;
	
	private AonToolbarButton printPreviewButton;
	private AonToolbarButton serviAgreementUpdateButton;
	private AonToolbarButton agreementInfoButton;
	private AonToolbarButton undoAllButton;
	
	private boolean hasChange = false;
	private boolean workplaceView = false;
	private boolean employeeView = false;
	private boolean showOldPayments = false;
	private boolean isOpenCollapse = false;
	private boolean readOnly = false;
	
	private int disclouroseHeight = 0;
	
	private List<Payment> paymentList;
	private List<Payment> paymentExtraList;
	
	// Toolbar
	
	private AonToolbarSmallButton saveBtn;
	private AonToolbarSmallButton newLevelBtn;
	private AonToolbarSmallButton newDateBtn;
	private ListBox datesLB;
	private AonToolbarSmallButton deleteDateBtn;
	private AonToolbarSmallButton variablesVisivility;
	private AonToolbarSmallButton addPaymentButton;
	private AonToolbarSmallButton showOlPaymentsButton;
	
	private ListBox tc2ListBox;
	private ListBox levelListBox;
	private TextBox partialTextBox;
	private ListBox groupListBox;
	private Label pdfLoaded;
	
	private ArrayList<Label> dateLabels;
	
	private Date selectedDate;
	
	private HandlerRegistration salaryOpenHandler;
	private HandlerRegistration paymentOpenHandler;
	private HandlerRegistration paymentExtraOpenHandler;
	
	// ------------------------------------------ Constructor

	protected AgreementPreview() {
		createToolbar();
		createToolbarSimulator();
		createDiscPanelButtons();
		
		provideAgreementPaymentDG();
		provideAgreementExtraPaymentDG();
		
		initWidget(uiBinder.createAndBindUi(this));
		
		description.ensureDebugId("descriptionTextBox");
		ssNumber.ensureDebugId("ssNumberTextBox");
		
		initDiscPanels();
		dateLabels = new ArrayList<>();
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
	    ActionCell<Payment> editActionCell = new ActionCell<>("", selectedPayment -> {if(!readOnly) openDialog(selectedPayment);});
	    
	    Column<Payment, Payment> editColumn = new Column<Payment, Payment>(editActionCell) {

			@Override
			public Payment getValue(Payment payment) {
				return payment;
			}
			
			@Override
			public void render(Context context, Payment payment, SafeHtmlBuilder sb) {
				if(null != payment && !readOnly) {
					if(payment.isModify())
						sb.appendHtmlConstant("<button type=\"button\" id=\"edit_payment_" + context.getIndex() + "\" class=\"aon_button aon_table_button aon_icon_arrow_right_modify\" style=\"border: none !important; height: 20px;\" title=\"Editar\"></button>");
					else
						sb.appendHtmlConstant("<button type=\"button\" id=\"edit_payment_" + context.getIndex() + "\" class=\"aon_button aon_table_button aon_icon_right\" style=\"border: none !important; height: 20px;\" title=\"Editar\"></button>");
				} else
					 sb.appendHtmlConstant("<div></div>");
				
			}
		};
		
		editColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		agreementPaymentDG.setColumnWidth(editColumn, 50, Unit.PX);
		
		// Code columns.
		Column<Payment, String> codeColumn = new Column<Payment, String>(new TextCell()) {
			@Override
	        public String getValue(Payment payment) {
				return null == payment.getType() ? "Revisar CRA" : AonStringUtils.leftPad(payment.getType().getCode() + "", 4, '0');
	        }
		};

		codeColumn.setSortable(true);
		codeColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		agreementPaymentDG.setColumnWidth(codeColumn, 80, Unit.PX);
		
		// Concept column.
	    Column<Payment, String> conceptColumn = new Column<Payment, String>(new TextCell()) {
	    	@Override
	        public String getValue(Payment payment) {
	    		return payment.getName();
	        }
	    	
	    	@Override
	    	public void render(Context context, Payment payment, SafeHtmlBuilder sb) {
	    		if(null != payment) {
	    			String name = payment.getName();
	    			if(AonStringUtils.isBlank(name)) name = "";
	    			sb.appendHtmlConstant("<div class=\"elipsis\" title=\"" + name + "\" >" + name + "</div>");
	    		}
	    	}
		};

		conceptColumn.setSortable(true);
		agreementPaymentDG.setColumnWidth(conceptColumn, 200, Unit.PX);

	    // Description column.
		Column<Payment, String> descriptionColumn = new Column<Payment, String>(new TextCell()) {
			@Override
			public String getValue(Payment payment) {
				return payment.getDescription();
			}
	    	
	    	@Override
	    	public void render(Context context, Payment payment, SafeHtmlBuilder sb) {
	    		if(null != payment) {
	    			String description = payment.getDescription();
	    			if(AonStringUtils.isBlank(description)) description = "";
	    			sb.appendHtmlConstant("<div class=\"elipsis\" title=\"" + description + "\" >" + description + "</div>");
	    		}
	    	}
		};
		
		descriptionColumn.setSortable(true);
	    
	    // Expression column.
	    Column<Payment, String> expressionColumn = new Column<Payment, String>(new TextCell()) {
	    	@Override
	        public String getValue(Payment payment) {
	    		return getParsedExpression(payment.getExpression());
	        }
	    	
	    	@Override
	    	public void render(Context context, Payment payment, SafeHtmlBuilder sb) {
	    		if(null != payment) {
	    			String expression = getParsedExpression(payment.getExpression());
	    			if(AonStringUtils.isBlank(expression)) expression = "";
	    			sb.appendHtmlConstant("<div class=\"elipsis\" title=\"" + expression + "\" >" + expression + "</div>");
	    		}
	    	}
		};

	    expressionColumn.setSortable(true);
		
		 // Info column.
	    ActionCell<Payment> infoActionCell = new ActionCell<>("", payment -> {
	    	// In the future maybe open a dialog widht info
	    });
	    
	    Column<Payment, Payment> infoColumn = new Column<Payment, Payment>(infoActionCell) {

			@Override
			public Payment getValue(Payment payment) {
				return payment;
			}
			
			@Override
			public void render(Context context, Payment payment, SafeHtmlBuilder sb) {
				if(null != payment) {
					String title = getInfoTitle(payment);
					if(AonStringUtils.isNotBlank(title))
						sb.appendHtmlConstant("<button type=\"button\" class=\"aon-editDataTable-button aon-no-margin aon-icon-info\" title=\"" + title + "\"></button>");
				}
			}
		};
		
		infoColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		agreementPaymentDG.setColumnWidth(infoColumn, 60, Unit.PX);
	    
	    // Visibility column.
	    ActionCell<Payment> visibilityActionCell = new ActionCell<>("", payment -> {
	    	if(!readOnly) {
		    	showHidePayment(payment);
		    	payment.setModify(true);
		    	agreementPaymentDG.redraw();
				setHasChange(true);
	    	}
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
		agreementPaymentDG.setColumnWidth(visibilityColumn, 60, Unit.PX);
	    
	    // Delete column.
	    ActionCell<Payment> deleteActionCell = new ActionCell<>("", payment -> {
	    	if(!readOnly) {
		    	AonDialog deleteDialog = new AonDialog("Eliminar concepto", new HTML("\u00BFDesea eliminar el concepto seleccionado\u003F"));
		    	deleteDialog.confirm(new AonAcceptDialogCallback() {
					
					@Override
					public void onCancel() {
						// Nothing to do here
					}
					
					@Override
					public void onAccept() {
						agreement.deletePayment(payment);
						setAgreementPreview(agreement);
						setHasChange(true);
					}
				});
	    	}
	    }); 
	    
	    Column<Payment, Payment> deleteColumn = new Column<Payment, Payment>(deleteActionCell) {

			@Override
			public Payment getValue(Payment payment) {
				return payment;
			}
			
			@Override
			public void render(Context context, Payment payment, SafeHtmlBuilder sb) {
				if(null != payment && !readOnly) {
					sb.appendHtmlConstant("<button type=\"button\" id=\"gwt-debug-deletePaymentTabButton-" + context.getIndex() + "\" class=\"aon_button aon_table_button aon_icon_delete\" style=\"border: none !important; height: 20px;\" title=\"Eliminar\"></button>");
				} else
					sb.appendHtmlConstant("<div></div>");
			}
		};
		
		deleteColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		agreementPaymentDG.setColumnWidth(deleteColumn, 50, Unit.PX);
		
		agreementPaymentDG.setRowStyles((payment, rowIdx) -> {
			if(payment.isModify()) return style.modify();
			else return null;
		});
		
	    // Add the columns.
		agreementPaymentDG.addColumn(editColumn, "");
		agreementPaymentDG.addColumn(codeColumn, "CRA");
		agreementPaymentDG.addColumn(conceptColumn, "Concepto");
		agreementPaymentDG.addColumn(descriptionColumn, "Descripci\u00F3n");
		agreementPaymentDG.addColumn(expressionColumn, "Expresi\u00F3n");
		
		agreementPaymentDG.addColumn(infoColumn, ""); 
		agreementPaymentDG.addColumn(visibilityColumn, "Estado");  
		agreementPaymentDG.addColumn(deleteColumn, "");  
	}
	
	private void openDialog(Payment payment) {
		boolean isHide = AonStringUtils.isNotBlank(payment.getExpression()) && AonStringUtils.containsIgnoreCase(payment.getExpression(), "HIDE") && AonStringUtils.startsWithIgnoreCase(payment.getExpression(), "HIDE");
		Optional<Date> startDate = agreement.getSortedDates().stream().findFirst();
    	new AgreementPaymentEditor(payment, agreement.getExtraPayment(payment.getId()), agreement.getPayments(), startDate.get()) {
			@Override
			protected void onAccept(Payment updatedPayment, AgreementExtra extra, Payment associatedPayment, AgreementExtra associatedExtra) {
				updatePaymentExpresion(isHide, payment, updatedPayment);
				agreement.replacePayment(payment);
				
				if(null != extra && null != extra.getId() && extra.getId() > 0) agreement.replaceExtra(extra); 
				if(null != extra && null != extra.getId()) agreement.addExtra(extra);
				
				if(null != associatedExtra && null != associatedExtra.getId() && associatedExtra.getId() > 0) agreement.replaceExtra(associatedExtra); 
				
				if(null != associatedPayment) agreement.addPayment(associatedPayment);
				if(null != associatedExtra && (null == associatedExtra.getId() || associatedExtra.getId() < 0)) agreement.addExtra(associatedExtra);
				
				setAgreementPreview(agreement);
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
		
	}
	
	private boolean isHideExpression(Payment payment) {
		String expression = payment.getExpression();
		return !AonStringUtils.isBlank(expression) && AonStringUtils.containsIgnoreCase(expression, "HIDE") && AonStringUtils.startsWithIgnoreCase(expression, "HIDE");
	}
	
	public String showHidePayment(String description, String expression) {
		if(!AonStringUtils.isBlank(expression) && AonStringUtils.containsIgnoreCase(expression, "HIDE") && AonStringUtils.startsWithIgnoreCase(expression, "HIDE"))
			expression = expression.replaceAll("HIDE\\(.*\\);\\s", "");
		else
			expression = "HIDE(\"<div>" + description + " oculto desde Convenio</div><div>&nbsp;</div><div class='aon-text-right'><span class='aon-icon aon-icon-logo'/>aon Solutions</div>\"); " + expression;
		
		return expression;
	}
	
	public void showHidePayment(Payment payment) {
		String expression = payment.getExpression();
		
		expression = !AonStringUtils.isBlank(expression) && AonStringUtils.containsIgnoreCase(expression, "HIDE") && AonStringUtils.startsWithIgnoreCase(expression, "HIDE") ?
				expression.replaceAll("HIDE\\(.*\\);\\s", "") :
				"HIDE(\"<div>" + payment.getDescription() + " oculto desde Convenio</div><div>&nbsp;</div><div class='aon-text-right'><span class='aon-icon aon-icon-logo'/>aon Solutions</div>\"); " + expression;
		
		payment.setExpression(expression);
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
	    ActionCell<Payment> editActionCell = new ActionCell<>("", selectedPayment -> {if(!readOnly) openDialog(selectedPayment);});
	    
	    Column<Payment, Payment> editColumn = new Column<Payment, Payment>(editActionCell) {

			@Override
			public Payment getValue(Payment payment) {
				return payment;
			}
			
			@Override
			public void render(Context context, Payment payment, SafeHtmlBuilder sb) {
				if(null != payment && !readOnly) {
					if(payment.isModify())
						sb.appendHtmlConstant("<button type=\"button\" class=\"aon_button aon_table_button aon_icon_arrow_right_modify\" style=\"border: none !important; height: 20px;\" title=\"Editar\"></button>");
					else
						sb.appendHtmlConstant("<button type=\"button\" class=\"aon_button aon_table_button aon_icon_right\" style=\"border: none !important; height: 20px;\" title=\"Editar\"></button>");
				} else
					sb.appendHtmlConstant("<div></div>");
			}
		};
		
		editColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		agreementExtraPaymentDG.setColumnWidth(editColumn, 50, Unit.PX);
		
		// Pay date columns.
		Column<Payment, String> payDateColumn = new Column<Payment, String>(new TextCell()) {
			@Override
	        public String getValue(Payment payment) {
				AgreementExtra extra = agreement.getExtraPayment(payment.getId());
				if(null != extra && !extra.isDeleted() && payment.getType().equals(Payment.Type.CRA_0004))
					return extra.getIssueDate() + (readOnly ? " (" + getPayDescription(payment) + ")" : "");
				else return "Prorrat.";
				
	        }
		};

		payDateColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		agreementExtraPaymentDG.setColumnWidth(payDateColumn, 100, Unit.PX);
		
		// Concept column.
	    Column<Payment, String> conceptColumn = new Column<Payment, String>(new TextCell()) {
	    	@Override
	        public String getValue(Payment payment) {
	    		return payment.getName();
	        }
	    	
	    	@Override
	    	public void render(Context context, Payment payment, SafeHtmlBuilder sb) {
	    		if(null != payment) {
	    			String name = payment.getName();
	    			if(AonStringUtils.isBlank(name)) name = "";
	    			sb.appendHtmlConstant("<div class=\"elipsis\" title=\"" + name + "\" >" + name + "</div>");
	    		}
	    	}
		};

		conceptColumn.setSortable(true);
	    agreementExtraPaymentDG.setColumnWidth(conceptColumn, 200, Unit.PX);

	    // Description column.
		Column<Payment, String> descriptionColumn = new Column<Payment, String>(new TextCell()) {
			@Override
			public String getValue(Payment payment) {
				return payment.getDescription();
			}
	    	
	    	@Override
	    	public void render(Context context, Payment payment, SafeHtmlBuilder sb) {
	    		if(null != payment) {
	    			String description = payment.getDescription();
	    			if(AonStringUtils.isBlank(description)) description = "";
	    			sb.appendHtmlConstant("<div class=\"elipsis\" title=\"" + description + "\" >" + description + "</div>");
	    		}
	    	}
		};
		
		descriptionColumn.setSortable(true);
	    
	    // Expression column.
	    Column<Payment, String> expressionColumn = new Column<Payment, String>(new TextCell()) {
	    	@Override
	        public String getValue(Payment payment) {
	    		return getParsedExpression(payment.getExpression());
	        }
	    	
	    	@Override
	    	public void render(Context context, Payment payment, SafeHtmlBuilder sb) {
	    		if(null != payment) {
	    			String expression = getParsedExpression(payment.getExpression());
	    			if(AonStringUtils.isBlank(expression)) expression = "";
	    			sb.appendHtmlConstant("<div class=\"elipsis\" title=\"" + expression + "\" >" + expression + "</div>");
	    		}
	    	}
		};

	    expressionColumn.setSortable(true);
	    
	    List<String> periodicities = new ArrayList<>();
	    periodicities.add("Anual");
	    periodicities.add("Semestral");
	    periodicities.add("Prorrat.");
	    
	    SelectionCell periodicityCell = new SelectionCell(periodicities);
	    Column<Payment, String> periodicityColumn = new Column<Payment, String>(periodicityCell) {
	    	@Override
	    	public String getValue(Payment payment) {
	    		return getPayLongDescription(payment);
	    	}
	    	
	    	@Override
	    	public void render(Context context, Payment payment, SafeHtmlBuilder sb) {
	    		AgreementExtra extra = agreement.getExtraPayment(payment.getId());
	    		if(null == extra || extra.isDeleted() || readOnly) sb.appendHtmlConstant("<div></div>");
	    		else super.render(context, payment, sb);
	    	}
		};
		
		periodicityColumn.setFieldUpdater(new FieldUpdater<Payment, String>() {
			
			@Override
			public void update(int index, Payment payment, String periodicity) {
				if(!readOnly) {
					checkPaymentExtra(payment, periodicity);
					agreementExtraPaymentDG.redraw();
					setHasChange(true);
				}
			}
		});
		
		agreementExtraPaymentDG.setColumnWidth(periodicityColumn, 120, Unit.PX);
	    
	    // Info column.
	    ActionCell<Payment> infoActionCell = new ActionCell<>("", payment -> {
	    	// In the future maybe open a dialog widht info
	    });
	    
	    Column<Payment, Payment> infoColumn = new Column<Payment, Payment>(infoActionCell) {

			@Override
			public Payment getValue(Payment payment) {
				return payment;
			}
			
			@Override
			public void render(Context context, Payment payment, SafeHtmlBuilder sb) {
				if(null != payment) {
					String title = getExtraInfoTitle(payment);
					if(AonStringUtils.isNotBlank(title))
						sb.appendHtmlConstant("<button type=\"button\" class=\"aon-editDataTable-button aon-no-margin aon-icon-info\" title=\"" + title + "\"></button>");
				}
			}
		};
		
		infoColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		agreementExtraPaymentDG.setColumnWidth(infoColumn, 60, Unit.PX);
	    
	     // Visibility column.
	    ActionCell<Payment> visibilityActionCell = new ActionCell<>("", payment -> {
	    	if(!readOnly) {
		    	showHidePayment(payment);
		    	payment.setModify(true);
		    	agreementExtraPaymentDG.redraw();
				setHasChange(true);
	    	}
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
		agreementExtraPaymentDG.setColumnWidth(visibilityColumn, 60, Unit.PX);
	    
	    // Delete column.
	    ActionCell<Payment> deleteActionCell = new ActionCell<>("", payment -> {
	    	if(!readOnly) {
		    	AonDialog deleteDialog = new AonDialog("Eliminar concepto", new HTML("\u00BFDesea eliminar el concepto seleccionado\u003F"));
		    	deleteDialog.confirm(new AonAcceptDialogCallback() {
					
					@Override
					public void onCancel() {
						// Nothing to do here
					}
					
					@Override
					public void onAccept() {
						agreement.deletePayment(payment);
						setAgreementPreview(agreement);
						setHasChange(true);
					}
				});
	    	}
	    }); 
	    
	    Column<Payment, Payment> deleteColumn = new Column<Payment, Payment>(deleteActionCell) {

			@Override
			public Payment getValue(Payment payment) {
				return payment;
			}
			
			@Override
			public void render(Context context, Payment payment, SafeHtmlBuilder sb) {
				if(null != payment && !readOnly) {
					sb.appendHtmlConstant("<button type=\"button\" id=\"gwt-debug-deletePaymentTabButton-" + context.getIndex() + "\" class=\"aon_button aon_table_button aon_icon_delete\" style=\"border: none !important; height: 20px;\" title=\"Eliminar\"></button>");
				} else
					sb.appendHtmlConstant("<div></div>");
			}
		};
		
		deleteColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		agreementExtraPaymentDG.setColumnWidth(deleteColumn, 50, Unit.PX);
		
		agreementExtraPaymentDG.setRowStyles((payment, rowIdx) -> {
			if(payment.isModify()) return style.modify();
			else return null;
		});
		
	    // Add the columns.
		agreementExtraPaymentDG.addColumn(editColumn, "");
		agreementExtraPaymentDG.addColumn(payDateColumn, "F. Cobro");
		agreementExtraPaymentDG.addColumn(conceptColumn, "Concepto");
		agreementExtraPaymentDG.addColumn(descriptionColumn, "Descripci\u00F3n");
		agreementExtraPaymentDG.addColumn(expressionColumn, "Expresi\u00F3n");
		agreementExtraPaymentDG.addColumn(periodicityColumn, "");
		
		agreementExtraPaymentDG.addColumn(infoColumn, "");
		agreementExtraPaymentDG.addColumn(visibilityColumn, "Estado");  
		agreementExtraPaymentDG.addColumn(deleteColumn, "");  
	}
	
	// ------------------------------------------ setAgreementPreview
	
	public void setAgreementPreview(AgreementInfo agreementIn) {
		agreement = agreementIn;
		toolbar.setTitle(agreement.getDescription());
		serviAgreementUpdateButton.setVisible(!readOnly && agreement.getOwner().equals(AgreementOwner.SERVICONVENIOS));
		
		fillDatesLB();
		
		disclouroseHeight = Window.getClientHeight() - 370;
		
		hideLevelSalaryMessage();
		showAgreementPreview();
		fillAgreementInfo();
		hideMessage();
	}

	// ------------------------------------------ fillAgreementInfo
	
	private void fillAgreementInfo() {
		
		printPreviewButton.setVisible(!agreement.getDates().isEmpty());
			
		description.setText(agreement.getDescription());
		description.setReadOnly(readOnly);
		description.removeStyleName(style.modify());
		description.addValueChangeHandler(e -> {
			agreement.setDescription(e.getValue());
			description.addStyleName(style.modify());
			setHasChange(true);
		});
		ssNumber.setText(agreement.getSSNumber());
		ssNumber.setReadOnly(readOnly);
		ssNumber.removeStyleName(style.modify());
		ssNumber.addValueChangeHandler(e -> {
			agreement.setSSNumber(e.getValue());
			ssNumber.addStyleName(style.modify());
			setHasChange(true);
		});
		
		if(agreement.getOwner().equals(AgreementOwner.SERVICONVENIOS))
			createServiAgreementPanel();
		else
			serviAgreementPanel.clear();
		
		createCategoryTable();
		createSalaryTable();
		
		initAgreementPaymentDG();
		initAgreementExtraPaymentDG();
		
		setTablesWidth();
	}
	
	// ------------------------------------------ serviAgreementPanel
	
	private void createServiAgreementPanel() {
		serviAgreementPanel.clear();
		
		Label serviAgreementLabel = new Label("Vinculado con ServiConvenios");
		serviAgreementLabel.getElement().getStyle().setMarginLeft(10, Unit.PX);
		serviAgreementPanel.add(serviAgreementLabel);
		
		AonToolbarButton serviAgreementPDFButton = new AonToolbarButton("ServiConvenios PDF", AON.CSS.aonIconPdf() );
		serviAgreementPDFButton.addClickHandler(e -> {
			String url = GWT.getModuleBaseURL() + "servi_agreement?fileType=pdf&ssNumber=" + agreement.getSSNumber();
			Window.open( url, "_blank", "status=0,toolbar=0,menubar=0,location=0");
		});
		serviAgreementPanel.add(serviAgreementPDFButton);
		
		AonToolbarButton serviAgreementXLSButton = new AonToolbarButton("ServiConvenios XLS", AON.CSS.aonIconExcel() );
		serviAgreementXLSButton.addClickHandler(e -> {
			String url = GWT.getModuleBaseURL() + "servi_agreement?fileType=xls&ssNumber=" + agreement.getSSNumber();
			Window.open( url, "_blank", "status=0,toolbar=0,menubar=0,location=0");
		});
		serviAgreementPanel.add(serviAgreementXLSButton);
		
	}

	// ------------------------------------------ salaryTableButtons

	private void setSelectedValueLB(ListBox lBox, String str) {
	    String text = str;
	    int indexToFind = 0;
	    for (int i = 0; i < lBox.getItemCount(); i++) {
	    	if (AonStringUtils.equalsIgnoreCase(lBox.getValue(i), text)) {
	            indexToFind = i;
	            break;
	        }
	    }
	    lBox.setSelectedIndex(indexToFind);
	}

	// ------------------------------------------ salaryTable
	
	private void createSalaryTable() {
		if(agreement.getSortedDates().isEmpty())
			showSalaryTableMessage();
		else {
			hideLevelSalaryMessage();
			getSalaryTableHeader();
			fillSalaryTable();
			salaryTableWidth();
			if(employeeView) 
				salaryGrid.removeRow(1);
		}
	}
	
	private void fillDatesLB() {
		datesLB.clear();
		agreement.getSortedDates().forEach(date -> datesLB.addItem(formatDate.format(date), formatDate.format(date)));
		datesLB.setSelectedIndex(0);
	}
	
	private void getSalaryTableHeader() {
		Date selectedDate = formatDate.parse(datesLB.getSelectedValue());
		
		salaryGrid.clear();
		salaryGrid.resize(0, agreement.getVariablesByDate(selectedDate).size()+3);
		int row = salaryGrid.insertRow(salaryGrid.getRowCount());
		
		Label category = new Label("Categoria");
		category.addStyleName(style.gridTitle());
		category.addStyleName(style.textCenter());
		category.addStyleName(style.categoryWidth());
		category.addStyleName(style.headerFSize());
		salaryGrid.setWidget(row, 0, category);
		salaryGrid.getCellFormatter().addStyleName(row, 0, style.headerLevelFixed());
		salaryGrid.getColumnFormatter().addStyleName(0, style.columnBorder());
		
		int col = 1;
		
		for(String variable : agreement.getVariablesByDate(selectedDate)) {
			Label label = new Label(variable);
			label.addStyleName(style.gridTitle());
			label.addStyleName(style.textCenter());
			label.addStyleName(style.cellWidth());
			label.addStyleName(style.headerFSize());
			salaryGrid.setWidget(row, col, label);
			
			salaryGrid.getColumnFormatter().removeStyleName(col, style.widthAll());
			
			salaryGrid.getColumnFormatter().addStyleName(col, style.columnBorder());
			salaryGrid.getCellFormatter().addStyleName(row, col, style.headerFixed());
			
			col++;
		}
		
		Label emptyCell = new Label("");
		emptyCell.addStyleName(style.widthAll());
		salaryGrid.setWidget(row, col, emptyCell);
		salaryGrid.getCellFormatter().addStyleName(row, col, style.headerFixed());
		salaryGrid.getColumnFormatter().addStyleName(col, style.widthAll());
		
		col++;
		
		Label deleteCell = new Label("");
		salaryGrid.setWidget(row, col, deleteCell);
		salaryGrid.getCellFormatter().addStyleName(row, col, style.headerDeleteFixed());
	}

	private void fillSalaryTable() {
		Date selectedDate = formatDate.parse(datesLB.getSelectedValue());
		
		for(Level level : agreement.getLevels()) {
			if(level.getId() != 0 && ((level.isDeleted() || (null != agreement.getSelectedLevel() && !level.getId().equals(agreement.getSelectedLevel().getId()))))) continue;
			
			int row = salaryGrid.insertRow(salaryGrid.getRowCount());
			
			Widget categoryCell;
			
			if(level.getId() == 0) {
				categoryLB = createCategoryLB();
				categoryLB.ensureDebugId("category_filter");
				categoryLB.getElement().getStyle().setHeight(1.7, Unit.EM);
				categoryLB.getElement().getStyle().setWidth(290, Unit.PX);
				categoryLB.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
				
				setSelectedValueLB(categoryLB, null == agreement.getSelectedLevel() ? "" : String.valueOf(agreement.getSelectedLevel().getId()));
				categoryLB.setVisible(!agreement.getLevels().isEmpty());
				
				categoryLB.addChangeHandler(e -> filterSelectedCategory());
				
				categoryCell = categoryLB;
			} else {
				String levelCategories = getLevelCategories(level);
				categoryCell = new Label(levelCategories);
				categoryCell.setTitle(getLevelCategoriesTitle(level));
				categoryCell.addStyleName(style.overflowEllipsis());
				categoryCell.addStyleName(style.gridTitle());
				categoryCell.addStyleName(style.gridCell());
				categoryCell.addStyleName(style.levelCell());
			}
			
			salaryGrid.setWidget(row, 0, categoryCell);
			salaryGrid.getCellFormatter().addStyleName(row, 0, style.levelFixed());
			if(row % 2 == 0 ) salaryGrid.getCellFormatter().addStyleName(row, 0, style.oddRow());
			
			int col = 1;
			
			for(String variable : agreement.getVariablesByDate(selectedDate)) {
				LevelData levelData = agreement.getLevelData(level.getId(), variable, selectedDate);
				TextBox cell = new ExpressionBox();
				
				cell.ensureDebugId("textBox_" + variable + "_" + level.getDescription() );

				cell.addStyleName(style.gridCell());
				cell.addStyleName(style.valueCell());
				cell.addStyleName(style.textBoxSalary());
				cell.setValue(null == levelData ? null : levelData.getExpression());
				cell.setTitle("Valor nivel retributivo");
				cell.setReadOnly(readOnly);
				
				if(row % 2 == 0 ) cell.addStyleName(style.oddRow());
				cell.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
				if(null != levelData && AonStringUtils.isNotBlank(levelData.getExpression()) && SpecialExpresion.parse(levelData.getExpression()).getInput().length() > 16)
					cell.setWidth((7.5 * levelData.getExpression().length()) + "px");
				else
					cell.setWidth("95%");
				
				if(levelData != null && levelData.isModify())
					cell.addStyleName(style.modify());
				else
					cell.removeStyleName(style.modify());
				
				cell.addValueChangeHandler(event -> {
					
					String expression = event.getValue();
					String value = event.getValue();
					
					try {
						double expressionValue = evalExpression(expression);
						value = expressionValue + "";
					} catch (Exception e) {
						// TODO: handle exception
					}
					
					if(null == levelData || null == levelData.getId())
						agreement.createLevelData(level.getId(), variable, value, selectedDate);
					else
						agreement.updateLevelData(level.getId(), levelData.getId(), value);
					
					setAgreementPreview(agreement);
					setHasChange(true);
				});
				
				// check if level 0 or default value
				if(level.getId() == 0 || null == levelData || AonStringUtils.isBlank(levelData.getExpression())) {
					LevelData levelDataDefault = agreement.getDefaultLevelData(variable, selectedDate);
					cell.setText(null == levelDataDefault ? null : SpecialExpresion.parse(levelDataDefault.getExpression()).getInput());
					cell.setTitle("Valor por defecto");
					cell.addStyleName(style.levelDefaultValue());
					cell.setReadOnly(readOnly);
					
					if(null != levelDataDefault && AonStringUtils.isNotBlank(levelDataDefault.getExpression()) && cell.getText().length() > 20)
						cell.setWidth((7.5 * levelDataDefault.getExpression().length()) + "px");
					else
						cell.setWidth("95%");
				}
						
				salaryGrid.setWidget(row, col, cell);
				if(row % 2 == 0 ) salaryGrid.getCellFormatter().addStyleName(row, col, style.oddRow());
				col++;
			}
			
			Label emptyCell = new Label("");
			salaryGrid.setWidget(row, col, emptyCell);
			if(row % 2 == 0 ) salaryGrid.getCellFormatter().addStyleName(row, col, style.oddRow());
			
			col++;
			
			Widget deleteCell = new Label();
			if(!readOnly) {
				deleteCell = new AonToolbarSmallButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete());
				((AonToolbarSmallButton) deleteCell).addClickHandler(event -> {
						AonDialog deleteDialog = new AonDialog("Borrar nivel", new HTMLPanel("\u00bfDesea realmente eliminar el nivel <b>" + level.getDescription() +"</b>\u003f"));
						deleteDialog.setGlassStyleName(style.dialogGlass());
						deleteDialog.addStyleName(style.dialogZIndex());
						deleteDialog.confirm(new AonAcceptDialogCallback() {
							
							@Override
							public void onCancel() {
								// Not use here
							}
							
							@Override
							public void onAccept() {
								agreement.deleteLevel(level.getId());
								createSalaryTable();
								setHasChange(true);
							}
						});
					});
			}
			
			salaryGrid.setWidget(row, col, level.getId() == 0 ? new Label("") : deleteCell);
			salaryGrid.getCellFormatter().addStyleName(row, col, style.deleteFixed());
			if(row % 2 == 0 ) salaryGrid.getCellFormatter().addStyleName(row, col, style.oddRow());
		}
		
	}
	
	public double evalExpression(String expression) {
	    return calculate(expression);
	}

	public final native double calculate(String expression) /*-{
	    return eval(expression);
	}-*/;
	
	private ListBox createCategoryLB() {
		Map<String, Integer> allCategories = new TreeMap<>();
		
		for(Level levelIT : agreement.getLevels()) {
			if(levelIT.getId() == 0) continue;
			Set<String> levelCategories = agreement.getCategoriesMap().get(levelIT.getId());
			Set<String> levelContracts = agreement.getContractsMap().get(levelIT.getId());
			if(null !=levelContracts && !levelContracts.isEmpty()) levelContracts.forEach(levelContract -> allCategories.put(levelIT.getDescription() + " - " + levelContract, levelIT.getId()));
			else if(levelCategories.size() > 1) levelCategories.forEach(category -> allCategories.put(category, levelIT.getId()));
			else if(levelCategories.size() == 1) {
				String category = (String)levelCategories.toArray()[0];
				RegExp regExp = RegExp.compile("Categoria|Nivel|categoria|nivel|CATEGORIA|NIVEL?");
				MatchResult matcher = regExp.exec(category);
				boolean matchFound = matcher != null;
				if(matchFound) allCategories.put(levelIT.getDescription(), levelIT.getId());
			    else  allCategories.put(category, levelIT.getId());
			}
		}
		
		ListBox listBox = new ListBox();
		listBox.addItem("Todos", "");
		listBox.addItem("Por defecto", "0");
		allCategories.entrySet().forEach(e -> listBox.addItem(e.getKey(), e.getValue().toString()));
		return listBox;
	}
	
	private String getLevelCategories(Level level) {
		StringBuilder categoriesBuilder = new StringBuilder();
		Set<String> levelContracts = agreement.getContractsMap().get(level.getId());
		if(null !=levelContracts && !levelContracts.isEmpty()) {
			for(String levelContract : levelContracts){
				if(AonStringUtils.isBlank(categoriesBuilder.toString()))
					categoriesBuilder.append(levelContract);
				else
					categoriesBuilder.append(", " + levelContract);
			}
			return level.getDescription() + " - " + categoriesBuilder.toString();
		} else if(agreement.getCategoriesMap().get(level.getId()).size() > 1) {
			for(String category : agreement.getCategoriesMap().get(level.getId())){
				if(AonStringUtils.isBlank(categoriesBuilder.toString()))
					categoriesBuilder.append(category);
				else
					categoriesBuilder.append(", " + category);
			}
			return categoriesBuilder.toString();
		} else if(agreement.getCategoriesMap().get(level.getId()).size() == 1) {
			String category = (String)agreement.getCategoriesMap().get(level.getId()).toArray()[0];
			RegExp regExp = RegExp.compile("Categoria|Nivel|categoria|nivel|CATEGORIA|NIVEL?");
			MatchResult matcher = regExp.exec(category);
			boolean matchFound = matcher != null;
			if(matchFound) return level.getDescription();
		    else return category;
		} else return null;
	}
	
	private String getLevelCategoriesTitle(Level level) {
		StringBuilder categoriesBuilder = new StringBuilder();
		Set<String> levelContracts = agreement.getContractsMap().get(level.getId());
		if(null !=levelContracts && !levelContracts.isEmpty()) {
			
			StringBuilder contractsBuilder = new StringBuilder();
			for(String levelContract : levelContracts){
				if(AonStringUtils.isBlank(contractsBuilder.toString()))
					contractsBuilder.append(levelContract);
				else
					contractsBuilder.append(", " + levelContract);
			}
			
			for(String category : agreement.getCategoriesMap().get(level.getId())){
				if(AonStringUtils.isBlank(categoriesBuilder.toString()))
					categoriesBuilder.append(category);
				else
					categoriesBuilder.append(", " + category);
			}
			
			return  "Nivel : " + level.getDescription() + "\nContratos : " + contractsBuilder.toString() + "\nCategorias : " + categoriesBuilder.toString();
			
		} if(agreement.getCategoriesMap().get(level.getId()).size() > 1) {
			for(String category : agreement.getCategoriesMap().get(level.getId())){
				if(AonStringUtils.isBlank(categoriesBuilder.toString()))
					categoriesBuilder.append(category);
				else
					categoriesBuilder.append(", " + category);
			}
			return "Nivel : " + level.getDescription() + "\nCategorias : " + categoriesBuilder.toString();
		} else if(agreement.getCategoriesMap().get(level.getId()).size() == 1) {
			String category = (String)agreement.getCategoriesMap().get(level.getId()).toArray()[0];
			RegExp regExp = RegExp.compile("Categoria|Nivel|categoria|nivel|CATEGORIA|NIVEL?");
			MatchResult matcher = regExp.exec(category);
			boolean matchFound = matcher != null;
			if(matchFound) return "Nivel : " + level.getDescription();
		    else return "Nivel : " + level.getDescription() + "\nCategoria : " + category;
		} else return null;
	}
	
	private void filterSelectedCategory() {
		String levelId = categoryLB.getSelectedValue();
		agreement.setSelectedLevel(AonStringUtils.isBlank(levelId) ? null : agreement.getLevelById(Integer.parseInt(categoryLB.getSelectedValue())));
		setAgreementPreview(agreement);
	}

	private void salaryTableWidth() {
		salaryGrid.getColumnFormatter().setWidth(0, "120px");
	}
	
	// ------------------------------------------ categoryTable

	private void createCategoryTable() {
		if(agreement.getActiveLevels().size()  <= 1)
			showLevelMessage();
		else {
			hideLevelSalaryMessage();
			getCategoryTableHeader();
			fillCategoryTable();
			categoryTableWidth();
		}
	}
	
	private void getCategoryTableHeader() {
		levelGrid.clear();
		levelGrid.resize(0, 3);
		int row = levelGrid.insertRow(levelGrid.getRowCount());
		
		Label level = new Label("Nivel");
		level.addStyleName(style.gridTitle());
		level.addStyleName(style.headerFSize());
		levelGrid.setWidget(row, 0, level);
		levelGrid.getCellFormatter().addStyleName(row, 0, style.headerFixed());
		
		Label category = new Label("Categoria");
		category.addStyleName(style.gridTitle());
		category.addStyleName(style.headerFSize());
		levelGrid.setWidget(row, 1, category);
		levelGrid.getCellFormatter().addStyleName(row, 1, style.headerFixed());

		levelGrid.getRowFormatter().addStyleName(row, style.headerColor());
		
		Label delete = new Label("");
		levelGrid.setWidget(row, 2, delete);
		levelGrid.getCellFormatter().addStyleName(row, 2, style.headerFixed());
	}

	private void fillCategoryTable() {
		for(Entry<Integer, Set<String>> e : agreement.getCategoriesMap().entrySet()) {
			
			Level level = agreement.getLevelById(e.getKey());
			if(level.isDeleted() || level.getId() == 0) continue;
			
			int row = levelGrid.insertRow(levelGrid.getRowCount());
			
			Set<String> categories = e.getValue();
			StringBuilder categoriesBuilder = new StringBuilder();
			for(String category : categories){
				if(AonStringUtils.isBlank(categoriesBuilder.toString()))
					categoriesBuilder.append(category);
				else
					categoriesBuilder.append(", " + category);
			}
			
			TextBox levelCell = new TextBox();
			levelCell.setValue(level.getDescription());
			levelCell.setTitle(level.getDescription());
			levelCell.addStyleName(style.overflowEllipsis());
			levelCell.addStyleName(style.gridTitle());
			levelCell.addStyleName(style.gridCell());
			levelCell.addStyleName(style.levelCell());
			levelCell.addStyleName(style.textBoxSalary());
			levelCell.setReadOnly(readOnly);
			if(row % 2 == 0 ) levelCell.addStyleName(style.oddRow());
			
			if(level != null && level.isModify())
				levelCell.addStyleName(style.modify());
			else
				levelCell.removeStyleName(style.modify());
			
			levelCell.addValueChangeHandler(ev -> {
				if(AonStringUtils.isBlank(ev.getValue()) || agreement.existLevel(ev.getValue())) {
					showWarning("Nivel existente", "La descripci\u00f3n no puede ser vacia o coincidir con la de otro nivel ya existente");
					levelCell.setValue(level.getDescription());
				} else {
					level.setDescription(ev.getValue());
					level.setModify(true);
					setAgreementPreview(agreement);
					setHasChange(true);
				}
			});
			
			TextBox categoryCell = new TextBox();
			categoryCell.addStyleName(style.levelCell());
			categoryCell.addStyleName(style.textBoxSalary());
			categoryCell.setValue(categoriesBuilder.toString());
			categoryCell.setTitle("Categorias nivel " + level.getDescription());
			categoryCell.setReadOnly(readOnly);
			if(row % 2 == 0 ) categoryCell.addStyleName(style.oddRow());
			
			if(level != null && level.isCatModify())
				categoryCell.addStyleName(style.modify());
			else
				categoryCell.removeStyleName(style.modify());
			
			categoryCell.addValueChangeHandler(categoryValue -> {
				if(AonStringUtils.isNotBlank(categoryValue.getValue())) {
					agreement.getCategoriesMap().remove(level.getId());
					String[] categorySplit = AonStringUtils.split(categoryValue.getValue(), ',');
					for(int i = 0; i < categorySplit.length; i++)
						agreement.addCategory(level.getId(), categorySplit[i].trim());
					
					level.setCatModify(true);
					setAgreementPreview(agreement);
					setHasChange(true);
				}
				
			});
			
			Widget deleteCell = new Label();
			
			if(!readOnly) {
				deleteCell = new AonToolbarSmallButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete());
				((AonToolbarSmallButton) deleteCell).addClickHandler(event -> {
					AonDialog deleteDialog = new AonDialog("Borrar nivel", new HTMLPanel("\u00bfDesea realmente eliminar el nivel <b>" + level.getDescription() +"</b>\u003f"));
					deleteDialog.setGlassStyleName(style.dialogGlass());
					deleteDialog.addStyleName(style.dialogZIndex());
					deleteDialog.confirm(new AonAcceptDialogCallback() {
						
						@Override
						public void onCancel() {
							// Not use here
						}
						
						@Override
						public void onAccept() {
							agreement.deleteLevel(level.getId());
							setAgreementPreview(agreement);
							setHasChange(true);
						}
					});
				});
			}
			
			levelGrid.setWidget(row, 0, levelCell);
			levelGrid.setWidget(row, 1, categoryCell);
			levelGrid.setWidget(row, 2, deleteCell);
			
			if(row % 2 == 0 ) levelGrid.getCellFormatter().addStyleName(row, 0, style.oddRow());
			if(row % 2 == 0 ) levelGrid.getCellFormatter().addStyleName(row, 1, style.oddRow());
			if(row % 2 == 0 ) levelGrid.getCellFormatter().addStyleName(row, 2, style.oddRow());
		}
	}

	private void categoryTableWidth() {
		levelGrid.setWidth("100%");
		levelGrid.getColumnFormatter().setWidth(0, "120px");
		levelGrid.getColumnFormatter().setWidth(2, "15px");
	}
	
	// ------------------------------------------ paymentTable
	
	public void initAgreementPaymentDG() {	
		// Create a data provider.
		ListDataProvider<Payment> paymentDataProvider = new ListDataProvider<>();

	    // Connect the table to the data provider.
		paymentDataProvider.addDataDisplay(agreementPaymentDG);
	    
	    // Add the data to the data provider, which automatically pushes it to the widget.
	    List<Payment> paymentListAux = paymentDataProvider.getList();
	    paymentListAux.clear();
	    
	   this.paymentList = new ArrayList<>(showOldPayments ? agreement.getOldPaymentsAndHides() : agreement.getPaymentsAndHides());
	    
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
	
	public void initAgreementExtraPaymentDG() {	
		// Create a data provider.
		ListDataProvider<Payment> paymentDataProvider = new ListDataProvider<>();
	
	    // Connect the table to the data provider.
		paymentDataProvider.addDataDisplay(agreementExtraPaymentDG);
	    
	    // Add the data to the data provider, which automatically pushes it to the widget.
	    List<Payment> paymentListAux = paymentDataProvider.getList();
	    paymentListAux.clear();
	    
	    this.paymentExtraList = new ArrayList<>(showOldPayments ? agreement.getOldPaymentsExtraAndHides() : agreement.getPaymentsExtraAndHides());
	    
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
	
	private int compareString(Object o1, Object o2, String s1, String s2) {
		if (o1 == o2) return 0;
		else if (o1 == null) return -1;
		else if (o2 == null) return 1;
		else
        	return s1.compareTo(s2);
	}
	
	private String getInfoTitle(Payment payment) {
		String title = "";
		
		String irpfExpression = payment.getIrpfExpression();
		if(!AonStringUtils.equalsIgnoreCase(irpfExpression, "_P")) {
			title += "Tributa : " + getTaxedDescription(irpfExpression) + "\n";
		}
		
		String quoteExpression = payment.getQuoteExpression();
		if(!AonStringUtils.equalsIgnoreCase(quoteExpression, "_P")) {
			title += "Cotiza : " + getQuoteDescription(quoteExpression) + "\n";
		}
		
		if(payment.getType().equals(Payment.Type.CRA_0005)) {
			title += "Pago : " + getPayDescription(payment) + "\n";
		}
		
		return title;
	}
	
	private String getTaxedDescription(String irpfExpression) {
		if(AonStringUtils.equalsIgnoreCase(irpfExpression, "_P")) return "Importe \u00cdntegro";
		if(AonStringUtils.equalsIgnoreCase(irpfExpression, "0.00")) return "Exento";
		if(AonStringUtils.containsIgnoreCase(irpfExpression, "BASE_CTA_ESP")) return "Ingreso a Cuenta";
		if(AonStringUtils.isNotBlank(irpfExpression)) return "Personalizado";
		return "No definido";
	}
	
	private String getQuoteDescription(String quoteExpression) {
		if(AonStringUtils.equalsIgnoreCase(quoteExpression, "_P")) return "Importe \u00cdntegro";
		if(AonStringUtils.equalsIgnoreCase(quoteExpression, "0.00")) return "Exento";
		if(AonStringUtils.containsIgnoreCase(quoteExpression, "PRORRATEAR")) return "Prorrateado";
		if(AonStringUtils.isNotBlank(quoteExpression)) return "Personalizado";
		return "No definido";
	}
	
	private String getPayDescription(Payment payment) {
		AgreementExtra extra = agreement.getExtraPayment(payment.getId());
		
		if(	null == extra && 
				!payment.getType().equals(Payment.Type.CRA_0004) && 
				!payment.getType().equals(Payment.Type.CRA_0005)) return "";
			
		if(	null == extra && 
				(payment.getType().equals(Payment.Type.CRA_0004) || 
				payment.getType().equals(Payment.Type.CRA_0005))) return "Prorrat.";
		
		return (extra == null || extra.isDeleted()) ? "Prorrat." : getExtraPeriodTitle(extra);
	}
	
	private String getExtraPeriodTitle(AgreementExtra extra) {
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
				return (endMonth - startMonth) + " m.";
			}
		} catch (Exception e) {
			return "Revisar esta extra!!";
		}
	}

	private void checkPaymentExtra(Payment payment, String periodicity) {
		AgreementExtra extra = agreement.getExtraPayment(payment.getId());
		
		if(null != extra) {
			if(AonStringUtils.equals(periodicity, "Prorrat.")) extra.setDeleted(true);
			else if(AonStringUtils.equals(periodicity, "Anual")) checkAnualExtra(extra);
			else if(AonStringUtils.equals(periodicity, "Semestral")) checkSemestralExtra(extra);
			
			payment.setModify(true);
		}
	}
	
	private void checkAnualExtra(AgreementExtra extra) {
		extra.setDeleted(false);
		String issueDate = extra.getIssueDate();
		if(AonStringUtils.isNotBlank(issueDate)) {
			if(AonStringUtils.contains(issueDate, "03")) {
				extra.setStartDate("01/01 -1");
				extra.setEndDate("31/12 -1");
			} else if(AonStringUtils.contains(issueDate, "06") || AonStringUtils.contains(issueDate, "07")) {
				extra.setStartDate("01/07 -1");
				extra.setEndDate("30/06");
			} else if(AonStringUtils.contains(issueDate, "12")) {
				extra.setStartDate("01/01");
				extra.setEndDate("31/12");
			} 
		}
	}

	private void checkSemestralExtra(AgreementExtra extra) {
		extra.setDeleted(false);
		String issueDate = extra.getIssueDate();
		if(AonStringUtils.isNotBlank(issueDate)) {
			if(AonStringUtils.contains(issueDate, "03")) {
				extra.setStartDate("01/07 -1");
				extra.setEndDate("31/12 -1");
			} else if(AonStringUtils.contains(issueDate, "06") || AonStringUtils.contains(issueDate, "07")) {
				extra.setStartDate("01/01");
				extra.setEndDate("30/06");
			} else if(AonStringUtils.contains(issueDate, "12")) {
				extra.setStartDate("01/07");
				extra.setEndDate("31/12");
			} 
		}
		
	}

	private String getPayLongDescription(Payment payment) {
		AgreementExtra extra = agreement.getExtraPayment(payment.getId());
		
		if(	null == extra && 
				(payment.getType().equals(Payment.Type.CRA_0004) || 
				payment.getType().equals(Payment.Type.CRA_0005))) return "Prorrat.";
		
		return (extra == null || extra.isDeleted()) ? "Prorrat." : getExtraPeriodLongTitle(extra);
	}
	
	private String getExtraPeriodLongTitle(AgreementExtra extra) {
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
				return "Anual";
			}
		} catch (Exception e) {
			return "Anual";
		}
	}
	
	private String getExtraInfoTitle(Payment payment) {
		String title = "";
		
		String irpfExpression = payment.getIrpfExpression();
		if(!AonStringUtils.equalsIgnoreCase(irpfExpression, "_P")) {
			title += "Tributa : " + getTaxedDescription(irpfExpression) + "\n";
		}
		
		String quoteExpression = payment.getQuoteExpression();
		if(!AonStringUtils.equalsIgnoreCase(quoteExpression, "_P")) {
			title += "Cotiza : " + getQuoteDescription(quoteExpression) + "\n";
		}
		
		return title;
	}

	private String getParsedExpression(String expression) {
//		expression = AonStringUtils.isBlank(expression) ? expression : expression.replaceAll("HIDE\\(.*\\); ", "");
		return SpecialExpresion.parse(expression).getInput();
	}
	
	// ------------------------------------------ tables widht
	
	public void setTablesWidth() {
		Scheduler.get().scheduleDeferred(() -> {
			salaryScrollPanel.setWidth((Window.getClientWidth() - 420) + "px");
			
			levelScrollPanel.setHeight((levelDiscPanelContent.getOffsetHeight() - 30) + "px");
			salaryScrollPanel.setHeight((salaryDiscPanelContent.getOffsetHeight() - 30) + "px");
		});
	}
	
	public void setTablesWidthCollapseMenu() {
		salaryScrollPanel.setWidth((Window.getClientWidth() - 70) + "px");
	}

	// ------------------------------------------ deckLayoutPanel
	
	private void showAgreementPreview() {
		deckLayoutPanel.showWidget(0);
	}
	
	private void showAgreementSimulator() {
		deckLayoutPanel.showWidget(1);
	}
	
	// ------------------------------------------ salaryTable
	
	private void showSalaryTableMessage() {
		agreementSalaryTableMessage.getElement().getStyle().clearDisplay();
		agreementLevelMessage.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void showLevelMessage() {
		agreementSalaryTableMessage.getElement().getStyle().setDisplay(Display.NONE);
		agreementLevelMessage.getElement().getStyle().clearDisplay();
	}
	
	private void hideLevelSalaryMessage() {
		agreementSalaryTableMessage.getElement().getStyle().setDisplay(Display.NONE);
		agreementLevelMessage.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	// ------------------------------------------ DisclosurePanel

	private void initDiscPanels() {
		levelDiscPanel.setAnimationEnabled(true);
		levelDiscPanel.addOpenHandler(e -> {
			handleIcon(levelDiscBtn, true);
			
			salaryDiscPanel.setOpen(false);
			handleIcon(salaryDiscBtn, false);
			
			paymentDiscPanel.setOpen(false);
			handleIcon(paymentDiscBtn, false);
			
			paymentExtraDiscPanel.setOpen(false);
			handleIcon(paymentExtraDiscBtn, false);
			
			showCategoryButtons();
		});
		levelDiscPanel.addCloseHandler(e -> handleIcon(levelDiscBtn, false));
		levelDiscPanelContent.setHeight((Window.getClientHeight() - 450) + "px");
		
		salaryDiscPanel.setAnimationEnabled(true);
		salaryOpenHandler = salaryDiscPanel.addOpenHandler(e -> {
			handleIcon(salaryDiscBtn, true);
			
			levelDiscPanel.setOpen(false);
			handleIcon(levelDiscBtn, false);
			
			paymentDiscPanel.setOpen(false);
			handleIcon(paymentDiscBtn, false);
			
			paymentExtraDiscPanel.setOpen(false);
			handleIcon(paymentExtraDiscBtn, false);
			
			showSalaryTableButtons();
		});
		salaryDiscPanel.addCloseHandler(e -> handleIcon(salaryDiscBtn, false));
		salaryDiscPanelContent.setHeight((Window.getClientHeight() - 450) + "px");
		salaryDiscPanel.setOpen(true);
		
		paymentDiscPanel.setAnimationEnabled(true);
		paymentOpenHandler = paymentDiscPanel.addOpenHandler(e -> {
			handleIcon(paymentDiscBtn, true);
			
			levelDiscPanel.setOpen(false);
			handleIcon(levelDiscBtn, false);
			
			salaryDiscPanel.setOpen(false);
			handleIcon(salaryDiscBtn, false);
			
			paymentExtraDiscPanel.setOpen(false);
			handleIcon(paymentExtraDiscBtn, false);
			
			showPaymentButtons();
			
			setScrollDGHeight();
			agreementPaymentDG.redraw();
			
		});
		paymentDiscPanel.addCloseHandler(e -> handleIcon(paymentDiscBtn, false));
		paymentDiscPanelContent.setHeight((Window.getClientHeight() - 450) + "px");
		
		paymentExtraDiscPanel.setAnimationEnabled(true);
		paymentExtraOpenHandler = paymentExtraDiscPanel.addOpenHandler(e -> {
			handleIcon(paymentExtraDiscBtn, true);
			
			levelDiscPanel.setOpen(false);
			handleIcon(levelDiscBtn, false);
			
			salaryDiscPanel.setOpen(false);
			handleIcon(salaryDiscBtn, false);
			
			paymentDiscPanel.setOpen(false);
			handleIcon(paymentDiscBtn, false);
			
			showPaymentButtons();
			
			setScrollDGHeight();
			agreementExtraPaymentDG.redraw();
		});
		paymentExtraDiscPanel.addCloseHandler(e -> handleIcon(paymentExtraDiscBtn, false));
		extraDiscPanelContent.setHeight((Window.getClientHeight() - 450) + "px");
	}
	
	public void setScrollDGHeight() {
		agreementPaymentDG.setWidth("100%");
		agreementExtraPaymentDG.setWidth("100%");
		
		if(!isOpenCollapse) {
			agreementPaymentDG.setHeight(disclouroseHeight + "px");
			agreementExtraPaymentDG.setHeight(disclouroseHeight + "px");
		} else {
			agreementPaymentDG.setHeight((paymentDiscPanelContent.getOffsetHeight() - 15) + "px");
			agreementExtraPaymentDG.setHeight((extraDiscPanelContent.getOffsetHeight() - 15) + "px");
		}
	}
	
	public void setScrollDGHeightEmployee() {
		agreementPaymentDG.setWidth("100%");
		agreementExtraPaymentDG.setWidth("100%");
		
		agreementPaymentDG.setHeight((Window.getClientHeight() - 495) + "px");
		agreementExtraPaymentDG.setHeight((Window.getClientHeight() - 495) + "px");
	}
	
	public void setScrollDGHeightWorkplace(int salaryHeight) {
		agreementPaymentDG.setWidth("100%");
		agreementExtraPaymentDG.setWidth("100%");
		
		agreementPaymentDG.setHeight((Window.getClientHeight() - 420 - salaryHeight) + "px");
		agreementExtraPaymentDG.setHeight((Window.getClientHeight() - 420 - salaryHeight) + "px");
	}
	
	public void setIsOpenCollapse(boolean isCollapse) {
		this.isOpenCollapse = isCollapse;
		setScrollDGHeight();
	}

	private void createDiscPanelButtons() {
		levelDiscBtn = new AonToolbarButton("Desplegar Nivel / Categoria", AON.CSS.aonIconRight());
		salaryDiscBtn = new AonToolbarButton("Desplegar Tabla Salarial", AON.CSS.aonIconRight());
		paymentDiscBtn = new AonToolbarButton("Desplegar Devengos", AON.CSS.aonIconRight());
		paymentExtraDiscBtn = new AonToolbarButton("Desplegar Devengos", AON.CSS.aonIconRight());
		
		levelDiscBtn.addClickHandler(e -> handleIcon(levelDiscBtn, levelDiscPanel.isOpen()));
		salaryDiscBtn.addClickHandler(e -> handleIcon(salaryDiscBtn, salaryDiscPanel.isOpen()));
		paymentDiscBtn.addClickHandler(e -> handleIcon(paymentDiscBtn, paymentDiscPanel.isOpen()));
		paymentExtraDiscBtn.addClickHandler(e -> handleIcon(paymentDiscBtn, paymentDiscPanel.isOpen()));
	}

	private void handleIcon(AonToolbarButton button, boolean open) {
		if(open) {
			button.removeStyleName(AON.CSS.aonIconRight());
			button.addStyleName(AON.CSS.aonIconDown());
			
			if(button.equals(levelDiscBtn)) button.setTitle("Colapsar Nivel / Categoria");
			if(button.equals(levelDiscBtn)) button.setTitle("Colapsar Tabla Salarial");
			if(button.equals(levelDiscBtn)) button.setTitle("Colapsar Devengos");
			if(button.equals(levelDiscBtn)) button.setTitle("Colapsar Extras");
		} else {
			button.removeStyleName(AON.CSS.aonIconDown());
			button.addStyleName(AON.CSS.aonIconRight());
			
			if(button.equals(levelDiscBtn)) button.setTitle("Desplegar Nivel / Categoria");
			if(button.equals(levelDiscBtn)) button.setTitle("Desplegar Tabla Salarial");
			if(button.equals(levelDiscBtn)) button.setTitle("Desplegar Devengos");
			if(button.equals(levelDiscBtn)) button.setTitle("Desplegar Extras");
		}
	}
	
	// ------------------------------------------ toolbar

	private void createToolbar() {
		toolbar = new AonToolbar("Convenio");
		
		saveBtn = new AonToolbarSmallButton(AON.MSG.saveAction(), AON.CSS.aonIconSave());
		saveBtn.ensureDebugId("acceptButton");
		saveBtn.addClickHandler(e -> {
			showLoading("Guardando convenio " + toolbar.getTitle() + " ...");
			setHasChange(false);
			onSaved();
		});
		
		toolbar.add(saveBtn);
		
		newLevelBtn = new AonToolbarSmallButton(AON.MSG.newAction() + " nivel/categoria", AON.CSS.aonIconAdd());
		newLevelBtn.addClickHandler(e -> {
			HorizontalPanel panel = new HorizontalPanel();
			Label description = new Label("Descripci\u00f3n: ");
			TextBox levelDescription = new TextBox();
			levelDescription.setWidth("100%");
			panel.setWidth("98%");
			panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			panel.add(description);
			panel.add(levelDescription);
			AonDialog dialog = new AonDialog("Nuevo nivel", panel);
			dialog.setGlassStyleName(style.dialogGlass());
			dialog.addStyleName(style.dialogZIndex());
			dialog.confirm(new AonAcceptDialogCallback() {
				
				@Override
				public void onCancel() {
					// Not use here
				}
				
				@Override
				public void onAccept() {
					if(AonStringUtils.isBlank(levelDescription.getValue()) || agreement.existLevel(levelDescription.getValue())) {
						showWarning("Nivel existente", "La descripci\u00f3n no puede ser vacia o coincidir con la de otro nivel ya existente");
					} else {
						agreement.createLevel(levelDescription.getValue());
						setAgreementPreview(agreement);
						setHasChange(true);
					}
				}
			});
		});
		
		toolbar.add(newLevelBtn);
		
		newDateBtn = new AonToolbarSmallButton(AON.MSG.newAction() + " tramo", AON.CSS.aonIconAdd());
		newDateBtn.ensureDebugId("newSalaryTabButton");
		newDateBtn.addClickHandler(e -> {
			PopupPanel popup = new PopupPanel(true); // auto-hide
			DatePicker picker = new DatePicker();
			picker.setValue(new Date());
			picker.setYearAndMonthDropdownVisible(true);
			
			picker.addValueChangeHandler(event -> {
				popup.hide();
				Date newPeriod = event.getValue();
				if(agreement.getSortedDates().isEmpty()) {
					agreement.createNewPeriod(newPeriod);
					agreement.setFilteredAllVariables();
					setAgreementPreview(agreement);
					setHasChange(true);
				} else {
					Date maxDate = agreement.getSortedDates().stream().findFirst().get();
					if(maxDate.after(newPeriod) || maxDate.equals(newPeriod))
						showWarning("Error fechas", "No se puede seleccionar un fecha anterior o igual al ultimo tramo existente");
					else {
						agreement.createNewPeriod(newPeriod);
						setAgreementPreview(agreement);
						setHasChange(true);
					}
				}
			});
			
			popup.setWidget(picker);
			popup.setStyleName(style.datePickerPanel());
			popup.showRelativeTo(newDateBtn);

			popup.ensureDebugId("morePopupPanel");
			picker.ensureDebugId("moreDatePicker");
		});
		
		toolbar.add(newDateBtn);
		
		datesLB = new ListBox();
		datesLB.ensureDebugId("datesLB");
		datesLB.getElement().getStyle().setHeight(1.7, Unit.EM);
		datesLB.addChangeHandler(e -> createSalaryTable());
		
		toolbar.add(datesLB);
		
		deleteDateBtn = new AonToolbarSmallButton(AON.MSG.deleteAction() + " tramo", AON.CSS.aonIconDelete());
		deleteDateBtn.ensureDebugId("deleteSalaryTabButton");
		deleteDateBtn.addClickHandler(e -> {
			AonDialog deleteDialog = new AonDialog("Borrar tramo", new HTMLPanel("\u00bfDesea realmente eliminar el tramo <b>" + datesLB.getSelectedValue() +"</b> de la tabla salarial\u003f"));
			deleteDialog.ensureDebugId("deleteDateDialog");
			deleteDialog.setGlassStyleName(style.dialogGlass());
			deleteDialog.addStyleName(style.dialogZIndex());
			deleteDialog.confirm(new AonAcceptDialogCallback() {
				
				@Override
				public void onCancel() {
				}
				
				@Override
				public void onAccept() {
					Date selectedDate = formatDate.parse(datesLB.getSelectedValue());
					agreement.deletePeriod(selectedDate);
					deleteDialog.hide(true);
					setAgreementPreview(agreement);
					showLoading("Guardando convenio " + toolbar.getTitle() + " ...");
					setHasChange(false);
					onSaved();
				}
			});
		});
		
		toolbar.add(deleteDateBtn);
		
		variablesVisivility = new AonToolbarSmallButton("Mostrar/Ocultar variables", AON.CSS.aonIconVisibility());
		variablesVisivility.addClickHandler(click -> {
			Date selectedDate = formatDate.parse(datesLB.getSelectedValue());
			AgreementVariablesDialog dialog = new AgreementVariablesDialog(agreement.getAllVariables(), agreement.getVariablesByDate(selectedDate)) {
				
				@Override
				protected void onAccept(String variablesType, Set<String> variables) {
					switch (variablesType) {
						case "VALUES":
							agreement.setFilteredValuesVariables();
							break;
						case "NO_VALUES":
							agreement.setFilteredNoValuesVariables();
							break;
						case "ALL":
							agreement.setFilteredAllVariables();
							break;
						default:
							agreement.setFilteredVariables(variables);
							break;
					}
					createSalaryTable();	
				}
			};
			dialog.setGlassStyleName(style.dialogGlass());
			dialog.addStyleName(style.dialogZIndex());
			dialog.setShowVariables(agreement.getShownVariables());
		});
		

		toolbar.add(variablesVisivility);
		
		addPaymentButton = new AonToolbarSmallButton("A\u00F1adir Devengo", AON.CSS.aonIconAdd());
		addPaymentButton.ensureDebugId("newPaymentButton");
		addPaymentButton.addClickHandler(e -> new AddAonPaymentCommand().execute());
		
		toolbar.add(addPaymentButton);
		
		
		showOlPaymentsButton = new AonToolbarSmallButton("Mostrar devengo antiguos", AON.CSS.aonIconVisibility());
		showOlPaymentsButton.ensureDebugId("showOlPaymentsButton");
		showOlPaymentsButton.addClickHandler(e -> {
			showOldPayments = !showOldPayments;
			
			if(showOldPayments) {
				showOlPaymentsButton.setTitle("Ocultar devengo antiguos");
				showOlPaymentsButton.removeStyleName(AON.CSS.aonIconVisibility());
				showOlPaymentsButton.addStyleName(AON.CSS.aonIconVisibilityOff());
			} else {
				showOlPaymentsButton.setTitle("Mostrar devengo antiguos");
				showOlPaymentsButton.removeStyleName(AON.CSS.aonIconVisibilityOff());
				showOlPaymentsButton.addStyleName(AON.CSS.aonIconVisibility());
			}
			
			setAgreementPreview(agreement);
		});
		
		toolbar.add(showOlPaymentsButton);
		
		undoAllButton = new AonToolbarButton(AON.MSG.undo(), AON.CSS.aonIconUndoAll());
		undoAllButton.ensureDebugId("undoAllButton");
		setHasChange(false);
		undoAllButton.addClickHandler(e -> {
			AonDialog deleteDialog = new AonDialog("Restaurar convenio", new HTMLPanel("\u00bfDesea realmente deshacer los cambios sin guardar del convenio <b>" + toolbar.getTitle() + "</b>\u003f"));
			deleteDialog.setGlassStyleName(style.dialogGlass());
			deleteDialog.addStyleName(style.dialogZIndex());
			deleteDialog.confirm(new AonAcceptDialogCallback() {
				
				@Override
				public void onCancel() {
					// Not use here
				}
				
				@Override
				public void onAccept() {
					showLoading("Deshaciendo cambios " + toolbar.getTitle() + " ...");
					setHasChange(false);
					reloadAgreement();
				}
			});
		});
		
		toolbar.add(undoAllButton);
		
		
		agreementInfoButton = new AonToolbarButton("Informaci\u00f3n Convenio", AON.CSS.aonIconInfo());
		agreementInfoButton.ensureDebugId("infoButton");
		agreementInfoButton.addClickHandler(e -> 
			impl.getAgreementUsedInfo(agreement.getId(), agreement.getDescription(), new AsyncCallback<String>() {
				
				@Override
				public void onSuccess(String message) {
					AonDialog dialog = new AonDialog(agreement.getDescription(), new HTML(message));
					dialog.setGlassStyleName(style.dialogGlass());
					dialog.addStyleName(style.dialogZIndex());
					dialog.info();
				}
				
				@Override
				public void onFailure(Throwable caught) {
					showError("Error informaci\u00f3n convenio", caught.getMessage());
				}
			})
		);
			
		toolbar.add(agreementInfoButton);
		
		serviAgreementUpdateButton = new AonToolbarButton("Actualizar Convenio", AON.CSS.aonIconCloudImport());
		serviAgreementUpdateButton.addClickHandler(e -> 
			impl.getDomainUserRoles(new AsyncCallback<DomainUserRoles>() {
				
				@Override
				public void onSuccess(DomainUserRoles userRole) {
					if(userRole.isConvenios()) {
						
						impl.canUpdateServiAgreement(agreement, new AsyncCallback<Boolean>() {

							@Override
							public void onFailure(Throwable caught) {
								showError("Error actualizaci\u00F3n", caught.getMessage());
							}

							@Override
							public void onSuccess(Boolean canUpdate) {
								if(!canUpdate)
									showSuccess("Actualizaci\u00F3n", "El convenio se encuentra actualizado, no existen nuevos tramos");
								else {
									PaymentsCleanDialog dialog = new PaymentsCleanDialog(agreement.getPayments()) {
										
										@Override
										public void onAccept() {
											showLoading("Actualizando convenio");
											impl.checkAndUpdateServiAgreement(agreement, new AsyncCallback<Void>() {

												@Override
												public void onFailure(Throwable caught) {
													showError("Error actualizaci\u00F3n", caught.getMessage());
												}

												@Override
												public void onSuccess(Void result) {
													showSuccess("Actualizaci\u00F3n", "El convenio ha sido actualizado correctamente");
													reloadAgreement();
												}});
										}
									
									};
									
									dialog.setGlassStyleName(style.dialogGlass());
									dialog.addStyleName(style.dialogZIndex());
								}
							}});
						
					} else 
						showError("Actualizaci\u00f3n no disponible", "Para poder actualizar un convenio a traves de ServiConvenios debe tener contrato el m\u00f3dulo.");
				}
				
				@Override
				public void onFailure(Throwable caught) {
					showError("Error", caught.getMessage());
				}
			})
		);
		toolbar.add(serviAgreementUpdateButton);
		
		
		printPreviewButton = new AonToolbarButton(AON.MSG.draftPrint(), AON.CSS.aonIconPdf() );
		printPreviewButton.ensureDebugId("printPreviewButton");
		printPreviewButton.addClickHandler(e -> {
			showAgreementSimulator();
			initTc2ListBox();
			initLevelListBox();
			printPreview();
		});
		toolbar.add(printPreviewButton);
		
		setHasChange(false);
		
	}
	
	private void showCategoryButtons() {
		if(readOnly)
			showReadOnlyButtons();
		else {
			newLevelBtn.setVisible(true);
			
			newDateBtn.setVisible(false);
			datesLB.setVisible(false);
			deleteDateBtn.setVisible(false);
			variablesVisivility.setVisible(false);
			
			addPaymentButton.setVisible(false);
			showOlPaymentsButton.setVisible(false);
		}
	}
	
	private void showSalaryTableButtons() {
		if(readOnly) {
			showReadOnlyButtons();
			datesLB.setVisible(!agreement.getDates().isEmpty());
		} else {
			newLevelBtn.setVisible(false);
			
			newDateBtn.setVisible(true);
			datesLB.setVisible(true);
			deleteDateBtn.setVisible(true);
			variablesVisivility.setVisible(true);
			
			addPaymentButton.setVisible(false);
			showOlPaymentsButton.setVisible(false);
		}
	}
	
	private void showPaymentButtons() {
		if(readOnly)
			showReadOnlyButtons();
		else {
			newLevelBtn.setVisible(false);
			
			newDateBtn.setVisible(false);
			datesLB.setVisible(false);
			deleteDateBtn.setVisible(false);
			variablesVisivility.setVisible(false);
			
			addPaymentButton.setVisible(true);
			showOlPaymentsButton.setVisible(true);
		}
	}
	
	private void showReadOnlyButtons() {
		saveBtn.setVisible(false);
		undoAllButton.setVisible(false);
		
		newLevelBtn.setVisible(false);
		
		newDateBtn.setVisible(false);
		datesLB.setVisible(false);
		deleteDateBtn.setVisible(false);
		variablesVisivility.setVisible(false);
		
		addPaymentButton.setVisible(false);
		showOlPaymentsButton.setVisible(false);
		
		serviAgreementUpdateButton.setVisible(false);
	}
	
	// ------------------------------------------ abstractMethod
	
	protected abstract void reloadAgreement();
	
	// ------------------------------------------ toolbarSimulator

	private void createToolbarSimulator() {
		toolbarSimulator = new AonToolbar("Simulador");
		
		AonToolbarButton closeSimulatorBtn = new AonToolbarButton(AON.MSG.close(), AON.CSS.aonIconClose());
		closeSimulatorBtn.ensureDebugId("closeSimulatorBtn");
		closeSimulatorBtn.addClickHandler(e -> {
			setPDFLoadedEnsureDebugId("pdfNotLoaded");
			showAgreementPreview();
		});
		toolbarSimulator.add(closeSimulatorBtn);
		
		tc2ListBox = new ListBox();
		tc2ListBox.setWidth("250px");
		tc2ListBox.addChangeHandler(e -> printPreview());
		toolbarSimulator.add(tc2ListBox);
		
		levelListBox = new ListBox();
		levelListBox.setWidth("250px");
		levelListBox.addChangeHandler(e -> printPreview());
		toolbarSimulator.add(levelListBox);
		
		Label partilialityL = new Label("Coef. Part.");
		partilialityL.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		partialTextBox = new TextBox();
		partialTextBox.setValue("1");
		partialTextBox.setMaxLength(5);
		partialTextBox.setVisibleLength(5);
		partialTextBox.setAlignment(TextAlignment.RIGHT);
		partialTextBox.addValueChangeHandler(e -> printPreview());
		toolbarSimulator.add(partilialityL);
		toolbarSimulator.add(partialTextBox);
		
		Label quoteGroupL = new Label("Grup. Cotiz.");
		quoteGroupL.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		groupListBox = new ListBox();
		initQuoteGroup(groupListBox);
		groupListBox.addChangeHandler(e -> printPreview());
		toolbarSimulator.add(quoteGroupL);
		toolbarSimulator.add(groupListBox);
		
		pdfLoaded = new Label();
		setPDFLoadedEnsureDebugId("pdfNotLoaded");
		toolbarSimulator.add(pdfLoaded);
	}
	
	public void setPDFLoadedEnsureDebugId(String debugId) {
		this.pdfLoaded.ensureDebugId(debugId);
	}

	private void initTc2ListBox() {
		tc2ListBox.clear();

		for (Entry<Integer, ContractTypeRecord> entry : new ContractType().getContractTypes().entrySet()) { 
			String value = AonStringUtils.leftPad(entry.getKey().toString(), 3, '0');
			String item = entry.getKey() + " - " + AonStringUtils.upperCase(AonStringUtils.abbreviate(entry.getValue().getContractTypeShortDescription(),40));
			tc2ListBox.addItem(item, value);
		}
		
		tc2ListBox.setSelectedIndex(1);// 100
	}
	
	private void initLevelListBox() {
		levelListBox.clear();
		for (Level level : agreement.getLevels()) {
			if (level.getId() == 0)
				continue;
			String levelDescription = level.getDescription();
			StringBuilder buffer = new StringBuilder();
			if (!AonStringUtils.isBlank(levelDescription))
				buffer.append(levelDescription);

			Set<String> categories = agreement.getCategoriesMap().get(level.getId());
			if (categories != null) {
				for (String category : categories) {
					if (!AonStringUtils.isBlank(category)) {
						buffer.append(" " + category);
						break;
					}
				}
			}
			levelListBox.addItem(buffer.toString(), Integer.toString(level.getId()));
		}
	}
	
	private void initQuoteGroup(ListBox quoteGroup) {
		quoteGroup.clear();
		quoteGroup.addItem("1", "01");
		quoteGroup.addItem("2", "02");
		quoteGroup.addItem("3", "03");
		quoteGroup.addItem("4", "04");
		quoteGroup.addItem("5", "05");
		quoteGroup.addItem("6", "06");
		quoteGroup.addItem("7", "07");
		quoteGroup.addItem("8", "08");
		quoteGroup.addItem("9", "09");
		quoteGroup.addItem("10", "10");
		quoteGroup.addItem("11", "11");
	}
	
	// ------------------------------------------ printPreview
	
	private void printPreview() {
		showLoading("Preparando simulador del borrador...");
		
		int levelId = getLevelId();
		String tc2 = getTc2();
		String group = getGroup();
		double partial = getPartial();
		
		List<Variable> context = new ArrayList<>();
		context.add(new StringVariable.Builder().setName("TC2").setValue(tc2).create());
		context.add(new StringVariable.Builder().setName("GRUPO_COTIZACION").setValue(group).create());
		context.add(new NumberVariable.Builder().setName("COEFICIENTE_PARCIALIDAD").setValue(partial).create());
		
		impl.getAgreementDraftReceipt(agreement, context, levelId, "application/pdf", new AsyncCallback<String>() {

			@Override
			public void onSuccess(String html) {
				hideMessage();
				setPartial(partial);
				if(null != html) 
					setPDFLoadedEnsureDebugId("pdfLoaded");
				printPreviewViewer.open(html);
			}

			@Override
			public void onFailure(Throwable caught) {
				setPDFLoadedEnsureDebugId("pdfNotLoaded");
				showError("Error simulador", caught.getMessage());
			}
		});

	}

	private int getLevelId() {
		int index = levelListBox.getSelectedIndex();
		String value = levelListBox.getValue(index);
		return Integer.valueOf(value);
	}

	private String getTc2() {
		return tc2ListBox.getSelectedValue();
	}

	private String getGroup() {
		return groupListBox.getSelectedValue();
	}
	
	private void setPartial(double partial) {
		partialTextBox.setValue(Double.toString(partial), false);
	}
	
	private double getPartial() {
		String text =  partialTextBox.getText();
		try {
			return Double.parseDouble(text);
		} catch ( Exception e ) {
			return 1.0;
		}
	}
	
	// ------------------------------------------ setSelectedLevel
	
	public void setSelectedLevel(Integer levelId, String toolbarTitle) {
		blockElements();
		hideToolbarButtons();
		hideLevelDiscPanel();
		employeeView = true;
		setReadOnly(true);
		filterLevel(levelId);
		toolbar.setTitle(toolbarTitle);
		calcDiscPanelHeightsEmployee();
		createAgreementGoToBtn();
		saveBtn.setVisible(false);
		undoAllButton.setVisible(false);
	}

	private void filterLevel(Integer levelId) {
		setSelectedValueLB(categoryLB, null == levelId ? "" : String.valueOf(levelId));
		filterSelectedCategory();
	}

	public void payrollPreview() {
		blockElements();
		hideToolbarButtons();
		hideLevelDiscPanel();
		workplaceView = true;
		setReadOnly(true);
		createSalaryTable();
		calcDiscPanelHeightsPayroll();
		createAgreementGoToBtn();
		saveBtn.setVisible(false);
		undoAllButton.setVisible(false);
	}
	
	private void blockElements() {
		description.setEnabled(false);
		ssNumber.setEnabled(false);
		description.getElement().getStyle().setBackgroundColor("transparent");
		ssNumber.getElement().getStyle().setBackgroundColor("transparent");
	}
	
	private void hideToolbarButtons() {
		saveBtn.addStyleName(style.displayNone());
		undoAllButton.addStyleName(style.displayNone());
		serviAgreementUpdateButton.addStyleName(style.displayNone());
		printPreviewButton.addStyleName(style.displayNone());
		agreementInfoButton.addStyleName(style.displayNone());
	}
	
	private void hideLevelDiscPanel() {
		levelDiscPanel.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void calcDiscPanelHeightsEmployee() {
		Scheduler.get().scheduleDeferred(() -> {
			int salaryHeight = (salaryGrid.getRowCount() * 20) + 20;
			
			salaryDiscPanelContent.setHeight(salaryHeight + "px");
			salaryScrollPanel.setHeight("100%");
			salaryScrollPanel.setWidth((Window.getClientWidth() - 360) + "px");
			
			paymentDiscPanelContent.setHeight((Window.getClientHeight() - 480) + "px");
			extraDiscPanelContent.setHeight((Window.getClientHeight() - 480) + "px");
			
			agreementPaymentDG.setHeight((Window.getClientHeight() - 495) + "px");
			agreementPaymentDG.redraw();
			
			salaryOpenHandler.removeHandler();
			salaryDiscPanel.addOpenHandler(e -> handleIcon(salaryDiscBtn, true));
			
			paymentOpenHandler.removeHandler();
			paymentDiscPanel.addOpenHandler(e -> {
				handleIcon(paymentDiscBtn, true);
				
				paymentExtraDiscPanel.setOpen(false);
				handleIcon(paymentExtraDiscBtn, false);
				
				setScrollDGHeightEmployee();
				agreementPaymentDG.redraw();
			});
			
			paymentExtraOpenHandler.removeHandler();
			paymentExtraDiscPanel.addOpenHandler(e -> {
				handleIcon(paymentExtraDiscBtn, true);
				
				paymentDiscPanel.setOpen(false);
				handleIcon(paymentDiscBtn, false);
				
				setScrollDGHeightEmployee();
				agreementExtraPaymentDG.redraw();
			});
			
			paymentDiscPanel.setOpen(true);
			salaryDiscPanel.setOpen(true);
			
		});
	}
	
	private void calcDiscPanelHeightsPayroll() {
		Scheduler.get().scheduleDeferred(() -> {
			int salaryHeight = salaryGrid.getRowCount() > 8 ? 180 : (salaryGrid.getRowCount() * 20) + 20;
			salaryDiscPanelContent.setHeight((salaryHeight) + "px");
			salaryScrollPanel.setHeight("100%");
			salaryScrollPanel.setWidth((Window.getClientWidth() - 360) + "px");
			
			paymentDiscPanelContent.setHeight((Window.getClientHeight() - 405 - salaryHeight) + "px");
			extraDiscPanelContent.setHeight((Window.getClientHeight() - 405 - salaryHeight) + "px");
			
			agreementPaymentDG.setHeight((Window.getClientHeight() - 420 - salaryHeight) + "px");
			agreementPaymentDG.redraw();
			
			salaryOpenHandler.removeHandler();
			salaryDiscPanel.addOpenHandler(e -> handleIcon(salaryDiscBtn, true));
			
			paymentOpenHandler.removeHandler();
			paymentDiscPanel.addOpenHandler(e -> {
				handleIcon(paymentDiscBtn, true);
				
				paymentExtraDiscPanel.setOpen(false);
				handleIcon(paymentExtraDiscBtn, false);
				
				setScrollDGHeightWorkplace(salaryHeight);
				agreementPaymentDG.redraw();
			});
			
			paymentExtraOpenHandler.removeHandler();
			paymentExtraDiscPanel.addOpenHandler(e -> {
				handleIcon(paymentExtraDiscBtn, true);
				
				paymentDiscPanel.setOpen(false);
				handleIcon(paymentDiscBtn, false);
				
				setScrollDGHeightWorkplace(salaryHeight);
				agreementExtraPaymentDG.redraw();
			});
			
			paymentDiscPanel.setOpen(true);
			salaryDiscPanel.setOpen(true);
		});
	}
	
	private void createAgreementGoToBtn() {
		FlowPanel toolbarButtons = toolbar.getButtonContainer();
		if(toolbarButtons.getWidgetCount() > 4)
			toolbarButtons.remove(toolbarButtons.getWidgetCount() - 1);
		
		AonToolbarButton goToAgreementBtn = new AonToolbarButton("Ir al convenio " + agreement.getDescription(), AON.CSS.aonIconOpenInNew());
		goToAgreementBtn.addClickHandler(e -> goToAgreement(agreement.getId()));
		
		// TODO: quitar esta linea cuando este implementado
		goToAgreementBtn.setVisible(false);
		
		toolbar.add(goToAgreementBtn);
	}
	
	private void goToAgreement(Integer agreementId) {
		// TODO: Aqui iria la navegacion a los convenios
		// MainAgreement mainAgreementTab = new MainAgreement();
		// mainAgreementTab.onModuleLoad();
		// mainAgreementTab.agreements.getAgreementsAndSelectImported(agreementId, s -> {});
	}
	
	public void setToolbarTitle(String title) {
		toolbar.setTitle(title);
	}

	// ------------------------------------------ HasChange
	
	public boolean hasChange() {
		return hasChange;
	}

	public void setHasChange(boolean hasChange) {
		this.hasChange = hasChange;
		saveBtn.setEnabled(hasChange());
		undoAllButton.setEnabled(hasChange());
		if(!hasChange()) {
			saveBtn.getElement().getStyle().setDisplay(Display.BLOCK);
			saveBtn.getElement().getStyle().setVisibility(Visibility.VISIBLE);
			undoAllButton.getElement().getStyle().setDisplay(Display.BLOCK);
			undoAllButton.getElement().getStyle().setVisibility(Visibility.VISIBLE);
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
	
	public void showWarning(String title, String message) {
		Map<String, String> warningMap = new HashMap<>();
		warningMap.put(title, message);
		AonMessagePanel.showWarning(messagePanel, warningMap);
	}

	public void showError(String title, String message) {
		Map<String, String> errorMap = new HashMap<>();
		errorMap.put(title, message);
		AonMessagePanel.showError(messagePanel, errorMap);
	}
	
	public void showLoading(String message) {
		AonMessagePanel.showLoading(messagePanel, message);
	}

	private void hideMessage() {
		AonMessagePanel.hideMessage(messagePanel);
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
		if(readOnly) {
			showReadOnlyButtons();
			saveBtn.setVisible(false);
			undoAllButton.setVisible(false);
			datesLB.setVisible(true);
		}
	}
	
}
