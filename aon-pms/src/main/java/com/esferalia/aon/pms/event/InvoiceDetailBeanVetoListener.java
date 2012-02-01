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
import com.esferalia.aon.pms.ProjectReservationServiceDetail;

public class InvoiceDetailBeanVetoListener extends ManagerBeanVetoListenerAdapter {

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
	        	}
    		}
    	} catch (ManagerBeanException ex) {
    		throw new ManagerBeanVetoListenerException(ex.getMessage(), ex);
    	}
    }

}
