package com.code.aon.ui.sales.event;

import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.company.WorkPlace;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesDetail;
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
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Elaboration;
import com.esferalia.aon.occam.api.model.type.ElaborationSource;

public class SalesControllerListener extends ControllerAdapter implements ISalesConstants {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void afterModelInitialized(ControllerEvent event)throws ControllerListenerException {
		SalesController controller = (SalesController)event.getController();
		controller.setListTotal(null);
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		CompanyCollectionsController companyColls = (CompanyCollectionsController)AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
		SalesController controller = (SalesController)event.getController();
		Sales sales = (Sales)controller.getTo();
		try {
			List<SelectItem> workPlaces = companyColls.getCurrentUserWorkPlaces();
			if (workPlaces.size() > 0) {
				sales.setWorkPlace((WorkPlace)workPlaces.get(0).getValue());
			} else {
				throw new ControllerListenerException("No hay un Centro de Trabajo definido.");
			}
			sales.setSecurityLevel(SecurityLevel.OFFICIAL);
			sales.setStatus(SalesStatus.PENDING);
			sales.setScope(sales.getWorkPlace().getScope());
			sales.setDocumentType(DocumentType.NORMAL);
			controller.setAddresses(null);
			controller.setProjects(null);
			controller.setDefaultPayMethod(null);
			controller.resetSalesPayMethod();
			controller.initSeries();
			controller.setSavedDeliveryDate(null);
			controller.setSavedCarrier(null);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		SalesController controller = (SalesController)event.getController();
		Sales sales = (Sales)controller.getTo();
		try {
			controller.loadAddresses(((Sales)controller.getTo()).getCustomer().getRegistry().getId());
			controller.loadProjects(((Sales)controller.getTo()).getCustomer().getRegistry().getId());
			controller.loadDefaultPayMethod(((Sales)controller.getTo()).getCustomer().getRegistry(), false);
			controller.setShippingAlternativeAddress(controller.isShippingAlternativeAddressDefined());
			if (sales.getCarrier() == null) {
				sales.setCarrier((Carrier)BeanManager.getManagerBean(Carrier.class).createNewTo());
			}
			
			controller.setShowPurchaseWindow(false);
			controller.setPurchaseGenerator(null);
			controller.setSavedDeliveryDate(sales.getDeliveryDate());
			controller.setSavedCarrier(sales.getCarrier());
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		SalesController controller = (SalesController)event.getController();
		Sales sales = (Sales)controller.getTo();
		controller.setSavedDeliveryDate(sales.getDeliveryDate());
		controller.setSavedCarrier(sales.getCarrier());

		IController detailController = FormUtil.getController(SALES_DETAIL_CONTROLLER_NAME);
		detailController.onReset(null);
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		SalesController controller = (SalesController)event.getController();
		if (!controller.isShippingAlternativeAddress()) {
			emptyShippingAlternativeAddress((Sales)controller.getTo());
		}
		controller.setShippingAlternativeAddress(controller.isShippingAlternativeAddressDefined());
	}
	
	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		SalesController controller = (SalesController)event.getController();
		try {
			controller.linkDeliveryDate((Sales)controller.getTo());
			controller.linkCarrier((Sales)controller.getTo());
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}

		IController salesDetailController = FormUtil.getController(ISalesConstants.SALES_DETAIL_CONTROLLER_NAME);
		salesDetailController.onSearch(null);
	}

	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		SalesController controller = (SalesController)event.getController();
		Sales sales = (Sales)controller.getTo();		
		Integer[] ids = sales.getDetailList().stream()
				.map(to -> (SalesDetail) to).mapToInt(SalesDetail::getId)
				.boxed().toArray(Integer[]::new);
		List<Elaboration> list = AON.getElaborationList(AonUtil.getDomainName(), sales.getDomain(),
				AonUtil.getRemoteUser(),
				f -> f.getSourceProperty().eq(ElaborationSource.SALES.value())
						.and(f.getSourceIdProperty().in(ids)));
		if(list!=null && list.size()>0){
			throw new ControllerListenerException("No se puede borrar, hay productos que están en proceso de elaboración.");
		}
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