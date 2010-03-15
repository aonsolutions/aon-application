package com.code.aon.ui.ecommerce.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.richfaces.component.html.HtmlPanelMenuItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.product.ProductCategory;
import com.code.aon.product.ProductCategoryGroup;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.ecommerce.util.ECommerceUtil;
import com.code.aon.ui.ecommerce.util.IECommerceConstants;
import com.code.aon.ui.util.AonUtil;

public class CategoryGadget {
	
	private DataModel groupModel;
	private DataModel categoryModel;
	private DataModel categoryGroupModel;

	public void setGroupModel(DataModel groupModel) {
		this.groupModel = groupModel;
	}

	public void setCategoryModel(DataModel categoryModel) {
		this.categoryModel = categoryModel;
	}
	
	public DataModel getGroupModel() {
		if(groupModel==null){
			groupModel = new ListDataModel(getGroupList());
		}
		return groupModel;
	}
	
	public DataModel getCategoryModel() {
		ProductCategoryGroup pcg = (ProductCategoryGroup) getGroupModel().getRowData();
		categoryModel = new ListDataModel(getCategoryList(pcg));
		return categoryModel;
	}
	
	public DataModel getCategoryGroupModel() {
		if(categoryGroupModel==null){
			categoryGroupModel = new ListDataModel(getCategoryGroupList());
		}
		return categoryGroupModel;
	}

	public void setCategoryGroupModel(DataModel categoryGroupModel) {
		this.categoryGroupModel = categoryGroupModel;
	}

	public void onCategorySelect(ActionEvent event) {
		UIComponent c = event.getComponent();
		HtmlPanelMenuItem item = (HtmlPanelMenuItem) c;
		
//		CategoryGroup cg = (CategoryGroup)categoryGroupModel.getRowData();
		ProductCategory pc = (ProductCategory)item.getValue();
		try {
//			ProductCategory cat = (ProductCategory) categoryModel.getRowData();
//			cg.categoryModel.getRowData();
			
//			ProductCategory cat = (ProductCategory)cg.categoryModel.getRowData();
			Criteria criteria = new Criteria();
			String identifier = BeanManager.getManagerBean(Item.class)
					.getFieldName(IECommerceConstants.CATEGORY_ALIAS);
			criteria.addEqualExpression(identifier, pc.getId());
			/*
			 * AINADIR AL CRITERIA EL internetVisible DE ITEM A true
			 * 
			 */
			ShopItemsController shop = ECommerceUtil.getShopItems();
			shop.resetCriteria(criteria);
			shop.onSearch(null);
			
			// Establece como titulo la cagegoria seleccionada
			shop.setSelectionTitle(" >> "+pc.getGroup().getName()+" >> "+pc.getName());
		} catch (ManagerBeanException e) {
			String msg = "La búsqueda falló";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
		ShopController sc = (ShopController) AonUtil.getRegisteredBean(IECommerceConstants.SHOP_CONTROLLER);
		sc.setBackView( sc.getContentView() );
		sc.setContentView( ViewEnum.ITEM_LIST );
	}
	
	public List<CategoryGroup> getCategoryGroupList(){
		List<CategoryGroup> cgList = new LinkedList<CategoryGroup>();
		List<ITransferObject> catList;
		
		for(ITransferObject to:getGroupList()){
			ProductCategoryGroup pcg = (ProductCategoryGroup)to;
			catList = getCategoryList(pcg);
			categoryModel = new ListDataModel(catList);
			CategoryGroup cg = new CategoryGroup();
			cg.setPcg(pcg);
			cg.setCategoryModel(categoryModel);
			cg.setCategoryList(catList);
			cgList.add(cg);
		}
		return cgList;
	}
	
	private List<ITransferObject> getGroupList(){
		List<ITransferObject> categoryList = getCategoryList();
		List<ITransferObject> groupList = new LinkedList<ITransferObject>();
		List<ITransferObject> otherGroupList = new LinkedList<ITransferObject>();
//		List<CategoryGroup> cgList = new LinkedList<CategoryGroup>();
		
		for(ITransferObject to:categoryList){
			ProductCategory pc = (ProductCategory)to;
			ProductCategoryGroup pcg = pc.getGroup();
			
			if(!groupList.contains(pcg)){
				if(pcg!=null){
					groupList.add(pcg);
				}else if(otherGroupList.size()==0){
					otherGroupList.add(pcg);
				}
			}
			
		}
		groupList.addAll(otherGroupList);
		
//		for(ITransferObject to:groupList){
//			ProductCategoryGroup pcg = (ProductCategoryGroup)to;
//			categoryModel = new ListDataModel(getCategoryList(pcg));
//			CategoryGroup cg = new CategoryGroup();
//			cg.setPcg(pcg);
//			cg.setCategoryModel(categoryModel);
//			cgList.add(cg);
//		}
		return groupList;
	}
	
	private List<ITransferObject> getCategoryList(ProductCategoryGroup pcg){
		List<ITransferObject> itemList = getItemList();
		List<ITransferObject> categoryList = new LinkedList<ITransferObject>() ;

		for(ITransferObject to:itemList){
			Item item = (Item)to;
			ProductCategory pc = item.getProduct().getCategory();
			
			if(pcg != null && !categoryList.contains(pc)){
				if(pc.getGroup()!=null && pcg.equals(pc.getGroup())){
					categoryList.add(pc);
				}
			} else if(pcg == null && !categoryList.contains(pc)){
				if(pc.getGroup()==null){
					categoryList.add(pc);
				}
			}
		}
		return categoryList;
	}
	private List<ITransferObject> getCategoryList(){
		List<ITransferObject> itemList = getItemList();
		List<ITransferObject> categoryList = new LinkedList<ITransferObject>() ;
		
		for(ITransferObject to:itemList){
			Item item = (Item)to;
			ProductCategory pc = item.getProduct().getCategory();
			
			if(!categoryList.contains(pc)){
				categoryList.add(pc);
			} 
		}
		return categoryList;
	}
	
	private List<ITransferObject> getItemList(){
		List<ITransferObject> itemList = null;
		IManagerBean bean = null;
		try {
			bean = BeanManager.getManagerBean(Item.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IProductAlias.ITEM_INTERNET), true);
			itemList = bean.getList(criteria);
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		return itemList;
	}
	
	public class CategoryGroup{
		private ProductCategoryGroup pcg;
		private DataModel categoryModel;
		private List<ITransferObject> categoryList;
		
		public ProductCategoryGroup getPcg() {
			return pcg;
		}
		public void setPcg(ProductCategoryGroup pcg) {
			this.pcg = pcg;
		}
		public DataModel getCategoryModel() {
			return categoryModel;
		}
		public void setCategoryModel(DataModel categoryModel) {
			this.categoryModel = categoryModel;
		}
		public List<ITransferObject> getCategoryList() {
			return categoryList;
		}
		public void setCategoryList(List<ITransferObject> categoryList) {
			this.categoryList = categoryList;
		}
	}


}
