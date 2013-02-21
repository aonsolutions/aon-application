package com.esferalia.aon.gwt.payroll.client;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.payroll.client.SalaryDraftObject.CalculateCallback;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.DeductionComparator;
import com.esferalia.aon.gwt.payroll.shared.HasDeduction;
import com.esferalia.aon.gwt.payroll.shared.HasPayment;
import com.esferalia.aon.gwt.payroll.shared.Item;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.PaymentComparator;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Event;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.StringVariable;
import com.esferalia.aon.gwt.payroll.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.UndefinedDeductionVariable;
import com.esferalia.aon.gwt.payroll.shared.UndefinedPaymentVariable;
import com.esferalia.aon.gwt.payroll.shared.UndefinedVariable;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.LongBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;

public class SalaryDraft extends ResizeComposite implements CalculateCallback,
		SalarySelect.Listener {

	private static final int ZOOM_STEP = 20;

	private static final int MIN_ZOOM = 50;
	private static final int MAX_ZOOM = 200;
	private static final int DEFAULT_ZOOM = 135;

	private static final String GWT_HORIZONTAL_PANEL = "gwt-HorizontalPanel";

	private static final DateTimeFormat DATE_FORMAT = DateTimeFormat
			.getFormat(PredefinedFormat.YEAR_MONTH_NUM_DAY);

	private static final NumberFormat PERCENT_FORMAT = NumberFormat
			.getPercentFormat();

	private static final NumberFormat CURRENCY_FORMAT = NumberFormat
			.getFormat("#,##0.00");

	private static Map<Scope, String> SCOPE_DESCRIPTIONS = new HashMap<Scope, String>() {
		{
			put(Scope.AGREEMENT, "Convenio");
			put(Scope.APPLICATION, "Sistema");
			put(Scope.SYSTEM, "Sistema");
			put(Scope.CONTRACT, "Contrato");
			put(Scope.SALARY, "Borrador");
		}
	};

	private static Map<Deduction.Type, String> DEDUCTION_DESCRIPTIONS = new HashMap<Deduction.Type, String>() {
		{
			put(Deduction.Type.IRPF, "IRPF");
			put(Deduction.Type.COMMON_CONTINGENCY, "Contingencias Comunes");
			put(Deduction.Type.UNEMPLOYMENT, "Desempleo");
			put(Deduction.Type.JOB_TRAINING, "Formaci&oacute;n Profesional");
			put(Deduction.Type.STRUCTURAL_OVERTIME, "Horas Extras");
			put(Deduction.Type.NON_STRUCTURAL_OVERTIME,
					"Horas Extras no autorizadas");
		}
	};

	private Scope SCOPE_STEPS[] = { Scope.CONTRACT, Scope.AGREEMENT,
			Scope.SYSTEM };

	class VariableChangeHandler<T extends UIObject & HasValue<String> & HasAllFocusHandlers>
			implements ValueChangeHandler<String>, FocusHandler, BlurHandler {

		private T uiObject;
		private Variable variable;

		public VariableChangeHandler(Variable variable) {
			this.variable = variable;
		}

		public void setUiObject(T uiObject) {
			this.uiObject = uiObject;
			this.uiObject.addBlurHandler(this);
			this.uiObject.addFocusHandler(this);
			//this.uiObject.addValueChangeHandler(this);
			setValue(variable.getValue());
		}

		@Override
		public void onFocus(FocusEvent event) {
			setValue(variable.getExpression());
		}

		@Override
		public void onBlur(BlurEvent event) {
			if ( !isChanged() ) { 
				setValue(variable.getValue());
			}
			else {
				onValueChange(null);
			}
		}

		@Override
		public void onValueChange(ValueChangeEvent<String> event) {
			
			String value = uiObject.getValue();
			
			StringVariable var = new StringVariable();
			var.setImplicit(false);
			var.setScope(Scope.SALARY); // DRAFT
			var.setName(variable.getName());
			var.setEndDate(variable.getEndDate());
			var.setStartDate(variable.getStartDate());
			var.setExpression(StringUtils.isEmpty(value) ? "REMOVE_VARIABLE()" : value);
			
			salaryDraftObject.addDraftVariable(var);
			salaryDraftObject.calculate(SalaryDraft.this);
		}

		private void setValue(Object value) {
			uiObject.setValue(value == null ? null : value.toString());
		}
		private boolean isChanged() {
			return !StringUtils.equals(uiObject.getValue(), variable.getExpression());
		}

	}

	abstract class ItemChangeHandler<T extends UIObject & HasValue<String> & HasAllFocusHandlers, I extends Item> {

		I item;

		public ItemChangeHandler(I item) {
			this.item = item;
		}

		public void setDescriptionWidget(final T descriptionWidget) {
			descriptionWidget
					.addValueChangeHandler(new ValueChangeHandler<String>() {
						@Override
						public void onValueChange(ValueChangeEvent<String> event) {
							onDescriptionChange(item, event.getValue());
						}
					});
			descriptionWidget.addFocusHandler(new FocusHandler() {
				@Override
				public void onFocus(FocusEvent event) {
					// TODO:
				}
			});
			descriptionWidget.addBlurHandler(new BlurHandler() {
				@Override
				public void onBlur(BlurEvent event) {
					// TODO:
				}
			});
		}

		public void setExpressionWidget(final T expressionWidget) {
			expressionWidget
					.addValueChangeHandler(new ValueChangeHandler<String>() {
						@Override
						public void onValueChange(ValueChangeEvent<String> event) {
							onExpressionChange(item, event.getValue());
						}
					});
			expressionWidget.addFocusHandler(new FocusHandler() {
				@Override
				public void onFocus(FocusEvent event) {
					expressionWidget.setValue(item.getExpression());
				}
			});
			expressionWidget.addBlurHandler(new BlurHandler() {
				@Override
				public void onBlur(BlurEvent event) {
					expressionWidget.setValue(format(item.getAmount()));
				}
			});
		}

		public void setDeleteButton(HasClickHandlers deleteButton) {
			deleteButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					onExpressionChange(item, "REMOVE()");
				}
			});
		}


		abstract void onExpressionChange(I item, String expression);
		
		abstract void onDescriptionChange(I item, String description);
	}

	class PaymentChangeHandler<T extends UIObject & HasValue<String> & HasAllFocusHandlers>
			extends ItemChangeHandler<T, Payment> {

		public PaymentChangeHandler(Payment payment) {
			super(payment);
		}

		@Override
		void onDescriptionChange(Payment payment, String description) {

			payment.setScope(Scope.SALARY);
			payment.setDescription(description);
			salaryDraftObject.addDraftPayment(payment);

			salaryDraftObject.calculate(SalaryDraft.this);
		}

		@Override
		void onExpressionChange(Payment payment, String expression) {
			payment.setScope(Scope.SALARY);
			payment.setExpression(expression);
			salaryDraftObject.addDraftPayment(payment);
			salaryDraftObject.calculate(SalaryDraft.this);
		}

	}

	class DeductionChangeHandler<T extends UIObject & HasValue<String> & HasAllFocusHandlers>
			extends ItemChangeHandler<T, Deduction> {

		public DeductionChangeHandler(Deduction deduction) {
			super(deduction);
		}
		
		
		@Override
		void onDescriptionChange(Deduction item, String description) {
			item.setScope(Scope.SALARY);
			item.setDescription(description);
			salaryDraftObject.addDraftDeduction(item);
			salaryDraftObject.calculate(SalaryDraft.this);
		}
		
		@Override
		void onExpressionChange(Deduction item, String expression) {
			item.setScope(Scope.SALARY);
			item.setExpression(expression);
			salaryDraftObject.addDraftDeduction(item);
			salaryDraftObject.calculate(SalaryDraft.this);
		}
		
	}

	abstract class NewItemHandler<T extends Item> implements
			AsyncCallback<List<T>>, ValueChangeHandler<String> {

		SuggestBox descriptionBox;
		TextBox expressionBox;
		MultiWordSuggestOracle oracle;

		Map<String, T> paymentConceptsMap;

		public void setDescriptionBox(SuggestBox descriptionBox) {
			this.descriptionBox = descriptionBox;
			this.descriptionBox.addValueChangeHandler(this);
		}

		public void setOracle(MultiWordSuggestOracle oracle) {
			this.oracle = oracle;
			getAvailableItems();
		}

		public void setExpressionBox(TextBox expressionBox) {
			this.expressionBox = expressionBox;
			this.expressionBox.addValueChangeHandler(this);
		}

		@Override
		public void onSuccess(List<T> items) {
			this.paymentConceptsMap = new HashMap<String, T>();
			for (T item : items) {
				String suggestion = item.getDescription() + " ( "
						+ item.getName() + " )";
				oracle.add(suggestion);
				paymentConceptsMap.put(suggestion, item);
			}

		}

		@Override
		public void onFailure(Throwable caught) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onValueChange(ValueChangeEvent<String> event) {

			String suggestion = descriptionBox.getValue();
			T item = paymentConceptsMap.get(suggestion);

			String expression = null;
			if (item != null) {
				expression = item.getExpression();
			}

			if (StringUtils.isEmpty(expression)) {
				expression = expressionBox.getValue();
				if (StringUtils.isEmpty(expression)) {
					expressionBox.setVisible(true);
					expressionBox.setFocus(true);
					return;
				}
			}

			addDrafItem(item, expression);

			salaryDraftObject.calculate(SalaryDraft.this);
		}

		protected abstract void getAvailableItems();

		protected abstract void addDrafItem(T item, String expr);

	}

	class NewDeductionHandler extends NewItemHandler<Deduction> {

		@Override
		protected void getAvailableItems() {
			onSuccess(Collections.<Deduction> emptyList());
		}

		@Override
		protected void addDrafItem(Deduction item, String expr) {
			Deduction deduction = new Deduction();
			deduction.setExpression(expr);
			if (item != null) {
				deduction.setType(item.getType());
				deduction.setName(item.getName());
			} else {
				deduction.setType(Deduction.Type.OTHER);
			}
			deduction.setScope(Scope.SALARY);
			deduction.setSalaryType(salaryDraftObject.getType());
			deduction.setStartDate(salaryDraftObject.getStartDate());
			deduction.setStartDate(salaryDraftObject.getEndDate());

			deduction.setDescription(descriptionBox.getValue());

			salaryDraftObject.addDraftDeduction(deduction);
		}
	}

	class NewPaymentHandler extends NewItemHandler<Payment> {

		@Override
		protected void getAvailableItems() {
			salaryDraftObject.getPaymentConcepts(this);
		}

		@Override
		protected void addDrafItem(Payment payment, String expression) {
			Payment draftPayment = new Payment();

			draftPayment.setExpression(expression);

			draftPayment.setName(payment.getName());
			draftPayment.setType(payment.getType());
			draftPayment.setDescription(payment.getDescription());
			draftPayment.setIrpfExpression(payment.getIrpfExpression());
			draftPayment.setQuoteExpression(payment.getQuoteExpression());

			draftPayment.setScope(Scope.SALARY);
			draftPayment.setEndDate(salaryDraftObject.getEndDate());
			draftPayment.setStartDate(salaryDraftObject.getStartDate());
			draftPayment.setSalaryType(salaryDraftObject.getType());
			// draftPayment.setMonth(payment.getMonth());

			salaryDraftObject.addDraftPayment(draftPayment);

		}
	}

	class VariableRemoveHandler implements ClickHandler {

		private Variable variable;

		public VariableRemoveHandler(Variable variable) {
			this.variable = variable;
		}

		@Override
		public void onClick(ClickEvent event) {
			StringVariable var = new StringVariable();
			var.setImplicit(false);
			var.setScope(Scope.SALARY); // DRAFT
			var.setName(variable.getName());
			var.setEndDate(variable.getEndDate());
			var.setStartDate(variable.getStartDate());
			var.setExpression("REMOVE_VARIABLE()");
		
			salaryDraftObject.addDraftVariable(var);

			salaryDraftObject.calculate(SalaryDraft.this);
		}

	}

	interface MyStyle extends CssResource {
		@ClassName("cell-label")
		String cellLabel();

		@ClassName("cell-warn")
		String cellWarn();

		@ClassName("text-warn")
		String textWarn();

		@ClassName("text-error")
		String textError();

		String expandAllButton();

		String collapseAllButton();
	}

	interface Binder extends UiBinder<Widget, SalaryDraft> {

	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	DeckPanel deckPanel;
	@UiField
	Panel draftPanel;
	@UiField
	HTML printPreviewHTML;

	@UiField
	ListBox zoomListBox;
	@UiField
	SalarySelect salarySelect;
	@UiField
	FlexTable contextTable;
	@UiField
	FlexTable eventsTable;
	@UiField
	Widget eventsTableSpace;

	@UiField
	FlexTable paymentsTable;

	@UiField
	Label enterpriseNameLabel;
	@UiField
	Label enterpriseAddressLabel;
	@UiField
	Label enterpriseCityLabel;
	@UiField
	Label enterpriseCCCLabel;

	@UiField
	Label employeeNameLabel;
	@UiField
	Label employeeSSLabel;
	@UiField
	Label employeeDocumentLabel;
	@UiField
	Label employeeSeniorityLabel;
	@UiField
	Label employeeAgreementCategoryLabel;

	@UiField
	Label periodLabel;
	@UiField
	Label daysLabel;

	@UiField
	Label remunerationLabel;
	@UiField
	Label cgcBaseLabel;
	@UiField
	Label cgpBaseLabel;
	@UiField
	Label irpfBaseLabel;
	@UiField
	Label hExtraBaseLabel;
	@UiField
	Label nonHExtraBaseLabel;
	@UiField
	Label prorationBaseLabel;

	@UiField
	Label totalPaymentLabel;
	@UiField
	Label totalPaymentsLabel;
	@UiField
	Label totalLiquidLabel;
	@UiField
	Label totalDeductionLabel;

	@UiField
	Button printButton;

	@UiField
	Button undoButton;
	@UiField
	Button redoButton;

	@UiField
	Button printPreviewButton;
	@UiField
	Button closePreviewButton;

	@UiField
	MyStyle style;
	
	

	private int zoom;
	private Scope scope;
	private SalaryDraftObject salaryDraftObject;

	private Map<Event.Type, String[]> eventStyles;
	

	public SalaryDraft() {
		initWidget(binder.createAndBindUi(this));
		initContextTable();
		initPaymentsTable();
		initPrint();
		initPrintPreview();
		scope = Scope.CONTRACT;
		salarySelect.addListener(this);
		showDraft();
		zoom = DEFAULT_ZOOM;
		initEventsStyles(style);
		initUndoRedo();
	}

	public void setSalaryDraftObject(SalaryDraftObject salaryDraftObject) {
		showDraft();
		this.salaryDraftObject = salaryDraftObject;
		onChangedSalaryDraftObject();
	}

	@Override
	public void onChange(SalarySelect salarySelect) {
		salaryDraftObject.calculate(this);
	}

	@Override
	public void onCalculateSucces(SalaryDraftObject object) {
		salarySelect.setSalaryPreview(object.asSalaryPreview());
		dumpSalaryDraft();
	}

	@Override
	public void onCalculateFailure(Throwable throwable) {
		// TODO Auto-generated method stub

	}

	private void showDraft() {
		showWidget(draftPanel);

		zoomListBox.setVisible(false);
		closePreviewButton.setVisible(false);

		salarySelect.setVisible(true);
		printPreviewButton.setVisible(true);
	}

	private void showPreview() {
		showWidget(printPreviewHTML);

		zoomListBox.setVisible(true);
		closePreviewButton.setVisible(true);

		salarySelect.setVisible(false);
		printPreviewButton.setVisible(false);
	}

	private void showWidget(Widget widget) {
		deckPanel.showWidget(deckPanel.getWidgetIndex(widget));
	}

	private void onChangedSalaryDraftObject() {
		salaryDraftObject.calculate(this);
	}

	private void dumpSalaryDraft() {

		enterpriseNameLabel.setText(salaryDraftObject.getEnterpriseName());
		enterpriseCCCLabel.setText(salaryDraftObject.getEnterpriseCCC());
		enterpriseAddressLabel.setText(salaryDraftObject.getEnterpriseAddress());

		employeeSSLabel.setText(salaryDraftObject.getEmployeeSS());
		employeeNameLabel.setText(salaryDraftObject.getEmployeeName());
		employeeDocumentLabel.setText(salaryDraftObject.getEmployeeDocument());
		employeeSeniorityLabel.setText(format(salaryDraftObject
				.getEmployeeSeniorityDate()));
		employeeAgreementCategoryLabel.setText(salaryDraftObject
				.getEmployeeAgreementCategory());

		Date startDate = salaryDraftObject.getStartDate();
		Date endDate = salaryDraftObject.getEndDate();

		periodLabel.setText(format(startDate) + " - " + format(endDate));
		daysLabel.setText(Integer.toString(CalendarUtil.getDaysBetween(
				startDate, endDate) + 1));
		totalPaymentsLabel.setText(format(salaryDraftObject.getTotalPayment()));

		cgcBaseLabel.setText(format(salaryDraftObject.getCgcBase()));
		cgpBaseLabel.setText(format(salaryDraftObject.getCgpBase()));
		irpfBaseLabel.setText(format(salaryDraftObject.getIrpfBase()));
		hExtraBaseLabel.setText(format(salaryDraftObject.gethExtraBase()));
		nonHExtraBaseLabel.setText(format(salaryDraftObject.getNonHExtraBase()));
		prorationBaseLabel.setText(format(salaryDraftObject.getProrationBase()));

		remunerationLabel.setText(format(salaryDraftObject.getRemuneration()));

		totalPaymentLabel.setText(format(salaryDraftObject.getTotalPayment()));
		totalDeductionLabel.setText(format(salaryDraftObject.getTotalDeduction()));
		totalLiquidLabel.setText(format(salaryDraftObject.getTotalLiquid()));

		clearEventsTable();
		clearContextTable();
		clearPaymentsTable();

		List<Payment> payments = salaryDraftObject.getPayments();
		dumpPayments(payments);
		insertNewPaymentRow();
		insertBlankPaymentRow();

		List<Deduction> deductions = salaryDraftObject.getDeductions();
		dumpDeductions(deductions);
		insertNewDeductionRow();
		insertBlankPaymentRow();
		insertBlankPaymentRow();

		List<Variable> context = salaryDraftObject.getContext();
		int added = 0;
		for (Scope step : SCOPE_STEPS) {
			if (added == context.size()) {
				break;
			}
			if (step.compareTo(scope) < 0) {
				break;
			}
			if (context.get(added).getScope().compareTo(step) < 0) {
				continue;
			}
			List<Variable> subContext = context.subList(added, context.size());
			added += dumpContext(subContext, step);
		}

		List<Event> events = salaryDraftObject.getEvents();
		dumpEvents(events);
		eventsTableSpace.setVisible(eventsTable.getRowCount() > 0);

	}

	private void initPaymentsTable() {

		paymentsTable.setText(0, 0, "CUANTIA");
		paymentsTable.getFlexCellFormatter().setColSpan(0, 0, 2);
		paymentsTable.setText(0, 1, "CONCEPTO");
		paymentsTable.setText(0, 2, "DEVENGOS");
		paymentsTable.setText(0, 3, "DEDUCCIONES");
		paymentsTable.getFlexCellFormatter().setColSpan(0, 3, 2);

		paymentsTable.getRowFormatter().addStyleName(0,
				AON.AON_DATA_TABLE_ROW_ODD);
		for (int i = 0; i < paymentsTable.getCellCount(0); i++) {
			paymentsTable.getCellFormatter().addStyleName(0, i, AON.AON_BOLD);
			paymentsTable.getCellFormatter().addStyleName(0, i,
					AON.AON_TEXT_CENTER);
		}

		paymentsTable.getColumnFormatter().setWidth(0, "3%");
		paymentsTable.getColumnFormatter().setWidth(1, "12%"); // CUANTIA
		// 2 ...
		paymentsTable.getColumnFormatter().setWidth(3, "16%"); // DEVENGO
		paymentsTable.getColumnFormatter().setWidth(4, "12%"); // DEDUCCION
		paymentsTable.getColumnFormatter().setWidth(5, "3%");

	}

	private void initContextTable() {
	}

	private void initEventsStyles(MyStyle myStyle) {
		eventStyles = new HashMap<Event.Type, String[]>();
		eventStyles.put(Event.Type.INFO, new String[] { "", "" });
		eventStyles.put(Event.Type.DEBUG, new String[] { "", "" });
		eventStyles.put(Event.Type.ERROR, new String[] { "aon-icon-exception",
				myStyle.textError() });
		eventStyles.put(Event.Type.WARNING, new String[] { myStyle.cellWarn(),
				myStyle.textWarn() });
	}
	
	private void initUndoRedo() {
		
		undoButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				salaryDraftObject.undo();
				salaryDraftObject.calculate(SalaryDraft.this);
			}
		});
		redoButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				salaryDraftObject.redo();
				salaryDraftObject.calculate(SalaryDraft.this);
			}
		});
	}

	private void initPrint() {
		printButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				print();
			}
		});

	}

	private void initPrintPreview() {
		printPreviewButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				printPreview();
			}
		});

		closePreviewButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				showDraft();
			}
		});

		for (int zoom = MIN_ZOOM; zoom < DEFAULT_ZOOM; zoom += ZOOM_STEP)
			zoomListBox.addItem(PERCENT_FORMAT.format((double) zoom / 100));
		int selectedIndex = zoomListBox.getItemCount();
		for (int zoom = DEFAULT_ZOOM; zoom < MAX_ZOOM; zoom += ZOOM_STEP)
			zoomListBox.addItem(PERCENT_FORMAT.format((double) zoom / 100));
		zoomListBox.addItem(PERCENT_FORMAT.format((double) MAX_ZOOM / 100));
		zoomListBox.setSelectedIndex(selectedIndex);
		zoomListBox.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				int index = SalaryDraft.this.zoomListBox.getSelectedIndex();
				String text = SalaryDraft.this.zoomListBox.getItemText(index);
				SalaryDraft.this.zoom = (int) (PERCENT_FORMAT.parse(text));
				getPrintPreview();
			}
		});
	}

	private void clearEventsTable() {
		eventsTable.removeAllRows();
	}

	private void clearContextTable() {
		contextTable.removeAllRows();
	}

	private void clearPaymentsTable() {
		for (int i = paymentsTable.getRowCount() - 1; i > 0; i--)
			paymentsTable.removeRow(i);
	}

	private void dumpEvents(List<Event> events) {

		int row = eventsTable.getRowCount();
		for (Event event : events) {

			Button headButton = new Button();
			headButton.setStyleName(AON.AON_ICON_EXCEPTION);
			headButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);

			eventsTable.setWidget(row, 0, headButton);
			eventsTable.setText(row, 1, event.getMessage());

			String styles[] = eventStyles.get(event.getType());

			StyleToggleButton itemButton = null;
			if (event instanceof HasPayment)
				itemButton = getPaymentButton((HasPayment) event, styles[0],
						styles[1]);
			else if (event instanceof HasDeduction)
				itemButton = getDeductionButton((HasDeduction) event,
						styles[0], styles[1]);

			if (itemButton != null) {
				eventsTable.setWidget(row, 2, itemButton);
				itemButton.setValue(true, true); // down
			}

			eventsTable.getCellFormatter().getElement(row, 0).getStyle()
					.setPropertyPx("borderRightWidth", 0);
			eventsTable.getCellFormatter().getElement(row, 1).getStyle()
					.setPropertyPx("borderLeftWidth", 0);
			eventsTable.getCellFormatter().getElement(row, 1).getStyle()
					.setPropertyPx("borderRightWidth", 0);
			eventsTable.getCellFormatter().getElement(row, 2).getStyle()
					.setPropertyPx("borderLeftWidth", 0);

			for (int col = 0; col < eventsTable.getCellCount(row); col++)
				eventsTable.getCellFormatter().addStyleName(row, col,
						"aon-panelGrid-odd");
			row++;
		}

	}

	private void dumpPayments(List<Payment> payments) {

		int row = paymentsTable.getRowCount();
		for (Payment payment : payments) {
			// dumpPayment(payment, tr++, AON.AON_ICON_ROW_SELECTOR);
			dumpItem(payment, row++, AON.AON_ICON_ROW_SELECTOR,
					new PaymentChangeHandler<TextBox>(payment));
		}

	}

	private void formatRow(int row) {
		paymentsTable.getCellFormatter().getElement(row, 0).getStyle()
				.setPropertyPx("borderRightWidth", 0);
		paymentsTable.getCellFormatter().getElement(row, 1).getStyle()
				.setPropertyPx("borderLeftWidth", 0);
		if (paymentsTable.getCellCount(row) > 5) {
			paymentsTable.getCellFormatter().getElement(row, 4).getStyle()
					.setPropertyPx("borderRightWidth", 0);
			paymentsTable.getCellFormatter().getElement(row, 5).getStyle()
					.setPropertyPx("borderLeftWidth", 0);
		}

		paymentsTable.getRowFormatter().addStyleName(
				row,
				row % 2 == 0 ? AON.AON_DATA_TABLE_ROW_ODD
						: AON.AON_DATA_TABLE_ROW_EVEN);
	}

	private void insertNewPaymentRow() {
		int row = paymentsTable.getRowCount();

		Button newButton = new Button();
		newButton.setStyleName(AON.AON_ICON_RESET); // clear gwt-Button
		newButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		newButton.getElement().getStyle().setProperty("padding", "inherit");

		paymentsTable.setWidget(row, 0, newButton);
		paymentsTable.setHTML(row, 1, "&nbsp;");

		final MultiWordSuggestOracle paymentsOracle = new MultiWordSuggestOracle();
		SuggestBox descriptionBox = new SuggestBox(paymentsOracle);
		descriptionBox.getElement().getStyle().setWidth(98, Unit.PCT);
		paymentsTable.setWidget(row, 2, descriptionBox);
		NewPaymentHandler newPaymentHandler = new NewPaymentHandler();
		newPaymentHandler.setOracle(paymentsOracle);
		newPaymentHandler.setDescriptionBox(descriptionBox);

		TextBox amountBox = new TextBox();
		amountBox.getElement().getStyle().setWidth(98, Unit.PCT);
		amountBox.addStyleName(AON.AON_TEXT_RIGHT);
		amountBox.setVisible(false);
		paymentsTable.setWidget(row, 3, amountBox);
		newPaymentHandler.setExpressionBox(amountBox);

		paymentsTable.setHTML(row, 4, "&nbsp;");
		paymentsTable.setHTML(row, 5, "&nbsp;");

		formatRow(row);
	}

	private void insertNewDeductionRow() {
		int row = paymentsTable.getRowCount();

		Button newButton = new Button();
		newButton.setStyleName(AON.AON_ICON_RESET); // clear gwt-Button
		newButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		newButton.getElement().getStyle().setProperty("padding", "inherit");

		paymentsTable.setWidget(row, 0, newButton);
		paymentsTable.setHTML(row, 1, "&nbsp;");

		final MultiWordSuggestOracle deductionsOracle = new MultiWordSuggestOracle();
		SuggestBox descriptionBox = new SuggestBox(deductionsOracle);
		descriptionBox.getElement().getStyle().setWidth(98, Unit.PCT);
		paymentsTable.setWidget(row, 2, descriptionBox);
		NewDeductionHandler newDeductionHandler = new NewDeductionHandler();
		newDeductionHandler.setOracle(deductionsOracle);
		newDeductionHandler.setDescriptionBox(descriptionBox);

		paymentsTable.setHTML(row, 3, "&nbsp;");

		TextBox amountBox = new TextBox();
		amountBox.getElement().getStyle().setWidth(98, Unit.PCT);
		amountBox.addStyleName(AON.AON_TEXT_RIGHT);
		amountBox.setVisible(false);
		paymentsTable.setWidget(row, 4, amountBox);
		newDeductionHandler.setExpressionBox(amountBox);
		paymentsTable.getFlexCellFormatter().setColSpan(row, 4, 2);

		// paymentsTable.setHTML(tr, 5, "&nbsp;");

		formatRow(row);
	}

	private void insertBlankPaymentRow() {
		int row = paymentsTable.getRowCount();
		paymentsTable.setHTML(row, 0, "&nbsp;");
		paymentsTable.setHTML(row, 1, "&nbsp;");
		paymentsTable.setHTML(row, 2, "&nbsp;");
		paymentsTable.setHTML(row, 3, "&nbsp;");
		paymentsTable.setHTML(row, 4, "&nbsp;");
		paymentsTable.setHTML(row, 5, "&nbsp;");

		formatRow(row);
	}

	private void dumpDeductions(List<Deduction> deductions) {

		int row = paymentsTable.getRowCount();
		for (Deduction deduction : deductions) {

			String description = DEDUCTION_DESCRIPTIONS
					.get(deduction.getType());
			if (description != null) {
				dumpSystemDeduction(deduction, description, row++);
			} else {
				dumpItem(deduction, row++, AON.AON_ICON_ROW_SELECTOR,
						new DeductionChangeHandler<TextBox>(deduction), true);
			}
		}

	}

	private <I extends Item> void dumpItem(I item, int row,
			String iconStyleName, ItemChangeHandler<TextBox, I> handler) {
		dumpItem(item, row, iconStyleName, handler, false);
	}

	private <I extends Item> void dumpItem(I item, int row,
			String iconStyleName, ItemChangeHandler<TextBox, I> handler,
			boolean isDeduction) {

		// first cell for edit other stuff buttons.
		Button editButton = new Button();
		editButton.setStyleName(iconStyleName);
		editButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		// remove 'aon-editDataTable-button' margin & paddind.
		// We don't like it here.
		editButton.getElement().getStyle().setProperty("padding", "inherit");
		paymentsTable.setWidget(row, 0, editButton);

		paymentsTable.setHTML(row, 1, "&nbsp;");

		TextBox descriptionBox = new TextBox();
		descriptionBox.setText(item.getDescription());
		descriptionBox.getElement().getStyle().setWidth(98, Unit.PCT);
		paymentsTable.setWidget(row, 2, descriptionBox);

		final TextBox amountBox = new TextBox();

		String amount = format(item.getAmount());
		amountBox.setText(amount != null ? amount : item.getExpression());
		amountBox.getElement().getStyle().setWidth(98, Unit.PCT);
		amountBox.addStyleName(AON.AON_TEXT_RIGHT);
		paymentsTable.setWidget(row, isDeduction ? 4 : 3, amountBox);

		handler.setDescriptionWidget(descriptionBox);
		handler.setExpressionWidget(amountBox);

		paymentsTable.setHTML(row, isDeduction ? 3 : 4, "&nbsp;");

		Button deleteButton = new Button();
		deleteButton.setStyleName(AON.AON_ICON_DELETE);
		deleteButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		deleteButton.getElement().getStyle().setProperty("padding", "inherit");
		paymentsTable.setWidget(row, 5, deleteButton);
		paymentsTable.getCellFormatter().addStyleName(row, 5,
				AON.AON_TEXT_RIGHT);
		handler.setDeleteButton(deleteButton);

		formatRow(row);
	}

	private void dumpSystemDeduction(Deduction deduction, String description,
			int row) {

		paymentsTable.setHTML(row, 0, "&nbsp;");
		paymentsTable.setText(row, 1, deduction.getDescription());
		paymentsTable.getCellFormatter().addStyleName(row, 0,
				AON.AON_TEXT_CENTER);
		paymentsTable.setHTML(row, 2, description);
		paymentsTable.setText(row, 4, format(deduction.getAmount()));
		paymentsTable.getCellFormatter().addStyleName(row, 4,
				AON.AON_TEXT_RIGHT);
		paymentsTable.getFlexCellFormatter().setColSpan(row, 4, 2);
		formatRow(row);
	}

	private int dumpContext(List<Variable> context, Scope to) {
		int cols = 3;

		int count = contextTable.getRowCount() * cols; // getCellCount(contextTable);

		int i = 0;

		for (; i < context.size(); i++) {

			Variable variable = context.get(i);

			Scope scope = variable.getScope();
			if (scope.compareTo(to) < 0) {
				break;
			}

			HTMLPanel htmlPanel = new HTMLPanel("");

			htmlPanel.add(getLabel(variable));

			Panel valuePanel = new HorizontalPanel();
			valuePanel.setStyleName(GWT_HORIZONTAL_PANEL);

			TextBox variableTextBox = new TextBox();
			VariableChangeHandler<TextBox> variableChangeHandler = new VariableChangeHandler<TextBox>(
					variable);
			variableChangeHandler.setUiObject(variableTextBox);
			valuePanel.add(variableTextBox);

			if (!(variable instanceof UndefinedVariable)) {
				valuePanel.add(getDeleteButton(variable));
			} else if (variable instanceof UndefinedPaymentVariable) {
				String styles[] = eventStyles.get(Event.Type.WARNING);

				StyleToggleButton itemButton = getPaymentButton(
						(UndefinedPaymentVariable) variable, styles[0],
						styles[1]);

				valuePanel.add(itemButton);
				itemButton.setValue(true, true);
			} else if (variable instanceof UndefinedDeductionVariable) {
				String styles[] = eventStyles.get(Event.Type.WARNING);

				StyleToggleButton itemButton = getDeductionButton(
						(UndefinedDeductionVariable) variable, styles[0],
						styles[1]);

				valuePanel.add(itemButton);
				itemButton.setValue(true, true);
			}

			htmlPanel.add(valuePanel);

			int row = count / cols;
			int col = count % cols;
			contextTable.setWidget(row, col, htmlPanel);

			contextTable.getRowFormatter().addStyleName(
					row,
					row % 2 == 0 ? AON.AON_DATA_TABLE_ROW_ODD
							: AON.AON_DATA_TABLE_ROW_EVEN);

			contextTable.getColumnFormatter().setWidth(col, (100 / cols) + "%");

			count++;
		}

		if (i < context.size()) {
			Variable variable = context.get(i);

			final Scope scope = variable.getScope();
			final List<Variable> remainContext = context.subList(i,
					context.size());

			Panel expandPanel = new HorizontalPanel();
			expandPanel.setStyleName(GWT_HORIZONTAL_PANEL);
			final Label expandLabel = new Label(
					((SalaryDraft.this.scope.compareTo(scope) <= 0) ? "Ocultar"
							: "Mostrar")
							+ " variables del "
							+ SCOPE_DESCRIPTIONS.get(scope));
			expandPanel.add(expandLabel);
			final Button expandButton = new Button();
			expandButton.setStyleName(style.expandAllButton());
			expandButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
			expandPanel.add(expandButton);

			HTMLPanel htmlPanel = new HTMLPanel("");
			HTML blank = new HTML("&nbsp;");
			blank.setStyleName(style.cellLabel());
			htmlPanel.add(blank);
			htmlPanel.add(expandPanel);

			final int row = count / cols;
			final int col = count % cols;

			expandButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					if (SalaryDraft.this.scope.compareTo(scope) <= 0)
						collapse();
					else
						expand();
				}

				private void expand() {
					dumpContext(remainContext, scope);
					expandButton.removeStyleName(style.expandAllButton());
					expandButton.setStyleName(style.collapseAllButton(), true);
					expandLabel.setText("Ocultar variables del "
							+ SCOPE_DESCRIPTIONS.get(scope));
					SalaryDraft.this.scope = scope;
				}

				private void collapse() {
					for (int i = contextTable.getRowCount() - 1; i > row; i--)
						contextTable.removeRow(i);
					for (int i = contextTable.getCellCount(row) - 1; i > col; i--)
						contextTable.removeCell(row, i);

					expandButton.removeStyleName(style.collapseAllButton());
					expandButton.setStyleName(style.expandAllButton(), true);
					expandLabel.setText("Mostrar variables del "
							+ SCOPE_DESCRIPTIONS.get(scope));
					SalaryDraft.this.scope = Scope.values()[SalaryDraft.this.scope
							.ordinal() + 1];
				}

			});

			contextTable.setWidget(row, col, htmlPanel);
		}

		return i;
	}

	private void print() {

		salaryDraftObject.download("application/pdf",
				new AsyncCallback<String>() {
					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(String dataURI) {
						// TODO Auto-generated method stub
						Window.open(dataURI, "Vista Preliminar", null);
					}

				});
	}

	private void printPreview() {
		showPreview();
		getPrintPreview();
	}

	private void getPrintPreview() {
		salaryDraftObject.getAsHTML(zoom, new AsyncCallback<String>() {

			@Override
			public void onSuccess(String result) {
				printPreviewHTML.setHTML(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO
				printPreviewHTML.setHTML(caught.getMessage());
			}
		});
	}

	private String format(Double amount) {
		return amount == null ? null : CURRENCY_FORMAT.format(amount);
	}

	private String format(Date date) {
		return date == null ? null : DATE_FORMAT.format(date);
	}

	private Widget getLabel(Variable variable) {
		String text = variable.getName();

		if (variable.getScope() == Scope.SALARY) {
			text = "*" + text;
		}

		Label label = new Label(text);

		label.setStyleName(style.cellLabel());
		if (variable instanceof UndefinedVariable) {
			label.setStyleName(style.cellWarn(), true);
		}

		return label;
	}

	private Button getDeleteButton(Variable variable) {
		Button deleteButton = new Button();
		deleteButton.setStyleName(AON.AON_ICON_DELETE);
		deleteButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		deleteButton.addClickHandler(new VariableRemoveHandler(variable));
		return deleteButton;
	}

	private StyleToggleButton getPaymentButton(final HasPayment hasPayment,
			final String iconStyleName, final String textStyleName) {
		StyleToggleButton paymentButton = new StyleToggleButton(
				"aon-icon-file-entrance", "aon-icon-file-exit-cancel");

		paymentButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		paymentButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			private Element tr;

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				boolean down = event.getValue();
				if (down) {
					tr = showPayment(hasPayment.getPayment(), iconStyleName,
							textStyleName);
				} else {
					removePayment(tr);
					removePayment(hasPayment.getPayment());
				}

			}
		});

		return paymentButton;
	}

	private StyleToggleButton getDeductionButton(
			final HasDeduction hasDeduction, final String iconStyleName,
			final String textStyleName) {

		StyleToggleButton paymentButton = new StyleToggleButton(
				"aon-icon-file-entrance", "aon-icon-file-exit-cancel");

		paymentButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		paymentButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			private Element tr;

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				boolean down = event.getValue();
				if (down) {
					tr = showDeduction(hasDeduction.getDeduction(),
							iconStyleName, textStyleName);
				} else {
					removePayment(tr);
					removeDeduction(hasDeduction.getDeduction());
				}

			}
		});

		return paymentButton;
	}

	private void removePayment(Element tr) {
		RowFormatter formatter = paymentsTable.getRowFormatter();
		for (int row = 0; row < paymentsTable.getRowCount(); row++) {
			if (tr == formatter.getElement(row)) {
				paymentsTable.removeRow(row);
				return;
			}
		}
	}

	private void removePayment(Payment payment) {
		List<Payment> payments = salaryDraftObject.getPayments();
		for (int i = 0; i < payments.size(); i++) {
			if (payment == payments.get(i)) {
				payments.remove(i);
				return;
			}
		}
	}

	private void removeDeduction(Deduction deduction) {
		List<Deduction> deductions = salaryDraftObject.getDeductions();
		for (int i = 0; i < deductions.size(); i++) {
			if (deduction == deductions.get(i)) {
				deductions.remove(i);
				return;
			}
		}
	}

	private Element showPayment(Payment payment, String iconStyleName,
			String textStyleName) {

		List<Payment> payments = salaryDraftObject.getPayments();
		PaymentComparator comparator = new PaymentComparator();

		int idx = 0;
		for (; idx < payments.size(); idx++)
			if (comparator.compare(payments.get(idx), payment) > 0)
				break;

		payments.add(idx, payment);

		idx += 1; // We add one due to header

		paymentsTable.insertRow(idx);
		dumpItem(payment, idx, iconStyleName,
				new PaymentChangeHandler<TextBox>(payment));

		CellFormatter fomatter = paymentsTable.getCellFormatter();
		for (int col = 0; col < paymentsTable.getCellCount(idx); col++) {
			fomatter.addStyleName(idx, col, textStyleName);
		}

		return paymentsTable.getRowFormatter().getElement(idx);

	}

	private Element showDeduction(Deduction deduction, String iconStyleName,
			String textStyleName) {

		List<Deduction> deductions = salaryDraftObject.getDeductions();
		DeductionComparator comparator = new DeductionComparator();

		int idx = 0;
		for (; idx < deductions.size(); idx++)
			if (comparator.compare(deductions.get(idx), deduction) > 0)
				break;

		deductions.add(idx, deduction);

		idx += salaryDraftObject.getPayments().size() + 3; // We add one due to header
		paymentsTable.insertRow(idx);

		dumpItem(deduction, idx, iconStyleName,
				new DeductionChangeHandler<TextBox>(deduction), true);

		CellFormatter fomatter = paymentsTable.getCellFormatter();
		for (int col = 0; col < paymentsTable.getCellCount(idx); col++) {
			fomatter.addStyleName(idx, col, textStyleName);
		}

		return paymentsTable.getRowFormatter().getElement(idx);
	}

}
