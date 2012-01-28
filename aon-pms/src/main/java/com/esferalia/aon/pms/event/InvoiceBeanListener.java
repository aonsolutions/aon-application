package com.esferalia.aon.pms.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.RectificationType;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.enumeration.ReservationStatus;

public class InvoiceBeanListener extends ManagerBeanListenerAdapter {

    @Override
    public void beanRemoved(ManagerBeanEvent evt) throws ManagerBeanException {
    	Invoice invoice = (Invoice)evt.getTo();
    	if (invoice.getRectificationType() == RectificationType.NONE && invoice.getProject() != null && invoice.getProject().getId() != null) {
	    	IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
	    	IManagerBean reservationBean = BeanManager.getManagerBean(ProjectReservation.class);
	    	Criteria criteria = new Criteria();
	    	criteria.addEqualExpression(reservationBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_PROJECT_ID), invoice.getProject().getId());
	    	for (ITransferObject ito : reservationBean.getList(criteria)) {
	    		ProjectReservation reservation = (ProjectReservation)ito;
	    		criteria = new Criteria();
	    		criteria.addNotEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_ID), invoice.getId());
	    		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_PROJECT_ID), invoice.getProject().getId());
	    		if (invoiceBean.getCount(criteria) == 0) {
		    		reservation.setStatus(ReservationStatus.ACTIVE);
		    		reservationBean.update(reservation);
	    		}
	    	}
    	}
    }

}
