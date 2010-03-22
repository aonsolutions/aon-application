package com.code.aon.academy.event;

import com.code.aon.academy.CourseAlumn;
import com.code.aon.academy.enumeration.CourseAlumnStatus;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;

public class CourseAlumnVetoListener extends ManagerBeanVetoListenerAdapter {

    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		CourseAlumn courseAlumn = (CourseAlumn)evt.getTo();
		if (courseAlumn.getStatus()==null){
			courseAlumn.setStatus(CourseAlumnStatus.ACTIVE);
		}
    }

}