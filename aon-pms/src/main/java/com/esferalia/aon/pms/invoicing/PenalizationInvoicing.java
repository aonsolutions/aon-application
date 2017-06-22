package com.esferalia.aon.pms.invoicing;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationServiceDetail;
import com.esferalia.aon.pms.reservation.IReservationConstants;

public class PenalizationInvoicing implements IReservationConstants {

	private static final Logger LOGGER = LoggerFactory.getLogger(PenalizationInvoicing.class.getName());
	
	public Invoice agencyCheckOutInvoice(ReservationInvoiceTo reservationInvoiceTo, ProjectReservation reservation) throws ManagerBeanException {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);

			HibernateUtil.beginTransaction(sessionName);
			
			Invoice invoice = createInvoice(reservationInvoiceTo, reservation);
			createInvoiceDetails(invoice, reservationInvoiceTo, reservation);
			updateInvoiceDate(invoice);
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

	private Invoice createInvoice(ReservationInvoiceTo reservationInvoiceTo, ProjectReservation reservation) throws ManagerBeanException {
		Invoice invoice = new Invoice();
		invoice.setProject(reservation.getProject());
		invoice.setSeries(reservationInvoiceTo.getSeries());
		invoice.setNumber(reservationInvoiceTo.getNumber());
		invoice.setRegistry(reservationInvoiceTo.getRegistry());
		invoice.setRegistryDocument(reservationInvoiceTo.getRegistry().getDocument());
		invoice.setRegistryDocumentType(reservationInvoiceTo.getRegistry().getDocumentType());
		invoice.setRegistryDocumentCountry(reservationInvoiceTo.getRegistry().getDocumentCountry());
		invoice.setRegistryName(reservationInvoiceTo.getRegistry().getName());
		invoice.setRegistryAddress(!reservationInvoiceTo.isDirectCustomer() ? reservationInvoiceTo.getRegistry().getDefaultAddress() : null);
		invoice.setIssueDate(reservationInvoiceTo.getIssueDate());
		invoice.setSecurityLevel(SecurityLevel.OFFICIAL);
		invoice.setStatus(InvoiceStatus.PENDING);
		invoice.setType(InvoiceType.SALES);
		invoice.setScope(reservation.getHotelReservation().getScope());
		invoice.setService(false);
		invoice.setComments(reservationInvoiceTo.getComments());
		invoice.setPosShift(reservationInvoiceTo.getPosShift());

		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		return (Invoice)invoiceBean.insert(invoice);
	}

	private void createInvoiceDetails(Invoice invoice, ReservationInvoiceTo reservationInvoiceTo, ProjectReservation reservation) throws ManagerBeanException {
		int line = 0;
		double taxableBase = 0;

		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		IManagerBean reservationServiceDetailBean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
		Criteria criteria = new Criteria();
		String alias = reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_PROJECT_RESERVATION_ID);
		criteria.addEqualExpression(alias, reservation.getId());
		alias = reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_EFFECTIVE_DATE);
		criteria.addGreaterThanOrEqualExpression(alias, reservationInvoiceTo.getEarlyCheckOutDate());
		alias = reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_EXTRA);
		criteria.addEqualExpression(alias, Boolean.FALSE);
		alias = reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_REMOVED);
		criteria.addEqualExpression(alias, Boolean.FALSE);
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
			invoiceDetail.setQuantity(reservationServiceDetail.getQuantity() * (-1));
			invoiceDetail.setDiscountExpression(new DiscountExpression("0.0"));
			invoiceDetail.setPrice(reservationServiceDetail.getPrice());
			invoiceDetail.setSource(InvoiceSource.RESERVATION);
			invoiceDetail.setSourceId(reservationServiceDetail.getId());
			invoiceDetail.setTaxableBase(reservationServiceDetail.getTaxableBase() * (-1));
			invoiceDetail.setWorkPlace(reservation.getHotelReservation().getWorkPlace());
			invoiceDetail.getInvoice().setUpdateEnabled(line == reservationServiceDetailList.size());
			invoiceDetailBean.insert(invoiceDetail);
			taxableBase = CommonUtil.round(taxableBase + invoiceDetail.getTaxableBase(), 4);
		}

		InvoiceDetail invoiceDetail = new InvoiceDetail();
		invoiceDetail.setInvoice(invoice);
		invoiceDetail.setProject(reservation.getProject());
		invoiceDetail.setLine(++line);
		invoiceDetail.setItem(reservationInvoiceTo.getPenaltyItem());
		invoiceDetail.setDescription(obtainDetailDescription(reservationInvoiceTo.getEarlyCheckOutDate(), null, invoiceDetail.getItem().getFullName()));
		invoiceDetail.setQuantity(1);
		invoiceDetail.setDiscountExpression(new DiscountExpression("0.0"));
		invoiceDetail.setPrice(taxableBase * (-1));
		invoiceDetail.setSource(InvoiceSource.DIRECT_INVOICE);
		invoiceDetail.setTaxableBase(taxableBase * (-1));
		invoiceDetail.setWorkPlace(reservation.getHotelReservation().getWorkPlace());
		invoiceDetail.getInvoice().setUpdateEnabled(true);
		invoiceDetailBean.insert(invoiceDetail);
	}

	private void updateInvoiceDate(Invoice invoice) throws ManagerBeanException {
		if (!DateUtils.isSameDay(invoice.getIssueDate(), new Date())) {
			invoice.setIssueDate(new Date());
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

	private String obtainDetailDescription(Date effectiveDate, String room, String description) {
    	DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    	String date = StringUtils.rightPad(formatter.format(effectiveDate), 11);
    	room = (room == null) ? StringUtils.rightPad(StringUtils.repeat("-", 5), 6) : StringUtils.rightPad(room, 6);
    	return (date + room + description);
	}

}
