package net.aonsolutions.occam.api;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public abstract class OccamEntity<T extends Enum<?>> implements Serializable {

	private static final long serialVersionUID = -2298765155655521769L;
	
	private HashSet<T> dirtySet;
	private boolean selected;

	public boolean isDirty() {
		return !getDirtySet().isEmpty();
	}

	public OccamEntity<T> markAsClean() {
		dirtySet = new HashSet<>();
		return this;
	}
	
	public Set<T> getDirtySet() {
		if (dirtySet == null) markAsClean();
		return dirtySet;
	}
	
	public OccamEntity<T> markAsDirty(T key) {
		getDirtySet().add(key);
		return this;
	}
	
	public boolean isDirty(T key) {
		return getDirtySet().contains(key);
	}

	public boolean isSelected() {
		return selected;
	}
	public OccamEntity<T> setSelected(boolean selected) {
		this.selected = selected;
		return this;
	}
	
	public <R extends Enum<?>> boolean mustMarkaAsDirty(OccamEntity<R> original, OccamEntity<R> toUpdate) {
		if (toUpdate == null && original == null) {
			return false;
		} else if (toUpdate != null) {
			return toUpdate.isDirty();
		} else {
			return true;
		}
	}

	protected abstract Object getUuid();
	
}
