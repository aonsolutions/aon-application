package net.aonsolutions.occam.api;

public interface HasConfidentiality<T> {

	boolean isConfidential();
	T setConfidential(boolean confidential);

}
