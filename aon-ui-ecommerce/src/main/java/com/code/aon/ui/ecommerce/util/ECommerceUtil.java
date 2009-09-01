package com.code.aon.ui.ecommerce.util;

import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.dao.ICompanyAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.ecommerce.controller.ShopItemsController;
import com.code.aon.ui.util.AonUtil;

public class ECommerceUtil implements IECommerceConstants{
	
	public static ShopItemsController getShopItems() {
		return (ShopItemsController) AonUtil.getRegisteredBean(SHOP_ITEMS_CONTROLLER);
	}
	
	public static List<ITransferObject> getWorkPlaces() throws ManagerBeanException{
		List<SelectItem> workPlaces = new LinkedList<SelectItem>();
		IManagerBean workplaceBean = BeanManager.getManagerBean(WorkPlace.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(workplaceBean.getFieldName(ICompanyAlias.WORK_PLACE_ID));
		List<ITransferObject> list = workplaceBean.getList(criteria);
		for (ITransferObject to : list) {
			WorkPlace workPlace = (WorkPlace)to;
			workPlaces.add(new SelectItem(workPlace, workPlace.getDescription()));
		}
		//return workPlaces;
		return list;
	}

}
