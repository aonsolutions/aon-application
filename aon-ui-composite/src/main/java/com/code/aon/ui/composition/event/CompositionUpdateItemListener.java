package com.code.aon.ui.composition.event;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.composition.Composition;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class CompositionUpdateItemListener extends ControllerAdapter {

    private static final Logger LOGGER = Logger.getLogger(CompositionUpdateItemListener.class.getName());

    /**
     * Before bean added. Inserts item.
     * 
     * @param event
     * @throws ControllerListenerException
     */
    @Override
    public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
        insertItem((Composition)event.getController().getTo());
    }

    /**
     * Before bean updated. Updates item.
     * 
     * @param event
     * @throws ControllerListenerException
     */
    @Override
    public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
        updateItem((Composition)event.getController().getTo());
    }

    /**
     * After bean removed. Removes item.
     * 
     * @param event
     * @throws ControllerListenerException
     */
    @Override
    public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
        removeItem((Composition)event.getController().getTo());
    }

    /**
     * Inserts item.
     * 
     * @param to
     * @throws ControllerListenerException
     */
    private void insertItem(Composition to) throws ControllerListenerException {
        IManagerBean bean;
        try {
            bean = BeanManager.getManagerBean(Product.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(bean.getFieldName(IProductAlias.PRODUCT_CODE), to.getItem().getProduct().getCode());
            Iterator iter = bean.getList(criteria).iterator();
            if(iter.hasNext()) {
                throw new ControllerListenerException("Duplicated Product Code!");
            } 
            Product product = new Product();
            product.setCode(to.getItem().getProduct().getCode());
            product.setStatus(ProductStatus.ACTIVE);
            product.setName(to.getItem().getProduct().getName());
            product.setCategory(to.getItem().getProduct().getCategory());
            product.setType(ProductType.COMMERCIAL_PRODUCT);
            product.setVat(to.getItem().getProduct().getVat());
            product.setInventoriable(false);
            product.setComposition(true);
            product = (Product)bean.insert(product);

            bean = BeanManager.getManagerBean(Item.class);
            Item item = new Item();
            item.setProduct(product);
            item.setStatus(ProductStatus.ACTIVE);
            item.setPrice(to.getPrice());
            item = (Item)bean.insert(item);

            to.setItem(item);
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error beforeBeanAdded in CompositionUpdateItemListener.", e);
            throw new ControllerListenerException(e.getMessage(), e);
        }
    }

    /**
     * Updates item.
     * 
     * @param to
     * @throws ControllerListenerException
     */
    private void updateItem(Composition to) throws ControllerListenerException {
        IManagerBean bean;
        try {
            bean = BeanManager.getManagerBean(Product.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(bean.getFieldName(IProductAlias.PRODUCT_ID), to.getItem().getProduct().getId());
            Iterator iterator = bean.getList(criteria).iterator();
            if (iterator.hasNext()) {
                Product product = (Product)iterator.next();
                product.setCode(to.getItem().getProduct().getCode());
                product.setName(to.getItem().getProduct().getName());
                product.setCategory(to.getItem().getProduct().getCategory());
                product.setVat(to.getItem().getProduct().getVat());
                bean.update(product);
            }

            bean = BeanManager.getManagerBean(Item.class);
            criteria = new Criteria();
            criteria.addEqualExpression(bean.getFieldName(IProductAlias.ITEM_ID), to.getItem().getId());
            iterator = bean.getList(criteria).iterator();
            if (iterator.hasNext()) {
                Item item = (Item)iterator.next();
                item.setPrice(to.getPrice());
                bean.update(item);
            }
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error beforeBeanUpdated in CompositionUpdateItemListener.", e);
            throw new ControllerListenerException(e.getMessage(), e);
        }
    }

    /**
     * Removes item.
     * 
     * @param to
     * @throws ControllerListenerException
     */
    private void removeItem(Composition to) throws ControllerListenerException {
        IManagerBean bean;
        try {
            bean = BeanManager.getManagerBean(Item.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(bean.getFieldName(IProductAlias.ITEM_ID), to.getItem().getId());
            Iterator iterator = bean.getList(criteria).iterator();
            if (iterator.hasNext()) {
                Item item = (Item)iterator.next();
                bean.remove(item);
            }

            bean = BeanManager.getManagerBean(Product.class);
            criteria = new Criteria();
            criteria.addEqualExpression(bean.getFieldName(IProductAlias.PRODUCT_ID), to.getItem().getProduct().getId());
            iterator = bean.getList(criteria).iterator();
            if (iterator.hasNext()) {
                Product product = (Product)iterator.next();
                bean.remove(product);
            }
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error afterBeanRemoved in CompositionUpdateItemListener.", e);
            throw new ControllerListenerException(e.getMessage(), e);
        }
    }

}
