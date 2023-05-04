package net.aonsolutions.occam.api;

public interface HasSelector<T> {

	boolean isSelected();
	T setSelected( boolean selected);

}
