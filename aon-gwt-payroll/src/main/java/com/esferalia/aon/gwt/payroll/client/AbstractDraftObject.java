package com.esferalia.aon.gwt.payroll.client;

import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.Undoable;

public class AbstractDraftObject {

	protected UndoManager<Undoable> undoManager;

	public AbstractDraftObject() {
		super();
	}

	public void redo() {
		undoManager.redo();
	}

	public void undo() {
		undoManager.undo();
	}

	public void add(Undoable undoable) {
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

	protected <T> void add(Consumer<T> setter, final T old, final T nevv) {
		add(new Undoable() {
			
			@Override
			public void undo() {
				setter.accept(old);
			}
			
			@Override
			public void redo() {
				setter.accept(nevv);
			}
		});
	}

}