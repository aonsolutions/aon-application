package com.code.aon.fiscal.invoice;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.config.enumeration.VatDeductionType;
import com.code.aon.config.enumeration.WithholdingType;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.enumeration.RectificationType;
import com.code.aon.report.ReportException;
import com.code.aon.report.poi.ExcelReportExporter;
import com.code.aon.report.poi.IReportExporter;
import com.code.aon.report.poi.ReportColumnMetadata;
import com.code.aon.report.poi.ReportMetadata;

public class InvoiceReportManager {
	
	private static final String ID = "id";
	private static final String TYPE = "tipo";
	private static final String ACTIVITY = "activity";
	private static final String INVEST_ASSET = "invest_asset";
	private static final String TRANSACTION = "transaccion";
	private static final String INVESTMENT = "investment";
	private static final String TAX_DATE = "tax_date";
	private static final String ISSUE_DATE = "issue_date";
	private static final String REFERENCE_CODE = "reference_code";
	private static final String SERIES = "series";
	private static final String NUMBER = "number";
	private static final String RDOCUMENT = "rdocument";
	private static final String RNAME = "rname";
	private static final String SERVICE = "service";
	private static final String RECTIFICATION_TYPE = "rectification_type";
	private static final String RECTIFICATION_INVOICE = "rectification_invoice";
	private static final String VAT_ACCRUAL_PAYMENT = "vat_accrual_payment";
	private static final String WITHHOLDING_FARMER = "withholding_farmer";
	private static final String TAX_TYPE = "tax_type";
	private static final String PERCENTAGE = "percentage";
	private static final String SURCHARGE = "surcharge";
	private static final String VAT_DEDUCTION_TYPE = "vat_deduction_type";
	private static final String WITHHOLDING_TYPE = "withholding_type";
	private static final String DEDUCTIBLE_PERCENT = "deductible_percent";
	private static final String DEDUCTIBLE_QUOTA = "deductible_quota";
	private static final String TAXABLE_BASE = "taxable_base";
	private static final String TAX = "tax";
	private static final String SURCHARGE_QUOTA = "surcharge_quota";
	private static final String FINANCE_DATE = "finance_date";
	private static final String FINANCE_TOTAL = "finance_amount";
	private static final String FINANCE_BASE = "finance_base";
	private static final String FINANCE_PERCENTAGE = "finance_percentage";
	private static final String FINANCE_QUOTA = "finance_quota";
	private static final String INVOICE_TOTAL = "invoice_total";
	private static final String INVOICE_VAT = "invoice_vat";
	private static final String INVOICE_BASE = "invoice_base";
	private static final String INVOICE_RETENTION = "invoice_retention";
	
	
	
	private static final String OUTPUT_INVOICE = "Emit.";
	private static final String INPUT_INVOICE = "Recb.";

	private static final String CONCEPT_NATURE_INVESTMENT = "Bien Inver.";
	private static final String CONCEPT_NATURE_EXPENSE    = "Gasto";
	private static final String CONCEPT_NATURE_STANDARD   = "Bien Corriente";
	private static final String CONCEPT_NATURE_SERVICE    = "Servicio";
	
	private static final String RECT_NORMAL = "Rect. Normal";
	private static final String RECT_SPECIAL = "Rect. Especial";
	
	private static final ReportColumnMetadata[] COLUMN_LABELS = new ReportColumnMetadata[]{
		new ReportColumnMetadata(ID,Types.INTEGER,ID,10)
		,new ReportColumnMetadata(TYPE,Types.VARCHAR,"Tipo Fac.",6)
		,new ReportColumnMetadata(TYPE,Types.VARCHAR,"Tipo Fac.",8)
		,new ReportColumnMetadata(TRANSACTION,Types.VARCHAR,"Transc.",8)
		,new ReportColumnMetadata(SERVICE,Types.VARCHAR,"Naturaleza",8)
		,new ReportColumnMetadata(RECTIFICATION_TYPE,Types.VARCHAR,"Rectificativa",6)
		,new ReportColumnMetadata(RECTIFICATION_INVOICE,Types.INTEGER,"ID Fra. Rectificada",15)
		,new ReportColumnMetadata(ISSUE_DATE,Types.DATE,"Fecha",10)
		,new ReportColumnMetadata(TAX_DATE,Types.DATE,"F.IVA",10)
		,new ReportColumnMetadata(ACTIVITY,Types.VARCHAR,"Actividad",40)
		,new ReportColumnMetadata(INVEST_ASSET,Types.VARCHAR,"Bien afecto",40)
		,new ReportColumnMetadata(NUMBER,Types.VARCHAR,"Nº Factura",15)
		,new ReportColumnMetadata("document_number",Types.VARCHAR,"Nº Docum.",15)
		,new ReportColumnMetadata(RDOCUMENT,Types.VARCHAR,"NIF",15)
		,new ReportColumnMetadata(RNAME,Types.VARCHAR,"Titular",40)
		,new ReportColumnMetadata(TAX_TYPE,Types.VARCHAR,"Tipo Imp.",6)
		,new ReportColumnMetadata(VAT_DEDUCTION_TYPE,Types.VARCHAR,"Tipo Ded.",20)
		,new ReportColumnMetadata(WITHHOLDING_TYPE,Types.VARCHAR,"Tipo Ret.",15)
		,new ReportColumnMetadata(TAXABLE_BASE,Types.DOUBLE,"B.Imp.",10)
		,new ReportColumnMetadata(PERCENTAGE,Types.DOUBLE,"Porc.",5)
		,new ReportColumnMetadata(TAX,Types.DOUBLE,"Cuota",10)
		,new ReportColumnMetadata(DEDUCTIBLE_PERCENT,Types.DOUBLE,"Porc. Ded.",10)
		,new ReportColumnMetadata(DEDUCTIBLE_QUOTA,Types.DOUBLE,"Cuota Ded.",10)
		,new ReportColumnMetadata(WITHHOLDING_FARMER,Types.VARCHAR,"Reg.Agric.",7)
		,new ReportColumnMetadata(VAT_ACCRUAL_PAYMENT,Types.VARCHAR,"Reg. Caja.",10)
		,new ReportColumnMetadata(FINANCE_DATE,Types.DATE,"F.Vto.",10)
		,new ReportColumnMetadata(FINANCE_TOTAL,Types.DOUBLE,"Imp. Vto.",10)
		,new ReportColumnMetadata(FINANCE_BASE,Types.DOUBLE,"B.Imp.",10)
		,new ReportColumnMetadata(FINANCE_PERCENTAGE,Types.DOUBLE,"%",10)
		,new ReportColumnMetadata(FINANCE_QUOTA,Types.DOUBLE,"Cuota",10)
	};
	
	public PreparedStatement getStatement(Connection conn,InvoiceReportParams params, boolean vatAccrualPayment) throws SQLException {
		PreparedStatement ps = null;
		StringWriter stmt = new StringWriter();
		stmt.append(" SELECT i.id " + ID);
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
		stmt.append(" ,i.withholding_farmer " + WITHHOLDING_FARMER);
		stmt.append(" ,i.service " + SERVICE);
		stmt.append(" ,i.rectification_type " + RECTIFICATION_TYPE);
		stmt.append(" ,i.rectification_invoice " + RECTIFICATION_INVOICE);
		stmt.append(" ,ea.description " + ACTIVITY);
		stmt.append(" ,ia.description " + INVEST_ASSET);
		stmt.append(" ,i.vat_accrual_payment " + VAT_ACCRUAL_PAYMENT);
		stmt.append(" ,i.total " + INVOICE_TOTAL);
		stmt.append(" ,i.taxable_base " + INVOICE_BASE);
		stmt.append(" ,i.vat_quota " + INVOICE_VAT);
		stmt.append(" ,i.retention_quota " + INVOICE_RETENTION);
		stmt.append(" ,it.tax_type " + TAX_TYPE);
		stmt.append(" ,it.percentage " + PERCENTAGE);
		stmt.append(" ,it.surcharge " + SURCHARGE);
		stmt.append(" ,it.vat_deduction_type " + VAT_DEDUCTION_TYPE);
		stmt.append(" ,it.withholding_type " + WITHHOLDING_TYPE);
		stmt.append(" ,it.deductible_percent " + DEDUCTIBLE_PERCENT);		
		stmt.append("  ,SUM( (IF(it.deductible_quota!=0,it.deductible_quota,IF(it.quota != 0,it.quota,ROUND(it.base * it.percentage / 100, 2)))) * it.deductible_percent / 100 ) " + DEDUCTIBLE_QUOTA);
	    stmt.append("  ,SUM(it.base) " + TAXABLE_BASE);
		stmt.append("  ,SUM( IF(it.quota != 0,it.quota,ROUND(it.base * it.percentage / 100, 2) ) ) " + TAX);
		stmt.append("  ,SUM( IF(it.surcharge_quota != 0,it.surcharge_quota,ROUND(it.base * it.surcharge / 100, 2) ) ) " + SURCHARGE_QUOTA);
		if (!vatAccrualPayment) {
			stmt.append(" FROM invoice_tax it ");
			stmt.append(" INNER JOIN invoice_detail id ON (it.invoice_detail = id.id) ");
			stmt.append(" INNER JOIN invoice i ON (id.invoice = i.id) ");
			stmt.append(" LEFT OUTER JOIN enterprise_activity ea ON (i.activity = ea.id) ");
			stmt.append(" LEFT OUTER JOIN invest_asset ia ON (i.invest_asset = ia.id) ");
			stmt.append(" WHERE it.domain = ?");
			stmt.append(" AND (i.vat_accrual_payment = 0 OR (vat_accrual_payment = 1 AND it.tax_type != 1)) " );  
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
		} else {
			stmt.append(",IF(ft.type=1, ft.amount,  -ft.amount ) " + FINANCE_TOTAL);
			stmt.append(",ft.tracking_date " + FINANCE_DATE);
			stmt.append("  FROM finance_tracking ft ");
			stmt.append("  INNER JOIN finance f ON (ft.finance = f.id) ");
			stmt.append("  INNER JOIN invoice i ON (f.invoice = i.id) ");
			stmt.append("  INNER JOIN invoice_detail id ON (id.invoice = i.id) ");
			stmt.append("  INNER JOIN invoice_tax it ON (it.invoice_detail = id.id) ");
			stmt.append("  LEFT OUTER JOIN enterprise_activity ea ON (i.activity = ea.id) ");
			stmt.append("  LEFT OUTER JOIN invest_asset ia ON (i.invest_asset = ia.id) ");
			stmt.append(" WHERE ft.domain = ?");
			stmt.append("  AND ft.type IN (1,2) ");
			stmt.append(" AND (vat_accrual_payment = 1 AND it.tax_type = 1) " );
			if (params.getFromTaxDate() != null) {
				stmt.append(" AND ft.tracking_date >= ?");
			}
			if (params.getToTaxDate() != null) {
				stmt.append(" AND ft.tracking_date <= ?");
			}
			if (params.getFromInvoiceDate() != null) {
				stmt.append(" AND ft.tracking_date >= ?");
			}
			if (params.getToInvoiceDate() != null) {
				stmt.append(" AND ft.tracking_date <= ?");
			}
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
		stmt.append(" ,i.withholding_farmer,i.service,i.rectification_type,i.rectification_invoice,it.tax_type,it.percentage,it.surcharge");
		stmt.append(" ,it.vat_deduction_type,it.withholding_type ");
		if (vatAccrualPayment) {
			stmt.append("," + FINANCE_TOTAL);
			stmt.append("," + FINANCE_DATE);
		}
		ps = conn.prepareStatement(stmt.toString(),ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		int i = 0;
		ps.setInt(++i, params.getDomain());
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
		return ps;
	}
	public void excelReport(Connection conn, InvoiceReportParams params, Locale locale,OutputStream output) throws IOException, ReportException {
		ReportMetadata metadata = getMetadata();
		ExcelReportExporter exporter = new ExcelReportExporter();
		exporter.startExport(IReportExporter.DEFAULT_NAME);
		exporter.exportHeader(metadata);

		excelReport(conn, params, locale,output, exporter,metadata, false);	
		excelReport(conn, params, locale,output, exporter,metadata, true);
		
		exporter.endExport(output);
		output.flush();
		
	}
	
	
	private void excelReport(Connection conn, InvoiceReportParams params, Locale locale,OutputStream output, ExcelReportExporter exporter, ReportMetadata metadata, boolean isVatAccrualPayment) throws IOException, ReportException {
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			ps = getStatement(conn, params, isVatAccrualPayment);
			rs = ps.executeQuery();
			while (rs.next()) {
				exporter.startLine();
				int i = fillInvoiceCells(rs,exporter,metadata,locale,false);
				if (isVatAccrualPayment) {
					boolean vatAccrualPayment = rs.getInt(VAT_ACCRUAL_PAYMENT) == 1;				
					TaxType taxType = TaxType.values()[rs.getInt(TAX_TYPE)];
					params.setAccrualVatVisible(true);
					if ( vatAccrualPayment && params.isAccrualVatVisible() && taxType == TaxType.VAT ) {
						
						double invoicePercent = rs.getDouble(PERCENTAGE);
						double invoiceTotal = rs.getDouble(INVOICE_TOTAL);
						double invoiceBase = rs.getDouble(INVOICE_BASE);
						double invoiceVAT = rs.getDouble(INVOICE_VAT);
						double invoiceRetention = rs.getDouble(INVOICE_RETENTION);
						invoiceTotal = CommonUtil.round(invoiceBase + invoiceVAT - invoiceRetention);
						double taxableBase= rs.getDouble(TAXABLE_BASE);
						exporter.exportColumn(metadata.getColumns().get((i++)), rs.getDate(FINANCE_DATE));
						double financeTotal = rs.getDouble(FINANCE_TOTAL);
						exporter.exportColumn(metadata.getColumns().get((i++)), financeTotal);
						double financeBase = CommonUtil.round(financeTotal * taxableBase / invoiceTotal,4);
						exporter.exportColumn(metadata.getColumns().get((i++)), CommonUtil.round(financeBase));
						exporter.exportColumn(metadata.getColumns().get((i++)), invoicePercent);
						double financeQuota = CommonUtil.round(financeBase * invoicePercent / 100);
						exporter.exportColumn(metadata.getColumns().get((i++)), financeQuota);
					}
				}
				double surchargePercent = rs.getDouble(SURCHARGE);
				if (surchargePercent > 0) {
					exporter.endLine();		
					exporter.startLine();
					i = fillInvoiceCells(rs,exporter,metadata,locale, true);
				}
				exporter.endLine();
			}
		} catch (SQLException e) {
			throw new ReportException(e.getMessage(),e);
		} finally {
			try {
				if (rs != null)
					rs.close();	
			} catch (SQLException e) {
				
			}
			try {
				if (ps != null)
					ps.close();	
			} catch (SQLException e) {
				
			}
		}
	}


	private int fillInvoiceCells(ResultSet rs, ExcelReportExporter exporter, ReportMetadata metadata, Locale locale, boolean surcharge) throws SQLException, ReportException {
		int i = 0;
		int id = rs.getInt(ID);
		exporter.exportColumn(metadata.getColumns().get((i++)), id );
		
		InvoiceType type = InvoiceType.values()[rs.getInt(TYPE)];
		exporter.exportColumn(metadata.getColumns().get((i++)), (type == InvoiceType.SALES)?OUTPUT_INVOICE:INPUT_INVOICE);
		
		exporter.exportColumn(metadata.getColumns().get((i++)), type.getName(locale));
		
		InvoiceTransactionType transaction = InvoiceTransactionType.values()[rs.getInt(TRANSACTION)];
		exporter.exportColumn(metadata.getColumns().get((i++)), transaction.getName(locale));
		
		boolean investment = rs.getBoolean(INVESTMENT);
		boolean service = (rs.getInt(SERVICE)==1);
		String conceptNature = CONCEPT_NATURE_STANDARD;
		if (type == InvoiceType.SALES && service) {
			conceptNature = CONCEPT_NATURE_SERVICE;
		} 
		if (investment) {
			conceptNature = CONCEPT_NATURE_INVESTMENT;
		} 
		if (type == InvoiceType.EXPENSES || type == InvoiceType.UNDEDUCTIBLE) {
			conceptNature = CONCEPT_NATURE_EXPENSE;
		}
		exporter.exportColumn(metadata.getColumns().get((i++)), conceptNature);
		
		RectificationType rectificationType = RectificationType.values()[rs.getInt(RECTIFICATION_TYPE)];
		String rectification = "";
		if (rectificationType == RectificationType.NORMAL_RECTIFIER) {
			rectification = RECT_NORMAL;
		} else if (rectificationType == RectificationType.SPECIAL_RECTIFIER) {
			rectification = RECT_SPECIAL;
		}
		exporter.exportColumn(metadata.getColumns().get((i++)), rectification);
		
		exporter.exportColumn(metadata.getColumns().get((i++)), rs.getInt(RECTIFICATION_INVOICE));
		exporter.exportColumn(metadata.getColumns().get((i++)), rs.getDate(ISSUE_DATE));
		exporter.exportColumn(metadata.getColumns().get((i++)), rs.getDate(TAX_DATE));
		exporter.exportColumn(metadata.getColumns().get((i++)), rs.getString(ACTIVITY));
		exporter.exportColumn(metadata.getColumns().get((i++)), rs.getString(INVEST_ASSET));
		exporter.exportColumn(metadata.getColumns().get((i++)), rs.getString(REFERENCE_CODE));
		
		String documentNumber = ((InvoiceType.SALES == type) ? "E" : (InvoiceType.UNDEDUCTIBLE == type) ? "G" : "R") + "-";
		String series = rs.getString(SERIES);
		int number = rs.getInt(NUMBER);
		if (!StringUtils.isEmpty(series)) {
			documentNumber += series + "/";
		}
		documentNumber += StringUtils.leftPad(Integer.toString(number), 6, "0");
		exporter.exportColumn(metadata.getColumns().get((i++)), documentNumber);
		exporter.exportColumn(metadata.getColumns().get((i++)), rs.getString(RDOCUMENT));
		exporter.exportColumn(metadata.getColumns().get((i++)), rs.getString(RNAME));

		TaxType taxType = TaxType.values()[rs.getInt(TAX_TYPE)];
		exporter.exportColumn(metadata.getColumns().get((i++)), surcharge?"R.E.":taxType.getName(locale));
		
		if (taxType == TaxType.VAT) {
			VatDeductionType vatDeductionType = VatDeductionType.values()[rs.getInt(VAT_DEDUCTION_TYPE)];
			exporter.exportColumn(metadata.getColumns().get((i++)), vatDeductionType.getName(locale));
		} else {
			exporter.exportColumn(metadata.getColumns().get((i++)), "");
		}
			
		if (taxType == TaxType.RETENTION) {
			WithholdingType withholdingType = WithholdingType.values()[rs.getInt(WITHHOLDING_TYPE)];
			exporter.exportColumn(metadata.getColumns().get((i++)), withholdingType.getName(locale));
		} else {
			exporter.exportColumn(metadata.getColumns().get((i++)), "");
		}
		
		double invoiceBase= rs.getDouble(TAXABLE_BASE);
		exporter.exportColumn(metadata.getColumns().get((i++)), invoiceBase);
		
		double invoicePercent = rs.getDouble(surcharge?SURCHARGE:PERCENTAGE);
		exporter.exportColumn(metadata.getColumns().get((i++)), invoicePercent);
		exporter.exportColumn(metadata.getColumns().get((i++)), rs.getDouble(surcharge?SURCHARGE_QUOTA:TAX));
		exporter.exportColumn(metadata.getColumns().get((i++)), surcharge?0.0:rs.getDouble(DEDUCTIBLE_PERCENT));
		exporter.exportColumn(metadata.getColumns().get((i++)), rs.getDouble(surcharge?SURCHARGE_QUOTA:DEDUCTIBLE_QUOTA));
		
		boolean farmer = rs.getInt(WITHHOLDING_FARMER) == 1;
		exporter.exportColumn(metadata.getColumns().get((i++)), farmer?"X":"");

		boolean vatAccrualPayment = rs.getInt(VAT_ACCRUAL_PAYMENT) == 1;
		exporter.exportColumn(metadata.getColumns().get((i++)), vatAccrualPayment?"X":"");
		
		return i;
	}


	private ReportMetadata getMetadata() throws ReportException {
		ReportMetadata metadata = new ReportMetadata();
		for (ReportColumnMetadata rcm : COLUMN_LABELS) {
			metadata.getColumns().add(rcm);
		}
		return metadata;
	}
	
	
}
