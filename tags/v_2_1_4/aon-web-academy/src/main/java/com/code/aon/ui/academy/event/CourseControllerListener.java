package com.code.aon.ui.academy.event;

import java.util.Iterator;
import java.util.List;

import com.code.aon.academy.AcademicSkill;
import com.code.aon.academy.Course;
import com.code.aon.academy.CourseAcademicSkill;
import com.code.aon.academy.dao.IAcademyAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.academy.controller.CourseAcademicSkillController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class CourseControllerListener extends ControllerAdapter {

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			Criteria criteria = event.getController().getCriteria();
			criteria.addOrder(event.getController().getManagerBean().getFieldName(IAcademyAlias.COURSE_CODE));
			criteria.addOrder(event.getController().getManagerBean().getFieldName(IAcademyAlias.COURSE_START_DATE),false);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		try {
			IManagerBean courseAcademicSkillBean = BeanManager.getManagerBean(CourseAcademicSkill.class);
			Course course = (Course)event.getController().getTo();
			IManagerBean academikSkillBean = BeanManager.getManagerBean(AcademicSkill.class);
			Criteria criteriaAcademicSkill = new Criteria();
			criteriaAcademicSkill.addOrder(academikSkillBean.getFieldName(IAcademyAlias.ACADEMIC_SKILL_CODE));
			List<ITransferObject> academicSkillList = academikSkillBean.getList(criteriaAcademicSkill);
			Iterator<ITransferObject> iter = academicSkillList.iterator();
			while (iter.hasNext()){
				AcademicSkill academicSkill = (AcademicSkill)iter.next();
				CourseAcademicSkill courseAcademicSkill = new CourseAcademicSkill();
				courseAcademicSkill.setAcademicSkill(academicSkill);
				courseAcademicSkill.setCourse(course);
				courseAcademicSkillBean.insert(courseAcademicSkill);
			}
			CourseAcademicSkillController courseAcademicSkillController = (CourseAcademicSkillController)AonUtil.getController("courseAcademicSkill");
			courseAcademicSkillController.onSearch(null);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
}
