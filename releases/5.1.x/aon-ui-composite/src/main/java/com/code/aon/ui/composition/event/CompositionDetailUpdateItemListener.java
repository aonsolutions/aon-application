package com.code.aon.ui.composition.event;

import java.util.Iterator;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.composition.CompositionDetail;
import com.code.aon.product.Item;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class CompositionDetailUpdateItemListener extends ControllerAdapter {

    /**
     * After bean added. Updates item prices.
     * 
     * @param event
     * @throws ControllerListenerException
     */
    @Override
    public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
        updateItem((CompositionDetail)event.getController().getTo());
    }

    /**
     * After bean updated. Updates item prices.
     * 
     * @param event
     * @throws ControllerListenerException
     */
    @Override
    public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
        updateItem((CompositionDetail)event.getController().getTo());
    }

    /**
     * Updates item prices.
     * 
     * @param to
     * @throws ControllerListenerException
     */
    private void updateItem(CompositionDetail to) throws ControllerListenerException {
        IManagerBean bean;
        try {
            bean = BeanManager.getManagerBean(Item.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(bean.getFieldName(IProductAlias.ITEM_ID), to.getItem().getId());
            Iterator iterator = bean.getList(criteria).iterator();
            if (iterator.hasNext()) {
                Item item = (Item)iterator.next();
                item.setPrice(to.getPrice());
                bean.update(item);
            }
        } catch (ManagerBeanException e) {
            throw new ControllerListenerException(e.getMessage(), e);
        }
    }

}
