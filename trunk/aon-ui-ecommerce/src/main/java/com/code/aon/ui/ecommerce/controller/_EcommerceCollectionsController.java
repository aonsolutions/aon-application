package com.code.aon.ui.ecommerce.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.event.ActionEvent;

import org.richfaces.component.html.HtmlDataTable;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.product.Catalogue;
import com.code.aon.product.CatalogueItem;
import com.code.aon.product.Item;
import com.code.aon.product.ProductCategory;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.product.controller.ProductCollectionsController;


public class _EcommerceCollectionsController {
	
	private static final String PCATEGORY_CONTROLLER = "productCategory";
	private static final String PRODUCT_COLLECTIONS_CONTROLLER = "productCollections";
	private static final String CATALOGUE_CONTROLLER = "catalogue";
	List<ProductCategory> categories;
	List<Item> categoryItems;
	
	
	/**
	 * Gets the categories grouped by Catalogues.
	 * 
	 * @return the catalogueItems
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
//	public List<ProductCategory> getCatalogueCategories(){
//		return categories;
//	}
	
	/**
	 * Gets the products grouped by Catalogues.
	 * 
	 * @return the catalogueItems
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
//	public List<Item> getCategoryItems(){
//		return categoryItems;
//	}
	
	/**
	 * Refresh the categories grouped by Catalogues.
	 * 
	 * @return the catalogueItems
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
//	public void refreshCatalogueCategories(ActionEvent event) throws ManagerBeanException {
//		categories = new LinkedList<ProductCategory>();
//		IManagerBean categoryBean = BeanManager.getManagerBean(ProductCategory.class);
//		
//		
//		Criteria criteria = new Criteria();
//		criteria.addEqualExpression(categoryBean.getFieldName(IProductAlias.PRODUCT_CATEGORY_ID),1);
//		
//		
////		UIComponent c = event.getComponent().getParent();
////		HtmlDataTable o = (HtmlDataTable) c;
////		int idx = (Integer) o.getValue();
////		
////		System.out.println("+++++++++++++++" + idx);
//		
//		//((ProductCollectionsController)FormUtil.getController(PRODUCT_COLLECTIONS_CONTROLLER)).getCatalogues();
//		
//		
//		//ProductCollectionsController con = FormUtil.getController(PRODUCT_COLLECTIONS_CONTROLLER);
//		
//		Iterator<ITransferObject> iter = categoryBean.getList(criteria).iterator();
//		while (iter.hasNext()) {
//			ProductCategory category = (ProductCategory) iter.next();
//			categories.add(category);
//		}
//	}
	
	
	/**
	 * Refresh the products grouped by Catalogues.
	 * 
	 * @return the catalogueItems
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
//	public void refreshCategoryItems(ActionEvent event) throws ManagerBeanException {
//		categoryItems = new LinkedList<Item>();
//		IManagerBean catalogueItemBean = BeanManager.getManagerBean(CatalogueItem.class);
//		BasicController catalogueController = ((BasicController)FormUtil.getController(CATALOGUE_CONTROLLER)); 
//		
//		
//		event.getSource();
//		UIComponent c = event.getComponent().getParent();
//		HtmlDataTable o = (HtmlDataTable) c;
//		int idx = (Integer) o.getValue();
//		
//		
//		if(catalogueController.getSelectedIndex()<=0){
//			((BasicController)FormUtil.getController(CATALOGUE_CONTROLLER)).onSearch(null);
//			catalogueController.onSelectFirst(null);
//		}
//		Integer id = ((Catalogue)catalogueController.getTo()).getId();
//		
//		
//		
//		Criteria criteria = new Criteria();
//		criteria.addEqualExpression(catalogueItemBean.getFieldName(IProductAlias.CATALOGUE_ITEM_ID),id);
//		Iterator<ITransferObject> iter = catalogueItemBean.getList(criteria).iterator();
//		while (iter.hasNext()) {
//			CatalogueItem cItem = (CatalogueItem) iter.next();
//			//SelectItem item = new SelectItem(pCategory, pCategory.getName());
//			//Item item = cItem;
//			categoryItems.add(cItem.getItem());
//		}
//	}
	
	
	/**
	 * 
	 * @param event
	 */
//	public void onCatalogueSelect(ActionEvent event) {
//		System.out.println("................................................CATALOGue");
//		Criteria criteria;
//		
//		
//		//criteria.addEqualExpression(categoryBean.getFieldName(IProductAlias.CATALOGUE_ITEM_ID),id);
//		try {
//			refreshCatalogueCategories(event);
//		} catch (ManagerBeanException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
	/**
	 * 
	 * @param event
	 */
//	public void onCategorySelect(ActionEvent event) {
//		System.out.println(".....................................................Category");
//		try {
//			refreshCategoryItems(event);
//		} catch (ManagerBeanException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}
}