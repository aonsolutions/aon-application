package com.code.aon.ui.fiscal.file;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.Country;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Company;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.code.aon.file.format.model.Fd0Exception;
import com.code.aon.file.tax.model.MOD340.MOD340;
import com.code.aon.file.tax.model.MOD340.MOD340Format;
import com.code.aon.file.tax.model.MOD340.data.Deponent;
import com.code.aon.file.tax.model.MOD340.data.Invoice;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.enumeration.RectificationType;
import com.code.aon.finance.util.FinanceUtil;
import com.code.aon.fiscal.mod340.Model340Parameters;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.fiscal.controller.FiscalParametersController;
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
		PreparedStatement rectifiedInvoicePs= null;
		ResultSet rectifiedInvoiceRs= null;
		PreparedStatement financePs= null;
		ResultSet financeRs= null;
		Connection conn = null;
		
		try {
			conn = DatabaseUtil.getConnection( params.getDomain() );
			String rectifiedSelect = "SELECT i.type,i.series,i.number FROM invoice i WHERE id = ?";
			rectifiedInvoicePs  = conn.prepareStatement( rectifiedSelect , ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			
			Deponent deponent = getDeponent();
			StringWriter stmt = new StringWriter();
			stmt.append("( SELECT i.id invoice_id");
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
			stmt.append(",i.vat_accrual_payment vat_accrual_payment");
			stmt.append(",i.taxable_base taxable_base");
			stmt.append(",i.vat_quota vat_quota");
			stmt.append(",i.retention_quota retention_quota");
			stmt.append(",i.total total");
			stmt.append("  FROM invoice i ");
			stmt.append(" WHERE ");
			stmt.append( DomainManager.getSQLWhereClause("i.domain") );
			stmt.append(" AND " + (params.isTaxDateEnabled()?"i.tax_date":"i.issue_date") + " >= ?");
			stmt.append(" AND " + (params.isTaxDateEnabled()?"i.tax_date":"i.issue_date") + " <= ?");
			stmt.append(" )");
			stmt.append(" UNION ");
			stmt.append("( SELECT i.id invoice_id");
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
			stmt.append(",i.vat_accrual_payment vat_accrual_payment");
			stmt.append(",i.taxable_base taxable_base");
			stmt.append(",i.vat_quota vat_quota");
			stmt.append(",i.retention_quota retention_quota");
			stmt.append(",i.total total");
			stmt.append("  FROM finance_tracking ft ");
			stmt.append("  INNER JOIN finance f ON ft.finance = f.id"); 
			stmt.append("  INNER JOIN invoice i ON f.invoice = i.id");
			stmt.append(" WHERE ");
			stmt.append( DomainManager.getSQLWhereClause("i.domain") );
			stmt.append(" AND i.vat_accrual_payment = 1");
			stmt.append(" AND " + (params.isTaxDateEnabled()?"i.tax_date":"i.issue_date") + " < ?");
			stmt.append(" AND ft.tracking_date >= ?");
			stmt.append(" AND ft.tracking_date <= ?");
			stmt.append(") ORDER BY type,series,number");
			invoicesPs  = conn.prepareStatement( stmt.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			
			stmt = new StringWriter();
			stmt.append(" SELECT count(DISTINCT it.percentage) ");
			stmt.append("  FROM invoice_tax it ");
			stmt.append("  INNER JOIN invoice_detail id ON (it.invoice_detail = id.id) ");
			stmt.append("  WHERE id.invoice = ?");
			stmt.append("  AND it.tax_type = 1"); // Solo IVA
			sumPs  = conn.prepareStatement( stmt.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);			

			String vatAccrualSelect = "SELECT "
				+ " ft.tracking_date tracking_date"
				+ ",IF(ft.type=1, ft.amount,  -ft.amount ) amount"
				+ ",ft.rbank rbank"
				+ ",rbank.bank_account bank_account" 
				+" FROM finance_tracking ft"
				+" INNER JOIN finance f ON ft.finance = f.id"
				+" LEFT OUTER JOIN rbank ON ft.rbank = rbank.id"
				+" WHERE ft.domain = ?"
				+" AND f.invoice = ?"
				+" AND ft.tracking_date BETWEEN ? AND ?"
				+" AND ft.type IN (1,2)";
			financePs  = conn.prepareStatement( vatAccrualSelect, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			
			stmt = new StringWriter();
			stmt.append(" SELECT it.tax_type tax_type");
			stmt.append(",it.percentage percentage");
			stmt.append(",it.surcharge surcharge");
			stmt.append(",it.deductible_quota deductible_quota");
			stmt.append(",SUM(id.taxable_base) taxable_base");
			stmt.append(",SUM( IF(it.quota != 0,it.quota,ROUND(id.taxable_base * it.percentage / 100, 2) ) ) quota");
			stmt.append(",SUM( IF(it.surcharge_quota != 0,it.surcharge_quota,ROUND(id.taxable_base * it.surcharge / 100, 2) ) ) surcharge_quota ");
			stmt.append("  FROM invoice_tax it ");
			stmt.append("  INNER JOIN invoice_detail id ON (it.invoice_detail = id.id) ");
			stmt.append("  WHERE id.invoice = ?");
			stmt.append("  AND it.tax_type = 1"); // Solo IVA
			stmt.append("  GROUP BY id.invoice,it.percentage,it.surcharge");
			taxPs  = conn.prepareStatement( stmt.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);			
			int i = 0;
			invoicesPs.setDate(++i, new java.sql.Date(params.getFromDate().getTime()));
			invoicesPs.setDate(++i, new java.sql.Date(params.getToDate().getTime()));
			invoicesPs.setDate(++i, new java.sql.Date(params.getFromDate().getTime()));
			invoicesPs.setDate(++i, new java.sql.Date(params.getFromDate().getTime()));
			invoicesPs.setDate(++i, new java.sql.Date(params.getToDate().getTime()));
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
//					Invoice inv = fillInvoice(rectifiedInvoicePs,rectifiedInvoiceRs, invoicesRs );
					
					Invoice inv = new Invoice();
					boolean vatAccrualPayment = (invoicesRs.getInt("vat_accrual_payment") == 1); 
					InvoiceType type = InvoiceType.values()[invoicesRs.getInt("type")];
					InvoiceTransactionType transaction = InvoiceTransactionType.values()[invoicesRs.getInt("transaction")];
					if (transaction == InvoiceTransactionType.OTHER_ISP) {
						inv.setOperation("I");
					}
					boolean investment = invoicesRs.getInt("investment") == 1;
					if ( type == InvoiceType.SALES) {
						inv.setType(MOD340.ISSUED);
						inv.setInvoiceCount(1); 
						inv.setFirstInvoiceNumber("");
						inv.setLastInvoiceNumber("");
						inv.setRectifiedInvoiceNumber("");
						RectificationType rt = RectificationType.values()[invoicesRs.getInt("rectification_type")];
						if ( rt == RectificationType.NORMAL_RECTIFIER || rt == RectificationType.SPECIAL_RECTIFIER ) {
							int rectifiedInvoice = invoicesRs.getInt("rectification_invoice");	
							rectifiedInvoicePs.setInt(1, rectifiedInvoice);
							rectifiedInvoiceRs = rectifiedInvoicePs.executeQuery();
							if (rectifiedInvoiceRs.next()) {
								InvoiceType rType = InvoiceType.values()[rectifiedInvoiceRs.getInt(1)];
								String rSeries = rectifiedInvoiceRs.getString(2);
								int rNumber = rectifiedInvoiceRs.getInt(3);
								String documentNumber = FinanceUtil.getDocumentNumber(rType, rSeries, rNumber);
								inv.setOperation("D");
								inv.setRectifiedInvoiceNumber(documentNumber);
							}
							rectifiedInvoiceRs.close();
						}
					} else if (investment && params.isInvestmentBookEnabled()) {
							inv.setType(MOD340.INVESTMENT);
							inv.setYearProrate(0); 
							inv.setYearRegularization(0);
							inv.setDeliveryInvoice("");
							inv.setDoneRegularization(0);
							inv.setInvestementDate("00000000");
							inv.setInvestementName("");
					} else {
						inv.setType(MOD340.RECEIVED);
						inv.setInvoiceCount(1); 
						inv.setFirstInvoiceNumber("");
						inv.setLastInvoiceNumber("");
					}
					
					inv.setYear( params.getYear());
					inv.setPeriod(params.getPeriodString());
					inv.setCode(getCompany().getDocument());
					String document = invoicesRs.getString("rdocument");
					inv.setName(invoicesRs.getString("rname"));
					inv.setCountry(invoicesRs.getString("rdocument_country"));
					Country c = Country.valueOf(inv.getCountry());
					if (c == Country.ES) {
						inv.setCountryKey("1");
						inv.setCountryCode("");
						inv.setDocument(document);
					} else {
						if (c.isEuropeanUnionMember()) {
							inv.setCountryKey("2");
							inv.setCountryCode(c == Country.GR ? "EL" : c.getValue());
							inv.setCountryNif(document);
						} else {
							// No se si esto está bien.
							inv.setCountryKey("4");
							inv.setCountryCode(c.getValue());
							inv.setCountryNif(document);
						}
					}
					inv.setIssueDate(getFormatter().format(invoicesRs.getDate("issue_date")));
					inv.setOperationDate(getFormatter().format(invoicesRs.getDate("tax_date")));
					inv.setInvoiceNumber(invoicesRs.getString("reference_code"));
					String series = invoicesRs.getString("series");
					int number = invoicesRs.getInt("number");
					String documentNumber = FinanceUtil.getDocumentNumber(type, series, number);
					inv.setDocumentNumber(documentNumber);

					inv.setRegisterCount(numTaxes);
					if (StringUtils.isEmpty(inv.getOperation())) {
						inv.setOperation(numTaxes > 1?"C":"");
					}
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
					
					// TODO Hay que cambiar el programa y aplicar el porcentaje de deducibilidad.
					if (MOD340.RECEIVED.equals(inv.getType())) {
						inv.setDeductibleQuota(quota);	
					}
					// ------------------------------------------------
					
					deponent.setTotalTaxableBase( CommonUtil.round(deponent.getTotalTaxableBase() + taxableBase ));
					deponent.setTotalInvoice( CommonUtil.round(deponent.getTotalInvoice() + total));
					deponent.setTotalQuota( CommonUtil.round(deponent.getTotalQuota() + quota));
					deponent.setTotalRegister( CommonUtil.round(deponent.getTotalRegister() + 1 ));
					
					if (vatAccrualPayment 
						&& (inv.getType() == MOD340.ISSUED || inv.getType() == MOD340.RECEIVED)
						&& (!StringUtils.equals("I", inv.getOperation()))) {
						
						financePs.setInt(1, DomainManager.getCurrentDomain());
						financePs.setInt(2, id);
						financePs.setDate(3, new java.sql.Date(params.getFromDate().getTime()));
						financePs.setDate(4, new java.sql.Date(params.getToDate().getTime()));
						financeRs = financePs.executeQuery();
						boolean first = true;
						while (financeRs.next()) {
							if (!first) {
								String operation = "Z";
								if (StringUtils.equals("C",inv.getOperation())) {
									operation = "2";	
								} else if (StringUtils.equals("D",inv.getOperation())) {
									operation = "3";
								}
								inv.setOperation(operation);
								invoices.add(inv);
								inv = inv.cloneInvoice();
							}
							double amount = financeRs.getDouble("amount");
							double invoiceBase = invoicesRs.getDouble("taxable_base");
							double invoiceVat  = invoicesRs.getDouble("vat_quota");
							double invoiceRetention = invoicesRs.getDouble("retention_quota");
							double invoiceTotal = invoicesRs.getDouble("total");
							invoiceTotal = CommonUtil.round(invoiceBase + invoiceVat - invoiceRetention);
							amount = CommonUtil.round(amount * taxableBase / invoiceTotal,4);
							
							inv.setFinanceAmount(amount);
							inv.setFinanceDate(getFormatter().format(financeRs.getDate("tracking_date")));
							String iban = financeRs.getString("bank_account");
							if (StringUtils.isEmpty(iban)) {
								inv.setFinanceType("O");
								inv.setFinanceBank("");
							} else {
								inv.setFinanceType("C");
								inv.setFinanceBank(iban);
							}
							inv.setFinanceDate(getFormatter().format(financeRs.getDate("tracking_date")));
							first = false;
						}
						financeRs.close();
						String operation = "Z";
						if (StringUtils.equals("C",inv.getOperation())) {
							operation = "2";	
						} else if (StringUtils.equals("D",inv.getOperation())) {
							operation = "3";
						}
						inv.setOperation(operation);
					}
					invoices.add(inv);
				}
				taxRs.close();
			}
			MOD340 mod340 = new MOD340(this.format,writer,deponent,invoices);
			mod340.create();
			
			sumPs.close();
			taxPs.close();
			invoicesRs.close();
			invoicesPs.close();
		} catch (IOException e) {
			throw new Fd0Exception("ERROR", e.getMessage());
		} catch (SQLException e) {
			e.printStackTrace();
			throw new Fd0Exception("ERROR", e.getMessage());
		} catch (AonConnectionException e) {
			throw new Fd0Exception("ERROR", e.getMessage());
		} finally {
			DatabaseUtil.closeQuietly(sumRs);
			DatabaseUtil.closeQuietly(sumPs);
			DatabaseUtil.closeQuietly(taxRs);
			DatabaseUtil.closeQuietly(taxPs);
			DatabaseUtil.closeQuietly(invoicesRs);
			DatabaseUtil.closeQuietly(invoicesPs);
			DatabaseUtil.closeQuietly(rectifiedInvoiceRs);
			DatabaseUtil.closeQuietly(rectifiedInvoicePs);
			DatabaseUtil.closeQuietly(financeRs);
			DatabaseUtil.closeQuietly(financePs);
			DatabaseUtil.closeQuietly(conn);
		}
	}

	private Deponent getDeponent() throws Fd0Exception{
		FiscalParametersController fiscalParams = (FiscalParametersController) AonUtil
				.getRegisteredBean(FiscalParametersController.FISCAL_PARAMS_BEAN_NAME);

		Deponent deponent = new  Deponent();
		deponent.setYear(params.getYear());
		deponent.setPeriod(params.getPeriodString());
		deponent.setCode(getCompany().getDocument());
		deponent.setType("T");
		deponent.setName(getCompany().getName());
		try {
			String phone = fiscalParams.getContactPhone();
			phone = StringUtils.remove(phone," ");
			deponent.setRelPhone(Integer.parseInt( phone ));
		} catch (NumberFormatException e) {
			// Nothing
		}
		deponent.setRelName(fiscalParams.getContactPerson() );
		deponent.setNumber("340" + deponent.getYear() + deponent.getPeriod() + "000" + (params.isReplacement()?"1":"0")); 
		deponent.setComplementary(null);
		deponent.setReplacement(params.isReplacement()?"S":null);
		deponent.setPreviousNumber(params.isReplacement()?params.getPreviousNumber():"0000000000000");
		deponent.setVatDeclarationNumber(params.getVatDeclarationNumber() );
		deponent.setTotalRegister( 0 ); 
		deponent.setTotalTaxableBase(0);
		deponent.setTotalQuota(0);
		deponent.setTotalInvoice(0);
		return deponent;
	}

/*	
	private Invoice fillInvoice(PreparedStatement rectifiedInvoicePs, ResultSet  rectifiedInvoiceRs
			, ResultSet rs) throws SQLException {
		Invoice inv = new Invoice();
		InvoiceType type = InvoiceType.values()[rs.getInt("type")];
		InvoiceTransactionType transaction = InvoiceTransactionType.values()[rs.getInt("transaction")];
		if (transaction == InvoiceTransactionType.OTHER_ISP) {
			inv.setOperation("I");
		}
		boolean investment = rs.getInt("investment") == 1;
		if ( type == InvoiceType.SALES) {
			inv.setType(MOD340.ISSUED);
			inv.setInvoiceCount(1); 
			inv.setFirstInvoiceNumber("");
			inv.setLastInvoiceNumber("");
			inv.setRectifiedInvoiceNumber("");
			RectificationType rt = RectificationType.values()[rs.getInt("rectification_type")];
			if ( rt == RectificationType.NORMAL_RECTIFIER || rt == RectificationType.SPECIAL_RECTIFIER ) {
				int rectifiedInvoice = rs.getInt("rectification_invoice");	
				rectifiedInvoicePs.setInt(1, rectifiedInvoice);
				rectifiedInvoiceRs = rectifiedInvoicePs.executeQuery();
				if (rectifiedInvoiceRs.next()) {
					InvoiceType rType = InvoiceType.values()[rectifiedInvoiceRs.getInt(1)];
					String rSeries = rectifiedInvoiceRs.getString(2);
					int rNumber = rectifiedInvoiceRs.getInt(3);
					String documentNumber = FinanceUtil.getDocumentNumber(rType, rSeries, rNumber);
					inv.setOperation("D");
					inv.setRectifiedInvoiceNumber(documentNumber);
				}
				rectifiedInvoiceRs.close();
			}
		} else if (investment && params.isInvestmentBookEnabled()) {
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
			inv.setCountryCode("");
			inv.setDocument(document);
		} else {
			if (c.isEuropeanUnionMember()) {
				inv.setCountryKey("2");
				inv.setCountryCode(c == Country.GR ? "EL" : c.getValue());
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
*/	
}
