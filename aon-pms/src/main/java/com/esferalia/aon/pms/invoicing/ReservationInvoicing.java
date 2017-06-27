package com.esferalia.aon.pms.invoicing;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceAddress;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.InvoiceTax;
import com.code.aon.finance.bridge.invoicing.RectificationInvoicingManager;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.enumeration.RectificationType;
import com.code.aon.finance.invoicing.finance.FinanceGenerator;
import com.code.aon.finance.invoicing.finance.FinanceTrackingWriter;
import com.code.aon.product.Item;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.IAddress;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationRoom;
import com.esferalia.aon.pms.ProjectReservationRoomDetail;
import com.esferalia.aon.pms.ProjectReservationService;
import com.esferalia.aon.pms.ProjectReservationServiceDetail;
import com.esferalia.aon.pms.invoicing.ReservationInvoiceTo.HotelService;
import com.esferalia.aon.pms.reservation.IReservationConstants;
import com.esferalia.aon.pms.reservation.ReservationUtils;

public class ReservationInvoicing implements IReservationConstants {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReservationInvoicing.class.getName());
	
	public Invoice invoice(ReservationInvoiceTo reservationInvoiceTo, ProjectReservation reservation) throws ManagerBeanException {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);

			HibernateUtil.beginTransaction(sessionName);
			
			Invoice invoice = createInvoice(reservationInvoiceTo, reservation);
			createInvoiceDetails(invoice, reservation, reservationInvoiceTo);
			if (reservationInvoiceTo.getAddress() != null) {
				createInvoiceAddress(invoice, reservationInvoiceTo.getAddress());
			}
			updateInvoiceDate(invoice);
			double financesAmount = createInvoiceFinances(invoice, reservationInvoiceTo);
			if (invoice.getTotal() == financesAmount) {
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
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}

	public Invoice rectify(Invoice invoice, ReservationInvoiceTo reservationInvoiceTo, boolean settleFinance) throws ManagerBeanException {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);

			HibernateUtil.beginTransaction(sessionName);

			String series = reservationInvoiceTo.getSeries();
			int number = reservationInvoiceTo.getNumber();
			Date date = reservationInvoiceTo.getIssueDate();
			String comments = reservationInvoiceTo.getComments();

			RectificationInvoicingManager rectificationManager = new RectificationInvoicingManager();
			Invoice rectifier = rectificationManager.rectifyInvoice(invoice, series, number, date, comments, settleFinance);
			rectifier.setPosShift(reservationInvoiceTo.getPosShift());
			recordInvoice(rectifier);

			if (invoice.isService() && !reservationInvoiceTo.isEarlyCheckOut() && invoice.getProject() != null && invoice.getProject().getId() != null) {
				removeRectifiedServices(invoice);
			}

			HibernateUtil.getSession(sessionName).flush();
			HibernateUtil.commitTransaction(sessionName);

			return rectifier;
		} catch (Exception e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg = "Unable to rollback transaction!";
				LOGGER.error(msg,daoe);
			}
			LOGGER.error(e.getMessage());
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}

	public Invoice modify(Invoice invoice, ReservationInvoiceTo reservationInvoiceTo) throws ManagerBeanException {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);

			HibernateUtil.beginTransaction(sessionName);
			
			invoice.setRegistryDocument(reservationInvoiceTo.getRegistry().getDocument());
			invoice.setRegistryDocumentType(reservationInvoiceTo.getRegistry().getDocumentType());
			invoice.setRegistryDocumentCountry(reservationInvoiceTo.getRegistry().getDocumentCountry());
			invoice.setRegistryName(reservationInvoiceTo.getRegistry().getName());
			invoice.setUpdateEnabled(false);
			invoice = (Invoice)BeanManager.getManagerBean(Invoice.class).update(invoice);

			if (reservationInvoiceTo.getAddress() != null) {
				removeInvoiceAddress(invoice);
				createInvoiceAddress(invoice, reservationInvoiceTo.getAddress());
			}
			removeInvoiceFinances(invoice);
			createInvoiceFinances(invoice, reservationInvoiceTo);

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
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}

	public Invoice duplicate(Invoice invoice, ReservationInvoiceTo reservationInvoiceTo) throws ManagerBeanException {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);

			HibernateUtil.beginTransaction(sessionName);
			
			Integer sourceId = invoice.getId();
			Invoice duplicate = duplicateInvoice(invoice, reservationInvoiceTo);
			duplicateInvoiceDetails(duplicate, sourceId);
			if (reservationInvoiceTo.getAddress() != null) {
				createInvoiceAddress(duplicate, reservationInvoiceTo.getAddress());
			}
			createInvoiceFinances(duplicate, reservationInvoiceTo);
			recordInvoice(duplicate);

			HibernateUtil.getSession(sessionName).flush();
			HibernateUtil.commitTransaction(sessionName);

			return duplicate;
		} catch (Exception e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg = "Unable to rollback transaction!";
				LOGGER.error(msg,daoe);
			}
			LOGGER.error(e.getMessage());
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}

	public void settle(Invoice invoice1, Invoice invoice2) throws ManagerBeanException {
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_ID), invoice1.getId());
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_FINANCE_STATUS), FinanceStatus.PENDING);
		for (ITransferObject ito1 : financeBean.getList(criteria)) {
			Finance finance1 = (Finance)ito1;
			criteria = new Criteria();
			criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_ID), invoice2.getId());
			criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_FINANCE_STATUS), FinanceStatus.PENDING);
			criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_AMOUNT), CommonUtil.round(0 - finance1.getAmount()));
			if (finance1.getPayMethod() != null && finance1.getPayMethod().getId() != null) {
				criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_PAY_METHOD_ID), finance1.getPayMethod().getId());
			} else {
				criteria.addNullExpression(financeBean.getFieldName(IEntityAlias.FINANCE_PAY_METHOD));
			}
			for (ITransferObject ito2 : financeBean.getList(criteria)) {
				Finance finance2 = (Finance)ito2;
				settleFinances(finance1, finance2);
				break;
			}
		}

	}

	private Invoice createInvoice(ReservationInvoiceTo reservationInvoiceTo, ProjectReservation reservation) throws ManagerBeanException {
		Invoice invoice = new Invoice();
		invoice.setProject((!reservationInvoiceTo.isService()) ? reservation.getProject() : ((reservation!=null) ? reservation.getProject() : null));
		invoice.setSeries(reservationInvoiceTo.getSeries());
		invoice.setNumber(reservationInvoiceTo.getNumber());
		invoice.setRegistry(reservationInvoiceTo.getRegistry());
		invoice.setRegistryDocument(reservationInvoiceTo.getRegistry().getDocument());
		invoice.setRegistryDocumentType(reservationInvoiceTo.getRegistry().getDocumentType());
		invoice.setRegistryDocumentCountry(reservationInvoiceTo.getRegistry().getDocumentCountry());
		invoice.setRegistryName(reservationInvoiceTo.getRegistry().getName());
		invoice.setRegistryAddress(null);
		invoice.setIssueDate(reservationInvoiceTo.getIssueDate());
		invoice.setSecurityLevel(SecurityLevel.OFFICIAL);
		invoice.setStatus(InvoiceStatus.PENDING);
		invoice.setType(InvoiceType.SALES);
		invoice.setScope((!reservationInvoiceTo.isService()) ? reservation.getHotelReservation().getScope() : reservationInvoiceTo.getHotel().getScope());
		invoice.setService(reservationInvoiceTo.isService());
		invoice.setComments(reservationInvoiceTo.getComments());
		invoice.setPosShift(reservationInvoiceTo.getPosShift());

		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		return (Invoice)invoiceBean.insert(invoice);
	}

	private void createInvoiceDetails(Invoice invoice, ProjectReservation reservation, ReservationInvoiceTo reservationInvoiceTo) throws ManagerBeanException {
		if (!reservationInvoiceTo.isService() || reservationInvoiceTo.isEarlyCheckOut()) {
			createReservationDetails(invoice, reservation, reservationInvoiceTo);
		} else {
			if (!reservationInvoiceTo.isTouristTax()) {
				createServiceDetails(invoice, reservation, reservationInvoiceTo);
			} else {
				createTouristTaxDetails(invoice, reservation, reservationInvoiceTo);
			}
		}
	}

	private void createReservationDetails(Invoice invoice, ProjectReservation reservation, ReservationInvoiceTo reservationInvoiceTo) throws ManagerBeanException {
		int line = 0;
		double taxableBase = 0;

		ReservationUtils reservationUtils = new ReservationUtils(reservation.getDomain());
		boolean isVatWrong = false;
		double vatAmount = 0;
		boolean allAdvanced = false;
		double advancedAmount = 0;
		if (!reservationInvoiceTo.isEarlyCheckOut() && !reservation.isEarlyCheckOut()) {
			advancedAmount = reservation.getAdvancedAmount();
			if (advancedAmount > 0 && advancedAmount == reservationUtils.getReservationCalculatedTotal(reservation)) {
				vatAmount = reservationUtils.getReservationAdvancedVatAmount(reservation.getId());
				allAdvanced = true;
			} else {
				vatAmount = reservationUtils.getReservationCalculatedVatQuota(reservation);
			}
			isVatWrong = (reservation.getVatQuota() != vatAmount || advancedAmount > 0);
		}

		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		IManagerBean reservationServiceDetailBean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
		Criteria criteria = new Criteria();
		String alias = reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_PROJECT_RESERVATION_ID);
		criteria.addEqualExpression(alias, reservation.getId());
		alias = reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_REMOVED);
		criteria.addEqualExpression(alias, Boolean.FALSE);
		if (!reservationInvoiceTo.isService()) {
			alias = reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_EXTRA);
			criteria.addEqualExpression(alias, Boolean.FALSE);
		} else if (reservationInvoiceTo.isEarlyCheckOut() && reservationInvoiceTo.getServicesIds().size() > 0) {
			alias = reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_EXTRA);
			criteria.addEqualExpression(alias, Boolean.TRUE);
			alias = reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_ID);
			criteria.addInExpression(alias, reservationInvoiceTo.getServicesIds());
		}
		criteria.addOrder(reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_ITEM_PRODUCT_TYPE));
		criteria.addOrder(reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_EFFECTIVE_DATE));
		criteria.addOrder(reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_ROOM_DETAIL_ID));
		List<ITransferObject> reservationServiceDetailList = reservationServiceDetailBean.getList(criteria);
		for (ITransferObject ito : reservationServiceDetailList) {
			ProjectReservationServiceDetail reservationServiceDetail = (ProjectReservationServiceDetail)ito;
			InvoiceDetail invoiceDetail = new InvoiceDetail();
			invoiceDetail.setInvoice(invoice);
			invoiceDetail.setProject(reservation.getProject());
			invoiceDetail.setLine(++line);
			invoiceDetail.setItem(reservationServiceDetail.getProjectReservationService().getItem());
			invoiceDetail.setDescription(obtainDetailDescription(reservationServiceDetail));
			invoiceDetail.setQuantity(reservationServiceDetail.getQuantity());
			invoiceDetail.setDiscountExpression(new DiscountExpression("0.0"));
			invoiceDetail.setPrice(reservationServiceDetail.getPrice());
			invoiceDetail.setSource(InvoiceSource.RESERVATION);
			invoiceDetail.setSourceId(reservationServiceDetail.getId());
			invoiceDetail.setTaxableBase(reservationServiceDetail.getTaxableBase());
			invoiceDetail.setWorkPlace(reservation.getHotelReservation().getWorkPlace());
			if (isVatWrong) {
				invoiceDetail.setTaxDataInDetail(true);
				if (vatAmount != 0) {
					invoiceDetail.setVatPercent(invoiceDetail.getItem().getProduct().getVat().getDatedPercentage(invoice.getIssueDate()));
					double vatQuota = CommonUtil.round(invoiceDetail.getTaxableBase() * invoiceDetail.getVatPercent() / 100);
					if (isVatWrong && line == reservationServiceDetailList.size()) {
						vatQuota = vatAmount;
					}
					invoiceDetail.setVatQuota(vatQuota);
					vatAmount = CommonUtil.round(vatAmount - vatQuota);
				} else {
					invoiceDetail.setVatPercent(0);
					invoiceDetail.setVatQuota(0);
				}
			}
			invoiceDetail.getInvoice().setUpdateEnabled(line == reservationServiceDetailList.size());
			invoiceDetailBean.insert(invoiceDetail);
			taxableBase = CommonUtil.round(taxableBase + invoiceDetail.getTaxableBase(), 4);
		}

		if (isVatWrong) {
			reservation.setTaxableBase(invoice.getTaxableBase());
			reservation.setVatQuota(invoice.getVatQuota());
			reservation.setTotal(invoice.getTotal());
		}

		if (advancedAmount > 0 && !reservationInvoiceTo.isEarlyCheckOut()) {
			criteria = new Criteria();
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_PROJECT_ID), reservation.getId());
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ADVANCE), Boolean.TRUE);
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_RECTIFICATION_TYPE), RectificationType.NONE);
			for (ITransferObject ito : invoiceDetailBean.getList(criteria)) {
				InvoiceDetail advanceDetail = (InvoiceDetail)ito;
				InvoiceDetail invoiceDetail = new InvoiceDetail();
				invoiceDetail.setInvoice(invoice);
				invoiceDetail.setProject(reservation.getProject());
				invoiceDetail.setLine(++line);
				invoiceDetail.setItem(advanceDetail.getItem());
				invoiceDetail.setDescription(advanceDetail.getDescription());
				invoiceDetail.setQuantity(-1);
				invoiceDetail.setDiscountExpression(new DiscountExpression("0.0"));
				invoiceDetail.setPrice(advanceDetail.getPrice());
				invoiceDetail.setSource(InvoiceSource.DIRECT_INVOICE);
				invoiceDetail.setTaxableBase(advanceDetail.getTaxableBase() * (-1));
				invoiceDetail.setWorkPlace(reservation.getHotelReservation().getWorkPlace());
				invoiceDetail.setTaxDataInDetail(true);
				for (TaxBreakDown taxBreakDown : advanceDetail.getTaxBreakDowns()) {
					invoiceDetail.setVatPercent(taxBreakDown.getTaxPercent());
					if (taxBreakDown.getTaxQuota() != 0) {
						invoiceDetail.setVatQuota(taxBreakDown.getTaxQuota() * (-1));
					} else {
						invoiceDetail.setVatQuota(CommonUtil.round(invoiceDetail.getTaxableBase() * taxBreakDown.getTaxPercent() / 100));
					}
				}
				invoiceDetail.getInvoice().setUpdateEnabled(true);
				invoiceDetail = (InvoiceDetail)invoiceDetailBean.insert(invoiceDetail);
				taxableBase = CommonUtil.round(taxableBase + invoiceDetail.getTaxableBase(), 4);

				if (allAdvanced && taxableBase != 0 && taxableBase <= Math.abs(0.01)) {
					invoiceDetail.setPrice(invoiceDetail.getPrice() + taxableBase);
					invoiceDetail.setTaxableBase(invoiceDetail.getPrice() * (-1));
					invoiceDetailBean.update(invoiceDetail);
				}
			}
		}

		if (reservationInvoiceTo.isEarlyCheckOut() && !reservationInvoiceTo.isService()) {
			InvoiceDetail invoiceDetail = new InvoiceDetail();
			invoiceDetail.setInvoice(invoice);
			invoiceDetail.setProject(reservation.getProject());
			invoiceDetail.setLine(++line);
			invoiceDetail.setItem(reservationInvoiceTo.getPenaltyItem());
			invoiceDetail.setDescription(obtainPenaltyDetailDescription(reservationInvoiceTo, invoiceDetail.getItem()));
			invoiceDetail.setQuantity(1);
			invoiceDetail.setDiscountExpression(new DiscountExpression("0.0"));
			invoiceDetail.setPrice(reservationInvoiceTo.getPenaltyAmount());
			invoiceDetail.setSource(InvoiceSource.DIRECT_INVOICE);
			invoiceDetail.setTaxableBase(reservationInvoiceTo.getPenaltyAmount());
			invoiceDetail.setWorkPlace(reservation.getHotelReservation().getWorkPlace());
			invoiceDetail.getInvoice().setUpdateEnabled(true);
			invoiceDetailBean.insert(invoiceDetail);
		}
	}

	private void createServiceDetails(Invoice invoice, ProjectReservation reservation, ReservationInvoiceTo reservationInvoiceTo) throws ManagerBeanException {
		int line = 0;
		IPriceStrategy strategy = PriceStrategyFactory.getPriceStrategy();
		ReservationUtils reservationUtils = new ReservationUtils();

		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		IManagerBean reservationServiceBean = BeanManager.getManagerBean(ProjectReservationService.class);
		IManagerBean reservationServiceDetailBean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
		for (HotelService service : reservationInvoiceTo.getServices()) {
			ProjectReservationService reservationService = new ProjectReservationService();
			if (reservation != null) {
				reservationService.setProjectReservation(reservation);
				reservationService.setItem(service.getItem());
				reservationService.setDescription(service.getItem().getProduct().getName());
				reservationService.setMealPlan(reservationUtils.obtainMealPlan(service.getItem().getDetail()));
				reservationService.setProjectReservationRoom(reservationInvoiceTo.getRoom().getProjectReservationRoom().getId());
				reservationService.setExtra(true);
				reservationService = (ProjectReservationService)reservationServiceBean.insert(reservationService);
			}

			Date date = service.getFromDate();
			while (date.compareTo(service.getToDate()) <= 0) {
				ProjectReservationServiceDetail reservationServiceDetail = new ProjectReservationServiceDetail();
				if (reservation != null) {
					reservationServiceDetail.setProjectReservationService(reservationService);
					reservationServiceDetail.setProjectReservationRoomDetail(obtainRoomDetailByDate(reservationInvoiceTo.getRoom(), date));
					reservationServiceDetail.setEffectiveDate(date);
					reservationServiceDetail.setQuantity(service.getQuantity());
					reservationServiceDetail.setPrice(strategy.getUnitPrice(reservationServiceDetail, date, reservationInvoiceTo.getHotel().getCustomer()));
					reservationServiceDetail.setTaxableBase(strategy.getBasePrice(reservationServiceDetail));
					reservationServiceDetail = (ProjectReservationServiceDetail)reservationServiceDetailBean.insert(reservationServiceDetail);
				}

				InvoiceDetail invoiceDetail = new InvoiceDetail();
				invoiceDetail.setInvoice(invoice);
				invoiceDetail.setProject((reservation!=null) ? reservation.getProject() : null);
				invoiceDetail.setLine(++line);
				invoiceDetail.setItem(service.getItem());
				invoiceDetail.setDescription(obtainDetailDescription(date, reservationInvoiceTo.getRoom(), service.getItem()));
				invoiceDetail.setQuantity(service.getQuantity());
				invoiceDetail.setDiscountExpression(new DiscountExpression("0.0"));
				invoiceDetail.setPrice(strategy.getUnitPrice(invoiceDetail, date, reservationInvoiceTo.getHotel().getCustomer()));
				invoiceDetail.setSource((reservation != null) ? InvoiceSource.RESERVATION : InvoiceSource.DIRECT_INVOICE);
				invoiceDetail.setSourceId((reservation != null) ? reservationServiceDetail.getId() : null);
				invoiceDetail.setTaxableBase(strategy.getBasePrice(invoiceDetail));
				invoiceDetail.setWorkPlace(reservationInvoiceTo.getHotel().getWorkPlace());
				invoiceDetail.getInvoice().setUpdateEnabled(service.equals(reservationInvoiceTo.getLastService()) && date.equals(service.getToDate()));
				invoiceDetailBean.insert(invoiceDetail);

				date = DateUtils.addDays(date, 1);
			}
		}
	}

	private void createTouristTaxDetails(Invoice invoice, ProjectReservation reservation, ReservationInvoiceTo reservationInvoiceTo) throws ManagerBeanException {
		int line = 0;
		IPriceStrategy strategy = PriceStrategyFactory.getPriceStrategy();

		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		IManagerBean reservationServiceBean = BeanManager.getManagerBean(ProjectReservationService.class);
		IManagerBean reservationServiceDetailBean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
		for (ProjectReservationRoom reservationRoom : reservation.getReservationRoomList()) {
			HotelService service = reservationInvoiceTo.getFirstService();

			ProjectReservationService reservationService = new ProjectReservationService();
			reservationService.setProjectReservation(reservation);
			reservationService.setItem(service.getItem());
			reservationService.setDescription(service.getItem().getProduct().getName());
			reservationService.setMealPlan(null);
			reservationService.setProjectReservationRoom(reservationRoom.getId());
			reservationService.setExtra(true);
			reservationService = (ProjectReservationService)reservationServiceBean.insert(reservationService);

			int night = 0;
			Date date = service.getFromDate();
			while (date.compareTo(service.getToDate()) <= 0) {
				ProjectReservationServiceDetail reservationServiceDetail = new ProjectReservationServiceDetail();
				reservationServiceDetail.setProjectReservationService(reservationService);
				reservationServiceDetail.setProjectReservationRoomDetail(obtainRoomDetailByDate(reservationRoom, date));
				reservationServiceDetail.setEffectiveDate(date);
				reservationServiceDetail.setQuantity(++night);
				reservationServiceDetail.setPrice(strategy.getUnitPrice(reservationServiceDetail, date, reservationInvoiceTo.getHotel().getCustomer()));
				reservationServiceDetail.setQuantity(reservationRoom.getAdults());
				reservationServiceDetail.setTaxableBase(strategy.getBasePrice(reservationServiceDetail));
				reservationServiceDetail = (ProjectReservationServiceDetail)reservationServiceDetailBean.insert(reservationServiceDetail);

				InvoiceDetail invoiceDetail = new InvoiceDetail();
				invoiceDetail.setInvoice(invoice);
				invoiceDetail.setProject(reservation.getProject());
				invoiceDetail.setLine(++line);
				invoiceDetail.setItem(service.getItem());
				invoiceDetail.setDescription(obtainDetailDescription(date, reservationServiceDetail.getProjectReservationRoomDetail(), service.getItem()));
				invoiceDetail.setQuantity(night);
				invoiceDetail.setDiscountExpression(new DiscountExpression("0.0"));
				invoiceDetail.setPrice(strategy.getUnitPrice(invoiceDetail, date, reservationInvoiceTo.getHotel().getCustomer()));
				invoiceDetail.setQuantity(reservationRoom.getAdults());
				invoiceDetail.setSource(InvoiceSource.RESERVATION);
				invoiceDetail.setSourceId(reservationServiceDetail.getId());
				invoiceDetail.setTaxableBase(strategy.getBasePrice(invoiceDetail));
				invoiceDetail.setWorkPlace(reservationInvoiceTo.getHotel().getWorkPlace());
				invoiceDetail.getInvoice().setUpdateEnabled(service.equals(reservationInvoiceTo.getLastService()) && date.equals(service.getToDate()));
				invoiceDetailBean.insert(invoiceDetail);

				date = DateUtils.addDays(date, 1);
			}
		}
	}

	private void createInvoiceAddress(Invoice invoice, IAddress address) throws ManagerBeanException {
		IManagerBean invoiceAddressBean = BeanManager.getManagerBean(InvoiceAddress.class);
		InvoiceAddress invoiceAddress = new InvoiceAddress();
		if (address instanceof InvoiceAddress) {
			invoiceAddress = (InvoiceAddress)address;
		} else {
			invoiceAddress.setStreetType(address.getStreetType()); 
			invoiceAddress.setAddress(address.getAddress());
			invoiceAddress.setNumber(address.getNumber()); 
			invoiceAddress.setAddress2(address.getAddress2());
			invoiceAddress.setZip(address.getZip()); 
			invoiceAddress.setCity(address.getCity()); 
			invoiceAddress.setProvince(address.getProvince()); 
			invoiceAddress.setGeozone(address.getGeozone()); 
		}
		invoiceAddress.setInvoice(invoice);
		invoiceAddressBean.insert(invoiceAddress);
	}

	private void updateInvoiceDate(Invoice invoice) throws ManagerBeanException {
		//La Factura se graba inicialmente con la fecha de inicio de la Reserva, para que los impuestos se apliquen a esa fecha, y la fecha de iva igual. Pero 
		//posteriormente se modifica esa fecha si no coincide con la fecha actual, para mantener la correlatividad fecha - serie/numero.
		if (!DateUtils.isSameDay(invoice.getIssueDate(), new Date())) {
			invoice.setIssueDate(new Date());
		}
	}

	private double createInvoiceFinances(Invoice invoice, ReservationInvoiceTo reservationInvoiceTo) throws ManagerBeanException {
		double financesAmount = 0;
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		if (!reservationInvoiceTo.isDirectCustomer() && invoice.getTotal() != 0) {
			FinanceGenerator financeGenerator = new FinanceGenerator();
			financeGenerator.generateFinances(invoice, invoice.getTotal());
			financesAmount = invoice.getTotal();
		} else {
			for (Finance finance : reservationInvoiceTo.getFinances()) {
				if (finance.getAmount() != 0) {
					finance.setInvoice(invoice);
					finance.setRegistry(invoice.getRegistry());
					finance.setPayment(false);
					finance.setDueDate(invoice.getIssueDate());
					finance.setScope(invoice.getScope());
					finance.setFinanceStatus(FinanceStatus.PENDING);
					finance.setBatchDetails(null);
					financeBean.insert(finance);
					financesAmount = CommonUtil.round(financesAmount + finance.getAmount());
				}
			}
		}
		return financesAmount;
	}

	private void recordInvoice(Invoice invoice) throws ManagerBeanException {
		AccountEntryInvoiceWriter entryWriter = new AccountEntryInvoiceWriter();
		entryWriter.recordAndUpdateInvoice(invoice);
	}

	private String obtainDetailDescription(ProjectReservationServiceDetail reservationServiceDetail) throws ManagerBeanException {
		Date date = reservationServiceDetail.getEffectiveDate();
		String room = reservationServiceDetail.getProjectReservationRoomDetail().getRoom().getAsset().getName();
		String description = reservationServiceDetail.getProjectReservationService().getDescription();
		return obtainDetailDescription(date, room, description);
	}

	private String obtainDetailDescription(Date effectiveDate, ProjectReservationRoomDetail reservationRoomDetail, Item item) throws ManagerBeanException {
		String room = (reservationRoomDetail != null) ? reservationRoomDetail.getRoomNumber(effectiveDate) : null;
		return obtainDetailDescription(effectiveDate, room, item.getProduct().getName());
	}

	private String obtainPenaltyDetailDescription(ReservationInvoiceTo reservationInvoiceTo, Item item) {
		String penaltyDays = " (" + reservationInvoiceTo.getPenaltyDays() + (reservationInvoiceTo.getPenaltyDays() == 1 ? " día" : " días") + ")";
		return obtainDetailDescription(reservationInvoiceTo.getEarlyCheckOutDate(), null, item.getProduct().getName() + penaltyDays);
	}

	private String obtainDetailDescription(Date effectiveDate, String room, String description) {
    	DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    	String date = StringUtils.rightPad(formatter.format(effectiveDate), 11);
    	room = (room == null) ? StringUtils.rightPad(StringUtils.repeat("-", 5), 6) : StringUtils.rightPad(room, 6);
    	return (date + room + description);
	}

	private ProjectReservationRoomDetail obtainRoomDetailByDate(ProjectReservationRoomDetail roomDetail, Date effectiveDate) throws ManagerBeanException {
		return obtainRoomDetailByDate(roomDetail.getProjectReservationRoom(), effectiveDate);
	}

	private ProjectReservationRoomDetail obtainRoomDetailByDate(ProjectReservationRoom reservationRoom, Date effectiveDate) throws ManagerBeanException {
		IManagerBean reservationRoomDetailBean = BeanManager.getManagerBean(ProjectReservationRoomDetail.class);
		Criteria criteria = new Criteria();
		String alias = reservationRoomDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_PROJECT_RESERVATION_ROOM_ID);
		criteria.addEqualExpression(alias, reservationRoom.getId());
		alias = reservationRoomDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_ASSET_ACTIVITY_DATE);
		criteria.addEqualExpression(alias, effectiveDate);
		for (ITransferObject ito : reservationRoomDetailBean.getList(criteria)) {
			return (ProjectReservationRoomDetail)ito;
		}
		return null;
	}

	private void removeRectifiedServices(Invoice invoice) throws ManagerBeanException {
		List<Integer> servicesToRemove = new LinkedList<Integer>();
		IManagerBean reservationServiceDetailBean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
		for (ITransferObject ito : invoice.getDetailList()) {
			Integer serviceDetailId = ((InvoiceDetail)ito).getSourceId();
			if (serviceDetailId != null) {
	    		ProjectReservationServiceDetail reservationServiceDetail = (ProjectReservationServiceDetail)reservationServiceDetailBean.get(serviceDetailId);
	    		if (reservationServiceDetail != null && reservationServiceDetail.getProjectReservationService().isExtra()) {
	    			if (!reservationServiceDetail.hasProduction()) {
		    			reservationServiceDetailBean.remove(reservationServiceDetail);

						if (!servicesToRemove.contains(reservationServiceDetail.getProjectReservationService().getId())) {
							servicesToRemove.add(reservationServiceDetail.getProjectReservationService().getId());
						}
	    			} else {
	    				reservationServiceDetail.setTaxableBase(0);
	    				reservationServiceDetail.setProjectReservationRoomDetail(null);
		    			reservationServiceDetailBean.update(reservationServiceDetail);

		    			if (!reservationServiceDetail.getProjectReservationService().isRemoved()) {
		    				reservationServiceDetail.getProjectReservationService().setRemoved(true);
		    	    		IManagerBean reservationServiceBean = BeanManager.getManagerBean(ProjectReservationService.class);
			    			reservationServiceBean.update(reservationServiceDetail.getProjectReservationService());
		    			}
	    			}
				}
			}
		}

		if (servicesToRemove.size() > 0) {
			IManagerBean reservationServiceBean = BeanManager.getManagerBean(ProjectReservationService.class);
			Criteria criteria = new Criteria();
			criteria.addInExpression(reservationServiceBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_ID), servicesToRemove);
			criteria.addEqualExpression(reservationServiceBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_REMOVED), Boolean.FALSE);
			for (ITransferObject ito : reservationServiceBean.getList(criteria)) {
				ProjectReservationService reservationService = (ProjectReservationService)ito;
				reservationServiceBean.remove(reservationService);
			}
		}
	}

	private void removeInvoiceAddress(Invoice invoice) throws ManagerBeanException {
		IManagerBean invoiceAddressBean = BeanManager.getManagerBean(InvoiceAddress.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceAddressBean.getFieldName(IEntityAlias.INVOICE_ADDRESS_INVOICE_ID), invoice.getId());
		for (ITransferObject ito : invoiceAddressBean.getList(criteria)) {
			invoiceAddressBean.remove(ito);
		}
	}

	private void removeInvoiceFinances(Invoice invoice) throws ManagerBeanException {
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_ID), invoice.getId());
		for (ITransferObject ito : financeBean.getList(criteria)) {
			financeBean.remove(ito);
		}
	}

	private Invoice duplicateInvoice(Invoice invoice, ReservationInvoiceTo reservationInvoiceTo) throws ManagerBeanException {
		Invoice duplicate = invoice;
		duplicate.setId(null);
		duplicate.setSeries(reservationInvoiceTo.getSeries());
		duplicate.setNumber(reservationInvoiceTo.getNumber());
		duplicate.setRegistryDocument(reservationInvoiceTo.getRegistry().getDocument());
		duplicate.setRegistryDocumentType(reservationInvoiceTo.getRegistry().getDocumentType());
		duplicate.setRegistryDocumentCountry(reservationInvoiceTo.getRegistry().getDocumentCountry());
		duplicate.setRegistryName(reservationInvoiceTo.getRegistry().getName());
		duplicate.setIssueDate(reservationInvoiceTo.getIssueDate());
		duplicate.setStatus(InvoiceStatus.PENDING);
		duplicate.setSigned(false);
		duplicate.setRectificationType(RectificationType.NONE);
		duplicate.setRectificationInvoice(null);
		duplicate.setPosShift(reservationInvoiceTo.getPosShift());
		duplicate.setCreationUser(null);
		duplicate.setCreationDate(null);
		duplicate.setModificationUser(null);
		duplicate.setModificationDate(null);
		duplicate.setLines(null);
		duplicate.setFinances(null);
		duplicate.setAddresses(null);
		duplicate.setAttachments(null);
		duplicate.setUpdateEnabled(false);

		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		return (Invoice)invoiceBean.insert(duplicate);
	}

	private void duplicateInvoiceDetails(Invoice duplicate, Integer sourceId) throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), sourceId);
		criteria.addOrder(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_LINE));
		for (ITransferObject ito : invoiceDetailBean.getList(criteria)) {
			InvoiceDetail invoiceDetail = (InvoiceDetail)ito;
			InvoiceTax invoiceVatTax = obtainInvoiceTax(invoiceDetail, TaxType.VAT);
			InvoiceTax invoiceRetentionTax = obtainInvoiceTax(invoiceDetail, TaxType.RETENTION);

			InvoiceDetail duplicateDetail = invoiceDetail;
			duplicateDetail.setId(null);
			duplicateDetail.setInvoice(duplicate);
			duplicateDetail.setSkipServiceProcess(true);
			duplicateDetail.setUpdateEnabled(false);
			duplicateDetail.getInvoice().setUpdateEnabled(false);
			duplicateDetail.setTaxDataInDetail(true);
			if (invoiceVatTax != null) {
				duplicateDetail.setVatPercent(invoiceVatTax.getPercentage());
				duplicateDetail.setVatQuota(invoiceVatTax.getQuota());
			}
			if (invoiceRetentionTax != null) {
				duplicateDetail.setRetentionPercent(invoiceRetentionTax.getPercentage());
				duplicateDetail.setRetentionQuota(invoiceRetentionTax.getQuota());
			}

			HibernateUtil.getSession(HibernateUtil.getSessionFactoryName()).evict(duplicateDetail);
			invoiceDetailBean.insert(duplicateDetail);
		}
	}

	private InvoiceTax obtainInvoiceTax(InvoiceDetail invoiceDetail, TaxType taxType) throws ManagerBeanException {
		IManagerBean invoiceTaxBean = BeanManager.getManagerBean(InvoiceTax.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceTaxBean.getFieldName(IEntityAlias.INVOICE_TAX_INVOICE_DETAIL_ID), invoiceDetail.getId());
		criteria.addEqualExpression(invoiceTaxBean.getFieldName(IEntityAlias.INVOICE_TAX_TAX_TYPE), taxType);
		for (ITransferObject ito : invoiceTaxBean.getList(criteria)) {
			return (InvoiceTax)ito;
		}
		return null;
	}

	private void settleFinances(Finance finance1, Finance finance2) throws ManagerBeanException {
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);

		finance1.setFinanceStatus(FinanceStatus.SETTLED);
		finance1 = (Finance)financeBean.update(finance1);
		FinanceTrackingWriter.addFinanceTracking(finance1, new Date(), FinanceTrackingType.SETTLED, "Saldado");

		finance2.setFinanceStatus(FinanceStatus.SETTLED);
		finance2 = (Finance)financeBean.update(finance2);
		FinanceTrackingWriter.addFinanceTracking(finance2, new Date(), FinanceTrackingType.SETTLED, "Saldado");
	}

}
