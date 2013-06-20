package com.code.aon.ui.academy.print;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.AbortProcessingException;

import com.code.aon.academy.Course;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.academy.controller.CourseController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class CourseAbsenceTemplatePrinter extends AbsenceTemplatePrinter {
	
	
	@Override
	@SuppressWarnings("unchecked")
	public Collection getCollection() {
		List<AbsenceReportTo> list = new ArrayList<AbsenceReportTo>();
		try {
			CourseController courseController = (CourseController)FormUtil.getController(COURSE_CONTROLLER_NAME);
			Iterator iter = courseController.getManagerBean().getList(courseController.getCriteria()).iterator();
			while(iter.hasNext()){
				Course course = (Course)iter.next();
				AbsenceReportTo absenceReportTo = new AbsenceReportTo();
				absenceReportTo.setCourse(course);
				absenceReportTo.setCourseAlumns(obtainCourseAlumnList(course));
				absenceReportTo.setInstructor(obtainCourseInstructor(course));
				list.add(absenceReportTo);
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Unable to load collection to print");
			throw new AbortProcessingException();
		}
		return list;
	}

}