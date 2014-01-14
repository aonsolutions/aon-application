package com.esferalia.aon.pms.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceSource;
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
    		if (invoiceDetail.getInvoice().isSales() && !invoiceDetail.isSkipServiceProcess()) {
    			invoiceDetail.setSkipServiceProcess(invoiceDetail.getSource() == InvoiceSource.RESERVATION || isHotelInvoice(invoiceDetail));
    		}
    	} catch (ManagerBeanException ex) {
    		throw new ManagerBeanVetoListenerException(ex.getMessage(), ex);
    	}
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	InvoiceDetail invoiceDetail = (InvoiceDetail)evt.getTo();
    	try {
    		if (invoiceDetail.getInvoice().isSales() && !invoiceDetail.isSkipServiceProcess()) {
    			invoiceDetail.setSkipServiceProcess(invoiceDetail.getSource() == InvoiceSource.RESERVATION || isHotelInvoice(invoiceDetail));
    		}
    	} catch (ManagerBeanException ex) {
    		throw new ManagerBeanVetoListenerException(ex.getMessage(), ex);
    	}
	}	

	@Override
	public void vetoableBeanRemoved(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	InvoiceDetail invoiceDetail = (InvoiceDetail)evt.getTo();
    	try {
    		if (invoiceDetail.getSource() == InvoiceSource.RESERVATION && invoiceDetail.getSourceId() != null) {
    			Integer serviceDetailId = invoiceDetail.getSourceId();
    			IManagerBean reservationServiceDetailBean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
	    		ProjectReservationServiceDetail reservationServiceDetail = (ProjectReservationServiceDetail)reservationServiceDetailBean.get(serviceDetailId);
	    		if (reservationServiceDetail != null && reservationServiceDetail.getProjectReservationService().isExtra()) {
	        		reservationServiceDetailBean.remove(reservationServiceDetail);
	    			
		    		Criteria criteria = new Criteria();
		        	String alias = reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_ID);
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
