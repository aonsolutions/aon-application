package com.code.aon.stat.engine;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.commercial.enumeration.OfferStatus;
import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.stat.Stat;
import com.code.aon.stat.StatParams;

public class StatEngine implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private List<Integer> currentUserWorkPlacesIds;

	public StatEngine() {
	}
	public StatEngine(List<Integer> currentUserWorkPlacesIds) {
		this.currentUserWorkPlacesIds = currentUserWorkPlacesIds;
	}

	private String getCurrentUserWorkPlacesIdsList() {
		String ids = "";
		ids = StringUtils.join(currentUserWorkPlacesIds, ",");
		return ids;
	}

	private void addCurrentUserWorkPlacesSQLClause(StringBuffer stmt) {
		stmt.append( " AND id.workplace IN ("+getCurrentUserWorkPlacesIdsList() + ")" );
	}

	public Collection<Stat> getYearStats(StatParams params)
			throws ManagerBeanException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = DatabaseUtil.getConnection(params.getDomainName());

			StringBuffer stmt = new StringBuffer();
			stmt.append("SELECT YEAR(i.issue_date) YEAR,COUNT(DISTINCT i.id),");
			stmt.append(" SUM(id.taxable_base)");
			stmt.append(" FROM invoice_detail id  ");
			stmt.append(" INNER JOIN invoice i ON (id.invoice = i.id)");
			stmt.append(" WHERE " + DomainManager.getSQLWhereClause("id.domain"));
			if (params.getFromDate() != null) {
				stmt.append(" AND i.issue_date >= ?");
			}
			if (params.getToDate() != null) {
				stmt.append(" AND i.issue_date <= ?");
			}
			if (params.getInvoiceType() != null) {
				stmt.append(" AND i.type = ?");
			}
			if (params.getWorkPlace() != null && params.getWorkPlace().getId() != null){
				stmt.append(" AND id.workplace = ?");
			} else {
				addCurrentUserWorkPlacesSQLClause(stmt);
			}
			stmt.append(" GROUP BY YEAR(i.issue_date)");
			stmt.append(" ORDER BY YEAR(i.issue_date) DESC");
			ps = conn.prepareStatement(stmt.toString(),ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			if (params.getFromDate() != null) {
				ps.setDate(1, new java.sql.Date(params.getFromDate().getTime()));
			}
			if (params.getToDate() != null) {
				ps.setDate(2, new java.sql.Date(params.getToDate().getTime()));
			}
			if (params.getInvoiceType() != null) {
				ps.setInt(3, new Integer(params.getInvoiceType()));
			}
			if (params.getWorkPlace() != null && params.getWorkPlace().getId() != null){
				ps.setInt(4, new Integer(params.getWorkPlace().getId()));
			}

			rs = ps.executeQuery();
			List<Stat> stats = new LinkedList<Stat>();
			while (rs.next()) {
				Stat stat = new Stat();
				stat.setKey(rs.getInt(1));
				stat.setName(rs.getString(1));
				int count = rs.getInt(2);
				double amount = rs.getDouble(3);
				stat.setNumInvoice(count);
				stat.setAmount(amount);
				stat.setAverageAmount(CommonUtil.round(amount / count));
				stats.add(stat);
			}
			return stats;
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

	public Collection<Stat> getMonthsStats(StatParams params)
			throws ManagerBeanException {
		List<Stat> stats = new LinkedList<Stat>();

		for (int i = 0; i < 12; i++) {
			stats.add(i, new Stat());
			stats.get(i).setKey(i);
			stats.get(i).setName(Month.getMonthByValue(i).getName(params.getLocale()));
		}

		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = DatabaseUtil.getConnection(params.getDomainName());
			
			StringBuffer stmt = new StringBuffer();
			stmt.append("SELECT MONTH(i.issue_date) MONTH,COUNT(DISTINCT i.id),");
			stmt.append(" SUM(id.taxable_base)");
			stmt.append(" FROM invoice_detail id  ");
			stmt.append(" INNER JOIN invoice i ON (id.invoice = i.id)");
			stmt.append(" WHERE " + DomainManager.getSQLWhereClause("id.domain"));

			if (params.getFromDate() != null) {
				stmt.append(" AND i.issue_date >= ?");
			}
			if (params.getToDate() != null) {
				stmt.append(" AND i.issue_date <= ?");
			}
			if (params.getInvoiceType() != null) {
				stmt.append(" AND i.type = ?");
			}
			if (params.getWorkPlace() != null && params.getWorkPlace().getId() != null){
				stmt.append(" AND id.workplace = ?");
			} else {
				addCurrentUserWorkPlacesSQLClause(stmt);
			}
			stmt.append(" GROUP BY MONTH(i.issue_date)");
			stmt.append(" ORDER BY MONTH(i.issue_date)");

			ps = conn.prepareStatement(stmt.toString(), ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			if (params.getFromDate() != null) {
				ps.setDate(1, new java.sql.Date(params.getFromDate().getTime()));
			}
			if (params.getToDate() != null) {
				ps.setDate(2, new java.sql.Date(params.getToDate().getTime()));
			}
			if (params.getInvoiceType() != null) {
				ps.setInt(3, new Integer(params.getInvoiceType()));
			}
			if (params.getWorkPlace() != null && params.getWorkPlace().getId() != null){
				ps.setInt(4, new Integer(params.getWorkPlace().getId()));
			}
			rs = ps.executeQuery();

			while (rs.next()) {

				Stat stat = stats.get((Month.getMonthByValue(rs.getInt(1) - 1)).getValue());
				stat.setKey(rs.getInt(1)-1);
				stat.setName(Month.getMonthByValue(rs.getInt(1) - 1).getName(params.getLocale()));
				int count = rs.getInt(2);
				double amount = rs.getDouble(3);
				stat.setNumInvoice(stat.getNumInvoice() + count);
				stat.setAmount(stat.getAmount() + amount);
				stat.setAverageAmount(CommonUtil.round(stat.getAmount() / stat.getNumInvoice()));
				stats.set(Month.getMonthByValue(rs.getInt(1) - 1).getValue(), stat);

			}
			return stats;
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

	public Collection<Stat> getDaysStats(StatParams params)
			throws ManagerBeanException {

		List<Stat> stats = new LinkedList<Stat>();

		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = DatabaseUtil.getConnection(params.getDomainName());

			StringBuffer stmt = new StringBuffer();
			stmt.append("SELECT i.issue_date DAY,COUNT(DISTINCT i.id),SUM(id.taxable_base)");
			stmt.append(" FROM invoice_detail id  ");
			stmt.append(" INNER JOIN invoice i ON (id.invoice = i.id)");
			stmt.append(" WHERE " + DomainManager.getSQLWhereClause("id.domain"));

			if (params.getFromDate() != null) {
				stmt.append(" AND i.issue_date >= ?");
			}
			if (params.getToDate() != null) {
				stmt.append(" AND i.issue_date <= ?");
			}
			if (params.getInvoiceType() != null) {
				stmt.append(" AND i.type = ?");
			}
			if (params.getWorkPlace() != null && params.getWorkPlace().getId() != null){
				stmt.append(" AND id.workplace = ?");
			} else {
				addCurrentUserWorkPlacesSQLClause(stmt);
			}
			stmt.append(" GROUP BY DAY(i.issue_date)");
			stmt.append(" ORDER BY DAY(i.issue_date)");

			ps = conn.prepareStatement(stmt.toString(), ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			if (params.getFromDate() != null) {
				ps.setDate(1, new java.sql.Date(params.getFromDate().getTime()));
			}
			if (params.getToDate() != null) {
				ps.setDate(2, new java.sql.Date(params.getToDate().getTime()));
			}
			if (params.getInvoiceType() != null) {
				ps.setInt(3, new Integer(params.getInvoiceType()));
			}
			if (params.getWorkPlace() != null && params.getWorkPlace().getId() != null){
				ps.setInt(4, new Integer(params.getWorkPlace().getId()));
			}
			SimpleDateFormat format = new SimpleDateFormat();
			format.applyPattern("dd - MM - yyyy");

			rs = ps.executeQuery();
			while (rs.next()) {
				Stat stat = new Stat();
				Date date = rs.getDate(1);
				Calendar c = Calendar.getInstance();
				c.setTime(date);
				stat.setKey(c.get(Calendar.DAY_OF_MONTH));
				// stat.setExtra(rs.getInt(1));
				stat.setName(format.format(date));
				int count = rs.getInt(2);
				double amount = rs.getDouble(3);
				stat.setNumInvoice(count);
				stat.setAmount(amount);
				stat.setAverageAmount(CommonUtil.round(amount / count));
				stats.add(stat);
			}
			return stats;
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

	public Collection<Stat> getCustomerStats(StatParams params)
			throws ManagerBeanException {

		List<Stat> stats = new LinkedList<Stat>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = DatabaseUtil.getConnection(params.getDomainName());

			StringBuffer stmt = new StringBuffer();
			stmt.append("SELECT r.id,r.name,COUNT(DISTINCT i.id),SUM(id.taxable_base)");
			stmt.append(" FROM invoice_detail id  ");
			stmt.append(" INNER JOIN invoice i ON (id.invoice = i.id)");
			stmt.append(" INNER JOIN registry r ON (i.registry = r.id)");
			stmt.append(" WHERE " + DomainManager.getSQLWhereClause("id.domain"));

			if (params.getFromDate() != null) {
				stmt.append(" AND i.issue_date >= ?");
			}
			if (params.getToDate() != null) {
				stmt.append(" AND i.issue_date <= ?");
			}
			if (params.getInvoiceType() != null) {
				stmt.append(" AND i.type = ?");
			}
			if (params.getWorkPlace() != null && params.getWorkPlace().getId() != null){
				stmt.append(" AND id.workplace = ?");
			} else {
				addCurrentUserWorkPlacesSQLClause(stmt);
			}
			stmt.append(" GROUP BY r.id,r.name");
			stmt.append(" ORDER BY SUM(id.taxable_base) DESC,r.id,r.name");

			ps = conn.prepareStatement(stmt.toString(), ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			if (params.getFromDate() != null) {
				ps.setDate(1, new java.sql.Date(params.getFromDate().getTime()));
			}
			if (params.getToDate() != null) {
				ps.setDate(2, new java.sql.Date(params.getToDate().getTime()));
			}
			if (params.getInvoiceType() != null) {
				ps.setInt(3, new Integer(params.getInvoiceType()));
			}
			if (params.getWorkPlace() != null && params.getWorkPlace().getId() != null){
				ps.setInt(4, new Integer(params.getWorkPlace().getId()));
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				Stat stat = new Stat();
				stat.setKey(rs.getInt(1));
				stat.setName(rs.getString(2));
				int count = rs.getInt(3);
				double amount = rs.getDouble(4);
				stat.setNumInvoice(count);
				stat.setAmount(amount);
				stat.setAverageAmount(CommonUtil.round(amount / count));
				stats.add(stat);
			}
			return stats;
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

	public Collection<Stat> getCategoryStats(StatParams params)
			throws ManagerBeanException {

		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = DatabaseUtil.getConnection(params.getDomainName());

			StringBuffer stmt = new StringBuffer();
			stmt.append(" SELECT YEAR(i.issue_date) YEAR,c.id,c.name,COUNT(DISTINCT i.id),SUM(id.taxable_base)");
			stmt.append(" FROM invoice_detail id  ");
			stmt.append(" INNER JOIN invoice i ON (id.invoice = i.id)");
			stmt.append(" INNER JOIN item ON (item.id = id.item)");
			stmt.append(" INNER JOIN product p ON (p.id = item.product)");
			stmt.append(" INNER JOIN pcategory c ON (p.category = c.id)");
			stmt.append(" WHERE " + DomainManager.getSQLWhereClause("id.domain"));

			if (params.getFromDate() != null) {
				stmt.append(" AND i.issue_date >= ?");
			}
			if (params.getToDate() != null) {
				stmt.append(" AND i.issue_date <= ?");
			}
			if (params.getInvoiceType() != null) {
				stmt.append(" AND i.type = ?");
			}
			if (params.getWorkPlace() != null && params.getWorkPlace().getId() != null){
				stmt.append(" AND id.workplace = ?");
			} else {
				addCurrentUserWorkPlacesSQLClause(stmt);
			}
			stmt.append(" GROUP BY c.id");
			stmt.append(" ORDER BY c.name");

			ps = conn.prepareStatement(stmt.toString(), ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			if (params.getFromDate() != null) {
				ps.setDate(1, new java.sql.Date(params.getFromDate().getTime()));
			}
			if (params.getToDate() != null) {
				ps.setDate(2, new java.sql.Date(params.getToDate().getTime()));
			}
			if (params.getInvoiceType() != null) {
				ps.setInt(3, new Integer(params.getInvoiceType()));
			}
			if (params.getWorkPlace() != null && params.getWorkPlace().getId() != null){
				ps.setInt(4, new Integer(params.getWorkPlace().getId()));
			}

			rs = ps.executeQuery();
			List<Stat> stats = new LinkedList<Stat>();
			while (rs.next()) {
				Stat stat = new Stat();
				stat.setKey(rs.getInt(2));
				stat.setName(rs.getString(3));
				int count = rs.getInt(4);
				double amount = rs.getDouble(5);
				stat.setNumInvoice(count);
				stat.setAmount(amount);
				stat.setAverageAmount(CommonUtil.round(amount / count));
				stats.add(stat);
			}
			return stats;
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

	public Collection<Stat> getCategoryProductsStats(StatParams params)
			throws ManagerBeanException {

		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = DatabaseUtil.getConnection(params.getDomainName());


			StringBuffer stmt = new StringBuffer();
			stmt.append("SELECT p.id,p.name,COUNT(DISTINCT i.id),SUM(id.taxable_base),SUM(id.quantity)");
			stmt.append(" FROM invoice_detail id ");
			stmt.append(" INNER JOIN invoice i ON (id.invoice = i.id)");
			stmt.append(" INNER JOIN item ON (item.id = id.item)");
			stmt.append(" INNER JOIN product p ON (p.id = item.product)");
			stmt.append(" INNER JOIN pcategory c ON (p.category = c.id)");
			stmt.append(" WHERE " + DomainManager.getSQLWhereClause("id.domain"));

			if (params.getFromDate() != null) {
				stmt.append(" AND i.issue_date >= ?");
			}
			if (params.getToDate() != null) {
				stmt.append(" AND i.issue_date <= ?");
			}

			if (params.getCategory() != null) {
				stmt.append(" AND p.category = ?");
			}

			if (params.getInvoiceType() != null) {
				stmt.append(" AND i.type = ?");
			}
			if (params.getWorkPlace() != null && params.getWorkPlace().getId() != null){
				stmt.append(" AND id.workplace = ?");
			} else {
				addCurrentUserWorkPlacesSQLClause(stmt);
			}
			stmt.append(" GROUP BY p.name");
			stmt.append(" ORDER BY SUM(id.taxable_base) DESC,p.id,p.name");

			ps = conn.prepareStatement(stmt.toString(), ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			if (params.getFromDate() != null) {
				ps.setDate(1, new java.sql.Date(params.getFromDate().getTime()));
			}
			if (params.getToDate() != null) {
				ps.setDate(2, new java.sql.Date(params.getToDate().getTime()));
			}
			if (params.getCategory() != null) {
				ps.setInt(3, params.getCategory());
			}
			if (params.getInvoiceType() != null) {
				ps.setInt(4, new Integer(params.getInvoiceType()));
			}
			if (params.getWorkPlace() != null && params.getWorkPlace().getId() != null){
				ps.setInt(5, new Integer(params.getWorkPlace().getId()));
			}

			rs = ps.executeQuery();
			List<Stat> stats = new LinkedList<Stat>();
			while (rs.next()) {
				Stat stat = new Stat();
				stat.setKey(rs.getInt(1));
				stat.setName(rs.getString(2));
				int count = rs.getInt(3);
				stat.setNumInvoice(count);
				double amount = rs.getDouble(4);
				stat.setAmount(amount);
				stat.setProductCount(rs.getInt(5));
				stat.setAverageAmount(CommonUtil.round(amount / count));
				stats.add(stat);
			}
			return stats;
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

	public Collection<Stat> getCategoryCustomerStats(StatParams params)
			throws ManagerBeanException {

		List<Stat> stats = new LinkedList<Stat>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = DatabaseUtil.getConnection(params.getDomainName());

			StringBuffer stmt = new StringBuffer();
			stmt.append("SELECT r.id,r.name, COUNT(DISTINCT i.id), SUM(id.taxable_base)");
			stmt.append(" FROM invoice_detail id  ");
			stmt.append(" INNER JOIN invoice i ON (id.invoice = i.id)");
			stmt.append(" INNER JOIN registry r ON (i.registry = r.id)");
			stmt.append(" INNER JOIN item ON (item.id = id.item)");
			stmt.append(" INNER JOIN product p ON (p.id = item.product)");
			stmt.append(" INNER JOIN pcategory c ON (p.category = c.id)");
			stmt.append(" WHERE " + DomainManager.getSQLWhereClause("id.domain"));

			if (params.getFromDate() != null) {
				stmt.append(" AND i.issue_date >= ?");
			}
			if (params.getToDate() != null) {
				stmt.append(" AND i.issue_date <= ?");
			}
			if (params.getCategory() != null) {
				stmt.append(" AND p.category = ?");
			}
			if (params.getInvoiceType() != null) {
				stmt.append(" AND i.type = ?");
			}
			if (params.getWorkPlace() != null && params.getWorkPlace().getId() != null){
				stmt.append(" AND id.workplace = ?");
			} else {
				addCurrentUserWorkPlacesSQLClause(stmt);
			}
			stmt.append(" GROUP BY r.id");
			stmt.append(" ORDER BY YEAR(i.issue_date),c.id");

			ps = conn.prepareStatement(stmt.toString(), ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			if (params.getFromDate() != null) {
				ps.setDate(1, new java.sql.Date(params.getFromDate().getTime()));
			}
			if (params.getToDate() != null) {
				ps.setDate(2, new java.sql.Date(params.getToDate().getTime()));
			}
			if (params.getCategory() != null) {
				ps.setInt(3, params.getCategory());
			}
			if (params.getInvoiceType() != null) {
				ps.setInt(4, new Integer(params.getInvoiceType()));
			}
			if (params.getWorkPlace() != null && params.getWorkPlace().getId() != null){
				ps.setInt(5, new Integer(params.getWorkPlace().getId()));
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				Stat stat = new Stat();
				stat.setKey(rs.getInt(1));
				stat.setName(rs.getString(2));
				int count = rs.getInt(3);
				double amount = rs.getDouble(4);
				stat.setNumInvoice(count);
				stat.setAmount(amount);
				stat.setAverageAmount(CommonUtil.round(amount / count));
				stats.add(stat);
			}
			return stats;
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

	public Collection<Stat> getProductRegistryStats(StatParams params)
			throws ManagerBeanException {

		List<Stat> stats = new LinkedList<Stat>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = DatabaseUtil.getConnection(params.getDomainName());

			StringBuffer stmt = new StringBuffer();
			stmt.append("SELECT r.id,r.name, COUNT(DISTINCT i.id), SUM(id.taxable_base)");
			stmt.append("FROM invoice_detail id  ");
			stmt.append(" INNER JOIN invoice i ON (id.invoice = i.id)");
			stmt.append(" INNER JOIN registry r ON (i.registry = r.id)");
			stmt.append(" INNER JOIN item ON (item.id = id.item)");
			stmt.append(" INNER JOIN product p ON (p.id = item.product)");
			stmt.append(" INNER JOIN pcategory c ON (p.category = c.id)");
			stmt.append(" WHERE " + DomainManager.getSQLWhereClause("id.domain"));

			if (params.getFromDate() != null) {
				stmt.append(" AND i.issue_date >= ?");
			}
			if (params.getToDate() != null) {
				stmt.append(" AND i.issue_date <= ?");
			}
			if (params.getProduct() != null) {
				stmt.append(" AND p.id = ?");
			}
			if (params.getInvoiceType() != null) {
				stmt.append(" AND i.type = ?");
			}
			if (params.getWorkPlace() != null && params.getWorkPlace().getId() != null){
				stmt.append(" AND id.workplace = ?");
			} else {
				addCurrentUserWorkPlacesSQLClause(stmt);
			}
			stmt.append(" GROUP BY r.id,r.name");
			stmt.append(" ORDER BY YEAR(i.issue_date),c.id");

			ps = conn.prepareStatement(stmt.toString(), ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			if (params.getFromDate() != null) {
				ps.setDate(1, new java.sql.Date(params.getFromDate().getTime()));
			}
			if (params.getToDate() != null) {
				ps.setDate(2, new java.sql.Date(params.getToDate().getTime()));
			}
			if (params.getProduct() != null) {
				ps.setInt(3, params.getProduct());
			}
			if (params.getInvoiceType() != null) {
				ps.setInt(4, new Integer(params.getInvoiceType()));
			}
			if (params.getWorkPlace() != null && params.getWorkPlace().getId() != null){
				ps.setInt(5, new Integer(params.getWorkPlace().getId()));
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				Stat stat = new Stat();
				stat.setKey(rs.getInt(1));
				stat.setName(rs.getString(2));
				int count = rs.getInt(3);
				double amount = rs.getDouble(4);
				stat.setNumInvoice(count);
				stat.setAmount(amount);
				stat.setAverageAmount(CommonUtil.round(amount / count));
				stats.add(stat);
			}
			return stats;
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

	public Collection<Stat> getSummaryStats(StatParams params)
			throws ManagerBeanException {
		List<Stat> stats = new LinkedList<Stat>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = DatabaseUtil.getConnection(params.getDomainName());

			StringBuffer stmt = new StringBuffer();
			stmt.append("SELECT MONTH(i.issue_date) MONTH,COUNT(DISTINCT i.id),");
			stmt.append(" SUM(id.taxable_base), YEAR(i.issue_date)");
			stmt.append(" FROM invoice_detail id  ");
			stmt.append(" INNER JOIN invoice i ON (id.invoice = i.id)");
			stmt.append(" WHERE " + DomainManager.getSQLWhereClause("id.domain"));

			if (params.getFromDate() != null) {
				stmt.append(" AND i.issue_date >= ?");
			}
			if (params.getToDate() != null) {
				stmt.append(" AND i.issue_date <= ?");
			}
			if (params.getInvoiceType() != null) {
				stmt.append(" AND i.type = ?");
			}
			if (params.getWorkPlace() != null && params.getWorkPlace().getId() != null){
				stmt.append(" AND id.workplace = ?");
			} else {
				addCurrentUserWorkPlacesSQLClause(stmt);
			}
			stmt.append(" GROUP BY MONTH(i.issue_date)");
			stmt.append(" ORDER BY YEAR(i.issue_date),MONTH(i.issue_date)");

			ps = conn.prepareStatement(
					stmt.toString(), ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			if (params.getFromDate() != null) {
				ps.setDate(1, new java.sql.Date(params.getFromDate().getTime()));
			}
			if (params.getToDate() != null) {
				ps.setDate(2, new java.sql.Date(params.getToDate().getTime()));
			}
			if (params.getInvoiceType() != null) {
				ps.setInt(3, new Integer(params.getInvoiceType()));
			}
			if (params.getWorkPlace() != null && params.getWorkPlace().getId() != null){
				ps.setInt(4, new Integer(params.getWorkPlace().getId()));
			}
			rs = ps.executeQuery();

			while (rs.next()) {

				Stat stat = new Stat();
				stat.setKey(rs.getInt(1));
				stat.setName(Month.getMonthByValue(rs.getInt(1) - 1).getName(
						params.getLocale())
						+ " " + rs.getInt(4));
				int count = rs.getInt(2);
				double amount = rs.getDouble(3);
				stat.setNumInvoice(count);
				stat.setAmount(amount);
				stat.setAverageAmount(CommonUtil.round(amount / count));
				stats.add(stat);

			}
			return stats;
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

	public Collection<Stat> getAbcCustomerStats(StatParams params)
			throws ManagerBeanException {

		List<Stat> stats = new LinkedList<Stat>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = DatabaseUtil.getConnection(params.getDomainName());

			StringBuffer stmt = new StringBuffer();
			stmt.append("SELECT MONTH(i.issue_date) MONTH,COUNT(DISTINCT i.id),SUM(id.taxable_base),YEAR(i.issue_date) YEAR");
			stmt.append(" FROM invoice_detail id  ");
			stmt.append(" INNER JOIN invoice i ON (id.invoice = i.id)");
			stmt.append(" INNER JOIN registry r ON (i.registry = r.id)");
			stmt.append(" WHERE " + DomainManager.getSQLWhereClause("id.domain"));

			if (params.getFromDate() != null) {
				stmt.append(" AND i.issue_date >= ?");
			}
			if (params.getToDate() != null) {
				stmt.append(" AND i.issue_date <= ?");
			}
			if (params.getCustomer() != null) {
				stmt.append(" AND r.id = ?");
			}
			if (params.getInvoiceType() != null) {
				stmt.append(" AND i.type = ?");
			}
			if (params.getWorkPlace() != null && params.getWorkPlace().getId() != null){
				stmt.append(" AND id.workplace = ?");
			} else {
				addCurrentUserWorkPlacesSQLClause(stmt);
			}
			stmt.append(" GROUP BY MONTH(i.issue_date)");
			stmt.append(" ORDER BY YEAR(i.issue_date),MONTH(i.issue_date)");

			ps = conn.prepareStatement(stmt.toString(), ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			if (params.getFromDate() != null) {
				ps.setDate(1, new java.sql.Date(params.getFromDate().getTime()));
			}
			if (params.getToDate() != null) {
				ps.setDate(2, new java.sql.Date(params.getToDate().getTime()));
			}
			if (params.getCustomer() != null) {

				ps.setInt(3, params.getCustomer());
			}
			if (params.getInvoiceType() != null) {
				ps.setInt(4, new Integer(params.getInvoiceType()));
			}
			if (params.getWorkPlace() != null && params.getWorkPlace().getId() != null){
				ps.setInt(5, new Integer(params.getWorkPlace().getId()));
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				Stat stat = new Stat();
				stat.setName(Month.getMonthByValue(rs.getInt(1) - 1).getName(
						params.getLocale())
						+ " " + rs.getInt(4));
				int count = rs.getInt(2);
				double amount = rs.getDouble(3);
				stat.setNumInvoice(count);
				stat.setAmount(amount);
				stat.setAverageAmount(CommonUtil.round(amount / count));
				stats.add(stat);
			}
			return stats;
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

	public Collection<Stat> getABCStatsByCustomer(StatParams params)
			throws ManagerBeanException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = DatabaseUtil.getConnection(params.getDomainName());

			StringBuffer stmt = new StringBuffer();
			stmt.append("SELECT r.id,r.name,COUNT(DISTINCT i.id),SUM(id.taxable_base)");
			stmt.append(" FROM invoice_detail id ");
			stmt.append(" INNER JOIN invoice i ON (id.invoice = i.id)");
			stmt.append(" INNER JOIN registry r ON (i.registry = r.id)");
			stmt.append(" WHERE " + DomainManager.getSQLWhereClause("id.domain"));

			if (params.getFromDate() != null) {
				stmt.append(" AND i.issue_date >= ?");
			}
			if (params.getToDate() != null) {
				stmt.append(" AND i.issue_date <= ?");
			}
			if (params.getInvoiceType() != null) {
				stmt.append(" AND i.type = ?");
			}
			if (params.getWorkPlace() != null && params.getWorkPlace().getId() != null){
				stmt.append(" AND id.workplace = ?");
			} else {
				addCurrentUserWorkPlacesSQLClause(stmt);
			}
			stmt.append(" GROUP BY r.id,r.name");
			stmt.append(" ORDER BY SUM(id.taxable_base) DESC,r.id,r.name");

			ps = conn.prepareStatement(stmt.toString(), ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			if (params.getFromDate() != null) {
				ps.setDate(1, new java.sql.Date(params.getFromDate().getTime()));
			}
			if (params.getToDate() != null) {
				ps.setDate(2, new java.sql.Date(params.getToDate().getTime()));
			}
			if (params.getInvoiceType() != null) {
				ps.setInt(3, new Integer(params.getInvoiceType()));
			}
			if (params.getWorkPlace() != null && params.getWorkPlace().getId() != null){
				ps.setInt(4, new Integer(params.getWorkPlace().getId()));
			}

			rs = ps.executeQuery();
			List<Stat> stats = new LinkedList<Stat>();
			while (rs.next()) {
				Stat stat = new Stat();
				stat.setKey(rs.getInt(1));
				stat.setName(rs.getString(2));
				int count = rs.getInt(3);
				double amount = rs.getDouble(4);
				stat.setNumInvoice(count);
				stat.setAmount(amount);
				stat.setAverageAmount(CommonUtil.round(amount / count));
				stats.add(stat);
			}
			return stats;
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

	public Collection<Stat> getRegistryProductStats(StatParams params)
			throws ManagerBeanException {

		List<Stat> stats = new LinkedList<Stat>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = DatabaseUtil.getConnection(params.getDomainName());

			StringBuffer stmt = new StringBuffer();
			stmt.append("SELECT p.id,p.name, COUNT(DISTINCT i.id), SUM(id.taxable_base)");
			stmt.append(" FROM invoice_detail id  ");
			stmt.append(" INNER JOIN invoice i ON (id.invoice = i.id)");
			stmt.append(" INNER JOIN registry r ON (i.registry = r.id)");
			stmt.append(" INNER JOIN item ON (item.id = id.item)");
			stmt.append(" INNER JOIN product p ON (p.id = item.product)");
			stmt.append(" WHERE " + DomainManager.getSQLWhereClause("id.domain"));

			if (params.getFromDate() != null) {
				stmt.append(" AND i.issue_date >= ?");
			}
			if (params.getToDate() != null) {
				stmt.append(" AND i.issue_date <= ?");
			}
			if (params.getCustomer() != null) {
				stmt.append(" AND r.id = ?");
			}
			if (params.getInvoiceType() != null) {
				stmt.append(" AND i.type = ?");
			}
			if (params.getWorkPlace() != null && params.getWorkPlace().getId() != null){
				stmt.append(" AND id.workplace = ?");
			} else {
				addCurrentUserWorkPlacesSQLClause(stmt);
			}
			stmt.append(" GROUP BY p.id,p.name");
			stmt.append(" ORDER BY SUM(id.taxable_base) DESC,p.id,p.name");

			ps = conn.prepareStatement(stmt.toString(), ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			if (params.getFromDate() != null) {
				ps.setDate(1, new java.sql.Date(params.getFromDate().getTime()));
			}
			if (params.getToDate() != null) {
				ps.setDate(2, new java.sql.Date(params.getToDate().getTime()));
			}
			if (params.getCustomer() != null) {
				ps.setInt(3, params.getCustomer());
			}
			if (params.getInvoiceType() != null) {
				ps.setInt(4, new Integer(params.getInvoiceType()));
			}
			if (params.getWorkPlace() != null && params.getWorkPlace().getId() != null){
				ps.setInt(5, new Integer(params.getWorkPlace().getId()));
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				Stat stat = new Stat();
				stat.setKey(rs.getInt(1));
				stat.setName(rs.getString(2));
				int count = rs.getInt(3);
				double amount = rs.getDouble(4);
				stat.setNumInvoice(count);
				stat.setAmount(amount);
				stat.setAverageAmount(CommonUtil.round(amount / count));
				stats.add(stat);
			}
			return stats;
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

	public Collection<Stat> getABCStatsByProduct(StatParams params)
			throws ManagerBeanException {

		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = DatabaseUtil.getConnection(params.getDomainName());

			StringBuffer stmt = new StringBuffer();
			stmt.append("SELECT p.id,p.code,p.name,COUNT(DISTINCT i.id),SUM(id.taxable_base),SUM(id.quantity)");
			stmt.append(" FROM invoice_detail id ");
			stmt.append(" INNER JOIN invoice i ON (id.invoice = i.id)");
			stmt.append(" INNER JOIN item ON (item.id = id.item)");
			stmt.append(" INNER JOIN product p ON (p.id = item.product)");
			stmt.append(" WHERE " + DomainManager.getSQLWhereClause("id.domain"));

			if (params.getFromDate() != null) {
				stmt.append(" AND i.issue_date >= ?");
			}
			if (params.getToDate() != null) {
				stmt.append(" AND i.issue_date <= ?");
			}
			if (params.getInvoiceType() != null) {
				stmt.append(" AND i.type = ?");
			}
			if (params.getWorkPlace() != null && params.getWorkPlace().getId() != null){
				stmt.append(" AND id.workplace = ?");
			} else {
				addCurrentUserWorkPlacesSQLClause(stmt);
			}
			stmt.append(" GROUP BY p.id,p.code,p.name");
			stmt.append(" ORDER BY SUM(id.taxable_base) DESC,p.id,p.name");

			ps = conn.prepareStatement(stmt.toString(), ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			if (params.getFromDate() != null) {
				ps.setDate(1, new java.sql.Date(params.getFromDate().getTime()));
			}
			if (params.getToDate() != null) {
				ps.setDate(2, new java.sql.Date(params.getToDate().getTime()));
			}
			if (params.getInvoiceType() != null) {
				ps.setInt(3, new Integer(params.getInvoiceType()));
			}
			if (params.getWorkPlace() != null && params.getWorkPlace().getId() != null){
				ps.setInt(4, new Integer(params.getWorkPlace().getId()));
			}

			rs = ps.executeQuery();
			List<Stat> stats = new LinkedList<Stat>();
			while (rs.next()) {
				Stat stat = new Stat();
				stat.setKey(rs.getInt(1));
				stat.setName(rs.getString(2)+" - "+rs.getString(3));
				stat.setNumInvoice(rs.getInt(4));
				double amount = rs.getDouble(5);
				stat.setAmount(amount);
				int count = rs.getInt(6);
				stat.setProductCount(count);
				stat.setAverageAmount((count == 0) ? 0 : CommonUtil.round(amount / count));
				stats.add(stat);
			}
			return stats;
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

	public Collection<Stat> getABCStatsByCategory(StatParams params)
			throws ManagerBeanException {

		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = DatabaseUtil.getConnection(params.getDomainName());

			StringBuffer stmt = new StringBuffer();
			stmt.append("SELECT c.id,c.name,COUNT(DISTINCT i.id),SUM(id.taxable_base)");
			stmt.append(" FROM invoice_detail id  ");
			stmt.append(" INNER JOIN invoice i ON (id.invoice = i.id)");
			stmt.append(" INNER JOIN item ON (item.id = id.item)");
			stmt.append(" INNER JOIN product p ON (p.id = item.product)");
			stmt.append(" INNER JOIN pcategory c ON (p.category = c.id)");
			stmt.append(" WHERE " + DomainManager.getSQLWhereClause("id.domain"));

			if (params.getFromDate() != null) {
				stmt.append(" AND i.issue_date >= ?");
			}
			if (params.getToDate() != null) {
				stmt.append(" AND i.issue_date <= ?");
			}
			if (params.getInvoiceType() != null) {
				stmt.append(" AND i.type = ?");
			}
			if (params.getWorkPlace() != null && params.getWorkPlace().getId() != null){
				stmt.append(" AND id.workplace = ?");
			} else {
				addCurrentUserWorkPlacesSQLClause(stmt);
			}
			stmt.append(" GROUP BY c.id,c.name");
			stmt.append(" ORDER BY SUM(id.taxable_base) DESC,c.id,c.name");

			ps = conn.prepareStatement(stmt.toString(), ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			if (params.getFromDate() != null) {
				ps.setDate(1, new java.sql.Date(params.getFromDate().getTime()));
			}
			if (params.getToDate() != null) {
				ps.setDate(2, new java.sql.Date(params.getToDate().getTime()));
			}
			if (params.getInvoiceType() != null) {
				ps.setInt(3, new Integer(params.getInvoiceType()));
			}
			if (params.getWorkPlace() != null && params.getWorkPlace().getId() != null){
				ps.setInt(4, new Integer(params.getWorkPlace().getId()));
			}

			rs = ps.executeQuery();
			List<Stat> stats = new LinkedList<Stat>();
			while (rs.next()) {
				Stat stat = new Stat();
				stat.setKey(rs.getInt(1));
				stat.setName(rs.getString(2));
				int count = rs.getInt(3);
				double amount = rs.getDouble(4);
				stat.setNumInvoice(count);
				stat.setAmount(amount);
				stat.setAverageAmount(CommonUtil.round(amount / count));
				stats.add(stat);
			}
			return stats;
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

	public Collection<Stat> getCommercialCategoryStats(StatParams params)
			throws ManagerBeanException {

		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = DatabaseUtil.getConnection(params.getDomainName());

			StringBuffer stmt = new StringBuffer();
			stmt.append(" SELECT c.id,c.name,COUNT(DISTINCT o.id),SUM(od.price*od.quantity)");
			stmt.append(" FROM offer_detail od  ");
			stmt.append(" INNER JOIN offer o ON (od.offer = o.id)");
			stmt.append(" INNER JOIN item ON (item.id = od.item)");
			stmt.append(" INNER JOIN product p ON (p.id = item.product)");
			stmt.append(" INNER JOIN pcategory c ON (p.category = c.id)");
			stmt.append(" WHERE " + DomainManager.getSQLWhereClause("od.domain"));

			if (params.getFromDate() != null) {
				stmt.append(" AND o.issue_date >= ?");
			}
			if (params.getToDate() != null) {
				stmt.append(" AND o.issue_date <= ?");
			}

			OfferStatus[] os = params.getOfferStatuses();
			String s = "";
			for (int i = 0; i < os.length; i++) {
				if (os[i] != null) {
					if (i == 0) {
						s = " AND (";
						s = s + "o.status=" + os[i].ordinal() + " ";
					}
					if (i > 0) {
						s = s + "OR ";
						s = s + "o.status=" + os[i].ordinal() + " ";
					}
				}
			}
			if (os.length != 0) {
				s = s + ")";
			}
			stmt.append(s);
			stmt.append(" GROUP BY c.id");
			stmt.append(" ORDER BY SUM(od.price*od.quantity) DESC,c.name");

			ps = conn.prepareStatement(stmt.toString(), ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			if (params.getFromDate() != null) {
				ps.setDate(1, new java.sql.Date(params.getFromDate().getTime()));
			}
			if (params.getToDate() != null) {
				ps.setDate(2, new java.sql.Date(params.getToDate().getTime()));
			}

			rs = ps.executeQuery();
			List<Stat> stats = new LinkedList<Stat>();
			while (rs.next()) {
				Stat stat = new Stat();
				stat.setKey(rs.getInt(1));
				stat.setName(rs.getString(2));
				int count = rs.getInt(3);
				double amount = rs.getDouble(4);
				stat.setNumInvoice(count);
				stat.setAmount(amount);
				stat.setAverageAmount(CommonUtil.round(amount / count));
				stats.add(stat);
			}
			return stats;
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

	public Collection<Stat> getCommercialSellerStats(StatParams params)
			throws ManagerBeanException {

		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = DatabaseUtil.getConnection(params.getDomainName());

			StringBuffer stmt = new StringBuffer();
			stmt.append(" SELECT r.id,r.name,COUNT(DISTINCT o.id),SUM(od.price*od.quantity)");
			stmt.append(" FROM offer_detail od  ");
			stmt.append(" INNER JOIN offer o ON (od.offer = o.id)");
			stmt.append(" INNER JOIN seller s ON (o.seller = s.registry)");
			stmt.append(" INNER JOIN registry r ON (s.registry = r.id)");
			stmt.append(" WHERE " + DomainManager.getSQLWhereClause("od.domain"));
			if (params.getFromDate() != null) {
				stmt.append(" AND o.issue_date >= ?");
			}
			if (params.getToDate() != null) {
				stmt.append(" AND o.issue_date <= ?");
			}
			OfferStatus[] os = params.getOfferStatuses();
			String s = "";
			for (int i = 0; i < os.length; i++) {
				if (os[i] != null) {
					if (i == 0) {
						s = " AND (";
						s = s + "o.status=" + os[i].ordinal() + " ";
					}
					if (i > 0) {
						s = s + "OR ";
						s = s + "o.status=" + os[i].ordinal() + " ";
					}
				}
			}
			if (os.length != 0) {
				s = s + ")";
			}
			stmt.append(s);
			stmt.append(" GROUP BY r.id");
			stmt.append(" ORDER BY SUM(od.price*od.quantity) DESC");

			ps = conn.prepareStatement(stmt.toString(), ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			if (params.getFromDate() != null) {
				ps.setDate(1, new java.sql.Date(params.getFromDate().getTime()));
			}
			if (params.getToDate() != null) {
				ps.setDate(2, new java.sql.Date(params.getToDate().getTime()));
			}
			rs = ps.executeQuery();
			List<Stat> stats = new LinkedList<Stat>();
			while (rs.next()) {
				Stat stat = new Stat();
				stat.setKey(rs.getInt(1));
				stat.setName(rs.getString(2));
				int count = rs.getInt(3);
				double amount = rs.getDouble(4);
				stat.setNumInvoice(count);
				stat.setAmount(amount);
				stat.setAverageAmount(CommonUtil.round(amount / count));
				stats.add(stat);
			}
			return stats;
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

	public Collection<Stat> getCommercialGeozoneStats(StatParams params)
			throws ManagerBeanException {

		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = DatabaseUtil.getConnection(params.getDomainName());

			StringBuffer stmt = new StringBuffer();
			stmt.append(" SELECT g.id,g.name,COUNT(DISTINCT o.id),SUM(od.price*od.quantity)");
			stmt.append(" FROM offer_detail od  ");
			stmt.append(" INNER JOIN offer o ON (od.offer = o.id)");
			stmt.append(" INNER JOIN raddress s ON (o.address = s.id)");
			stmt.append(" INNER JOIN geozone g ON (s.geozone = g.id)");
			stmt.append(" WHERE " + DomainManager.getSQLWhereClause("od.domain"));
			
			if (params.getFromDate() != null) {
				stmt.append(" AND o.issue_date >= ?");
			}
			if (params.getToDate() != null) {
				stmt.append(" AND o.issue_date <= ?");
			}
			OfferStatus[] os = params.getOfferStatuses();
			String s = "";
			for (int i = 0; i < os.length; i++) {
				if (os[i] != null) {
					if (i == 0) {
						s = " AND (";
						s = s + "o.status=" + os[i].ordinal() + " ";
					}
					if (i > 0) {
						s = s + "OR ";
						s = s + "o.status=" + os[i].ordinal() + " ";
					}
				}
			}
			if (os.length != 0) {
				s = s + ")";
			}
			stmt.append(s);
			stmt.append(" GROUP BY g.id");
			stmt.append(" ORDER BY SUM(od.price*od.quantity) DESC");

			ps = conn.prepareStatement(stmt.toString(), ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			if (params.getFromDate() != null) {
				ps.setDate(1, new java.sql.Date(params.getFromDate().getTime()));
			}
			if (params.getToDate() != null) {
				ps.setDate(2, new java.sql.Date(params.getToDate().getTime()));
			}
			rs = ps.executeQuery();
			List<Stat> stats = new LinkedList<Stat>();
			while (rs.next()) {
				Stat stat = new Stat();
				stat.setKey(rs.getInt(1));
				stat.setName(rs.getString(2));
				int count = rs.getInt(3);
				double amount = rs.getDouble(4);
				stat.setNumInvoice(count);
				stat.setAmount(amount);
				stat.setAverageAmount(CommonUtil.round(amount / count));
				stats.add(stat);
			}
			return stats;
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

	public Collection<Stat> getCommercialCategoryProductsStats(StatParams params)
			throws ManagerBeanException {

		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = DatabaseUtil.getConnection(params.getDomainName());

			StringBuffer stmt = new StringBuffer();
			stmt.append("SELECT p.id,p.name,COUNT(DISTINCT o.id),SUM(od.price*od.quantity)");
			stmt.append(" FROM offer_detail od ");
			stmt.append(" INNER JOIN offer o ON (od.offer = o.id)");
			stmt.append(" INNER JOIN item ON (item.id = od.item)");
			stmt.append(" INNER JOIN product p ON (p.id = item.product)");
			stmt.append(" INNER JOIN pcategory c ON (p.category = c.id)");
			stmt.append(" WHERE " + DomainManager.getSQLWhereClause("od.domain"));

			if (params.getFromDate() != null) {
				stmt.append(" AND o.issue_date >= ?");
			}
			if (params.getToDate() != null) {
				stmt.append(" AND o.issue_date <= ?");
			}

			if (params.getCategory() != null) {
				stmt.append(" AND p.category = ?");
			}
			OfferStatus[] os = params.getOfferStatuses();
			String s = "";
			for (int i = 0; i < os.length; i++) {
				if (os[i] != null) {
					if (i == 0) {
						s = " AND (";
						s = s + "o.status=" + os[i].ordinal() + " ";
					}
					if (i > 0) {
						s = s + "OR ";
						s = s + "o.status=" + os[i].ordinal() + " ";
					}
				}
			}
			if (os.length != 0) {
				s = s + ")";
			}
			stmt.append(s);
			stmt.append(" GROUP BY p.name");
			stmt.append(" ORDER BY SUM(od.price*od.quantity) DESC,p.id,p.name");

			ps = conn.prepareStatement(stmt.toString(), ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			if (params.getFromDate() != null) {
				ps.setDate(1, new java.sql.Date(params.getFromDate().getTime()));
			}
			if (params.getToDate() != null) {
				ps.setDate(2, new java.sql.Date(params.getToDate().getTime()));
			}
			if (params.getCategory() != null) {
				ps.setInt(3, params.getCategory());
			}

			rs = ps.executeQuery();
			List<Stat> stats = new LinkedList<Stat>();
			while (rs.next()) {
				Stat stat = new Stat();
				stat.setKey(rs.getInt(1));
				stat.setName(rs.getString(2));
				int count = rs.getInt(3);
				double amount = rs.getDouble(4);
				stat.setNumInvoice(count);
				stat.setAmount(amount);
				stat.setAverageAmount(CommonUtil.round(amount / count));
				stats.add(stat);
			}
			return stats;
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

	public Collection<Stat> getCommercialTargetStats(StatParams params)
			throws ManagerBeanException {

		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = DatabaseUtil.getConnection(params.getDomainName());

			StringBuffer stmt = new StringBuffer();
			stmt.append(" SELECT r.id,r.name,COUNT(DISTINCT o.id),SUM(od.price*od.quantity)");
			stmt.append(" FROM  offer o");
			stmt.append(" INNER JOIN target t ON (o.target = t.registry)");
			stmt.append(" INNER JOIN registry r ON (t.registry = r.id)");
			stmt.append(" INNER JOIN offer_detail od ON (od.offer = o.id)");
			stmt.append(" WHERE " + DomainManager.getSQLWhereClause("o.domain"));
			
			if (params.getFromDate() != null) {
				stmt.append(" AND o.issue_date >= ?");
			}
			if (params.getToDate() != null) {
				stmt.append(" AND o.issue_date <= ?");
			}
			OfferStatus[] os = params.getOfferStatuses();
			String s = "";
			for (int i = 0; i < os.length; i++) {
				if (os[i] != null) {
					if (i == 0) {
						s = " AND (";
						s = s + "o.status=" + os[i].ordinal() + " ";
					}
					if (i > 0) {
						s = s + "OR ";
						s = s + "o.status=" + os[i].ordinal() + " ";
					}
				}
			}
			if (os.length != 0) {
				s = s + ")";
			}
			stmt.append(s);
			stmt.append(" GROUP BY r.id");
			stmt.append(" ORDER BY SUM(od.price*od.quantity) DESC");
			ps = conn.prepareStatement(stmt.toString(), ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			if (params.getFromDate() != null) {
				ps.setDate(1, new java.sql.Date(params.getFromDate().getTime()));
			}
			if (params.getToDate() != null) {
				ps.setDate(2, new java.sql.Date(params.getToDate().getTime()));
			}
			rs = ps.executeQuery();
			List<Stat> stats = new LinkedList<Stat>();
			while (rs.next()) {
				Stat stat = new Stat();
				stat.setKey(rs.getInt(1));
				stat.setName(rs.getString(2));
				int count = rs.getInt(3);
				double amount = rs.getDouble(4);
				stat.setNumInvoice(count);
				stat.setAmount(amount);
				stat.setAverageAmount(CommonUtil.round(amount / count));
				stats.add(stat);
			}
			return stats;
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
	
	
	public List<Stat> getSalesByCountry(Connection c, Date fromDate, Date toDate) throws ManagerBeanException {
		String select = "SELECT "
			+" r.nationality "
			+",SUM( i.taxable_base ) "
			+" FROM invoice i "
			+" INNER JOIN registry r ON r.id = i.registry "
			+" WHERE " + DomainManager.getSQLWhereClause("i.domain")
			+" AND i.issue_date >= ?"
			+" AND i.issue_date <= ?"
			+" AND i.type=1 "
			+" GROUP BY r.nationality";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = c.prepareStatement(select, ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			ps.setDate(1, new java.sql.Date(fromDate.getTime()));
			ps.setDate(2, new java.sql.Date(toDate.getTime()));
			rs = ps.executeQuery();
			List<Stat> stats = new LinkedList<Stat>();
			while (rs.next()) {
				Stat stat = new Stat();
				stat.setName(rs.getString(1));
				stat.setAmount(CommonUtil.round(rs.getDouble(2)));
				stats.add(stat);	
			}
			return stats;
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(ps);
		}
	}
	
	public List<Stat> getSalesByProvince(Connection c, Date fromDate, Date toDate) throws ManagerBeanException {
		String select = "SELECT "
			+" g.name,SUM( i.taxable_base ) "
			+" FROM invoice i "
			+" LEFT OUTER JOIN raddress ra ON ra.registry = i.registry and ra.type = 0"
			+" LEFT OUTER JOIN geozone   g ON g.id = ra.geozone "
			+" WHERE " + DomainManager.getSQLWhereClause("i.domain")
			+" AND i.issue_date >= ?"
			+" AND i.issue_date <= ?"
			+" AND i.type=1 "
			+" GROUP BY g.name";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = c.prepareStatement(select, ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			ps.setDate(1, new java.sql.Date(fromDate.getTime()));
			ps.setDate(2, new java.sql.Date(toDate.getTime()));
			rs = ps.executeQuery();
			List<Stat> stats = new LinkedList<Stat>();
			while (rs.next()) {
				Stat stat = new Stat();
				stat.setName(rs.getString(1));
				stat.setAmount(CommonUtil.round(rs.getDouble(2)));
				stats.add(stat);	
			}
			return stats;
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(ps);
		}
	}
}
