package com.code.aon.ui.product.event;

import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Tax;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.controller.ConfigCollectionsController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class ItemControllerListener extends ControllerAdapter {

	private static final String CONFIG_COLLECTIONS_CONTROLLER = "configCollections";

    @Override
    public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
        IController controller = event.getController();
        try {
            controller.getCriteria().addEqualExpression(controller.getFieldName(IProductAlias.ITEM_PRODUCT_COMPOSITION), new Boolean(false));
        } catch (ManagerBeanException e) {
            throw new ControllerListenerException(e.getMessage(), e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
    	Item item = (Item)event.getController().getTo();
    	try {
            ConfigCollectionsController collections = (ConfigCollectionsController)AonUtil.getRegisteredBean(CONFIG_COLLECTIONS_CONTROLLER);
        	List vats = collections.getVatTaxes();
        	if (vats.size() > 0) {
        		Tax vat = (Tax)((SelectItem)vats.get(0)).getValue();
        		item.getProduct().setVat(vat);
        	}
        } catch (ManagerBeanException e) {
            throw new ControllerListenerException(e.getMessage(), e);
        }
        item.setStatus(ProductStatus.ACTIVE);
        item.getProduct().setType(ProductType.COMMERCIAL_PRODUCT);
        item.getProduct().setInventoriable(false);
        item.getProduct().setComposition(false);
    }

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		if (event.getController().isNew()) {
			try {
				IManagerBean productBean = BeanManager.getManagerBean(Product.class);
				Item item = (Item)event.getController().getTo();
				Product product = item.getProduct();
				product.setStatus(item.getStatus());
				if (product.getBrand() != null && product.getBrand().getId() == null) {
					product.setBrand(null);
				}
                product = (Product) productBean.insert(item.getProduct());
				item.setProduct(product);
			} catch (ManagerBeanException e) {
                throw new ControllerListenerException(e.getMessage(), e);
			}
		}
	}

    @Override
	public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		try {
			Item item = (Item)event.getController().getTo();
			IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(itemBean.getFieldName(IProductAlias.ITEM_PRODUCT_ID), item.getProduct().getId());
			if (itemBean.getCount(criteria) == 0) {
				IManagerBean productBean = BeanManager.getManagerBean(Product.class);
				productBean.remove(item.getProduct());
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

}