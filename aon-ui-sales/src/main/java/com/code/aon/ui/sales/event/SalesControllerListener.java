package com.code.aon.ui.sales.event;

import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.company.WorkPlace;
import com.code.aon.sales.Sales;
import com.code.aon.sales.enumeration.DocumentType;
import com.code.aon.sales.enumeration.SalesStatus;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.sales.controller.ISalesConstants;
import com.code.aon.ui.sales.controller.SalesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.carrier.Carrier;

public class SalesControllerListener extends ControllerAdapter implements ISalesConstants {

	@Override
	public void afterModelInitialized(ControllerEvent event)throws ControllerListenerException {
		SalesController controller = (SalesController) event.getController();
		controller.setSalesTotalAmount(null);
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		SalesController controller = (SalesController)event.getController();
		if(!controller.isShippingAlternativeAddress()){
			emptyShippingAlternativeAddress((Sales)controller.getTo());
		}
		controller.setShippingAlternativeAddress(controller.isShippingAlternativeAddressDefined());
	}

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		CompanyCollectionsController companyColls = (CompanyCollectionsController)AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
		SalesController controller = (SalesController)event.getController();
		try {
			((Sales)controller.getTo()).setSecurityLevel(SecurityLevel.OFFICIAL);
			((Sales)controller.getTo()).setStatus(SalesStatus.PENDING);
			WorkPlace workPlace = (WorkPlace)((SelectItem)companyColls.getCurrentUserWorkPlaces().get(0)).getValue();
			((Sales)controller.getTo()).setWorkPlace(workPlace);
			((Sales)controller.getTo()).setScope(workPlace.getScope());
			((Sales)controller.getTo()).setDocumentType(DocumentType.NORMAL);
			controller.setAddresses(null);
			controller.setProjects(null);
			controller.setDefaultPayMethod(null);
			controller.resetSalesPayMethod();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		SalesController controller = (SalesController)event.getController();
		try {
			controller.loadAddresses(((Sales)controller.getTo()).getCustomer().getRegistry().getId());
			controller.loadProjects(((Sales)controller.getTo()).getCustomer().getRegistry().getId());
			controller.loadDefaultPayMethod(((Sales)controller.getTo()).getCustomer().getRegistry().getId(), true);

			controller.loadDefaultPayMethod(((Sales)controller.getTo()).getCustomer().getRegistry().getId(), true);
			
			if(((Sales)controller.getTo()).getCarrier()==null){
				((Sales)controller.getTo()).setCarrier((Carrier) BeanManager.getManagerBean(Carrier.class).createNewTo());
			}
			controller.setShippingAlternativeAddress(controller.isShippingAlternativeAddressDefined());
			
			controller.setShowPurchaseWindow(false);
			controller.setPurchaseGenerator(null);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		IController salesDetailController = FormUtil.getController(SALES_DETAIL_CONTROLLER_NAME);
		salesDetailController.onReset(null);
	}

	private void emptyShippingAlternativeAddress(Sales sales) {
		sales.setShippingAlternativeAddress(null);
		sales.setShippingAlternativeAddress2(null);
		sales.setShippingAlternativeZip(null);
		sales.setShippingAlternativeCity(null);
		sales.setShippingAlternativePhone(null);
		sales.setShippingAlternativeRecipient(null);
	}
	
}