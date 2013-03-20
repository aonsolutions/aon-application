package com.esferalia.aon.ui.payroll.controller;

import java.io.Serializable;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.company.WorkPlace;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.PayrollWorkPlace;

public class PayrollWorkPlaceController extends LinesController {
	
	@Override
	public void load(ActionEvent event, Serializable workPlaceId)
			throws ManagerBeanException {
		super.load(event, obtainPayrollWorkPlace(workPlaceId).getId());
	}
	
	private PayrollWorkPlace obtainPayrollWorkPlace(Serializable workPlaceId) throws ManagerBeanException{
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(this.getFieldName(IEntityAlias.PAYROLL_WORK_PLACE_WORK_PLACE_ID), workPlaceId);
		List<ITransferObject> list = this.getManagerBean().getList(criteria);
		if(!list.isEmpty()){
			return (PayrollWorkPlace) list.get(0);
		}
		return null;
	}
	
	@Override
	public void accept(ActionEvent event) {
		insertCurrentTOWorkPlace();
		super.accept(event);
	}
	
	private void insertCurrentTOWorkPlace() {
		Enterprise enterprise = (Enterprise) this.getMasterController().getTo();
		PayrollWorkPlace pw = (PayrollWorkPlace) this.getTo();
		WorkPlace workPlace = pw.getWorkPlace();
		workPlace.setEnterprise(enterprise);
		try {
			IManagerBean bean = BeanManager.getManagerBean(WorkPlace.class);
			bean.restoreNullSubPOJOs(workPlace);
			pw.setWorkPlace((WorkPlace) bean.insert(workPlace));
		} catch (ManagerBeanException e) {
			String message = "Error al crear el centro de trabajo";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message, e);
		}
	}
}
