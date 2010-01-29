package com.code.aon.warehouse.event;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.ql.Criteria;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.IStockable;
import com.code.aon.warehouse.IncomeDetail;
import com.code.aon.warehouse.Stock;
import com.code.aon.warehouse.dao.IWarehouseAlias;

/**
 * This class is a ManagerBeanVetoListenerAdapter for IStockable bean. 
 * An "ManagerBean" event gets fired before an operation is performed  
 * over a bean and can abort it.
 * 
 * @author Consulting & Development.
 * @since 1.0
 * @see		com.code.aon.common.event.ManagerBeanListenerAdapter
 *
 */
public class StockableBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	/**
	 * The logger 
	 */
	private static final Logger LOGGER = Logger.getLogger(StockableBeanVetoListener.class.getName());

	/**
     * This method gets called when a bean is going to be removed.
     * Checks if stock exists and if it is of entry o not, and modifies it.
     * 
     * @param evt A ManagerBeanEvent object describing the event source.
	 * @see com.code.aon.common.event.ManagerBeanVetoListenerAdapter#vetoableBeanRemoved(com.code.aon.common.event.ManagerBeanEvent)
	 */
	@Override
	public void vetoableBeanRemoved(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		try {
			IStockable stockable = (IStockable)evt.getTo();
			if(stockable.getItem().getProduct().isInventoriable()){
				Stock stock = obtainStock(stockable);
				IManagerBean stockBean = BeanManager.getManagerBean(Stock.class);
				if(stock != null){
					stock.setQuantity(new Double((stockable.isEntry()?stock.getQuantity().doubleValue() - stockable.getQuantity():stock.getQuantity().doubleValue() + stockable.getQuantity())));
					stockBean.update(stock);
				} else {
					throw new ManagerBeanVetoListenerException("No stock data for item with id= " + stock.getItem().getId() 
							+ "and warehouse with id= " + stock.getWarehouse().getId());
				}
			}
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e);
		}
	}

	/**
     * This method gets called when a bean is going to be removed.
     * Checks if stock exists and if it is of entry o not, and modifies it.
     * 
     * @param evt A ManagerBeanEvent object describing the event source.
	 * @see com.code.aon.common.event.ManagerBeanVetoListenerAdapter#vetoableBeanUpdated(com.code.aon.common.event.ManagerBeanEvent)
	 */
	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		IStockable newStockable = (IStockable)evt.getTo();
		if(newStockable.getItem().getProduct().isInventoriable()){
			IStockable oldStockable;
			if(newStockable.isEntry()){
				oldStockable = obtainIncomeDetail(newStockable);
			}else{
				oldStockable = obtainDeliveryDetail(newStockable);
			}
			double diff = (newStockable.isEntry()?newStockable.getQuantity() - oldStockable.getQuantity():oldStockable.getQuantity() - newStockable.getQuantity());
			try {
				IManagerBean stockBean = BeanManager.getManagerBean(Stock.class);
				Stock stock = obtainStock(newStockable);
				if(stock != null){
					stock.setQuantity(new Double(stock.getQuantity().doubleValue() + diff));
					stockBean.update(stock);
				} else {
					throw new ManagerBeanVetoListenerException("No stock data for item with id= " + stock.getItem().getId() 
							+ "and warehouse with id= " + stock.getWarehouse().getId());
				}
			} catch (ManagerBeanException e) {
				throw new ManagerBeanVetoListenerException(e);
			}
		}
	}
	
	/**
	 * Returns the Stock of a Stockable
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
	
	/**
	 * Obtains an IStockable from the Income Detail and a IStockatble
	 * 
	 * @param stockable
	 * @return stock
	 */
	private IStockable obtainIncomeDetail(IStockable stockable) {
		IncomeDetail incomeDetail = (IncomeDetail)stockable;
		try {
			IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(incomeDetailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_ID), incomeDetail.getId());
			Iterator iter = incomeDetailBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return (IStockable)iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining incomeDetail with id= " + incomeDetail.getId() , e);
		}
		return null;
	}
	
	/**
	 * Obtains an IStockable from the Delivery Detail and a IStockatble
	 * 
	 * @param stockable
	 * @return stock
	 */
	private IStockable obtainDeliveryDetail(IStockable stockable) {
		DeliveryDetail deliveryDetail = (DeliveryDetail)stockable;
		try {
			IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(deliveryDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_ID), deliveryDetail.getId());
			Iterator iter = deliveryDetailBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return (IStockable)iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining deliveryDetail with id= " + deliveryDetail.getId() , e);
		}
		return null;
	}
}