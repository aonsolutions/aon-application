package com.code.aon.ui.composition.controller;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.composition.Composition;
import com.code.aon.composition.dao.ICompositionAlias;
import com.code.aon.product.Item;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.composition.util.CompositionPriceProvider;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;

/**
 * Controller for Product Compositions.
 * 
 * @author Consulting & Development.
 */
public class CompositionController extends BasicController {
    
    /**
     * Object that provides the different prices that take part in the product composition.
     */
    private CompositionPriceProvider provider;

    /**
     * The type of composition. Possible values: 0 means quartering and 1 means elaboration.
     */
    private Short type;

    /**
     * Returns the price provider.
     * 
     * @return CompositionPriceProvider
     */
    public CompositionPriceProvider getPriceProvider() {
        if (provider == null) {
            provider = new CompositionPriceProvider();
        }
        return provider;
    }

    /**
     * Sets the current composition as a quartering.
     * 
     * @param event
     * @throws ManagerBeanException
     */
    @SuppressWarnings("unused")
    public void setQuarteringMode(ActionEvent event) throws ManagerBeanException {
        type = Short.valueOf((short)0);
        clearCriteria();
        initializeModel();
    }

    /**
     * Sets the current composition as an elaboration.
     * 
     * @param event
     * @throws ManagerBeanException
     */
    @SuppressWarnings("unused")
    public void setManufactureMode(ActionEvent event) throws ManagerBeanException {
        type = Short.valueOf((short)1);
        clearCriteria();
        initializeModel();
    }

    /**
     * Returns the type of composition.
     * 
     * @return Short
     */
    public Short getType() {
        return type;
    }

    /**
     * Returns if the company has surcharge.
     * 
     * @return boolean
     */
    public boolean isSurcharge() {
        return getPriceProvider().isSurcharge();
    }

    /**
     * Returns the tax percentage of the composition item. 
     * 
     * @return double
     */
    public double getTaxPercent() {
        return (this.getTo() != null) ? getPriceProvider().getTaxPercent(((Composition)this.getTo()).getItem()) : 0;
    }

    /**
     * Returns the surcharge percentage of the composition item. 
     * 
     * @return double
     */
    public double getSurchargePercent() {
        return (this.getTo() != null) ? getPriceProvider().getSurchargePercent(((Composition)this.getTo()).getItem()) : 0;
    }

    /**
     * Returns the total of expenses of the composition. 
     * 
     * @return double
     */
    public double getTotalExpenses() {
        return (this.getTo() != null) ? getPriceProvider().getTotalExpenses((Composition)this.getTo()) : 0;
    }

    /**
     * Returns the total of details of the composition. 
     * 
     * @return double
     */
    public double getTotalDetails() {
        return (this.getTo() != null) ? getPriceProvider().getTotalDetails((Composition)this.getTo()) : 0;
    }

    /**
     * Returns the total of purchase of the composition. 
     * 
     * @return double
     */
    public double getTotalPurchase() {
        return (this.getTo() != null) ? getPriceProvider().getTotalPurchase((Composition)this.getTo()) : 0;
    }

    /**
     * Returns the total of sale of the composition. 
     * 
     * @return double
     */
    public double getTotalSale()  {
        return (this.getTo() != null) ? getPriceProvider().getTotalSale((Composition)this.getTo()) : 0;
    }

    /**
     * Returns the profit of the composition. 
     * 
     * @return double
     */
    public double getProfit() {
        return (this.getTo() != null) ? getPriceProvider().getProfit((Composition)this.getTo()) : 0;
    }

    /**
     * Returns the purchase profit of the composition. 
     * 
     * @return double
     */
    public double getPurchaseProfit() {
        return (this.getTo() != null) ? getPriceProvider().getPurchaseProfit((Composition)this.getTo()) : 0;
    }

    /**
     * Returns the sale profit of the composition. 
     * 
     * @return double
     */
    public double getSaleProfit() {
        return (this.getTo() != null) ? getPriceProvider().getSaleProfit((Composition)this.getTo()) : 0;
    }

    /**
     * Returns the sale quantity of the composition. 
     * 
     * @return double
     */
    public double getSaleQuantity() {
        return (this.getTo() != null) ? getPriceProvider().getSaleQuantity((Composition)this.getTo()) : 0;
    }

    /**
     * Returns the sale percentage of the composition. 
     * 
     * @return double
     */
    public double getSalePercentage() {
        return (this.getTo() != null) ? getPriceProvider().getSalePercentage((Composition)this.getTo()) : 0;
    }

    /**
     * Retrieves the whole <code>Item</code> object when the lookup field changes.
     * 
     * @param event
     * @throws ManagerBeanException
     */
    public void itemData(ValueChangeEvent event) throws ManagerBeanException {
        if(event.getNewValue() != null){
            IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(itemBean.getFieldName(IProductAlias.ITEM_ID), event.getNewValue());
            Iterator iter = itemBean.getList(criteria).iterator();
            if(iter.hasNext()){
                Item item = (Item)iter.next();
                ((Composition)this.getTo()).setItem(item);
            }
        }
    }

    /**
     * Updates the item purchase price, sale price and profit percent, all obtained from composition.
     * 
     * @param event
     * @throws ManagerBeanException
     */
    @SuppressWarnings("unused")
    public void updateItemPrices(ActionEvent event) throws ManagerBeanException {
        IManagerBean itemBean = BeanManager.getManagerBean(Item.class);

        Composition composition = (Composition)this.getTo();
        Item item = composition.getItem();
        double basePrice = getPriceProvider().getUnitTotalPurchase(composition);
        item.setPurchasePrice(getPriceProvider().obtainItemPurchasePrice(composition.getItem(), basePrice));
        item.setPrice(getPriceProvider().obtainItemTaxableBase(composition.getItem(), composition.getPrice()));
        item.setProfitPercent(getPriceProvider().getPurchaseProfit(composition));

        itemBean.update(item);
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
        CompositionDetailController detailController = (CompositionDetailController)FormUtil.getController("compositionDetail");
        detailController.onCancel(event);

        CompositionExpenseController expenseController = (CompositionExpenseController)FormUtil.getController("compositionExpense");
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
        CompositionDetailController detailController = (CompositionDetailController)FormUtil.getController("compositionDetail");
        detailController.onCancel(event);

        CompositionExpenseController expenseController = (CompositionExpenseController)FormUtil.getController("compositionExpense");
        expenseController.onCancel(event);

        super.onSelect(event);
    }

    /**
     * Adds product code expression.
     * 
     * @param event
     * @throws ManagerBeanException
     * @throws ExpressionException
     */
    public void addProductCodeExpression(ValueChangeEvent event) throws ManagerBeanException, ExpressionException {
        addProductExpression(event, IProductAlias.ITEM_PRODUCT_CODE);
    }

    /**
     * Adds product name expression.
     * 
     * @param event
     * @throws ManagerBeanException
     * @throws ExpressionException
     */
    public void addProductNameExpression(ValueChangeEvent event) throws ManagerBeanException, ExpressionException {
        addProductExpression(event, IProductAlias.ITEM_PRODUCT_NAME);
    }

    /**
     * Adds product expression to criteria.
     * 
     * @param event
     * @param alias
     * @throws ManagerBeanException
     * @throws ExpressionException
     */
    private void addProductExpression(ValueChangeEvent event, String alias) throws ManagerBeanException, ExpressionException {
        Criteria criteria = getCriteria();
        if (event.getNewValue() != null && !"".equals(event.getNewValue().toString().trim())) {
            Criteria filter = new Criteria();
            IManagerBean bean = BeanManager.getManagerBean(Item.class);
            filter.addExpression(bean.getFieldName(alias), event.getNewValue().toString());
            Iterator iterator = bean.getList(filter).iterator();
            if (iterator.hasNext()) {
                String values = null;
                while (iterator.hasNext()) {
                    if (values == null){
                        values = "";
                    } else{
                        values += "|";
                    }
                    Item item = (Item)iterator.next();
                    values += item.getId();
                }
                criteria.addExpression(getFieldName(ICompositionAlias.COMPOSITION_ITEM), values);
                setCriteria(criteria);
            } else{
                Object value = new Integer(-1); 
                criteria.addEqualExpression(getFieldName(ICompositionAlias.COMPOSITION_ITEM), value);
                setCriteria(criteria);
            }
        }
    }

}
