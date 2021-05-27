package com.code.aon.common;

/**
 * @author ecastellano
 * 
 */
public interface IProgression {
	
	Long CANCEL_VALUE = -1L;
	
	Long START_VALUE = 0L;
	
	Long FINISH_VALUE = 101L;
	
	Long ERROR_VALUE = 666L;
	
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
