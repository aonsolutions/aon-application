package com.code.aon.finance.invoicing;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.Series;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.config.enumeration.VatDeductionType;
import com.code.aon.config.enumeration.WithholdingType;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.InvoiceTax;
import com.code.aon.finance.Pos;
import com.code.aon.finance.PosShift;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class WsPosInvoicing {

	private static final Logger LOGGER = LoggerFactory.getLogger(WsPosInvoicing.class.getName());

	private String userName;

	public Invoice createInvoice(PosShift posShift, Date issueDate, String comments, List<InvoiceDetail> details, List<Finance> finances, String userName) 
			throws ManagerBeanException {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);

			HibernateUtil.beginTransaction(sessionName);

			this.userName = userName;
			Invoice invoice = createInvoice(posShift, issueDate, comments, obtainTaxableBase(details), obtainVatQuota(details));
			createInvoiceDetails(invoice, posShift.getPos().getWorkPlace(), details);
			createInvoiceTaxes(obtainTaxList(details));
			createInvoiceFinances(invoice, finances);

			HibernateUtil.getSession(sessionName).flush();
			HibernateUtil.commitTransaction(sessionName);

			return invoice;
		} catch (Exception e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg = "Unable to rollback transaction!";
				LOGGER.error(msg,daoe);
			}
			LOGGER.error(e.getMessage());
			throw new ManagerBeanException(e.getMessage(),e);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}

	private Invoice createInvoice(PosShift posShift, Date issueDate, String comments, double taxableBase, double vatQuota) throws ManagerBeanException {
		Customer customer = obtainPosCustomer(posShift.getPos());
		Pos pos = posShift.getPos();
		String shiftName = posShift.getShift().getName(Locale.getDefault());

		Invoice invoice = new Invoice();
		invoice.setDomain(posShift.getDomain());
		invoice.setSeries(obtainPosInvoiceSeries(posShift.getPos()));
		invoice.setNumber(obtainSeriesMaxNumber(invoice.getSeries()));
		invoice.setReferenceCode(obtainReferenceCode(invoice.getSeries(), invoice.getNumber()));
		invoice.setRegistry(customer.getRegistry());
		invoice.setRegistryDocument(customer.getRegistry().getDocument());
		invoice.setRegistryDocumentType(customer.getRegistry().getDocumentType());
		invoice.setRegistryDocumentCountry(customer.getRegistry().getDocumentCountry());
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    	String date = StringUtils.rightPad(formatter.format(issueDate), 11);
		invoice.setRegistryName(pos.getWorkPlace().getDescription().concat(" - ").concat(pos.getName()).concat(" - ").concat(date).concat(" - ").concat(shiftName));
		invoice.setIssueDate(issueDate);
		invoice.setTaxDate(issueDate);
		invoice.setSecurityLevel(SecurityLevel.OFFICIAL);
		invoice.setStatus(InvoiceStatus.PENDING);
		invoice.setType(InvoiceType.SALES);
		invoice.setTransaction(InvoiceTransactionType.NATIONAL);
		invoice.setScope(pos.getWorkPlace().getScope());
		invoice.setService(false);
		invoice.setComments(comments);
		invoice.setPosShift(posShift);
		invoice.setTaxableBase(taxableBase);
		invoice.setVatQuota(vatQuota);
		invoice.setTotal(CommonUtil.round(invoice.getTaxableBase() + invoice.getVatQuota()));
		invoice.setCreationUser(userName);
		invoice.setCreationDate(new Date());

		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		return (Invoice)invoiceBean.insert(invoice);
	}

	private void createInvoiceDetails(Invoice invoice, WorkPlace workPlace, List<InvoiceDetail> details) throws ManagerBeanException {
		int line = 0;
		for (InvoiceDetail invoiceDetail : details) {
			invoiceDetail.setDomain(invoice.getDomain());
			invoiceDetail.setInvoice(invoice);
			invoiceDetail.setLine(++line);
			invoiceDetail.setDescription(obtainDetailDescription(invoice.getIssueDate(), null, invoiceDetail.getItem().getProduct().getName()));
			invoiceDetail.setSource(InvoiceSource.DIRECT_INVOICE);
			invoiceDetail.setWorkPlace(workPlace);
			invoiceDetail.setCreationUser(userName);
			invoiceDetail.setCreationDate(new Date());

			invoiceDetail = (InvoiceDetail)BeanManager.getManagerBean(InvoiceDetail.class).insert(invoiceDetail);
		}
	}

	private void createInvoiceTaxes(List<InvoiceTax> taxes) throws ManagerBeanException {
		for (InvoiceTax invoiceTax : taxes) {
			invoiceTax = (InvoiceTax)BeanManager.getManagerBean(InvoiceTax.class).insert(invoiceTax);
		}
	}

	private void createInvoiceFinances(Invoice invoice, List<Finance> finances) throws ManagerBeanException {
		for (Finance finance : finances) {
			finance.setDomain(invoice.getDomain());
			finance.setPayment(false);
			finance.setInvoice(invoice);
			finance.setRegistry(invoice.getRegistry());
			finance.setRegistryDocument(invoice.getRegistryDocument());
			finance.setRegistryDocumentType(invoice.getRegistryDocumentType());
			finance.setRegistryDocumentCountry(invoice.getRegistryDocumentCountry());
			finance.setRegistryName(invoice.getRegistryName());
	        finance.setConcept(invoice.getDocumentNumber()); 
			finance.setDueDate(invoice.getIssueDate());
			finance.setSecurityLevel(invoice.getSecurityLevel());
			finance.setScope(invoice.getScope());
			finance.setFinanceStatus(FinanceStatus.PENDING);
			finance.setManual(true);
			finance.setCreationUser(userName);
			finance.setCreationDate(new Date());
			finance.setSkipCheckPosShift(true);

			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			financeBean.insert(finance);
		}
	}

	private double obtainTaxableBase(List<InvoiceDetail> details) {
		double taxableBase = 0;
		for (InvoiceDetail invoiceDetail : details) {
			taxableBase = CommonUtil.round(taxableBase + invoiceDetail.getTaxableBase(), 4);
		}
		return CommonUtil.round(taxableBase);
	}

	private double obtainVatQuota(List<InvoiceDetail> details) {
		Map<Double, Double> vatMap = new HashMap<Double, Double>();
		for (InvoiceDetail invoiceDetail : details) {
			double base = 0;
			if (vatMap.containsKey(invoiceDetail.getVatPercent())) {
				base = vatMap.get(invoiceDetail.getVatPercent());
			}
			base = CommonUtil.round(base + invoiceDetail.getTaxableBase(), 4);
			vatMap.put(invoiceDetail.getVatPercent(), base);
		}

		double vatQuota = 0;
		for (Double vatPercent : vatMap.keySet()) {
			vatQuota = CommonUtil.round(vatQuota + CommonUtil.round(vatMap.get(vatPercent) * vatPercent / 100));
		}
		return CommonUtil.round(vatQuota);
	}

	private Customer obtainPosCustomer(Pos pos) {
		if (pos.getCustomer() != null && pos.getCustomer().getId() != null) {
			return pos.getCustomer();
		}
		return pos.getWorkPlace().getCustomer();
	}

	private String obtainPosInvoiceSeries(Pos pos) throws ManagerBeanException {
		if (StringUtils.isNotEmpty(pos.getSeries())) {
			return pos.getSeries();
		} else {
			IManagerBean seriesBean = BeanManager.getManagerBean(Series.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_SCOPE_ID), pos.getWorkPlace().getScope().getId());
			criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_ACTIVE), true);
			criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_SECURITY_LEVEL), SecurityLevel.OFFICIAL);
			criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_INVOICE), true);
			for (ITransferObject ito : seriesBean.getList(criteria)) {
				return ((Series)ito).getCode();
			}
		}
		return null;
	}

	private int obtainSeriesMaxNumber(String seriesId) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression("invoice.type", InvoiceType.SALES.ordinal());
		return SeriesNumberUtil.obtainNumber(seriesId, "Invoice", criteria);
	}
	
	private String obtainReferenceCode(String series, int number) throws ManagerBeanException {
		String referenceCode = StringUtils.leftPad(Integer.toString(number), 6, "0");
		if (!StringUtils.isBlank(series)) {
			referenceCode = series + "/" + referenceCode;
		}
		return referenceCode;
	}
	
	private String obtainDetailDescription(Date effectiveDate, String room, String description) {
    	DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    	String date = StringUtils.rightPad(formatter.format(effectiveDate), 11);
    	room = (room == null) ? StringUtils.rightPad(StringUtils.repeat("-", 5), 6) : StringUtils.rightPad(room, 6);
    	return (date + room + description);
	}

	private List<InvoiceTax> obtainTaxList(List<InvoiceDetail> details) throws ManagerBeanException {
		List<InvoiceTax> taxes = new LinkedList<InvoiceTax>();
		for (InvoiceDetail invoiceDetail : details) {
			InvoiceTax invoiceTax = new InvoiceTax();
			invoiceTax.setDomain(invoiceDetail.getDomain());
			invoiceTax.setInvoiceDetail(invoiceDetail);
			invoiceTax.setTaxType(TaxType.VAT);
			invoiceTax.setBase(invoiceDetail.getTaxableBase());
			invoiceTax.setPercentage(invoiceDetail.getVatPercent());
			invoiceTax.setDeductiblePercent(100);
			invoiceTax.setVatDeductionType(VatDeductionType.WITH_RIGHT);
			invoiceTax.setWithholdingType(WithholdingType.PROFESSIONAL);
			taxes.add(invoiceTax);
		}
		return taxes;
	}

}
