package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.payroll.client.UndoManager.Undoable;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Salary.Type;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Event;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SalaryDraftObject {

	interface CalculateCallback {
		void onCalculateSucces(SalaryDraftObject object);

		void onCalculateFailure(Throwable throwable);
	}

	abstract private class UndoableEdit<T> implements Undoable {

		private T oldT;
		private T newT;

		public UndoableEdit(T oldT, T newT) {
			this.oldT = oldT;
			this.newT = newT;
		}

		@Override
		public void redo() {
			addDraft(newT);
		}

		@Override
		public void undo() {
			if (oldT != null) {
				addDraft(oldT);
			} else {
				removeDraft(newT);
			}
		}
		
		abstract void addDraft(T t);
		abstract void removeDraft(T t);
	}
	
	class UndoableVariableEdit extends UndoableEdit<Variable>{
		
		
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

	private SalaryDraft salaryDraft;
	private UndoManager undoManager = new UndoManager();
	private EmployeesServiceAsync employeesServiceAsync;

	public SalaryDraftObject(SalaryDraft salaryDraft,
			EmployeesServiceAsync employeesServiceAsync) {
		this.salaryDraft = salaryDraft;
		this.employeesServiceAsync = employeesServiceAsync;
	}

	public void calculate(final CalculateCallback callback) {
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

	public void redo() {
		undoManager.redo();
	}

	public void undo() {
		undoManager.undo();
	}

	public void add(Undoable undoable) {
		undoManager.add(undoable);
	}

	public final boolean canUndo() {
		return undoManager.canUndo();
	}

	public final boolean canRedo() {
		return undoManager.canRedo();
	}
	
	public void addUndoManagerListener(UndoManager.Listener  listener){
		undoManager.addListener(listener);
	}

	public void getAsHTML(int zoom, AsyncCallback<String> callback) {
		employeesServiceAsync.getSalaryDraftReceiptHTML(salaryDraft, zoom,
				callback);
	}

	public void download(String mime, AsyncCallback<String> callback) {
		employeesServiceAsync
				.getSalaryDraftReceipt(salaryDraft, mime, callback);
	}

	public void getPaymentConcepts(AsyncCallback<List<Payment>> callback) {
		employeesServiceAsync.getPaymentConcepts(callback);
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
		return deduction;
	}

	public Variable addDraftVariable(Variable var) {
		Variable oldVar = salaryDraft.addDraftVariable(var);
		undoManager.add(new UndoableVariableEdit(oldVar, var));
		return oldVar;
	}

}
