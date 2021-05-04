package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.esferalia.aon.gwt.common.client.Undoable;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.common.shared.HasId;
import com.esferalia.aon.gwt.common.shared.HasStartAndEndDate;
import com.esferalia.aon.gwt.common.shared.NumberUtils;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.client.UndoManager.Listener;
import com.esferalia.aon.gwt.payroll.shared.Agreement.Level;
import com.esferalia.aon.gwt.payroll.shared.AgreementDraft;
import com.esferalia.aon.gwt.payroll.shared.AgreementDraft.SalaryTable;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.Event;
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Result;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;
import com.esferalia.aon.gwt.payroll.shared.StringVariable;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.esferalia.aon.gwt.payroll.shared.VariableDescriptor;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.datepicker.client.CalendarUtil;

public class AgreementDraftObject {

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
	
	private class UndoableSSNumberEdit implements Undoable {

		private String newSSNumber, oldSSNumber;

		public UndoableSSNumberEdit(String oldSSNumber,
				String newSSNumber) {
			this.newSSNumber = newSSNumber;
			this.oldSSNumber = oldSSNumber;

		}

		@Override
		public void redo() {
			agreementDraft.setSSNumber(newSSNumber);
		}

		@Override
		public void undo() {
			agreementDraft.setSSNumber(oldSSNumber);
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

		public UndoableLevelCategoryEdit(Level level, Set<String> oldCategories,
				Set<String> newCategories) {
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
			for (T undo : undos)
				undo.undo();
		}

	}

	private Date draftEndDate;
	private Date draftStartDate;

	private Integer draftDomain;
	private String draftDomainName;

	private int nextDraftLevelId = 0;
	private int nextDraftExtraId = 0;
	private int nextDraftPaymentId = 0;
	private Set<String> shownVariables;
	private AgreementDraft agreementDraft;
	private AgreementDraft oldAgreementDraft;
	private UndoManager<Undoable> undoManager;
	private AgreementServiceAsync agreementsServiceAsync;
	private ArrayList<Date> newDatesChanges;
	private ArrayList<Date> deleteDatesChanges;

	public AgreementDraftObject(
			Integer draftDomain,
			String draftDomainName,
			AgreementDraft agreementDraft,
			AgreementServiceAsync employeesServiceAsync) {
		this.oldAgreementDraft = null;
		this.agreementDraft = agreementDraft;
		this.undoManager = new UndoManager<Undoable>();
		this.shownVariables = new HashSet<String>();
		this.draftDomain = draftDomain;
		this.draftDomainName = draftDomainName;
		this.newDatesChanges = new ArrayList<>();
		this.deleteDatesChanges = new ArrayList<>();
		this.agreementsServiceAsync = employeesServiceAsync;
	}

	public AgreementDraftObject(
			Integer draftDomain,
			String draftDomainName,
			AgreementDraft agreementDraft,
			DomainEmployeesServiceAsync employeesServiceAsync) {
		this(draftDomain, draftDomainName, agreementDraft, employeesServiceAsync.asAgreementServiceAsync());
	}

	public boolean isMine() {
		return NumberUtils.equals(draftDomain, agreementDraft.getDomain());
	}

	public boolean isMine(Payment payment) {
		return NumberUtils.equals(draftDomain, payment.getDomain());
	}

	public boolean isSystem() {
		return NumberUtils.equals(0, agreementDraft.getDomain());
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
	
	public int getNextDraftExtraId() {
		return this.nextDraftExtraId;
	}
	
	public void setNextDraftExtraId(int nextDraftExtraId) {
		this.nextDraftExtraId = nextDraftExtraId;
	}
	
	public int getNextDraftPaymentId() {
		return this.nextDraftPaymentId;
	}
	
	public void setNextDraftPaymentId(int newNextDraftPaymentId) {
		this.nextDraftPaymentId = newNextDraftPaymentId;
	}

	public Integer getDraftDomain() {
		return draftDomain;
	}

	public void setDraftDomain(Integer draftDomain) {
		this.draftDomain = draftDomain;
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
	
	public String getSSNumber() {
		return agreementDraft.getSSNumber();
	}

	public boolean isServiAgreement() {
		return agreementDraft.getIsServiAgreement();
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

	public Set<Event> getEvents() {
		return agreementDraft.getEvents();
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
		Set<String> vars = new HashSet<String>();
		for (String var : agreementDraft.getVariables())
			if (shownVariables.contains(var))
				vars.add(var);
		for (String var : getImplicitVariables())
			if (shownVariables.contains(var))
				vars.add(var);
		return vars;
	}

	public Set<String> getHiddenVariables() {
		Set<String> hidden = new HashSet<String>();
		for (String var : agreementDraft.getVariables())
			if (!shownVariables.contains(var))
				hidden.add(var);

		for (String var : getImplicitVariables())
			if (!shownVariables.contains(var))
				hidden.add(var);

		return hidden;
	}

	public void hideVariable(String variable) {
		shownVariables.remove(variable);
	}

	public void showVariable(String variable) {
		shownVariables.add(variable);
	}

	public Variable getVariable(String var) {
		return agreementDraft.getSalaryTable().get(0, var);
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

	public SortedSet<Date> getDatesWithChanges() {
		SortedSet<Date> newList = agreementDraft.getDatesWithChanges();
		newList.addAll(newDatesChanges);
		Date[] datesList = newList.toArray(new Date[]{});
		
		SortedSet<Date> resultList = new TreeSet<>();
		for(int i=0; i<datesList.length; i++){
			if(this.deleteDatesChanges.contains(datesList[i])){
				continue;
			}
			resultList.add(datesList[i]);
		}
		
		return resultList;
	}
	
	public void addNewDatesWithChanges(Date newDate) {
		this.newDatesChanges.add(newDate);
	}
	
	public void addDeleteDatesChanges(Date date) {
		this.deleteDatesChanges.add(date);
	}
	
	public void clearNewDatesWithChanges() {
		this.newDatesChanges.clear();
	}
	
//	public SortedSet<Date> getDatesWithChanges() {
//		return agreementDraft.getDatesWithChanges();
//	}

	public boolean isDraftLevel(Level level) {
		return agreementDraft.getDraftLevels().contains(level);
	}

	public boolean isDraftExtra(Extra extra) {
		return agreementDraft.getDraftExtras().contains(extra);
	}

	public boolean isDraftPayment(Payment payment) {
		return agreementDraft.getDraftPayments().contains(payment);
	}

	public boolean isDraftVariable(Level level, Variable variable) {
		return agreementDraft.getDraftSalaryTable().contains(level.getId(),
				variable.getName());
	}

	public boolean isDraftCategories(Level level) {
		return agreementDraft.getDraftCategories().containsKey(level.getId());
	}

	public void clearDrafts() {
		agreementDraft.clearDrafts();
		undoManager.discardAll();
	}

	// ------------------------------------------
	// Undo & Redo Support

	public void setDescription(String description) {
		String old = agreementDraft.getDescription();
		agreementDraft.setDescription(description);
		undoManager.add(new UndoableDescriptionEdit(old, description));
	}
	
	public void setSSNumber(String ssNumber) {
		String old = agreementDraft.getSSNumber();
		agreementDraft.setSSNumber(ssNumber);
		undoManager.add(new UndoableSSNumberEdit(old, ssNumber));
	}

	public void addDraftExtra(Extra extra) {
		Extra oldExtra = agreementDraft.addDraftExtra(extra);
		undoManager.add(new UndoableExtraEdit(oldExtra, extra));
	}

	public void addDraftPayment(Payment payment) {
		List<Payment> payments = new LinkedList<Payment>();
		payments.addAll(getTopPayments(payment));
		addDraftPayments(payments);
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
		
		agreementDraft.setDatesWithChanges(getDatesWithChanges());
		
		checkFixLevelCategories();
		agreementsServiceAsync.saveAgreementDraft(draftDomainName, agreementDraft,
				new AsyncCallback<AgreementDraft>() {

					@Override
					public void onSuccess(AgreementDraft savedAgreementDraft) {
						oldAgreementDraft = agreementDraft;
						agreementDraft = savedAgreementDraft;
						undoManager.discardAll();
						agreementDraft.clearDrafts();

						callback.onCalculateSucces(AgreementDraftObject.this);
//						calculate(callback);

					}

					@Override
					public void onFailure(Throwable caught) {
						callback.onCalculateFailure(caught);
					}
				});
	}

	public void calculate(final CalculateCallback callback) {
		
		AgreementServiceAsync agreementServiceAsync = agreementsServiceAsync;

		//Prueba
		setDraftPeriod(getDraftStartDate(), getDraftEndDate(), agreementDraft);
		
		agreementServiceAsync.calculateAgreementDraft(
				draftDomainName,
				agreementDraft,
				new AsyncCallback<AgreementDraft>() {
					@Override
					public void onFailure(Throwable caught) {
						callback.onCalculateFailure(caught);
					}

					@Override
					public void onSuccess(AgreementDraft newAgreementDraft) {
						oldAgreementDraft = agreementDraft;
						agreementDraft = newAgreementDraft;
						getSystemContext(callback, agreementDraft);
						// callback.onCalculateSucces(AgreementDraftObject.this);
					}

				});
	}

	public void preview(int levelId,
			com.esferalia.aon.gwt.payroll.shared.Salary.Type type, int zoom,
			AsyncCallback<String> callback) {
		agreementsServiceAsync.getAgreementDraftReceipt(draftDomainName, 
				agreementDraft,levelId, type, "application/pdf", callback);
	}

	public void getPaymentConcepts(AsyncCallback<List<Payment>> callback) {
		agreementsServiceAsync.getAvailablePayments(draftDomainName, Integer.MIN_VALUE, callback);
	}

	public boolean hasErrors() {
		return agreementDraft.hasExtrasWithoutDates();
	}

	public boolean hasWarnings() {
		return agreementDraft.hasLevelsWithoutCategories();
	}

	// -------------------------------------------------------------------------

	public void getContext(int levelId,
			AsyncCallback<ContextDescriptor> callback) {
		agreementsServiceAsync.getContext(draftDomainName, agreementDraft, levelId, callback);
	}

	public void eval(String expression, int levelId, List<Variable> vars,
			AsyncCallback<List<Result>> callback) {

		agreementsServiceAsync.eval(draftDomainName, expression,
				newAgreementDraft(agreementDraft, vars), levelId, callback);
	}

	// ------------------------------------------------------------------------

	protected AgreementDraft getAgreementDraft() {
		return agreementDraft;
	}

	// ------------------------------------------------------------------------

	Set<Level> getChangedLevels() {
		if (oldAgreementDraft == null
				|| oldAgreementDraft.getLevels().isEmpty())
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
				|| oldAgreementDraft.getVariables().isEmpty())
			return Collections.emptySet();
		Set<String> changed = new HashSet<String>(
				agreementDraft.getVariables());
		changed.removeAll(oldAgreementDraft.getVariables());
		return changed;
	}

	// ------------------------------------------------------------------------

	public void addDraftPayments(Collection<Payment> payments) {
		
		List<UndoableEdit<?>> edits = new LinkedList<UndoableEdit<?>>();
		for (Payment payment : payments) {
			payment.setDomain(draftDomain); // TODO: Here???
			Payment oldPayment = agreementDraft.addDraftPayment(payment);
			edits.add(new UndoablePaymentEdit(oldPayment, payment));
		}
		undoManager.add(new CompositeUndoable(edits));

	}

	public void getSystemContext(final CalculateCallback callback,
			final AgreementDraft agreementDraft) {

		agreementsServiceAsync.getContext(draftDomainName, agreementDraft, -666,
				new AsyncCallback<ContextDescriptor>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						onSuccess(new ContextDescriptor());
					}

					@Override
					public void onSuccess(ContextDescriptor context) {
						syncShowVariables(agreementDraft, context);
						syncSalaryTable(agreementDraft, context);
						callback.onCalculateSucces(AgreementDraftObject.this);
					}

				});
	}

	public void clearDeleteDatesWithChanges() {
		this.deleteDatesChanges.clear();
		
	}
	
	public void cleanDeleteDate(Date date) {
		cleanDeleteDate(date, getEndDate(date));
	}

	public void strechDate(Date date) {
		strechDate(date, getPrevDate(date));
	}
	
	public void strechNewDate(Date newDate) {
		
	}

	private void syncShowVariables(AgreementDraft agreementDraft,
			ContextDescriptor systemContext) {
		Set<String> systemVars = systemContext.getVariables();
		SalaryTable salaryTable = agreementDraft.getSalaryTable();
		for (String var : agreementDraft.getVariables()) {
			if (isHiddenByDefault(var))
				continue;
			if (salaryTable.contains(var) || !systemVars.contains(var))
				shownVariables.add(var);
		}
	}

	private void syncSalaryTable(AgreementDraft agreementDraft,
			ContextDescriptor systemContext) {
		SalaryTable salaryTable = agreementDraft.getSalaryTable();
		for (String name : systemContext.getVariables()) {
			if (!salaryTable.contains(0, name)) {
				VariableDescriptor descriptor = systemContext.get(name);
				StringVariable var = new StringVariable();
				var.setName(name);
				var.setScope(Scope.SYSTEM);
				var.setValue(descriptor.getValue());
				var.setExpression(descriptor.getSyntax());
				var.setStartDate(agreementDraft.getStartDate());
				var.setEndDate(agreementDraft.getEndDate());
				salaryTable.put(0, var);
			}
		}
	}

	private AgreementDraft newAgreementDraft(AgreementDraft src,
			List<Variable> vars) {
		AgreementDraft draft = new AgreementDraft();

		draft.setId(src.getId());
		draft.setDomain(src.getDomain());

		draft.setStartDate(src.getStartDate());
		draft.setEndDate(src.getEndDate());

		SalaryTable draftSalaryTable = src.getDraftSalaryTable();

		for (int level : draftSalaryTable.getAllLevels())
			for (Variable var : draftSalaryTable.getVariables(level))
				draft.addDraftVariable(level, var);

		for (Variable var : vars)
			draft.addDraftVariable(0, var);

		return draft;
	}

	private List<Payment> getTopPayments(Payment payment) {

		List<Payment> twins = new LinkedList<Payment>();
		twins.add(payment);

		if ( payment.getConceptId() == null ) 
			return twins;
		
		for (Payment p : agreementDraft.getPayments()) {
			if (NumberUtils.equals(p.getId(),payment.getId()) )
				continue;
			if (NumberUtils.equals(p.getDomain(),draftDomain) )
				continue;
			if (StringUtils.equals(payment.getName(), p.getName())
				|| NumberUtils.equals(payment.getConceptId(), p.getConceptId())) {
				Payment draftPayment = newDraftPayment();
				draftPayment.setType(p.getType());
				draftPayment.setDomain(draftDomain);
				draftPayment.setMonth(p.getMonth());
				draftPayment.setSalaryType(p.getSalaryType());
				draftPayment.setConceptId(p.getConceptId());
				draftPayment.setDescription(p.getDescription());
				draftPayment.setExpression(p.getExpression());
				draftPayment.setIrpfExpression(p.getIrpfExpression());
				draftPayment.setQuoteExpression(p.getQuoteExpression());
				twins.add(draftPayment);
			}
		}


		return twins;
	}

	// -------------------------------------------------- TODO: Common factor ?

	private Set<String> split(String str) {
		LinkedHashSet<String> categories = new LinkedHashSet<String>();
		for (String category : str.split("\\W*,\\W*")) {
			if (category.length() > 0)
				categories.add(category);
		}
		return categories;
	}
	
	private void cleanDeleteDates() {
		for ( Date date : deleteDatesChanges )
			cleanDeleteDate(date, getEndDate(date));
	}
	
	private void cleanDeleteDate(Date startDate, Date endDate) {
		Date newEndDate = null;
		
		for ( String name: getVariables() ) {
			for ( Level level: getLevels() ) {
				Variable var = getVariable(level, name);
				if ( var == null || !var.getStartDate().equals(startDate) )
					continue;
//				Window.alert("DELETE");
				var.setExpression(""); // DELETE
//				newEndDate = DateUtils.copyDateOnly(var.getEndDate());
				addDraftVariable(level, var);
			}
		}
		
		Date findingEndDate = DateUtils.copyDateOnly(startDate);
		DateUtils.deleteDays2Date(findingEndDate, 1);
		
		modifyPreviusEndDate(findingEndDate, newEndDate);
	}
	private void modifyPreviusEndDate(Date findingEndDate, Date newEndDate) {
//		Window.alert("New end date : " + newEndDate + ", findingEndDate : " + findingEndDate);
		
		for ( String name: getVariables() ) {
			for ( Level level: getLevels() ) {
				Variable var = getVariable(level, name);
				if(var == null || !findingEndDate.equals(var.getEndDate()))
					continue;
				
//				Window.alert("Update End Date");
				var.setEndDate(newEndDate);
				addDraftVariable(level, var);	
			}
		}
	}

	private void strechDate(Date startDate, Date prevDate) {
		for ( String name: getVariables() ) {
			for ( Level level: getLevels() ) {
				Variable var = getVariable(level, name);
				if ( var == null || !var.getStartDate().equals(prevDate) )
					continue;
				//var.setStartDate(startDate);
				Date endDate = DateUtils.copyDateOnly(startDate);
				var.setEndDate(DateUtils.addDays2Date(endDate, -1));
				addDraftVariable(level, var);
			}
		}
	}
	
	private Date getEndDate(Date startDate) {
		Iterator<Date> datesIt = getDatesWithChanges().iterator();
		while  ( datesIt.hasNext() ) {
			Date date = datesIt.next();
			if ( date.equals(startDate ))
				continue;
			else {
				Date endDate = CalendarUtil.copyDate(date);
				CalendarUtil.addDaysToDate(endDate, -1);
				return endDate;
			}
		}
		return null; // Really an Exception ?
	}

	private Date getPrevDate(Date startDate) {
		Iterator<Date> datesIt = getDatesWithChanges().iterator();
		Date prev  = null;
		while  ( datesIt.hasNext() ) {
			Date date = datesIt.next();
			if ( date.equals(startDate ))
				return prev;
			
			prev = date;
			
		}
		return null; // Really an Exception ?
	}
	
	
	
	// -------------------------------------------------------------------------
	private static Variable getVariable(String name, List<Variable> list) {
		for (Variable var : list)
			if (name.equals(var.getName()))
				return var;
		return null;
	}

	private static <V extends HasId<K>, K> Map<K, V> toMap(Set<V> set) {
		Map<K, V> map = new HashMap<K, V>();
		for (V v : set) {
			map.put(v.getId(), v);
		}
		return map;
	}

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

	private static void setDraftPeriod(Date draftStartDate, Date draftEndDate,
			AgreementDraft draft) {
		
		setDateDrafts(draftStartDate, draftEndDate, draft);
	}

	
	
	private static <T extends HasStartAndEndDate> void setStartAndEndDates(
			Date draftStartDate, Date draftEndDate, Collection<T> items) {
		for (T item : items) {
			item.setStartDate(draftStartDate);
			item.setEndDate(draftEndDate);
		}
	}

	private static void setDateDrafts(Date draftStartDate, Date draftEndDate,
			AgreementDraft draft) {
		for (Payment payment : draft.getPayments()) {

			if (draft.isDraftPayment(payment))
				continue;
			if (DateUtils.compare(payment.getStartDate(), draftStartDate) <= 0
					&& DateUtils.compare(payment.getEndDate(),
							draftEndDate) >= 0)
				continue;

			payment.setStartDate(draftStartDate);
			payment.setEndDate(draftEndDate);
			draft.addDraftPayment(payment);

		}

		Collection<SalaryTable.Entry> entries = draft.getSalaryTable()
				.getEntries();
		for (SalaryTable.Entry entry : entries) {

			int level = entry.getLevel();
			Variable variable = entry.getVariable();

			if (draft.isDraftVariable(level, variable.getName()))
				continue;

			if (DateUtils.compare(variable.getStartDate(), draftStartDate) <= 0
					&& DateUtils.compare(variable.getEndDate(),
							draftEndDate) >= 0)
				continue;

			variable.setStartDate(draftStartDate);
			variable.setEndDate(draftEndDate);

			draft.addDraftVariable(level, variable);
		}
	}

	private static boolean isHiddenByDefault(String var) {
		return new HashSet<String>() {
			{
				add("EDAD");
				add("SEXO");
				add("HOMBRE");
				add("MUJER");
				add("MAYOR_65");
				add("TRUE");
				add("FALSE");
				add("TODO");
				
				// BASES
				add("BASE_CGC");
				add("BASE_REGULADORA");

				// DIAS
				add("DIAS_AÑO");
				add("DIAS_MES");
				add("DIAS_NATURALES_MES");
				add("DIAS_VACACIONES");
				add("DIAS_VACACIONES_NO_DISFRUTADOS");
				add("DIAS_TRABAJADOS");
				add("DIAS_SEMANA");
				add("DIAS_CANONTRATO");
				add("DIAS_NOMINA");
				add("DIAS_PAGA");
				add("DIAS_BONIFICACION");
				add("DIAS_COTIZADOS");
				add("DIAS_EFECTIVOS");
				add("DIAS_IT");
				add("DIAS_ESPECIALES");
				add("DIAS_PATERNIDAD");
				add("DIAS_MATERNIDAD");
				add("DIAS_ENFERMEDAD_COMUN");
				add("DIAS_ENFERMEDAD_PROFESIONAL");
				add("NUM_PAGAS");
				add("DIAS_REALES");
				add("DIAS_HUELGA");
				add("DIAS_ERE");
				
				//ANTIGUEDAD
				add("AÑOS_ANTIGUEDAD");
				
				// FINIQUITO ?
				add("DIAS_INDEMNIZACION");
				add("CAUSA_INDEMNIZACION");
				add("AÑOS_TRABAJADOS");

				add("MESES_NOMINA");
				add("MESES_PAGA");
				add("SEMANAS_TRABAJADAS");
				add("SEMANAS_NOMINA");
				add("SEMANAS_PAGA");

				// HORAS
				add("HORAS");
				add("HORAS_SEMANA");
				add("HORAS_NOMINA");
				add("HORAS_LUNES");
				add("HORAS_MARTES");
				add("HORAS_MIERCOLES");
				add("HORAS_JUEVES");
				add("HORAS_VIERNES");
				add("HORAS_SABADO");
				add("HORAS_DOMINGO");
				add("HORAS_CONVENIO");
				add("HORAS_TRABAJADAS");
				add("HORAS_EXTRAS");

				// STUFF
				add("TC2");
				add("CNO");
				add("IPREM");
				add("CATEGORIA");
				add("INDEFINIDO");
				add("OCUPACION");
				add("GARANTIZADO");
				add("IRREGULAR");
				add("TIEMPO_COMPLETO");
				add("PORCENTAJE_IRPF");
				add("GRUPO_COTIZACION");
				add("EXENTO_IPREM");
				add("XIPREM");
				add("TARIFA_IT");
				add("TARIFA_IMS");
				add("CONTRATO_CORTA_DURACION");
				add("AÑOS_ANTIGUEDAD");
				add("COLECT_PECULIAR_COTIZACION");
				add("COD_FIN_CONTRATO");
				add("DESC_FIN_CONTRATO");
				add("COEFICIENTE_PARCIALIDAD");
				add("COEFICIENTE_ERE");
				add("COEFICIENTE_HUELGA");

				add("ASIMILADO_REGIMEN_GRAL");
				add("INGRESO_AC_EMPRESA");
				
				add("MENSUALIDAD");

			}
		}.contains(var) 
		|| var.startsWith("DIAS")
		|| var.startsWith("D_")
		;
	}

	private static Set<String> getImplicitVariables() {
		return new HashSet<String>() {
			{
				// HORAS
				add("HORAS_CONVENIO");
				add("INICIO_ANTIGUEDAD");
			}
		};
	}

	public void clearSalaryDraftTable() {
		// TODO Auto-generated method stub
		agreementDraft.getDraftSalaryTable().clear();
	}
	
	private void checkFixLevelCategories() {
		Set<Level> levels = agreementDraft.getLevels();
		Set<Level> draftLevels = agreementDraft.getDraftLevels();
		levels.removeAll(draftLevels);
		Map<Integer, Set<String>> categories = agreementDraft.getCategoriesMap();
		Map<Integer, Set<String>> draftCategories = agreementDraft.getDraftCategories();
		
		for(Level level : levels) {
			Integer levelId = level.getId();
			Set<String> levelCategories = null != categories.get(levelId) ? categories.get(levelId) : draftCategories.get(levelId);
			if((null == levelCategories || levelCategories.isEmpty()) && AonStringUtils.isNotBlank(level.getDescription()))
				addDraftCategories(level, "Cat " + level.getDescription());
		}
		
		for(Level level : draftLevels) {
			Integer levelId = level.getId();
			Set<String> levelCategories = null != draftCategories.get(levelId) ? draftCategories.get(levelId) : categories.get(levelId);
			if((null == levelCategories || levelCategories.isEmpty()) && AonStringUtils.isNotBlank(level.getDescription()))
				addDraftCategories(level, "Cat " + level.getDescription());
		}
		
	}

}
