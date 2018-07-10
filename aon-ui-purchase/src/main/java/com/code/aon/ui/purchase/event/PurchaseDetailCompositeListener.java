package com.code.aon.ui.purchase.event;

import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.product.ItemComposition;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.purchase.controller.PurchaseDetailController;
import com.code.aon.ui.util.AonUtil;

public class PurchaseDetailCompositeListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void afterBeanReset(ControllerEvent event) throws ControllerListenerException {
		PurchaseDetailController controller = (PurchaseDetailController)event.getController();
		controller.getCompositeHandler().reset();;
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		PurchaseDetailController controller = (PurchaseDetailController)event.getController();
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		if(ds.isAlphaEnabled()){
			controller.getCompositeHandler().acceptItemComposition();
		} else {
			PurchaseDetail purchaseDetail = (PurchaseDetail)controller.getTo();
			if (purchaseDetail.getItem().getProduct().isComposition()) {
				double quantity = purchaseDetail.getQuantity();
				try {
					for (ItemComposition composition : purchaseDetail.getItem().getItemCompositionList()) {
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
					throw new ControllerListenerException(e.getMessage(), e);
				}
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

}
