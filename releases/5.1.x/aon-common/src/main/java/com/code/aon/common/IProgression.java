package com.code.aon.common;

/**
 * @author ecastellano
 * 
 */
public interface IProgression {
	/**
	 * @return Long
	 */
	public Long getProgressionCurrentValue();

	/**
	 * @param currentValue
	 */
	public void setProgressionCurrentValue(Long currentValue);
	
	/**
	 * @return boolean
	 */
	public boolean isProgressionEnabled();

	/**
	 * @param enabled
	 */
	public void setProgressionEnabled(boolean enabled);
}
