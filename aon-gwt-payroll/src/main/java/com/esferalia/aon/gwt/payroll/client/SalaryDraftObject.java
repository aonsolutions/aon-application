package com.esferalia.aon.gwt.payroll.client;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.payroll.client.FxDialog.IContextProvider;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.HasStartAndEndDate;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.StringVariable;
import com.esferalia.aon.gwt.payroll.shared.Salary.Type;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Event;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.datepicker.client.CalendarUtil;

public class SalaryDraftObject implements IContextProvider{

	public static Date NULL_DATE = new Date() {
	};

	interface CalculateCallback {
		void onCalculateSucces(SalaryDraftObject object);

		void onCalculateFailure(Throwable throwable);
	}

	abstract private class UndoableEdit<T> implements Undoable {

		T oldT;
		T newT;

		public UndoableEdit(T oldT, T newT) {
			this.newT = newT;
			this.oldT = oldT;
		}

		@Override
		public void redo() {
			addDraft(newT);
		}

		@Override
		public void undo() {
			if (oldT != null)
				addDraft(oldT);
			else
				removeDraft(newT);
		}

		abstract void addDraft(T t);

		abstract void removeDraft(T t);
	}

	class UndoableVariableEdit extends UndoableEdit<Variable> {

		public UndoableVariableEdit(Variable oldT, Variable newT) {
			super(oldT, newT);
		}

		@Override
		void addDraft(Variable t) {
			salaryDraft.addDraftVariable(t);
		}

		@Override
		void removeDraft(Variable t) {
			salaryDraft.removeDraftVariable(t);
		}
	}

	class UndoablePaymentEdit extends UndoableEdit<Payment> {

		public UndoablePaymentEdit(Payment oldT, Payment newT) {
			super(oldT, newT);
		}

		@Override
		void addDraft(Payment t) {
			salaryDraft.addDraftPayment(t);
		}

		@Override
		void removeDraft(Payment t) {
			salaryDraft.removeDraftPayment(t);
		}

	}

	class UndoableDeductionEdit extends UndoableEdit<Deduction> {

		public UndoableDeductionEdit(Deduction oldT, Deduction newT) {
			super(oldT, newT);
		}

		@Override
		void addDraft(Deduction t) {
			salaryDraft.addDraftDeduction(t);
		}

		@Override
		void removeDraft(Deduction t) {
			salaryDraft.removeDraftDeduction(t);

		}

	}

	private Date draftEndDate;
	private Date draftStartDate;

	private SalaryDraft salaryDraft;
	private UndoManager<UndoableEdit<?>> undoManager;

	private EmployeesServiceAsync employeesServiceAsync;

	public SalaryDraftObject(SalaryDraft salaryDraft,
			EmployeesServiceAsync employeesServiceAsync) {
		this.salaryDraft = salaryDraft;
		this.employeesServiceAsync = employeesServiceAsync;
		this.undoManager = new UndoManager<UndoableEdit<?>>();
	}
	

	@Override
	public void getContext( AsyncCallback<ContextDescriptor> callback ) {
		employeesServiceAsync.getContext(salaryDraft, callback);
	}
	
	@Override
	public void eval(String expression, AsyncCallback<Double> callback) {
		employeesServiceAsync.eval(expression, salaryDraft, callback);
	}
	
	public void save(final CalculateCallback callback) {

		setDraftPeriod(getDraftStartDate(), getDraftEndDate(), salaryDraft);
		removeSalaryPart(salaryDraft);

		employeesServiceAsync.saveSalaryDraft(salaryDraft,
				new AsyncCallback<Void>() {

					@Override
					public void onSuccess(Void result) {


						undoManager.discardAll();
						salaryDraft.clearDrafts();

						employeesServiceAsync.calculateSalaryDraft(salaryDraft,
								new AsyncCallback<SalaryDraft>() {

									@Override
									public void onSuccess(SalaryDraft result) {
										SalaryDraftObject.this.salaryDraft = result;
										callback.onCalculateSucces(SalaryDraftObject.this);
									}

									@Override
									public void onFailure(Throwable caught) {
										callback.onCalculateFailure(caught);
									}
								});

					}

					@Override
					public void onFailure(Throwable caught) {
						callback.onCalculateFailure(caught);
					}
				});

	}

	public void calculate(final CalculateCallback callback) {

		setDraftPeriod(getDraftStartDate(), getDraftEndDate(), salaryDraft);
		removeSalaryPart(salaryDraft);

		employeesServiceAsync.calculateSalaryDraft(salaryDraft,
				new AsyncCallback<SalaryDraft>() {

					@Override
					public void onSuccess(SalaryDraft result) {
						SalaryDraftObject.this.salaryDraft = result;
						callback.onCalculateSucces(SalaryDraftObject.this);
					}

					@Override
					public void onFailure(Throwable caught) {
						callback.onCalculateFailure(caught);
					}
				});
	}

	public void emitSalary(final CalculateCallback callback) {

		setDraftPeriod(getDraftStartDate(), getDraftEndDate(), salaryDraft);
		removeSalaryPart(salaryDraft);

		employeesServiceAsync.saveSalary(salaryDraft,
				new AsyncCallback<SalaryDraft>() {

					@Override
					public void onSuccess(SalaryDraft result) {
						SalaryDraftObject.this.salaryDraft = result;
						callback.onCalculateSucces(SalaryDraftObject.this);
					}

					@Override
					public void onFailure(Throwable caught) {
						callback.onCalculateFailure(caught);
					}
				});
	}

	public void redo() {
		undoManager.redo();
	}

	public void undo() {
		undoManager.undo();
	}

	public void add(UndoableEdit<?> undoable) {
		undoManager.add(undoable);
	}

	public final boolean canUndo() {
		return undoManager.canUndo();
	}

	public final boolean canRedo() {
		return undoManager.canRedo();
	}

	public void addUndoManagerListener(UndoManager.Listener listener) {
		undoManager.addListener(listener);
	}

	public void getAsHTML(int zoom, AsyncCallback<String> callback) {
		employeesServiceAsync.getSalaryDraftReceiptHTML(salaryDraft, zoom,
				callback);
	}

	public void getIrpfAsHTML(int zoom, AsyncCallback<String> callback) {
		employeesServiceAsync.getIrpfDraftReceiptHTML(salaryDraft, zoom,
				callback);
	}

	public void download(String mime, AsyncCallback<String> callback) {
		employeesServiceAsync
				.getSalaryDraftReceipt(salaryDraft, mime, callback);
	}

	public void downloadIrpf(String mime, AsyncCallback<String> callback) {
		employeesServiceAsync
				.getIrpfDraftReceipt(salaryDraft, mime, callback);
	}

	public void getPaymentConcepts(AsyncCallback<List<Payment>> callback) {
		int employeeId = salaryDraft.getEmployee().getId();
		employeesServiceAsync.getAvailablePayments(employeeId, callback);
	}

	public SalaryDraft asSalaryPreview() {
		return salaryDraft;
	}
	

	// -------------------------------------------
	// SalaryDraft Delegated
	// -------------------------------------------
	

	public Type getType() {
		return salaryDraft.getType();
	}

	public Date getStartDate() {
		return salaryDraft.getStartDate();
	}

	public Date getEndDate() {
		return salaryDraft.getEndDate();
	}

	public Employee getEmployee(){
		return salaryDraft.getEmployee();
	}

	public List<Payment> getPayments() {
		return salaryDraft.getPayments();
	}

	public List<Deduction> getDeductions() {
		return salaryDraft.getDeductions();
	}

	public List<Variable> getContext() {
		return salaryDraft.getContext();
	}

	public Date getIssueDate() {
		return salaryDraft.getIssueDate();
	}

	public Date getChargeDate() {
		return salaryDraft.getChargeDate();
	}

	public List<Event> getEvents() {
		return salaryDraft.getEvents();
	}

	public String getEmployeeSS() {
		return salaryDraft.getEmployeeSS();
	}

	public Date getEmployeeSeniorityDate() {
		return salaryDraft.getEmployeeSeniorityDate();
	}

	public Double getIrpfBase() {
		return salaryDraft.getIrpfBase();
	}

	public Double getProrationBase() {
		return salaryDraft.getProrationBase();
	}

	public String getEmployeeDocument() {
		return salaryDraft.getEmployeeDocument();
	}

	public Double getCgcBase() {
		return salaryDraft.getCgcBase();
	}

	public Double getCgpBase() {
		return salaryDraft.getCgpBase();
	}

	public Double gethExtraBase() {
		return salaryDraft.gethExtraBase();
	}

	public Double getNonHExtraBase() {
		return salaryDraft.getNonHExtraBase();
	}

	public Double getRemuneration() {
		return salaryDraft.getRemuneration();
	}

	public Double getTotalLiquid() {
		return salaryDraft.getTotalLiquid();
	}

	public Double getTotalPayment() {
		return salaryDraft.getTotalPayment();
	}

	public Double getTotalDeduction() {
		return salaryDraft.getTotalDeduction();
	}

	public String getEnterpriseName() {
		return salaryDraft.getEnterpriseName();
	}

	public String getEnterpriseAddress() {
		return salaryDraft.getEnterpriseAddress();
	}

	public String getEnterpriseDocument() {
		return salaryDraft.getEnterpriseDocument();
	}

	public String getEnterpriseCCC() {
		return salaryDraft.getEnterpriseCCC();
	}

	public String getEmployeeName() {
		return salaryDraft.getEmployeeName();
	}

	public String getEmployeeQuoteGroup() {
		return salaryDraft.getEmployeeQuoteGroup();
	}

	public String getEmployeeAgreementCategory() {
		return salaryDraft.getEmployeeAgreementCategory();
	}

	public Double getDbCgcBase() {
		return salaryDraft.getDbGgcBase();
	}

	public Double getDbCgpBase() {
		return salaryDraft.getDbGgpBase();
	}

	public Double getDbIrpfBase() {
		return salaryDraft.getDbIrpfBase();
	}

	public Double getDbHExtraBase() {
		return salaryDraft.getDbHExtraBase();
	}

	public Double getDbNonHExtraBase() {
		return salaryDraft.getDbNonHExtraBase();
	}

	public Double getDbProrationBase() {
		return salaryDraft.getDbProrationBase();
	}

	public Double getDbRemuneration() {
		return salaryDraft.getDbRemuneration();
	}

	public Double getDbTotalLiquid() {
		return salaryDraft.getDbTotalLiquid();
	}

	public Double getDbTotalPayment() {
		return salaryDraft.getDbTotalPayment();
	}

	public Double getDbTotalDeduction() {
		return salaryDraft.getDbTotalDeduction();
	}

	public boolean hasDbSalary() {
		return salaryDraft.hasDbSalary();
	}

	public boolean hasDrafts() {
		return salaryDraft.hasDrafts()
				|| !isDraftPeriodSet(getDraftStartDate(), getDraftEndDate(),
						salaryDraft);
	}

	// ------------------------------------------
	//
	//

	public Date getDraftEndDate() {
		return draftEndDate == null ? salaryDraft.getEndDate()
				: (draftEndDate == NULL_DATE ? null : draftEndDate);
	}

	public Date getDraftStartDate() {
		return draftStartDate == null ? salaryDraft.getStartDate()
				: draftStartDate;
	}

	public void setDraftPeriod(Date draftStartDate) {
		setDraftPeriod(draftStartDate, NULL_DATE);
	}

	public void setDraftPeriod(Date draftStartDate, Date draftEndDate) {
		this.draftStartDate = draftStartDate;
		this.draftEndDate = draftEndDate;
	}

	// ------------------------------------------
	// Undo & Redo Support
	//

	public Payment addDraftPayment(Payment payment) {
		Payment oldPayment = salaryDraft.addDraftPayment(payment);
		undoManager.add(new UndoablePaymentEdit(oldPayment, payment));
		return oldPayment;
	}

	public Deduction addDraftDeduction(Deduction deduction) {
		Deduction oldDeduction = salaryDraft.addDraftDeduction(deduction);
		undoManager.add(new UndoableDeductionEdit(oldDeduction, deduction));
		return oldDeduction;
	}

	public Variable addDraftVariable(Variable var) {
		Variable oldVar = salaryDraft.addDraftVariable(var);
		undoManager.add(new UndoableVariableEdit(oldVar, var));
		return oldVar;
	}

	public void renameVariable(Variable oldVar, String newName ) {
		
		String oldName = oldVar.getName(); 

		addDraftVariable( clone(oldVar, newName));
		
		List<Payment> payments = salaryDraft.getDraftPayments();
		for (Payment payment : payments) {
			String expression = payment.getExpression();
			if ( expression == null  ) 
				continue;
			if ( expression.indexOf(oldName) == - 1)
				continue;

			String newExpression = expression.replaceAll(oldName, newName);
			
			addDraftPayment(clonePayment(payment, newExpression));
		}

		payments = salaryDraft.getPayments();
		for (Payment payment : payments) {
			String expression = payment.getExpression();
			if ( expression == null  ) 
				continue;
			if ( expression.indexOf(oldName) == - 1)
				continue;

			String newExpression = expression.replaceAll(oldName, newName);
			
			addDraftPayment(clonePayment(payment, newExpression));
		}
		
	}
	// ------------------------------------------
	//

	private Variable clone(Variable var, String newName){
		StringVariable newVar = new StringVariable();
		newVar.setImplicit(var.isImpicit());
		newVar.setScope(Scope.SALARY); // DRAFT
		newVar.setName(newName);
		newVar.setEndDate(getEndDate());
		newVar.setStartDate(getStartDate());
		newVar.setExpression(var.getExpression());
		return newVar;
	}
	
	private Payment clonePayment(Payment oldPayment, String newExpression){
		Payment newPayment = new Payment();
		
		newPayment.setId(oldPayment.getId());
		newPayment.setName(oldPayment.getName());
		newPayment.setType(oldPayment.getType());
		newPayment.setConceptId(oldPayment.getId());
		newPayment.setDescription(oldPayment.getDescription());
		newPayment.setExpression(newExpression);
		newPayment.setIrpfExpression(oldPayment.getIrpfExpression());
		newPayment.setQuoteExpression(oldPayment.getQuoteExpression());

		newPayment.setScope(Scope.SALARY);
		newPayment.setEndDate(getEndDate());
		newPayment.setStartDate(getStartDate());
		newPayment.setSalaryType(getType());

		
		return newPayment;
	}
	// ------------------------------------------
	//

	private static <T> boolean sameDate(Date d1, Date d2) {
		if (d1 == d2)
			return true;
		if (d1 == null)
			return false;
		if (d2 == null)
			return false;
		return CalendarUtil.isSameDate(d1, d2);
	}


	private static void removeSalaryPart(SalaryDraft salaryDraft) {
		salaryDraft.clearDb();
		salaryDraft.clearEvents();
		salaryDraft.clearContext();
		salaryDraft.clearPayments();
		salaryDraft.clearDeductions();
	}

	private static boolean isDraftPeriodSet(Date draftStartDate,
			Date draftEndDate, SalaryDraft draft) {
		if (!isStartAndEndDatesSet(draftStartDate, draftEndDate,
				draft.getDraftContext()))
			return false;
		if (!isStartAndEndDatesSet(draftStartDate, draftEndDate,
				draft.getDraftPayments()))
			return false;
		if (!isStartAndEndDatesSet(draftStartDate, draftEndDate,
				draft.getDraftDeductions()))
			return false;
		return true;
	}

	private static void setDraftPeriod(Date draftStartDate, Date draftEndDate,
			SalaryDraft draft) {
		setStartAndEndDates(draftStartDate, draftEndDate,
				draft.getDraftContext());
		setStartAndEndDates(draftStartDate, draftEndDate,
				draft.getDraftPayments());
		setStartAndEndDates(draftStartDate, draftEndDate,
				draft.getDraftDeductions());
	}

	private static <T extends HasStartAndEndDate> void setStartAndEndDates(
			Date draftStartDate, Date draftEndDate, Collection<T> items) {
		for (T item : items) {
			item.setStartDate(draftStartDate);
			item.setEndDate(draftEndDate);
		}
	}

	private static <T extends HasStartAndEndDate> boolean isStartAndEndDatesSet(
			Date draftStartDate, Date draftEndDate, Collection<T> items) {
		for (T item : items) {
			if (!sameDate(draftStartDate, item.getStartDate()))
				return false;
			if (!sameDate(draftEndDate, item.getEndDate()))
				return false;
		}
		return true;
	}
}
