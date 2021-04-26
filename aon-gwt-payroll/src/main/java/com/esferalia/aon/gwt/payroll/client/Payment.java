package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.gwt.common.shared.EvalException;
import com.esferalia.aon.gwt.common.shared.EvalSyntaxErrorException;
import com.esferalia.aon.gwt.common.shared.EvalWarning;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.client.FxDialog.IContextProvider;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.Payment.Type;
import com.esferalia.aon.gwt.payroll.shared.Result;
import com.esferalia.aon.gwt.payroll.shared.SpecialExpresion;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.InvocationException;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionModel.AbstractSelectionModel;

public class Payment extends ResizeComposite {

	public static final String ALL = "ALL";
	public static final String NONE = "NONE";
	public static final String IPREM = "IPREM";
	public static final String CUSTOM = "CUSTOM";
	public static final String PRORATED = "PRORATED";
	
	private static final List<Variable> EMPTY_VARS = Collections.emptyList();

	private static final DateTimeFormat MONTH_FORMAT = DateTimeFormat
			.getFormat(PredefinedFormat.MONTH);

	interface Binder extends UiBinder<Widget, Payment> {

	}

	private static final Binder binder = GWT.create(Binder.class);
	
	private class PaymentsSelectionModel extends AbstractSelectionModel<com.esferalia.aon.gwt.payroll.shared.Payment> {

		private Timer synchronizer = new Timer() {
			
			@Override
			public void run() {
				PaymentsSelectionModel.this.fireSelectionChangeEvent();
			}
		};
		
		public PaymentsSelectionModel(ProvidesKey<com.esferalia.aon.gwt.payroll.shared.Payment> keyProvider) {
			super(keyProvider);
			paymentTextBox.addKeyUpHandler( e -> synchronizer.schedule(2000));
		}

		@Override
		public boolean isSelected(com.esferalia.aon.gwt.payroll.shared.Payment payment) {
			String expression =  paymentTextBox.getCurrentExpression();//getExpression();
			//indow.alert("expression :" + expression );
			if ( AonStringUtils.isBlank(expression))
				return false;

			String var = getVariableName(payment);
			if ( AonStringUtils.isBlank(var))
				return false;
			
			return RegExp.compile("\\b"+var+"\\b").test(expression);
		}

		@Override
		public void setSelected(com.esferalia.aon.gwt.payroll.shared.Payment payment, boolean selected) {
			String expression = getExpression();
			if ( AonStringUtils.isBlank(expression))
				return;

			String var = getVariableName(payment);
			if ( AonStringUtils.isBlank(var))
				return;
			
			RegExp varRegExp = RegExp.compile("\\b"+var+"\\b");
			
			if ( selected ) {
				if ( !varRegExp.test(expression) ) { 
					RegExp userReadOnlyRegExp = RegExp.compile("(.*/\\*.*\\*/)(.*)(/\\*\\*/.*)");
					if ( userReadOnlyRegExp.test(expression ))
						expression = userReadOnlyRegExp.replace(expression, "$1$2"+"+"+var+"$3");
					else 	
						expression += "+" + var;
				}
			} else {
				// first of all remove variable. 
				expression = varRegExp.replace(expression, ""); 

				// now remove empty parenthesis.
				expression = RegExp.compile("\\(\\s*\\)").replace(expression, "");
				// now remove operators at begin.
				expression = RegExp.compile("^[\\+\\*]").replace(expression, "");
				// now remove operators at end.
				expression = RegExp.compile("[\\+\\*]$").replace(expression, "");
				// now remove consecutive operators.
				//expression = RegExp.compile("[\\s\\+\\*]+").replace(expression, "");
				
			}
			
			paymentTextBox.setExpression(expression,false);
			fireSelectionChangeEvent();
		}
		
	}

	private class MyExpressionBox extends ExpressionBox implements BlurHandler,
			FocusHandler, AsyncCallback<List<Result>> {

		private boolean hasFocus;

		private String result;
		private String expression;

		private MyExpressionBox parent;
		private MyExpressionBox childs[];

		public MyExpressionBox(MyExpressionBox... childs) {
			addBlurHandler(this);
			addFocusHandler(this);
			this.childs = childs;
			for (MyExpressionBox child : childs)
				child.parent = this;
		}

		@Override
		public void onBlur(BlurEvent event) {
			setExpression(getText(), true);
			hasFocus = false;
		}

		@Override
		public void onFocus(FocusEvent event) {
			setText(expression);
			hasFocus = true;
		}

		@Override
		public void onFailure(Throwable caught) {
			// Convenient way to find out which exception was thrown.
			result = null;

			setText(expression);

			try {
				throw caught;
			} catch (InvocationException e) {
				// the call didn't complete cleanly
			} catch (EvalWarning e) {
				// one of the 'throws' from the original method
			} catch (EvalSyntaxErrorException e) {
				// one of the 'throws' from the original method
			} catch (EvalException e) {
				// one of the 'throws' from the original method
			} catch (Throwable e) {
				// last resort -- a very unexpected exception
			}

		}

		@Override
		public void onSuccess(List<Result> results) {
			double total = 0.00;
			for (Result result : results)
				total += result.getResult().doubleValue();
			result = format(total);
			setText(result);
		}

		public String getExpression() {
			return expression;
		}

		void setExpression(String newExpression) {
			setExpression(newExpression, false);
		}

		void setExpression(String newExpression, boolean fire) {
			boolean changed = changed(newExpression);
			this.expression = newExpression;
			if (changed) {
				eval(fire);
			} else {
				setText(result != null ? result : expression);
			}
		}

		public String getCurrentExpression() {
			return !hasFocus ? getExpression() : getText();
		}

		String format(Double d) {
			return d != null ? numberFormat.format(d) : null;
		}

		void eval(boolean fire) {
			contextProvider.eval(getParentExpression() + expression, EMPTY_VARS,this);
			if (fire) {
				for (MyExpressionBox child : childs) {
					child.eval(fire);
				}
			}
		}

		boolean changed(String newExpression) {
			if (expression == newExpression)
				return false;
			if (expression == null)
				return true;
			if (newExpression == null)
				return true;

			return !expression.trim().equals(newExpression.trim());
		}

		String getParentExpression() {

			if (parent == null)
				return "";

			StringBuffer buffer = new StringBuffer();
			buffer.append("_P = ");
			String expr = parent.getExpression();
			buffer.append(StringUtils.isEmpty(expr) ? "0.00" : expr);
			buffer.append(";");
			if (concept != null)
				buffer.append(concept.getName() + " = _P;");
			return buffer.toString();
		}

		void enable(boolean enabled) {

			if (isEnabled() == enabled)
				return;

			setEnabled(enabled);

			if (!enabled) {
				getElement().getStyle().setColor("inherit");
				getElement().getStyle().setBackgroundColor("inherit");
				getElement().getStyle().setBorderStyle(BorderStyle.NONE);
			} else {
				getElement().getStyle().clearColor();
				getElement().getStyle().clearBackgroundColor();
				getElement().getStyle().clearBorderStyle();
			}
		}
	}

	@UiField
	Grid mainGrid;
	@UiField
	Grid paymentsGrid;
	
	@UiField
	DeckPanel typeDeckPanel;
	@UiField
	ListBox typeListBox;
	@UiField
	Button resetTypeButton;
	@UiField
	Label typeLabel;
	
	@UiField
	ListBox receiptListBox;

	@UiField(provided = true)
	SuggestBox conceptSuggestBox;
	@UiField
	Label conceptDescriptionLabel;

	@UiField(provided = true)
	SuggestBox descriptionSuggestBox;
	@UiField
	Button resetDescriptionButton;

	@UiField
	Button fxPaymentButton;
	@UiField(provided = true)
	MyExpressionBox paymentTextBox;
	@UiField
	Button resetPaymentButton;
	
	@UiField
	DeckPanel taxTypeDeckPanel;
	@UiField
	ListBox taxListBox;
	@UiField
	Label taxLabel;
	@UiField
	Button resetTaxButton;
	@UiField
	DeckPanel quoteTypeDeckPanel;
	@UiField
	ListBox quoteListBox;
	@UiField
	Label quoteLabel;
	@UiField
	Button resetQuoteButton;
	@UiField
	ListBox monthListBox;

	@UiField
	Button fxTaxButton;
	@UiField(provided = true)
	MyExpressionBox taxTextBox;

	@UiField
	Button fxQuoteButton;
	@UiField(provided = true)
	MyExpressionBox quoteTextBox;
	
	@UiField
	Panel taxFullPanel;
	@UiField
	Panel taxNonePanel;
	@UiField
	Panel taxEditPanel;
	@UiField
	DeckPanel taxDeckPanel;
	@UiField
	Panel quoteFullPanel;
	@UiField
	Panel quoteNonePanel;
	@UiField
	Panel quoteEditPanel;
	@UiField
	DeckPanel quoteDeckPanel;
	
	@UiField(provided = true)
	DataGrid<com.esferalia.aon.gwt.payroll.shared.Payment> paymentsDataGrid;

	
	private int taxFullPanelIndex;
	private int taxEditPanelIndex;
	private int taxNonePanelIndex;
	private int quoteFullPanelIndex;
	private int quoteEditPanelIndex;
	private int quoteNonePanelIndex;

	private NumberFormat numberFormat;
	private IContextProvider contextProvider;

	private MultiWordSuggestOracle conceptSuggestOracle;
	private MultiWordSuggestOracle descriptionSuggestOracle;
	private MultiWordSuggestOracle expressionSuggestOracle;
	private com.esferalia.aon.gwt.payroll.shared.Payment concept;
	
	private List<com.esferalia.aon.gwt.payroll.shared.Payment> payments;
	private PaymentsSelectionModel paymentsSelectionModel;

	public Payment() {
		initProvided();
		initWidget(binder.createAndBindUi(this));
		initTypeListBox();
		initReceiptListBox();
		initMonthListBox();
		initTaxDeckPanel();
		showReceipt(false);
		paymentsGrid.setVisible(false);
	}

	public void setNumberFormat(NumberFormat numberFormat) {
		this.numberFormat = numberFormat;
	}

	public Short getMonth() {
		int index = monthListBox.getSelectedIndex();
		return index == 0 ? null : (short) (index - 1);
	}

	public void setMonth(Short month) {
		monthListBox.setSelectedIndex(month == null ? 0 : month + 1);
	}

	public String getDescription() {
		return descriptionSuggestBox.getText();
	}

	public void setDescription(String description) {
		descriptionSuggestBox.setText(description);
		showOrHideResetDescriptionButton();
	}

	public String getExpression() {
		return ((MyExpressionBox) paymentTextBox).getExpression();
	}

	public void setExpression(String payment) {
		boolean notZero = !SpecialExpresion.isZero(payment);
		boolean notReadOnly = !SpecialExpresion.isReadOnly(payment);
		paymentTextBox.setExpression(payment);
		paymentTextBox.enable(notReadOnly);
		showOrHideFxPaymentButton();
		showOrHideResetPaymentButton();
		paymentTextBox.setVisible(notReadOnly || notZero);
	}

	public String getIrpfExpression() {
		String selected = getValueSelected(taxListBox);
		return getExpression(selected, taxTextBox.expression);

	}

	public void setIrpfExpression(String expression) {
		boolean notZero = !SpecialExpresion.isZero(expression);
		boolean notReadOnly = !SpecialExpresion.isReadOnly(expression);
		String listValue = getListValue(expression);
		selectByValue(taxListBox, listValue);
		onTaxListBoxChange(null);
		taxTextBox.setExpression(getExpression(listValue, expression));
		showOrHideResetTaxButton();
		//taxTextBox.enable(!SpecialExpresion.isReadOnly(expression));
		enableCustomTax(listValue.equals(CUSTOM) && notReadOnly);
		taxTextBox.setVisible(notReadOnly || notZero);
	}

	public String getQuoteExpression() {
		String selected = getValueSelected(quoteListBox);
		return getExpression(selected, quoteTextBox.expression);
	}

	public void setQuoteExpression(String expression) {
		boolean notZero = !SpecialExpresion.isZero(expression);
		boolean notReadOnly = !SpecialExpresion.isReadOnly(expression);
		String listValue = getListValue(expression);
		selectByValue(quoteListBox, listValue);
		onQuoteListBoxChange(null);
		quoteTextBox.setExpression(getExpression(listValue, expression));
		showOrHideResetQuoteButton();
		//quoteTextBox.enable(!SpecialExpresion.isReadOnly(expression));
		enableCustomQuote(listValue.equals(CUSTOM) && !SpecialExpresion.isReadOnly(expression));
		quoteTextBox.setVisible(notReadOnly || notZero);
	}

	public void setType(com.esferalia.aon.gwt.payroll.shared.Payment.Type type) {
		
		
		for (int i = 0; i < typeListBox.getItemCount(); i++)
			if (Integer.valueOf(typeListBox.getValue(i)) == type.getCode())
				typeListBox.setSelectedIndex(i);

		showOrHideResetTypeButton();
		enableOrDisableTaxAndQuote();
		enableOrDisableMonth();
		enableOrDisablePayments();
	}

	public com.esferalia.aon.gwt.payroll.shared.Payment.Type getType() {
		int index = typeListBox.getSelectedIndex();
		int code = Integer.valueOf(typeListBox.getValue(index));
		return com.esferalia.aon.gwt.payroll.shared.Payment.Type
				.getByCode(code);
	}

	public void setReceiptType(
			com.esferalia.aon.gwt.payroll.shared.Salary.Type type) {
		receiptListBox.setSelectedIndex(type.ordinal());
	}

	public com.esferalia.aon.gwt.payroll.shared.Salary.Type getReceiptType() {
		int index = receiptListBox.getSelectedIndex();
		return com.esferalia.aon.gwt.payroll.shared.Salary.Type.values()[index];
	}

	public void setAvailableConcepts(
			List<com.esferalia.aon.gwt.payroll.shared.Payment> availableConcepts) {

		for (com.esferalia.aon.gwt.payroll.shared.Payment payment : availableConcepts) {
			String name = payment.getName();
			if (!StringUtils.isEmpty(name)) {
				conceptSuggestOracle.add(name);
			}
			String description = payment.getDescription();
			if (!StringUtils.isEmpty(description)) {
				descriptionSuggestOracle.add(description);
			}
		}
	}

	public com.esferalia.aon.gwt.payroll.shared.Payment getConcept() {
		return concept;
	}

	public void setConcept(com.esferalia.aon.gwt.payroll.shared.Payment concept) {
		this.concept = concept;
		// From now ypu can't edit concept
		conceptSuggestBox.setEnabled(concept == null || concept.getId() < 0);
		onChangeConcept();
	}

	public void setContextProvider(IContextProvider contextProvider) {
		this.contextProvider = contextProvider;
		loadExpressionSuggestOracle();
	}

	public String getName() {
		return conceptSuggestBox.getText();
	}

	public void setName(String name) {
		conceptSuggestBox.setText(name);
	}

	public void showMonth(boolean show) {
		Element el = mainGrid.getRowFormatter().getElement(
				mainGrid.getRowCount() - 1);
		if (show)
			el.getStyle().clearDisplay();
		else
			el.getStyle().setDisplay(Display.NONE);
	}

	public void showReceipt(boolean show) {

		Element el = mainGrid.getRowFormatter().getElement(1);
		if (show)
			el.getStyle().clearDisplay();
		else
			el.getStyle().setDisplay(Display.NONE);
	}
	
	public void setEnabledMonthListBox(boolean enabled){
		monthListBox.setEnabled(enabled);
	}
	
	
	public void setAvailablePayments(List<com.esferalia.aon.gwt.payroll.shared.Payment> payments) {
		this.payments = payments;
		enableOrDisablePayments();
	}
	
	public void setReadOnly(boolean readOnly) {
		conceptSuggestBox.getValueBox().setReadOnly(readOnly);
		conceptSuggestBox.getValueBox().setEnabled(!readOnly);
		
		if(readOnly) {
			typeLabel.setText(typeListBox.getSelectedItemText());
			typeDeckPanel.showWidget(1);
		}
		
		descriptionSuggestBox.getValueBox().setReadOnly(readOnly);
		descriptionSuggestBox.getValueBox().setEnabled(!readOnly);
		resetDescriptionButton.setEnabled(!readOnly);
		
		paymentTextBox.setReadOnly(readOnly);
		paymentTextBox.setEnabled(!readOnly);
		fxPaymentButton.setEnabled(!readOnly);
		resetPaymentButton.setEnabled(!readOnly);
		
		if(readOnly) {
			taxLabel.setText(taxListBox.getSelectedItemText());
			taxTypeDeckPanel.showWidget(1);
		}
		
		taxTextBox.setReadOnly(readOnly);
		taxTextBox.setEnabled(!readOnly);
		fxTaxButton.setEnabled(!readOnly);
		resetTaxButton.setEnabled(!readOnly);
		
		if(readOnly) {
			quoteLabel.setText(quoteListBox.getSelectedItemText());
			quoteTypeDeckPanel.showWidget(1);
		}
		
		quoteTextBox.setReadOnly(readOnly);
		quoteTextBox.setEnabled(!readOnly);
		fxQuoteButton.setEnabled(!readOnly);
		resetQuoteButton.setEnabled(!readOnly);
		
		paymentsGrid.setVisible(false);
		
	}
	
	// ------------------------------------------
	// Protected members
	// ------------------------------------------

	@UiHandler("taxListBox")
	void onTaxListBoxChange(ChangeEvent event) {
		String tax = getValueSelected(taxListBox);
		enableCustomTax(tax.equals(CUSTOM));
		taxTextBox.setExpression(getExpression(tax, taxTextBox.expression));
		showOrHideResetTaxButton();
	}

	@UiHandler("quoteListBox")
	void onQuoteListBoxChange(ChangeEvent event) {
		String quote = getValueSelected(quoteListBox);
		enableCustomQuote(quote.equals(CUSTOM));
		quoteTextBox
				.setExpression(getExpression(quote, quoteTextBox.expression));
		showOrHideResetQuoteButton();
	}

	@UiHandler("descriptionSuggestBox")
	void onDescriptionSuggestBoxChange(ValueChangeEvent<String> event) {
		showOrHideResetDescriptionButton();
	}

	@UiHandler("resetDescriptionButton")
	void onResetDescriptionButtonClick(ClickEvent event) {
		setDescription(concept.getDescription());
	}

	@UiHandler("paymentTextBox")
	void onPaymentTextBoxChange(BlurEvent event) {
		showOrHideFxPaymentButton();
		showOrHideResetPaymentButton();
	}

	@UiHandler("resetPaymentButton")
	void onResetPaymentButtonClick(ClickEvent event) {
		setExpression(concept.getExpression());
	}

	@UiHandler("taxTextBox")
	void onTaxTextBoxChange(BlurEvent event) {
		showOrHideResetTaxButton();
	}

	@UiHandler({ "resetTaxButton" })
	void onResetTaxButtonClick(ClickEvent event) {
		setIrpfExpression(concept.getIrpfExpression());
	}

	@UiHandler("quoteTextBox")
	void onQuoteTextBoxChange(BlurEvent event) {
		showOrHideResetQuoteButton();
	}

	@UiHandler({ "resetQuoteButton" })
	void onResetQuoteButtonClick(ClickEvent event) {
		setQuoteExpression(concept.getQuoteExpression());
	}

	@UiHandler("typeListBox")
	void onTypeListBoxChange(ChangeEvent event) {
		showOrHideResetTypeButton();
		enableOrDisableTaxAndQuote();
		enableOrDisableMonth();
		enableOrDisablePayments();
	}

	@UiHandler("resetTypeButton")
	void onResetTypeButtonClick(ClickEvent event) {
		setType(concept.getType());
	}

	@UiHandler("fxPaymentButton")
	void onFxPaymentButtonClick(ClickEvent event) {
		showFxDialog((MyExpressionBox) paymentTextBox);
	}

	@UiHandler("fxTaxButton")
	void onFxTaxButtonClick(ClickEvent event) {
		showFxDialog((MyExpressionBox) taxTextBox);
	}

	@UiHandler("fxQuoteButton")
	void onFxQuoteButtonClick(ClickEvent event) {
		showFxDialog((MyExpressionBox) quoteTextBox);
	}
	
	
	// ------------------------------------------
	// Private members
	// ------------------------------------------

	@SuppressWarnings("deprecation")
	private void initMonthListBox() {
		monthListBox.addItem("-");
		Date date = new Date();
		date.setDate(1);
		for (int month = 0; month < 12; month++) {
			date.setMonth(month);
			monthListBox.addItem(MONTH_FORMAT.format(date));
		}
	}

	private void initTaxDeckPanel() {
		taxFullPanelIndex = taxDeckPanel.getWidgetIndex(taxFullPanel);
		taxEditPanelIndex = taxDeckPanel.getWidgetIndex(taxEditPanel);
		taxNonePanelIndex = taxDeckPanel.getWidgetIndex(taxNonePanel);
		quoteFullPanelIndex = quoteDeckPanel.getWidgetIndex(quoteFullPanel);
		quoteEditPanelIndex = quoteDeckPanel.getWidgetIndex(quoteEditPanel);
		quoteNonePanelIndex = quoteDeckPanel.getWidgetIndex(quoteNonePanel);
		
		taxDeckPanel.showWidget(taxEditPanelIndex);
		quoteDeckPanel.showWidget(quoteEditPanelIndex);
	}

	public String getExpression(String listValue, String src) {
		if (NONE.equals(listValue)) {
			return "0.00"; // Avoid null' 
		}
		if (ALL.equals(listValue)) {
			return "_P";
		}
		if (IPREM.equals(listValue)) {
			return "EXCESO_IPREM(_P)";
		}
		if (PRORATED.equals(listValue)) {
			return "PRORRATEAR()";
		}
		// It must be CUSTOM
		return src;
	}

	private void initProvided() {

		taxTextBox = new MyExpressionBox(); // TODO : UiBinder
		quoteTextBox = new MyExpressionBox(); // TODO : UiBinder
		paymentTextBox = new MyExpressionBox((MyExpressionBox) taxTextBox,
				(MyExpressionBox) quoteTextBox); // TODO : UiBinder

		conceptSuggestOracle = new MultiWordSuggestOracle();
		descriptionSuggestOracle = new MultiWordSuggestOracle();
		conceptSuggestBox = new SuggestBox(conceptSuggestOracle);
		descriptionSuggestBox = new SuggestBox(descriptionSuggestOracle);

		expressionSuggestOracle = new MultiWordSuggestOracle();
		
		paymentsDataGrid = providePaymentsDataGrid();
		
	}

	protected CustomDataGrid<com.esferalia.aon.gwt.payroll.shared.Payment> providePaymentsDataGrid() {
		
		payments = Collections.emptyList();
		
		/*
		 * Creates Payments DataGrid
		 * Set a key provider that provides a unique key for each payment.
		 */
		ProvidesKey<com.esferalia.aon.gwt.payroll.shared.Payment> keyProvider = HasIdKeyProvider.getKeyProvider();
		CustomDataGrid<com.esferalia.aon.gwt.payroll.shared.Payment> paymentsDataGrid = new CustomDataGrid<com.esferalia.aon.gwt.payroll.shared.Payment>(10, keyProvider);

		/*
		 * Do not refresh the headers every time the dataGrid is updated. The
		 * footer depends on the current dataGrid, so we do not disable auto
		 * refresh on the footer.
		 */
		paymentsDataGrid.setAutoHeaderRefreshDisabled(true);
		
		// Set the message to display when the table is empty.
		// TODO : selectDataGrid.setEmptyTableWidget(new Label());

		// Add a selection model to handle user selection.
//		paymentsSelectionModel = 
//				new MultiSelectionModel<com.esferalia.aon.gwt.payroll.shared.Payment>(keyProvider);
		paymentsSelectionModel = 
				new PaymentsSelectionModel(keyProvider);
		paymentsDataGrid.setSelectionModel(paymentsSelectionModel,
				DefaultSelectionEventManager.<com.esferalia.aon.gwt.payroll.shared.Payment> createCheckboxManager(0));
		

		// Checkbox column. This table will uses a checkbox column for  selection.
		Column<com.esferalia.aon.gwt.payroll.shared.Payment, Boolean> checkColumn = 
				new Column<com.esferalia.aon.gwt.payroll.shared.Payment, Boolean>(
				new CheckboxCell()) {
			@Override
			public Boolean getValue(com.esferalia.aon.gwt.payroll.shared.Payment payment) {
				return paymentsSelectionModel.isSelected(payment);
			}
		};

		paymentsDataGrid.addColumn(checkColumn);
		paymentsDataGrid.setColumnWidth(checkColumn, "40px");
		
		Column<com.esferalia.aon.gwt.payroll.shared.Payment, String> nameColumn =
				new Column<com.esferalia.aon.gwt.payroll.shared.Payment, String>(
				new TextCell()) {
			@Override
			public String getValue(com.esferalia.aon.gwt.payroll.shared.Payment payment) {
				if (AonStringUtils.isBlank(payment.getName()))
					return payment.getDescription();
				return null == payment.getDescription() ? " (" + payment.getName() + ")" : payment.getDescription() + " (" + payment.getName() + ")";
			}
		};
		paymentsDataGrid.addColumn(nameColumn);
		
		paymentsDataGrid.addStyleName(AON.AON_WIDTH_ALL);
		paymentsDataGrid.getElement().getStyle()
				.setPropertyPx("minHeight", Window.getClientHeight() / 6);
		paymentsDataGrid.setWidth("100%");
			
		new ListDataProvider<com.esferalia.aon.gwt.payroll.shared.Payment>(Collections.emptyList()).addDataDisplay(paymentsDataGrid);
		
		return paymentsDataGrid;
	}

	private String getListValue(String expression) {
		if (StringUtils.isEmpty(expression))
			return NONE;
		if (StringUtils.equals(getName(), expression))
			return ALL;
		if (concept != null && expression.equals(concept.getName()))
			return ALL;
		if ("_P".equals(expression))
			return ALL;
		if (StringUtils.equals(getExpression(), expression))// TODO:
			return ALL;
		if ("_P/12".equals(expression))
			return PRORATED;
		if ("PRORRATEAR()".equals(expression))
			return PRORATED;
		if ("PRORRATEAR(_P)".equals(expression))
			return PRORATED;

		try {
			if (Double.parseDouble(expression) == 0.00)
				return NONE;
		} catch (Exception e) {
		}

		return CUSTOM;
	}

	private String getMyExpression(String expression) {
		String listValue = getListValue(expression);
		return getExpression(listValue, expression);
	}

	private void initTypeListBox() {
		// typeListBox.addItem("-");
		for (com.esferalia.aon.gwt.payroll.shared.Payment.Type type : com.esferalia.aon.gwt.payroll.shared.Payment.Type
				.values())
			if (!StringUtils.isBlank(type.getDescription()))
				typeListBox.addItem(type.getDescription(),
						Integer.toString(type.getCode()));
	}

	private void initReceiptListBox() {
		for (com.esferalia.aon.gwt.payroll.shared.Salary.Type type : com.esferalia.aon.gwt.payroll.shared.Salary.Type
				.values())
			receiptListBox.addItem(type.getDescription());
	}

	private void onChangeConcept() {
		conceptSuggestBox.setText(concept == null ? null : concept.getName());
		conceptDescriptionLabel.setVisible(concept != null);
		conceptDescriptionLabel.setText(concept == null ? null : concept
				.getDescription());

		showOrHideResetTaxButton();
		showOrHideResetQuoteButton();
		showOrHideFxPaymentButton();
		showOrHideResetPaymentButton();
		showOrHideResetDescriptionButton();

	}

	private void showFxDialog(final MyExpressionBox textBox) {
		final FxDialog fxDialog = new FxDialog(contextProvider);
		fxDialog.setExpression(textBox.getExpression());
		fxDialog.center();
		fxDialog.show();

		fxDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				textBox.setFocus(true);
				if (fxDialog.isAccepted()) {
					textBox.setText(fxDialog.getExpression());
					//textBox.setExpression(fxDialog.getExpression(), true);
				}
			}
		});

	}

	private void selectByValue(ListBox listBox, String value) {
		for (int i = 0; i < listBox.getItemCount(); i++) {
			if (value.equals(listBox.getValue(i))) {
				listBox.setSelectedIndex(i);
				return;
			}
		}
	}

	private String getValueSelected(ListBox listBox) {
		return listBox.getValue(listBox.getSelectedIndex());
	}

	private void showOrHideResetTypeButton() {
		resetTypeButton.setVisible(concept != null
				&& concept.getType() != getType());
	}

	private void showOrHideResetTaxButton() {
		boolean visible = concept != null
				&& !StringUtils.equals(
						getMyExpression(concept.getIrpfExpression()),
						getIrpfExpression());
		resetTaxButton.setVisible(visible);
	}

	private void showOrHideResetQuoteButton() {
		boolean visible = concept != null
				&& !StringUtils.equals(
						getMyExpression(concept.getQuoteExpression()),
						getQuoteExpression());
		resetQuoteButton.setVisible(visible);
	}

	private void showOrHideResetDescriptionButton() {
		resetDescriptionButton.setVisible(concept != null
				&& !StringUtils.equals(concept.getDescription(),
						descriptionSuggestBox.getText()));
	}

	private void showOrHideResetPaymentButton() {
		resetPaymentButton.setVisible(concept != null
				&& !StringUtils.equals(concept.getExpression(),
					((MyExpressionBox) paymentTextBox).expression)
				&& paymentTextBox.isEnabled());
	}

	private void showOrHideFxPaymentButton() {
		fxPaymentButton.setVisible(paymentTextBox.isEnabled());
	}

	private void enableCustomTax(boolean enabled) {
		taxTextBox.setEnabled(enabled);
		fxTaxButton.setVisible(enabled);
	}

	private void enableCustomQuote(boolean enabled) {
		quoteTextBox.setEnabled(enabled);
		fxQuoteButton.setVisible(enabled);
	}

	private void enableOrDisableMonth() {
		int rows = mainGrid.getRowCount();
		com.google.gwt.user.client.Element monthRow = mainGrid.getRowFormatter().getElement(rows-1);
		com.esferalia.aon.gwt.payroll.shared.Payment.Type type = getType();
		UIObject.setVisible(monthRow, type== Type.CRA_0005);
//		UIObject.setVisible(monthRow, type == Type.CRA_0004 || type== Type.CRA_0005);
	}

	private void enableOrDisableTaxAndQuote() {
		int rows = mainGrid.getRowCount();
		com.google.gwt.user.client.Element quoteRow = mainGrid.getRowFormatter().getElement(rows-2);
		com.google.gwt.user.client.Element taxRow = mainGrid.getRowFormatter().getElement(rows-3);
		
		
		if ( taxEditableAndQuoteFull() ) {
			taxDeckPanel.showWidget(taxEditPanelIndex);
			UIObject.setVisible(taxRow, true );
			quoteDeckPanel.showWidget(quoteFullPanelIndex);
			UIObject.setVisible(quoteRow, false );
		} else if ( taxAndQuoteFull() ) {
			taxDeckPanel.showWidget(taxFullPanelIndex);
			UIObject.setVisible(taxRow, false );
			quoteDeckPanel.showWidget(quoteFullPanelIndex);
			UIObject.setVisible(quoteRow, false );
		} else if ( taxAndQuoteNone() ) {
			taxDeckPanel.showWidget(taxNonePanelIndex);
			UIObject.setVisible(taxRow, false );
			quoteDeckPanel.showWidget(quoteNonePanelIndex);
			UIObject.setVisible(quoteRow, false );
		} else {
			taxDeckPanel.showWidget(taxEditPanelIndex);
			UIObject.setVisible(taxRow, true );
			quoteDeckPanel.showWidget(quoteEditPanelIndex);
			UIObject.setVisible(quoteRow, true );
		}
		
	}
	
	private void enableOrDisablePayments() {
		Type type = getType();
		boolean visible = type == Type.CRA_0004 
				|| type == Type.CRA_0055
				|| type == Type.CRA_0056 ;
		paymentsGrid.setVisible(visible);
		if ( !visible )
			return;

		filterAvailablePayments();
	}
	
	private void filterAvailablePayments() {
		List<com.esferalia.aon.gwt.payroll.shared.Payment> availablePayments = 
				payments.stream().filter(p -> p.getType() != getType()).collect(Collectors.toList());
		
		//Filter availablePayments only one entry for each type
		availablePayments = filterAvailablePayments(availablePayments);
		
		new ListDataProvider<com.esferalia.aon.gwt.payroll.shared.Payment>(availablePayments).addDataDisplay(paymentsDataGrid);
	}

	private List<com.esferalia.aon.gwt.payroll.shared.Payment> filterAvailablePayments(
			List<com.esferalia.aon.gwt.payroll.shared.Payment> availablePayments) {
		

		List<com.esferalia.aon.gwt.payroll.shared.Payment> availablePaymentsResult = new ArrayList<>();
		
		for(com.esferalia.aon.gwt.payroll.shared.Payment payment : availablePayments){
			if(availablePaymentsResult.isEmpty() || !availablePaymentsContainsType(payment.getName(), availablePaymentsResult))
				availablePaymentsResult.add(payment);
		}
		
		return availablePaymentsResult;
	}

	private boolean availablePaymentsContainsType(String searchName,
			List<com.esferalia.aon.gwt.payroll.shared.Payment> availablePaymentsResult) {
		
		for(com.esferalia.aon.gwt.payroll.shared.Payment payment : availablePaymentsResult){
			if(payment.getName() == searchName)
				return true;
		}
		
		return false;
	}

	private boolean taxEditableAndQuoteFull() {
		com.esferalia.aon.gwt.payroll.shared.Payment.Type type = getType();
		return ( type == com.esferalia.aon.gwt.payroll.shared.Payment.Type.CRA_0013 );
	}

	private boolean taxAndQuoteFull() {
		com.esferalia.aon.gwt.payroll.shared.Payment.Type type = getType();
		return ( type.isBBCCIncluded() && !type.isBBCCExcluded() );
	}
	
	private boolean taxAndQuoteNone() {
		com.esferalia.aon.gwt.payroll.shared.Payment.Type type = getType();
		return ( !type.isBBCCIncluded() && type.isBBCCExcluded() );
	}
	
	private void loadExpressionSuggestOracle() {

		expressionSuggestOracle.clear();

		class ContextCallback implements AsyncCallback<ContextDescriptor> {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(ContextDescriptor descriptor) {
				expressionSuggestOracle.addAll(descriptor.getVariables());
			}
		}
		;
		contextProvider.getContext(new ContextCallback());
	}
	


	private static String getVariableName(com.esferalia.aon.gwt.payroll.shared.Payment payment) {
		String name = payment.getName();
		if ( AonStringUtils.isNotBlank(name))
			return name;
		
		String description = payment.getDescription();
		if ( AonStringUtils.isNotBlank(description) )
			return description.toUpperCase()
					.replaceAll("\\s", "_")
					.replaceAll("\u00c1", "A")
					.replaceAll("\u00c9", "E")
					.replaceAll("\u00cd", "I")
					.replaceAll("\u00d3", "O")
					.replaceAll("\u00da", "U")
					.replaceAll("\u00dc", "U")

					.replaceAll("\u00d1", "N")
					.replaceAll("\\W", "")
					;
		return null;
	}
	
	
 }
