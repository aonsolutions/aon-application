package com.code.aon.ui.cms.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private final static Logger LOGGER = LoggerFactory.getLogger(HiruCourseControllerListener.class);
	
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
			LOGGER.error(th.getMessage(), th);
		}
	}

}
