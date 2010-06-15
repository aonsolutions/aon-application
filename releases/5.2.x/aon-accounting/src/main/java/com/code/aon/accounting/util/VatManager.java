package com.code.aon.accounting.util;

import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.accounting.Period;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.IProgression;
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

	public void regenerateVAT(Period period, SecurityLevel securityLevel, IProgression progressionBean) throws ManagerBeanException {
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
				IManagerBean bean = BeanManager.getManagerBean(Invoice.class);
				Criteria criteria = new Criteria();
				criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IFinanceAlias.INVOICE_ISSUE_DATE), period.getInitiationDate());
				criteria.addLessThanOrEqualExpression(bean.getFieldName(IFinanceAlias.INVOICE_ISSUE_DATE), period.getDeadline());
				criteria.addEqualExpression(bean.getFieldName(IFinanceAlias.INVOICE_SECURITY_LEVEL), securityLevel );
				Expression exp = ExpressionUtilities.getEqualExpression(bean.getFieldName(IFinanceAlias.INVOICE_TYPE), InvoiceType.EXPENSES);
				Expression pur = ExpressionUtilities.getEqualExpression(bean.getFieldName(IFinanceAlias.INVOICE_TYPE), InvoiceType.PURCHASE);
				criteria.addExpression( ExpressionUtilities.getOrExpression(exp, pur) );
				criteria.addOrder(bean.getFieldName(IFinanceAlias.INVOICE_SERIES));
				criteria.addOrder(bean.getFieldName(IFinanceAlias.INVOICE_ISSUE_DATE));
				List<ITransferObject> list = bean.getList(criteria); 
				int count = list.size();
		        int i = 0;
		        int journal= 0;
		        String series = "*****";
		        for (ITransferObject to : list ) {
		        	Invoice invoice = (Invoice) to;
		        	if (!ObjectUtils.equals(series, invoice.getSeries())) {
		        		series = invoice.getSeries();
		        		journal = 1;
		        	}
		        	System.out.print( invoice.getDocumentNumber()  + "\t");
		        	// Se asigna el contador en negativo para evitar
		        	// las claves duplicadas del indice unico type/series/number
		        	invoice.setNumber(journal * -1);
		        	System.out.println( invoice.getDocumentNumber() );
			        bean.update(invoice);    	
		        	progressionBean.setProgressionCurrentValue((long) ( i * 100 / count/2));
		        	i++;
	        		journal++;
		        }
	        	// Se asigna el número como diox manda.
		        list = bean.getList(criteria);
		        for (ITransferObject to : list ) {
		        	Invoice invoice = (Invoice) to;
		        	invoice.setNumber(invoice.getNumber() * -1);
		        	System.out.println( invoice.getDocumentNumber() );
			        bean.update(invoice);    	
		        	progressionBean.setProgressionCurrentValue((long) ( i * 100 / count/2));
		        	i++;
	        		journal++;
		        }
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
}
