package net.aonsolutions.occam.api;

import java.io.Serializable;
import java.util.HashSet;

public abstract class OccamEntity implements Serializable {

	private static final long serialVersionUID = -2298765155655521769L;
	
	private HashSet<String> dirtySet;
	private boolean selected;

	public boolean isDirty() {
		return !getDirtySet().isEmpty();
	}

	public OccamEntity markAsClean() {
		dirtySet = new HashSet<>();
		return this;
	}
	
	public HashSet<String> getDirtySet() {
		if (dirtySet == null) markAsClean();
		return dirtySet;
	}
	
	public OccamEntity markAsDirty(String key) {
		getDirtySet().add(key);
		return this;
	}
	
	public boolean isDirty(String key) {
		return getDirtySet().contains(key);
	}

	public boolean isSelected() {
		return selected;
	}
	public OccamEntity setSelected(boolean selected) {
		this.selected = selected;
		return this;
	}
	
	public boolean mustMarkaAsDirty(OccamEntity original, OccamEntity toUpdate) {
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
