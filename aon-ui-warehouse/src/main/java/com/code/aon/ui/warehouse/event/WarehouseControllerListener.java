package com.code.aon.ui.warehouse.event;

import static com.code.aon.ui.audit.controller.IAuditConstants.ACTION_DENIED_CONTROLLER_NAME;

import com.code.aon.AonVersion;
import com.code.aon.audit.enumeration.Module;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.audit.controller.ActionDeniedController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Warehouse;
import com.esferalia.aon.entity.IEntityAlias;

public class WarehouseControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Warehouse warehouse = (Warehouse) event.getController().getTo();
		checkWarehouse(warehouse);
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		Warehouse warehouse = (Warehouse) event.getController().getTo();
		checkWarehouse(warehouse);
	}

	private void checkWarehouse(Warehouse warehouse) throws ControllerListenerException {
		ActionDeniedController adc = (ActionDeniedController) AonUtil.getRegisteredBean(ACTION_DENIED_CONTROLLER_NAME);
		if ( adc.isDeniedModule(Module.HOTEL.getName()) ) {
			if (warehouse.getWorkPlace() != null && warehouse.getWorkPlace().getId() != null) {
				try {
					IManagerBean warehouseBean = BeanManager.getManagerBean(Warehouse.class);
					Criteria criteria = new Criteria();
					if (warehouse.getId() != null) {
						criteria.addNotEqualExpression(warehouseBean.getFieldName(IEntityAlias.WAREHOUSE_ID), warehouse.getId());
					}
					criteria.addEqualExpression(warehouseBean.getFieldName(IEntityAlias.WAREHOUSE_WORK_PLACE_ID), warehouse.getWorkPlace().getId());
					if (warehouseBean.getCount(criteria) > 0) {
						throw new ControllerListenerException("El Centro de Trabajo ya tiene Almacen asignado.");
					}
				} catch(ManagerBeanException e) {
					throw new ControllerListenerException(e.getMessage(), e);
				}
			}			
		}
	}
	
}