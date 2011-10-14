package com.code.aon.ui.purchase.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.purchase.Purchase;
import com.code.aon.purchase.enumeration.PurchaseDocumentType;
import com.code.aon.purchase.enumeration.PurchaseStatus;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.purchase.controller.IPurchaseConstants;
import com.code.aon.ui.purchase.controller.PurchaseController;

public class PurchaseControllerListener extends ControllerAdapter implements IPurchaseConstants {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		PurchaseController controller = (PurchaseController)event.getController();
		((Purchase)controller.getTo()).setSecurityLevel(SecurityLevel.OFFICIAL);
		((Purchase)controller.getTo()).setStatus(PurchaseStatus.PENDING);
		((Purchase)controller.getTo()).setDocumentType(PurchaseDocumentType.NORMAL);
		controller.setAddresses(null);
		controller.setProjects(null);
		controller.setDefaultPayMethod(null);
		controller.resetPurchasePayMethod();
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		PurchaseController controller = (PurchaseController)event.getController();
		try {
			controller.loadAddresses(((Purchase)controller.getTo()).getSupplier().getRegistry().getId());
			controller.loadProjects(((Purchase)controller.getTo()).getSupplier().getRegistry().getId());
			controller.loadDefaultPayMethod(((Purchase)controller.getTo()).getSupplier().getRegistry().getId(), true);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		IController purchaseDetailController = FormUtil.getController(PURCHASE_DETAIL_CONTROLLER_NAME);
		purchaseDetailController.onReset(null);
	}
	
	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		PurchaseController purchaseController = (PurchaseController)this.getController();
		Purchase purchase = (Purchase)purchaseController.getTo();
		if (purchase.getProject() != null && purchase.getProject().getId() != null) {
			IController purchaseDetailController = FormUtil.getController(PURCHASE_DETAIL_CONTROLLER_NAME);
			purchaseDetailController.onSearch(null);
		}
	}
	
}