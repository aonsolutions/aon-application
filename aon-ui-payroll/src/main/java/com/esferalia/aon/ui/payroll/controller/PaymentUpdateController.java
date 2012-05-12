package com.esferalia.aon.ui.payroll.controller;

import static com.esferalia.aon.ui.payroll.controller.IPayrollConstants.PAYMENT_UPDATE_CONTROLLER;
import static com.esferalia.aon.ui.payroll.controller.IPayrollConstants.SHOW_ENTERPRISE_IN_SEARCH;

import java.util.Calendar;
import java.util.Collection;
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

import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Enterprise;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.calculator.ISalaryCalculatorContext;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.IExpression;
import com.esferalia.aon.ui.payroll.controller.salary.draft.SalaryDraftController;

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
		try {
			if ( AonUtil.isBeanValue(PAYMENT_UPDATE_CONTROLLER, SHOW_ENTERPRISE_IN_SEARCH) ) {
				IManagerBean bean = BeanManager.getManagerBean(Enterprise.class);
				setEnterprise((Enterprise) bean.createNewTo());
			} else {
				IManagerBean bean = BeanManager.getManagerBean(Enterprise.class);
				setEnterprise((Enterprise) bean.get(UserUtils.getInstance().getLoggedUser().getEnterprise()));
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible realizar la búsqueda de los datos. [" + e.getMessage()+"]";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
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
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_WORK_PLACE_ENTERPRISE_ID), getEnterprise().getId());			
			criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_START_DATE), endDate);			
			Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_END_DATE), startDate);
			Expression expr2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IEntityAlias.CONTRACT_END_DATE));
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
				IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
				// operaciones de la transaccion
				List<PaymentUpdate> dataList = (List<PaymentUpdate>) getModel().getWrappedData();
				for (PaymentUpdate pu: dataList) {
					for (ContractData ce: pu.getMap().values()) {
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
		for (PaymentUpdate pu: dataList) {
			IContractSalaryCalculatorContext scc = (IContractSalaryCalculatorContext) pu.getContract().getSalaryCalculatorContext(CommonUtil.getYear(date),Month.getMonthByValue(CommonUtil.getMonth(date)),SalaryType.SALARY);
//			List<IExpression> exps = scc.getExpressionContext().getExpressionVariables();
			// TODO ¿a donde hay que ir a buscar las variables?
			Collection<IContractPayment> exps = null;
			try {
				exps = scc.getContractPayments();
			} catch (AonException e) {
				// sigue...
			}
			
			for (IExpression exp: exps) {
				try {
					if (!exp.isReadOnly()) {
						if (!getColumns().contains(exp.getName())){
							getColumns().add(exp.getName());
						}
						ContractData ce = getContractContext(pu,exp,scc);
						pu.getMap().put(exp.getName(),ce);
					}
				} catch (Exception e) {
					// sigue...
				}
			}
			Collections.sort( getColumns() ); 
		}
	}

	private ContractData getContractContext(PaymentUpdate pu, IExpression exp, ISalaryCalculatorContext scc) throws SalaryException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_CONTRACT_ID), pu.getContract().getId());			
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_NAME), exp.getName());
			criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_START_DATE), scc.getEndDate());			
			Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_END_DATE), scc.getStartDate());
			Expression expr2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_END_DATE));
			criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
			List<ITransferObject> list = bean.getList(criteria);
			ContractData ce = null;
			if (list != null && list.size()>0) {
				return (ContractData) list.get(0);
			}
			if (!exp.isReadOnly()) {
				ce = new ContractData();
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
		private Map<String,ContractData> map;
		
		public Contract getContract() {
			return contract;
		}
		public void setContract(Contract contract) {
			this.contract = contract;
		}
		
		public Map<String, ContractData> getMap() {
			if (map == null) {
				setMap(new HashMap<String, ContractData>());
			}
			return map;
		}
		public void setMap(Map<String, ContractData> map) {
			this.map = map;
		}
	}
	
	
	public void onSalaryDraft(ActionEvent event) {
		try {
			PaymentUpdate pu = (PaymentUpdate) getModel().getRowData();
			SalaryDraftController controller = (SalaryDraftController) FormUtil.getController("salaryDraft");
			controller.setYear(getYear());
			controller.setMonth(getMonth());
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(controller.getManagerBean().getFieldName(IEntityAlias.CONTRACT_ID), pu.getContract().getId());
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
