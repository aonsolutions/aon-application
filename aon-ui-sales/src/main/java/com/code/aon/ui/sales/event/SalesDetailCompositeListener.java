package com.code.aon.ui.sales.event;

import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.product.ItemComposition;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesDetail;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.sales.controller.SalesDetailController;
import com.code.aon.ui.util.AonUtil;

public class SalesDetailCompositeListener extends ControllerAdapter {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void afterBeanReset(ControllerEvent event) throws ControllerListenerException {
		SalesDetailController controller = (SalesDetailController)event.getController();
		controller.getCompositeHandler().reset();
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		SalesDetailController controller = (SalesDetailController)event.getController();
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		if(ds.isAlphaDomain()){
			controller.getCompositeHandler().acceptItemComposition();
		} else {
			Sales sales = (Sales)controller.getMasterController().getTo();
			SalesDetail salesDetail = (SalesDetail)controller.getTo();
			if (salesDetail.getItem().getProduct().isComposition()) {
				double quantity = salesDetail.getQuantity();
				try {
					for (ItemComposition composition : salesDetail.getItem().getItemCompositionList()) {
						salesDetail.setId(null);
						salesDetail.setLine(salesDetail.getLine()+1);
						salesDetail.setItem(composition.getCompositionItem());
						salesDetail.setDescription(composition.getDescription());
						salesDetail.setQuantity(CommonUtil.round(quantity * composition.getQuantity(), 3));
						salesDetail.setPrice(obtainCompositionItemPrice(salesDetail, sales, composition, controller.getPriceStrategy()));
						salesDetail.setDiscountExpression(obtainCompositionDiscount(composition));
						salesDetail = (SalesDetail)controller.getManagerBean().insert(salesDetail);
					}
				} catch (ManagerBeanException e) {
					throw new ControllerListenerException(e.getMessage(), e);
				}
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

	private DiscountExpression obtainCompositionDiscount(ItemComposition composition) {
		DiscountExpression discountExpr = new DiscountExpression("0.0");
		if (composition.getItem().getProduct().isCompositionPrice() && composition.getDiscountExpression() != null) {
			discountExpr = composition.getDiscountExpression();
		}
		return discountExpr;
	}

}
