package com.code.aon.ui.academy.event;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.code.aon.academy.Course;
import com.code.aon.ui.academy.controller.CourseMarkController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class CourseMarksControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		Course course = (Course)event.getController().getTo();
        FacesContext ctx = FacesContext.getCurrentInstance();
        ValueBinding vb = ctx.getApplication().createValueBinding("#{courseMark}");
        CourseMarkController controller = (CourseMarkController)vb.getValue(ctx);
        controller.setCourse(course);
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		Course course = (Course)event.getController().getTo();
        FacesContext ctx = FacesContext.getCurrentInstance();
        ValueBinding vb = ctx.getApplication().createValueBinding("#{courseMark}");
        CourseMarkController controller = (CourseMarkController)vb.getValue(ctx);
        controller.setCourse(course);
	}
	
}
