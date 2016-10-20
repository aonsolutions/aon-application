package com.code.aon.ui.product.event;

import static com.code.aon.ui.common.ICommonMessages.PRODUCT_NOT_ACTIVE_ITEMS_FOUND_WARNING;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Tag;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.product.ProductTag;
import com.code.aon.product.enumeration.ProductKind;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.product.controller.IItemConstants;
import com.code.aon.ui.product.controller.ItemController;
import com.code.aon.ui.product.controller.ProductController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Stock;
import com.esferalia.aon.entity.IEntityAlias;

public class ProductControllerListener extends ControllerAdapter implements IItemConstants {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
    public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
    	ProductController controller = (ProductController)event.getController();
    	Product product = (Product)controller.getTo();
    	product.setKind(ProductKind.SALE_PURCHASE);
    	product.setStatus(ProductStatus.ACTIVE);
    	product.setType(ProductType.COMMERCIAL_PRODUCT);
    	product.setInventoriable(false);
    	product.setSerializable(false);
    	product.setLotable(false);
    	product.setManufactured(false);
    	product.setComposition(false);
    	try {
    		ProductController.updateVat(product);
    		controller.setItem((Item)BeanManager.getManagerBean(Item.class).createNewTo());
    		controller.getItem().setProduct(product);
    		getSearch().setTags(null);
        } catch (ManagerBeanException e) {
            throw new ControllerListenerException(e.getMessage(), e);
        }
    }
	
	@Override
    public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
    	ProductController controller = (ProductController)event.getController();
		try {
			Product product = (Product)event.getController().getTo();
			controller.onSearchAllItem(null);
			if (controller.getItemController().getRowCount() == 0) {
				AonUtil.addWarningMessageFromBundle(PRODUCT_NOT_ACTIVE_ITEMS_FOUND_WARNING);
			}

			getSearch().setTags(getTagList(product));			
		} catch (ManagerBeanException e) {
            throw new ControllerListenerException(e.getMessage(), e);
		}
    }
    
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
    	ProductController controller = (ProductController)event.getController();
		validateStockUnits(controller.getItem());
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
    	ProductController controller = (ProductController)event.getController();
		try {
			Product product = (Product)controller.getTo();
			controller.getItem().setStatus(product.getStatus());
			ItemController.clearBarcode(controller.getItem());
			BeanManager.getManagerBean(Item.class).restoreNullSubPOJOs(controller.getItem());
			BeanManager.getManagerBean(Item.class).insert(controller.getItem());
			controller.onSearchItem(null);

			updateTagList(product, getSearch().getTags(), true);
		} catch (ManagerBeanException e) {
            throw new ControllerListenerException(e.getMessage(), e);
		}
	}
    
	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		try {
	    	ProductController controller = (ProductController)event.getController();
	    	Product product = (Product)controller.getTo();
			controller.onAcceptItem(null);

			if (!product.isInventoriable()) {
				removeStocks(product);
			}
			updateTagList(product, getSearch().getTags(), false);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}    

	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		try {
			Product product = (Product) event.getController().getTo();
			removeStocks(product);			
			removeItems(product);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	public ProductSearchListener getSearch() {
		return (ProductSearchListener) AonUtil.getRegisteredBean(PRODUCT_SEARCH_CONTROLLER_NAME);
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<ProductTag> getProductTags(Product product) throws ManagerBeanException {
		IManagerBean productTagBean = BeanManager.getManagerBean(ProductTag.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(productTagBean.getFieldName(IEntityAlias.PRODUCT_TAG_PRODUCT_ID), product.getId());
		criteria.addOrder(productTagBean.getFieldName(IEntityAlias.PRODUCT_TAG_TAG_NAME));
		return (List)productTagBean.getList(criteria);
	}	
	
	private Tag[] getTagList(Product product) throws ManagerBeanException {
		List<Tag> list = new LinkedList<Tag>();
		for (ProductTag pt : getProductTags(product)) {
			if (! list.contains(pt.getTag())) {
				list.add(pt.getTag());
			}
		}
		return list.toArray(new Tag[list.size()]);
	}	

	private void updateTagList(Product product, Tag[] tags, boolean _new) throws ManagerBeanException {
		List<Tag> _tags = new LinkedList<Tag>(Arrays.asList(tags));
		IManagerBean bean = BeanManager.getManagerBean(ProductTag.class);
		if (! _new) {
			for (ProductTag pt : getProductTags(product)) {
				if (_tags.contains(pt.getTag())) {
					_tags.remove(pt.getTag());
				} else {
					bean.remove(pt);
				}
			}
		}
		if (! _tags.isEmpty()) {
			for (Tag tag : _tags) {
				if ((tag != null) && (tag.getId() != null)) {
					ProductTag pt = new ProductTag();
					pt.setProduct(product);
					pt.setTag(tag);
					bean.insert(pt);
				}
			}
		}
	}		

	private void removeStocks(Product product) throws ManagerBeanException {
		IManagerBean stockBean = BeanManager.getManagerBean(Stock.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(stockBean.getFieldName(IEntityAlias.STOCK_ITEM_PRODUCT_ID), product.getId());
		for (ITransferObject ito : stockBean.getList(criteria)) {
			stockBean.remove(ito);
		}		
	}
	
	private void removeItems(Product product) throws ManagerBeanException {
		IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_ID), product.getId());
		for (ITransferObject ito : itemBean.getList(criteria)) {
			itemBean.remove(ito);
		}		
	}

}