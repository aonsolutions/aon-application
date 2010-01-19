package com.code.aon.warehouse.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.sales.SalesDetail;
import com.code.aon.sales.enumeration.SalesDetailStatus;
import com.code.aon.warehouse.DeliveryDetail;

/**
 * This class is a ManagerBeanListenerAdapter for DeliveryDetail bean. 
 * An "ManagerBean" event gets fired whenever an operation is performed  
 * over a bean.   
 * 
 * @author Consulting & Development.
 * @since 1.0
 * @see		com.code.aon.common.event.ManagerBeanListenerAdapter
 *
 */
public class DeliveryDetailListener extends ManagerBeanListenerAdapter {

	/**
     * This method gets called when a bean is inserted.
     * It closes the sales detail linked.
     * 
     * @param evt A ManagerBeanEvent object describing the event source.
	 * @see com.code.aon.common.event.ManagerBeanListenerAdapter#beanInserted(com.code.aon.common.event.ManagerBeanEvent)
	 */
	@Override
	public void beanInserted(ManagerBeanEvent evt) {
		DeliveryDetail deliveryDetail = (DeliveryDetail)evt.getTo();
		if(deliveryDetail.getSalesDetail()!=null){
			SalesDetail salesDetail = deliveryDetail.getSalesDetail();
			salesDetail.setStatus(SalesDetailStatus.SETTLED);
			try {
				IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
				salesDetailBean.update(salesDetail);
			} catch (ManagerBeanException e) {
				e.printStackTrace();
			}
		}
	}

}
