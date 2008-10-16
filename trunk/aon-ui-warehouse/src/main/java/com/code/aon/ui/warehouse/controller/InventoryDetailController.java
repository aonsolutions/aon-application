package com.code.aon.ui.warehouse.controller;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.product.ProductCategory;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Inventory;

/**
 * Controller for inventory detail.
 * 
 * @author Consulting & Development.
 * @since 1.0
 */
public class InventoryDetailController extends BasicController implements ICollectionProvider {
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(InventoryDetailController.class.getName());

	/** Inventory controller. */
	private static final String INVENTORY_CONTROLLER_NAME = "inventory";
	
	/** Category identifier. */
	private Integer categoryId = new Integer(-1);
	
	/** CategoryGroup identifier. */
	private Integer categoryGroupId = new Integer(-1);
	
	/** The category list. */
	private List<SelectItem> categoryList = new LinkedList<SelectItem>();
	
	/**
	 * Returns the categoy ident.
	 * 
	 * @return the categoryId
	 */
	public Integer getCategoryId() {
		return categoryId;
	}

	/**
	 * Assigns the category ident.
	 * 
	 * @param categoryId the categoryId to set
	 */
	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}
	
	/**
	 * Gets the category group id.
	 * 
	 * @return the category group id
	 */
	public Integer getCategoryGroupId() {
		return categoryGroupId;
	}

	/**
	 * Sets the category group id.
	 * 
	 * @param categoryGroupId the category group id
	 */
	public void setCategoryGroupId(Integer categoryGroupId) {
		this.categoryGroupId = categoryGroupId;
	}

	/**
	 * Gets the category list.
	 * 
	 * @return the category list
	 */
	public List<SelectItem> getCategoryList() {
		return categoryList;
	}

	/**
	 * Sets the category list.
	 * 
	 * @param categoryList the category list
	 */
	public void setCategoryList(List<SelectItem> categoryList) {
		this.categoryList = categoryList;
	}

	/**
	 * Resets the controller.
	 * 
	 * @param event a menu event
	 */
	public void onStart(ActionEvent event){
		this.model = null;
		super.onReset(null);
	}

	/**
	 * If needed reloads the detail model.
	 * 
	 * @param event contains the new value
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public void addCategoryCriteria(ValueChangeEvent event)
		throws ManagerBeanException {
		if (event.getNewValue() != null) {
			categoryId = (Integer)event.getNewValue();
		}
	}

	/**
	 * Accepts current row and selects the next.
	 * 
	 * @param event an action event
	 */
	public void onAcceptNext(ActionEvent event) {
		accept(event);
		int current = this.model.getRowIndex();
		int max = this.model.getRowCount();
		if ((current+1) < max){
			this.model.setRowIndex(current+1);
			onSelect(event);
		}
	}
	
	/**
	 * Assigns a new list of inventory detail.
	 */
	public void loadDetailModel(ActionEvent event) {
		this.setModel(new ListDataModel(getDetailList(false)));
	}
	
	/**
	 * Queries an inventory detail list for this inventory.id and category if needed
	 * 
	 * @return the list of inventory detail
	 */
	@SuppressWarnings("unchecked")
	private List getDetailList(boolean reportOrder){
		InventoryController inventoryController = (InventoryController)AonUtil.getController(INVENTORY_CONTROLLER_NAME);
		Integer inventoryId = ((Inventory)inventoryController.getTo()).getId();
		Session session = HibernateUtil.getSession();
		String query = "select inventoryDetail " +
	        "from InventoryDetail inventoryDetail, " +
	        "Item item, " + 
	        "Product prod, " + 
	        "ProductCategory cat " +
	        ((categoryGroupId==null||categoryGroupId.equals(new Integer(-1)))?"":", ProductCategoryGroup catGroup ") +
	        "where inventoryDetail.item.id = item.id " +
	        "and item.product.id = prod.id " +
	        "and prod.category.id = cat.id " +
	        ((categoryGroupId==null||categoryGroupId.equals(new Integer(-1)))?"":"and cat.group.id = catGroup.id ") +
	        "and inventoryDetail.inventory.id=" + inventoryId.intValue() +
	        (categoryId==null || categoryId.equals(new Integer(-1))?"":" and cat.id=" + categoryId.intValue()) + 
	        (categoryGroupId==null || categoryGroupId.equals(new Integer(-1))?"":" and catGroup.id=" + categoryGroupId.intValue()) +
	        " order by " + (reportOrder?"prod.category.name, ":"") +
	        "item.product.name";
        Query q = session.createQuery(query);
        return q.list();
	}

	/**
	 * On category group changed.
	 * 
	 * @param event the event
	 */
	public void onCategoryGroupChanged(ValueChangeEvent event){
		if(event.getNewValue() != null){
			categoryGroupId = (Integer)event.getNewValue();
			categoryList = new LinkedList<SelectItem>();
			loadCategoryList();
		}
	}
	
	/**
	 * Load category list.
	 */
	@SuppressWarnings("unchecked")
	private void loadCategoryList(){
		try {
			IManagerBean pCategoryBean = BeanManager.getManagerBean(ProductCategory.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(pCategoryBean.getFieldName(IProductAlias.PRODUCT_CATEGORY_CATEGORY_GROUP_ID), categoryGroupId);
			Iterator iter = pCategoryBean.getList(criteria).iterator();
			while(iter.hasNext()){
				ProductCategory category = (ProductCategory)iter.next();
				SelectItem item = new SelectItem(category.getId(), category.getName());
				categoryList.add(item);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining categories with categoryGroup = " + categoryGroupId, e);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.code.aon.ui.form.BasicController#getCollection()
	 */
	/**
	 * Gets the collection.
	 * 
	 * @return the collection
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Collection getCollection() {
		return getDetailList(true);
	}
}