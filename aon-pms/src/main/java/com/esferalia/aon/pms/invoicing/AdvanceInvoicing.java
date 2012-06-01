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
import com.code.aon.finance.InvoiceAddress;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.product.Item;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.IAddress;
import com.code.aon.registry.Registry;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationGuest;

public class AdvanceInvoicing {

	private static final Logger LOGGER = LoggerFactory.getLogger(AdvanceInvoicing.class.getName());

	public int invoice(AdvanceInvoiceTo advanceInvoiceTo, List<Integer> reservations) throws ManagerBeanException {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);

			HibernateUtil.beginTransaction(sessionName);

			IManagerBean reservationBean = BeanManager.getManagerBean(ProjectReservation.class);
			int count = 0;
			for (Integer reservationId : reservations) {
				ProjectReservation reservation = (ProjectReservation)reservationBean.get(reservationId);
				Invoice invoice = createAdvanceInvoice(advanceInvoiceTo, reservation);
				double total = createAdvanceInvoiceDetails(invoice, reservation, advanceInvoiceTo.getPercent());
				createInvoiceAddress(invoice, reservation);
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
		invoice.setSeries(getHotelSeries(reservation));
		invoice.setNumber(obtainSeriesMaxNumber(invoice.getSeries()));
		invoice.setRegistry(obtainRegistry(reservation));
		invoice.setRegistryDocument(invoice.getRegistry().getDocument());
		invoice.setRegistryDocumentType(invoice.getRegistry().getDocumentType());
		invoice.setRegistryDocumentCountry(invoice.getRegistry().getDocumentCountry());
		invoice.setRegistryName(invoice.getRegistry().getName());
		invoice.setRegistryAddress(null);
		invoice.setIssueDate(advanceInvoiceTo.getIssueDate());
		invoice.setSecurityLevel(SecurityLevel.OFFICIAL);
		invoice.setStatus(InvoiceStatus.PENDING);
		invoice.setType(InvoiceType.SALES);
		invoice.setScope(reservation.getHotelReservation().getScope());
		invoice.setAdvance(true);

		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		return (Invoice)invoiceBean.insert(invoice);
	}

	private double createAdvanceInvoiceDetails(Invoice invoice, ProjectReservation reservation, double advancePercent) throws ManagerBeanException {
		Item item = reservation.getHotelReservation().getItemAdvance();
		double total = (reservation.isGuestHolder()) ? reservation.getAdvance() : CommonUtil.round(reservation.getTotal() * advancePercent / 100);
		double taxableBase = CommonUtil.round(total / (1 + item.getVat().getPercentage() / 100));

		InvoiceDetail invoiceDetail = new InvoiceDetail();
		invoiceDetail.setInvoice(invoice);
		invoiceDetail.setProject(reservation.getProject());
		invoiceDetail.setLine(1);
		invoiceDetail.setItem(item);
		invoiceDetail.setDescription(obtainDetailDescription(reservation.getStartDate(), null, item.getProduct().getName()));
		invoiceDetail.setQuantity(1);
		invoiceDetail.setPrice(taxableBase);
		invoiceDetail.setDiscountExpression(new DiscountExpression("0.0"));
		invoiceDetail.setSource(InvoiceSource.DIRECT_INVOICE);
		invoiceDetail.setTaxableBase(taxableBase);
		invoiceDetail.setWorkPlace(reservation.getHotelReservation().getWorkPlace());
		invoiceDetail.setTaxDataInDetail(true);
		invoiceDetail.setVatPercent(item.getVat().getPercentage());
		invoiceDetail.setVatQuota(CommonUtil.round(total - taxableBase));

		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		invoiceDetailBean.insert(invoiceDetail);

		return total;
	}

	private void createInvoiceAddress(Invoice invoice, ProjectReservation reservation) throws ManagerBeanException {
		IAddress address = null;
		if (reservation.isGuestHolder()) {
			ProjectReservationGuest reservationGuest = obtainMainGuest(reservation);
			address = new InvoiceAddress();
			address.setAddress(StringUtils.abbreviate(reservationGuest.getAddress(), 45));
			address.setZip(StringUtils.abbreviate(reservationGuest.getZip(), 16));
			address.setCity(StringUtils.abbreviate(reservationGuest.getCity(), 45));
			address.setProvince(StringUtils.abbreviate(reservationGuest.getProvince(), 45));
		} else {
			address = invoice.getRegistry().getDefaultAddress();
		}

		if (address != null && StringUtils.isNotEmpty(address.getAddress())) {
			InvoiceAddress invoiceAddress = new InvoiceAddress();
			invoiceAddress.setInvoice(invoice);
			invoiceAddress.setStreetType(address.getStreetType()); 
			invoiceAddress.setAddress(address.getAddress());
			invoiceAddress.setAddress2(address.getAddress2());
			invoiceAddress.setNumber(address.getNumber()); 
			invoiceAddress.setZip(address.getZip()); 
			invoiceAddress.setCity(address.getCity()); 
			invoiceAddress.setGeozone(address.getGeozone()); 

			IManagerBean invoiceAddressBean = BeanManager.getManagerBean(InvoiceAddress.class);
			invoiceAddressBean.insert(invoiceAddress);
		}
	}

	private void createAdvanceInvoiceFinances(Invoice invoice, AdvanceInvoiceTo advanceInvoiceTo, double total) throws ManagerBeanException {
		Finance finance = new Finance();
		finance.setInvoice(invoice);
		finance.setRegistry(invoice.getRegistry());
		finance.setRegistryDocument(invoice.getRegistryDocument());
		finance.setRegistryDocumentType(invoice.getRegistryDocumentType());
		finance.setRegistryDocumentCountry(invoice.getRegistryDocumentCountry());
		finance.setRegistryName(invoice.getRegistryName());
		finance.setPayment(false);
		finance.setDueDate(advanceInvoiceTo.getFinanceDate());
		finance.setAmount(total);
		finance.setSecurityLevel(invoice.getSecurityLevel());
		finance.setScope(invoice.getScope());
		finance.setFinanceStatus(FinanceStatus.PENDING);
		finance.setPayMethod(advanceInvoiceTo.getPayMethod());
		if (advanceInvoiceTo.getRegistryBank() != null) {
			finance.setBank(advanceInvoiceTo.getRegistryBank().getBank());
			finance.setBankAccount(advanceInvoiceTo.getRegistryBank().getBankAccount());
		}

		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		financeBean.insert(finance);
	}

	private void recordInvoice(Invoice invoice) throws ManagerBeanException {
		AccountEntryInvoiceWriter entryWriter = new AccountEntryInvoiceWriter();
		entryWriter.recordAndUpdateInvoice(invoice);
	}

	private String getHotelSeries(ProjectReservation reservation) throws ManagerBeanException {
		IManagerBean seriesBean = BeanManager.getManagerBean(Series.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_SCOPE_ID), reservation.getHotelReservation().getScope().getId());
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_ACTIVE), new Boolean(true));
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_SECURITY_LEVEL), SecurityLevel.OFFICIAL);
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_INVOICE), new Boolean(true));
		for (ITransferObject ito : seriesBean.getList(criteria)) {
			Series series = (Series) ito;
			return series.getCode();
		}
		return null;
	}

	private int obtainSeriesMaxNumber(String seriesId) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression("invoice.type", InvoiceType.SALES.ordinal());
		return SeriesNumberUtil.obtainNumber(seriesId, "Invoice", criteria);
	}

	private Registry obtainRegistry(ProjectReservation reservation) throws ManagerBeanException {
		if (reservation.isGuestHolder()) {
			Registry registry = new Registry();
			registry.setId(reservation.getHotelReservation().getCustomer().getRegistry().getId());
			ProjectReservationGuest reservationGuest = obtainMainGuest(reservation);
			if (reservationGuest != null) {
				registry.setDocument(reservationGuest.getDocument());
				registry.setDocumentType(reservationGuest.getDocumentType());
				registry.setDocumentCountry(reservationGuest.getDocumentCountry());
				registry.setName(reservationGuest.getFullName());
			} else {
				registry.setDocument(reservation.getHotelReservation().getCustomer().getRegistry().getDocument());
				registry.setDocumentType(reservation.getHotelReservation().getCustomer().getRegistry().getDocumentType());
				registry.setDocumentCountry(reservation.getHotelReservation().getCustomer().getRegistry().getDocumentCountry());
				registry.setName(reservation.getHotelReservation().getCustomer().getRegistry().getFullName());
			}
			return registry;
		} else {
			return (reservation.isAgencyHolder()) ? reservation.getAgency().getRegistry() : reservation.getCompany().getRegistry();
		}
	}

	private ProjectReservationGuest obtainMainGuest(ProjectReservation reservation)  throws ManagerBeanException {
		IManagerBean reservationGuestBean = BeanManager.getManagerBean(ProjectReservationGuest.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(reservationGuestBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_GUEST_PROJECT_RESERVATION_ID), reservation.getId());
		criteria.addEqualExpression(reservationGuestBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_GUEST_GUEST_INDEX), 1);
		for (ITransferObject ito : reservationGuestBean.getList(criteria)) {
			return (ProjectReservationGuest)ito;
		}
		return null;
	}

	private String obtainDetailDescription(Date effectiveDate, String room, String description) {
    	DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    	String date = StringUtils.rightPad(formatter.format(effectiveDate), 11);
    	room = (room == null) ? StringUtils.rightPad(StringUtils.repeat("-", 5), 6) : StringUtils.rightPad(room, 6);
    	return (date + room + description);
	}

}
