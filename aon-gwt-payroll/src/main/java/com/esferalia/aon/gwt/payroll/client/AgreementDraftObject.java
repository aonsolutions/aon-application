package com.esferalia.aon.gwt.payroll.client;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.esferalia.aon.gwt.payroll.client.FxDialog.IContextProvider;
import com.esferalia.aon.gwt.payroll.client.UndoManager.Listener;
import com.esferalia.aon.gwt.payroll.shared.AgreementDraft;
import com.esferalia.aon.gwt.payroll.shared.AgreementDraft.Level;
import com.esferalia.aon.gwt.payroll.shared.AgreementDraft.SalaryTable;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.EvalException;
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.HasId;
import com.esferalia.aon.gwt.payroll.shared.HasStartAndEndDate;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Salary.Type;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.datepicker.client.CalendarUtil;

public class AgreementDraftObject implements IContextProvider {

	public static Date NULL_DATE = new Date();

	static interface CalculateCallback {
		void onCalculateFailure(Throwable throwable);

		void onCalculateSucces(AgreementDraftObject object);

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

	private class UndoableExtraEdit extends UndoableEdit<Extra> {

		public UndoableExtraEdit(Extra oldT, Extra newT) {
			super(oldT, newT);
		}

		@Override
		void addDraft(Extra t) {
			agreementDraft.addDraftExtra(t);
		}

		@Override
		void removeDraft(Extra t) {
			agreementDraft.removeDraftExtra(t);
		}

	}

	private class UndoableLevelEdit extends UndoableEdit<Level> {

		public UndoableLevelEdit(Level oldT, Level newT) {
			super(oldT, newT);
		}

		@Override
		void addDraft(Level t) {
			agreementDraft.addDraftLevel(t);
		}

		@Override
		void removeDraft(Level t) {
			agreementDraft.removeDraftLevel(t);
		}

	}

	private class UndoablePaymentEdit extends UndoableEdit<Payment> {

		public UndoablePaymentEdit(Payment oldT, Payment newT) {
			super(oldT, newT);
		}

		@Override
		void addDraft(Payment t) {
			agreementDraft.addDraftPayment(t);
		}

		@Override
		void removeDraft(Payment t) {
			agreementDraft.removeDraftPaymet(t);
		}

	}

	private class UndoableDescriptionEdit implements Undoable {

		private String newDescription, oldDescription;

		public UndoableDescriptionEdit(String oldDescription,
				String newDescription) {
			this.newDescription = newDescription;
			this.oldDescription = oldDescription;

		}

		@Override
		public void redo() {
			agreementDraft.setDescription(newDescription);
		}

		@Override
		public void undo() {
			agreementDraft.setDescription(oldDescription);
		}

	}

	private class UndoableLevelVariableEdit extends UndoableEdit<Variable> {

		private Level level;

		public UndoableLevelVariableEdit(Level level, Variable oldVariable,
				Variable newVariable) {
			super(oldVariable, newVariable);
			this.level = level;
		}

		@Override
		void addDraft(Variable var) {
			agreementDraft.addDraftVariable(level, var);
		}

		@Override
		void removeDraft(Variable var) {
			agreementDraft.addDraftVariable(level, var);
		}
	}

	private class UndoableLevelCategoryEdit extends UndoableEdit<Set<String>> {

		private Level level;

		public UndoableLevelCategoryEdit(Level level,
				Set<String> oldCategories, Set<String> newCategories) {
			super(oldCategories, newCategories);
			this.level = level;
		}

		@Override
		void addDraft(Set<String> categories) {
			agreementDraft.addDraftCategories(level, categories);
		}

		@Override
		void removeDraft(Set<String> categories) {
			agreementDraft.addDraftCategories(level, categories);
		}

	}

	private Date draftEndDate;
	private Date draftStartDate;

	private int nextDraftLevelId = 0;
	private int nextDraftExtraId = 0;
	private int nextDraftPaymentId = 0;
	private AgreementDraft agreementDraft;
	private AgreementDraft oldAgreementDraft;
	private UndoManager<Undoable> undoManager;
	private EmployeesServiceAsync employeesServiceAsync;

	public AgreementDraftObject(AgreementDraft agreementDraft,
			EmployeesServiceAsync employeesServiceAsync) {
		this.oldAgreementDraft = null;
		this.agreementDraft = agreementDraft;
		this.undoManager = new UndoManager<Undoable>();
		this.employeesServiceAsync = employeesServiceAsync;
	}

	public Level newLevel() {
		Level level = new Level();
		level.setId(--nextDraftLevelId);
		return level;

	}

	public Extra newDraftExtra() {
		Extra extra = new Extra();
		extra.setId(--nextDraftExtraId);
		return extra;

	}

	public Payment newDraftPayment() {
		Payment payment = new Payment();
		payment.setId(--nextDraftPaymentId);
		return payment;

	}

	public Date getDraftEndDate() {
		if (draftEndDate == null)
			return agreementDraft.getEndDate();
		if (draftEndDate == NULL_DATE)
			return null;
		if (DateUtils.isLastDayOfYear(draftEndDate))
			return DateUtils.getLastDayOfYear(agreementDraft.getEndDate());
		return draftEndDate;

	}

	public Date getDraftStartDate() {
		if (draftStartDate == null)
			return agreementDraft.getStartDate();
		if (DateUtils.isFirstDayOfYear(draftStartDate))
			return DateUtils.getFirstDayOfYear(agreementDraft.getStartDate());
		return draftStartDate;
	}

	public void setDraftPeriod(Date draftStartDate) {
		setDraftPeriod(draftStartDate, NULL_DATE);
	}

	public void setDraftPeriod(Date draftStartDate, Date draftEndDate) {
		this.draftStartDate = draftStartDate;
		this.draftEndDate = draftEndDate;
	}

	// ------------------------------------------
	// AgreeementDraft delegates
	// ------------------------------------------

	public Integer getId() {
		return agreementDraft.getId();
	}

	public void setId(int id) {
		agreementDraft.setId(id);
	}

	public String getDescription() {
		return agreementDraft.getDescription();
	}

	public Date getStartDate() {
		return agreementDraft.getStartDate();
	}

	public void setStartDate(Date startDate) {
		agreementDraft.setStartDate(startDate);
	}

	public Date getEndDate() {
		return agreementDraft.getEndDate();
	}

	public void setEndDate(Date endDate) {
		agreementDraft.setEndDate(endDate);
	}

	public Set<Extra> getExtras() {
		return agreementDraft.getExtras();
	}

	public Set<Payment> getPayments() {
		return agreementDraft.getPayments();
	}

	public Set<Level> getLevels() {
		return agreementDraft.getLevels();
	}

	public Set<String> getVariables() {
		return agreementDraft.getVariables();
	}

	public Variable getVariable(Level level, String var) {
		return agreementDraft.getSalaryTable().get(level.getId(), var);
	}

	public Set<String> getCategories(Level level) {
		return agreementDraft.getCategoriesMap().get(level.getId());
	}

	public boolean hasDrafts() {
		return agreementDraft.hasDrafts();
		// || !isDraftPeriodSet(getDraftStartDate(),
		// getDraftEndDate(),agreementDraft);
	}

	public Set<Date> getDatesWithChanges() {
		return agreementDraft.getDatesWithChanges();
	}
	
	public boolean isDraftLevel(Level level){
		return agreementDraft.getDraftLevels().contains(level);
	}
	
	public boolean isDraftExtra(Extra extra){
		return agreementDraft.getDraftExtras().contains(extra);
	}

	public boolean isDraftPayment(Payment payment){
		return agreementDraft.getDraftPayments().contains(payment);
	}

	public boolean isDraftVariable(Level level, Variable variable){
		return agreementDraft.getDraftSalaryTable().contains(level.getId(), variable.getName());
	}

	public boolean isDraftCategories(Level level){
		return agreementDraft.getDraftCategories().containsKey(level.getId());
	}

	// ------------------------------------------
	// Undo & Redo Support

	public void setDescription(String description) {
		String old = agreementDraft.getDescription();
		agreementDraft.setDescription(description);
		undoManager.add(new UndoableDescriptionEdit(old, description));
	}

	public void addDraftExtra(Extra extra) {
		Extra oldExtra = agreementDraft.addDraftExtra(extra);
		undoManager.add(new UndoableExtraEdit(oldExtra, extra));
	}

	public void addDraftPayment(Payment payment) {
		Payment oldPayment = agreementDraft.addDraftPayment(payment);
		undoManager.add(new UndoablePaymentEdit(oldPayment, payment));
	}

	public void addDraftLevel(Level level) {
		Level oldLevel = agreementDraft.addDraftLevel(level);
		undoManager.add(new UndoableLevelEdit(oldLevel, level));
	}

	public void addDraftVariable(Level level, Variable var) {
		Variable old = agreementDraft.addDraftVariable(level, var);
		undoManager.add(new UndoableLevelVariableEdit(level, old, var));
	}

	public void addDraftCategories(Level level, String str) {
		Set<String> set = split(str);
		Set<String> old = agreementDraft.addDraftCategories(level, set);
		undoManager.add(new UndoableLevelCategoryEdit(level, old, set));
	}

	// ------------------------------------------
	// UndoManager delegates
	// ------------------------------------------

	public void redo() {
		undoManager.redo();
	}

	public void undo() {
		undoManager.undo();
	}

	public void discardAll() {
		undoManager.discardAll();
	}

	public final boolean canUndo() {
		return undoManager.canUndo();
	}

	public final boolean canRedo() {
		return undoManager.canRedo();
	}

	public void addListener(Listener listener) {
		undoManager.addListener(listener);
	}

	public void removeListener(Listener listener) {
		undoManager.removeListener(listener);
	}

	// ------------------------------------------
	//
	// ------------------------------------------

	public void save(final CalculateCallback callback) {

		setDraftPeriod(getDraftStartDate(), getDraftEndDate(), agreementDraft);
		// TODO: Clean Database data.

		employeesServiceAsync.saveAgreementDraft(agreementDraft,
				new AsyncCallback<AgreementDraft>() {

					@Override
					public void onSuccess(AgreementDraft savedAgreementDraft) {
						oldAgreementDraft = agreementDraft;
						agreementDraft = savedAgreementDraft;
						undoManager.discardAll();
						agreementDraft.clearDrafts();

						calculate(callback);

					}

					@Override
					public void onFailure(Throwable caught) {
						callback.onCalculateFailure(caught);
					}
				});

	}

	public void calculate(final CalculateCallback callback) {

		employeesServiceAsync.calculateAgreementDraft(agreementDraft,
				new AsyncCallback<AgreementDraft>() {
					@Override
					public void onFailure(Throwable caught) {
						callback.onCalculateFailure(caught);
					}

					@Override
					public void onSuccess(AgreementDraft newAgreementDraft) {
						oldAgreementDraft = agreementDraft;
						agreementDraft = newAgreementDraft;
						callback.onCalculateSucces(AgreementDraftObject.this);
					}

				});
	}

	public void getPaymentConcepts(AsyncCallback<List<Payment>> callback) {
		employeesServiceAsync.getAvailablePayments(Integer.MIN_VALUE, callback);
	}

	public boolean hasErrors() {
		return agreementDraft.hasExtrasWithoutDates();
	}

	public boolean hasWarnings() {
		return agreementDraft.hasLevelsWithoutCategories();
	}

	// ------------------------------------------------------------------------
	//
	// ------------------------------------------------------------------------
	private SalaryDraft newFakeSalaryDraft(){
		SalaryDraft draft = new SalaryDraft();
		
		draft.setStartDate(DateUtils.getFirstDayOfMonth());
		draft.setEndDate(DateUtils.getLastDayOfMonth());
		draft.setIssueDate(draft.getEndDate());
		draft.setChargeDate(draft.getEndDate());
		Employee employee = new Employee();
		employee.setId(-1);
		draft.setEmployee(employee);
		draft.setType(Type.SALARY);
		
		return draft;
	}

	@Override
	public void getContext(AsyncCallback<ContextDescriptor> callback) {
		
		employeesServiceAsync.getContext(newFakeSalaryDraft(), callback);
	}

	@Override
	public void eval(String expression, AsyncCallback<Double> callback) {
		employeesServiceAsync.eval(expression, newFakeSalaryDraft(), callback);
	}
	

	// ------------------------------------------
	// Differences
	// ------------------------------------------

	Set<Level> getChangedLevels() {
		if (oldAgreementDraft == null || oldAgreementDraft.getLevels() == null)
			return Collections.emptySet();

		Set<Level> changed = new HashSet<Level>();
		Map<Integer, Level> oldLevels = toMap(oldAgreementDraft.getLevels());
		for (Level newLevel : agreementDraft.getLevels()) {
			Level oldLevel = oldLevels.get(newLevel.getId());
			if (oldLevel == null
					|| !StringUtils.equals(oldLevel.getDescription(),
							newLevel.getDescription())) {
				changed.add(newLevel);
			}
		}
		return changed;
	}

	Set<String> getChangedVariables() {
		if (oldAgreementDraft == null
				|| oldAgreementDraft.getVariables() == null)
			return Collections.emptySet();
		Set<String> changed = new HashSet<String>(agreementDraft.getVariables());
		changed.removeAll(oldAgreementDraft.getVariables());
		return changed;
	}

	private static <V extends HasId<K>, K> Map<K, V> toMap(Set<V> set) {
		Map<K, V> map = new HashMap<K, V>();
		for (V v : set) {
			map.put(v.getId(), v);
		}
		return map;
	}

	// -------------------------------------------------- TODO: Common factor ?

	private static <T> boolean sameDate(Date d1, Date d2) {
		if (d1 == d2)
			return true;
		if (d1 == null)
			return false;
		if (d2 == null)
			return false;
		return CalendarUtil.isSameDate(d1, d2);
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

	private Set<String> split(String str) {
		LinkedHashSet<String> categories = new LinkedHashSet<String>();
		for (String category : str.split("\\W*,\\W*")) {
			if (category.length() > 0)
				categories.add(category);
		}
		return categories;
	}

	private static void setDraftPeriod(Date draftStartDate, Date draftEndDate,
			AgreementDraft draft) {

		setStartAndEndDates(draftStartDate, draftEndDate,
				draft.getDraftPayments());
		setStartAndEndDates(draftStartDate, draftEndDate, draft
				.getDraftSalaryTable().getAllVariables());
		
		setDateDrafts(draftStartDate, draftEndDate, draft);
	}

	private static <T extends HasStartAndEndDate> void setStartAndEndDates(
			Date draftStartDate, Date draftEndDate, Collection<T> items) {
		for (T item : items) {
			item.setStartDate(draftStartDate);
			item.setEndDate(draftEndDate);
		}
	}

	private static void setDateDrafts(
			Date draftStartDate, Date draftEndDate, AgreementDraft draft) {
		for (Payment payment : draft.getPayments()) {
			
			if ( draft.isDraftPayment(payment))
				continue;
			if (DateUtils.compare(payment.getStartDate(), draftStartDate) <= 0
					&& DateUtils.compare(payment.getEndDate(), draftEndDate) >= 0)
				continue;
			
			payment.setStartDate(draftStartDate);
			payment.setEndDate(draftEndDate);
			draft.addDraftPayment(payment);

		}

		Collection<SalaryTable.Entry> entries = draft.getSalaryTable().getEntries();
		for (SalaryTable.Entry entry: entries) {
			
			int level = entry.getLevel();
			Variable variable = entry.getVariable();
			
			if ( draft.isDraftVariable(level, variable.getName()))
				continue;
			
			if (DateUtils.compare(variable.getStartDate(), draftStartDate) <= 0
					&& DateUtils.compare(variable.getEndDate(), draftEndDate) >= 0)
				continue;
				
			variable.setStartDate(draftStartDate);
			variable.setEndDate(draftEndDate);
			
			draft.addDraftVariable(level, variable);
		}
	}

}
