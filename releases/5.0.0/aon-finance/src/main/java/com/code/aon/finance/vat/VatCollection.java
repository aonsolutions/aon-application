package com.code.aon.finance.vat;

import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.enumeration.VatReportOrder;
import com.code.aon.finance.enumeration.VatReportType;
import com.code.aon.finance.enumeration.VatType;

public class VatCollection {

	public List<Vat> getVatList(VatCollectionParameters params) throws ManagerBeanException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			StringWriter stmt = new StringWriter();
			stmt.append("SELECT i.type,YEAR(i.tax_date) YEAR,QUARTER(i.tax_date) QUARTER, ");
			stmt.append("	   MONTH(i.tax_date) MONTH,it.percentage,it.surcharge,i.transaction,");
			stmt.append("	   i.investment,SUM(id.taxable_base),");
			stmt.append("	   SUM( IF(it.quota != 0,it.quota,ROUND(id.taxable_base * it.percentage / 100, 2) ) ) IVA,");
			stmt.append("	   SUM( IF(it.surcharge_quota != 0,it.surcharge_quota,ROUND(id.taxable_base * it.surcharge / 100, 2) ) ) RE");
			stmt.append(" FROM invoice_tax it ");
			stmt.append(" INNER JOIN invoice_detail id ON (it.invoice_detail = id.id)"); 
			stmt.append(" INNER JOIN invoice i ON (id.invoice = i.id)"); 
			stmt.append(" WHERE it.tax_type = 1");
			if (params.getFromDate() != null) {
				stmt.append(" AND i.tax_date >= ?");
			}
			if (params.getToDate() != null) {
				stmt.append(" AND i.tax_date <= ?");
			}
			if (params.getFromInvoiceDate() != null) {
				stmt.append(" AND i.issue_date >= ?");
			}
			if (params.getToInvoiceDate() != null) {
				stmt.append(" AND i.issue_date <= ?");
			}
			if (params.getFromSeries() != null) {
				stmt.append(" AND i.series >= ?");
			}
			if (params.getToSeries() != null) {
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
			stmt.append(" GROUP BY i.TYPE,QUARTER(i.tax_date),MONTH(i.tax_date),YEAR(i.tax_date),");
			stmt.append("         it.percentage,it.surcharge,i.transaction,i.investment");
			stmt.append(" ORDER BY type DESC,year,quarter,month,i.transaction,i.investment,");
			stmt.append("		 it.percentage,it.surcharge");
			String sessionName = HibernateUtil.getSessionFactoryName();
			ps = HibernateUtil.getSQLConnection(sessionName).prepareStatement(stmt.toString(),
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			int i = 0;
			if (params.getFromDate() != null) {
				ps.setDate(++i, new java.sql.Date( params.getFromDate().getTime()));
			}
			if (params.getToDate() != null) {
				ps.setDate(++i, new java.sql.Date( params.getToDate().getTime()));
			}
			if (params.getFromInvoiceDate() != null) {
				ps.setDate(++i, new java.sql.Date( params.getFromInvoiceDate().getTime()));
			}
			if (params.getToInvoiceDate() != null) {
				ps.setDate(++i, new java.sql.Date( params.getToInvoiceDate().getTime()));
			}
			
			if (params.getFromSeries() != null) {
				ps.setString(++i, params.getFromSeries());
			}
			if (params.getToSeries() != null) {
				ps.setString(++i, params.getToSeries());
			}
			
			if (params.getFromNumber() != null) {
				ps.setInt(++i, params.getFromNumber());
			}
			if (params.getToNumber() != null) {
				ps.setInt(++i, params.getToNumber());
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

	public List<Vat> getVatDetailList(VatCollectionParameters params, VatReportOrder order) throws ManagerBeanException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			// Mediante la operacion siguiente se determinada cual de los tipos de factura
			// es de ventas, compras o inversión y se asocia al tipo de IVA correspondiente.
			String operation = "CEIL((i.investment+ELT((i.type+1),20,10,20,20)) / 10) ";
			// ---------------------------------------------------------------------------
			StringWriter stmt = new StringWriter();
			stmt.append(" SELECT i.type,i.transaction,i.tax_date,i.issue_date,i.reference_code,i.series,i.number,i.rdocument,i.rname ");
			stmt.append("  ,it.percentage,it.surcharge,SUM(id.taxable_base) ");
			stmt.append("  ,SUM( IF(it.quota != 0,it.quota,ROUND(id.taxable_base * it.percentage / 100, 2) ) ) IVA");
			stmt.append("  ,SUM( IF(it.surcharge_quota != 0,it.surcharge_quota,ROUND(id.taxable_base * it.surcharge / 100, 2) ) ) RE,");
			stmt.append(operation + " vatType ");
			stmt.append("  FROM invoice_tax it ");
			stmt.append("  INNER JOIN invoice_detail id ON (it.invoice_detail = id.id) ");
			stmt.append("  INNER JOIN invoice i ON (id.invoice = i.id) ");
			stmt.append("  WHERE it.tax_type = 1 ");
			if (params.getFromDate() != null) {
				stmt.append(" AND i.tax_date >= ?");
			}
			if (params.getToDate() != null) {
				stmt.append(" AND i.tax_date <= ?");
			}
			if (params.getFromInvoiceDate() != null) {
				stmt.append(" AND i.issue_date >= ?");
			}
			if (params.getToInvoiceDate() != null) {
				stmt.append(" AND i.issue_date <= ?");
			}
			if (params.getFromSeries() != null) {
				stmt.append(" AND i.series >= ?");
			}
			if (params.getToSeries() != null) {
				stmt.append(" AND i.series <= ?");
			}
			if (params.getFromNumber() != null) {
				stmt.append(" AND i.number >= ?");
			}
			if (params.getToNumber() != null) {
				stmt.append(" AND i.number <= ?");
			}
			if (params.getVatPercent() != null) {
				if (params.getVatPercent() != -1) {
					stmt.append(" AND it.percentage = ?");
				} else {
					stmt.append(" AND it.percentage != 16");
					stmt.append(" AND it.percentage != 7");
					stmt.append(" AND it.percentage != 4");
					stmt.append(" AND it.percentage != 0");
				}
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
				} else if (params.getVatReportType() == VatReportType.INTRACOMMUNITY) {
					stmt.append(" AND i.transaction = "+ InvoiceTransactionType.INTRACOMMUNITY.ordinal());
				}
				else if (params.getVatReportType() == VatReportType.EXTRACOMMUNITY) {
					stmt.append(" AND i.transaction = "+ InvoiceTransactionType.EXTRACOMMUNITY.ordinal());
				}				
			}
			if (params.getSecurityLevel() != null) {
				stmt.append(" AND i.security_level = " + params.getSecurityLevel().ordinal());
			}
			stmt.append(" GROUP BY i.type,i.transaction,i.tax_date,i.issue_date,i.reference_code,i.rdocument,i.rname ");
			stmt.append("  ,it.percentage,it.surcharge,");
			stmt.append(operation);
			if (order == null) {
				stmt.append(" ORDER BY vatType,i.transaction,i.tax_date,i.reference_code");
			} else if (order == VatReportOrder.INVOICE_DATE) {
				stmt.append(" ORDER BY i.issue_date,i.reference_code");
			} else if (order == VatReportOrder.TAX_DATE) {
				stmt.append(" ORDER BY i.tax_date,i.reference_code");
			} else if (order == VatReportOrder.INVOICE_REFERENCE) {
				stmt.append(" ORDER BY i.reference_code");
			} else if (order == VatReportOrder.INVOICE_ORDER_NUMBER) {
				stmt.append(" ORDER BY vatType,i.series,i.number");
			} else if (order == VatReportOrder.INVOICE_REGISTRY_DOCUMENT) {
				stmt.append(" ORDER BY i.rdocument,i.reference_code");
			} else if (order == VatReportOrder.INVOICE_REGISTRY_NAME) {
				stmt.append(" ORDER BY i.rname,i.reference_code");
			}
			String sessionName = HibernateUtil.getSessionFactoryName();
			ps = HibernateUtil.getSQLConnection(sessionName).prepareStatement(stmt.toString(),
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			int i = 0;
			if (params.getFromDate() != null) {
				ps.setDate(++i, new java.sql.Date( params.getFromDate().getTime()));
			}
			if (params.getToDate() != null) {
				ps.setDate(++i, new java.sql.Date( params.getToDate().getTime()));
			}
			if (params.getFromInvoiceDate() != null) {
				ps.setDate(++i, new java.sql.Date( params.getFromInvoiceDate().getTime()));
			}
			if (params.getToInvoiceDate() != null) {
				ps.setDate(++i, new java.sql.Date( params.getToInvoiceDate().getTime()));
			}
			if (params.getFromSeries() != null) {
				ps.setString(++i, params.getFromSeries());
			}
			if (params.getToSeries() != null) {
				ps.setString(++i, params.getToSeries());
			}
			if (params.getFromNumber() != null) {
				ps.setInt(++i, params.getFromNumber());
			}
			if (params.getToNumber() != null) {
				ps.setInt(++i, params.getToNumber());
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
				vat.setInvoiceDate(rs.getDate(4));
				vat.setReference(rs.getString(5));
				vat.setSeries(rs.getString(6));
				vat.setNumber(rs.getInt(7));
				vat.setDocument(rs.getString(8));
				vat.setName(rs.getString(9));
				vat.setPercent(rs.getDouble(10));
				vat.setSurcharge(rs.getDouble(11));
				vat.setBase(rs.getDouble(12));
				vat.setVatQuota(rs.getDouble(13));
				vat.setSurchargeQuota(rs.getDouble(14));
				VatType vatType = VatType.values()[(rs.getInt(15) - 1)];
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
