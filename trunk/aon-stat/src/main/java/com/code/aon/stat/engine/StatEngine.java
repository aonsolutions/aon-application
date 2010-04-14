package com.code.aon.stat.engine;

import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import com.code.aon.commercial.enumeration.OfferStatus;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.stat.Stat;
import com.code.aon.stat.StatParams;
import com.code.aon.ui.util.AonUtil;

public class StatEngine {

	public Collection<Stat> getYearStats(StatParams params)
			throws ManagerBeanException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			StringWriter stmt = new StringWriter();
			stmt.append("SELECT YEAR(i.issue_date) YEAR,COUNT(DISTINCT i.id),");
			stmt.append(" SUM(id.taxable_base)");
			stmt.append(" FROM invoice_detail id  ");
			stmt.append(" INNER JOIN invoice i ON (id.invoice = i.id)");

			if (params.getFromDate() != null) {
				stmt.append(" AND i.issue_date >= ?");
			}
			if (params.getToDate() != null) {
				stmt.append(" AND i.issue_date <= ?");
			}
			if (params.getInvoiceType() != null) {
				stmt.append(" AND i.type = ?");
			}
			stmt.append(" GROUP BY YEAR(i.issue_date)");
			stmt.append(" ORDER BY YEAR(i.issue_date) DESC");

			ps = HibernateUtil.getSQLConnection().prepareStatement(
					stmt.toString(), ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);

			if (params.getFromDate() != null) {
				ps
						.setDate(1, new java.sql.Date(params.getFromDate()
								.getTime()));
			}
			if (params.getToDate() != null) {
				ps.setDate(2, new java.sql.Date(params.getToDate().getTime()));
			}
			if (params.getInvoiceType() != null) {
				ps.setInt(3, new Integer(params.getInvoiceType()));
			}

			rs = ps.executeQuery();
			List<Stat> stats = new LinkedList<Stat>();
			while (rs.next()) {
				Stat stat = new Stat();
				// stat.setExtra(rs.getInt(1));
				stat.setKey(rs.getInt(1));
				stat.setName(rs.getString(1));
				int count = rs.getInt(2);
				double amount = rs.getInt(3);
				stat.setNumInvoice(count);
				stat.setAmount(amount);
				stat.setAverageAmount(CommonUtil.round(amount / count));
				stats.add(stat);
			}
			return stats;
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	public Collection<Stat> getMonthsStats(StatParams params)
			throws ManagerBeanException {
		List<Stat> stats = new LinkedList<Stat>();

		for (int i = 0; i < 12; i++) {
			stats.add(i, new Stat());
			stats.get(i).setKey(i);
			stats.get(i).setName(
					Month.getMonthByValue(i).getName(params.getLocale()));
		}

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			StringWriter stmt = new StringWriter();
			stmt
					.append("SELECT MONTH(i.issue_date) MONTH,COUNT(DISTINCT i.id),");
			stmt.append(" SUM(id.taxable_base)");
			stmt.append(" FROM invoice_detail id  ");
			stmt.append(" INNER JOIN invoice i ON (id.invoice = i.id)");

			if (params.getFromDate() != null) {
				stmt.append(" AND i.issue_date >= ?");
			}
			if (params.getToDate() != null) {
				stmt.append(" AND i.issue_date <= ?");
			}
			if (params.getInvoiceType() != null) {
				stmt.append(" AND i.type = ?");
			}
			stmt.append(" GROUP BY MONTH(i.issue_date)");
			stmt.append(" ORDER BY MONTH(i.issue_date)");

			ps = HibernateUtil.getSQLConnection().prepareStatement(
					stmt.toString(), ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			if (params.getFromDate() != null) {
				ps
						.setDate(1, new java.sql.Date(params.getFromDate()
								.getTime()));
			}
			if (params.getToDate() != null) {
				ps.setDate(2, new java.sql.Date(params.getToDate().getTime()));
			}
			if (params.getInvoiceType() != null) {
				ps.setInt(3, new Integer(params.getInvoiceType()));
			}
			rs = ps.executeQuery();

			while (rs.next()) {

				Stat stat = stats.get((Month.getMonthByValue(rs.getInt(1) - 1))
						.getValue());
				stat.setKey(rs.getInt(1));
				stat.setName(Month.getMonthByValue(rs.getInt(1) - 1).getName(
						params.getLocale()));
				int count = rs.getInt(2);
				double amount = rs.getInt(3);
				stat.setNumInvoice(stat.getNumInvoice() + count);
				stat.setAmount(stat.getAmount() + amount);
				stat.setAverageAmount(CommonUtil.round(stat.getAmount()
						/ stat.getNumInvoice()));
				stats.set(Month.getMonthByValue(rs.getInt(1) - 1).getValue(),
						stat);

			}
			return stats;
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	public Collection<Stat> getDaysStats(StatParams params)
			throws ManagerBeanException {

		List<Stat> stats = new LinkedList<Stat>();

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			StringWriter stmt = new StringWriter();
			stmt
					.append("SELECT i.issue_date DAY,COUNT(DISTINCT i.id),SUM(id.taxable_base)");
			stmt.append(" FROM invoice_detail id  ");
			stmt.append(" INNER JOIN invoice i ON (id.invoice = i.id)");

			if (params.getFromDate() != null) {
				stmt.append(" AND i.issue_date >= ?");
			}
			if (params.getToDate() != null) {
				stmt.append(" AND i.issue_date <= ?");
			}
			if (params.getInvoiceType() != null) {
				stmt.append(" AND i.type = ?");
			}
			stmt.append(" GROUP BY DAY(i.issue_date)");
			stmt.append(" ORDER BY DAY(i.issue_date)");

			ps = HibernateUtil.getSQLConnection().prepareStatement(
					stmt.toString(), ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			if (params.getFromDate() != null) {
				ps
						.setDate(1, new java.sql.Date(params.getFromDate()
								.getTime()));
			}
			if (params.getToDate() != null) {
				ps.setDate(2, new java.sql.Date(params.getToDate().getTime()));
			}
			if (params.getInvoiceType() != null) {
				ps.setInt(3, new Integer(params.getInvoiceType()));
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
				double amount = rs.getInt(3);
				stat.setNumInvoice(count);
				stat.setAmount(amount);
				stat.setAverageAmount(CommonUtil.round(amount / count));
				stats.add(stat);
			}
			return stats;
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	public Collection<Stat> getCustomerStats(StatParams params)
			throws ManagerBeanException {

		List<Stat> stats = new LinkedList<Stat>();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			StringWriter stmt = new StringWriter();
			stmt
					.append("SELECT r.id,r.name,COUNT(DISTINCT i.id),SUM(id.taxable_base),r.surname");
			stmt.append(" FROM invoice_detail id  ");
			stmt.append(" INNER JOIN invoice i ON (id.invoice = i.id)");
			stmt.append(" INNER JOIN registry r ON (i.registry = r.id)");

			if (params.getFromDate() != null) {
				stmt.append(" AND i.issue_date >= ?");
			}
			if (params.getToDate() != null) {
				stmt.append(" AND i.issue_date <= ?");
			}
			if (params.getInvoiceType() != null) {
				stmt.append(" AND i.type = ?");
			}
			stmt.append(" GROUP BY r.id,r.name");
			stmt.append(" ORDER BY SUM(id.taxable_base) DESC,r.id,r.name");

			ps = HibernateUtil.getSQLConnection().prepareStatement(
					stmt.toString(), ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			if (params.getFromDate() != null) {
				ps
						.setDate(1, new java.sql.Date(params.getFromDate()
								.getTime()));
			}
			if (params.getToDate() != null) {
				ps.setDate(2, new java.sql.Date(params.getToDate().getTime()));
			}
			if (params.getInvoiceType() != null) {
				ps.setInt(3, new Integer(params.getInvoiceType()));
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				Stat stat = new Stat();
				stat.setKey(rs.getInt(1));
				stat.setName(rs.getString(2) + rs.getString(5));
				int count = rs.getInt(3);
				double amount = rs.getInt(4);
				stat.setNumInvoice(count);
				stat.setAmount(amount);
				stat.setAverageAmount(CommonUtil.round(amount / count));
				stats.add(stat);
			}
			return stats;
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	public Collection<Stat> getCategoryStats(StatParams params)
			throws ManagerBeanException {

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {

			StringWriter stmt = new StringWriter();
			stmt
					.append(" SELECT YEAR(i.issue_date) YEAR,c.id,c.name,COUNT(DISTINCT i.id),SUM(id.taxable_base)");
			stmt.append(" FROM invoice_detail id  ");
			stmt.append(" INNER JOIN invoice i ON (id.invoice = i.id)");
			stmt.append(" INNER JOIN item ON (item.id = id.item)");
			stmt.append(" INNER JOIN product p ON (p.id = item.product)");
			stmt.append(" INNER JOIN pcategory c ON (p.category = c.id)");

			if (params.getFromDate() != null) {
				stmt.append(" AND i.issue_date >= ?");
			}
			if (params.getToDate() != null) {
				stmt.append(" AND i.issue_date <= ?");
			}
			if (params.getInvoiceType() != null) {
				stmt.append(" AND i.type = ?");
			}
			stmt.append(" GROUP BY c.id");
			stmt.append(" ORDER BY c.name");

			ps = HibernateUtil.getSQLConnection().prepareStatement(
					stmt.toString(), ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			if (params.getFromDate() != null) {
				ps
						.setDate(1, new java.sql.Date(params.getFromDate()
								.getTime()));
			}
			if (params.getToDate() != null) {
				ps.setDate(2, new java.sql.Date(params.getToDate().getTime()));
			}
			if (params.getInvoiceType() != null) {
				ps.setInt(3, new Integer(params.getInvoiceType()));
			}

			rs = ps.executeQuery();
			List<Stat> stats = new LinkedList<Stat>();
			while (rs.next()) {
				Stat stat = new Stat();
				stat.setKey(rs.getInt(2));
				stat.setName(rs.getString(3));
				int count = rs.getInt(4);
				double amount = rs.getInt(5);
				stat.setNumInvoice(count);
				stat.setAmount(amount);
				stat.setAverageAmount(CommonUtil.round(amount / count));
				stats.add(stat);
			}
			return stats;
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	public Collection<Stat> getCategoryProductsStats(StatParams params)
			throws ManagerBeanException {

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			StringWriter stmt = new StringWriter();
			stmt
					.append("SELECT p.id,p.name,COUNT(DISTINCT i.id),SUM(id.taxable_base)");
			stmt.append(" FROM invoice_detail id ");
			stmt.append(" INNER JOIN invoice i ON (id.invoice = i.id)");
			stmt.append(" INNER JOIN item ON (item.id = id.item)");
			stmt.append(" INNER JOIN product p ON (p.id = item.product)");
			stmt.append(" INNER JOIN pcategory c ON (p.category = c.id)");

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
			stmt.append(" GROUP BY p.name");
			stmt.append(" ORDER BY SUM(id.taxable_base) DESC,p.id,p.name");

			ps = HibernateUtil.getSQLConnection().prepareStatement(
					stmt.toString(), ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			if (params.getFromDate() != null) {
				ps
						.setDate(1, new java.sql.Date(params.getFromDate()
								.getTime()));
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

			rs = ps.executeQuery();
			List<Stat> stats = new LinkedList<Stat>();
			while (rs.next()) {
				Stat stat = new Stat();
				stat.setKey(rs.getInt(1));
				stat.setName(rs.getString(2));
				int count = rs.getInt(3);
				double amount = rs.getInt(4);
				stat.setNumInvoice(count);
				stat.setAmount(amount);
				stat.setAverageAmount(CommonUtil.round(amount / count));
				stats.add(stat);
			}
			return stats;
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}
		}

	}

	public Collection<Stat> getCategoryCustomerStats(StatParams params)
			throws ManagerBeanException {

		List<Stat> stats = new LinkedList<Stat>();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			StringWriter stmt = new StringWriter();
			stmt
					.append("SELECT r.id,r.name, COUNT(DISTINCT i.id), SUM(id.taxable_base)");
			stmt.append("FROM invoice_detail id  ");
			stmt.append("INNER JOIN invoice i ON (id.invoice = i.id)");
			stmt.append("INNER JOIN registry r ON (i.registry = r.id)");
			stmt.append(" INNER JOIN item ON (item.id = id.item)");
			stmt.append(" INNER JOIN product p ON (p.id = item.product)");
			stmt.append(" INNER JOIN pcategory c ON (p.category = c.id)");

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
			stmt.append(" GROUP BY r.id");
			stmt.append(" ORDER BY YEAR(i.issue_date),c.id");

			ps = HibernateUtil.getSQLConnection().prepareStatement(
					stmt.toString(), ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			if (params.getFromDate() != null) {
				ps
						.setDate(1, new java.sql.Date(params.getFromDate()
								.getTime()));
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
			rs = ps.executeQuery();
			while (rs.next()) {
				Stat stat = new Stat();
				stat.setKey(rs.getInt(1));
				stat.setName(rs.getString(2));
				int count = rs.getInt(3);
				double amount = rs.getInt(4);
				stat.setNumInvoice(count);
				stat.setAmount(amount);
				stat.setAverageAmount(CommonUtil.round(amount / count));
				stats.add(stat);
			}
			return stats;
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	public Collection<Stat> getProductRegistryStats(StatParams params)
			throws ManagerBeanException {

		List<Stat> stats = new LinkedList<Stat>();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			StringWriter stmt = new StringWriter();
			stmt
					.append("SELECT r.id,r.name, COUNT(DISTINCT i.id), SUM(id.taxable_base)");
			stmt.append("FROM invoice_detail id  ");
			stmt.append("INNER JOIN invoice i ON (id.invoice = i.id)");
			stmt.append("INNER JOIN registry r ON (i.registry = r.id)");
			stmt.append(" INNER JOIN item ON (item.id = id.item)");
			stmt.append(" INNER JOIN product p ON (p.id = item.product)");
			stmt.append(" INNER JOIN pcategory c ON (p.category = c.id)");

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
			stmt.append(" GROUP BY r.id,r.name");
			stmt.append(" ORDER BY YEAR(i.issue_date),c.id");

			ps = HibernateUtil.getSQLConnection().prepareStatement(
					stmt.toString(), ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			if (params.getFromDate() != null) {
				ps
						.setDate(1, new java.sql.Date(params.getFromDate()
								.getTime()));
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
			rs = ps.executeQuery();
			while (rs.next()) {
				Stat stat = new Stat();
				stat.setKey(rs.getInt(1));
				stat.setName(rs.getString(2));
				int count = rs.getInt(3);
				double amount = rs.getInt(4);
				stat.setNumInvoice(count);
				stat.setAmount(amount);
				stat.setAverageAmount(CommonUtil.round(amount / count));
				stats.add(stat);
			}
			return stats;
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	public Collection<Stat> getSummaryStats(StatParams params)
			throws ManagerBeanException {
		List<Stat> stats = new LinkedList<Stat>();

		/*
		 * for (int i = 0; i < 13; i++) { stats.add(i, new Stat());
		 * stats.get(i).setKey(i);
		 * stats.get(i).setName(Month.getMonthByValue(i).getName(locale)); }
		 */

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			StringWriter stmt = new StringWriter();
			stmt
					.append("SELECT MONTH(i.issue_date) MONTH,COUNT(DISTINCT i.id),");
			stmt.append(" SUM(id.taxable_base), YEAR(i.issue_date)");
			stmt.append(" FROM invoice_detail id  ");
			stmt.append(" INNER JOIN invoice i ON (id.invoice = i.id)");

			if (params.getFromDate() != null) {
				stmt.append(" AND i.issue_date >= ?");
			}
			if (params.getToDate() != null) {
				stmt.append(" AND i.issue_date <= ?");
			}
			if (params.getInvoiceType() != null) {
				stmt.append(" AND i.type = ?");
			}
			stmt.append(" GROUP BY MONTH(i.issue_date)");
			stmt.append(" ORDER BY YEAR(i.issue_date),MONTH(i.issue_date)");

			ps = HibernateUtil.getSQLConnection().prepareStatement(
					stmt.toString(), ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			if (params.getFromDate() != null) {
				ps
						.setDate(1, new java.sql.Date(params.getFromDate()
								.getTime()));
			}
			if (params.getToDate() != null) {
				ps.setDate(2, new java.sql.Date(params.getToDate().getTime()));
			}
			if (params.getInvoiceType() != null) {
				ps.setInt(3, new Integer(params.getInvoiceType()));
			}
			rs = ps.executeQuery();

			while (rs.next()) {

				Stat stat = new Stat();
				stat.setKey(rs.getInt(1));
				stat.setName(Month.getMonthByValue(rs.getInt(1) - 1).getName(
						params.getLocale())
						+ " " + rs.getInt(4));
				int count = rs.getInt(2);
				double amount = rs.getInt(3);
				stat.setNumInvoice(count);
				stat.setAmount(amount);
				stat.setAverageAmount(CommonUtil.round(amount / count));
				stats.add(stat);

			}
			return stats;
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	public Collection<Stat> getAbcCustomerStats(StatParams params)
			throws ManagerBeanException {

		List<Stat> stats = new LinkedList<Stat>();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			StringWriter stmt = new StringWriter();
			stmt
					.append("SELECT MONTH(i.issue_date) MONTH,COUNT(DISTINCT i.id),SUM(id.taxable_base),YEAR(i.issue_date) YEAR");
			stmt.append(" FROM invoice_detail id  ");
			stmt.append(" INNER JOIN invoice i ON (id.invoice = i.id)");
			stmt.append(" INNER JOIN registry r ON (i.registry = r.id)");

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
			stmt.append(" GROUP BY MONTH(i.issue_date)");
			stmt.append(" ORDER BY YEAR(i.issue_date),MONTH(i.issue_date)");

			ps = HibernateUtil.getSQLConnection().prepareStatement(
					stmt.toString(), ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			if (params.getFromDate() != null) {
				ps
						.setDate(1, new java.sql.Date(params.getFromDate()
								.getTime()));
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
			rs = ps.executeQuery();
			while (rs.next()) {
				Stat stat = new Stat();
				stat.setName(Month.getMonthByValue(rs.getInt(1) - 1).getName(
						params.getLocale())
						+ " " + rs.getInt(4));
				int count = rs.getInt(2);
				double amount = rs.getInt(3);
				stat.setNumInvoice(count);
				stat.setAmount(amount);
				stat.setAverageAmount(CommonUtil.round(amount / count));
				stats.add(stat);
			}
			return stats;
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	public Collection<Stat> getABCStatsByCustomer(StatParams params)
			throws ManagerBeanException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			StringWriter stmt = new StringWriter();
			stmt
					.append("SELECT r.id,r.name,COUNT(DISTINCT i.id),SUM(id.taxable_base)");
			stmt.append(" FROM invoice_detail id ");
			stmt.append(" INNER JOIN invoice i ON (id.invoice = i.id)");
			stmt.append(" INNER JOIN registry r ON (i.registry = r.id)");

			if (params.getFromDate() != null) {
				stmt.append(" AND i.issue_date >= ?");
			}
			if (params.getToDate() != null) {
				stmt.append(" AND i.issue_date <= ?");
			}
			if (params.getInvoiceType() != null) {
				stmt.append(" AND i.type = ?");
			}
			stmt.append(" GROUP BY r.id,r.name");
			stmt.append(" ORDER BY SUM(id.taxable_base) DESC,r.id,r.name");

			ps = HibernateUtil.getSQLConnection().prepareStatement(
					stmt.toString(), ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			if (params.getFromDate() != null) {
				ps
						.setDate(1, new java.sql.Date(params.getFromDate()
								.getTime()));
			}
			if (params.getToDate() != null) {
				ps.setDate(2, new java.sql.Date(params.getToDate().getTime()));
			}
			if (params.getInvoiceType() != null) {
				ps.setInt(3, new Integer(params.getInvoiceType()));
			}

			rs = ps.executeQuery();
			List<Stat> stats = new LinkedList<Stat>();
			while (rs.next()) {
				Stat stat = new Stat();
				stat.setKey(rs.getInt(1));
				stat.setName(rs.getString(2));
				int count = rs.getInt(3);
				double amount = rs.getInt(4);
				stat.setNumInvoice(count);
				stat.setAmount(amount);
				stat.setAverageAmount(CommonUtil.round(amount / count));
				stats.add(stat);
			}
			return stats;
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	public Collection<Stat> getRegistryProductStats(StatParams params)
			throws ManagerBeanException {

		List<Stat> stats = new LinkedList<Stat>();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			StringWriter stmt = new StringWriter();
			stmt
					.append("SELECT p.id,p.name, COUNT(DISTINCT i.id), SUM(id.taxable_base)");
			stmt.append("FROM invoice_detail id  ");
			stmt.append("INNER JOIN invoice i ON (id.invoice = i.id)");
			stmt.append("INNER JOIN registry r ON (i.registry = r.id)");
			stmt.append(" INNER JOIN item ON (item.id = id.item)");
			stmt.append(" INNER JOIN product p ON (p.id = item.product)");

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
			stmt.append(" GROUP BY p.id,p.name");
			stmt.append(" ORDER BY SUM(id.taxable_base) DESC,p.id,p.name");

			ps = HibernateUtil.getSQLConnection().prepareStatement(
					stmt.toString(), ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			if (params.getFromDate() != null) {
				ps
						.setDate(1, new java.sql.Date(params.getFromDate()
								.getTime()));
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
			rs = ps.executeQuery();
			while (rs.next()) {
				Stat stat = new Stat();
				stat.setKey(rs.getInt(1));
				stat.setName(rs.getString(2));
				int count = rs.getInt(3);
				double amount = rs.getInt(4);
				stat.setNumInvoice(count);
				stat.setAmount(amount);
				stat.setAverageAmount(CommonUtil.round(amount / count));
				stats.add(stat);
			}
			return stats;
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	public Collection<Stat> getABCStatsByProduct(StatParams params)
			throws ManagerBeanException {

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			StringWriter stmt = new StringWriter();
			stmt
					.append("SELECT p.id,p.name,COUNT(DISTINCT i.id),SUM(id.taxable_base)");
			stmt.append(" FROM invoice_detail id ");
			stmt.append(" INNER JOIN invoice i ON (id.invoice = i.id)");
			stmt.append(" INNER JOIN item ON (item.id = id.item)");
			stmt.append(" INNER JOIN product p ON (p.id = item.product)");

			if (params.getFromDate() != null) {
				stmt.append(" AND i.issue_date >= ?");
			}
			if (params.getToDate() != null) {
				stmt.append(" AND i.issue_date <= ?");
			}
			if (params.getInvoiceType() != null) {
				stmt.append(" AND i.type = ?");
			}
			stmt.append(" GROUP BY p.id,p.name");
			stmt.append(" ORDER BY SUM(id.taxable_base) DESC,p.id,p.name");

			ps = HibernateUtil.getSQLConnection().prepareStatement(
					stmt.toString(), ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			if (params.getFromDate() != null) {
				ps
						.setDate(1, new java.sql.Date(params.getFromDate()
								.getTime()));
			}
			if (params.getToDate() != null) {
				ps.setDate(2, new java.sql.Date(params.getToDate().getTime()));
			}
			if (params.getInvoiceType() != null) {
				ps.setInt(3, new Integer(params.getInvoiceType()));
			}

			rs = ps.executeQuery();
			List<Stat> stats = new LinkedList<Stat>();
			while (rs.next()) {
				Stat stat = new Stat();
				stat.setKey(rs.getInt(1));
				stat.setName(rs.getString(2));
				int count = rs.getInt(3);
				double amount = rs.getInt(4);
				stat.setNumInvoice(count);
				stat.setAmount(amount);
				stat.setAverageAmount(CommonUtil.round(amount / count));
				stats.add(stat);
			}
			return stats;
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	public Collection<Stat> getABCStatsByCategory(StatParams params)
			throws ManagerBeanException {

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			StringWriter stmt = new StringWriter();
			stmt
					.append("SELECT c.id,c.name,COUNT(DISTINCT i.id),SUM(id.taxable_base)");
			stmt.append(" FROM invoice_detail id  ");
			stmt.append(" INNER JOIN invoice i ON (id.invoice = i.id)");
			stmt.append(" INNER JOIN item ON (item.id = id.item)");
			stmt.append(" INNER JOIN product p ON (p.id = item.product)");
			stmt.append(" INNER JOIN pcategory c ON (p.category = c.id)");

			if (params.getFromDate() != null) {
				stmt.append(" AND i.issue_date >= ?");
			}
			if (params.getToDate() != null) {
				stmt.append(" AND i.issue_date <= ?");
			}
			if (params.getInvoiceType() != null) {
				stmt.append(" AND i.type = ?");
			}
			stmt.append(" GROUP BY c.id,c.name");
			stmt.append(" ORDER BY SUM(id.taxable_base) DESC,c.id,c.name");

			ps = HibernateUtil.getSQLConnection().prepareStatement(
					stmt.toString(), ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			if (params.getFromDate() != null) {
				ps
						.setDate(1, new java.sql.Date(params.getFromDate()
								.getTime()));
			}
			if (params.getToDate() != null) {
				ps.setDate(2, new java.sql.Date(params.getToDate().getTime()));
			}
			if (params.getInvoiceType() != null) {
				ps.setInt(3, new Integer(params.getInvoiceType()));
			}

			rs = ps.executeQuery();
			List<Stat> stats = new LinkedList<Stat>();
			while (rs.next()) {
				Stat stat = new Stat();
				stat.setKey(rs.getInt(1));
				stat.setName(rs.getString(2));
				int count = rs.getInt(3);
				double amount = rs.getInt(4);
				stat.setNumInvoice(count);
				stat.setAmount(amount);
				stat.setAverageAmount(CommonUtil.round(amount / count));
				stats.add(stat);
			}
			return stats;
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}
		}

	}

	public Collection<Stat> getCommercialCategoryStats(StatParams params)
			throws ManagerBeanException {

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {

			StringWriter stmt = new StringWriter();
			stmt
					.append(" SELECT c.id,c.name,COUNT(DISTINCT o.id),SUM(od.price*od.quantity)");
			stmt.append(" FROM offer_detail od  ");
			stmt.append(" INNER JOIN offer o ON (od.offer = o.id)");
			stmt.append(" INNER JOIN item ON (item.id = od.item)");
			stmt.append(" INNER JOIN product p ON (p.id = item.product)");
			stmt.append(" INNER JOIN pcategory c ON (p.category = c.id)");

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

			ps = HibernateUtil.getSQLConnection().prepareStatement(
					stmt.toString(), ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			if (params.getFromDate() != null) {
				ps
						.setDate(1, new java.sql.Date(params.getFromDate()
								.getTime()));
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
				double amount = rs.getInt(4);
				stat.setNumInvoice(count);
				stat.setAmount(amount);
				stat.setAverageAmount(CommonUtil.round(amount / count));
				stats.add(stat);
			}
			return stats;
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	public Collection<Stat> getCommercialSellerStats(StatParams params)
			throws ManagerBeanException {

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			StringWriter stmt = new StringWriter();
			stmt.append(" SELECT r.id,r.name,COUNT(DISTINCT o.id),SUM(od.price*od.quantity)");
			stmt.append(" FROM offer_detail od  ");
			stmt.append(" INNER JOIN offer o ON (od.offer = o.id)");
			stmt.append(" INNER JOIN seller s ON (o.seller = s.registry)");
			stmt.append(" INNER JOIN registry r ON (s.registry = r.id)");
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

			ps = HibernateUtil.getSQLConnection().prepareStatement(
					stmt.toString(), ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			if (params.getFromDate() != null) {
				ps
						.setDate(1, new java.sql.Date(params.getFromDate()
								.getTime()));
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
				double amount = rs.getInt(4);
				stat.setNumInvoice(count);
				stat.setAmount(amount);
				stat.setAverageAmount(CommonUtil.round(amount / count));
				stats.add(stat);
			}
			return stats;
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	public Collection<Stat> getCommercialGeozoneStats(StatParams params)
			throws ManagerBeanException {

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			StringWriter stmt = new StringWriter();
			stmt.append(" SELECT g.id,g.name,COUNT(DISTINCT o.id),SUM(od.price*od.quantity)");
			stmt.append(" FROM offer_detail od  ");
			stmt.append(" INNER JOIN offer o ON (od.offer = o.id)");
			stmt.append(" INNER JOIN raddress s ON (o.address = s.id)");
			stmt.append(" INNER JOIN geozone g ON (s.geozone = g.id)");

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

			ps = HibernateUtil.getSQLConnection().prepareStatement(
					stmt.toString(), ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
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
				double amount = rs.getInt(4);
				stat.setNumInvoice(count);
				stat.setAmount(amount);
				stat.setAverageAmount(CommonUtil.round(amount / count));
				stats.add(stat);
			}
			return stats;
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	public Collection<Stat> getCommercialCategoryProductsStats(StatParams params)
			throws ManagerBeanException {

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			StringWriter stmt = new StringWriter();
			stmt.append("SELECT p.id,p.name,COUNT(DISTINCT o.id),SUM(od.price*od.quantity)");
			stmt.append(" FROM offer_detail od ");
			stmt.append(" INNER JOIN offer o ON (od.offer = o.id)");
			stmt.append(" INNER JOIN item ON (item.id = od.item)");
			stmt.append(" INNER JOIN product p ON (p.id = item.product)");
			stmt.append(" INNER JOIN pcategory c ON (p.category = c.id)");

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

			ps = HibernateUtil.getSQLConnection().prepareStatement(
					stmt.toString(), ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			if (params.getFromDate() != null) {
				ps
						.setDate(1, new java.sql.Date(params.getFromDate()
								.getTime()));
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
				double amount = rs.getInt(4);
				stat.setNumInvoice(count);
				stat.setAmount(amount);
				stat.setAverageAmount(CommonUtil.round(amount / count));
				stats.add(stat);
			}
			return stats;
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}
		}

	}

}
