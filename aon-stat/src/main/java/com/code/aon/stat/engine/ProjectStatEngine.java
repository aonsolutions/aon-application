package com.code.aon.stat.engine;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.stat.DailyTracking;
import com.code.aon.stat.Delivery;
import com.code.aon.stat.Invoice;
import com.code.aon.stat.Offer;
import com.code.aon.stat.PagedList;

import net.aonsolutions.core.dbutils.DatabaseUtil;


public class ProjectStatEngine implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public double getTotalSales(Connection c, ProjectStatParams params) throws ManagerBeanException {
		double totalSales = 0d;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String select = "SELECT SUM(id.taxable_base) "
					+ " FROM invoice i "
					+ " INNER JOIN invoice_detail id ON id.invoice = i.id"
					+ " WHERE "
					+ DomainManager.getSQLWhereClause("i.domain")
					+ " AND i.type = 1 " + " AND id.project = ? "
					+ " AND i.issue_date BETWEEN ? AND ?";
			ps = c.prepareStatement(select, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			ps.setInt(1, params.getProjectId() );
			ps.setDate(2, new java.sql.Date( params.getFromDate().getTime()) );
			ps.setDate(3, new java.sql.Date( params.getToDate().getTime()) );
			rs = ps.executeQuery();
			if (rs.next()) {
				totalSales = rs.getDouble(1);
			}
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(),e);
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(ps);
		}
		return CommonUtil.round(totalSales);
	}
	
	public double getTotalIncome(Connection c, ProjectStatParams params) throws ManagerBeanException {
		double totalIncome = 0d;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String select = "SELECT SUM(id.quantity * id.price) "
					+ " FROM income i "
					+ " INNER JOIN income_detail id ON id.income = i.id"
					+ " WHERE "
					+ DomainManager.getSQLWhereClause("i.domain")
					+ " AND i.status = 0"
					+ " AND id.project = ? "
					+ " AND i.issue_time BETWEEN ? AND ?";
			ps = c.prepareStatement(select, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			ps.setInt(1, params.getProjectId() );
			ps.setDate(2, new java.sql.Date( params.getFromDate().getTime()) );
			ps.setDate(3, new java.sql.Date( params.getToDate().getTime()) );
			rs = ps.executeQuery();
			if (rs.next()) {
				totalIncome = rs.getDouble(1);
			}
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(),e);
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(ps);
		}
		return CommonUtil.round(totalIncome);
	}
	
	public double getTotalDelivery(Connection c, ProjectStatParams params) throws ManagerBeanException {
		double totalDelivery = 0d;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String select = "SELECT SUM(dd.quantity * dd.price) "
					+ " FROM delivery d "
					+ " INNER JOIN delivery_detail dd ON dd.delivery = d.id"
					+ " WHERE "
					+ DomainManager.getSQLWhereClause("d.domain")
					+ " AND d.status = 0"
					+ " AND d.project = ? "
					+ " AND d.issue_time BETWEEN ? AND ?";
			ps = c.prepareStatement(select, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			ps.setInt(1, params.getProjectId() );
			ps.setDate(2, new java.sql.Date( params.getFromDate().getTime()) );
			ps.setDate(3, new java.sql.Date( params.getToDate().getTime()) );
			rs = ps.executeQuery();
			if (rs.next()) {
				totalDelivery = rs.getDouble(1);
			}
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(),e);
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(ps);
		}
		return CommonUtil.round(totalDelivery);
	}
	
	public double getTotalInvoiceCosts(Connection c, ProjectStatParams params) throws ManagerBeanException {
		double totalInvoiceCosts = 0d;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String select = "SELECT SUM(id.taxable_base) "
					+ " FROM invoice i "
					+ " INNER JOIN invoice_detail id ON id.invoice = i.id"
					+ " WHERE "
					+ DomainManager.getSQLWhereClause("i.domain")
					+ " AND i.type != 1 " + " AND id.project = ? "
					+ " AND i.issue_date BETWEEN ? AND ?";
			ps = c.prepareStatement(select, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			ps.setInt(1, params.getProjectId() );
			ps.setDate(2, new java.sql.Date( params.getFromDate().getTime()) );
			ps.setDate(3, new java.sql.Date( params.getToDate().getTime()) );
			rs = ps.executeQuery();

			if (rs.next()) {
				totalInvoiceCosts = rs.getDouble(1);
			}
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(),e);
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(ps);
		}
		return CommonUtil.round(totalInvoiceCosts);
	}

	public double getTotalDailyTracking(Connection c, ProjectStatParams params) throws ManagerBeanException {
		double totalDailyTracking = 0d;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String select = "SELECT SUM(tracking_duration * cost) "
					+ " FROM daily_tracking dt " + " WHERE "
					+ DomainManager.getSQLWhereClause("dt.domain")
					+ " AND dt.project = ? "
					+ " AND dt.tracking_date BETWEEN ? AND ?";
			ps = c.prepareStatement(select, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			ps.setInt(1, params.getProjectId() );
			ps.setDate(2, new java.sql.Date( params.getFromDate().getTime()) );
			ps.setDate(3, new java.sql.Date( params.getToDate().getTime()) );
			rs = ps.executeQuery();
			if (rs.next()) {
				totalDailyTracking = rs.getDouble(1);
			}
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(),e);
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(ps);
		}
		return CommonUtil.round(totalDailyTracking);
	}

	
	public double getTotalOffered(Connection c, ProjectStatParams params) throws ManagerBeanException {
		double totalOffered = 0d;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String select = "SELECT SUM(  ROUND((od.quantity * od.price) - (od.quantity * od.price * od.discount_expr / 100) ,2) ) "
					+ " FROM offer o "
					+ " INNER JOIN offer_detail od ON od.offer = o.id"
					+ " WHERE "
					+ DomainManager.getSQLWhereClause("o.domain")
					+ " AND o.status in (1, 4) "
					+ " AND o.project = ? "
					+ " AND o.issue_date BETWEEN ? AND ?";
			ps = c.prepareStatement(select, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			ps.setInt(1, params.getProjectId() );
			ps.setDate(2, new java.sql.Date( params.getFromDate().getTime()) );
			ps.setDate(3, new java.sql.Date( params.getToDate().getTime()) );
			rs = ps.executeQuery();
			if (rs.next()) {
				totalOffered = rs.getDouble(1);
			}
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(),e);
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(ps);
		}
		return CommonUtil.round(totalOffered);
	}
	
	public void fillApprovedOfferPage(Connection c,PagedList<Offer> page, ProjectStatParams params) throws ManagerBeanException {
		page.setList( new LinkedList<Offer>() );		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String select = "SELECT"
					+" o.id"
					+",o.series"
					+",o.number"
					+",o.version"
					+",o.issue_date"
					+",r.name"
					+",w.description"
					+",SUM(  ROUND((od.quantity * od.price) - (od.quantity * od.price * od.discount_expr / 100) ,2) )"
					+" FROM offer o"
				+" INNER JOIN offer_detail od ON od.offer = o.id"
				+" LEFT OUTER JOIN registry r ON r.id = o.seller"
				+" LEFT OUTER JOIN workplace w ON w.id = o.workplace"
				+" WHERE " + DomainManager.getSQLWhereClause("o.domain")
				+" AND o.project = ?"
				+" AND o.status in (1, 4)"
				+" AND o.issue_date BETWEEN ? AND ?"
				+" GROUP BY o.id,o.series,o.number,o.version,o.issue_date,r.name,w.description"
				+" ORDER BY o.issue_date DESC"
				+" LIMIT " + ( page.getPageSize() + 1 ) + " OFFSET " + page.getOffset();
			ps =  c.prepareStatement(select,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			ps.setInt(1, params.getProjectId() );
			ps.setDate(2, new java.sql.Date( params.getFromDate().getTime()) );
			ps.setDate(3, new java.sql.Date( params.getToDate().getTime()) );
			rs = ps.executeQuery();
			int i = 1;
			page.setNextAvailable(false);
			while (rs.next()) {
				if (i > page.getPageSize()) {
					page.setNextAvailable(true);
					break;
				}
				Offer offer = new Offer();
				Integer id = rs.getInt(1);
				offer.setId(id);
				String series = rs.getString(2);
				int number = rs.getInt(3);
				int version = rs.getInt(4);
		    	String referenceCode = StringUtils.leftPad(Integer.toString(number), 6, "0");
		    	referenceCode += "/" + version;
				if (!StringUtils.isEmpty(series)) {
					referenceCode = series + "/" + referenceCode;
				}
				offer.setReference(referenceCode);
				offer.setIssueDate(rs.getDate(5));
				offer.setSellerName(rs.getString(6));
				offer.setWorkplaceName(rs.getString(7));
				offer.setProjectTaxableBase(CommonUtil.round(rs.getDouble(8)));
				page.getList().add(offer);
				page.setOffset( page.getOffset() + 1 );
				i++;
			}
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(),e);
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(ps);
		}
	}
	
	public void fillSaleInvoicePage(Connection c,PagedList<Invoice> page, ProjectStatParams params) throws ManagerBeanException {
		page.setList( new LinkedList<Invoice>() );		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String select = "SELECT" 
				+" i.id"
				+",i.type"
				+",i.reference_code"
				+",i.issue_date"
				+",i.rname"
				+",i.taxable_base"
				+",i.total"
				+",SUM(id.taxable_base)"
			+" FROM invoice i"
			+" INNER JOIN invoice_detail id ON id.invoice = i.id"
			+" WHERE " + DomainManager.getSQLWhereClause("i.domain")
			+" AND i.type = 1"
			+" AND id.project = ?"
			+" AND i.issue_date BETWEEN ? AND ?"
			+" GROUP BY i.id"
			+" ORDER BY i.series,i.number"
			+" LIMIT " + ( page.getPageSize() + 1 ) + " OFFSET " + page.getOffset(); 
			ps =  c.prepareStatement(select,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			ps.setInt(1, params.getProjectId() );
			ps.setDate(2, new java.sql.Date( params.getFromDate().getTime()) );
			ps.setDate(3, new java.sql.Date( params.getToDate().getTime()) );
			rs = ps.executeQuery();
			int i = 1;
			page.setNextAvailable(false);
			while (rs.next()) {
				if (i > page.getPageSize()) {
					page.setNextAvailable(true);
					break;
				}
				Invoice invoice = new Invoice();
				Integer id = rs.getInt(1);
				invoice.setId(id);
				invoice.setType( rs.getInt(2) );
				invoice.setReference(rs.getString(3));
				invoice.setIssueDate(rs.getDate(4));
				invoice.setRegistryName(rs.getString(5));
				invoice.setInvoiceTaxableBase(rs.getDouble(6));
				invoice.setInvoiceTotal(rs.getDouble(7));
				invoice.setProjectTaxableBase(CommonUtil.round(rs.getDouble(8)));
				page.getList().add(invoice);
				page.setOffset( page.getOffset() + 1 );
				i++;
			}
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(),e);
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(ps);
		}
	}
	
	public void fillCostInvoicePage(Connection c,PagedList<Invoice> page, ProjectStatParams params) throws ManagerBeanException {
		page.setList( new LinkedList<Invoice>() );		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String select = "SELECT" 
				+" i.id"
				+",i.type"
				+",i.reference_code"
				+",i.issue_date"
				+",i.rname"
				+",i.taxable_base"
				+",i.total"
				+",SUM(id.taxable_base)"
			+" FROM invoice i"
			+" INNER JOIN invoice_detail id ON id.invoice = i.id"
			+" WHERE " + DomainManager.getSQLWhereClause("i.domain")
			+" AND i.type != 1"
			+" AND id.project = ?"
			+" AND i.issue_date BETWEEN ? AND ?"
			+" GROUP BY i.id"
			+" ORDER BY i.series,i.number"
			+" LIMIT " + ( page.getPageSize() + 1 ) + " OFFSET " + page.getOffset(); 
			ps =  c.prepareStatement(select,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			ps.setInt(1, params.getProjectId() );
			ps.setDate(2, new java.sql.Date( params.getFromDate().getTime()) );
			ps.setDate(3, new java.sql.Date( params.getToDate().getTime()) );
			rs = ps.executeQuery();
			int i = 1;
			page.setNextAvailable(false);
			while (rs.next()) {
				if (i > page.getPageSize()) {
					page.setNextAvailable(true);
					break;
				}
				Invoice invoice = new Invoice();
				Integer id = rs.getInt(1);
				invoice.setId(id);
				invoice.setType( rs.getInt(2) );
				invoice.setReference(rs.getString(3));
				invoice.setIssueDate(rs.getDate(4));
				invoice.setRegistryName(rs.getString(5));
				invoice.setInvoiceTaxableBase(rs.getDouble(6));
				invoice.setInvoiceTotal(rs.getDouble(7));
				invoice.setProjectTaxableBase(CommonUtil.round(rs.getDouble(8)));
				page.getList().add(invoice);
				page.setOffset( page.getOffset() + 1 );
				i++;
			}
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(),e);
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(ps);
		}
	}

	public void fillDeliveryPage(Connection c,PagedList<Delivery> page, ProjectStatParams params) throws ManagerBeanException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String select = "SELECT" 
				+" d.id"
				+",d.series"
				+",d.number"
				+",d.issue_time"
				+",c.name"
				+",SUM(dd.price * dd.quantity)"
			+" FROM delivery d"
			+" INNER JOIN delivery_detail dd ON dd.delivery = d.id"
			+" INNER JOIN registry c ON c.id = d.customer"
			+" WHERE " + DomainManager.getSQLWhereClause("d.domain")
			+" AND d.status = 0"
			+" AND d.project = ?"
			+" AND d.issue_time BETWEEN ? AND ?"
			+" GROUP BY d.id"
			+" ORDER BY d.series,d.number"; 
			ps =  c.prepareStatement(select,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			ps.setInt(1, params.getProjectId() );
			ps.setDate(2, new java.sql.Date( params.getFromDate().getTime()) );
			ps.setDate(3, new java.sql.Date( params.getToDate().getTime()) );
			rs = ps.executeQuery();
			int i = 1;
			page.setNextAvailable(false);
 			while (rs.next()) {
				Integer id = rs.getInt(1);
				Delivery delivery = new Delivery()
						.setId(id)
						.setType("Venta")
						.setReference(rs.getString(2) + "/" + rs.getInt(3))
						.setIssueDate(rs.getDate(4))
						.setRegistryName(rs.getString(5))
						.setTotal(rs.getDouble(6));
				page.getList().add(delivery);
				i++;
			}
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(),e);
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(ps);
		}
	}
	
	public void fillIncomePage(Connection c,PagedList<Delivery> page, ProjectStatParams params) throws ManagerBeanException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String select = "SELECT" 
					+" i.id"
					+",i.reference_code"
					+",i.issue_time"
					+",s.name"
					+",SUM(id.price * id.quantity)"
			+" FROM income i"
			+" INNER JOIN income_detail id ON id.income = i.id"
			+" INNER JOIN registry s ON s.id = i.supplier"
			+" WHERE " + DomainManager.getSQLWhereClause("i.domain")
			+" AND i.status = 0"
			+" AND id.project = ?"
			+" AND i.issue_time BETWEEN ? AND ?"
			+" GROUP BY i.id"
			+" ORDER BY i.reference_code";
			ps =  c.prepareStatement(select,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			ps.setInt(1, params.getProjectId() );
			ps.setDate(2, new java.sql.Date( params.getFromDate().getTime()) );
			ps.setDate(3, new java.sql.Date( params.getToDate().getTime()) );
			rs = ps.executeQuery();
			int i = 1;
			page.setNextAvailable(false);
			while (rs.next()) {
				Integer id = rs.getInt(1);
				Delivery income = new Delivery()
						.setId(id)
						.setType("Compra")
						.setReference(rs.getString(2))
						.setIssueDate(rs.getDate(3))
						.setRegistryName(rs.getString(4))
						.setTotal(rs.getDouble(5));
				page.getList().add(income);
				i++;
			}
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(),e);
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(ps);
		}
	}
	
	public String getLastProjectAlias(Connection c) throws ManagerBeanException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String select =
					  "SELECT p.alias"
					+ " FROM project p "
					+" WHERE " + DomainManager.getSQLWhereClause("p.domain")
					+" ORDER BY p.id DESC LIMIT 1";
			ps =  c.prepareStatement(select,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);		
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString(1);
			}
			return "-";
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(),e);
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(ps);
		}
	}
	
	public void fillDailyTrackingPage(Connection c,PagedList<DailyTracking> page, ProjectStatParams params) throws ManagerBeanException {
		page.setList( new LinkedList<DailyTracking>() );		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String select = "SELECT"
					+" dt.id"
					+",dt.tracking_date"
					+",dt.comments"
					+",th.name"
					+",j.description"
					+",r.name"
					+",dt.tracking_duration"
					+",dt.cost"
					+" FROM daily_tracking dt"
				+" LEFT OUTER JOIN registry th ON th.id = dt.task_holder"
				+" LEFT OUTER JOIN registry r ON r.id = dt.registry"
				+" LEFT OUTER JOIN job_type j ON j.id = dt.job_type"
				+" WHERE " + DomainManager.getSQLWhereClause("dt.domain")
				+" AND dt.project = ?"
				+" AND dt.tracking_date BETWEEN ? AND ?"
				+" ORDER BY dt.tracking_date DESC"
				+" LIMIT " + ( page.getPageSize() + 1 ) + " OFFSET " + page.getOffset();
			ps =  c.prepareStatement(select,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			ps.setInt(1, params.getProjectId() );
			ps.setDate(2, new java.sql.Date( params.getFromDate().getTime()) );
			ps.setDate(3, new java.sql.Date( params.getToDate().getTime()) );
			rs = ps.executeQuery();
			int i = 1;
			page.setNextAvailable(false);
			while (rs.next()) {
				if (i > page.getPageSize()) {
					page.setNextAvailable(true);
					break;
				}
				DailyTracking dt = new DailyTracking();
				Integer id = rs.getInt(1);
				dt.setId(id);
				dt.setTrackingDate(rs.getDate(2));
				dt.setComments(rs.getString(3));
				dt.setTaskHolderName(rs.getString(4));
				dt.setJobDescription(rs.getString(5));
				dt.setRegistryName(rs.getString(6));
				dt.setTrackingDuration(rs.getDouble(7));
				dt.setCost(rs.getDouble(8));
				page.getList().add(dt);
				page.setOffset( page.getOffset() + 1 );
				i++;
			}
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(),e);
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(ps);
		}
	}
	
}
