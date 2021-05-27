package com.esferalia.aon.pms.event;

import java.util.Date;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.enumeration.ReservationCheckStatus;
import com.esferalia.aon.pms.enumeration.ReservationStatus;

public class InvoiceBeanListener extends ManagerBeanListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

    @Override
    public void beanInserted(ManagerBeanEvent evt) throws ManagerBeanException {
    	Invoice invoice = (Invoice)evt.getTo();
        if (invoice.getProject() != null && invoice.getProject().getId() != null && invoice.getProject().isReservation()) {
        	modifyReservationStatus(invoice);
        }
    }

    @Override
    public void beanUpdated(ManagerBeanEvent evt) throws ManagerBeanException {
    	Invoice invoice = (Invoice)evt.getTo();
        if (invoice.getProject() != null && invoice.getProject().getId() != null && invoice.getProject().isReservation()) {
        	modifyReservationStatus(invoice);
        }
    }

    @Override
    public void beanRemoved(ManagerBeanEvent evt) throws ManagerBeanException {
    	Invoice invoice = (Invoice)evt.getTo();
        if (invoice.getProject() != null && invoice.getProject().getId() != null && invoice.getProject().isReservation()) {
        	modifyReservationStatus(invoice);
        }
    }

    private void modifyReservationStatus(Invoice invoice) throws ManagerBeanException {
		IManagerBean reservationBean = BeanManager.getManagerBean(ProjectReservation.class);
    	ProjectReservation reservation = (ProjectReservation)reservationBean.get(invoice.getProject().getId());
    	if (reservation != null) {
        	IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
    		Criteria criteria = new Criteria();
    		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_PROJECT_ID), invoice.getProject().getId());
    		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_TYPE), InvoiceType.SALES);
    		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_SERVICE), false);
    		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_ADVANCE), invoice.isAdvance());
    		Projection projection = Projection.sum(invoiceBean.getFieldName(IEntityAlias.INVOICE_TOTAL));
    		Object result = invoiceBean.getUniqueResult(projection, criteria);
    		double totalInvoiced = (result != null) ? CommonUtil.round(((Double)result).doubleValue()) : 0;
    		projection = Projection.countDistinct(invoiceBean.getFieldName(IEntityAlias.INVOICE_ID));
    		result = invoiceBean.getUniqueResult(projection, criteria);
    		int countInvoices = (result != null) ? ((Integer)result).intValue() : 0;

			boolean invoiced = (totalInvoiced != 0 || countInvoices % 2 != 0);
    		if (!invoice.isAdvance()) {
    			boolean cancelled = (reservation.isCancelled() || reservation.isNoShow());
       			reservation.setStatus((invoiced) ? ReservationStatus.INVOICED : (cancelled) ? ReservationStatus.CANCELLED : ReservationStatus.ACTIVE);
				if (reservation.isInvoiced() && reservation.isNoCheck()) {
					reservation.setCheckStatus(reservation.getEndDate().after(new Date()) ? ReservationCheckStatus.CHECK_IN : ReservationCheckStatus.CHECK_OUT);
				} else if (reservation.isActive() && !reservation.isNoCheck()) {
					reservation.setCheckStatus(ReservationCheckStatus.NO_CHECK);
				}
    		} else {
        		reservation.setAdvanceInvoiced(invoiced);
        		reservation.setAdvance(0);
    		}
    		reservationBean.update(reservation);
    	}
    }

}
