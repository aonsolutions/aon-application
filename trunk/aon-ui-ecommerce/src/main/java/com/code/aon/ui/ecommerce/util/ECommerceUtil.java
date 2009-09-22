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
import com.code.aon.ebackoffice.dao.IEbackofficeAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.ecommerce.controller.ConfigController;
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
	
	public static List<SelectItem> payMethodList(){
		//ConfigController config = (ConfigController)AonUtil.getRegisteredBean(IECommerceConstants.CONFIG_CONTROLLER);
		
		List<SelectItem> payMethods = new LinkedList<SelectItem>();
		//IManagerBean workplaceBean = BeanManager.getManagerBean(WorkPlace.class);
		ConfigController config = (ConfigController)AonUtil.getRegisteredBean(IECommerceConstants.CONFIG_CONTROLLER);
		
		
		//**************************************
		//**************************************
		//**************************************
		// REVISAR LOS VALORES DE LOS PAYMETHOD NO ACTIVOS, NULOS (null o cero ?????)
		//**************************************
		//**************************************
		//**************************************
		if(config.getActiveConfig().getBankDraft().getId()!=0){
			payMethods.add(new SelectItem(config.getActiveConfig().getBankDraft(), config.getActiveConfig().getBankDraft().getName()));
		}
		if(config.getActiveConfig().getBankTransfer().getId()!=0){
			payMethods.add(new SelectItem(config.getActiveConfig().getBankTransfer(), config.getActiveConfig().getBankTransfer().getName()));
		}
		if(config.getActiveConfig().getCashOnDelivery().getId()!=0){
			payMethods.add(new SelectItem(config.getActiveConfig().getCashOnDelivery(), config.getActiveConfig().getCashOnDelivery().getName()));
		}
		if(config.getActiveConfig().getPaypal().getId()!=0){
			payMethods.add(new SelectItem(config.getActiveConfig().getPaypal(), config.getActiveConfig().getPaypal().getName()));
		}
		if(config.getActiveConfig().getVisa().getId()!=0){
			payMethods.add(new SelectItem(config.getActiveConfig().getVisa(), config.getActiveConfig().getVisa().getName()));
		}
		
		return payMethods;
	}
	
//	public List<SelectItem> getAccountPeriods() throws ManagerBeanException, ExpressionException {
//		List<SelectItem> accountPeriods = new LinkedList<SelectItem>();
//		IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
//		Criteria criteria = new Criteria();
//		criteria.addOrder(periodBean.getFieldName(IAccountingAlias.PERIOD_ID), false);
//		Iterator<?> iter = periodBean.getList(criteria).iterator();
//		while (iter.hasNext()) {
//			Period period = (Period) iter.next();
//			SelectItem item = new SelectItem(period, period.getId());
//			accountPeriods.add(item);
//		}
//		return accountPeriods;
//	}

}
