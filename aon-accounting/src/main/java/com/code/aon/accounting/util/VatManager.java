package com.code.aon.accounting.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.SecurityLevel;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.esferalia.aon.entity.IEntityAlias;

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
				e.printStackTrace();
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
		Criteria criteria = getCriteria(params);
		List<ITransferObject> list = bean.getList(criteria); 
	    int i = params.getFirstNumber() + params.getCount() - 1;
	    for (ITransferObject to : list ) {
	    	Invoice invoice = (Invoice) to;
	    	String oldDoument = invoice.getDocumentNumber();
	    	invoice.setSeries(params.getSeries().getCode());
	    	invoice.setNumber(i);
	    	invoice = (Invoice) bean.update(invoice);
	    	String newDocument = invoice.getDocumentNumber();
	    	if (invoice.getStatus() == InvoiceStatus.SCORED) {
	    		updateAccountEntryDetail(oldDoument, newDocument);
	    	}
			i--;
	    }
	}

	private void updateAccountEntryDetail(String oldDoument, String newDocument) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(AccountEntryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_DOCUMENT_NUMBER), oldDoument);
		List<ITransferObject> list = bean.getList(criteria);
	    for (ITransferObject to : list ) {
	    	AccountEntryDetail detail = (AccountEntryDetail) to;
	    	detail.setDocumentNumber(newDocument);
	    	bean.update(detail);
	    }
	}

	private Criteria getCriteria(VatManagerParams params) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Invoice.class);
		Criteria criteria = new Criteria();
		criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.INVOICE_ISSUE_DATE), params.getPeriod().getInitiationDate());
		criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.INVOICE_ISSUE_DATE), params.getPeriod().getDeadline());
		if (params.getSecurityLevel() == SecurityLevel.CONFIDENTIAL ) {
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.INVOICE_SECURITY_LEVEL), SecurityLevel.CONFIDENTIAL );	
		} else {
			Expression e1 = ExpressionUtilities.getNotEqualExpression(bean.getFieldName(IEntityAlias.INVOICE_SECURITY_LEVEL), SecurityLevel.CONFIDENTIAL);
			Expression e2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IEntityAlias.INVOICE_SECURITY_LEVEL));
			criteria.addExpression( ExpressionUtilities.getOrExpression(e1, e2));
		}
		if (params.getFromSeries() != null) {
			criteria.addGreaterThanOrEqualExpression( bean.getFieldName(IEntityAlias.INVOICE_SERIES), params.getFromSeries().getCode());	
		}
		if (params.getFromNumber() != null) {
			criteria.addGreaterThanOrEqualExpression( bean.getFieldName(IEntityAlias.INVOICE_NUMBER), params.getFromNumber());	
		}
		if (params.getToSeries() != null) {
			criteria.addLessThanOrEqualExpression( bean.getFieldName(IEntityAlias.INVOICE_SERIES), params.getToSeries().getCode());	
		}
		if (params.getToNumber() != null) {
			criteria.addLessThanOrEqualExpression( bean.getFieldName(IEntityAlias.INVOICE_NUMBER), params.getToNumber());	
		}
		criteria.addExpression( ExpressionUtilities.getNotEqualExpression(bean.getFieldName(IEntityAlias.INVOICE_TYPE), InvoiceType.SALES));
		criteria.addEqualExpression( bean.getFieldName(IEntityAlias.INVOICE_INVESTMENT), params.isInvestment());
		criteria.addOrder(bean.getFieldName(IEntityAlias.INVOICE_ISSUE_DATE),false);
		criteria.addOrder(bean.getFieldName(IEntityAlias.INVOICE_ID),false);
		return criteria;
	}
	
	public boolean validateVAT(VatManagerParams params, String domainName) throws ManagerBeanException {
		Connection c = null; 
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			IManagerBean bean = BeanManager.getManagerBean(Invoice.class);
			Criteria criteria = getCriteria(params);
			params.setCount( bean.getCount(criteria)); 
			StringBuilder  stmt = new StringBuilder();
			stmt.append(" select count(*) from invoice i WHERE"); 
			stmt.append(DomainManager.getSQLWhereClause("i.domain"));
			stmt.append(" and i.series = ?");
			stmt.append(" and i.number BETWEEN ? AND ?");
			stmt.append(" and i.type != 1");
			stmt.append(" and i.id not in ( ");
			stmt.append("  select inv.id from invoice inv");
			stmt.append("   where ");
			stmt.append(DomainManager.getSQLWhereClause("inv.domain"));
			stmt.append("   and inv.issue_date >= ?");
			stmt.append("   and inv.issue_date <= ?");
			stmt.append("   and (inv.security_level = ?");
			if (params.getSecurityLevel() == SecurityLevel.OFFICIAL) {
				stmt.append("   or inv.security_level is null");			
			}
			stmt.append(" )");
			if (params.getFromSeries() != null) {
				stmt.append("   and inv.series >= ? ");
			}
			if (params.getFromNumber() != null) {
				stmt.append("   and inv.number >= ?");
			}
			if (params.getToSeries() != null) {
				stmt.append("   and inv.series <= ?"); 
			}
			if (params.getToSeries() != null) {
				stmt.append("   and inv.number <= ?");
			}
			stmt.append("   and inv.type != 1");
			stmt.append("   and inv.investment = ?");
			stmt.append(" )");
			c = DatabaseUtil.getConnection(domainName);
			ps = c .prepareStatement(stmt.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			int i = 0;
			ps.setString(++i, params.getSeries().getCode());
			ps.setInt(++i, params.getFirstNumber());
			ps.setInt(++i, (params.getFirstNumber() + params.getCount() - 1));
			ps.setDate(++i, new java.sql.Date(params.getPeriod().getInitiationDate().getTime()) );
			ps.setDate(++i, new java.sql.Date(params.getPeriod().getDeadline().getTime()) );
			ps.setInt(++i, params.getSecurityLevel().ordinal() );
			if (params.getFromSeries() != null) {
				ps.setString(++i, params.getFromSeries().getCode());
			}
			if (params.getFromNumber() != null) {
				ps.setInt(++i, params.getFromNumber());
			}
			if (params.getToSeries() != null) {
				ps.setString(++i, params.getToSeries().getCode());
			}
			if (params.getToSeries() != null) {
				ps.setInt(++i, params.getToNumber());
			}
			ps.setInt(++i, params.isInvestment()?1:0);
			rs = ps.executeQuery();
			int count = 0;
			while (rs.next()) {
				count = rs.getInt(1);
			}
			return (count ==  0);
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} catch (AonConnectionException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(c);
		}
		
		
	}
	
}
