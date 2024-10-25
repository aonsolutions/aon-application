package net.aonsolutions.occam.api.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

public abstract class AonEntity<T extends Enum<?>> implements Serializable {

	private static final long serialVersionUID = -2298765155655521769L;
	
	private HashSet<T> dirtySet;
	private boolean selected;
	private boolean deleted;

	public boolean isDirty() {
		return !dirtySet().isEmpty();
	}

	public AonEntity<T> markAsClean() {
		dirtySet = new HashSet<>();
		return this;
	}
	
	public Set<T> dirtySet() {
		if (dirtySet == null) markAsClean();
		return dirtySet;
	}
	public Stream<T> dirtySetStream() {
		return AonCollectionUtils.stream(dirtySet());
	}
	
	public AonEntity<T> markAsDirty(T key) {
		dirtySet().add(key);
		return this;
	}
	
	public boolean isDirty(T key) {
		return dirtySet().contains(key);
	}

	protected <R extends Enum<?>> boolean mustMarkAsDirty(AonEntity<R> original, AonEntity<R> toUpdate) {
		if (toUpdate == null && original == null) return false;
		if (toUpdate != null) return toUpdate.isDirty();
		return true;
	}

	protected <C> void checkIfDirty(C c1, C c2, T t) {
		if (notEquals(c1,c2)) markAsDirty(t);
	}
	protected void checkIfDirty(Number n1, Number n2, T t) {
		if (notEquals(n1,n2)) markAsDirty(t);
	}
	protected void checkIfDirty(AonEntity<?> a1, AonEntity<?> a2, T t) {
		if (notEquals(a1,a2)) markAsDirty(t);
	}
	
	private boolean notEquals(final AonEntity<?> a1, final AonEntity<?> a2) {
		if (a1 == a2) return false;
		if (a1 == null || a2 == null) return true;
		if (a1.getUuid() == a2.getUuid()) return false;
		if (a1.getUuid() == null || a2.getUuid() == null) return true;
		return !a1.getUuid().equals(a2.getUuid());
	}

	private boolean equals(final Object obj1, final Object obj2) {
		if (obj1 == obj2) {
			return true;
		}
		if (obj1 == null || obj2 == null) {
			return false;
		}
		return obj1.equals(obj2);
	}
	private boolean notEquals(final Object obj1, final Object obj2) {
		return !equals(obj1, obj2);
	}

	private boolean equals(final Number n1, final Number n2) {
		return AonNumberUtils.equals(n1, n2);
	}
	private boolean notEquals(final Number n1, final Number n2) {
		return !equals(n1, n2);
	}

	public boolean isSelected() {
		return selected;
	}
	public AonEntity<T> setSelected(boolean selected) {
		this.selected = selected;
		return this;
	}

	public boolean isDeleted() {
		return deleted;
	}
	public boolean isNotDeleted() {
		return !deleted;
	}
	public AonEntity<T> setDeleted(boolean deleted) {
		this.deleted = deleted;
		return this;
	}

	protected abstract Object getUuid();
	
}