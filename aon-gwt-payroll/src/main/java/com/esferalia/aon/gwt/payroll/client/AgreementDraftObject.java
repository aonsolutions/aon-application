package com.esferalia.aon.gwt.payroll.client;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.esferalia.aon.gwt.payroll.client.FxDialog.IContextProvider;
import com.esferalia.aon.gwt.payroll.client.UndoManager.Listener;
import com.esferalia.aon.gwt.payroll.shared.AgreementDraft;
import com.esferalia.aon.gwt.payroll.shared.AgreementDraft.Level;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.HasId;
import com.esferalia.aon.gwt.payroll.shared.HasStartAndEndDate;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.datepicker.client.CalendarUtil;

public class AgreementDraftObject implements IContextProvider {

	public static Date NULL_DATE = new Date() {
	};
	
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

	private Date draftEndDate;
	private Date draftStartDate;

	private int nextDraftLevelId = 0;
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
		level.setId(--nextDraftPaymentId);
		return level;

	}

	public Payment newDraftPayment() {
		Payment payment = new Payment();
		payment.setId(--nextDraftLevelId);
		return payment;

	}

	public Date getDraftEndDate() {
		return draftEndDate == null ? agreementDraft.getEndDate()
				: (draftEndDate == NULL_DATE ? null : draftEndDate);
	}

	public Date getDraftStartDate() {
		return draftStartDate == null ? agreementDraft.getStartDate()
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

	public void setDescription(String description) {
		agreementDraft.setDescription(description);
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
		return agreementDraft.hasDrafts()
				|| !isDraftPeriodSet(getDraftStartDate(), getDraftEndDate(),
						agreementDraft);
	}

	// ------------------------------------------
	// Undo & Redo Support

	public void addDraftPayment(Payment payment) {
		Payment oldPayment = agreementDraft.addDraftPayment(payment);
		undoManager.add(new UndoablePaymentEdit(oldPayment, payment));
	}

	public void addDraftLevel(Level level) {
		Level oldLevel = agreementDraft.addDraftLevel(level);
		undoManager.add(new UndoableLevelEdit(oldLevel, level));
	}

	public void addDraftVariable(Level level, Variable var) {
		agreementDraft.addDraftVariable(level, var);
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


		employeesServiceAsync.saveAgreementDraft(agreementDraft,
				new AsyncCallback<Void>() {

					@Override
					public void onSuccess(Void result) {


						undoManager.discardAll();

						employeesServiceAsync.calculateAgreementDraft(agreementDraft,
								new AsyncCallback<AgreementDraft>() {

									@Override
									public void onSuccess(AgreementDraft result) {
										AgreementDraftObject.this.agreementDraft= result;
										callback.onCalculateSucces(AgreementDraftObject.this);
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


	// ------------------------------------------
	//
	// ------------------------------------------
	@Override
	public void getContext(AsyncCallback<ContextDescriptor> callback) {
		// employeesServiceAsync.getContext(salaryDraft, callback);
	}

	@Override
	public void eval(String expression, AsyncCallback<Double> callback) {
		// employeesServiceAsync.eval(expression, salaryDraft, callback)
	}

	// ------------------------------------------
	// Differences
	// ------------------------------------------

	Set<Level> getChangedLevels() {
		if (oldAgreementDraft == null || oldAgreementDraft.getLevels() == null )
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
		if (oldAgreementDraft == null || oldAgreementDraft.getVariables() == null)
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

	private static boolean isDraftPeriodSet(Date draftStartDate,
			Date draftEndDate, AgreementDraft draft) {
		if (!isStartAndEndDatesSet(draftStartDate, draftEndDate,
				draft.getDraftPayments()))
			return false;
		return true;
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
