package com.code.aon.ui.registry.controller;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.registry.RegistrySegment;
import com.code.aon.registry.Segment;
import com.code.aon.ui.form.LinesController;

public class RegistrySegmentController extends LinesController {

	public void segmentData(ValueChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			IManagerBean segmentBean = BeanManager.getManagerBean(Segment.class);
			Segment segment = (Segment)segmentBean.get((Integer)event.getNewValue());
			((RegistrySegment)getTo()).setSegment(segment);
		}
	}

}