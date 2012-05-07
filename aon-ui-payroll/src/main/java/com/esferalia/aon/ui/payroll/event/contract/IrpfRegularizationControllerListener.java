package com.esferalia.aon.ui.payroll.event.contract;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.payroll.IrpfData;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;

public class IrpfRegularizationControllerListener extends ControllerAdapter{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IrpfRegularizationControllerListener.class.getName());
	
	@Override
	public void afterBeanCanceled(ControllerEvent event)
			throws ControllerListenerException {
		filterIrpfRegularization();
	}
	
	private void filterIrpfRegularization() {
		LinesController regController = (LinesController) getController();
		IrpfData data = ((IrpfData) FormUtil.getController(IPayrollConstants.IRPF_DATA_CONTROLLER_NAME).getTo());
		if(data!=null){
			try {
				regController.initializeModel();
				regController.getCriteria().addGreaterThanOrEqualExpression(
						regController.getFieldName(
								IEntityAlias.IRPF_REGULARIZATION_EFFECTIVE_DATE), data.getStartDate());
				if(data.getEndDate()!=null){
					regController.getCriteria().addLessThanOrEqualExpression(
							regController.getFieldName(
									IEntityAlias.IRPF_REGULARIZATION_EFFECTIVE_DATE), data.getStartDate());
				}
				regController.getCriteria().addOrder(regController.getFieldName(
						IEntityAlias.IRPF_REGULARIZATION_EFFECTIVE_DATE));
				regController.onSearch(null);
				if(regController.getModel().isRowAvailable()){
					regController.onSelectFirst(null);
				} else {
					regController.onReset(null);
				}
			} catch (ManagerBeanException e) {
				String msg = "Error al filtrar los datos de regularizacion";
				LOGGER.error(msg);
			}
		}
	}
		
}
