package com.esferalia.aon.pms.reservation;

import java.util.Date;
import java.util.List;

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
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.IAddress;
import com.code.aon.registry.Registry;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationServiceDetail;

public class ReservationInvoicing implements IReservationConstants {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReservationInvoicing.class.getName());
	
	public void invoice(ProjectReservation reservation, String series, int number, Registry registry, IAddress address, List<Finance> finances) throws ManagerBeanException {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);

			HibernateUtil.beginTransaction(sessionName);
			
			Invoice invoice = createInvoice(reservation, series, number, registry);
			createInvoiceDetails(invoice, reservation);
			createInvoiceAddress(invoice, reservation, address);
			createInvoiceFinances(invoice, finances);
			recordInvoice(invoice);

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

	public void rectify(Invoice invoice, String series, int number, Date date, String cause) throws ManagerBeanException {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);

			HibernateUtil.beginTransaction(sessionName);
			
			RectificationInvoicingManager rectificationManager = new RectificationInvoicingManager();
			Invoice rectifier = rectificationManager.rectifyInvoice(invoice, series, number, date, cause);
			recordInvoice(rectifier);

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

	private Invoice createInvoice(ProjectReservation reservation, String series, int number, Registry registry) throws ManagerBeanException {
		Invoice invoice = new Invoice();
		invoice.setProject(reservation.getProject());
		invoice.setSeries(series);
		invoice.setNumber(number);
		invoice.setRegistry(registry);
		invoice.setRegistryDocument(registry.getDocument());
		invoice.setRegistryDocumentType(registry.getDocumentType());
		invoice.setRegistryDocumentCountry(registry.getDocumentCountry());
		invoice.setRegistryName(registry.getName());
		invoice.setRegistryAddress(null);
		invoice.setIssueDate(reservation.getStartDate());
		invoice.setSecurityLevel(SecurityLevel.OFFICIAL);
		invoice.setStatus(InvoiceStatus.PENDING);
		invoice.setType(InvoiceType.SALES);
		invoice.setScope(reservation.getHotel().getScope());

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
			invoiceDetail.setDescription(reservationServiceDetail.getProjectReservationService().getDescription());
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
			invoiceDetail.setUpdateEnabled(line == reservationServiceDetailList.size());
			invoiceDetail = (InvoiceDetail)invoiceDetailBean.insert(invoiceDetail);

			reservationServiceDetail.setInvoiceDetail(invoiceDetail);
			reservationServiceDetailBean.update(reservationServiceDetail);
		}
	}

	private void createInvoiceAddress(Invoice invoice, ProjectReservation reservation, IAddress address) throws ManagerBeanException {
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

	private void createInvoiceFinances(Invoice invoice, List<Finance> finances) throws ManagerBeanException {
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		for (Finance finance : finances) {
			if (finance.getAmount() > 0) {
				finance.setInvoice(invoice);
				finance.setPayment(false);
				finance.setDueDate(invoice.getIssueDate());
				finance.setScope(invoice.getScope());
				finance.setFinanceStatus(FinanceStatus.PENDING);
				financeBean.insert(finance);
			}
		}
	}

	private void recordInvoice(Invoice invoice) throws ManagerBeanException {
		AccountEntryInvoiceWriter entryWriter = new AccountEntryInvoiceWriter();
		entryWriter.recordAndUpdateInvoice(invoice);
	}

}
