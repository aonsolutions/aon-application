package com.code.aon.ui.fiscal.controller;

import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.config.enumeration.VatDeductionType;
import com.code.aon.config.enumeration.WithholdingType;
import com.code.aon.finance.enumeration.InvoiceType;

public class InvoiceReportController {

	private InvoiceReportParams params;
	
	public InvoiceReportParams getParams() {
		if (params == null) {
			params = new InvoiceReportParams();
		}
		return params;
	}
	public void setParams(InvoiceReportParams params) {
		this.params = params;
	}
	
	public void onReset(ActionEvent event) {
		getParams().reset();
	}
	
	public List<InvoiceReport> getInvoices() throws ManagerBeanException  {
		try {
			return getInvoiceList(getParams());
		} catch (ManagerBeanException e) {
			e.printStackTrace();
			throw e;
		}
	}

	@SuppressWarnings("deprecation")
	public List<InvoiceReport> getInvoiceList(InvoiceReportParams params) throws ManagerBeanException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			// ---------------------------------------------------------------------------
			StringWriter stmt = new StringWriter();
			stmt.append(" SELECT i.type,i.transaction,i.investment,i.tax_date,i.issue_date,i.reference_code,i.series,i.number,i.rdocument,i.rname ");
			stmt.append("  ,it.tax_type,it.percentage,it.surcharge,it.vat_deduction_type,it.withholding_type,it.deductible_quota");
		    stmt.append("  ,SUM(id.taxable_base) ");
			stmt.append("  ,SUM( IF(it.quota != 0,it.quota,ROUND(id.taxable_base * it.percentage / 100, 2) ) ) TAX");
			stmt.append("  ,SUM( IF(it.surcharge_quota != 0,it.surcharge_quota,ROUND(id.taxable_base * it.surcharge / 100, 2) ) ) RE ");
			stmt.append("  FROM invoice_tax it ");
			stmt.append("  INNER JOIN invoice_detail id ON (it.invoice_detail = id.id) ");
			stmt.append("  INNER JOIN invoice i ON (id.invoice = i.id) ");
			stmt.append("  WHERE 1 = 1 ");
			if (params.getFromTaxDate() != null) {
				stmt.append(" AND i.tax_date >= ?");
			}
			if (params.getToTaxDate() != null) {
				stmt.append(" AND i.tax_date <= ?");
			}
			if (params.getFromInvoiceDate() != null) {
				stmt.append(" AND i.issue_date >= ?");
			}
			if (params.getToInvoiceDate() != null) {
				stmt.append(" AND i.issue_date <= ?");
			}
			if (params.getSecurityLevel() != null) {
				stmt.append(" AND i.security_level = " + params.getSecurityLevel().ordinal());
			}
			stmt.append(" GROUP BY i.type,i.transaction,i.investment,i.tax_date,i.issue_date,i.reference_code,i.rdocument,i.rname ");
			stmt.append("  ,it.tax_type,it.percentage,it.surcharge,it.vat_deduction_type,it.withholding_type,it.deductible_quota ");
			String sessionName = HibernateUtil.getSessionFactoryName();
			ps = HibernateUtil.getSQLConnection(sessionName).prepareStatement(stmt.toString(),
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			int i = 0;
			if (params.getFromTaxDate() != null) {
				ps.setDate(++i, new java.sql.Date( params.getFromTaxDate().getTime()));
			}
			if (params.getToTaxDate() != null) {
				ps.setDate(++i, new java.sql.Date( params.getToTaxDate().getTime()));
			}
			if (params.getFromInvoiceDate() != null) {
				ps.setDate(++i, new java.sql.Date( params.getFromInvoiceDate().getTime()));
			}
			if (params.getToInvoiceDate() != null) {
				ps.setDate(++i, new java.sql.Date( params.getToInvoiceDate().getTime()));
			}
			rs = ps.executeQuery();
			List<InvoiceReport> invoices = new LinkedList<InvoiceReport>();
			while (rs.next()) {
				InvoiceReport inv = new InvoiceReport();
				InvoiceType type = InvoiceType.values()[rs.getInt(1)];
				inv.setInvoiceType( type );
				InvoiceTransactionType transaction = InvoiceTransactionType.values()[rs.getInt(2)];
				inv.setTransaction(transaction);
				inv.setInvestment( rs.getBoolean(3) );
				inv.setTaxDate(rs.getDate(4));
				inv.setIssueDate(rs.getDate(5));
				inv.setReferenceCode(rs.getString(6));
				inv.setSeries(rs.getString(7));
				inv.setNumber(rs.getInt(8));
				inv.setRegistryDocument(rs.getString(9));
				inv.setRegistryName(rs.getString(10));
				TaxType taxType = TaxType.values()[rs.getInt(11)];
				inv.setTaxType(taxType);
				inv.setPercentage(rs.getDouble(12));
				VatDeductionType vatDeductionType = VatDeductionType.values()[rs.getInt(14)];
				inv.setVatDeductionType(vatDeductionType);
				WithholdingType withholdingType = WithholdingType.values()[rs.getInt(15)];
				inv.setWithholdingType(withholdingType);
				inv.setDeductibleQuota(rs.getDouble(16));
				inv.setTaxableBase(rs.getDouble(17));
				inv.setQuota(rs.getDouble(18));
				inv.setSurcharge(false);
				invoices.add(inv);
				double surchargePercent = rs.getDouble(13);
				if (surchargePercent > 0) {
					double surchargeQuota = rs.getDouble(19);
					InvoiceReport cloned = inv.clone();
					cloned.setSurcharge(true);
					cloned.setPercentage(surchargePercent);
					cloned.setQuota(surchargeQuota);
					cloned.setDeductibleQuota(surchargeQuota);
					invoices.add(cloned);
				}
			}
			return invoices;
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
