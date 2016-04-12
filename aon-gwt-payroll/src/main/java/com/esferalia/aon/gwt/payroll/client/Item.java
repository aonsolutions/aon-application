package com.esferalia.aon.gwt.payroll.client;

import java.util.Collections;
import java.util.List;

import com.esferalia.aon.gwt.common.shared.EvalException;
import com.esferalia.aon.gwt.common.shared.EvalSyntaxErrorException;
import com.esferalia.aon.gwt.common.shared.EvalWarning;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.client.FxDialog.IContextProvider;
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
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
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

public abstract class Item<T extends Enum<?>> extends ResizeComposite {

	private static final DateTimeFormat MONTH_FORMAT = DateTimeFormat
			.getFormat(PredefinedFormat.MONTH);

	interface Binder extends UiBinder<Widget, Item> {

	}

	private static final List<Variable> EMPTY_VARS = Collections.emptyList();

	private static final Binder binder = GWT.create(Binder.class);

	private class MyExpressionBox extends ExpressionBox implements BlurHandler,
			FocusHandler, AsyncCallback<List<Result>> {

		private String result;
		private String expression;

		private MyExpressionBox parent;

		public MyExpressionBox() {
			addBlurHandler(this);
			addFocusHandler(this);
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
			contextProvider.eval(expression, EMPTY_VARS, this);
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

	@UiField(provided = true)
	SuggestBox conceptSuggestBox;
	@UiField
	Label conceptDescriptionLabel;

	@UiField(provided = true)
	SuggestBox descriptionSuggestBox;
	@UiField
	Button resetDescriptionButton;

	@UiField(provided = true)
	MyExpressionBox expressionTextBox;
	@UiField
	Button resetExpressionButton;

	@UiField
	Button fxExpressionButton;

	private NumberFormat numberFormat;
	private IContextProvider contextProvider;

	private MultiWordSuggestOracle conceptSuggestOracle;
	private MultiWordSuggestOracle descriptionSuggestOracle;
	private com.esferalia.aon.gwt.payroll.shared.Item<T> concept;

	public Item() {
		initProvided();
		initWidget(binder.createAndBindUi(this));
		initTypeListBox();
		initUiHandlers();
	}

	public void setNumberFormat(NumberFormat numberFormat) {
		this.numberFormat = numberFormat;
	}

	public String getName() {
		return conceptSuggestBox.getText();
	}

	public void setName(String name) {
		conceptSuggestBox.setText(name);
	}

	public String getDescription() {
		return descriptionSuggestBox.getText();
	}

	public void setDescription(String description) {
		descriptionSuggestBox.setText(description);
		showOrHideResetDescriptionButton();
	}

	public String getExpression() {
		return ((MyExpressionBox) expressionTextBox).getExpression();
	}

	public void setExpression(String deduction) {
		expressionTextBox.setExpression(deduction);
		showOrHideResetPaymentButton();
		expressionTextBox.enable(!SpecialExpresion.isReadOnly(deduction));
	}

	public abstract T getType();

	public abstract void setType(T type);

	public void setAvailableDeductions(
			List<com.esferalia.aon.gwt.payroll.shared.Deduction> availableDeductions) {

		for (com.esferalia.aon.gwt.payroll.shared.Deduction deduction : availableDeductions) {
			String name = deduction.getName();
			if (!StringUtils.isEmpty(name)) {
				conceptSuggestOracle.add(name);
			}
			String description = deduction.getDescription();
			if (!StringUtils.isEmpty(description)) {
				descriptionSuggestOracle.add(description);
			}
		}
	}

	public com.esferalia.aon.gwt.payroll.shared.Item<T> getConcept() {
		return concept;
	}

	public void setConcept(com.esferalia.aon.gwt.payroll.shared.Item<T> concept) {
		this.concept = concept;
		// From now ypu can't edit concept
		conceptSuggestBox.setEnabled(concept == null);
		onChangeConcept();
	}

	public void setContextProvider(IContextProvider contextProvider) {
		this.contextProvider = contextProvider;
	}

	public void showName(boolean show) {
		Element el = mainGrid.getRowFormatter().getElement(0);
		if (show)
			el.getStyle().clearDisplay();
		else
			el.getStyle().setDisplay(Display.NONE);
	}
	
	public void addStyleName(String style){
		mainGrid.addStyleName(style);
	}

	protected abstract void initTypeListBox();

	protected void initUiHandlers() {
		descriptionSuggestBox
				.addValueChangeHandler(new ValueChangeHandler<String>() {
					@Override
					public void onValueChange(ValueChangeEvent<String> event) {
						Item.this.showOrHideResetDescriptionButton();
					}
				});
		resetDescriptionButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Item.this.setDescription(concept.getDescription());
			}
		});
		expressionTextBox.addBlurHandler(new BlurHandler() {

			@Override
			public void onBlur(BlurEvent event) {
				Item.this.showOrHideResetPaymentButton();
			}
		});
		resetExpressionButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Item.this.setExpression(concept.getExpression());
			}
		});
		resetTypeButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Item.this.setType(concept.getType());
			}
		});

		typeListBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				Item.this.showOrHideResetTypeButton();
			}
		});
		fxExpressionButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Item.this.showFxDialog((MyExpressionBox) expressionTextBox);
			}
		});
	}

	// ------------------------------------------

	private void initProvided() {

		expressionTextBox = new MyExpressionBox(); // TODO : UiBinder

		conceptSuggestOracle = new MultiWordSuggestOracle();
		descriptionSuggestOracle = new MultiWordSuggestOracle();
		conceptSuggestBox = new SuggestBox(conceptSuggestOracle);
		descriptionSuggestBox = new SuggestBox(descriptionSuggestOracle);

	}

	private void onChangeConcept() {
		conceptSuggestBox.setText(concept == null ? null : concept.getName());
		conceptDescriptionLabel.setVisible(concept != null);
		conceptDescriptionLabel.setText(concept == null ? null : concept
				.getDescription());

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
				//textBox.setFocus(true);
				if (fxDialog.isAccepted()) {
					//textBox.setValue(fxDialog.getExpression());
					textBox.setExpression(fxDialog.getExpression(), true);
				}
			}
		});

	}

	private void showOrHideResetTypeButton() {
		resetTypeButton.setVisible(concept != null
				&& concept.getType() != getType());
	}

	private void showOrHideResetDescriptionButton() {
		resetDescriptionButton.setVisible(concept != null
				&& !StringUtils.equals(concept.getDescription(),
						descriptionSuggestBox.getText()));
	}

	private void showOrHideResetPaymentButton() {
		resetExpressionButton.setVisible(concept != null
				&& !StringUtils.equals(concept.getExpression(),
						((MyExpressionBox) expressionTextBox).expression));
	}

}
