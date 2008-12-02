package com.code.aon.ui.warehouse.event;

import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesDetail;
import com.code.aon.sales.dao.ISalesAlias;
import com.code.aon.sales.enumeration.SalesStatus;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.warehouse.controller.DeliveryController;
import com.code.aon.ui.warehouse.controller.DeliveryDetailController;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;

/**
 * A listener for DeliveryDetailController
 * 
 * @author Consulting & Development. Joseba Urkiri - 6-jun-2006
 * @since 1.0
 * 
 */
public class DeliveryDetailControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		DeliveryDetailController deliveryDetailController = (DeliveryDetailController)event.getController();
		DeliveryDetail deliveryDetail = (DeliveryDetail)deliveryDetailController.getTo();

		DeliveryController deliveryController = (DeliveryController)deliveryDetailController.getMasterController();
		deliveryDetail.setWarehouse(deliveryController.getWarehouse());

/*
		Sales sales = null;
		try {
			if (deliveryDetailController.getModel().getRowCount() == 0) {
				sales = insertSales((Delivery)deliveryController.getTo());
				//deliveryDetailController.setSales(sales);
			}else{
				//sales = deliveryDetailController.getSales();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE,"Error inserting sales for delivery with id: " + ((Sales)deliveryDetailController.getTo()).getId(), e);
		}
		SalesDetail salesDetail = insertSalesDetail(sales, deliveryDetail);
		deliveryDetail.setSalesDetail(salesDetail);
*/
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		DeliveryDetailController deliveryDetailController = (DeliveryDetailController)event.getController();
		DeliveryDetail deliveryDetail = (DeliveryDetail)deliveryDetailController.getTo();

		DeliveryController deliveryController = (DeliveryController)deliveryDetailController.getMasterController();
		deliveryDetail.setWarehouse(deliveryController.getWarehouse());

/*
		SalesDetail salesDetail = deliveryDetail.getSalesDetail();
		salesDetail.setItem(deliveryDetail.getItem());
        salesDetail.setDescription(deliveryDetail.getDescription());
		salesDetail.setPrice(deliveryDetail.getPrice());
		salesDetail.setQuantity(deliveryDetail.getQuantity());
		IManagerBean salesDetailBean;
		try {
			salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
			salesDetail = (SalesDetail)salesDetailBean.update(salesDetail);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE,"Error updating salesDetail for deliveryDetail with id: " + deliveryDetail.getId(), e);
		}
*/
	}

	@Override
	public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		DeliveryDetailController deliveryDetailController = (DeliveryDetailController)event.getController();
		DeliveryDetail deliveryDetail = (DeliveryDetail)deliveryDetailController.getTo();
		SalesDetail salesDetail = deliveryDetail.getSalesDetail();
		if (salesDetail != null) {
			try {
				IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
				salesDetailBean.remove(deliveryDetail.getSalesDetail());
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(salesDetailBean.getFieldName(ISalesAlias.SALES_DETAIL_SALES_ID), salesDetail.getSales().getId());
				if (salesDetailBean.getCount(criteria) == 0) {
					IManagerBean salesBean = BeanManager.getManagerBean(Sales.class);
					salesBean.remove(deliveryDetail.getSalesDetail().getSales());
					//deliveryDetailController.setSales(null);
				}
			} catch (ManagerBeanException e) {
				throw new ControllerListenerException(e.getMessage());
			}
		}
	}
	

























	private Sales insertSales(Delivery delivery) throws ControllerListenerException {
		Sales sales = new Sales();
		sales.setCustomer((delivery.getCustomer()));
		sales.setStatus(SalesStatus.CLOSED);
		sales.setSeries(delivery.getSeries());
		sales.setNumber(SeriesNumberUtil.obtainNumber(delivery.getSeries(), "Sales"));
		sales.setPayMethod(null);
		sales.setIssueDate(delivery.getIssueTime());
		sales.setShippingAddress(delivery.getRaddress());
		sales.setWorkPlace(obtainWorkPlace());
		IManagerBean salesBean;
		try {
			salesBean = BeanManager.getManagerBean(Sales.class);
			sales = (Sales)salesBean.insert(sales);
			return sales;
		} catch (ManagerBeanException e) {
		}
		return null;
	}
	
	private WorkPlace obtainWorkPlace() {
		try {
			IManagerBean wpBean = BeanManager.getManagerBean(WorkPlace.class);
			List<ITransferObject> wpLst = wpBean.getList(null);
			if (wpLst.size() > 0) {
				WorkPlace wp = (WorkPlace)wpLst.get(0);
				return wp;
			}
		}
		catch (ManagerBeanException mbe) {
			mbe.printStackTrace();
		}
		return null;
	}

	private SalesDetail insertSalesDetail(Sales sales, DeliveryDetail deliveryDetail) {
		SalesDetail salesDetail = new SalesDetail();
		salesDetail.setSales(sales);
		salesDetail.setItem(deliveryDetail.getItem());
        salesDetail.setDescription(deliveryDetail.getDescription());
		salesDetail.setPrice(deliveryDetail.getPrice());
		salesDetail.setQuantity(deliveryDetail.getQuantity());
		IManagerBean salesDetailBean;
		try {
			salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
			salesDetail = (SalesDetail)salesDetailBean.insert(salesDetail);
			return salesDetail;
		} catch (ManagerBeanException e) {
		}
		return null;
	}

}