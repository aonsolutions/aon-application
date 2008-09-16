package com.code.aon.ui.salesPurchase.event;

import java.util.Iterator;

import javax.faces.event.ActionEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.sales.PointOfSale;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.salesPurchase.controller.PurchaseSalesController;
import com.code.aon.ui.tasDelivery.controller.TasDeliveryImportController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.warehouse.controller.DeliveryController;
import com.code.aon.ui.warehouse.controller.DeliveryDetailController;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.dao.IWarehouseAlias;

public class IncomeDetailControllerPurchaseSalesListener extends ControllerAdapter {

	private static final String PURCHASE_SALES_CONTROLLER_NAME = "purchaseSales";
	
	private static final String DELIVERY_CONTROLLER_NAME = "delivery";
	
	private static final String DELIVERY_DETAIL_CONTROLLER_NAME = "deliveryDetail";

	private static final String TAS_DELIVERY_IMPORT_CONTROLLER_NAME = "tasDeliveryImport";

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		PurchaseSalesController purchaseSalesController = (PurchaseSalesController)AonUtil.getController(PURCHASE_SALES_CONTROLLER_NAME);
		purchaseSalesController.onInitialize();
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		try {
			PurchaseSalesController purchaseSalesController = (PurchaseSalesController)AonUtil.getController(PURCHASE_SALES_CONTROLLER_NAME);
			DeliveryController deliveryController = (DeliveryController)AonUtil.getController(DELIVERY_CONTROLLER_NAME);
			if(purchaseSalesController.isDeliveryRelated()){
				/* La orden de reparación elegida no tiene albarán asociado*/
				if(purchaseSalesController.getDelivery().getId() == null){
					deliveryController.onReset((ActionEvent)null);
					importDelivery(purchaseSalesController.getDelivery(), purchaseSalesController.getSupportOrderId());
					purchaseSalesController.setDelivery((Delivery)deliveryController.getTo());
				}else{ /* La orden de reparación elegida tiene albarán asociado*/
					deliveryControllerForUpdate(purchaseSalesController.getDelivery(), purchaseSalesController.getDeliveryDetail().getWarehouse().getId());
				}
				DeliveryDetail deliveryDetail = purchaseSalesController.getDeliveryDetail();
				deliveryDetail.setDelivery(purchaseSalesController.getDelivery());
				deliveryController.setWarehouseId(deliveryDetail.getWarehouse().getId());
				insertDeliveryDetail(deliveryDetail);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	private void importDelivery(Delivery delivery, Integer supportOrderId) throws ManagerBeanException {
		TasDeliveryImportController importController = (TasDeliveryImportController)AonUtil.getController(TAS_DELIVERY_IMPORT_CONTROLLER_NAME);
		DeliveryController deliveryController = (DeliveryController)AonUtil.getController(DELIVERY_CONTROLLER_NAME);
		deliveryController.setPosId(obtainDefaultPos());
		deliveryController.setSupportOrderId(supportOrderId);
		Delivery to = (Delivery)deliveryController.getTo();
		to.setIssueTime(delivery.getIssueTime());
		to.setSeries(delivery.getSeries());
		to.setNumber(delivery.getNumber());
		importController.importDelivery(null);
	}

	private void deliveryControllerForUpdate(Delivery dbDelivery, Integer warehouseId) throws ManagerBeanException {
		DeliveryController deliveryController = (DeliveryController)AonUtil.getController(DELIVERY_CONTROLLER_NAME);
		deliveryController.setWarehouseId(warehouseId);
		Criteria criteria =  new Criteria();
		criteria.addEqualExpression(deliveryController.getFieldName(IWarehouseAlias.DELIVERY_SERIES), dbDelivery.getSeries());
		criteria.addEqualExpression(deliveryController.getFieldName(IWarehouseAlias.DELIVERY_NUMBER), dbDelivery.getNumber());
		deliveryController.setCriteria(criteria);
		deliveryController.onSearch(null);
		deliveryController.getModel().setRowIndex(0);
		deliveryController.onSelect(null);
	}

	private void insertDeliveryDetail(DeliveryDetail deliveryDetail) throws ManagerBeanException {
		DeliveryDetailController deliveryDetailController = (DeliveryDetailController)AonUtil.getController(DELIVERY_DETAIL_CONTROLLER_NAME);
		deliveryDetailController.onReset((ActionEvent)null);
		DeliveryDetail to = (DeliveryDetail)deliveryDetailController.getTo(); 
		to.setDescription(deliveryDetail.getDescription());
		to.setDiscountExpression(deliveryDetail.getDiscountExpression());
		to.setDelivery(deliveryDetail.getDelivery());
		to.setItem(obtainItemData(deliveryDetail.getItem().getId()));
		to.setPrice(deliveryDetail.getPrice());
		to.setSalesDetail(null);
		to.setQuantity(deliveryDetail.getQuantity());
		to.setWarehouse(deliveryDetail.getWarehouse());
		deliveryDetailController.accept(null);
	}
	
	@SuppressWarnings("unchecked")
	private Integer obtainDefaultPos() throws ManagerBeanException {
		IManagerBean posBean = BeanManager.getManagerBean(PointOfSale.class);
		Iterator iter = posBean.getList(null, 0, 1).iterator();
		if(iter.hasNext()){
			return ((PointOfSale)iter.next()).getId();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private Item obtainItemData(Integer id) throws ManagerBeanException {
		IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(itemBean.getFieldName(IProductAlias.ITEM_ID), id);
		Iterator iter = itemBean.getList(criteria, 0, 1).iterator();
		if(iter.hasNext()){
			return (Item)iter.next();
		}
		return null;
	}
}