package com.esferalia.aon.ui.payroll.controller;

import static com.esferalia.aon.ui.payroll.controller.IPayrollConstants.PAYMENT_UPDATE_CONTROLLER;
import static com.esferalia.aon.ui.payroll.controller.IPayrollConstants.SHOW_ENTERPRISE_IN_SEARCH;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Enterprise;
import com.code.aon.company.WorkPlace;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.AgreementData;
import com.esferalia.aon.payroll.AgreementLevelData;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.IVariableData;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.ui.payroll.controller.salary.draft.SalaryDraftController;
import com.esferalia.aon.ui.payroll.utils.PayrollUtils;

public class PaymentUpdateController {
	
	private static String BEAN_NAME = "paymentUpdate";
	
	private Enterprise enterprise;
	private WorkPlace workPlace;
	private Month month;
	private int year;
	private DataModel model;
	private List<String> columns;
	private String selectedVariable;
	private PayrollUtils utils;

	
	public PayrollUtils getUtils() {
		if(utils==null){
			utils = new PayrollUtils();
		}
		return utils;
	}
	public void setUtils(PayrollUtils utils) {
		this.utils = utils;
	}
	public String getSelectedVariable() {
		return selectedVariable;
	}
	public void setSelectedVariable(String selectedVariable) {
		this.selectedVariable = selectedVariable;
	}
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
	
	public WorkPlace getWorkPlace() {
		return workPlace;
	}
	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
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

	public String searchAction() {
		return "paymentUpdate_search";
	}
	public String getBeanName() {
		return BEAN_NAME; 
	}
	public List<SelectItem> getAvailableVariables(){
		List<SelectItem> list = new LinkedList<SelectItem>();
		for(String name: getColumns()){
			SelectItem item = new SelectItem(name, name);
			list.add(item);			
		}
		return list;
	}
	
	public List<SelectItem> getWorkPlaces() throws ManagerBeanException {
		IManagerBean workPlaceBean = BeanManager.getManagerBean(WorkPlace.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(workPlaceBean.getFieldName(IEntityAlias.WORK_PLACE_ACTIVE), new Boolean(true));
		criteria.addEqualExpression(workPlaceBean.getFieldName(IEntityAlias.WORK_PLACE_ENTERPRISE_ID), getEnterprise().getId());
		UserUtils.getInstance().addScopeFilterToCriteria(criteria, workPlaceBean.getFieldName(IEntityAlias.WORK_PLACE_SCOPE_ID));
		criteria.addOrder(workPlaceBean.getFieldName(IEntityAlias.WORK_PLACE_DESCRIPTION));
		List<SelectItem> workPlaces = new LinkedList<SelectItem>();
    	for(ITransferObject to: workPlaceBean.getList(criteria)){
    		WorkPlace workPlace = (WorkPlace)to;
    		workPlaces.add(new SelectItem(workPlace, workPlace.getDescription()));
    	}
		return workPlaces;
	}	
	
	public List<ITransferObject> getCurrentUserWorkPlaceList() throws ManagerBeanException {
		IManagerBean workPlaceBean = BeanManager.getManagerBean(WorkPlace.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(workPlaceBean.getFieldName(IEntityAlias.WORK_PLACE_ACTIVE), new Boolean(true));
		UserUtils.getInstance().addScopeFilterToCriteria(criteria, workPlaceBean.getFieldName(IEntityAlias.WORK_PLACE_SCOPE_ID));
		criteria.addOrder(workPlaceBean.getFieldName(IEntityAlias.WORK_PLACE_DESCRIPTION));
		return workPlaceBean.getList(criteria);
	}
	
	private void populateList(List<PaymentUpdate> dataList, Date date) throws SalaryException {
		setColumns( new LinkedList<String>());
		for (PaymentUpdate pu: dataList) {
			Calendar startCal = Calendar.getInstance();
			startCal.setTime(date);
			startCal.set(Calendar.DAY_OF_MONTH, startCal.getActualMinimum(Calendar.DAY_OF_MONTH));
			Calendar endCal = Calendar.getInstance();
			endCal.setTime(date);
			endCal.set(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			
			Set<String> var = getUtils().getContractVariableList(pu.getContract(), startCal.getTime(),endCal.getTime());
			for(String name: var){
				try {
					if (!getColumns().contains(name)){
						getColumns().add(name);
					}
					IVariableData ce = getContractContext(pu,name,startCal.getTime(),endCal.getTime());
					pu.getMap().put(name,ce);
				} catch (Exception e) {
					// sigue...
				}
			}
			Collections.sort( getColumns() ); 
		}
	}
	
	private IVariableData getContractContext(PaymentUpdate pu, String name, Date startDate, Date endDate) throws SalaryException {
		try {
			List<ITransferObject> list = getUtils().getContractDataList(pu.getContract(), startDate, endDate, name); 
			if (list != null && list.size()>0) {
				return (ContractData) list.get(0);
			}
			list = getUtils().getAgreementLevelDataList(pu.getContract(), startDate, endDate, name);
			if (list != null && list.size()>0) {
				return (AgreementLevelData) list.get(0);
			}
			list = getUtils().getAgreementDataList(pu.getContract(), startDate, endDate, name);
			if (list != null && list.size()>0) {
				return (AgreementData) list.get(0);
			}
			ContractData cd = null;
			cd = new ContractData();
			cd.setContract(pu.getContract());
			cd.setName(name);
			cd.setStartDate(startDate);
			cd.setEndDate(endDate);
			cd.setExpression("0");
			return cd;
		} catch (ManagerBeanException ex) {
			throw new SalaryException(ex.getMessage(),ex);
		}
	}
	
	public void onEditSearch(ActionEvent event) {
		try {
			if ( AonUtil.isBeanValue(PAYMENT_UPDATE_CONTROLLER, SHOW_ENTERPRISE_IN_SEARCH) ) {
				CompanyCollectionsController collections = new CompanyCollectionsController();
				if(collections.getCurrentUserEnterprisesCount()==1){
					setEnterprise((Enterprise) collections.getCurrentUserEnterprises().get(0).getValue());
				} else {
					IManagerBean bean = BeanManager.getManagerBean(Enterprise.class);
					setEnterprise((Enterprise) bean.createNewTo());
				}
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
		setWorkPlace(null);
		setMonth(Month.getMonthByValue(CommonUtil.getMonth(date)));
		setYear(CommonUtil.getYear(date));
		setModel(null);
		setColumns(null);	
		setSelectedVariable(null);
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
			if(getWorkPlace()!=null && getWorkPlace().getId()!=null){
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_WORK_PLACE_ID), getWorkPlace().getId());			
			} else {
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_WORK_PLACE_ENTERPRISE_ID), getEnterprise().getId());			
			}
			criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_START_DATE), endDate);			
			Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_END_DATE), startDate);
			Expression expr2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IEntityAlias.CONTRACT_END_DATE));
			criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
			criteria.addOrder(bean.getFieldName(IEntityAlias.CONTRACT_PERSON_FIRST_SURNAME));
			criteria.addOrder(bean.getFieldName(IEntityAlias.CONTRACT_PERSON_SECOND_SURNAME));
			criteria.addOrder(bean.getFieldName(IEntityAlias.CONTRACT_PERSON_REGISTRY_NAME));
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
					for(IVariableData var: pu.getMap().values()){
						if (var.getExpression() != null && !"0".equals(var.getExpression())) {
							if(var.getScope()!=ExpressionScope.CONTRACT){
								var = createContractData(pu.getContract(), var);
							}
							bean.insertOrUpdate((ContractData)var);	
						} else {
							if (var.getId() != null) {
								bean.remove((ContractData)var);	
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

	private IVariableData createContractData(Contract contract, IVariableData var) {
		ContractData data = new ContractData();
		data.setContract(contract);
		data.setStartDate(var.getStartDate());
		data.setEndDate(var.getEndDate());
		data.setName(var.getName());
		data.setExpression(var.getExpression());
		return data;
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

	public class PaymentUpdate {
		private Contract contract;
		private Map<String,IVariableData> map;
		
		public Contract getContract() {
			return contract;
		}
		public void setContract(Contract contract) {
			this.contract = contract;
		}
		
		public Map<String, IVariableData> getMap() {
			if (map == null) {
				setMap(new HashMap<String, IVariableData>());
			}
			return map;
		}
		public void setMap(Map<String, IVariableData> map) {
			this.map = map;
		}
	}
	
	
}
