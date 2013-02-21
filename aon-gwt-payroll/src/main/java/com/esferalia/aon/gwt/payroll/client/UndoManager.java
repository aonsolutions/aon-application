package com.esferalia.aon.gwt.payroll.client;

import java.util.Stack;

public class UndoManager {

	interface Undoable {
		void undo();

		void redo();
	}

	private Stack<Undoable> undoStack;
	private Stack<Undoable> redoStack;

	public UndoManager() {
		undoStack = new Stack<UndoManager.Undoable>();
		redoStack = new Stack<UndoManager.Undoable>();
	}

	public void redo() {
		if ( canRedo()) {
			Undoable undoable = redoStack.pop();
			undoable.redo();
			undoStack.push(undoable);
		}
	}

	public void undo() {
		if ( canUndo()) {
			Undoable undoable = undoStack.pop();
			undoable.undo();
			redoStack.push(undoable);
		}
	}

	public void add(Undoable undoable) {
		undoStack.push(undoable);
	}

	public final boolean canUndo() {
		return !undoStack.isEmpty();
	}

	public final boolean canRedo() {
		return !redoStack.isEmpty();
	}
}
