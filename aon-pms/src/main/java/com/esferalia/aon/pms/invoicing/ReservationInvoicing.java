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
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceAddress;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.bridge.invoicing.RectificationInvoicingManager;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.finance.FinanceGenerator;
import com.code.aon.product.Item;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.IAddress;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationRoomDetail;
import com.esferalia.aon.pms.ProjectReservationService;
import com.esferalia.aon.pms.ProjectReservationServiceDetail;
import com.esferalia.aon.pms.invoicing.ReservationInvoiceTo.HotelService;
import com.esferalia.aon.pms.reservation.IReservationConstants;
import com.esferalia.aon.pms.reservation.ReservationUtils;

public class ReservationInvoicing implements IReservationConstants {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReservationInvoicing.class.getName());
	
	public Invoice invoice(ReservationInvoiceTo reservationInvoiceTo, ProjectReservation reservation, boolean service) throws ManagerBeanException {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);

			HibernateUtil.beginTransaction(sessionName);
			
			Invoice invoice = createInvoice(reservationInvoiceTo, reservation, service);
			if (!service) {
				createInvoiceDetails(invoice, reservation);
			} else {
				createInvoiceDetails(invoice, reservation, reservationInvoiceTo);
			}
			if (reservationInvoiceTo.getAddress() != null) {
				createInvoiceAddress(invoice, reservationInvoiceTo.getAddress());
			}
			createInvoiceFinances(invoice, reservationInvoiceTo);
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

	public Invoice rectify(Invoice invoice, ReservationInvoiceTo reservationInvoiceTo) throws ManagerBeanException {
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
			Invoice rectifier = rectificationManager.rectifyInvoice(invoice, series, number, date, comments);
			recordInvoice(rectifier);

			if (invoice.isService() && invoice.getProject() != null && invoice.getProject().getId() != null) {
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
			throw new ManagerBeanException(e.getMessage(),e);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}

	private Invoice createInvoice(ReservationInvoiceTo reservationInvoiceTo, ProjectReservation reservation, boolean service) throws ManagerBeanException {
		Invoice invoice = new Invoice();
		invoice.setProject((!service) ? reservation.getProject() : ((reservation!=null) ? reservation.getProject() : null));
		invoice.setSeries(reservationInvoiceTo.getSeries());
		invoice.setNumber(reservationInvoiceTo.getNumber());
		invoice.setRegistry(reservationInvoiceTo.getRegistry());
		invoice.setRegistryDocument(reservationInvoiceTo.getRegistry().getDocument());
		invoice.setRegistryDocumentType(reservationInvoiceTo.getRegistry().getDocumentType());
		invoice.setRegistryDocumentCountry(reservationInvoiceTo.getRegistry().getDocumentCountry());
		invoice.setRegistryName(reservationInvoiceTo.getRegistry().getName());
		invoice.setRegistryAddress(null);
		invoice.setIssueDate((!service) ? reservation.getStartDate() : reservationInvoiceTo.getIssueDate());
		invoice.setSecurityLevel(SecurityLevel.OFFICIAL);
		invoice.setStatus(InvoiceStatus.PENDING);
		invoice.setType(InvoiceType.SALES);
		invoice.setScope((!service) ? reservation.getHotel().getScope() : reservationInvoiceTo.getHotel().getScope());
		invoice.setService(service);

		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		return (Invoice)invoiceBean.insert(invoice);
	}

	private void createInvoiceDetails(Invoice invoice, ProjectReservation reservation) throws ManagerBeanException {
		int line = 0;

		ReservationUtils reservationUtils = new ReservationUtils();
		double calculatedVatQuota = reservationUtils.getReservationCalculatedVatQuota(reservation);
		boolean isVatGap = (reservation.getVatQuota() != calculatedVatQuota);
		double vatGap = (isVatGap) ? reservation.getVatQuota() : 0;

		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		IManagerBean reservationServiceDetailBean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
		Criteria criteria = new Criteria();
		String alias = reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_PROJECT_RESERVATION_ID);
		criteria.addEqualExpression(alias, reservation.getId());
		alias = reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_EXTRA);
		criteria.addEqualExpression(alias, false);
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
			invoiceDetail.setPrice(reservationServiceDetail.getPrice());
			invoiceDetail.setDiscountExpression(new DiscountExpression("0.0"));
			invoiceDetail.setSource(InvoiceSource.DIRECT_INVOICE);
			invoiceDetail.setTaxableBase(reservationServiceDetail.getTaxableBase());
			invoiceDetail.setWorkPlace(reservation.getHotel().getWorkPlace());
			if (isVatGap) {
				invoiceDetail.setTaxDataInDetail(true);
				if (reservation.getVatQuota() != 0) {
					invoiceDetail.setVatPercent(invoiceDetail.getItem().getProduct().getVat().getPercentage());
					double vatQuota = CommonUtil.round(invoiceDetail.getTaxableBase() * invoiceDetail.getVatPercent() / 100);
					if (line == reservationServiceDetailList.size()) {
						vatQuota = vatGap;
					}
					invoiceDetail.setVatQuota(vatQuota);
					vatGap = vatGap - vatQuota;
				} else {
					invoiceDetail.setVatPercent(0);
					invoiceDetail.setVatQuota(0);
				}
			}
			invoiceDetail.getInvoice().setUpdateEnabled(line == reservationServiceDetailList.size());
			invoiceDetailBean.insert(invoiceDetail);
		}
	}

	private void createInvoiceDetails(Invoice invoice, ProjectReservation reservation, ReservationInvoiceTo reservationInvoiceTo) throws ManagerBeanException {
		int line = 0;
		IPriceStrategy strategy = PriceStrategyFactory.getPriceStrategy();

		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		IManagerBean reservationServiceBean = BeanManager.getManagerBean(ProjectReservationService.class);
		IManagerBean reservationServiceDetailBean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
		for (HotelService service : reservationInvoiceTo.getServices()) {
			ProjectReservationService reservationService = new ProjectReservationService();
			if (reservation != null) {
				reservationService.setProjectReservation(reservation);
				reservationService.setItem(service.getItem());
				reservationService.setDescription(service.getItem().getProduct().getName());
				reservationService.setExtra(true);
				reservationService = (ProjectReservationService)reservationServiceBean.insert(reservationService);
			}

			Date date = service.getFromDate();
			while (date.compareTo(service.getToDate()) <= 0) {
				InvoiceDetail invoiceDetail = new InvoiceDetail();
				invoiceDetail.setInvoice(invoice);
				invoiceDetail.setProject((reservation!=null) ? reservation.getProject() : null);
				invoiceDetail.setLine(++line);
				invoiceDetail.setItem(service.getItem());
				invoiceDetail.setDescription(obtainDetailDescription(date, reservationInvoiceTo.getRoom(), service.getItem()));
				invoiceDetail.setQuantity(service.getQuantity());
				invoiceDetail.setPrice(strategy.getUnitPrice(invoiceDetail, date, reservationInvoiceTo.getHotel().getCustomer().getTariff()));
				invoiceDetail.setDiscountExpression(new DiscountExpression("0.0"));
				invoiceDetail.setSource(InvoiceSource.DIRECT_INVOICE);
				invoiceDetail.setTaxableBase(strategy.getBasePrice(invoiceDetail));
				invoiceDetail.setWorkPlace(reservationInvoiceTo.getHotel().getWorkPlace());
				invoiceDetail.getInvoice().setUpdateEnabled(service.equals(reservationInvoiceTo.getLastService()));
				invoiceDetailBean.insert(invoiceDetail);

				if (reservation != null) {
					ProjectReservationServiceDetail reservationServiceDetail = new ProjectReservationServiceDetail();
					reservationServiceDetail.setProjectReservationService(reservationService);
					reservationServiceDetail.setProjectReservationRoomDetail(reservationInvoiceTo.getRoom());
					reservationServiceDetail.setEffectiveDate(date);
					reservationServiceDetail.setQuantity(invoiceDetail.getQuantity());
					reservationServiceDetail.setPrice(invoiceDetail.getPrice());
					reservationServiceDetail.setTaxableBase(invoiceDetail.getTaxableBase());
					reservationServiceDetail.setInvoiceDetail(invoiceDetail);
					reservationServiceDetailBean.insert(reservationServiceDetail);
				}

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
			invoiceAddress.setAddress2(address.getAddress2());
			invoiceAddress.setNumber(address.getNumber()); 
			invoiceAddress.setZip(address.getZip()); 
			invoiceAddress.setCity(address.getCity()); 
			invoiceAddress.setGeozone(address.getGeozone()); 
		}
		invoiceAddress.setInvoice(invoice);
		invoiceAddressBean.insert(invoiceAddress);
	}

	private void createInvoiceFinances(Invoice invoice, ReservationInvoiceTo reservationInvoiceTo) throws ManagerBeanException {
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		for (Finance finance : reservationInvoiceTo.getFinances()) {
			if (finance.getAmount() > 0) {
				if (!reservationInvoiceTo.isDirectCustomer()) {
					FinanceGenerator financeGenerator = new FinanceGenerator();
					financeGenerator.generateFinances(invoice, finance.getAmount());
					break;
				} else {
					finance.setInvoice(invoice);
					finance.setRegistry(invoice.getRegistry());
					finance.setPayment(false);
					finance.setDueDate(invoice.getIssueDate());
					finance.setScope(invoice.getScope());
					finance.setFinanceStatus(FinanceStatus.PENDING);
					financeBean.insert(finance);
				}
			}
		}
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
		String room = null;
		if (reservationRoomDetail != null) {
			IManagerBean reservationRoomDetailBean = BeanManager.getManagerBean(ProjectReservationRoomDetail.class);
			Criteria criteria = new Criteria();
			String alias = reservationRoomDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_PROJECT_RESERVATION_ROOM_ID);
			criteria.addEqualExpression(alias, reservationRoomDetail.getProjectReservationRoom().getId());
			criteria.addEqualExpression(reservationRoomDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_ASSET_ACTIVITY_DATE), effectiveDate);
			for (ITransferObject ito : reservationRoomDetailBean.getList(criteria)) {
				room = ((ProjectReservationRoomDetail)ito).getAssetActivity().getAsset().getName();
				break;
			}
		}
		return obtainDetailDescription(effectiveDate, room, item.getProduct().getName());
	}

	private String obtainDetailDescription(Date effectiveDate, String room, String description) {
    	DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    	String date = StringUtils.rightPad(formatter.format(effectiveDate), 11);
    	room = (room == null) ? " - " : StringUtils.rightPad(room, 6);
    	return (date + room + description);
	}

	private void removeRectifiedServices(Invoice invoice) throws ManagerBeanException {
		List<ProjectReservationService> servicesToRemove = new LinkedList<ProjectReservationService>();
		IManagerBean reservationServiceDetailBean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
		for (ITransferObject ito : invoice.getDetailList()) {
			InvoiceDetail invoiceDetail = (InvoiceDetail)ito;
			Criteria criteria = new Criteria();
			String alias = reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_INVOICE_DETAIL_ID);
			criteria.addEqualExpression(alias, invoiceDetail.getId());
			alias = reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_EXTRA);
			criteria.addEqualExpression(alias, true);
			for (ITransferObject itr : reservationServiceDetailBean.getList(criteria)) {
				ProjectReservationServiceDetail reservationServiceDetail = (ProjectReservationServiceDetail)itr;
				if (!servicesToRemove.contains(reservationServiceDetail.getProjectReservationService())) {
					servicesToRemove.add(reservationServiceDetail.getProjectReservationService());
				}
				reservationServiceDetailBean.remove(reservationServiceDetail);
			}
		}

		IManagerBean reservationServiceBean = BeanManager.getManagerBean(ProjectReservationService.class);
		for (ProjectReservationService reservationService : servicesToRemove) {
			reservationServiceBean.remove(reservationService);
		}
	}

}
