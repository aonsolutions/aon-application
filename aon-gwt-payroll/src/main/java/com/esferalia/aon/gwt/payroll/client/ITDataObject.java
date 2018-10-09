package com.esferalia.aon.gwt.payroll.client;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.Undoable;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.client.SalaryDraft.NewItemHandler;
import com.esferalia.aon.gwt.payroll.client.UndoManager.Listener;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.ITData;
import com.esferalia.aon.gwt.payroll.shared.ITDataPerson;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ITDataObject {

	interface CallculateCallback {
		void onCalculateSuccess(ITDataObject object);

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
			addIT(newT);
		}

		@Override
		public void undo() {

			if (oldT != null)
				addIT(oldT);
			else
				removeIT(newT);
		}

		abstract void addIT(T t);

		abstract void removeIT(T t);
	}

	private class UndoableInsertEdit extends UndoableEdit<ITDataPerson> {

		public UndoableInsertEdit(ITDataPerson oldT, ITDataPerson newT) {
			super(oldT, newT);
		}

		@Override
		void addIT(ITDataPerson t) {
			saveInserts(t);
		}

		@Override
		void removeIT(ITDataPerson t) {
			removeSaveInserts(t);
		}
	}

	private class UndoableDeleteEdit extends UndoableEdit<ITDataPerson> {

		public UndoableDeleteEdit(ITDataPerson oldT, ITDataPerson newT) {
			super(oldT, newT);
		}

		@Override
		void addIT(ITDataPerson t) {			
			saveDeletes(t);
			
		}

		@Override
		void removeIT(ITDataPerson t) {			
			removeSaveDeletes(t);
		}
	}

	private class UndoableUpdateEdit extends UndoableEdit<ITDataPerson> {

		public UndoableUpdateEdit(ITDataPerson oldT, ITDataPerson newT) {
			super(oldT, newT);
		}

		@Override
		void addIT(ITDataPerson t) {
			saveUpdates(t);
		}

		@Override
		void removeIT(ITDataPerson t) {
			removeSaveUpdates(t);
		}
	}

	private Map<Integer, LinkedHashMap<Integer, ITDataPerson>> inserts;
	private Map<Integer, LinkedHashMap<Integer, ITDataPerson>> updates;
	private Map<Integer, LinkedHashMap<Integer, ITDataPerson>> deletes;
	
	private List<ITDataPerson> draftList;

	private int contador;

	private UndoManager<UndoableEdit<?>> undoManager;
	private DomainEmployeesServiceAsync employeesService;
	private int workplaceId;
	private ITData itData;

	public ITDataObject(Integer workplace,
			DomainEmployeesServiceAsync employeesService) {
		this.workplaceId = workplace;
		this.employeesService = employeesService;
		this.undoManager = new UndoManager<UndoableEdit<?>>();
		
		this.inserts = new LinkedHashMap<Integer, LinkedHashMap<Integer, ITDataPerson>>();
		this.deletes = new LinkedHashMap<Integer, LinkedHashMap<Integer, ITDataPerson>>();
		this.updates = new LinkedHashMap<Integer, LinkedHashMap<Integer, ITDataPerson>>();

		this.draftList = new LinkedList<ITDataPerson>();

		this.contador = 0;
	}

	// ------------------------------------------

	public Map<Integer, Employee> getEmployees() {
		return itData.getEmployees();
	}

	public List<Integer> getYears() {
		return itData.getYears();
	}

	public Map<Integer, ITDataPerson> getDataIts(int contractId) {
		
		Map<Integer, ITDataPerson> map = new LinkedHashMap<Integer, ITDataPerson>();
		map.putAll(itData.getDataIts(contractId));
		map.putAll(getSaveInserts(contractId));
		map.putAll(getSaveUpdates(contractId));
		map.keySet().removeAll(getSaveDeletes(contractId).keySet());
		sortMap(map);		

		return map;

	}

	// ------------------------------------------
	// UndoManager delegates
	// ------------------------------------------

	// ------------------------------------------
	
	public void addLeaveItem(ITDataPerson newItem) {
		ITDataPerson oldItem = saveInserts(newItem);
		undoManager.add(new UndoableInsertEdit(oldItem, newItem));
	}

	private ITDataPerson saveInserts(ITDataPerson object) {
		contador++;	
		return getSaveInserts(object.getContractId()).put(
				object.getContractLeaveId(), object);
	}

	private ITDataPerson removeSaveInserts(ITDataPerson object) {
		--contador;		
		return getSaveInserts(object.getContractId()).remove(
				object.getContractLeaveId());
	}

	public void removeLeaveItem(int contractId, int leaveId) {
		
		ITDataPerson oldItem = getDataIts(contractId).get(leaveId);
		ITDataPerson newItem = saveDeletes(oldItem);
		undoManager.add(new UndoableDeleteEdit(newItem, oldItem));
	}

	private ITDataPerson saveDeletes(ITDataPerson object) {

		if (object.getContractLeaveId() < 0) {
			--contador;			
			return getSaveInserts(object.getContractId()).remove(
					object.getContractLeaveId());
		}

		else {
			++contador;
			object.setRegBase("REMOVE_VARIABLE()");
			return getSaveDeletes(object.getContractId()).put(
					object.getContractLeaveId(), object);
		}
	}

	private ITDataPerson removeSaveDeletes(ITDataPerson object) {
		if (object.getContractLeaveId() < 0) {
			++contador;
			object.setRegBase(null);			
			return getSaveInserts(object.getContractId()).put(
					object.getContractLeaveId(), object);
		}

		else {
			--contador;
			object.setRegBase(null);
			return getSaveDeletes(object.getContractId()).remove(
					object.getContractLeaveId());			
		}
	}

	public void updateLeaveItem(ITDataPerson newItem) {
		ITDataPerson oldItem = saveUpdates(newItem);
		undoManager.add(new UndoableUpdateEdit(oldItem, newItem));
	}

	private ITDataPerson saveUpdates(ITDataPerson object) {

		ITDataPerson dataPerson;
		if (object.getContractLeaveId() < 0)
			dataPerson = getSaveInserts(object.getContractId()).put(
					object.getContractLeaveId(), object);

		else
			dataPerson = getSaveUpdates(object.getContractId()).put(
					object.getContractLeaveId(), object);

		++contador;		
		return dataPerson;
	}

	private ITDataPerson removeSaveUpdates(ITDataPerson object) {
		ITDataPerson dataPerson;
		if (object.getContractLeaveId() < 0) {
			dataPerson = getSaveInserts(object.getContractId()).remove(
					object.getContractLeaveId());
		} else {

			dataPerson = getSaveUpdates(object.getContractId()).remove(
					object.getContractLeaveId());
		}
		--contador;		
		return dataPerson;
	}

	private void clearDrafts() {
	
		inserts.clear();		
		updates.clear();		
		deletes.clear();		
		draftList.clear();		
	}

	public Map<Integer, LinkedHashMap<Integer, ITDataPerson>> getInserts() {
		return Collections.unmodifiableMap(inserts);
	}

	public Map<Integer, LinkedHashMap<Integer, ITDataPerson>> getUpdates() {
		return Collections.unmodifiableMap(updates);
	}

	public Map<Integer, LinkedHashMap<Integer, ITDataPerson>> getDeletes() {
		return Collections.unmodifiableMap(deletes);
	}

	// ---------------------------------------------------------

	private Map<Integer, ITDataPerson> getSaveInserts(int id) {
		if (inserts.containsKey(id) == false) {
			inserts.put(id, new LinkedHashMap<Integer, ITDataPerson>());
		}
		return inserts.get(id);
	}

	private Map<Integer, ITDataPerson> getSaveDeletes(int id) {
		if (deletes.containsKey(id) == false) {
			deletes.put(id, new LinkedHashMap<Integer, ITDataPerson>());
		}
		return deletes.get(id);
	}

	private Map<Integer, ITDataPerson> getSaveUpdates(int id) {
		if (updates.containsKey(id) == false) {
			updates.put(id, new LinkedHashMap<Integer, ITDataPerson>());
		}
		return updates.get(id);
	}

	// --------------------------------------------------------- SalaryDraft

	public List<ITDataPerson> getDraftList(int contractId) {
		
		Map<Integer, ITDataPerson> map = new HashMap<Integer, ITDataPerson>();
		map.putAll(getSaveInserts(contractId));
		map.putAll(getSaveUpdates(contractId));
		map.keySet().removeAll(getSaveDeletes(contractId).keySet());
		map.putAll(getSaveDeletes(contractId));
		
		sortMap(map);
		draftList.clear();
		draftList.addAll(map.values());
		
		return draftList;
	}

	// --------------------------------------------------------- CorrectChecks

	public boolean isCorrectStartDateLeave(int contractId, int leaveId,
			Date startDate) {

		Map<Integer, ITDataPerson> map = getDataIts(contractId);

		for (ITDataPerson iterator : map.values()) {
			if (leaveId == iterator.getContractLeaveId())
				continue;

			if ((DateUtils.compare(startDate, iterator.getLeaveStartDate()) > 0)
					&& (DateUtils
							.compare(startDate, iterator.getLeaveEndDate()) < 0))
				return false;
		}
		return true;
	}

	public boolean isCorrectEndDateLeave(int contractId, int leaveId,
			Date startDate, Date endDate) {

		Map<Integer, ITDataPerson> map = getDataIts(contractId);

		for (ITDataPerson iterator : map.values()) {

			if (leaveId == iterator.getContractLeaveId())
				continue;

			if ((DateUtils.compare(endDate, iterator.getLeaveStartDate()) > 0)
					&& (DateUtils.compare(endDate, iterator.getLeaveEndDate()) < 0)
					|| (DateUtils
							.compare(startDate, iterator.getLeaveEndDate()) > 0)
					&& (DateUtils
							.compare(endDate, iterator.getLeaveStartDate()) < 0)
					|| (DateUtils
							.compare(startDate, iterator.getLeaveEndDate()) < 0)
					&& (DateUtils
							.compare(endDate, iterator.getLeaveStartDate()) > 0))
				return false;
		}
		return true;
	}

	// ---------------------------------------------------------

	public void load(final AsyncCallback<ITDataObject> cb) {
		if (itData != null) {
			cb.onSuccess(ITDataObject.this);

		} else {
			getWorkplaceITData(cb);
		}
	}

	private void getWorkplaceITData(final AsyncCallback<ITDataObject> cb) {
		employeesService.getWorkplaceITData(workplaceId,
				new AsyncCallback<ITData>() {
					@Override
					public void onFailure(Throwable caught) {
						cb.onFailure(caught);
					}

					@Override
					public void onSuccess(ITData itData) {
						ITDataObject.this.itData = itData;
						cb.onSuccess(ITDataObject.this);
						contador = 0;
					}
				});
	}

	public void save(final CallculateCallback callback) {

		employeesService.saveITDataPerson(inserts, deletes, updates,
				new AsyncCallback<ITData>() {

					@Override
					public void onSuccess(ITData result) {
						contador = 0;
						undoManager.discardAll();
						clearDrafts();
						employeesService.saveITDataPerson(inserts, deletes,
								updates, new AsyncCallback<ITData>() {

									@Override
									public void onSuccess(ITData result) {										
										ITDataObject.this.itData = result;										
										callback.onCalculateSuccess(ITDataObject.this);										
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

	// ------------------------------------------

	@SuppressWarnings("unchecked")
	private void sortMap(Map<Integer, ITDataPerson> map) {

		List<ITDataPerson> sortedList = new LinkedList<ITDataPerson>();
		@SuppressWarnings("rawtypes")
		Iterator it = map.entrySet().iterator();

		while (it.hasNext()) {

			Map.Entry<Integer, ITDataPerson> e = (Map.Entry<Integer, ITDataPerson>) it
					.next();
			sortedList.add(e.getValue());
		}

		Collections.sort(sortedList, new Comparator<ITDataPerson>() {

			@Override
			public int compare(ITDataPerson o1, ITDataPerson o2) {

				return o1.getLeaveStartDate().compareTo(o2.getLeaveStartDate());
			}
		});

		map.clear();

		for (int x = 0; x < sortedList.size(); x++) {

			map.put(sortedList.get(x).getContractLeaveId(), sortedList.get(x));
		}

	}

	// ------------------------------------------
	// Undo & Redo Support
	// ------------------------------------------

	public void redo() {
		undoManager.redo();
	}

	public void undo() {
		undoManager.undo();
	}

	public void add(UndoableEdit<?> undoable) {
		undoManager.add(undoable);
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

	public final boolean saveActive() {
		return contador > 0;
	}
	
	// ------------------------------------------

}
