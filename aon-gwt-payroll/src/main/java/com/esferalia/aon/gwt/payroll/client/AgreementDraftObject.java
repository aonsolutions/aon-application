package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
import com.esferalia.aon.gwt.payroll.shared.AgreementDraft;
import com.esferalia.aon.gwt.payroll.shared.AgreementDraft.Level;
import com.esferalia.aon.gwt.payroll.shared.AgreementDraft.SalaryTable;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.CretaService;
import com.esferalia.aon.gwt.payroll.shared.Event;
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.HttpException;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.PaymentConcept;
import com.esferalia.aon.gwt.payroll.shared.Result;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;
import com.esferalia.aon.gwt.payroll.shared.StringVariable;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.esferalia.aon.gwt.payroll.shared.VariableDescriptor;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;

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
		this.agreementsServiceAsync = employeesServiceAsync;
		this.shownVariables = new HashSet<String>();
		this.draftDomain = draftDomain;
		this.draftDomainName = draftDomainName;
		this.newDatesChanges = new ArrayList<>();
		this.deleteDatesChanges = new ArrayList<>();
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
		
		agreementsServiceAsync.saveAgreementDraft(draftDomainName, agreementDraft,
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
		
		AgreementServiceAsync agreementServiceAsync = agreementsServiceAsync;

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
		agreementsServiceAsync.getAgreementDraftReceiptHTML(draftDomainName, 
				agreementDraft,levelId, type, zoom, callback);
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
					}

					@Override
					public void onSuccess(ContextDescriptor context) {
						syncShowVariables(agreementDraft, context);
						syncSalaryTable(agreementDraft, context);
						callback.onCalculateSucces(AgreementDraftObject.this);
					}

				});
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

		setStartAndEndDates(draftStartDate, draftEndDate,
				draft.getDraftPayments());
		setStartAndEndDates(draftStartDate, draftEndDate,
				draft.getDraftSalaryTable().getAllVariables());

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


				// DIAS
				add("DIAS_Aﾃ前");
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
				add("Aﾃ前S_ANTIGUEDAD");
				
				
				// FINIQUITO ?
				add("DIAS_INDEMNIZACION");
				add("CAUSA_INDEMNIZACION");
				add("Aﾃ前S_TRABAJADOS");

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
				add("Aﾃ前S_ANTIGUEDAD");
				add("COLECT_PECULIAR_COTIZACION");
				add("COD_FIN_CONTRATO");
				add("DESC_FIN_CONTRATO");
				add("COEFICIENTE_PARCIALIDAD");
				add("COEFICIENTE_ERE");
				add("COEFICIENTE_HUELGA");

				add("ASIMILADO_REGIMEN_GRAL");
				add("INGRESO_AC_EMPRESA");

			}
		}.contains(var) 
		|| var.startsWith("DIAS")
		;
	}

	private static Set<String> getImplicitVariables() {
		return new HashSet<String>() {
			{
				// HORAS
				add("HORAS_CONVENIO");
				add("A\u00D1OS_ANTIGUEDAD");
			}
		};
	}

	public void clearDeleteDatesWithChanges() {
		this.deleteDatesChanges.clear();
		
	}
	
	public String generateJSONUpdate(){
		//ｿPOR QUﾉ SOLO ME TRAE LOS PAYMENTS DEL TRAMO EN EL QUE ESTOY? EL CALCULATE CAMBIA EL DRAFT ACORDE AL STARTDATE Y ENDDATE
		JSONObject json = new JSONObject();
		
		json.put("id", createJSONValue(getId()));
		json.put("domain", createJSONValue(getDraftDomain()));
		json.put("calendar", createJSONValue(null));
		json.put("description", createJSONValue(getDescription()));
		
		
		// ----------------------------------------------------- payments --------------------------------------------------------
		JSONArray paymentsArray = new JSONArray();
		int indexPayments = 0;
		
		//ARRAY CON LOS IDS DE LOS PAYMENTS QUE PERTENECEN A EXTRA Y DEBERIAN SALTARSE EN LOS PAYMENTS NORMALES
		//ACTUALMENTE NO SE UTILIZA POR QUE EL METODO getPayments() DEVUELVE TODOS MENOS LOS RELACIONADOS CON EXTRAS
		ArrayList<Integer> paymentsExtra = new ArrayList<>();
		Set<Extra> extrasToExclude = getExtras();
		for(Extra extra : extrasToExclude){
			paymentsExtra.add(extra.getPaymentId());
		}

		Set<Payment> payments = getPayments();
		for(Payment payment : payments){
			Payment paymentFinally = getPaymentFinally(payment);
			
			if(paymentsExtra.contains(paymentFinally.getId()))
				continue;
			
			if(paymentFinally != null){
				JSONObject paymentObj = new JSONObject();
				paymentObj.put("id", createJSONValue(paymentFinally.getId()));
				paymentObj.put("code", createJSONValue(paymentFinally.getName()));
//				if(paymentFinally.getExpression()!= null)
				paymentObj.put("expression", createJSONValue(paymentFinally.getExpression()));
				paymentObj.put("description", createJSONValue(paymentFinally.getDescription()));
				paymentObj.put("type", createJSONValue(paymentFinally.getType().getDescription()));
				paymentObj.put("startDate", createJSONValue(paymentFinally.getStartDate()));
				paymentObj.put("descriptionDecorable", createJSONValue(0));
				paymentObj.put("irpfExpression", createJSONValue(paymentFinally.getIrpfExpression()));
//				if(paymentFinally.getQuoteExpression() != null)
				paymentObj.put("quoteExpression", createJSONValue(paymentFinally.getQuoteExpression()));
			
				PaymentConcept paymentPaymentConcept = paymentFinally.getConcept();
				
				if(paymentPaymentConcept != null){
					JSONObject paymentPaymentConceptObj = new JSONObject();
					paymentPaymentConceptObj.put("id", createJSONValue(paymentFinally.getConceptId()));
					paymentPaymentConceptObj.put("domain", createJSONValue(paymentPaymentConcept.getDomain()));
					paymentPaymentConceptObj.put("code", createJSONValue(paymentPaymentConcept.getCode()));
					paymentPaymentConceptObj.put("description", createJSONValue(paymentPaymentConcept.getDescription()));
					paymentPaymentConceptObj.put("type", createJSONValue(paymentPaymentConcept.getType()));
					paymentPaymentConceptObj.put("descriptionDecorable", createJSONValue(paymentPaymentConcept.getDescription_decorable()));
//					if(paymentPaymentConcept.getExpression() != null)
					paymentPaymentConceptObj.put("expression", createJSONValue(paymentPaymentConcept.getExpression()));
					paymentPaymentConceptObj.put("irpfExpression", createJSONValue(paymentPaymentConcept.getIrpf_expression()));
//					if(paymentPaymentConcept.getQuote_expression() != null)
					paymentPaymentConceptObj.put("quoteExpression", createJSONValue(paymentPaymentConcept.getQuote_expression()));
					
					paymentObj.put("__payConcept", paymentPaymentConceptObj);
				}
				
				paymentsArray.set(indexPayments, paymentObj);
				indexPayments++;
			}	
		}
		
		json.put("payments", paymentsArray);
		
		
		// ----------------------------------------------------- extras ----------------------------------------------------------
		JSONArray extrasArray = new JSONArray();
		int indexExtras = 0;
		
		Set<Extra> extras = getExtras();
		for(Extra extra : extras){
//			Window.alert("StartDate :"+extra.getStartDate()+", EndDate :"+extra.getEndDate()+
//						 ", IssueDate :"+extra.getIssueDate()+", paymentId :"+extra.getPaymentId());
			
			JSONObject extraObj = new JSONObject();
			extraObj.put("start_date", createJSONValue(extra.getStartDate()));
			extraObj.put("end_date", createJSONValue(extra.getEndDate()));
			extraObj.put("issue_date", createJSONValue(extra.getIssueDate()));
			
			Integer extraPaymentId = extra.getPaymentId();			
			Payment payment = extra.getPayment();
			
//			Window.alert("Payment :"+payment);
//			if(payment != null){
//				Window.alert("Payment Extra -> id :"+extraPaymentId+", expression :"+payment.getExpression()+", description :"+
//						 payment.getDescription()+", type :"+payment.getType().getDescription()+", startDate :"+
//						 payment.getStartDate()+", irpfExpression :"+payment.getIrpfExpression()+", quoteExpression :"
//						 +payment.getQuoteExpression());
//			}
			
			if(payment != null){ //payment.getId() != 0
				JSONObject extraAgreementPayment = new JSONObject();
				extraAgreementPayment.put("id", createJSONValue(extraPaymentId));
				extraAgreementPayment.put("expression", createJSONValue(payment.getExpression()));
				extraAgreementPayment.put("description", createJSONValue(payment.getDescription()));
				//FALTA TYPE
				//extraAgreementPayment.put("type", new JSONString(payment.getType().getDescription()));
//				if(payment.getStartDate() != null)
				extraAgreementPayment.put("startDate", createJSONValue(payment.getStartDate()));
				extraAgreementPayment.put("descriptionDecorable", createJSONValue(0));
				extraAgreementPayment.put("irpfExpression", createJSONValue(payment.getIrpfExpression()));
				extraAgreementPayment.put("quoteExpression",createJSONValue(payment.getQuoteExpression()));
				
				Integer extraPaymentConceptId = payment.getConceptId();
				PaymentConcept extraPaymentConcept = payment.getConcept();
				
//				Window.alert("Payment Concept :"+extraPaymentConceptId);
//				if(extraPaymentConcept != null){
//					Window.alert("Payment Extra -> id :"+extraPaymentConceptId+", domain :"+extraPaymentConcept.getDomain()
//						+", code :"+extraPaymentConcept.getCode()+", expression :"+extraPaymentConcept.getExpression()
//						+", description :"+extraPaymentConcept.getDescription()+", type :"+payment.getType().getDescription()
//						+", decriptionDecorable :"+extraPaymentConcept.getDescription_decorable()
//						+", irpfExpression :"+payment.getIrpfExpression()+", quoteExpression :"+payment.getQuoteExpression());
//				}
				
				if(extraPaymentConcept != null){//extraPaymentConceptId !=0
					JSONObject extraPaymentConceptObj = new JSONObject();
					extraPaymentConceptObj.put("id", createJSONValue(extraPaymentConceptId));
					extraPaymentConceptObj.put("domain", createJSONValue(extraPaymentConcept.getDomain()));
					extraPaymentConceptObj.put("code", createJSONValue(extraPaymentConcept.getCode()));
					extraPaymentConceptObj.put("description", createJSONValue(extraPaymentConcept.getDescription()));
					extraPaymentConceptObj.put("type", createJSONValue(extraPaymentConcept.getType()));
					extraPaymentConceptObj.put("descriptionDecorable", createJSONValue(extraPaymentConcept.getDescription_decorable()));
					extraPaymentConceptObj.put("expression", createJSONValue(extraPaymentConcept.getExpression()));
					extraPaymentConceptObj.put("irpfExpression", createJSONValue(extraPaymentConcept.getIrpf_expression()));
					extraPaymentConceptObj.put("quoteExpression", createJSONValue(extraPaymentConcept.getQuote_expression()));
					
					extraAgreementPayment.put("__payConcept", extraPaymentConceptObj);
				}
				
				extraObj.put("agreePayment", extraAgreementPayment);
				extrasArray.set(indexExtras, extraObj);
				indexExtras++;
			}	
		}
		
		json.put("extras", extrasArray); 
		
		
		// ----------------------------------------------------- peridos ----------------------------------------------------------
		JSONArray periodsArray = new JSONArray();
		int indexPeriods = 0;
	
		Date[] periods = getDatesWithChanges().toArray(new Date[]{});
		for(int i = 0; i<periods.length; i++){
			Date startDate = periods[i];
			Date endDate = null;
			if(i<periods.length-1){
				endDate = DateUtils.copyDateOnly(periods[i+1]);
				DateUtils.deleteDays2Date(endDate, 1);
			}
			
			JSONObject period = new JSONObject();
			period.put("start_date", createJSONValue((startDate.getYear()+1900)+"-"+(startDate.getMonth()+1)+"-"+startDate.getDate()));
			if(endDate == null){
				period.put("end_date", createJSONValue(null));
			}else{
				period.put("end_date", createJSONValue((endDate.getYear()+1900)+"-"+(endDate.getMonth()+1)+"-"+endDate.getDate()));
			}
			
			JSONArray levelsArray = new JSONArray();
			int indexLevels = 0;
		
			Set<Level> levelsPeriod = getLevels();
			for(Level level : levelsPeriod){
				Level levelFinally = level;
				JSONObject levelData = new JSONObject();
				levelData.put("id", createJSONValue(levelFinally.getId()));
				if(levelFinally.getId() == 0){
					levelData.put("description", createJSONValue("0"));
				}else{
					levelFinally = getLevelFinally(level, agreementDraft.getDraftLevels());
					levelData.put("description", createJSONValue(levelFinally.getDescription()));
				}
			
				JSONObject datas = new JSONObject();
				
				Set<String> variables = getVariables();
				for(String var : variables){
					Variable varLevelFinally = getVarLevelFinally(levelFinally, var);
					if(varLevelFinally != null){
						datas.put(varLevelFinally.getName(), createJSONValue(varLevelFinally.getExpression()));
					}
				}
				
				levelData.put("datas", datas);
				levelsArray.set(indexLevels, levelData);
				indexLevels++;
			}
			
			period.put("levels", levelsArray);
			periodsArray.set(indexPeriods, period);
			indexPeriods++;
		}
		
		json.put("periods", periodsArray);
		
		
		// ------------------------------------------------- levelsCategoy ------------------------------------------------
		JSONArray levelsCategoryArray = new JSONArray();
		int indexLevel = 0;
		
		Set<Level> levels = getLevels();
		for(Level level : levels){
			if(level.getId() != 0){
				Level levelFinally = getLevelFinally(level, agreementDraft.getDraftLevels());
				JSONObject levelCategory = new JSONObject();
				levelCategory.put("id", createJSONValue(levelFinally.getId()));
				levelCategory.put("description", createJSONValue(levelFinally.getDescription()));
				
				JSONArray categoriesArray = new JSONArray();
				int indexCategory = 0;
				
				Set<String> categoriesFinally = getCategoriesFinally(levelFinally);
				if(categoriesFinally != null){
					for(String category : categoriesFinally){
						categoriesArray.set(indexCategory, createJSONValue(category));
						indexCategory++;
					}
				}
				
				levelCategory.put("categories", categoriesArray);
				levelsCategoryArray.set(indexLevel, levelCategory);
				indexLevel++;
			}
		}
		
		json.put("levelsCategory", levelsCategoryArray);
		
		agreement(json);
		
		return null;
		
	}


	private JSONValue createJSONValue(Object object) {
		if (object == null)
			return JSONNull.getInstance();
		if (object instanceof Number)
			return new JSONNumber((Integer)object);
		if (object instanceof String)
			return new JSONString((String)object);
		if (object instanceof Date){
			Date date = (Date)object;
			return new JSONString(date.toGMTString());
		}
		
		return null;
		
	}


	public static String AGREE_URL = URL.encode(GWT.getModuleBaseURL() + "sergio");

	private void agreement(JSONObject json) {
		Window.alert("GENERANDO SERVLET, CRUCEMOS LOS DEDOS!");
		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.open("POST", AGREE_URL);
		xhr.setRequestHeader("Content-type", "application/json");
		xhr.send(json.toString());
	}

	/**
	 * Method to known if a Level Draft exists
	 * @param level
	 * @param draftLevels
	 * @return Level with changes if exist or origal Level if not
	 */
	private Level getLevelFinally(Level level, Set<Level> draftLevels) {
		for(Level levelDraft : draftLevels){
			if(level.getId() == levelDraft.getId())
				return levelDraft;
		}
		return level;
	}
	
	/**
	 * Method to known if a Category Draft exists
	 * @param levelFinally
	 * @return Set<String> Categories with changes if exist or origal Categories if not
	 */
	private Set<String> getCategoriesFinally(Level levelFinally) {
		Set<String> categoriesDraft = agreementDraft.getDraftCategories().get(levelFinally.getId());
		if(categoriesDraft != null)
			return categoriesDraft;
		else
			return getCategories(levelFinally);
	}
	
	private Variable getVarLevelFinally(Level level, String var) {
		Variable varLevel = getVariable(level, var);
		if(varLevel != null){
			if(isDraftVariable(level, getVariable(level, var))){
				SalaryTable draftSalaryTable = getDraftSalaryTable();
				varLevel = draftSalaryTable.get(level.getId(), var);
			}
		}
		
		return varLevel;
	}
	
	private Payment getPaymentFinally(Payment originalPayment) {
		for(Payment payment : agreementDraft.getDraftPayments()){
			if(payment.getId() == originalPayment.getId())
				return payment;
		}
		return originalPayment;
	}

//	private Payment getPaymentExtra(Integer extraPaymentId) {
//		for(Payment payment : agreementDraft.getDraftPayments()){
//			Window.alert("DRAFT -> "+payment.getId()+" == "+extraPaymentId);
//			if(payment.getId() == extraPaymentId)
//				return payment;
//		}
//		
//		for(Payment payment : getPayments()){
//			Window.alert(payment.getId()+" == "+extraPaymentId);
//			if(payment.getId() == extraPaymentId)
//				return payment;
//		}
//		
//		return null;
//		
//	}

	private SalaryTable getDraftSalaryTable() {
		return agreementDraft.getDraftSalaryTable();
		
	}

	private int getLevelIndexArray(Integer id, JSONArray levelsCategoryArray) {
		for(int i=0; i<levelsCategoryArray.size(); i++){
			JSONObject levelCategory = levelsCategoryArray.get(i).isObject();
			if(id == (int)levelCategory.get("id").isNumber().getValue()){
				return i;
			}
		}
		return -1;
		
	}
}
