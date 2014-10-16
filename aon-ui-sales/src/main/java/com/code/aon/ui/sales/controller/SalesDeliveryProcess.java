package com.code.aon.ui.sales.controller;

import static com.code.aon.common.IProgression.FINISH_VALUE;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.common.IProgression;
import com.code.aon.sales.Sales;
import com.code.aon.sales.bridge.DeliveryManager;
import com.code.aon.ui.common.ILongProcess;
import com.code.aon.ui.warehouse.controller.DeliveryController;
import com.code.aon.warehouse.Delivery;

public class SalesDeliveryProcess implements ILongProcess {

	private OrderServerController controller;
	
	private DeliveryController deliveryController;
	
	public SalesDeliveryProcess(OrderServerController orderServerController, DeliveryController deliveryController) {
		this.controller = orderServerController;
		this.deliveryController = deliveryController;
	}

	private void updateProgress( int current, int total ) {
		controller.getProgressionState().setProgressionCurrentValue(Math.round((current * 100.0)/total));
	}
	
	public int getMaxDeliveryNumber() {
		return this.deliveryController.obtainMaxNumber(controller.getDeliverySeries());
	}	
	
	@Override
	public void execute() {
		controller.setDeliveryIds(null);
		try {
			int total = controller.getCheckList().size();
			List<Integer> deliveryIds = new LinkedList<Integer>();
			DeliveryManager deliveryManager = new DeliveryManager();
			int i = 0;
			for(Serializable id : controller.getCheckList()){
				Sales sales = (Sales) controller.getManagerBean().get(id);
				int number = getMaxDeliveryNumber();
				Date date = controller.isSalesDateCheck()?sales.getDate():controller.getDeliveryDate(); 
				Delivery delivery = deliveryManager.salesDelivery(sales, controller.getDeliverySeries(), number, date, controller.getDeliveryWarehouse());
				deliveryIds.add(delivery.getId());
				updateProgress(++i, total);
			}
			controller.setDeliveryIds(deliveryIds);
			controller.getProgressionState().setProgressionCurrentValue(FINISH_VALUE);
		} catch (Throwable e) {
			String msg = "No se pudo grabar los Albaranes de Ventas. (" + e.getMessage()+ ")";
			controller.getProgressionState().setProgressionErrorMessage(msg);
			controller.getProgressionState().setProgressionCurrentValue(IProgression.ERROR_VALUE);
		}
	}

}
