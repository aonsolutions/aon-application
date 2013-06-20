package com.esferalia.aon.pms.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservationService;
import com.esferalia.aon.pms.ProjectReservationServiceDetail;

public class InvoiceDetailBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	InvoiceDetail invoiceDetail = (InvoiceDetail)evt.getTo();
    	try {
    		if (!invoiceDetail.isSkipServiceProcess()) {
    			invoiceDetail.setSkipServiceProcess(isHotelInvoice(invoiceDetail));
    		}
    	} catch (ManagerBeanException ex) {
    		throw new ManagerBeanVetoListenerException(ex.getMessage(), ex);
    	}
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	InvoiceDetail invoiceDetail = (InvoiceDetail)evt.getTo();
    	try {
    		if (!invoiceDetail.isSkipServiceProcess()) {
    			invoiceDetail.setSkipServiceProcess(isHotelInvoice(invoiceDetail));
    		}
    	} catch (ManagerBeanException ex) {
    		throw new ManagerBeanVetoListenerException(ex.getMessage(), ex);
    	}
	}	

	@Override
	public void vetoableBeanRemoved(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	InvoiceDetail invoiceDetail = (InvoiceDetail)evt.getTo();
    	try {
    		if (invoiceDetail.getInvoice().isService()) {
	    		IManagerBean reservationServiceDetailBean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
	        	Criteria criteria = new Criteria();
	        	String alias = reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_INVOICE_DETAIL_ID);
	        	criteria.addEqualExpression(alias, invoiceDetail.getId());
	        	for (ITransferObject ito : reservationServiceDetailBean.getList(criteria)) {
	        		ProjectReservationServiceDetail reservationServiceDetail = (ProjectReservationServiceDetail)ito;
	        		reservationServiceDetailBean.remove(reservationServiceDetail);

	        		criteria = new Criteria();
		        	alias = reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_ID);
		        	criteria.addEqualExpression(alias, reservationServiceDetail.getProjectReservationService().getId());
	        		if (reservationServiceDetailBean.getCount(criteria) == 0) {
	    	    		IManagerBean reservationServiceBean = BeanManager.getManagerBean(ProjectReservationService.class);
	    	    		reservationServiceBean.remove(reservationServiceDetail.getProjectReservationService());
	        		}
	        	}
    		}
    	} catch (ManagerBeanException ex) {
    		throw new ManagerBeanVetoListenerException(ex.getMessage(), ex);
    	}
    }

	private boolean isHotelInvoice(InvoiceDetail invoiceDetail) throws ManagerBeanException {
		IManagerBean hotelBean = BeanManager.getManagerBean(Hotel.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(hotelBean.getFieldName(IEntityAlias.HOTEL_WORK_PLACE_ID), invoiceDetail.getWorkPlace().getId());
		return (hotelBean.getCount(criteria) > 0);
	}

}
