package com.code.aon.ui.warehouse.event;

import javax.faces.model.SelectItem;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.company.WorkPlace;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.warehouse.controller.DeliveryController;
import com.code.aon.ui.warehouse.controller.IWarehouseConstants;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.enumeration.DeliveryStatus;

public class DeliveryControllerListener extends ControllerAdapter implements IWarehouseConstants {
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		CompanyCollectionsController companyColls = (CompanyCollectionsController)AonUtil.getRegisteredBean(COMPANY_COLLECTIONS_CONTROLLER_NAME);
		DeliveryController controller = (DeliveryController)event.getController();
		try {
			((Delivery)controller.getTo()).setSecurityLevel(SecurityLevel.OFFICIAL);
			((Delivery)controller.getTo()).setStatus(DeliveryStatus.PENDING);
			((Delivery)controller.getTo()).setWorkPlace((WorkPlace)((SelectItem)companyColls.getWorkPlaces().get(0)).getValue());
			controller.setAddresses(null);
			controller.setProjects(null);
	        controller.setWarehouse(controller.obtainWarehouse((Delivery)controller.getTo()));
			controller.setDefaultPayMethod(null);
			controller.resetDeliveryPayMethod();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		DeliveryController controller = (DeliveryController)event.getController();
		try {
			controller.loadAddresses(((Delivery)controller.getTo()).getCustomer().getRegistry().getId());
			controller.loadProjects(((Delivery)controller.getTo()).getCustomer().getRegistry().getId());
	        controller.setWarehouse(controller.obtainWarehouse((Delivery)controller.getTo()));
			controller.loadDefaultPayMethod(((Delivery)controller.getTo()).getCustomer().getRegistry().getId(), true);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		IController deliveryDetailController = FormUtil.getController(DELIVERY_DETAIL_CONTROLLER_NAME);
		deliveryDetailController.onReset(null);
	}

}