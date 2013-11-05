package com.esferalia.aon.ui.sepe.utils;

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
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryData;

public class SEPEUtils {

	public Map<String, String> getContractDataMap(Contract contract) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_CONTRACT_ID), contract.getId());
			criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_START_DATE), contract.getStartDate());
			Expression endDateExp = ExpressionUtilities.getNullExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_END_DATE));
			if(contract.getEndDate()!=null){
				Expression exp = ExpressionUtilities.getLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_END_DATE), contract.getEndDate());
				endDateExp = ExpressionUtilities.getOrExpression(exp, endDateExp);
			} else {
				criteria.addExpression(endDateExp);
			}
			completeDomainCriteria(criteria, bean.getFieldName(IEntityAlias.CONTRACT_DATA_DOMAIN));
			for(ITransferObject to: bean.getList(criteria)){
				ContractData data = (ContractData) to;
				map.put(data.getName(), data.getExpression()!=null?data.getExpression().replace('"', ' ').trim():"");
			}
		} catch (ManagerBeanException e) {
			// NADA, se devuelve un mapa vacio
			return map;
		}
		return map;
	}
	
	public Map<String, ContractData> getContractDataMap(Contract contract, Date startDate, Date endDate) {
		Map<String, ContractData> map = new HashMap<String, ContractData>();
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_CONTRACT_ID), contract.getId());
			if(startDate!=null){
				criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_START_DATE), startDate);
			}
			if(endDate!=null){
				criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_END_DATE), endDate);
			}
			completeDomainCriteria(criteria, bean.getFieldName(IEntityAlias.CONTRACT_DATA_DOMAIN));
			for(ITransferObject to: bean.getList(criteria)){
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
	
	public List<SalaryData> getSalaryDataList(Salary salary, Date startDate, Date endDate, String name) throws ManagerBeanException {
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
			completeDomainCriteria(criteria, bean.getFieldName(IEntityAlias.SALARY_DATA_DOMAIN));
			for(ITransferObject to: bean.getList(criteria)){
				SalaryData data = (SalaryData) to;
				if(data.getExpression()!=null){
					list.add(data);
				}
			}
		}
		return list;
	}
	
	public List<SalaryData> getSalaryDataList(Salary salary, Date startDate, Date endDate) throws ManagerBeanException {
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
	
	// //////////////////////////////////
	// DOMAIN METHODS
	// //////////////////////////////////
	
	public void completeDomainCriteria(Criteria criteria, String fieldName){
		if(DomainManager.isDomainManagementAvailable()){
			criteria.setSkipDomainFilter( true );
			criteria.addInExpression(fieldName, getCurrentChildDomainIds());
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
	
}
