package com.code.aon.ui.sales.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.sales.PointOfSale;
import com.code.aon.sales.dao.ISalesAlias;
import com.code.aon.sales.enumeration.DocumentType;
import com.code.aon.sales.enumeration.SalesStatus;
import com.code.aon.sales.enumeration.SellerStatus;

/**
 * Controller used to get Collections related with clasess in <code>com.code.aon.sales</code>
 * 
 * @author Consulting & Development. igayarre - 22-jun-2006
 */
public class SalesCollectionsController {
	
	/**
	 * Gets the Points of sale.
	 * 
	 * @return the Points of sale
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	@SuppressWarnings("unchecked")
	public List<SelectItem> getPointsOfSale() throws ManagerBeanException {
		List<SelectItem>pointsOfSale = new LinkedList<SelectItem>();
		IManagerBean pointOfSaleBean = BeanManager.getManagerBean(PointOfSale.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(pointOfSaleBean.getFieldName(ISalesAlias.POINT_OF_SALE_DESCRIPTION));
		Iterator iter = pointOfSaleBean.getList(criteria).iterator();
		while(iter.hasNext()){
			PointOfSale pos = (PointOfSale)iter.next();
			SelectItem item = new SelectItem(pos, pos.getDescription());
			pointsOfSale.add(item);
		}
		return pointsOfSale;
	}

	public List<SelectItem> getSellerStatuses() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		LinkedList<SelectItem> statuses = new LinkedList<SelectItem>();
		for( SellerStatus status : SellerStatus.values() ) {
			String name = status.getName(locale);
			SelectItem item = new SelectItem(status, name);
			statuses.add(item);
		}
		return statuses;
	}

	public List<SelectItem> getSalesStatuses() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		LinkedList<SelectItem> statuses = new LinkedList<SelectItem>();
		for( SalesStatus status : SalesStatus.values() ) {
			String name = status.getName(locale);
			SelectItem item = new SelectItem(status, name);
			statuses.add(item);
		}
		return statuses;
	}

	public List<SelectItem> getDocumentTypes() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		LinkedList<SelectItem> types = new LinkedList<SelectItem>();
		for( DocumentType type : DocumentType.values() ) {
			String name = type.getName(locale);
			SelectItem item = new SelectItem(type, name);
			types.add(item);
		}
		return types;
	}

}