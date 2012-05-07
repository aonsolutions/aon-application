package com.esferalia.aon.pms.invoicing;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
import com.code.aon.config.Series;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.product.Item;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationGuest;

public class AdvanceInvoicingManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(AdvanceInvoicingManager.class.getName());
	
	public int invoice(AdvanceInvoiceTo advanceInvoiceTo, List<Integer> reservations) throws ManagerBeanException {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);

			HibernateUtil.beginTransaction(sessionName);
			IManagerBean bean = BeanManager.getManagerBean(ProjectReservation.class);
			int count = 0;
			for (Integer reservationId : reservations) {
				ProjectReservation reservation = (ProjectReservation) bean.get(reservationId);
				Invoice invoice = createAdvanceInvoice(advanceInvoiceTo, reservation);
				double total = createAdvanceInvoiceDetails(invoice, reservation, advanceInvoiceTo);
				createAdvanceInvoiceFinances(invoice, advanceInvoiceTo, total);
				recordInvoice(invoice);
				++count;
			}
			
			HibernateUtil.getSession(sessionName).flush();
			HibernateUtil.commitTransaction(sessionName);

			return count;
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

	private Invoice createAdvanceInvoice(AdvanceInvoiceTo advanceInvoiceTo, ProjectReservation reservation) throws ManagerBeanException {
		Invoice invoice = new Invoice();
		invoice.setProject(reservation.getProject());
		Series series = getHotelSeries(reservation);
		if (series == null || series.getId() == null) {
			throw new ManagerBeanException("El hotel +'"+ reservation.getHotel().getWorkPlace().getDescription() +"' no tiene serie de facturación definida.");
		}
		invoice.setSeries( series.getCode() );
		invoice.setNumber(obtainSeriesMaxNumber(series.getCode()));
		Registry registry;
		if (reservation.isGuestHolder()) {
			registry = reservation.getHotel().getCustomer().getRegistry();
			invoice.setRegistry(registry);
			for (ProjectReservationGuest guest :  reservation.getGuests() ) {
				invoice.setRegistryDocument(guest.getDocument() );
				invoice.setRegistryDocumentType(guest.getDocumentType());
				invoice.setRegistryDocumentCountry(guest.getDocumentCountry());
				invoice.setRegistryName(guest.getFullName());
				break;
			}
		} else {
			registry = reservation.getAgency().getRegistry();
			invoice.setRegistry(registry);
			invoice.setRegistryDocument(registry.getDocument());
			invoice.setRegistryDocumentType(registry.getDocumentType());
			invoice.setRegistryDocumentCountry(registry.getDocumentCountry());
			invoice.setRegistryName(registry.getName());
		}
		invoice.setRegistryAddress(null);
		invoice.setIssueDate(advanceInvoiceTo.getIssueDate());
		invoice.setSecurityLevel(SecurityLevel.OFFICIAL);
		invoice.setStatus(InvoiceStatus.PENDING);
		invoice.setType(InvoiceType.SALES);
		invoice.setScope(reservation.getHotel().getScope());
		
		invoice.setAdvance(true);

		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		return (Invoice)invoiceBean.insert(invoice);
	}
	
	public Series getHotelSeries(ProjectReservation reservation) throws ManagerBeanException {
		IManagerBean seriesBean = BeanManager.getManagerBean(Series.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_SCOPE_ID), reservation.getHotel().getScope().getId());
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_ACTIVE), new Boolean(true));
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_SECURITY_LEVEL), SecurityLevel.OFFICIAL);
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_INVOICE), new Boolean(true));
		for (ITransferObject ito : seriesBean.getList(criteria)) {
			Series series = (Series) ito;
			return series;
		}
		return null;
	}

	private int obtainSeriesMaxNumber(String seriesId) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression("invoice.type", InvoiceType.SALES.ordinal());
		return SeriesNumberUtil.obtainNumber(seriesId, "Invoice", criteria);
	}
	
	private double createAdvanceInvoiceDetails(Invoice invoice, ProjectReservation reservation,AdvanceInvoiceTo advanceInvoiceTo) throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		double total = 0.0;
		if (advanceInvoiceTo.isGuestReservation()) {
			total = reservation.getAdvance();
		} else {
			total = CommonUtil.round(reservation.getTotal() * advanceInvoiceTo.getPercent() / 100);	
		}
		Item item = reservation.getHotel().getItemAdvance();
		if (item == null || item.getId() == null) {
			throw new ManagerBeanException("El hotel +'"+ reservation.getHotel().getWorkPlace().getDescription() +"' no tiene artículo de anticipos definido.");
		}
		double vatPercent = item.getVat().getPercentage();
		double taxableBase = CommonUtil.round( (total * 100 ) / (vatPercent + 100));
		double vatQuota = CommonUtil.round(total - taxableBase);
		
		InvoiceDetail invoiceDetail = new InvoiceDetail();
		invoiceDetail.setInvoice(invoice);
		invoiceDetail.setProject(reservation.getProject());
		invoiceDetail.setLine(1);
		invoiceDetail.setItem(item);
		invoiceDetail.setDescription(obtainDetailDescription(reservation.getStartDate(),null,item.getProduct().getName()));
		invoiceDetail.setQuantity(1);
		invoiceDetail.setPrice(taxableBase);
		invoiceDetail.setDiscountExpression(new DiscountExpression("0.0"));
		invoiceDetail.setSource(InvoiceSource.DIRECT_INVOICE);
		invoiceDetail.setSourceId( null );
		invoiceDetail.setTaxableBase(taxableBase);
		invoiceDetail.setWorkPlace(reservation.getHotel().getWorkPlace());
		
		invoiceDetail.setTaxDataInDetail(true);
		invoiceDetail.setVatPercent(vatPercent);
		invoiceDetail.setVatQuota(vatQuota);
		
		invoiceDetailBean.insert(invoiceDetail);
		return total;
	}

	private String obtainDetailDescription(Date effectiveDate, String room, String description) {
    	DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    	String date = StringUtils.rightPad(formatter.format(effectiveDate), 11);
    	room = (room == null) ? StringUtils.rightPad(StringUtils.repeat("-", 5), 6) : StringUtils.rightPad(room, 6);
    	return (date + room + description);
	}
	
	private void createAdvanceInvoiceFinances(Invoice invoice, AdvanceInvoiceTo advanceInvoiceTo, double total) throws ManagerBeanException {
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Finance finance = new Finance();
		finance.setInvoice(invoice);
		finance.setRegistry(invoice.getRegistry());
		finance.setRegistryDocument(invoice.getRegistryDocument());
		finance.setRegistryDocumentType(invoice.getRegistryDocumentType());
		finance.setRegistryDocumentCountry(invoice.getRegistryDocumentCountry());
		finance.setRegistryName(invoice.getRegistryName());
		finance.setSecurityLevel(invoice.getSecurityLevel());
		finance.setScope(invoice.getScope());
		finance.setPayMethod( advanceInvoiceTo.getPayMethod() );
		if (advanceInvoiceTo.getRbank() != null) {
			finance.setBank(advanceInvoiceTo.getRbank().getBank());
			finance.setBankAccount(advanceInvoiceTo.getRbank().getBankAccount());
		}
		finance.setPayment(false);
		finance.setDueDate(advanceInvoiceTo.getFinanceDate());
		finance.setFinanceStatus(FinanceStatus.PENDING);
		finance.setAmount(total);
		
		financeBean.insert(finance);
	}
	
	private void recordInvoice(Invoice invoice) throws ManagerBeanException {
		AccountEntryInvoiceWriter entryWriter = new AccountEntryInvoiceWriter();
		entryWriter.recordAndUpdateInvoice(invoice);
	}
	
}
