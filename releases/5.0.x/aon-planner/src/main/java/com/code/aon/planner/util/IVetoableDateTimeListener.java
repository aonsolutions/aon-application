package com.code.aon.planner.util;


public interface IVetoableDateTimeListener {

	static final String FIRSTTIME = "VETO_FIRSTTIME";

	/**
	 * Comprueba que la fecha para la que se está citando está libre.
	 * 
	 * @param event
	 */
	void vetoableDate(DateTimeEvent event) throws DateTimeException;

	/**
	 * Comprueba que la hora para la que se está citando está libre.
	 * 
	 * @param event
	 */
	void vetoableTime(DateTimeEvent event) throws DateTimeException;
}
