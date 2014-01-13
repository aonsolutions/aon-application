package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.esferalia.aon.gwt.payroll.client.AgreementDraftObject.CalculateCallback;
import com.esferalia.aon.gwt.payroll.client.FxDialog.IContextProvider;
import com.esferalia.aon.gwt.payroll.shared.AgreementDraft.Level;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.DateTimeFormatException;
import com.esferalia.aon.gwt.payroll.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.EmptyStringException;
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.HasDescription;
import com.esferalia.aon.gwt.payroll.shared.ItemComparator;
import com.esferalia.aon.gwt.payroll.shared.LevelComparator;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.StringVariable;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.user.datepicker.client.DateBox;

public class AgreementDraft extends ResizeComposite implements
		CalculateCallback {

	static int SALARY_TABLE_COLS = 7;
	static int SALARY_TABLE_LINES = 7;
	static int VARIABLE_TEXTBOX_SIZE = 10;

	public static final String CUSTOM = "CUSTOM";
	public static final String ALWAYS = "ALWAYS";
	public static final String ONLY_THIS_YEAR = "ONLY_THIS_YEAR";
	public static final String ONLY_THIS_MONTH = "ONLY_THIS_MONTH";
	public static final String FROM_THIS_MONTH = "FROM_THIS_MONTH";

	static class TypeListBox<T extends Enum<?> & HasDescription> extends
			ListBox {

		private Class<T> type;

		public TypeListBox(Class<T> type) {
			this(type, 20);

		}

		public TypeListBox(Class<T> type, int size) {
			this.type = type;
			loadTypeItems(size);
		}

		public TypeListBox(Class<T> type, String nullItem) {
			this(type, nullItem, 20);

		}

		public TypeListBox(Class<T> type, String nullItem, int size) {
			this.type = type;
			addItem(nullItem);
			loadTypeItems(size);
		}

		public T getSelected() {
			T types[] = type.getEnumConstants();
			int ordinal = getSelectedIndex() - (getItemCount() - types.length);
			return ordinal < 0 ? null : types[ordinal];
		}

		public void setSelected(T t) {
			T types[] = type.getEnumConstants();
			int index = (t == null ? -1 : t.ordinal())
					+ (getItemCount() - types.length);
			setSelectedIndex(index);
		}

		private void loadTypeItems(int size) {
			for (T t : type.getEnumConstants()) {
				String description = t.getDescription();

				if (description.length() > size)
					description = description.substring(0, size - 3) + "...";

				addItem(description, t.name());
			}
		}

	}

	interface MyStyle extends CssResource {
		@ClassName("icon-warn")
		String iconWarn();

		String highlight();
	}

	interface Binder extends UiBinder<Widget, AgreementDraft> {

	}

	private static final Binder binder = GWT.create(Binder.class);

	private class UndoListener implements UndoManager.Listener {

		@Override
		public void onChange(UndoManager undoManager) {
			enableUndoRedoButtons();
			// acceptButton.setEnabled(agreementDraftObject.hasDrafts());
		}

	}

	private class ExtraEditor {
		Extra extra;

		public ExtraEditor(Extra extra) {
			this.extra = extra;
		}

		void setDeleteButton(HasClickHandlers button) {
			button.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					extra.setIssueDate("REMOVE()");
					AgreementDraft.this.agreementDraftObject
							.addDraftExtra(extra);
					;
					AgreementDraft.this.calculate();
				}
			});
		}

		void setStartDateBox(DateBox dateBox) {
			dateBox.addValueChangeHandler(new ValueChangeHandler<Date>() {
				@Override
				public void onValueChange(ValueChangeEvent<Date> event) {
					extra.setStartDate(AgreementDraft.this
							.formatExtraDate(event.getValue()));
					AgreementDraft.this.agreementDraftObject
							.addDraftExtra(extra);
					AgreementDraft.this.calculate();
				}
			});
		}

		void setEndDateBox(DateBox dateBox) {
			dateBox.addValueChangeHandler(new ValueChangeHandler<Date>() {
				@Override
				public void onValueChange(ValueChangeEvent<Date> event) {
					extra.setEndDate(AgreementDraft.this.formatExtraDate(event
							.getValue()));
					AgreementDraft.this.agreementDraftObject
							.addDraftExtra(extra);
					AgreementDraft.this.calculate();
				}
			});

		}

		void setIssueDateBox(DateBox dateBox) {
			dateBox.addValueChangeHandler(new ValueChangeHandler<Date>() {
				@Override
				public void onValueChange(ValueChangeEvent<Date> event) {
					extra.setIssueDate(AgreementDraft.this
							.formatExtraDate(event.getValue()));
					AgreementDraft.this.agreementDraftObject
							.addDraftExtra(extra);
					AgreementDraft.this.calculate();
				}
			});
		}

		void setPaymentListBox(final ListBox listBox) {
			listBox.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					String value = listBox.getValue(listBox.getSelectedIndex());
					extra.setPaymentId(value == null ? null : Integer
							.valueOf(value));
					AgreementDraft.this.agreementDraftObject
							.addDraftExtra(extra);
					AgreementDraft.this.calculate();
				}
			});
		}

	}

	private class LevelEditor {
		Level level;

		public LevelEditor(Level level) {
			this.level = level;
		}

		void setDescriptionTextBox(TextBox textBox) {
			textBox.addValueChangeHandler(new ValueChangeHandler<String>() {
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					level.setDescription(event.getValue());
					AgreementDraft.this.agreementDraftObject
							.addDraftLevel(level);
					AgreementDraft.this.calculate();
				}
			});
		}

		void setDeleteButton(HasClickHandlers button) {
			button.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					level.setDescription("REMOVE()");
					AgreementDraft.this.agreementDraftObject
							.addDraftLevel(level);
					AgreementDraft.this.calculate();
				}
			});
		}

	}

	private class CategoriesEditor {
		Level level;

		public CategoriesEditor(Level level) {
			this.level = level;
		}

		void setCategoriesTextBox(TextBox textBox) {
			textBox.addValueChangeHandler(new ValueChangeHandler<String>() {
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					String text = event.getValue();
					AgreementDraft.this.agreementDraftObject
							.addDraftCategories(level, text);
					// TODO: really need to go server side.
					AgreementDraft.this.calculate();
				}
			});
		}

	}

	private class VariableEditor {

		Level level;
		Variable var;

		VariableEditor(Level level, Variable var) {
			this.var = var;
			this.level = level;
		}

		void setExpressionTextBox(TextBox textBox) {
			textBox.addFocusHandler(new FocusHandler() {
				@Override
				public void onFocus(FocusEvent event) {
					AgreementDraft.this.fxButton.setEnabled(true);
				}
			});
			textBox.addBlurHandler(new BlurHandler() {
				@Override
				public void onBlur(BlurEvent event) {
					AgreementDraft.this.fxButton.setEnabled(false);
				}
			});
			textBox.addValueChangeHandler(new ValueChangeHandler<String>() {
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					var.setExpression(event.getValue());
					// TODO: Check syntax????
					AgreementDraft.this.agreementDraftObject.addDraftVariable(
							level, var);
				}
			});
		}

	}

	private class PaymentEditor {

		Payment payment;
		TextBox expressionBox;
		TypeListBox<Payment.Type> typeListBox;
		TypeListBox<Salary.Type> salaryTypeListBox;

		PaymentEditor(Payment payment) {
			this.payment = payment;
		}

		Payment getConcept() {
			if (payment.getName() == null)
				return null;
			for (Payment concept : AgreementDraft.this.availablePaymens)
				if (StringUtils.equals(payment.getName(), concept.getName()))
					return concept;
			return null;
		}

		// --------------------------------------------------------------------
		//
		// --------------------------------------------------------------------

		void setEditButton(HasClickHandlers button) {

			class EditHandler implements ClickHandler, PaymentDialog.Callback {
				@Override
				public void onClick(ClickEvent event) {
					PaymentDialog dialog = new PaymentDialog();
					dialog.setNumberFormat(AON.CURRENCY_FORMAT);
					dialog.setConcept(PaymentEditor.this.getConcept());
					dialog.setContextProvider(agreementDraftObject);
					dialog.setMonth(payment.getMonth());
					dialog.setType(payment.getType());
					dialog.setReceiptType(payment.getSalaryType());
					dialog.setDescription(payment.getDescription());
					dialog.setPaymentExpression(payment.getExpression()); //
					dialog.setIrpfExpression(payment.getIrpfExpression());
					dialog.setQuoteExpression(payment.getQuoteExpression());

					dialog.center();
					dialog.show(this);
				}

				// ------------------------------------------------------------
				// PaymentDialog.Callback methods.
				// ------------------------------------------------------------
				@Override
				public void onAccept(PaymentDialog dialog) {
					payment.setType(dialog.getType());
					payment.setMonth(dialog.getMonth());
					payment.setDescription(dialog.getDescription());
					payment.setExpression(dialog.getPaymentExpression());
					payment.setIrpfExpression(dialog.getIrpfExpression());
					payment.setQuoteExpression(dialog.getQuoteExpression());
					
					AgreementDraft.this.agreementDraftObject
							.addDraftPayment(payment);
					AgreementDraft.this.calculate();
				}


			}

			button.addClickHandler(new EditHandler());
		}

		void setDeleteButton(HasClickHandlers button) {
			button.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					payment.setExpression("REMOVE()");
					AgreementDraft.this.agreementDraftObject
							.addDraftPayment(payment);
					AgreementDraft.this.calculate();
				}
			});
		}

		void setPaymentTypeListBox(TypeListBox<Payment.Type> listBox) {
			this.typeListBox = listBox;
			this.typeListBox.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					payment.setType(PaymentEditor.this.typeListBox
							.getSelected());
				}
			});
		}

		void setSalaryTypeListBox(TypeListBox<Salary.Type> listBox) {
			this.salaryTypeListBox = listBox;
			this.salaryTypeListBox.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					payment.setSalaryType(PaymentEditor.this.salaryTypeListBox
							.getSelected());
					AgreementDraft.this.agreementDraftObject
							.addDraftPayment(payment);
					AgreementDraft.this.calculate();
				}
			});
		}

		void setExpressionTextBox(TextBox textBox) {
			this.expressionBox = textBox;
			this.expressionBox.addFocusHandler(new FocusHandler() {
				@Override
				public void onFocus(FocusEvent event) {
					AgreementDraft.this.fxButton.setEnabled(true);
				}
			});

			this.expressionBox.addBlurHandler(new BlurHandler() {
				@Override
				public void onBlur(BlurEvent event) {
					AgreementDraft.this.fxButton.setEnabled(false);
				}
			});
			this.expressionBox
					.addValueChangeHandler(new ValueChangeHandler<String>() {
						@Override
						public void onValueChange(ValueChangeEvent<String> event) {
							payment.setExpression(event.getValue());
							AgreementDraft.this.agreementDraftObject
									.addDraftPayment(payment);
							AgreementDraft.this.calculate();
						}
					});
		}

		void setDescriptionTextBox(TextBox textBox) {
			textBox.addValueChangeHandler(new ValueChangeHandler<String>() {
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					payment.setDescription(event.getValue());
				}
			});
		}

		void setDescriptionSuggestBox(SuggestBox suggestBox) {
			suggestBox
					.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {

						@Override
						public void onSelection(SelectionEvent<Suggestion> event) {
							String description = event.getSelectedItem()
									.getReplacementString();
							Payment concept = getPayment(description);
							if (concept == null)
								return;

							payment.setType(concept.getType());
							payment.setName(concept.getName());
							payment.setConceptId(concept.getId());
							payment.setDescription(concept.getDescription());
							payment.setIrpfExpression(concept
									.getIrpfExpression());
							payment.setQuoteExpression(concept
									.getQuoteExpression());

							if (concept.getExpression() != null) {
								payment.setExpression(concept.getExpression());
								AgreementDraft.this.agreementDraftObject
										.addDraftPayment(payment);
								AgreementDraft.this.calculate();
							} else {
								typeListBox.setSelected(payment.getType());
							}

						}
					});
		}

	}

	private class PaymentSuggestionDisplay extends
			AbstractItemSuggestionDisplay<Payment> {

		@Override
		Payment getItem(String replacementString) {
			for (Payment payment : availablePaymens) {
				String suggestion = getSuggestionString(payment);
				if (StringUtils.equals(replacementString, suggestion))
					return payment;
			}
			return null;
		}
	}

	@UiField
	MyStyle style;

	@UiField
	Button fxButton;

	@UiField
	Button undoButton;

	@UiField
	Button redoButton;

	@UiField
	Button acceptButton;

	@UiField
	ListBox datesListBox;

	@UiField
	FlexTable salaryTable;

	@UiField
	FlexTable extrasTable;

	@UiField
	FlexTable paymentsTable;

	@UiField
	ScrollPanel mainScrollPane;

	@UiField
	TextBox descriptionTextBox;

	@UiField
	MonthListBox draftMonthListBox;

	@UiField
	ScrollPanel salaryTableScrollPane;

	// Stuff for a properly built salary table.
	// Head, first column, and last column frozen.
	Element salaryTableHead;
	Element salaryTableFirstColumn;
	Element salaryTableLastColumn;
	Element salaryTableUpperLeftCorner;
	Element salaryTableUpperRightCorner;

	AgreementDraftObject agreementDraftObject;

	UndoListener undoListener;
	List<Payment> availablePaymens;
	MultiWordSuggestOracle paymentDescriptionOracle;
	PaymentSuggestionDisplay paymentSuggestionDisplay;

	private List<Integer> changedLevelsRows;
	private List<Integer> changedVariablesCols;

	public AgreementDraft() {
		initWidget(binder.createAndBindUi(this));
		initPaymentsTable();
		initExtrasTable();
		undoListener = new UndoListener();
		availablePaymens = new ArrayList<Payment>();
		changedLevelsRows = new LinkedList<Integer>();
		changedVariablesCols = new LinkedList<Integer>();
		paymentDescriptionOracle = new MultiWordSuggestOracle();
		paymentSuggestionDisplay = new PaymentSuggestionDisplay();
	}

	@Override
	public void onResize() {
		if (salaryTableUpperLeftCorner != null) {
			moveSalaryTableFrozenColsAndRows();
		}
		super.onResize();
	}

	public void setAgreementDraftObject(
			AgreementDraftObject agreementDraftObject) {
		if (this.agreementDraftObject != null) {
			agreementDraftObject.removeListener(undoListener);
		}
		this.agreementDraftObject = agreementDraftObject;
		this.agreementDraftObject.calculate(this);
		enableUndoRedoButtons();
		this.agreementDraftObject.addListener(undoListener);

		setDescription();

	}

	@Override
	public void onCalculateFailure(Throwable throwable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCalculateSucces(AgreementDraftObject object) {

		draftMonthListBox.setHighLightMonths(agreementDraftObject
				.getDatesWithChanges());
		draftMonthListBox.setSelectedMonth(agreementDraftObject.getStartDate());
		syncDatesListBox();

		loadAvailablePayments();

		setDescription();

		clearSalaryTable();
		dumpSalaryTable();
		insertNewLevelRow(salaryTable.getRowCount());
		initSalaryTableFrozenColsAndRows();

		clearPaymentsTable();
		dumpPayments();
		insertNewPaymentRow(paymentsTable.getRowCount());

		clearExtrasTable();
		SortedSet<Payment> extraPayments = getAvailableExtraPayments();
		dumpExtras(extraPayments);
		insertNewExtraRow(extrasTable.getRowCount(), extraPayments);

	}

	// ------------------------------------------
	// Handlers
	// ------------------------------------------

	@UiHandler("redoButton")
	void onRedoClick(ClickEvent event) {
		agreementDraftObject.redo();
		calculate();
	}

	@UiHandler("undoButton")
	void onUndoClick(ClickEvent event) {
		agreementDraftObject.undo();
		calculate();
	}

	@UiHandler("acceptButton")
	void onAcceptClick(ClickEvent event) {
		agreementDraftObject.save(this);
	}

	@UiHandler("datesListBox")
	void onChangeDateListBox(ChangeEvent event) {
		int index = datesListBox.getSelectedIndex();
		String value = datesListBox.getValue(index);

		if (CUSTOM.equals(value)) {
		} else if (ALWAYS.equals(value)) {
			agreementDraftObject.setDraftPeriod(getFirstDateWithChanges());
		} else if (FROM_THIS_MONTH.equals(value)) {
			agreementDraftObject.setDraftPeriod(null);
		} else if (ONLY_THIS_YEAR.equals(value)) {
			Date month = draftMonthListBox.getSelected();
			Date firstDayOfYear = DateUtils.getFirstDayOfYear(month);
			Date lastDayOfYear = DateUtils.getLastDayOfYear(month);
			agreementDraftObject.setDraftPeriod(firstDayOfYear, lastDayOfYear);
		} else if (ONLY_THIS_MONTH.equals(value)) {
			agreementDraftObject.setDraftPeriod(null, null);
		}

	}

	@UiHandler("fxButton")
	void onFxClicked(ClickEvent event) {
		FxDialog fxDialog = new FxDialog(agreementDraftObject);
		fxDialog.setExpression("");
		fxDialog.center();
		fxDialog.show();
	}

	@UiHandler("descriptionTextBox")
	void onDescriptionValueChange(ValueChangeEvent<String> event) {
		agreementDraftObject.setDescription(event.getValue());
	}

	@UiHandler("salaryTableScrollPane")
	void onSalaryTableScroll(ScrollEvent event) {
		moveSalaryTableFrozenColsAndRows();
	}

	@UiHandler("mainScrollPane")
	void onMainScroll(ScrollEvent event) {
		moveSalaryTableFrozenColsAndRows();
	}

	@UiHandler("draftMonthListBox")
	void onDraftMonthListBoxChanged(ChangeEvent event) {
		Date month = draftMonthListBox.getSelectedMonth();
		agreementDraftObject.setStartDate(DateUtils.getFirstDayOfMonth(month));
		agreementDraftObject.setEndDate(DateUtils.getLastDayOfMonth(month));
		calculate();
	}

	// ------------------------------------------------------------------------
	//
	// ------------------------------------------------------------------------

	private void calculate() {
		agreementDraftObject.calculate(this);
	}

	private void setDescription() {
		// TODO: When null it will be desirable warn user.
		String description = this.agreementDraftObject.getDescription();
		descriptionTextBox.setText(description == null ? "" : description);

	}

	private void dumpExtras(SortedSet<Payment> payments) {

		int row = extrasTable.getRowCount();

		for (Extra extra : agreementDraftObject.getExtras())
			dumpExtra(extra, row++, payments);
	}

	private void dumpPayments() {

		int row = paymentsTable.getRowCount();

		SortedSet<Payment> payments = new TreeSet<Payment>(new ItemComparator());
		payments.addAll(agreementDraftObject.getPayments());
		for (Payment payment : payments) {
			dumpPayment(payment, row++);
		}

	}

	private void dumpSalaryTable() {

		RowFormatter rowFormatter = salaryTable.getRowFormatter();
		CellFormatter cellFormatter = salaryTable.getCellFormatter();

		salaryTable.setText(0, 0, "NIVEL");
		cellFormatter.addStyleName(0, 0, AON.AON_BOLD);
		cellFormatter.addStyleName(0, 0, AON.AON_TEXT_CENTER);
		cellFormatter.addStyleName(0, 0, AON.AON_INPUT_REQUIRED);

		int col = 1;

		SortedSet<String> variables = new TreeSet<String>();
		variables.addAll(agreementDraftObject.getVariables());

		Set<String> changedVariables = agreementDraftObject
				.getChangedVariables();

		for (String var : variables) {
			salaryTable.setText(0, col, var);
			cellFormatter.addStyleName(0, col, AON.AON_BOLD);
			cellFormatter.addStyleName(0, col, AON.AON_TEXT_CENTER);
			if (changedVariables.contains(var)) {
				cellFormatter.addStyleName(0, col, style.highlight());
				changedVariablesCols.add(col);
			}
			col++;
		}

		salaryTable.setText(0, col, "CATEGORIAS");
		cellFormatter.addStyleName(0, col, AON.AON_BOLD);
		cellFormatter.addStyleName(0, col, AON.AON_TEXT_CENTER);
		cellFormatter.addStyleName(0, col, AON.AON_INPUT_REQUIRED);
		cellFormatter.addStyleName(0, col, "aon-width-all"); // fill remain
																// space
		col++;

		salaryTable.setHTML(0, col, "&nbsp;");

		SortedSet<Level> levels = new TreeSet<Level>(new LevelComparator());
		levels.addAll(agreementDraftObject.getLevels());

		Set<Level> changedLevels = agreementDraftObject.getChangedLevels();

		int row = 1;
		for (Level level : levels) {

			TextBox descriptionTextBox = new TextBox();
			descriptionTextBox.setText(level.getDescription());
			descriptionTextBox.setVisibleLength(5);
			hide(descriptionTextBox, level.getId() == 0);
			salaryTable.setWidget(row, 0, descriptionTextBox);
			LevelEditor editor = new LevelEditor(level);
			editor.setDescriptionTextBox(descriptionTextBox);

			cellFormatter.addStyleName(row, 0, AON.AON_BOLD);

			if (changedLevels.contains(level)) {
				changedLevelsRows.add(row);
				rowFormatter.addStyleName(row, style.highlight());
			}

			row++;
		}

		row = 1;
		for (Level level : levels) {
			col = 1;
			for (String var : variables) {
				Variable variable = agreementDraftObject
						.getVariable(level, var);
				if (variable != null) {
					dumpVariable(row, col, level, variable);
				} else {
					dumpUndefVariable(row, col, level, var);
				}
				if (changedVariables.contains(var)) {
					cellFormatter.addStyleName(row, col, style.highlight());
				}
				col++;
			}

			Widget categoriesWidget = dumpCategories(row, col++, level,
					agreementDraftObject.getCategories(level));

			hide(categoriesWidget, level.getId() == 0);

			Button deleteButton = new Button();
			deleteButton.setStyleName(AON.AON_ICON_DELETE);
			deleteButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);

			LevelEditor editor = new LevelEditor(level);
			editor.setDeleteButton(deleteButton);

			hide(deleteButton, level.getId() == 0);

			salaryTable.setWidget(row, col++, deleteButton);

			row++;
		}

		// Set max width...
		int offsetWidth = 1;
		offsetWidth += cellFormatter.getElement(0, 0).getOffsetWidth();

		// Asume that each column have 1.5 width of level column that has an
		// input with size 5
		offsetWidth += offsetWidth * 1.5 * (SALARY_TABLE_COLS - 1);

		offsetWidth += cellFormatter.getElement(0,
				salaryTable.getCellCount(0) - 1).getOffsetWidth();

		salaryTableScrollPane.setWidth(offsetWidth + "px");

	}

	private void initSalaryTableFrozenColsAndRows() {

		// Set max heigth...
		int offsetHeight = 1;
		offsetHeight += salaryTable.getCellFormatter().getElement(1, 0)
				.getOffsetHeight()
				* SALARY_TABLE_LINES;
		salaryTableScrollPane.setHeight(offsetHeight + "px");

		salaryTableHead = getFreezeTableHead(salaryTable);
		salaryTableUpperLeftCorner = getFreezeTableUpperLeftCorner(salaryTable);
		salaryTableUpperRightCorner = getFreezeTableUpperRightCorner(
				salaryTable, salaryTableScrollPane);
		salaryTableFirstColumn = getFreezeTableFirstCol(salaryTable);
		salaryTableLastColumn = getFreezeTableLastCol(salaryTable,
				salaryTableScrollPane);

		Element scroller = salaryTableScrollPane.getElement();
		DOM.appendChild(scroller, salaryTableHead);
		DOM.appendChild(scroller, salaryTableFirstColumn);
		DOM.appendChild(scroller, salaryTableLastColumn);
		DOM.appendChild(scroller, salaryTableUpperLeftCorner);
		DOM.appendChild(scroller, salaryTableUpperRightCorner);

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {

				int width = salaryTableScrollPane.getElement().getClientWidth();
				int height = salaryTableScrollPane.getElement()
						.getClientHeight();

				toFixedPosition(salaryTableUpperLeftCorner, width, height);
				toFixedPosition(salaryTableUpperRightCorner, width, height);
				toFixedPosition(salaryTableHead, width
						- salaryTableUpperLeftCorner.getOffsetWidth(), height);
				toFixedPosition(salaryTableFirstColumn, width, height
						- salaryTableUpperLeftCorner.getOffsetHeight());
				toFixedPosition(salaryTableLastColumn, width, height
						- salaryTableUpperLeftCorner.getOffsetHeight());
				moveSalaryTableFrozenColsAndRows();
				ensureChangesVisible();
			}
		});
	}

	private void loadAvailablePayments() {
		availablePaymens.clear();
		agreementDraftObject
				.getPaymentConcepts(new AsyncCallback<List<Payment>>() {
					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(List<Payment> result) {
						availablePaymens.addAll(result);
						paymentDescriptionOracle.clear();
						for (Payment payment : availablePaymens) {
							paymentDescriptionOracle
									.add(getSuggestionString(payment));
						}
					}
				});
	}

	private Payment getPayment(String description) {
		for (Payment payment : availablePaymens) {
			if (StringUtils.equals(description, payment.getDescription()))
				return payment;
		}
		return null;
	}

	private void syncDatesListBox() {

		Date draftStartDate = agreementDraftObject.getDraftStartDate();
		Date draftEndDate = agreementDraftObject.getDraftEndDate();

		Date startDate = agreementDraftObject.getStartDate();

		// clear selection.
		datesListBox.setSelectedIndex(-1);

		int alwaysIndex = 0;

		for (int i = 0; i < datesListBox.getItemCount(); i++) {
			String value = datesListBox.getValue(i);
			String text = datesListBox.getItemText(i);
			text = text.replaceAll(" \\([^\\)]*\\)", "");
			if (ALWAYS.equals(value)) {
				alwaysIndex = i;
				DateTimeFormat format = DateTimeFormat
						.getFormat(PredefinedFormat.YEAR_MONTH_NUM_DAY);
				Date alwaysDate = getFirstDateWithChanges();
				datesListBox.setItemText(i,
						text + " ( " + format.format(alwaysDate) + "... )");
				if (alwaysDate.equals(draftStartDate) && draftEndDate == null) {
					datesListBox.setSelectedIndex(i);
				}

			} else if (ONLY_THIS_YEAR.equals(value)) {
				DateTimeFormat format = DateTimeFormat
						.getFormat(PredefinedFormat.YEAR);
				datesListBox.setItemText(i,
						text + " ( " + format.format(startDate) + " )");
				if (DateUtils.isFirstDayOfYear(draftStartDate)
						&& DateUtils.isLastDayOfYear(draftEndDate)) {
					datesListBox.setSelectedIndex(i);
				}

			} else if (FROM_THIS_MONTH.equals(value)) {
				DateTimeFormat format = DateTimeFormat
						.getFormat(PredefinedFormat.YEAR_MONTH_NUM);
				datesListBox.setItemText(i,
						text + " ( " + format.format(startDate) + "... )");
				if (draftStartDate.equals(startDate) && draftEndDate == null) {
					datesListBox.setSelectedIndex(i);
				}
			}
		}
		// Window.alert(datesListBox.getSelectedIndex() + " " + draftStartDate +
		// "..." + ( draftEndDate == null ? "" : draftEndDate ));

		if (datesListBox.getSelectedIndex() == -1) {
			datesListBox.setSelectedIndex(alwaysIndex);
			agreementDraftObject.setDraftPeriod(getFirstDateWithChanges());
		}

	}

	private Date getFirstDateWithChanges() {
		Date firstDateWithChanges = agreementDraftObject.getStartDate();
		Set<Date> datesWithChanges = agreementDraftObject.getDatesWithChanges();
		if (datesWithChanges == null)
			return DateUtils.copyDateOnly(firstDateWithChanges);

		for (Date date : datesWithChanges) {
			if (date.before(firstDateWithChanges))
				firstDateWithChanges = date;
		}
		return DateUtils.copyDateOnly(firstDateWithChanges);
	}

	// ------------------------------------------------------------------------
	//
	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	//
	// ------------------------------------------------------------------------

	private void clearSalaryTable() {
		for (int i = salaryTable.getRowCount() - 1; i >= 0; i--)
			salaryTable.removeRow(i);

		changedLevelsRows.clear();
		changedVariablesCols.clear();
		salaryTableHead = clear(salaryTableHead);
		salaryTableFirstColumn = clear(salaryTableFirstColumn);
		salaryTableLastColumn = clear(salaryTableLastColumn);
		salaryTableUpperLeftCorner = clear(salaryTableUpperLeftCorner);
		salaryTableUpperRightCorner = clear(salaryTableUpperRightCorner);

	}

	private void clearExtrasTable() {
		for (int i = extrasTable.getRowCount() - 1; i > 0; i--)
			extrasTable.removeRow(i);
	}

	private void clearPaymentsTable() {
		for (int i = paymentsTable.getRowCount() - 1; i > 0; i--)
			paymentsTable.removeRow(i);
	}

	private void dumpVariable(int row, int col, Level level, Variable var) {

		TextBox expressionTextBox = new TextBox();
		// yes we assume all variables are numeric.
		expressionTextBox.addStyleName(AON.AON_TEXT_RIGHT);
		expressionTextBox.setVisibleLength(VARIABLE_TEXTBOX_SIZE);
		expressionTextBox.setText(var.getExpression());

		salaryTable.setWidget(row, col, expressionTextBox);

		VariableEditor variableEditor = new VariableEditor(level, var);
		variableEditor.setExpressionTextBox(expressionTextBox);
	}

	private void dumpUndefVariable(int row, int col, Level level, String name) {

		TextBox expressionTextBox = new TextBox();
		// yes we assume all variables are numeric.
		expressionTextBox.addStyleName(AON.AON_TEXT_RIGHT);
		expressionTextBox.setVisibleLength(VARIABLE_TEXTBOX_SIZE);
		salaryTable.setWidget(row, col, expressionTextBox);

		Variable variable = new StringVariable();
		variable.setName(name);
		VariableEditor variableEditor = new VariableEditor(level, variable);
		variableEditor.setExpressionTextBox(expressionTextBox);
	}

	private Widget dumpCategories(int row, int col, Level level,
			Set<String> categories) {

		String text = null;
		TextBox categoriesTextBox = new TextBox();
		if (categories != null) {
			text = reduce(categories, ", ");
			categoriesTextBox.setText(text);
		}
		if (text == null || text.isEmpty()) {
			categoriesTextBox.addStyleName(AON.AON_ICON_WARN);
			categoriesTextBox.addStyleName(AON.AON_PADDING_LEFT);
			categoriesTextBox
					.setTitle("Defina al menos una categoria."
							+ " Recuerde que los empleados se asocian a categorias no a niveles retributivos.");
		}

		categoriesTextBox.getElement().getStyle().setWidth(98, Unit.PCT);
		categoriesTextBox.getElement().getStyle()
				.setProperty("minWidth", VARIABLE_TEXTBOX_SIZE * 2, Unit.EM);
		salaryTable.setWidget(row, col, categoriesTextBox);

		CategoriesEditor categoriesEditor = new CategoriesEditor(level);
		categoriesEditor.setCategoriesTextBox(categoriesTextBox);

		return categoriesTextBox;
	}

	private void insertNewLevelRow(int row) {

		TextBox descriptionTextBox = new TextBox();
		descriptionTextBox.setVisibleLength(5);
		salaryTable.setWidget(row, 0, descriptionTextBox);

		int cols = salaryTable.getCellCount(row - 1);
		for (int col = 1; col < cols; col++)
			salaryTable.insertCell(row, col);

		LevelEditor editor = new LevelEditor(agreementDraftObject.newLevel());
		editor.setDescriptionTextBox(descriptionTextBox);
	}

	private void dumpExtra(Extra extra, int row,
			SortedSet<Payment> availablePayments) {

		// first cell for edit other stuff buttons.
		Button editButton = new Button();
		editButton.setStyleName(AON.AON_ICON_ROW_SELECTOR);
		editButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		extrasTable.setWidget(row, 0, editButton);

		DateTimeFormat yearMonthNumDayFormat = DateTimeFormat
				.getFormat("d/M/y");

		DateBox startDateBox = new DateBox();
		startDateBox
				.setFormat(new DateBox.DefaultFormat(yearMonthNumDayFormat));
		try {
			Date startDate = parseExtraDate(extra.getStartDate());
			startDateBox.setValue(startDate);
		} catch (EmptyStringException e) {
			startDateBox
					.setTitle("Es necesario introducir una fecha inicial de devengo.");
			startDateBox.addStyleName(AON.AON_ICON_EXCEPTION);
		} catch (DateTimeFormatException e) {
			startDateBox.setTitle("La fecha inicial de devengo '"
					+ extra.getStartDate() + "' no es correcta.");
			startDateBox.addStyleName(AON.AON_ICON_EXCEPTION);
		}
		startDateBox.addStyleName(AON.AON_TEXT_RIGHT);
		startDateBox.getTextBox().setVisibleLength(10);
		extrasTable.setWidget(row, 1, startDateBox);

		DateBox endDateBox = new DateBox();
		endDateBox.setFormat(new DateBox.DefaultFormat(yearMonthNumDayFormat));
		try {
			Date endDate = parseExtraDate(extra.getEndDate());
			endDateBox.setValue(endDate);
		} catch (EmptyStringException e) {
			endDateBox
					.setTitle("Es necesario introducir una fecha final de devengo.");
			endDateBox.addStyleName(AON.AON_ICON_EXCEPTION);
		} catch (DateTimeFormatException e) {
			endDateBox.setTitle("La fecha final de devengo '"
					+ extra.getEndDate() + "' no es correcta.");
			endDateBox.addStyleName(AON.AON_ICON_EXCEPTION);
		}
		endDateBox.addStyleName(AON.AON_TEXT_RIGHT);
		endDateBox.getTextBox().setVisibleLength(10);
		extrasTable.setWidget(row, 2, endDateBox);

		DateBox issueDateBox = new DateBox();
		issueDateBox
				.setFormat(new DateBox.DefaultFormat(yearMonthNumDayFormat));
		try {
			Date issueDate = parseExtraDate(extra.getIssueDate());
			issueDateBox.setValue(issueDate);
		} catch (EmptyStringException e) {
			issueDateBox
					.setTitle("Es necesario introducir una fecha de cobro.");
			issueDateBox.addStyleName(AON.AON_ICON_EXCEPTION);
		} catch (DateTimeFormatException e) {
			issueDateBox.setTitle("La fecha de cobro '" + extra.getIssueDate()
					+ "' no es correcta.");
			issueDateBox.addStyleName(AON.AON_ICON_EXCEPTION);
		}
		issueDateBox.addStyleName(AON.AON_TEXT_RIGHT);
		issueDateBox.getTextBox().setVisibleLength(10);

		extrasTable.setWidget(row, 3, issueDateBox);

		ListBox paymentListBox = new ListBox();
		paymentListBox.addItem("-", (String) null);

		Payment extraPayment = getExtraPayment(extra);
		if (extraPayment != null) {
			paymentListBox.addItem(extraPayment.getDescription(),
					String.valueOf(extraPayment.getId()));
			paymentListBox.setSelectedIndex(1);
		}

		for (Payment payment : availablePayments) {
			paymentListBox.addItem(payment.getDescription(),
					String.valueOf(payment.getId()));
		}

		paymentListBox.addStyleName(AON.AON_WIDTH_ALL);
		extrasTable.setWidget(row, 4, paymentListBox);

		Button deleteButton = new Button();
		deleteButton.setStyleName(AON.AON_ICON_DELETE);
		deleteButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		extrasTable.setWidget(row, 5, deleteButton);
		extrasTable.getCellFormatter().addStyleName(row, 5, AON.AON_TEXT_RIGHT);

		formatExtraRow(row);

		ExtraEditor editor = new ExtraEditor(extra);
		editor.setEndDateBox(endDateBox);
		editor.setStartDateBox(startDateBox);
		editor.setIssueDateBox(issueDateBox);
		editor.setPaymentListBox(paymentListBox);
		editor.setDeleteButton(deleteButton);
	}

	private void dumpPayment(Payment payment, int row) {

		// first cell for edit other stuff buttons.
		Button editButton = new Button();
		editButton.setStyleName(AON.AON_ICON_ROW_SELECTOR);
		editButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		paymentsTable.setWidget(row, 0, editButton);

		TypeListBox<Payment.Type> paymentTypeListBox = new TypeListBox<Payment.Type>(
				Payment.Type.class, "-", 15);
		paymentTypeListBox.setSelected(payment.getType());
		paymentsTable.setWidget(row, 1, paymentTypeListBox);

		TextBox descriptionBox = new TextBox();
		descriptionBox.setText(payment.getDescription());
		descriptionBox.getElement().getStyle().setWidth(98, Unit.PCT);
		paymentsTable.setWidget(row, 2, descriptionBox);

		TextBox expressionBox = new TextBox();
		expressionBox.setText(payment.getExpression());
		expressionBox.getElement().getStyle().setWidth(98, Unit.PCT);
		expressionBox.addStyleName(AON.AON_TEXT_RIGHT);
		paymentsTable.setWidget(row, 3, expressionBox);

		TypeListBox<Salary.Type> salaryTypeListBox = new TypeListBox<Salary.Type>(
				Salary.Type.class, 13);
		salaryTypeListBox.setSelected(payment.getSalaryType());
		paymentsTable.setWidget(row, 4, salaryTypeListBox);

		Button deleteButton = new Button();
		deleteButton.setStyleName(AON.AON_ICON_DELETE);
		deleteButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		paymentsTable.setWidget(row, 5, deleteButton);
		paymentsTable.getCellFormatter().addStyleName(row, 5,
				AON.AON_TEXT_RIGHT);
		formatPaymentRow(row);

		PaymentEditor changeHandler = new PaymentEditor(payment);
		changeHandler.setEditButton(editButton);
		changeHandler.setDeleteButton(deleteButton);
		changeHandler.setExpressionTextBox(expressionBox);
		changeHandler.setDescriptionTextBox(descriptionBox);
		changeHandler.setSalaryTypeListBox(salaryTypeListBox);
		changeHandler.setPaymentTypeListBox(paymentTypeListBox);
	}

	private void insertNewExtraRow(int row, SortedSet<Payment> payments) {
		// first cell for edit other stuff buttons.
		Button newButton = new Button();
		newButton.setStyleName(AON.AON_ICON_RESET);
		newButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		extrasTable.setWidget(row, 0, newButton);

		DateTimeFormat yearMonthNumDayFormat = DateTimeFormat
				.getFormat("d/M/y");

		DateBox startDateBox = new DateBox();
		startDateBox
				.setFormat(new DateBox.DefaultFormat(yearMonthNumDayFormat));
		startDateBox.addStyleName(AON.AON_TEXT_RIGHT);
		startDateBox.getTextBox().setVisibleLength(10);
		extrasTable.setWidget(row, 1, startDateBox);

		DateBox endDateBox = new DateBox();
		endDateBox.setFormat(new DateBox.DefaultFormat(yearMonthNumDayFormat));
		endDateBox.addStyleName(AON.AON_TEXT_RIGHT);
		endDateBox.getTextBox().setVisibleLength(10);
		extrasTable.setWidget(row, 2, endDateBox);

		DateBox issueDateBox = new DateBox();
		issueDateBox
				.setFormat(new DateBox.DefaultFormat(yearMonthNumDayFormat));
		issueDateBox.addStyleName(AON.AON_TEXT_RIGHT);
		issueDateBox.getTextBox().setVisibleLength(10);
		extrasTable.setWidget(row, 3, issueDateBox);

		ListBox paymentListBox = new ListBox();
		for (Payment payment : payments) {
			paymentListBox.addItem(payment.getDescription(),
					String.valueOf(payment.getId()));
		}
		paymentListBox.addStyleName(AON.AON_WIDTH_ALL);
		extrasTable.setWidget(row, 4, paymentListBox);

		extrasTable.insertCell(row, 5);

		formatExtraRow(row);

		ExtraEditor editor = new ExtraEditor(
				agreementDraftObject.newDraftExtra());
		editor.setEndDateBox(endDateBox);
		editor.setStartDateBox(startDateBox);
		editor.setIssueDateBox(issueDateBox);
		editor.setPaymentListBox(paymentListBox);
	}

	private void insertNewPaymentRow(int row) {
		// first cell for edit other stuff buttons.

		Button newButton = new Button();
		newButton.setStyleName(AON.AON_ICON_RESET);
		newButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		paymentsTable.setWidget(row, 0, newButton);

		TypeListBox<Payment.Type> paymentTypeListBox = new TypeListBox<Payment.Type>(
				Payment.Type.class, "-", 15);
		paymentTypeListBox.setSelected(null);
		paymentsTable.setWidget(row, 1, paymentTypeListBox);

		TextBox descriptionBox = new TextBox();
		SuggestBox descriptionSuggest = new SuggestBox(
				paymentDescriptionOracle, descriptionBox,
				paymentSuggestionDisplay);
		descriptionSuggest.getElement().getStyle().setWidth(98, Unit.PCT);
		paymentsTable.setWidget(row, 2, descriptionSuggest);

		TextBox expressionBox = new TextBox();
		expressionBox.getElement().getStyle().setWidth(98, Unit.PCT);
		expressionBox.addStyleName(AON.AON_TEXT_RIGHT);
		paymentsTable.setWidget(row, 3, expressionBox);

		TypeListBox<Salary.Type> salaryTypeListBox = new TypeListBox<Salary.Type>(
				Salary.Type.class, 13);
		salaryTypeListBox.setSelected(Salary.Type.SALARY);
		paymentsTable.setWidget(row, 4, salaryTypeListBox);

		paymentsTable.insertCell(row, 5);

		formatPaymentRow(row);
		Payment payment = agreementDraftObject.newDraftPayment();
		payment.setSalaryType(Salary.Type.SALARY);
		PaymentEditor paymentEditor = new PaymentEditor(payment);
		paymentEditor.setEditButton(newButton);
		paymentEditor.setExpressionTextBox(expressionBox);
		paymentEditor.setSalaryTypeListBox(salaryTypeListBox);
		paymentEditor.setPaymentTypeListBox(paymentTypeListBox);
		paymentEditor.setDescriptionTextBox(descriptionBox);
		paymentEditor.setDescriptionSuggestBox(descriptionSuggest);

	}

	private void formatExtraRow(int row) {
		extrasTable.getCellFormatter().getElement(row, 0).getStyle()
				.setPropertyPx("borderRightWidth", 0);
		extrasTable.getCellFormatter().getElement(row, 1).getStyle()
				.setPropertyPx("borderLeftWidth", 0);
		extrasTable.getCellFormatter().getElement(row, 4).getStyle()
				.setPropertyPx("borderRightWidth", 0);
		if (extrasTable.getCellCount(row) > 5)
			extrasTable.getCellFormatter().getElement(row, 5).getStyle()
					.setPropertyPx("borderLeftWidth", 0);

		extrasTable.getRowFormatter().addStyleName(
				row,
				row % 2 == 0 ? AON.AON_DATA_TABLE_ROW_ODD
						: AON.AON_DATA_TABLE_ROW_EVEN);
	}

	private void formatPaymentRow(int row) {
		paymentsTable.getCellFormatter().getElement(row, 0).getStyle()
				.setPropertyPx("borderRightWidth", 0);
		paymentsTable.getCellFormatter().getElement(row, 1).getStyle()
				.setPropertyPx("borderLeftWidth", 0);
		paymentsTable.getCellFormatter().getElement(row, 4).getStyle()
				.setPropertyPx("borderRightWidth", 0);
		if (paymentsTable.getCellCount(row) > 5)
			paymentsTable.getCellFormatter().getElement(row, 5).getStyle()
					.setPropertyPx("borderLeftWidth", 0);

		paymentsTable.getRowFormatter().addStyleName(
				row,
				row % 2 == 0 ? AON.AON_DATA_TABLE_ROW_ODD
						: AON.AON_DATA_TABLE_ROW_EVEN);
	}

	private void initExtrasTable() {

		extrasTable.setText(0, 0, "INICIO");
		extrasTable.getFlexCellFormatter().setColSpan(0, 0, 2);
		extrasTable.getCellFormatter().addStyleName(0, 0,
				AON.AON_INPUT_REQUIRED);
		extrasTable.setText(0, 1, "FIN");
		extrasTable.getCellFormatter().addStyleName(0, 1,
				AON.AON_INPUT_REQUIRED);
		extrasTable.setText(0, 2, "COBRO");
		extrasTable.getCellFormatter().addStyleName(0, 2,
				AON.AON_INPUT_REQUIRED);
		extrasTable.setText(0, 3, "CONCEPTO");
		extrasTable.getFlexCellFormatter().setColSpan(0, 3, 2);
		// endDateBox.addStyleName(AON.AON_INPUT_REQUIRED);

		extrasTable.getRowFormatter().addStyleName(0,
				AON.AON_DATA_TABLE_ROW_ODD);
		for (int i = 0; i < extrasTable.getCellCount(0); i++) {
			extrasTable.getCellFormatter().addStyleName(0, i, AON.AON_BOLD);
			extrasTable.getCellFormatter().addStyleName(0, i,
					AON.AON_TEXT_CENTER);
		}

		extrasTable.getColumnFormatter().setWidth(0, "2%");
		extrasTable.getColumnFormatter().setWidth(1, "5%"); // INICIO
		extrasTable.getColumnFormatter().setWidth(2, "5%"); // FIN
		extrasTable.getColumnFormatter().setWidth(3, "5%"); // COBRO
		// 4 ...
		extrasTable.getColumnFormatter().setWidth(5, "2%");

	}

	private void initPaymentsTable() {

		paymentsTable.setText(0, 0, "TIPO");
		paymentsTable.getFlexCellFormatter().setColSpan(0, 0, 2);
		paymentsTable.setText(0, 1, "CONCEPTO");
		paymentsTable.setText(0, 2, "DEVENGO");
		paymentsTable.setText(0, 3, "RECIBO");
		paymentsTable.getFlexCellFormatter().setColSpan(0, 3, 2);

		paymentsTable.getRowFormatter().addStyleName(0,
				AON.AON_DATA_TABLE_ROW_ODD);
		for (int i = 0; i < paymentsTable.getCellCount(0); i++) {
			paymentsTable.getCellFormatter().addStyleName(0, i, AON.AON_BOLD);
			paymentsTable.getCellFormatter().addStyleName(0, i,
					AON.AON_TEXT_CENTER);
		}

		paymentsTable.getColumnFormatter().setWidth(0, "2%");
		paymentsTable.getColumnFormatter().setWidth(1, "12%"); // TIPO
		// 2 ...
		paymentsTable.getColumnFormatter().setWidth(3, "18%"); // DEVENGO
		paymentsTable.getColumnFormatter().setWidth(4, "12%"); // RECIBO
		paymentsTable.getColumnFormatter().setWidth(5, "2%");

	}

	//@formatter:off
	// ======================================
	// | NIVEL | SALARIO_BASE | PAGA_EXTRA |.
	// ======================================
	// | 	   |   ###.###,00 | ###.###,00 |.
	// --------------------------------------
	// |   01  |   ###.###,00 | ###.###,00 |.
	// --------------------------------------
	// |   02  |   ###.###,00 | ###.###,00 |.
	// --------------------------------------
	//@formatter:on
	private void moveSalaryTableFrozenColsAndRows() {
		int verticalScroll = salaryTableScrollPane.getVerticalScrollPosition();
		int horizontalScroll = salaryTableScrollPane
				.getHorizontalScrollPosition();

		int clientWidth = Math
				.min(salaryTableScrollPane.getElement().getClientWidth(),
						mainScrollPane.getElement().getClientWidth());
		int clientHeight = salaryTableScrollPane.getElement().getClientHeight();

		int top = salaryTableScrollPane.getAbsoluteTop();
		int left = salaryTableScrollPane.getAbsoluteLeft();

		int clipTop = mainScrollPane.getAbsoluteTop() - top;
		int clipLeft = mainScrollPane.getAbsoluteLeft() - left;

		salaryTableUpperLeftCorner.getStyle().setTop(top, Unit.PX);
		salaryTableUpperLeftCorner.getStyle().setLeft(left, Unit.PX);
		setClip(salaryTableUpperLeftCorner.getStyle(), clipTop,
				salaryTableUpperLeftCorner.getOffsetWidth(),
				salaryTableUpperLeftCorner.getOffsetHeight(), clipLeft);

		salaryTableUpperRightCorner.getStyle().setTop(top, Unit.PX);
		left = salaryTableScrollPane.getAbsoluteLeft() + clientWidth
				- salaryTableUpperRightCorner.getOffsetWidth()
				+ mainScrollPane.getHorizontalScrollPosition();
		salaryTableUpperRightCorner.getStyle().setLeft(left + 1, Unit.PX);
		setClip(salaryTableUpperRightCorner.getStyle(), clipTop,
				salaryTableUpperRightCorner.getOffsetWidth(),
				salaryTableUpperRightCorner.getOffsetHeight(), 0);

		left = salaryTableScrollPane.getAbsoluteLeft()
				+ salaryTableUpperLeftCorner.getOffsetWidth()
				- horizontalScroll;
		salaryTableHead.getStyle().setTop(top, Unit.PX);
		salaryTableHead.getStyle().setLeft(left - 1, Unit.PX);

		clipLeft = horizontalScroll + 1 + (clipLeft > 0 ? clipLeft : 0);
		int clipRight = horizontalScroll + clientWidth
				- salaryTableUpperLeftCorner.getOffsetWidth() + 1;
		setClip(salaryTableHead.getStyle(), clipTop, clipRight, clientHeight,
				clipLeft);

		left = salaryTableScrollPane.getAbsoluteLeft();
		top = salaryTableScrollPane.getAbsoluteTop()
				+ salaryTableUpperLeftCorner.getOffsetHeight() - verticalScroll;
		salaryTableFirstColumn.getStyle().setLeft(left, Unit.PX);
		salaryTableFirstColumn.getStyle().setTop(top - 1, Unit.PX);

		clipTop = verticalScroll + 1;
		if ((top + clipTop) < mainScrollPane.getAbsoluteTop())
			clipTop += mainScrollPane.getAbsoluteTop() - top - clipTop;

		clipLeft = mainScrollPane.getAbsoluteLeft() - left;
		int clipBottom = verticalScroll
				+ (clientHeight - salaryTableUpperLeftCorner.getOffsetHeight())
				+ 1;
		setClip(salaryTableFirstColumn.getStyle(), clipTop, clientWidth,
				clipBottom, clipLeft);

		salaryTableLastColumn.getStyle().setTop(top - 1, Unit.PX);
		left = salaryTableScrollPane.getAbsoluteLeft() + clientWidth
				- salaryTableLastColumn.getOffsetWidth()
				+ mainScrollPane.getHorizontalScrollPosition();
		salaryTableLastColumn.getStyle().setLeft(left + 1, Unit.PX);
		setClip(salaryTableLastColumn.getStyle(), clipTop, clientWidth,
				clipBottom, 0 /**/);

	}

	private void enableUndoRedoButtons() {
		undoButton.setEnabled(agreementDraftObject.canUndo());
		redoButton.setEnabled(agreementDraftObject.canRedo());
	}

	private void ensureChangesVisible() {
		// shows first level changed row.
		if (!changedLevelsRows.isEmpty()) {
			ensureVisibleTopImpl(
					salaryTableScrollPane.getElement(),
					salaryTable.getRowFormatter().getElement(
							changedLevelsRows.get(0)));
		}
		// shows first variable changed column.
		if (!changedVariablesCols.isEmpty()) {
			ensureVisibleLeftImpl(
					salaryTableScrollPane.getElement(),
					salaryTable.getCellFormatter().getElement(0,
							changedVariablesCols.get(0)));
		}
	}

	private Payment getExtraPayment(Extra extra) {
		Integer paymentId = extra.getPaymentId();
		if (paymentId == null)
			return null;

		Set<Payment> payments = agreementDraftObject.getPayments();
		for (Payment payment : payments)
			if (paymentId.equals(payment.getId()))
				return payment;

		return null;
	}

	private SortedSet<Payment> getAvailableExtraPayments() {

		Set<Integer> extraIds = new HashSet<Integer>();

		for (Extra extra : agreementDraftObject.getExtras()) {
			if (extra.getPaymentId() != null)
				extraIds.add(extra.getPaymentId());
		}

		SortedSet<Payment> payments = new TreeSet<Payment>(new ItemComparator());
		for (Payment payment : agreementDraftObject.getPayments())
			if (payment.getSalaryType() == Salary.Type.EXTRA
					&& !extraIds.contains(payment.getId()))
				payments.add(payment);

		return payments;
	}

	private Date parseExtraDate(String text) {
		return parseExtraDate(text,
				CalendarUtil.copyDate(agreementDraftObject.getStartDate()));
	}

	private String formatExtraDate(Date extraDate) {
		return formatExtraDate(extraDate, agreementDraftObject.getStartDate());
	}

	// ------------------------------------------------------------------------

	protected static String formatExtraDate(Date extraDate, Date date) {

		DateTimeFormat format = DateTimeFormat.getFormat("d/M");
		String text = format.format(extraDate);

		int years = DateUtils.getYears(extraDate, date);

		if (years == 0)
			return text;
		else
			return text + " " + String.valueOf(years);
	}

	protected static Date parseExtraDate(String text, Date date) {

		if (text == null)
			throw new EmptyStringException();

		DateTimeFormat format = DateTimeFormat.getFormat("d/M");

		int start = -1;
		while (++start < text.length() && Character.isSpace(text.charAt(start)))
			;
		if (start >= text.length())
			throw new EmptyStringException();

		try {
			start += format.parse(text, start, date);
		} catch (Throwable t) {
			throw new DateTimeFormatException("'" + text + "/" + start
					+ "' it's not a valid extra date");
		}

		while (++start < text.length() && Character.isSpace(text.charAt(start)))
			;

		int end = text.length();
		while (--end > 0 && Character.isSpace(text.charAt(end)))
			;

		if (start > end)
			return date;
		try {
			int years = Integer.valueOf(text.substring(start, end + 1));
			return DateUtils.addYears2Date(date, years);
		} catch (Throwable t) {
			throw new DateTimeFormatException("'" + text + "/" + start
					+ "' it's not a valid extra date");
		}
	}

	// ------------------------------------------------------------------------

	private static Element clear(Element el) {
		if (el != null)
			el.removeFromParent();
		return null;
	}

	private static String reduce(Set<String> set, String sep) {
		StringBuffer buffer = new StringBuffer();
		for (String string : set) {
			if (buffer.length() > 0)
				buffer.append(sep);
			if (string == null)
				continue;

			buffer.append(string.trim());
		}
		return buffer.toString();
	}

	private static Element cloneTR(Element tr) {
		Element clone = DOM.clone(tr, true);

		com.google.gwt.dom.client.Element td = tr.getFirstChildElement();
		com.google.gwt.dom.client.Element cloneTd = clone
				.getFirstChildElement();

		while (td != null) {
			int offsetWidth = td.getOffsetWidth();
			cloneTd.getStyle().setWidth(offsetWidth - 1, Unit.PX);
			cloneTd.getStyle().setPaddingLeft(0, Unit.PX);
			cloneTd.getStyle().setPaddingRight(0, Unit.PX);
			td = td.getNextSiblingElement();
			cloneTd = cloneTd.getNextSiblingElement();
		}

		return clone;

	}

	private static Element getFreezeTableUpperLeftCorner(FlexTable flexTable) {
		Element th = cloneTR(flexTable.getRowFormatter().getElement(0));

		for (int i = th.getChildCount() - 1; i > 0; i--) {
			th.getChild(i).removeFromParent();
		}

		int width = flexTable.getCellFormatter().getElement(0, 0)
				.getOffsetWidth();

		Element table = DOM.createTable();
		Element tbody = DOM.createTBody();

		DOM.appendChild(table, tbody);
		DOM.appendChild(tbody, th);
		table.getStyle().setPosition(Position.ABSOLUTE);
		table.getStyle().setTop(0, Unit.PX);
		table.getStyle().setBackgroundColor("white");
		table.getStyle().setWidth(width + 2, Unit.PX);
		table.setClassName(flexTable.getElement().getClassName());

		return table;
	}

	private static Element getFreezeTableUpperRightCorner(FlexTable flexTable,
			ScrollPanel scrollPane) {

		Element th = cloneTR(flexTable.getRowFormatter().getElement(0));

		// remove all columns except last..
		for (int i = th.getChildCount() - 2; i >= 0; i--) {
			th.getChild(i).removeFromParent();
		}

		int width = flexTable.getCellFormatter()
				.getElement(0, flexTable.getCellCount(0) - 1).getOffsetWidth();

		int left = scrollPane.getElement().getClientWidth() - width;

		Element table = DOM.createTable();
		Element tbody = DOM.createTBody();

		DOM.appendChild(table, tbody);
		DOM.appendChild(tbody, th);
		table.getStyle().setPosition(Position.ABSOLUTE);
		table.getStyle().setTop(0, Unit.PX);
		table.getStyle().setLeft(left, Unit.PX);
		table.getStyle().setBackgroundColor("white");
		table.getStyle().setWidth(width + 1, Unit.PX);
		table.setClassName(flexTable.getElement().getClassName());

		return table;
	}

	private static Element getFreezeTableHead(FlexTable flexTable) {
		Element th = cloneTR(flexTable.getRowFormatter().getElement(0));

		int left = flexTable.getCellFormatter().getElement(0, 0)
				.getOffsetWidth();

		// removes first cell, this belongs to corner.
		th.getFirstChildElement().removeFromParent();

		int width = 0;
		for (int col = 1; col < flexTable.getCellCount(0); col++)
			width += flexTable.getCellFormatter().getElement(0, col)
					.getOffsetWidth();

		Element table = DOM.createTable();
		Element tbody = DOM.createTBody();

		DOM.appendChild(table, tbody);
		DOM.appendChild(tbody, th);
		table.getStyle().setPosition(Position.ABSOLUTE);
		table.getStyle().setTop(0, Unit.PX);
		table.getStyle().setLeft(left + 1, Unit.PX);
		table.getStyle().setWidth(width + 1, Unit.PX);
		table.getStyle().setBackgroundColor("white");
		table.setClassName(flexTable.getElement().getClassName());

		return table;
	}

	private static Element getFreezeTableFirstCol(FlexTable flexTable) {
		Element table = getFreezeTableCol(flexTable, 0);
		int width = flexTable.getCellFormatter().getElement(0, 0)
				.getOffsetWidth();
		table.getStyle().setWidth(width + 2, Unit.PX);

		return table;
	}

	private static Element getFreezeTableLastCol(FlexTable flexTable,
			ScrollPanel scrollPanel) {
		int col = flexTable.getCellCount(0) - 1;
		Element table = getFreezeTableCol(flexTable, col);

		int width = flexTable.getCellFormatter().getElement(0, col)
				.getOffsetWidth();
		int left = scrollPanel.getElement().getClientWidth() - width;
		table.getStyle().setLeft(left - 1, Unit.PX);

		return table;
	}

	private static Element getFreezeTableCol(FlexTable flexTable, int col) {

		Element table = DOM.createTable();
		Element tbody = DOM.createTBody();

		for (int row = 1; row < flexTable.getRowCount(); row++) {
			Element tr = DOM.createTR();
			tr.setClassName(flexTable.getRowFormatter().getElement(row)
					.getClassName());

			Element td = flexTable.getCellFormatter().getElement(row, col);

			Element clone = DOM.clone(td, true);

			int offsetWidth = td.getOffsetWidth();
			int offsetHeight = td.getOffsetHeight();

			if (DOM.getChildCount(td) > 0) {
				Element tdChild = DOM.getChild(td, 0);
				Element cloneChild = DOM.getChild(clone, 0);
				DOM.removeChild(td, tdChild);
				DOM.removeChild(clone, cloneChild);
				DOM.appendChild(clone, tdChild);
				DOM.appendChild(td, cloneChild);

			}

			clone.getStyle().setPaddingTop(0, Unit.PX);
			clone.getStyle().setPaddingBottom(0, Unit.PX);
			clone.getStyle().setHeight(offsetHeight - 1, Unit.PX);
			clone.getStyle().setWidth(offsetWidth - 13, Unit.PX);

			DOM.appendChild(tr, clone);
			DOM.appendChild(tbody, tr);
		}

		int width = flexTable.getCellFormatter().getElement(0, col)
				.getOffsetWidth();
		int height = flexTable.getCellFormatter().getElement(0, col)
				.getOffsetHeight();

		DOM.appendChild(table, tbody);
		table.getStyle().setPosition(Position.ABSOLUTE);
		table.getStyle().setTop(height, Unit.PX);
		table.getStyle().setWidth(width + 1, Unit.PX);
		table.getStyle().setBackgroundColor("white");
		table.setClassName(flexTable.getElement().getClassName());

		return table;
	}

	private static void toFixedPosition(Element el, int width, int height) {
		int top = el.getAbsoluteTop();
		int left = el.getAbsoluteLeft();

		Style style = el.getStyle();
		style.setPosition(Position.FIXED);
		style.setTop(top, Unit.PX);
		style.setLeft(left, Unit.PX);

		// rect(<top>, <right>, <bottom>, <left>)
		style.setProperty("clip", "rect(0px," + width + "px," + height
				+ "px,0px)");
		setClip(style, 0, width, height, 0);

	}

	private static void setClip(Style style, int top, int right, int bottom,
			int left) {
		style.setProperty("clip", "rect(" + top + "px," + right + "px,"
				+ bottom + "px, " + left + "px)");
	}

	private static String getSuggestionString(Payment payment) {
		StringBuffer suggestion = new StringBuffer(payment.getDescription());
		if (!StringUtils.isEmpty(payment.getName()))
			suggestion.append("(").append(payment.getName()).append(")");
		return suggestion.toString();
	}

	private static int getRealOffsetTop(Element scroll, Element e) {
		int realOffsetTop = 0;
		for (com.google.gwt.dom.client.Element item = e; item != scroll; item = item
				.getOffsetParent())
			realOffsetTop += item.getOffsetTop();

		return realOffsetTop;
	}

	private static int getRealOffsetLeft(Element scroll, Element e) {
		int realOffsetLeft = 0;
		for (com.google.gwt.dom.client.Element item = e; item != scroll; item = item
				.getOffsetParent())
			realOffsetLeft += item.getOffsetLeft();

		return realOffsetLeft;
	}

	private static void ensureVisibleTopImpl(Element scroll, Element e) {
		scroll.setScrollTop(getRealOffsetTop(scroll, e)
				- scroll.getOffsetHeight() / 2);
	}

	private static void ensureVisibleLeftImpl(Element scroll, Element e) {
		scroll.setScrollLeft(getRealOffsetLeft(scroll, e)
				- scroll.getOffsetWidth() / 2);
	}

	/*
	 * Hides an element, but it will still take up the same space as before. The
	 * element will be hidden, but still affect the layout.
	 */
	private static void hide(Widget widget, boolean hide) {
		if (hide) {
			widget.getElement().getStyle().setVisibility(Visibility.HIDDEN);
		}
	}

}
