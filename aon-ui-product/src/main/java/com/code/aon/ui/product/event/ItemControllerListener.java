package com.code.aon.ui.product.event;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Tag;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.product.controller.IItemConstants;
import com.code.aon.ui.product.controller.ItemCompositionController;
import com.code.aon.ui.product.controller.ItemController;
import com.code.aon.ui.product.controller.ItemTariffController;
import com.code.aon.ui.product.controller.ProductController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class ItemControllerListener extends ControllerAdapter implements IItemConstants {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

    @Override
    public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
    	Item item = (Item)event.getController().getTo();
        item.setStatus(ProductStatus.ACTIVE);
        if (item.getProduct().getId() == null) {
        	item.getProduct().setType(ProductType.COMMERCIAL_PRODUCT);
            item.getProduct().setStatus(ProductStatus.ACTIVE);
        }
        try {
    		ProductController.updateVat(item.getProduct());
        } catch (ManagerBeanException e) {
            throw new ControllerListenerException(e.getMessage(), e);
        }
    }

    @Override
    public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		Item item = (Item)event.getController().getTo();
		try {
			IManagerBean productBean = BeanManager.getManagerBean(Product.class);
			productBean.initializePOJO(item.getProduct());
		} catch (ManagerBeanException e) {
            throw new ControllerListenerException(e.getMessage(), e);
		}
    }

    @Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Item item = (Item)event.getController().getTo();
		try {
			if (item.getStatus() == null) {
				item.setStatus(ProductStatus.ACTIVE);
			}
			ItemController.clearBarcode(item);
			ProductController.updateVat(item.getProduct());
			validateStockUnits(item);

			IManagerBean productBean = BeanManager.getManagerBean(Product.class);
			productBean.restoreNullSubPOJOs(item.getProduct());
		} catch (ManagerBeanException e) {
            throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		Item item = (Item)event.getController().getTo();
		try {
			if (item.getStatus() == null) {
				item.setStatus(ProductStatus.ACTIVE);
			}
			ItemController.clearBarcode(item);
			validateStockUnits(item);

			IManagerBean productBean = BeanManager.getManagerBean(Product.class);
			productBean.restoreNullSubPOJOs(item.getProduct());
		} catch (ManagerBeanException e) {
            throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event)	throws ControllerListenerException {
		ItemCompositionController itemCompositionController = (ItemCompositionController)AonUtil.getRegisteredBean(ITEM_COMPOSITION);
		itemCompositionController.setModel(null);
		itemCompositionController.onSearch(null);

		ItemTariffController itemTariffController = (ItemTariffController)AonUtil.getRegisteredBean(ITEM_TARIFF);
		itemTariffController.setModel(null);
		itemTariffController.onSearch(null);
	}

	@Override
	public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		Item item = (Item)event.getController().getTo();
		try {
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

	private void validateStockUnits(Item item) throws ControllerListenerException {
		Tag formatTag = item.getPackFormatTag();
		Tag unitsTag = item.getPackUnitsTag();
		Tag measurementTag = item.getPackMeasurementTag();
		Tag stockUnitTag = item.getStockUnitTag();
		if (item.getProduct().isPackaged() && !(stockUnitTag.equals(formatTag) || stockUnitTag.equals(unitsTag) || stockUnitTag.equals(measurementTag))) {
			throw new ControllerListenerException("La Ud.Stock tiene que ser " + formatTag.getName() + ", " + unitsTag.getName() + " o " + measurementTag.getName());
		}
	}

}