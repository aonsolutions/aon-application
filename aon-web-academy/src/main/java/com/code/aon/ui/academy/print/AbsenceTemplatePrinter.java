package com.code.aon.ui.academy.print;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.academy.CourseAlumn;
import com.code.aon.academy.enumeration.CourseAlumnStatus;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.academy.controller.CourseAlumnController;
import com.code.aon.ui.util.AonUtil;

public class AbsenceTemplatePrinter implements ICollectionProvider{

	private static final Logger LOGGER = Logger.getLogger(AbsenceTemplatePrinter.class.getName());
	
	private static final String COURSE_ALUMN_CONTROLLER_NAME = "courseAlumn";

	@SuppressWarnings("unchecked")
	public Collection getCollection() {
		List<CourseAlumn> list = new LinkedList<CourseAlumn>();
		try{
			CourseAlumnController courseAlumnController = (CourseAlumnController)AonUtil.getController(COURSE_ALUMN_CONTROLLER_NAME);
			Iterator iter = ((List)courseAlumnController.getModel().getWrappedData()).iterator();
			while (iter.hasNext()){
				CourseAlumn courseAlumn = (CourseAlumn)iter.next();
				if (CourseAlumnStatus.ACTIVE.compareTo(courseAlumn.getStatus())==0){
					list.add(courseAlumn);
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining Collection", e);
		}
		return list;
	}
}