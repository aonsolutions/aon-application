package com.code.aon.ui.finance.event;

import java.util.Iterator;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.Tariff;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.product.ItemComposition;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.ui.finance.controller.InvoiceDetailController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.entity.IEntityAlias;

public class InvoiceDetailCompositeListener extends ControllerAdapter {

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		InvoiceDetailController controller = (InvoiceDetailController)event.getController();
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
					invoiceDetail.setPrice(obtainCompositionItemPrice(invoiceDetail, composition, controller.getPriceStrategy()));
					invoiceDetail.setDiscountExpression(obtainCompositionDiscount(invoiceDetail, composition));
					invoiceDetail.setTaxableBase(controller.getPriceStrategy().getBasePrice(invoiceDetail));
					invoiceDetail = (InvoiceDetail)controller.getManagerBean().insert(invoiceDetail);
				}
			} catch (ManagerBeanException e) {
				throw new ControllerListenerException(e.getMessage(), e);
			}
		}
	}

	private double obtainCompositionItemPrice(InvoiceDetail invoiceDetail, ItemComposition composition, IPriceStrategy priceStrategy) {
		double price = 0;
		if (composition.getItem().getProduct().isCompositionPrice()) {
			Invoice invoice = invoiceDetail.getInvoice();
			if (invoice.isSales()) {
				price = priceStrategy.getUnitPrice(invoiceDetail, invoice.getIssueDate(), getTariff(invoice.getRegistry()));
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

	private Tariff getTariff(Registry registry) {
		try {
			IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(customerBean.getFieldName(IEntityAlias.CUSTOMER_REGISTRY_ID), registry.getId());
			Iterator<?> iterator = customerBean.getList(criteria).iterator();
			if (iterator.hasNext()) {
				Customer customer = (Customer)iterator.next();
				return customer.getTariff();
			}
			return null;
		} catch (ManagerBeanException e) {
			return null;
		}
	}

}
