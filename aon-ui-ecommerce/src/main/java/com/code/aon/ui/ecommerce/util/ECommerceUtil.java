package com.code.aon.ui.ecommerce.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import com.code.aon.bridge.session.DomainResolver;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.dao.ICompanyAlias;
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
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		if(config.getActiveConfig().getBankDraft()!=null){
			payMethods.add(new SelectItem(config.getActiveConfig().getBankDraft(), config.getActiveConfig().getBankDraft().getType().getName(locale)));
		}
		if(config.getActiveConfig().getBankTransfer()!=null){
			payMethods.add(new SelectItem(config.getActiveConfig().getBankTransfer(), config.getActiveConfig().getBankTransfer().getType().getName(locale)));
		}
		if(config.getActiveConfig().getCashOnDelivery()!=null){
			payMethods.add(new SelectItem(config.getActiveConfig().getCashOnDelivery(), config.getActiveConfig().getCashOnDelivery().getType().getName(locale)));
		}
		if(config.getActiveConfig().getPaypal()!=null){
			payMethods.add(new SelectItem(config.getActiveConfig().getPaypal(), config.getActiveConfig().getPaypal().getType().getName(locale)+" ("+config.getActiveConfig().getPaypal().getName()+")"));
		}
		if(config.getActiveConfig().getCreditCard()!=null){
			payMethods.add(new SelectItem(config.getActiveConfig().getCreditCard(), config.getActiveConfig().getCreditCard().getType().getName(locale)));
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
	
private static final String DOMAIN_RESOLVER = "domainResolver";
	
	public static String getDomain(){
//		AuthPrincipal user = UserUtils.getInstance().getPrincipal();
//		return user.getDomain();
		DomainResolver resolver = (DomainResolver) AonUtil.getRegisteredBean(DOMAIN_RESOLVER);
    	String domain = resolver.getDomain();
		return domain;
	}
	
	public static String getUrl(){
		StringBuffer url = new StringBuffer( "http://" );
		url.append( getDomain() );
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		if ( request.getRemotePort() != 80 ) {
			url.append( ":" ).append( String.valueOf(request.getLocalPort()) );
		}
		url.append( "/aon-ecommerce" );
		return url.toString();	
	}

}
