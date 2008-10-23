package com.code.aon.ui.config.controller;

import java.util.Iterator;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.dao.ICompanyAlias;
import com.code.aon.config.Series;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;

public class SeriesController extends BasicController {

	@SuppressWarnings("unchecked")
	public void onWorkPlaceChanged(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			IManagerBean workplaceBean = BeanManager.getManagerBean(WorkPlace.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(workplaceBean.getFieldName(ICompanyAlias.WORK_PLACE_ID), event.getNewValue());
			Iterator iter = workplaceBean.getList(criteria).iterator();
			if(iter.hasNext()){
				WorkPlace workPlace = (WorkPlace)iter.next();
				((Series)this.getTo()).setWorkPlace(workPlace);
			}
		}
	}
}