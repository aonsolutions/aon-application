package com.code.aon.ui.employee.controller;

import static com.code.aon.ui.employee.controller.IEmployeeConstants.PAYMENT_UPDATE_CONTROLLER;
import static com.code.aon.ui.employee.controller.IEmployeeConstants.SHOW_ENTERPRISE_IN_SEARCH;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Enterprise;
import com.code.aon.employee.Contract;
import com.code.aon.employee.ContractEvent;
import com.code.aon.employee.dao.IEmployeeAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.calculator.SalaryCalculatorContext;
import com.esferalia.aon.salary.calculator.SalaryCalculatorManager;
import com.esferalia.aon.salary.expression.IExpression;

public class PaymentUpdateController {
	
	private static String BEAN_NAME = "paymentUpdate";
	
	private Enterprise enterprise;
	private Month month;
	private int year;
	private DataModel model;
	private List<String> columns;

	public DataModel getModel() {
		return model;
	}
	public void setModel(DataModel model) {
		this.model = model;
	}

	public List<String> getColumns() {
		return columns;
	}
	public void setColumns(List<String> columns) {
		this.columns = columns;
	}
	
	public Enterprise getEnterprise() {
		return enterprise;
	}
	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}

	public Month getMonth() {
		return month;
	}
	public void setMonth(Month month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}

	public String getBeanName() {
		return BEAN_NAME; 
	}
	
	public void onEditSearch(ActionEvent event) {
		if ( AonUtil.isBeanValue(PAYMENT_UPDATE_CONTROLLER, SHOW_ENTERPRISE_IN_SEARCH) ) {
			setEnterprise(new Enterprise());	
		}
		Date date = new Date();
		setMonth(Month.getMonthByValue(CommonUtil.getMonth(date)));
		setYear(CommonUtil.getYear(date));
		setModel(null);
		setColumns(null);		
	}
	
	public void onSearch(ActionEvent event) {
		try {
			Calendar c = Calendar.getInstance();
			c.set(Calendar.YEAR, getYear());
			c.set(Calendar.MONTH, getMonth().ordinal());
			c.set(Calendar.DAY_OF_MONTH, 1);
			Date startDate = c.getTime();
			Date endDate = CommonUtil.getMonthLastDay(startDate);
			IManagerBean bean = BeanManager.getManagerBean(Contract.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEmployeeAlias.CONTRACT_WORK_PLACE_ENTERPRISE_ID), getEnterprise().getId());			
			criteria.addLessThanOrEqualExpression(bean.getFieldName(IEmployeeAlias.CONTRACT_START_DATE), endDate);			
			Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IEmployeeAlias.CONTRACT_END_DATE), startDate);
			Expression expr2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IEmployeeAlias.CONTRACT_END_DATE));
			criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
			List<ITransferObject> list = bean.getList(criteria);
			List<PaymentUpdate> dataList = new LinkedList<PaymentUpdate>();
			
			for (ITransferObject to: list) {
				Contract contract = (Contract) to;
				PaymentUpdate pu = new PaymentUpdate();
				pu.setContract(contract);
				dataList.add(pu);
			}
			populateList(dataList,startDate);
			setModel( new ListDataModel( dataList ) );
		} catch (SalaryException ex) {
			String msg = "Imposible realizar la búsqueda de los datos. [" + ex.getMessage()+"]";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,ex);
		} catch (ManagerBeanException ex) {
			String msg = "Imposible realizar la búsqueda de los datos. [" + ex.getMessage()+"]";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,ex);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void onSave(ActionEvent event) {
		// inicio transaccion
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			try {
				IManagerBean bean = BeanManager.getManagerBean(ContractEvent.class);
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
				// operaciones de la transaccion
				List<PaymentUpdate> dataList = (List<PaymentUpdate>) getModel().getWrappedData();
				for (PaymentUpdate pu: dataList) {
					for (ContractEvent ce: pu.getMap().values()) {
						if (ce.getExpression() != null && !"0".equals(ce.getExpression())) {
							bean.insertOrUpdate(ce);	
						} else {
							if (ce.getId() != null) {
								bean.remove(ce);	
							}
						}
					}
				}
				// FIN operaciones de la transaccion
				HibernateUtil.getSession(sessionName).flush();
				HibernateUtil.commitTransaction(sessionName);
				onSearch(event);
			} catch (Exception e) {
				try {
					HibernateUtil.rollbackTransaction(sessionName);
				} catch (DAOException daoe) {
					// Sigue...
				}
				String msg = "No se han podido guardar los datos. [" + e.getMessage()+"]";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			} finally {
				HibernateUtil.closeSession(sessionName);
			}
		} finally {
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}

	private void populateList(List<PaymentUpdate> dataList, Date date) throws SalaryException {
		setColumns( new LinkedList<String>());
		SalaryCalculatorManager factoryManager = SalaryCalculatorManager.getInstance();
		for (PaymentUpdate pu: dataList) {
			SalaryCalculatorContext scc = pu.getContract().getSalaryCalculatorContext();
			scc.setIssueDate(date);
			Date startDate= CommonUtil.getMonthFirstDay(date);
			scc.setStartDate(startDate);
			Date endDate = CommonUtil.getMonthLastDay(date);
			scc.setEndDate( endDate);
			factoryManager.getCalculator(scc);  // Fuerza a inicializar el contexto.
			List<IExpression> exps = scc.getExpressionContext().getExpressionVariables();
			for (IExpression exp: exps) {
				if (!exp.isReadOnly()) {
					if (!getColumns().contains(exp.getName())){
						getColumns().add(exp.getName());
					}
					ContractEvent ce = getContractEvent(pu,exp,scc);
					pu.getMap().put(exp.getName(),ce);
				}
			}
			Collections.sort( getColumns() ); 
		}
		
	}

	private ContractEvent getContractEvent(PaymentUpdate pu, IExpression exp, SalaryCalculatorContext scc) throws SalaryException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractEvent.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEmployeeAlias.CONTRACT_EVENT_CONTRACT_ID), pu.getContract().getId());			
			criteria.addEqualExpression(bean.getFieldName(IEmployeeAlias.CONTRACT_EVENT_NAME), exp.getName());
			criteria.addLessThanOrEqualExpression(bean.getFieldName(IEmployeeAlias.CONTRACT_EVENT_START_DATE), scc.getEndDate());			
			Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IEmployeeAlias.CONTRACT_EVENT_END_DATE), scc.getStartDate());
			Expression expr2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IEmployeeAlias.CONTRACT_EVENT_END_DATE));
			criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
			List<ITransferObject> list = bean.getList(criteria);
			ContractEvent ce = null;
			if (list != null && list.size()>0) {
				return (ContractEvent) list.get(0);
			}
			if (!exp.isReadOnly()) {
				ce = new ContractEvent();
				ce.setContract(pu.getContract());
				ce.setName(exp.getName());
				ce.setStartDate(scc.getStartDate());
				ce.setEndDate(scc.getEndDate());
				ce.setExpression("0");
			}
			return ce;
		} catch (ManagerBeanException ex) {
			throw new SalaryException(ex.getMessage(),ex);
		}
	}

	public class PaymentUpdate {
		private Contract contract;
		private Map<String,ContractEvent> map;
		
		public Contract getContract() {
			return contract;
		}
		public void setContract(Contract contract) {
			this.contract = contract;
		}
		
		public Map<String, ContractEvent> getMap() {
			if (map == null) {
				setMap(new HashMap<String, ContractEvent>());
			}
			return map;
		}
		public void setMap(Map<String, ContractEvent> map) {
			this.map = map;
		}
	}
	
	
	public void onSalaryDraft(ActionEvent event) {
		try {
			PaymentUpdate pu = (PaymentUpdate) getModel().getRowData();
			SalaryDraftController controller = (SalaryDraftController) FormUtil.getController("salaryDraft");
			Calendar c = Calendar.getInstance();
			c.set(Calendar.YEAR, getYear());
			c.set(Calendar.MONTH, getMonth().ordinal());
			c.set(Calendar.DAY_OF_MONTH, 1);
			controller.setIssueDate(c.getTime());
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(controller.getManagerBean().getFieldName(IEmployeeAlias.CONTRACT_ID), pu.getContract().getId());
			controller.setCriteria(criteria);
			controller.onSearch(null);
			controller.getModel().setRowIndex(0);
			controller.onSelect(null);
			controller.setBackAction("paymentUpdate_list");
		} catch (ManagerBeanException e) {
			String msg = "Error al el borrador de la nómina.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
}
