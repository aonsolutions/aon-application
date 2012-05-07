package com.esferalia.aon.ui.payroll.controller.launcher;


import java.util.ArrayList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryCost;
import com.esferalia.aon.payroll.SalaryDeduction;
import com.esferalia.aon.payroll.SalaryEmbargo;
import com.esferalia.aon.payroll.SalaryPayment;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;

public class SalaryRemoverController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SalaryRemoverController.class.getName());
	
	private DataModel model;
	private SalaryType salaryType;
	private List<Salary> selectedSalaries;
	
	public List<Salary> getSelectedSalaries(){
		return selectedSalaries;
	}
	public void setSelectedSalaries(List<Salary> selectedSalaries) {
		this.selectedSalaries = selectedSalaries;
	}
	
	public DataModel getModel() {
		if (model == null) {
			model = new ListDataModel();
		}
		return model;
	}
	public void setModel(DataModel model) {
		this.model = model;
	}
	public SalaryType getSalaryType() {
		return salaryType;
	}
	public void setSalaryType(SalaryType salaryType) {
		this.salaryType = salaryType;
	}
	private List<SelectableSalary> transformList(List<ITransferObject> list) {
		List<SelectableSalary> rList = new ArrayList<SelectableSalary>();
		for (ITransferObject to : list) {
			SelectableSalary r = new SelectableSalary();
			r.setSalary((Salary) to);
			rList.add(r);
		}
		return rList;
	}
	
	public void initializeModel(List<ITransferObject> list) {
		setModel(new ListDataModel(transformList(list)));
	}

	/*
	 * ACTION LISTENER
	 */
	
	public void onInitialize(ActionEvent event) {
		setSalaryType(null);
	}
	
	public void onSearch(ActionEvent event) {
		setModel(null);
		try {
			IController controller = FormUtil.getController(IPayrollConstants.SALARY_CONTROLLER);
			if(getSalaryType()!=null){
				controller.getCriteria().addEqualExpression(controller.getFieldName(IEntityAlias.SALARY_TYPE), getSalaryType());
			}
			controller.getCriteria().addOrder(controller.getFieldName(IEntityAlias.SALARY_CONTRACT_WORK_PLACE_ENTERPRISE_ID));
			controller.getCriteria().addOrder(controller.getFieldName(IEntityAlias.SALARY_EMPLOYEE_NAME));
			controller.onSearch(event);
			initializeModel(controller.getManagerBean().getList(controller.getCriteria()));
		} catch (ManagerBeanException e) {
			String msg = "No se pudieron buscar las nóminas";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}
	
	public void onSelectAll(ActionEvent event) {
		processAll(true);
	}

	public void onDeselectAll(ActionEvent event) {
		processAll(false);
	}

	private void processAll(boolean selected) {
		for (int i = 0; i < getModel().getRowCount(); i++) {
			getModel().setRowIndex(i);
			SelectableSalary r = (SelectableSalary) getModel().getRowData();
			r.setSelected(selected);
		}
	}
	
	private void buildSelectedSalaries(){
		if(getSelectedSalaries()==null){
			setSelectedSalaries(new ArrayList<Salary>());
			for (int i = 0; i < getModel().getRowCount(); i++) {
				getModel().setRowIndex(i);
				SelectableSalary s = (SelectableSalary) getModel().getRowData();
				if(s.isSelected()){
					getSelectedSalaries().add(s.getSalary());
				}
			}
		}
	}
	
	public void onRemoveSelected(ActionEvent event){
		removeSelected();
		onSearch(event);
	}
	
	public void removeSelected(){
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
				// BEGIN operaciones de la transaccion
				buildSelectedSalaries();
				IManagerBean bean = BeanManager.getManagerBean(Salary.class);
				for(Salary s: getSelectedSalaries()){
					removePayment(s);
					removeDeduction(s);
					removeCost(s);
					removeEmbargo(s);
					bean.remove(s);
				}
				setSelectedSalaries(null);
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
				throw new AbortProcessingException(e);
			} finally {
				HibernateUtil.closeSession(sessionName);
			}
		} finally {
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}
	
	private void removePayment(Salary salary) throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(SalaryPayment.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_PAYMENT_SALARY_ID), salary.getId());
		for(ITransferObject to: bean.getList(criteria)){
			bean.remove(to);
		}
	}
	
	private void removeDeduction(Salary salary) throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(SalaryDeduction.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_DEDUCTION_SALARY_ID), salary.getId());
		for(ITransferObject to: bean.getList(criteria)){
			bean.remove(to);
		}
	}
	
	private void removeCost(Salary salary) throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(SalaryCost.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_COST_SALARY_ID), salary.getId());
		for(ITransferObject to: bean.getList(criteria)){
			bean.remove(to);
		}
	}
	
	private void removeEmbargo(Salary salary) throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(SalaryEmbargo.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_EMBARGO_SALARY_ID), salary.getId());
		for(ITransferObject to: bean.getList(criteria)){
			bean.remove(to);
		}
	}
	
	
	/*
	 * INNER CLASS
	 */
	public class SelectableSalary{
		private boolean selected;
		private Salary salary;
		
		public boolean isSelected() {
			return selected;
		}
		public void setSelected(boolean selected) {
			this.selected = selected;
		}
		public Salary getSalary() {
			return salary;
		}
		public void setSalary(Salary salary) {
			this.salary = salary;
		}
	}
	
}
