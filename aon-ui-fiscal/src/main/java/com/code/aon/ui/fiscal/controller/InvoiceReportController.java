package com.code.aon.ui.fiscal.controller;

import static com.code.aon.ui.common.ICommonMessages.DATE;
import static com.code.aon.ui.common.ICommonMessages.FEE;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_RECTIFIER_INVOICE;
import static com.code.aon.ui.common.ICommonMessages.HOLDER;
import static com.code.aon.ui.common.ICommonMessages.INVOICE_DEDUCTIBLE_QUOTA;
import static com.code.aon.ui.common.ICommonMessages.INVOICE_DED_TYPE;
import static com.code.aon.ui.common.ICommonMessages.INVOICE_DOCUMENT;
import static com.code.aon.ui.common.ICommonMessages.INVOICE_DOCUMENT_NUMBER;
import static com.code.aon.ui.common.ICommonMessages.INVOICE_NATURE;
import static com.code.aon.ui.common.ICommonMessages.INVOICE_NUMBER;
import static com.code.aon.ui.common.ICommonMessages.INVOICE_PERCENTAGE;
import static com.code.aon.ui.common.ICommonMessages.INVOICE_RECTIFIED_ID;
import static com.code.aon.ui.common.ICommonMessages.INVOICE_RET_TYPE;
import static com.code.aon.ui.common.ICommonMessages.INVOICE_TAXABLE_BASE;
import static com.code.aon.ui.common.ICommonMessages.INVOICE_TAXDATE;
import static com.code.aon.ui.common.ICommonMessages.INVOICE_TAX_TYPE;
import static com.code.aon.ui.common.ICommonMessages.INVOICE_TRANSACTION;
import static com.code.aon.ui.common.ICommonMessages.INVOICE_TYPE;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.config.enumeration.VatDeductionType;
import com.code.aon.config.enumeration.WithholdingType;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.enumeration.RectificationType;
import com.code.aon.fiscal.invoice.InvoiceReport;
import com.code.aon.fiscal.invoice.InvoiceReportManager;
import com.code.aon.fiscal.invoice.InvoiceReportParams;
import com.code.aon.fiscal.invoice.InvoiceReportParamsDetail;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.report.ReportException;
import com.code.aon.report.dynamic.DynaElements;
import com.code.aon.report.dynamic.DynaReport;
import com.code.aon.ui.report.controller.DynaReportManager;
import com.code.aon.ui.util.AonUtil;

public class InvoiceReportController implements Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private InvoiceReportParams params;
	
	public InvoiceReportParams getParams() {
		if (params == null) {
			params = new InvoiceReportParams( DomainManager.getCurrentDomain() );
		}
		return params;
	}
	public void setParams(InvoiceReportParams params) {
		this.params = params;
	}
	
	public void onReset(ActionEvent event) {
		getParams().reset();
		getParams().setSecurityLevel(
				AonUtil.getRoleManager().isConfidentiality()
					?null
					:SecurityLevel.OFFICIAL);
	}
	
	public List<InvoiceReport> getInvoices() throws ManagerBeanException  {
		try {
			return getInvoiceList(getParams());
		} catch (ManagerBeanException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public List<InvoiceReport> getInvoiceList(InvoiceReportParams params) throws ManagerBeanException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
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
			stmt.append("  WHERE ");
			stmt.append( DomainManager.getSQLWhereClause("it.domain"));
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
			if (params.getTaxType() != null) {
				stmt.append(" AND it.tax_type = " + params.getTaxType().ordinal());
			}
			if (params.getDetails().size() > 0) {
				stmt.append(" AND (");
				int detailNum = 0;
				for (InvoiceReportParamsDetail detail : params.getDetails()) {
					if (detailNum > 0) {
						stmt.append(" OR ");	
					}
					stmt.append(" (1=1 ");
					if (detail.getTransaction() != null) {
						stmt.append(" AND i.transaction = " + detail.getTransaction().ordinal());	
					}
					if (detail.getInvestment() != null) {
						stmt.append(" AND i.investment = " + (detail.getInvestment().booleanValue()?"1":"0"));
					}
					if (detail.getType() != null) {
						stmt.append(" AND i.type = " + detail.getType().ordinal());
					}
					if (detail.getSurcharge() != null) {
						stmt.append(" AND it.surcharge = " + (detail.getSurcharge().booleanValue()?"1":"0"));
					}
					if (detail.getService() != null) {
						stmt.append(" AND i.service = " + (detail.getService().booleanValue()?"1":"0"));
					}
					if (detail.getVatDeductionTypeWithoutRight() != null) {
						stmt.append(" AND it.vat_deduction_type " + (detail.getVatDeductionTypeWithoutRight().booleanValue()?"= 1":"!= 1"));
					}
					if (detail.getPercent() != null) {
						stmt.append(" AND it.percentage = " + detail.getPercent());
					}
					if (detail.getRectificationTypeSpecial() != null) {
						stmt.append(" AND i.rectification_type " + (detail.getRectificationTypeSpecial().booleanValue()?"= 2":"!= 2"));
					}
					stmt.append(" )");
					detailNum++;
				}
				stmt.append(" )");
			}
			
			stmt.append(" GROUP BY i.id,i.type,i.transaction,i.investment,i.tax_date,i.issue_date,i.reference_code,i.rdocument,i.rname ");
			stmt.append(" ,i.service,i.rectification_type,i.rectification_invoice,it.tax_type,it.percentage,it.surcharge");
			stmt.append(" ,it.vat_deduction_type,it.withholding_type,it.deductible_quota ");
			ps = conn.prepareStatement(stmt.toString(),ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			
			System.out.println( "***********************" );
			System.out.println(  stmt.toString()); 
			System.out.println( "***********************" );
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
		} catch (AonConnectionException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
	}
	
	public String onExcelReport() {
		Connection conn = null; 
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			Locale locale = AonUtil.getCurrentLocale();
			InvoiceReportManager manager = new InvoiceReportManager();

			FacesContext faces = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
			String fileName = "Listado Detallado";
			response.setContentType(MimeType.MIME_MS_EXCEL_2007.getName());
			response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".xls\";");
			ServletOutputStream output = response.getOutputStream();
			
			manager.excelReport(conn, getParams(), locale, output);
			
			response.flushBuffer();
			faces.responseComplete();
			return null;
		} catch (ReportException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		} catch (IOException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		} catch (AonConnectionException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		} finally {
			DatabaseUtil.closeQuietly(conn);
		}
		
	}
	
	public String onExcelReport1() {
		try {
			Locale locale = AonUtil.getCurrentLocale();
			DynaElements dyn = new DynaElements();
			DynaReport report = new DynaReport();
			report.getReport().setReportLocale(locale);
			report
				.addColumn(dyn.getIntegerColumn("id","ID"))
				.addColumn(dyn.getStringColumn("abbreviatedInvoiceType",AonUtil.getMessage(INVOICE_TYPE),27))
				.addColumn(dyn.getStringColumn("invoiceTypeDesc",AonUtil.getMessage(INVOICE_TYPE),47))
				.addColumn(dyn.getStringColumn("transactionDesc",AonUtil.getMessage(INVOICE_TRANSACTION),60)) 
				.addColumn(dyn.getStringColumn("conceptNature",AonUtil.getMessage(INVOICE_NATURE),64))
				.addColumn(dyn.getStringColumn("rectificationTypeDesc",AonUtil.getMessage(FINANCE_RECTIFIER_INVOICE),64))
				.addColumn(dyn.getIntegerColumn("rectifiedInvoice",AonUtil.getMessage(INVOICE_RECTIFIED_ID)))
				.addColumn(dyn.getDateColumn("issueDate",AonUtil.getMessage(DATE)))
				.addColumn(dyn.getDateColumn("taxDate",AonUtil.getMessage(INVOICE_TAXDATE)))
				.addColumn(dyn.getStringColumn("referenceCode",AonUtil.getMessage(INVOICE_NUMBER),100))
				.addColumn(dyn.getStringColumn("documentNumber",AonUtil.getMessage(INVOICE_DOCUMENT_NUMBER),100))
				.addColumn(dyn.getStringColumn("registryDocument",AonUtil.getMessage(INVOICE_DOCUMENT),64))
				.addColumn(dyn.getStringColumn("registryName",AonUtil.getMessage(HOLDER),308))
				.addColumn(dyn.getStringColumn("taxTypeDesc",AonUtil.getMessage(INVOICE_TAX_TYPE),47))
				.addColumn(dyn.getStringColumn("vatDeductionTypeDesc",AonUtil.getMessage(INVOICE_DED_TYPE),114))
				.addColumn(dyn.getStringColumn("withholdingTypeDesc",AonUtil.getMessage(INVOICE_RET_TYPE),66))
				.addColumn(dyn.getNumberColumn("taxableBase",AonUtil.getMessage(INVOICE_TAXABLE_BASE)))
				.addColumn(dyn.getNumberColumn("percentage",AonUtil.getMessage(INVOICE_PERCENTAGE)))
				.addColumn(dyn.getNumberColumn("quota",AonUtil.getMessage(FEE)))
				.addColumn(dyn.getNumberColumn("deductibleQuota",AonUtil.getMessage(INVOICE_DEDUCTIBLE_QUOTA)))
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
