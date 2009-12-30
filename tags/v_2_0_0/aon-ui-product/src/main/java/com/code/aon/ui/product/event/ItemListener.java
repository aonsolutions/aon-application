package com.code.aon.ui.product.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.product.Item;
import com.code.aon.product.Product;

/**
 * Listener added to the ItemController.
 */
public class ItemListener extends ManagerBeanListenerAdapter{

	/**
	 * Bean removed. Removes the product related with the current item
	 * 
	 * @param evt the evt
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	@Override
	public void beanRemoved(ManagerBeanEvent evt) throws ManagerBeanException {
		try {
			IManagerBean productBean = BeanManager.getManagerBean(Product.class);
			Item item = (Item)evt.getTo();
			productBean.remove(item.getProduct());
		} catch (ManagerBeanException e) {
			throw new ManagerBeanException("Could not Remove Product");
		}
	}

	/**
	 * Bean updated. Updates the product related with the current item
	 * 
	 * @param evt the evt
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	@Override
	public void beanUpdated(ManagerBeanEvent evt) throws ManagerBeanException {
		try {
			IManagerBean productBean = BeanManager.getManagerBean(Product.class);
			Item item = (Item)evt.getTo();
			productBean.update(item.getProduct());
		} catch (ManagerBeanException e) {
			throw new ManagerBeanException("Could not Update Product");
		}
	}
}