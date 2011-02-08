package com.code.aon.planner.core;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Period;

public class WorkingTime {

	public static final String[] INTERVAL = {"AM", "PM"};

	private Component component;
	private Period period;
    private int shift;
	private int start;
	private int end;

	/**
	 * @return Returns the wrapped shift.
	 */
	public String getWrappedShift() {
		return INTERVAL[shift];
	}

	/**
	 * @return Returns the shift.
	 */
	public int getShift() {
		return shift;
	}
	/**
	 * @param shift The shift to set.
	 */
	public void setShift(int shift) {
		this.shift = shift;
	}

	/**
	 * @return Returns the end.
	 */
	public int getEnd() {
		return end;
	}

	/**
	 * @param end The end to set.
	 */
	public void setEnd(int end) {
		this.end = end;
	}

	/**
	 * @return Returns the start.
	 */
	public int getStart() {
		return start;
	}

	/**
	 * @param start The start to set.
	 */
	public void setStart(int start) {
		this.start = start;
	}

	/**
	 * @return Returns the component.
	 */
	public Component getComponent() {
		return component;
	}

	/**
	 * @param component The component to set.
	 */
	public void setComponent(Component component) {
		this.component = component;
	}

	/**
	 * @return Returns the period.
	 */
	public Period getPeriod() {
		return period;
	}

	/**
	 * @param period The period to set.
	 */
	public void setPeriod(Period period) {
		this.period = period;
	}

	
}
