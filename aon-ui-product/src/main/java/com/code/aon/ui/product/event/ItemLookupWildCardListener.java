package com.code.aon.ui.product.event;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.entity.IEntityAlias;

public class ItemLookupWildCardListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void beforeModelSearched(ControllerEvent event) throws ControllerListenerException {
		IController controller = event.getController();
		try {					
			Expression expr1 = ExpressionUtilities.getEqualExpression(controller.getFieldName(IEntityAlias.ITEM_PRODUCT_SERIALIZABLE), Boolean.FALSE);
			Expression expr2 = ExpressionUtilities.getNullExpression(controller.getFieldName(IEntityAlias.ITEM_SERIAL_NUMBER));
			controller.getCriteria().addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
		} catch (ManagerBeanException ex) {
			throw new ControllerListenerException(ex.getMessage(), ex);
		}
	}

}