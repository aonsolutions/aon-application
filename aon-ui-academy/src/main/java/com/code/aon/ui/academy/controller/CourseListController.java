package com.code.aon.ui.academy.controller;

import java.util.ArrayList;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.academy.Course;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.BasicController;

public class CourseListController extends BasicController {

	private ArrayList<Course> checks = new ArrayList<Course>();

	public void rowSelected(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			setRowChecked(((Boolean)event.getNewValue()).booleanValue());
		}
	}
	
	public boolean getRowChecked() {
		Course to = (Course) model.getRowData();
		return checks.contains(to);
	}
	
	public void setRowChecked(boolean rowChecked) {
		if (rowChecked) {
			Course to = (Course) model.getRowData();
			if (!checks.contains(to)) {
				checks.add(to);
			}
		} else {
			Course to = (Course) model.getRowData();
			if (checks.contains(to)) {
				checks.remove(to);
			}
		}
	}
	
	public ArrayList<Course> getCheckedCourses() {
		return checks;
	}
	
	public void clearCheckedCourses() {
		checks = new ArrayList<Course>();
	}
	
	public void checkAll(ActionEvent event) throws ManagerBeanException {
		for (ITransferObject ito : this.getManagerBean().getList(this.getCriteria())) {
			Course course = (Course)ito;
			if (!checks.contains(course)) {
				checks.add(course);
			}
		}
	}

	public void checkNone(ActionEvent event) {
		clearCheckedCourses();
	}

	public int getCheckedCount() {
		return getCheckedCourses().size();
	}

}