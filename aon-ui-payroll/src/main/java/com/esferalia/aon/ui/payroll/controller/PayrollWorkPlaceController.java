package com.esferalia.aon.ui.payroll.controller;

import java.io.Serializable;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.AonVersion;
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
import com.esferalia.aon.payroll.EnterpriseCCC;
import com.esferalia.aon.payroll.PayrollWorkPlace;

public class PayrollWorkPlaceController extends LinesController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void load(ActionEvent event, Serializable workPlaceId)
			throws ManagerBeanException {
		// FIXME necessary for gwt tree do not crash
		// FIXME must change method called in com.esferalia.aon.gwt.payroll.bean.EmployeeTree.onWorkplaceSelected from load(...) to loadWorkPlace(...)
		loadWorkPlace(event, workPlaceId);
	}

	public void loadWorkPlace(ActionEvent event, Serializable workPlaceId)
			throws ManagerBeanException {
		PayrollWorkPlace pw = obtainPayrollWorkPlace(workPlaceId);
		if(pw!=null){
			super.load(event, pw.getId());
		} else {
			this.onReset(event);
			((PayrollWorkPlace)this.getTo()).setWorkPlace(obtainWorkPlace(workPlaceId));
		}
	}
	
	@Override
	public void accept(ActionEvent event) {
		PayrollWorkPlace pw = (PayrollWorkPlace)this.getTo();
		if(this.isNew() && (pw.getWorkPlace()!=null && pw.getWorkPlace().getId()==null)){
			insertCurrentTOWorkPlace();
		}
		super.accept(event);
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
	
	private WorkPlace obtainWorkPlace(Serializable workPlaceId) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(WorkPlace.class);
			return (WorkPlace) bean.get(workPlaceId);
		} catch (ManagerBeanException e) {
			String message = "Error al obtener el centro de trabajo";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message, e);
		}
	}
	
	public boolean isExistValidCCC() throws ManagerBeanException{
		PayrollWorkPlace pw = (PayrollWorkPlace) this.getTo();
		if(pw==null){
			pw = (PayrollWorkPlace) this.getModel().getRowData();
		}
		if(pw.getEnterpriseActivity()!=null && pw.getEnterpriseActivity().getId()!=null){
			IManagerBean bean = BeanManager.getManagerBean(EnterpriseCCC.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ENTERPRISE_CCC_ACTIVITY_ID), pw.getEnterpriseActivity().getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ENTERPRISE_CCC_GEOZONE_ID), pw.getWorkPlace().getAddress().getGeozone().getId());
			return bean.getCount(criteria)>0;
		}
		return false;
	}
	
}
