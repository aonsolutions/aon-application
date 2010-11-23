package com.code.aon.product.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.product.Item;
import com.code.aon.product.Product;

/**
 * This class is a ManagerBeanListenerAdapter for Item bean. 
 * An "ManagerBean" event gets fired whenever an operation is performed  
 * over a bean.   
 * 
 * @author Consulting & Development.
 * @since 1.0
 * @see		com.code.aon.common.event.ManagerBeanListenerAdapter
 *
 */
public class ItemListener extends ManagerBeanListenerAdapter {

	
	/**
     * This method gets called when a bean is removed.
     * Removes de product linket to the item removed.
     * 
     * @param evt A ManagerBeanEvent object describing the event source.
	 * 
	 * @see com.code.aon.common.event.ManagerBeanListenerAdapter#beanRemoved(com.code.aon.common.event.ManagerBeanEvent)
	 */
	@Override
	public void beanRemoved(ManagerBeanEvent evt) {
		Item item = (Item)evt.getTo();
		if(item.getProduct().getId() != null){
			try {
				IManagerBean productBean = BeanManager.getManagerBean( Product.class );
				productBean.remove(item.getProduct());
			} catch (ManagerBeanException e) {
				e.printStackTrace();
			}
		}
	}
	
}