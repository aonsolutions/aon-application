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
import com.code.aon.config.PayMethod;
import com.code.aon.config.Series;
import com.code.aon.config.enumeration.PayMethodType;
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
import com.code.aon.registry.RegistryBank;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationGuest;

public class AdvanceInvoicing {

	private static final Logger LOGGER = LoggerFactory.getLogger(AdvanceInvoicing.class.getName());

	public int invoice(AdvanceInvoiceTo advanceInvoiceTo, List<Integer> reservations) throws ManagerBeanException {
		try {
			int count = 0;
			IManagerBean reservationBean = BeanManager.getManagerBean(ProjectReservation.class);
			for (Integer reservationId : reservations) {
				ProjectReservation reservation = (ProjectReservation)reservationBean.get(reservationId);
				if (invoice(advanceInvoiceTo, reservation) != null) {
					++count;
				}
			}
			return count;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new ManagerBeanException(e.getMessage(),e);
		}
	}

	public Invoice invoice(AdvanceInvoiceTo advanceInvoiceTo, ProjectReservation reservation) throws ManagerBeanException {
		if (advanceInvoiceTo.getItem() != null) {
			boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
			boolean mustCloseSession = HibernateUtil.mustCloseSession();
			String sessionName = HibernateUtil.getSessionFactoryName();
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);

				HibernateUtil.beginTransaction(sessionName);

				Invoice invoice = null;
				double advanceAmount = getAdvanceAmount(reservation, advanceInvoiceTo);
				if (advanceAmount > 0) {
					invoice = createInvoice(advanceInvoiceTo, reservation);
					createInvoiceDetails(invoice, reservation, advanceInvoiceTo.getItem(), advanceAmount);
					createInvoiceAddress(invoice, advanceInvoiceTo.getReservationInvoiceTo(), reservation);
					if (advanceAmount != 0) {
						createInvoiceFinances(invoice, reservation, advanceInvoiceTo, advanceAmount);
					}
					recordInvoice(invoice);
				}

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
		return null;
	}

	private double getAdvanceAmount(ProjectReservation reservation, AdvanceInvoiceTo advanceInvoiceTo) throws ManagerBeanException {
		double invoiceAmount = 0;
		double pendingAmount = reservation.getPendingAmount();
		if (pendingAmount > 0) {
			if (advanceInvoiceTo.getAmount() == 0) {
				double advancePercent = advanceInvoiceTo.getPercent();
				invoiceAmount = (reservation.isGuestHolder()) ? reservation.getAdvance() : CommonUtil.round(reservation.getTotal() * advancePercent / 100);
			} else {
				invoiceAmount = advanceInvoiceTo.getAmount();
			}
			invoiceAmount = (pendingAmount > invoiceAmount) ? invoiceAmount : pendingAmount;
		}
		return invoiceAmount;
	}

	private Invoice createInvoice(AdvanceInvoiceTo advanceInvoiceTo, ProjectReservation reservation) throws ManagerBeanException {
		Invoice invoice = new Invoice();
		invoice.setProject(reservation.getProject());
		invoice.setSeries(getHotelSeries(reservation));
		invoice.setNumber(obtainSeriesMaxNumber(invoice.getSeries()));
		invoice.setRegistry(obtainRegistry(advanceInvoiceTo.getReservationInvoiceTo(), reservation));
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
		invoice.setPosShift(advanceInvoiceTo.getPosShift());

		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		return (Invoice)invoiceBean.insert(invoice);
	}

	private void createInvoiceDetails(Invoice invoice, ProjectReservation reservation, Item item, double amount) throws ManagerBeanException {
		double vatPercent = item.getVat().getDatedPercentage(invoice.getIssueDate());
		double taxableBase = CommonUtil.round(amount / (1 + vatPercent / 100));

		InvoiceDetail invoiceDetail = new InvoiceDetail();
		invoiceDetail.setInvoice(invoice);
		invoiceDetail.setProject(reservation.getProject());
		invoiceDetail.setLine(1);
		invoiceDetail.setItem(item);
		invoiceDetail.setDescription(obtainDetailDescription(reservation.getStartDate(), null, invoiceDetail.getItem().getFullName()));
		invoiceDetail.setQuantity(1);
		invoiceDetail.setPrice(taxableBase);
		invoiceDetail.setDiscountExpression(new DiscountExpression("0.0"));
		invoiceDetail.setSource(InvoiceSource.DIRECT_INVOICE);
		invoiceDetail.setTaxableBase(taxableBase);
		invoiceDetail.setWorkPlace(reservation.getHotelReservation().getWorkPlace());
		invoiceDetail.setTaxDataInDetail(true);
		invoiceDetail.setVatPercent(vatPercent);
		invoiceDetail.setVatQuota(CommonUtil.round(amount - taxableBase));

		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		invoiceDetailBean.insert(invoiceDetail);
	}

	private void createInvoiceAddress(Invoice invoice, ReservationInvoiceTo reservationInvoiceTo, ProjectReservation reservation) throws ManagerBeanException {
		IAddress address = null;
		if (reservation.isGuestHolder()) {
			if (reservationInvoiceTo != null) {
				address = reservationInvoiceTo.getAddress();
			} else {
				ProjectReservationGuest reservationGuest = obtainMainGuest(reservation);
				address = new InvoiceAddress();
				address.setAddress(StringUtils.abbreviate(reservationGuest.getAddress(), 45));
				address.setNumber(StringUtils.abbreviate(reservationGuest.getNumber(), 12));
				address.setAddress2(StringUtils.abbreviate(reservationGuest.getAddress2(), 45));
				address.setZip(StringUtils.abbreviate(reservationGuest.getZip(), 16));
				address.setCity(StringUtils.abbreviate(reservationGuest.getCity(), 45));
				address.setProvince(StringUtils.abbreviate(reservationGuest.getProvince(), 45));
			}
		} else {
			address = invoice.getRegistry().getDefaultAddress();
		}

		if (address != null && !isEmptyAddress(address)) {
			InvoiceAddress invoiceAddress = new InvoiceAddress();
			invoiceAddress.setInvoice(invoice);
			invoiceAddress.setStreetType(address.getStreetType()); 
			invoiceAddress.setAddress(address.getAddress());
			invoiceAddress.setNumber(address.getNumber()); 
			invoiceAddress.setAddress2(address.getAddress2());
			invoiceAddress.setZip(address.getZip()); 
			invoiceAddress.setCity(address.getCity()); 
			invoiceAddress.setProvince(address.getProvince()); 
			invoiceAddress.setGeozone(address.getGeozone()); 

			IManagerBean invoiceAddressBean = BeanManager.getManagerBean(InvoiceAddress.class);
			invoiceAddressBean.insert(invoiceAddress);
		}
	}

	private void createInvoiceFinances(Invoice invoice, ProjectReservation reservation, AdvanceInvoiceTo advanceInvoiceTo, double advanceAmount) 
			throws ManagerBeanException {
		Finance finance = new Finance();
		finance.setInvoice(invoice);
		finance.setRegistry(invoice.getRegistry());
		finance.setPayment(false);
		finance.setDueDate(advanceInvoiceTo.getFinanceDate());
		finance.setScope(invoice.getScope());
		finance.setFinanceStatus(FinanceStatus.PENDING);
		finance.setPayMethod(obtainPayMethod(reservation, advanceInvoiceTo));
		if (advanceInvoiceTo.getRegistryBank() != null) {
			finance.setBankAccount(advanceInvoiceTo.getRegistryBank().getBankAccount());
			finance.setBankAlias(advanceInvoiceTo.getRegistryBank().getBankAlias());
			finance.setBic(advanceInvoiceTo.getRegistryBank().getBic());
		} else if (advanceInvoiceTo.getPayMethod().getType() == PayMethodType.NEGOTIABLE_DOCUMENT) {
			RegistryBank rBank = getRegistryBank(invoice.getRegistry());
			if (rBank != null) {
				finance.setBankAccount(rBank.getBankAccount());
				finance.setBankAlias(rBank.getBankAlias());
				finance.setBic(rBank.getBic());
			}
		}
		finance.setAmount(advanceAmount);

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
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_ACTIVE), true);
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_SECURITY_LEVEL), SecurityLevel.OFFICIAL);
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_INVOICE), true);
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

	private Registry obtainRegistry(ReservationInvoiceTo reservationInvoiceTo, ProjectReservation reservation) throws ManagerBeanException {
		if (reservation.isGuestHolder()) {
			Registry registry = new Registry();
			if (reservationInvoiceTo != null) {
				registry = reservationInvoiceTo.getRegistry();
			} else {
				registry.setId(reservation.getHotelReservation().getCustomer().getRegistry().getId());
				ProjectReservationGuest reservationGuest = obtainMainGuest(reservation);
				if (reservationGuest != null) {
					registry.setName(reservationGuest.getFullName());
					registry.setDocument(reservationGuest.getDocument());
					registry.setDocumentType(reservationGuest.getDocumentType());
					registry.setDocumentCountry(reservationGuest.getDocumentCountry());
				} else {
					registry.setName(reservation.getHotelReservation().getCustomer().getRegistry().getFullName());
					registry.setDocument(reservation.getHotelReservation().getCustomer().getRegistry().getDocument());
					registry.setDocumentType(reservation.getHotelReservation().getCustomer().getRegistry().getDocumentType());
					registry.setDocumentCountry(reservation.getHotelReservation().getCustomer().getRegistry().getDocumentCountry());
				}
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

	private boolean isEmptyAddress(IAddress address) {
		return (StringUtils.isEmpty(address.getAddress()) && StringUtils.isEmpty(address.getCity()) && StringUtils.isEmpty(address.getProvince()));
	}

	private PayMethod obtainPayMethod(ProjectReservation reservation, AdvanceInvoiceTo advanceInvoiceTo) {
		boolean conexFlow = reservation.isPrepay() || reservation.isConexFlowNotRefundable();
		return (conexFlow) ? advanceInvoiceTo.getConexFlowPayMethod() : advanceInvoiceTo.getPayMethod();
	}
	
	private RegistryBank getRegistryBank(Registry registry) throws ManagerBeanException {
		IManagerBean rBankBean = BeanManager.getManagerBean(RegistryBank.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rBankBean.getFieldName(IEntityAlias.REGISTRY_BANK_REGISTRY_ID), registry.getId());
		criteria.addEqualExpression(rBankBean.getFieldName(IEntityAlias.REGISTRY_BANK_ACTIVE), true);
		for (ITransferObject ito : rBankBean.getList(criteria)) {
			return (RegistryBank)ito; 
		}
		return null;
	}

}
