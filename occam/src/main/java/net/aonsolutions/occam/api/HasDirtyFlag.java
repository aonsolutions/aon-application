package net.aonsolutions.occam.api;

public interface HasDirtyFlag<T> {

	public boolean isDirty();
	public T setDirty(boolean dirty);
	
	public default void dirtyMark(boolean dirty) {
		setDirty( isDirty() || dirty);
	}
	
	public default void dirtyMark(HasDirtyFlag<?> original, HasDirtyFlag<?> toUpdate) {
		if (toUpdate == null && original == null) {
			this.dirtyMark(false);
		} else if (toUpdate != null) {
			this.dirtyMark(toUpdate.isDirty());
		} else {
			this.dirtyMark(true);
		}
	}
	

	
}
