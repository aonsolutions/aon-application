package com.esferalia.aon.ui.pms.controller;

import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.ProjectReservation;



public class ReservationInvoicePrinter {
	
	public ReservationInvoicePrinter getInstance() {
		return new ReservationInvoicePrinter();
	}
	
	public ProjectReservation getProjectReservation(Integer projectId) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ProjectReservation.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROJECT_RESERVATION_PROJECT_ID), projectId);
		List<ITransferObject> list = bean.getList(criteria);
		if(!list.isEmpty()){
			return ((ProjectReservation)list.get(0));
		}
		return null;
	}


}