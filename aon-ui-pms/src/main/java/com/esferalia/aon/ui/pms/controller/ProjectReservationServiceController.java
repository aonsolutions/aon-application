package com.esferalia.aon.ui.pms.controller;

import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.LinesController;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.ProjectReservationService;
import com.esferalia.aon.pms.ProjectReservationServiceDetail;

public class ProjectReservationServiceController extends LinesController {

	private IPriceStrategy priceStrategy;
	
	public IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null){
			priceStrategy = PriceStrategyFactory.getPriceStrategy();
		}
		return priceStrategy;
	}

	@Override
	public void onReset(ActionEvent arg0) {
	}

	@Override
	public void onSelect(ActionEvent event) {
	}

	public boolean isShowServiceDetail() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			ProjectReservationService service = (ProjectReservationService)getModel().getRowData();
			return service.isShowServiceDetail();
		}
		return false;
	}

	public List<ITransferObject> getServiceDetailList() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			ProjectReservationService service = (ProjectReservationService)getModel().getRowData();
			return getServiceDetailList(service);
		}
		return null;
	}

	private List<ITransferObject> getServiceDetailList(ProjectReservationService service) throws ManagerBeanException {
		IManagerBean serviceDetailBean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(serviceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_ID), service.getId());
		criteria.addOrder(serviceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_EFFECTIVE_DATE));
		return serviceDetailBean.getList(criteria);
	}



	/*public void onItemChanged(LookupChangeEvent event) {
		ProjectReservationService reservationService = (ProjectReservationService)getTo();
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Item item = (Item)event.getNewValue();
			reservationService.setItem(item);
			reservationService.setDescription(item.getProduct().getName() + (item.getDetail() != null ? " " + item.getDetail() : ""));
			if (reservationService.getQuantity() == 0) {
				reservationService.setQuantity(1);
			}
			Date date = (reservationService.getEffectiveDate() != null) ? reservationService.getEffectiveDate() : new Date();
			ProjectReservationController master = (ProjectReservationController)getMasterController();
			Tariff tariff = ((ProjectReservation)master.getTo()).getTariff();
			reservationService.setPrice(getPriceStrategy().getUnitPrice(reservationService, date, tariff));
			reservationService.setTaxableBase(getPriceStrategy().getBasePrice(reservationService));
		}
	}	

	public void onQuantityChanged(ValueChangeEvent event) {
		ProjectReservationService reservationService = (ProjectReservationService)getTo();
		if (reservationService.getItem() != null && reservationService.getItem().getId() != null) {
			if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
				reservationService.setQuantity((Double)event.getNewValue());
	
				Date date = (reservationService.getEffectiveDate() != null) ? reservationService.getEffectiveDate() : new Date();
				ProjectReservationController master = (ProjectReservationController)getMasterController();
				Tariff tariff = ((ProjectReservation)master.getTo()).getTariff();
				reservationService.setPrice(getPriceStrategy().getUnitPrice(reservationService, date, tariff));
			} else {
				reservationService.setQuantity(1);
			}
		}
		reservationService.setTaxableBase(getPriceStrategy().getBasePrice(reservationService));
	}	

	public void onPriceChanged(ValueChangeEvent event) {
		ProjectReservationService reservationService = (ProjectReservationService)getTo();
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			reservationService.setPrice((Double)event.getNewValue());
		} else {
			reservationService.setPrice(0);
		}
		reservationService.setTaxableBase(getPriceStrategy().getBasePrice(reservationService));
	}*/	

}