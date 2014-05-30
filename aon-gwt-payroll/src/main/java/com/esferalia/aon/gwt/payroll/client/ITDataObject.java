package com.esferalia.aon.gwt.payroll.client;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.payroll.client.UndoManager.Listener;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.ITData;
import com.esferalia.aon.gwt.payroll.shared.ITDataPerson;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ITDataObject {

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

	private class UndoableUpdateEdit extends UndoableEdit<ITDataPerson> {

		public UndoableUpdateEdit(ITDataPerson oldT, ITDataPerson newT) {
			super(oldT, newT);
		}

		@Override
		void addIT(ITDataPerson t) {
			itData.setLeaveItem(t);
		}

		@Override
		void removeIT(ITDataPerson t) {
			itData.removeLeaveItem(t);
		}
	}
	
	private class UndoableRemoveEdit extends UndoableEdit<ITDataPerson> {

		public UndoableRemoveEdit(ITDataPerson oldT, ITDataPerson newT) {
			super(oldT, newT);			
		}
		
		@Override
		void addIT(ITDataPerson t) {
			itData.setLeaveItem(t);						
		}
		@Override
		void removeIT(ITDataPerson t) {
			itData.removeLeaveItem(t);			
		}
		
	}

	private UndoManager<UndoableEdit<?>> undoManager;
	private EmployeesServiceAsync employeesService;
	private Map<Integer, ITDataPerson> undoableMap;
	private Map<Integer, ITDataPerson> update;
	private int workplaceId;	
	private ITData itData;
	
	public ITDataObject(Integer workplace,
			EmployeesServiceAsync employeesService) {		
		this.workplaceId = workplace;
		this.employeesService = employeesService;		
		this.undoManager = new UndoManager<UndoableEdit<?>>();
		this.update = new LinkedHashMap<Integer, ITDataPerson>();
		this.undoableMap = new LinkedHashMap<Integer, ITDataPerson>();
		
	}

	// ------------------------------------------
	// Undo & Redo Support

	public Map<Integer, Employee> getEmployees() {
		return itData.getEmployees();
	}

	public List<Integer> getYears() {
		return itData.getYears();
	}

	public Map<Integer, ITDataPerson> getDataIts(int contractId) {
		return itData.getDataIts(contractId);
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

	// ------------------------------------------

	public void addLeaveItem(ITDataPerson newItObject) {
		ITDataPerson oldData = itData.setLeaveItem(newItObject);
		setUndoableUpdateEdit(oldData, newItObject);
		
	}

	public void removeLeaveItem(ITDataPerson newItObject) {
		ITDataPerson oldData = itData.removeLeaveItem(newItObject);
		setUndoableRemoveEdit(oldData, newItObject);		
	}

	public void updateItem(ITDataPerson newItObject) {		
		ITDataPerson oldData = itData.setLeaveItem(newItObject);
		setUndoableUpdateEdit(oldData, newItObject);		
	}
	
	private void setUndoableUpdateEdit(ITDataPerson oldData, ITDataPerson newData) {
		undoManager.add(new UndoableUpdateEdit(oldData, newData));
	}
	
	private void setUndoableRemoveEdit(ITDataPerson oldData, ITDataPerson newData) {
		undoManager.add(new UndoableRemoveEdit(oldData, newData));
	}

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
					}
				});
	}

	// ------------------------------------------
	
	
	
	// ------------------------------------------ Undoable control


}
