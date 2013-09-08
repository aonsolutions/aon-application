package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.payroll.client.SalaryDraftObject.CalculateCallback;
import com.esferalia.aon.gwt.payroll.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.DeductionComparator;
import com.esferalia.aon.gwt.payroll.shared.HasDeduction;
import com.esferalia.aon.gwt.payroll.shared.HasPayment;
import com.esferalia.aon.gwt.payroll.shared.Item;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.PaymentComparator;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Event;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;
import com.esferalia.aon.gwt.payroll.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.StringVariable;
import com.esferalia.aon.gwt.payroll.shared.UndefinedDeductionVariable;
import com.esferalia.aon.gwt.payroll.shared.UndefinedPaymentVariable;
import com.esferalia.aon.gwt.payroll.shared.UndefinedVariable;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.esferalia.aon.payroll.IrpfResult;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestBox.DefaultSuggestionDisplay;
import com.google.gwt.user.client.ui.SuggestBox.SuggestionCallback;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;

public class SalaryDraft extends ResizeComposite implements CalculateCallback,
		SalarySelect.Listener, UndoManager.Listener {

	public static final String CUSTOM = "CUSTOM";
	public static final String ONLY_THIS_MONTH = "ONLY_THIS_MONTH";
	public static final String FROM_THIS_MONTH = "FROM_THIS_MONTH";

	private static final int ZOOM_STEP = 20;

	private static final int MIN_ZOOM = 50;
	private static final int MAX_ZOOM = 200;
	private static final int DEFAULT_ZOOM = 135;

	private static final DateTimeFormat DATE_SHORT = DateTimeFormat
			.getFormat(PredefinedFormat.DATE_SHORT);

	private static final DateTimeFormat DATE_FORMAT = DateTimeFormat
			.getFormat(PredefinedFormat.YEAR_MONTH_NUM_DAY);

	public static final NumberFormat PERCENT_FORMAT = NumberFormat
			.getPercentFormat();

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

	static class VisibilityImpl implements HasVisibility {

		private com.google.gwt.dom.client.Element elem;

		public VisibilityImpl(com.google.gwt.dom.client.Element elem) {
			this.elem = elem;
		}

		@Override
		public boolean isVisible() {
			return UIObject.isVisible(elem);
		}

		@Override
		public void setVisible(boolean visible) {
			UIObject.setVisible(elem, visible);
		}

	}

	class VariableChangeHandler<T extends UIObject & HasValue<String> & HasAllFocusHandlers>
			implements FocusHandler, BlurHandler {

		private T uiObject;
		private Variable variable;

		public VariableChangeHandler(Variable variable) {
			this.variable = variable;
		}

		public void setLabel(Label label) {
			label.addDoubleClickHandler(new DoubleClickHandler() {

				@Override
				public void onDoubleClick(DoubleClickEvent event) {
					InputDialog inputDialog = new InputDialog("Renombrar...",
							"Nuevo Nombre"){
						@Override
						public void onAccept() {
							String newName = getInputValue();
							if ( variable.getName().equals(newName) )
								return;
							salaryDraftObject.renameVariable(variable, newName);
							salaryDraftObject.calculate(SalaryDraft.this);
							
						}
					};
					inputDialog.setInputValue(variable.getName());
					inputDialog.center();
					inputDialog.show();
				}
			});
		}

		public void setUiObject(T uiObject) {
			this.uiObject = uiObject;
			this.uiObject.addBlurHandler(this);
			this.uiObject.addFocusHandler(this);
			setValue(variable.getValue());
		}

		@Override
		public void onFocus(FocusEvent event) {
			setValue(variable.getExpression());
			fxButton.setEnabled(true);
			fxhasValue = uiObject;
		}

		@Override
		public void onBlur(BlurEvent event) {
			if (!isChanged()) {
				setValue(variable.getValue());
			} else {
				onValueChange();
			}
			fxButton.setEnabled(false);
		}

		private void onValueChange() {

			String value = uiObject.getValue();

			StringVariable var = new StringVariable();
			var.setImplicit(false);
			var.setScope(Scope.SALARY); // DRAFT
			var.setName(variable.getName());
			var.setEndDate(variable.getEndDate());
			var.setStartDate(variable.getStartDate());
			var.setExpression(StringUtils.isEmpty(value) ? "REMOVE_VARIABLE()"
					: value);

			salaryDraftObject.addDraftVariable(var);
			salaryDraftObject.calculate(SalaryDraft.this);
		}

		private void setValue(Object value) {
			uiObject.setValue(value == null ? null : value.toString());
		}

		private boolean isChanged() {
			return !StringUtils.equals(uiObject.getValue(),
					variable.getExpression());
		}

	}

	abstract class ItemChangeHandler<T extends UIObject & HasValue<String> & HasAllFocusHandlers, I extends Item> {

		I item;

		public ItemChangeHandler(I item) {
			this.item = item;
		}

		public void setDescriptionWidget(final T widget) {
			widget.addFocusHandler(new FocusHandler() {
				@Override
				public void onFocus(FocusEvent event) {
					// TODO :
				}
			});
			widget.addBlurHandler(new BlurHandler() {
				@Override
				public void onBlur(BlurEvent event) {
					if (!StringUtils.equals(widget.getValue(),
							item.getDescription()))
						onDescriptionChange(item, widget.getValue());
				}
			});
		}

		public void setExpressionWidget(final T widget) {
			widget.addFocusHandler(new FocusHandler() {
				@Override
				public void onFocus(FocusEvent event) {
					widget.setValue(item.getExpression());
					fxButton.setEnabled(true);
					fxhasValue = widget;
				}
			});
			widget.addBlurHandler(new BlurHandler() {
				@Override
				public void onBlur(BlurEvent event) {
					if (!StringUtils.equals(widget.getValue(),
							item.getExpression()))
						onExpressionChange(item, widget.getValue());
					else
						widget.setValue(format(item.getAmount()));

					fxButton.setEnabled(false);
				}
			});
		}

		public void setEditButton(HasClickHandlers deleteButton) {
			deleteButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					onEdit();
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

		abstract void onEdit();

		abstract void onExpressionChange(I item, String expression);

		abstract void onDescriptionChange(I item, String description);
	}

	class PaymentChangeHandler<T extends UIObject & HasValue<String> & HasAllFocusHandlers>
			extends ItemChangeHandler<T, Payment> {

		public PaymentChangeHandler(Payment payment) {
			super(payment);
		}

		@Override
		void onEdit() {
			// TODO Auto-generated method stub
			PaymentDialog paymentDialog = new PaymentDialog();
			paymentDialog.setNumberFormat(AON.CURRENCY_FORMAT);
			paymentDialog.setContextProvider(salaryDraftObject);
			paymentDialog.setConcept(getConcept());
			paymentDialog.setMonth(item.getMonth());
			paymentDialog.setType(item.getType());
			paymentDialog.setReceiptType(item.getSalaryType());
			paymentDialog.setDescription(item.getDescription());
			paymentDialog.setPaymentExpression(item.getExpression()); //
			paymentDialog.setIrpfExpression(item.getIrpfExpression());
			paymentDialog.setQuoteExpression(item.getQuoteExpression());

			int width = paymentDialog.getOffsetWidth();
			// paymentDialog.setWidth(Math.max(Window.getClientWidth() * 6 / 10,
			// width ) + "px");
			paymentDialog.center();
			paymentDialog.show();
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

		private Payment getConcept() {
			if (item.getName() == null)
				return null;
			for (Payment payment : availablePaymens)
				if (StringUtils.equals(payment.getName(), item.getName()))
					return payment;
			return null;
		}

	}

	class DeductionChangeHandler<T extends UIObject & HasValue<String> & HasAllFocusHandlers>
			extends ItemChangeHandler<T, Deduction> {

		@Override
		void onEdit() {
			// TODO Auto-generated method stub

		}

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

	abstract class NewItemHandler<T extends Item> extends
			DefaultSuggestionDisplay {

		Button recoverButton;
		TextBox expressionBox;
		SuggestBox descriptionBox;
		MultiWordSuggestOracle oracle;

		Map<String, T> itemsConceptsMap;

		public void setDescriptionBox(SuggestBox descriptionBox) {
			this.descriptionBox = descriptionBox;

			this.descriptionBox.getValueBox().addFocusHandler(
					new FocusHandler() {

						@Override
						public void onFocus(FocusEvent event) {
							if (itemsConceptsMap == null) {
								itemsConceptsMap = new HashMap<String, T>();

								for (T item : getAvailableItems()) {
									String suggestion = item.getDescription();
									if (!StringUtils.isEmpty(item.getName()))
										suggestion += " ( " + item.getName()
												+ " )";

									oracle.add(suggestion);
									itemsConceptsMap.put(suggestion, item);
								}
							}
						}
					});

			this.descriptionBox.getValueBox().addBlurHandler(new BlurHandler() {
				@Override
				public void onBlur(BlurEvent event) {
					onValueChange(NewItemHandler.this.descriptionBox);
				}
			});
			this.descriptionBox
					.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {

						@Override
						public void onSelection(SelectionEvent<Suggestion> event) {
							onValueChange(NewItemHandler.this.descriptionBox);
						}
					});
		}

		public void setOracle(MultiWordSuggestOracle oracle) {
			this.oracle = oracle;
		}

		public void setExpressionBox(TextBox expressionBox) {
			this.expressionBox = expressionBox;

			this.expressionBox.addBlurHandler(new BlurHandler() {
				@Override
				public void onBlur(BlurEvent event) {
					onValueChange(NewItemHandler.this.expressionBox);
				}
			});
			// this.expressionBox.addValueChangeHandler(this);
		}

		protected void onValueChange(UIObject source) {

			String suggestion = descriptionBox.getValue();
			T item = itemsConceptsMap.get(suggestion);

			String expression = null;
			if (item != null) {
				expression = item.getExpression();
			}

			if (StringUtils.isEmpty(expression)) {

				expression = expressionBox.getValue();
				if (StringUtils.isEmpty(expression)
						&& (source != expressionBox)) {
					expressionBox.setVisible(true);
					// wait for event's loop to terminate.
					Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						@Override
						public void execute() {
							expressionBox.setFocus(true);

						}
					});

					return;
				}
			}

			addDrafItem(item, expression);

			salaryDraftObject.calculate(SalaryDraft.this);
		}

		protected T getItem(String description) {
			return itemsConceptsMap.get(description);
		}

		protected abstract List<T> getAvailableItems();

		protected abstract void addDrafItem(T item, String expr);

	}

	class NewDeductionHandler extends NewItemHandler<Deduction> {

		@Override
		protected List<Deduction> getAvailableItems() {
			return Collections.<Deduction> emptyList();
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
			deduction.setDescription(descriptionBox.getValue());
			deduction.setSalaryType(salaryDraftObject.getType());
			deduction.setStartDate(salaryDraftObject.getEndDate());
			deduction.setStartDate(salaryDraftObject.getStartDate());

			salaryDraftObject.addDraftDeduction(deduction);
		}

	}

	class NewPaymentHandler extends NewItemHandler<Payment> {

		@Override
		protected List<Payment> getAvailableItems() {
			return SalaryDraft.this.availablePaymens;
		}

		@Override
		protected void addDrafItem(Payment payment, String expression) {
			Payment draftPayment = new Payment();

			draftPayment.setExpression(expression);
			if (payment != null) {
				draftPayment.setName(payment.getName());
				draftPayment.setType(payment.getType());
				draftPayment.setConceptId(payment.getId());
				draftPayment.setDescription(payment.getDescription());
				draftPayment.setIrpfExpression(payment.getIrpfExpression());
				draftPayment.setQuoteExpression(payment.getQuoteExpression());
			} else {
				draftPayment.setIrpfExpression("_P");
				draftPayment.setQuoteExpression("_P");
				draftPayment.setType(Payment.Type.SALARY_SUPPLEMENTS);
				draftPayment.setDescription(descriptionBox.getText());
			}

			draftPayment.setScope(Scope.SALARY);
			draftPayment.setEndDate(salaryDraftObject.getEndDate());
			draftPayment.setStartDate(salaryDraftObject.getStartDate());
			draftPayment.setSalaryType(salaryDraftObject.getType());
			// draftPayment.setMonth(deduction.getMonth());

			salaryDraftObject.addDraftPayment(draftPayment);

		}

		@Override
		protected void showSuggestions(SuggestBox suggestBox,
				Collection<? extends Suggestion> suggestions,
				boolean isDisplayStringHTML, boolean isAutoSelectEnabled,
				SuggestionCallback callback) {

			Collection<Suggestion> mySuggestions = new ArrayList<Suggestion>(
					suggestions.size());

			for (Suggestion suggestion : suggestions) {
				String replacementString = suggestion.getReplacementString();
				Payment payment = getItem(replacementString);

				SafeHtmlBuilder htmlBuilder = new SafeHtmlBuilder();

				String clazz = null;
				if (StringUtils.isEmpty(payment.getName()))
					clazz = payment.getScope() == Scope.CONTRACT ? "employee_payment"
							: "enterprise_payment";
				else
					clazz = "payment_concept";

				htmlBuilder.appendHtmlConstant("<span class=\"" + clazz
						+ "\" >");
				htmlBuilder.appendHtmlConstant(suggestion.getDisplayString());
				htmlBuilder.appendHtmlConstant("</span>");

				mySuggestions
						.add(new MultiWordSuggestOracle.MultiWordSuggestion(
								replacementString, htmlBuilder.toSafeHtml()
										.asString()));
			}

			super.showSuggestions(suggestBox, mySuggestions,
					isDisplayStringHTML, isAutoSelectEnabled, callback);
		}

	}

	abstract class AbstractVarHandler implements ClickHandler {

		private Variable variable;

		public AbstractVarHandler(Variable variable) {
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
			var.setExpression(getExpression(var));
			salaryDraftObject.addDraftVariable(var);

			salaryDraftObject.calculate(SalaryDraft.this);
		}

		abstract String getExpression(Variable var);

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

	abstract class RecoverItemHandler<T extends Item> implements ClickHandler {

		private T item;

		public RecoverItemHandler(T item) {
			this.item = item;
		}

		@Override
		public void onClick(ClickEvent event) {
			addDrafItem(item);
			salaryDraftObject.calculate(SalaryDraft.this);

		}

		abstract void addDrafItem(T item);

	}

	class RecoverPaymentHandler extends RecoverItemHandler<Payment> {

		public RecoverPaymentHandler(Payment payment) {
			super(payment);
		}

		@Override
		void addDrafItem(Payment payment) {
			fillPayment(payment);
			salaryDraftObject.addDraftPayment(payment);
		}

		void fillPayment(Payment draftPayment) {

			if (draftPayment.getType() != null)
				return;

			for (Payment payment : SalaryDraft.this.availablePaymens) {
				if (StringUtils.equals(payment.getName(),
						draftPayment.getName())) {
					draftPayment.setConceptId(payment.getId());
					draftPayment.setType(payment.getType());
					draftPayment.setIrpfExpression(payment.getIrpfExpression());
					draftPayment.setQuoteExpression(payment
							.getQuoteExpression());

					draftPayment.setScope(Scope.SALARY);
					draftPayment.setEndDate(salaryDraftObject.getEndDate());
					draftPayment.setStartDate(salaryDraftObject.getStartDate());
					draftPayment.setSalaryType(salaryDraftObject.getType());
				}
			}
		}

	}

	class RecoverDeductionHandler implements ClickHandler {

		private Deduction deduction;

		public RecoverDeductionHandler(Deduction deduction) {
			this.deduction = deduction;
		}

		@Override
		public void onClick(ClickEvent event) {
			// TODO Auto-generated method stub
			salaryDraftObject.addDraftDeduction(deduction);
			salaryDraftObject.calculate(SalaryDraft.this);

		}
	}

	interface MyStyle extends CssResource {
		@ClassName("cell-label")
		String cellLabel();

		@ClassName("cell-warn")
		String cellWarn();

		@ClassName("text-ok")
		String textOk();

		@ClassName("text-warn")
		String textWarn();

		@ClassName("text-error")
		String textError();

		@ClassName("value-changed")
		String valueChanged();

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
	HTML irpfPreviewHTML;

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
	ValueLabel remunerationLabel;
	@UiField
	Label dbRemunerationLabel;
	@UiField
	ValueLabel cgcBaseLabel;
	@UiField
	Label dbCgcBaseLabel;
	@UiField
	ValueLabel cgpBaseLabel;
	@UiField
	Label dbCgpBaseLabel;
	@UiField
	ValueLabel irpfBaseLabel;
	@UiField
	Label dbIrpfBaseLabel;
	@UiField
	ValueLabel hExtraBaseLabel;
	@UiField
	Label dbHExtraBaseLabel;
	@UiField
	ValueLabel nonHExtraBaseLabel;
	@UiField
	Label dbNonHExtraBaseLabel;
	@UiField
	ValueLabel prorationBaseLabel;
	@UiField
	Label dbProrationBaseLabel;

	@UiField
	ValueLabel totalPaymentLabel;
	@UiField
	Label dbTotalPaymentLabel;
	@UiField
	ValueLabel totalPaymentsLabel;
	@UiField
	Label dbTotalPaymentsLabel;
	@UiField
	ValueLabel totalLiquidLabel;
	@UiField
	Label dbTotalLiquidLabel;
	@UiField
	ValueLabel totalDeductionLabel;
	@UiField
	Label dbTotalDeductionLabel;

	@UiField
	Button printButton;
	@UiField
	Button acceptButton;
	@UiField
	Button salaryButton;

	@UiField
	Button fxButton;

	@UiField
	Button undoButton;
	@UiField
	Button redoButton;

	@UiField
	ListBox datesListBox;

	@UiField
	CheckBox dbSalaryCheck;

	@UiField
	Button closePreviewButton;
	@UiField
	Button printPreviewButton;

	@UiField
	MyStyle style;

	private int zoom;
	private Scope scope;
	private List<HasVisibility> dbUIObjects;
	private SalaryDraftObject salaryDraftObject;
	private Map<Event.Type, String[]> eventStyles;
	private List<Payment> availablePaymens = new ArrayList<Payment>();

	private HasValue<String> fxhasValue;

	public SalaryDraft() {
		initWidget(binder.createAndBindUi(this));
		initPaymentsTable();
		initPrintPreview();
		scope = Scope.CONTRACT;
		salarySelect.addListener(this);
		showDraft();
		zoom = DEFAULT_ZOOM;
		initEventsStyles(style);
		initSalaryDb();
		initDatesListBox();
	}

	public void setSalaryDraftObject(SalaryDraftObject salaryDraftObject) {
		showDraft();
		// this.salaryDraftObject = salaryDraftObject;
		onChangedSalaryDraftObject(salaryDraftObject);
	}

	@Override
	public void onChange(SalarySelect salarySelect) {
		salaryDraftObject.calculate(this);
		syncDatesListBox();
	}

	@Override
	public void onChange(UndoManager undoManager) {
		redoButton.setEnabled(salaryDraftObject.canRedo());
		undoButton.setEnabled(salaryDraftObject.canUndo());

		acceptButton.setEnabled(salaryDraftObject.hasDrafts());
	}

	@Override
	public void onCalculateSucces(SalaryDraftObject salaryDraftObject) {
		boolean draftObjectChanged = this.salaryDraftObject != salaryDraftObject;
		if (draftObjectChanged)
			this.salaryDraftObject = salaryDraftObject;

		salarySelect.setSalaryPreview(salaryDraftObject.asSalaryPreview());

		// I don't like it. But almost it's clear enough.
		if (isPreviewVisible()) {
			getPrintPreview();
		}
		dumpSalaryDraft(!draftObjectChanged);

	}

	@Override
	public void onCalculateFailure(Throwable throwable) {
		// TODO Auto-generated method stub
		Window.alert(throwable.getMessage());
	}

	@UiHandler("irpfPreviewButton")
	void onIrpfPreviewClick(ClickEvent event) {
		irpfPreview();
	}

	private void setDbVisible(boolean visible) {
		dbCgcBaseLabel.setVisible(visible);
		dbCgpBaseLabel.setVisible(visible);
		dbHExtraBaseLabel.setVisible(visible);
		dbIrpfBaseLabel.setVisible(visible);
		dbNonHExtraBaseLabel.setVisible(visible);
		dbProrationBaseLabel.setVisible(visible);
		dbRemunerationLabel.setVisible(visible);
		dbTotalDeductionLabel.setVisible(visible);
		dbTotalLiquidLabel.setVisible(visible);
		dbTotalPaymentLabel.setVisible(visible);
		dbTotalPaymentsLabel.setVisible(visible);

		for (HasVisibility obj : dbUIObjects)
			obj.setVisible(visible);
	}

	private void showDraft() {
		showWidget(draftPanel);

		zoomListBox.setVisible(false);
		closePreviewButton.setVisible(false);

		fxButton.setVisible(true);
		salarySelect.setVisible(true);
		datesListBox.setVisible(true);
		printPreviewButton.setVisible(true);
		dbSalaryCheck.setVisible(salaryDraftObject != null
				&& salaryDraftObject.hasDbSalary());
	}

	private void showPreview() {
		showWidget(printPreviewHTML);

		zoomListBox.setVisible(true);
		closePreviewButton.setVisible(true);

		fxButton.setVisible(false);
		salarySelect.setVisible(false);
		datesListBox.setVisible(false);
		dbSalaryCheck.setVisible(false);
		printPreviewButton.setVisible(false);
	}

	private void showIrpfPreview() {
		showWidget(irpfPreviewHTML);

		zoomListBox.setVisible(true);
		closePreviewButton.setVisible(true);

		fxButton.setVisible(false);
		salarySelect.setVisible(false);
		datesListBox.setVisible(false);
		dbSalaryCheck.setVisible(false);
		printPreviewButton.setVisible(false);

	}

	boolean isIrpfPreviewVisible() {
		return isWidgetVisible(irpfPreviewHTML);
	}

	boolean isPreviewVisible() {
		return isWidgetVisible(printPreviewHTML);
	}

	private void showWidget(Widget widget) {
		deckPanel.showWidget(deckPanel.getWidgetIndex(widget));
	}

	private boolean isWidgetVisible(Widget w) {
		int index = deckPanel.getVisibleWidget();
		Widget visibleWidget = deckPanel.getWidget(index);
		return visibleWidget == w;
	}

	private void onChangedSalaryDraftObject(SalaryDraftObject salaryDraftObject) {

		salaryDraftObject.calculate(this);

		syncDatesListBox();

		// Sync undo & redo controls
		salaryDraftObject.addUndoManagerListener(this);
		redoButton.setEnabled(salaryDraftObject.canRedo());
		undoButton.setEnabled(salaryDraftObject.canUndo());
		acceptButton.setEnabled(salaryDraftObject.hasDrafts());

	}

	private void dumpSalaryDraft(boolean displayChanges) {

		enterpriseNameLabel.setText(salaryDraftObject.getEnterpriseName());
		enterpriseCCCLabel.setText(salaryDraftObject.getEnterpriseCCC());
		enterpriseAddressLabel
				.setText(salaryDraftObject.getEnterpriseAddress());

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

		totalPaymentsLabel.setText(format(salaryDraftObject.getTotalPayment()),
				displayChanges);
		dbTotalPaymentsLabel.setText(format(salaryDraftObject
				.getDbTotalPayment()));
		setDbStyleName(dbTotalPaymentsLabel, totalPaymentsLabel);

		cgcBaseLabel.setText(format(salaryDraftObject.getCgcBase()),
				displayChanges);
		dbCgcBaseLabel.setText(format(salaryDraftObject.getDbCgcBase()));
		setDbStyleName(dbCgcBaseLabel, cgcBaseLabel);
		cgpBaseLabel.setText(format(salaryDraftObject.getCgpBase()),
				displayChanges);
		dbCgpBaseLabel.setText(format(salaryDraftObject.getDbCgpBase()));
		setDbStyleName(dbCgpBaseLabel, cgpBaseLabel);
		irpfBaseLabel.setText(format(salaryDraftObject.getIrpfBase()),
				displayChanges);
		dbIrpfBaseLabel.setText(format(salaryDraftObject.getDbIrpfBase()));
		setDbStyleName(dbIrpfBaseLabel, irpfBaseLabel);
		hExtraBaseLabel.setText(format(salaryDraftObject.gethExtraBase()),
				displayChanges);
		dbHExtraBaseLabel.setText(format(salaryDraftObject.getDbHExtraBase()));
		setDbStyleName(dbHExtraBaseLabel, hExtraBaseLabel);
		nonHExtraBaseLabel.setText(
				format(salaryDraftObject.getNonHExtraBase()), displayChanges);
		dbNonHExtraBaseLabel.setText(format(salaryDraftObject
				.getDbNonHExtraBase()));
		setDbStyleName(dbNonHExtraBaseLabel, nonHExtraBaseLabel);
		prorationBaseLabel.setText(
				format(salaryDraftObject.getProrationBase()), displayChanges);
		dbProrationBaseLabel.setText(format(salaryDraftObject
				.getDbProrationBase()));
		setDbStyleName(dbProrationBaseLabel, prorationBaseLabel);

		remunerationLabel.setText(format(salaryDraftObject.getRemuneration()),
				displayChanges);
		dbRemunerationLabel.setText(format(salaryDraftObject
				.getDbRemuneration()));
		setDbStyleName(dbRemunerationLabel, remunerationLabel);

		totalPaymentLabel.setText(format(salaryDraftObject.getTotalPayment()),
				displayChanges);
		dbTotalPaymentLabel.setText(format(salaryDraftObject
				.getDbTotalPayment()));
		setDbStyleName(dbTotalPaymentLabel, totalPaymentLabel);

		totalDeductionLabel.setText(
				format(salaryDraftObject.getTotalDeduction()), displayChanges);
		dbTotalDeductionLabel.setText(format(salaryDraftObject
				.getDbTotalDeduction()));
		setDbStyleName(dbTotalDeductionLabel, totalDeductionLabel);

		totalLiquidLabel.setText(format(salaryDraftObject.getTotalLiquid()),
				displayChanges);
		dbTotalLiquidLabel
				.setText(format(salaryDraftObject.getDbTotalLiquid()));
		setDbStyleName(dbTotalLiquidLabel, totalLiquidLabel);

		clearDbWidgets();
		clearEventsTable();
		clearContextTable();
		clearPaymentsTable();

		initAvailablePayments();
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
			/*
			 * if (context.get(added).getScope().compareTo(step) < 0) {
			 * continue; }
			 */
			List<Variable> subContext = context.subList(added, context.size());
			boolean show = scope.compareTo(Scope.CONTRACT) >= 0;
			added += dumpContext(subContext, step, show);
		}

		List<Event> events = salaryDraftObject.getEvents();
		dumpEvents(events);

		eventsTableSpace.setVisible(eventsTable.getRowCount() > 0);

		dbSalaryCheck.setVisible(salaryDraftObject.hasDbSalary());
		setDbVisible(salaryDraftObject.hasDbSalary()
				&& dbSalaryCheck.getValue());
		acceptButton.setEnabled(salaryDraftObject.hasDrafts());
		undoButton.setEnabled(salaryDraftObject.canUndo());
		redoButton.setEnabled(salaryDraftObject.canRedo());
	}

	private void initAvailablePayments() {
		availablePaymens.clear();
		salaryDraftObject
				.getPaymentConcepts(new AsyncCallback<List<Payment>>() {
					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(List<Payment> result) {
						availablePaymens.addAll(result);
					}
				});
	}

	private void initAvailableEmbargoes() {
	}

	@UiHandler("fxButton")
	void onFxHelperMouseDown(MouseDownEvent event) {
		final FxDialog fxDialog = new FxDialog(salaryDraftObject);
		fxDialog.setExpression(fxhasValue.getValue());
		fxDialog.setWidth(Window.getClientWidth() / 2 + "px");
		fxDialog.center();
		fxDialog.show();

		fxDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				((Focusable) fxhasValue).setFocus(true);
				if (fxDialog.isAccepted()) {
					fxhasValue.setValue(fxDialog.getExpression());
				}
			}
		});
	}

	private void initDatesListBox() {
		datesListBox.addChangeHandler(new ChangeHandler() {

			PeriodDialog periodDialog = new PeriodDialog() {
				{
					setDateTimeFormat(DATE_SHORT);
				}

				@Override
				protected void onAccept() {
					if (getEndDate() == null) {
						salaryDraftObject.setDraftPeriod(getStartDate());
					} else {
						salaryDraftObject.setDraftPeriod(getStartDate(),
								getEndDate());
					}
					salaryDraftObject.calculate(SalaryDraft.this);
					syncDatesListBox();
				}
			};

			@Override
			public void onChange(ChangeEvent event) {

				int index = datesListBox.getSelectedIndex();
				String value = datesListBox.getValue(index);
				if (CUSTOM.equals(value)) {
					periodDialog.center();
					periodDialog.show();
				} else if (ONLY_THIS_MONTH.equals(value)) {
					salaryDraftObject.setDraftPeriod(null, null);
				} else if (FROM_THIS_MONTH.equals(value)) {
					salaryDraftObject.setDraftPeriod(null);
				} else {
					String dates[] = value.split("\\.\\.\\.");
					Date start = DATE_SHORT.parse(dates[0]);
					if (dates.length == 1) {
						salaryDraftObject.setDraftPeriod(start);
					} else {
						Date end = DATE_SHORT.parse(dates[1]);
						salaryDraftObject.setDraftPeriod(start, end);
					}
				}

				acceptButton.setEnabled(salaryDraftObject.hasDrafts());
			}
		});
	}

	@UiHandler("acceptButton")
	void onAcceptButtonClick(ClickEvent event) {
		salaryDraftObject.save(this);
	}

	@UiHandler("salaryButton")
	void onSalaryButtonClick(ClickEvent event) {
		salaryDraftObject.emitSalary(this);
	}

	private void syncDatesListBox() {

		Date draftStartDate = salaryDraftObject.getDraftStartDate();
		Date draftEndDate = salaryDraftObject.getDraftEndDate();

		Date date = salaryDraftObject.getStartDate();
		Date firstDayOfMonth = DateUtils.getFirstDayOfMonth(date);

		datesListBox
				.setItemText(
						0,
						"Este mes ( "
								+ AON.MONTH_FORMAT.format(firstDayOfMonth)
								+ " )");
		datesListBox.setItemText(1, "A partir de este mes ( "
				+ AON.MONTH_FORMAT.format(firstDayOfMonth) + "...)");

		if (DateUtils.equals(draftStartDate, firstDayOfMonth)) {
			if (draftEndDate == null) {
				datesListBox.setSelectedIndex(1);
				return;
			}
			Date lastDayOfMonth = DateUtils.getLastDayOfMonth(date);
			if (DateUtils.equals(draftEndDate, lastDayOfMonth)) {
				datesListBox.setSelectedIndex(0);
				return;
			}
		}

		StringBuffer buffer = new StringBuffer("");
		buffer.append(DATE_SHORT.format(draftStartDate));
		buffer.append("...");
		if (draftEndDate != null)
			buffer.append(DATE_SHORT.format(draftEndDate));

		datesListBox.setItemText(2, buffer.toString());
		datesListBox.setValue(2, buffer.toString());
		datesListBox.setSelectedIndex(2);

		if (datesListBox.getItemCount() < 4)
			datesListBox.addItem("Personalizado...", CUSTOM);
	}

	private void initSalaryDb() {
		dbSalaryCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				setDbVisible(event.getValue());
			}
		});
		dbUIObjects = new LinkedList<HasVisibility>();
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

		paymentsTable.getColumnFormatter().setWidth(0, "2%");
		paymentsTable.getColumnFormatter().setWidth(1, "12%"); // CUANTIA
		// 2 ...
		paymentsTable.getColumnFormatter().setWidth(3, "18%"); // DEVENGO
		paymentsTable.getColumnFormatter().setWidth(4, "12%"); // DEDUCCION
		paymentsTable.getColumnFormatter().setWidth(5, "2%");

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

	@UiHandler("undoButton")
	void onUndoButtonClick(ClickEvent event) {
		salaryDraftObject.undo();
		salaryDraftObject.calculate(SalaryDraft.this);
	}

	@UiHandler("redoButton")
	void onRedoButtonClick(ClickEvent event) {
		salaryDraftObject.redo();
		salaryDraftObject.calculate(SalaryDraft.this);
	}

	@UiHandler("printButton")
	void onPrintButtonClick(ClickEvent event) {
		if (isIrpfPreviewVisible())
			irpfPrint();
		else
			print();
	}

	// -------------------------------------------------------------------------

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

	private void clearDbWidgets() {
		dbUIObjects.clear();
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

	private void addDbWidget(HasVisibility widget) {
		dbUIObjects.add(widget);
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
			// dumpPayment(deduction, tr++, AON.AON_ICON_ROW_SELECTOR);
			if (payment.getAmount() != null) {
				dumpItem(payment, row++, AON.AON_ICON_ROW_SELECTOR,
						new PaymentChangeHandler<TextBox>(payment));
			} else {
				String styles[] = eventStyles.get(Event.Type.ERROR);
				dumpDbItem(payment, row++, styles[0], styles[1],
						new RecoverPaymentHandler(payment), false);
			}
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

		paymentsTable.setWidget(row, 0, newButton);
		paymentsTable.setHTML(row, 1, "&nbsp;");

		NewPaymentHandler newPaymentHandler = new NewPaymentHandler();
		MultiWordSuggestOracle paymentsOracle = new MultiWordSuggestOracle();
		SuggestBox descriptionBox = new SuggestBox(paymentsOracle,
				new TextBox(), newPaymentHandler);
		descriptionBox.getElement().getStyle().setWidth(98, Unit.PCT);
		paymentsTable.setWidget(row, 2, descriptionBox);
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
				if (deduction.getAmount() != null) {
					dumpSystemDeduction(deduction, description, row++);
				} else {
					String styles[] = eventStyles.get(Event.Type.ERROR);
					dumpDbSystemDeduction(deduction, description, row++,
							styles[0], styles[1]);
				}
			} else {
				if (deduction.getAmount() != null)
					dumpItem(deduction, row++, AON.AON_ICON_ROW_SELECTOR,
							new DeductionChangeHandler<TextBox>(deduction),
							true);
				else {
					String styles[] = eventStyles.get(Event.Type.ERROR);
					dumpDbItem(deduction, row++, styles[0], styles[1],
							new RecoverDeductionHandler(deduction), false);

				}

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
		handler.setEditButton(editButton);

		// remove 'aon-editDataTable-button' margin & paddind.
		// We don't like it here.
		paymentsTable.setWidget(row, 0, editButton);

		paymentsTable.setHTML(row, 1, "&nbsp;");

		TextBox descriptionBox = new TextBox();
		descriptionBox.setText(item.getDescription());
		descriptionBox.getElement().getStyle().setWidth(98, Unit.PCT);
		paymentsTable.setWidget(row, 2, descriptionBox);

		TextBox amountBox = new TextBox();
		String amount = format(item.getAmount());
		amountBox.setText(amount != null ? amount : item.getExpression());
		amountBox.getElement().getStyle().setWidth(98, Unit.PCT);
		amountBox.addStyleName(AON.AON_TEXT_RIGHT);

		InlineLabel dbAmountLabel = new InlineLabel();
		dbAmountLabel.setText(format(item.getDbAmount()));
		dbAmountLabel.setVisible(salaryDraftObject.hasDbSalary());
		dbAmountLabel.addStyleName(AON.AON_TEXT_RIGHT);
		setDbStyleName(dbAmountLabel, amountBox.getText(),
				dbAmountLabel.getText());

		HorizontalPanel amountsPanel = new HorizontalPanel();
		amountsPanel.setStyleName(AON.GWT_HORIZONTAL_PANEL);
		amountsPanel.add(amountBox);
		amountsPanel.add(dbAmountLabel);
		amountsPanel.setCellWidth(dbAmountLabel, "50%");
		amountsPanel.setCellHorizontalAlignment(dbAmountLabel,
				HorizontalAlignmentConstant.startOf(Direction.RTL));
		addDbWidget(new VisibilityImpl(dbAmountLabel.getElement()
				.getParentElement()));

		paymentsTable.setWidget(row, isDeduction ? 4 : 3, amountsPanel);

		handler.setDescriptionWidget(descriptionBox);
		handler.setExpressionWidget(amountBox);

		paymentsTable.setHTML(row, isDeduction ? 3 : 4, "&nbsp;");

		Button deleteButton = new Button();
		deleteButton.setStyleName(AON.AON_ICON_DELETE);
		deleteButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		paymentsTable.setWidget(row, 5, deleteButton);
		paymentsTable.getCellFormatter().addStyleName(row, 5,
				AON.AON_TEXT_RIGHT);
		handler.setDeleteButton(deleteButton);

		formatRow(row);
	}

	private <I extends Item> void dumpDbItem(I item, int row,
			String iconStyleName, String textStyleName, ClickHandler handler,
			boolean isDeduction) {

		// first cell for edit other stuff buttons.
		Button editButton = new Button();
		editButton.setStyleName(iconStyleName);
		editButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		// remove 'aon-editDataTable-button' margin & paddind.
		// We don't like it here.
		paymentsTable.setWidget(row, 0, editButton);

		paymentsTable.setHTML(row, 1, "&nbsp;");

		Label descriptionLabel = new InlineLabel(item.getDescription());
		// descriptionLabel.getElement().getStyle().setWidth(98, Unit.PCT);
		paymentsTable.setWidget(row, 2, descriptionLabel);

		InlineLabel dbAmountLabel = new InlineLabel();
		dbAmountLabel.setText(format(item.getDbAmount()));
		dbAmountLabel.setVisible(salaryDraftObject.hasDbSalary());
		dbAmountLabel.addStyleName(AON.AON_TEXT_RIGHT);
		setDbStyleName(dbAmountLabel, null, dbAmountLabel.getText());

		HorizontalPanel amountsPanel = new HorizontalPanel();
		amountsPanel.setStyleName(AON.GWT_HORIZONTAL_PANEL);
		amountsPanel.setWidth("100%");
		amountsPanel.add(new InlineLabel());
		amountsPanel.add(dbAmountLabel);
		amountsPanel.setCellWidth(dbAmountLabel, "50%");
		amountsPanel.setCellHorizontalAlignment(dbAmountLabel,
				HorizontalAlignmentConstant.startOf(Direction.RTL));

		paymentsTable.setWidget(row, isDeduction ? 4 : 3, amountsPanel);

		paymentsTable.setHTML(row, isDeduction ? 3 : 4, "&nbsp;");

		// paymentsTable.getFlexCellFormatter().setColSpan(row, 4, 2);

		Button recoverButton = new Button();
		recoverButton.setStyleName(AON.AON_ICON_CANCEL);
		recoverButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		paymentsTable.setWidget(row, 5, recoverButton);
		paymentsTable.getCellFormatter().addStyleName(row, 5,
				AON.AON_TEXT_RIGHT);
		recoverButton.addClickHandler(handler);

		CellFormatter fomatter = paymentsTable.getCellFormatter();
		for (int col = 0; col < paymentsTable.getCellCount(row); col++) {
			fomatter.addStyleName(row, col, textStyleName);
		}

		formatRow(row);

		addDbWidget(new VisibilityImpl(paymentsTable.getRowFormatter()
				.getElement(row)));
	}

	private void dumpSystemDeduction(Deduction deduction, String description,
			int row) {

		paymentsTable.setHTML(row, 0, "&nbsp;");
		paymentsTable.setText(row, 1, deduction.getDescription());
		paymentsTable.getCellFormatter().addStyleName(row, 0,
				AON.AON_TEXT_CENTER);
		paymentsTable.setHTML(row, 2, description);

		HorizontalPanel amountsPanel = new HorizontalPanel();
		amountsPanel.setStyleName(AON.GWT_HORIZONTAL_PANEL);
		InlineLabel amountLabel = new InlineLabel();
		amountLabel.setText(format(deduction.getAmount()));

		InlineLabel dbAmountLabel = new InlineLabel();
		dbAmountLabel.setText(format(deduction.getDbAmount()));
		dbAmountLabel.setVisible(salaryDraftObject.hasDbSalary());
		setDbStyleName(dbAmountLabel, amountLabel);
		amountsPanel.add(amountLabel);
		amountsPanel.add(dbAmountLabel);

		amountsPanel.setWidth("100%");
		amountsPanel.setCellWidth(dbAmountLabel, "50%");
		amountsPanel.setCellHorizontalAlignment(amountLabel,
				HorizontalAlignmentConstant.startOf(Direction.RTL));
		amountsPanel.setCellHorizontalAlignment(dbAmountLabel,
				HorizontalAlignmentConstant.startOf(Direction.RTL));
		addDbWidget(new VisibilityImpl(dbAmountLabel.getElement()
				.getParentElement()));

		paymentsTable.setWidget(row, 4, amountsPanel);

		paymentsTable.getCellFormatter().addStyleName(row, 4,
				AON.AON_TEXT_RIGHT);
		paymentsTable.getFlexCellFormatter().setColSpan(row, 4, 2);

		formatRow(row);
	}

	private void dumpDbSystemDeduction(Deduction deduction, String description,
			int row, String iconStyleName, String textStyleName) {
		dumpSystemDeduction(deduction, description, row);

		// first cell for edit other stuff buttons.
		Button iconButton = new Button();
		iconButton.setStyleName(iconStyleName);
		iconButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		// remove 'aon-editDataTable-button' margin & paddind.
		// We don't like it here.
		paymentsTable.setWidget(row, 0, iconButton);

		CellFormatter fomatter = paymentsTable.getCellFormatter();
		for (int col = 0; col < paymentsTable.getCellCount(row); col++) {
			fomatter.addStyleName(row, col, textStyleName);
		}

	}

	private int dumpContext(List<Variable> context, Scope to, boolean show) {
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

			VariableChangeHandler<TextBox> variableChangeHandler = new VariableChangeHandler<TextBox>(
					variable);

			Label label = getLabel(variable);
			htmlPanel.add(label);
			variableChangeHandler.setLabel(label);

			Panel valuePanel = new HorizontalPanel();
			valuePanel.setStyleName(AON.GWT_HORIZONTAL_PANEL);

			TextBox variableTextBox = new TextBox();
			variableChangeHandler.setUiObject(variableTextBox);
			valuePanel.add(variableTextBox);

			valuePanel.add(new InlineHTML("&nbsp;"));

			if (!(variable instanceof UndefinedVariable)) {
				valuePanel.add(getDeleteButton(variable));
			} else if (variable instanceof UndefinedPaymentVariable) {
				String styles[] = eventStyles.get(Event.Type.WARNING);

				StyleToggleButton itemButton = getPaymentButton(
						(UndefinedPaymentVariable) variable, styles[0],
						styles[1]);

				valuePanel.add(itemButton);
				itemButton.setValue(show, true);
			} else if (variable instanceof UndefinedDeductionVariable) {
				String styles[] = eventStyles.get(Event.Type.WARNING);

				StyleToggleButton itemButton = getDeductionButton(
						(UndefinedDeductionVariable) variable, styles[0],
						styles[1]);

				valuePanel.add(itemButton);
				itemButton.setValue(show, true);
			}

			if (variable.getScope().compareTo(Scope.AGREEMENT) > 0
					&& variable.isDefinedAt(Scope.AGREEMENT))
				valuePanel.add(getAgreementVarButton(variable));
			if (variable.getScope().compareTo(Scope.APPLICATION) > 0
					&& (variable.isDefinedAt(Scope.SYSTEM) || variable
							.isDefinedAt(Scope.APPLICATION)))
				valuePanel.add(getSystemVarButton(variable));

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
			expandPanel.setStyleName(AON.GWT_HORIZONTAL_PANEL);
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
					dumpContext(remainContext, scope, false);
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

	private void irpfPrint() {

		salaryDraftObject.downloadIrpf("application/pdf",
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

	private void irpfPreview() {
		showIrpfPreview();
		getIrpfPreview();
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

	private void getIrpfPreview() {
		salaryDraftObject.getIrpfAsHTML(zoom, new AsyncCallback<String>() {

			@Override
			public void onSuccess(String result) {
				irpfPreviewHTML.setHTML(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO
				irpfPreviewHTML.setHTML(caught.getMessage());
			}
		});
	}

	private String format(Double amount) {
		return amount == null ? null : AON.CURRENCY_FORMAT.format((double) Math
				.round(amount * 1000.00) / 1000.00);
	}

	private void setDbStyleName(Label l2, HasText l1) {
		setDbStyleName(l2, l1.getText(), l2.getText());
	}

	private void setDbStyleName(Widget widget, String s1, String s2) {

		if (StringUtils.equals(s1, s2)) {
			widget.removeStyleName(style.textError());
			widget.addStyleName(style.textOk());
		} else {
			widget.removeStyleName(style.textOk());
			widget.addStyleName(style.textError());
		}
	}

	private String format(Date date) {
		return date == null ? null : DATE_FORMAT.format(date);
	}

	private Label getLabel(Variable variable) {
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

	private Button getSystemVarButton(Variable variable) {
		Button sysButton = new Button();
		sysButton.setStyleName(AON.AON_ICON_CONFIG);
		sysButton.setStyleName(AON.AON_NO_MARGIN, true);
		sysButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		sysButton.addClickHandler(new AbstractVarHandler(variable) {
			@Override
			String getExpression(Variable var) {
				return "SISTEMA('" + var.getName() + "')";
			}
		});
		return sysButton;
	}

	private Button getAgreementVarButton(Variable variable) {
		Button agreementButton = new Button();
		agreementButton.setStyleName(AON.AON_ICON_AGREEMENT);
		agreementButton.setStyleName(AON.AON_NO_MARGIN, true);
		agreementButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		agreementButton.addClickHandler(new AbstractVarHandler(variable) {
			@Override
			String getExpression(Variable var) {
				return "CONVENIO('" + var.getName() + "')";
			}
		});
		return agreementButton;
	}

	private Button getDeleteButton(Variable variable) {
		Button deleteButton = new Button();
		deleteButton.setStyleName(AON.AON_ICON_DELETE);
		deleteButton.setStyleName(AON.AON_NO_MARGIN, true);
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

		idx += salaryDraftObject.getPayments().size() + 3; // We add one due to
															// header
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
