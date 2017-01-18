package com.code.aon.ui.purchase.event;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.purchase.ProposalDetail;
import com.code.aon.purchase.Purchase;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.purchase.enumeration.ProposalDetailStatus;
import com.code.aon.purchase.enumeration.PurchaseDetailStatus;
import com.code.aon.purchase.enumeration.PurchaseDocumentType;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.purchase.controller.PurchaseController;
import com.code.aon.ui.purchase.controller.PurchaseDetailController;
import com.code.aon.ui.purchase.util.PurchaseUtils;

public class PurchaseDetailControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		PurchaseDetailController controller = (PurchaseDetailController)event.getController();
		PurchaseDetail purchaseDetail = (PurchaseDetail)controller.getTo();
		Purchase purchase = (Purchase)controller.getMasterController().getTo();

		controller.setLongDescription(false);
		try {
			PurchaseUtils utils = new PurchaseUtils();
			purchaseDetail.setProject((purchase.getProject() != null && purchase.getProject().getId() != null) ? purchase.getProject() : null);
			purchaseDetail.setLine(utils.calculateNextLine((Purchase)controller.getMasterController().getTo()));
			purchaseDetail.setStatus(PurchaseDetailStatus.PENDING);
			purchaseDetail.getPurchase().setWorkPlace(purchase.getWorkPlace());
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		PurchaseController purchaseController = (PurchaseController) ((LinesController)event.getController()).getMasterController();
		Purchase purchase = (Purchase) purchaseController.getTo();
		PurchaseDetail purchaseDetail = (PurchaseDetail)event.getController().getTo();
		checkQuantities(purchase, purchaseDetail);
		checkSerializable(purchaseDetail);
		purchaseDetail.setDeliveryDate(purchaseController.getLinesDeliveryDate());
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		Purchase purchase = (Purchase)((LinesController)event.getController()).getMasterController().getTo();
		PurchaseDetail purchaseDetail = (PurchaseDetail)event.getController().getTo();
		checkQuantities(purchase, purchaseDetail);
		checkSerializable(purchaseDetail);
	}
	
	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		event.getController().initializeModel();
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		PurchaseDetailController controller = (PurchaseDetailController)event.getController();
		PurchaseDetail purchaseDetail = (PurchaseDetail)controller.getTo();

		controller.setLongDescription((purchaseDetail.getDescription().length() > 64) ? true : false);
	}
	
	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		PurchaseDetailController controller = (PurchaseDetailController)event.getController();
		PurchaseDetail purchaseDetail = (PurchaseDetail)controller.getTo();
		if(purchaseDetail.getProposalDetail()!=null && purchaseDetail.getProposalDetail().getId()!=null){
			try {
				updateProposalDetail(purchaseDetail.getProposalDetail());
			} catch (ManagerBeanException e) {
				throw new ControllerListenerException(e.getMessage(), e);
			}
		}
	}
	
	private void checkQuantities(Purchase purchase, PurchaseDetail purchaseDetail) throws ControllerListenerException {
		if (purchaseDetail.getQuantity() < 0 && purchase.getDocumentType()!=PurchaseDocumentType.ITEM_RETURN) {
			throw new ControllerListenerException("La cantidad no puede ser negativa.");
		}
		if (purchaseDetail.getQuantity() > 0 && purchase.getDocumentType()==PurchaseDocumentType.ITEM_RETURN) {
			throw new ControllerListenerException("La cantidad a devolver no puede ser positiva.");
		}
	}

	private void checkSerializable(PurchaseDetail purchaseDetail) throws ControllerListenerException {
		try {
			if (purchaseDetail.getItem() != null && purchaseDetail.getItem().getProduct().isSerializable()) {
				if (!purchaseDetail.getItem().getProduct().isLotable() && !purchaseDetail.getItem().isWildCard() && Math.abs(purchaseDetail.getQuantity()) != 1) {
					purchaseDetail.setQuantity(1);
				}
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	private void updateProposalDetail(ProposalDetail proposalDetail) throws ManagerBeanException {
		IManagerBean proposalDetailBean = BeanManager.getManagerBean(ProposalDetail.class);
		ProposalDetail pd = (ProposalDetail) proposalDetailBean.get(proposalDetail.getId());
		pd.setStatus(ProposalDetailStatus.PENDING);
		proposalDetailBean.update(pd);
	}

}