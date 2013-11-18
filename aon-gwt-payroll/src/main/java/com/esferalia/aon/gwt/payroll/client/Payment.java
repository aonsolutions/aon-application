package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.payroll.client.FxDialog.IContextProvider;
import com.esferalia.aon.gwt.payroll.shared.EvalException;
import com.esferalia.aon.gwt.payroll.shared.EvalSyntaxErrorException;
import com.esferalia.aon.gwt.payroll.shared.EvalWarning;
import com.esferalia.aon.gwt.payroll.shared.StringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
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
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.Panel;
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

	private static final DateTimeFormat MONTH_FORMAT = DateTimeFormat
			.getFormat(PredefinedFormat.MONTH);

	private static boolean equalsExpressions(String s1, String s2) {
		if (s1 == s2)
			return true;
		if (s1 == null)
			return false;
		if (s2 == null)
			return false;

		return s1.trim().equals(s2.trim());
	}

	interface Binder extends UiBinder<Widget, Payment> {

	}

	private static final Binder binder = GWT.create(Binder.class);

	private class ExpressionTextBox extends TextBox implements BlurHandler,
			FocusHandler, AsyncCallback<Double> {

		private String result;
		private String expression;
		
		private ExpressionTextBox parent;
		private ExpressionTextBox childs[];

		public ExpressionTextBox(ExpressionTextBox... childs) {
			addBlurHandler(this);
			addFocusHandler(this);
			this.childs = childs;
			for (ExpressionTextBox child : childs)
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
		public void onSuccess(Double d) {
			result = format(d);
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
				eval( fire );
			} else {
				setText(result);				
			}
		}

		String format(Double d) {
			return d != null ? numberFormat.format(d) : null;
		}

		void eval(boolean fire) {
			contextProvider.eval(getParentExpression() + expression, this);
			if ( fire ){
				for (ExpressionTextBox child : childs) {
					child.eval( fire);
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
		
		String getParentExpression(){
			
			if ( parent == null )
				return "";
			
			StringBuffer buffer = new StringBuffer();
			buffer.append("_P = ");
			String expr = parent.getExpression();
			buffer.append( StringUtils.isEmpty(expr) ? "0.00" : expr);
			buffer.append(";");
			if ( concept != null ) 
				buffer.append(concept.getName() + " = _P;");
			return buffer.toString();
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
	TextBox paymentTextBox;
	@UiField
	Button resetPaymentButton;
	@UiField
	ListBox taxListBox;
	@UiField
	Button resetTaxButton;
	@UiField
	Button resetCustomTaxButton;
	@UiField
	ListBox quoteListBox;
	@UiField
	Button resetQuoteButton;
	@UiField
	Button resetCustomQuoteButton;
	@UiField
	ListBox monthListBox;

	@UiField
	DeckPanel taxPanel;
	@UiField
	DeckPanel quotePanel;

	@UiField
	Panel listTaxPanel;
	@UiField
	Panel customTaxPanel;
	@UiField
	Button fxTaxButton;
	@UiField(provided = true)
	TextBox taxTextBox;
	@UiField
	Button undoTaxButton;

	@UiField
	Panel listQuotePanel;
	@UiField
	Panel customQuotePanel;
	@UiField
	Button fxQuoteButton;
	@UiField(provided = true)
	TextBox quoteTextBox;
	@UiField
	Button undoQuoteButton;

	private NumberFormat numberFormat;
	private IContextProvider contextProvider;

	private MultiWordSuggestOracle conceptSuggestOracle;
	private MultiWordSuggestOracle descriptionSuggestOracle;
	private com.esferalia.aon.gwt.payroll.shared.Payment concept;

	public Payment() {
		initProvided();
		initWidget(binder.createAndBindUi(this));
		initTypeListBox();
		initReceiptListBox();
		initMonthListBox();
		initTaxPanel();
		initQuotePanel();
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

	public String getPaymentExpression() {
		return ((ExpressionTextBox) paymentTextBox).getExpression();
	}

	public void setPaymentExpression(String payment) {
		((ExpressionTextBox) paymentTextBox).setExpression(payment);
		showOrHideResetPaymentButton();
	}

	public String getIrpfExpression() {
		String selected = getValueSelected(taxListBox);
		return getExpression(selected,
				((ExpressionTextBox) taxTextBox).expression);

	}

	public void setIrpfExpression(String expression) {
		String listValue = getListValue(expression);
		selectByValue(taxListBox, listValue);
		((ExpressionTextBox) taxTextBox).setExpression(getExpression(listValue,
				expression));
		showOrHideResetTaxButton();
	}

	public String getQuoteExpression() {
		String selected = getValueSelected(quoteListBox);
		return getExpression(selected,
				((ExpressionTextBox) quoteTextBox).expression);
	}

	public void setQuoteExpression(String expression) {
		String listValue = getListValue(expression);
		selectByValue(quoteListBox, listValue);
		((ExpressionTextBox) quoteTextBox).setExpression(getExpression(
				listValue, expression));
		showOrHideResetQuoteButton();
	}

	public void setType(com.esferalia.aon.gwt.payroll.shared.Payment.Type type) {
		typeListBox.setSelectedIndex(type == null ? 0 : type.ordinal() + 1);
		showOrHideResetTypeButton();
	}

	public com.esferalia.aon.gwt.payroll.shared.Payment.Type getType() {
		int index = typeListBox.getSelectedIndex();
		return index == 0 ? null
				: com.esferalia.aon.gwt.payroll.shared.Payment.Type.values()[index - 1];
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
		conceptSuggestBox.setEnabled(concept == null);
		onChangeConcept();
	}

	public void setContextProvider(IContextProvider contextProvider) {
		this.contextProvider = contextProvider;
	}

	// ------------------------------------------
	// Protected members
	// ------------------------------------------

	@UiHandler("taxListBox")
	void onTaxListBoxChange(ChangeEvent event) {
		String tax = getValueSelected(taxListBox);
		if (tax.equals(CUSTOM)) {
			showCustomTaxPanel(); /* Really don't changed anything. */
		} else {
			((ExpressionTextBox) taxTextBox).setExpression(getExpression(tax,
					null /* Not 'CUSTOM' */));
			showOrHideResetTaxButton();
		}

	}

	@UiHandler("quoteListBox")
	void onQuoteListBoxChange(ChangeEvent event) {
		String quote = getValueSelected(quoteListBox);
		if (quote.equals(CUSTOM)) {
			showCustomQuotePanel(); /* Really don't changed anything. */
		} else {
			((ExpressionTextBox) quoteTextBox).setExpression(getExpression(
					quote, null /* Not 'CUSTOM' */));
			showOrHideResetQuoteButton();
		}
	}

	@UiHandler("undoTaxButton")
	void onUndoTaxButtonClick(ClickEvent event) {
		selectByValue(taxListBox,
				getListValue(((ExpressionTextBox) taxTextBox).getExpression()));
		showListTaxPanel();
	}

	@UiHandler("undoQuoteButton")
	void onUndoQuoteButtonClick(ClickEvent event) {
		selectByValue(
				quoteListBox,
				getListValue(((ExpressionTextBox) quoteTextBox).getExpression()));
		showListQuotePanel();
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
		setPaymentExpression(concept.getExpression());
	}

	@UiHandler("taxTextBox")
	void onTaxTextBoxChange(BlurEvent event) {
		showOrHideResetTaxButton();
	}

	@UiHandler({ "resetTaxButton", "resetCustomTaxButton" })
	void onResetTaxButtonClick(ClickEvent event) {
		setIrpfExpression(concept.getIrpfExpression());
	}

	@UiHandler("quoteTextBox")
	void onQuoteTextBoxChange(BlurEvent event) {
		showOrHideResetQuoteButton();

	}

	@UiHandler({ "resetQuoteButton", "resetCustomQuoteButton" })
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
		showFxDialog((ExpressionTextBox)paymentTextBox);
	}

	@UiHandler("fxTaxButton")
	void onFxTaxButtonClick(ClickEvent event) {
		showFxDialog((ExpressionTextBox)taxTextBox);
	}

	@UiHandler("fxQuoteButton")
	void onFxQuoteButtonClick(ClickEvent event) {
		showFxDialog((ExpressionTextBox)quoteTextBox);
	}

	// ------------------------------------------
	// Private members
	// ------------------------------------------
	private void initTaxPanel() {
		showListTaxPanel();
	}

	private void initQuotePanel() {
		showListQuotePanel();
	}

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
			return null;
		}
		if (ALL.equals(listValue)) {
			return "_P";
		}
		if (IPREM.equals(listValue)) {
			return "EXCESO_IPREM(_P)";
		}
		// It must be CUSTOM
		return src;
	}

	private void initProvided() {

		taxTextBox = new ExpressionTextBox(); // TODO : UiBinder
		quoteTextBox = new ExpressionTextBox(); // TODO : UiBinder
		paymentTextBox = new ExpressionTextBox((ExpressionTextBox) taxTextBox,
				(ExpressionTextBox) quoteTextBox); // TODO : UiBinder

		conceptSuggestOracle = new MultiWordSuggestOracle();
		descriptionSuggestOracle = new MultiWordSuggestOracle();
		conceptSuggestBox = new SuggestBox(conceptSuggestOracle);
		descriptionSuggestBox = new SuggestBox(descriptionSuggestOracle);

	}

	private String getListValue(String expression) {
		if (StringUtils.isEmpty(expression))
			return NONE;
		if (concept != null && expression.equals(concept.getName()))
			return ALL;
		if (expression.equals("_P"))
			return ALL;
		if (expression.equals("EXCESO_IPREM()"))
			return IPREM;
		if (expression.equals(getPaymentExpression())) // TODO:
			return ALL;

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
		typeListBox.addItem("-");
		for (com.esferalia.aon.gwt.payroll.shared.Payment.Type type : com.esferalia.aon.gwt.payroll.shared.Payment.Type
				.values())
			typeListBox.addItem(type.getDescription());
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

	private void showFxDialog(final ExpressionTextBox textBox) {
		final FxDialog fxDialog = new FxDialog(contextProvider);
		fxDialog.setExpression(textBox.getExpression());
		fxDialog.setWidth(Window.getClientWidth() / 2 + "px");
		fxDialog.center();
		fxDialog.show();

		fxDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				textBox.setFocus(true);
				if (fxDialog.isAccepted()) {
					textBox.setValue(fxDialog.getExpression());
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

	private void showListTaxPanel() {
		taxPanel.showWidget(taxPanel.getWidgetIndex(listTaxPanel));
	}

	private void showListQuotePanel() {
		quotePanel.showWidget(quotePanel.getWidgetIndex(listQuotePanel));
	}

	private void showCustomTaxPanel() {
		taxPanel.showWidget(taxPanel.getWidgetIndex(customTaxPanel));
	}

	private void showCustomQuotePanel() {
		quotePanel.showWidget(quotePanel.getWidgetIndex(customQuotePanel));
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
		resetCustomTaxButton.setVisible(visible);
	}

	private void showOrHideResetQuoteButton() {
		boolean visible = concept != null
				&& !StringUtils.equals(
						getMyExpression(concept.getQuoteExpression()),
						getQuoteExpression());
		resetQuoteButton.setVisible(visible);
		resetCustomQuoteButton.setVisible(visible);
	}

	private void showOrHideResetDescriptionButton() {
		resetDescriptionButton.setVisible(concept != null
				&& !StringUtils.equals(concept.getDescription(),
						descriptionSuggestBox.getText()));
	}

	private void showOrHideResetPaymentButton() {
		resetPaymentButton.setVisible(concept != null
				&& !StringUtils.equals(concept.getExpression(),
						((ExpressionTextBox) paymentTextBox).expression));
	}
	
	private void showReceipt( boolean show) {
		Element el = mainGrid.getRowFormatter().getElement(1);
		if ( show ) 
			el.getStyle().clearDisplay();
		else
			el.getStyle().setDisplay(Display.NONE);
	}
}
