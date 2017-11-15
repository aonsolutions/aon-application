package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.Undoable;
import com.esferalia.aon.gwt.common.shared.HasStartAndEndDate;
import com.esferalia.aon.gwt.common.shared.NumberUtils;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.client.FxDialog.IContextProvider;
import com.esferalia.aon.gwt.payroll.shared.Bonus;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Event;
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.ITDataPerson;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Period;
import com.esferalia.aon.gwt.payroll.shared.Result;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.Salary.Type;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;
import com.esferalia.aon.gwt.payroll.shared.StringVariable;
import com.esferalia.aon.gwt.payroll.shared.UndefinedPaymentVariable;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.datepicker.client.CalendarUtil;

public class SalaryDraftObject implements IContextProvider {

	public static Date NULL_DATE = new Date() {
	};
	
	
	enum Calculate {
		STANDARD {
			@Override
			void accept(CalculateVisitor visitor) {
				visitor.visitStandard();
			}
		},
		DUMMIES {
			@Override
			void accept(CalculateVisitor visitor) {
				visitor.visit4Dummies();
			}
		};
		
		abstract void accept(CalculateVisitor visitor);
	}
	
	interface CalculateVisitor {
		void visitStandard();
		void visit4Dummies();
	}

	interface CalculateCallback {
		
		Calculate getCalculate();
		
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

	abstract private class UndoableRemove<T> implements Undoable {

		T oldT;

		public UndoableRemove(T oldT) {
			this.oldT = oldT;
		}

		@Override
		public void redo() {
			removeDraft(oldT);
		}

		@Override
		public void undo() {
			addDraft(oldT);
		}

		abstract void addDraft(T t);

		abstract void removeDraft(T t);
	}

	private class CompositeUndoable<T extends Undoable > implements Undoable {

		private Collection<T> undos;

		public CompositeUndoable(Collection<T> undos) {
			this.undos = undos;
		}

		@Override
		public void redo() {
			for (T undo : undos)
				undo.redo();
		}

		@Override
		public void undo() {
			for (T undo : undos){
				undo.undo();
			}
		}

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

	class UndoableVariableRemove extends UndoableRemove<Variable> {

		public UndoableVariableRemove(Variable oldT) {
			super(oldT);
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

	class UndoableEmbargoEdit extends UndoableEdit<Deduction> {

		public UndoableEmbargoEdit(Deduction oldT, Deduction newT) {
			super(oldT, newT);
		}

		@Override
		void addDraft(Deduction t) {
			salaryDraft.addDraftEmbargo(t);
		}

		@Override
		void removeDraft(Deduction t) {
			salaryDraft.removeDraftEmbargo(t);

		}

	}

	class UndoableBonusEdit extends UndoableEdit<Bonus> {

		public UndoableBonusEdit(Bonus oldT, Bonus newT) {
			super(oldT, newT);
		}

		@Override
		void addDraft(Bonus t) {
			salaryDraft.addDraftBonus(t);
		}

		@Override
		void removeDraft(Bonus t) {
			salaryDraft.removeDraftBonus(t);

		}
	}

	private Date draftEndDate;
	private Date draftStartDate;
	
	private Set<Date> draftSections;

	private ITDataObject dataObject;
	private SalaryDraft salaryDraft;
	private UndoManager<Undoable> undoManager;
	private EmployeeCalendarDraftObjectData employeeCalendarDraftObjectData;
	private EmployeeEventsDraftObject employeeEventsDraftObject;
	private EmployeesServiceAsync employeesServiceAsync;

	public SalaryDraftObject(SalaryDraft salaryDraft, ITDataObject dataObject,
			EmployeesServiceAsync employeesServiceAsync) {
		this.dataObject = dataObject;
		this.salaryDraft = salaryDraft;
		this.draftSections = new HashSet<Date>();
		this.employeesServiceAsync = employeesServiceAsync;
		this.undoManager = new UndoManager<Undoable>();
	}

	// ------------------------------------------------------------------------
	// IContextProvider methods
	// ------------------------------------------------------------------------

	public boolean isEditable(String name) {
		for (Payment payment : getPayments())
			if (StringUtils.equals(name, payment.getName()))
				return false;
		return true;
	};

	@Override
	public void getContext(AsyncCallback<ContextDescriptor> callback) {
		employeesServiceAsync.getContext(salaryDraft, callback);
	}

	@Override
	public void eval(String expression, List<Variable> vars,
			AsyncCallback<List<Result>> callback) {
		employeesServiceAsync.eval(expression,
				newSalaryDraft(salaryDraft, vars), callback);
	}

	public void getExtras(AsyncCallback<List<Extra>> callback)
			throws IllegalArgumentException {
		employeesServiceAsync.getExtras(Collections.singletonList(getEmployee()), callback);
	}

	public void save(final CalculateCallback callback) {
		removeCalendarDraft();
		setDraftPeriod(getDraftStartDate(), getDraftEndDate(), salaryDraft);
		addCalendarVariablesDraft();
		addEventsVariablesDraft();
		removeSalaryPart(salaryDraft);
		employeeCalendarDraftObjectData.clearDraftHours();
		
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
								callback.onCalculateSucces(
										SalaryDraftObject.this);
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

	public void getVariables(String[] names, Date startDate, Date endDate,
			AsyncCallback<List<Variable>> callback) {
		employeesServiceAsync.getVariables(salaryDraft, startDate, endDate,
				names, callback);
	}

	public void calculate(final CalculateCallback callback) {

		setDraftType(salaryDraft);
		removeCalendarDraft();
		removeEventsDraft();
		setDraftPeriod(getDraftStartDate(), getDraftEndDate(), salaryDraft);
		addCalendarVariablesDraft();
		addEventsVariablesDraft();
		employeeCalendarDraftObjectData.getSalaryDraftChanged(salaryDraft);
		
		removeSalaryPart(salaryDraft);

		salaryDraft.setDraftLeaveIts(getDrafLeaveIts());
		
		final AsyncCallback<SalaryDraft> asyncCallback = new AsyncCallback<SalaryDraft>() {

			@Override
			public void onSuccess(SalaryDraft result) {
				SalaryDraftObject.this.salaryDraft = result;
				callback.onCalculateSucces(SalaryDraftObject.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				callback.onCalculateFailure(caught);
			}
		};
		
		callback.getCalculate().accept( new CalculateVisitor() {
			
			@Override
			public void visitStandard() {
				Date sections [] = getSections();
				if ( sections != null && sections.length > 0 )
					employeesServiceAsync.calculateSalaryDraft(salaryDraft,
							sections,asyncCallback);
				else 
					employeesServiceAsync.calculateSalaryDraft(salaryDraft,
							asyncCallback);
			}
			
			@Override
			public void visit4Dummies() {
				employeesServiceAsync.calculateSalaryDraft4Dummies(salaryDraft,
						asyncCallback);
			}

		});
	}

	
	public void addCalendarVariablesDraft(){
		addCalendarDraft(employeeCalendarDraftObjectData.getVariablesList(getDraftStartDate(), getDraftEndDate()));
		addCalendarDraft(employeeCalendarDraftObjectData.getVariablesListCE(getDraftStartDate(), getDraftEndDate()));
		addCalendarDraft(employeeCalendarDraftObjectData.getVariablesListStrike(getDraftStartDate(), getDraftEndDate()));
		addCalendarDraft(employeeCalendarDraftObjectData.getVariablesListHolidays(getDraftStartDate(), getDraftEndDate()));
		addCalendarDraft(employeeCalendarDraftObjectData.getVariablesListExtraHours(getDraftStartDate(), getDraftEndDate()));
		
	}
	
	public void addEventsVariablesDraft(){
		addEventsDraft(employeeEventsDraftObject.getVariablesList(getDraftStartDate(), getDraftEndDate()));
	}
	

	public void emitSalary(final CalculateCallback callback) {

		setDraftPeriod(getDraftStartDate(), getDraftEndDate(), salaryDraft);
		removeSalaryPart(salaryDraft);
		
		AsyncCallback<SalaryDraft> saveCallback = new AsyncCallback<SalaryDraft>() {

			@Override
			public void onSuccess(SalaryDraft result) {
				SalaryDraftObject.this.salaryDraft = result;
				callback.onCalculateSucces(SalaryDraftObject.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				callback.onCalculateFailure(caught);
			}
		};
		
		Date sections [] = getSections();
		if ( sections == null || sections.length == 0 )
			employeesServiceAsync.saveSalary(salaryDraft, saveCallback );
		else 
			employeesServiceAsync.saveSalary(salaryDraft, sections, saveCallback );
	}

	public void saveITData(final CalculateCallback callback) {

		// TODO: Save only data relative to this employee.

		dataObject.save(new ITDataObject.CallculateCallback() {

			@Override
			public void onCalculateSuccess(ITDataObject object) {
				dataObject = object;
				callback.onCalculateSucces(SalaryDraftObject.this);
			}

			@Override
			public void onCalculateFailure(Throwable throwable) {
				callback.onCalculateFailure(throwable);
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
		employeesServiceAsync.getSalaryDraftReceipt(salaryDraft, mime,
				callback);
	}

	public void downloadIrpf(String mime, AsyncCallback<String> callback) {
		employeesServiceAsync.getIrpfDraftReceipt(salaryDraft, mime, callback);
	}

	public void getPaymentConcepts(AsyncCallback<List<Payment>> callback) {
		int employeeId = salaryDraft.getEmployee().getId();
		employeesServiceAsync.getAvailablePayments(employeeId, callback);
	}

	public void getDeductionConcepts(AsyncCallback<List<Deduction>> callback) {
		int employeeId = salaryDraft.getEmployee().getId();
		employeesServiceAsync.getAvailableDeductions(employeeId, callback);
	}

	public void getBonusConcepts(AsyncCallback<List<Bonus>> callback) {
		int employeeId = salaryDraft.getEmployee().getId();
		employeesServiceAsync.getAvailableBonuses(employeeId, callback);
	}

	public SalaryDraft asSalaryPreview() {
		return salaryDraft;
	}
	
	public void addDraftSection(Date section) {
		draftSections.add(section);
	}

	public void removeDraftSection(Date section) {
		draftSections.remove(section);
	}
	
	public boolean hasDraftSection(Date section) {
		return draftSections.contains(section);
	}

	// -------------------------------------------
	// SalaryDraft Delegated
	// -------------------------------------------

	public void clearDrafts() {
		salaryDraft.clearDrafts();
		undoManager.discardAll();
	}

	public Type getType() {
		return salaryDraft.getType();
	}

	public Date getStartDate() {
		return salaryDraft.getStartDate();
	}

	public Date getEndDate() {
		return salaryDraft.getEndDate();
	}

	public Employee getEmployee() {
		return salaryDraft.getEmployee();
	}

	public List<Deduction> getCosts() {
		return salaryDraft.getCosts();
	}

	public List<Bonus> getBonuses() {
		return salaryDraft.getBonuses();
	}

	public List<Payment> getPayments() {
		return salaryDraft.getPayments();
	}

	public List<Deduction> getDeductions() {
		return salaryDraft.getDeductions();
	}

	public List<Deduction> getEmbargos() {
		return salaryDraft.getEmbargos();
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

	public Double getRawCgcBase() {
		return salaryDraft.getRawCgcBase();
	}

	public Double getRawCgpBase() {
		Double rawCgcBase = salaryDraft.getRawCgcBase();
		if (rawCgcBase == null)
			return null;
		Double hExtraBase = salaryDraft.gethExtraBase();
		if (hExtraBase == null)
			return null;
		Double nonHExtraBase = salaryDraft.getNonHExtraBase();
		if (nonHExtraBase == null)
			return null;
		return rawCgcBase + hExtraBase + nonHExtraBase;
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

	public List<Variable> getDbContext() {
		return salaryDraft.getDbContext();
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
		return salaryDraft.hasDrafts() || !isDraftPeriodSet(getDraftStartDate(),
				getDraftEndDate(), salaryDraft);
	}

	public List<Variable> getDrafContext() {
		return salaryDraft.getDraftContext();
	}
	
	public Integer getTimeUnits() {
		return salaryDraft.getTimeUnits();
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

	public String getCommunity() {
		return salaryDraft.getCommunity();
	}

	public void setCommunity(String community) {
		salaryDraft.setCommunity(community);
	}

	public void addDraftPayment(Payment payment) {
		List<Payment> payments = new LinkedList<Payment>();
		payments.addAll(getTopPayments(payment));
		addDraftPayments(payments);
	}

	public Deduction addDraftDeduction(Deduction deduction) {
		Deduction oldDeduction = salaryDraft.addDraftDeduction(deduction);

		undoManager.add(new UndoableDeductionEdit(oldDeduction, deduction));
		return oldDeduction;
	}

	public Deduction addDraftEmbargo(Deduction embargo) {
		Deduction oldDeduction = salaryDraft.addDraftEmbargo(embargo);

		undoManager.add(new UndoableEmbargoEdit(oldDeduction, embargo));
		return oldDeduction;
	}

	public Variable addDraftVariable(Variable var) {
		Variable oldVar = salaryDraft.addDraftVariable(var);
		undoManager.add(new UndoableVariableEdit(oldVar, var));
		return oldVar;
	}

	public Bonus addDraftBonus(Bonus bonus) {
		Bonus oldBonus = salaryDraft.addDraftBonus(bonus);

		undoManager.add(new UndoableBonusEdit(oldBonus, bonus));
		return oldBonus;
	}

	public void renameVariable(Variable oldVar, String newName) {

		String oldName = oldVar.getName();

		addDraftVariable(clone(oldVar, newName));

		List<Payment> payments = salaryDraft.getDraftPayments();
		for (Payment payment : payments) {
			String expression = payment.getExpression();
			if (expression == null)
				continue;
			if (expression.indexOf(oldName) == -1)
				continue;

			String newExpression = expression.replaceAll(oldName, newName);

			addDraftPayment(clonePayment(payment, newExpression));
		}

		payments = salaryDraft.getPayments();
		for (Payment payment : payments) {
			String expression = payment.getExpression();
			if (expression == null)
				continue;
			if (expression.indexOf(oldName) == -1)
				continue;

			String newExpression = expression.replaceAll(oldName, newName);

			addDraftPayment(clonePayment(payment, newExpression));
		}

	}

	public void removeDraftVariables(Collection<Variable> vars){
		List<UndoableRemove<Variable>> removes = new ArrayList<UndoableRemove<Variable>>(vars.size());
		for ( Variable var : vars )
			if ( salaryDraft.removeDraftVariable(var) )
				removes.add(new UndoableVariableRemove(var));
		
		CompositeUndoable<UndoableRemove<Variable>> undoableCompositeRemove = 
				new CompositeUndoable<UndoableRemove<Variable>>(removes);
		
		undoManager.add(undoableCompositeRemove);
	}

	public void recoverDraftPayment(Payment payment) {
		List<Payment> payments = new LinkedList<Payment>();
		payments.addAll(getBottomPayments(payment));
		for( Payment p : payments ){
			p.setExpression(payment.getExpression());
		}
		addDraftPayments(payments);
	}

	// ------------------------------------------
	
	private Date [] getSections() {
		return
		draftSections.stream()
		.filter(s ->  s.compareTo(salaryDraft.getStartDate()) >= 0 )
		.filter(s ->  s.compareTo(salaryDraft.getEndDate()) <= 0 )
		.sorted()
		.toArray(Date[]::new )
		;
	}

	private Variable clone(Variable var, String newName) {
		StringVariable newVar = new StringVariable();
		newVar.setImplicit(var.isImpicit());
		newVar.setScope(Scope.SALARY); // DRAFT
		newVar.setName(newName);
		newVar.setEndDate(getEndDate());
		newVar.setStartDate(getStartDate());
		newVar.setExpression(var.getExpression());
		return newVar;
	}

	private Payment clonePayment(Payment oldPayment, String newExpression) {
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

	private SalaryDraft newSalaryDraft(SalaryDraft src, List<Variable> vars) {
		SalaryDraft draft = new SalaryDraft();

		draft.setId(src.getId());
		draft.setType(src.getType());

		draft.setStartDate(src.getStartDate());
		draft.setEndDate(src.getEndDate());
		draft.setIssueDate(src.getIssueDate());
		draft.setChargeDate(src.getChargeDate());

		draft.setEmployee(src.getEmployee());

		for (Variable variable : src.getDraftContext())
			draft.addDraftVariable(variable);

		for (Variable variable : vars) {
			variable.setScope(Scope.SALARY);
			variable.setStartDate(src.getStartDate());
			variable.setEndDate(src.getEndDate());
			draft.addDraftVariable(variable);
		}

		List<Variable> ctx = src.getContext();

		for (Payment payment : src.getPayments()) {
			String name = payment.getName();
			if (name == null)
				continue;

			Variable variable = getVariable(name, ctx);
			if (variable != null) {
				draft.addDraftVariable(variable);
			}

		}

		return draft;
	}

	private List<Payment> getTopPayments(Payment payment) {
		

		List<Payment> twins = new LinkedList<Payment>();
		twins.add(payment);


		if ( payment.getConceptId() == null ) 
			return twins;
		
		for (Payment p : salaryDraft.getPayments()) {
			if (p.getScope().compareTo(Scope.AGREEMENT) > 0)
				continue;
			if (StringUtils.equals(payment.getName(), p.getName())){
				twins.add(p);
				continue;
			}
			if (NumberUtils.equals(payment.getConceptId(), p.getConceptId())){
				twins.add(p);
			}
		}

		for (Variable var : salaryDraft.getContext()) {
			if (!(var instanceof UndefinedPaymentVariable))
				continue;
			if (var.getScope().compareTo(Scope.AGREEMENT) > 0)
				continue;
			Payment p = ((UndefinedPaymentVariable) var).getPayment();
			if (StringUtils.equals(payment.getName(), p.getName())){
				twins.add(p);
				continue;
			}
			if (NumberUtils.equals(payment.getConceptId(), p.getConceptId())){
				twins.add(p);
			}
		}

		return twins;
	}


	private List<Payment> getBottomPayments(Payment payment) {
		

		List<Payment> twins = new LinkedList<Payment>();
		twins.add(payment);


		if ( payment.getConceptId() == null ) 
			return twins;
		
		for (Payment p : salaryDraft.getPayments()) {
			if ( p.equals(payment) )
				continue;
			if (p.getScope().compareTo(Scope.AGREEMENT) <= 0)
				continue;
			if (StringUtils.equals(payment.getName(), p.getName())){
				twins.add(p);
				continue;
			}
			if (NumberUtils.equals(payment.getConceptId(), p.getConceptId())){
				twins.add(p);
			}
		}

		for (Variable var : salaryDraft.getContext()) {
			if (!(var instanceof UndefinedPaymentVariable))
				continue;
			if (var.getScope().compareTo(Scope.AGREEMENT) <= 0)
				continue;
			Payment p = ((UndefinedPaymentVariable) var).getPayment();
			if ( p.equals(payment) )
				continue;
			if (StringUtils.equals(payment.getName(), p.getName())){
				twins.add(p);
				continue;
			}
			if (NumberUtils.equals(payment.getConceptId(), p.getConceptId())){
				twins.add(p);
			}
		}

		return twins;
	}


	private void addDraftPayments(Collection<Payment> payments) {
		List<UndoablePaymentEdit> edits = new LinkedList<UndoablePaymentEdit>();
		for (Payment payment : payments) {
			Payment oldPayment = salaryDraft.addDraftPayment(payment);
			edits.add(new UndoablePaymentEdit(oldPayment, payment));
		}
		undoManager.add(new CompositeUndoable<UndoablePaymentEdit>(edits));
	}

	private List<ITDataPerson> getDrafLeaveIts() {
		return dataObject.getDraftList(salaryDraft.getEmployee().getId());
	}

	// ------------------------------------------------------------------------
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
		salaryDraft.clear();
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
		if (!isStartAndEndDatesSet(draftStartDate, draftEndDate,
				draft.getDraftEmbargos()))
			return false;
		return true;
	}

	private static void setDraftType(SalaryDraft draft) {
		for ( Payment p : draft.getDraftPayments() )
			if ( p.getSalaryType() == null)
				p.setSalaryType(draft.getType());
		for ( Deduction d : draft.getDraftDeductions() )
			setDeductionType(d, draft.getType());
		for ( Deduction d : draft.getDraftEmbargos() )
			setDeductionType(d, draft.getType());
	}
	
	private void removeCalendarDraft() {
		List<Variable> draftContext = salaryDraft.getDraftContext();
		for ( int i = draftContext.size()-1; i >= 0; i--) {
			Variable var = draftContext.get(i);
			if (employeeCalendarDraftObjectData.isMine(var)){
				draftContext.remove(i);
			}
		}
	}
	
	private void removeEventsDraft() {
		List<Variable> draftContext = salaryDraft.getDraftContext();
		for ( int i = draftContext.size()-1; i >= 0; i--) {
			Variable var = draftContext.get(i);
			if (employeeEventsDraftObject.isMine(var)){
				draftContext.remove(i);
			}
		}
	}
	
	private static void setDraftPeriod(Date draftStartDate, Date draftEndDate,
			SalaryDraft draft) {
		setStartAndEndDates(draftStartDate, draftEndDate,
				draft.getDraftContext());
		setStartAndEndDates(draftStartDate, draftEndDate,
				draft.getDraftPayments());
		setStartAndEndDates(draftStartDate, draftEndDate,
				draft.getDraftDeductions());
		setStartAndEndDates(draftStartDate, draftEndDate,
				draft.getDraftEmbargos());
		setStartAndEndDates(draftStartDate, draftEndDate,
				draft.getDraftBonuses());
	}
	
	private void addCalendarDraft(ArrayList<StringVariable> variablesList) {
		for (StringVariable stringVariable : variablesList){
			salaryDraft.addDraftVariable(stringVariable);
		}
	}
	
	private void addEventsDraft(ArrayList<StringVariable> variablesList) {
		for (StringVariable stringVariable : variablesList){
			salaryDraft.addDraftVariable(stringVariable);
		}
	}

	private static void setDeductionType(Deduction d,
			Salary.Type type) {
		String expression = d.getExpression();
		
		String pattern ="\\s*\\(\\s*.*\\s*\\)\\s*\\?\\s*\\(?\\s*(.*)\\s*\\)\\s*:\\s*REMOVE\\s*\\(\\s*\\)\\s*";
		RegExp regexp = RegExp.compile(pattern, "im");
		MatchResult matchResult = regexp.exec(expression);
		if ( matchResult != null ) {
			expression = matchResult.getGroup(1);
		}else {
			expression = "/*user*/"+ expression + "/**/";
		}
		
		d.setExpression("("+type.getVariable()+")?("+ expression +"):REMOVE()");
		
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

	private static Variable getVariable(String name, List<Variable> list) {
		for (Variable var : list)
			if (name.equals(var.getName()))
				return var;
		return null;
	}
	public void setEmployeeCalendarDraftObjectData(EmployeeCalendarDraftObjectData employeeCalendarDraftObjectData) {
		this.employeeCalendarDraftObjectData = employeeCalendarDraftObjectData;
	}
	
	public EmployeeCalendarDraftObjectData getEmployeeCalendarDraftObjectData() {
		return employeeCalendarDraftObjectData;
	}
	
	public void setEmployeeEventsDraftObject(EmployeeEventsDraftObject employeeEventsDraftObject) {
		this.employeeEventsDraftObject = employeeEventsDraftObject;
	}
	
	public EmployeeEventsDraftObject getEmployeeEventsDraftObjecta() {
		return employeeEventsDraftObject;
	}

}
