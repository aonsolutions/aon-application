package com.esferalia.aon.pms.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.finance.Invoice;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.enumeration.ReservationStatus;

public class InvoiceBeanListener extends ManagerBeanListenerAdapter {

    @Override
    public void beanInserted(ManagerBeanEvent evt) throws ManagerBeanException {
    	Invoice invoice = (Invoice)evt.getTo();
        if (invoice.isAdvance() && invoice.getProject() != null && invoice.getProject().getId() != null) {
        	modifyReservationStatus(invoice,Boolean.TRUE);
        }
    }

    @Override
    public void beanUpdated(ManagerBeanEvent evt) throws ManagerBeanException {
    	Invoice invoice = (Invoice)evt.getTo();
        if (invoice.getProject() != null && invoice.getProject().getId() != null) {
        	modifyReservationStatus(invoice,null);
        }
    }

    @Override
    public void beanRemoved(ManagerBeanEvent evt) throws ManagerBeanException {
    	Invoice invoice = (Invoice)evt.getTo();
        if (invoice.getProject() != null && invoice.getProject().getId() != null) {
        	modifyReservationStatus(invoice,Boolean.FALSE);
        }
    }

    private void modifyReservationStatus(Invoice invoice,Boolean adding) throws ManagerBeanException {
    	ProjectReservation reservation = (ProjectReservation)BeanManager.getManagerBean(ProjectReservation.class).get(invoice.getProject().getId());
    	if (reservation != null) {
        	IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
    		Criteria criteria = new Criteria();
    		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_PROJECT_ID), invoice.getProject().getId());
    		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_SERVICE), false);
    		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_ADVANCE), false);
    		Projection projection = Projection.sum(invoiceBean.getFieldName(IEntityAlias.INVOICE_TOTAL));
    		Object result = invoiceBean.getUniqueResult(projection, criteria);
    		double totalInvoiced = (result != null) ? CommonUtil.round(((Double)result).doubleValue()) : 0;

    		IManagerBean reservationBean = BeanManager.getManagerBean(ProjectReservation.class);
    		reservation.setStatus((totalInvoiced == 0) ? ReservationStatus.ACTIVE : ReservationStatus.INVOICED);
    		if (adding != null && invoice.isAdvance()) {
    			adding = invoice.isRectifier()?!adding:adding;
    			reservation.setAdvanceInvoiced(adding);
    		}
    		reservationBean.update(reservation);
    	}
    }

}
