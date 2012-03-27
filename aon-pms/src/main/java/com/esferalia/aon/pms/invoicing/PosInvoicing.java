package com.esferalia.aon.pms.invoicing;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.bridge.writer.AccountEntryInvoiceWriter;
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
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.product.Item;
import com.code.aon.product.pricing.ItemPricesManager;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.PosShift;
import com.esferalia.aon.pms.PosShiftCount;
import com.esferalia.aon.pms.reservation.IReservationConstants;

public class PosInvoicing implements IReservationConstants {

	private static final Logger LOGGER = LoggerFactory.getLogger(PosInvoicing.class.getName());
	
	public void createInvoice(PosShift posShift, String comments) throws ManagerBeanException {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			Hotel hotel = obtainHotel(posShift.getPos().getWorkPlace());
			if (hotel != null) {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);

				HibernateUtil.beginTransaction(sessionName);

				Invoice invoice = createInvoice(hotel, posShift.getStartTime(), posShift.getPos().getName(), comments);
				createInvoiceDetail(invoice, hotel, posShift.getPos().getItem());
				savePosInvoice(invoice, posShift);

				HibernateUtil.getSession(sessionName).flush();
				HibernateUtil.commitTransaction(sessionName);
			}
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

	public void completeInvoice(PosShift posShift) throws ManagerBeanException {
		if (posShift.getInvoice() == null) {
			throw new ManagerBeanException("El Turno no tiene Factura asociada.");
		}
		if (posShift.getInvoice().isRecorded()) {
			throw new ManagerBeanException("La Factura ya esta Contabilizada.");
		}

		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
	
			HibernateUtil.beginTransaction(sessionName);
			
			IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
			Invoice invoice = (Invoice) invoiceBean.get(posShift.getInvoice().getId());
			if (invoice != null) {
				if (StringUtils.isNotEmpty(posShift.getRemarks())) {
					completeInvoice(invoice, posShift.getRemarks());
				}
				completeInvoiceDetail(invoice, posShift.getFinalAmount() - posShift.getInitialAmount());
				createInvoiceFinances(invoice, posShift);
				recordInvoice(invoice);
			}
	
			HibernateUtil.getSession(sessionName).flush();
			HibernateUtil.commitTransaction(sessionName);
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

	private Invoice createInvoice(Hotel hotel, Date issueDate, String posName, String comments) throws ManagerBeanException {
		Invoice invoice = new Invoice();
		invoice.setProject(null);
		invoice.setSeries(obtainHotelInvoiceSeries(hotel));
		invoice.setNumber(obtainSeriesMaxNumber(invoice.getSeries()));
		invoice.setRegistry(hotel.getCustomer().getRegistry());
		invoice.setRegistryDocument(hotel.getCustomer().getRegistry().getDocument());
		invoice.setRegistryDocumentType(hotel.getCustomer().getRegistry().getDocumentType());
		invoice.setRegistryDocumentCountry(hotel.getCustomer().getRegistry().getDocumentCountry());
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    	String date = StringUtils.rightPad(formatter.format(issueDate), 11);
		invoice.setRegistryName(hotel.getWorkPlace().getDescription().concat(" - ").concat(posName).concat(" - ").concat(date).concat(" - ").concat(comments));
		invoice.setRegistryAddress(null);
		invoice.setIssueDate(issueDate);
		invoice.setSecurityLevel(SecurityLevel.OFFICIAL);
		invoice.setStatus(InvoiceStatus.PENDING);
		invoice.setType(InvoiceType.SALES);
		invoice.setScope(hotel.getScope());
		invoice.setService(false);
		invoice.setComments(comments);

		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		return (Invoice)invoiceBean.insert(invoice);
	}

	private void completeInvoice(Invoice invoice, String remarks) throws ManagerBeanException {
		invoice.setRemarks(remarks);

		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		invoiceBean.update(invoice);
	}

	private void createInvoiceDetail(Invoice invoice, Hotel hotel, Item item) throws ManagerBeanException {
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
		invoiceDetail.setWorkPlace(hotel.getWorkPlace());

		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		invoiceDetailBean.insert(invoiceDetail);
	}

	private void completeInvoiceDetail(Invoice invoice, double total) throws ManagerBeanException {
		InvoiceDetail invoiceDetail = (InvoiceDetail)invoice.getDetailList().get(0);
		if (invoiceDetail != null) {
			ItemPricesManager pricesManager = new ItemPricesManager();
			double taxableBase = pricesManager.getPrice(invoiceDetail.getItem(), total, 2);

			invoiceDetail.setQuantity(1);
			invoiceDetail.setPrice(taxableBase);
			invoiceDetail.setTaxableBase(taxableBase);

			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			invoiceDetailBean.update(invoiceDetail);
		}
	}

	private void savePosInvoice(Invoice invoice, PosShift posShift) throws ManagerBeanException {
		posShift.setInvoice(invoice);

		IManagerBean posShiftBean = BeanManager.getManagerBean(PosShift.class);
		posShiftBean.update(posShift);
	}

	private void createInvoiceFinances(Invoice invoice, PosShift posShift) throws ManagerBeanException {
		double initialAmount = posShift.getInitialAmount();
		IManagerBean posShiftCountBean = BeanManager.getManagerBean(PosShiftCount.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(posShiftCountBean.getFieldName(IEntityAlias.POS_SHIFT_COUNT_POS_SHIFT_ID), posShift.getId());
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

			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			financeBean.insert(finance);
		}
	}

	private void recordInvoice(Invoice invoice) throws ManagerBeanException {
		AccountEntryInvoiceWriter entryWriter = new AccountEntryInvoiceWriter();
		entryWriter.recordAndUpdateInvoice(invoice);
	}

	private Hotel obtainHotel(WorkPlace workPlace) throws ManagerBeanException {
		IManagerBean hotelBean = BeanManager.getManagerBean(Hotel.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(hotelBean.getFieldName(IEntityAlias.HOTEL_WORK_PLACE_ID), workPlace.getId());
		for (ITransferObject ito : hotelBean.getList(criteria)) {
			return ((Hotel)ito);
		}
		return null;
	}

	private String obtainHotelInvoiceSeries(Hotel hotel) throws ManagerBeanException {
		String series = "";
		IManagerBean seriesBean = BeanManager.getManagerBean(Series.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_SCOPE_ID), hotel.getScope().getId());
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_ACTIVE), new Boolean(true));
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_SECURITY_LEVEL), SecurityLevel.OFFICIAL);
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_INVOICE), new Boolean(true));
		for (ITransferObject ito : seriesBean.getList(criteria)) {
			series = ((Series)ito).getCode();
		}
		return series;
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

}
