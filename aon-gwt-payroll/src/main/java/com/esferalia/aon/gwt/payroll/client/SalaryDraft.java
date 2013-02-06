package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.payroll.client.SalaryDraftObject.CalculateCallback;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Deduction;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Payment;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Variable;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LongBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;

public class SalaryDraft extends ResizeComposite implements CalculateCallback,
		SalarySelect.Listener {

	private static final String AON_DATA_TABLE_ROW_EVEN = "aon-dataTable-row-even";
	private static final String AON_DATA_TABLE_ROW_ODD = "aon-dataTable-row-odd";
	private static final String AON_BOLD = "aon-bold";
	private static final String AON_TEXT_RIGHT = "aon-text-right";
	private static final String AON_TEXT_CENTER = "aon-text-center";

	private static final DateTimeFormat DATE_FORMAT = DateTimeFormat
			.getFormat(PredefinedFormat.YEAR_MONTH_NUM_DAY);

	private static final NumberFormat CURRENCY_FORMAT = NumberFormat
			.getFormat("#,##0.00");

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
	
	class VariableChangeHandler<T> implements ValueChangeHandler<T>{
		
		
		private Variable variable;
		
		public VariableChangeHandler(Variable variable) {
			this.variable = variable;
		}
		
		@Override
		public void onValueChange(ValueChangeEvent<T> event) {
			com.esferalia.aon.gwt.payroll.shared.SalaryDraft salaryDraft = 
					salaryDraftObject.getSalaryDraft();
			salaryDraft.addContextVariable(variable.getName(), 
					event.getValue(), 
					salaryDraft.getStartDate(), 
					salaryDraft.getEndDate());
			
			salaryDraftObject.calculate(SalaryDraft.this);
		}
	}

	interface MyStyle extends CssResource {
		@ClassName("cell-label")
		String cellLabel();
	}

	interface Binder extends UiBinder<Widget, SalaryDraft> {

	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	SalarySelect salarySelect;
	@UiField
	FlexTable contextTable;
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
	MyStyle style;

	private SalaryDraftObject salaryDraftObject;

	public SalaryDraft() {
		initWidget(binder.createAndBindUi(this));
		initContextTable();
		initPaymentsTable();
		salarySelect.addListener(this);
	}

	public void setSalaryDraftObject(SalaryDraftObject salaryDraftObject) {
		this.salaryDraftObject = salaryDraftObject;
		onChangedSalaryDraftObject();
	}

	@Override
	public void onChange(SalarySelect salarySelect) {
		salaryDraftObject.calculate(this);
	}

	@Override
	public void onCalculateSucces(SalaryDraftObject object) {
		com.esferalia.aon.gwt.payroll.shared.SalaryDraft salaryDraft = salaryDraftObject
				.getSalaryDraft();
		salarySelect.setSalaryPreview(salaryDraft);
		dumpSalaryDraft(salaryDraft);
	}

	@Override
	public void onCalculateFailure(Throwable throwable) {
		// TODO Auto-generated method stub

	}

	private void onChangedSalaryDraftObject() {
		salaryDraftObject.calculate(this);
	}

	private void dumpSalaryDraft(
			com.esferalia.aon.gwt.payroll.shared.SalaryDraft salaryDraft) {

		enterpriseNameLabel.setText(salaryDraft.getEnterpriseName());
		enterpriseCCCLabel.setText(salaryDraft.getEnterpriseCCC());
		enterpriseAddressLabel.setText(salaryDraft.getEnterpriseAddress());

		employeeSSLabel.setText(salaryDraft.getEmployeeSS());
		employeeNameLabel.setText(salaryDraft.getEmployeeName());
		employeeDocumentLabel.setText(salaryDraft.getEmployeeDocument());
		employeeSeniorityLabel.setText(format(salaryDraft
				.getEmployeeSeniorityDate()));
		employeeAgreementCategoryLabel.setText(salaryDraft
				.getEmployeeAgreementCategory());

		Date startDate = salaryDraft.getStartDate();
		Date endDate = salaryDraft.getEndDate();

		periodLabel.setText(format(startDate) + " - " + format(endDate));
		daysLabel.setText(Integer.toString(CalendarUtil.getDaysBetween(
				startDate, endDate)));
		totalPaymentsLabel.setText(format(salaryDraft.getTotalPayment()));

		cgcBaseLabel.setText(format(salaryDraft.getCgcBase()));
		cgpBaseLabel.setText(format(salaryDraft.getCgpBase()));
		irpfBaseLabel.setText(format(salaryDraft.getIrpfBase()));
		hExtraBaseLabel.setText(format(salaryDraft.gethExtraBase()));
		nonHExtraBaseLabel.setText(format(salaryDraft.getNonHExtraBase()));
		prorationBaseLabel.setText(format(salaryDraft.getProrationBase()));

		remunerationLabel.setText(format(salaryDraft.getRemuneration()));

		totalPaymentLabel.setText(format(salaryDraft.getTotalPayment()));
		totalDeductionLabel.setText(format(salaryDraft.getTotalDeduction()));
		totalLiquidLabel.setText(format(salaryDraft.getTotalLiquid()));

		clearPaymentsTable();
		clearContextTable();

		List<Variable> context = salaryDraft.getContext();
		dumpContext(context);

		List<Payment> payments = salaryDraft.getPayments();
		dumpPayments(payments);
		insertNewPaymentRow();
		insertBlankPaymentRow();
		List<Deduction> deductions = salaryDraft.getDeductions();
		dumpDeductions(deductions);
		insertNewPaymentRow();
		insertBlankPaymentRow();
		insertBlankPaymentRow();

	}

	private void initPaymentsTable() {

		paymentsTable.setText(0, 0, "CUANTIA");
		paymentsTable.setText(0, 1, "CONCEPTO");
		paymentsTable.setText(0, 2, "DEVENGOS");
		paymentsTable.setText(0, 3, "DEDUCCIONES");

		paymentsTable.getRowFormatter().addStyleName(0, AON_DATA_TABLE_ROW_ODD);
		for (int i = 0; i < paymentsTable.getCellCount(0); i++) {
			paymentsTable.getCellFormatter().addStyleName(0, i, AON_BOLD);
			paymentsTable.getCellFormatter()
					.addStyleName(0, i, AON_TEXT_CENTER);
		}

		paymentsTable.getColumnFormatter().setWidth(0, "16%");
		paymentsTable.getColumnFormatter().setWidth(2, "16%");
		paymentsTable.getColumnFormatter().setWidth(3, "16%");

	}

	private void initContextTable() {
	}

	private void clearContextTable() {
		contextTable.removeAllRows();
	}

	private void clearPaymentsTable() {
		for (int i = paymentsTable.getRowCount() - 1; i > 0; i--)
			paymentsTable.removeRow(i);
	}

	private void dumpPayments(List<Payment> payments) {

		int row = paymentsTable.getRowCount();
		for (Payment payment : payments) {
			paymentsTable.setText(row, 1, payment.getDescription());
			paymentsTable.setText(row, 2, format(payment.getAmount()));
			paymentsTable.setHTML(row, 3, "&nbsp;");
			paymentsTable.getCellFormatter().addStyleName(row, 2,
					AON_TEXT_RIGHT);
			paymentsTable.getRowFormatter().addStyleName(
					row,
					row % 2 == 0 ? AON_DATA_TABLE_ROW_ODD
							: AON_DATA_TABLE_ROW_EVEN);
			row++;
		}

	}

	private void insertNewPaymentRow() {
		int row = paymentsTable.getRowCount();

		Button newButton = new Button();
		newButton.setStyleName("aon-icon-reset"); // clear gwt-Button
		newButton.setStyleName("aon-editDataTable-button", true);

		paymentsTable.setWidget(row, 0, newButton);
		paymentsTable.setHTML(row, 1, "&nbsp;");
		paymentsTable.setHTML(row, 2, "&nbsp;");
		paymentsTable.setHTML(row, 3, "&nbsp;");

	}

	private void insertBlankPaymentRow() {
		int row = paymentsTable.getRowCount();
		paymentsTable.setHTML(row, 0, "&nbsp;");
		paymentsTable.setHTML(row, 1, "&nbsp;");
		paymentsTable.setHTML(row, 2, "&nbsp;");
		paymentsTable.setHTML(row, 3, "&nbsp;");
	}

	private void dumpDeductions(List<Deduction> deductions) {

		int row = paymentsTable.getRowCount();
		for (Deduction deduction : deductions) {
			String description = DEDUCTION_DESCRIPTIONS
					.get(deduction.getType());
			if (description != null) {
				paymentsTable.setText(row, 0, deduction.getDescription());
				paymentsTable.getCellFormatter().addStyleName(row, 0,
						AON_TEXT_CENTER);
				paymentsTable.setHTML(row, 1, description);
			} else {
				paymentsTable.setText(row, 1, deduction.getDescription());
			}
			paymentsTable.setText(row, 3, format(deduction.getAmount()));
			paymentsTable.getCellFormatter().addStyleName(row, 3,
					AON_TEXT_RIGHT);
			paymentsTable.getRowFormatter().addStyleName(
					row,
					row % 2 == 0 ? AON_DATA_TABLE_ROW_ODD
							: AON_DATA_TABLE_ROW_EVEN);
			row++;
		}

	}

	private void dumpContext(List<Variable> context) {
		int cols = 3;

		int count = contextTable.getRowCount();

		for (Variable variable : context) {
			String name = variable.getName();

			HTMLPanel htmlPanel = new HTMLPanel("");
			Label nameLabel = new Label(name);
			nameLabel.setStyleName(style.cellLabel());
			htmlPanel.add(nameLabel);

			Panel valuePanel = new HorizontalPanel();
			valuePanel.setStyleName("gwt-HorizontalPanel");
			HasValueChangeHandlers<?> valueWidget = newWidget(variable);
			valueWidget.addValueChangeHandler( new VariableChangeHandler(variable) );
			valuePanel.add((Widget) valueWidget);
			Button deleteButton = new Button();
			deleteButton.setStyleName("aon-icon-delete");
			deleteButton.setStyleName("aon-editDataTable-button", true);
			valuePanel.add(deleteButton);

			htmlPanel.add(valuePanel);

			int row = count / cols;
			int col = count % cols;
			contextTable.setWidget(row, col, htmlPanel);

			contextTable.getRowFormatter().addStyleName(
					row,
					row % 2 == 0 ? AON_DATA_TABLE_ROW_ODD
							: AON_DATA_TABLE_ROW_EVEN);

			contextTable.getColumnFormatter().setWidth(col, (100 / cols) + "%");

			count++;
		}

	}

	private String format(Double amount) {
		return CURRENCY_FORMAT.format(amount);
	}

	private String format(Date date) {
		return DATE_FORMAT.format(date);
	}

	private String format(Object object) {
		if (object instanceof Double)
			return format((Double) object);
		if (object instanceof Date)
			return format((Date) object);
		return object.toString();
	}

	private HasValueChangeHandlers<?> newWidget(Variable variable) {
		Object value = variable.getValue();

		if (value instanceof Long) {
			LongBox longBox = new LongBox();
			longBox.setValue((Long) value);
			longBox.setStyleName("gwt-TextBox");
			return longBox;
		}
		if (value instanceof Double) {
			DoubleBox doubleBox = new DoubleBox();
			doubleBox.setValue((Double) value);
			doubleBox.setStyleName("gwt-TextBox");
			return doubleBox;
		}
		if (value instanceof Integer) {
			IntegerBox integerBox = new IntegerBox();
			integerBox.setValue((Integer) value);
			integerBox.setStyleName("gwt-TextBox");
			return integerBox;
		} else {
			TextBox textBox = new TextBox();
			textBox.setValue(value.toString());
			return textBox;
		}
	}
}
