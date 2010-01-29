package com.code.aon.ui.ecommerce.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

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
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;
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
//		BasicController pcg = (BasicController)FormUtil.getController("productCategoryGroup");
//		pcg.onSearch(null);
//		try {
//			groupModel = pcg.getModel();
//		} catch (ManagerBeanException e) {
//			e.printStackTrace();
//		}
		if(groupModel==null){
			groupModel = new ListDataModel(getGroupList());
		}
		return groupModel;
	}
	
	public DataModel getCategoryModel() {
//		LinesController pc = (LinesController)FormUtil.getController("productCategory");
//		try {
//			ProductCategoryGroup pcg = (ProductCategoryGroup)groupModel.getRowData();
//			pc.clearCriteria();
//			pc.getCriteria().addEqualExpression(pc.getFieldName(IProductAlias.PRODUCT_CATEGORY_CATEGORY_GROUP_ID), pcg.getId());
//			pc.onSearch(null);
//			categoryModel = pc.getModel();
//		} catch (ManagerBeanException e) {
//			e.printStackTrace();
//		}
//		if(categoryModel==null){
			ProductCategoryGroup pcg = (ProductCategoryGroup) getGroupModel().getRowData();
			categoryModel = new ListDataModel(getCategoryList(pcg));
//		}
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



//	public DataModel getModel() {
//		if (groupModel == null) {
////			model = new ListDataModel(getList());
//			buildList();
//		}
//		return groupModel;
//	}
//
//	public void setModel(DataModel model) {
//		this.groupModel = model;
//	}

//	public List<ITransferObject> getList() {
//		if (list == null) {
//			buildList();
//		}
//		return list;
//	}
//
//	private void buildList() {
//		IManagerBean bean;
//		try {
//			bean = BeanManager.getManagerBean(ProductCategory.class);
//			BasicController pcg = (BasicController)FormUtil.getController("productCategoryGroup");
////			list = bean.getList(null);
//			pcg.onSearch(null);
//			groupModel = pcg.getModel();
//		} catch (ManagerBeanException e) {
//			e.printStackTrace();
//		}
//	}

//	public void setList(List<ITransferObject> list) {
//		this.list = list;
//	}

	public void onCategorySelect(ActionEvent event) {
		CategoryGroup cg = (CategoryGroup)categoryGroupModel.getRowData();
		try {
//			ProductCategory cat = (ProductCategory) categoryModel.getRowData();
//			cg.categoryModel.getRowData();
			ProductCategory cat = (ProductCategory)cg.categoryModel.getRowData();
			Criteria criteria = new Criteria();
			String identifier = BeanManager.getManagerBean(Item.class)
					.getFieldName(IECommerceConstants.CATEGORY_ALIAS);
			criteria.addEqualExpression(identifier, cat.getId());
			/*
			 * AINADIR AL CRITERIA EL internetVisible DE ITEM A true
			 * 
			 */
			ShopItemsController shop = ECommerceUtil.getShopItems();
			shop.resetCriteria(criteria);
			shop.onSearch(null);
		} catch (ManagerBeanException e) {
			String msg = "La búsqueda falló";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
		ShopController sc = (ShopController) AonUtil.getRegisteredBean(IECommerceConstants.SHOP_CONTROLLER);
		sc.setBackView( sc.getContentView() );
		sc.setContentView( ViewEnum.ITEM_LIST );
	}
	
	
	
	private List<CategoryGroup> getCategoryGroupList(){
//		List<ITransferObject> categoryList = getCategoryList(null);
//		List<ITransferObject> groupList = new LinkedList<ITransferObject>();
//		List<ITransferObject> otherGroupList = new LinkedList<ITransferObject>();
//		
//		
//		
//		for(ITransferObject to:categoryList){
//			ProductCategory pc = (ProductCategory)to;
//			ProductCategoryGroup pcg = pc.getGroup();
//			
//			if(!groupList.contains(pcg)){
//				if(pcg!=null){
//					groupList.add(pcg);
//				}else if(otherGroupList.size()==0){
//					otherGroupList.add(pcg);
//				}
//			}
//			
//		}
//		groupList.addAll(otherGroupList);
//		

		
		List<CategoryGroup> cgList = new LinkedList<CategoryGroup>();
		for(ITransferObject to:getGroupList()){
			ProductCategoryGroup pcg = (ProductCategoryGroup)to;
			categoryModel = new ListDataModel(getCategoryList(pcg));
			CategoryGroup cg = new CategoryGroup();
			cg.setPcg(pcg);
			cg.setCategoryModel(categoryModel);
			cgList.add(cg);
		}
		
		return cgList;
	}
	private List<ITransferObject> getGroupList(){
//		List<ITransferObject> itemList;
//		List<ProductCategoryGroup> groupList;
//		
//		BasicController pc = (BasicController)FormUtil.getController("productCategory");
////		BasicController item = (Item)FormUtil.getController("item");
//		IManagerBean bean = BeanManager.getManagerBean(Item.class);
//		Criteria criteria = new Criteria();
//		criteria.addEqualExpression(IProductAlias.ITEM_INTERNET, true);
//		itemList = bean.getList(criteria);
//		
//		for(ITransferObject to:itemList){
//			Item item = (Item) to;
//			if(!groupList.contains(item.getProduct().getCategory().getGroup())){
//				groupList.add(item.getProduct().getCategory().getGroup());
//			}
//		}
//		
//		
//		pc.clearCriteria();
//		pc.getCriteria().addEqualExpression(identifier, item);
//		pc.onSearch(null);
//		
//		
//		
//		IManagerBean bean;
//		try {
//			bean = BeanManager.getManagerBean(ProductCategory.class);
//			BasicController pcg = (BasicController)FormUtil.getController("productCategoryGroup");
//			list = bean.getList(null);
//			pcg.onSearch(null);
//			groupModel = pcg.getModel();
//		} catch (ManagerBeanException e) {
//			e.printStackTrace();
//		}
//		
//		
//		if (groupModel == null) {
//			groupModel = new ListDataModel(list);
//		}
		List<ITransferObject> categoryList = getCategoryList(null);
		List<ITransferObject> groupList = new LinkedList<ITransferObject>();
		List<ITransferObject> otherGroupList = new LinkedList<ITransferObject>();
		List<CategoryGroup> cgList = new LinkedList<CategoryGroup>();
		
		
		
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
		
		for(ITransferObject to:groupList){
			ProductCategoryGroup pcg = (ProductCategoryGroup)to;
			categoryModel = new ListDataModel(getCategoryList(pcg));
			CategoryGroup cg = new CategoryGroup();
			cg.setPcg(pcg);
			cg.setCategoryModel(categoryModel);
			cgList.add(cg);
		}
		
		return groupList;
	}
	
	private List<ITransferObject> getCategoryList(ProductCategoryGroup pcg){
//		LinesController pc = (LinesController)FormUtil.getController("productCategory");
//		ProductCategoryGroup pcg = (ProductCategoryGroup) getGroupModel().getRowData();
//		String identifier = IProductAlias.i
//		Criteria criteria = new Criteria();
//		criteria.addEqualExpression(identifier, pcg.getId())
		
		List<ITransferObject> itemList = getItemList();
		List<ITransferObject> categoryList = new LinkedList<ITransferObject>() ;
//		List<ITransferObject> otherCategoryList = new LinkedList<ITransferObject>() ;
		
		for(ITransferObject to:itemList){
			Item item = (Item)to;
			ProductCategory pc = item.getProduct().getCategory();
			
			
			if(pcg != null && !categoryList.contains(pc)){
				if(pc.getGroup()!=null && pcg.equals(pc.getGroup())){
					categoryList.add(pc);
				}
			} else if(pcg == null && !categoryList.contains(pc)){
				categoryList.add(pc);
			}
			
			
			
//			if(!categoryList.contains(pc) && pcg==null ){
//				categoryList.add(pc);
//				if(pcg !=null && pc.getGroup().equals(pcg)){
//				}
//			}
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
	}


}
