package com.code.aon.warehouse.event;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.ql.Criteria;
import com.code.aon.warehouse.IStockable;
import com.code.aon.warehouse.Stock;
import com.code.aon.warehouse.dao.IWarehouseAlias;

/**
 * This class is a ManagerBeanListenerAdapter for IStockable bean. 
 * An "ManagerBean" event gets fired whenever an operation is performed  
 * over a bean.   
 * 
 * @author Consulting & Development.
 * @since 1.0
 * @see		com.code.aon.common.event.ManagerBeanListenerAdapter
 *
 */
public class StockableBeanListener extends ManagerBeanListenerAdapter {
	
	/**
	 * The logger
	 */
	private static final Logger LOGGER = Logger.getLogger(StockableBeanListener.class.getName());

	/**
     * This method gets called when a bean is inserted.
     * It updates the stock.
     * 
     * @param evt A ManagerBeanEvent object describing the event source.
	 * @see com.code.aon.common.event.ManagerBeanListenerAdapter#beanInserted(com.code.aon.common.event.ManagerBeanEvent)
	 */
	@Override
	public void beanInserted(ManagerBeanEvent evt) throws ManagerBeanException {
		IStockable stockable = (IStockable)evt.getTo();
		if(stockable.getItem().getProduct().isInventoriable()){
			Stock stock = obtainStock(stockable);
			IManagerBean stockBean = BeanManager.getManagerBean(Stock.class);
			if(stock == null){
				stock = new Stock();
				stock.setWarehouse(stockable.getWarehouse());
				stock.setItem(stockable.getItem());
				stock.setQuantity(new Double(stockable.getQuantity()));
				stockBean.insert(stock);
			}else{
				stock.setQuantity(new Double((stockable.isEntry()?stock.getQuantity().doubleValue() + stockable.getQuantity():stock.getQuantity().doubleValue() - stockable.getQuantity())));
				stockBean.update(stock);
			}
		}
	}

	/**
	 * Get the stock  
	 * 
	 * @param stockable
	 * @return stock
	 */
	private Stock obtainStock(IStockable stockable) {
		try {
			IManagerBean stockBean = BeanManager.getManagerBean(Stock.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(stockBean.getFieldName(IWarehouseAlias.STOCK_ITEM_ID), stockable.getItem().getId());
			criteria.addEqualExpression(stockBean.getFieldName(IWarehouseAlias.STOCK_WAREHOUSE_ID), stockable.getWarehouse().getId());
			Iterator iter = stockBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return (Stock)iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining stock for item with id= " + stockable.getItem().getId() 
					+ "and warehouse with id=" + stockable.getWarehouse().getId(), e);
		}
		return null;
	}
}
