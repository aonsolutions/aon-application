package com.code.aon.ui.product.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.CatalogueItem;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class ItemCatalogueControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		CatalogueItem catalogueItem = (CatalogueItem)event.getController().getTo();
		try {
			IManagerBean catalogueItemBean = BeanManager.getManagerBean(CatalogueItem.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(catalogueItemBean.getFieldName(IProductAlias.CATALOGUE_ITEM_ITEM_ID), catalogueItem.getItem().getId());
			criteria.addEqualExpression(catalogueItemBean.getFieldName(IProductAlias.CATALOGUE_ITEM_CATALOGUE_ID), catalogueItem.getCatalogue().getId());
			if (catalogueItemBean.getCount(criteria) > 0) {
				throw new ControllerListenerException("El Producto ya esta definido para ese Catalogo.");
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

}