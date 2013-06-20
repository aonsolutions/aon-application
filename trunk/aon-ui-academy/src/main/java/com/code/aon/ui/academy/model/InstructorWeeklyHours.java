package com.code.aon.ui.academy.model;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.code.aon.groupware.TaskHolder;

public class InstructorWeeklyHours {

	private TaskHolder instructor;
	
	private Double[] values;

	private String[] valuesFormatted;

	/**
	 * @return the instructor
	 */
	public TaskHolder getInstructor() {
		return instructor;
	}

	/**
	 * @param instructor the instructor to set
	 */
	public void setInstructor(TaskHolder instructor) {
		this.instructor = instructor;
	}

	public Double[] getValues() {
		return values;
	}

	public void setValues(Double[] values) {
		this.values = values;
	}

	public String[] getValuesFormatted() {
		if (valuesFormatted == null) {
			NumberFormat formatter = new DecimalFormat("#,##0.00"); 
			valuesFormatted = new String[getValues().length];
			for(int i=0; i<valuesFormatted.length; valuesFormatted[i] = formatter.format(getValues()[i]), i++);
		}
		return valuesFormatted;
	}

}
