package com.esferalia.aon.gwt.payroll.client;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.common.shared.EvalException;
import com.esferalia.aon.gwt.common.shared.EvalSyntaxErrorException;
import com.esferalia.aon.gwt.common.shared.EvalWarning;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.client.FxDialog.IContextProvider;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.NumberVariable;
import com.esferalia.aon.gwt.payroll.shared.Result;
import com.esferalia.aon.gwt.payroll.shared.SpecialExpresion;
import com.esferalia.aon.gwt.payroll.shared.Variable;
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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.InvocationException;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

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

	private class MyExpressionBox extends ExpressionBox implements BlurHandler,
			FocusHandler, AsyncCallback<List<Result>> {

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
		}

		@Override
		public void onFocus(FocusEvent event) {
			setText(expression);
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
				setText(result);
			}
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
	ListBox typeListBox;
	@UiField
	Button resetTypeButton;
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

	@UiField(provided = true)
	MyExpressionBox paymentTextBox;
	@UiField
	Button resetPaymentButton;
	@UiField
	ListBox taxListBox;
	@UiField
	Button resetTaxButton;
	@UiField
	ListBox quoteListBox;
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

	private NumberFormat numberFormat;
	private IContextProvider contextProvider;

	private MultiWordSuggestOracle conceptSuggestOracle;
	private MultiWordSuggestOracle descriptionSuggestOracle;
	private MultiWordSuggestOracle expressionSuggestOracle;
	private com.esferalia.aon.gwt.payroll.shared.Payment concept;

	public Payment() {
		initProvided();
		initWidget(binder.createAndBindUi(this));
		initTypeListBox();
		initReceiptListBox();
		initMonthListBox();
		showReceipt(false);
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
		paymentTextBox.setExpression(payment);
		showOrHideResetPaymentButton();
		paymentTextBox.enable(!SpecialExpresion.isReadOnly(payment));
	}

	public String getIrpfExpression() {
		String selected = getValueSelected(taxListBox);
		return getExpression(selected, taxTextBox.expression);

	}

	public void setIrpfExpression(String expression) {
		String listValue = getListValue(expression);
		selectByValue(taxListBox, listValue);
		onTaxListBoxChange(null);
		taxTextBox.setExpression(getExpression(listValue, expression));
		showOrHideResetTaxButton();
		//taxTextBox.enable(!SpecialExpresion.isReadOnly(expression));
		enableCustomTax(listValue.equals(CUSTOM) && !SpecialExpresion.isReadOnly(expression));
	}

	public String getQuoteExpression() {
		String selected = getValueSelected(quoteListBox);
		return getExpression(selected, quoteTextBox.expression);
	}

	public void setQuoteExpression(String expression) {
		String listValue = getListValue(expression);
		selectByValue(quoteListBox, listValue);
		onQuoteListBoxChange(null);
		quoteTextBox.setExpression(getExpression(listValue, expression));
		showOrHideResetQuoteButton();
		//quoteTextBox.enable(!SpecialExpresion.isReadOnly(expression));
		enableCustomQuote(listValue.equals(CUSTOM) && !SpecialExpresion.isReadOnly(expression));
	}

	public void setType(com.esferalia.aon.gwt.payroll.shared.Payment.Type type) {

		for (int i = 0; i < typeListBox.getItemCount(); i++)
			if (Integer.valueOf(typeListBox.getValue(i)) == type.getCode())
				typeListBox.setSelectedIndex(i);

		showOrHideResetTypeButton();
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

	public void setAvailablePaymens(
			List<com.esferalia.aon.gwt.payroll.shared.Payment> availablePaymens) {

		for (com.esferalia.aon.gwt.payroll.shared.Payment payment : availablePaymens) {
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
					//textBox.setValue(fxDialog.getExpression());
					textBox.setExpression(fxDialog.getExpression(), true);
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
						((MyExpressionBox) paymentTextBox).expression));
	}

	private void enableCustomTax(boolean enabled) {
		taxTextBox.setEnabled(enabled);
		fxTaxButton.setVisible(enabled);
	}

	private void enableCustomQuote(boolean enabled) {
		quoteTextBox.setEnabled(enabled);
		fxQuoteButton.setVisible(enabled);
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
}
