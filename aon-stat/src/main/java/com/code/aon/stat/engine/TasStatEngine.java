package com.code.aon.stat.engine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.Country;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.code.aon.finance.enumeration.InvoiceType;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.stat.tas.TasStatDetail;
import com.code.aon.stat.tas.TasStatDetailType;
import com.code.aon.stat.tas.TasStatHeader;
import com.code.aon.stat.tas.TasStatParams;

public class TasStatEngine {
	
	private static String ID="id";
	private static String TYPE="type";
	private static String SERIES="series";
	private static String NUMBER="number";
	private static String REFERENCE_CODE="referenceCode";
	private static String DATE="date";
	private static String DOCUMENT_TYPE="documentType";
	private static String DOCUMENT_COUNTRY="documentCountry";
	private static String DOCUMENT="document";
	private static String NAME="name";
	private static String TASK_HOLDER="taskHolder";
	private static String COMMENTS="comments";
	private static String STATUS="status";
	private static String STATUS_DATE="statusDate";
	private static String TOTAL="total";

	private static String TAS_ITEM_STATEMENT = 
		"SELECT pt.project " + ID 
		+",pt.series " + SERIES
		+",pt.number " + NUMBER
		+",p.date "+ DATE
		+",r.document_type "+ DOCUMENT_TYPE 
		+",r.document_country " + DOCUMENT_COUNTRY
		+",r.document "+ DOCUMENT
		+",r.name "+ NAME
		+",rth.name " + TASK_HOLDER
		+",pt.comments "+ COMMENTS
		+",pt.status "+ STATUS 
		+",pt.status_date "+ STATUS_DATE
		+",null "+ TOTAL
		+" FROM project_tas pt"
		+" INNER JOIN project p ON p.id = pt.project"
		+" INNER JOIN tas_item ti ON pt.tas_item  = ti.id"
		+" INNER JOIN target t ON pt.target = t.registry"
		+" INNER JOIN registry r ON r.id = t.registry"
		+" LEFT OUTER JOIN task_holder th ON pt.task_holder = th.registry"
		+" LEFT OUTER JOIN registry rth ON rth.id = th.registry"
		+" WHERE ti.publicCode=?";

	private static String OFFER_STATEMENT = 
		"SELECT o.id " + ID
		+",o.series " + SERIES
		+",o.number " + NUMBER
		+",o.issue_date " + DATE
		+",r.document_type " + DOCUMENT_TYPE
		+",r.document_country " + DOCUMENT_COUNTRY
		+",r.document " + DOCUMENT
		+",r.name " + NAME
		+",null " + TASK_HOLDER
		+",o.comments " + COMMENTS
		+",o.status " + STATUS
		+",null " + STATUS_DATE
		+",null "+ TOTAL
		+" FROM offer o"
		+" INNER JOIN registry r ON r.id = o.target"
		+" WHERE o.project=?"
		+" AND " + DomainManager.getStaticSQLWhereClause("o.domain");

	private static String INVOICE_STATEMENT = 
			"SELECT i.id " + ID
			+",i.type " + TYPE
			+",i.series " + SERIES
			+",i.number " + NUMBER
			+",i.issue_date " + DATE
			+",r.document_type " + DOCUMENT_TYPE
			+",r.document_country " + DOCUMENT_COUNTRY
			+",r.document " + DOCUMENT
			+",r.name " + NAME
			+",null " + TASK_HOLDER
			+",i.comments " + COMMENTS
			+",i.status " + STATUS
			+",i.total " + TOTAL
			+",null " + STATUS_DATE
			+" FROM invoice i"
			+" INNER JOIN registry r ON r.id = i.registry"
			+" WHERE i.project=?"
			+" AND " + DomainManager.getStaticSQLWhereClause("i.domain");

	private static String SALES_STATEMENT = 
			"SELECT s.id " + ID
			+",null " + TYPE
			+",s.series " + SERIES
			+",s.number " + NUMBER
			+",s.issue_date " + DATE
			+",r.document_type " + DOCUMENT_TYPE
			+",r.document_country " + DOCUMENT_COUNTRY
			+",r.document " + DOCUMENT
			+",r.name " + NAME
			+",null " + TASK_HOLDER
			+",s.comments " + COMMENTS
			+",s.status " + STATUS
			+",null " + TOTAL
			+",null " + STATUS_DATE
			+" FROM sales s"
			+" INNER JOIN registry r ON r.id = s.customer"
			+" WHERE s.project=?"
			+" AND " + DomainManager.getStaticSQLWhereClause("s.domain");

	private static String PURCHASE_STATEMENT = 
			"SELECT p.id " + ID
			+",null " + TYPE
			+",p.series " + SERIES
			+",p.number " + NUMBER
			+",p.issue_date " + DATE
			+",r.document_type " + DOCUMENT_TYPE
			+",r.document_country " + DOCUMENT_COUNTRY
			+",r.document " + DOCUMENT
			+",r.name " + NAME
			+",null " + TASK_HOLDER
			+",p.comments " + COMMENTS
			+",p.status " + STATUS
			+",null " + TOTAL
			+",null " + STATUS_DATE
			+" FROM purchase p"
			+" INNER JOIN registry r ON r.id = p.supplier"
			+" WHERE p.project=?"
			+" AND " + DomainManager.getStaticSQLWhereClause("p.domain");

	private static String DELIVERY_STATEMENT = 
			"SELECT d.id " + ID
			+",null " + TYPE
			+",d.series " + SERIES
			+",d.number " + NUMBER
			+",d.issue_time " + DATE
			+",r.document_type " + DOCUMENT_TYPE
			+",r.document_country " + DOCUMENT_COUNTRY
			+",r.document " + DOCUMENT
			+",r.name " + NAME
			+",null " + TASK_HOLDER
			+",d.comments " + COMMENTS
			+",d.status " + STATUS
			+",null " + TOTAL
			+",null " + STATUS_DATE
			+" FROM delivery d"
			+" INNER JOIN registry r ON r.id = d.customer"
			+" WHERE d.project=?"
			+" AND " + DomainManager.getStaticSQLWhereClause("d.domain");

	private static String INCOME_STATEMENT = 
			"SELECT i.id " + ID
			+",null " + TYPE
			+",i.reference_code " + REFERENCE_CODE
			+",i.issue_time " + DATE
			+",r.document_type " + DOCUMENT_TYPE
			+",r.document_country " + DOCUMENT_COUNTRY
			+",r.document " + DOCUMENT
			+",r.name " + NAME
			+",null " + TASK_HOLDER
			+",i.comments " + COMMENTS
			+",i.status " + STATUS
			+",null " + TOTAL
			+",null " + STATUS_DATE
			+" FROM income i"
			+" INNER JOIN registry r ON r.id = i.supplier"
			+" WHERE i.project=?"
			+" AND " + DomainManager.getStaticSQLWhereClause("i.domain");

	public List<TasStatHeader> getTasHeaders(TasStatParams params) throws ManagerBeanException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = DatabaseUtil.getConnection(params.getDomainName());

			StringBuffer stmt = new StringBuffer();
			stmt.append("SELECT r.id,r.document,r.name,ti.id,ti.publicCode,mk.name,md.name");
			stmt.append(" FROM project_tas pt");
			stmt.append(" INNER JOIN project p ON p.id = pt.project");
			stmt.append(" INNER JOIN tas_item ti ON pt.tas_item = ti.id");
			stmt.append(" INNER JOIN model md ON ti.model = md.id");
			stmt.append(" INNER JOIN make mk ON md.make = mk.id");
			stmt.append(" INNER JOIN target t ON pt.target = t.registry");
			stmt.append(" INNER JOIN registry r ON r.id = t.registry");
			stmt.append(" WHERE " + DomainManager.getSQLWhereClause("pt.domain"));
			if (params.getFromDate() != null) {
				stmt.append(" AND p.date >= ?");
			}
			if (params.getToDate() != null) {
				stmt.append(" AND p.date <= ?");
			}
			if (params.getTarget() != null) {
				stmt.append(" AND pt.target = ?");
			}
			if (params.getTasItem() != null) {
				stmt.append(" AND ti.id = ?");
			} else {
				if (params.getModel() != null) {
					stmt.append(" AND ti.model = ?");	
				}
				if (StringUtils.isNotBlank(params.getPublicCode())) {
					stmt.append(" AND ti.publicCode LIKE ?");	
				}
				if (StringUtils.isNotBlank(params.getPrivateCode())) {
					stmt.append(" AND ti.privateCode LIKE ?");
				}
				if (StringUtils.isNotBlank(params.getDescription())) {
					stmt.append(" AND ti.description LIKE ?");
				}
				if (StringUtils.isNotBlank(params.getAddInfo())) {
					stmt.append(" AND ti.add_info LIKE ?");
				}
			}
			
			stmt.append(" GROUP BY r.id,r.document,r.name,ti.publicCode,mk.name,md.name");
			stmt.append(" ORDER BY r.name");

			ps = conn.prepareStatement(stmt.toString(), ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			int i = 0;
			if (params.getFromDate() != null) {
				ps.setDate(++i, new java.sql.Date(params.getFromDate().getTime()));
			}
			if (params.getToDate() != null) {
				ps.setDate(++i, new java.sql.Date(params.getToDate().getTime()));
			}
			if (params.getTarget() != null) {
				ps.setInt(++i, params.getTarget());
			}
			if (params.getTasItem() != null) {
				ps.setInt(++i, params.getTasItem());
			} else {
				if (params.getModel() != null) {
					ps.setInt(++i, params.getModel());	
				}
				if (StringUtils.isNotBlank(params.getPublicCode())) {
					ps.setString(++i, params.getPublicCode());
				}
				if (StringUtils.isNotBlank(params.getPrivateCode())) {
					ps.setString(++i, params.getPrivateCode());
				}
				if (StringUtils.isNotBlank(params.getDescription())) {
					ps.setString(++i, params.getDescription());
				}
				if (StringUtils.isNotBlank(params.getAddInfo())) {
					ps.setString(++i, params.getAddInfo());
				}
			}
			rs = ps.executeQuery();
			List<TasStatHeader> stats = new LinkedList<TasStatHeader>();
			while (rs.next()) {
				TasStatHeader owner = new TasStatHeader();
				owner.setId(rs.getInt(1));
				owner.setDocument(rs.getString(2));
				owner.setName(rs.getString(3));
				owner.setTasItem(rs.getInt(4));
				owner.setPublicCode(rs.getString(5));
				owner.setMake(rs.getString(6));
				owner.setModel(rs.getString(7));
				stats.add(owner);
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

	public List<TasStatDetail> getTasDetails(TasStatHeader header, TasStatParams params) throws ManagerBeanException {
		if (header == null || header.getTasItem() == null) {
			throw new ManagerBeanException("Vehículo no identificado (id null).");
		}
		PreparedStatement offerPs = null;
		PreparedStatement invoicePs = null;
		PreparedStatement salesPs = null;
		PreparedStatement purchasePs = null;
		PreparedStatement deliveryPs = null;
		PreparedStatement incomePs = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = DatabaseUtil.getConnection(params.getDomainName());

			fillHeader(conn, header);

			StringBuffer stmt = new StringBuffer();
			stmt.append(TAS_ITEM_STATEMENT);
			stmt.append(" AND " + DomainManager.getStaticSQLWhereClause("ti.domain") );
			if (params.getFromDate() != null) {
				stmt.append(" AND p.date >= ?");
			}
			if (params.getToDate() != null) {
				stmt.append(" AND p.date <= ?");
			}
			stmt.append(" ORDER BY p.date desc");

			offerPs = conn.prepareStatement(OFFER_STATEMENT, ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			invoicePs = conn.prepareStatement(INVOICE_STATEMENT, ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			salesPs = conn.prepareStatement(SALES_STATEMENT, ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			purchasePs = conn.prepareStatement(PURCHASE_STATEMENT, ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			deliveryPs = conn.prepareStatement(DELIVERY_STATEMENT, ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			incomePs = conn.prepareStatement(INCOME_STATEMENT, ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			ps = conn.prepareStatement(stmt.toString(), ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			int i = 0;
			ps.setString(++i, header.getPublicCode());
			if (params.getFromDate() != null) {
				ps.setDate(++i, new java.sql.Date(params.getFromDate().getTime()));
			}
			DomainManager.fillHostVariables(ps, ++i);
			if (params.getToDate() != null) {
				ps.setDate(++i, new java.sql.Date(params.getToDate().getTime()));
			}
			rs = ps.executeQuery();
			List<TasStatDetail> details = new LinkedList<TasStatDetail>();
			while (rs.next()) {
				TasStatDetail detail = populateTasStatDetail(TasStatDetailType.PROJECT, rs );
				details.add(detail);
				details.addAll( populateOffer(offerPs, detail) );
				details.addAll( populateSales(salesPs, detail) );
				details.addAll( populatePurchase(purchasePs, detail) );
				details.addAll( populateDelivery(deliveryPs, detail) );
				details.addAll( populateIncome(incomePs, detail) );
				details.addAll( populateInvoice(invoicePs, detail) );
			}
			return details;
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} catch (AonConnectionException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(invoicePs);
			DatabaseUtil.closeQuietly(salesPs);
			DatabaseUtil.closeQuietly(purchasePs );
			DatabaseUtil.closeQuietly(deliveryPs);
			DatabaseUtil.closeQuietly(incomePs);
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(offerPs);
			DatabaseUtil.closeQuietly(conn);
		}
	}
	
	private List<TasStatDetail> populateOffer(PreparedStatement detailsPs,TasStatDetail detail) throws SQLException {
		ResultSet detailsRs = null;
		try {
			List<TasStatDetail> details = new LinkedList<TasStatDetail>(); 
			detailsPs.setInt(1, detail.getId());
			DomainManager.fillHostVariables(detailsPs, 2);
			detailsRs = detailsPs.executeQuery();
			while (detailsRs.next()) {
				details.add(populateTasStatDetail(TasStatDetailType.OFFER, detailsRs ));
			}
			detailsRs.close();
			return details;
		} finally {
			if (detailsRs != null) {
				try {
					detailsRs.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	private List<TasStatDetail> populateInvoice(PreparedStatement detailsPs,TasStatDetail detail) throws SQLException {
		ResultSet detailsRs = null;
		try {
			List<TasStatDetail> details = new LinkedList<TasStatDetail>(); 
			detailsPs.setInt(1, detail.getId());
			DomainManager.fillHostVariables(detailsPs, 2);
			detailsRs = detailsPs.executeQuery();
			while (detailsRs.next()) {
				TasStatDetailType type = null;
				InvoiceType invoiceType = InvoiceType.values()[detailsRs.getInt(TYPE)];
				if  (invoiceType == InvoiceType.SALES) {
					type = TasStatDetailType.SALES_INVOICE;
				} else if  (invoiceType == InvoiceType.PURCHASE) {
					type = TasStatDetailType.PURCHASE_INVOICE;
				} else {
					type = TasStatDetailType.EXPENSE_INVOICE;
				}
				details.add(populateTasStatDetail(type, detailsRs ));
			}
			detailsRs.close();
			return details;
		} finally {
			if (detailsRs != null) {
				try {
					detailsRs.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	private List<TasStatDetail> populateSales(PreparedStatement detailsPs,TasStatDetail detail) throws SQLException {
		ResultSet detailsRs = null;
		try {
			List<TasStatDetail> details = new LinkedList<TasStatDetail>(); 
			detailsPs.setInt(1, detail.getId());
			DomainManager.fillHostVariables(detailsPs, 2);
			detailsRs = detailsPs.executeQuery();
			while (detailsRs.next()) {
				details.add(populateTasStatDetail(TasStatDetailType.SALES, detailsRs ));
			}
			detailsRs.close();
			return details;
		} finally {
			if (detailsRs != null) {
				try {
					detailsRs.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	private List<TasStatDetail> populatePurchase(PreparedStatement detailsPs,TasStatDetail detail) throws SQLException {
		ResultSet detailsRs = null;
		try {
			List<TasStatDetail> details = new LinkedList<TasStatDetail>(); 
			detailsPs.setInt(1, detail.getId());
			DomainManager.fillHostVariables(detailsPs, 2);
			detailsRs = detailsPs.executeQuery();
			while (detailsRs.next()) {
				details.add(populateTasStatDetail(TasStatDetailType.PURCHASE, detailsRs ));
			}
			detailsRs.close();
			return details;
		} finally {
			if (detailsRs != null) {
				try {
					detailsRs.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	private List<TasStatDetail> populateDelivery(PreparedStatement detailsPs,TasStatDetail detail) throws SQLException {
		ResultSet detailsRs = null;
		try {
			List<TasStatDetail> details = new LinkedList<TasStatDetail>(); 
			detailsPs.setInt(1, detail.getId());
			DomainManager.fillHostVariables(detailsPs, 2);
			detailsRs = detailsPs.executeQuery();
			while (detailsRs.next()) {
				details.add(populateTasStatDetail(TasStatDetailType.DELIVERY, detailsRs ));
			}
			detailsRs.close();
			return details;
		} finally {
			if (detailsRs != null) {
				try {
					detailsRs.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	private List<TasStatDetail> populateIncome(PreparedStatement detailsPs,TasStatDetail detail) throws SQLException {
		ResultSet detailsRs = null;
		try {
			List<TasStatDetail> details = new LinkedList<TasStatDetail>(); 
			detailsPs.setInt(1, detail.getId());
			DomainManager.fillHostVariables(detailsPs, 2);
			detailsRs = detailsPs.executeQuery();
			while (detailsRs.next()) {
				details.add(populateTasStatDetail(TasStatDetailType.INCOME, detailsRs, true ));
			}
			detailsRs.close();
			return details;
		} finally {
			if (detailsRs != null) {
				try {
					detailsRs.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	private TasStatDetail populateTasStatDetail(TasStatDetailType type, ResultSet rs) throws SQLException {
		return populateTasStatDetail(type, rs, false);
	}
	
	private TasStatDetail populateTasStatDetail(TasStatDetailType type, ResultSet rs, boolean isIncome) throws SQLException {
		TasStatDetail detail = new TasStatDetail(type);
		detail.setId(rs.getInt(ID));
		if(isIncome){
			detail.setReferenceCode(rs.getString(REFERENCE_CODE));
		} else {
			detail.setSeries(rs.getString(SERIES));
			detail.setNumber(rs.getInt(NUMBER));
		}
		detail.setDate(rs.getDate(DATE));
		detail.setDocumentType(DocumentType.values()[rs.getInt(DOCUMENT_TYPE)]);
		detail.setDocumentCountry(Country.valueOf(rs.getString(DOCUMENT_COUNTRY)) );
		detail.setDocument(rs.getString(DOCUMENT));
		detail.setName(rs.getString(NAME));
		detail.setTaskHolderName(rs.getString(TASK_HOLDER));
		detail.setComments(rs.getString(COMMENTS));
		detail.setTotal(rs.getDouble(TOTAL));
		return detail;
	}

	private void fillHeader(Connection conn, TasStatHeader header) throws ManagerBeanException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {

			StringBuffer stmt = new StringBuffer();
			stmt.append("SELECT ti.description,ti.add_info");
			stmt.append(" FROM tas_item ti");
			stmt.append(" WHERE ti.id=?");
			ps = conn.prepareStatement(stmt.toString(), ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			int i = 0;
			ps.setInt(++i, header.getTasItem());
			rs = ps.executeQuery();
			if (rs.next()) {
				String s = rs.getString(1); 
				header.setTasItemDescription(StringUtils.isNotBlank(s)?s:null);
				s = rs.getString(2);
				header.setTasItemAdditionalInfo(StringUtils.isNotBlank(s)?s:null);
			}
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

	public List<TasStatDetail> getOwnerDetails(TasStatHeader header, com.code.aon.stat.tas.TasStatParams statParams) throws ManagerBeanException {
		return null;
	}
}
