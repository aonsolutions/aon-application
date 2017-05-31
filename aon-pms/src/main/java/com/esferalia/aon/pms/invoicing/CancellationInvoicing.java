package com.esferalia.aon.pms.invoicing;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
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
import com.code.aon.finance.enumeration.RectificationType;
import com.code.aon.product.Item;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.IAddress;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryBank;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationGuest;
import com.esferalia.aon.pms.reservation.ReservationUtils;

public class CancellationInvoicing {

	private static final Logger LOGGER = LoggerFactory.getLogger(CancellationInvoicing.class.getName());

	public int invoice(CancellationInvoiceTo cancellationInvoiceTo, List<Integer> reservations) throws ManagerBeanException {
		try {
			int count = 0;
			IManagerBean reservationBean = BeanManager.getManagerBean(ProjectReservation.class);
			for (Integer reservationId : reservations) {
				ProjectReservation reservation = (ProjectReservation)reservationBean.get(reservationId);
				if (!reservation.isInvoiced() && invoice(cancellationInvoiceTo, reservation) != null) {
					++count;
				}
			}
			return count;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new ManagerBeanException(e.getMessage(),e);
		}
	}

	public Invoice invoice(CancellationInvoiceTo cancellationInvoiceTo, ProjectReservation reservation) throws ManagerBeanException {
		if (!reservation.isInvoiced() && cancellationInvoiceTo.getItem() != null && reservation.getPenaltyValue() != null) {
			boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
			boolean mustCloseSession = HibernateUtil.mustCloseSession();
			String sessionName = HibernateUtil.getSessionFactoryName();
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);

				HibernateUtil.beginTransaction(sessionName);

				Invoice invoice = createInvoice(cancellationInvoiceTo, reservation);
				double cancellationAmount = createInvoiceDetails(invoice, reservation, cancellationInvoiceTo);
				createInvoiceAddress(invoice, reservation);
				if (cancellationAmount != 0) {
					createInvoiceFinances(invoice, reservation, cancellationInvoiceTo, cancellationAmount);
				}
				recordInvoice(invoice);
	
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

	private Invoice createInvoice(CancellationInvoiceTo cancellationInvoiceTo, ProjectReservation reservation) throws ManagerBeanException {
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
		invoice.setIssueDate(cancellationInvoiceTo.getIssueDate());
		invoice.setSecurityLevel(SecurityLevel.OFFICIAL);
		invoice.setStatus(InvoiceStatus.PENDING);
		invoice.setType(InvoiceType.SALES);
		invoice.setScope(reservation.getHotelReservation().getScope());
		invoice.setPosShift(cancellationInvoiceTo.getPosShift());

		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		return (Invoice)invoiceBean.insert(invoice);
	}

	private double createInvoiceDetails(Invoice invoice, ProjectReservation reservation, CancellationInvoiceTo cancellationInvoiceTo) throws ManagerBeanException {
		ReservationUtils reservationUtils = new ReservationUtils(reservation.getDomain());

		double advancedAmount = reservation.getAdvancedAmount();
		Item advanceItem = (advancedAmount != 0) ? reservationUtils.obtainAdvanceItem() : null;
		advanceItem = (advancedAmount != 0 && advanceItem == null) ? reservationUtils.obtainDefaultServiceItem() : advanceItem;
		double advanceVatPercent = (advancedAmount != 0) ? advanceItem.getVat().getDatedPercentage(invoice.getIssueDate()) : 0;
		double advanceTaxableBase = (advancedAmount != 0) ? CommonUtil.round(advancedAmount / (1 + advanceVatPercent / 100), 4) : 0;
		double advanceVatQuota = (advancedAmount != 0) ? CommonUtil.round(advancedAmount - CommonUtil.round(advanceTaxableBase)) : 0;

		double penaltyAmount = (cancellationInvoiceTo.isKeepAdvance()) ? advancedAmount : reservation.getPenaltyAmount();
		double penaltyVatPercent = cancellationInvoiceTo.getItem().getVat().getDatedPercentage(invoice.getIssueDate());
		double penaltyTaxableBase = CommonUtil.round(penaltyAmount / (1 + penaltyVatPercent / 100), 4);
		double penaltyVatQuota = CommonUtil.round(penaltyAmount - CommonUtil.round(penaltyTaxableBase));
		double invoiceTotal = CommonUtil.round(penaltyAmount - advancedAmount);

		Map<Date, Double> penaltyTaxableBases = new HashMap<Date, Double>();
		if (cancellationInvoiceTo.isKeepAdvance() || reservation.getPenaltyMode().equals("P") || reservation.getPenaltyMode().equals("M")) {
			penaltyTaxableBases.put(reservation.getStartDate(), penaltyTaxableBase);
		} else {
			Date fromDate = reservation.getStartDate();
			Date toDate = (reservation.getPenaltyDays() < 0) ? reservation.getEndDate() : DateUtils.addDays(fromDate, reservation.getPenaltyDays()-1);
			penaltyTaxableBases = reservation.getReservationTaxableBasesPerDay(fromDate, toDate, cancellationInvoiceTo.getItem().getVat());

			penaltyTaxableBase = reservation.getCancellationPenaltyTaxableBase(fromDate, toDate);
		}

		int line = 0;
		boolean taxDataInDetail = (invoiceTotal != CommonUtil.round((penaltyTaxableBase - advanceTaxableBase) * (1 + penaltyVatPercent / 100)));
		for (Date date=reservation.getStartDate(); !date.after(reservation.getEndDate()); date = DateUtils.addDays(date, 1)) {
			if (penaltyTaxableBases.containsKey(date)) {
				double taxableBase = penaltyTaxableBases.get(date);
				InvoiceDetail invoiceDetail = new InvoiceDetail();
				invoiceDetail.setInvoice(invoice);
				invoiceDetail.setProject(reservation.getProject());
				invoiceDetail.setLine(++line);
				invoiceDetail.setItem(cancellationInvoiceTo.getItem());
				invoiceDetail.setDescription(obtainDetailDescription(date, null, invoiceDetail.getItem().getFullName()));
				invoiceDetail.setQuantity(1);
				invoiceDetail.setPrice(taxableBase);
				invoiceDetail.setDiscountExpression(new DiscountExpression("0.0"));
				invoiceDetail.setSource(InvoiceSource.DIRECT_INVOICE);
				invoiceDetail.setTaxableBase(taxableBase);
				invoiceDetail.setWorkPlace(reservation.getHotelReservation().getWorkPlace());
				if (taxDataInDetail) {
					invoiceDetail.setTaxDataInDetail(true);
					invoiceDetail.setVatPercent(penaltyVatPercent);
					invoiceDetail.setVatQuota((line == penaltyTaxableBases.size()) ? penaltyVatQuota : CommonUtil.round(taxableBase * penaltyVatPercent / 100));
					penaltyVatQuota = CommonUtil.round(penaltyVatQuota - invoiceDetail.getVatQuota());
				}
				invoiceDetail.setUpdateEnabled((advancedAmount > 0) ? false : line == penaltyTaxableBases.size());
				BeanManager.getManagerBean(InvoiceDetail.class).insert(invoiceDetail);
			}
		}

		if (advancedAmount > 0) {
			InvoiceDetail invoiceDetail = new InvoiceDetail();
			invoiceDetail.setInvoice(invoice);
			invoiceDetail.setProject(reservation.getProject());
			invoiceDetail.setLine(++line);
			invoiceDetail.setItem(advanceItem);
			invoiceDetail.setDescription(obtainDetailDescription(reservation.getStartDate(), null, invoiceDetail.getItem().getFullName()));
			invoiceDetail.setQuantity(-1);
			invoiceDetail.setPrice(advanceTaxableBase);
			invoiceDetail.setDiscountExpression(new DiscountExpression("0.0"));
			invoiceDetail.setSource(InvoiceSource.DIRECT_INVOICE);
			invoiceDetail.setTaxableBase(advanceTaxableBase * (-1));
			invoiceDetail.setWorkPlace(reservation.getHotelReservation().getWorkPlace());
			if (taxDataInDetail) {
				invoiceDetail.setTaxDataInDetail(true);
				invoiceDetail.setVatPercent(advanceVatPercent);
				invoiceDetail.setVatQuota(CommonUtil.round(advanceVatQuota * (-1)));
			}
			BeanManager.getManagerBean(InvoiceDetail.class).insert(invoiceDetail);
		}

		if (advancedAmount > 0 && invoiceTotal != 0) {
			Finance finance = getAdvancedFinance(reservation);
			if (finance != null) {
				cancellationInvoiceTo.setPayMethod(finance.getPayMethod());
				if (finance.getBankAccount() != null && StringUtils.isNotEmpty(finance.getBankAccount().toString())) {
					RegistryBank registryBank = new RegistryBank();
					registryBank.setBankAccount(finance.getBankAccount());
					registryBank.setBankAlias(finance.getBankAlias());
					registryBank.setBic(finance.getBic());
					cancellationInvoiceTo.setRegistryBank(registryBank);
				}
			}
		}

		return invoiceTotal;
	}

	private void createInvoiceAddress(Invoice invoice, ProjectReservation reservation) throws ManagerBeanException {
		IAddress address = null;
		if (reservation.isGuestHolder()) {
			ProjectReservationGuest reservationGuest = obtainMainGuest(reservation);
			address = new InvoiceAddress();
			address.setAddress(StringUtils.abbreviate(reservationGuest.getAddress(), 45));
			address.setNumber(StringUtils.abbreviate(reservationGuest.getNumber(), 12));
			address.setAddress2(StringUtils.abbreviate(reservationGuest.getAddress2(), 45));
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

	private void createInvoiceFinances(Invoice invoice, ProjectReservation reservation, CancellationInvoiceTo cancellationInvoiceTo, double cancellationAmount) 
			throws ManagerBeanException {
		Finance finance = new Finance();
		finance.setInvoice(invoice);
		finance.setRegistry(invoice.getRegistry());
		finance.setPayment(false);
		finance.setDueDate(cancellationInvoiceTo.getFinanceDate());
		finance.setScope(invoice.getScope());
		finance.setFinanceStatus(FinanceStatus.PENDING);
		finance.setPayMethod(obtainPayMethod(reservation, cancellationInvoiceTo));
		if (cancellationInvoiceTo.getRegistryBank() != null) {
			finance.setBankAccount(cancellationInvoiceTo.getRegistryBank().getBankAccount());
			finance.setBankAlias(cancellationInvoiceTo.getRegistryBank().getBankAlias());
			finance.setBic(cancellationInvoiceTo.getRegistryBank().getBic());
		} else if (cancellationInvoiceTo.getPayMethod().getType() == PayMethodType.NEGOTIABLE_DOCUMENT) {
			RegistryBank rBank = getRegistryBank(invoice.getRegistry());
			if (rBank != null) {
				finance.setBankAccount(rBank.getBankAccount());
				finance.setBankAlias(rBank.getBankAlias());
				finance.setBic(rBank.getBic());
			}
		}
		finance.setAmount(cancellationAmount);

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

	private PayMethod obtainPayMethod(ProjectReservation reservation, CancellationInvoiceTo cancellationInvoiceTo) {
		boolean conexFlow = !reservation.isBlankToken() && reservation.isConexFlowSaleOk();
		return (conexFlow) ? cancellationInvoiceTo.getConexFlowPayMethod() : cancellationInvoiceTo.getPayMethod();
	}

	public RegistryBank getRegistryBank(Registry registry) throws ManagerBeanException {
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
