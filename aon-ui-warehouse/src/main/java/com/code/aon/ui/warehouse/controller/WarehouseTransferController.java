package com.code.aon.ui.warehouse.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.WorkPlace;
import com.code.aon.ui.common.controller.IAuditableController;
import com.code.aon.ui.config.controller.ConfigCollectionsController;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.config.controller.HeaderObjectController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.WarehouseTransfer;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Series;

public class WarehouseTransferController extends HeaderObjectController implements IAuditableController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private boolean showAuditInfoWindow;
	private WorkPlace workplace;

	public boolean isShowAuditInfoWindow() {
		return showAuditInfoWindow;
	}
	public void setShowAuditInfoWindow(boolean showAuditInfoWindow) {
		this.showAuditInfoWindow = showAuditInfoWindow;
	}
	
	public WorkPlace getWorkplace(){
		return workplace;
	}
	
	public void setWorkplace(WorkPlace workplace){
		this.workplace = workplace;
	}
	
	@Override
	public void onReset(ActionEvent event) {
		super.onReset(event);
		initSeries();
	}

	@Override
	public List<SelectItem> getSeriesCodes() throws ManagerBeanException {
		WorkPlace workplace = getWorkplace();
		if(workplace != null){
			setWorkplace(null);
			String domainName = AonUtil.getDomainName();
			Integer domainId = workplace.getDomain();
			String login = AonUtil.getRemoteUser();
			LinkedList<Series> seriesList = AON.getSeriesDeliveryList(domainName, domainId, login, workplace.getScope().getId());
			List<SelectItem> list = new LinkedList<SelectItem>();
			seriesList.stream().forEach(s -> {
				SelectItem selectItem = new SelectItem();
				selectItem.setDescription(s.getCode());
				selectItem.setLabel(s.getCode());
				selectItem.setValue(s.getCode());
				list.add(selectItem);
			});
			return list;
		}
		ConfigCollectionsController ccc = (ConfigCollectionsController) AonUtil.getRegisteredBean(ConfigConstants.CONFIG_COLLECTIONS);
		return ccc.getDeliverySeriesIds();
	}		
	
	public void onGoToInventory( ActionEvent event ) throws ManagerBeanException {
		WarehouseTransfer wt = (WarehouseTransfer) getTo();
		InventoryController ic = (InventoryController) AonUtil.getRegisteredBean(IWarehouseConstants.INVENTORY_CONTROLLER_NAME);
		ic.select(event, wt.getInventory());
		ic.setBackAction(formAction());
	}
	
	public boolean isReadOnly() {
		WarehouseTransfer wt = (WarehouseTransfer) getTo();
		return (wt.getInventory() != null) && (wt.getInventory().getId() != null);
	}
	
}