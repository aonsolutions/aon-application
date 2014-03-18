package com.code.aon.ui.academy.controller;

import java.util.Calendar;

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.academy.CourseSchedule;
import com.code.aon.AonVersion;
import com.code.aon.ui.form.LinesController;

public class CourseScheduleController extends LinesController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private CourseSchedule getCourseSchedule() {
		return (CourseSchedule) getTo();
	}

	public int getStartTimeHours() {
        Calendar c = Calendar.getInstance();
        c.setTime(getCourseSchedule().getStartTime());
        return c.get(Calendar.HOUR_OF_DAY);
	}

	public void setStartTimeHours(int startTimeHours) {
		CourseSchedule cs = getCourseSchedule();
		cs.setStartTime( DateUtils.setHours(cs.getStartTime(), startTimeHours) );
	}

	public int getStartTimeMinutes() {
        Calendar c = Calendar.getInstance();
        c.setTime(getCourseSchedule().getStartTime());
        return c.get(Calendar.MINUTE);
	}

	public void setStartTimeMinutes(int startTimeMinutes) {
		CourseSchedule cs = getCourseSchedule();
		cs.setStartTime( DateUtils.setMinutes(cs.getStartTime(), startTimeMinutes) );
	}

	public int getEndTimeHours() {
        Calendar c = Calendar.getInstance();
        c.setTime(getCourseSchedule().getEndTime());
        return c.get(Calendar.HOUR_OF_DAY);
	}

	public void setEndTimeHours(int endTimeHours) {
		CourseSchedule cs = getCourseSchedule();
		cs.setEndTime( DateUtils.setHours(cs.getEndTime(), endTimeHours) );
	}

	public int getEndTimeMinutes() {
        Calendar c = Calendar.getInstance();
        c.setTime(getCourseSchedule().getEndTime());
        return c.get(Calendar.MINUTE);
	}

	public void setEndTimeMinutes(int endTimeMinutes) {
		CourseSchedule cs = getCourseSchedule();
		cs.setEndTime( DateUtils.setMinutes(cs.getEndTime(), endTimeMinutes) );
	}
	
}