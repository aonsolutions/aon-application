package com.esferalia.aon.gwt.payroll.client;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class UndoManager<T extends Undoable> {

	interface Listener {
		void onChange(UndoManager undoManager);
	}

	private Stack<T> undoStack;
	private Stack<T> redoStack;
	
	private List<Listener> listeners;

	public UndoManager() {
		undoStack = new Stack<T>();
		redoStack = new Stack<T>();
		listeners = new LinkedList<Listener>();
	}
	

	public void redo() {
		if ( canRedo()) {
			T undoable = redoStack.pop();
			undoable.redo();
			undoStack.push(undoable);
			fireOnChange();
		}
	}

	public void undo() {
		if ( canUndo()) {
			T undoable = undoStack.pop();
			undoable.undo();
			redoStack.push(undoable);
			fireOnChange();
		}
	}
	
	public void discardAll() {
		undoStack.clear();
		redoStack.clear();
	}
	
	public void add(T undoable) {
		undoStack.push(undoable);
		fireOnChange();
	}


	public final boolean canUndo() {
		return !undoStack.isEmpty();
	}

	public final boolean canRedo() {
		return !redoStack.isEmpty();
	}
	

	public void addListener(Listener listener){
		if ( !listeners.contains(listener) )
			listeners.add(listener);
	}

	public void removeListener(Listener listener){
		listeners.remove(listener);
	}
	
	
	private void fireOnChange(){
		for (Listener listener : listeners)
			listener.onChange(this);
	}

}
