package com.code.aon.ui.salesPurchase.controller;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.product.Tax;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.product.enumeration.TaxType;
import com.code.aon.purchase.Purchase;
import com.code.aon.purchase.dao.IPurchaseAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.salesPurchase.SalesPurchase;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.warehouse.controller.DeliveryDetailController;
import com.code.aon.warehouse.DeliveryDetail;

/**
 * Controller used in the SalesPurchase maintenance.
 */
public class SalesPurchaseController extends BasicController {

	/** The LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(SalesPurchaseController.class.getName());

	/** DeliveryDetail Controller name. */
	private static final String DELIVERY_DETAIL_CONTROLLER_NAME = "deliveryDetail";

	/** If a purchase is related or not. */
	public boolean withPurchase;
	
	/** The purchase warehouse id. */
	private Integer purchaseWarehouseId;

	/**
	 * Checks if a purchase is related or not.
	 * 
	 * @return true, if a purchase is related
	 */
	public boolean isWithPurchase() {
		return withPurchase;
	}

	/**
	 * Sets the with if a purchase is related.
	 * 
	 * @param withPurchase true if a purchase is related
	 */
	public void setWithPurchase(boolean withPurchase) {
		this.withPurchase = withPurchase;
	}
	
	/**
	 * Gets the purchase warehouse id.
	 * 
	 * @return the purchase warehouse id
	 */
	public Integer getPurchaseWarehouseId() {
		return purchaseWarehouseId;
	}

	/**
	 * Sets the purchase warehouse id.
	 * 
	 * @param purchaseWarehouseId the purchase warehouse id
	 */
	public void setPurchaseWarehouseId(Integer purchaseWarehouseId) {
		this.purchaseWarehouseId = purchaseWarehouseId;
	}

	/**
	 * On reset. Method launched by the menu
	 * 
	 * @param event the event
	 */
	@SuppressWarnings("unused")
	public void onReset(MenuEvent event) {
		super.onReset(null);
	}

	/**
	 * On remove. Sends a reset and set false to withPurchase
	 * 
	 * @param event the event
	 */
	@Override
	public void onRemove(ActionEvent event) {
		super.remove(null);
		super.onReset(null);
		this.setWithPurchase(false);
	}

	/**
	 * On save. Creates the <code>SalesPurchase</code> and the <code>DeliveryDetail</code>. withPurchase = true
	 * 
	 * @param event the event
	 */
	@SuppressWarnings("unused")
	public void onSave(ActionEvent event) {
		Purchase purchase = getPurchaseDetailParent(((SalesPurchase) this.getTo()).getPurchaseDetail().getPurchase());
		purchase.setIssueDate(((SalesPurchase) this.getTo()).getPurchaseDetail().getPurchase().getIssueDate());
		((SalesPurchase) this.getTo()).getPurchaseDetail().setPurchase(purchase);
        ((SalesPurchase) this.getTo()).getPurchaseDetail().getItem().getProduct().setName(((SalesPurchase) this.getTo()).getPurchaseDetail().getDescription());
        ((SalesPurchase) this.getTo()).getPurchaseDetail().setItem(
                updateOrInsertItem(((SalesPurchase) this.getTo()).getPurchaseDetail().getItem()));
        ((SalesPurchase) this.getTo()).getPurchaseDetail().setPrice(
				((SalesPurchase) this.getTo()).getPurchaseDetail().getItem().getPurchasePrice());

        DeliveryDetailController deliveryDetailController = (DeliveryDetailController)AonUtil.getController(DELIVERY_DETAIL_CONTROLLER_NAME);
		((DeliveryDetail) deliveryDetailController.getTo()).setItem(((SalesPurchase) this.getTo()).getPurchaseDetail()
				.getItem());
        ((DeliveryDetail) deliveryDetailController.getTo()).setDescription(((SalesPurchase) this.getTo()).getPurchaseDetail()
                .getDescription());
        ((DeliveryDetail) deliveryDetailController.getTo()).setPrice(((SalesPurchase) this.getTo()).getPurchaseDetail()
				.getItem().getPrice());
		((DeliveryDetail) deliveryDetailController.getTo()).setQuantity(((SalesPurchase) this.getTo())
				.getPurchaseDetail().getQuantity());
		setWithPurchase(true);
	}

	/**
	 * Updates an item, if it exists, and inserts it if it not exists.
	 * 
	 * @param item the item
	 * 
	 * @return the item
	 */
	private Item updateOrInsertItem(Item item) {
		try {
			IManagerBean productBean = BeanManager.getManagerBean(Product.class);
			IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
			if (item.getId() == null) {
				item.getProduct().setType(ProductType.COMMERCIAL_PRODUCT);
				item.getProduct().setVat(obtainDefaultVat());
				item.getProduct().setBrand(null);
				item.getProduct().setInventoriable(true);
				item.setProduct((Product) productBean.insert(item.getProduct()));
				return (Item) itemBean.insert(item);
			}
			Product product = obtainProduct(item.getProduct().getId());
			product.setCategory(item.getProduct().getCategory());
			item.setProduct(product);
			return (Item) itemBean.update(item);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error inserting item", e);
		}
		return null;
	}

	/**
	 * Obtains the product with id equals to parameter <code>id</code>.
	 * 
	 * @param id the id
	 * 
	 * @return the product
	 */
	@SuppressWarnings("unchecked")
	private Product obtainProduct(Integer id) {
		try {
			IManagerBean productBean = BeanManager.getManagerBean(Product.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(productBean.getFieldName(IProductAlias.PRODUCT_ID), id);
			Iterator iter = productBean.getList(criteria).iterator();
			if (iter.hasNext()) {
				return (Product) iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error retrieving data for product with id= " + id, e);
		}
		return null;
	}

	/**
	 * Obtains the default vat.(<code>TaxType == VAT, percentage == 16</code>)
	 * 
	 * @return the tax
	 */
	@SuppressWarnings("unchecked")
	private Tax obtainDefaultVat() {
		try {
			IManagerBean taxBean = BeanManager.getManagerBean(Tax.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(taxBean.getFieldName(IProductAlias.TAX_TYPE), TaxType.VAT);
			criteria.addEqualExpression(taxBean.getFieldName(IProductAlias.TAX_PERCENTAGE), new Double(16));
			Iterator iter = taxBean.getList(criteria).iterator();
			if (iter.hasNext()) {
				return (Tax) iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining default Vat", e);
		}
		return null;
	}

	/**
	 * Retrieves the whole <code>Item</code> object when the lookup field changes
	 * 
	 * @param event the event
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	@SuppressWarnings("unchecked")
	public void itemData(ValueChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null) {
			IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(itemBean.getFieldName(IProductAlias.ITEM_ID), event.getNewValue());
			Iterator iter = itemBean.getList(criteria).iterator();
			if (iter.hasNext()) {
				Item item = (Item) iter.next();
				((SalesPurchase) this.getTo()).getPurchaseDetail().setItem(item);
			}
		}
	}

	/**
	 * Gets the purchase with series, number and supplier equals to Purchase passed as parameter <code>purchase</code>
	 * 
	 * @param purchase the purchase
	 * 
	 * @return the purchase detail parent
	 */
	@SuppressWarnings("unchecked")
	private Purchase getPurchaseDetailParent(Purchase purchase) {
		try {
			IManagerBean purchaseBean = BeanManager.getManagerBean(Purchase.class);
			Criteria criteria = new Criteria();
			if (!purchase.getSeries().equals("")) {
				criteria.addExpression(purchaseBean.getFieldName(IPurchaseAlias.PURCHASE_SERIES), purchase.getSeries());
			}
			criteria
					.addEqualExpression(purchaseBean.getFieldName(IPurchaseAlias.PURCHASE_NUMBER), new Integer(purchase.getNumber()));
			criteria.addEqualExpression(purchaseBean.getFieldName(IPurchaseAlias.PURCHASE_SUPPLIER_ID), purchase
					.getSupplier().getId());
			Iterator iter = purchaseBean.getList(criteria).iterator();
			if (iter.hasNext()) {
				return (Purchase) iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining purchase for PurchaseDetail", e);
		} catch (ExpressionException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining purchase for PurchaseDetail", e);
		}
		return purchase;
	}
}
