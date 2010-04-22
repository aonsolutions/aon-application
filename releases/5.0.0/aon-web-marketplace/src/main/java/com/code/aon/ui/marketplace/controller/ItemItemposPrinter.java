package com.code.aon.ui.marketplace.controller;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.product.ItemPos;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.product.controller.ItemController;
import com.code.aon.ui.util.AonUtil;

public class ItemItemposPrinter implements ICollectionProvider {
	
	private static final Logger LOGGER = Logger.getLogger(ItemItemposPrinter.class.getName());
	
	private static final String ITEM_CONTROLLER_NAME = "item";

	@SuppressWarnings("unchecked")
	public Collection getCollection() {
		List<ItemPos> reportItemposList = new LinkedList<ItemPos>();
		try {
			ItemController itemController = (ItemController)AonUtil.getController(ITEM_CONTROLLER_NAME);
			Iterator iter = itemController.getManagerBean().getList(itemController.getCriteria()).iterator();
			while(iter.hasNext()){
				Item item = (Item)iter.next();
				IManagerBean itemPosBean = BeanManager.getManagerBean(ItemPos.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(itemPosBean.getFieldName(IProductAlias.ITEM_POS_ITEM_ID), item.getId());
				Iterator<ITransferObject> iterItemPos = itemPosBean.getList(criteria).iterator();
				if (iterItemPos.hasNext()) {
					reportItemposList.add((ItemPos)iterItemPos.next());
				}else{
					ItemPos temp = new ItemPos();
					temp.setItem(item);
					temp.setBarcode("");
					reportItemposList.add(temp);
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining Item with BarCode Collection", e);
		}
		return reportItemposList;
	}

	@SuppressWarnings("unchecked")
	public Collection getCollection(boolean arg0) throws ManagerBeanException {
		return getCollection();
	}
}
