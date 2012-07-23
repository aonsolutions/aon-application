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
import com.code.aon.finance.enumeration.RectificationType;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.IAddress;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryBank;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationGuest;
import com.esferalia.aon.pms.reservation.ReservationUtils;

public class NoShowInvoicing {

	private static final Logger LOGGER = LoggerFactory.getLogger(NoShowInvoicing.class.getName());

	public int invoice(NoShowInvoiceTo noShowInvoiceTo, List<Integer> reservations) throws ManagerBeanException {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);

			HibernateUtil.beginTransaction(sessionName);

			int count = 0;
			IManagerBean reservationBean = BeanManager.getManagerBean(ProjectReservation.class);
			for (Integer reservationId : reservations) {
				ProjectReservation reservation = (ProjectReservation)reservationBean.get(reservationId);
				if (reservation.getHotelReservation().getItemNoShow() != null && reservation.getHotelReservation().getItemNoShow().getId() != null) {
					Invoice invoice = createNoShowInvoice(noShowInvoiceTo, reservation);
					double noShowAmount = createNoShowInvoiceDetails(invoice, reservation, noShowInvoiceTo);
					createNoShowInvoiceAddress(invoice, reservation);
					if (noShowAmount != 0) {
						createNoShowInvoiceFinances(invoice, noShowInvoiceTo, noShowAmount);
					}
					recordInvoice(invoice);
					++count;
				}
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

	private Invoice createNoShowInvoice(NoShowInvoiceTo noShowInvoiceTo, ProjectReservation reservation) throws ManagerBeanException {
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
		invoice.setIssueDate(noShowInvoiceTo.getIssueDate());
		invoice.setSecurityLevel(SecurityLevel.OFFICIAL);
		invoice.setStatus(InvoiceStatus.PENDING);
		invoice.setType(InvoiceType.SALES);
		invoice.setScope(reservation.getHotelReservation().getScope());

		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		return (Invoice)invoiceBean.insert(invoice);
	}

	private double createNoShowInvoiceDetails(Invoice invoice, ProjectReservation reservation, NoShowInvoiceTo noShowInvoiceTo) throws ManagerBeanException {
		ReservationUtils reservationUtils = new ReservationUtils();
		boolean taxDataInDetail = false;

		double advanceVatPercent = reservationUtils.getTaxPercentage(reservation.getHotelReservation().getItemAdvance().getVat(), invoice.getIssueDate());
		double advancedAmount = reservation.getAdvancedAmount();
		double advanceTaxableBase = CommonUtil.round(advancedAmount / (1 + advanceVatPercent / 100));

		double penaltyVatPercent = reservationUtils.getTaxPercentage(reservation.getHotelReservation().getItemNoShow().getProduct().getVat(), invoice.getIssueDate());
		double penaltyTaxableBase = (noShowInvoiceTo.isKeepAdvance()) ? CommonUtil.round(advancedAmount / (1 + penaltyVatPercent / 100)) : getPenaltyTaxableBase(reservation, noShowInvoiceTo.getPenaltyDays());
		double penaltyAmount = (noShowInvoiceTo.isKeepAdvance()) ? advancedAmount : CommonUtil.round(penaltyTaxableBase * (1 + penaltyVatPercent / 100));

		double invoiceTotal = CommonUtil.round(penaltyAmount - advancedAmount);
		if (advancedAmount > 0) {
			taxDataInDetail = (invoiceTotal != CommonUtil.round((penaltyTaxableBase - advanceTaxableBase) * (1 + penaltyVatPercent / 100)));

			InvoiceDetail invoiceDetail = new InvoiceDetail();
			invoiceDetail.setInvoice(invoice);
			invoiceDetail.setProject(reservation.getProject());
			invoiceDetail.setLine(1);
			invoiceDetail.setItem(reservation.getHotelReservation().getItemAdvance());
			invoiceDetail.setDescription(obtainDetailDescription(reservation.getStartDate(), null, invoiceDetail.getItem().getProduct().getName()));
			invoiceDetail.setQuantity(-1);
			invoiceDetail.setPrice(advanceTaxableBase);
			invoiceDetail.setDiscountExpression(new DiscountExpression("0.0"));
			invoiceDetail.setSource(InvoiceSource.DIRECT_INVOICE);
			invoiceDetail.setTaxableBase(advanceTaxableBase * (-1));
			invoiceDetail.setWorkPlace(reservation.getHotelReservation().getWorkPlace());
			if (taxDataInDetail) {
				invoiceDetail.setTaxDataInDetail(true);
				invoiceDetail.setVatPercent(advanceVatPercent);
				invoiceDetail.setVatQuota(CommonUtil.round((advancedAmount - advanceTaxableBase) * (-1)));
			}

			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			invoiceDetailBean.insert(invoiceDetail);
		}

		InvoiceDetail invoiceDetail = new InvoiceDetail();
		invoiceDetail.setInvoice(invoice);
		invoiceDetail.setProject(reservation.getProject());
		invoiceDetail.setLine((advancedAmount <= 0) ? 1 : 2);
		invoiceDetail.setItem(reservation.getHotelReservation().getItemNoShow());
		invoiceDetail.setDescription(obtainDetailDescription(reservation.getStartDate(), null, invoiceDetail.getItem().getProduct().getName()));
		invoiceDetail.setQuantity(1);
		invoiceDetail.setPrice(penaltyTaxableBase);
		invoiceDetail.setDiscountExpression(new DiscountExpression("0.0"));
		invoiceDetail.setSource(InvoiceSource.DIRECT_INVOICE);
		invoiceDetail.setTaxableBase(penaltyTaxableBase);
		invoiceDetail.setWorkPlace(reservation.getHotelReservation().getWorkPlace());
		if (taxDataInDetail) {
			invoiceDetail.setTaxDataInDetail(true);
			invoiceDetail.setVatPercent(penaltyVatPercent);
			invoiceDetail.setVatQuota(CommonUtil.round((penaltyAmount - penaltyTaxableBase)));
		}

		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		invoiceDetailBean.insert(invoiceDetail);

		if (advancedAmount > 0 && invoiceTotal != 0) {
			Finance finance = getAdvancedFinance(reservation);
			if (finance != null) {
				noShowInvoiceTo.setPayMethod(finance.getPayMethod());
				if (finance.getBankAccount() != null && StringUtils.isNotEmpty(finance.getBankAccount().toString())) {
					RegistryBank registryBank = new RegistryBank();
					registryBank.setBank(finance.getBank());
					registryBank.setBankAccount(finance.getBankAccount());
					noShowInvoiceTo.setRegistryBank(registryBank);
				}
			}
		}

		return invoiceTotal;
	}

	private void createNoShowInvoiceAddress(Invoice invoice, ProjectReservation reservation) throws ManagerBeanException {
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

		if (address != null && !isEmptyAddress(address)) {
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

	private void createNoShowInvoiceFinances(Invoice invoice, NoShowInvoiceTo noShowInvoiceTo, double noShowAmount) throws ManagerBeanException {
		Finance finance = new Finance();
		finance.setInvoice(invoice);
		finance.setRegistry(invoice.getRegistry());
		finance.setPayment(false);
		finance.setDueDate(noShowInvoiceTo.getFinanceDate());
		finance.setScope(invoice.getScope());
		finance.setFinanceStatus(FinanceStatus.PENDING);
		finance.setPayMethod(noShowInvoiceTo.getPayMethod());
		if (noShowInvoiceTo.getRegistryBank() != null) {
			finance.setBank(noShowInvoiceTo.getRegistryBank().getBank());
			finance.setBankAccount(noShowInvoiceTo.getRegistryBank().getBankAccount());
		}
		finance.setAmount(noShowAmount);

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

	private Registry obtainRegistry(ProjectReservation reservation) throws ManagerBeanException {
		if (reservation.isGuestHolder()) {
			Registry registry = new Registry();
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

	private double getPenaltyTaxableBase(ProjectReservation reservation, int penaltyDays) throws ManagerBeanException {
		switch (penaltyDays) {
			case 1:
				return reservation.getOneNightNoShowTaxableBase();
			case 2:
				return reservation.getTwoNightNoShowTaxableBase();
			default:
				return 0;
		}
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

	private Finance getAdvancedFinance(ProjectReservation reservation) throws ManagerBeanException {
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_PROJECT_ID), reservation.getId());
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_TYPE), InvoiceType.SALES);
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_SERVICE), false);
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_ADVANCE), true);
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_RECTIFICATION_TYPE), RectificationType.NONE);
		for (ITransferObject ito : financeBean.getList(criteria)) {
			return (Finance)ito;
		}
		return null;
	}

}
