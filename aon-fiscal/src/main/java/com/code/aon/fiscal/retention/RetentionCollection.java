package com.code.aon.fiscal.retention;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.config.enumeration.WithholdingType;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.fiscal.enumeration.InvoiceReportOrder;
import net.aonsolutions.core.pool.AonConnectionException;

public class RetentionCollection {
	private static final String WITHHOLDING_TYPE = "withholding_type";
	private static final String PERCENTAGE = "percentage";
	private static final String DOCUMENT = "document";
	private static final String BASE = "base";
	private static final String QUOTA = "quota";
	
	private static final String TAX_DATE = "i.tax_date";
	private static final String ISSUE_DATE = "i.issue_date";
	
	public List<Retention> getRetentionList(RetentionCollectionParameters params) throws ManagerBeanException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = DatabaseUtil.getConnection(params.getDomainName());

			StringWriter stmt = new StringWriter();
			stmt.append("SELECT it.withholding_type " + WITHHOLDING_TYPE);
			if (params.isByPercent()) {
				stmt.append(",it.percentage " + PERCENTAGE);
			}
			stmt.append(",COUNT(DISTINCT i.rdocument) " + DOCUMENT);
			stmt.append(",SUM(it.base) " + BASE);
			stmt.append(",SUM( IF(it.quota != 0,it.quota,ROUND(it.base * it.percentage / 100, 2) ) ) " + QUOTA);
			stmt.append(" FROM invoice_tax it ");
			stmt.append(" INNER JOIN invoice_detail id ON (it.invoice_detail = id.id)"); 
			stmt.append(" INNER JOIN invoice i ON (id.invoice = i.id)"); 
			stmt.append(" WHERE " + DomainManager.getSQLWhereClause("it.domain"));
			if (params.isToCustomer()) {
				stmt.append(" AND i.type = 1 ");	// Ventas
			} else {
				stmt.append(" AND i.type != 1 ");	// No Ventas
			}
			stmt.append(" AND it.tax_type = 2");
			if (params.getFromInvoiceDate() != null) {
				stmt.append(" AND "+(params.isTaxDateEnabled()?TAX_DATE:ISSUE_DATE)+" >= ?");
			}
			if (params.getToInvoiceDate() != null) {
				stmt.append(" AND "+(params.isTaxDateEnabled()?TAX_DATE:ISSUE_DATE)+" <= ?");
			}
			if (!StringUtils.isEmpty(params.getFromSeries())) {
				stmt.append(" AND i.series >= ?");
			}
			if (!StringUtils.isEmpty(params.getToSeries())) {
				stmt.append(" AND i.series <= ?");
			}
			if (params.getFromNumber() != null) {
				stmt.append(" AND i.number >= ?");
			}
			if (params.getToNumber() != null) {
				stmt.append(" AND i.number <= ?");
			}
			if (params.getSecurityLevel() != null) {
				stmt.append(" AND i.security_level = " + params.getSecurityLevel().ordinal());
			}
			stmt.append(" GROUP BY it.withholding_type");
			if (params.isByPercent()) {
				stmt.append(",it.percentage");
			}
			
			ps = conn.prepareStatement(stmt.toString(),ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			int i = 0;
			if (params.getFromInvoiceDate() != null) {
				ps.setDate(++i, new java.sql.Date( params.getFromInvoiceDate().getTime()));
			}
			if (params.getToInvoiceDate() != null) {
				ps.setDate(++i, new java.sql.Date( params.getToInvoiceDate().getTime()));
			}
			
			if (!StringUtils.isEmpty(params.getFromSeries())) {
				ps.setString(++i, params.getFromSeries());
			}
			if (!StringUtils.isEmpty(params.getToSeries())) {
				ps.setString(++i, params.getToSeries());
			}
			
			if (params.getFromNumber() != null) {
				ps.setInt(++i, params.getFromNumber());
			}
			if (params.getToNumber() != null) {
				ps.setInt(++i, params.getToNumber());
			}

			rs = ps.executeQuery();
			List<Retention> retentions = new LinkedList<Retention>();
			while (rs.next()) {
				Retention ret = new Retention();
				ret.setWithholdingType(WithholdingType.values()[rs.getInt(WITHHOLDING_TYPE)]);
				if (params.isByPercent()) {
					ret.setPercent(rs.getDouble(PERCENTAGE));
				}
				ret.setCount(rs.getInt(DOCUMENT));
				ret.setBase(rs.getDouble(BASE));
				ret.setQuota(rs.getDouble(QUOTA));
				retentions.add(ret);
			}
			return retentions;
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} catch (AonConnectionException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
	}

	public List<Retention> getRetentionDetailList(RetentionCollectionParameters params, InvoiceReportOrder order) throws ManagerBeanException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = DatabaseUtil.getConnection(params.getDomainName());

			StringWriter stmt = new StringWriter();
			stmt.append(" SELECT i.type,i.transaction,i.investment,i.tax_date,i.issue_date,i.reference_code,i.series,i.number,i.rdocument,i.rname ");
			stmt.append("  ,it.percentage,SUM(it.base) ");
			stmt.append("  ,SUM( IF(it.quota != 0,it.quota,ROUND(it.base * it.percentage / 100, 2) ) ) IVA");
			stmt.append("  ,it.withholding_type ");
			stmt.append("  FROM invoice_tax it ");
			stmt.append("  INNER JOIN invoice_detail id ON (it.invoice_detail = id.id) ");
			stmt.append("  INNER JOIN invoice i ON (id.invoice = i.id) ");
			stmt.append(" WHERE " + DomainManager.getSQLWhereClause("it.domain"));
			if (params.isToCustomer()) {
				stmt.append("  AND i.type = 1");	// Ventas
			} else {
				stmt.append("  AND i.type != 1");	// No Ventas
			}
			stmt.append(" AND it.tax_type = 2");
			if (params.getFromInvoiceDate() != null) {
				stmt.append(" AND "+(params.isTaxDateEnabled()?TAX_DATE:ISSUE_DATE)+" >= ?");
			}
			if (params.getToInvoiceDate() != null) {
				stmt.append(" AND "+(params.isTaxDateEnabled()?TAX_DATE:ISSUE_DATE)+" <= ?");
			}
			if (!StringUtils.isEmpty(params.getFromSeries())) {
				stmt.append(" AND i.series >= ?");
			}
			if (!StringUtils.isEmpty(params.getToSeries())) {
				stmt.append(" AND i.series <= ?");
			}
			if (params.getFromNumber() != null) {
				stmt.append(" AND i.number >= ?");
			}
			if (params.getToNumber() != null) {
				stmt.append(" AND i.number <= ?");
			}
			if (params.getPercent() != null) {
				stmt.append(" AND it.percentage = ?");
			}
			if (params.getWithholdingType() != null) {
				stmt.append(" AND it.withholding_type = ?");	
			}
			if (params.getSecurityLevel() != null) {
				stmt.append(" AND i.security_level = " + params.getSecurityLevel().ordinal());
			}
			stmt.append(" GROUP BY i.type,i.transaction,i.investment,i.tax_date,i.issue_date,i.reference_code,i.rdocument,i.rname ");
			stmt.append("  ,it.percentage,it.withholding_type");
			if (order == null) {
				stmt.append(" ORDER BY vatType,i.transaction,i.tax_date,i.reference_code");
			} else if (order == InvoiceReportOrder.INVOICE_DATE) {
				stmt.append(" ORDER BY i.issue_date,i.series,i.number");
			} else if (order == InvoiceReportOrder.TAX_DATE) {
				stmt.append(" ORDER BY i.tax_date,i.series,i.number");
			} else if (order == InvoiceReportOrder.INVOICE_REFERENCE) {
				stmt.append(" ORDER BY i.reference_code");
			} else if (order == InvoiceReportOrder.INVOICE_ORDER_NUMBER) {
				stmt.append(" ORDER BY vatType,i.series,i.number");
			} else if (order == InvoiceReportOrder.INVOICE_REGISTRY_DOCUMENT) {
				stmt.append(" ORDER BY i.rdocument,i.series,i.number");
			} else if (order == InvoiceReportOrder.INVOICE_REGISTRY_NAME) {
				stmt.append(" ORDER BY i.rname,i.series,i.number");
			}
			ps = conn.prepareStatement(stmt.toString(),ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			int i = 0;
			if (params.getFromInvoiceDate() != null) {
				ps.setDate(++i, new java.sql.Date( params.getFromInvoiceDate().getTime()));
			}
			if (params.getToInvoiceDate() != null) {
				ps.setDate(++i, new java.sql.Date( params.getToInvoiceDate().getTime()));
			}
			if (!StringUtils.isEmpty(params.getFromSeries())) {
				ps.setString(++i, params.getFromSeries());
			}
			if (!StringUtils.isEmpty(params.getToSeries())) {
				ps.setString(++i, params.getToSeries());
			}
			if (params.getFromNumber() != null) {
				ps.setInt(++i, params.getFromNumber());
			}
			if (params.getToNumber() != null) {
				ps.setInt(++i, params.getToNumber());
			}
			if (params.getPercent() != null) {
				ps.setDouble(++i, params.getPercent());
			}
			if (params.getWithholdingType() != null) {
				ps.setInt(++i, params.getWithholdingType().ordinal());
			}
			rs = ps.executeQuery();
			List<Retention> rets = new LinkedList<Retention>();
			while (rs.next()) {
				Retention ret = new Retention();
				InvoiceType type = InvoiceType.values()[rs.getInt(1)];
				ret.setInvoiceType( type );
				InvoiceTransactionType transaction = InvoiceTransactionType.values()[rs.getInt(2)];
				ret.setTransactionType(transaction);
				ret.setInvestment( rs.getBoolean(3) );
				ret.setDate(rs.getDate(4));
				ret.setInvoiceDate(rs.getDate(5));
				ret.setReference(rs.getString(6));
				ret.setSeries(rs.getString(7));
				ret.setNumber(rs.getInt(8));
				ret.setDocument(rs.getString(9));
				ret.setName(rs.getString(10));
				ret.setPercent(rs.getDouble(11));
				ret.setBase(rs.getDouble(12));
				ret.setQuota(rs.getDouble(13));
				ret.setWithholdingType(WithholdingType.values()[rs.getInt(14)]);
				rets.add(ret);
			}
			return rets;
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} catch (AonConnectionException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
	}
	
	public List<Retention> getGroupedRetentionDetailList(RetentionCollectionParameters params, InvoiceReportOrder order) throws ManagerBeanException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = DatabaseUtil.getConnection(params.getDomainName());

			StringWriter stmt = new StringWriter();
			stmt.append(" SELECT i.rdocument,i.rname,SUM(it.base) ");
			stmt.append("  ,SUM( IF(it.quota != 0,it.quota,ROUND(it.base * it.percentage / 100, 2) ) ) IVA");
			stmt.append("  FROM invoice_tax it ");
			stmt.append("  INNER JOIN invoice_detail id ON (it.invoice_detail = id.id) ");
			stmt.append("  INNER JOIN invoice i ON (id.invoice = i.id) ");
			stmt.append(" WHERE " + DomainManager.getSQLWhereClause("it.domain"));
			if (params.isToCustomer()) { 
				stmt.append(" AND i.type = 1");	// Ventas
			} else {
				stmt.append(" AND i.type != 1"); // No Ventas	
			}
			stmt.append(" AND it.tax_type = 2");
			if (params.getFromInvoiceDate() != null) {
				stmt.append(" AND "+(params.isTaxDateEnabled()?TAX_DATE:ISSUE_DATE)+" >= ?");
			}
			if (params.getToInvoiceDate() != null) {
				stmt.append(" AND "+(params.isTaxDateEnabled()?TAX_DATE:ISSUE_DATE)+" <= ?");
			}
			if (!StringUtils.isEmpty(params.getFromSeries())) {
				stmt.append(" AND i.series >= ?");
			}
			if (!StringUtils.isEmpty(params.getToSeries())) {
				stmt.append(" AND i.series <= ?");
			}
			if (params.getFromNumber() != null) {
				stmt.append(" AND i.number >= ?");
			}
			if (params.getToNumber() != null) {
				stmt.append(" AND i.number <= ?");
			}
			if (params.getPercent() != null) {
				stmt.append(" AND it.percentage = ?");
			}
			if (params.getWithholdingType() != null) {
				stmt.append(" AND it.withholding_type = ?");	
			}
			if (params.getSecurityLevel() != null) {
				stmt.append(" AND i.security_level = " + params.getSecurityLevel().ordinal());
			}
			stmt.append(" GROUP BY i.rdocument,i.rname");
			stmt.append(" ORDER BY i.rdocument,i.rname");
			ps = conn.prepareStatement(stmt.toString(),ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			int i = 0;
			if (params.getFromInvoiceDate() != null) {
				ps.setDate(++i, new java.sql.Date( params.getFromInvoiceDate().getTime()));
			}
			if (params.getToInvoiceDate() != null) {
				ps.setDate(++i, new java.sql.Date( params.getToInvoiceDate().getTime()));
			}
			if (!StringUtils.isEmpty(params.getFromSeries())) {
				ps.setString(++i, params.getFromSeries());
			}
			if (!StringUtils.isEmpty(params.getToSeries())) {
				ps.setString(++i, params.getToSeries());
			}
			if (params.getFromNumber() != null) {
				ps.setInt(++i, params.getFromNumber());
			}
			if (params.getToNumber() != null) {
				ps.setInt(++i, params.getToNumber());
			}
			if (params.getPercent() != null) {
				ps.setDouble(++i, params.getPercent());
			}
			if (params.getWithholdingType() != null) {
				ps.setInt(++i, params.getWithholdingType().ordinal());
			}
			rs = ps.executeQuery();
			List<Retention> rets = new LinkedList<Retention>();
			while (rs.next()) {
				Retention ret = new Retention();
				ret.setDocument(rs.getString(1));
				ret.setName(rs.getString(2));
				ret.setBase(rs.getDouble(3));
				ret.setQuota(rs.getDouble(4));
				rets.add(ret);
			}
			return rets;
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} catch (AonConnectionException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
	}
}
