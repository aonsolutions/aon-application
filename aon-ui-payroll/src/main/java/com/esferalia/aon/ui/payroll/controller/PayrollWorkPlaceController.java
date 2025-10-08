package com.esferalia.aon.ui.payroll.controller;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.LinesController;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.EnterpriseCCC;
import com.esferalia.aon.payroll.PayrollWorkPlace;

public class PayrollWorkPlaceController extends LinesController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PayrollWorkPlaceController.class.getName());
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
//	@Override
//	public void load(ActionEvent event, Serializable workPlaceId) throws ManagerBeanException {
//		// FIXME necessary for gwt tree do not crash
//		// FIXME must change method called in com.esferalia.aon.gwt.payroll.bean.EmployeeTree.onWorkplaceSelected 
//		//       from load(...) to loadWorkPlace(...)
//		loadWorkPlace(event, workPlaceId);
//	}
//
//	private void loadWorkPlace(ActionEvent event, Serializable workPlaceId) throws ManagerBeanException {
//		PayrollWorkPlace pw = obtainPayrollWorkPlace(workPlaceId);
//		if(pw!=null){
//			super.load(event, pw.getId());
//		} else {
//			this.onReset(event);
//			((PayrollWorkPlace)this.getTo()).setWorkPlace(obtainWorkPlace(workPlaceId));
//		}
//	}
	
//	private PayrollWorkPlace obtainPayrollWorkPlace(Serializable workPlaceId) throws ManagerBeanException{
//		Criteria criteria = new Criteria();
//		criteria.addEqualExpression(this.getFieldName(IEntityAlias.PAYROLL_WORK_PLACE_WORK_PLACE_ID), workPlaceId);
//		List<ITransferObject> list = this.getManagerBean().getList(criteria);
//		if(!list.isEmpty()){
//			return (PayrollWorkPlace) list.get(0);
//		} 
//		return null;
//	}
//	
//	private WorkPlace obtainWorkPlace(Serializable workPlaceId) {
//		try {
//			IManagerBean bean = BeanManager.getManagerBean(WorkPlace.class);
//			return (WorkPlace) bean.get(workPlaceId);
//		} catch (ManagerBeanException e) {
//			String message = "Error al obtener el centro de trabajo";
//			AonUtil.addErrorMessage(message);
//			throw new AbortProcessingException(message, e);
//		}
//	}
	
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

	@Override
	protected void remove() {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);

			HibernateUtil.beginTransaction(sessionName);

			super.remove();
			
			HibernateUtil.getSession(sessionName).flush();
			HibernateUtil.commitTransaction(sessionName);
		} catch (Exception e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg = "Unable to rollback transaction!";
				LOGGER.error(msg, e);
			}
			String msg = "No se pudo borrar el centro de trabajo.";
			LOGGER.error(msg, e);
			throw new AbortProcessingException(msg, e);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}
	
	@Override
	public void onAccept(ActionEvent event) {
		try {
			super.accept(event);
			resetTo();
		} catch (Throwable e) {
			if (e instanceof AbortProcessingException ape) {
				throw ape;
			}
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
}
