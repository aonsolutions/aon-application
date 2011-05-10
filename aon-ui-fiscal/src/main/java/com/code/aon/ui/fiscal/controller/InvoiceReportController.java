package com.code.aon.ui.fiscal.controller;

import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.config.enumeration.VatDeductionType;
import com.code.aon.config.enumeration.WithholdingType;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.enumeration.RectificationType;
import com.code.aon.report.ReportException;
import com.code.aon.report.dynamic.DynaElements;
import com.code.aon.report.dynamic.DynaReport;
import com.code.aon.ui.report.controller.DynaReportManager;
import com.code.aon.ui.util.AonUtil;

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
			stmt.append(" SELECT i.id,i.type,i.transaction,i.investment,i.tax_date,i.issue_date,i.reference_code,i.series,i.number,i.rdocument,i.rname ");
			stmt.append("  ,i.service,i.rectification_type,i.rectification_invoice ");
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
			
			if (params.getTransaction() != null) {
				stmt.append(" AND (");
				for (int i =0; i < params.getTransaction().length;i++) {
					if (i > 0) {
						stmt.append(" OR ");
					}
					stmt.append(" i.transaction = " + params.getTransaction()[i].ordinal());	
				}
				stmt.append(" ) ");
				
			}
			if (params.getInvestment() != null) {
				stmt.append(" AND i.investment = " + (params.getInvestment().booleanValue()?"1":"0"));
			}
			if (params.getType() != null) {
				stmt.append(" AND (");
				for (int i =0; i < params.getType().length;i++) {
					if (i > 0) {
						stmt.append(" OR ");
					}
					stmt.append(" i.type = " + params.getType()[i].ordinal());	
				}
				stmt.append(" ) ");
				
			}
			if (params.getSurcharge() != null) {
				stmt.append(" AND it.surcharge = " + (params.getSurcharge().booleanValue()?"1":"0"));
			}
			if (params.getService() != null) {
				stmt.append(" AND i.service = " + (params.getService().booleanValue()?"1":"0"));
			}
			if (params.getTaxType() != null) {
				stmt.append(" AND it.tax_type = " + params.getTaxType().ordinal());
			}
			if (params.getVatDeductionTypeWithoutRight() != null) {
				stmt.append(" AND it.vat_deduction_type " + (params.getVatDeductionTypeWithoutRight().booleanValue()?"= 1":"!= 1"));
			}
			if (params.getPercent() != null) {
				stmt.append(" AND it.percentage = " + params.getPercent());
			}
			if (params.getRectificationTypeSpecial() != null) {
				stmt.append(" AND i.rectification_type " + (params.getRectificationTypeSpecial().booleanValue()?"= 2":"!= 2"));
			}
			stmt.append(" GROUP BY i.id,i.type,i.transaction,i.investment,i.tax_date,i.issue_date,i.reference_code,i.rdocument,i.rname ");
			stmt.append(" ,i.service,i.rectification_type,i.rectification_invoice,it.tax_type,it.percentage,it.surcharge");
			stmt.append(" ,it.vat_deduction_type,it.withholding_type,it.deductible_quota ");
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
				inv.setId(rs.getInt(1));
				InvoiceType type = InvoiceType.values()[rs.getInt(2)];
				inv.setInvoiceType( type );
				InvoiceTransactionType transaction = InvoiceTransactionType.values()[rs.getInt(3)];
				inv.setTransaction(transaction);
				inv.setInvestment( rs.getBoolean(4) );
				inv.setTaxDate(rs.getDate(5));
				inv.setIssueDate(rs.getDate(6));
				inv.setReferenceCode(rs.getString(7));
				inv.setSeries(rs.getString(8));
				inv.setNumber(rs.getInt(9));
				inv.setRegistryDocument(rs.getString(10));
				inv.setRegistryName(rs.getString(11));
				inv.setService(rs.getInt(12)==1);
				inv.setRectificationType(RectificationType.values()[rs.getInt(13)]);
				inv.setRectifiedInvoice(rs.getInt(14));
				TaxType taxType = TaxType.values()[rs.getInt(15)];
				inv.setTaxType(taxType);
				inv.setPercentage(rs.getDouble(16));
				double surchargePercent = rs.getDouble(17);
				VatDeductionType vatDeductionType = VatDeductionType.values()[rs.getInt(18)];
				inv.setVatDeductionType(vatDeductionType);
				WithholdingType withholdingType = WithholdingType.values()[rs.getInt(19)];
				inv.setWithholdingType(withholdingType);
				inv.setDeductibleQuota(rs.getDouble(20));
				inv.setTaxableBase(rs.getDouble(21));
				inv.setQuota(rs.getDouble(22));
				inv.setSurcharge(false);
				invoices.add(inv);
				if (surchargePercent > 0) {
					double surchargeQuota = rs.getDouble(23);
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
	
	
	public String onExcelReport() {
		try {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			DynaElements dyn = new DynaElements();
			DynaReport report = new DynaReport();
			ResourceBundle bundle = ResourceBundle.getBundle("com.code.aon.ui.fiscal.i18n.report",locale);
			report.getReport().setReportLocale(locale);
			report
				.addColumn(dyn.getIntegerColumn("id","ID"))
				.addColumn(dyn.getStringColumn("abbreviatedInvoiceType",bundle.getString("invoice_type"),27))
				.addColumn(dyn.getStringColumn("invoiceTypeDesc",bundle.getString("invoice_type"),47))
				.addColumn(dyn.getStringColumn("transactionDesc",bundle.getString("invoice_transaction"),60)) 
				.addColumn(dyn.getStringColumn("conceptNature",bundle.getString("invoice_nature"),64))
				.addColumn(dyn.getStringColumn("rectificationTypeDesc",bundle.getString("invoice_rectificationType"),64))
				.addColumn(dyn.getIntegerColumn("rectifiedInvoice",bundle.getString("invoice_rectified_id")))
				.addColumn(dyn.getDateColumn("issueDate",bundle.getString("invoice_date")))
				.addColumn(dyn.getDateColumn("taxDate",bundle.getString("invoice_taxdate")))
				.addColumn(dyn.getStringColumn("referenceCode",bundle.getString("invoice_number"),100))
				.addColumn(dyn.getStringColumn("documentNumber",bundle.getString("invoice_document_number"),100))
				.addColumn(dyn.getStringColumn("registryDocument",bundle.getString("invoice_document"),64))
				.addColumn(dyn.getStringColumn("registryName",bundle.getString("invoice_company"),308))
				.addColumn(dyn.getStringColumn("taxTypeDesc",bundle.getString("invoice_tax_type"),47))
				.addColumn(dyn.getStringColumn("vatDeductionTypeDesc",bundle.getString("invoice_ded_type"),114))
				.addColumn(dyn.getStringColumn("withholdingTypeDesc",bundle.getString("invoice_ret_type"),66))
				.addColumn(dyn.getNumberColumn("taxableBase",bundle.getString("invoice_taxable_base")))
				.addColumn(dyn.getNumberColumn("percentage",bundle.getString("invoice_percentage")))
				.addColumn(dyn.getNumberColumn("quota",bundle.getString("invoice_quota")))
				.addColumn(dyn.getNumberColumn("deductibleQuota",bundle.getString("invoice_deductible_quota")))
				;
			DynaReportManager drm = new DynaReportManager();
			drm.toExcel(report,"Facturas",getInvoices() );
		} catch (ReportException e) {
			e.printStackTrace();
			String msg = "No se pudo generar el listado";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} catch (ManagerBeanException e) {
			e.printStackTrace();
			String msg = "No se pudo generar el listado";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		return null;
	}
	
	
}
