package com.code.aon.ui.composition.event;

import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.composition.Composition;
import com.code.aon.composition.dao.ICompositionAlias;
import com.code.aon.product.Tax;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.composition.controller.CompositionController;
import com.code.aon.ui.composition.controller.CompositionDetailController;
import com.code.aon.ui.composition.controller.CompositionExpenseController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.product.controller.ProductCollectionsController;

public class CompositionControllerListener extends ControllerAdapter {

    /**
     * After bean created. Sets a default item tax percentage from the composition.
     * 
     * @param event
     * @throws ControllerListenerException
     */
    @Override
    public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
        double percentage = 0;

        FacesContext ctx = FacesContext.getCurrentInstance();
        ValueBinding vb = ctx.getApplication().createValueBinding("#{productCollections}");
        ProductCollectionsController collections = (ProductCollectionsController)vb.getValue(ctx);
        IManagerBean bean;
        try {
            List vats = collections.getVatTaxes();
            int vatId = ((Integer)((SelectItem)vats.get(0)).getValue()).intValue();

            bean = BeanManager.getManagerBean(Tax.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(bean.getFieldName(IProductAlias.TAX_ID), new Integer(vatId));
            percentage = ((Tax)bean.getList(criteria).get(0)).getPercentage();
        } catch (ManagerBeanException e) {
            throw new ControllerListenerException(e.getMessage(), e);
        }

        ((Composition)event.getController().getTo()).getItem().getProduct().getVat().setPercentage(percentage);
    }

    /**
     * Before bean added. If quantity is 0, it is changed to 1.
     * 
     * @param event
     * @throws ControllerListenerException
     */
    @Override
    public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
        CompositionController controller = (CompositionController)event.getController();
        ((Composition)controller.getTo()).setType(controller.getType());
        if (((Composition)controller.getTo()).getQuantity() == 0) {
            ((Composition)controller.getTo()).setQuantity(1);
        }
    }

    /**
     * After bean canceled. Cancels the compositionExpenseController and the compositionDetailController.
     * 
     * @param event
     * @throws ControllerListenerException
     */
    @Override
    public void afterBeanCanceled(ControllerEvent event) throws ControllerListenerException {
        FacesContext ctx = FacesContext.getCurrentInstance();
        ValueBinding vb = ctx.getApplication().createValueBinding("#{compositionExpense}");
        CompositionExpenseController expenseController = (CompositionExpenseController)vb.getValue(ctx);
        expenseController.onCancel(null);

        vb = ctx.getApplication().createValueBinding("#{compositionDetail}");
        CompositionDetailController detailController = (CompositionDetailController)vb.getValue(ctx);
        detailController.onCancel(null);
    }

    /**
     * Before bean initialized. Adds the type of composition to criteria.
     * 
     * @param event
     * @throws ControllerListenerException
     */
    @Override
    public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
        CompositionController controller = (CompositionController)event.getController();
        Short type = controller.getType();
        Criteria criteria;
        try {
            criteria = controller.getCriteria();
            criteria.addEqualExpression(controller.getManagerBean().getFieldName(ICompositionAlias.COMPOSITION_TYPE), type);
            controller.setCriteria(criteria);
        } catch (ManagerBeanException e) {
            throw new ControllerListenerException(e.getMessage(), e);
        }
    }

}
