package com.esferalia.aon.gwt.payroll.client;

import static com.esferalia.aon.gwt.common.client.AON.format;
import static com.esferalia.aon.gwt.payroll.client.Constants.EXPRESSION_MAX_LENGTH;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.hibernate.event.def.OnReplicateVisitor;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.shared.EvalException;
import com.esferalia.aon.gwt.common.shared.EvalSyntaxErrorException;
import com.esferalia.aon.gwt.common.shared.EvalWarning;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.common.shared.UnknownVariablesWarning;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.Result;
import com.esferalia.aon.gwt.payroll.shared.StringVariable;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.esferalia.aon.gwt.payroll.shared.VariableDescriptor;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.editor.client.IsEditor;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.InvocationException;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.mchange.v2.async.CarefulRunnableQueue;

public class FxDialog extends CustomDialog {

	private static final DateTimeFormat DATE_SHORT = DateTimeFormat
			.getFormat(PredefinedFormat.DATE_SHORT);

	private static final NumberFormat CURRENCY_FORMAT = NumberFormat
			.getFormat("#,##0.00");

	public static interface IContextProvider {
		boolean isEditable(String name);

		void getContext(AsyncCallback<ContextDescriptor> callback);

		void eval(String expression, List<Variable> vars,
				AsyncCallback<List<Result>> callback);

	}

	interface Binder extends UiBinder<Widget, FxDialog> {

	}

	interface Style extends CssResource {
		@ClassName("variable-label")
		String variableLabel();

		@ClassName("variable-warn")
		String variableWarn();

		@ClassName("variable-changed")
		String variableChanged();

	}

	class VariableChangeHandler implements ValueChangeHandler<String> {

		private String name;

		public VariableChangeHandler(String name, HasValue<String> hasValue) {
			this.name = name;
			hasValue.addValueChangeHandler(this);
		}

		@Override
		public void onValueChange(ValueChangeEvent<String> event) {
			FxDialog.this.addVariable(name, event.getValue());
			FxDialog.this.evalExpression(0);
		}
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

			FxDialog.this.evalExpression(0);

		}

	}

	class ExpressionCallback extends Timer implements
			AsyncCallback<List<Result>> {
		private String expression ;
		@Override
		public void run() {
			expression = expressionCodeArea.getText();
			if (StringUtils.isEmpty(expression)) {
				cleanError();
				cleanResult();
				cleanContext();
			} else {
				contextProvider.eval(expression, FxDialog.this.vars, this);
			}
		}

		@Override
		public void onFailure(Throwable caught) {
			cleanError();
			cleanResult();
			cleanContext();
			// Convenient way to find out which exception was thrown.
			try {
				throw caught;
			} catch (InvocationException e) {
				setError(e.getMessage());
				// the call didn't complete cleanly
			} catch (UnknownVariablesWarning w) {
				for (String name : w.getNames())
					dumpUnknownVariable(name);
				// one of the 'throws' from the original method
			} catch (EvalWarning w) {
				setWarning(w.getMessage());
				// one of the 'throws' from the original method
			} catch (EvalSyntaxErrorException e) {
				setError(e.getMessage());
				// one of the 'throws' from the original method
			} catch (EvalException e) {
				errorLabel.setHTML(e.getMessage());
				// one of the 'throws' from the original method
			} catch (Throwable e) {
				// last resort -- a very unexpected exception
				setError("Error desconocido.");
			}

		}

		@Override
		public void onSuccess(List<Result> results) {
			cleanError();
			cleanContext();

			double total = 0.00;

			for (Result result : results) {
				total += result.getResult().doubleValue();
				dumpContext(result.getContext()); // TODO
			}

			resultLabel.setText(format(total));
			
			checkLenght();
			
		}
		
		private void checkLenght() {
			boolean tooLong = ( expression.length() > EXPRESSION_MAX_LENGTH );
			if (tooLong) {
				setError("F\u00f3rmula demasiado larga ( l\u00edmite "
						+ EXPRESSION_MAX_LENGTH + " caracteres ).");
			}
			acceptButton.setEnabled(expression.length() <= EXPRESSION_MAX_LENGTH);
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
	Style style;

	@UiField
	ListBox categoryListBox;
	@UiField
	ListBox functionListBox;

	@UiField
	FlexTable contextTable;

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
	ExpressionCodeArea expressionCodeArea;

	@UiField
	Button editButton;
	@UiField
	Button cancelButton;
	@UiField (provided = true)
	Button acceptButton;
	

	List<Variable> vars;

	private boolean accepted;
	

	private IContextProvider contextProvider;
	private ContextDescriptor contextDescriptor;

	private ContextCallback contextCallback;
	private ExpressionCallback expressionCallback;

	public FxDialog(IContextProvider contextProvider) {

		setCaption("Asistente");

		ensureDebugId("fxDialog");
		
		initAcceptButton();

		setWidget(binder.createAndBindUi(this));
		

		for (Category category : Category.values()) {
			categoryListBox.addItem(category.getName());
		}
		this.vars = new ArrayList<Variable>();
		this.contextProvider = contextProvider;
		this.contextCallback = new ContextCallback();
		this.expressionCallback = new ExpressionCallback();


	}

	private void initAcceptButton() {
		acceptButton = new Button();
		acceptButton.setStyleName(AON.CSS.aonOkButtonSmall());
		acceptButton.setText( AON.MSG.accept());
	}

	@Override
	public void show() {
		contextProvider.getContext(contextCallback);
		// evalExpression(0);
		super.show();
		
	}

	public boolean isAccepted() {
		return accepted;
	}

	public void setExpression(String expression) {
		expressionCodeArea.setText(expression);
	}

	public String getExpression() {
		return expressionCodeArea.getText();
	}

	public List<Variable> getVariables() {
		return vars;
	}

	// ------------------------------------------------------------- UiHandlers

	@UiHandler("cancelButton")
	void onCancelButtonClick(ClickEvent event) {
		hide();
	}

	@UiHandler("acceptButton")
	void onAcceptButtonClick(ClickEvent event) {
		accepted = true;
		hide();
	}

	@UiHandler("editButton")
	void onEditButtonClick(ClickEvent event) {
		expressionCodeArea.setAdvancedMode(!expressionCodeArea.getAdvancedMode());
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
		int curPos = expressionCodeArea.getCursorPos();
		String expression = expressionCodeArea.getText();
		StringBuffer expressionBuffer = new StringBuffer(expression);
		expressionBuffer.insert(curPos, varName + var.getSyntax());
		expressionCodeArea.setValue(expressionBuffer.toString());
		evalExpression(0); // eval now ???
		
	}

	@UiHandler({"expressionCodeArea"})
	void onExpressionChange(ValueChangeEvent<String> event) {
		evalExpression(1000);
	}

	
	
	
	// ---------------------------------------------------------------- Private

	private void evalExpression(int milliseconds) {
		expressionCallback.cancel();
		expressionCallback.schedule(milliseconds);
	}

	private void loadContext4Category(Category category) {

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

	private void cleanResult() {
		resultLabel.setHTML("&nbsp");
	}

	private void cleanError() {
		errorLabel.setHTML("&nbsp");
		errorLabel.setStyleName("");
	}

	private void setError(String html) {
		errorLabel.setHTML(html);
		errorLabel.addStyleName("gwt-Error");
	}

	private void setWarning(String html) {
		errorLabel.setHTML(html);
		errorLabel.addStyleName("gwt-Warn");
	}

	private void cleanContext() {
		contextTable.removeAllRows();
	}

	private void dumpContext(List<Variable> ctx) {
		for (Variable var : ctx)
			dumpVariable(var);
	}

	private <T extends IsWidget & HasValue<String> & HasEnabled> void dumpVariable(
			Variable var) {

		if (SalaryDraft.skipVariable(var))
			return;

		String name = var.getName();
		Label label = new Label(name);
		label.setTitle(getDescription(name));
		label.addStyleName(style.variableLabel());

		int row = contextTable.getRowCount();
		contextTable.setWidget(row, 0, label);

		T editor = SalaryDraft.createEditor(var);
		if (var.getValue() != null)
			editor.setValue(String.valueOf(var.getValue()));
		editor.setEnabled(contextProvider.isEditable(name));

		contextTable.setWidget(row, 1, editor);
		new VariableChangeHandler(name, editor);

	}

	private void dumpUnknownVariable(final String name) {

		if (SalaryDraft.skipVariable(name))
			return;

		Label label = new Label(name);
		label.setTitle(getDescription(name));
		label.addStyleName(AON.AON_ICON_WARN);
		label.addStyleName(style.variableWarn());
		label.addStyleName(style.variableLabel());

		int row = contextTable.getRowCount();
		contextTable.setWidget(row, 0, label);

		TextBox textBox = new TextBox();
		contextTable.setWidget(row, 1, textBox);

		new VariableChangeHandler(name, textBox);

	}

	private void addVariable(String name, String value) {
		StringVariable variable = new StringVariable();
		variable.setName(name);
		variable.setExpression(value);
		vars.add(variable);
	}

	private String getDescription(String name) {
		VariableDescriptor descriptor = contextDescriptor.get(name);
		return descriptor != null ? descriptor.getDescription() : null;
	}
	
	
	// ---------------------------------------------------------------- Insight


}
