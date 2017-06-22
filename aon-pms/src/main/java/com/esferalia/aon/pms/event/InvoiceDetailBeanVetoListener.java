package com.esferalia.aon.pms.event;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.InvoiceTax;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservationService;
import com.esferalia.aon.pms.ProjectReservationServiceDetail;

public class InvoiceDetailBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	InvoiceDetail invoiceDetail = (InvoiceDetail)evt.getTo();
    	try {
    		if (invoiceDetail.getInvoice().isSales()) {
    			boolean reservationInvoice = invoiceDetail.getSource() == InvoiceSource.RESERVATION || isHotelInvoice(invoiceDetail);
    			if (!invoiceDetail.isSkipServiceProcess()) {
        			invoiceDetail.setSkipServiceProcess(reservationInvoice);
    			}
    			if (reservationInvoice && !invoiceDetail.isTaxDataInDetail() && isInvoiceTaxDataInDetail(invoiceDetail)) {
    				invoiceDetail.setTaxDataInDetail(true);
   					invoiceDetail.setVatPercent(invoiceDetail.getItem().getProduct().getVat().getPercentage());
   					invoiceDetail.setVatQuota(CommonUtil.round(invoiceDetail.getTaxableBase() * invoiceDetail.getVatPercent() / 100));
    			}
    		}
    	} catch (ManagerBeanException ex) {
    		throw new ManagerBeanVetoListenerException(ex.getMessage(), ex);
    	}
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	InvoiceDetail invoiceDetail = (InvoiceDetail)evt.getTo();
    	try {
    		if (invoiceDetail.getInvoice().isSales()) {
    			boolean reservationInvoice = invoiceDetail.getSource() == InvoiceSource.RESERVATION || isHotelInvoice(invoiceDetail);
    			if (!invoiceDetail.isSkipServiceProcess()) {
        			invoiceDetail.setSkipServiceProcess(reservationInvoice);
    			}
    			if (reservationInvoice && !invoiceDetail.isTaxDataInDetail() && isInvoiceTaxDataInDetail(invoiceDetail)) {
    				invoiceDetail.setTaxDataInDetail(true);
   					invoiceDetail.setVatPercent(invoiceDetail.getItem().getProduct().getVat().getPercentage());
   					invoiceDetail.setVatQuota(CommonUtil.round(invoiceDetail.getTaxableBase() * invoiceDetail.getVatPercent() / 100));
    			}
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
	    			if (!reservationServiceDetail.hasProduction()) {
		    			reservationServiceDetailBean.remove(reservationServiceDetail);

			    		Criteria criteria = new Criteria();
			        	String alias = reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_ID);
			        	criteria.addEqualExpression(alias, reservationServiceDetail.getProjectReservationService().getId());
		        		if (reservationServiceDetailBean.getCount(criteria) == 0) {
		    	    		IManagerBean reservationServiceBean = BeanManager.getManagerBean(ProjectReservationService.class);
		    	    		reservationServiceBean.remove(reservationServiceDetail.getProjectReservationService());
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

	private boolean isInvoiceTaxDataInDetail(InvoiceDetail invoiceDetail) throws ManagerBeanException {
		IManagerBean invoiceTaxBean = BeanManager.getManagerBean(InvoiceTax.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceTaxBean.getFieldName(IEntityAlias.INVOICE_TAX_INVOICE_DETAIL_INVOICE_ID), invoiceDetail.getInvoice().getId());
		criteria.addNotEqualExpression(invoiceTaxBean.getFieldName(IEntityAlias.INVOICE_TAX_QUOTA), Double.valueOf(0));
		return (invoiceTaxBean.getCount(criteria) > 0);
	}

}
