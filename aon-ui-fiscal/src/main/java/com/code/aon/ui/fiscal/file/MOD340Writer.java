package com.code.aon.ui.fiscal.file;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.Country;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Company;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.file.format.model.Fd0Exception;
import com.code.aon.file.tax.model.MOD340.MOD340;
import com.code.aon.file.tax.model.MOD340.MOD340Format;
import com.code.aon.file.tax.model.MOD340.data.Deponent;
import com.code.aon.file.tax.model.MOD340.data.Invoice;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.util.FinanceUtil;
import com.code.aon.fiscal.mod340.Model340Parameters;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.util.AonUtil;

public class MOD340Writer implements IFinanceConstants{

	private Model340Parameters params;
	private MOD340Format format;
	private Company company;
	private SimpleDateFormat formatter;

	public MOD340Writer(Model340Parameters params,MOD340Format format) {
		this.params = params;	
		this.format = format;	
	}
	private SimpleDateFormat getFormatter() {
		if (formatter == null) {
			formatter = new SimpleDateFormat("yyyyMMdd");
		}
		return formatter;
	}
	
	private Company getCompany() {
		if (company == null) {
			CompanyController companyController = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
			setCompany( companyController.obtainCompany() );
		}
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}

	public void createMOD340(PrintWriter writer ) throws ManagerBeanException {
		PreparedStatement invoicesPs = null;
		ResultSet invoicesRs = null;
		PreparedStatement taxPs = null;
		ResultSet taxRs = null;
		PreparedStatement sumPs = null;
		ResultSet sumRs = null;
		try {
			Deponent deponent = getDeponent();
			String sessionName = HibernateUtil.getSessionFactoryName();
			StringWriter stmt = new StringWriter();
			stmt.append(" SELECT i.id invoice_id");
			stmt.append(", i.type type");
			stmt.append(",i.transaction transaction");
			stmt.append(",i.investment investment");
			stmt.append(",i.tax_date tax_date");
			stmt.append(",i.issue_date issue_date");
			stmt.append(",i.reference_code reference_code");
			stmt.append(",i.series series");
			stmt.append(",i.number number");
			stmt.append(",i.rdocument rdocument");
			stmt.append(",i.rdocument_country rdocument_country");
			stmt.append(",i.rname rname");
			stmt.append(",i.rectification_type rectification_type");
			stmt.append(",i.rectification_invoice rectification_invoice");
			stmt.append("  FROM invoice i ");
			stmt.append(" WHERE ");
			stmt.append( DomainManager.getSQLWhereClause("i.domain") );
			appendParams(stmt);
			stmt.append(" ORDER BY i.series,i.number");
			invoicesPs  = HibernateUtil.getSQLConnection(sessionName).prepareStatement( stmt.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			
			stmt = new StringWriter();
			stmt.append(" SELECT count(DISTINCT it.percentage) ");
			stmt.append("  FROM invoice_tax it ");
			stmt.append("  INNER JOIN invoice_detail id ON (it.invoice_detail = id.id) ");
			stmt.append("  WHERE id.invoice = ?");
			stmt.append("  AND it.tax_type = 1"); // Solo IVA
			sumPs  = HibernateUtil.getSQLConnection(sessionName).prepareStatement( stmt.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);			

			stmt = new StringWriter();
			stmt.append(" SELECT it.tax_type tax_type");
			stmt.append(",it.percentage percentage");
			stmt.append(",it.surcharge surcharge");
			stmt.append(",it.vat_deduction_type vat_deduction_type");
			stmt.append(",it.withholding_type withholding_type");
			stmt.append(",it.deductible_quota deductible_quota");
			stmt.append(",SUM(id.taxable_base) taxable_base");
			stmt.append(",SUM( IF(it.quota != 0,it.quota,ROUND(id.taxable_base * it.percentage / 100, 2) ) ) quota");
			stmt.append(",SUM( IF(it.surcharge_quota != 0,it.surcharge_quota,ROUND(id.taxable_base * it.surcharge / 100, 2) ) ) surcharge_quota ");
			stmt.append("  FROM invoice_tax it ");
			stmt.append("  INNER JOIN invoice_detail id ON (it.invoice_detail = id.id) ");
			stmt.append("  WHERE id.invoice = ?");
			stmt.append("  AND it.tax_type = 1"); // Solo IVA
			stmt.append("  GROUP BY id.invoice,it.percentage,it.surcharge");
			taxPs  = HibernateUtil.getSQLConnection(sessionName).prepareStatement( stmt.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);			
			appendParams(invoicesPs);
			invoicesRs = invoicesPs.executeQuery();
			List<Invoice> invoices = new LinkedList<Invoice>();
			while (invoicesRs.next()) {
				Integer id = invoicesRs.getInt("invoice_id");
				sumPs.setInt(1, id);
				sumRs = sumPs.executeQuery();
				int numTaxes = 1;
				if (sumRs.next()) {
					numTaxes = sumRs.getInt(1);	
				}
				sumRs.close();
				taxPs.setInt(1, id);
				taxRs = taxPs.executeQuery();
				while (taxRs.next()) {
					Invoice inv = fillInvoice( invoicesRs );
					inv.setRegisterCount(numTaxes);
					inv.setOperation(numTaxes > 1?"C":"");
					double taxableBase = taxRs.getDouble("taxable_base");
					double quota = taxRs.getDouble("quota");
					double surchargeQuota = taxRs.getDouble("surcharge_quota");
					inv.setTaxableBase(taxRs.getDouble("taxable_base"));
					inv.setPercent(taxRs.getDouble("percentage"));
					inv.setQuota(quota);
					
					inv.setSurchargePercent(taxRs.getDouble("surcharge"));
					inv.setSurchargeQuota(surchargeQuota);
					
					double total = CommonUtil.round( taxableBase + quota + surchargeQuota);
					inv.setTotal(total);
					inv.setCostTaxableBase(0);
					inv.setDeductibleQuota(taxRs.getDouble("deductible_quota"));
					deponent.setTotalTaxableBase( CommonUtil.round(deponent.getTotalTaxableBase() + taxableBase ));
					deponent.setTotalInvoice( CommonUtil.round(deponent.getTotalInvoice() + total));
					deponent.setTotalQuota( CommonUtil.round(deponent.getTotalQuota() + quota));
					deponent.setTotalRegister( CommonUtil.round(deponent.getTotalRegister() + 1 ));
					invoices.add(inv);
				}
				taxRs.close();
			}
			System.out.println(" Total Registros ..: " + deponent.getTotalRegister() );
			System.out.println(" Total Base Imp. ..: " + deponent.getTotalTaxableBase() );
			System.out.println(" Total Cuota ......: " + deponent.getTotalQuota() );
			System.out.println(" Total ............: " + deponent.getTotalInvoice() );
			
			MOD340 mod340 = new MOD340(this.format,writer,deponent,invoices);
			mod340.create();
			
			sumPs.close();
			taxPs.close();
			invoicesRs.close();
			invoicesPs.close();
		} catch (IOException e) {
			finalize(taxPs, taxRs);
			finalize(invoicesPs, invoicesRs);
			throw new Fd0Exception("ERROR", e.getMessage());
		} catch (SQLException e) {
			finalize(taxPs, taxRs);
			finalize(invoicesPs, invoicesRs);
			e.printStackTrace();
			throw new Fd0Exception("ERROR", e.getMessage());
		}
	}

	private Deponent getDeponent() throws Fd0Exception{
		try {
			Deponent deponent = new  Deponent();
			deponent.setYear(params.getYear());
			deponent.setPeriod(params.getPeriodString());
			deponent.setCode(getCompany().getDocument());
			deponent.setType("T");
			deponent.setName(getCompany().getName());
			RegistryMedia phone = getCompany().getPhone();
			deponent.setRelPhone(null);
			if (phone != null){
				try {
					deponent.setRelPhone(Integer.parseInt( phone.getValue() ));
				} catch (NumberFormatException e) {
					// Nothing
				}
			}
			deponent.setRelName(getCompany().getName());
			deponent.setNumber("340" + deponent.getYear() + deponent.getPeriod() + "0000"); 
			deponent.setComplementary(null);
			deponent.setReplacement(null);
			deponent.setPreviousNumber(null);
			deponent.setTotalRegister( 0 ); 
			deponent.setTotalTaxableBase(0);
			deponent.setTotalQuota(0);
			deponent.setTotalInvoice(0);
			return deponent;
		} catch (ManagerBeanException e) {
			throw new Fd0Exception("ERROR", e.getMessage());
		}
	}

	private void appendParams(StringWriter stmt) {
		if (params.getFromDate() != null) {
			stmt.append(" AND " + (params.isTaxDateEnabled()?"i.tax_date":"i.issue_date") + " >= ?");
		}
		if (params.getToDate() != null) {
			stmt.append(" AND " + (params.isTaxDateEnabled()?"i.tax_date":"i.issue_date") + " <= ?");
		}
	}
	
	private void appendParams(PreparedStatement ps) throws SQLException {
		int i = 0;
		if (params.getFromDate() != null) {
			ps.setDate(++i, new java.sql.Date(params.getFromDate().getTime()));
		}
		if (params.getToDate() != null) {
			ps.setDate(++i, new java.sql.Date(params.getToDate().getTime()));
		}
	}

	private Invoice fillInvoice(ResultSet rs) throws SQLException {
		Invoice inv = new Invoice();
		InvoiceType type = InvoiceType.values()[rs.getInt("type")];
		InvoiceTransactionType transaction = InvoiceTransactionType.values()[rs.getInt("transaction")];
		boolean investment = rs.getInt("investment") == 1;
		if ( type == InvoiceType.SALES) {
			inv.setType(MOD340.ISSUED);
			inv.setInvoiceCount(1); 
			inv.setFirstInvoiceNumber("");
			inv.setLastInvoiceNumber("");
			inv.setRectifiedInvoiceNumber("");
		} else if (investment) {
				inv.setType(MOD340.INVESTMENT);
				inv.setYearProrate(0); 
				inv.setYearRegularization(0);
				inv.setDeliveryInvoice("");
				inv.setDoneRegularization(0);
				inv.setInvestementDate("00000000");
				inv.setInvestementName("");
//		} else if ( transaction == InvoiceTransactionType.INTRACOMMUNITY) {
//			inv.setType(MOD340.INTRACOMMUNITARY);
//			inv.setIntracommunitaryType("A");
//			inv.setDeclaredKey(type == InvoiceType.SALES?"D":"R");
//			inv.setCountryKey("");
//			inv.setOperationPeriod(0);
//			inv.setDescription("");
//			inv.setAddress("");
//			inv.setCity("");
//			inv.setZip("");
//			inv.setOther("");
		} else {
			inv.setType(MOD340.RECEIVED);
			inv.setInvoiceCount(1); 
			inv.setFirstInvoiceNumber("");
			inv.setLastInvoiceNumber("");
		}
		
		inv.setYear( params.getYear());
		inv.setPeriod(params.getPeriodString());
		inv.setCode(getCompany().getDocument());
		String document = rs.getString("rdocument");
		inv.setName(rs.getString("rname"));
		inv.setCountry(rs.getString("rdocument_country"));
		Country c = Country.valueOf(inv.getCountry());
		if (c == Country.ES) {
			inv.setCountryKey("1");
			inv.setCountryCode("ES");
			inv.setDocument(document);
		} else {
			if (c.isEuropeanUnionMember()) {
				inv.setCountryKey("2");
				inv.setCountryCode(c == Country.GR ?"EL" : c.getValue());
				inv.setCountryNif(document);
			} else {
				// No se si esto está bien.
				inv.setCountryKey("4");
				inv.setCountryCode(c.getValue());
				inv.setCountryNif(document);
			}
		}
		inv.setIssueDate(getFormatter().format(rs.getDate("issue_date")));
		inv.setOperationDate(getFormatter().format(rs.getDate("tax_date")));
		inv.setInvoiceNumber(rs.getString("reference_code"));
		String series = rs.getString("series");
		int number = rs.getInt("number");
		String documentNumber = FinanceUtil.getDocumentNumber(type, series, number);
		inv.setDocumentNumber(documentNumber);
		return inv;
	}

	private void finalize(PreparedStatement ps,ResultSet rs) {
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
