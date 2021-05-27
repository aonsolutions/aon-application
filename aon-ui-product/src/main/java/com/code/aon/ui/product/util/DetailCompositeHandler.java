package com.code.aon.ui.product.util;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.commercial.Offer;
import com.code.aon.commercial.OfferDetail;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.Item;
import com.code.aon.product.ItemComposition;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.ql.Criteria;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesDetail;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.DataScrollerState;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.IncomeDetail;
import com.esferalia.aon.entity.IEntityAlias;

public class DetailCompositeHandler extends DataScrollerState implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DetailCompositeHandler.class.getName());

	
	private IPriceStrategy priceStrategy;
	
	private LinesController controller;
	
	private boolean showItemCompositionWindow;
	
	private boolean showSerialNumberWindow;
	
	private Item composeItem;
	
	/*
	 * GRID OBJECTS
	 */
	private boolean nevv;
	
	private List<ItemComposition> list;
	
	private ItemComposition to;
	
	private Item serializableItem;
	
	public Item getSerializableItem() {
		return serializableItem;
	}

	public void setSerializableItem(Item serializableItem) {
		this.serializableItem = serializableItem;
	}
	
	
	public DetailCompositeHandler() {
		setBeanName("compositeHandler");
		setPageLimit(-1);
	}
	
	/*
	 * GRID METHODS
	 */
	public boolean isNevv() {
		return nevv;
	}
	
	public void onSelect(ActionEvent event) {
		if (getDirectModel().isRowAvailable())
			setTo((ItemComposition) getDirectModel().getRowData());
		this.nevv = false;
	}
	
	public void onAccept(ActionEvent event) {
		if (getDirectModel().isRowAvailable())
			list.set(getDirectModel().getRowIndex(), this.to);
		else
			this.list.add(this.to);
		this.to = null;
		this.nevv = false;
	}

	public void onCancel(ActionEvent event) {
		this.to = null;
		this.nevv = false;
	}
	
	public void onReset(ActionEvent event) throws ManagerBeanException {
		this.nevv = true;
		this.to = (ItemComposition) BeanManager.getManagerBean(ItemComposition.class).createNewTo();
	}
	
	public void onRemove(ActionEvent event) {
		if (getDirectModel().isRowAvailable())
			list.remove(getDirectModel().getRowIndex());
		this.to = null;
	}
	
	public void onCompositionItemChanged(LookupChangeEvent event) {
		ItemComposition itemComposition = (ItemComposition)getTo();
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Item item = (Item)event.getNewValue();
			itemComposition.setCompositionItem(item);
			itemComposition.setDescription(item.getFullName());
			if (itemComposition.getQuantity() == 0) {
				itemComposition.setQuantity(1);
			}
		}
	}
	
	public List<SelectItem> getAvailableSerials() throws ManagerBeanException{
		List<SelectItem> list = new LinkedList<SelectItem>();
		if(getSerializableItem()!=null) {
			Item item = getSerializableItem();
			IManagerBean bean = BeanManager.getManagerBean(Item.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ITEM_PRODUCT_ID), item.getProduct().getId());
			criteria.addNotNullExpression(bean.getFieldName(IEntityAlias.ITEM_SERIAL_NUMBER));
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ITEM_STATUS), ProductStatus.ACTIVE);
			for( ITransferObject to : bean.getList(criteria) ) {
				Item i = (Item) to;
				SelectItem si = new SelectItem(i, i.getSerialNumber());
				list.add(si);
			}
		}
        return list;
	}
	
	
	public void onAssignSerialNumberShow(ActionEvent event) throws ManagerBeanException {
		if (getDirectModel().isRowAvailable()) {
			onSelect(event);
			setSerializableItem(getTo().getCompositionItem());
		} else {
			setShowSerialNumberWindow(false);
		}
		onCancel(event);
	}
	
	public void onAssignSerialNumber(ActionEvent event) {
		if(getSerializableItem()!=null)
			getTo().setCompositionItem(getSerializableItem());
		onCancel(event);
	}
	
	
	////
	////
	
	
	public boolean isShowItemCompositionWindow() {
		return showItemCompositionWindow;
	}

	public void setShowItemCompositionWindow(boolean showItemCompositionWindow) {
		this.showItemCompositionWindow = showItemCompositionWindow;
	}
	
	public boolean isShowSerialNumberWindow() {
		return showSerialNumberWindow;
	}

	public void setShowSerialNumberWindow(boolean value) {
		this.showSerialNumberWindow = value;
	}

	public ItemComposition getTo() {
		return to;
	}
	public void setTo(ItemComposition to) {
		this.to = to;
	}
	////
	////
	
	public Item getComposeItem() {
		return composeItem;
	}

	public void load(LinesController controller, Item item) {
		this.priceStrategy = null;
		this.list = null;
		this.nevv = false;
		this.to = null;
		
		this.controller = controller;
		this.composeItem = item;
		try {
			list = item.getItemCompositionList();
			setModel(new SerializableListDataModel(list));
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException("No se han podido localizar los componentes.");
		}
	}
	
	public void reset() {
		this.priceStrategy = null;
		this.list = null;
		this.nevv = false;
		
		this.controller = null;
		this.composeItem = null;
		list = null;
		setModel(null);
	}
	
	public void accept(ActionEvent event) {
		controller.onAccept(event);
	}
	
	public void discard(ActionEvent event) {
		load(controller, composeItem);
	}
	
	public void checkSerialNumbers() throws ControllerListenerException {
		if(controller!=null && composeItem!=null) {
			if(listContainsIncompleteSerials())
				throw new ControllerListenerException("Revise la composicion, existen Series/Lotes sin asignar.");
		}
	}
	
	public void acceptItemComposition() throws ControllerListenerException {
		if(controller!=null && composeItem!=null) {
			if("saleInvoiceDetail".equals(controller.getBeanName())
					|| "purchaseInvoiceDetail".equals(controller.getBeanName()))
				addInvoiceDetailItems();
			else if("offerDetail".equals(controller.getBeanName()))
				addOfferDetailItems();
			else if("purchaseDetail".equals(controller.getBeanName()))
				addPurchaseDetailItems();
			else if("salesDetail".equals(controller.getBeanName()))
				addSalesDetailItems();
			else if("deliveryDetail".equals(controller.getBeanName()))
				addDeliveryDetailItems();
			else if("incomeDetail".equals(controller.getBeanName()))
				addIncomeDetailItems();
		}
	}
	
	private boolean listContainsIncompleteSerials() {
		return list.stream()
			.filter(ic -> ic.getCompositionItem().getProduct().isSerializable()
					&& ic.getCompositionItem().getSerialNumber()==null)
			.count() > 0;
	}

	private IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null)
			if("invoiceDetail".equals(controller.getBeanName()))
				priceStrategy = new InvoicePriceStrategy();
			else
				priceStrategy = PriceStrategyFactory.getPriceStrategy();
		return priceStrategy;
	}
	
	
	/*
	 * OFFER DETAIL
	 */
	private void addOfferDetailItems() {
		Offer offer = (Offer)controller.getMasterController().getTo();
		OfferDetail offerDetail = (OfferDetail)controller.getTo();
		if (offerDetail.getItem() != null && offerDetail.getItem().getId() != null && offerDetail.getItem().getProduct().isComposition()) {
			double quantity = offerDetail.getQuantity();
			try {
				for (ItemComposition composition : list) {
					offerDetail.setId(null);
					offerDetail.setLine(offerDetail.getLine()+1);
					offerDetail.setItem(composition.getCompositionItem());
					offerDetail.setDescription(composition.getCompositionItem().getFullName());
					offerDetail.setQuantity(CommonUtil.round(quantity * composition.getQuantity(), 3));
					offerDetail.setPrice(obtainCompositionItemPrice(offerDetail, offer, composition, getPriceStrategy()));
					offerDetail.setDiscountExpression(obtainCompositionDiscount(composition));
					offerDetail = (OfferDetail)controller.getManagerBean().insert(offerDetail);
				}
			} catch (ManagerBeanException e) {
				LOGGER.error("No me han podido crear los componentes");
				throw new AbortProcessingException(e);
			}
		}
	}

	
	
	/*
	 * INVOICE DETAIL
	 */
	private void addInvoiceDetailItems() throws ControllerListenerException {
		if(listContainsIncompleteSerials())
			throw new ControllerListenerException("Revise la composicion, existen Series/Lotes sin asignar.");
		
		Invoice invoice = (Invoice)controller.getMasterController().getTo();
		InvoiceDetail invoiceDetail = (InvoiceDetail)controller.getTo();
		if (invoiceDetail.getItem().getProduct().isComposition()) {
			double quantity = invoiceDetail.getQuantity();
			try {
				for (ItemComposition composition : list) {
					invoiceDetail.setId(null);
					invoiceDetail.setLine(invoiceDetail.getLine()+1);
					invoiceDetail.setItem(composition.getCompositionItem());
					invoiceDetail.setDescription(composition.getCompositionItem().getFullName());
					invoiceDetail.setQuantity(CommonUtil.round(quantity * composition.getQuantity(), 3));
					invoiceDetail.setPrice(obtainCompositionItemPrice(invoiceDetail, invoice, composition, getPriceStrategy()));
					invoiceDetail.setDiscountExpression(obtainCompositionDiscount(invoiceDetail, composition));
					invoiceDetail.setTaxableBase(getPriceStrategy().getBasePrice(invoiceDetail));
					invoiceDetail = (InvoiceDetail)controller.getManagerBean().insert(invoiceDetail);
				}
			} catch (ManagerBeanException e) {
				LOGGER.error("No me han podido crear los componentes");
				throw new AbortProcessingException(e);
			}
		}
	}

	
	/*
	 * PURCHASE DETAIL
	 */
	private void addPurchaseDetailItems() {
		PurchaseDetail purchaseDetail = (PurchaseDetail)controller.getTo();
		if (purchaseDetail.getItem().getProduct().isComposition()) {
			double quantity = purchaseDetail.getQuantity();
			try {
				for (ItemComposition composition : list) {
					purchaseDetail.setId(null);
					purchaseDetail.setLine(purchaseDetail.getLine()+1);
					purchaseDetail.setItem(composition.getCompositionItem());
					purchaseDetail.setDescription(composition.getCompositionItem().getFullName());
					purchaseDetail.setQuantity(CommonUtil.round(quantity * composition.getQuantity(), 3));
					purchaseDetail.setPrice(obtainCompositionItemPrice(composition));
					purchaseDetail.setDiscountExpression(new DiscountExpression("0.0"));
					purchaseDetail = (PurchaseDetail)controller.getManagerBean().insert(purchaseDetail);
				}
			} catch (ManagerBeanException e) {
				LOGGER.error("No me han podido crear los componentes");
				throw new AbortProcessingException(e);
			}
		}
	}
	
	
	/*
	 * SALES DETAIL
	 */
	private void addSalesDetailItems() {
		Sales sales = (Sales)controller.getMasterController().getTo();
		SalesDetail salesDetail = (SalesDetail)controller.getTo();
		if (salesDetail.getItem().getProduct().isComposition()) {
			double quantity = salesDetail.getQuantity();
			try {
				for (ItemComposition composition : list) {
					salesDetail.setId(null);
					salesDetail.setLine(salesDetail.getLine()+1);
					salesDetail.setItem(composition.getCompositionItem());
					salesDetail.setDescription(composition.getCompositionItem().getFullName());
					salesDetail.setQuantity(CommonUtil.round(quantity * composition.getQuantity(), 3));
					salesDetail.setPrice(obtainCompositionItemPrice(salesDetail, sales, composition, getPriceStrategy()));
					salesDetail.setDiscountExpression(obtainCompositionDiscount(composition));
					salesDetail = (SalesDetail)controller.getManagerBean().insert(salesDetail);
				}
			} catch (ManagerBeanException e) {
				LOGGER.error("No me han podido crear los componentes");
				throw new AbortProcessingException(e);
			}
		}
	}
	
	/*
	 * DELIVERY DETAIL
	 */
	private void addDeliveryDetailItems() throws ControllerListenerException {
		if(listContainsIncompleteSerials())
			throw new ControllerListenerException("Revise la composicion, existen Series/Lotes sin asignar.");
		
		Delivery delivery = (Delivery)controller.getMasterController().getTo();
		DeliveryDetail deliveryDetail = (DeliveryDetail)controller.getTo();
		if (deliveryDetail.getItem().getProduct().isComposition()) {
			double quantity = deliveryDetail.getQuantity();
			try {
				for (ItemComposition composition : list) {
					deliveryDetail.setId(null);
					deliveryDetail.setLine(deliveryDetail.getLine()+1);
					deliveryDetail.setItem(composition.getCompositionItem());
					deliveryDetail.setDescription(composition.getCompositionItem().getFullName());
					deliveryDetail.setQuantity(CommonUtil.round(quantity * composition.getQuantity(), 3));
					deliveryDetail.setPrice(obtainCompositionItemPrice(deliveryDetail, delivery, composition, getPriceStrategy()));
					deliveryDetail.setDiscountExpression(obtainCompositionDiscount(deliveryDetail, composition));
					deliveryDetail = (DeliveryDetail)controller.getManagerBean().insert(deliveryDetail);
				}
			} catch (ManagerBeanException e) {
				LOGGER.error("No me han podido crear los componentes");
				throw new AbortProcessingException(e);
			}
		}
	}
	
	/*
	 * INCOME DETAIL
	 */
	private void addIncomeDetailItems() {
		IncomeDetail incomeDetail = (IncomeDetail)controller.getTo();
		if (incomeDetail.getItem().getProduct().isComposition()) {
			double quantity = incomeDetail.getQuantity();
			try {
				for (ItemComposition composition : list) {
					incomeDetail.setId(null);
					incomeDetail.setLine(incomeDetail.getLine()+1);
					incomeDetail.setItem(composition.getCompositionItem());
					incomeDetail.setDescription(composition.getCompositionItem().getFullName());
					incomeDetail.setQuantity(CommonUtil.round(quantity * composition.getQuantity(), 3));
					incomeDetail.setPrice(obtainCompositionItemPrice(composition));
					incomeDetail.setDiscountExpression(new DiscountExpression("0.0"));
					incomeDetail = (IncomeDetail)controller.getManagerBean().insert(incomeDetail);
				}
			} catch (ManagerBeanException e) {
				LOGGER.error("No me han podido crear los componentes");
				throw new AbortProcessingException(e);
			}
		}
	}

	private double obtainCompositionItemPrice(OfferDetail offerDetail, Offer offer, ItemComposition composition, IPriceStrategy priceStrategy) {
		double price = 0;
		if (composition.getItem().getProduct().isCompositionPrice()) {
			price = priceStrategy.getUnitPrice(offerDetail, offer.getDate(), offer.getTarget());
		}
		return price;
	}
	
	private double obtainCompositionItemPrice(InvoiceDetail invoiceDetail, Invoice invoice, ItemComposition composition, IPriceStrategy priceStrategy) {
		double price = 0;
		if (composition.getItem().getProduct().isCompositionPrice()) {
			if (invoice.isSales()) {
				Customer customer = null;
				try {
					customer = (Customer)BeanManager.getManagerBean(Customer.class).get(invoice.getRegistry().getId());
				} catch (ManagerBeanException e) {
				}
				price = priceStrategy.getUnitPrice(invoiceDetail, invoice.getIssueDate(), customer);
			} else {
				price = composition.getCompositionItem().getPurchasePrice();
			}
		}
		return price;
	}
	
	private double obtainCompositionItemPrice(ItemComposition composition) {
		double price = 0;
		if (composition.getItem().getProduct().isCompositionPrice()) {
			price = composition.getCompositionItem().getPurchasePrice();
		}
		return price;
	}
	
	private double obtainCompositionItemPrice(SalesDetail salesDetail, Sales sales, ItemComposition composition, IPriceStrategy priceStrategy) {
		double price = 0;
		if (composition.getItem().getProduct().isCompositionPrice()) {
			price = priceStrategy.getUnitPrice(salesDetail, sales.getIssueDate(), sales.getCustomer());
		}
		return price;
	}
	
	private double obtainCompositionItemPrice(DeliveryDetail deliveryDetail, Delivery delivery, ItemComposition composition, IPriceStrategy priceStrategy) {
		double price = 0;
		if (composition.getItem().getProduct().isCompositionPrice()) {
			price = priceStrategy.getUnitPrice(deliveryDetail, delivery.getIssueTime(), delivery.getCustomer());
		}
		return price;
	}
	
	private DiscountExpression obtainCompositionDiscount(ItemComposition composition) {
		DiscountExpression discountExpr = new DiscountExpression("0.0");
		if (composition.getItem().getProduct().isCompositionPrice() && composition.getDiscountExpression() != null) {
			discountExpr = composition.getDiscountExpression();
		}
		return discountExpr;
	}
	
	private DiscountExpression obtainCompositionDiscount(InvoiceDetail invoiceDetail, ItemComposition composition) {
		DiscountExpression discountExpr = new DiscountExpression("0.0");
		if (composition.getItem().getProduct().isCompositionPrice() && invoiceDetail.getInvoice().isSales()) {
			if (composition.getDiscountExpression() != null) {
				discountExpr = composition.getDiscountExpression();
			}
		}
		return discountExpr;
	}
	
	private DiscountExpression obtainCompositionDiscount(DeliveryDetail deliveryDetail, ItemComposition composition) {
		DiscountExpression discountExpr = new DiscountExpression("0.0");
		if (composition.getItem().getProduct().isCompositionPrice()) {
			if (composition.getDiscountExpression() != null) {
				discountExpr = composition.getDiscountExpression();
			}
		}
		return discountExpr;
	}

	
}
