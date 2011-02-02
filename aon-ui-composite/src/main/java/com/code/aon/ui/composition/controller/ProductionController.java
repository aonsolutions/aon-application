package com.code.aon.ui.composition.controller;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.composition.Composition;
import com.code.aon.composition.CompositionDetail;
import com.code.aon.composition.CompositionExpense;
import com.code.aon.composition.Production;
import com.code.aon.composition.ProductionDetail;
import com.code.aon.composition.ProductionExpense;
import com.code.aon.composition.dao.ICompositionAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.composition.util.ProductionPriceProvider;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;

/**
 * Controller for Product Productions.
 * 
 * @author Consulting & Development.
 */
public class ProductionController extends BasicController {

    /** The LOGGER. */
    private final static Logger LOGGER = Logger.getLogger(ProductionController.class.getName()); 

	/**
     * Object that provides the different prices that take part in the product production.
     */
    private ProductionPriceProvider provider;

    /**
     * Returns the price provider.
     * 
     * @return ProductionPriceProvider
     */
    public ProductionPriceProvider getPriceProvider() {
        if (provider == null) {
            provider = new ProductionPriceProvider();
        }
        return provider;
    }

    /**
     * Returns the total of expenses of the production. 
     * 
     * @return double
     */
    public double getTotalExpenses() {
        return (this.getTo() != null) ? getPriceProvider().getTotalExpenses((Production)this.getTo()) : 0;
    }

    /**
     * Returns the total of details of the production. 
     * 
     * @return double
     */
    public double getTotalDetails() {
        return (this.getTo() != null) ? getPriceProvider().getTotalDetails((Production)this.getTo()) : 0;
    }

    /**
     * Returns the total of purchase of the production. 
     * 
     * @return double
     */
    public double getTotalPurchase() {
        return (this.getTo() != null) ? getPriceProvider().getTotalPurchase((Production)this.getTo()) : 0;
    }

    /**
     * Returns the total of sale of the production. 
     * 
     * @return double
     */
    public double getTotalSale() {
        return (this.getTo() != null) ? getPriceProvider().getTotalSale((Production)this.getTo()) : 0;
    }

    /**
     * Returns the profit of the production. 
     * 
     * @return double
     */
    public double getProfit()  {
        return (this.getTo() != null) ? getPriceProvider().getProfit((Production)this.getTo()) : 0;
    }

    /**
     * Returns the purchase profit of the production. 
     * 
     * @return double
     */
    public double getPurchaseProfit() {
        return (this.getTo() != null) ? getPriceProvider().getPurchaseProfit((Production)this.getTo()) : 0;
    }

    /**
     * Returns the sale profit of the production. 
     * 
     * @return double
     */
    public double getSaleProfit() {
        return (this.getTo() != null) ? getPriceProvider().getSaleProfit((Production)this.getTo()) : 0;
    }

    /**
     * Returns the sale quantity of the production. 
     * 
     * @return double
     */
    public double getSaleQuantity() {
        return (this.getTo() != null) ? getPriceProvider().getSaleQuantity((Production)this.getTo()) : 0;
    }

    /**
     * Returns the sale percentage of the production. 
     * 
     * @return double
     */
    public double getSalePercentage()  {
        return (this.getTo() != null) ? getPriceProvider().getSalePercentage((Production)this.getTo()) : 0;
    }

    /**
     * Gets the collection.
     * 
     * @return Collection
     * @see com.code.aon.ui.form.BasicController#getCollection()
     */
    public Collection getCollection() {
        List<ITransferObject> list = new LinkedList<ITransferObject>();
        list.add(this.getTo());
        return list;
    }

    /**
     * On reset.
     * 
     * @param event
     * @see com.code.aon.ui.form.IController#onReset(javax.faces.event.ActionEvent)
     */
    @Override
    public void onReset(ActionEvent event) {
        ProductionDetailController detailController = (ProductionDetailController)FormUtil.getController("productionDetail");
        detailController.onCancel(event);

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
        ProductionDetailController detailController = (ProductionDetailController)FormUtil.getController("productionDetail");
        detailController.onCancel(event);

        ProductionExpenseController expenseController = (ProductionExpenseController)FormUtil.getController("productionExpense");
        expenseController.onCancel(event);

        super.onSelect(event);
    }

    /**
     * Adds production date greater than expression.
     * 
     * @param event
     * @throws ManagerBeanException
     */
    public void addProductionDateGreaterThanExpression(ValueChangeEvent event) throws ManagerBeanException {
        if (event.getNewValue() != null) {
            Criteria criteria = getCriteria();
            Object value = event.getNewValue();
            criteria.addGreaterThanOrEqualExpression(getFieldName(ICompositionAlias.PRODUCTION_PRODUCTION_DATE), value);
            setCriteria(criteria);
        }
    }

    /**
     * Adds production date less than expression.
     * 
     * @param event
     * @throws ManagerBeanException
     */
    public void addProductionDateLessThanExpression(ValueChangeEvent event) throws ManagerBeanException {
        if (event.getNewValue() != null) {
            Criteria criteria = getCriteria();
            Object value = event.getNewValue();
            criteria.addLessThanOrEqualExpression(getFieldName(ICompositionAlias.PRODUCTION_PRODUCTION_DATE), value);
            setCriteria(criteria);
        }
    }

    /**
     * Action of elaboration. This means that a product production is going to be generated from a composition of type 
     * elaboration, that is going to act as a preparation or point to begin with. 
     * 
     * @param event
     */
    public void elaborate(ActionEvent event) {
        try {
            this.onReset(event);
            this.setTo(getProduction());
            this.accept(event);

            insertProductionDetail();
            insertProductionExpense();
            this.accept(event);
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error creating production.", e);
        }
    }

    /**
     * Returns a <code>Production</code>, generated from current composition.
     * 
     * @return Production
     */
    private Production getProduction() {
        CompositionController controller = (CompositionController)FormUtil.getController("composition");
        Composition composition = (Composition)controller.getTo();

        Production production = new Production();
        production.setDescription(composition.getDescription());
        production.setItem(composition.getItem());
        production.setInitialQuantity(composition.getQuantity());
        production.setQuantity(composition.getQuantity());
        production.setPrice(composition.getPrice());
        return production;
    }

    /**
     * Inserts <code>ProductionDetail</code>, extracted from current composition.
     * 
     * @throws ManagerBeanException
     */
    private void insertProductionDetail() throws ManagerBeanException {
        IManagerBean detailsBean = BeanManager.getManagerBean(ProductionDetail.class);

        CompositionController controller = (CompositionController)FormUtil.getController("composition");
        Composition composition = (Composition)controller.getTo();

        Iterator iterator = composition.getDetailList().iterator();
        while (iterator.hasNext()) {
            CompositionDetail compositionDetail = (CompositionDetail)iterator.next();

            ProductionDetail detail = new ProductionDetail();
            detail.setProduction((Production)this.getTo());
            detail.setItem(compositionDetail.getItem());
            detail.setDescription(compositionDetail.getDescription());
            detail.setInitialQuantity(compositionDetail.getQuantity());
            detail.setQuantity(compositionDetail.getQuantity());
            detail.setPrice(compositionDetail.getPrice());

            detailsBean.insert(detail);
        }
    }

    /**
     * Inserts a <code>ProductionExpense</code>, extracted from current composition.
     * 
     * @throws ManagerBeanException
     */
    private void insertProductionExpense() throws ManagerBeanException {
        IManagerBean expenseBean = BeanManager.getManagerBean(ProductionExpense.class);

        CompositionController controller = (CompositionController)FormUtil.getController("composition");
        Composition composition = (Composition)controller.getTo();

        Iterator iterator = composition.getExpensesList().iterator();
        while (iterator.hasNext()) {
            CompositionExpense compositionExpense = (CompositionExpense)iterator.next();

            ProductionExpense expense = new ProductionExpense();
            expense.setProduction((Production)this.getTo());
            expense.setDescription(compositionExpense.getDescription());
            expense.setQuantity(compositionExpense.getQuantity());
            expense.setPrice(compositionExpense.getPrice());

            expenseBean.insert(expense);
        }
    }

    /**
     * Executed when quantity of the initial preparation is changed.
     * 
     * @param event
     */
    public void productionQuantityChanged(ValueChangeEvent event) {
        double iniQuantity = ((Production)this.getTo()).getInitialQuantity();
        double quantity = ((Double)event.getNewValue()).doubleValue();
        double coefficient = (iniQuantity==0) ? 0 : quantity / iniQuantity;
        changeProductionDetailQuantity(coefficient);
    }


    /**
     * Changes the quantity in the details of the production based on the initial preparation.
     * 
     * @param coefficient
     */
    public void changeProductionDetailQuantity(double coefficient) {
        IManagerBean detailsBean = null;
        try {
            detailsBean = BeanManager.getManagerBean(ProductionDetail.class);
            Iterator iterator = ((Production)this.getTo()).getDetailList().iterator();
            while (iterator.hasNext()) {
                ProductionDetail detail = (ProductionDetail)iterator.next();
                double quantity = provider.round(detail.getInitialQuantity() * coefficient, 3);
                detail.setQuantity(quantity);
                detailsBean.update(detail);
            }
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error updating production detail.", e);
        }
    }

}
