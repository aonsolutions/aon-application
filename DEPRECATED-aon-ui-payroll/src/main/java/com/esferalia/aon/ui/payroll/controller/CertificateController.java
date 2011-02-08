package com.esferalia.aon.ui.payroll.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.employee.Contract;
import com.code.aon.employee.dao.IEmployeeAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.esferalia.aon.payroll.EnterpriseCertificate;
import com.esferalia.aon.payroll.EnterpriseCertificateDetail;
import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.payroll.enumeration.FileStatus;


public class CertificateController {

//	public IActividadCCC getActividadCCC(WorkActivity actividad, CuentaCotizacion ccc) throws PayrollException {
//		try {
//			IManagerBean bean = BeanManager.getManagerBean(ActividadCCC.class);
//			Criteria criteria = new Criteria();
//			criteria.addEqualExpression(ACTCCC_ACT_ALIAS, actividad.getId());
//			String c = null;
//			if (ccc == CuentaCotizacion.PRINCIPAL) {
//				c = "P";
//			} else if (ccc == CuentaCotizacion.ALTO_CARGO) {
//				c = "A";
//			} else if (ccc == CuentaCotizacion.APRENDIZ) {
//				c = "R";
//			} else if (ccc == CuentaCotizacion.ASIMILADO) {
//				c = "S";
//			} 
//			criteria.addEqualExpression(ACTCCC_CCC_ALIAS, c );
//			List<?> list = bean.getList(criteria);
//			if (list.size() > 0) {
//				return (IActividadCCC) list.get(0);	
//			}
//			return null;
//		} catch (ManagerBeanException e) {
//			throw new PayrollException(e);
//		}
//	}
	
	@SuppressWarnings("unchecked")
	public List<EnterpriseCertificate> getCertificateBatchList(CertificateBatchParams params) throws PayrollException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(EnterpriseCertificate.class);
			Criteria criteria = new Criteria();
			if (!StringUtils.isEmpty(params.getEnterprise())) {
				criteria.addExpression(bean.getFieldName(IPayrollAlias.ENTERPRISE_CERTIFICATE_ENTERPRISE_REGISTRY_NAME),params.getEnterprise());
			}
			if (params.getDate() != null) {
				criteria.addLessThanOrEqualExpression(bean.getFieldName(IPayrollAlias.ENTERPRISE_CERTIFICATE_DATE),params.getDate());
			}
			if (!ArrayUtils.isEmpty(params.getStatusList())) {
				String status = bean.getFieldName(IPayrollAlias.ENTERPRISE_CERTIFICATE_STATUS);
				addEnumToCriteria(criteria, status, params.getStatusList());
			}
			criteria.addOrder(bean.getFieldName(IPayrollAlias.ENTERPRISE_CERTIFICATE_DATE), false);
			List<?> list = bean.getList(criteria);
			return (List<EnterpriseCertificate>)list;
		} catch (ManagerBeanException e) {
			throw new PayrollException(e);
		} catch (ExpressionException e) {
			throw new PayrollException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<EnterpriseCertificateDetail> getCertificateDetailBatchList(EnterpriseCertificate remesa) throws PayrollException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(EnterpriseCertificateDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.ENTERPRISE_CERTIFICATE_DETAIL_ENTERPRISE_CERTIFICATE_ID), remesa.getId());
//			criteria.addEqualExpression(REMESA_CERT_DET_REMESA_ALIAS, remesa.getId());
			List<?> list = bean.getList(criteria);
			return (List<EnterpriseCertificateDetail>)list;
		} catch (ManagerBeanException e) {
			throw new PayrollException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Contract> getEmpleados(CertificateBatchParams params)
			throws PayrollException {
		final int FROM_YEAR = 2010;
		final int FROM_MONTH = 05;
		final int FROM_DAY = 30;
		try {
			// fecha desde la que se empieza a usar el nuevo metodo de certificados de empresa
			Calendar date = new GregorianCalendar();
			date.set(Calendar.YEAR, FROM_YEAR);
			date.set(Calendar.MONTH, FROM_MONTH);
			date.set(Calendar.DAY_OF_MONTH, FROM_DAY);
			
			Criteria criteria = new Criteria();
			IManagerBean empleadoBean = BeanManager
					.getManagerBean(Contract.class);
			if (!StringUtils.isEmpty(params.getEnterprise())) {
				criteria.addExpression(empleadoBean.getFieldName(IEmployeeAlias.CONTRACT_WORK_PLACE_ENTERPRISE_REGISTRY_NAME),params.getEnterprise());
			}
			if (!StringUtils.isEmpty(params.getDocument())) {
				criteria.addExpression(empleadoBean.getFieldName(IEmployeeAlias.CONTRACT_PERSON_REGISTRY_DOCUMENT),params.getDocument());
			}
			if (!StringUtils.isEmpty(params.getName())) {
				criteria.addExpression(empleadoBean.getFieldName(IEmployeeAlias.CONTRACT_PERSON_REGISTRY_NAME),params.getName());
			}
			if (!StringUtils.isEmpty(params.getSurname())) {
				criteria.addExpression(empleadoBean.getFieldName(IEmployeeAlias.CONTRACT_PERSON_FIRST_SURNAME),params.getSurname());
			}
//			if (!StringUtils.isEmpty(params.getApellido2())) {
//				criteria.addExpression(empleadoBean.getFieldName(IPayrollAlias.EMPLEADO_PERSONA_LAST_NAME),params.getApellido2());
//			}
			if (params.getStartDate()!=null) {
				criteria.addGreaterThanOrEqualExpression(empleadoBean.getFieldName(IEmployeeAlias.CONTRACT_END_DATE), params.getStartDate());
			}
			if (params.getEndDate()!=null) {
				criteria.addLessThanOrEqualExpression(empleadoBean.getFieldName(IEmployeeAlias.CONTRACT_END_DATE), params.getEndDate());
			}
			
//			Expression exp1  = ExpressionUtilities.getNotEqualExpression(empleadoBean.getFieldName(IPayrollAlias.EMPLEADO_CODCCC), "A");
//			Expression exp2  = ExpressionUtilities.getNotEqualExpression(empleadoBean.getFieldName(IPayrollAlias.EMPLEADO_CODCCC), "S");
//			criteria.addExpression( ExpressionUtilities.getAndExpression(exp1, exp2) );
			criteria.addNotNullExpression(empleadoBean.getFieldName(IEmployeeAlias.CONTRACT_END_DATE));
			criteria.addOrder(empleadoBean.getFieldName(IEmployeeAlias.CONTRACT_WORK_PLACE_ENTERPRISE_REGISTRY_NAME));
			
			List<?> list = empleadoBean.getList(criteria);
			return (List<Contract>) list;
		} catch (ManagerBeanException e) {
			throw new PayrollException(e);
		} catch (ExpressionException e) {
			throw new PayrollException(e);
		}
	}
	
	public EnterpriseCertificate getNewBatch(Contract contract) throws PayrollException {
		EnterpriseCertificate remesa = new EnterpriseCertificate();
		remesa.setEnterprise(contract.getWorkPlace().getEnterprise());
		if(contract.getEndDate().before(Calendar.getInstance().getTime())){
			remesa.setDate(Calendar.getInstance().getTime());
		} else {
			remesa.setDate(new Date(contract.getEndDate().getTime()+(1*24*60*60*1000)));
		}
		remesa.setStatus(FileStatus.PENDIENTE);
		return remesa;
	}
	
	public EnterpriseCertificateDetail getNewDetailBatch(Contract contract) {
		EnterpriseCertificateDetail detalle = new EnterpriseCertificateDetail();
		detalle.setContract(contract);
		return detalle;
	}
	
	public EnterpriseCertificate accept(EnterpriseCertificate remesa) throws PayrollException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(EnterpriseCertificate.class);
			EnterpriseCertificate r = (EnterpriseCertificate) remesa;
			return (EnterpriseCertificate) bean.insertOrUpdate(r);
		} catch (ManagerBeanException e) {
			throw new PayrollException(e);
		}
	}
	
	public void accept(EnterpriseCertificateDetail detalle) throws PayrollException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(EnterpriseCertificateDetail.class);
			EnterpriseCertificateDetail d = (EnterpriseCertificateDetail) detalle;
			bean.insertOrUpdate(d);
		} catch (ManagerBeanException e) {
			throw new PayrollException(e);
		}
	}
	
	public String getEmpresaCccEmpleado(Contract contract) throws PayrollException {
//		String ccc;
//		ccc = getRegimenCode(contract.getActividad().getRegimen());
//		ccc += getActividadCCC(contract.getActividad(), contract.getCuentaCotizacion()).getDescripcion();
//		return ccc;
		return contract.getEnterpriseCCC().getCcc();
	}
	
//	private String getRegimenCode(Regimen regimen) {
//		if(regimen == Regimen.AGRARIO){
//			return "0613";
//		} else if(regimen == Regimen.GENERAL){
//			return "0111";
//		} else if(regimen == Regimen.ARTISTAS){
//			return "0112";
//		} else if(regimen == Regimen.MARITIMO){
//			return "0811";
//		}
//		return null;
//	}
	
	protected void addEnumToCriteria( Criteria criteria, String alias, Object[] values ) throws ManagerBeanException {
		Expression expToAdd = null;
		for( Object value : values ) {
			if ( value != null ) {
				if ( expToAdd == null ) {
					expToAdd = ExpressionUtilities.getEqualExpression(alias, value);				
				} else {
					Expression exp  = ExpressionUtilities.getEqualExpression(alias, value);
					expToAdd = ExpressionUtilities.getOrExpression(expToAdd, exp);
				}
			}
		}
		if ( expToAdd != null ) {
			criteria.addExpression(expToAdd);
		}
	}	
	
}
