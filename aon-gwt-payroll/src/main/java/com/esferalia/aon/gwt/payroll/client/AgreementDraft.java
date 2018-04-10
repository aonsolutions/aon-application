package com.esferalia.aon.gwt.payroll.client;

import static com.esferalia.aon.gwt.payroll.client.Constants.DEFAULT_ZOOM;
import static com.esferalia.aon.gwt.payroll.client.Constants.DESCRIPTION_MAX_LENGTH;
import static com.esferalia.aon.gwt.payroll.client.Constants.EXPRESSION_MAX_LENGTH;
import static com.esferalia.aon.gwt.payroll.client.Constants.MAX_ZOOM;
import static com.esferalia.aon.gwt.payroll.client.Constants.MIN_ZOOM;
import static com.esferalia.aon.gwt.payroll.client.Constants.PERCENT_FORMAT;
import static com.esferalia.aon.gwt.payroll.client.Constants.ZOOM_STEP;
import static com.esferalia.aon.gwt.payroll.shared.Event.Type.ERROR;
import static com.esferalia.aon.gwt.payroll.shared.Event.Type.WARNING;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.MonthListBox;
import com.esferalia.aon.gwt.common.shared.DateTimeFormatException;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.common.shared.EmptyStringException;
import com.esferalia.aon.gwt.common.shared.HasDescription;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.client.AgreementDraftObject.CalculateCallback;
import com.esferalia.aon.gwt.payroll.client.FxDialog.IContextProvider;
import com.esferalia.aon.gwt.payroll.shared.AgreementDraft.Level;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.Event;
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.Item;
import com.esferalia.aon.gwt.payroll.shared.ItemComparator;
import com.esferalia.aon.gwt.payroll.shared.LevelComparator;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.PaymentEvent;
import com.esferalia.aon.gwt.payroll.shared.Period;
import com.esferalia.aon.gwt.payroll.shared.Result;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.Salary.Type;
import com.esferalia.aon.gwt.payroll.shared.StringVariable;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.text.client.DateTimeFormatRenderer;
import com.google.gwt.text.shared.Parser;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestBox.DefaultSuggestionDisplay;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.ValueBox;
import com.google.gwt.user.client.ui.ValueBoxBase;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.user.datepicker.client.DatePicker;

public class AgreementDraft extends ResizeComposite implements CalculateCallback {

	private static final Date TODAY = new Date();
	static int SALARY_TABLE_COLS = 8;
	static int SALARY_TABLE_LINES = 7;
	static int VARIABLE_TEXTBOX_SIZE = 10;

	public static final String CUSTOM = "CUSTOM";
	public static final String ALWAYS = "ALWAYS";
	public static final String ONLY_THIS_YEAR = "ONLY_THIS_YEAR";
	public static final String ONLY_THIS_MONTH = "ONLY_THIS_MONTH";
	public static final String FROM_THIS_MONTH = "FROM_THIS_MONTH";

	static class TypeListBox<T extends Enum<?> & HasDescription> extends ListBox {

		private Class<T> type;

		public TypeListBox(Class<T> type) {
			this(type, 20);

		}

		public TypeListBox(Class<T> type, int size) {
			this.type = type;
			loadTypeItems();
			getElement().getStyle().setWidth(size, Unit.EM);
		}

		public T getSelected() {
			int ordinal = getOrdinal(getSelectedIndex());
			for (T t : type.getEnumConstants())
				if (ordinal == t.ordinal())
					return t;
			return null;
		}

		public void setSelected(T t) {
			int ordinal = t == null ? -1 : t.ordinal();
			for (int i = 0; i < getItemCount(); i++)
				if (ordinal == getOrdinal(i))
					setSelectedIndex(i);
		}

		private void loadTypeItems() {

			for (T t : type.getEnumConstants()) {
				String description = t.getDescription();
				if (description != null) {
					addItem(description, Integer.toString(t.ordinal()));
				}
			}
		}

		private int getOrdinal(int index) {
			return Integer.valueOf(getValue(index));
		}

	}

	private static abstract class SuccessCalculateCallback implements CalculateCallback {
		@Override
		public void onCalculateFailure(Throwable throwable) {
			// Nothing
		}
	}

	interface MyStyle extends CssResource {
		@ClassName("icon-warn")
		String iconWarn();

		String highlight();

		@ClassName("text-warn")
		String textWarn();

		@ClassName("text-error")
		String textError();
		
		String categoryStyleButtonUp();
		
		String categoryStyleButtonDown();
		
		String selectButtonSalaryToggleButton();
		
		String marginToggleButton();
		
		String moreButton();
		
		String deleteButtonUp();
		
		String deleteButtonDown();
		
		String datePickerPanel();
		
		String panelButtons();
		
		String hide();

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
		
		Button deleteButton;
		List<Button> buttons;
		ListBox paymentListBox;
		ValueBox<Date> endDateBox;
		ValueBox<Date> startDateBox;
		ValueBox<Date> issueDateBox;

		PopupPanel popup;
		DatePicker picker;
		HandlerRegistration registration;

		public ExtraEditor(Extra extra) {
			this.extra = extra;
			this.picker = new DatePicker();
			this.popup = new PopupPanel(true); // auto-hide
			this.popup.setWidget(this.picker);
			this.popup.setStyleName(""); // remove all styles.
			this.popup.addCloseHandler(new CloseHandler<PopupPanel>() {

				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					if (ExtraEditor.this.registration != null)
						ExtraEditor.this.registration.removeHandler();
				}
			});
			this.buttons = new LinkedList<Button>();
		}

		void setReadOnly(boolean readOnly) {
			if ( this.deleteButton != null )
				this.deleteButton.setEnabled(!readOnly);
			if ( this.endDateBox != null )
				this.endDateBox.setReadOnly(readOnly);
			if ( this.startDateBox != null )
				this.startDateBox.setReadOnly(readOnly);
			if ( this.issueDateBox != null )
				this.issueDateBox.setReadOnly(readOnly);
			if ( paymentListBox != null ) 
				AgreementDraft.this.setReadOnly(paymentListBox, readOnly);
			for ( Button button: buttons )
				button.setEnabled(!readOnly);
		}

		void setDeleteButton(Button button) {
			this.deleteButton = button;
			this.deleteButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					extra.setIssueDate("REMOVE()");
					AgreementDraft.this.agreementDraftObject.addDraftExtra(extra);
					;
					AgreementDraft.this.calculate();
				}
			});
		}

		void setButtonFor(Button button, final ValueBox<Date> valueBox) {
			buttons.add(button);
			button.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					Date current = valueBox.getValue();
					if (current != null) {
						ExtraEditor.this.picker.setValue(current);
						ExtraEditor.this.picker.setCurrentMonth(current);
					}
					ExtraEditor.this.registration = ExtraEditor.this.picker
							.addValueChangeHandler(new ValueChangeHandler<Date>() {

								@Override
								public void onValueChange(ValueChangeEvent<Date> event) {
									valueBox.setValue(event.getValue(), true);
									ExtraEditor.this.popup.hide();
								}

							});
					ExtraEditor.this.popup.showRelativeTo(valueBox);
				}
			});
		}

		void setStartDateBox(ValueBox<Date> dateBox) {
			startDateBox = dateBox;
			startDateBox.addValueChangeHandler(new ValueChangeHandler<Date>() {
				@Override
				public void onValueChange(ValueChangeEvent<Date> event) {
					Date startDate = event.getValue();
					if (startDate != null)
						extra.setStartDate(AgreementDraft.this.formatExtraDate(startDate));
					else
						extra.setStartDate(startDateBox.getText());

					AgreementDraft.this.agreementDraftObject.addDraftExtra(extra);
					AgreementDraft.this.calculate(getEndDateCallback());

				}
			});

		}

		void setEndDateBox(ValueBox<Date> dateBox) {
			endDateBox = dateBox;
			endDateBox.addValueChangeHandler(new ValueChangeHandler<Date>() {
				@Override
				public void onValueChange(ValueChangeEvent<Date> event) {
					Date endDate = event.getValue();
					if (endDate != null)
						extra.setEndDate(AgreementDraft.this.formatExtraDate(endDate));
					else
						extra.setEndDate(endDateBox.getText());

					AgreementDraft.this.agreementDraftObject.addDraftExtra(extra);
					AgreementDraft.this.calculate(getIssueDateCallback());
				}
			});

		}

		void setIssueDateBox(ValueBox<Date> dateBox) {
			issueDateBox = dateBox;
			issueDateBox.addValueChangeHandler(new ValueChangeHandler<Date>() {
				@Override
				public void onValueChange(ValueChangeEvent<Date> event) {
					Date issueDate = event.getValue();
					if (issueDate != null)
						extra.setIssueDate(AgreementDraft.this.formatExtraDate(issueDate));
					else
						extra.setIssueDate(issueDateBox.getText());

					extra.setIssueDate(AgreementDraft.this.formatExtraDate(issueDate));
					AgreementDraft.this.agreementDraftObject.addDraftExtra(extra);
					AgreementDraft.this.calculate(getPaymentListBoxCallback());
				}
			});
		}

		void setPaymentListBox(final ListBox listBox) {
			paymentListBox = listBox;
			paymentListBox.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					String value = listBox.getValue(listBox.getSelectedIndex());
					extra.setPaymentId(value == null ? null : Integer.valueOf(value));
					AgreementDraft.this.agreementDraftObject.addDraftExtra(extra);
					AgreementDraft.this.calculate();
				}
			});
			paymentListBox.addBlurHandler(new BlurHandler() {

				@Override
				public void onBlur(BlurEvent event) {
					ExtraEditor editor = getNextExtraEditorFor(extra.getId());
					if (editor != null)
						editor.startDateBox.setFocus(true);
				}
			});
		}

		CalculateCallback getEndDateCallback() {
			return new SuccessCalculateCallback() {
				private int extraId = extra.getId();

				@Override
				public void onCalculateSucces(AgreementDraftObject object) {
					ExtraEditor editor = getExtraEditorFor(extraId);
					if (editor != null)
						editor.endDateBox.setFocus(true);
				}
			};
		}

		CalculateCallback getIssueDateCallback() {
			return new SuccessCalculateCallback() {
				private int extraId = extra.getId();

				@Override
				public void onCalculateSucces(AgreementDraftObject object) {
					ExtraEditor editor = getExtraEditorFor(extraId);
					if (editor != null)
						editor.issueDateBox.setFocus(true);
				}
			};
		}

		CalculateCallback getPaymentListBoxCallback() {
			return new SuccessCalculateCallback() {
				private int extraId = extra.getId();

				@Override
				public void onCalculateSucces(AgreementDraftObject object) {
					ExtraEditor editor = getExtraEditorFor(extraId);
					if (editor != null)
						editor.paymentListBox.setFocus(true);
				}
			};
		}

	}

	static interface IFocusableEditor {
		void setFocus();
		void setReadOnly(boolean readOnly);
	}

	protected class LevelEditor implements IFocusableEditor {
		Level level;
		Button deleteButton;
		TextBox descriptionTextBox;

		public LevelEditor(Level level) {
			this.level = level;
		}

		void setDescriptionTextBox(TextBox textBox) {
			this.descriptionTextBox = textBox;
			this.descriptionTextBox.addValueChangeHandler(new ValueChangeHandler<String>() {
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					level.setDescription(event.getValue());
					AgreementDraft.this.agreementDraftObject.addDraftLevel(level);
					AgreementDraft.this.calculate(getNextFocusCallback());
				}
			});
			this.descriptionTextBox.addBlurHandler(new BlurHandler() {

				@Override
				public void onBlur(BlurEvent event) {
					int next = getSalaryTableEditorIndexOf(LevelEditor.this) + 1;
					if (salaryTableEditors.size() > next)
						salaryTableEditors.get(next).setFocus();
				}
			});
		}

		void setDeleteButton(Button button) {
			this.deleteButton = button;
			button.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					level.setDescription("REMOVE()");
					AgreementDraft.this.agreementDraftObject.addDraftLevel(level);
					AgreementDraft.this.calculate();
				}
			});
		}

		// ---------------------------------------------------- FocusableEditor
		
		@Override
		public void setFocus() {
			if ( descriptionTextBox.isReadOnly() ) return;
			this.descriptionTextBox.setFocus(true);
		}
		
		@Override
		public void setReadOnly(boolean readOnly) {
			if ( this.deleteButton != null )
				this.deleteButton.setEnabled(!readOnly);
			if ( this.descriptionTextBox != null)
				this.descriptionTextBox.setReadOnly(readOnly);
		}

		CalculateCallback getNextFocusCallback() {
			return new SuccessCalculateCallback() {

				private int levelId = level.getId();

				private int currentIndex = AgreementDraft.this.getSalaryTableEditorIndexOf(LevelEditor.this);

				@Override
				public void onCalculateSucces(AgreementDraftObject object) {

					IFocusableEditor editor = getNextSalaryTableEditorForLevel(levelId);
					if (editor != null)
						editor.setFocus();
				}
			};
		}
	}

	private class CategoriesEditor implements IFocusableEditor {
		Level level;
		TextBox categoriesTextBox;

		public CategoriesEditor(Level level) {
			this.level = level;
		}

		void setCategoriesTextBox(TextBox textBox) {
			this.categoriesTextBox = textBox;
			this.categoriesTextBox.addValueChangeHandler(new ValueChangeHandler<String>() {
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					String text = event.getValue();
					AgreementDraft.this.agreementDraftObject.addDraftCategories(level, text);
					// TODO: really need to go server side.
					AgreementDraft.this.calculate(getNextFocusCallback());
				}
			});
			this.categoriesTextBox.addBlurHandler(new BlurHandler() {

				@Override
				public void onBlur(BlurEvent event) {
					int next = getSalaryTableEditorIndexOf(CategoriesEditor.this) + 1;
					if (salaryTableEditors.size() > next)
						salaryTableEditors.get(next).setFocus();
				}
			});
		}

		void hide(boolean hide) {
			AgreementDraft.hide(this.categoriesTextBox, hide);
		}

		// ---------------------------------------------------- FocusableEditor
		@Override
		public void setFocus() {
			this.categoriesTextBox.setFocus(true);
		}

		@Override
		public void setReadOnly(boolean readOnly) {
			if ( this.categoriesTextBox != null)
				this.categoriesTextBox.setReadOnly(readOnly);
		}

		CalculateCallback getNextFocusCallback() {
			return new SuccessCalculateCallback() {
				private int currentIndex = AgreementDraft.this.getSalaryTableEditorIndexOf(CategoriesEditor.this);

				@Override
				public void onCalculateSucces(AgreementDraftObject object) {
					IFocusableEditor editor = getSalaryTableEditorFor(currentIndex + 1);
					if (editor != null)
						editor.setFocus();
				}
			};
		}

	}

	private class VariableEditor implements IFocusableEditor {

		Level level;
		Variable var;

		String color;
		TextBox expressionBox;
		

		Timer reset = new Timer() {
			@Override
			public void run() {
				Object value = var.getValue();
				if (value == null)
					return;
				String text = value.toString();
				if (StringUtils.isBlank(text))
					return;
				expressionBox.setText(text);
			}
		};

		VariableEditor(Level level, Variable var) {
			this.var = var;
			this.level = level;
		}

		void setExpressionTextBox(TextBox textBox) {
			this.expressionBox = textBox;
			expressionBox.addFocusHandler(new FocusHandler() {
				@Override
				public void onFocus(FocusEvent event) {
					
					if ( expressionBox.isReadOnly() ) return;
					
					expressionBox.setText(var.getExpression());
					AgreementDraft.this.fxButton.setEnabled(true);
					AgreementDraft.this.fxLevel = level.getId();
					AgreementDraft.this.fxhasValue = expressionBox;
					color = expressionBox.getElement().getStyle().getColor();
					expressionBox.getElement().getStyle().clearColor();
				}
			});
			expressionBox.addBlurHandler(new BlurHandler() {
				@Override
				public void onBlur(BlurEvent event) {
					
					if ( expressionBox.isReadOnly() ) return;
					
					AgreementDraft.this.fxButton.setEnabled(false);
					expressionBox.getElement().getStyle().setColor(color);
					reset.schedule(100);
				}
			});
			expressionBox.addValueChangeHandler(new ValueChangeHandler<String>() {
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					if ( expressionBox.isReadOnly() ) return;
					
					reset.cancel();

					String value = event.getValue();

					if (!StringUtils.isBlank(value)) {
						value = "REMOVE()";
					}

					var.setExpression(event.getValue());
					// TODO: Check syntax????
					Period draftPeriod = getCurrentDraftPeriod();
					var.setEndDate(draftPeriod.getEnd());
					var.setStartDate(draftPeriod.getStart());
					AgreementDraft.this.agreementDraftObject.addDraftVariable(level, var);
					AgreementDraft.this.calculate(getNextFocusCallback());
				}
			});
		}

		// ---------------------------------------------------- FocusableEditor
		@Override
		public void setFocus() {
			this.expressionBox.setFocus(true);
		}
		
		@Override
		public void setReadOnly(boolean readOnly) {
			if ( this.expressionBox != null )
				this.expressionBox.setReadOnly(readOnly);
		}
		
		CalculateCallback getNextFocusCallback() {
			return new SuccessCalculateCallback() {
				private int currentIndex = AgreementDraft.this.getSalaryTableEditorIndexOf(VariableEditor.this);

				@Override
				public void onCalculateSucces(AgreementDraftObject object) {
					IFocusableEditor editor = getSalaryTableEditorFor(currentIndex + 1);
					if (editor != null)
						editor.setFocus();
				}
			};
		}
	}

	private class PaymentEditor {
		Payment payment;
		Button deleteButton;
		TextBox expressionBox;
		ValueBoxBase<String> descriptionBox;
		TypeListBox<Payment.Type> typeListBox;

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
		
		void setReadOnly(boolean readOnly) {
			if ( this.deleteButton != null )
				this.deleteButton.setEnabled(!readOnly);
			if ( this.expressionBox != null )
				this.expressionBox.setReadOnly(readOnly);
			if ( this.descriptionBox != null )
				descriptionBox.setReadOnly(readOnly);
			//if ( this.typeListBox != null  )
			//	AgreementDraft.this.setReadOnly(typeListBox, readOnly);
			
		}

		void setEditButton(Button button) {

			class EditHandler implements ClickHandler, PaymentDialog.Callback {
				@Override
				public void onClick(ClickEvent event) {
					PaymentDialog dialog = new PaymentDialog();
					dialog.setNumberFormat(AON.CURRENCY_FORMAT);
					dialog.setConcept(PaymentEditor.this.getConcept());
					dialog.setName(payment.getName()); // Not if ???
					dialog.setContextProvider(AgreementDraft.this.contextProvider);
					dialog.setMonth(payment.getMonth());
					dialog.setType(payment.getType());
					dialog.setReceiptType(payment.getSalaryType());
					dialog.setDescription(payment.getDescription());
					dialog.setPaymentExpression(payment.getExpression()); //
					dialog.setIrpfExpression(payment.getIrpfExpression());
					dialog.setQuoteExpression(payment.getQuoteExpression());
					dialog.setEnabledMonthListBox(!isExtraPayment(payment));
					

					dialog.center();
					dialog.show(this);
				}

				// ------------------------------------------------------------
				// PaymentDialog.Callback methods.
				// ------------------------------------------------------------
				@Override
				public void onAccept(PaymentDialog dialog) {
					payment.setName(dialog.getName());
					payment.setType(dialog.getType());
					payment.setMonth(dialog.getMonth());
					payment.setDescription(dialog.getDescription());
					payment.setExpression(dialog.getPaymentExpression());
					payment.setIrpfExpression(dialog.getIrpfExpression());
					payment.setQuoteExpression(dialog.getQuoteExpression());

					AgreementDraft.this.agreementDraftObject.addDraftPayment(payment);
					AgreementDraft.this.calculate();
				}

			}

			button.addClickHandler(new EditHandler());
		}

		void setDeleteButton(Button button) {
			this.deleteButton = button;
			this.deleteButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					payment.setExpression(isRemove(payment) ? "PARENT()" : "REMOVE()");
					AgreementDraft.this.agreementDraftObject.addDraftPayment(payment);
					AgreementDraft.this.calculate();
				}
			});
		}

		void setPaymentTypeListBox(TypeListBox<Payment.Type> listBox) {
			this.typeListBox = listBox;
			this.typeListBox.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					payment.setType(PaymentEditor.this.typeListBox.getSelected());
					AgreementDraft.this.agreementDraftObject.addDraftPayment(payment);
					AgreementDraft.this.calculate(getDescriptionFocusCallback());
				}
			});
		}

		void setExpressionTextBox(TextBox textBox) {
			this.expressionBox = textBox;
			this.expressionBox.addFocusHandler(new FocusHandler() {
				@Override
				public void onFocus(FocusEvent event) {
					AgreementDraft.this.fxButton.setEnabled(true);
					AgreementDraft.this.fxLevel = 0;
					AgreementDraft.this.fxhasValue = expressionBox;
				}
			});

			this.expressionBox.addBlurHandler(new BlurHandler() {
				@Override
				public void onBlur(BlurEvent event) {
					AgreementDraft.this.fxButton.setEnabled(false);
				}
			});
			this.expressionBox.addValueChangeHandler(new ValueChangeHandler<String>() {
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					//Window.alert("DEVENGO");
					payment.setExpression(event.getValue());
					AgreementDraft.this.agreementDraftObject.addDraftPayment(payment);
					AgreementDraft.this.calculate(getExpressionFocusCallback());
					//Window.alert("PAGOS : "+AgreementDraft.this.agreementDraftObject.getPayments().size());
					if(agreementDraftObject.getDatesWithChanges().size()<1){
						EmployeeCalendarUntillDialog selectedDate = new EmployeeCalendarUntillDialog("Fecha inicio tramo", 
								"Para poder crear el primer devengo es\nnecesario crear un primer tramo.") {
							
							@Override
							protected void onAccept() {
								Date month = this.getSelectedDate();
								agreementDraftObject.addNewDatesWithChanges(month);
								agreementDraftObject.setStartDate(month);
								agreementDraftObject.setEndDate(DateUtils.getLastDayOfMonth(month));
								calculate();
							}
						};
						
						selectedDate.setDefaultDate(new Date());
						selectedDate.show();
						selectedDate.center();
					}
				}
			});
		}

		void setDescriptionTextBox(TextBox textBox) {
			this.descriptionBox = textBox;
			this.descriptionBox.addValueChangeHandler(new ValueChangeHandler<String>() {
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					payment.setDescription(event.getValue());
					AgreementDraft.this.agreementDraftObject.addDraftPayment(payment);
					AgreementDraft.this.calculate(getExpressionFocusCallback());

				}
			});
		}

		void setDescriptionSuggestBox(final SuggestBox suggestBox) {

			suggestBox.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {

				@Override
				public void onSelection(SelectionEvent<Suggestion> event) {

					Suggestion suggestion = event.getSelectedItem();
					Payment concept = getPayment(suggestion.getReplacementString());
					if (concept == null)
						return;

					payment.setType(concept.getType());
					payment.setName(concept.getName());
					payment.setConceptId(concept.getId());
					payment.setDescription(concept.getDescription());
					payment.setIrpfExpression(concept.getIrpfExpression());
					payment.setQuoteExpression(concept.getQuoteExpression());

					if (concept.getExpression() != null) {
						
						payment.setExpression(getExpression4Payment(concept));
						
						AgreementDraft.this.agreementDraftObject.addDraftPayment(payment);
						AgreementDraft.this.calculate(getExpressionFocusCallback());
					} else {
						typeListBox.setSelected(payment.getType());
						expressionBox.setFocus(true);
					}

				}
			});
			this.descriptionBox = suggestBox.getValueBox();
			this.descriptionBox.addBlurHandler(new BlurHandler() {

				@Override
				public void onBlur(BlurEvent event) {
					if (((DefaultSuggestionDisplay) suggestBox.getSuggestionDisplay()).isSuggestionListShowing())
						return;
					String description = PaymentEditor.this.descriptionBox.getValue();
					if (StringUtils.isBlank(description))
						return;

					payment.setDescription(description);
					AgreementDraft.this.agreementDraftObject.addDraftPayment(payment);
					AgreementDraft.this.calculate(getExpressionFocusCallback());

				}
			});
		}

		CalculateCallback getDescriptionFocusCallback() {
			return new SuccessCalculateCallback() {
				private int paymentId = payment.getId();

				@Override
				public void onCalculateSucces(AgreementDraftObject object) {
					PaymentEditor editor = getPaymentEditorFor(paymentId);
					if (editor != null)
						editor.descriptionBox.setFocus(true);
				}
			};
		}

		CalculateCallback getExpressionFocusCallback() {
			return new SuccessCalculateCallback() {
				private int paymentId = payment.getId();

				@Override
				public void onCalculateSucces(AgreementDraftObject object) {
					PaymentEditor editor = getPaymentEditorFor(paymentId);
					if (editor != null)
						editor.expressionBox.setFocus(true);
				}
			};
		}

	}

	private class PaymentSuggestionDisplay extends AbstractItemSuggestionDisplay<Payment> {

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

	private class ContextProvider implements IContextProvider {

		@Override
		public boolean isEditable(String name) {
			for (Payment payment : AgreementDraft.this.agreementDraftObject.getPayments())
				if (StringUtils.equals(payment.getName(), name))
					return false;
			return true;
		}

		@Override
		public void getContext(AsyncCallback<ContextDescriptor> callback) {
			AgreementDraft.this.agreementDraftObject.getContext(AgreementDraft.this.fxLevel, callback);
		}

		@Override
		public void eval(String expression, List<Variable> vars, AsyncCallback<List<Result>> callback) {
			AgreementDraft.this.agreementDraftObject.eval(expression, AgreementDraft.this.fxLevel, vars, callback);
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
	Button undoAllButton;

	@UiField
	Button acceptButton;

	@UiField
	Button deleteButton;

//	@UiField
//	ListBox datesListBox;

	@UiField
	FlexTable eventsTable;

	@UiField
	Widget eventsTableSpace;
	
//	@UiField
//	Button categoryButton;
	
	@UiField
	HorizontalPanel categoryButtonPanel;
	
	@UiField
	HorizontalPanel salaryToggleButtonsPanel;
	
	@UiField
	HorizontalPanel moreToggleButtonsPanel;
	
//	@UiField
//	TabLayoutPanel salaryTabLayoutPanel;

	@UiField
	FlexTable salaryTable;

	@UiField
	FlexTable extrasTable;

	@UiField
	FlexTable paymentsTable;

	@UiField
	ScrollPanel draftScrollPane;

	@UiField
	TextBox descriptionTextBox;

//	@UiField
//	MonthListBox draftMonthListBox;

	@UiField
	ScrollPanel salaryTableScrollPane;

	@UiField
	DeckLayoutPanel deckPanel;
	@UiField
	DockLayoutPanel draftPanel;

	@UiField
	DockLayoutPanel printPreviewPanel;
	@UiField
	HTML printPreviewHTML;
	@UiField
	ListBox typeListBox;
	@UiField
	ListBox levelListBox;
	@UiField
	ListBox zoomListBox;
	@UiField
	MonthListBox previewMonthListBox;

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

	private int fxLevel = 0;
	private HasValue<String> fxhasValue;

	private List<Integer> changedLevelsRows;
	private List<Integer> changedVariablesCols;

	private List<ExtraEditor> extraEditors;
	private List<PaymentEditor> paymentEditors;
	private List<IFocusableEditor> salaryTableEditors;

	private ContextProvider contextProvider;

	private Map<Event.Type, String[]> eventStyles;

	private ContentAsistManager contentAssistManager;
	
	private boolean firstCalculate;
	
	public AgreementDraft() {
		initWidget(binder.createAndBindUi(this));
		initPaymentsTable();
		initExtrasTable();
		initEventsStyles(style);
		undoListener = new UndoListener();
		availablePaymens = new ArrayList<Payment>();
		changedLevelsRows = new LinkedList<Integer>();
		changedVariablesCols = new LinkedList<Integer>();
		paymentDescriptionOracle = new MultiWordSuggestOracle();
		paymentSuggestionDisplay = new PaymentSuggestionDisplay();

		extraEditors = new ArrayList<ExtraEditor>();
		paymentEditors = new ArrayList<PaymentEditor>();
		salaryTableEditors = new ArrayList<IFocusableEditor>();

		contextProvider = new ContextProvider();
		contentAssistManager = new ContentAsistManager();
		
		showDraft();
	}
	
	private void initCategoryPanel(boolean readOnly) {
		categoryButtonPanel.clear();
		HorizontalPanel hPanel = new HorizontalPanel();
		Button button = new Button("Categorias");
		button.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				putAllSalaryToggleButtonsUp();
				button.removeStyleName(style.categoryStyleButtonUp());
				button.addStyleName(style.categoryStyleButtonDown());
				clearSalaryTable();
				hPanel.addStyleName(style.selectButtonSalaryToggleButton());
				
				salaryTableEditors.clear();
				salaryTableEditors.addAll(dumpSalaryTableCategory());
				salaryTableEditors.add(insertNewLevelRow(salaryTable.getRowCount()));
				initSalaryTableFrozenColsAndRows();
				setReadOnly(/*object.isSystem() &&*/ !agreementDraftObject.isMine() );			
			}

			private void putAllSalaryToggleButtonsUp() {
				for(int i=0; i<salaryToggleButtonsPanel.getWidgetCount(); i+=2){
					HorizontalPanel hPanel = (HorizontalPanel) salaryToggleButtonsPanel.getWidget(i);
					ToggleButton toggleButton = (ToggleButton) hPanel.getWidget(0);
					if(!readOnly){
						Button deleteButton = (Button) hPanel.getWidget(1);
						deleteButton.removeStyleName(style.deleteButtonDown());
						deleteButton.addStyleName(style.deleteButtonUp());
					}
					toggleButton.setDown(false);
					hPanel.removeStyleName(style.selectButtonSalaryToggleButton());
				}
			}
			
		});
		
		button.removeStyleName(style.categoryStyleButtonDown());
		button.addStyleName(style.categoryStyleButtonUp());
		hPanel.addStyleName(style.panelButtons());
		hPanel.add(button);
		categoryButtonPanel.add(hPanel);
		HTML html = new HTML("&nbsp");
		categoryButtonPanel.add(html);
		
	}

	private void initSalarytabs(Date draftStratDate, boolean readOnly) {
		Date[] datesList = agreementDraftObject.getDatesWithChanges().toArray(new Date[]{});
		
		if(firstCalculate && agreementDraftObject.getDatesWithChanges().size()>0){ // La primera vez que entra (PARCHE)
			firstCalculate = false;
			Date month = datesList[datesList.length - 1];
			agreementDraftObject.setStartDate(month);
			agreementDraftObject.setEndDate(DateUtils.getLastDayOfMonth(month));
			calculate();
		}
			
		salaryToggleButtonsPanel.clear();
		moreToggleButtonsPanel.clear();
		salaryToggleButtonsPanel.removeStyleName(style.hide());
		int startTab = 0;
		
		if (datesList.length != 0){
			for(int i=0; i<datesList.length; i++){
				
				Date date = datesList[i];
				Date _endDate =  (i < datesList.length -1) ? DateUtils.getPrevDay(datesList[i+1]) : null;
				
				if (
						(date.equals(draftStratDate) || 
						(date.getMonth() == draftStratDate.getMonth() && date.getYear() == draftStratDate.getYear()))
					){
					startTab = i;
					startTab = startTab*2;
				}
				
				HorizontalPanel hPanel = new HorizontalPanel();
				hPanel.ensureDebugId("panel_" + DateTimeFormat.getFormat("dd_MM_yyyy").format(date));

				ToggleButton button = new ToggleButton(date.getDate()+"/"+(date.getMonth()+1)+"/"+(date.getYear()+1900));
				button.ensureDebugId("toggleButton_" + DateTimeFormat.getFormat("dd_MM_yyyy").format(date));
				button.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						if(button.isDown()){
							putAllToggleButtonsUp();
							button.setDown(true);
							int selectedButton = 0;
							
							for(int i=0; i<salaryToggleButtonsPanel.getWidgetCount(); i++){
								if(i%2 !=0){
									continue;
								}
								HorizontalPanel hPanel = (HorizontalPanel) salaryToggleButtonsPanel.getWidget(i);
								ToggleButton toggleButton = (ToggleButton) hPanel.getWidget(0);
								if(button.equals(toggleButton)){
									break;
								}
								selectedButton++;
							}
							
						
							Date month = datesList[selectedButton];
							agreementDraftObject.setStartDate(month);
							agreementDraftObject.setEndDate(DateUtils.getLastDayOfMonth(month));
							calculate();
						
						}else{
							return;
						}
					}
	
					private void putAllToggleButtonsUp() {
						for(int i=0; i<salaryToggleButtonsPanel.getWidgetCount(); i+=2){
							HorizontalPanel hPanel = (HorizontalPanel) salaryToggleButtonsPanel.getWidget(i);
							ToggleButton toggleButton = (ToggleButton) hPanel.getWidget(0);
							toggleButton.setDown(false);
						}	
					}
				});
				
				hPanel.add(button);
				
				if(!readOnly){
					Button deleteButton = new Button("x");
					deleteButton.ensureDebugId("deleteButton_" +DateTimeFormat.getFormat("dd_MM_yyyy").format(date) );
					deleteButton.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							int selectedButton = 0;
							
							for(int i=0; i<salaryToggleButtonsPanel.getWidgetCount(); i+=2){
								if(i%2 !=0){
									continue;
								}
								HorizontalPanel hPanel = (HorizontalPanel) salaryToggleButtonsPanel.getWidget(i);
								ToggleButton toggleButton = (ToggleButton) hPanel.getWidget(0);
								if(button.equals(toggleButton)){
									break;
								}
								selectedButton++;
							}
							
							Date month = datesList[selectedButton];
							agreementDraftObject.addDeleteDatesChanges(month);
							Date newSelectMonth = null;
							if(selectedButton==0) {
								newSelectMonth = datesList[selectedButton+1];
								agreementDraftObject.cleanDeleteDate(month);
							}
							else {
								newSelectMonth = datesList[selectedButton-1];
							}
							
							if(newSelectMonth == null)
								newSelectMonth = new Date(0);
							
							int finalSelectedButton = selectedButton;
							Date finalnewSelectMonth = newSelectMonth;
							
							agreementDraftObject.setStartDate(newSelectMonth);
							agreementDraftObject.setEndDate(DateUtils.getLastDayOfMonth(newSelectMonth));
							calculate( new CalculateCallback() {

								@Override
								public void onCalculateSucces(AgreementDraftObject object) {
									if ( finalSelectedButton > 0 )
										agreementDraftObject.strechDate(finalnewSelectMonth);
								}
								
								@Override
								public void onCalculateFailure(Throwable throwable) {
								}

							});
							
						}	
					});
					
					deleteButton.addStyleName(style.deleteButtonUp());
					hPanel.add(deleteButton);
				}
				
				hPanel.addStyleName(style.panelButtons());
				salaryToggleButtonsPanel.add(hPanel);
				HTML html = new HTML("&nbsp");
				salaryToggleButtonsPanel.add(html);
				
			}
		}
		
		//addMoreButton
		if(!readOnly){
			Button moreButton = addMoreButton();
			moreToggleButtonsPanel.add(moreButton);
		}
		
		
		if (datesList.length != 0){
			HorizontalPanel hPanel = (HorizontalPanel) salaryToggleButtonsPanel.getWidget(startTab);
			ToggleButton toggleButton = (ToggleButton) hPanel.getWidget(0);
			toggleButton.setDown(true);
			if(!readOnly){
				Button deleteButton = (Button) hPanel.getWidget(1);
				deleteButton.addStyleName(style.deleteButtonDown());
			}
			hPanel.addStyleName(style.selectButtonSalaryToggleButton());
		}
	}

	private Button addMoreButton() {
		Button moreButton = new Button("+");
		moreButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				PopupPanel popup = new PopupPanel(true); // auto-hide
				DatePicker picker = new DatePicker();
				picker.setValue(TODAY);
				picker.addValueChangeHandler(new ValueChangeHandler<Date>() {
					@Override
					public void onValueChange(ValueChangeEvent<Date> event) {
						popup.hide();
						Date month = event.getValue();
						agreementDraftObject.addNewDatesWithChanges(month);
						agreementDraftObject.setStartDate(month);
						agreementDraftObject.setEndDate(DateUtils.getLastDayOfMonth(month));
						calculate();
					}
				});
				popup.setWidget(picker);
				popup.setStyleName(style.datePickerPanel());
				popup.showRelativeTo(moreButton);

				popup.ensureDebugId("morePopupPanel");
				picker.ensureDebugId("moreDatePicker");
			}
		});
		moreButton.addStyleName(style.moreButton());
		
		moreButton.ensureDebugId("moreButton");
		
		
		return moreButton;
	}
	
	@Override
	public void onResize() {
		if (salaryTableUpperLeftCorner != null) {
			moveSalaryTableFrozenColsAndRows();
		}
		super.onResize();
	}

	public void setAgreementDraftObject(AgreementDraftObject agreementDraftObject) {
		if (this.agreementDraftObject != null) {
			agreementDraftObject.removeListener(undoListener);
		}
		showDraft();
		firstCalculate = true;
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

		initCategoryPanel(/*object.isSystem() &&*/ !object.isMine());
		initSalarytabs(agreementDraftObject.getStartDate(), /*object.isSystem() &&*/ !object.isMine());
		
		loadAvailablePayments();

		setDescription();

		clearSalaryTable();
		salaryTableEditors.clear();
		salaryTableEditors.addAll(dumpSalaryTable());
		salaryTableEditors.add(insertNewLevelRow(salaryTable.getRowCount()));
		initSalaryTableFrozenColsAndRows();
		
		clearPaymentsTable();
		paymentEditors.clear();
		paymentEditors.addAll(dumpPayments());
		if(object.isMine())
			paymentEditors.add(insertNewPaymentRow(paymentsTable.getRowCount()));

		clearExtrasTable();
		SortedSet<Payment> extraPayments = getAvailableExtraPayments();
		extraEditors.clear();
		extraEditors.addAll(dumpExtras(extraPayments));
		if(object.isMine())
			extraEditors.add(insertNewExtraRow(extrasTable.getRowCount(), extraPayments));

		clearEventsTable();
		dumpEvents();
		loadContentAssistManager();
		
		setReadOnly(/*object.isSystem() &&*/ !object.isMine() );

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

	@UiHandler("fxButton")
	void onFxClicked(MouseDownEvent event) {
		FxDialog fxDialog = new FxDialog(contextProvider) {
			@Override
			void onAcceptButtonClick(ClickEvent event) {
				super.onAcceptButtonClick(event);
				fxhasValue.setValue(getExpression(), true);
				((Focusable) fxhasValue).setFocus(true);
			}
		};

		fxDialog.setExpression(fxhasValue.getValue());
		fxDialog.center();
		fxDialog.show();

	}

	@UiHandler("undoAllButton")
	void onUndoAllClicked(MouseDownEvent event) {
		agreementDraftObject.clearDrafts();
		agreementDraftObject.clearNewDatesWithChanges();
		agreementDraftObject.clearDeleteDatesWithChanges();
		agreementDraftObject.calculate(AgreementDraft.this);
	}

	@UiHandler("descriptionTextBox")
	void onDescriptionValueChange(ValueChangeEvent<String> event) {
		agreementDraftObject.setDescription(event.getValue());
	}

	@UiHandler("salaryTableScrollPane")
	void onSalaryTableScroll(ScrollEvent event) {
		moveSalaryTableFrozenColsAndRows();
	}

	@UiHandler("draftScrollPane")
	void onMainScroll(ScrollEvent event) {
		moveSalaryTableFrozenColsAndRows();
	}

	@UiHandler("printPreviewButton")
	void onClickPrintPreviewButton(ClickEvent event) {
		showPreview();

		initZoomListBox();
		initTypeListBox();
		initLevelListBox();
		initPreviewMonthListBox();

		printPreview();
	}

	@UiHandler("closePreviewButton")
	void onClickClosePreviewButton(ClickEvent event) {
		showDraft();
	}

	@UiHandler({ "zoomListBox", "typeListBox", "levelListBox", })
	void onChangePreview(ChangeEvent event) {
		printPreview();
	}

	@UiHandler("previewMonthListBox")
	void onPreviewMonthListBoxChanged(ChangeEvent event) {
		Date month = previewMonthListBox.getSelectedMonth();
		agreementDraftObject.setStartDate(month);
		agreementDraftObject.setEndDate(DateUtils.getLastDayOfMonth(month));
		calculate(new CalculateCallback() {

			@Override
			public void onCalculateFailure(Throwable throwable) {
			}

			@Override
			public void onCalculateSucces(AgreementDraftObject object) {
				printPreview();
			}

		});
	}

	// ------------------------------------------------------------------------


	public void setReadOnly(boolean readOnly) {
		
		
		for ( IFocusableEditor editor: salaryTableEditors)
			editor.setReadOnly(readOnly);
		for ( PaymentEditor editor: paymentEditors)
			editor.setReadOnly(readOnly);
		for ( ExtraEditor editor: extraEditors)
			editor.setReadOnly(readOnly);
		
		fxButton.setEnabled(!readOnly);
		undoButton.setEnabled(!readOnly);
		redoButton.setEnabled(!readOnly);
		undoAllButton.setEnabled(!readOnly);
		acceptButton.setEnabled(!readOnly);
		deleteButton.setEnabled(!readOnly);
		
		descriptionTextBox.setReadOnly(readOnly);

		setReadOnly(salaryTableFirstColumn, readOnly);
	}

	public AgreementDraftObject getAgreementDraftObject() {
		return agreementDraftObject;
	}

	protected Widget createWidget4Level(Level level, LevelEditor levelEditor) {
		TextBox descriptionTextBox = new TextBox();
		descriptionTextBox.setMaxLength(DESCRIPTION_MAX_LENGTH);
		descriptionTextBox.setText(level.getDescription());
		descriptionTextBox.setVisibleLength(5);
		levelEditor.setDescriptionTextBox(descriptionTextBox);
		return descriptionTextBox;
	}

	protected void formatRow(Level level, int row, RowFormatter formatter) {
	}

	// ------------------------------------------------------------------------
	//
	// ------------------------------------------------------------------------

	private void calculate() {
		agreementDraftObject.calculate(this);
	}
	

	private void calculate(final CalculateCallback cb) {

		CalculateCallback compositeCb = new CalculateCallback() {

			@Override
			public void onCalculateFailure(Throwable throwable) {
				AgreementDraft.this.onCalculateFailure(throwable);
				cb.onCalculateFailure(throwable);
			}

			@Override
			public void onCalculateSucces(final AgreementDraftObject draft) {
				AgreementDraft.this.onCalculateSucces(draft);
				cb.onCalculateSucces(draft);
			}

		};

		agreementDraftObject.calculate(compositeCb);
	}

	private void reloadSalaryTable() {
		clearSalaryTable();
		salaryTableEditors.clear();
		salaryTableEditors.addAll(dumpSalaryTable());
		salaryTableEditors.add(insertNewLevelRow(salaryTable.getRowCount()));
		initSalaryTableFrozenColsAndRows();
	}

	private void setDescription() {
		// TODO: When null it will be desirable warn user.
		String description = this.agreementDraftObject.getDescription();
		descriptionTextBox.setText(description == null ? "" : description);
		// descriptionTextBox.setEnabled(isEditable());

	}

	private void dumpEvents() {

		int row = eventsTable.getRowCount();
		for (Event event : agreementDraftObject.getEvents()) {
			dumpEvent(row++, event);
		}
		eventsTableSpace.setVisible(eventsTable.getRowCount() > 0);
	}

	private List<ExtraEditor> dumpExtras(SortedSet<Payment> payments) {

		List<ExtraEditor> editors = new LinkedList();

		int row = extrasTable.getRowCount();

		for (Extra extra : agreementDraftObject.getExtras()) {
			editors.add(dumpExtra(extra, row++, payments));
		}
		return editors;
	}

	private List<PaymentEditor> dumpPayments() {

		List<PaymentEditor> editors = new LinkedList();

		int row = paymentsTable.getRowCount();

		SortedSet<Payment> payments = new TreeSet<Payment>(new ItemComparator<Payment.Type>());
		payments.addAll(agreementDraftObject.getPayments());
		for (Payment payment : payments) {
			editors.add(dumpPayment(payment, row++));
		}
		
		
		return editors;
	}

	private List<IFocusableEditor> dumpSalaryTable() {

		RowFormatter rowFormatter = salaryTable.getRowFormatter();
		CellFormatter cellFormatter = salaryTable.getCellFormatter();

		salaryTable.setText(0, 0, "NIVEL");
		cellFormatter.addStyleName(0, 0, AON.AON_BOLD);
		cellFormatter.addStyleName(0, 0, AON.AON_TEXT_CENTER);
		cellFormatter.addStyleName(0, 0, AON.AON_INPUT_REQUIRED);

		int col = 1;

		SortedSet<String> variables = new TreeSet<String>();
		variables.addAll(agreementDraftObject.getVariables());

		Set<String> changedVariables = agreementDraftObject.getChangedVariables();

		// fill table head

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
		
		//Columna vacia para el ancho
		salaryTable.setWidget(0, col, new Label());
		cellFormatter.addStyleName(0, col, "aon-width-all"); // fill remain
		col++;
		
		//cellFormatter.addStyleName(0, col-1, "aon-width-all"); // fill remain
		// salaryTable.setHTML(0, col, "&nbsp;");
		salaryTable.setWidget(0, col, getViewButton());

		SortedSet<Level> levels = new TreeSet<Level>(new LevelComparator());
		levels.addAll(agreementDraftObject.getLevels());

		Set<Level> changedLevels = agreementDraftObject.getChangedLevels();

		int cols = variables.size() + 2;
		int size = levels.size() * cols;
		List<IFocusableEditor> editors = new ArrayList<IFocusableEditor>(size);
		for (int i = 0; i < size; i++)
			editors.add(i, null);

		// now level rows
		int row = 1;
		for (Level level : levels) {

			LevelEditor levelEditor = new LevelEditor(level);
			Widget levelWidget = createWidget4Level(level, levelEditor);

			hide(levelWidget, level.getId() == 0);

			salaryTable.setWidget(row, 0, levelWidget);

			cellFormatter.addStyleName(row, 0, AON.AON_BOLD);

			if (changedLevels.contains(level)) {
				changedLevelsRows.add(row);
				rowFormatter.addStyleName(row, style.highlight());
			}

			formatRow(level, row, rowFormatter);

			int index = (row - 1) * cols;
			editors.set(index, levelEditor);

			row++;
		}

		row = 1;

		for (Level level : levels) {
			col = 1;
			for (String var : variables) {
				Variable variable = agreementDraftObject.getVariable(level, var);
				VariableEditor variableEditor;
				if (variable != null) {
					variableEditor = dumpVariable(row, col, level, variable);
				} else {
					variableEditor = dumpUndefVariable(row, col, level, var);
				}
				if (changedVariables.contains(var)) {
					cellFormatter.addStyleName(row, col, style.highlight());
				}

				int index = ((row - 1) * cols) + col;
				editors.set(index, variableEditor);

				col++;
			}
			
			int index = 0;
			
			Button deleteButton = new Button();
			deleteButton.setStyleName(AON.AON_ICON_DELETE);
			deleteButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);

			LevelEditor deleteEditor = new LevelEditor(level);
			deleteEditor.setDeleteButton(deleteButton);
			

			hide(deleteButton, level.getId() == 0);

			index = ((row - 1) * cols) + col;
			editors.set(index, deleteEditor);
			salaryTable.setWidget(row, col++, new Label());
			salaryTable.setWidget(row, col++, deleteButton);


			if (isDraftLevel(level)) {
				salaryTable.getRowFormatter().addStyleName(row, AON.AON_DATA_TABLE_ROW_HIGHLIGHT);
				salaryTable.getRowFormatter().addStyleName(row - 1, AON.AON_DATA_TABLE_ROW_HIGHLIGHT_TOP);
			}

			row++;
		}

		// Set max width...
		int offsetWidth = 1;
		offsetWidth += cellFormatter.getElement(0, 0).getOffsetWidth();

		// Asume that each column have 1.5 width of level column that has an
		// input with size 5
		offsetWidth += offsetWidth * 1.5 * (SALARY_TABLE_COLS - 1);
		offsetWidth += cellFormatter.getElement(0, salaryTable.getCellCount(0) - 1).getOffsetWidth();

		
		if(variables.size() == 0)
			offsetWidth = 721;
		
		salaryTableScrollPane.setWidth(offsetWidth + "px");
		
		return editors;
	}

	
	private List<IFocusableEditor> dumpSalaryTableCategory() {

		RowFormatter rowFormatter = salaryTable.getRowFormatter();
		CellFormatter cellFormatter = salaryTable.getCellFormatter();

		salaryTable.setText(0, 0, "NIVEL");
		cellFormatter.addStyleName(0, 0, AON.AON_BOLD);
		cellFormatter.addStyleName(0, 0, AON.AON_TEXT_CENTER);
		cellFormatter.addStyleName(0, 0, AON.AON_INPUT_REQUIRED);

		int col = 1;

		SortedSet<String> variables = new TreeSet<String>();
		variables.addAll(agreementDraftObject.getVariables());

		salaryTable.setText(0, col, "CATEGORIAS");
		cellFormatter.addStyleName(0, col, AON.AON_BOLD);
		cellFormatter.addStyleName(0, col, AON.AON_TEXT_CENTER);
		cellFormatter.addStyleName(0, col, AON.AON_INPUT_REQUIRED);
		cellFormatter.addStyleName(0, col, "aon-width-all"); // fill remain
																 //space
		col++;
		
		// salaryTable.setHTML(0, col, "&nbsp;");
		salaryTable.setWidget(0, col, getViewButton());

		SortedSet<Level> levels = new TreeSet<Level>(new LevelComparator());
		levels.addAll(agreementDraftObject.getLevels());

		Set<Level> changedLevels = agreementDraftObject.getChangedLevels();

		int cols = /*variables.size() +*/ 3;
		int size = levels.size() * cols;
		List<IFocusableEditor> editors = new ArrayList<IFocusableEditor>(size);
		for (int i = 0; i < size; i++)
			editors.add(i, null);

		// now level rows
		int row = 1;
		for (Level level : levels) {

			LevelEditor levelEditor = new LevelEditor(level);
			Widget levelWidget = createWidget4Level(level, levelEditor);

			hide(levelWidget, level.getId() == 0);

			salaryTable.setWidget(row, 0, levelWidget);

			cellFormatter.addStyleName(row, 0, AON.AON_BOLD);

			if (changedLevels.contains(level)) {
				changedLevelsRows.add(row);
				rowFormatter.addStyleName(row, style.highlight());
			}

			formatRow(level, row, rowFormatter);

			int index = (row - 1) * cols;
			editors.set(index, levelEditor);

			row++;
		}

		row = 1;

		for (Level level : levels) {

			col = 1;
			
			int index = 0;
			index = ((row - 1) * cols) + col;

			CategoriesEditor categoriesEditor = dumpCategories(row, col, level,
					agreementDraftObject.getCategories(level));

			editors.set(index, categoriesEditor);
			categoriesEditor.hide(level.getId() == 0);
			
			col = 2;
			
			Button deleteButton = new Button();
			deleteButton.setStyleName(AON.AON_ICON_DELETE);
			deleteButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);

			LevelEditor deleteEditor = new LevelEditor(level);
			deleteEditor.setDeleteButton(deleteButton);
			

			hide(deleteButton, level.getId() == 0);

			index = ((row - 1) * cols) + col;
			editors.set(index, deleteEditor);
			salaryTable.setWidget(row, col, deleteButton);


			if (isDraftLevel(level)) {
				salaryTable.getRowFormatter().addStyleName(row, AON.AON_DATA_TABLE_ROW_HIGHLIGHT);
				salaryTable.getRowFormatter().addStyleName(row - 1, AON.AON_DATA_TABLE_ROW_HIGHLIGHT_TOP);
			}

			row++;
		}

		// Set max width...
		int offsetWidth = 1;
		offsetWidth += cellFormatter.getElement(0, 0).getOffsetWidth();

		// Asume that each column have 1.5 width of level column that has an
		// input with size 5
		offsetWidth += offsetWidth * 1.5 * (SALARY_TABLE_COLS - 1);
		offsetWidth += cellFormatter.getElement(0, salaryTable.getCellCount(0) - 1).getOffsetWidth();

		salaryTableScrollPane.setWidth(offsetWidth + "px");
		
		return editors;
	}
	
	
	private void initSalaryTableFrozenColsAndRows() {

		// Set max heigth...
		int offsetHeight = 1;
		offsetHeight += salaryTable.getCellFormatter().getElement(1, 0).getOffsetHeight() * SALARY_TABLE_LINES;
		salaryTableScrollPane.setHeight(offsetHeight + "px");

		salaryTableHead = getFreezeTableHead(salaryTable);
		salaryTableUpperLeftCorner = getFreezeTableUpperLeftCorner(salaryTable);
		salaryTableUpperRightCorner = getFreezeTableUpperRightCorner(salaryTable, salaryTableScrollPane);
		salaryTableFirstColumn = getFreezeTableFirstCol(salaryTable);
		salaryTableLastColumn = getFreezeTableLastCol(salaryTable, salaryTableScrollPane);

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
				int height = salaryTableScrollPane.getElement().getClientHeight();

				toFixedPosition(salaryTableUpperLeftCorner, width, height);
				toFixedPosition(salaryTableUpperRightCorner, width, height);
				toFixedPosition(salaryTableHead, width - salaryTableUpperLeftCorner.getOffsetWidth(), height);
				toFixedPosition(salaryTableFirstColumn, width, height - salaryTableUpperLeftCorner.getOffsetHeight());
				toFixedPosition(salaryTableLastColumn, width, height - salaryTableUpperLeftCorner.getOffsetHeight());
				moveSalaryTableFrozenColsAndRows();
				ensureChangesVisible();
			}
		});
	}

	private void loadAvailablePayments() {
		availablePaymens.clear();
		agreementDraftObject.getPaymentConcepts(new AsyncCallback<List<Payment>>() {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(List<Payment> result) {
				availablePaymens.addAll(result);
				paymentDescriptionOracle.clear();
				for (Payment payment : availablePaymens) {
					paymentDescriptionOracle.add(getSuggestionString(payment));
				}
			}
		});
	}

	private Payment getPayment(String suggestionString) {
		for (Payment payment : availablePaymens) {
			if (StringUtils.equals(suggestionString, getSuggestionString(payment)))
				return payment;
		}
		return null;
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

	private void clearEventsTable() {
		for (int i = eventsTable.getRowCount() - 1; i >= 0; i--)
			eventsTable.removeRow(i);
	}

	private void clearExtrasTable() {
		for (int i = extrasTable.getRowCount() - 1; i > 0; i--)
			extrasTable.removeRow(i);
	}

	private void clearPaymentsTable() {
		for (int i = paymentsTable.getRowCount() - 1; i > 0; i--)
			paymentsTable.removeRow(i);
	}

	private VariableEditor dumpVariable(int row, int col, Level level, Variable var) {

		TextBox expressionTextBox = new ExpressionBox();
		expressionTextBox.setMaxLength(EXPRESSION_MAX_LENGTH);
		expressionTextBox.addStyleName(AON.AON_TEXT_RIGHT);
		if (var.isImpicit())
			expressionTextBox.getElement().getStyle().setColor("gray");
		expressionTextBox.getElement().getStyle().setWidth(96, Unit.PCT);

		Object value = var.getValue();
		String expression = value == null ? var.getExpression() : value.toString();

		expressionTextBox.setText(expression);

		salaryTable.setWidget(row, col, expressionTextBox);

		VariableEditor variableEditor = new VariableEditor(level, var);
		variableEditor.setExpressionTextBox(expressionTextBox);

		if (isDraftVariable(level, var)) {
			salaryTable.getCellFormatter().addStyleName(row, col, AON.AON_DATA_TABLE_CELL_HIGHLIGHT);
			if (row > 0)
				salaryTable.getCellFormatter().addStyleName(row - 1, col, AON.AON_DATA_TABLE_CELL_HIGHLIGHT_TOP);
			expressionTextBox.addStyleName(AON.AON_ICON_CHANGED);
		}

		if (expression.matches("Error.*")) {
			expressionTextBox.setTitle(expression);
			expressionTextBox.removeStyleName(AON.AON_ICON_CHANGED);
			expressionTextBox.addStyleName(AON.AON_ICON_EXCEPTION);
			expressionTextBox.getElement().getStyle().setTextIndent(17, Unit.PX);
		}
		
		expressionTextBox.ensureDebugId("textBox_" + var.getName()+ "_" + level.getDescription() );

		return variableEditor;
	}

	private VariableEditor dumpUndefVariable(int row, int col, Level level, String name) {

		TextBox expressionTextBox = new ExpressionBox();
		expressionTextBox.setMaxLength(EXPRESSION_MAX_LENGTH);
		// yes we assume all variables are numeric.
		expressionTextBox.addStyleName(AON.AON_TEXT_RIGHT);
		// expressionTextBox.setVisibleLength(VARIABLE_TEXTBOX_SIZE);
		expressionTextBox.getElement().getStyle().setWidth(96, Unit.PCT);
		salaryTable.setWidget(row, col, expressionTextBox);

		Variable variable = new StringVariable();
		variable.setName(name);
		VariableEditor variableEditor = new VariableEditor(level, variable);

		variableEditor.setExpressionTextBox(expressionTextBox);

		expressionTextBox.ensureDebugId("textBox_" + name + "_" + level.getDescription());

		return variableEditor;
	}

	private CategoriesEditor dumpCategories(int row, int col, Level level, Set<String> categories) {

		String text = null;
		TextBox categoriesTextBox = new TextBox();

		if (categories != null) {
			text = StringUtils.reduce(categories, ", ");
			categoriesTextBox.setText(text);
		}

		if (StringUtils.isBlank(text)) {
			categoriesTextBox.addStyleName(AON.AON_ICON_WARN);
			categoriesTextBox.addStyleName(AON.AON_PADDING_LEFT);
			categoriesTextBox.setTitle("Defina al menos una categoria."
					+ " Recuerde que los empleados se asocian a categorias no a niveles retributivos.");
		}

		if (isDraftCategories(level)) {
			salaryTable.getCellFormatter().addStyleName(row, col, AON.AON_DATA_TABLE_CELL_HIGHLIGHT);
			if (row > 0)
				salaryTable.getCellFormatter().addStyleName(row - 1, col, AON.AON_DATA_TABLE_CELL_HIGHLIGHT_TOP);
			if (!StringUtils.isBlank(text)) {
				categoriesTextBox.addStyleName(AON.AON_ICON_CHANGED);
				categoriesTextBox.addStyleName(AON.AON_PADDING_LEFT);
			}
		}

		categoriesTextBox.getElement().getStyle().setWidth(98, Unit.PCT);
		categoriesTextBox.getElement().getStyle().setProperty("minWidth", VARIABLE_TEXTBOX_SIZE * 2, Unit.EM);
		salaryTable.setWidget(row, col, categoriesTextBox);

		CategoriesEditor categoriesEditor = new CategoriesEditor(level);
		categoriesEditor.setCategoriesTextBox(categoriesTextBox);

		return categoriesEditor;
	}

	private IFocusableEditor insertNewLevelRow(int row) {

		TextBox descriptionTextBox = new TextBox();
		descriptionTextBox.setVisibleLength(5);
		descriptionTextBox.setMaxLength(DESCRIPTION_MAX_LENGTH);
		salaryTable.setWidget(row, 0, descriptionTextBox);

		int col;
		//int cols = salaryTable.getCellCount(row - 1);
		int cols = salaryTable.getCellCount(row - 2);
		for (col = 1; col < cols - 2; col++)
			salaryTable.setWidget(row, col, newHiddenTextBox(VARIABLE_TEXTBOX_SIZE));

		for (; col < cols; col++)
			salaryTable.insertCell(row, col);

		LevelEditor editor = new LevelEditor(agreementDraftObject.newLevel()){
			@Override
			public void setReadOnly(boolean readOnly) {
				if ( this.deleteButton != null )
					this.deleteButton.setVisible(!readOnly);
				if ( this.descriptionTextBox != null)
					this.descriptionTextBox.setVisible(!readOnly);
			}
		};
		editor.setDescriptionTextBox(descriptionTextBox);

		return editor;
	}

	private void dumpEvent(int row, Event event) {
		String styles[] = eventStyles.get(event.getType());

		Button headButton = new Button();
		headButton.setStyleName(styles[0]);
		headButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);

		eventsTable.setWidget(row, 0, headButton);

		eventsTable.setHTML(row, 1, event.getMessage());
		eventsTable.getCellFormatter().getElement(row, 1).getStyle().setWhiteSpace(WhiteSpace.NORMAL);
		eventsTable.getCellFormatter().getElement(row, 1).getStyle().setProperty("maxWidth", 55, Unit.EM);


		eventsTable.setHTML(row, 2, "&nbsp;");

		eventsTable.getCellFormatter().getElement(row, 0).getStyle().setPropertyPx("borderRightWidth", 0);
		eventsTable.getCellFormatter().getElement(row, 1).getStyle().setPropertyPx("borderLeftWidth", 0);
		eventsTable.getCellFormatter().getElement(row, 1).getStyle().setPropertyPx("borderRightWidth", 0);
		eventsTable.getCellFormatter().getElement(row, 2).getStyle().setPropertyPx("borderLeftWidth", 0);

		for (int col = 0; col < eventsTable.getCellCount(row); col++)
			eventsTable.getCellFormatter().addStyleName(row, col, "aon-panelGrid-odd");
	}

	private ExtraEditor dumpExtra(Extra extra, int row, SortedSet<Payment> availablePayments) {

		// first cell for edit other stuff buttons.
		Button editButton = new Button();
		editButton.setStyleName(getIconRowStyle(extra));
		editButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		extrasTable.setWidget(row, 0, editButton);

		ValueBox<Date> startDateBox = newDateBox();
		try {
			Date startDate = parseExtraDate(extra.getStartDate());
			startDateBox.setValue(startDate);
		} catch (EmptyStringException e) {
			startDateBox.setTitle("Es necesario introducir una fecha inicial de devengo.");
		} catch (DateTimeFormatException e) {
			startDateBox.setTitle("La fecha inicial de devengo '" + extra.getStartDate() + "' no es correcta.");
			startDateBox.addStyleName(AON.AON_ICON_EXCEPTION);
			startDateBox.setText(extra.getStartDate());
		}
		startDateBox.ensureDebugId("startDateBox" + row);
		
		
		Button startButton = new Button();

		extrasTable.setWidget(row, 1, newExtraDatePanel(startDateBox, startButton));

		ValueBox<Date> endDateBox = newDateBox();
		try {
			Date endDate = parseExtraDate(extra.getEndDate());
			endDateBox.setValue(endDate);
		} catch (EmptyStringException e) {
			endDateBox.setTitle("Es necesario introducir una fecha final de devengo.");
			endDateBox.addStyleName(AON.AON_ICON_EXCEPTION);
		} catch (DateTimeFormatException e) {
			endDateBox.setTitle("La fecha final de devengo '" + extra.getEndDate() + "' no es correcta.");
			endDateBox.addStyleName(AON.AON_ICON_EXCEPTION);
		}
		endDateBox.ensureDebugId("endDateBox" + row);
		
		Button endButton = new Button();

		extrasTable.setWidget(row, 2, newExtraDatePanel(endDateBox, endButton));

		ValueBox<Date> issueDateBox = newDateBox();
		try {
			Date issueDate = parseExtraDate(extra.getIssueDate());
			issueDateBox.setValue(issueDate);
		} catch (EmptyStringException e) {
			issueDateBox.setTitle("Es necesario introducir una fecha de cobro.");
			issueDateBox.addStyleName(AON.AON_ICON_EXCEPTION);
		} catch (DateTimeFormatException e) {
			issueDateBox.setTitle("La fecha de cobro '" + extra.getIssueDate() + "' no es correcta.");
			issueDateBox.addStyleName(AON.AON_ICON_EXCEPTION);
		}
		issueDateBox.ensureDebugId("issueDateBox" + row);
		
		Button issueButton = new Button();

		extrasTable.setWidget(row, 3, newExtraDatePanel(issueDateBox, issueButton));

		ListBox paymentListBox = new ListBox();
		paymentListBox.addItem("-", (String) null);

		Payment extraPayment = getExtraPayment(extra);
		if (extraPayment != null) {
			paymentListBox.addItem(extraPayment.getDescription(), String.valueOf(extraPayment.getId()));
			paymentListBox.setSelectedIndex(1);
		}

		for (Payment payment : availablePayments) {
			paymentListBox.addItem(payment.getDescription(), String.valueOf(payment.getId()));
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
		editor.setButtonFor(endButton, endDateBox);
		editor.setStartDateBox(startDateBox);
		editor.setButtonFor(startButton, startDateBox);
		editor.setIssueDateBox(issueDateBox);
		editor.setButtonFor(issueButton, issueDateBox);
		editor.setPaymentListBox(paymentListBox);
		editor.setDeleteButton(deleteButton);

		if (isDraftExtra(extra)) {
			extrasTable.getRowFormatter().addStyleName(row, AON.AON_DATA_TABLE_ROW_HIGHLIGHT);
			extrasTable.getRowFormatter().addStyleName(row - 1, AON.AON_DATA_TABLE_ROW_HIGHLIGHT_TOP);
		}

		return editor;

	}

	private PaymentEditor dumpPayment(Payment payment, int row) {

		// first cell for edit other stuff buttons.
		Button editButton = new Button();
		editButton.setStyleName(getIconRowStyle(payment));
		editButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);

		paymentsTable.setWidget(row, 0, editButton);

		TypeListBox<Payment.Type> paymentTypeListBox = new TypeListBox<Payment.Type>(Payment.Type.class, 10);
		paymentTypeListBox.setSelected(payment.getType());

		TextBox descriptionBox = new TextBox();
		descriptionBox.setMaxLength(DESCRIPTION_MAX_LENGTH);
		descriptionBox.setText(payment.getDescription());
		descriptionBox.getElement().getStyle().setWidth(98, Unit.PCT);
		paymentsTable.setWidget(row, 1, descriptionBox);

		TextBox expressionBox = new ExpressionBox();
		expressionBox.setMaxLength(EXPRESSION_MAX_LENGTH);
		expressionBox.setText(payment.getExpression());
		expressionBox.getElement().getStyle().setWidth(98, Unit.PCT);
		expressionBox.addStyleName(AON.AON_TEXT_RIGHT);
		paymentsTable.setWidget(row, 2, expressionBox);
		contentAssistManager.addValueBox(expressionBox);

		Button deleteButton = new Button();
		deleteButton.setStyleName(AON.AON_ICON_DELETE);
		deleteButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		paymentsTable.setWidget(row, 3, deleteButton);
		paymentsTable.getCellFormatter().addStyleName(row, 3, AON.AON_TEXT_RIGHT);
		formatPaymentRow(row);

		PaymentEditor paymentEditor = new PaymentEditor(payment);
		paymentEditor.setEditButton(editButton);
		paymentEditor.setDeleteButton(deleteButton);
		paymentEditor.setExpressionTextBox(expressionBox);
		paymentEditor.setDescriptionTextBox(descriptionBox);
		paymentEditor.setPaymentTypeListBox(paymentTypeListBox);

		if (isDraftPayment(payment)) {
			paymentsTable.getRowFormatter().addStyleName(row, AON.AON_DATA_TABLE_ROW_HIGHLIGHT);
			paymentsTable.getRowFormatter().addStyleName(row - 1, AON.AON_DATA_TABLE_ROW_HIGHLIGHT_TOP);
		}

		if (isError(payment))
			addStyle(paymentsTable, row, style.textError());
		else if (isWarn(payment))
			addStyle(paymentsTable, row, style.textWarn());
		else if (isRemove(payment))
			addStyle(paymentsTable, row, style.textWarn());
		
		editButton.ensureDebugId("edit-button-" + row );
		deleteButton.ensureDebugId("delete-button-" + row );
		expressionBox.ensureDebugId("expression-box" + row );
		descriptionBox.ensureDebugId("description-box" + row );
		paymentTypeListBox.ensureDebugId("payment-type-list-box" + row );
		ensureDebugId(paymentsTable.getRowFormatter().getElement(row), "payment-row-" + row);
		
		return paymentEditor;
	}

	private ExtraEditor insertNewExtraRow(int row, SortedSet<Payment> payments) {
		// first cell for edit other stuff buttons.
		Button newButton = new Button();
		newButton.setStyleName(AON.AON_ICON_RESET);
		newButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		extrasTable.setWidget(row, 0, newButton);

		DateTimeFormat yearMonthNumDayFormat = DateTimeFormat.getFormat("d/M/y");

		ValueBox<Date> startDateBox = newDateBox();
		Button startButton = new Button();

		extrasTable.setWidget(row, 1, newExtraDatePanel(startDateBox, startButton));

		ValueBox<Date> endDateBox = newDateBox();
		Button endButton = new Button();

		extrasTable.setWidget(row, 2, newExtraDatePanel(endDateBox, endButton));

		ValueBox<Date> issueDateBox = newDateBox();
		Button issueButton = new Button();
		extrasTable.setWidget(row, 3, newExtraDatePanel(issueDateBox, issueButton));

		ListBox paymentListBox = new ListBox();
		for (Payment payment : payments) {
			paymentListBox.addItem(payment.getDescription(), String.valueOf(payment.getId()));
		}
		paymentListBox.addStyleName(AON.AON_WIDTH_ALL);
		extrasTable.setWidget(row, 4, paymentListBox);

		extrasTable.insertCell(row, 5);

		formatExtraRow(row);

		ExtraEditor editor = new ExtraEditor(agreementDraftObject.newDraftExtra()){
			void setReadOnly(boolean readOnly) {
				if ( this.deleteButton != null )
					this.deleteButton.setVisible(!readOnly);
				if ( this.endDateBox != null )
					this.endDateBox.setVisible(!readOnly);
				if ( this.startDateBox != null )
					this.startDateBox.setVisible(!readOnly);
				if ( this.issueDateBox != null )
					this.issueDateBox.setVisible(!readOnly);
				if ( paymentListBox != null ) 
					this.paymentListBox.setVisible(!readOnly);
				for ( Button button: buttons )
					button.setVisible(!readOnly);
			}
		};

		editor.setEndDateBox(endDateBox);
		editor.setButtonFor(endButton, endDateBox);
		editor.setStartDateBox(startDateBox);
		editor.setButtonFor(startButton, startDateBox);
		editor.setIssueDateBox(issueDateBox);
		editor.setButtonFor(issueButton, issueDateBox);
		editor.setPaymentListBox(paymentListBox);


		return editor;
	}

	private PaymentEditor insertNewPaymentRow(int row) {
		// first cell for edit other stuff buttons.

		Button newButton = new Button();
		newButton.setStyleName(AON.AON_ICON_RESET);
		newButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		paymentsTable.setWidget(row, 0, newButton);
		newButton.ensureDebugId("button-new-payment");

		TypeListBox<Payment.Type> paymentTypeListBox = new TypeListBox<Payment.Type>(Payment.Type.class, 10);
		paymentTypeListBox.setSelected(Payment.Type.DEFAULT);

		TextBox descriptionBox = new TextBox();
		descriptionBox.setMaxLength(DESCRIPTION_MAX_LENGTH);
		SuggestBox descriptionSuggest = new SuggestBox(paymentDescriptionOracle, descriptionBox,
				paymentSuggestionDisplay);
		descriptionSuggest.setAutoSelectEnabled(false);
		descriptionSuggest.getElement().getStyle().setWidth(98, Unit.PCT);
		paymentsTable.setWidget(row, 1, descriptionSuggest);
		descriptionBox.ensureDebugId("description-box-new-payment");

		TextBox expressionBox = new ExpressionBox();
		expressionBox.setMaxLength(EXPRESSION_MAX_LENGTH);
		expressionBox.getElement().getStyle().setWidth(98, Unit.PCT);
		expressionBox.addStyleName(AON.AON_TEXT_RIGHT);
		paymentsTable.setWidget(row, 2, expressionBox);
		expressionBox.ensureDebugId("amount-box-new-payment");

		paymentsTable.insertCell(row, 3);

		formatPaymentRow(row);
		Payment payment = agreementDraftObject.newDraftPayment();

		payment.setIrpfExpression("_P");
		payment.setQuoteExpression("_P");
		payment.setType(Payment.Type.DEFAULT);
		payment.setSalaryType(Salary.Type.SALARY);

		PaymentEditor paymentEditor = new PaymentEditor(payment){
			@Override
			void setReadOnly(boolean readOnly) {
				if ( this.deleteButton != null )
					this.deleteButton.setVisible(!readOnly);
				if ( this.expressionBox != null )
					this.expressionBox.setVisible(!readOnly);
				if ( this.descriptionBox != null )
					this.descriptionBox.setVisible(!readOnly);
				if ( this.typeListBox != null )
					this.typeListBox.setVisible(!readOnly);
			}
		};
		paymentEditor.setEditButton(newButton);
		paymentEditor.setExpressionTextBox(expressionBox);
		paymentEditor.setDescriptionSuggestBox(descriptionSuggest);

		return paymentEditor;

	}

	private void formatExtraRow(int row) {
		extrasTable.getCellFormatter().getElement(row, 0).getStyle().setPropertyPx("borderRightWidth", 0);
		extrasTable.getCellFormatter().getElement(row, 1).getStyle().setPropertyPx("borderLeftWidth", 0);
		extrasTable.getCellFormatter().getElement(row, 4).getStyle().setPropertyPx("borderRightWidth", 0);
		if (extrasTable.getCellCount(row) > 5)
			extrasTable.getCellFormatter().getElement(row, 5).getStyle().setPropertyPx("borderLeftWidth", 0);

		extrasTable.getRowFormatter().addStyleName(row,
				row % 2 == 0 ? AON.AON_DATA_TABLE_ROW_ODD : AON.AON_DATA_TABLE_ROW_EVEN);
	}

	private void formatPaymentRow(int row) {
		paymentsTable.getCellFormatter().getElement(row, 0).getStyle().setPropertyPx("borderRightWidth", 0);
		paymentsTable.getCellFormatter().getElement(row, 1).getStyle().setPropertyPx("borderLeftWidth", 0);
		paymentsTable.getCellFormatter().getElement(row, 2).getStyle().setPropertyPx("borderRightWidth", 0);
		if (paymentsTable.getCellCount(row) > 3)
			paymentsTable.getCellFormatter().getElement(row, 3).getStyle().setPropertyPx("borderLeftWidth", 0);

		paymentsTable.getRowFormatter().addStyleName(row,
				row % 2 == 0 ? AON.AON_DATA_TABLE_ROW_ODD : AON.AON_DATA_TABLE_ROW_EVEN);
	}

	private void initExtrasTable() {

		extrasTable.setText(0, 0, "INICIO");
		extrasTable.getFlexCellFormatter().setColSpan(0, 0, 2);
		extrasTable.getCellFormatter().addStyleName(0, 0, AON.AON_INPUT_REQUIRED);
		extrasTable.setText(0, 1, "FIN");
		extrasTable.getCellFormatter().addStyleName(0, 1, AON.AON_INPUT_REQUIRED);
		extrasTable.setText(0, 2, "COBRO");
		extrasTable.getCellFormatter().addStyleName(0, 2, AON.AON_INPUT_REQUIRED);
		extrasTable.setText(0, 3, "CONCEPTO");
		extrasTable.getFlexCellFormatter().setColSpan(0, 3, 2);
		// endDateBox.addStyleName(AON.AON_INPUT_REQUIRED);

		extrasTable.getRowFormatter().addStyleName(0, AON.AON_DATA_TABLE_ROW_ODD);
		for (int i = 0; i < extrasTable.getCellCount(0); i++) {
			extrasTable.getCellFormatter().addStyleName(0, i, AON.AON_BOLD);
			extrasTable.getCellFormatter().addStyleName(0, i, AON.AON_TEXT_CENTER);
		}

		extrasTable.getColumnFormatter().setWidth(0, "2%");
		extrasTable.getColumnFormatter().setWidth(1, "5%"); // INICIO
		extrasTable.getColumnFormatter().setWidth(2, "5%"); // FIN
		extrasTable.getColumnFormatter().setWidth(3, "5%"); // COBRO
		// 4 ...
		extrasTable.getColumnFormatter().setWidth(5, "2%");

	}

	private void initPaymentsTable() {

		paymentsTable.setText(0, 0, "CONCEPTO");
		paymentsTable.getFlexCellFormatter().setColSpan(0, 0, 2);
		paymentsTable.setText(0, 1, "DEVENGO");
		paymentsTable.getFlexCellFormatter().setColSpan(0, 1, 2);

		paymentsTable.getRowFormatter().addStyleName(0, AON.AON_DATA_TABLE_ROW_ODD);
		for (int i = 0; i < paymentsTable.getCellCount(0); i++) {
			paymentsTable.getCellFormatter().addStyleName(0, i, AON.AON_BOLD);
			paymentsTable.getCellFormatter().addStyleName(0, i, AON.AON_TEXT_CENTER);
		}
		
		paymentsTable.getColumnFormatter().setWidth(0, "2%");	// RESET
		//1.. 													// CONCEPTO
		paymentsTable.getColumnFormatter().setWidth(2, "38%"); 	// DEVENGO
		paymentsTable.getColumnFormatter().setWidth(3, "2%");	// DELETE

	}

	private void initEventsStyles(MyStyle myStyle) {
		eventStyles = new HashMap<Event.Type, String[]>();
		eventStyles.put(Event.Type.INFO, new String[] { "aon-icon-info", "" });
		eventStyles.put(Event.Type.DEBUG, new String[] { "aon-icon-info", "" });
		eventStyles.put(Event.Type.ERROR, new String[] { "aon-icon-exception", myStyle.textError() });
		eventStyles.put(Event.Type.WARNING, new String[] { AON.AON_ICON_WARN, myStyle.textWarn() });
	}

	// @formatter:off
	// ======================================
	// | NIVEL | SALARIO_BASE | PAGA_EXTRA |.
	// ======================================
	// | | ###.###,00 | ###.###,00 |.
	// --------------------------------------
	// | 01 | ###.###,00 | ###.###,00 |.
	// --------------------------------------
	// | 02 | ###.###,00 | ###.###,00 |.
	// --------------------------------------
	// @formatter:on
	private void moveSalaryTableFrozenColsAndRows() {
		int verticalScroll = salaryTableScrollPane.getVerticalScrollPosition();
		int horizontalScroll = salaryTableScrollPane.getHorizontalScrollPosition();

		int clientWidth = Math.min(salaryTableScrollPane.getElement().getClientWidth(),
				draftScrollPane.getElement().getClientWidth());
		int clientHeight = salaryTableScrollPane.getElement().getClientHeight();

		int top = salaryTableScrollPane.getAbsoluteTop();
		int left = salaryTableScrollPane.getAbsoluteLeft();

		int clipTop = draftScrollPane.getAbsoluteTop() - top;
		int clipLeft = draftScrollPane.getAbsoluteLeft() - left;

		salaryTableUpperLeftCorner.getStyle().setTop(top, Unit.PX);
		salaryTableUpperLeftCorner.getStyle().setLeft(left, Unit.PX);
		setClip(salaryTableUpperLeftCorner.getStyle(), clipTop, salaryTableUpperLeftCorner.getOffsetWidth(),
				salaryTableUpperLeftCorner.getOffsetHeight(), clipLeft);

		salaryTableUpperRightCorner.getStyle().setTop(top, Unit.PX);
		left = salaryTableScrollPane.getAbsoluteLeft() + clientWidth - salaryTableUpperRightCorner.getOffsetWidth()
				+ draftScrollPane.getHorizontalScrollPosition();
		salaryTableUpperRightCorner.getStyle().setLeft(left + 1, Unit.PX);
		setClip(salaryTableUpperRightCorner.getStyle(), clipTop, salaryTableUpperRightCorner.getOffsetWidth(),
				salaryTableUpperRightCorner.getOffsetHeight(), 0);

		left = salaryTableScrollPane.getAbsoluteLeft() + salaryTableUpperLeftCorner.getOffsetWidth() - horizontalScroll;
		salaryTableHead.getStyle().setTop(top, Unit.PX);
		salaryTableHead.getStyle().setLeft(left - 1, Unit.PX);

		clipLeft = horizontalScroll + 1 + (clipLeft > 0 ? clipLeft : 0);
		int clipRight = horizontalScroll + clientWidth - salaryTableUpperLeftCorner.getOffsetWidth() + 1;
		setClip(salaryTableHead.getStyle(), clipTop, clipRight, clientHeight, clipLeft);

		left = salaryTableScrollPane.getAbsoluteLeft();
		top = salaryTableScrollPane.getAbsoluteTop() + salaryTableUpperLeftCorner.getOffsetHeight() - verticalScroll;
		salaryTableFirstColumn.getStyle().setLeft(left, Unit.PX);
		salaryTableFirstColumn.getStyle().setTop(top - 1, Unit.PX);

		clipTop = verticalScroll + 1;
		if ((top + clipTop) < draftScrollPane.getAbsoluteTop())
			clipTop += draftScrollPane.getAbsoluteTop() - top - clipTop;

		clipLeft = draftScrollPane.getAbsoluteLeft() - left;
		int clipBottom = verticalScroll + (clientHeight - salaryTableUpperLeftCorner.getOffsetHeight()) + 1;
		setClip(salaryTableFirstColumn.getStyle(), clipTop, clientWidth, clipBottom, clipLeft);

		salaryTableLastColumn.getStyle().setTop(top - 1, Unit.PX);
		left = salaryTableScrollPane.getAbsoluteLeft() + clientWidth - salaryTableLastColumn.getOffsetWidth()
				+ draftScrollPane.getHorizontalScrollPosition();
		salaryTableLastColumn.getStyle().setLeft(left + 1, Unit.PX);
		setClip(salaryTableLastColumn.getStyle(), clipTop, clientWidth, clipBottom, 0 /**/);

	}

	private void enableUndoRedoButtons() {
		undoButton.setEnabled(agreementDraftObject.canUndo());
		redoButton.setEnabled(agreementDraftObject.canRedo());
	}

	private void ensureChangesVisible() {
		// shows first level changed row.
		if (!changedLevelsRows.isEmpty()) {
			ensureVisibleTopImpl(salaryTableScrollPane.getElement(),
					salaryTable.getRowFormatter().getElement(changedLevelsRows.get(0)));
		}
		// shows first variable changed column.
		if (!changedVariablesCols.isEmpty()) {
			ensureVisibleLeftImpl(salaryTableScrollPane.getElement(),
					salaryTable.getCellFormatter().getElement(0, changedVariablesCols.get(0)));
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
			if ( (payment.getType() == Payment.Type.CRA_0004
				|| payment.getType() == Payment.Type.CRA_0005)
				&& !extraIds.contains(payment.getId()))
				payments.add(payment);

		return payments;
	}

	private boolean isExtraPayment(Payment payment) {

		for (Extra extra : agreementDraftObject.getExtras())
			if (extra.getPaymentId() != null && extra.getPaymentId().equals(payment.getId()))
				return true;

		return false;
	}

	private Date parseExtraDate(String text) {
		return parseExtraDate(text, CalendarUtil.copyDate(TODAY));
	}

	private String formatExtraDate(Date extraDate) {
		return formatExtraDate(extraDate, TODAY);
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
			throw new DateTimeFormatException("'" + text + "/" + start + "' it's not a valid extra date");
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
			throw new DateTimeFormatException("'" + text + "/" + start + "' it's not a valid extra date");
		}
	}

	protected static Date getReferenceDate(String text, Date date) {
		if (text == null)
			throw new EmptyStringException();

		text = text.trim();

		int start = -1;

		// Skip d/M
		while (++start < text.length() && !Character.isSpace(text.charAt(start)))
			;
		while (Character.isSpace(text.charAt(start)) && ++start < text.length())
			;

		if (start == text.length())
			return date;

		try {
			int years = Integer.valueOf(text.substring(start));
			return DateUtils.addYears2Date(date, -1 * years);
		} catch (Throwable t) {
			throw new DateTimeFormatException("'" + text + "/" + start + "' it's not a valid extra date");
		}

	}

	private boolean isMine() {
		return agreementDraftObject.isMine();
	}

	private boolean isDraftLevel(Level level) {
		return agreementDraftObject.isDraftLevel(level);
	}

	private boolean isDraftExtra(Extra extra) {
		return agreementDraftObject.isDraftExtra(extra);
	}

	private boolean isNotMine(Payment payment) {
		return !agreementDraftObject.isMine(payment);
	}

	private boolean isWarn(Payment payment) {
		return getEventType(payment) == WARNING;
	}

	private boolean isError(Payment payment) {
		return getEventType(payment) == ERROR;
	}

	private boolean isDraftPayment(Payment payment) {
		return agreementDraftObject.isDraftPayment(payment);
	}

	private boolean isDraftVariable(Level level, Variable variable) {
		return agreementDraftObject.isDraftVariable(level, variable);
	}

	private boolean isDraftCategories(Level level) {
		return agreementDraftObject.isDraftCategories(level);
	}

	private com.esferalia.aon.gwt.payroll.shared.Event.Type getEventType(Payment payment) {
		for (Event event : agreementDraftObject.getEvents())
			if (event instanceof PaymentEvent)
				if (((PaymentEvent) event).getPayment().equals(payment))
					return ((PaymentEvent) event).getType();
		return null;
	}

	private String getIconRowStyle(Extra extra) {
		return isDraftExtra(extra) ? AON.AON_ICON_ROW_SELECTOR_CHANGED : AON.AON_ICON_ROW_SELECTOR;
	}

	private String getIconRowStyle(Payment payment) {
		if (isWarn(payment))
			return AON.AON_ICON_WARN;
		if (isError(payment))
			return AON.AON_ICON_ERROR;
		if (isRemove(payment))
			return AON.AON_ICON_WARN;
		if (isDraftPayment(payment))
			return AON.AON_ICON_ROW_SELECTOR_CHANGED;
		if (isNotMine(payment)) {
			return AON.AON_ICON_ROW_SELECTOR_PARENT;
		}
		return AON.AON_ICON_ROW_SELECTOR;
	}

	// ------------------------------------------------------------------------

	private static Element clear(Element el) {
		if (el != null)
			el.removeFromParent();
		return null;
	}

	private static Element cloneTR(Element tr) {
		Element clone = DOM.clone(tr, true);

		com.google.gwt.dom.client.Element td = tr.getFirstChildElement();
		com.google.gwt.dom.client.Element cloneTd = clone.getFirstChildElement();

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

		int width = flexTable.getCellFormatter().getElement(0, 0).getOffsetWidth();

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

	private static Element getFreezeTableUpperRightCorner(FlexTable flexTable, ScrollPanel scrollPane) {

		Element th = cloneTR(flexTable.getRowFormatter().getElement(0));

		int cols = th.getChildCount();
		// remove all columns except last..
		for (int i = cols - 2; i >= 0; i--) {
			th.getChild(i).removeFromParent();
		}

		com.google.gwt.dom.client.Element clone = th.getFirstChildElement();

		Element td = flexTable.getCellFormatter().getElement(0, cols - 1);
		if (DOM.getChildCount(td) > 0) {
			Element tdChild = DOM.getChild(td, 0);
			Element cloneChild = DOM.getChild(clone, 0);
			DOM.removeChild(td, tdChild);
			DOM.removeChild(clone, cloneChild);
			DOM.appendChild(clone, tdChild);
			DOM.appendChild(td, cloneChild);

		}

		int width = flexTable.getCellFormatter().getElement(0, flexTable.getCellCount(0) - 1).getOffsetWidth();

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

		int left = flexTable.getCellFormatter().getElement(0, 0).getOffsetWidth();

		// removes first cell, this belongs to corner.
		th.getFirstChildElement().removeFromParent();

		int width = 0;
		for (int col = 1; col < flexTable.getCellCount(0); col++)
			width += flexTable.getCellFormatter().getElement(0, col).getOffsetWidth();

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
		int width = flexTable.getCellFormatter().getElement(0, 0).getOffsetWidth();
		table.getStyle().setWidth(width + 2, Unit.PX);

		return table;
	}

	private static Element getFreezeTableLastCol(FlexTable flexTable, ScrollPanel scrollPanel) {
		int col = flexTable.getCellCount(0) - 1;
		Element table = getFreezeTableCol(flexTable, col);

		int width = flexTable.getCellFormatter().getElement(0, col).getOffsetWidth();
		int left = scrollPanel.getElement().getClientWidth() - width;
		table.getStyle().setLeft(left - 1, Unit.PX);

		return table;
	}

	private static Element getFreezeTableCol(FlexTable flexTable, int col) {

		Element table = DOM.createTable();
		Element tbody = DOM.createTBody();

		for (int row = 1; row < flexTable.getRowCount(); row++) {
			Element tr = DOM.createTR();
			tr.setClassName(flexTable.getRowFormatter().getElement(row).getClassName());

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

		int width = flexTable.getCellFormatter().getElement(0, col).getOffsetWidth();
		int height = flexTable.getCellFormatter().getElement(0, col).getOffsetHeight();

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
		style.setProperty("clip", "rect(0px," + width + "px," + height + "px,0px)");
		setClip(style, 0, width, height, 0);

	}

	private static void setClip(Style style, int top, int right, int bottom, int left) {
		style.setProperty("clip", "rect(" + top + "px," + right + "px," + bottom + "px, " + left + "px)");
	}

	private static String getSuggestionString(Payment payment) {
		String description = payment.getDescription();
		StringBuffer suggestion = StringUtils.isEmpty(description) ? new StringBuffer() : new StringBuffer(description);
		if (!StringUtils.isEmpty(payment.getName()))
			suggestion.append(" (").append(payment.getName()).append(")");
		return suggestion.toString();
	}

	private static int getRealOffsetTop(Element scroll, Element e) {
		int realOffsetTop = 0;
		for (com.google.gwt.dom.client.Element item = e; item != scroll; item = item.getOffsetParent())
			realOffsetTop += item.getOffsetTop();

		return realOffsetTop;
	}

	private static int getRealOffsetLeft(Element scroll, Element e) {
		int realOffsetLeft = 0;
		for (com.google.gwt.dom.client.Element item = e; item != scroll; item = item.getOffsetParent())
			realOffsetLeft += item.getOffsetLeft();

		return realOffsetLeft;
	}

	private static void ensureVisibleTopImpl(Element scroll, Element e) {
		scroll.setScrollTop(getRealOffsetTop(scroll, e) - scroll.getOffsetHeight() / 2);
	}

	private static void ensureVisibleLeftImpl(Element scroll, Element e) {
		scroll.setScrollLeft(getRealOffsetLeft(scroll, e) - scroll.getOffsetWidth() / 2);
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

	private Widget newHiddenTextBox(int size) {
		TextBox textBox = new TextBox();
		textBox.setVisibleLength(size);
		textBox.getElement().getStyle().setVisibility(Visibility.HIDDEN);
		return textBox;
	}

	private ExtraEditor getExtraEditorFor(int id) {
		for (ExtraEditor editor : extraEditors)
			if (id == editor.extra.getId())
				return editor;
		return null;
	}

	private ExtraEditor getNextExtraEditorFor(int id) {
		for (Iterator<ExtraEditor> iterator = extraEditors.iterator(); iterator.hasNext();) {
			ExtraEditor editor = iterator.next();
			if (id == editor.extra.getId())
				return iterator.hasNext() ? iterator.next() : null;

		}
		return null;
	}

	private PaymentEditor getPaymentEditorFor(int id) {
		for (PaymentEditor editor : paymentEditors)
			if (id == editor.payment.getId())
				return editor;
		return null;
	}

	private PaymentEditor getNextPaymentEditorFor(int id) {
		for (Iterator<PaymentEditor> iterator = paymentEditors.iterator(); iterator.hasNext();) {
			PaymentEditor editor = iterator.next();
			if (id == editor.payment.getId())
				return iterator.hasNext() ? iterator.next() : null;

		}
		return null;
	}

	private IFocusableEditor getNextSalaryTableEditorIndexOf(IFocusableEditor editor) {
		int index = salaryTableEditors.indexOf(editor) + 1;
		return index < salaryTableEditors.size() ? salaryTableEditors.get(index) : null;
	}

	private int getSalaryTableEditorIndexOf(IFocusableEditor editor) {
		return salaryTableEditors.indexOf(editor);
	}

	private IFocusableEditor getSalaryTableEditorFor(int index) {
		if (index < 0)
			return null;
		if (index >= salaryTableEditors.size())
			return null;
		return salaryTableEditors.get(index);
	}

	private IFocusableEditor getNextSalaryTableEditorForLevel(int id) {
		Set<Level> levels = agreementDraftObject.getLevels();
		Set<String> vars = agreementDraftObject.getVariables();

		int cols = vars != null ? vars.size() + 2 : 2;
		int rows = levels != null ? levels.size() : 0;

		for (int row = 0; row < rows; row++) {
			int index = row * cols;
			LevelEditor editor = (LevelEditor) salaryTableEditors.get(index);
			if (editor.level.getId() == id) {
				int next = index + 1;
				return salaryTableEditors.size() > next ? salaryTableEditors.get(next) : null;
			}
		}
		return null;
	}

	// ---------------------------------------------------------------- Preview
	private Type getType() {
		int index = typeListBox.getSelectedIndex();
		String value = typeListBox.getValue(index);
		return Type.valueOf(value);
	}

	private void initTypeListBox() {
		typeListBox.clear();
		typeListBox.addItem(Type.SALARY.getDescription(), Type.SALARY.name());
		typeListBox.setSelectedIndex(0);
	}

	private int getLevelId() {
		int index = levelListBox.getSelectedIndex();
		String value = levelListBox.getValue(index);
		return Integer.valueOf(value);

	}

	private void initLevelListBox() {
		levelListBox.clear();
		for (Level level : agreementDraftObject.getLevels()) {
			if (level.getId() == 0)
				continue;
			String description = level.getDescription();
			StringBuffer buffer = new StringBuffer();
			if (!StringUtils.isBlank(description))
				buffer.append(description);

			Set<String> categories = agreementDraftObject.getCategories(level);
			if (categories != null) {
				for (String category : categories) {
					if (!StringUtils.isBlank(category)) {
						buffer.append(" " + category);
						break;
					}
				}
			}
			levelListBox.addItem(buffer.toString(), Integer.toString(level.getId()));
		}

	}

	private int getZoom() {
		int index = zoomListBox.getSelectedIndex();
		String value = zoomListBox.getValue(index);
		return Integer.valueOf(value);
	}

	private void initZoomListBox() {
		if (zoomListBox.getItemCount() > 0)
			return;

		for (int zoom = MIN_ZOOM; zoom < DEFAULT_ZOOM; zoom += ZOOM_STEP)
			zoomListBox.addItem(PERCENT_FORMAT.format((double) zoom / 100), Integer.toString(zoom));

		zoomListBox.addItem(PERCENT_FORMAT.format((double) DEFAULT_ZOOM / 100), Integer.toString(DEFAULT_ZOOM));
		zoomListBox.setSelectedIndex(zoomListBox.getItemCount() - 1);

		for (int zoom = DEFAULT_ZOOM + ZOOM_STEP; zoom <= MAX_ZOOM; zoom += ZOOM_STEP)
			zoomListBox.addItem(PERCENT_FORMAT.format((double) zoom / 100), Integer.toString(zoom));
	}

	private void initPreviewMonthListBox() {
		previewMonthListBox.setHighLightMonths(agreementDraftObject.getDatesWithChanges());
		previewMonthListBox.setSelectedMonth(agreementDraftObject.getStartDate());

	}

	private void showDraft() {
		deckPanel.showWidget(deckPanel.getWidgetIndex(draftPanel));
	}

	private void showPreview() {
		deckPanel.showWidget(deckPanel.getWidgetIndex(printPreviewPanel));
	}

	private void printPreview() {
		int zoom = getZoom();
		Type type = getType();
		int levelId = getLevelId();

		agreementDraftObject.preview(levelId, type, zoom, new AsyncCallback<String>() {

			@Override
			public void onSuccess(String html) {
				printPreviewHTML.setHTML(html);
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Almost auto-generated method stub
				printPreviewHTML.setText(caught.getMessage());

			}
		});

	}

	private static DateTimeFormat YEAR_MONTH_NUM_DAY_FORMAT = DateTimeFormat.getFormat("d/M/y");
	private static DateTimeFormatRenderer YEAR_MONTH_NUM_DAY_RENDERER = new DateTimeFormatRenderer(
			YEAR_MONTH_NUM_DAY_FORMAT);
	private static Parser<Date> YEAR_MONTH_NUM_DAY_PARSER = new Parser<Date>() {
		public Date parse(CharSequence text) throws java.text.ParseException {
			try {
				return YEAR_MONTH_NUM_DAY_FORMAT.parse(text.toString());
			} catch (Exception e) {
				throw new ParseException(e.getMessage(), 0);
			}

		};
	};

	private static class DateBox extends ValueBox<Date> {

		public DateBox(Renderer<Date> renderer, Parser<Date> parser) {
			super(Document.get().createTextInputElement(), renderer, parser);
		}

	}

	private Panel newExtraDatePanel(ValueBox<Date> dateBox, Button button) {
		Panel datePanel = new HorizontalPanel();
		datePanel.setStyleName(AON.GWT_HORIZONTAL_PANEL);

		dateBox.addStyleName("gwt-TextBox");
		dateBox.addStyleName(AON.AON_TEXT_RIGHT);
		dateBox.setVisibleLength(10);

		button.setStyleName(AON.AON_ICON_CALENDAR);
		button.addStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON);
		button.getElement().getStyle().setMarginRight(0, Unit.PX);
		button.setTabIndex(-2);

		datePanel.add(dateBox);
		datePanel.add(button);
		return datePanel;
	}

	private ValueBox<Date> newDateBox() {
		// return ValueBox.wrap(Document.get().createTextInputElement(),
		// YEAR_MONTH_NUM_DAY_RENDERER, YEAR_MONTH_NUM_DAY_PARSER);
		return new DateBox(YEAR_MONTH_NUM_DAY_RENDERER, YEAR_MONTH_NUM_DAY_PARSER);
	}

	private void loadContentAssistManager() {

		class ProposalsLoader implements AsyncCallback<ContextDescriptor> {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(ContextDescriptor result) {
				contentAssistManager.cleanAll();
				contentAssistManager.addAll(result);
			}

		}

		agreementDraftObject.getContext(0, new ProposalsLoader());
	}

	private Button getViewButton() {
		final Button viewButton = new Button();
		viewButton.setStyleName(AON.AON_ICON_VIEW);
		viewButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);

		class HideVariableCommad implements ScheduledCommand {
			private String var;
			private PopupPanel popup;

			public HideVariableCommad(String var, PopupPanel popup) {
				this.var = var;
				this.popup = popup;
			}

			@Override
			public void execute() {
				agreementDraftObject.hideVariable(var);
				reloadSalaryTable();
				popup.hide();
			}
		}
		class ShowVariableCommad implements ScheduledCommand {
			private String var;
			private PopupPanel popup;

			public ShowVariableCommad(String var, PopupPanel popup) {
				this.var = var;
				this.popup = popup;
			}

			@Override
			public void execute() {
				agreementDraftObject.showVariable(var);
				reloadSalaryTable();
				popup.hide();
			}
		}

		viewButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				final PopupPanel popup = new PopupPanel();
				MenuBar menuBar = new MenuBar(true);

				for (String var : agreementDraftObject.getVariables())
					menuBar.addItem(var, new HideVariableCommad(var, popup)).addStyleName("aon-MenuItemCheckYes");

				for (String var : agreementDraftObject.getHiddenVariables())
					menuBar.addItem(var, new ShowVariableCommad(var, popup));

				popup.add(menuBar);
				popup.setStyleName("gwt-MenuBarPopup");
				popup.setAutoHideEnabled(true);

				final int left = viewButton.getAbsoluteLeft();
				final int top = viewButton.getAbsoluteTop() + viewButton.getOffsetHeight();
				popup.setPopupPositionAndShow(new PositionCallback() {
					@Override
					public void setPosition(int offsetWidth, int offsetHeight) {
						popup.setPopupPosition(left - offsetWidth, top);
					}
				});
			}
		});
		return viewButton;
	}



	private boolean isEditable() {
		return agreementDraftObject.isMine() || !agreementDraftObject.isSystem();
	}
	
	private void setReadOnly ( ListBox listBox, boolean readOnly ){
		if ( !readOnly )
			return;
		
		FlexTable table = (FlexTable)listBox.getParent();
		
		for ( int row = 0; row < table.getRowCount(); row++ ){
			for ( int col = 0; col < table.getCellCount(row); col++ ){
				if ( listBox == table.getWidget(row, col) ){
					TextBox textBox = new TextBox();
					textBox.setText(listBox.getSelectedItemText());
					textBox.setReadOnly(readOnly);
					
					String styleName = listBox.getStyleName();
					String gwtListBox = listBox.getStylePrimaryName();
					styleName = styleName.replace(gwtListBox, "");
					if ( styleName.trim().length() > 0 )
						textBox.addStyleName(styleName);
					
					table.setWidget(row, col, textBox);
					
					return;
				}
			}
		}
		
	}
	
	private Period getCurrentDraftPeriod() {
		Date startDate = agreementDraftObject.getStartDate();
		SortedSet<Date> dates = agreementDraftObject.getDatesWithChanges();
		Iterator<Date> datesIt = dates.iterator();
		while ( datesIt.hasNext() ) {
			Date date = datesIt.next();
			
			if ( date.equals(startDate ))  
				return new Period(date, datesIt.hasNext() ? DateUtils.getPrevDay(datesIt.next()): null );
			
		}
		
		throw new IndexOutOfBoundsException();
	}
	
	
	// ------------------------------------------------------------------------

	private static <T extends Item<?>> boolean isRemove(T item) {
		return StringUtils.equalsIgnoreCase("REMOVE()", item.getExpression());
	}

	private static void addStyle(FlexTable table, int row, String style) {
		CellFormatter fomatter = table.getCellFormatter();
		for (int col = 0; col < table.getCellCount(row); col++)
			fomatter.addStyleName(row, col, style);
	}

	private static void setReadOnly(Element el, boolean readOnly) {
		NodeList<com.google.gwt.dom.client.Element> inputs = el.getElementsByTagName(InputElement.TAG);
		for ( int i = 0; i < inputs.getLength(); i++) {
			((InputElement)inputs.getItem(i)).setReadOnly(readOnly);
		}
		
	}
	
	private static String getExpression4Payment(Payment concept) {
		String expression = concept.getExpression();
		
		Payment.Type type = concept.getType(); 
		if ( type == Payment.Type.CRA_0055 
			|| type ==  Payment.Type.CRA_0056 ) 
			expression = expression.replaceAll("REMOVE", "HIDE");
		
		return expression;
	}
}
