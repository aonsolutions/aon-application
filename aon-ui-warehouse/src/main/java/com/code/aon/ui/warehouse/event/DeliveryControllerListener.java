package com.code.aon.ui.warehouse.event;

import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.company.WorkPlace;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
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
import com.esferalia.aon.carrier.Carrier;

public class DeliveryControllerListener extends ControllerAdapter implements IWarehouseConstants {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void afterModelInitialized(ControllerEvent event) throws ControllerListenerException {
		DeliveryController controller = (DeliveryController)event.getController();
		controller.setListTotal(null);
	}

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		CompanyCollectionsController companyColls = (CompanyCollectionsController)AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
		DeliveryController controller = (DeliveryController)event.getController();
		Delivery delivery = (Delivery)controller.getTo(); 
		try {
			List<SelectItem> workPlaces = companyColls.getCurrentUserWorkPlaces();
			if (workPlaces.size() > 0) {
				delivery.setWorkPlace((WorkPlace)workPlaces.get(0).getValue());
			} else {
				throw new ControllerListenerException("No hay un Centro de Trabajo definido.");
			}
			delivery.setSecurityLevel(SecurityLevel.OFFICIAL);
			delivery.setStatus(DeliveryStatus.PENDING);
			delivery.setScope(delivery.getWorkPlace().getScope());
			controller.setAddresses(null);
			controller.setProjects(null);
	        controller.setWarehouse(controller.obtainWarehouse((Delivery)controller.getTo()));
			controller.setDefaultPayMethod(null);
			controller.resetDeliveryPayMethod();
			controller.initSeries();
			controller.getPackagesHandler().init();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		DeliveryController controller = (DeliveryController)event.getController();
		try {
			controller.setSelectedTab(null);
			controller.loadAddresses(((Delivery)controller.getTo()).getCustomer().getRegistry().getId());
			controller.loadProjects(((Delivery)controller.getTo()).getCustomer().getRegistry().getId());
	        controller.setWarehouse(controller.obtainWarehouse((Delivery)controller.getTo()));
			controller.loadDefaultPayMethod(((Delivery)controller.getTo()).getCustomer().getRegistry(), false);
			
			if(((Delivery)controller.getTo()).getCarrier()==null){
				((Delivery)controller.getTo()).setCarrier((Carrier) BeanManager.getManagerBean(Carrier.class).createNewTo());
			}
			controller.setShippingAlternativeAddress(controller.isShippingAlternativeAddressDefined());
			controller.getPackagesHandler().init();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		IController deliveryDetailController = FormUtil.getController(DELIVERY_DETAIL_CONTROLLER_NAME);
		deliveryDetailController.onReset(null);
	}
	
	@Override
	public void beforeBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		DeliveryController controller = (DeliveryController) event.getController();
		try {
			if(controller.getPackagesHandler().isPackagesDefined()) {
				controller.getPackagesHandler().removePackages();
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		DeliveryController controller = (DeliveryController) event.getController();
		if(!controller.isShippingAlternativeAddress()){
			emptyShippingAlternativeAddress((Delivery)controller.getTo());
		}
		controller.setShippingAlternativeAddress(controller.isShippingAlternativeAddressDefined());
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event)	throws ControllerListenerException {
		DeliveryController controller = (DeliveryController)event.getController();
		controller.setListTotal(null);
	}

	private void emptyShippingAlternativeAddress(Delivery delivery) {
		delivery.setShippingAlternativeAddress(null);
		delivery.setShippingAlternativeAddress2(null);
		delivery.setShippingAlternativeZip(null);
		delivery.setShippingAlternativeCity(null);
		delivery.setShippingAlternativePhone(null);
		delivery.setShippingAlternativeRecipient(null);
	}

}