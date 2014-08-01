package com.code.aon.warehouse.event;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.ql.Criteria;
import com.code.aon.warehouse.Warehouse;
import com.esferalia.aon.entity.IEntityAlias;

public class WarehouseBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Warehouse warehouse = (Warehouse)evt.getTo();
		checkWarehouse(warehouse);
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Warehouse warehouse = (Warehouse)evt.getTo();
		checkWarehouse(warehouse);
	}

	private void checkWarehouse(Warehouse warehouse) throws ManagerBeanVetoListenerException{
		if (warehouse.getWorkPlace() != null && warehouse.getWorkPlace().getId() != null) {
			try {
				IManagerBean warehouseBean = BeanManager.getManagerBean(Warehouse.class);
				Criteria criteria = new Criteria();
				if (warehouse.getId() != null) {
					criteria.addNotEqualExpression(warehouseBean.getFieldName(IEntityAlias.WAREHOUSE_ID), warehouse.getId());
				}
				criteria.addEqualExpression(warehouseBean.getFieldName(IEntityAlias.WAREHOUSE_WORK_PLACE_ID), warehouse.getWorkPlace().getId());
				if (warehouseBean.getCount(criteria) > 0) {
					throw new ManagerBeanVetoListenerException("El Centro de Trabajo ya tiene Almacen asignado.");
				}
			} catch(ManagerBeanException e) {
				throw new ManagerBeanVetoListenerException(e.getMessage(), e);
			}
		} else {
			try {
				IManagerBean warehouseBean = BeanManager.getManagerBean(Warehouse.class);
				Criteria criteria = new Criteria();
				if (warehouse.getId() != null) {
					criteria.addNotEqualExpression(warehouseBean.getFieldName(IEntityAlias.WAREHOUSE_ID), warehouse.getId());
				}
				criteria.addNullExpression(warehouseBean.getFieldName(IEntityAlias.WAREHOUSE_WORK_PLACE));
				if (warehouseBean.getCount(criteria) > 0) {
					throw new ManagerBeanVetoListenerException("Ya existe un almacén común.");
				}
			} catch(ManagerBeanException e) {
				throw new ManagerBeanVetoListenerException(e.getMessage(), e);
			}
		}
	}

}