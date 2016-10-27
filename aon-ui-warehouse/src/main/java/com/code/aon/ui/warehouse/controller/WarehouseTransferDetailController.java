package com.code.aon.ui.warehouse.controller;

import static javax.faces.application.FacesMessage.SEVERITY_ERROR;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.validator.ValidatorException;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.WarehouseTransferDetail;

public class WarehouseTransferDetailController extends LinesController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public void onRefresh(ActionEvent event) {
		initializeModel();
	}
	
	public void quantityCheck(FacesContext context, UIComponent component, Object value) throws ManagerBeanException {
		Double quantity = (Double) value;
		if ( quantity <= 0 ) {
			FacesMessage fm = new FacesMessage(AonUtil.getMessage(ICommonMessages.WAREHOUSE_QUANTITY_POSITIVE));
			fm.setSeverity(SEVERITY_ERROR);
			throw new ValidatorException(fm);		
		}
		WarehouseTransferDetail wtd = (WarehouseTransferDetail) getTo();
		if ( wtd.getItem().getProduct().isSerializable() && !wtd.getItem().getProduct().isLotable() && (quantity != 1) ) {
			FacesMessage fm = new FacesMessage(AonUtil.getMessage(ICommonMessages.WAREHOUSE_QUANTITY_SERIALIZABLE));
			fm.setSeverity(SEVERITY_ERROR);
			throw new ValidatorException(fm);					
		}
	}	
	
}