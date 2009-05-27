package com.code.aon.ui.cms.event;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.cms.HiruCourse;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.controller.HiruCourseController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class HiruCourseControllerListener extends ControllerAdapter {

	private static final Logger LOGGER = Logger.getLogger(HiruCourseControllerListener.class.getName());
	
	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		HiruCourseController controller = (HiruCourseController)event.getController(); 
		try{
			Criteria criteria = controller.getCriteria();
			IManagerBean bean = BeanManager.getManagerBean(HiruCourse.class);
			controller.completeCriteria();
			criteria.addOrder(bean.getFieldName(ICMSAlias.HIRU_COURSE_ACTIVE));
			criteria.addOrder(bean.getFieldName(ICMSAlias.HIRU_COURSE_ALIAS));
		}catch (Throwable th) {
			LOGGER.log(Level.SEVERE, th.getMessage(), th);
		}
	}

}
