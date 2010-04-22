package com.code.aon.ui.composition.util;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.composition.Composition;
import com.code.aon.composition.CompositionDetail;
import com.code.aon.composition.CompositionExpense;
import com.code.aon.composition.dao.ICompositionAlias;
import com.code.aon.config.Tax;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.product.util.ItemCompanyTaxProvider;

/**
 * Class used to calculate prices of composition.
 * 
 * @author Consulting & Development.
 */
public class CompositionPriceProvider {

    /** The tax provider. */
    private ItemCompanyTaxProvider taxProvider = new ItemCompanyTaxProvider();

    /**
     * Returns if the company has surcharge.
     * 
     * @return boolean
     */
    public boolean isSurcharge() {
        return taxProvider.isSurcharge();
    }

    /**
     * Returns the tax percentage of the composition item. 
     * 
     * @return double
     */
    public double getTaxPercent(Item item) {
        double percent = 0;
        List taxList = taxProvider.getTaxList(item);
        if (taxList != null) {
            Iterator iterator = taxList.iterator();
            while (iterator.hasNext()) {
                Tax tax = (Tax)iterator.next();
                percent += tax.getPercentage();
            }
        }
        return round(percent, PRICE_PRECISION);       
    }

    /**
     * Returns the surcharge percentage of the composition item. 
     * 
     * @return double
     */
    public double getSurchargePercent(Item item) {
        double percent = 0;
        if(isSurcharge()){
            List taxList = taxProvider.getTaxList(item);
            if (taxList != null) {
                Iterator iterator = taxList.iterator();
                while (iterator.hasNext()) {
                    Tax tax = (Tax)iterator.next();
                    percent += tax.getSurcharge();
                }
            }
        }
        return round(percent, PRICE_PRECISION);
    }
    
    /**
     * Returns the total price by expense.
     * 
     * @return double
     */
    public double getListTotal(CompositionExpense to) {
        double quantity = to.getQuantity();
        double price = to.getPrice();
        return round(quantity * price, PRICE_PRECISION);
    }

    /**
     * Returns the total of expenses of the composition. 
     * 
     * @return double
     */
    public double getTotalExpenses(Composition to) {
        double expenses = 0;
        try {
            IManagerBean expensesBean = BeanManager.getManagerBean(CompositionExpense.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(expensesBean.getFieldName(ICompositionAlias.COMPOSITION_EXPENSE_COMPOSITION_ID), to.getId());
            Iterator iterator = expensesBean.getList(criteria).iterator();
            while (iterator.hasNext()) {
                CompositionExpense expense = (CompositionExpense)iterator.next();
                expenses += getListTotal(expense);
            }
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error getting composition expenses", e);
        }
        return expenses;
    }

    /**
     * Returns the total of details of the composition. 
     * 
     * @return double
     */
    public double getTotalDetails(Composition to) {
        double price = 0;
        try {
            IManagerBean detailsBean = BeanManager.getManagerBean(CompositionDetail.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(detailsBean.getFieldName(ICompositionAlias.COMPOSITION_DETAIL_COMPOSITION_ID), to.getId());
            Iterator iterator = detailsBean.getList(criteria).iterator();
            while (iterator.hasNext()) {
                CompositionDetail detail = (CompositionDetail)iterator.next();
                price += round(detail.getQuantity() * detail.getPrice(), PRICE_PRECISION);
            }
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error getting composition details", e);
        }
        return price;
    }

    /**
     * Returns the total of purchase of the composition. 
     * 
     * @return double
     */
    public double getTotalPurchase(Composition to) {
        short type = to.getType().shortValue();
        return (type == 0) ? getQuarteringTotalPurchase(to) : getManufactureTotalPurchase(to);
    }

    /**
     * Returns the total of sale of the composition. 
     * 
     * @return double
     */
    public double getTotalSale(Composition to) {
        short type = to.getType().shortValue();
        return (type == 0) ? getQuarteringTotalSale(to) : getManufactureTotalSale(to);
    }

    /**
     * Returns the unitary total of purchase of the composition. 
     * 
     * @return double
     */
    public double getUnitTotalPurchase(Composition to) {
        return (to.getQuantity() == 0) ? 0 : round(getTotalPurchase(to) / to.getQuantity(), PRICE_PRECISION);
    }

    /**
     * Returns the total of purchase of the composition of type quartering. 
     * 
     * @return double
     */
    public double getQuarteringTotalPurchase(Composition to) {
        double base = to.getQuantity() * (to.getPrice() * (1 + to.getExpensesPercent() / 100) + to.getExpensesFixed());
        double tax = base * (getTaxPercent(to.getItem()) + ((isSurcharge()) ? getSurchargePercent(to.getItem()) : 0)) / 100;
        double totalExpenses = getTotalExpenses(to);
        return round(base + tax + totalExpenses, PRICE_PRECISION);
    }

    /**
     * Returns the total of purchase of the composition of type elaboration. 
     * 
     * @return double
     */
    public double getManufactureTotalPurchase(Composition to) {
        return round(getTotalDetails(to) + getTotalExpenses(to), PRICE_PRECISION);
    }

    /**
     * Returns the total of sale of the composition of type quartering. 
     * 
     * @return double
     */
    public double getQuarteringTotalSale(Composition to) {
        return round(getTotalDetails(to), PRICE_PRECISION);
    }

    /**
     * Returns the total of sale of the composition of type elaboration. 
     * 
     * @return double
     */
    public double getManufactureTotalSale(Composition to) {
        return round(to.getQuantity() * to.getPrice(), PRICE_PRECISION);
    }

    /**
     * Returns the profit of the composition. 
     * 
     * @return double
     */
    public double getProfit(Composition to) {
        return getTotalSale(to) - getTotalPurchase(to);
    }

    /**
     * Returns the purchase profit of the composition. 
     * 
     * @return double
     */
    public double getPurchaseProfit(Composition to) {
        double purchasePrice = getTotalPurchase(to);
        return (purchasePrice==0) ? 0 : round(100 * getProfit(to) / purchasePrice, 2);
    }

    /**
     * Returns the sale profit of the composition. 
     * 
     * @return double
     */
    public double getSaleProfit(Composition to) {
        double salePrice = getTotalSale(to);
        return (salePrice==0) ? 0 : round(100 * getProfit(to) / salePrice, 2);
    }

    /**
     * Returns the sale quantity of the composition. 
     * 
     * @return double
     */
    public double getSaleQuantity(Composition to) {
        double quantity = 0;
        try {
            IManagerBean detailsBean = BeanManager.getManagerBean(CompositionDetail.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(detailsBean.getFieldName(ICompositionAlias.COMPOSITION_DETAIL_COMPOSITION_ID), to.getId());
            Iterator iterator = detailsBean.getList(criteria).iterator();
            while (iterator.hasNext()) {
                CompositionDetail detail = (CompositionDetail)iterator.next();
                quantity += detail.getQuantity();
            }
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error getting composition details", e);
        }
        return quantity;
    }

    /**
     * Returns the sale percentage of the composition. 
     * 
     * @return double
     */
    public double getSalePercentage(Composition to) {
        double masterQuantity = to.getQuantity();
        double childQuantity = getSaleQuantity(to);
        return (masterQuantity==0) ? 0 : round(100 * childQuantity / masterQuantity, 2);
    }

    /**
     * Returns the quantity percentage by detail.
     * 
     * @return double
     */
    public double getListPercentage(CompositionDetail to) {
        double masterQuantity = to.getComposition().getQuantity();
        double childQuantity = to.getQuantity();
        return (masterQuantity==0) ? 0 : round(100 * childQuantity / masterQuantity, PRICE_PRECISION);
    }

    /**
     * Returns the total price by detail.
     * 
     * @return double
     */
    public double getListTotal(CompositionDetail to) {
        double quantity = to.getQuantity();
        double price = to.getPrice();
        return round(quantity * price, PRICE_PRECISION);
    }

    /**
     * Returns the total cost by detail.
     * 
     * @return double
     */
    public double getListCost(CompositionDetail to) {
        double price = to.getPrice();
        return (price - round(price * getSaleProfit(to.getComposition()) / 100, PRICE_PRECISION));
    }

    /**
     * Returns the item purchase price from base price.
     * 
     * @return double
     */
    public double obtainItemPurchasePrice(Item item, double basePrice) {
        double expensesPercent = item.getExpensesPercent();
        double expensesFixed = item.getExpensesFixed();
        double tax = getTaxPercent(item);
        double surcharge = getSurchargePercent(item);
        return round((basePrice / (1 + (tax + surcharge) / 100) - expensesFixed) / (1 + expensesPercent / 100), PRICE_PRECISION); 
    }

    /**
     * Returns the item base price from purchase price.
     * 
     * @return double
     */
    public double obtainItemBasePrice(Item item, double purchasePrice) {
        double expensesPercent = item.getExpensesPercent();
        double expensesFixed = item.getExpensesFixed();
        double tax = getTaxPercent(item);
        double surcharge = getSurchargePercent(item);
        return round((purchasePrice * (1 + expensesPercent / 100) + expensesFixed) * (1 + (tax + surcharge) / 100), PRICE_PRECISION); 
    }

    /**
     * Returns the item taxable base from sale price.
     * 
     * @return double
     */
    public double obtainItemTaxableBase(Item item, double salePrice) {
        return round(salePrice / ((1 + getTaxPercent(item) / 100)), BASE_PRECISION);
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

    /** The PRICE PRECISION. */
    private int PRICE_PRECISION = 2;
    /** The BASE PRECISION. */
    private int BASE_PRECISION = 3;
    /** The LOGGER. */
    private final static Logger LOGGER = Logger.getLogger(CompositionPriceProvider.class.getName()); 

}
