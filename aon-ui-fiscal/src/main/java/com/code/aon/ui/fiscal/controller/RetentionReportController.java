package com.code.aon.ui.fiscal.controller;

import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.runtime.parser.node.MathUtils;
import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.fiscal.enumeration.VatPeriod;
import com.code.aon.fiscal.enumeration.VatReportOrder;
import com.code.aon.fiscal.enumeration.VatReportType;
import com.code.aon.fiscal.enumeration.VatType;
import com.code.aon.fiscal.vat.Vat;
import com.code.aon.fiscal.vat.VatCollectionParameters;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class RetentionReportController extends BasicController{

	private List<Vat> retentionList;
	private DataModel retentionModel;
	private List<Invoice> invoiceList;
	private Date date;
	private Date fromDate;
	private Date toDate;

	private Date fromInvoiceDate;
	private Date toInvoiceDate;
	
	private String fromSeries;
	private String toSeries;
	private Integer fromNumber;
	private Integer toNumber;

	private VatType vatType;
	private Integer year;
	private VatPeriod vatPeriod;

	private VatReportOrder order;
	private SecurityLevel securityLevel;
	
	private InvoicePriceStrategy priceStrategy;
	
	public InvoicePriceStrategy getPriceStrategy() {
		if (priceStrategy == null) {
			priceStrategy = new InvoicePriceStrategy();
		}
		return priceStrategy;
	}
	
	public double getInvoiceTaxableBase() throws ManagerBeanException {
		Invoice invoice = (Invoice)this.getRetentionModel().getRowData();
		return getPriceStrategy().getTaxableBase(invoice);
	}
	
	public double getInvoiceTotalPrice() throws ManagerBeanException {
		Invoice invoice = (Invoice)this.getRetentionModel().getRowData();
		return getPriceStrategy().getTotalPrice(invoice, invoice);
	}
	
	public double getInvoiceTotalVat() throws ManagerBeanException {
		Invoice invoice = (Invoice)this.getRetentionModel().getRowData();
		return getPriceStrategy().getTotalVatQuota(invoice, invoice);
	}
	
	public double getInvoiceTotalRetention() throws ManagerBeanException {
		Invoice invoice = (Invoice)this.getRetentionModel().getRowData();
		Double ret = getPriceStrategy().getTotalRetentionQuota(invoice, invoice);
		return Math.abs(ret);
	}
	
	public double getInvoiceRetentionPercent() throws ManagerBeanException {
		Invoice invoice = (Invoice)this.getRetentionModel().getRowData();
		List list = new LinkedList<TaxBreakDown>();
		list=getPriceStrategy().getTaxBreakDowns(invoice, invoice);
		if (list.size()!=0){
			Iterator<?> iter = list.iterator();
			while(iter.hasNext()) {
				TaxBreakDown t = (TaxBreakDown) iter.next();
				if(t.getTaxType()==TaxType.RETENTION){
					return t.getTaxPercent();
				}
			}
		}
		return 0;
	}
	
	public double getRetentionPercent(Invoice invoice) throws ManagerBeanException {
		List list = new LinkedList<TaxBreakDown>();
		list=getPriceStrategy().getTaxBreakDowns(invoice, invoice);
		if (list.size()!=0){
			Iterator<?> iter = list.iterator();
			while(iter.hasNext()) {
				TaxBreakDown t = (TaxBreakDown) iter.next();
				if(t.getTaxType()==TaxType.RETENTION){
					return t.getTaxPercent();
				}
			}
		}
		return 0;
	}
	
	public List<Invoice> getInvoiceList() {
		return invoiceList;
	}

	public void setInvoiceList(List<Invoice> invoiceList) {
		this.invoiceList = invoiceList;
	}

	public DataModel getRetentionModel() {
		if (retentionModel == null) {
			retentionModel = new ListDataModel(getInvoiceList());
		}
		return retentionModel;
	}
	
	public void setRetentionModel(DataModel retentionModel) {
		this.retentionModel = retentionModel;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Date getFromInvoiceDate() {
		return fromInvoiceDate;
	}
	public void setFromInvoiceDate(Date fromInvoiceDate) {
		this.fromInvoiceDate = fromInvoiceDate;
	}

	public Date getToInvoiceDate() {
		return toInvoiceDate;
	}
	public void setToInvoiceDate(Date toInvoiceDate) {
		this.toInvoiceDate = toInvoiceDate;
	}

	public String getFromSeries() {
		return fromSeries;
	}
	public void setFromSeries(String fromSeries) {
		this.fromSeries = fromSeries;
	}

	public String getToSeries() {
		return toSeries;
	}
	public void setToSeries(String toSeries) {
		this.toSeries = toSeries;
	}

	public Integer getFromNumber() {
		return fromNumber;
	}
	public void setFromNumber(Integer fromNumber) {
		this.fromNumber = fromNumber;
	}

	public Integer getToNumber() {
		return toNumber;
	}
	public void setToNumber(Integer toNumber) {
		this.toNumber = toNumber;
	}

	public VatReportOrder getOrder() {
		return order;
	}
	public void setOrder(VatReportOrder order) {
		this.order = order;
	}

	public VatType getVatType() {
		return vatType;
	}
	public void setVatType(VatType vatType) {
		this.vatType = vatType;
	}

	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}

	public VatPeriod getVatPeriod() {
		return vatPeriod;
	}
	public void setVatPeriod(VatPeriod vatPeriod) {
		this.vatPeriod = vatPeriod;
	}

	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}

	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}
	public List<Vat> getRetentionList() {
		   return retentionList;
	}
	
	public void setRetentionList(List<Vat> retentionList) {
		this.retentionList = retentionList;
	}

	private VatCollectionParameters getParameters() {
		VatCollectionParameters vcp = new VatCollectionParameters();
		vcp.setDate(getDate());
		vcp.setFromDate(getFromDate());
		vcp.setToDate(getToDate());
		
		vcp.setFromInvoiceDate(getToInvoiceDate());
		vcp.setToInvoiceDate(getToInvoiceDate());
		vcp.setFromSeries(StringUtils.isBlank(getFromSeries())?null:getFromSeries());
		vcp.setToSeries(StringUtils.isBlank(getToSeries())?null:getToSeries());
		vcp.setFromNumber(getFromNumber());
		vcp.setToNumber(getToNumber());
		
		vcp.setVatType(getVatType());
		vcp.setSecurityLevel(getSecurityLevel());
		vcp.setVatPercent(null);
		vcp.setVatType(null);
		vcp.setVatReportType(null);
		return vcp;
	}
	
	public void onReset(ActionEvent event) {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		setDate(c.getTime());
		setYear(c.get(Calendar.YEAR));
		setVatPeriod( VatPeriod.getQuarterlyVatPeriod( c.get(Calendar.MONTH )) );
		setFromDate(getVatPeriod().getStartDate(getYear()));	
		setToDate(getVatPeriod().getDueDate(getYear()));
		setFromSeries(null);
		setFromNumber(null);
		setToSeries(null);
		setToNumber(null);
		setVatType(VatType.OUTPUT);
		setSecurityLevel(null);
		
	}
	
	public void onYearChanged(ValueChangeEvent event) {
		setFromDate(null);
		setToDate(null);
		if (event.getNewValue() != null) {
			Integer year = (Integer) event.getNewValue(); 
			setFromDate(getVatPeriod().getStartDate(year));	
			setToDate(getVatPeriod().getDueDate(year));
		}
	}
	
	public void onVatPeriodChanged(ValueChangeEvent event) {
		setFromDate(null);
		setToDate(null);
		if (getYear() != null) {
			VatPeriod vp = (VatPeriod) event.getNewValue(); 
			setFromDate(vp.getStartDate(getYear()));	
			setToDate(vp.getDueDate(getYear()));
		} else {
			String msg = "El Periodo IVA es necesario para calcular las fecha de inicio y fin del periodo.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	
	public void onRetentionList(ActionEvent e) throws ManagerBeanException{
		retentionList = new LinkedList<Vat>();
		invoiceList = new LinkedList<Invoice>();
		retentionList=getRetentionList(getParameters());
		getInvoiceRetentionList();
		setRetentionModel(null);
	}
	
	public List<Vat> getRetentionList(VatCollectionParameters params) throws ManagerBeanException {
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
			stmt.append("  WHERE it.tax_type = 2 ");
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
				stmt.append(" ORDER BY i.issue_date,i.series,i.number");
			} else if (order == VatReportOrder.TAX_DATE) {
				stmt.append(" ORDER BY i.tax_date,i.series,i.number");
			} else if (order == VatReportOrder.INVOICE_REFERENCE) {
				stmt.append(" ORDER BY i.reference_code");
			} else if (order == VatReportOrder.INVOICE_ORDER_NUMBER) {
				stmt.append(" ORDER BY vatType,i.series,i.number");
			} else if (order == VatReportOrder.INVOICE_REGISTRY_DOCUMENT) {
				stmt.append(" ORDER BY i.rdocument,i.series,i.number");
			} else if (order == VatReportOrder.INVOICE_REGISTRY_NAME) {
				stmt.append(" ORDER BY i.rname,i.series,i.number");
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
	
	public void getInvoiceRetentionList() throws ManagerBeanException {
		String select = "select InvoiceTax.invoiceDetail.invoice "
				+ "from InvoiceTax as InvoiceTax " + "where "
				+ " InvoiceTax.taxType= 2"
				+ " group by InvoiceTax.invoiceDetail.invoice.id"
				+ " order by InvoiceTax.invoiceDetail.invoice.issueDate,InvoiceTax.invoiceDetail.invoice.registryName";;
		Session session = HibernateUtil.getSession(HibernateUtil
				.getSessionFactoryName());
		Query query = session.createQuery(select);
		invoiceList = query.list();

	}



	


}
