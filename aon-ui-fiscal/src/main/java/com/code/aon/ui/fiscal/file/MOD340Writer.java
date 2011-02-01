package com.code.aon.ui.fiscal.file;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.company.Company;
import com.code.aon.file.format.model.Fd0Exception;
import com.code.aon.file.tax.model.MOD340.IMOD340Provider;
import com.code.aon.file.tax.model.MOD340.MOD340;
import com.code.aon.file.tax.model.MOD340.MOD340Format;
import com.code.aon.file.tax.model.MOD340.data.Deponent;
import com.code.aon.file.tax.model.MOD340.data.IntracommunitaryInvoice;
import com.code.aon.file.tax.model.MOD340.data.InvestmentInvoice;
import com.code.aon.file.tax.model.MOD340.data.Invoice;
import com.code.aon.file.tax.model.MOD340.data.IssuedInvoice;
import com.code.aon.file.tax.model.MOD340.data.ReceivedInvoice;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.util.FinanceUtil;
import com.code.aon.fiscal.model340.Model340Parameters;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.util.AonUtil;

public class MOD340Writer implements IMOD340Provider, IFinanceConstants{

	private Model340Parameters params;
	private MOD340Format format;
	private Company company;
	private SimpleDateFormat formatter;

	private PreparedStatement issuedPreparedStatement = null;
	private ResultSet issuedResultSet = null;
	private PreparedStatement receivedPreparedStatement = null;
	private ResultSet receivedResultSet = null;
	private PreparedStatement investmentPreparedStatement = null;
	private ResultSet investmentResultSet = null;
	private PreparedStatement intracommunitaryPreparedStatement = null;
	private ResultSet intracommunitaryResultSet = null;

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
		try {
			MOD340 mod340 = new MOD340(this,writer);
			List<Exception> exceptions = mod340.create();
			if (exceptions != null && exceptions.size() > 0 ) {
				throw new ManagerBeanException("Se han producido errores durante la generación del modelo");	
			}
		} catch (IOException e) {
			throw new ManagerBeanException(e);
		}
	}

	@Override
	public Deponent getDeponent() throws Fd0Exception{
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
			deponent.setTotalRegister(0); // TODO
			deponent.setTotalTaxableBase(0); // TODO
			deponent.setTotalQuota(0); // TODO
			deponent.setTotalInvoice(0); // TODO
			return deponent;
		} catch (ManagerBeanException e) {
			throw new Fd0Exception("ERROR", e.getMessage());
		}
	}

	@Override
	public MOD340Format getFormat() {
		return format;
	}

	// **************************
	// ******* ISSUED ***********
	// **************************
	@Override
	public void initializeIssuedInvoices() throws Fd0Exception {
		try {
			String sessionName = HibernateUtil.getSessionFactoryName();
			issuedPreparedStatement = HibernateUtil.getSQLConnection(sessionName).prepareStatement(getStatement("WHERE i.type = 1")
				, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			int i = 0;
			if (params.getFromDate() != null) {
				issuedPreparedStatement.setDate(++i, new java.sql.Date(params.getFromDate().getTime()));
			}
			if (params.getToDate() != null) {
				issuedPreparedStatement.setDate(++i, new java.sql.Date(params.getToDate().getTime()));
			}
			issuedResultSet = issuedPreparedStatement.executeQuery();
		} catch (SQLException e) {
			finalize(issuedPreparedStatement, issuedResultSet);
			throw new Fd0Exception("ERROR", e.getMessage());
		}
	}

	@Override
	public boolean hasNextIssuedInvoice() throws Fd0Exception {
		return hasNext(issuedPreparedStatement,issuedResultSet);
	}

	@Override
	public IssuedInvoice getNextIssueInvoice() throws Fd0Exception {
		try {
			IssuedInvoice inv = new IssuedInvoice();
			fillInvoice(inv,issuedResultSet);
			inv.setInvoiceCount(1); 
			inv.setRegisterCount(0); // TODO Número de Registros
			inv.setFirstInvoiceNumber("");
			inv.setLastInvoiceNumber("");
			inv.setCorrectedInvoiceNumber("");
			inv.setSurchargePercent(issuedResultSet.getDouble(13));
			inv.setSurchargeQuota(issuedResultSet.getDouble(19));
			return inv;
		} catch (SQLException e) {
			finalize(issuedPreparedStatement,issuedResultSet);
			throw new Fd0Exception("ERROR", e.getMessage());
		}
	}
	@Override
	public void finalizeIssuedInvoices() throws Fd0Exception {
		finalize(issuedPreparedStatement,issuedResultSet);
	}

	// **************************
	// ******* RECEIVED ***********
	// **************************
	@Override
	public void initializeReceivedInvoices() throws Fd0Exception {
		try {
			String sessionName = HibernateUtil.getSessionFactoryName();
			receivedPreparedStatement = HibernateUtil.getSQLConnection(sessionName).prepareStatement(getStatement("WHERE i.type != 1"), 
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			int i = 0;
			if (params.getFromDate() != null) {
				receivedPreparedStatement.setDate(++i, new java.sql.Date(params.getFromDate().getTime()));
			}
			if (params.getToDate() != null) {
				receivedPreparedStatement.setDate(++i, new java.sql.Date(params.getToDate().getTime()));
			}
			receivedResultSet = receivedPreparedStatement.executeQuery();
		} catch (SQLException e) {
			finalize(receivedPreparedStatement, receivedResultSet);
			throw new Fd0Exception("ERROR", e.getMessage());
		}
	}
	@Override
	public boolean hasNextReceivedInvoice() throws Fd0Exception {
		return hasNext(receivedPreparedStatement,receivedResultSet);
	}
	
	@Override
	public ReceivedInvoice getNextReceivedInvoice() throws Fd0Exception {
		try {
			ReceivedInvoice inv = new ReceivedInvoice();
			fillInvoice(inv,receivedResultSet);
			inv.setInvoiceCount(1); 
			inv.setRegisterCount(0); // TODO Número de Registros
			inv.setFirstInvoiceNumber("");
			inv.setLastInvoiceNumber("");
			inv.setDeductibleQuota(receivedResultSet.getDouble(16));
			return inv;
		} catch (SQLException e) {
			finalize(receivedPreparedStatement,receivedResultSet);
			throw new Fd0Exception("ERROR", e.getMessage());
		}
	}
	@Override
	public void finalizeReceivedInvoices() throws Fd0Exception {
	}

	// **************************
	// ******* INVESTMENT *******
	// **************************

	@Override
	public void initializeInvestmentInvoices() throws Fd0Exception {
		try {
			String sessionName = HibernateUtil.getSessionFactoryName();
			investmentPreparedStatement = HibernateUtil.getSQLConnection(sessionName).prepareStatement(getStatement("WHERE i.investment = 1"), 
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			int i = 0;
			if (params.getFromDate() != null) {
				investmentPreparedStatement.setDate(++i, new java.sql.Date(params.getFromDate().getTime()));
			}
			if (params.getToDate() != null) {
				investmentPreparedStatement.setDate(++i, new java.sql.Date(params.getToDate().getTime()));
			}
			investmentResultSet = investmentPreparedStatement.executeQuery();
		} catch (SQLException e) {
			finalize(investmentPreparedStatement, investmentResultSet);
			throw new Fd0Exception("ERROR", e.getMessage());
		}
	}
	@Override
	public boolean hasNextInvestmentInvoice() throws Fd0Exception {
		return hasNext(investmentPreparedStatement,investmentResultSet);
	}
	@Override
	public InvestmentInvoice getNextInvestmentInvoice() throws Fd0Exception {
		try {
			InvestmentInvoice inv = new InvestmentInvoice();
			fillInvoice(inv,investmentResultSet);
			inv.setYearProrate(0); 
			inv.setYearRegularization(0);
			inv.setDeliveryInvoice("");
			inv.setDoneRegularization(0);
			inv.setInvestementDate("000000");
			inv.setInvestementName("");
			return inv;
		} catch (SQLException e) {
			finalize(investmentPreparedStatement,investmentResultSet);
			throw new Fd0Exception("ERROR", e.getMessage());
		}
	}
	@Override
	public void finalizeInvestmentInvoices() throws Fd0Exception {
		finalize(investmentPreparedStatement,investmentResultSet);
	}
	
	// **************************
	// *** INVTRACOMMUNITARY ****
	// **************************
	
	
	@Override
	public void initializeIntracommunitaryInvoices() throws Fd0Exception {
		try {
			String sessionName = HibernateUtil.getSessionFactoryName();
			intracommunitaryPreparedStatement = HibernateUtil.getSQLConnection(sessionName).prepareStatement(getStatement("WHERE i.transaction = 1"), 
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			int i = 0;
			if (params.getFromDate() != null) {
				intracommunitaryPreparedStatement.setDate(++i, new java.sql.Date(params.getFromDate().getTime()));
			}
			if (params.getToDate() != null) {
				intracommunitaryPreparedStatement.setDate(++i, new java.sql.Date(params.getToDate().getTime()));
			}
			intracommunitaryResultSet = intracommunitaryPreparedStatement.executeQuery();
		} catch (SQLException e) {
			finalize(intracommunitaryPreparedStatement, intracommunitaryResultSet);
			throw new Fd0Exception("ERROR", e.getMessage());
		}
	}
	@Override
	public boolean hasNextIntracommunitaryInvoice() throws Fd0Exception {
		return hasNext(intracommunitaryPreparedStatement,intracommunitaryResultSet);
	}
	@Override
	public IntracommunitaryInvoice getNextIntracommunitaryInvoice() throws Fd0Exception {
		try {
			IntracommunitaryInvoice inv = new IntracommunitaryInvoice();
			fillInvoice(inv,intracommunitaryResultSet);
			inv.setIntracommunitaryType("A");
			InvoiceType type = InvoiceType.values()[intracommunitaryResultSet.getInt(1)];
			inv.setDeclaredKey(type == InvoiceType.SALES?"D":"R");
			inv.setCountryKey("");
			inv.setOperationPeriod(0);
			inv.setDescription("");
			inv.setAddress("");
			inv.setCity("");
			inv.setZip("");
			inv.setOther("");
			return inv;
		} catch (SQLException e) {
			finalize(receivedPreparedStatement,receivedResultSet);
			throw new Fd0Exception("ERROR", e.getMessage());
		}
	}
	@Override
	public void finalizeIntracommunitaryInvoices() throws Fd0Exception {
		finalize(intracommunitaryPreparedStatement,intracommunitaryResultSet);
	}

	// **************************

	private String getStatement(String where) {
			StringWriter stmt = new StringWriter();
			stmt.append(" SELECT i.type,i.transaction,i.investment,i.tax_date,i.issue_date,i.reference_code,i.series,i.number,i.rdocument,i.rname ");
			stmt.append("  ,it.tax_type,it.percentage,it.surcharge,it.vat_deduction_type,it.withholding_type,it.deductible_quota");
			stmt.append("  ,SUM(id.taxable_base) ");
			stmt.append("  ,SUM( IF(it.quota != 0,it.quota,ROUND(id.taxable_base * it.percentage / 100, 2) ) ) TAX");
			stmt.append("  ,SUM( IF(it.surcharge_quota != 0,it.surcharge_quota,ROUND(id.taxable_base * it.surcharge / 100, 2) ) ) RE ");
			stmt.append("  FROM invoice_tax it ");
			stmt.append("  INNER JOIN invoice_detail id ON (it.invoice_detail = id.id) ");
			stmt.append("  INNER JOIN invoice i ON (id.invoice = i.id) ");
			stmt.append(where);
			if (params.getFromDate() != null) {
				stmt.append(" AND i.issue_date >= ?");
			}
			if (params.getToDate() != null) {
				stmt.append(" AND i.issue_date <= ?");
			}
			stmt.append(" GROUP BY i.type,i.transaction,i.investment,i.tax_date,i.issue_date,i.reference_code,i.rdocument,i.rname ");
			stmt.append("  ,it.tax_type,it.percentage,it.surcharge,it.vat_deduction_type,it.withholding_type,it.deductible_quota ");
			return stmt.toString();
	}
	
	private void fillInvoice(Invoice inv,ResultSet rs) throws SQLException {
		inv.setYear( params.getYear());
		inv.setPeriod(params.getPeriodString());
		inv.setCode(getCompany().getDocument());
		inv.setDocument(rs.getString(9));
		inv.setName(rs.getString(10));
		inv.setCountry("ES");
		inv.setCountryKey("1");
		inv.setCountryCode("");
		inv.setCountryNif("");
		inv.setOperation(" ");
		inv.setIssueDate(getFormatter().format(rs.getDate(5)));
		inv.setOperationDate(getFormatter().format(rs.getDate(4)));
		inv.setPercent(rs.getDouble(12));
		inv.setTaxableBase(rs.getDouble(17));
		double quota = rs.getDouble(18);
		inv.setQuota(quota);
		inv.setTotal(quota); // TODO total factura
		inv.setCostTaxableBase(0);
		inv.setInvoiceNumber(rs.getString(6));
		String series = rs.getString(7);
		int number = rs.getInt(8);
		InvoiceType type = InvoiceType.values()[rs.getInt(1)];
		String documentNumber = FinanceUtil.getDocumentNumber(type, series, number);
		inv.setDocumentNumber(documentNumber);
	}

	private boolean hasNext(PreparedStatement ps, ResultSet rs) {
		try {
			return rs.next();
		} catch (SQLException e) {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e1) {
				}
			}
			throw new Fd0Exception("ERROR", e.getMessage());
		}
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
