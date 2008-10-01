package com.code.aon.ui.warehouse.event;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.sales.PointOfSale;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesDetail;
import com.code.aon.sales.dao.ISalesAlias;
import com.code.aon.sales.enumeration.SalesStatus;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.warehouse.controller.DeliveryController;
import com.code.aon.ui.warehouse.controller.DeliveryDetailController;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.Warehouse;

/**
 * A listener for DeliveryDetailController
 * 
 * @author Consulting & Development. Joseba Urkiri - 6-jun-2006
 * @since 1.0
 * 
 */
public class DeliveryDetailControllerListener extends ControllerAdapter {

	/**
	 * DeliveryDetail controller name
	 */
	private static final String DELIVERY_DETAIL_CONTROLLER_NAME = "deliveryDetail";
	
	/**
	 * Delivery controller name
	 */
	private static final String DELIVERY_CONTROLLER_NAME = "delivery";
	
	/**
	 * The logger of the class
	 */
	private static final Logger LOGGER = Logger.getLogger(DeliveryDetailControllerListener.class.getName());

	/**
	 * Creates Sales and SalesDetail for this DeliveryDetail
	 * and inserts referenced salesdetail in this deliverydetail
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeBeanAdded(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		DeliveryDetailController deliveryDetailController = (DeliveryDetailController)event.getController();
		DeliveryController deliveryController = (DeliveryController)AonUtil.getController(DELIVERY_CONTROLLER_NAME);
		DeliveryDetail deliveryDetail = (DeliveryDetail)deliveryDetailController.getTo();
		Warehouse warehouse = new Warehouse();
		warehouse.setId(deliveryController.getWarehouseId());
		deliveryDetail.setWarehouse(warehouse);
		Sales sales = null;
		try {
			if(deliveryDetailController.getModel().getRowCount() == 0){
				sales = insertSales((Delivery)deliveryController.getTo());
				deliveryDetailController.setSales(sales);
			}else{
				sales = deliveryDetailController.getSales();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE,"Error inserting sales for delivery with id: " + ((Sales)deliveryDetailController.getTo()).getId(), e);
		}
		SalesDetail salesDetail = insertSalesDetail(sales, deliveryDetail);
		deliveryDetail.setSalesDetail(salesDetail);
	}

	/** 
	 * Updates SalesDetail information from DeliveryDetail changes
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeBeanUpdated(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		DeliveryDetail deliveryDetail = (DeliveryDetail)event.getController().getTo();
		DeliveryController deliveryController = (DeliveryController)AonUtil.getController(DELIVERY_CONTROLLER_NAME);
		Warehouse warehouse = new Warehouse();
		warehouse.setId(deliveryController.getWarehouseId());
		deliveryDetail.setWarehouse(warehouse);
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
	}

	/**
	 * Removes Sales linked by this DeliveryDetail
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#afterBeanRemoved(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		DeliveryDetail deliveryDetail = (DeliveryDetail)event.getController().getTo();
		try {
			IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
			salesDetailBean.remove(deliveryDetail.getSalesDetail());
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(salesDetailBean.getFieldName(ISalesAlias.SALES_DETAIL_SALES_ID),deliveryDetail.getSalesDetail().getSales().getId());
			if(salesDetailBean.getCount(criteria) == 0){
				IManagerBean salesBean = BeanManager.getManagerBean(Sales.class);
				salesBean.remove(deliveryDetail.getSalesDetail().getSales());
				DeliveryDetailController deliveryDetailController = (DeliveryDetailController)AonUtil.getController(DELIVERY_DETAIL_CONTROLLER_NAME);
				deliveryDetailController.setSales(null);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE,"Error removing salesDetail for deliveryDetail with id: " + deliveryDetail.getId(), e);
		}
	}
	
	/**
	 * Creates a new Sales instead of this Delivery and inserts it
	 * 
	 * @param delivery Related delivery
	 * @return Inserted Sales
	 * @throws ControllerListenerException
	 */
	private Sales insertSales(Delivery delivery) throws ControllerListenerException {
		Sales sales = new Sales();
		DeliveryController deliveryController = (DeliveryController)AonUtil.getController(DELIVERY_CONTROLLER_NAME);
		sales.setPos(obtainPos(deliveryController.getPosId()));
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
			LOGGER.log(Level.SEVERE,"Error inserting sales for delivery with id: " + delivery.getId(), e);
		}
		return null;
	}
	
	/**
	 * Returns workplace.
	 * 
	 * @return workplace
	 */
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

	/**
	 * Creates and inserts a new SalesDetail instead of this DeliveryDetail and Sales
	 * 
	 * @param sales Related Sales
	 * @param deliveryDetail Related DeliveryDetail
	 * @return Inserted SalesDetail
	 */
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
			LOGGER.log(Level.SEVERE,"Error inserting salesDetail for deliveryDetail with id: " + deliveryDetail.getId(), e);
		}
		return null;
	}
	
	/**
	 * Returns the PointOfSale with the given id
	 * 
	 * @param posId the ident of the point of sale
	 * @return the point of sale
	 */
	@SuppressWarnings("unchecked")
	private PointOfSale obtainPos(Integer posId) {
		try {
			IManagerBean posBean = BeanManager.getManagerBean(PointOfSale.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(posBean.getFieldName(ISalesAlias.POINT_OF_SALE_ID), posId);
			Iterator iter = posBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return (PointOfSale)iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining pos with id= " + posId, e);
		}
		return null;
	}
}