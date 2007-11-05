package com.code.aon.ui.composition.controller;

import java.util.Iterator;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.composition.Composition;
import com.code.aon.composition.CompositionDetail;
import com.code.aon.product.Item;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.composition.util.CompositionPriceProvider;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;

/**
 * Controller for Product Composition Details.
 * 
 * @author Consulting & Development.
 */
public class CompositionDetailController extends LinesController {

    /**
     * Object that provides the different prices that take part in the product composition.
     */
    private CompositionPriceProvider provider;

    /**
     * Returns the price provider.
     * 
     * @return CompositionPriceProvider
     */
    private CompositionPriceProvider getPriceProvider() {
        if (provider == null) {
            provider = new CompositionPriceProvider();
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
        return (this.getModel().isRowAvailable()) ? getPriceProvider().getListPercentage((CompositionDetail)this.getModel().getRowData()) : 0;
    }

    /**
     * Returns the total price by detail.
     * 
     * @return double
     * @throws ManagerBeanException
     */
    public double getListTotal() throws ManagerBeanException {
        return (this.getModel().isRowAvailable()) ? getPriceProvider().getListTotal((CompositionDetail)this.getModel().getRowData()) : 0;
    }

    /**
     * Returns the total cost by detail.
     * 
     * @return double
     * @throws ManagerBeanException
     */
    public double getListCost() throws ManagerBeanException {
        return (this.getModel().isRowAvailable()) ? getPriceProvider().getListCost((CompositionDetail)this.getModel().getRowData()) : 0;
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
            criteria.addEqualExpression(itemBean.getFieldName(IProductAlias.ITEM_ID),event.getNewValue());
            Iterator iter = itemBean.getList(criteria).iterator();
            if(iter.hasNext()){
                Item item = (Item)iter.next();
                ((CompositionDetail)this.getTo()).setItem(item);
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

        CompositionController compositionController = (CompositionController)AonUtil.getController("composition");
        Composition composition = (Composition)compositionController.getTo();

        List list = (List)this.model.getWrappedData();
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            CompositionDetail detail = (CompositionDetail)iterator.next();
            Item item = detail.getItem();
            item.setPurchasePrice(getPriceProvider().obtainItemPurchasePrice(detail.getItem(), getPriceProvider().getListCost(detail)));
            item.setPrice(getPriceProvider().obtainItemTaxableBase(detail.getItem(), detail.getPrice()));
            item.setProfitPercent(getPriceProvider().getPurchaseProfit(composition));

            itemBean.update(item);
        }
    }

    /**
     * Reloads the item base price in preparations.
     * 
     * @param event
     * @throws ManagerBeanException
     */
    @SuppressWarnings("unused")
    public void reloadDetailPrices(ActionEvent event) throws ManagerBeanException {
        IManagerBean itemBean = BeanManager.getManagerBean(Item.class);

        List list = (List)this.model.getWrappedData();
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            CompositionDetail detail = (CompositionDetail)iterator.next();
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(itemBean.getFieldName(IProductAlias.ITEM_ID), detail.getItem().getId());
            Item item = (Item)itemBean.getList(criteria).get(0);

            detail.setPrice(getPriceProvider().obtainItemBasePrice(item, item.getPurchasePrice()));
            getManagerBean().update(detail);
        }
    }

    /**
     * On reset.
     * 
     * @param event
     * @see com.code.aon.ui.form.IController#onReset(javax.faces.event.ActionEvent)
     */
    @Override
    public void onReset(ActionEvent event) {
        CompositionExpenseController expenseController = (CompositionExpenseController)AonUtil.getController("compositionExpense");
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
        CompositionExpenseController expenseController = (CompositionExpenseController)AonUtil.getController("compositionExpense");
        expenseController.onCancel(event);

        super.onSelect(event);
    }

    /**
     * On accept.
     * 
     * @param event
     * @see com.code.aon.ui.form.IController#onAccept(javax.faces.event.ActionEvent)
     */
    @Override
    public void onAccept(ActionEvent event) {
        boolean wasNew = this.isNew();
        super.onAccept(event);
        if(wasNew){
            this.onReset(null);
        }
    }

}
