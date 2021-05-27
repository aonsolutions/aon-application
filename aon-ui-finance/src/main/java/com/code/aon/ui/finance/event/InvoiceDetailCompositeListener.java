package com.code.aon.ui.finance.event;

import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.product.ItemComposition;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.finance.controller.InvoiceDetailController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class InvoiceDetailCompositeListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		InvoiceDetailController controller = (InvoiceDetailController)event.getController();
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		if(ds.isAlphaDomain()){
			controller.getCompositeHandler().checkSerialNumbers();
		}
	}
	
	@Override
	public void afterBeanReset(ControllerEvent event) throws ControllerListenerException {
		InvoiceDetailController controller = (InvoiceDetailController)event.getController();
		controller.getCompositeHandler().reset();
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		InvoiceDetailController controller = (InvoiceDetailController)event.getController();
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		if(ds.isAlphaDomain()){
			controller.getCompositeHandler().acceptItemComposition();
		} else {
			Invoice invoice = (Invoice)controller.getMasterController().getTo();
			InvoiceDetail invoiceDetail = (InvoiceDetail)controller.getTo();
			if (invoiceDetail.getItem().getProduct().isComposition()) {
				double quantity = invoiceDetail.getQuantity();
				try {
					for (ItemComposition composition : invoiceDetail.getItem().getItemCompositionList()) {
						invoiceDetail.setId(null);
						invoiceDetail.setLine(invoiceDetail.getLine()+1);
						invoiceDetail.setItem(composition.getCompositionItem());
						invoiceDetail.setDescription(composition.getDescription());
						invoiceDetail.setQuantity(CommonUtil.round(quantity * composition.getQuantity(), 3));
						invoiceDetail.setPrice(obtainCompositionItemPrice(invoiceDetail, invoice, composition, controller.getPriceStrategy()));
						invoiceDetail.setDiscountExpression(obtainCompositionDiscount(invoiceDetail, composition));
						invoiceDetail.setTaxableBase(controller.getPriceStrategy().getBasePrice(invoiceDetail));
						invoiceDetail = (InvoiceDetail)controller.getManagerBean().insert(invoiceDetail);
					}
				} catch (ManagerBeanException e) {
					throw new ControllerListenerException(e.getMessage(), e);
				}
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

}
