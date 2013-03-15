package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.EvalException;
import com.esferalia.aon.gwt.payroll.shared.EvalSyntaxErrorException;
import com.esferalia.aon.gwt.payroll.shared.EvalWarning;
import com.esferalia.aon.gwt.payroll.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.VariableDescriptor;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.InvocationException;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class FxDialog extends CustomDialog {

	private static final DateTimeFormat DATE_SHORT = DateTimeFormat
			.getFormat(PredefinedFormat.DATE_SHORT);

	private static final NumberFormat CURRENCY_FORMAT = NumberFormat
			.getFormat("#,##0.00");

	static interface IContextProvider {

		void getContext(AsyncCallback<ContextDescriptor> callback);
		void eval(String expression, AsyncCallback<Double> callback);
	}
	

	interface Binder extends UiBinder<Widget, FxDialog> {

	}

	class ContextCallback implements AsyncCallback<ContextDescriptor> {

		@Override
		public void onFailure(Throwable caught) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSuccess(ContextDescriptor result) {
			FxDialog.this.contextDescriptor = result;

			FxDialog.this.categoryListBox.setSelectedIndex(0);
			onCategoryChanged(null);
			FxDialog.this.functionListBox.setSelectedIndex(0);
			onFunctionChanged(null);
		}

	}

	class ExpressionCallback extends Timer implements AsyncCallback<Double> {

		@Override
		public void run() {
			String expression = expressionTextArea.getText();
			if ( StringUtils.isEmpty(expression) ) {
				cleanError();
				cleanResult();
			}
			else {
				contextProvider.eval(expression, this);
			}
		}

		@Override
		public void onFailure(Throwable caught) {
			cleanResult();
			// Convenient way to find out which exception was thrown.
			try {
				throw caught;
			} catch (InvocationException e) {
				setError(e.getMessage());
				// the call didn't complete cleanly
			} catch (EvalWarning e) {
				setWarning(e.getMessage());
				// one of the 'throws' from the original method
			} catch (EvalSyntaxErrorException e) {
				setError(e.getMessage());
				// one of the 'throws' from the original method
			} catch (EvalException e) {
				errorLabel.setHTML(e.getMessage());
				// one of the 'throws' from the original method
			} catch (Throwable e) {
				// last resort -- a very unexpected exception
				setError("Error desconocido." );
			}

		}

		@Override
		public void onSuccess(Double result) {
			cleanError();
			resultLabel.setText(result.toString());
		}

	}

	private static final Binder binder = GWT.create(Binder.class);

	static enum Category {
		ALL("Todos", Object.class), BOOL("L\u00f3gicas", Boolean.class), MATH(
				"Matem\u00e1ticas", Number.class), DATE("Fecha", Date.class), TEXT(
				"Texto", String.class);

		String name;
		Class<?> type;

		private Category(String name, Class<?> type) {
			this.name = name;
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public Class<?> getType() {
			return type;
		}

		static Category getByName(String name) {
			for (Category category : Category.values()) {
				if (category.name.equals(name))
					return category;
			}
			return null;
		}
	}

	@UiField
	ListBox categoryListBox;
	@UiField
	ListBox functionListBox;

	@UiField
	Label nameLabel;
	@UiField
	HTML resultLabel;
	@UiField
	InlineHTML valueLabel;
	@UiField
	Label syntaxLabel;
	@UiField
	InlineHTML errorLabel;
	@UiField
	InlineHTML descriptionLabel;
	@UiField
	TextArea expressionTextArea;

	@UiField
	Button cancelButton;
	@UiField
	Button acceptButton;
	
	private boolean accepted;

	private IContextProvider contextProvider;
	private ContextDescriptor contextDescriptor;

	private ContextCallback contextCallback;
	private ExpressionCallback expressionCallback;

	public FxDialog(IContextProvider contextProvider) {

		setCaption("Asistente");
		setWidget(binder.createAndBindUi(this));

		for (Category category : Category.values()) {
			categoryListBox.addItem(category.getName());
		}
		this.contextProvider = contextProvider;
		this.contextCallback = new ContextCallback();
		this.expressionCallback = new ExpressionCallback();

	}

	@Override
	public void show() {
		contextProvider.getContext(contextCallback);
		evalExpression(0);
		super.show();

	}
	
	
	public boolean isAccepted() {
		return accepted;
	}

	public void setExpression(String expression) {
		expressionTextArea.setText(expression);
	}
	
	public String getExpression(){
		return expressionTextArea.getText();
	}
	
	@UiHandler("cancelButton")
	void onCancelButtonClick(ClickEvent event) {
		hide();
	}

	@UiHandler("acceptButton")
	void onAcceptButtonClick(ClickEvent event) {
		accepted = true;
		hide();
	}
	

	@UiHandler("categoryListBox")
	void onCategoryChanged(ChangeEvent event) {
		int index = categoryListBox.getSelectedIndex();
		String name = categoryListBox.getItemText(index);
		Category category = Category.getByName(name);
		loadContext4Category(category);
	}

	@UiHandler("functionListBox")
	void onFunctionChanged(ChangeEvent event) {
		int index = functionListBox.getSelectedIndex();
		String varName = functionListBox.getItemText(index);
		nameLabel.setText(varName);
		VariableDescriptor var = contextDescriptor.get(varName);
		syntaxLabel.setText(varName + var.getSyntax());

		String value = var.getValue();
		if (value != null) {
			if (value.equalsIgnoreCase(Boolean.TRUE.toString()))
				value = "VERDADERO";
			else if (value.equalsIgnoreCase(Boolean.FALSE.toString()))
				value = "FALSO";
			valueLabel.setHTML(value);
		} else {
			valueLabel.setHTML("&nbsp;");
		}
		String description = var.getDescription();
		descriptionLabel.setHTML(description != null ? description : "&nbsp;");

	}

	@UiHandler("functionListBox")
	void onFunctionDoubleClick(DoubleClickEvent event) {
		int index = functionListBox.getSelectedIndex();
		String varName = functionListBox.getItemText(index);
		VariableDescriptor var = contextDescriptor.get(varName);
		int curPos = expressionTextArea.getCursorPos();
		String expression = expressionTextArea.getText();
		StringBuffer expressionBuffer = new StringBuffer(expression);
		expressionBuffer.insert(curPos, varName + var.getSyntax());
		expressionTextArea.setValue(expressionBuffer.toString());
		onExpressionKeyUp(null);
		evalExpression(0); // eval now ???
	}

	@UiHandler("expressionTextArea")
	void onExpressionKeyUp(KeyUpEvent event) {
		evalExpression(1000);
	}

	void evalExpression(int milliseconds) {
		expressionCallback.cancel();
		expressionCallback.schedule(milliseconds);
	}

	void loadContext4Category(Category category) {

		functionListBox.clear();

		if (contextDescriptor == null)
			return;

		Class<?> type = category.getType();
		List<String> vars = new ArrayList<String>();
		for (String varName : contextDescriptor.getVariables()) {
			VariableDescriptor var = contextDescriptor.get(varName);
			if (type == Object.class || type == var.getType()) {
				vars.add(varName);
			}
		}

		Collections.sort(vars);

		for (String var : vars) {
			VariableDescriptor descriptor = contextDescriptor.get(var);
			functionListBox
					.addItem(
							var,
							descriptor.getClass() == VariableDescriptor.class ? "VariableDescriptor"
									: "FunctionDescriptor");
		}
	}
	
	void cleanResult() {
		resultLabel.setHTML("&nbsp");
	}

	void cleanError() {
		errorLabel.setHTML("&nbsp");
		errorLabel.setStyleName("");
	}

	void setError(String html) {
		errorLabel.setHTML(html);
		errorLabel.addStyleName("gwt-Error");
	}

	void setWarning(String html) {
		errorLabel.setHTML(html);
		errorLabel.addStyleName("gwt-Warn");
	}
}
