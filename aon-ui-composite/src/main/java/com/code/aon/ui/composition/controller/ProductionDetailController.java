package com.code.aon.ui.composition.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.composition.ProductionDetail;
import com.code.aon.ui.composition.util.ProductionPriceProvider;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;

/**
 * Controller for Product Production Details.
 * 
 * @author Consulting & Development.
 */
public class ProductionDetailController extends LinesController {

    /**
     * Object that provides the different prices that take part in the product production.
     */
    private ProductionPriceProvider provider;

    /**
     * Returns the price provider.
     * 
     * @return ProductionPriceProvider
     */
    private ProductionPriceProvider getPriceProvider() {
        if (provider == null) {
            provider = new ProductionPriceProvider();
        }
        return provider;
    }

    /**
     * Returns the quantity percentage by detail.
     * 
     * @return double
     * @throws ManagerBeanException
     */
    public double getListPercentage() throws ManagerBeanException {
        return (this.getModel().isRowAvailable()) ? getPriceProvider().getListPercentage((ProductionDetail)this.getModel().getRowData()) : 0;
    }

    /**
     * Returns the total price by detail.
     * 
     * @return double
     * @throws ManagerBeanException
     */
    public double getListTotal() throws ManagerBeanException {
        return (this.getModel().isRowAvailable()) ? getPriceProvider().getListTotal((ProductionDetail)this.getModel().getRowData()) : 0;
    }

    /**
     * On reset.
     * 
     * @param event
     * @see com.code.aon.ui.form.IController#onReset(javax.faces.event.ActionEvent)
     */
    @Override
    public void onReset(ActionEvent event) {
        ProductionExpenseController expenseController = (ProductionExpenseController)FormUtil.getController("productionExpense");
        expenseController.onCancel(event);

        super.onReset(event);
    }

    /**
     * On select.
     * 
     * @param event
     * @see com.code.aon.ui.form.IController#onSelect(javax.faces.event.ActionEvent)
     */
    @Override
    public void onSelect(ActionEvent event) {
        ProductionExpenseController expenseController = (ProductionExpenseController)FormUtil.getController("productionExpense");
        expenseController.onCancel(event);

        super.onSelect(event);
    }

}
