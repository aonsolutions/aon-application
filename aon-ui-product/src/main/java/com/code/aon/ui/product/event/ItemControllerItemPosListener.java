package com.code.aon.ui.product.event;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.product.ItemPos;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.product.enumeration.PluProductType;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

/**
 * Listener Added to ItemController
 * 
 */
public class ItemControllerItemPosListener extends ControllerAdapter {
	
	/** LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(ItemControllerItemPosListener.class.getName());
	
	/** ITEMPOS_CONTROLLER_NAME. */
	private static final String ITEMPOS_CONTROLLER_NAME = "itemPos";
	
	/**
	 * After bean added. Creates a ItemPos with the current item info
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Item item = (Item)event.getController().getTo();
		try {
			IManagerBean itemPosBean = BeanManager.getManagerBean(ItemPos.class);
			ItemPos itemPos = new ItemPos();
			itemPos.setItem(item);
			itemPos.setShortDescription((item.getDescription()!=null?item.getDescription():"") + (item.getProduct().getName()!=null?item.getProduct().getName():""));
			itemPos.setPlu(item.getProduct().getCode());
			itemPos.setBarcode("" + item.getId());
			itemPos.setPluProductType(PluProductType.WEITHED);
			itemPosBean.insert(itemPos);
			reloadItemPosModel(item.getId());
			
		} catch (ManagerBeanException e) {
            AonUtil.addErrorMessage("Error al insertar PLU.");
            LOGGER.error("Error al insertar PLU.", e);
		}
	}

	/**
	 * Reload itemPosController model, getting the itemPos related with the current item.
	 * 
	 * @param id the id
	 */
	private void reloadItemPosModel(Integer id) {
		IController itemPosController = FormUtil.getController(ITEMPOS_CONTROLLER_NAME);
		try {
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(itemPosController.getManagerBean().getFieldName(IProductAlias.ITEM_POS_ITEM_ID), id);
			itemPosController.setCriteria(criteria);
			itemPosController.onSearch(null);
		} catch (ManagerBeanException e) {
			LOGGER.error("Error reloading ItemPosModel", e);
		}
	}
	
	/**
	 * Removes the ItemPos linked to The Item
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		try{
			Item item = (Item)event.getController().getTo();
			IManagerBean itemPosBean = BeanManager.getManagerBean( ItemPos.class );
			Criteria criteriaip = new Criteria();
			criteriaip.addEqualExpression(itemPosBean.getFieldName(IProductAlias.ITEM_POS_ITEM_ID),item.getId());
			List<ITransferObject> itemposlst = itemPosBean.getList(criteriaip);
			Iterator<ITransferObject> itempositer = itemposlst.iterator(); 
			if (itempositer.hasNext()){
				itemPosBean.remove(itempositer.next());
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
}