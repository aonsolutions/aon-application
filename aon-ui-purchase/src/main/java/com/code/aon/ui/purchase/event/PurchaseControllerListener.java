package com.code.aon.ui.purchase.event;

import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.company.WorkPlace;
import com.code.aon.purchase.Purchase;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.purchase.enumeration.PurchaseDocumentType;
import com.code.aon.purchase.enumeration.PurchaseStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.purchase.controller.IPurchaseConstants;
import com.code.aon.ui.purchase.controller.PurchaseController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.carrier.Carrier;
import com.esferalia.aon.entity.IEntityAlias;

public class PurchaseControllerListener extends ControllerAdapter implements IPurchaseConstants {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void afterModelInitialized(ControllerEvent event)throws ControllerListenerException {
		PurchaseController controller = (PurchaseController) event.getController();
		controller.setListTotal(null);
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		CompanyCollectionsController companyColls = (CompanyCollectionsController)AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
		PurchaseController controller = (PurchaseController)event.getController();
		Purchase purchase = (Purchase) controller.getTo();
		try {
			List<SelectItem> workPlaces = companyColls.getCurrentUserWorkPlaces();
			if (workPlaces.size() > 0) {
				purchase.setWorkPlace((WorkPlace)workPlaces.get(0).getValue());
			} else {
				throw new ControllerListenerException("No hay un Centro de Trabajo definido.");
			}
			purchase.setSecurityLevel(SecurityLevel.OFFICIAL);
			purchase.setStatus(PurchaseStatus.PENDING);
			purchase.setScope(purchase.getWorkPlace().getScope());
			purchase.setDocumentType(PurchaseDocumentType.NORMAL);
			controller.setAddresses(null);
			controller.setDefaultPayMethod(null);
			controller.setLinesDeliveryDate(null);
			controller.resetPurchasePayMethod();
			controller.initSeries();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		PurchaseController controller = (PurchaseController)event.getController();
		try {
			controller.loadAddresses(((Purchase)controller.getTo()).getSupplier().getRegistry().getId());
			controller.loadDefaultPayMethod(((Purchase)controller.getTo()).getSupplier().getRegistry(), false);
			if(((Purchase)controller.getTo()).getCarrier()==null){
				((Purchase)controller.getTo()).setCarrier((Carrier) BeanManager.getManagerBean(Carrier.class).createNewTo());
			}
			controller.setLinesDeliveryDate(obtainLinesDeliveryDate());
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
		controller.setShippingAlternativeAddress(controller.isShippingAlternativeAddressDefined());
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		IController purchaseDetailController = FormUtil.getController(PURCHASE_DETAIL_CONTROLLER_NAME);
		purchaseDetailController.onReset(null);
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		PurchaseController purchaseController = (PurchaseController)this.getController();
		Purchase purchase = (Purchase)purchaseController.getTo();
		if (purchase.getProject() == null || purchase.getProject().getId() == null) {
			try {
				purchaseController.removePurchaseDetailProject();
			} catch (ManagerBeanException e) {
				throw new ControllerListenerException(e.getMessage());
			}
		}
		if(!purchaseController.isShippingAlternativeAddress()){
			emptyShippingAlternativeAddress((Purchase)purchaseController.getTo());
		}
		purchaseController.setShippingAlternativeAddress(purchaseController.isShippingAlternativeAddressDefined());
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		PurchaseController purchaseController = (PurchaseController)this.getController();
		Purchase purchase = (Purchase)purchaseController.getTo();
		if (purchase.getProject() != null && purchase.getProject().getId() != null) {
			IController purchaseDetailController = FormUtil.getController(PURCHASE_DETAIL_CONTROLLER_NAME);
			purchaseDetailController.onSearch(null);
		}
		purchaseController.setListTotal(null);
		try {
			updateLinesDeliveryDate();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}

	private Date obtainLinesDeliveryDate() throws ManagerBeanException {
		PurchaseController controller = (PurchaseController)this.getController();
		Purchase purchase = (Purchase)controller.getTo();
		IManagerBean purchaseDetailBean = BeanManager.getManagerBean(PurchaseDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_PURCHASE_ID), purchase.getId());
		criteria.addNotNullExpression(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_DELIVERY_DATE));
		List<ITransferObject> list = purchaseDetailBean.getList(criteria);
		if(!list.isEmpty()){
			return ((PurchaseDetail)list.get(0)).getDeliveryDate();
		}
		return null;
	}
	
	private void updateLinesDeliveryDate() throws ManagerBeanException {
		PurchaseController controller = (PurchaseController)this.getController();
		Purchase purchase = (Purchase)controller.getTo();
		Date deliveryDate = controller.getLinesDeliveryDate();
		IManagerBean purchaseDetailBean = BeanManager.getManagerBean(PurchaseDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_PURCHASE_ID), purchase.getId());
		for (ITransferObject ito : purchaseDetailBean.getList(criteria)) {
			PurchaseDetail purchaseDetail = (PurchaseDetail)ito;
			purchaseDetail.setDeliveryDate(deliveryDate);
			purchaseDetailBean.update(purchaseDetail);
		}
		IController purchaseDetailController = FormUtil.getController(PURCHASE_DETAIL_CONTROLLER_NAME);
		purchaseDetailController.onSearch(null);
	}

	private void emptyShippingAlternativeAddress(Purchase purchase) {
		purchase.setShippingAlternativeAddress(null);
		purchase.setShippingAlternativeAddress2(null);
		purchase.setShippingAlternativeZip(null);
		purchase.setShippingAlternativeCity(null);
		purchase.setShippingAlternativePhone(null);
		purchase.setShippingAlternativeRecipient(null);
	}
	
}