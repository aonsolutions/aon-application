package com.code.aon.ui.composition.event;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.composition.Production;
import com.code.aon.composition.ProductionDetail;
import com.code.aon.composition.ProductionExpense;
import com.code.aon.composition.dao.ICompositionAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.composition.controller.ProductionController;
import com.code.aon.ui.composition.controller.ProductionDetailController;
import com.code.aon.ui.composition.controller.ProductionExpenseController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class ProductionControllerListener extends ControllerAdapter {

    /**
     * After bean added. Reloads details.
     * 
     * @param event
     * @throws ControllerListenerException
     */
    @Override
    public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
        ProductionController controller = (ProductionController)event.getController();
        reloadDetail((Production)controller.getTo());
    }

    /**
     * After bean updated. Reloads details.
     * 
     * @param event
     * @throws ControllerListenerException
     */
    @Override
    public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
        ProductionController controller = (ProductionController)event.getController();
        reloadDetail((Production)controller.getTo());
    }

    /**
     * After bean canceled. Cancels the productionExpenseController and the productionDetailController.
     * 
     * @param event
     * @throws ControllerListenerException
     */
    @Override
    public void afterBeanCanceled(ControllerEvent event) throws ControllerListenerException {
        FacesContext ctx = FacesContext.getCurrentInstance();
        ValueBinding vb = ctx.getApplication().createValueBinding("#{productionExpense}");
        ProductionExpenseController expenseController = (ProductionExpenseController)vb.getValue(ctx);
        expenseController.onCancel(null);

        vb = ctx.getApplication().createValueBinding("#{productionDetail}");
        ProductionDetailController detailController = (ProductionDetailController)vb.getValue(ctx);
        detailController.onCancel(null);
    }

    /**
     * Reloads details.
     * 
     * @param to
     * @throws ControllerListenerException
     */
    private void reloadDetail(Production to) throws ControllerListenerException {
        FacesContext ctx = FacesContext.getCurrentInstance();
        ValueBinding vb = ctx.getApplication().createValueBinding("#{productionDetail}");
        ProductionDetailController detailController = (ProductionDetailController)vb.getValue(ctx);
        
        ctx = FacesContext.getCurrentInstance();
        vb = ctx.getApplication().createValueBinding("#{productionExpense}");
        ProductionExpenseController expenseController = (ProductionExpenseController)vb.getValue(ctx);

        IManagerBean bean;
        try {
            bean = BeanManager.getManagerBean(ProductionDetail.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(bean.getFieldName(ICompositionAlias.PRODUCTION_DETAIL_PRODUCTION_ID), to.getId());

            detailController.setCriteria(criteria);
            detailController.onSearch(null);

            bean = BeanManager.getManagerBean(ProductionExpense.class);
            criteria = new Criteria();
            criteria.addEqualExpression(bean.getFieldName(ICompositionAlias.PRODUCTION_EXPENSE_PRODUCTION_ID), to.getId());

            expenseController.setCriteria(criteria);
            expenseController.onSearch(null);
        } catch (ManagerBeanException e) {
            throw new ControllerListenerException(e.getMessage(), e);
        }
    }

}
