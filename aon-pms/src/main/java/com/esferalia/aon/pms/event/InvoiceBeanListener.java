package com.esferalia.aon.pms.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.finance.Invoice;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.enumeration.ReservationStatus;

public class InvoiceBeanListener extends ManagerBeanListenerAdapter {

    @Override
    public void beanRemoved(ManagerBeanEvent evt) throws ManagerBeanException {
    	Invoice invoice = (Invoice)evt.getTo();
        if (invoice.getProject() != null && invoice.getProject().getId() != null) {
        	ProjectReservation reservation = (ProjectReservation)BeanManager.getManagerBean(ProjectReservation.class).get(invoice.getProject().getId());
    		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
    		Criteria criteria = new Criteria();
    		criteria.addNotEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_ID), invoice.getId());
    		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_PROJECT_ID), invoice.getProject().getId());
    		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_SERVICE), false);
    		if (invoiceBean.getCount(criteria) == 0) {
	    		IManagerBean reservationBean = BeanManager.getManagerBean(ProjectReservation.class);
	    		reservation.setStatus(ReservationStatus.ACTIVE);
	    		reservationBean.update(reservation);
    		}
    	}
    }

}
