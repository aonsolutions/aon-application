package com.code.aon.ui.composition.event;

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
import com.code.aon.ui.util.AonUtil;

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
        ProductionExpenseController expenseController = (ProductionExpenseController)AonUtil.getController("productionExpense");
        expenseController.onCancel(null);

        ProductionDetailController detailController = (ProductionDetailController)AonUtil.getController("productionDetail");
        detailController.onCancel(null);
    }

    /**
     * Reloads details.
     * 
     * @param to
     * @throws ControllerListenerException
     */
    private void reloadDetail(Production to) throws ControllerListenerException {
        ProductionExpenseController expenseController = (ProductionExpenseController)AonUtil.getController("productionExpense");
        ProductionDetailController detailController = (ProductionDetailController)AonUtil.getController("productionDetail");

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
