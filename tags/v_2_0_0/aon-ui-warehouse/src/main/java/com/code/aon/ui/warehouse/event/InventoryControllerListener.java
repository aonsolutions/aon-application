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
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.warehouse.controller.InventoryDetailController;
import com.code.aon.warehouse.Inventory;
import com.code.aon.warehouse.InventoryDetail;
import com.code.aon.warehouse.dao.IWarehouseAlias;

/**
 * An iventory controller listener 
 * 
 * @author Consulting & Development. 
 * @since 1.0
 *
 */
public class InventoryControllerListener extends ControllerAdapter {
	
	/**
	 * The class logger
	 */
	private static final Logger LOGGER = Logger.getLogger(InventoryControllerListener.class.getName());
	
	/**
	 * The inventorydetail controller's name
	 */
	private static final String INVENTORY_DETAIL_CONTROLLER_NAME = "inventoryDetail";

	/**
	 * Before removing an inventory bean removes all his lines
	 * and updates inventorydetail controller status
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeBeanRemoved(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		Inventory inventory = (Inventory)event.getController().getTo();
		try {
			IManagerBean inventoryDetailBean = BeanManager.getManagerBean(InventoryDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(inventoryDetailBean.getFieldName(IWarehouseAlias.INVENTORY_DETAIL_INVENTORY_ID),inventory.getId());
			Iterator iter = inventoryDetailBean.getList(criteria).iterator();
			while(iter.hasNext()){
				InventoryDetail inventoryDetail = (InventoryDetail)iter.next();
				inventoryDetailBean.remove(inventoryDetail);
			}
			InventoryDetailController inventoryDetailController =(InventoryDetailController)AonUtil.getController(INVENTORY_DETAIL_CONTROLLER_NAME);
			inventoryDetailController.setModel(null);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Exception removing InventoryDetails", e);
		}
	}

	/**
	 * Updates inventorydetail controller status 
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeBeanCanceled(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void beforeBeanCanceled(ControllerEvent event) throws ControllerListenerException {
		InventoryDetailController inventoryDetailController =(InventoryDetailController)AonUtil.getController(INVENTORY_DETAIL_CONTROLLER_NAME);
		inventoryDetailController.setModel(null);
	}
}