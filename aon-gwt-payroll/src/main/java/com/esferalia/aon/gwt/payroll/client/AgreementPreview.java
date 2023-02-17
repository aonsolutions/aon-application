package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
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
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmall;
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
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
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

import net.aonsolutions.gwt.pdfjs.client.FullViewer;

public abstract class AgreementPreview extends Composite {

	// ------------------------------------------ UiBinder

	private static AgreementPreviewUiBinder uiBinder = GWT.create(AgreementPreviewUiBinder.class);

	interface AgreementPreviewUiBinder extends UiBinder<Widget, AgreementPreview> {}

	// ------------------------------------------ ScheduledCommand

	class AddAonPaymentCommand implements ScheduledCommand {

		@Override
		public void execute() {
			AgreementSuggestPaymentDialog paymentDialog = new AgreementSuggestPaymentDialog() {

				@Override
				protected void onAccept(Payment payment) {
					payment.setModify(true);
					agreement.addPayment(payment);
					setAgreementPreview(agreement);
					setHasChange(true);
				}

				@Override
				protected void onManualEdition() {
					AgreementPaymentWizard wizardDialog = new AgreementPaymentWizard(agreement.getPayments(), null) {

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

							if (null != extra)
								agreement.addExtra(extra);

							checkPairExtras(payment, extra);

							setAgreementPreview(agreement);
							setHasChange(true);

						}

						private void checkPairExtras(Payment payment, Extra extra) {
							if (payment.getType().equals(Payment.Type.CRA_0004)) {
								Optional<Payment> searchPayment = agreement.getPayments().stream()
										.filter(paymentIt -> !paymentIt.equals(payment)
												&& paymentIt.getType().equals(Payment.Type.CRA_0004))
										.findAny();
								if (!searchPayment.isPresent()) {
									Payment associatedPayment = new Payment();
									Random rand = new Random();
									int newPaymentId = rand.nextInt(1000) * -1;
									if (newPaymentId > 0)
										newPaymentId = newPaymentId * -1;
									associatedPayment.setId(newPaymentId);
									associatedPayment.setDomain(payment.getDomain());
									associatedPayment.setModify(true);

									associatedPayment.setType(Payment.Type.CRA_0004);
									associatedPayment.setConceptId(payment.getConceptId());
									associatedPayment.setName(payment.getName());

									associatedPayment.setDescription(
											AonStringUtils.containsIgnoreCase(payment.getDescription(), "verano")
													? "PAGA NAVIDAD"
													: "PAGA VERNAO");
									associatedPayment.setExpression(payment.getExpression());
									associatedPayment.setIrpfExpression(payment.getIrpfExpression());
									associatedPayment.setQuoteExpression(payment.getQuoteExpression());
									associatedPayment.setMonth(null);

									agreement.addPayment(associatedPayment);

									if (null != extra) {
										int newExtraId = rand.nextInt(1000) * -1;
										AgreementExtra associatedExtra = new AgreementExtra();
										associatedExtra.setId(newExtraId);

										associatedExtra.setDomain(payment.getDomain());
										associatedExtra.setAgreementPayment(associatedPayment.getId());

										if (AonStringUtils.containsIgnoreCase(extra.getIssueDate(), "06")
												|| AonStringUtils.containsIgnoreCase(extra.getIssueDate(), "07")) {
											associatedExtra.setIssueDate("31/12");
											if (AonStringUtils.containsIgnoreCase(extra.getStartDate(), "-1")) {
												associatedExtra.setStartDate("01/01");
												associatedExtra.setEndDate("31/12");
											} else {
												associatedExtra.setStartDate("01/07");
												associatedExtra.setEndDate("31/12");
											}
										} else if (AonStringUtils.containsIgnoreCase(extra.getIssueDate(), "12")) {
											associatedExtra.setIssueDate("30/06");
											if (AonStringUtils.containsIgnoreCase(extra.getStartDate(), "01")) {
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
							if (!payments.isEmpty()) {
								for (Payment payment : payments) {
									payment.setModify(true);
									agreement.addPayment(payment);
								}
							}
							setAgreementPreview(agreement);
							setHasChange(true);
						}
					};
					
					wizardDialog.setGlassStyleName(style.dialogGlass());
					wizardDialog.addStyleName(style.dialogZIndex());
				}

			};
			
			paymentDialog.setGlassStyleName(style.dialogGlass());
			paymentDialog.addStyleName(style.dialogZIndex());
			
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

		String elipsisConceptWidth();

		String elipsisOpenCollapseWidth();

		String elipsisCloseCollapseWidth();

		String elipsisOpenCollapseExtraWidth();

		String elipsisCloseCollapseExtraWidth();

		String zIndex1();
		
		String flexCenter();
		
		String unSelectDate ();
		
		String selectDate ();
		
		String p0();
		
		String gap05();
		
		String p5();
	}

	@UiField
	HTMLPanel messagePanel;

	@UiField
	DeckLayoutPanel deckLayoutPanel;

	@UiField(provided = true)
	AonToolbar toolbar;

	@UiField
	ScrollPanel scrollPanel;

	@UiField
	TextBox description;

	@UiField
	TextBox ssNumber;

	@UiField
	HTMLPanel serviAgreementPanel;

	// LEVEL / CATEGORY && SALARY TABLE

	@UiField(provided = true)
	AonToolbarSmall levelSalaryToolbar;
	
	@UiField
	HTMLPanel levelSalaryTabs;

	@UiField
	DeckPanel levelSalaryDeck;

	@UiField
	ScrollPanel levelScrollPanel;

	@UiField
	Grid levelGrid;

	@UiField
	ScrollPanel salaryScrollPanel;

	@UiField
	Grid salaryGrid;

	// PAYMENT

	@UiField(provided = true)
	AonToolbarSmall paymentToolbar;

	@UiField
	DeckPanel paymentDeck;

	@UiField
	Grid paymentGrid;

	// EXTRA

	@UiField(provided = true)
	AonToolbarSmall extraToolbar;

	@UiField
	DeckPanel extraDeck;

	@UiField
	Grid extraGrid;

	// AGREEMENT SIMULATOR

	@UiField(provided = true)
	AonToolbar toolbarSimulator;

	@UiField
	FullViewer printPreviewViewer;

	// ------------------------------------------ Variables

	private DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	private AgreementInfo agreement;

	private ListBox categoryLB;

	private AonToolbarButton saveBtn;
	private AonToolbarButton printPreviewButton;
	private AonToolbarButton serviAgreementUpdateButton;
	private AonToolbarButton agreementInfoButton;
	private AonToolbarButton undoAllButton;

	private boolean hasChange = false;
	private boolean workplaceView = false;
	private boolean employeeView = false;
	private boolean showOldPayments = false;
	private boolean isOpenCollapse = true;
	private boolean readOnly = false;

	private List<Payment> paymentList;
	private List<Payment> paymentExtraList;

	// Toolbar

	
	private AonToolbarSmallButton newLevelBtn;
	private AonToolbarSmallButton newDateBtn;
	private AonToolbarSmallButton deleteDateBtn;
	private AonToolbarSmallButton variablesVisivility;
	private AonToolbarSmallButton addPaymentButton;
	private AonToolbarSmallButton showOlPaymentsButton;
	
	private HTMLPanel datesTabs;

	private ListBox tc2ListBox;
	private ListBox levelListBox;
	private TextBox partialTextBox;
	private ListBox groupListBox;
	private Label pdfLoaded;

	private ArrayList<Label> dateLabels;

	private Date selectedDate;

	private AonToolbarSmallButton categoriesBtn;
	private AonToolbarSmallButton salaryTableBtn;

	private AonToolbarSmallButton levelSalaryDiscBtn;
	private AonToolbarSmallButton paymentDiscBtn;
	private AonToolbarSmallButton extraDiscBtn;

	private boolean isSalaryTableSelected = true;

	private boolean isLevelSalaryOpen = true;
	private boolean isPaymentOpen = true;
	private boolean isExtraOpen = true;

	// ------------------------------------------ Constructor

	protected AgreementPreview() {
		createToolbar();
		createLevelSalaryToolbar();
		createPaymentToolbar();
		createExtraToolbar();
		createToolbarSimulator();

		initWidget(uiBinder.createAndBindUi(this));

		scrollPanel.setHeight((Window.getClientHeight() - 200) + "px");
		
		levelSalaryToolbar.addStyleName(style.p0());
		paymentToolbar.addStyleName(style.p0());
		extraToolbar.addStyleName(style.p0());
		
		paymentGrid.getParent().addStyleName(style.flexCenter());
		extraGrid.getParent().addStyleName(style.flexCenter());
	}

	// ----------------------------------------------- PaymenteTable methods

	private void openDialog(Payment payment) {
		boolean isHide = AonStringUtils.isNotBlank(payment.getExpression())
				&& AonStringUtils.containsIgnoreCase(payment.getExpression(), "HIDE")
				&& AonStringUtils.startsWithIgnoreCase(payment.getExpression(), "HIDE");
		
		Date startDate = null;
		if(!agreement.getSortedDates().isEmpty())
			startDate = agreement.getSortedDates().stream().findFirst().get();
		
		AgreementPaymentEditor paymentDialog = new AgreementPaymentEditor(payment, agreement.getExtraPayment(payment.getId()), agreement.getPayments(),startDate) {
			
			@Override
			protected void onAccept(Payment updatedPayment, AgreementExtra extra, Payment associatedPayment,
					AgreementExtra associatedExtra) {
				updatePaymentExpresion(isHide, payment, updatedPayment);
				agreement.replacePayment(payment);

				if (null != extra && null != extra.getId() && extra.getId() > 0)
					agreement.replaceExtra(extra);
				if (null != extra && null != extra.getId())
					agreement.addExtra(extra);

				if (null != associatedExtra && null != associatedExtra.getId() && associatedExtra.getId() > 0)
					agreement.replaceExtra(associatedExtra);

				if (null != associatedPayment)
					agreement.addPayment(associatedPayment);
				if (null != associatedExtra && (null == associatedExtra.getId() || associatedExtra.getId() < 0))
					agreement.addExtra(associatedExtra);

				setAgreementPreview(agreement);
				setHasChange(true);
			}

			private void updatePaymentExpresion(boolean isHide, Payment selectedPayment, Payment updatedPayment) {
				selectedPayment.setExpression(Boolean.TRUE.equals(isHide)
						? showHidePayment(updatedPayment.getDescription(), updatedPayment.getExpression())
						: updatedPayment.getExpression());
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
		
		paymentDialog.setGlassStyleName(style.dialogGlass());
		paymentDialog.addStyleName(style.dialogZIndex());

	}

	private boolean isHideExpression(Payment payment) {
		String expression = payment.getExpression();
		return !AonStringUtils.isBlank(expression) && AonStringUtils.containsIgnoreCase(expression, "HIDE")
				&& AonStringUtils.startsWithIgnoreCase(expression, "HIDE");
	}

	public String showHidePayment(String description, String expression) {
		if (!AonStringUtils.isBlank(expression) && AonStringUtils.containsIgnoreCase(expression, "HIDE")
				&& AonStringUtils.startsWithIgnoreCase(expression, "HIDE"))
			expression = expression.replaceAll("HIDE\\(.*\\);\\s", "");
		else
			expression = "HIDE(\"<div>" + description
					+ " oculto desde Convenio</div><div>&nbsp;</div><div class='aon-text-right'><span class='aon-icon aon-icon-logo'/>aon Solutions</div>\"); "
					+ expression;

		return expression;
	}

	public void showHidePayment(Payment payment) {
		String expression = payment.getExpression();

		expression = !AonStringUtils.isBlank(expression) && AonStringUtils.containsIgnoreCase(expression, "HIDE")
				&& AonStringUtils.startsWithIgnoreCase(expression, "HIDE")
						? expression.replaceAll("HIDE\\(.*\\);\\s", "")
						: "HIDE(\"<div>" + payment.getDescription()
								+ " oculto desde Convenio</div><div>&nbsp;</div><div class='aon-text-right'><span class='aon-icon aon-icon-logo'/>aon Solutions</div>\"); "
								+ expression;

		payment.setExpression(expression);
	}

	private void getEnableDisableButton(Button button, boolean disabled, boolean readOnly) {
		button.removeStyleName(
				disabled ? AON.AON_ICON_DISABLE : (readOnly ? AON.AON_ICON_ENABLE_GRAY : AON.AON_ICON_ENABLE));
		button.removeStyleName(AON.AON_NO_MARGIN);
		button.removeStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON);

		button.setStyleName(
				!disabled ? AON.AON_ICON_DISABLE : (readOnly ? AON.AON_ICON_ENABLE_GRAY : AON.AON_ICON_ENABLE));
		button.setStyleName(AON.AON_NO_MARGIN, true);
		button.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
	}

	private void checkRowAndModify(Grid grid, int row, Payment payment, Widget widget) {
		if (row % 2 == 0)
			widget.addStyleName(style.oddRow());

		if (payment.isModify()) {
			widget.addStyleName(style.modify());

			grid.getCellFormatter().addStyleName(row, 0, style.modify());
			grid.getCellFormatter().addStyleName(row, 1, style.modify());
			grid.getCellFormatter().addStyleName(row, 2, style.modify());
			grid.getCellFormatter().addStyleName(row, 3, style.modify());
			grid.getCellFormatter().addStyleName(row, 4, style.modify());
			grid.getCellFormatter().addStyleName(row, 5, style.modify());
			grid.getCellFormatter().addStyleName(row, 6, style.modify());
			grid.getCellFormatter().addStyleName(row, 7, style.modify());
		} else {
			widget.removeStyleName(style.modify());

			grid.getCellFormatter().removeStyleName(row, 0, style.modify());
			grid.getCellFormatter().removeStyleName(row, 1, style.modify());
			grid.getCellFormatter().removeStyleName(row, 2, style.modify());
			grid.getCellFormatter().removeStyleName(row, 3, style.modify());
			grid.getCellFormatter().removeStyleName(row, 4, style.modify());
			grid.getCellFormatter().removeStyleName(row, 5, style.modify());
			grid.getCellFormatter().removeStyleName(row, 6, style.modify());
			grid.getCellFormatter().removeStyleName(row, 7, style.modify());
		}

	}

	// ------------------------------------------ setAgreementPreview

	public void setAgreementPreview(AgreementInfo agreementIn) {
		agreement = agreementIn;

		showAgreementPreview();
		fillAgreementInfo();
		hideMessage();
	}

	// ------------------------------------------ fillAgreementInfo

	private void fillAgreementInfo() {
		fillInfo();

		this.selectedDate = null == selectedDate && !agreement.getSortedDates().isEmpty() ? agreement.getSortedDates().stream().findFirst().get() : selectedDate;
		
		categoriesBtn = new AonToolbarSmallButton("Categorias", AON.CSS.aonIconList());
		salaryTableBtn = new AonToolbarSmallButton("Tabla salarial", AON.CSS.aonIconStatics());

		categoriesBtn.addClickHandler(e -> {
			isSalaryTableSelected = false;
			categoriesBtn.setVisible(false);
			salaryTableBtn.setVisible(true);
			handleIcon(levelSalaryDiscBtn, isLevelSalaryOpen);
			showLevelTable();
		});

		salaryTableBtn.addClickHandler(e -> {
			isSalaryTableSelected = true;
			categoriesBtn.setVisible(true);
			salaryTableBtn.setVisible(false);
			handleIcon(levelSalaryDiscBtn, isLevelSalaryOpen);
			showSalaryTable();
		});

		categoriesBtn.setVisible(!readOnly && isSalaryTableSelected && !agreement.getSortedDates().isEmpty());
		salaryTableBtn.setVisible(!readOnly && (!isSalaryTableSelected || agreement.getSortedDates().isEmpty()));

		if (isSalaryTableSelected && !agreement.getSortedDates().isEmpty())
			showSalaryTable();
		else
			showLevelTable();
		
		Set<Payment> payments = agreement.getPaymentsAndHides();
		Set<Payment> paymentsOld = agreement.getOldPaymentsAndHides();
		this.showOlPaymentsButton.setVisible(payments.size() != paymentsOld.size());

		showPaymentTable();
		showExtraTable();

		setTablesWidth();
	}

	private void fillInfo() {
		toolbar.setTitle(agreement.getDescription());

		serviAgreementUpdateButton.setVisible(!readOnly && agreement.getOwner().equals(AgreementOwner.SERVICONVENIOS));
		printPreviewButton.setVisible(!readOnly && !agreement.getDates().isEmpty());

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

		if (agreement.getOwner().equals(AgreementOwner.SERVICONVENIOS))
			createServiAgreementPanel();
		else
			serviAgreementPanel.clear();
	}

	// ------------------------------------------ serviAgreementPanel

	private void createServiAgreementPanel() {
		serviAgreementPanel.clear();

		Label serviAgreementLabel = new Label("Vinculado con ServiConvenios");
		serviAgreementLabel.getElement().getStyle().setMarginLeft(10, Unit.PX);
		serviAgreementPanel.add(serviAgreementLabel);

		AonToolbarButton serviAgreementPDFButton = new AonToolbarButton("ServiConvenios PDF", AON.CSS.aonIconPdf());
		serviAgreementPDFButton.addClickHandler(e -> {
			String url = GWT.getModuleBaseURL() + "servi_agreement?fileType=pdf&ssNumber=" + agreement.getSSNumber();
			Window.open(url, "_blank", "status=0,toolbar=0,menubar=0,location=0");
		});
		serviAgreementPanel.add(serviAgreementPDFButton);

		AonToolbarButton serviAgreementXLSButton = new AonToolbarButton("ServiConvenios XLS", AON.CSS.aonIconExcel());
		serviAgreementXLSButton.addClickHandler(e -> {
			String url = GWT.getModuleBaseURL() + "servi_agreement?fileType=xls&ssNumber=" + agreement.getSSNumber();
			Window.open(url, "_blank", "status=0,toolbar=0,menubar=0,location=0");
		});
		serviAgreementPanel.add(serviAgreementXLSButton);
	}

	// ------------------------------------------ salaryTable

	private void createSalaryTable() {
		createLevelSalaryTabs();
		getSalaryTableHeader();
		fillSalaryTable();
		salaryTableWidth();
		if (employeeView)
			salaryGrid.removeRow(1);
		setTableHeight();
	}

	private void createLevelSalaryTabs() {
		levelSalaryTabs.clear();
		
		levelSalaryTabs.add(categoriesBtn);
		levelSalaryTabs.add(salaryTableBtn);
		
		datesTabs = new HTMLPanel("");
		datesTabs.ensureDebugId("datesTabs");
		datesTabs.addStyleName(style.flex());
		datesTabs.addStyleName(style.gap05());
		
		if(isSalaryTableSelected) {
			agreement.getSortedDates().forEach(date -> {
				if(date.equals(selectedDate)) {
					HTMLPanel selectedDatePanel = new HTMLPanel("");
					selectedDatePanel.addStyleName(style.flex());
					selectedDatePanel.addStyleName(style.selectDate());
					
					Label dateLabel = new Label(formatDate.format(date));
					selectedDatePanel.add(dateLabel);
					
					if(!readOnly) {
					
						deleteDateBtn = new AonToolbarSmallButton(AON.MSG.deleteAction() + " tramo", AON.CSS.aonIconDelete());
						deleteDateBtn.ensureDebugId("deleteSalaryTabButton");
						deleteDateBtn.addClickHandler(e -> {
							AonDialog deleteDialog = new AonDialog("Borrar tramo",
									new HTMLPanel("\u00bfDesea realmente eliminar el tramo <b>" + formatDate.format(selectedDate)
											+ "</b> de la tabla salarial\u003f"));
							deleteDialog.ensureDebugId("deleteDateDialog");
							deleteDialog.setGlassStyleName(style.dialogGlass());
							deleteDialog.addStyleName(style.dialogZIndex());
							deleteDialog.confirm(new AonAcceptDialogCallback() {
	
								@Override
								public void onCancel() {
								}
	
								@Override
								public void onAccept() {
									agreement.deletePeriod(selectedDate);
									deleteDialog.hide(true);
									resetSelectedDate();
									showLoading("Guardando convenio " + toolbar.getTitle() + " ...");
									setHasChange(false);
									onSaved();
								}
							});
						});
						
						selectedDatePanel.add(deleteDateBtn);
					} else
						dateLabel.addStyleName(style.p5());
					
					datesTabs.add(selectedDatePanel);
				} else {
					Label dateLabel = new Label(formatDate.format(date));
					dateLabel.addStyleName(style.unSelectDate());
					dateLabel.addClickHandler(e -> {
						selectedDate = formatDate.parse(dateLabel.getText());
						createSalaryTable();
					});
					datesTabs.add(dateLabel);
				}
			});
			
			levelSalaryTabs.add(datesTabs);
			
			if(!readOnly) {
					
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
						if (agreement.getSortedDates().isEmpty()) {
							agreement.createNewPeriod(newPeriod);
							agreement.setFilteredAllVariables();
							setAgreementPreview(agreement);
							setHasChange(true);
						} else {
							Date maxDate = agreement.getSortedDates().stream().findFirst().get();
							if (maxDate.after(newPeriod) || maxDate.equals(newPeriod))
								showWarning("Error fechas",
										"No se puede seleccionar un fecha anterior o igual al ultimo tramo existente");
							else {
								agreement.createNewPeriod(newPeriod);
								selectedDate = newPeriod;
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
				
				levelSalaryTabs.add(newDateBtn);
			}
			
			variablesVisivility = new AonToolbarSmallButton("Mostrar/Ocultar variables", AON.CSS.aonIconVisibility());
			variablesVisivility.addClickHandler(click -> {
				AgreementVariablesDialog dialog = new AgreementVariablesDialog(agreement.getAllVariables(),
						agreement.getVariablesByDate(selectedDate)) {

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

			levelSalaryTabs.add(variablesVisivility);
		} else {
			if(!readOnly) {
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
							if (AonStringUtils.isBlank(levelDescription.getValue())
									|| agreement.existLevel(levelDescription.getValue())) {
								showWarning("Nivel existente",
										"La descripci\u00f3n no puede ser vacia o coincidir con la de otro nivel ya existente");
							} else {
								agreement.createLevel(levelDescription.getValue());
								setAgreementPreview(agreement);
								setHasChange(true);
							}
						}
					});
				});
				levelSalaryTabs.add(newLevelBtn);
			}
		}
		
	}

	private void getSalaryTableHeader() {
		salaryGrid.clear();
		salaryGrid.resize(0, agreement.getVariablesByDate(selectedDate).size() + 3);
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

		for (String variable : agreement.getVariablesByDate(selectedDate)) {
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
		for (Level level : agreement.getLevels()) {
			if (level.getId() != 0 && ((level.isDeleted() || (null != agreement.getSelectedLevel()
					&& !level.getId().equals(agreement.getSelectedLevel().getId())))))
				continue;

			int row = salaryGrid.insertRow(salaryGrid.getRowCount());

			Widget categoryCell;

			if (level.getId() == 0) {
				categoryLB = createCategoryLB();
				categoryLB.ensureDebugId("category_filter");
				categoryLB.getElement().getStyle().setHeight(1.7, Unit.EM);
				categoryLB.getElement().getStyle().setWidth(290, Unit.PX);
				categoryLB.getElement().getStyle().setBorderStyle(BorderStyle.NONE);

				setSelectedValueLB(categoryLB, null == agreement.getSelectedLevel() ? ""
						: String.valueOf(agreement.getSelectedLevel().getId()));
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
			if (row % 2 == 0)
				salaryGrid.getCellFormatter().addStyleName(row, 0, style.oddRow());

			int col = 1;

			for (String variable : agreement.getVariablesByDate(selectedDate)) {
				LevelData levelData = agreement.getLevelData(level.getId(), variable, selectedDate);
				
				String value;
				
				if(null == levelData)
					value = null;
				else {
					String expression = levelData.getExpression();
					value = levelData.getExpression();

					try {
						Double expressionValue = evalExpression(expression);
						value = null == expressionValue ? "" : format(expressionValue);
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
				
				Widget cell;
				if(readOnly) {
					cell = new Label();
					((Label)cell).setText(value);
				} else {
					cell = new ExpressionBox();
					((ExpressionBox)cell).setValue(value);
					((ExpressionBox)cell).addValueChangeHandler(event -> {

						String expression = event.getValue();
						String result = event.getValue();
						
						try {
							Double expressionValue = evalExpression(expression);
							result = null == expressionValue ? "" : expressionValue.toString();
						} catch (Exception e) {
							// TODO: handle exception
						}

						if (null == levelData || null == levelData.getId())
							agreement.createLevelData(level.getId(), variable, result, selectedDate);
						else
							agreement.updateLevelData(level.getId(), levelData.getId(), result);

						setAgreementPreview(agreement);
						setHasChange(true);
					});
				}
				
				cell.ensureDebugId("textBox_" + variable + "_" + level.getDescription());
				cell.setTitle("Valor nivel retributivo");

				cell.addStyleName(style.gridCell());
				cell.addStyleName(style.valueCell());
				cell.addStyleName(style.textBoxSalary());
				
				if (row % 2 == 0)
					cell.addStyleName(style.oddRow());
				cell.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
				if (null != levelData && AonStringUtils.isNotBlank(levelData.getExpression())
						&& SpecialExpresion.parse(levelData.getExpression()).getInput().length() > 16)
					cell.setWidth((7.5 * levelData.getExpression().length()) + "px");
				else
					cell.setWidth("95%");

				if (levelData != null && levelData.isModify())
					cell.addStyleName(style.modify());
				else
					cell.removeStyleName(style.modify());
				

				// check if level 0 or default value
				if (level.getId() == 0 || null == levelData || AonStringUtils.isBlank(levelData.getExpression())) {
					LevelData levelDataDefault = agreement.getDefaultLevelData(variable, selectedDate);
					String valueData = null == levelDataDefault ? null
							: SpecialExpresion.parse(levelDataDefault.getExpression()).getInput();
					
					cell.addStyleName(style.levelDefaultValue());
					cell.setTitle("Valor por defecto");

					if (null != levelDataDefault && AonStringUtils.isNotBlank(levelDataDefault.getExpression())
							&& valueData.length() > 20)
						cell.setWidth((7.5 * levelDataDefault.getExpression().length()) + "px");
					else
						cell.setWidth("95%");
					
					if(readOnly) ((Label)cell).setText(valueData);
					else ((ExpressionBox)cell).setValue(valueData);
				}

				salaryGrid.setWidget(row, col, cell);
				if (row % 2 == 0)
					salaryGrid.getCellFormatter().addStyleName(row, col, style.oddRow());
				col++;
			}

			Label emptyCell = new Label("");
			salaryGrid.setWidget(row, col, emptyCell);
			if (row % 2 == 0)
				salaryGrid.getCellFormatter().addStyleName(row, col, style.oddRow());

			col++;

			Widget deleteCell = new Label();
			if (!readOnly) {
				deleteCell = new AonToolbarSmallButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete());
				((AonToolbarSmallButton) deleteCell).addClickHandler(event -> {
					AonDialog deleteDialog = new AonDialog("Borrar nivel", new HTMLPanel(
							"\u00bfDesea realmente eliminar el nivel <b>" + level.getDescription() + "</b>\u003f"));
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
			if (row % 2 == 0)
				salaryGrid.getCellFormatter().addStyleName(row, col, style.oddRow());
		}

	}

	public static String format(Double amount) {
		return AonNumberUtils.isNotValid(amount) ? AON.CURRENCY_FORMAT.format(AON.round(0.00)) : AON.CURRENCY_FORMAT.format(AON.round(amount));
	}
	
	public double evalExpression(String expression) {
		return calculate(expression);
	}

	public final native double calculate(String expression) /*-{
		return eval(expression);
	}-*/;

	private ListBox createCategoryLB() {
		Map<String, Integer> allCategories = new TreeMap<>();

		for (Level levelIT : agreement.getLevels()) {
			if (levelIT.getId() == 0)
				continue;
			Set<String> levelCategories = agreement.getCategoriesMap().get(levelIT.getId());
			Set<String> levelContracts = agreement.getContractsMap().get(levelIT.getId());
			if (null != levelContracts && !levelContracts.isEmpty())
				levelContracts.forEach(levelContract -> allCategories
						.put(levelIT.getDescription() + " - " + levelContract, levelIT.getId()));
			else if (levelCategories.size() > 1)
				levelCategories.forEach(category -> allCategories.put(category, levelIT.getId()));
			else if (levelCategories.size() == 1) {
				String category = (String) levelCategories.toArray()[0];
				RegExp regExp = RegExp.compile("Categoria|Nivel|categoria|nivel|CATEGORIA|NIVEL?");
				MatchResult matcher = regExp.exec(category);
				boolean matchFound = matcher != null;
				if (matchFound)
					allCategories.put(levelIT.getDescription(), levelIT.getId());
				else
					allCategories.put(category, levelIT.getId());
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
		if (null != levelContracts && !levelContracts.isEmpty()) {
			for (String levelContract : levelContracts) {
				if (AonStringUtils.isBlank(categoriesBuilder.toString()))
					categoriesBuilder.append(levelContract);
				else
					categoriesBuilder.append(", " + levelContract);
			}
			return level.getDescription() + " - " + categoriesBuilder.toString();
		} else if (agreement.getCategoriesMap().get(level.getId()).size() > 1) {
			for (String category : agreement.getCategoriesMap().get(level.getId())) {
				if (AonStringUtils.isBlank(categoriesBuilder.toString()))
					categoriesBuilder.append(category);
				else
					categoriesBuilder.append(", " + category);
			}
			return categoriesBuilder.toString();
		} else if (agreement.getCategoriesMap().get(level.getId()).size() == 1) {
			String category = (String) agreement.getCategoriesMap().get(level.getId()).toArray()[0];
			RegExp regExp = RegExp.compile("Categoria|Nivel|categoria|nivel|CATEGORIA|NIVEL?");
			MatchResult matcher = regExp.exec(category);
			boolean matchFound = matcher != null;
			if (matchFound)
				return level.getDescription();
			else
				return category;
		} else
			return null;
	}

	private String getLevelCategoriesTitle(Level level) {
		StringBuilder categoriesBuilder = new StringBuilder();
		Set<String> levelContracts = agreement.getContractsMap().get(level.getId());
		if (null != levelContracts && !levelContracts.isEmpty()) {

			StringBuilder contractsBuilder = new StringBuilder();
			for (String levelContract : levelContracts) {
				if (AonStringUtils.isBlank(contractsBuilder.toString()))
					contractsBuilder.append(levelContract);
				else
					contractsBuilder.append(", " + levelContract);
			}

			for (String category : agreement.getCategoriesMap().get(level.getId())) {
				if (AonStringUtils.isBlank(categoriesBuilder.toString()))
					categoriesBuilder.append(category);
				else
					categoriesBuilder.append(", " + category);
			}

			return "Nivel : " + level.getDescription() + "\nContratos : " + contractsBuilder.toString()
					+ "\nCategorias : " + categoriesBuilder.toString();

		}
		if (agreement.getCategoriesMap().get(level.getId()).size() > 1) {
			for (String category : agreement.getCategoriesMap().get(level.getId())) {
				if (AonStringUtils.isBlank(categoriesBuilder.toString()))
					categoriesBuilder.append(category);
				else
					categoriesBuilder.append(", " + category);
			}
			return "Nivel : " + level.getDescription() + "\nCategorias : " + categoriesBuilder.toString();
		} else if (agreement.getCategoriesMap().get(level.getId()).size() == 1) {
			String category = (String) agreement.getCategoriesMap().get(level.getId()).toArray()[0];
			RegExp regExp = RegExp.compile("Categoria|Nivel|categoria|nivel|CATEGORIA|NIVEL?");
			MatchResult matcher = regExp.exec(category);
			boolean matchFound = matcher != null;
			if (matchFound)
				return "Nivel : " + level.getDescription();
			else
				return "Nivel : " + level.getDescription() + "\nCategoria : " + category;
		} else
			return null;
	}

	private void filterSelectedCategory() {
		String levelId = categoryLB.getSelectedValue();
		agreement.setSelectedLevel(AonStringUtils.isBlank(levelId) ? null
				: agreement.getLevelById(Integer.parseInt(categoryLB.getSelectedValue())));
		setAgreementPreview(agreement);
	}

	private void salaryTableWidth() {
		salaryGrid.getColumnFormatter().setWidth(0, "120px");
	}

	// ------------------------------------------ categoryTable

	private void createCategoryTable() {
		createLevelSalaryTabs();
		getCategoryTableHeader();
		fillCategoryTable();
		categoryTableWidth();
		setTableHeight();
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
		for (Entry<Integer, Set<String>> e : agreement.getCategoriesMap().entrySet()) {

			Level level = agreement.getLevelById(e.getKey());
			if (level.isDeleted() || level.getId() == 0)
				continue;

			int row = levelGrid.insertRow(levelGrid.getRowCount());

			Set<String> categories = e.getValue();
			StringBuilder categoriesBuilder = new StringBuilder();
			for (String category : categories) {
				if (AonStringUtils.isBlank(categoriesBuilder.toString()))
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
			if (row % 2 == 0)
				levelCell.addStyleName(style.oddRow());

			if (level != null && level.isModify())
				levelCell.addStyleName(style.modify());
			else
				levelCell.removeStyleName(style.modify());

			levelCell.addValueChangeHandler(ev -> {
				if (AonStringUtils.isBlank(ev.getValue()) || agreement.existLevel(ev.getValue())) {
					showWarning("Nivel existente",
							"La descripci\u00f3n no puede ser vacia o coincidir con la de otro nivel ya existente");
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
			if (row % 2 == 0)
				categoryCell.addStyleName(style.oddRow());

			if (level != null && level.isCatModify())
				categoryCell.addStyleName(style.modify());
			else
				categoryCell.removeStyleName(style.modify());

			categoryCell.addValueChangeHandler(categoryValue -> {
				if (AonStringUtils.isNotBlank(categoryValue.getValue())) {
					agreement.getCategoriesMap().remove(level.getId());
					String[] categorySplit = AonStringUtils.split(categoryValue.getValue(), ',');
					for (int i = 0; i < categorySplit.length; i++)
						agreement.addCategory(level.getId(), categorySplit[i].trim());

					level.setCatModify(true);
					setAgreementPreview(agreement);
					setHasChange(true);
				}

			});

			Widget deleteCell = new Label();

			if (!readOnly) {
				deleteCell = new AonToolbarSmallButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete());
				((AonToolbarSmallButton) deleteCell).addClickHandler(event -> {
					AonDialog deleteDialog = new AonDialog("Borrar nivel", new HTMLPanel(
							"\u00bfDesea realmente eliminar el nivel <b>" + level.getDescription() + "</b>\u003f"));
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

			if (row % 2 == 0)
				levelGrid.getCellFormatter().addStyleName(row, 0, style.oddRow());
			if (row % 2 == 0)
				levelGrid.getCellFormatter().addStyleName(row, 1, style.oddRow());
			if (row % 2 == 0)
				levelGrid.getCellFormatter().addStyleName(row, 2, style.oddRow());
		}
	}

	private void categoryTableWidth() {
//		levelGrid.setWidth("100%");
		levelGrid.getColumnFormatter().setWidth(0, "120px");
		levelGrid.getColumnFormatter().setWidth(2, "15px");
	}

	// ------------------------------------------ paymentTable

	private void createPaymentTable() {
		getPaymentTableHeader();
		fillPaymentTable();
		paymentTableWidth();
	}

	private void getPaymentTableHeader() {
		paymentGrid.clear();
		paymentGrid.resize(0, 8);
		int row = paymentGrid.insertRow(paymentGrid.getRowCount());

		Label editColumn = new Label();
		paymentGrid.setWidget(row, 0, editColumn);
		paymentGrid.getCellFormatter().addStyleName(row, 0, style.headerFixed());
		paymentGrid.getCellFormatter().addStyleName(row, 0, style.zIndex1());

		Label craColumn = new Label("CRA");
		craColumn.addStyleName(style.gridTitle());
		craColumn.addStyleName(style.headerFSize());
		paymentGrid.setWidget(row, 1, craColumn);
		paymentGrid.getCellFormatter().addStyleName(row, 1, style.headerFixed());

		Label conceptColumn = new Label("Concept");
		conceptColumn.addStyleName(style.gridTitle());
		conceptColumn.addStyleName(style.headerFSize());
		paymentGrid.setWidget(row, 2, conceptColumn);
		paymentGrid.getCellFormatter().addStyleName(row, 2, style.headerFixed());

		Label descriptionColumn = new Label("Descripci\u00f3n");
		descriptionColumn.addStyleName(style.gridTitle());
		descriptionColumn.addStyleName(style.headerFSize());
		paymentGrid.setWidget(row, 3, descriptionColumn);
		paymentGrid.getCellFormatter().addStyleName(row, 3, style.headerFixed());

		Label expressionColumn = new Label("Expresi\u00f3n");
		expressionColumn.addStyleName(style.gridTitle());
		expressionColumn.addStyleName(style.headerFSize());
		paymentGrid.setWidget(row, 4, expressionColumn);
		paymentGrid.getCellFormatter().addStyleName(row, 4, style.headerFixed());

		Label infoColumn = new Label();
		paymentGrid.setWidget(row, 5, infoColumn);
		paymentGrid.getCellFormatter().addStyleName(row, 5, style.headerFixed());

		Label visibilityColumn = new Label("Estado");
		visibilityColumn.addStyleName(style.gridTitle());
		visibilityColumn.addStyleName(style.headerFSize());
		paymentGrid.setWidget(row, 6, visibilityColumn);
		paymentGrid.getCellFormatter().addStyleName(row, 6, style.headerFixed());

		Label deleteColumn = new Label();
		paymentGrid.setWidget(row, 7, deleteColumn);
		paymentGrid.getCellFormatter().addStyleName(row, 7, style.headerFixed());
		paymentGrid.getCellFormatter().addStyleName(row, 7, style.zIndex1());

		paymentGrid.getRowFormatter().addStyleName(row, style.headerColor());
	}

	private void fillPaymentTable() {
		Set<Payment> payments = showOldPayments ? agreement.getOldPaymentsAndHides() : agreement.getPaymentsAndHides();

		for (Payment payment : payments) {

			int row = paymentGrid.insertRow(paymentGrid.getRowCount());

			Widget editCell = new Label();
			editCell.ensureDebugId("edit_payment_" + row);
			if (!readOnly) {
				editCell = new AonToolbarSmallButton("Editar devengo",
						payment.isModify() ? AON.CSS.aonIconArrowRightModify() : AON.CSS.aonIconRight());
				((AonToolbarSmallButton) editCell).addClickHandler(e -> openDialog(payment));
			}
			checkRowAndModify(paymentGrid, row, payment, editCell);

			Widget craCell = new Label(null == payment.getType() ? "Revisar CRA"
					: AonStringUtils.leftPad(payment.getType().getCode() + "", 4, '0'));
			checkRowAndModify(paymentGrid, row, payment, craCell);

			Widget conceptCell = new Label(AonStringUtils.isBlank(payment.getName()) ? "" : payment.getName());
			conceptCell.setTitle(AonStringUtils.isBlank(payment.getName()) ? "" : payment.getName());
			conceptCell.addStyleName(style.elipsis());
			conceptCell.addStyleName(style.elipsisConceptWidth());
			checkRowAndModify(paymentGrid, row, payment, conceptCell);

			Widget descriptionCell = new Label(
					AonStringUtils.isBlank(payment.getDescription()) ? "" : payment.getDescription());
			descriptionCell.setTitle(AonStringUtils.isBlank(payment.getDescription()) ? "" : payment.getDescription());
			descriptionCell.addStyleName(style.elipsis());
			descriptionCell.addStyleName(
					isOpenCollapse ? style.elipsisOpenCollapseWidth() : style.elipsisCloseCollapseWidth());
			checkRowAndModify(paymentGrid, row, payment, descriptionCell);

			Widget expressionCell = new Label(AonStringUtils.isBlank(payment.getExpression()) ? ""
					: getParsedExpression(payment.getExpression()));
			expressionCell.setTitle(AonStringUtils.isBlank(payment.getExpression()) ? ""
					: getParsedExpression(payment.getExpression()));
			expressionCell.addStyleName(style.elipsis());
			expressionCell.addStyleName(
					isOpenCollapse ? style.elipsisOpenCollapseWidth() : style.elipsisCloseCollapseWidth());
			checkRowAndModify(paymentGrid, row, payment, expressionCell);

			Widget infoCell = new Label();
			String infoTitle = getInfoTitle(payment);
			if (AonStringUtils.isNotBlank(infoTitle))
				infoCell = new AonToolbarSmallButton(infoTitle, AON.CSS.aonIconInfo());
			checkRowAndModify(paymentGrid, row, payment, infoCell);

			Widget visibilityCell = new Button();
			getEnableDisableButton((Button) visibilityCell, !isHideExpression(payment), readOnly);
			visibilityCell.setTitle(isHideExpression(payment) ? "Inactivo" : "Activo");
			if (!readOnly) {
				((Button) visibilityCell).addClickHandler(e -> {
					showHidePayment(payment);
					payment.setModify(true);
					setAgreementPreview(agreement);
					setHasChange(true);
				});
			}
			checkRowAndModify(paymentGrid, row, payment, visibilityCell);

			Widget deleteCell = new Label();
			if (!readOnly) {
				deleteCell = new AonToolbarSmallButton("Eliminar devengo", AON.CSS.aonIconDelete());
				((AonToolbarSmallButton) deleteCell).addClickHandler(e -> {
					AonDialog deleteDialog = new AonDialog("Eliminar concepto",
							new HTML("\u00BFDesea eliminar el concepto seleccionado\u003F"));
					deleteDialog.setGlassStyleName(style.dialogGlass());
					deleteDialog.addStyleName(style.dialogZIndex());
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
				});
			}
			deleteCell.ensureDebugId("deletePaymentTabButton-" + row);
			checkRowAndModify(paymentGrid, row, payment, deleteCell);

			if (payment.isModify())
				paymentGrid.getRowFormatter().addStyleName(row, style.modify());
			else
				paymentGrid.getRowFormatter().removeStyleName(row, style.modify());

			paymentGrid.setWidget(row, 0, editCell);
			paymentGrid.setWidget(row, 1, craCell);
			paymentGrid.setWidget(row, 2, conceptCell);
			paymentGrid.setWidget(row, 3, descriptionCell);
			paymentGrid.setWidget(row, 4, expressionCell);
			paymentGrid.setWidget(row, 5, infoCell);
			paymentGrid.setWidget(row, 6, visibilityCell);
			paymentGrid.setWidget(row, 7, deleteCell);
			
			paymentGrid.getWidget(row, 6).getElement().getStyle().setTextAlign(TextAlign.CENTER);

			if (row % 2 == 0)
				paymentGrid.getCellFormatter().addStyleName(row, 0, style.oddRow());
			if (row % 2 == 0)
				paymentGrid.getCellFormatter().addStyleName(row, 1, style.oddRow());
			if (row % 2 == 0)
				paymentGrid.getCellFormatter().addStyleName(row, 2, style.oddRow());
			if (row % 2 == 0)
				paymentGrid.getCellFormatter().addStyleName(row, 3, style.oddRow());
			if (row % 2 == 0)
				paymentGrid.getCellFormatter().addStyleName(row, 4, style.oddRow());
			if (row % 2 == 0)
				paymentGrid.getCellFormatter().addStyleName(row, 5, style.oddRow());
			if (row % 2 == 0)
				paymentGrid.getCellFormatter().addStyleName(row, 6, style.oddRow());
			if (row % 2 == 0)
				paymentGrid.getCellFormatter().addStyleName(row, 7, style.oddRow());
		}

	}

	private void paymentTableWidth() {
//		paymentGrid.setWidth("100%");
		paymentGrid.getColumnFormatter().setWidth(0, "50px");
		paymentGrid.getColumnFormatter().setWidth(1, "80px");
		paymentGrid.getColumnFormatter().setWidth(2, "200px");
		paymentGrid.getColumnFormatter().setWidth(3, "auto");
		paymentGrid.getColumnFormatter().setWidth(4, "auto");
		paymentGrid.getColumnFormatter().setWidth(5, "60px");
		paymentGrid.getColumnFormatter().setWidth(6, "60px");
		paymentGrid.getColumnFormatter().setWidth(7, "50px");
	}

	// ------------------------------------------ paymentTable

	private void createExtraTable() {
		getExtraTableHeader();
		fillExtraTable();
		extraTableWidth();
	}

	private void getExtraTableHeader() {
		extraGrid.clear();
		extraGrid.resize(0, 9);
		int row = extraGrid.insertRow(extraGrid.getRowCount());

		Label editColumn = new Label();
		extraGrid.setWidget(row, 0, editColumn);
		extraGrid.getCellFormatter().addStyleName(row, 0, style.headerFixed());
		extraGrid.getCellFormatter().addStyleName(row, 0, style.zIndex1());

		Label payDateColumn = new Label("F.Cobro");
		payDateColumn.addStyleName(style.gridTitle());
		payDateColumn.addStyleName(style.headerFSize());
		extraGrid.setWidget(row, 1, payDateColumn);
		extraGrid.getCellFormatter().addStyleName(row, 1, style.headerFixed());

		Label conceptColumn = new Label("Concept");
		conceptColumn.addStyleName(style.gridTitle());
		conceptColumn.addStyleName(style.headerFSize());
		extraGrid.setWidget(row, 2, conceptColumn);
		extraGrid.getCellFormatter().addStyleName(row, 2, style.headerFixed());

		Label descriptionColumn = new Label("Descripci\u00f3n");
		descriptionColumn.addStyleName(style.gridTitle());
		descriptionColumn.addStyleName(style.headerFSize());
		extraGrid.setWidget(row, 3, descriptionColumn);
		extraGrid.getCellFormatter().addStyleName(row, 3, style.headerFixed());

		Label expressionColumn = new Label("Expresi\u00f3n");
		expressionColumn.addStyleName(style.gridTitle());
		expressionColumn.addStyleName(style.headerFSize());
		extraGrid.setWidget(row, 4, expressionColumn);
		extraGrid.getCellFormatter().addStyleName(row, 4, style.headerFixed());

		Label periodicityColumn = new Label();
		extraGrid.setWidget(row, 0, periodicityColumn);
		extraGrid.getCellFormatter().addStyleName(row, 5, style.headerFixed());

		Label infoColumn = new Label();
		extraGrid.setWidget(row, 6, infoColumn);
		extraGrid.getCellFormatter().addStyleName(row, 6, style.headerFixed());

		Label visibilityColumn = new Label("Estado");
		visibilityColumn.addStyleName(style.gridTitle());
		visibilityColumn.addStyleName(style.headerFSize());
		extraGrid.setWidget(row, 7, visibilityColumn);
		extraGrid.getCellFormatter().addStyleName(row, 7, style.headerFixed());

		Label deleteColumn = new Label();
		extraGrid.setWidget(row, 8, deleteColumn);
		extraGrid.getCellFormatter().addStyleName(row, 8, style.headerFixed());
		extraGrid.getCellFormatter().addStyleName(row, 8, style.zIndex1());

		extraGrid.getRowFormatter().addStyleName(row, style.headerColor());
	}

	private void fillExtraTable() {
		Set<Payment> extras = showOldPayments ? agreement.getOldPaymentsExtraAndHides()
				: agreement.getPaymentsExtraAndHides();

		for (Payment payment : extras) {

			int row = extraGrid.insertRow(extraGrid.getRowCount());

			AgreementExtra extra = agreement.getExtraPayment(payment.getId());

			Widget editCell = new Label();
			editCell.ensureDebugId("edit_payment_" + row);
			if (!readOnly) {
				editCell = new AonToolbarSmallButton("Editar devengo",
						payment.isModify() ? AON.CSS.aonIconArrowRightModify() : AON.CSS.aonIconRight());
				((AonToolbarSmallButton) editCell).addClickHandler(e -> openDialog(payment));
			}
			checkRowAndModify(extraGrid, row, payment, editCell);

			Widget payDateCell;
			if (null != extra && !extra.isDeleted())
				payDateCell = new Label(
						extra.getIssueDate() + (readOnly ? " (" + getPayDescription(payment) + ")" : ""));
			else {
				if (readOnly)
					payDateCell = new Label("Prorrat.");
				else {
					payDateCell = new TextBox();
					payDateCell.setWidth("50px");
					payDateCell.getElement().setPropertyString("placeholder", "dd/mm");
					((TextBox) payDateCell).addValueChangeHandler(e -> checkExtra(payment, e.getValue()));
				}
			}
			checkRowAndModify(extraGrid, row, payment, payDateCell);

			Widget conceptCell = new Label(AonStringUtils.isBlank(payment.getName()) ? "" : payment.getName());
			conceptCell.setTitle(AonStringUtils.isBlank(payment.getName()) ? "" : payment.getName());
			conceptCell.addStyleName(style.elipsis());
			conceptCell.addStyleName(style.elipsisConceptWidth());
			checkRowAndModify(extraGrid, row, payment, conceptCell);

			Widget descriptionCell = new Label(
					AonStringUtils.isBlank(payment.getDescription()) ? "" : payment.getDescription());
			descriptionCell.setTitle(AonStringUtils.isBlank(payment.getDescription()) ? "" : payment.getDescription());
			descriptionCell.addStyleName(style.elipsis());
			descriptionCell.addStyleName(
					isOpenCollapse ? style.elipsisOpenCollapseExtraWidth() : style.elipsisCloseCollapseExtraWidth());
			checkRowAndModify(extraGrid, row, payment, descriptionCell);

			Widget expressionCell = new Label(AonStringUtils.isBlank(payment.getExpression()) ? ""
					: getParsedExpression(payment.getExpression()));
			expressionCell.setTitle(AonStringUtils.isBlank(payment.getExpression()) ? ""
					: getParsedExpression(payment.getExpression()));
			expressionCell.addStyleName(style.elipsis());
			expressionCell.addStyleName(
					isOpenCollapse ? style.elipsisOpenCollapseExtraWidth() : style.elipsisCloseCollapseExtraWidth());
			checkRowAndModify(extraGrid, row, payment, expressionCell);

			Widget periodicityCell = new Label();
			if (null != extra && !extra.isDeleted() && !readOnly) {
				String issueMonth = extra.getIssueDate().split("/")[1];
				String payDescription = getPayLongDescription(payment);

				ListBox periodicityLB = createPeriodicityLB(issueMonth);
				setSelectedValueLB(periodicityLB, payDescription);
				periodicityLB.addChangeHandler(e -> {
					checkPaymentExtra(payment, periodicityLB.getSelectedValue());
					setAgreementPreview(agreement);
					setHasChange(true);
				});

				periodicityCell = periodicityLB;
			}
			checkRowAndModify(extraGrid, row, payment, periodicityCell);

			Widget infoCell = new Label();
			String infoTitle = getExtraInfoTitle(payment);
			if (AonStringUtils.isNotBlank(infoTitle))
				infoCell = new AonToolbarSmallButton(infoTitle, AON.CSS.aonIconInfo());
			checkRowAndModify(extraGrid, row, payment, infoCell);

			Widget visibilityCell = new Button();
			getEnableDisableButton((Button) visibilityCell, !isHideExpression(payment), readOnly);
			visibilityCell.setTitle(isHideExpression(payment) ? "Inactivo" : "Activo");
			if (!readOnly) {
				((Button) visibilityCell).addClickHandler(e -> {
					showHidePayment(payment);
					payment.setModify(true);
					setAgreementPreview(agreement);
					setHasChange(true);
				});
			}
			checkRowAndModify(extraGrid, row, payment, visibilityCell);

			Widget deleteCell = new Label();
			if (!readOnly) {
				deleteCell = new AonToolbarSmallButton("Eliminar devengo", AON.CSS.aonIconDelete());
				((AonToolbarSmallButton) deleteCell).addClickHandler(e -> {
					AonDialog deleteDialog = new AonDialog("Eliminar concepto",
							new HTML("\u00BFDesea eliminar el concepto seleccionado\u003F"));
					deleteDialog.setGlassStyleName(style.dialogGlass());
					deleteDialog.addStyleName(style.dialogZIndex());
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
				});
			}
			deleteCell.ensureDebugId("deletePaymentTabButton-" + row);
			checkRowAndModify(extraGrid, row, payment, deleteCell);

			if (payment.isModify())
				extraGrid.getRowFormatter().addStyleName(row, style.modify());
			else
				extraGrid.getRowFormatter().removeStyleName(row, style.modify());

			extraGrid.setWidget(row, 0, editCell);
			extraGrid.setWidget(row, 1, payDateCell);
			extraGrid.setWidget(row, 2, conceptCell);
			extraGrid.setWidget(row, 3, descriptionCell);
			extraGrid.setWidget(row, 4, expressionCell);
			extraGrid.setWidget(row, 5, periodicityCell);
			extraGrid.setWidget(row, 6, infoCell);
			extraGrid.setWidget(row, 7, visibilityCell);
			extraGrid.setWidget(row, 8, deleteCell);

			if (row % 2 == 0)
				extraGrid.getCellFormatter().addStyleName(row, 0, style.oddRow());
			if (row % 2 == 0)
				extraGrid.getCellFormatter().addStyleName(row, 1, style.oddRow());
			if (row % 2 == 0)
				extraGrid.getCellFormatter().addStyleName(row, 2, style.oddRow());
			if (row % 2 == 0)
				extraGrid.getCellFormatter().addStyleName(row, 3, style.oddRow());
			if (row % 2 == 0)
				extraGrid.getCellFormatter().addStyleName(row, 4, style.oddRow());
			if (row % 2 == 0)
				extraGrid.getCellFormatter().addStyleName(row, 5, style.oddRow());
			if (row % 2 == 0)
				extraGrid.getCellFormatter().addStyleName(row, 6, style.oddRow());
			if (row % 2 == 0)
				extraGrid.getCellFormatter().addStyleName(row, 7, style.oddRow());
			if (row % 2 == 0)
				extraGrid.getCellFormatter().addStyleName(row, 8, style.oddRow());
		}

	}

	private ListBox createPeriodicityLB(String issueMonth) {
		ListBox lb = new ListBox();

		lb.addItem("Anual", "Anual");
		lb.addItem("Semestral", "Semestral");
		lb.addItem("Prorrat.", "Prorrat.");
		lb.addItem("Manual", "Manual");

		if (AonStringUtils.containsIgnoreCase(issueMonth, "7") || AonStringUtils.containsIgnoreCase(issueMonth, "6")
				|| AonStringUtils.containsIgnoreCase(issueMonth, "12"))
			lb.getElement().getElementsByTagName("option").getItem(3).setAttribute("disabled", "disabled");
		else if (AonStringUtils.containsIgnoreCase(issueMonth, "03")) {
			lb.getElement().getElementsByTagName("option").getItem(1).setAttribute("disabled", "disabled");
			lb.getElement().getElementsByTagName("option").getItem(3).setAttribute("disabled", "disabled");
		} else
			lb.getElement().getElementsByTagName("option").getItem(1).setAttribute("disabled", "disabled");

		return lb;
	}

	private void extraTableWidth() {
		extraGrid.getColumnFormatter().setWidth(0, "50px");
		extraGrid.getColumnFormatter().setWidth(1, "100px");
		extraGrid.getColumnFormatter().setWidth(2, "200px");
		extraGrid.getColumnFormatter().setWidth(3, "auto");
		extraGrid.getColumnFormatter().setWidth(4, "auto");
		extraGrid.getColumnFormatter().setWidth(5, "120px");
		extraGrid.getColumnFormatter().setWidth(6, "60px");
		extraGrid.getColumnFormatter().setWidth(7, "60px");
		extraGrid.getColumnFormatter().setWidth(8, "50px");
	}

	// ------------------------------------------ paymentTable

	private String getInfoTitle(Payment payment) {
		String title = "";

		String irpfExpression = payment.getIrpfExpression();
		if (!AonStringUtils.equalsIgnoreCase(irpfExpression, "_P")) {
			title += "Tributa : " + getTaxedDescription(irpfExpression) + "\n";
		}

		String quoteExpression = payment.getQuoteExpression();
		if (!AonStringUtils.equalsIgnoreCase(quoteExpression, "_P")) {
			title += "Cotiza : " + getQuoteDescription(quoteExpression) + "\n";
		}

		if (payment.getType().equals(Payment.Type.CRA_0005)) {
			title += "Pago : " + getPayDescription(payment) + "\n";
		}

		return title;
	}

	private String getTaxedDescription(String irpfExpression) {
		if (AonStringUtils.equalsIgnoreCase(irpfExpression, "_P"))
			return "Importe \u00cdntegro";
		if (AonStringUtils.equalsIgnoreCase(irpfExpression, "0.00"))
			return "Exento";
		if (AonStringUtils.containsIgnoreCase(irpfExpression, "BASE_CTA_ESP"))
			return "Ingreso a Cuenta";
		if (AonStringUtils.isNotBlank(irpfExpression))
			return "Personalizado";
		return "No definido";
	}

	private String getQuoteDescription(String quoteExpression) {
		if (AonStringUtils.equalsIgnoreCase(quoteExpression, "_P"))
			return "Importe \u00cdntegro";
		if (AonStringUtils.equalsIgnoreCase(quoteExpression, "0.00"))
			return "Exento";
		if (AonStringUtils.containsIgnoreCase(quoteExpression, "PRORRATEAR"))
			return "Prorrateado";
		if (AonStringUtils.isNotBlank(quoteExpression))
			return "Personalizado";
		return "No definido";
	}

	private String getPayDescription(Payment payment) {
		AgreementExtra extra = agreement.getExtraPayment(payment.getId());

		if (null == extra && !payment.getType().equals(Payment.Type.CRA_0004)
				&& !payment.getType().equals(Payment.Type.CRA_0005))
			return "";

		if (null == extra
				&& (payment.getType().equals(Payment.Type.CRA_0004) || payment.getType().equals(Payment.Type.CRA_0005)))
			return "Prorrat.";

		return (extra == null || extra.isDeleted()) ? "Prorrat." : getExtraPeriodTitle(extra);
	}

	private String getExtraPeriodTitle(AgreementExtra extra) {
		if (AonStringUtils.containsIgnoreCase(extra.getStartDate(), "-1"))
			return "A";

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

	private void checkExtra(Payment payment, String issueDate) {
		if (AonStringUtils.isBlank(issueDate))
			showWarning("Fecha cobro", "La fecha de cobro debe rellenarse y tener el formato dd/mm");
		else {
			AgreementExtra extra = agreement.getExtraPayment(payment.getId());
			if (null == extra) {
				Random rand = new Random();
				int newExtraId = rand.nextInt(1000) * -1;

				AgreementExtra newExtra = new AgreementExtra();
				newExtra.setId(newExtraId);
				newExtra.setAgreementPayment(payment.getId());

				newExtra.setIssueDate(issueDate);
				newExtra.setStartDate(getAnualStartDate(issueDate));
				newExtra.setEndDate(getAnualEndDate(issueDate));

				agreement.addExtra(newExtra);
				payment.setModify(true);

			} else {
				extra.setDeleted(false);

				extra.setIssueDate(issueDate);
				extra.setStartDate(getAnualStartDate(issueDate));
				extra.setEndDate(getAnualEndDate(issueDate));

				payment.setModify(true);
			}

			setAgreementPreview(agreement);
			setHasChange(true);
		}
	}

	private String getAnualStartDate(String issueDate) {
		if (AonStringUtils.containsIgnoreCase(issueDate, "03"))
			return "01/01 -1";
		else if (AonStringUtils.containsIgnoreCase(issueDate, "6") || AonStringUtils.containsIgnoreCase(issueDate, "7"))
			return "01/07 -1";
		else if (AonStringUtils.contains(issueDate, "12"))
			return "01/01";
		else {
			String month = issueDate.split("/")[1];
			return "01/" + month + " -1";
		}
	}

	private String getAnualEndDate(String issueDate) {
		if (AonStringUtils.containsIgnoreCase(issueDate, "03"))
			return "31/12 -1";
		else if (AonStringUtils.containsIgnoreCase(issueDate, "6") || AonStringUtils.containsIgnoreCase(issueDate, "7"))
			return "30/06";
		else if (AonStringUtils.contains(issueDate, "12"))
			return "31/12";
		else {
			String month = issueDate.split("/")[1];
			String newMonth = getNewEndMonth(month);
			return getEndDayMonth(newMonth) + "/" + newMonth + (AonStringUtils.equals(newMonth, "12") ? " -1" : "");
		}
	}

	private String getNewEndMonth(String monthStr) {
		Integer month = Integer.parseInt(monthStr);
		if (month == 1)
			month = 12;
		else
			month--;
		return AonStringUtils.leftPad(month.toString(), 2, "0");
	}

	private String getEndDayMonth(String newMonth) {
		switch (newMonth) {
		case "01":
			return "31";
		case "02":
			return "28";
		case "03":
			return "31";
		case "04":
			return "30";
		case "05":
			return "31";
		case "06":
			return "30";
		case "07":
			return "31";
		case "08":
			return "31";
		case "09":
			return "30";
		case "10":
			return "31";
		case "11":
			return "30";
		default:
			return "31";
		}
	}

	private void checkPaymentExtra(Payment payment, String periodicity) {
		AgreementExtra extra = agreement.getExtraPayment(payment.getId());

		if (null != extra) {
			if (AonStringUtils.equals(periodicity, "Prorrat."))
				extra.setDeleted(true);
			else if (AonStringUtils.equals(periodicity, "Anual"))
				checkAnualExtra(extra);
			else if (AonStringUtils.equals(periodicity, "Semestral"))
				checkSemestralExtra(extra);

			payment.setModify(true);
		}
	}

	private void checkAnualExtra(AgreementExtra extra) {
		extra.setDeleted(false);
		String issueDate = extra.getIssueDate();
		if (AonStringUtils.isNotBlank(issueDate)) {
			if (AonStringUtils.containsIgnoreCase(issueDate, "03")) {
				extra.setStartDate("01/01 -1");
				extra.setEndDate("31/12 -1");
			} else if (AonStringUtils.containsIgnoreCase(issueDate, "6")
					|| AonStringUtils.containsIgnoreCase(issueDate, "7")) {
				extra.setStartDate("01/07 -1");
				extra.setEndDate("30/06");
			} else if (AonStringUtils.containsIgnoreCase(issueDate, "12")) {
				extra.setStartDate("01/01");
				extra.setEndDate("31/12");
			}
		}
	}

	private void checkSemestralExtra(AgreementExtra extra) {
		extra.setDeleted(false);
		String issueDate = extra.getIssueDate();
		if (AonStringUtils.isNotBlank(issueDate)) {
			if (AonStringUtils.containsIgnoreCase(issueDate, "03")) {
				extra.setStartDate("01/07 -1");
				extra.setEndDate("31/12 -1");
			} else if (AonStringUtils.containsIgnoreCase(issueDate, "6")
					|| AonStringUtils.containsIgnoreCase(issueDate, "7")) {
				extra.setStartDate("01/01");
				extra.setEndDate("30/06");
			} else if (AonStringUtils.containsIgnoreCase(issueDate, "12")) {
				extra.setStartDate("01/07");
				extra.setEndDate("31/12");
			}
		}

	}

	private String getPayLongDescription(Payment payment) {
		AgreementExtra extra = agreement.getExtraPayment(payment.getId());

		if (null == extra
				&& (payment.getType().equals(Payment.Type.CRA_0004) || payment.getType().equals(Payment.Type.CRA_0005)))
			return "Prorrat.";

		return (extra == null || extra.isDeleted()) ? "Prorrat." : getExtraPeriodLongTitle(extra);
	}

	private String getExtraPeriodLongTitle(AgreementExtra extra) {
		if (AonStringUtils.containsIgnoreCase(extra.getIssueDate(), "03"))
			return "Anual";
		if (AonStringUtils.containsIgnoreCase(extra.getStartDate(), "-1")
				&& !AonStringUtils.containsIgnoreCase(extra.getEndDate(), "-1"))
			return "Anual";

		try {
			String startDate = extra.getStartDate();
			startDate = AonStringUtils.containsIgnoreCase(startDate, "-1") ? startDate.split(" ")[0] : startDate;
			String endDate = extra.getEndDate();
			endDate = AonStringUtils.containsIgnoreCase(endDate, "-1") ? endDate.split(" ")[0] : endDate;

			int startMonth = Integer.parseInt(startDate.split("/")[1]);
			int endMonth = Integer.parseInt(endDate.split("/")[1]);

			switch (endMonth - startMonth) {
			case 11:
				return "Anual";
			case 5:
				return "Semestral";
			default:
				return "Manual";
			}
		} catch (Exception e) {
			return "Manual";
		}
	}

	private String getExtraInfoTitle(Payment payment) {
		String title = "";

		String irpfExpression = payment.getIrpfExpression();
		if (!AonStringUtils.equalsIgnoreCase(irpfExpression, "_P")) {
			title += "Tributa : " + getTaxedDescription(irpfExpression) + "\n";
		}

		String quoteExpression = payment.getQuoteExpression();
		if (!AonStringUtils.equalsIgnoreCase(quoteExpression, "_P")) {
			title += "Cotiza : " + getQuoteDescription(quoteExpression) + "\n";
		}

		return title;
	}

	private String getParsedExpression(String expression) {
		return SpecialExpresion.parse(expression).getInput();
	}

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

	// ------------------------------------------ tables widht

	public void setTablesWidth() {
		Scheduler.get().scheduleDeferred(() -> {
			if(!readOnly) salaryScrollPanel.setWidth((Window.getClientWidth() - 400) + "px");
			levelScrollPanel.setWidth((Window.getClientWidth() - 400) + "px");
			levelGrid.setWidth((Window.getClientWidth() - 400) + "px");
		});
	}
	
	private void setTableHeight() {
		salaryScrollPanel.setHeight(salaryGrid.getRowCount() < 10 ? ((salaryGrid.getRowCount() * 26 + 10) + "px") : "260px");
		levelScrollPanel.setHeight(levelGrid.getRowCount() < 10 ? ((levelGrid.getRowCount() * 26 + 10) + "px") : "260px");
	}

	public void setTablesWidthCollapseMenu() {
		if(!readOnly) salaryScrollPanel.setWidth((Window.getClientWidth() - 50) + "px");
		levelScrollPanel.setWidth((Window.getClientWidth() - 50) + "px");
		levelGrid.setWidth((Window.getClientWidth() - 50) + "px");
	}

	// ------------------------------------------ deckLayoutPanel

	private void showAgreementPreview() {
		deckLayoutPanel.showWidget(0);
	}

	private void showAgreementSimulator() {
		deckLayoutPanel.showWidget(1);
	}

	private void showLevelTable() {
		levelSalaryToolbar.setTitle("Nivel / Categorias");
		if (!agreement.getLevels().isEmpty()) {
			createCategoryTable();
			levelSalaryDeck.showWidget(0);
		} else 
			showEmptyLevelMessage();
	}

	private void showSalaryTable() {
		levelSalaryToolbar.setTitle("Tabla Salarial");
		if (!agreement.getSortedDates().isEmpty()) {
			createSalaryTable();
			levelSalaryDeck.showWidget(1);
		} else {
			showEmptySalaryMessage();
		}
	}

	private void showEmptyLevelMessage() {
		levelSalaryDeck.showWidget(2);
	}

	private void showEmptySalaryMessage() {
		levelSalaryDeck.showWidget(3);
	}

	private void showPaymentTable() {
		Set<Payment> payments = showOldPayments ? agreement.getOldPaymentsAndHides() : agreement.getPaymentsAndHides();
		if (!payments.isEmpty()) {
			createPaymentTable();
			showPaymentButtons();
			paymentDeck.showWidget(0);
		} else
			showEmptyPaymentMessage();
	}
	
	private void showPaymentButtons() {		
		addPaymentButton.setVisible(!readOnly);
	}

	private void showEmptyPaymentMessage() {
		paymentDeck.showWidget(1);
	}

	private void showExtraTable() {
		Set<Payment> extras = showOldPayments ? agreement.getOldPaymentsExtraAndHides()
				: agreement.getPaymentsExtraAndHides();
		if (!extras.isEmpty()) {
			createExtraTable();
			extraDeck.showWidget(0);
		} else
			showEmptyExtraMessage();
	}

	private void showEmptyExtraMessage() {
		extraDeck.showWidget(1);
	}

	// ------------------------------------------ DisclosurePanel

	public void setIsOpenCollapse(boolean isCollapse) {
		this.isOpenCollapse = isCollapse;
		// For resize widget paint again
		showPaymentTable();
		showExtraTable();
	}

	// ------------------------------------------ toolbar

	private void createToolbar() {
		toolbar = new AonToolbar("Convenio");

		saveBtn = new AonToolbarButton(AON.MSG.saveAction(), AON.CSS.aonIconSave());
		saveBtn.ensureDebugId("acceptButton");
		saveBtn.addClickHandler(e -> {
			showLoading("Guardando convenio " + toolbar.getTitle() + " ...");
			setHasChange(false);
			onSaved();
		});

		toolbar.add(saveBtn);

		undoAllButton = new AonToolbarButton(AON.MSG.undo(), AON.CSS.aonIconUndoAll());
		undoAllButton.ensureDebugId("undoAllButton");
		undoAllButton.addClickHandler(e -> {
			AonDialog deleteDialog = new AonDialog("Restaurar convenio",
					new HTMLPanel("\u00bfDesea realmente deshacer los cambios sin guardar del convenio <b>"
							+ toolbar.getTitle() + "</b>\u003f"));
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
		
		addPaymentButton = new AonToolbarSmallButton("A\u00F1adir Devengo", AON.CSS.aonIconAdd());
		addPaymentButton.ensureDebugId("newPaymentButton");
		addPaymentButton.addClickHandler(e -> new AddAonPaymentCommand().execute());

		toolbar.add(addPaymentButton);

		showOlPaymentsButton = new AonToolbarSmallButton("Mostrar devengo antiguos", AON.CSS.aonIconVisibility());
		showOlPaymentsButton.ensureDebugId("showOlPaymentsButton");
		showOlPaymentsButton.addClickHandler(e -> {
			showOldPayments = !showOldPayments;

			if (showOldPayments) {
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

		agreementInfoButton = new AonToolbarButton("Informaci\u00f3n Convenio", AON.CSS.aonIconInfo());
		agreementInfoButton.ensureDebugId("infoButton");
		agreementInfoButton.addClickHandler(e -> impl.getAgreementUsedInfo(agreement.getId(),
				agreement.getDescription(), new AsyncCallback<String>() {

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
				}));

		toolbar.add(agreementInfoButton);

		serviAgreementUpdateButton = new AonToolbarButton("Actualizar Convenio", AON.CSS.aonIconCloudImport());
		serviAgreementUpdateButton.addClickHandler(e -> impl.getDomainUserRoles(new AsyncCallback<DomainUserRoles>() {

			@Override
			public void onSuccess(DomainUserRoles userRole) {
				if (userRole.isConvenios()) {

					impl.canUpdateServiAgreement(agreement, new AsyncCallback<Boolean>() {

						@Override
						public void onFailure(Throwable caught) {
							showError("Error actualizaci\u00F3n", caught.getMessage());
						}

						@Override
						public void onSuccess(Boolean canUpdate) {
							if (!canUpdate)
								showSuccess("Actualizaci\u00F3n",
										"El convenio se encuentra actualizado, no existen nuevos tramos");
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
												showSuccess("Actualizaci\u00F3n",
														"El convenio ha sido actualizado correctamente");
												reloadAgreement();
											}
										});
									}

								};

								dialog.setGlassStyleName(style.dialogGlass());
								dialog.addStyleName(style.dialogZIndex());
							}
						}
					});

				} else
					showError("Actualizaci\u00f3n no disponible",
							"Para poder actualizar un convenio a traves de ServiConvenios debe tener contrato el m\u00f3dulo.");
			}

			@Override
			public void onFailure(Throwable caught) {
				showError("Error", caught.getMessage());
			}
		}));
		toolbar.add(serviAgreementUpdateButton);

		printPreviewButton = new AonToolbarButton(AON.MSG.draftPrint(), AON.CSS.aonIconPdf());
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

	private void createLevelSalaryToolbar() {
		levelSalaryToolbar = new AonToolbarSmall("Tabla Salarial");

		levelSalaryDiscBtn = new AonToolbarSmallButton("Desplegar Nivel / Categoria", AON.CSS.aonIconDown());
		levelSalaryDiscBtn.addClickHandler(e -> {
			isLevelSalaryOpen = !isLevelSalaryOpen;
			handleIcon(levelSalaryDiscBtn, isLevelSalaryOpen);
			if(isLevelSalaryOpen) { 
				levelSalaryDeck.getElement().getStyle().clearDisplay();
				levelSalaryTabs.getElement().getStyle().clearDisplay();
			} else {
				levelSalaryDeck.getElement().getStyle().setDisplay(Display.NONE);
				levelSalaryTabs.getElement().getStyle().setDisplay(Display.NONE);
			}
		});
		levelSalaryToolbar.add(levelSalaryDiscBtn);
		
	}

	private void createPaymentToolbar() {
		paymentToolbar = new AonToolbarSmall("Devengos");
		
		paymentDiscBtn = new AonToolbarSmallButton("Desplegar Devengos", AON.CSS.aonIconDown());
		paymentDiscBtn.addClickHandler(e -> {
			isPaymentOpen = !isPaymentOpen;
			handleIcon(paymentDiscBtn, isPaymentOpen);
			if(isPaymentOpen) paymentDeck.getElement().getStyle().clearDisplay();
			else paymentDeck.getElement().getStyle().setDisplay(Display.NONE);
		});
		paymentToolbar.add(paymentDiscBtn);
		
	}

	private void createExtraToolbar() {
		extraToolbar = new AonToolbarSmall("Extras");

		extraDiscBtn = new AonToolbarSmallButton("Desplegar Extras", AON.CSS.aonIconDown());
		extraDiscBtn.addClickHandler(e -> {
			isExtraOpen = !isExtraOpen;
			handleIcon(extraDiscBtn, isExtraOpen);
			if(isExtraOpen) extraDeck.getElement().getStyle().clearDisplay();
			else extraDeck.getElement().getStyle().setDisplay(Display.NONE);
		});
		extraToolbar.add(extraDiscBtn);
	}

	private void handleIcon(AonToolbarSmallButton button, boolean open) {
		if (open) {
			button.removeStyleName(AON.CSS.aonIconLeft());
			button.addStyleName(AON.CSS.aonIconDown());

			if (button.equals(levelSalaryDiscBtn))
				button.setTitle(isSalaryTableSelected ? "Colapsar Tabla Salarial" : "Colapsar Nivel / Categoria");
			if (button.equals(paymentDiscBtn))
				button.setTitle("Colapsar Devengos");
			if (button.equals(extraDiscBtn))
				button.setTitle("Colapsar Extras");
		} else {
			button.removeStyleName(AON.CSS.aonIconDown());
			button.addStyleName(AON.CSS.aonIconLeft());

			if (button.equals(levelSalaryDiscBtn))
				button.setTitle(isSalaryTableSelected ? "Desplegar Tabla Salarial" : "Desplegar Nivel / Categoria");
			if (button.equals(paymentDiscBtn))
				button.setTitle("Desplegar Devengos");
			if (button.equals(extraDiscBtn))
				button.setTitle("Desplegar Extras");
		}
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
			String item = entry.getKey() + " - " + AonStringUtils
					.upperCase(AonStringUtils.abbreviate(entry.getValue().getContractTypeShortDescription(), 40));
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
				if (null != html)
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
		String text = partialTextBox.getText();
		try {
			return Double.parseDouble(text);
		} catch (Exception e) {
			return 1.0;
		}
	}

	// ------------------------------------------ setSelectedLevel

	public void setSelectedLevel(Integer levelId, String toolbarTitle) {
		blockElements();
		hideLevelDiscPanel();
		employeeView = true;
		setReadOnly(true);
		filterLevel(levelId);
		toolbar.setTitle(toolbarTitle);
		createAgreementGoToBtn();
		checkDeleteAgreement();
	}

	private void filterLevel(Integer levelId) {
		setSelectedValueLB(categoryLB, null == levelId ? "" : String.valueOf(levelId));
		filterSelectedCategory();
	}

	public void payrollPreview() {
		blockElements();
		hideLevelDiscPanel();
		workplaceView = true;
		setReadOnly(true);
		setAgreementPreview(agreement);
		createAgreementGoToBtn();
		checkDeleteAgreement();
	}

	private void blockElements() {
		description.setEnabled(false);
		ssNumber.setEnabled(false);
		description.getElement().getStyle().setBackgroundColor("transparent");
		ssNumber.getElement().getStyle().setBackgroundColor("transparent");
	}

	private void hideLevelDiscPanel() {
		isSalaryTableSelected = true;
		categoriesBtn.setVisible(false);
	}

	private void createAgreementGoToBtn() {
		FlowPanel toolbarButtons = toolbar.getButtonContainer();
		if (toolbarButtons.getWidgetCount() > 4)
			toolbarButtons.remove(toolbarButtons.getWidgetCount() - 1);

		AonToolbarButton goToAgreementBtn = new AonToolbarButton("Ir al convenio " + agreement.getDescription(),
				AON.CSS.aonIconOpenInNew());
		goToAgreementBtn.addClickHandler(e -> goToAgreement(agreement.getId()));

		// TODO: quitar esta linea cuando este implementado
		goToAgreementBtn.setVisible(false);

		toolbar.add(goToAgreementBtn);
	}

	private void goToAgreement(Integer agreementId) {
		// TODO: Aqui iria la navegacion a los convenios
		// MainAgreement mainAgreementTab = new MainAgreement();
		// mainAgreementTab.onModuleLoad();
		// mainAgreementTab.agreements.getAgreementsAndSelectImported(agreementId, s ->
		// {});
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
		if (hasChange()) {
			saveBtn.getElement().getStyle().clearDisplay();
			undoAllButton.getElement().getStyle().clearDisplay();
		}
		saveBtn.setEnabled(hasChange());
		undoAllButton.setEnabled(hasChange());
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

	private void checkDeleteAgreement() {
		if (agreement.getId() != null && agreement.getId() < 0)
			showError("Convenio eliminado",
					"Cuidado, el convenio que est\u00e1 visualizando se encuentra en la papelera.");
		else
			hideMessage();
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	
	public void resetSelectedDate() {
		this.selectedDate = null;
	}

}
