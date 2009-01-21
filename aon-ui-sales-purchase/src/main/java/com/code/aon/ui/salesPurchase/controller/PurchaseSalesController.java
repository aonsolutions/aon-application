package com.code.aon.ui.salesPurchase.controller;

import java.util.Iterator;

import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.customer.dao.ICustomerAlias;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.product.ProductCategory;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.tas.SupportOrder;
import com.code.aon.tas.dao.ITASAlias;
import com.code.aon.tasDelivery.TasDelivery;
import com.code.aon.tasDelivery.dao.ITasDeliveryAlias;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.warehouse.controller.IncomeDetailController;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.IncomeDetail;
import com.code.aon.warehouse.Warehouse;

public class PurchaseSalesController extends BasicController {
	
	private static final String INCOME_DETAIL_CONTROLLER_NAME = "incomeDetail";
	
	private boolean popupVisible;

	private boolean deliveryRelated;
	
	private Integer supportOrderId;

	private Delivery delivery;

	private DeliveryDetail deliveryDetail;


	public boolean isPopupVisible() {
		return popupVisible;
	}

	public void setPopupVisible(boolean popupVisible) {
		this.popupVisible = popupVisible;
	}

	public boolean isDeliveryRelated() {
		return deliveryRelated;
	}

	public void setDeliveryRelated(boolean deliveryRelated) {
		this.deliveryRelated = deliveryRelated;
	}

	public Integer getSupportOrderId() {
		return supportOrderId;
	}

	public void setSupportOrderId(Integer supportOrderId) {
		this.supportOrderId = supportOrderId;
	}

	public Delivery getDelivery() {
		return delivery;
	}

	public void setDelivery(Delivery delivery) {
		this.delivery = delivery;
	}

	public DeliveryDetail getDeliveryDetail() {
		return deliveryDetail;
	}

	public void setDeliveryDetail(DeliveryDetail deliveryDetail) {
		this.deliveryDetail = deliveryDetail;
	}

	@SuppressWarnings("unused")
	public void onSave(ActionEvent event) {
		setDeliveryRelated(true);
		setPopupVisible(false);
		IncomeDetailController incomeDetailController = (IncomeDetailController)AonUtil.getController(INCOME_DETAIL_CONTROLLER_NAME);
		((IncomeDetail)incomeDetailController.getTo()).setItem(getDeliveryDetail().getItem());
		((IncomeDetail)incomeDetailController.getTo()).setDescription(getDeliveryDetail().getDescription());
		((IncomeDetail)incomeDetailController.getTo()).setPrice(getDeliveryDetail().getItem().getPurchasePrice());
	}

	@SuppressWarnings("unused")
	public void onClose(ActionEvent event) {
		setPopupVisible(false);
		if (!isDeliveryRelated()) {
			this.onInitialize();
		}
	}

	@SuppressWarnings("unused")
	public void openPopup(ActionEvent event) {
		setPopupVisible(true);
	}

	public void onInitialize() {
		createNewDelivery();
		createNewDeliveryDetail();
		setDeliveryRelated(false);
		setPopupVisible(false);
		setSupportOrderId(null);
	}

	private void createNewDeliveryDetail() {
		this.deliveryDetail = new DeliveryDetail();
		this.deliveryDetail.setDiscountExpression(new DiscountExpression("0.0"));
		this.deliveryDetail.setDelivery(new Delivery());
		this.deliveryDetail.setItem(new Item());
		this.deliveryDetail.getItem().setProduct(new Product());
		this.deliveryDetail.getItem().getProduct().setCategory(new ProductCategory());
		this.deliveryDetail.setWarehouse(new Warehouse());
	}

	private void createNewDelivery() {
		this.delivery = new Delivery();
		this.delivery.setCustomer(new Customer());
		this.delivery.getCustomer().setRegistry(new Registry());
	}
	
	@SuppressWarnings("unchecked")
	public void supportOrderChanged(ValueChangeEvent event) throws ManagerBeanException{
		if (event.getPhaseId() == PhaseId.ANY_PHASE) {
			event.setPhaseId(PhaseId.INVOKE_APPLICATION );
			event.queue();
		}
		if (event.getPhaseId() == PhaseId.INVOKE_APPLICATION) {
			if(event.getNewValue()!=null){
				IManagerBean tasDeliveryBean = BeanManager.getManagerBean(TasDelivery.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(tasDeliveryBean.getFieldName(ITasDeliveryAlias.TAS_DELIVERY_SUPPORT_ORDER_ID), event.getNewValue());
				Iterator iter = tasDeliveryBean.getList(criteria, 0, 1).iterator();
				if(iter.hasNext()){
					TasDelivery tasDelivery = (TasDelivery)iter.next();
					setDelivery(tasDelivery.getDelivery());
				}else{
					createNewDelivery();
				}
			}
		}
	}
	
	public boolean isScopeNeeded() throws ManagerBeanException {
		IManagerBean suppOrderBean = BeanManager.getManagerBean(SupportOrder.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(suppOrderBean.getFieldName(ITASAlias.SUPPORT_ORDER_ID), supportOrderId);
		Iterator iter = suppOrderBean.getList(criteria, 0, 1).iterator();
		if(iter.hasNext()){
			SupportOrder supportOrder = (SupportOrder)iter.next();

			IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
			criteria = new Criteria();
			criteria.addEqualExpression(customerBean.getFieldName(ICustomerAlias.CUSTOMER_ID), supportOrder.getTarget().getRegistry().getId());
			return (customerBean.getList(criteria).size() == 0);
		}
		return false;
	}
}