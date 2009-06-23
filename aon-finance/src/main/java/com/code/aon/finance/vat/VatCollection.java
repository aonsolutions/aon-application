package com.code.aon.finance.vat;

import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.finance.enumeration.InvoiceTransactionType;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.enumeration.VatReportType;
import com.code.aon.finance.enumeration.VatType;

public class VatCollection {

	public List<Vat> getVatList(VatCollectionParameters params) throws ManagerBeanException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			StringWriter stmt = new StringWriter();
			stmt.append("SELECT i.type,YEAR(i.issue_date) YEAR,QUARTER(i.issue_date) QUARTER, ");
			stmt.append("	   MONTH(i.issue_date) MONTH,it.percentage,it.surcharge,i.transaction,");
			stmt.append("	   i.investment,SUM(id.taxable_base),");
			stmt.append("	   SUM(ROUND(id.taxable_base * it.percentage / 100, 2)),SUM(ROUND(id.taxable_base * it.surcharge / 100, 2))");
			stmt.append(" FROM invoice_tax it ");
			stmt.append(" INNER JOIN invoice_detail id ON (it.invoice_detail = id.id)"); 
			stmt.append(" INNER JOIN invoice i ON (id.invoice = i.id)"); 
			stmt.append(" WHERE it.tax_type = 1");
			if (params.getFromDate() != null) {
				stmt.append(" AND i.issue_date >= ?");
			}
			if (params.getToDate() != null) {
				stmt.append(" AND i.issue_date <= ?");
			}
			if (params.getSecurityLevel() != null) {
				stmt.append(" AND i.security_level = " + params.getSecurityLevel().ordinal());
			}
			stmt.append(" GROUP BY i.TYPE,QUARTER(i.issue_date),MONTH(i.issue_date),YEAR(i.issue_date),");
			stmt.append("         it.percentage,it.surcharge,i.transaction,i.investment");
			stmt.append(" ORDER BY type DESC,year,quarter,month,i.transaction,i.investment,");
			stmt.append("		 it.percentage,it.surcharge");
			ps = HibernateUtil.getSQLConnection().prepareStatement(stmt.toString(),
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			if (params.getFromDate() != null) {
				ps.setDate(1, new java.sql.Date( params.getFromDate().getTime()));
			}
			if (params.getToDate() != null) {
				ps.setDate(2, new java.sql.Date( params.getToDate().getTime()));
			}
			rs = ps.executeQuery();
			List<Vat> vats = new LinkedList<Vat>();
			while (rs.next()) {
				Vat vat = new Vat();
				InvoiceType type = InvoiceType.values()[rs.getInt(1)];
				vat.setInvoiceType( type );
				vat.setYear(rs.getInt(2));
				vat.setQuarter(rs.getInt(3));
				vat.setMonth(rs.getInt(4));
				vat.setPercent(rs.getDouble(5));
				vat.setSurcharge(rs.getDouble(6));
				InvoiceTransactionType transaction = InvoiceTransactionType.values()[rs.getInt(7)];
				vat.setTransactionType(transaction);
				vat.setInvestment(rs.getBoolean(8));
				vat.setBase(rs.getDouble(9));
				vat.setVatQuota(rs.getDouble(10));
				vat.setSurchargeQuota(rs.getDouble(11));
				vats.add(vat);
			}
			return vats;
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

	public List<Vat> getVatDetailList(VatCollectionParameters params) throws ManagerBeanException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String operation = "CEIL((i.investment+ELT((i.type+1),20,10,20,20)) / 10) "; 
			StringWriter stmt = new StringWriter();
			stmt.append(" SELECT i.type,i.transaction,i.issue_date,i.reference_code,i.rdocument,i.rname ");
			stmt.append("  ,it.percentage,it.surcharge,SUM(id.taxable_base) ");
			stmt.append("  ,SUM(ROUND(id.taxable_base * it.percentage / 100, 2)),SUM(ROUND(id.taxable_base * it.surcharge / 100, 2)),");
			stmt.append(operation + " vatType ");
			stmt.append("  FROM invoice_tax it ");
			stmt.append("  INNER JOIN invoice_detail id ON (it.invoice_detail = id.id) ");
			stmt.append("  INNER JOIN invoice i ON (id.invoice = i.id) ");
			stmt.append("  WHERE it.tax_type = 1 ");
			if (params.getFromDate() != null) {
				stmt.append(" AND i.issue_date >= ?");
			}
			if (params.getToDate() != null) {
				stmt.append(" AND i.issue_date <= ?");
			}
			if (params.getVatPercent() != null) {
				stmt.append(" AND it.percentage = ?");
			}
			if (params.getSurchargePercent() != null) {
				stmt.append(" AND it.surcharge = ?");
			}
			if (params.getVatType() != null) {
				stmt.append(" AND " + operation + " = "+ (params.getVatType().ordinal() + 1));	
			}
			if (params.getVatReportType() != null) {
				if (params.getVatReportType() == VatReportType.GENERAL) {
					stmt.append(" AND i.transaction = "+ InvoiceTransactionType.NATIONAL.ordinal());
				} else if (params.getVatReportType() == VatReportType.SURCHARGE) {
					stmt.append(" AND i.transaction = "+ InvoiceTransactionType.NATIONAL.ordinal());
					stmt.append(" AND it.surcharge > 0");
				} else if (params.getVatReportType() == VatReportType.INTRACOMUNNITARY) {
					stmt.append(" AND i.transaction = "+ InvoiceTransactionType.INTRACOMUNNITARY.ordinal());
				}
				else if (params.getVatReportType() == VatReportType.EXTRACOMUNNITARY) {
					stmt.append(" AND i.transaction = "+ InvoiceTransactionType.EXTRACOMUNNITARY.ordinal());
				}				
			}
			if (params.getSecurityLevel() != null) {
				stmt.append(" AND i.security_level = " + params.getSecurityLevel().ordinal());
			}
			stmt.append(" GROUP BY i.type,i.transaction,i.issue_date,i.reference_code,i.rdocument,i.rname ");
			stmt.append("  ,it.percentage,it.surcharge,");
			stmt.append(operation);
			stmt.append(" ORDER BY vatType,i.transaction,i.issue_date,i.reference_code");
			
			ps = HibernateUtil.getSQLConnection().prepareStatement(stmt.toString(),
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			int i = 0;
			if (params.getFromDate() != null) {
				ps.setDate(++i, new java.sql.Date( params.getFromDate().getTime()));
			}
			if (params.getToDate() != null) {
				ps.setDate(++i, new java.sql.Date( params.getToDate().getTime()));
			}
			if (params.getVatPercent() != null) {
				ps.setDouble(++i, params.getVatPercent());
			}
			if (params.getSurchargePercent() != null) {
				ps.setDouble(++i, params.getSurchargePercent());
			}
			rs = ps.executeQuery();
			List<Vat> vats = new LinkedList<Vat>();
			while (rs.next()) {
				Vat vat = new Vat();
				InvoiceType type = InvoiceType.values()[rs.getInt(1)];
				vat.setInvoiceType( type );
				InvoiceTransactionType transaction = InvoiceTransactionType.values()[rs.getInt(2)];
				vat.setTransactionType(transaction);
				vat.setDate(rs.getDate(3));
				vat.setReference(rs.getString(4));
				vat.setDocument(rs.getString(5));
				vat.setName(rs.getString(6));
				vat.setPercent(rs.getDouble(7));
				vat.setSurcharge(rs.getDouble(8));
				vat.setBase(rs.getDouble(9));
				vat.setVatQuota(rs.getDouble(10));
				vat.setSurchargeQuota(rs.getDouble(11));
				VatType vatType = VatType.values()[(rs.getInt(12) - 1)];
				vat.setVatType(vatType);
				vats.add(vat);
			}
			return vats;
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
