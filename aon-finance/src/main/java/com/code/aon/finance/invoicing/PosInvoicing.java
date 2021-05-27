package com.code.aon.finance.invoicing;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.Pos;
import com.code.aon.finance.PosShift;
import com.code.aon.finance.PosShiftCount;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.product.Item;
import com.code.aon.product.pricing.ItemPricesManager;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class PosInvoicing {

	private static final Logger LOGGER = LoggerFactory.getLogger(PosInvoicing.class.getName());
	
	public Invoice createInvoice(PosShift posShift, String shiftName) throws ManagerBeanException {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);

			HibernateUtil.beginTransaction(sessionName);

			Invoice invoice = createInvoice(posShift, posShift.getStartTime(), shiftName, shiftName);
			createInvoiceDetail(invoice, posShift.getPos().getWorkPlace(), posShift.getPos().getItemInvoice());

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

	public Invoice completeInvoice(PosShift posShift, String shiftName, String ticketInfo) throws ManagerBeanException {
		Invoice invoice = obtainPosShiftInvoice(posShift);
		if (invoice == null) {
			invoice = createInvoice(posShift, posShift.getEndTime(), shiftName, "GENERADA EN EL ARQUEO");
		}
		if (invoice.isRecorded()) {
			throw new ManagerBeanException("No se puede modificar la Factura asociada por estar Contabilizada.");
		}

		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
	
			HibernateUtil.beginTransaction(sessionName);
			
			completeInvoice(invoice);
			completeInvoiceDetail(invoice, posShift, ticketInfo);
			createInvoiceFinances(invoice);
	
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

	private Invoice createInvoice(PosShift posShift, Date issueDate, String shiftName, String comments) throws ManagerBeanException {
		Customer customer = obtainPosCustomer(posShift.getPos());
		Pos pos = posShift.getPos();

		Invoice invoice = new Invoice();
		invoice.setProject(null);
		invoice.setSeries(obtainPosInvoiceSeries(posShift.getPos()));
		invoice.setNumber(obtainSeriesMaxNumber(invoice.getSeries()));
		invoice.setRegistry(customer.getRegistry());
		invoice.setRegistryDocument(customer.getRegistry().getDocument());
		invoice.setRegistryDocumentType(customer.getRegistry().getDocumentType());
		invoice.setRegistryDocumentCountry(customer.getRegistry().getDocumentCountry());
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    	String date = StringUtils.rightPad(formatter.format(issueDate), 11);
		invoice.setRegistryName(pos.getWorkPlace().getDescription().concat(" - ").concat(pos.getName()).concat(" - ").concat(date).concat(" - ").concat(shiftName));
		invoice.setRegistryAddress(null);
		invoice.setIssueDate(issueDate);
		invoice.setSecurityLevel(SecurityLevel.CONFIDENTIAL);
		invoice.setStatus(InvoiceStatus.PENDING);
		invoice.setType(InvoiceType.SALES);
		invoice.setScope(pos.getWorkPlace().getScope());
		invoice.setService(false);
		invoice.setComments(comments);
		invoice.setPosShift(posShift);

		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		return (Invoice)invoiceBean.insert(invoice);
	}

	private InvoiceDetail createInvoiceDetail(Invoice invoice, WorkPlace workPlace, Item item) throws ManagerBeanException {
		InvoiceDetail invoiceDetail = new InvoiceDetail();
		invoiceDetail.setInvoice(invoice);
		invoiceDetail.setProject(null);
		invoiceDetail.setLine(1);
		invoiceDetail.setItem(item);
		invoiceDetail.setDescription(obtainDetailDescription(invoice.getIssueDate(), null, item.getProduct().getName()));
		invoiceDetail.setQuantity(1);
		invoiceDetail.setPrice(0);
		invoiceDetail.setDiscountExpression(new DiscountExpression("0.0"));
		invoiceDetail.setSource(InvoiceSource.DIRECT_INVOICE);
		invoiceDetail.setTaxableBase(0);
		invoiceDetail.setWorkPlace(workPlace);

		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		return (InvoiceDetail)invoiceDetailBean.insert(invoiceDetail);
	}

	private void completeInvoice(Invoice invoice) throws ManagerBeanException {
		invoice.setSecurityLevel(SecurityLevel.OFFICIAL);

		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		invoiceBean.update(invoice);
	}

	private void completeInvoiceDetail(Invoice invoice, PosShift posShift, String ticketInfo) throws ManagerBeanException {
		List<ITransferObject> detailList = invoice.getDetailList();
		InvoiceDetail invoiceDetail = null;
		if (detailList.size() > 0) {
			invoiceDetail = (InvoiceDetail)detailList.get(0);
		}
		if (invoiceDetail == null) {
			invoiceDetail = createInvoiceDetail(invoice, posShift.getPos().getWorkPlace(), posShift.getPos().getItemInvoice());
		}

		ItemPricesManager pricesManager = new ItemPricesManager();
		double taxableBase = pricesManager.getPrice(invoiceDetail.getItem(), posShift.getTotalCountAmount(), 2);

		invoiceDetail.setDescription(invoiceDetail.getDescription() + " " + ticketInfo);
		invoiceDetail.setQuantity(1);
		invoiceDetail.setPrice(taxableBase);
		invoiceDetail.setTaxableBase(taxableBase);

		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		invoiceDetailBean.update(invoiceDetail);
	}

	private void createInvoiceFinances(Invoice invoice) throws ManagerBeanException {
		double initialAmount = invoice.getPosShift().getInitialAmount();
		IManagerBean posShiftCountBean = BeanManager.getManagerBean(PosShiftCount.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(posShiftCountBean.getFieldName(IEntityAlias.POS_SHIFT_COUNT_POS_SHIFT_ID), invoice.getPosShift().getId());
		for (ITransferObject ito : posShiftCountBean.getList(criteria)) {
			PosShiftCount posShiftCount = (PosShiftCount)ito;

			Finance finance = new Finance();
			finance.setInvoice(invoice);
			finance.setRegistry(invoice.getRegistry());
			finance.setPayment(false);
			finance.setDueDate(invoice.getIssueDate());
			finance.setScope(invoice.getScope());
			finance.setFinanceStatus(FinanceStatus.PENDING);
			finance.setPayMethod(posShiftCount.getPayMethod());
			if (finance.getPayMethod().getType() == PayMethodType.CASH_BASIS) {
				double amount = CommonUtil.round(posShiftCount.getAmount() - initialAmount);
				initialAmount = 0;
				finance.setAmount(amount);
			} else {
				finance.setAmount(posShiftCount.getAmount());
			}
			finance.setSkipCheckPosShift(true);

			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			financeBean.insert(finance);
		}
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
	
	private String obtainDetailDescription(Date effectiveDate, String room, String description) {
    	DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    	String date = StringUtils.rightPad(formatter.format(effectiveDate), 11);
    	room = (room == null) ? StringUtils.rightPad(StringUtils.repeat("-", 5), 6) : StringUtils.rightPad(room, 6);
    	return (date + room + description);
	}

	private Invoice obtainPosShiftInvoice(PosShift posShift) throws ManagerBeanException {
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_POS_SHIFT_ID) , posShift.getId());
		criteria.addOrder(invoiceBean.getFieldName(IEntityAlias.INVOICE_ID));
		for (ITransferObject ito : invoiceBean.getList(criteria)) {
			return (Invoice)ito;
		}
		return null;
	}

}
