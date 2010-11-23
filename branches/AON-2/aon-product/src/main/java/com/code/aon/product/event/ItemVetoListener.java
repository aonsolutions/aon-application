package com.code.aon.product.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.product.Item;
import com.code.aon.product.Product;

/**
 * This class is a ManagerBeanVetoListenerAdapter for Item bean. 
 * An "ManagerBean" event gets fired whenever an operation is performed  
 * over a bean. 
 * 
 * @author 	Consulting & Development.
 * @see		com.code.aon.common.event.ManagerBeanVetoListenerAdapter
 *
 */
public class ItemVetoListener extends ManagerBeanVetoListenerAdapter{
	
	/** 
     * This method gets called before a bean is inserted.
     * Creates the product linked to this Item if not exists.
     * 
     * @param evt A ManagerBeanEvent object describing the event source.
	 * 
	 * @see com.code.aon.common.event.ManagerBeanVetoListenerAdapter#vetoableBeanInserted(com.code.aon.common.event.ManagerBeanEvent)
	 */
	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt){
		Item item = (Item)evt.getTo();
		if(item.getProduct().getId() == null){
			try {
				IManagerBean productBean = BeanManager.getManagerBean(Product.class);
				Product prod = (Product)productBean.insert(item.getProduct());
				((Item)evt.getTo()).setProduct(prod);
				((Item)evt.getTo()).setStatus(prod.getStatus());
			} catch (ManagerBeanException e) {
				e.printStackTrace();
			}
		}
	}
}