package com.code.aon.accounting.util;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;

public class VatManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(VatManager.class.getName()); 

	public void regenerateVAT(VatManagerParams params) throws ManagerBeanException {
		//inicio transaccion
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
				// BEGIN operaciones de la transaccion
				updateInvoices(params);
				// FIN operaciones de la transaccion
				HibernateUtil.getSession(sessionName).flush();
				HibernateUtil.commitTransaction(sessionName);
			} catch (Exception e) {
				try {
					HibernateUtil.rollbackTransaction(sessionName);
				} catch (DAOException daoe) {
					String msg = "Unable to rollback transaction!";
					LOGGER.error(msg, e);
				}
				throw new ManagerBeanException(e.getMessage(),e);
			} finally {
				HibernateUtil.closeSession(sessionName);
			}
		} finally {
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
		
	}
	
	private void updateInvoices(VatManagerParams params) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Invoice.class);
		Criteria criteria = new Criteria();
		criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IFinanceAlias.INVOICE_ISSUE_DATE), params.getPeriod().getInitiationDate());
		criteria.addLessThanOrEqualExpression(bean.getFieldName(IFinanceAlias.INVOICE_ISSUE_DATE), params.getPeriod().getDeadline());
		if (params.getSecurityLevel() == SecurityLevel.CONFIDENTIAL ) {
			criteria.addEqualExpression(bean.getFieldName(IFinanceAlias.INVOICE_SECURITY_LEVEL), params.getSecurityLevel() );	
		} else {
			Expression e1 = ExpressionUtilities.getNotEqualExpression(bean.getFieldName(IFinanceAlias.INVOICE_SECURITY_LEVEL), SecurityLevel.CONFIDENTIAL);
			Expression e2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IFinanceAlias.INVOICE_SECURITY_LEVEL));
			criteria.addExpression( ExpressionUtilities.getOrExpression(e1, e2));
		}
		if (params.getFromSeries() != null) {
			criteria.addGreaterThanOrEqualExpression( bean.getFieldName(IFinanceAlias.INVOICE_SERIES), params.getFromSeries().getId());	
		}
		if (params.getFromNumber() != null) {
			criteria.addGreaterThanOrEqualExpression( bean.getFieldName(IFinanceAlias.INVOICE_NUMBER), params.getFromNumber());	
		}
		if (params.getToSeries() != null) {
			criteria.addLessThanOrEqualExpression( bean.getFieldName(IFinanceAlias.INVOICE_SERIES), params.getToSeries().getId());	
		}
		if (params.getToNumber() != null) {
			criteria.addLessThanOrEqualExpression( bean.getFieldName(IFinanceAlias.INVOICE_NUMBER), params.getToNumber());	
		}
		criteria.addExpression( ExpressionUtilities.getNotEqualExpression(bean.getFieldName(IFinanceAlias.INVOICE_TYPE), InvoiceType.SALES));
		criteria.addEqualExpression( bean.getFieldName(IFinanceAlias.INVOICE_INVESTMENT), params.isInvestment());
		criteria.addOrder(bean.getFieldName(IFinanceAlias.INVOICE_ISSUE_DATE));
		criteria.addOrder(bean.getFieldName(IFinanceAlias.INVOICE_ID));
		List<ITransferObject> list = bean.getList(criteria); 
	    int i = params.getFirstNumber();
	    for (ITransferObject to : list ) {
	    	Invoice invoice = (Invoice) to;
	    	// Se asigna el contador en negativo para evitar
	    	// las claves duplicadas del indice unico type/series/number
	    	invoice.setSeries(params.getSeries().getId());
	    	invoice.setNumber(i * -1);
	        bean.update(invoice);    	
			i++;
	    }
		// Se asigna el número como diox manda.
	    list = bean.getList(criteria);
	    for (ITransferObject to : list ) {
	    	Invoice invoice = (Invoice) to;
	    	invoice.setNumber(invoice.getNumber() * -1);
	        bean.update(invoice);    	
			i++;
	    }
	}
	
}
