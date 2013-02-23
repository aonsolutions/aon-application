package com.esferalia.aon.gwt.payroll.client;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class UndoManager {

	interface Undoable {
		void undo();

		void redo();
	}
	
	interface Listener {
		void onChange(UndoManager undoManager);
	}

	private Stack<Undoable> undoStack;
	private Stack<Undoable> redoStack;
	
	private List<Listener> listeners;

	public UndoManager() {
		undoStack = new Stack<Undoable>();
		redoStack = new Stack<Undoable>();
		listeners = new LinkedList<Listener>();
	}
	

	public void redo() {
		if ( canRedo()) {
			Undoable undoable = redoStack.pop();
			undoable.redo();
			undoStack.push(undoable);
			fireOnChange();
		}
	}

	public void undo() {
		if ( canUndo()) {
			Undoable undoable = undoStack.pop();
			undoable.undo();
			redoStack.push(undoable);
			fireOnChange();
		}
	}

	public void add(Undoable undoable) {
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
