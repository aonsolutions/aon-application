package com.code.aon.ui.product.event;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.registry.RegistryItem;
import com.code.aon.registry.enumeration.RegistryMode;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.product.controller.ItemSupplierController;
import com.esferalia.aon.entity.IEntityAlias;

public class ItemSupplierControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		ItemSupplierController controller = (ItemSupplierController)event.getController();
		RegistryItem registryItem = (RegistryItem)controller.getTo();

		try {
			registryItem.setType(RegistryMode.SUPPLIER);
			registryItem.setPriority(calculateNextPriority((Item)controller.getMasterController().getTo()));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		event.getController().initializeModel();
	}

	private	Integer calculateNextPriority(Item item) throws ManagerBeanException {
		IManagerBean rItemBean = BeanManager.getManagerBean(RegistryItem.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_ITEM_ID), item.getId());
		criteria.addEqualExpression(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_TYPE), RegistryMode.SUPPLIER);
		Projection projection = Projection.max(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_PRIORITY));
		Object value = rItemBean.getUniqueResult(projection, criteria);
		return (value != null) ? ((Integer)value) + 1 : 1;
	}

}
