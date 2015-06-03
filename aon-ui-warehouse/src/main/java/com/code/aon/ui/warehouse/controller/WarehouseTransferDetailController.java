package com.code.aon.ui.warehouse.controller;

import static javax.faces.application.FacesMessage.SEVERITY_ERROR;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.validator.ValidatorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.WarehouseTransferDetail;
import com.esferalia.aon.entity.IEntityAlias;

public class WarehouseTransferDetailController extends LinesController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(WarehouseTransferDetailController.class.getName());
	
	private ItemFilter itemFilter;
	
	public void onRefresh(ActionEvent event) {
		initializeModel();
	}
	
	public IControllerListener getItemFilter() {
		if ( this.itemFilter == null ) {
			this.itemFilter = new ItemFilter();
		}		
		return this.itemFilter;
	}		
	
	public void quantityCheck(FacesContext context, UIComponent component, Object value) throws ManagerBeanException {
		Double quantity = (Double) value;
		if ( quantity <= 0 ) {
			FacesMessage fm = new FacesMessage(AonUtil.getMessage(ICommonMessages.WAREHOUSE_QUANTITY_POSITIVE));
			fm.setSeverity(SEVERITY_ERROR);
			throw new ValidatorException(fm);		
		}
		WarehouseTransferDetail wtd = (WarehouseTransferDetail) getTo();
		if ( wtd.getItem().getProduct().isSerializable() && (quantity != 1) ) {
			FacesMessage fm = new FacesMessage(AonUtil.getMessage(ICommonMessages.WAREHOUSE_QUANTITY_SERIALIZABLE));
			fm.setSeverity(SEVERITY_ERROR);
			throw new ValidatorException(fm);					
		}
	}	
	
	private static class ItemFilter extends ControllerAdapter {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		@Override
		public void beforeModelInitialized(ControllerEvent event)
				throws ControllerListenerException {
			IController controller = event.getController();
			try {					
				String serialNumber = controller.getFieldName(IEntityAlias.ITEM_SERIAL_NUMBER);
				Expression exprA = ExpressionUtilities.getNotNullExpression(serialNumber);
				String serialDate = controller.getFieldName(IEntityAlias.ITEM_SERIAL_DATE);
				Expression exprB = ExpressionUtilities.getNotNullExpression(serialDate);
				Expression exprAorB = ExpressionUtilities.getOrExpression(exprA, exprB);
				String serializable = controller.getFieldName(IEntityAlias.ITEM_PRODUCT_SERIALIZABLE);
				Expression exprC = ExpressionUtilities.getEqualExpression(serializable, Boolean.FALSE);
				String lotable = controller.getFieldName(IEntityAlias.ITEM_PRODUCT_LOTABLE);
				Expression exprD = ExpressionUtilities.getEqualExpression(lotable, Boolean.FALSE);
				Expression exprCandD = ExpressionUtilities.getAndExpression(exprC, exprD);
				controller.getCriteria().addExpression(ExpressionUtilities.getOrExpression(exprAorB, exprCandD));
			} catch (ManagerBeanException e) {
				LOGGER.error("Error filtering items", e);
			}
		}

	}
	
}