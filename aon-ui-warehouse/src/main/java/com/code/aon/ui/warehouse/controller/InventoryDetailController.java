package com.code.aon.ui.warehouse.controller;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Brand;
import com.code.aon.product.ProductCategory;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.OrderByList;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;
import com.code.aon.warehouse.Inventory;
import com.esferalia.aon.entity.IEntityAlias;

public class InventoryDetailController extends LinesController implements ICollectionProvider {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InventoryDetailController.class.getName());
	private static final String INVENTORY_CONTROLLER_NAME = "inventory";
	
	private boolean showSearchPanel;
	
	/* search fields */
	private ProductCategory category;
	private Brand brand;
	private String code;
	private String description;
	
	public boolean isShowSearchPanel() {
		return showSearchPanel;
	}
	
	public void setShowSearchPanel(boolean showSearchPanel) {
		this.showSearchPanel = showSearchPanel;
	}

	public ProductCategory getCategory() {
		return category;
	}

	public void setCategory(ProductCategory category) {
		this.category = category;
	}
	
	public Brand getBrand() {
		return brand;
	}

	public void setBrand(Brand brand) {
		this.brand = brand;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public void resetSearchPanel(){
		setShowSearchPanel(false);
		setCategory(null);
		setBrand(null);
		setCode(null);
		setDescription(null);
	}

	public void onAcceptNext(ActionEvent event) {
		accept(event);
		int current = this.model.getRowIndex();
		int max = this.model.getRowCount();
		if ((current+1) < max){
			this.model.setRowIndex(current+1);
			onSelect(event);
		}
	}
	
	private Inventory getCurrentInventory() {
		InventoryController inventoryController = (InventoryController)FormUtil.getController(INVENTORY_CONTROLLER_NAME);
		return (Inventory)inventoryController.getTo();
	}
	
	public void loadDetailModel(ActionEvent event) throws ManagerBeanException {
		onEditSearch(event);
		Criteria criteria = getCriteria();
		criteria.addEqualExpression(getFieldName(IEntityAlias.INVENTORY_DETAIL_INVENTORY_ID), getCurrentInventory().getId());
		if ( getCategory() != null ) {
			String field = getFieldName(IEntityAlias.INVENTORY_DETAIL_ITEM_PRODUCT_CATEGORY_ID);
			criteria.addEqualExpression(field, category.getId());
		}
		if ( getBrand() != null ) {
			String field = "InventoryDetail.item.product.brand.id";
			criteria.addEqualExpression(field, getBrand().getId());
		}
		if ( StringUtils.isNotBlank(getCode()) ) {
			String field = "InventoryDetail.item.product.code";
			criteria.addExpression( ExpressionUtilities.getLikeExpression(field, "%"+getCode()+"%") );
		}
		if ( StringUtils.isNotBlank(getDescription()) ) {
			String field = "InventoryDetail.item.product.name";
			criteria.addExpression( ExpressionUtilities.getLikeExpression(field, "%"+getDescription()+"%") );
		}
		
		onSearch(event);
	}
	
	@Override
	public Collection<ITransferObject> getCollection() {
		try {
			Criteria criteria = getCriteria();
			OrderByList oldOrderList = criteria.getOrderByList();
			criteria.setOrderByList(null);
			criteria.addOrder(getFieldName(IEntityAlias.INVENTORY_DETAIL_ITEM_PRODUCT_CATEGORY_NAME));
			criteria.addOrder(getFieldName(IEntityAlias.INVENTORY_DETAIL_ITEM_PRODUCT_NAME));
			int count = getManagerBean().getCount(getCriteria());
			List<ITransferObject> collection = search(0, count);
			criteria.setOrderByList( oldOrderList );
			return collection;
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage() );
		}
		return Collections.emptyList();
	}
}