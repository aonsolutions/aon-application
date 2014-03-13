package com.code.aon.ui.warehouse.event;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.warehouse.InventoryDetail;
import com.code.aon.warehouse.Stock;
import com.esferalia.aon.entity.IEntityAlias;

public class InventoryDetailControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InventoryDetailControllerListener.class.getName());

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		InventoryDetail inventoryDetail = (InventoryDetail)event.getController().getTo();
		try {
			double quantity = inventoryDetail.getRealQuantity();
			Double q = new Double(quantity);
			IManagerBean stockBean = BeanManager.getManagerBean(Stock.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(stockBean.getFieldName(IEntityAlias.STOCK_ITEM_ID) ,inventoryDetail.getItem().getId());
			Iterator<?> stockListIter = stockBean.getList(criteria).iterator();
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
			LOGGER.error("Exception removing InventoryDetails", e);
		}
	}
}