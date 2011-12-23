package com.code.aon.ui.product.event;

import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Tax;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.controller.ConfigCollectionsController;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.product.controller.IItemConstants;
import com.code.aon.ui.product.controller.ItemTariffController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class ItemControllerListener extends ControllerAdapter implements IItemConstants {

    @Override
    public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
    	Item item = (Item)event.getController().getTo();
    	try {
            ConfigCollectionsController collections = (ConfigCollectionsController)AonUtil.getRegisteredBean(ConfigConstants.CONFIG_COLLECTIONS);
        	List<?> vats = collections.getVatTaxes();
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
				if (item.getStatus() == null) {
					item.setStatus(ProductStatus.ACTIVE);
				}
				Product product = item.getProduct();
				product.setStatus(item.getStatus());
				if (product.getBrand() != null && product.getBrand().getId() == null) {
					product.setBrand(null);
				}
				if (product.getVat() == null || product.getVat().getId() == null) {
					ConfigCollectionsController collections = (ConfigCollectionsController)AonUtil.getRegisteredBean(ConfigConstants.CONFIG_COLLECTIONS);
		        	List<?> vats = collections.getVatTaxes();
		        	if (vats.size() > 0) {
		        		Tax vat = (Tax)((SelectItem)vats.get(0)).getValue();
		        		item.getProduct().setVat(vat);
		        	}
				}
				product = (Product) productBean.insert(item.getProduct());
				item.setProduct(product);
			} catch (ManagerBeanException e) {
                throw new ControllerListenerException(e.getMessage(), e);
			}
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event)	throws ControllerListenerException {
		ItemTariffController itemTariffController = (ItemTariffController)AonUtil.getRegisteredBean(ITEM_TARIFF);
		itemTariffController.onSearch(null);
	}

	@Override
	public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		try {
			Item item = (Item)event.getController().getTo();
			IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_ID), item.getProduct().getId());
			if (itemBean.getCount(criteria) == 0) {
				IManagerBean productBean = BeanManager.getManagerBean(Product.class);
				productBean.remove(item.getProduct());
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

}