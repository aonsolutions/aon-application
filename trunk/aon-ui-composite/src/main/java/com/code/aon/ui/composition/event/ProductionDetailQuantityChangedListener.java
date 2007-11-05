package com.code.aon.ui.composition.event;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.composition.Production;
import com.code.aon.composition.ProductionDetail;
import com.code.aon.ui.composition.controller.ProductionController;
import com.code.aon.ui.composition.controller.ProductionDetailController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class ProductionDetailQuantityChangedListener extends ControllerAdapter {


    /**
     * After bean updated. Changes production quantities.
     * 
     * @param event
     * @throws ControllerListenerException
     */
    @Override
    public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
        ProductionDetailController controller = (ProductionDetailController)event.getController();
        double iniQuantity = ((ProductionDetail)controller.getTo()).getInitialQuantity();
        double quantity = ((ProductionDetail)controller.getTo()).getQuantity();
        double coefficient = (iniQuantity==0) ? 0 : quantity / iniQuantity;
        try {
            productionDetailQuantityChanged(coefficient);
        } catch (ManagerBeanException e) {
            throw new ControllerListenerException(e);
        }
    }

    /**
     * Changes production quantities.
     * 
     * @param coefficient
     */
    private void productionDetailQuantityChanged(double coefficient) throws ManagerBeanException {
        FacesContext ctx = FacesContext.getCurrentInstance();
        ValueBinding vb = ctx.getApplication().createValueBinding("#{production}");
        ProductionController controller = (ProductionController)vb.getValue(ctx);
        controller.changeProductionDetailQuantity(coefficient);

        double quantity = ((Production)controller.getTo()).getInitialQuantity();
        quantity = round(quantity * coefficient, 3);
        Production to = (Production)controller.getTo();
        to.setQuantity(quantity);
        controller.getManagerBean().update(to);
    }

    /**
     * Rounds the <code>value</code> using the <code>precision</code> passed as a parameter.
     * 
     * @param value
     * @param precision
     * 
     * @return double
     */
    private double round(double value, int precision) {
        double decimal = Math.pow(10, precision);
        return Math.round(decimal*value) / decimal;
    }

}
