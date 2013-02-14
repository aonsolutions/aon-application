package com.code.aon.ui.product.event;

import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Catalogue;
import com.code.aon.product.CatalogueItem;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.product.IItemMessages;
import com.code.aon.ui.product.controller.IItemConstants;
import com.code.aon.ui.product.controller.ProductCollectionsController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class ItemCatalogueControllerListener extends ControllerAdapter implements IItemMessages, IItemConstants {

    @Override
    public void beforeBeanCreated(ControllerEvent event) throws ControllerListenerException {
		CatalogueItem catalogueItem = (CatalogueItem)event.getController().getTo();
    	try {
            ProductCollectionsController collections = (ProductCollectionsController)AonUtil.getRegisteredBean(PRODUCT_COLLECTIONS);
        	List<?> catalogues = collections.getCatalogues();
        	if (catalogues.size() > 0) {
        		Catalogue catalogue = (Catalogue)((SelectItem)catalogues.get(0)).getValue();
        		catalogueItem.setCatalogue(catalogue);
        	}
        } catch (ManagerBeanException e) {
            throw new ControllerListenerException(e.getMessage(), e);
        }
    }

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		CatalogueItem catalogueItem = (CatalogueItem)event.getController().getTo();
		try {
			IManagerBean bean = BeanManager.getManagerBean(CatalogueItem.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CATALOGUE_ITEM_ITEM_ID), catalogueItem.getItem().getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CATALOGUE_ITEM_CATALOGUE_ID), catalogueItem.getCatalogue().getId());
			if (bean.getCount(criteria) > 0) {
				throw new ControllerListenerException(AonUtil.getMessage(BUNDLE_NAME, PRODUCT_DEFINED_FOR_CATALOGUE_ERROR));
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

}