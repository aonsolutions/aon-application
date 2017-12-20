package com.esferalia.aon.ui.payroll.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.faces.event.AbortProcessingException;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.company.Enterprise;
import com.code.aon.config.Domain;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.AgreementData;
import com.esferalia.aon.payroll.AgreementLevelData;
import com.esferalia.aon.payroll.AgreementPayment;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.ContractPayment;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryData;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.calculator.ISalaryCalculatorContext;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionContext;


public class PayrollUtils extends com.esferalia.aon.payroll.util.PayrollUtils{
	
	private static PayrollUtils instance;
	
	public static PayrollUtils getInstance(){
		if(instance == null){
			instance = new PayrollUtils();
		}
		return instance;
	}
	
	
	// //////////////////////////////////
	// SALARY METHODS
	// //////////////////////////////////
	
	/**
	 * Calculate the salary of the complete month of the param date
	 * 
	 * @param contract
	 * @param date
	 * @return
	 */
	public ISalary calculateSalary(Contract contract, Date date) {
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(date);
		startCal.set(Calendar.DAY_OF_MONTH, startCal.getActualMinimum(Calendar.DAY_OF_MONTH));
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(date);
		endCal.set(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
		return calculateSalary(contract, startCal.getTime(), endCal.getTime());
	}

		
	public ISalary calculateSalary(Contract contract, Date startDate, Date endDate) {
		return calculateSalary(contract, startDate, endDate, SalaryType.SALARY);
	}
	
	public ISalary calculateSalary(Contract contract, Date startDate, Date endDate, SalaryType salaryType) {
		try {
			ISalaryCalculatorContext ctx = contract.getSalaryCalculatorContext(startDate, endDate, salaryType);
			return ctx.getSalaryProxy().getSalary();
		} catch (SalaryException e) {
			// sigue ...
		}
		return null;
	}
	
	public ISalary getLastSalary(Contract contract) {
		return getBeforeDateSalary(contract, null);
	}
	
	public ISalary getBeforeDateSalary(Contract contract, Date beforeDate) {
		return getSalary(contract, null, beforeDate);
	}
	
	public ISalary getSalary(Contract contract, Date startDate, Date endDate) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Salary.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_CONTRACT_ID), contract.getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_TYPE), SalaryType.SALARY);
			if(startDate != null){
				Calendar cal = Calendar.getInstance();
				cal.setTime(startDate);
				cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
				criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.SALARY_START_DATE), cal.getTime());
			}
			if(endDate != null){
				Calendar cal = Calendar.getInstance();
				cal.setTime(endDate);
				cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
				criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.SALARY_END_DATE), cal.getTime());
			}
			criteria.addOrder(bean.getFieldName(IEntityAlias.SALARY_END_DATE), false);
			List<ITransferObject> list = bean.getList(criteria);
			if(list!=null && !list.isEmpty()){
				return (ISalary) list.get(0);
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible obtener la ultima nomina";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
		return null;
	}
	
	// //////////////////////////////////
	// PAYMENT METHODS
	// //////////////////////////////////

	public List<ITransferObject> getContractPaymentList(Contract contract, Date startDate, Date endDate) throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(ContractPayment.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_PAYMENT_CONTRACT_ID), contract.getId());			
		criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_PAYMENT_START_DATE), endDate);			
		Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_PAYMENT_END_DATE), startDate);
		Expression expr2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IEntityAlias.CONTRACT_PAYMENT_END_DATE));
		criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
		return bean.getList(criteria);
	}
	public List<ITransferObject> getAgreementPaymentList(Contract contract, Date startDate, Date endDate) throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(AgreementPayment.class);
		Criteria criteria = null;
		criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.AGREEMENT_PAYMENT_AGREEMENT_ID), contract.getAgreementLevel().getAgreement().getId());
		criteria.addOrder(bean.getFieldName(IEntityAlias.AGREEMENT_PAYMENT_START_DATE), false);
		Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.AGREEMENT_PAYMENT_END_DATE), new Date());
		Expression expr2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IEntityAlias.AGREEMENT_PAYMENT_END_DATE));
		criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
		return bean.getList(criteria);
	}
	
	// //////////////////////////////////
	// VARIABLE METHODS (*_data)
	// //////////////////////////////////
	
	public Set<String> getContractVariableList(Contract contract, Date startDate, Date endDate){
		try {
			Set<String> vl = new HashSet<String>();
			for(ITransferObject to: getContractPaymentList(contract, startDate, endDate)){
				ContractPayment payment = (ContractPayment) to;
				for(String name: getPaymentVariableList(payment)){
					if(!vl.contains(name)){
						vl.add(name);
					}
				}
			}
			if(contract.getAgreementLevel()!=null){
				for(ITransferObject to: getAgreementPaymentList(contract, startDate, endDate)){
					AgreementPayment payment = (AgreementPayment) to;
					for(String name: getPaymentVariableList(payment)){
						if(!vl.contains(name)){
							vl.add(name);
						}
					}
				}
			}
			return vl;
		} catch (ManagerBeanException e) {
			String msg = "Imposible obtener las variables del contrato";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}
	public Set<String> getPaymentVariableList(ContractPayment payment){
		Set<String> vl = ExpressionContext.getVarNames(StringUtils.isBlank(payment.getExpression())?payment.getPaymentConcept().getExpression():payment.getExpression());
		return vl;
	}
	public Set<String> getPaymentVariableList(AgreementPayment payment){
		Set<String> vl = ExpressionContext.getVarNames(StringUtils.isBlank(payment.getExpression())?payment.getPaymentConcept().getExpression():payment.getExpression());
		return vl;
	}
	
	public List<ITransferObject> getSalaryDataList(Salary salary, boolean includeChildDomains) throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(SalaryData.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_DATA_SALARY_ID), salary.getId());
		if(includeChildDomains){
			completeChildDomainCriteria(criteria, bean.getFieldName(IEntityAlias.SALARY_DATA_DOMAIN));
		}
		return bean.getList(criteria);
	}
	public List<ITransferObject> getContractDataList(Contract contract, Date startDate, Date endDate, String variableName) throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_CONTRACT_ID), contract.getId());			
		if(variableName!=null){
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_NAME), variableName);
		}
		criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_START_DATE), endDate);			
		Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_END_DATE), startDate);
		Expression expr2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_END_DATE));
		criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
		criteria.addOrder(bean.getFieldName(IEntityAlias.CONTRACT_DATA_START_DATE), false);			
		return bean.getList(criteria);
	}
	public List<ITransferObject> getAgreementLevelDataList(Contract contract, Date startDate, Date endDate, String variableName) throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(AgreementLevelData.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.AGREEMENT_LEVEL_DATA_LEVEL_ID), contract.getAgreementLevel().getId());			
		if(variableName!=null){
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.AGREEMENT_LEVEL_DATA_NAME), variableName);
		}
		criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.AGREEMENT_LEVEL_DATA_START_DATE), endDate);			
		Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.AGREEMENT_LEVEL_DATA_END_DATE), startDate);
		Expression expr2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IEntityAlias.AGREEMENT_LEVEL_DATA_END_DATE));
		criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
		criteria.addOrder(bean.getFieldName(IEntityAlias.AGREEMENT_LEVEL_DATA_START_DATE), false);			
		return bean.getList(criteria);
	}
	public List<ITransferObject> getAgreementDataList(Contract contract, Date startDate, Date endDate, String variableName) throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(AgreementData.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.AGREEMENT_DATA_AGREEMENT_ID), contract.getAgreementLevel().getAgreement().getId());			
		if(variableName!=null){
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.AGREEMENT_DATA_NAME), variableName);
		}
		criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.AGREEMENT_DATA_START_DATE), endDate);			
		Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.AGREEMENT_DATA_END_DATE), startDate);
		Expression expr2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IEntityAlias.AGREEMENT_DATA_END_DATE));
		criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
		criteria.addOrder(bean.getFieldName(IEntityAlias.AGREEMENT_DATA_START_DATE), false);			
		return bean.getList(criteria);
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

	public List<ITransferObject> getCurrentChildEnterprises(){
		if( DomainManager.isDomainManagementAvailable() ){
			try {
				IManagerBean bean = BeanManager.getManagerBean(Enterprise.class);
				Criteria criteria = new Criteria();
				criteria.addInExpression(bean.getFieldName(IEntityAlias.ENTERPRISE_DOMAIN), getCurrentChildDomainIds());
				criteria.addOrder(bean.getFieldName(IEntityAlias.ENTERPRISE_REGISTRY_NAME));
				criteria.setSkipDomainFilter(true);
				return bean.getList(criteria);
			} catch (ManagerBeanException e) {
				// NADA. se devuelve vacio
			}
		} 
		return null;
	}
	
	public Integer getParentDomainId() {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String select = "SELECT parent FROM domain WHERE id = " + DomainManager.getCurrentDomain();
			ps = conn.prepareStatement(select);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			String msg = "Se ha producido un error al obtener los convenios. ("+ e.getMessage()+")";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} catch (AonConnectionException e) {
			String msg = "Se ha producido un error al obtener los convenios. ("+ e.getMessage()+")";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} finally {
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
		return null;
	}


}
