package com.esferalia.aon.ui.sepe.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.event.AbortProcessingException;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.company.Enterprise;
import com.code.aon.config.Domain;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.ContractInfo;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryData;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.ISalary;

public class SEPEUtils {
	
	private static SEPEUtils instance;
	
	private SEPEUtils(){
	}
	
	public static SEPEUtils getInstance(){
		if(instance == null){
			instance = new SEPEUtils();
		}
		return instance;
	}

	/*
	 * CONTRACT DATA
	 */
	public Map<String, String> getContractDataMap(Contract contract) {
		return getContractDataMap(contract, false, false);
	}
	
	public Map<String, String> getContractDataMap(Contract contract, boolean allowDuplicates, boolean includeChildDomainData) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			for(ITransferObject to: getContractDataList(contract, contract.getStartDate(), contract.getEndDate(), includeChildDomainData)){
				ContractData data = (ContractData) to;
				if(allowDuplicates || !map.containsKey(data.getName())){
					map.put(data.getName(), data.getExpression()!=null?data.getExpression().replace('"', ' ').trim():"");
				}
			}
		} catch (ManagerBeanException e) {
			// NADA, se devuelve un mapa vacio
			return map;
		}
		return map;
	}
	
	public Map<String, ContractData> getContractDataMap(Contract contract, Date startDate, Date endDate) {
		return getContractDataMap(contract, startDate, endDate, false);
	}

	public Map<String, ContractData> getContractDataMap(Contract contract, Date startDate, Date endDate, boolean includeChildDomains) {
		Map<String, ContractData> map = new HashMap<String, ContractData>();
		try {
			for(ITransferObject to: getContractDataList(contract, startDate, endDate, includeChildDomains)){
				ContractData data = (ContractData) to;
				if(data.getExpression()!=null){
					map.put(data.getName(), data);
				}
			}
		} catch (ManagerBeanException e) {
			// NADA, que siga generando el fichero
		}
		return map;
	}

	private List<ITransferObject> getContractDataList(Contract contract, Date startDate, Date endDate, boolean includeChildDomains) throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_CONTRACT_ID), contract.getId());
		if(startDate!=null){
			criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_START_DATE), startDate);
		}
		if(endDate!=null){
			Expression endDateExp = ExpressionUtilities.getNullExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_END_DATE));
			Expression exp = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_END_DATE), startDate);
			endDateExp = ExpressionUtilities.getOrExpression(exp, endDateExp);
			criteria.addExpression(endDateExp);
		}
		if(includeChildDomains){
			completeChildDomainCriteria(criteria, bean.getFieldName(IEntityAlias.CONTRACT_DATA_DOMAIN));
		}
		criteria.addOrder(bean.getFieldName(IEntityAlias.CONTRACT_DATA_START_DATE), false);
		return bean.getList(criteria);
	}

	public List<ITransferObject> getContractData(Contract contract, Date startDate, Date endDate, String name, boolean includeChildDomains) throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_CONTRACT_ID), contract.getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_NAME), name);
		if(startDate!=null){
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_START_DATE), startDate);
		}
		if(endDate!=null){
			Expression endDateExp = ExpressionUtilities.getNullExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_END_DATE));
			Expression exp = ExpressionUtilities.getEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_END_DATE), startDate);
			endDateExp = ExpressionUtilities.getOrExpression(exp, endDateExp);
			criteria.addExpression(endDateExp);
		}
		if(includeChildDomains){
			completeChildDomainCriteria(criteria, bean.getFieldName(IEntityAlias.CONTRACT_DATA_DOMAIN));
		}
		criteria.addOrder(bean.getFieldName(IEntityAlias.CONTRACT_DATA_START_DATE), false);
		return bean.getList(criteria);
	}
	
	public String getDataCurrentValue(Contract contract, String valueName) {
		return getContractCurrentValue(contract, "contract_data", valueName);
	}
	public String getInfoCurrentValue(Contract contract, String valueName) {
		return getContractCurrentValue(contract, "contract_info", valueName);
	}
	private String getContractCurrentValue(Contract contract, String tableName, String valueName) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String select = "SELECT expression"
			+ " FROM " + tableName
			+ " WHERE contract = " + contract.getId()
			+ " AND name = '" + valueName + "'"
			+ " ORDER BY start_date DESC";
			ps = conn.prepareStatement(select);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				String value = rs.getString(1);
				return value.replaceAll("\"", "");
			}
		} catch (SQLException e) {
			String msg = "Se ha producido un error al obtener el dato requerido. ("+ e.getMessage()+")";
			AonUtil.addErrorMessage(msg);
		} catch (AonConnectionException e) {
			String msg = "Se ha producido un error al obtener el dato requerido. ("+ e.getMessage()+")";
			AonUtil.addErrorMessage(msg);
		} finally {
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
		return null;
	}
	
	/*
	 * CONTRACT INFO
	 */
	public Map<String, String> getContractInfoMap(Contract contract) {
		return getContractInfoMap(contract, true, false);
	}
	
	public Map<String, String> getContractInfoMap(Contract contract, boolean allowDuplicates, boolean includeChildDomains) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			for(ITransferObject to: getContractInfoList(contract, contract.getStartDate(), contract.getEndDate(), includeChildDomains)){
				ContractInfo info = (ContractInfo) to;
				if(allowDuplicates || !map.containsKey(info.getName())){
					map.put(info.getName(), info.getExpression()!=null?info.getExpression().replace('"', ' ').trim():"");
				}
			}
		} catch (ManagerBeanException e) {
			// NADA, se devuelve un mapa vacio
			return map;
		}
		return map;
	}
	
	public Map<String, ContractInfo> getContractInfoMap(Contract contract, Date startDate, Date endDate) {
		return getContractInfoMap(contract, startDate, endDate, false);
	}
	public Map<String, ContractInfo> getContractInfoMap(Contract contract, Date startDate, Date endDate, boolean includeChildDomains) {
		Map<String, ContractInfo> map = new HashMap<String, ContractInfo>();
		try {
			for(ITransferObject to: getContractInfoList(contract, startDate, endDate, includeChildDomains)){
				ContractInfo info = (ContractInfo) to;
				if(info.getExpression()!=null){
					map.put(info.getName(), info);
				}
			}
		} catch (ManagerBeanException e) {
			// NADA, se devuelve un mapa vacio
		}
		return map;
	}
	
	private List<ITransferObject> getContractInfoList(Contract contract, Date startDate, Date endDate, boolean includeChildDomains) throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(ContractInfo.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_INFO_CONTRACT_ID), contract.getId());
		if(startDate!=null){
			criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_INFO_START_DATE), startDate);
		}
		if(endDate!=null){
			Expression endDateExp = ExpressionUtilities.getNullExpression(bean.getFieldName(IEntityAlias.CONTRACT_INFO_END_DATE));
			Expression exp = ExpressionUtilities.getLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_INFO_END_DATE), endDate);
			endDateExp = ExpressionUtilities.getOrExpression(exp, endDateExp);
			criteria.addExpression(endDateExp);
		}
		if(includeChildDomains){
			completeChildDomainCriteria(criteria, bean.getFieldName(IEntityAlias.CONTRACT_INFO_DOMAIN));
		}
		criteria.addOrder(bean.getFieldName(IEntityAlias.CONTRACT_INFO_START_DATE), false);
		return bean.getList(criteria);
	}
	
	/*
	 * SALARY DATA
	 */
	public List<SalaryData> getSalaryDataList(ISalary salary, Date startDate, Date endDate, String name) throws ManagerBeanException {
		List<SalaryData> list = new LinkedList<SalaryData>();
		if( salary!=null && salary.getId()!=null ){
			IManagerBean bean = BeanManager.getManagerBean(SalaryData.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_DATA_SALARY_ID), salary.getId());
			if(startDate!=null){
				criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.SALARY_DATA_START_DATE), startDate);
			}
			if(endDate!=null){
				criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.SALARY_DATA_END_DATE), endDate);
			}
			if(StringUtils.isNotBlank(name)){
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_DATA_NAME), name);
			}
			completeChildDomainCriteria(criteria, bean.getFieldName(IEntityAlias.SALARY_DATA_DOMAIN));
			for(ITransferObject to: bean.getList(criteria)){
				SalaryData data = (SalaryData) to;
				if(data.getExpression()!=null){
					list.add(data);
				}
			}
		}
		return list;
	}
	
	public List<SalaryData> getSalaryDataList(ISalary salary, Date startDate, Date endDate) throws ManagerBeanException {
		return getSalaryDataList(salary, startDate, endDate, null);
	}
	
	public Map<String, SalaryData> getSalaryDataMap(Salary salary, Date startDate, Date endDate) throws ManagerBeanException {
		Map<String, SalaryData> map = new HashMap<String, SalaryData>();
		for(ITransferObject to: getSalaryDataList(salary, startDate, endDate)){
			SalaryData data = (SalaryData) to;
			if(data.getExpression()!=null){
				map.put(data.getName(), data);
			}
		}
		return map;
	}
	
	
	/*
	 * CONTRACT WORKDAY HOURS
	 */
	public List<ITransferObject> getContractWorkdayHours(Contract contract) throws ManagerBeanException{
		String[] varList = {ContextVariable.MONDAY_HOURS.getName(),
				ContextVariable.TUESDAY_HOURS.getName(), ContextVariable.WEDNESDAY_HOURS.getName(),
				ContextVariable.THURSDAY_HOURS.getName(), ContextVariable.FRIDAY_HOURS.getName(),
				ContextVariable.SATURDAY_HOURS.getName(), ContextVariable.SUNDAY_HOURS.getName() };
		
		IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_CONTRACT_ID), contract.getId());
		criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_START_DATE), contract.getStartDate());
		if(contract.getEndDate()!=null){
			Expression endNull = ExpressionUtilities.getNullExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_END_DATE));
			Expression endGTstart = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_END_DATE), contract.getStartDate());
			criteria.addExpression(ExpressionUtilities.getOrExpression(endNull, endGTstart));
		}
		criteria.addInExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_NAME), varList);
		criteria.addOrder(bean.getFieldName(IEntityAlias.CONTRACT_DATA_START_DATE), false);
		return bean.getList(criteria);
	}
	
	public List<ContractData[]> obtainWeekList(Contract contract) {
		List<ContractData[]> weekList = null;
		try {
			List<ITransferObject> hoursList = getContractWorkdayHours(contract);
			weekList = new LinkedList<ContractData[]>();
			ContractData[] week = null;
			ContractData previous = null;
			for(ITransferObject to: hoursList){
				ContractData data = (ContractData) to;
				if(previous==null || data.getStartDate().before(previous.getStartDate())){
					week = new ContractData[7];
					for(int i = 0;i<week.length;i++){
						week[i]=new ContractData();
						week[i].setStartDate(data.getStartDate());
						week[i].setEndDate(data.getEndDate());
					}
					weekList.add(week);
				}
				
				if(data.getName().equals(ContextVariable.MONDAY_HOURS.getName())){
					week[0] = data;
				} else if(data.getName().equals(ContextVariable.TUESDAY_HOURS.getName())){
					week[1] = data;
				} else if(data.getName().equals(ContextVariable.WEDNESDAY_HOURS.getName())){
					week[2] = data;
				} else if(data.getName().equals(ContextVariable.THURSDAY_HOURS.getName())){
					week[3] = data;
				} else if(data.getName().equals(ContextVariable.FRIDAY_HOURS.getName())){
					week[4] = data;
				} else if(data.getName().equals(ContextVariable.SATURDAY_HOURS.getName())){
					week[5] = data;
				} else if(data.getName().equals(ContextVariable.SUNDAY_HOURS.getName())){
					week[6] = data;
				}
				
				previous = data;
			}
		} catch (ManagerBeanException e) {
			String msg = "onSearch contract workday hours. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
		return weekList;
	}
	
	// //////////////////////////////////
	// DOMAIN METHODS
	// //////////////////////////////////
	
	public void completeChildDomainCriteria(Criteria criteria, String fieldName){
		completeChildDomainCriteria(criteria, fieldName, true);
	}
	public void completeChildDomainCriteria(Criteria criteria, String fieldName, boolean discardParentDomain){
		if(DomainManager.isDomainManagementAvailable()){
			criteria.setSkipDomainFilter( true );
			if(!discardParentDomain){
				Expression expr1 = ExpressionUtilities.getInExpression(fieldName, getCurrentChildDomainIds());
				Expression expr2 = ExpressionUtilities.getEqualExpression(fieldName, DomainManager.getCurrentDomain());
				criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
			} else {
				criteria.addInExpression(fieldName, getCurrentChildDomainIds());
			}
		}
	}
	
	public Enterprise getCurrentDomainEnterprise(){
		try {
			IManagerBean bean = BeanManager.getManagerBean(Enterprise.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ENTERPRISE_DOMAIN), DomainManager.getCurrentDomain());
			if( bean.getCount(criteria)<1 ) {
				String msg = "No hay datos de empresa definidos.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			} else {
				return (Enterprise) bean.getList(criteria).get(0);
			}
		} catch (ManagerBeanException e) {
			// NADA. se devuelve nulo
		}
		return null;
	}
	
	public List<Integer> getCurrentChildDomainIds(){
		List<Integer> idList = new LinkedList<Integer>();
		if( DomainManager.isDomainManagementAvailable() ){
			try {
				IManagerBean bean = BeanManager.getManagerBean(Domain.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.DOMAIN_PARENT_ID), DomainManager.getCurrentDomain());
				List<ITransferObject> list = bean.getList(criteria);
				for(ITransferObject to: list){
					idList.add(((Domain)to).getId());
				}
			} catch (ManagerBeanException e) {
				// NADA. se devuelve vacio
			}
		} 
		return idList;
	}

	// /////////////////
	// DATES
	// /////////////////
	public Date getDateWithResettedHours(Date date, boolean resetToZero) {
		if (date != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			if (resetToZero) {
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
			} else {
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 59);
				cal.set(Calendar.SECOND, 59);
			}
			return cal.getTime();
		}
		return null;
	}
}
