package com.esferalia.aon.ui.payroll.controller;

import java.util.ArrayList;
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
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.AgreementData;
import com.esferalia.aon.payroll.AgreementLevelData;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.IVariableData;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculatorContext;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedObject;
import com.esferalia.aon.salary.expression.UndefinedVariablesException;
import com.esferalia.aon.ui.payroll.controller.salary.draft.SalaryDraftController;
import com.esferalia.aon.ui.payroll.utils.PayrollUtils;

public class PaymentUpdateController {

	private Enterprise enterprise;
	private WorkPlace workPlace;
	private Contract contract;
	private Month month;
	private int year;
	private DataModel model;
	private List<String> variables;
	private String selectedVariable;
	private PayrollUtils utils;
	private ViewType viewType;

	
	public ViewType getViewType() {
		return viewType;
	}
	public void setViewType(ViewType viewType) {
		this.viewType = viewType;
	}
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

	public List<String> getVariables() {
		return variables;
	}
	public void setVariables(List<String> variables) {
		this.variables = variables;
	}
	
	public Contract getContract() {
		return contract;
	}
	public void setContract(Contract contract) {
		this.contract = contract;
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
	public Date getStartDate(){
		Calendar startCal = Calendar.getInstance();
		startCal.set(Calendar.YEAR, getYear());
		startCal.set(Calendar.MONTH, getMonth().ordinal());
		startCal.set(Calendar.DAY_OF_MONTH, startCal.getActualMinimum(Calendar.DAY_OF_MONTH));
		return startCal.getTime();
	}
	public Date getEndDate(){
		Calendar endCal = Calendar.getInstance();
		endCal.set(Calendar.YEAR, getYear());
		endCal.set(Calendar.MONTH, getMonth().ordinal());
		endCal.set(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));;
		return endCal.getTime();
	}

	public String searchAction() {
		return "paymentUpdate_search";
	}
	public String getBeanName() {
		return IPayrollConstants.PAYMENT_UPDATE_CONTROLLER; 
	}
	
	public boolean isEmployeeView(){
		return getViewType()==ViewType.EMPLOYEE;
	}
	public boolean isVariableView(){
		return getViewType()==ViewType.VARIABLE;
	}
	
	public List<SelectItem> getViewTypes(){
		List<SelectItem> list = new LinkedList<SelectItem>();
		SelectItem item = new SelectItem(ViewType.EMPLOYEE, "Trabajadores");
		list.add(item);			
		item = new SelectItem(ViewType.VARIABLE, "Variables");
		list.add(item);			
		return list;
	}
	
	public void searchAvailableVariables(){		
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		String select = "" + 
			"select cp.expression as 'Payment', pc.expression as 'Concept', 'CP' as 'Source' " +
			"from contract_payment as cp left join payment_concept as pc on cp.payment_concept = pc.id, contract as c, workplace as wp " +
			"where " + getVariableFilterClause(true) +
			"and cp.start_date <= :end " +
			"and ( cp.end_date >= :start OR isnull(cp.end_date) ) " +
			"UNION " +
			"select ap.expression as 'Payment', pc.expression as 'Concept', 'AP' as 'Source' " +
			"from agreement_payment as ap left join payment_concept as pc on ap.payment_concept = pc.id " +
			"where ap.agreement in ( " +
			"	select distinct a.id " +
			"	from agreement as a, workplace as wp, contract as c, agreement_level_category as alc, agreement_level as al " +
			"	where " + getVariableFilterClause(false) +
			"	and al.agreement = a.id " +
			"	and alc.agreement_level = al.id " +
			"	and c.agreement_level_category = alc.id " +
			"   and c.start_date <= :end " +
			"	and ( c.end_date >= :start OR isnull(c.end_date) ) ) " +
			"and ap.start_date <= :end " +
			"and ( ap.end_date >= :start OR isnull(ap.end_date) ) "
			;
		
		Query sqlQuery = session.createSQLQuery(select);
		if (getContract() != null && getContract().getId() != null) {
			sqlQuery.setInteger("contract", getContract().getId());
		} else if (getWorkPlace() != null && getWorkPlace().getId() != null) {
			sqlQuery.setInteger("workplace", getWorkPlace().getId());
		} else if (getEnterprise() != null && getEnterprise().getId() != null) {
			sqlQuery.setInteger("enterprise", getEnterprise().getId());
		}
		
		sqlQuery.setDate("start", getStartDate());
		sqlQuery.setDate("end", getEndDate());
		List<?> list = sqlQuery.list();
		
		setVariables( new LinkedList<String>() );
		for(Object o: list){
			String var = (String) ((Object[])o)[0];
			if(StringUtils.isBlank(var)){
				var = (String) ((Object[])o)[1];
			}
			Set<String> vars = ExpressionContext.getVariables(var);
			for(String name: vars) {
				if (!getVariables().contains(name)) {
					getVariables().add(name);
				}
			}
		}
		Collections.sort( getVariables() ); 
		
	}
	
	private String getVariableFilterClause(boolean isContractFilter) {
		String clause = "";
		if (getContract() != null && getContract().getId() != null) {
			clause = (isContractFilter ? " cp.contract" : " c.id") + " = :contract ";
		} else if (getWorkPlace() != null && getWorkPlace().getId() != null) {
			clause += isContractFilter ? " cp.contract = c.id and " : "";
			clause += " c.workplace = :workplace ";
		} else if (getEnterprise() != null && getEnterprise().getId() != null) {
			clause += isContractFilter ? " cp.contract = c.id and " : "";
			clause += " c.workplace = wp.id ";
			clause += " and wp.enterprise = :enterprise ";
		}
		return clause;
	}

	public List<SelectItem> getAvailableVariables(){
		List<SelectItem> list = new LinkedList<SelectItem>();
		if( getEnterprise()!=null && getEnterprise().getId()!=null ){
			searchAvailableVariables();
			if( getVariables()!=null ){
				for(String name: getVariables()){
					SelectItem item = new SelectItem(name, name);
					list.add(item);			
				}
			}
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
	
	private void populateList(List<PaymentUpdate> dataList, Date date) throws SalaryException {
		for (PaymentUpdate pu: dataList) {
			Calendar startCal = Calendar.getInstance();
			startCal.setTime(date);
			startCal.set(Calendar.HOUR_OF_DAY, 0);
			startCal.set(Calendar.DAY_OF_MONTH, startCal.getActualMinimum(Calendar.DAY_OF_MONTH));
			Calendar endCal = Calendar.getInstance();
			endCal.setTime(date);
			endCal.set(Calendar.HOUR_OF_DAY, 0);
			endCal.set(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			
			Set<String> var = getUtils().getContractVariableList(pu.getContract(), startCal.getTime(),endCal.getTime());
			for(String name: var) {
				try {
					if( getSelectedVariable()==null || (getSelectedVariable()!=null && getSelectedVariable().equals(name)) ){
						ContractData ce = getContractContext(pu.getContract(),name,startCal.getTime(),endCal.getTime());
						pu.getMap().put(name,ce);
					}
				} catch (Exception e) {
					// sigue...
				}
			}
		}
	}
	
	private ContractData getContractContext(Contract contract, String name, Date startDate, Date endDate) throws SalaryException {
		ContractData cd = new ContractData();
		try {
			cd.setContract(contract);
			cd.setName(name);
			cd.setStartDate(null);
			cd.setEndDate(null);
			
			List<ITransferObject> list = getUtils().getContractDataList(contract, startDate, endDate, name); 
			if (list != null && list.size()>0) {
				ContractData data = (ContractData) list.get(0);
				cd.setExpression(data.getExpression());
				cd.setStartDate(data.getStartDate());
				cd.setEndDate(data.getEndDate());
				return cd;
			}
			
			list = getUtils().getAgreementLevelDataList(contract, startDate, endDate, name);
			if (list != null && list.size()>0) {
				AgreementLevelData data = (AgreementLevelData) list.get(0);
				cd.setExpression(data.getExpression());
				cd.setStartDate(data.getStartDate());
				cd.setEndDate(data.getEndDate());
				return cd;
			}
			list = getUtils().getAgreementDataList(contract, startDate, endDate, name);
			if (list != null && list.size()>0) {
				AgreementData data = (AgreementData) list.get(0);
				cd.setExpression(data.getExpression());
				cd.setStartDate(data.getStartDate());
				cd.setEndDate(data.getEndDate());
				return cd;
			}

			ContractSalaryCalculatorContext ctx = (ContractSalaryCalculatorContext) contract.getSalaryCalculatorContext(startDate, endDate, SalaryType.SALARY);
			List<ITimedObject<Object>> resultList = ctx.getExpressionContext().eval(name, startDate, endDate);
			if( !resultList.isEmpty() && resultList.get(0).getValue()!=null ){
				cd.setExpression( resultList.get(0).getValue().toString() );
				return cd;
			}
			cd.setExpression("");
			return cd;
		} catch (ManagerBeanException ex) {
			throw new SalaryException(ex.getMessage(),ex);
		} catch (UndefinedVariablesException e) {
			cd.setExpression("");
			return cd;
		} catch (ExpressionException e) {
			throw new SalaryException(e.getMessage(),e);
		}
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PaymentUpdateController.class);
	private IControllerListener contractFilter;
	
	public IControllerListener getContractFilter() {
		if ( this.contractFilter == null ) {
			this.contractFilter = new ControllerAdapter() {
				@Override
				public void beforeModelSearched(ControllerEvent event)
						throws ControllerListenerException {
					updateContractCriteria();
					IController controller = event.getController();
					try {					
						controller.setCriteria(criteria);
//						if( getWorkPlace()!=null && getWorkPlace().getId()!=null ){
//							controller.getCriteria().addEqualExpression(controller.getFieldName(IEntityAlias.CONTRACT_WORK_PLACE_ID), getWorkPlace().getId());
//						} else {
//							controller.getCriteria().addEqualExpression(controller.getFieldName(IEntityAlias.CONTRACT_WORK_PLACE_ENTERPRISE_ID), getEnterprise().getId());			
//						}
					} catch (ManagerBeanException e) {
						LOGGER.error("Error filtering contracts", e);
					}
				}
			};
		}
		return this.contractFilter;
	}
	
	public void onEditSearch(ActionEvent event) {
		init();
		setViewType(ViewType.EMPLOYEE);
	}
	
	private void init(){
		try {
			setContract((Contract) BeanManager.getManagerBean(Contract.class).createNewTo());
			setEnterprise(getUtils().getCurrentDomainEnterprise());
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
		setSelectedVariable(null);
	}
	
	public void onSearch(ActionEvent event) {
		if(getViewType()==ViewType.EMPLOYEE){
			searchContracts();	
		} else if(getViewType()==ViewType.VARIABLE){
			searchVariables();
		} else {
			
		}
	}
	private void searchVariables() {
		try {
			updateContractCriteria();
			Calendar c = Calendar.getInstance();
			c.set(Calendar.YEAR, getYear());
			c.set(Calendar.MONTH, getMonth().ordinal());
			c.set(Calendar.DAY_OF_MONTH, 1);
			Date startDate = c.getTime();
			IManagerBean bean = BeanManager.getManagerBean(Contract.class);
			List<ITransferObject> list = bean.getList(criteria);
			List<PaymentUpdate> dataList = new LinkedList<PaymentUpdate>();
			
			for (ITransferObject to: list) {
				Contract contract = (Contract) to;
				PaymentUpdate pu = new PaymentUpdate();
				pu.setContract(contract);
				dataList.add(pu);
			}
			populateList(dataList,startDate);
			setModel( new ListDataModel( new ArrayList<IVariableData>(dataList.get(0).getMap().values()) ) );
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
	private void searchContracts() {
		try {
			updateContractCriteria();
			Calendar c = Calendar.getInstance();
			c.set(Calendar.YEAR, getYear());
			c.set(Calendar.MONTH, getMonth().ordinal());
			c.set(Calendar.DAY_OF_MONTH, 1);
			Date startDate = c.getTime();
			IManagerBean bean = BeanManager.getManagerBean(Contract.class);
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
	
	private Criteria criteria;

	private void updateContractCriteria() {
		try{
			Calendar c = Calendar.getInstance();
			c.set(Calendar.YEAR, getYear());
			c.set(Calendar.MONTH, getMonth().ordinal());
			c.set(Calendar.DAY_OF_MONTH, 1);
			Date startDate = c.getTime();
			Date endDate = CommonUtil.getMonthLastDay(startDate);
			IManagerBean bean = BeanManager.getManagerBean(Contract.class);
			criteria = new Criteria();
			if(getContract()!=null && getContract().getId()!=null) {
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ID), getContract().getId());			
			} else if(getWorkPlace()!=null && getWorkPlace().getId()!=null) {
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
		} catch (ManagerBeanException ex) {
			String msg = "Imposible realizar la búsqueda de los datos. [" + ex.getMessage()+"]";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,ex);
		}
	}
	
	public void onChangeWorkPlace(ValueChangeEvent event){
		updateContractCriteria();
	}
	
	public void onChangeWorkPlace(ActionEvent event){
		updateContractCriteria();
	}

	public void onChangeView(ActionEvent event){
		init();
	}
	
	public void refreshVariables(LookupChangeEvent event){
		updateContractCriteria();
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
				if(getViewType()==ViewType.EMPLOYEE){
					List<PaymentUpdate> dataList = (List<PaymentUpdate>) getModel().getWrappedData();
					for (PaymentUpdate pu: dataList) {
						for(IVariableData var: pu.getMap().values()){
							if( StringUtils.isNotBlank(var.getExpression()) && isVariableValueChanged(pu.getContract(), var) ){
								
								if( var.getStartDate()==null ) {
									var.setStartDate(getStartDate());
								}
								if( var.getEndDate()==null  ){
									var.setEndDate(getEndDate());
								}
								IVariableData vd = null;
								if( DateUtils.isSameDay(var.getStartDate(),getStartDate()) && DateUtils.isSameDay(var.getEndDate(),getEndDate()) ){
									vd = obtainExistingContractData(pu.getContract(), var);
								} 
								if( vd!=null && vd.getId()!=null ){
									bean.update( (ContractData)vd );	
								} else {
									vd = createContractData(pu.getContract(), var);
									bean.insert( (ContractData)vd );	
								}
								
							}
						}
					}
				} else if (getViewType()==ViewType.VARIABLE){
					List<ContractData> dataList = (List<ContractData>) getModel().getWrappedData();
					for(IVariableData var: dataList){
						if( StringUtils.isNotBlank(var.getExpression()) && isVariableValueChanged(getContract(), var) ){
							if( var.getStartDate()==null ) {
								var.setStartDate(getStartDate());
							}
							if( var.getEndDate()==null  ){
								var.setEndDate(getEndDate());
							}
							IVariableData vd = null;
							if( DateUtils.isSameDay(var.getStartDate(),getStartDate()) && DateUtils.isSameDay(var.getEndDate(),getEndDate()) ){
								vd = obtainExistingContractData(getContract(), var);
							} 
							if( vd!=null && vd.getId()!=null ){
								bean.update( (ContractData)vd );	
							} else {
								vd = createContractData(getContract(), var);
								bean.insert( (ContractData)vd );	
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

	private boolean isVariableValueChanged(Contract contract, IVariableData var) throws SalaryException {
		ContractData data = getContractContext(contract, var.getName(), getStartDate(), getEndDate());
		if( data==null || data.getExpression()==null || !data.getExpression().equals(var.getExpression()) ){
			return true;
		}
		return false;
	}
	private IVariableData createContractData(Contract contract, IVariableData var) {
		ContractData data = new ContractData();
		data.setContract(contract);
		data.setStartDate(getStartDate());
		data.setEndDate(getEndDate());
		data.setName(var.getName());
		data.setExpression(var.getExpression());
		return data;
	}
	private IVariableData obtainExistingContractData(Contract contract, IVariableData var) throws ManagerBeanException {
		ContractData data = null;
		try {
			List<ITransferObject> list = getUtils().getContractDataList(contract, getStartDate(), getEndDate(), var.getName());
			if(!list.isEmpty() && list.size()>0){
				data = (ContractData) list.get(0);
				data.setExpression(var.getExpression());
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al obtener el valor de la variable.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		return data;
	}
	
	public void onSalaryDraft(ActionEvent event) {
		try {
			PaymentUpdate pu = (PaymentUpdate) getModel().getRowData();
			SalaryDraftController controller = (SalaryDraftController) FormUtil.getController(IPayrollConstants.SALARY_DRAFT_CONTROLLER);
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
			String msg = "Error al seleccionar el borrador de la nómina.";
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
	
	public enum ViewType {
		EMPLOYEE,
		VARIABLE
		;
	}
	
	
}
