package com.code.aon.ui.product.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
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
import com.esferalia.aon.entity.IEntityAlias;

public class ProductControllerListener extends ControllerAdapter implements IItemConstants {

    @Override
    public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
    	ProductController controller = (ProductController) event.getController();
    	Product product = (Product) controller.getTo();
    	product.setStatus(ProductStatus.ACTIVE);
    	product.setType(ProductType.COMMERCIAL_PRODUCT);
    	product.setInventoriable(false);
    	product.setComposition(false);
    	try {
    		ProductController.updateVat(product);
    		controller.setItem( (Item) BeanManager.getManagerBean(Item.class).createNewTo() );
    		controller.getItem().setProduct(product);
        } catch (ManagerBeanException e) {
            throw new ControllerListenerException(e.getMessage(), e);
        }
    }
	
    @Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
    	ProductController controller = (ProductController) event.getController();
		try {
			Product product = (Product) controller.getTo();
			controller.getItem().setStatus(product.getStatus());
			IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
			itemBean.insert(controller.getItem());
			updateItems(product);
		} catch (ManagerBeanException e) {
            throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
    public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		try {
			Product product = (Product) event.getController().getTo();
			updateItems(product);
		} catch (ManagerBeanException e) {
            throw new ControllerListenerException(e.getMessage(), e);
		}
    }
    
	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		try {
			Product product = (Product) event.getController().getTo();
			IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_ID), product.getId());
			for( ITransferObject to : itemBean.getList(criteria) ) {
				itemBean.remove( to );
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}
    
	private void updateItems( Product product ) throws ManagerBeanException {
		ItemController itemController = (ItemController) AonUtil.getRegisteredBean(ITEM);
		itemController.clearCriteria();
		Criteria criteria = itemController.getCriteria();
		criteria.addEqualExpression(itemController.getFieldName(IEntityAlias.ITEM_PRODUCT_ID), product.getId());
		itemController.initializeModel();
		if (itemController.getModel().getRowCount() > 0) {
			itemController.getModel().setRowIndex(0);
			itemController.onSelect(null);
		} else {
			itemController.onReset(null);
		}					
		Item item = (Item) itemController.getTo();
		item.setProduct(product);
	}

}