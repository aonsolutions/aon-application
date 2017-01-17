package com.code.aon.ui.sales.event;

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
import com.code.aon.ql.Criteria;
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
import com.code.aon.ui.sales.util.SalesUtils;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.carrier.Carrier;
import com.esferalia.aon.entity.IEntityAlias;

public class SalesControllerListener extends ControllerAdapter implements ISalesConstants {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void afterModelInitialized(ControllerEvent event)throws ControllerListenerException {
		SalesController controller = (SalesController) event.getController();
		controller.setListTotal(null);
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		CompanyCollectionsController companyColls = (CompanyCollectionsController)AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
		SalesController controller = (SalesController)event.getController();
		Sales sales = (Sales) controller.getTo();
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
			controller.setLinesDeliveryDate(null);
			controller.resetSalesPayMethod();
			controller.initSeries();
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
			controller.loadDefaultPayMethod(((Sales)controller.getTo()).getCustomer().getRegistry(), false);
			if(((Sales)controller.getTo()).getCarrier()==null){
				((Sales)controller.getTo()).setCarrier((Carrier) BeanManager.getManagerBean(Carrier.class).createNewTo());
			}
			controller.setShippingAlternativeAddress(controller.isShippingAlternativeAddressDefined());
			
			controller.setShowPurchaseWindow(false);
			controller.setPurchaseGenerator(null);
			controller.setLinesDeliveryDate(obtainLinesDeliveryDate());
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		IController salesDetailController = FormUtil.getController(SALES_DETAIL_CONTROLLER_NAME);
		salesDetailController.onReset(null);
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		SalesController controller = (SalesController)event.getController();
		if(!controller.isShippingAlternativeAddress()){
			emptyShippingAlternativeAddress((Sales)controller.getTo());
		}
		controller.setShippingAlternativeAddress(controller.isShippingAlternativeAddressDefined());
	}
	
	@Override
	public void beforeBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		SalesController controller = (SalesController) event.getController();
		Sales sales = (Sales) controller.getTo();
		SalesUtils utils = new SalesUtils();
		for(ITransferObject to: sales.getDetailList()){
			SalesDetail detail = (SalesDetail) to;
			if (utils.isManufactureDone(detail)) {
				throw new ControllerListenerException(
						detail.getDescription() + ": " +
						"No se puede borrar, el producto está en elaboración.");
			}
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		SalesController controller = (SalesController) event.getController();
		controller.setListTotal(null);
		try {
			updateLinesDeliveryDate();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}
	
	private Date obtainLinesDeliveryDate() throws ManagerBeanException {
		SalesController controller = (SalesController)this.getController();
		Sales sales = (Sales)controller.getTo();
		IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(salesDetailBean.getFieldName(IEntityAlias.SALES_DETAIL_SALES_ID), sales.getId());
		criteria.addNotNullExpression(salesDetailBean.getFieldName(IEntityAlias.SALES_DETAIL_DELIVERY_DATE));
		List<ITransferObject> list = salesDetailBean.getList(criteria);
		if(!list.isEmpty()){
			return ((SalesDetail)list.get(0)).getDeliveryDate();
		}
		return null;
	}
	
	private void updateLinesDeliveryDate() throws ManagerBeanException {
		SalesController controller = (SalesController)this.getController();
		Sales sales = (Sales)controller.getTo();
		Date deliveryDate = controller.getLinesDeliveryDate();
		IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(salesDetailBean.getFieldName(IEntityAlias.SALES_DETAIL_SALES_ID), sales.getId());
		for (ITransferObject ito : salesDetailBean.getList(criteria)) {
			SalesDetail salesDetail = (SalesDetail)ito;
			if (salesDetail.getDeliveryDate() == null) {
				salesDetail.setDeliveryDate(deliveryDate);
				salesDetailBean.update(salesDetail);
			}
		}
		IController salesDetailController = FormUtil.getController(SALES_DETAIL_CONTROLLER_NAME);
		salesDetailController.onSearch(null);
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