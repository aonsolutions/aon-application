package com.code.aon.ui.academy.event;

import java.util.List;

import com.code.aon.academy.CourseAlumn;
import com.code.aon.academy.enumeration.CourseAlumnStatus;
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.customer.Customer;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.customer.controller.CustomerController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class AlumnControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		CustomerController controller = (CustomerController) this.getController();
		try {
			controller.setCourseAlumnCount( obtainCourseAlumnList().size() );
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException();
		}
	}
	
	@Override
	public void afterBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		CustomerController controller = (CustomerController) this.getController();
		Customer customer = (Customer)controller.getTo();
		if(customer.getStatus() == CustomerStatus.INACTIVE && controller.isUpdateCourseAlumn()){
			try {
				updateAlumnCourse();
			} catch (ManagerBeanException e) {
				throw new ControllerListenerException();
			}
		}
	}
	
	private List<ITransferObject> obtainCourseAlumnList() throws ManagerBeanException{
		Customer customer = (Customer) this.getController().getTo();
		IManagerBean bean = BeanManager.getManagerBean(CourseAlumn.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.COURSE_ALUMN_CUSTOMER_ID), customer.getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.COURSE_ALUMN_STATUS), CourseAlumnStatus.ACTIVE);
		return bean.getList(criteria);
	}
	
	private void updateAlumnCourse() throws ManagerBeanException {
		// inicio de la transaccion
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
				// BEGIN operaciones de la transaccion
				IManagerBean bean = BeanManager.getManagerBean(CourseAlumn.class);
				List<ITransferObject> list = obtainCourseAlumnList();
				for(ITransferObject to: list){
					CourseAlumn ca = (CourseAlumn) to;
					ca.setStatus(CourseAlumnStatus.INACTIVE);
					bean.update(ca);
				}
				// FIN operaciones de la transaccion
				HibernateUtil.getSession(sessionName).flush();
				HibernateUtil.commitTransaction(sessionName);
			} catch (Exception e) {
				String msg = e.getMessage();
				try {
					HibernateUtil.rollbackTransaction(sessionName);
				} catch (DAOException daoe) {
					msg = "Unable to rollback transaction! (" + msg + ")";
				}
				AonUtil.addErrorMessage(msg);
			} finally {
				HibernateUtil.closeSession(sessionName);
			}
		} finally {
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}
	
}
