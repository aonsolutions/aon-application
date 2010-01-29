package com.code.aon.ui.warehouse.event;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.warehouse.InventoryDetail;
import com.code.aon.warehouse.Stock;
import com.code.aon.warehouse.dao.IWarehouseAlias;

/**
 * An iventorydetail controller listener 
 * 
 * @author Consulting & Development. 
 * @since 1.0
 *
 */
public class InventoryDetailControllerListener extends ControllerAdapter {
	
	/**
	 * The class logger
	 */
	private static final Logger LOGGER = Logger.getLogger(InventoryDetailControllerListener.class.getName());

	/**
	 * After updating the inventory search the stock and updates it or creates if needed
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#afterBeanUpdated(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		InventoryDetail inventoryDetail = (InventoryDetail)event.getController().getTo();
		try {
			double quantity = inventoryDetail.getRealQuantity();
			Double q = new Double(quantity);
			IManagerBean stockBean = BeanManager.getManagerBean(Stock.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(stockBean.getFieldName(IWarehouseAlias.STOCK_ITEM_ID) ,inventoryDetail.getItem().getId());
			Iterator stockListIter = stockBean.getList(criteria).iterator();
			if (stockListIter.hasNext()){
				Stock stock = (Stock) stockListIter.next();
				stock.setQuantity(q);
				stockBean.update(stock);
			}else{
				Stock stock = new Stock();
				stock.setItem(inventoryDetail.getItem());
				stock.setQuantity(q);
				stock.setWarehouse(inventoryDetail.getInventory().getWarehouse());
				stockBean.insert(stock);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Exception removing InventoryDetails", e);
		}
	}
}