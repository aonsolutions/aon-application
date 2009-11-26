package com.code.aon.accounting.vat;

import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.finance.enumeration.InvoiceTransactionType;
import com.code.aon.finance.enumeration.InvoiceType;

public class VatCollection {

	public List<Vat> getSummaryList(VatCollectionParameters params) throws ManagerBeanException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			StringWriter stmt = new StringWriter();
			stmt.append("SELECT i.type,YEAR(i.issue_date) YEAR,QUARTER(i.issue_date) QUARTER, ");
			stmt.append("	   MONTH(i.issue_date) MONTH,it.percentage,it.surcharge,i.transaction,");
			stmt.append("	   i.investment,SUM(id.taxable_base)");
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
/*	
	@Deprecated
	public List<Vat> getList(VatCollectionParameters params, boolean summary ) throws ManagerBeanException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			StringWriter stmt = new StringWriter();
			stmt.append("SELECT it.tax_type,it.percentage,it.surcharge,");
			stmt.append("id.taxable_base,");
			stmt.append("i.series,i.number,i.reference_code,i.rdocument,");
			stmt.append("i.rname,i.issue_date,i.transaction");
			stmt.append(" FROM invoice_tax it");
			stmt
					.append(" INNER JOIN invoice_detail id ON (it.invoice_detail = id.id)");
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
			if (params.getVatType() == VatType.INPUT) {
				stmt.append(" AND  i.type != " + InvoiceType.SALES.ordinal());
				stmt.append(" AND i.investment = 0");
			} else if (params.getVatType() == VatType.OUTPUT) {
				stmt.append(" AND i.type = " + InvoiceType.SALES.ordinal());
				stmt.append(" AND i.investment = 0");
			} else if (params.getVatType() == VatType.INVESTMENT) {
				stmt.append(" AND i.investment = 1");
			}
			if (summary) {
				stmt.append(" ORDER BY YEAR(i.issue_date),MONTH(i.issue_date),it.tax_type,it.percentage");
			} else {
				stmt.append(" ORDER BY i.series,i.number");	
			}
			
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
				TaxType type = TaxType.values()[rs.getInt(1)];
				vat.setTaxType( type );
				vat.setPercent(rs.getDouble(2));
				vat.setSurcharge(rs.getDouble(3));
				vat.setBase(rs.getDouble(4));
				vat.setSeries(rs.getString(5));
				vat.setNumber(rs.getInt(6));
				vat.setReference(rs.getString(7));
				vat.setDocument(rs.getString(8));
				vat.setName(rs.getString(9));
				vat.setDate(rs.getDate(10));
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
*/
}
