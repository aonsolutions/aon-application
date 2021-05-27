package com.code.aon.ui.product.event;

import static com.code.aon.ui.common.ICommonMessages.PRODUCT_DEFINED_FOR_CATALOGUE_ERROR;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.CatalogueItem;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class CatalogueItemControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		CatalogueItem catalogueItem = (CatalogueItem)event.getController().getTo();
		validateCatalogueItem(catalogueItem);
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		CatalogueItem catalogueItem = (CatalogueItem)event.getController().getTo();
		validateCatalogueItem(catalogueItem);
	}

	private void validateCatalogueItem(CatalogueItem catalogueItem) throws ControllerListenerException {
		try {
			IManagerBean catalogueItembean = BeanManager.getManagerBean(CatalogueItem.class);
			Criteria criteria = new Criteria();
			if (catalogueItem.getId() != null) {
				criteria.addNotEqualExpression(catalogueItembean.getFieldName(IEntityAlias.CATALOGUE_ITEM_ID), catalogueItem.getId());
			}
			criteria.addEqualExpression(catalogueItembean.getFieldName(IEntityAlias.CATALOGUE_ITEM_CATALOGUE_ID), catalogueItem.getCatalogue().getId());
			criteria.addEqualExpression(catalogueItembean.getFieldName(IEntityAlias.CATALOGUE_ITEM_PRODUCT_ID), catalogueItem.getProduct().getId());
			if (catalogueItem.getItem() != null && catalogueItem.getItem().getId() != null) {
				criteria.addEqualExpression(catalogueItembean.getFieldName(IEntityAlias.CATALOGUE_ITEM_ITEM_ID), catalogueItem.getItem().getId());
			} else {
				criteria.addNullExpression(catalogueItembean.getFieldName(IEntityAlias.CATALOGUE_ITEM_ITEM));
			}
			criteria.addEqualExpression(catalogueItembean.getFieldName(IEntityAlias.CATALOGUE_ITEM_QUANTITY), catalogueItem.getQuantity());
			if (catalogueItembean.getCount(criteria) > 0) {
				throw new ControllerListenerException(AonUtil.getMessage(PRODUCT_DEFINED_FOR_CATALOGUE_ERROR));
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

}