package com.code.aon.ui.composition.event;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.composition.Composition;
import com.code.aon.composition.dao.ICompositionAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class CompositionUniqueItemListener extends ControllerAdapter {

    private static final Logger LOGGER = Logger.getLogger(CompositionUniqueItemListener.class.getName());

    /**
     * Before bean added. Checks if item is used in another composition and if so, throws an exception.
     * 
     * @param event
     * @throws ControllerListenerException
     */
    @Override
    public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
        Composition to = (Composition)event.getController().getTo();
        try {
            IManagerBean bean = BeanManager.getManagerBean(Composition.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(bean.getFieldName(ICompositionAlias.COMPOSITION_ITEM), to.getItem().getId());
            Iterator iter = bean.getList(criteria).iterator();
            if(iter.hasNext()) {
                throw new ControllerListenerException("The item is already used in another service.");
            }
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error beforeBeanAdded in CompositionUniqueItemListener.", e);
            throw new ControllerListenerException(e.getMessage(), e);
        }
    }

    /**
     * Before bean updated. Checks if item is used in another composition and if so, throws an exception.
     * 
     * @param event
     * @throws ControllerListenerException
     */
    @Override
    public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
        Composition to = (Composition)event.getController().getTo();
        try {
            IManagerBean bean = BeanManager.getManagerBean(Composition.class);
            Criteria criteria = new Criteria();
            Map<String, String> map = new HashMap<String, String>();
            map.put(bean.getFieldName(ICompositionAlias.COMPOSITION_ID), "!" + to.getId());
            criteria.addExpression(ExpressionUtilities.getExpression(map));
            criteria.addEqualExpression(bean.getFieldName(ICompositionAlias.COMPOSITION_ITEM), to.getItem().getId());
            Iterator iter = bean.getList(criteria).iterator();
            if(iter.hasNext()) {
                criteria = new Criteria();
                criteria.addEqualExpression(bean.getFieldName(ICompositionAlias.COMPOSITION_ID), to.getId());
                Iterator exists = bean.getList(criteria).iterator();
                if (exists.hasNext()) {
                    Composition composition = (Composition)exists.next();
                    ((Composition)event.getController().getTo()).setItem(composition.getItem());
                }
                throw new ControllerListenerException("The item is already used in another service.");
            }
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error beforeBeanUpdated in CompositionUniqueItemListener.", e);
            throw new ControllerListenerException(e.getMessage(), e);
        } catch (ExpressionException e) {
            LOGGER.log(Level.SEVERE, "Error beforeBeanUpdated in CompositionUniqueItemListener.", e);
            throw new ControllerListenerException(e.getMessage(), e);
        }
    }

}
