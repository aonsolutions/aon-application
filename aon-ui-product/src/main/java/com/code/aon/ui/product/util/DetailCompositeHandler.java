package com.code.aon.ui.product.util;

import java.io.Serializable;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.AonVersion;
import com.code.aon.commercial.Offer;
import com.code.aon.commercial.OfferDetail;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.Item;
import com.code.aon.product.ItemComposition;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesDetail;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.IncomeDetail;

public class DetailCompositeHandler implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private IPriceStrategy priceStrategy;
	
	private LinesController controller;
	
	private Item composeItem;
	
	private List<ItemComposition> compositionList;
	
	private SerializableListDataModel compositionModel;
	
	private boolean showItemCompositionWindow;
	
		
	
	public boolean isShowItemCompositionWindow() {
		return showItemCompositionWindow;
	}

	public void setShowItemCompositionWindow(boolean showItemCompositionWindow) {
		this.showItemCompositionWindow = showItemCompositionWindow;
	}

	public Item getComposeItem() {
		return composeItem;
	}

	public SerializableListDataModel getCompositionModel() {
		if(compositionModel==null)
			compositionModel = new SerializableListDataModel(compositionList);
		return compositionModel;
	}

	public void load(LinesController controller, Item item) {
		this.priceStrategy = null;
		this.compositionList = null;
		this.compositionModel = null;
		
		this.controller = controller;
		this.composeItem = item;
		try {
			compositionList = item.getItemCompositionList();
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException("No se han podido localizar los componentes.");
		}
	}
	
	public void discard(ActionEvent event) {
		try {
			compositionList = this.composeItem.getItemCompositionList();
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException("No se han podido localizar los componentes.");
		}
	}
	
	public void accept() {
		if(controller==null || composeItem==null)
			throw new AbortProcessingException("Precarga de datos incorrecta, no se puede continuar.");
		
		if("offerDetail".equals(controller.getBeanName()))
			afterOfferDetailAdded();
		else if("invoiceDetail".equals(controller.getBeanName()))
			afterInvoiceDetailAdded();
		else if("purchaseDetail".equals(controller.getBeanName()))
			afterPurchaseDetailAdded();
		else if("salesDetail".equals(controller.getBeanName()))
			afterSalesDetailAdded();
		else if("deliveryDetail".equals(controller.getBeanName()))
			afterDeliveryDetailAdded();
		else if("incomeDetail".equals(controller.getBeanName()))
			afterIncomeDetailAdded();
	}
	
	private IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null) {
			if("invoiceDetail".equals(controller.getBeanName())) {
				priceStrategy = new InvoicePriceStrategy();
			} else {
				priceStrategy = PriceStrategyFactory.getPriceStrategy();
			}
		}
		return priceStrategy;
	}
	
	
	/*
	 * OFFER DETAIL
	 */
	private void afterOfferDetailAdded() {
//		OfferDetailController controller = (OfferDetailController)event.getController();
		Offer offer = (Offer)controller.getMasterController().getTo();
		OfferDetail offerDetail = (OfferDetail)controller.getTo();
		if (offerDetail.getItem() != null && offerDetail.getItem().getId() != null && offerDetail.getItem().getProduct().isComposition()) {
			double quantity = offerDetail.getQuantity();
			try {
//				for (ItemComposition composition : compositionList) {
				for (ItemComposition composition : (List<ItemComposition>)compositionModel.getWrappedData() ) {
//				for (ItemComposition composition : offerDetail.getItem().getItemCompositionList()) {
					offerDetail.setId(null);
					offerDetail.setLine(offerDetail.getLine()+1);
					offerDetail.setItem(composition.getCompositionItem());
					offerDetail.setDescription(composition.getDescription());
					offerDetail.setQuantity(CommonUtil.round(quantity * composition.getQuantity(), 3));
//					offerDetail.setPrice(obtainCompositionItemPrice(offerDetail, offer, composition, controller.getPriceStrategy()));
					offerDetail.setPrice(obtainCompositionItemPrice(offerDetail, offer, composition, getPriceStrategy()));
					offerDetail.setDiscountExpression(obtainCompositionDiscount(composition));
					offerDetail = (OfferDetail)controller.getManagerBean().insert(offerDetail);
				}
			} catch (ManagerBeanException e) {
//				throw new ControllerListenerException(e.getMessage(), e);
				AonUtil.addErrorMessage("No me han podido crear los componentes");
				AonUtil.addErrorMessage(e.getMessage());
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

	private DiscountExpression obtainCompositionDiscount(ItemComposition composition) {
		DiscountExpression discountExpr = new DiscountExpression("0.0");
		if (composition.getItem().getProduct().isCompositionPrice() && composition.getDiscountExpression() != null) {
			discountExpr = composition.getDiscountExpression();
		}
		return discountExpr;
	}
	
	
	
	/*
	 * INVOICE DETAIL
	 */
	private void afterInvoiceDetailAdded() {
//		InvoiceDetailController controller = (InvoiceDetailController)event.getController();
		Invoice invoice = (Invoice)controller.getMasterController().getTo();
		InvoiceDetail invoiceDetail = (InvoiceDetail)controller.getTo();
		if (invoiceDetail.getItem().getProduct().isComposition()) {
			double quantity = invoiceDetail.getQuantity();
			try {
//				for (ItemComposition composition : compositionList) {
				for (ItemComposition composition : (List<ItemComposition>)compositionModel.getWrappedData() ) {
//				for (ItemComposition composition : invoiceDetail.getItem().getItemCompositionList()) {
					invoiceDetail.setId(null);
					invoiceDetail.setLine(invoiceDetail.getLine()+1);
					invoiceDetail.setItem(composition.getCompositionItem());
					invoiceDetail.setDescription(composition.getDescription());
					invoiceDetail.setQuantity(CommonUtil.round(quantity * composition.getQuantity(), 3));
//					invoiceDetail.setPrice(obtainCompositionItemPrice(invoiceDetail, invoice, composition, controller.getPriceStrategy()));
					invoiceDetail.setPrice(obtainCompositionItemPrice(invoiceDetail, invoice, composition, getPriceStrategy()));
					invoiceDetail.setDiscountExpression(obtainCompositionDiscount(invoiceDetail, composition));
//					invoiceDetail.setTaxableBase(controller.getPriceStrategy().getBasePrice(invoiceDetail));
					invoiceDetail.setTaxableBase(getPriceStrategy().getBasePrice(invoiceDetail));
					invoiceDetail = (InvoiceDetail)controller.getManagerBean().insert(invoiceDetail);
				}
			} catch (ManagerBeanException e) {
//				throw new ControllerListenerException(e.getMessage(), e);
				AonUtil.addErrorMessage("No me han podido crear los componentes");
				AonUtil.addErrorMessage(e.getMessage());
			}
		}
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

	private DiscountExpression obtainCompositionDiscount(InvoiceDetail invoiceDetail, ItemComposition composition) {
		DiscountExpression discountExpr = new DiscountExpression("0.0");
		if (composition.getItem().getProduct().isCompositionPrice() && invoiceDetail.getInvoice().isSales()) {
			if (composition.getDiscountExpression() != null) {
				discountExpr = composition.getDiscountExpression();
			}
		}
		return discountExpr;
	}
	
	
	/*
	 * PURCHASE DETAIL
	 */
	private void afterPurchaseDetailAdded() {
//		PurchaseDetailController controller = (PurchaseDetailController)event.getController();
		PurchaseDetail purchaseDetail = (PurchaseDetail)controller.getTo();
		if (purchaseDetail.getItem().getProduct().isComposition()) {
			double quantity = purchaseDetail.getQuantity();
			try {
//				for (ItemComposition composition : compositionList) {
				for (ItemComposition composition : (List<ItemComposition>)compositionModel.getWrappedData() ) {
//				for (ItemComposition composition : purchaseDetail.getItem().getItemCompositionList()) {
					purchaseDetail.setId(null);
					purchaseDetail.setLine(purchaseDetail.getLine()+1);
					purchaseDetail.setItem(composition.getCompositionItem());
					purchaseDetail.setDescription(composition.getDescription());
					purchaseDetail.setQuantity(CommonUtil.round(quantity * composition.getQuantity(), 3));
					purchaseDetail.setPrice(obtainCompositionItemPrice(composition));
					purchaseDetail.setDiscountExpression(new DiscountExpression("0.0"));
					purchaseDetail = (PurchaseDetail)controller.getManagerBean().insert(purchaseDetail);
				}
			} catch (ManagerBeanException e) {
//				throw new ControllerListenerException(e.getMessage(), e);
				AonUtil.addErrorMessage("No me han podido crear los componentes");
				AonUtil.addErrorMessage(e.getMessage());
			}
		}
	}

	private double obtainCompositionItemPrice(ItemComposition composition) {
		double price = 0;
		if (composition.getItem().getProduct().isCompositionPrice()) {
			price = composition.getCompositionItem().getPurchasePrice();
		}
		return price;
	}
	
	
	/*
	 * SALES DETAIL
	 */
	private void afterSalesDetailAdded() {
//		SalesDetailController controller = (SalesDetailController)event.getController();
		Sales sales = (Sales)controller.getMasterController().getTo();
		SalesDetail salesDetail = (SalesDetail)controller.getTo();
		if (salesDetail.getItem().getProduct().isComposition()) {
			double quantity = salesDetail.getQuantity();
			try {
//				for (ItemComposition composition : compositionList) {
				for (ItemComposition composition : (List<ItemComposition>)compositionModel.getWrappedData() ) {
//				for (ItemComposition composition : salesDetail.getItem().getItemCompositionList()) {
					salesDetail.setId(null);
					salesDetail.setLine(salesDetail.getLine()+1);
					salesDetail.setItem(composition.getCompositionItem());
					salesDetail.setDescription(composition.getDescription());
					salesDetail.setQuantity(CommonUtil.round(quantity * composition.getQuantity(), 3));
//					salesDetail.setPrice(obtainCompositionItemPrice(salesDetail, sales, composition, controller.getPriceStrategy()));
					salesDetail.setPrice(obtainCompositionItemPrice(salesDetail, sales, composition, getPriceStrategy()));
					salesDetail.setDiscountExpression(obtainCompositionDiscount(composition));
					salesDetail = (SalesDetail)controller.getManagerBean().insert(salesDetail);
				}
			} catch (ManagerBeanException e) {
//				throw new ControllerListenerException(e.getMessage(), e);
				AonUtil.addErrorMessage("No me han podido crear los componentes");
				AonUtil.addErrorMessage(e.getMessage());
			}
		}
	}

	private double obtainCompositionItemPrice(SalesDetail salesDetail, Sales sales, ItemComposition composition, IPriceStrategy priceStrategy) {
		double price = 0;
		if (composition.getItem().getProduct().isCompositionPrice()) {
			price = priceStrategy.getUnitPrice(salesDetail, sales.getIssueDate(), sales.getCustomer());
		}
		return price;
	}

	// TODO check duplicated method
//	private DiscountExpression obtainCompositionDiscount(ItemComposition composition) {
//		DiscountExpression discountExpr = new DiscountExpression("0.0");
//		if (composition.getItem().getProduct().isCompositionPrice() && composition.getDiscountExpression() != null) {
//			discountExpr = composition.getDiscountExpression();
//		}
//		return discountExpr;
//	}

	
	/*
	 * DELIVERY DETAIL
	 */
	private void afterDeliveryDetailAdded() {
//		DeliveryDetailController controller = (DeliveryDetailController)event.getController();
		Delivery delivery = (Delivery)controller.getMasterController().getTo();
		DeliveryDetail deliveryDetail = (DeliveryDetail)controller.getTo();
		if (deliveryDetail.getItem().getProduct().isComposition()) {
			double quantity = deliveryDetail.getQuantity();
			try {
//				for (ItemComposition composition : compositionList) {
				for (ItemComposition composition : (List<ItemComposition>)compositionModel.getWrappedData() ) {
//				for (ItemComposition composition : deliveryDetail.getItem().getItemCompositionList()) {
					deliveryDetail.setId(null);
					deliveryDetail.setLine(deliveryDetail.getLine()+1);
					deliveryDetail.setItem(composition.getCompositionItem());
					deliveryDetail.setDescription(composition.getDescription());
					deliveryDetail.setQuantity(CommonUtil.round(quantity * composition.getQuantity(), 3));
//					deliveryDetail.setPrice(obtainCompositionItemPrice(deliveryDetail, delivery, composition, controller.getPriceStrategy()));
					deliveryDetail.setPrice(obtainCompositionItemPrice(deliveryDetail, delivery, composition, getPriceStrategy()));
					deliveryDetail.setDiscountExpression(obtainCompositionDiscount(deliveryDetail, composition));
					deliveryDetail = (DeliveryDetail)controller.getManagerBean().insert(deliveryDetail);
				}
			} catch (ManagerBeanException e) {
//				throw new ControllerListenerException(e.getMessage(), e);
				AonUtil.addErrorMessage("No me han podido crear los componentes");
				AonUtil.addErrorMessage(e.getMessage());
			}
		}
	}

	private double obtainCompositionItemPrice(DeliveryDetail deliveryDetail, Delivery delivery, ItemComposition composition, IPriceStrategy priceStrategy) {
		double price = 0;
		if (composition.getItem().getProduct().isCompositionPrice()) {
			price = priceStrategy.getUnitPrice(deliveryDetail, delivery.getIssueTime(), delivery.getCustomer());
		}
		return price;
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
	
	
	/*
	 * INCOME DETAIL
	 */
	private void afterIncomeDetailAdded() {
//		IncomeDetailController controller = (IncomeDetailController)event.getController();
		IncomeDetail incomeDetail = (IncomeDetail)controller.getTo();
		if (incomeDetail.getItem().getProduct().isComposition()) {
			double quantity = incomeDetail.getQuantity();
			try {
//				for (ItemComposition composition : compositionList) {
				for (ItemComposition composition : (List<ItemComposition>)compositionModel.getWrappedData() ) {
//				for (ItemComposition composition : incomeDetail.getItem().getItemCompositionList()) {
					incomeDetail.setId(null);
					incomeDetail.setLine(incomeDetail.getLine()+1);
					incomeDetail.setItem(composition.getCompositionItem());
					incomeDetail.setDescription(composition.getDescription());
					incomeDetail.setQuantity(CommonUtil.round(quantity * composition.getQuantity(), 3));
					incomeDetail.setPrice(obtainCompositionItemPrice(composition));
					incomeDetail.setDiscountExpression(new DiscountExpression("0.0"));
					incomeDetail = (IncomeDetail)controller.getManagerBean().insert(incomeDetail);
				}
			} catch (ManagerBeanException e) {
//				throw new ControllerListenerException(e.getMessage(), e);
				AonUtil.addErrorMessage("No me han podido crear los componentes");
				AonUtil.addErrorMessage(e.getMessage());
			}
		}
	}

	// TODO check duplicated method
//	private double obtainCompositionItemPrice(ItemComposition composition) {
//		double price = 0;
//		if (composition.getItem().getProduct().isCompositionPrice()) {
//			price = composition.getCompositionItem().getPurchasePrice();
//		}
//		return price;
//	}
	
}