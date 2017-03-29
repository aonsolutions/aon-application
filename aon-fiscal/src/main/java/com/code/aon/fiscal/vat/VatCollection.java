package com.code.aon.fiscal.vat;


import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.fiscal.enumeration.InvoiceReportOrder;
import com.code.aon.fiscal.enumeration.VatReportType;
import com.code.aon.fiscal.enumeration.VatType;
import com.code.aon.pool.AonConnectionException;

public class VatCollection {

	private static String TYPE = "type";
	private static String YEAR = "year";
	private static String QUARTER= "quarter";
	private static String MONTH = "month";
	
	private static String PERCENTAGE = "percentage";
	private static String SURCHARGE_PERCENT = "surcharge_percent";
	private static String TRANSACTION = "transaction";
	private static String INVESTMENT = "investment";
	private static String INVOICE_TOTAL = "invoice_total";
	private static String INVOICE_BASE = "invoice_base";
	private static String INVOICE_VAT = "invoice_vat";
	private static String INVOICE_RETENTION = "invoice_retention";

	
	private static final String TAXABLE_BASE = "taxable_base";
	private static final String QUOTA = "quota";
	private static final String SURCHARGE_QUOTA = "surcharge";
	private static final String FINANCE_AMOUNT = "finance_amount";
	private static final String TAX_DATE = "tax_date";
	private static final String ISSUE_DATE = "issue_date";
	private static final String REFERENCE_CODE = "reference_code";
	private static final String SERIES = "series";
	private static final String NUMBER = "number";
	private static final String RDOCUMENT = "rdocument";
	private static final String RNAME = "rname";
	private static final String ID = "id";
	private static final String VAT_TYPE = "vat_type";

	public List<Vat> getVatList(VatCollectionParameters params) throws ManagerBeanException {
		List<Vat> vats = new LinkedList<Vat>();
		getVatINNERList(vats,params,false);
		getVatINNERList(vats,params,true);
		return vats;
	}
	
	private List<Vat> getVatINNERList(List<Vat> vats, VatCollectionParameters params,
			boolean vatAccrualPayment) throws ManagerBeanException {
		Connection c = null; 
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String select = vatAccrualPayment?getVatAccrualSelect(params):getSelect( params );
			c = DatabaseUtil.getConnection( params.getDomain() );
			ps = c.prepareStatement(select,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			int i = 0;
			ps.setInt(++i, params.getDomainId());
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
			while (rs.next()) {
				Vat vat = new Vat();
				InvoiceType type = InvoiceType.values()[rs.getInt(TYPE)];
				vat.setInvoiceType( type );
				vat.setYear(rs.getInt(YEAR));
				vat.setQuarter(rs.getInt(QUARTER));
				vat.setMonth(rs.getInt(MONTH));
				double percent = rs.getDouble(PERCENTAGE);
				vat.setPercent(percent);
				vat.setSurcharge(rs.getDouble(SURCHARGE_PERCENT));
				InvoiceTransactionType transaction = InvoiceTransactionType.values()[rs.getInt(TRANSACTION)];
				vat.setTransactionType(transaction);
				vat.setInvestment(rs.getBoolean(INVESTMENT));
				vat.setSurchargeQuota(rs.getDouble(SURCHARGE_QUOTA));
				double taxableBase = rs.getDouble(TAXABLE_BASE);
				double quota = rs.getDouble(QUOTA);
				if (vatAccrualPayment ){
					double invoiceBase = rs.getDouble(INVOICE_BASE);
					double invoiceVat  = rs.getDouble(INVOICE_VAT);
					double invoiceRetention = rs.getDouble(INVOICE_RETENTION);
					double invoiceTotal = rs.getDouble(INVOICE_TOTAL);
					double financeAmount = rs.getDouble(FINANCE_AMOUNT);
					invoiceTotal = CommonUtil.round(invoiceBase + invoiceVat - invoiceRetention,4);
					taxableBase = CommonUtil.round(financeAmount * taxableBase / invoiceTotal,4);
					quota = CommonUtil.round(taxableBase * percent / 100);
				}
				vat.setBase(CommonUtil.round(taxableBase));
				vat.setVatQuota(quota);
				vats.add(vat);
			}
			return vats;
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

	private String getSelect(VatCollectionParameters params) {
		StringWriter stmt = new StringWriter();
		stmt.append("SELECT " );
		stmt.append(" i.type " + TYPE );
		stmt.append(" ,YEAR(i.tax_date) " + YEAR );
		stmt.append(" ,QUARTER(i.tax_date) " + QUARTER );
		stmt.append(" ,MONTH(i.tax_date) " + MONTH );
		stmt.append(" ,it.percentage " + PERCENTAGE );
		stmt.append(" ,it.surcharge " + SURCHARGE_PERCENT );
		stmt.append(" ,i.transaction " + TRANSACTION);
		stmt.append(" ,i.investment " + INVESTMENT);
		stmt.append(" ,SUM( it.base ) " + TAXABLE_BASE);
		stmt.append(" ,SUM( IF(it.quota != 0,it.quota,ROUND(it.base * it.percentage / 100, 2) ) ) " + QUOTA);
		stmt.append(" ,SUM( IF(it.surcharge_quota != 0,it.surcharge_quota,ROUND(it.base * it.surcharge / 100, 2) ) )" + SURCHARGE_QUOTA);
		stmt.append(" FROM invoice_tax it ");
		stmt.append(" INNER JOIN invoice_detail id ON (it.invoice_detail = id.id)"); 
		stmt.append(" INNER JOIN invoice i ON (id.invoice = i.id)"); 
		stmt.append(" WHERE it.domain = ?");
		stmt.append("  AND it.tax_type = 1");
		stmt.append("  AND i.vat_accrual_payment = 0");	// NO CRITERIO DE CAJA
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
		stmt.append(" GROUP BY ");
		stmt.append( TYPE );
		stmt.append("," + YEAR);
		stmt.append("," + QUARTER );
		stmt.append("," + MONTH );
		stmt.append("," + TRANSACTION);
		stmt.append("," + INVESTMENT);
		stmt.append("," + PERCENTAGE);
		stmt.append("," + SURCHARGE_PERCENT);
		stmt.append(" ORDER BY ");
		stmt.append( TYPE + " DESC");
		stmt.append("," + YEAR);
		stmt.append("," + QUARTER );
		stmt.append("," + MONTH );
		stmt.append("," + TRANSACTION);
		stmt.append("," + INVESTMENT);
		stmt.append("," + PERCENTAGE);
		stmt.append("," + SURCHARGE_PERCENT);
		return stmt.toString();
	}

	private String getVatAccrualSelect(VatCollectionParameters params) {
		StringWriter stmt = new StringWriter();
		stmt.append("SELECT it.id" );
		stmt.append(" ,i.type " + TYPE );
		stmt.append(" ,YEAR(ft.tracking_date) " + YEAR );
		stmt.append(" ,QUARTER(ft.tracking_date) " + QUARTER );
		stmt.append(" ,MONTH(ft.tracking_date) " + MONTH );
		stmt.append(" ,it.percentage " + PERCENTAGE );
		stmt.append(" ,it.surcharge " + SURCHARGE_PERCENT );
		stmt.append(" ,i.transaction " + TRANSACTION);
		stmt.append(" ,i.investment " + INVESTMENT);
		stmt.append(" ,i.taxable_base " + INVOICE_BASE);
		stmt.append(" ,i.vat_quota " + INVOICE_VAT);
		stmt.append(" ,i.retention_quota " + INVOICE_RETENTION);
		stmt.append(" ,i.total " + INVOICE_TOTAL);
		stmt.append(" ,it.base " + TAXABLE_BASE);
		stmt.append(" ,IF(it.quota != 0,it.quota,ROUND(it.base * it.percentage / 100, 2) ) " + QUOTA);
		stmt.append(" ,IF(it.surcharge_quota != 0,it.surcharge_quota,ROUND(it.base * it.surcharge / 100, 2) ) " + SURCHARGE_QUOTA);
		stmt.append(" ,SUM( IF(ft.type=1, ft.amount,  -ft.amount )) " + FINANCE_AMOUNT);
		stmt.append("  FROM finance_tracking ft ");
		stmt.append("  INNER JOIN finance f ON (ft.finance = f.id) ");
		stmt.append("  INNER JOIN invoice i ON (f.invoice = i.id AND vat_accrual_payment = 1) ");
		stmt.append("  INNER JOIN invoice_detail id ON (id.invoice = i.id) ");
		stmt.append("  INNER JOIN invoice_tax it ON (it.invoice_detail = id.id) ");
		stmt.append(" WHERE ft.domain = ?");
		stmt.append("  AND ft.type IN (1,2) ");
		stmt.append("  AND it.tax_type = 1");
		stmt.append("  AND i.tax_date >= '2014-01-01'");
		stmt.append("  AND i.vat_accrual_payment = 1");	// Criterio de Caja.
		if (params.getFromDate() != null) {
			stmt.append(" AND ft.tracking_date >= ?");
		}
		if (params.getToDate() != null) {
			stmt.append(" AND ft.tracking_date <= ?");
		}
		if (params.getFromInvoiceDate() != null) {
			stmt.append(" AND ft.tracking_date >= ?");
		}
		if (params.getToInvoiceDate() != null) {
			stmt.append(" AND ft.tracking_date <= ?");
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
		stmt.append(" GROUP BY it.id");
		stmt.append(" ORDER BY ");
		stmt.append( TYPE + " DESC");
		stmt.append("," + YEAR);
		stmt.append("," + QUARTER );
		stmt.append("," + MONTH );
		stmt.append("," + TRANSACTION);
		stmt.append("," + INVESTMENT);
		stmt.append("," + PERCENTAGE);
		stmt.append("," + SURCHARGE_PERCENT);
		return stmt.toString();
	}

	public List<Vat> getVatDetailList(VatCollectionParameters params, InvoiceReportOrder order) throws ManagerBeanException {
		List<Vat> vats = new LinkedList<Vat>();
		getINNERVatDetailList(vats,params,order,false);
		getINNERVatDetailList(vats,params,order,true);
		return vats;
	}
	
	private List<Vat> getINNERVatDetailList(List<Vat> vats,
			VatCollectionParameters params, InvoiceReportOrder order,
			boolean vatAccrualPayment) throws ManagerBeanException {
		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String select = vatAccrualPayment?getDetailVatAccrualSelect(params,order):getDetailSelect(params,order);
			c = DatabaseUtil.getConnection(params.getDomain());
			ps = c.prepareStatement(select,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			int i = 0;
			ps.setInt(++i, params.getDomainId());
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
			while (rs.next()) {
				Vat vat = new Vat();
				vat.setInvoiceId(rs.getInt(ID));
				InvoiceType type = InvoiceType.values()[rs.getInt(TYPE)];
				vat.setInvoiceType( type );
				InvoiceTransactionType transaction = InvoiceTransactionType.values()[rs.getInt(TRANSACTION)];
				vat.setTransactionType(transaction);
				vat.setInvestment( rs.getBoolean(INVESTMENT) );
				vat.setDate(rs.getDate(TAX_DATE));
				vat.setInvoiceDate(rs.getDate(ISSUE_DATE));
				vat.setReference(rs.getString(REFERENCE_CODE));
				vat.setSeries(rs.getString(SERIES));
				vat.setNumber(rs.getInt(NUMBER));
				vat.setDocument(rs.getString(RDOCUMENT));
				vat.setName(rs.getString(RNAME));
				double percent = rs.getDouble(PERCENTAGE);
				vat.setPercent(percent);
				vat.setSurcharge(rs.getDouble(SURCHARGE_PERCENT));
				vat.setSurchargeQuota(rs.getDouble(SURCHARGE_QUOTA));
				VatType vatType = VatType.values()[(rs.getInt(VAT_TYPE) - 1)];
				vat.setVatType(vatType);
				double taxableBase = rs.getDouble(TAXABLE_BASE);
				double quota = rs.getDouble(QUOTA);
				if (vatAccrualPayment ){
					double invoiceBase = rs.getDouble(INVOICE_BASE);
					double invoiceVat  = rs.getDouble(INVOICE_VAT);
					double invoiceRetention = rs.getDouble(INVOICE_RETENTION);
					double invoiceTotal = rs.getDouble(INVOICE_TOTAL);
					double financeAmount = rs.getDouble(FINANCE_AMOUNT);
					invoiceTotal = CommonUtil.round(invoiceBase + invoiceVat - invoiceRetention);
					taxableBase = CommonUtil.round(financeAmount * taxableBase / invoiceTotal,4);
					quota = CommonUtil.round(taxableBase * percent / 100);
				}
				vat.setBase(taxableBase);
				vat.setVatQuota(quota);
				vat.setVatAccrualPayment(vatAccrualPayment);
				vats.add(vat);
			}
			return vats;
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

	private String getDetailVatAccrualSelect(VatCollectionParameters params, InvoiceReportOrder order) {
		// Mediante la operacion siguiente se determinada cual de los tipos de factura
		// es de ventas, compras o inversión y se asocia al tipo de IVA correspondiente.
//		String operation = "CEIL((i.investment+ELT((i.type+1),20,10,20,20)) / 10) ";
		String operation = "IF(i.investment=1,3,ELT((i.type+1),2,1,2,2))";
		// ---------------------------------------------------------------------------
		StringWriter stmt = new StringWriter();
		stmt.append(" SELECT ");
		stmt.append(" i.id " + ID);
		stmt.append(" ,i.type " + TYPE);
		stmt.append(" ,i.transaction " + TRANSACTION);
		stmt.append(" ,i.investment " + INVESTMENT);
		stmt.append(" ,ft.tracking_date " + TAX_DATE);
		stmt.append(" ,i.issue_date " + ISSUE_DATE);
		stmt.append(" ,i.reference_code " + REFERENCE_CODE);
		stmt.append(" ,i.series " + SERIES);
		stmt.append(" ,i.number " + NUMBER);
		stmt.append(" ,i.rdocument " + RDOCUMENT);
		stmt.append(" ,i.rname " + RNAME);
		stmt.append(" ,it.percentage " + PERCENTAGE);
		stmt.append(" ,it.surcharge " + SURCHARGE_PERCENT);
		stmt.append(" ,i.taxable_base " + INVOICE_BASE);
		stmt.append(" ,i.vat_quota " + INVOICE_VAT);
		stmt.append(" ,i.retention_quota " + INVOICE_RETENTION);
		stmt.append(" ,i.total " + INVOICE_TOTAL);
		stmt.append(" ,SUM(it.base) / count(DISTINCT ft.id)" + TAXABLE_BASE);
		stmt.append(" ,IF(it.quota != 0,it.quota,ROUND(it.base * it.percentage / 100, 2) ) " + QUOTA);
		stmt.append(" ,IF(it.surcharge_quota != 0,it.surcharge_quota,ROUND(it.base * it.surcharge / 100, 2) ) " + SURCHARGE_QUOTA);
		stmt.append(" ," + operation + VAT_TYPE );
		stmt.append(" ,SUM( IF(ft.type=1, ft.amount,  -ft.amount )) / count(DISTINCT id.id)" + FINANCE_AMOUNT);
		stmt.append("  FROM finance_tracking ft ");
		stmt.append("  INNER JOIN finance f ON (ft.finance = f.id) ");
		stmt.append("  INNER JOIN invoice i ON (f.invoice = i.id AND vat_accrual_payment = 1) ");
		stmt.append("  INNER JOIN invoice_detail id ON (id.invoice = i.id) ");
		stmt.append("  INNER JOIN invoice_tax it ON (it.invoice_detail = id.id) ");
		stmt.append(" WHERE ft.domain = ?");
		stmt.append("  AND ft.type IN (1,2) ");
		stmt.append("  AND i.tax_date >= '2014-01-01'");
		stmt.append("  AND i.vat_accrual_payment = 1");	// Criterio de Caja.
		stmt.append("  AND it.tax_type = 1 ");
		if (params.getFromDate() != null) {
			stmt.append(" AND ft.tracking_date >= ?");
		}
		if (params.getToDate() != null) {
			stmt.append(" AND ft.tracking_date <= ?");
		}
		if (params.getFromInvoiceDate() != null) {
			stmt.append(" AND ft.tracking_date >= ?");
		}
		if (params.getToInvoiceDate() != null) {
			stmt.append(" AND ft.tracking_date <= ?");
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
			} else if (params.getVatReportType() == VatReportType.INTRACOMMUNITY) {
				stmt.append(" AND i.transaction = "+ InvoiceTransactionType.INTRACOMMUNITY.ordinal());
			} else if (params.getVatReportType() == VatReportType.EXTRACOMMUNITY) {
				stmt.append(" AND i.transaction = "+ InvoiceTransactionType.EXTRACOMMUNITY.ordinal());
			} else if (params.getVatReportType() == VatReportType.CAN_CEU_MEL) {
				stmt.append(" AND i.transaction = "+ InvoiceTransactionType.CAN_CEU_MEL.ordinal());
			} else if (params.getVatReportType() == VatReportType.OTHER_ISP) {
				stmt.append(" AND i.transaction = "+ InvoiceTransactionType.OTHER_ISP.ordinal());
			}				
		}
		if (params.getSecurityLevel() != null) {
			stmt.append(" AND i.security_level = " + params.getSecurityLevel().ordinal());
		}
		stmt.append(" GROUP BY ");
		stmt.append(ID);
//		stmt.append(TYPE);
//		stmt.append("," + TRANSACTION);
//		stmt.append("," + INVESTMENT);
//		stmt.append("," + TAX_DATE);
//		stmt.append("," + ISSUE_DATE);
//		stmt.append("," + REFERENCE_CODE);
//		stmt.append("," + RDOCUMENT);
//		stmt.append("," + RNAME );
		stmt.append("," + PERCENTAGE );
		stmt.append("," + SURCHARGE_PERCENT);
		stmt.append("," + VAT_TYPE);
		if (order == null) {
			stmt.append(" ORDER BY vat_type,i.transaction,i.tax_date,i.reference_code");
		} else if (order == InvoiceReportOrder.INVOICE_DATE) {
			stmt.append(" ORDER BY i.issue_date,i.series,i.number");
		} else if (order == InvoiceReportOrder.TAX_DATE) {
			stmt.append(" ORDER BY i.tax_date,i.series,i.number");
		} else if (order == InvoiceReportOrder.INVOICE_REFERENCE) {
			stmt.append(" ORDER BY i.reference_code");
		} else if (order == InvoiceReportOrder.INVOICE_ORDER_NUMBER) {
			stmt.append(" ORDER BY vat_type,i.series,i.number");
		} else if (order == InvoiceReportOrder.INVOICE_REGISTRY_DOCUMENT) {
			stmt.append(" ORDER BY i.rdocument,i.series,i.number");
		} else if (order == InvoiceReportOrder.INVOICE_REGISTRY_NAME) {
			stmt.append(" ORDER BY i.rname,i.series,i.number");
		}
		return stmt.toString();
	}

	private String getDetailSelect(VatCollectionParameters params, InvoiceReportOrder order) {
		// Mediante la operacion siguiente se determinada cual de los tipos de factura
		// es de ventas, compras o inversión y se asocia al tipo de IVA correspondiente.
//		String operation = "CEIL((i.investment+ELT((i.type+1),20,10,20,20)) / 10) ";
		String operation = "IF(i.investment=1,3,ELT((i.type+1),2,1,2,2))";
		// ---------------------------------------------------------------------------
		StringWriter stmt = new StringWriter();
		stmt.append(" SELECT ");
		stmt.append(" i.id " + ID);
		stmt.append(" ,i.type " + TYPE);
		stmt.append(" ,i.transaction " + TRANSACTION);
		stmt.append(" ,i.investment " + INVESTMENT);
		stmt.append(" ,i.tax_date " + TAX_DATE);
		stmt.append(" ,i.issue_date " + ISSUE_DATE);
		stmt.append(" ,i.reference_code " + REFERENCE_CODE);
		stmt.append(" ,i.series " + SERIES);
		stmt.append(" ,i.number " + NUMBER);
		stmt.append(" ,i.rdocument " + RDOCUMENT);
		stmt.append(" ,i.rname " + RNAME);
		stmt.append(" ,it.percentage " + PERCENTAGE);
		stmt.append(" ,it.surcharge " + SURCHARGE_PERCENT);
		stmt.append(" ,SUM(it.base) " + TAXABLE_BASE);
		stmt.append(" ,SUM( IF(it.quota != 0,it.quota,ROUND(it.base * it.percentage / 100, 2) ) ) " + QUOTA);
		stmt.append(" ,SUM( IF(it.surcharge_quota != 0,it.surcharge_quota,ROUND(it.base * it.surcharge / 100, 2) ) ) " + SURCHARGE_QUOTA);
		stmt.append(" ," + operation + VAT_TYPE );
		stmt.append("  FROM invoice_tax it ");
		stmt.append("  INNER JOIN invoice_detail id ON (it.invoice_detail = id.id) ");
		stmt.append("  INNER JOIN invoice i ON (id.invoice = i.id) ");
		stmt.append("  WHERE it.domain = ?");
		stmt.append("  AND i.vat_accrual_payment = 0");
		stmt.append("  AND it.tax_type = 1 ");
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
			} else if (params.getVatReportType() == VatReportType.INTRACOMMUNITY) {
				stmt.append(" AND i.transaction = "+ InvoiceTransactionType.INTRACOMMUNITY.ordinal());
			} else if (params.getVatReportType() == VatReportType.EXTRACOMMUNITY) {
				stmt.append(" AND i.transaction = "+ InvoiceTransactionType.EXTRACOMMUNITY.ordinal());
			} else if (params.getVatReportType() == VatReportType.CAN_CEU_MEL) {
				stmt.append(" AND i.transaction = "+ InvoiceTransactionType.CAN_CEU_MEL.ordinal());
			} else if (params.getVatReportType() == VatReportType.OTHER_ISP) {
				stmt.append(" AND i.transaction = "+ InvoiceTransactionType.OTHER_ISP.ordinal());
			}				
		}
		if (params.getSecurityLevel() != null) {
			stmt.append(" AND i.security_level = " + params.getSecurityLevel().ordinal());
		}
		stmt.append(" GROUP BY ");
		stmt.append(ID);
//		stmt.append(TYPE);
//		stmt.append("," + TRANSACTION);
//		stmt.append("," + INVESTMENT);
//		stmt.append("," + TAX_DATE);
//		stmt.append("," + ISSUE_DATE);
//		stmt.append("," + REFERENCE_CODE);
//		stmt.append("," + RDOCUMENT);
//		stmt.append("," + RNAME );
		stmt.append("," + PERCENTAGE );
		stmt.append("," + SURCHARGE_PERCENT);
		stmt.append("," + VAT_TYPE);
		if (order == null) {
			stmt.append(" ORDER BY vat_type,i.transaction,i.tax_date,i.reference_code");
		} else if (order == InvoiceReportOrder.INVOICE_DATE) {
			stmt.append(" ORDER BY i.issue_date,i.series,i.number");
		} else if (order == InvoiceReportOrder.TAX_DATE) {
			stmt.append(" ORDER BY i.tax_date,i.series,i.number");
		} else if (order == InvoiceReportOrder.INVOICE_REFERENCE) {
			stmt.append(" ORDER BY i.reference_code");
		} else if (order == InvoiceReportOrder.INVOICE_ORDER_NUMBER) {
			stmt.append(" ORDER BY vat_type,i.series,i.number");
		} else if (order == InvoiceReportOrder.INVOICE_REGISTRY_DOCUMENT) {
			stmt.append(" ORDER BY i.rdocument,i.series,i.number");
		} else if (order == InvoiceReportOrder.INVOICE_REGISTRY_NAME) {
			stmt.append(" ORDER BY i.rname,i.series,i.number");
		}
		return stmt.toString();
	}
	
}

		