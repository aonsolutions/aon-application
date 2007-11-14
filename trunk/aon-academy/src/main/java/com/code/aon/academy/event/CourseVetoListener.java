package com.code.aon.academy.event;

import com.code.aon.academy.AcademicYear;
import com.code.aon.academy.Course;
import com.code.aon.academy.CourseLevel;
import com.code.aon.academy.CourseSubject;
import com.code.aon.academy.dao.IAcademyAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.ql.Criteria;

public class CourseVetoListener extends ManagerBeanVetoListenerAdapter {

    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
        vetoableBeanSaved(evt);
    }

	private void vetoableBeanSaved(ManagerBeanEvent evt) {
		Course course = (Course)evt.getTo();
		String description = "";
        try {
            IManagerBean academicYearBean = BeanManager.getManagerBean(AcademicYear.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(academicYearBean.getFieldName(IAcademyAlias.ACADEMIC_YEAR_ID), course.getAcademicYear().getId());
            description += ((AcademicYear)academicYearBean.getList(criteria).get(0)).getDescription() + " ";

            IManagerBean courseSubjectBean = BeanManager.getManagerBean(CourseSubject.class);
            criteria = new Criteria();
            criteria.addEqualExpression(courseSubjectBean.getFieldName(IAcademyAlias.COURSE_SUBJECT_ID), course.getCourseSubject().getId());
            description += ((CourseSubject)courseSubjectBean.getList(criteria).get(0)).getDescription() + " ";
            
            IManagerBean courseLevelBean = BeanManager.getManagerBean(CourseLevel.class);
            criteria = new Criteria();
            criteria.addEqualExpression(courseLevelBean.getFieldName(IAcademyAlias.COURSE_LEVEL_ID), course.getCourseLevel().getId());
            description += ((CourseLevel)courseLevelBean.getList(criteria).get(0)).getDescription() + " ";
        } catch (ManagerBeanException e) {
            e.printStackTrace();
        } finally {
            course.setDescription(description);
        }
    }
}