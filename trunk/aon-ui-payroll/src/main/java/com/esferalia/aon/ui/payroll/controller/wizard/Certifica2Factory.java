package com.esferalia.aon.ui.payroll.controller.wizard;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.AbortProcessingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Certifica2Batch;
import com.esferalia.aon.payroll.Certifica2BatchData;
import com.esferalia.aon.payroll.Certifica2BatchDetail;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.payroll.enumeration.FileStatus;

public class Certifica2Factory {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Certifica2Factory.class);
	

	protected Map<String, String> getContractDataMap(Contract contract) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_DATA_CONTRACT_ID), contract.getId());
			criteria.addNotNullExpression(bean.getFieldName(IPayrollAlias.CONTRACT_DATA_END_DATE));
			for(ITransferObject to: bean.getList(criteria)){
				ContractData data = (ContractData) to;
				if(data.getExpression()!=null){
					map.put(data.getName(), data.getExpression().replace('"', ' ').trim());
				}
			}
		} catch (ManagerBeanException e) {
			// NADA, que siga generando el fichero
		}
		return map;
	}
	
	protected Integer differenceBetweenDates(Date from, Date to) {
		Integer diffDays = new Integer(0);
		final Double MS_PER_DAY = new Double(1000 * 60 * 60 * 24);
		if(from.before(to)) {
			diffDays = (int)((Math.floor((to.getTime() - from.getTime()) / MS_PER_DAY + 0.5d) + 1));
		}
		return diffDays;
	}

	@SuppressWarnings("unchecked")
	protected List<Certifica2Batch> getExistingRemesas(Contract contract) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Certifica2Batch.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.CERTIFICA2BATCH_ENTERPRISE_ID),contract.getWorkPlace().getEnterprise().getId());
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.CERTIFICA2BATCH_STATUS),FileStatus.PENDING);
			criteria.addOrder(bean.getFieldName(IPayrollAlias.CERTIFICA2BATCH_DATE), false);
			List<?> list = bean.getList(criteria);
			return (List<Certifica2Batch>)list;
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> getExistingRemesas ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	@SuppressWarnings("unchecked")
	protected List<Certifica2BatchDetail> getDetalleRemesaCertificados(Certifica2Batch remesa) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Certifica2BatchDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.CERTIFICA2BATCH_DETAIL_CERTIFICA2BATCH_ID), remesa.getId());
			List<?> list = bean.getList(criteria);
			return (List<Certifica2BatchDetail>)list;
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> getDetalleRemesaCertificados ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	protected Certifica2Batch accept(Certifica2Batch remesa) throws ManagerBeanException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Certifica2Batch.class);
			Certifica2Batch r = (Certifica2Batch) remesa;
			return (Certifica2Batch) bean.insertOrUpdate(r);
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> acceptBatch ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new ManagerBeanException(e.getMessage(), e);
		}
	}
	
	protected Certifica2BatchDetail accept(Certifica2BatchDetail detalle) throws ManagerBeanException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Certifica2BatchDetail.class);
			Certifica2BatchDetail d = (Certifica2BatchDetail) detalle;
			return (Certifica2BatchDetail) bean.insertOrUpdate(d);
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> acceptBatchDetail ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new ManagerBeanException(e.getMessage(), e);
		}
	}
	
	protected Certifica2BatchData accept(Certifica2BatchData data) throws ManagerBeanException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Certifica2BatchData.class);
			Certifica2BatchData d = (Certifica2BatchData) data;
			return (Certifica2BatchData) bean.insertOrUpdate(d);
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> acceptBatchData ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new ManagerBeanException(e.getMessage(), e);
		}
	}


	
}
