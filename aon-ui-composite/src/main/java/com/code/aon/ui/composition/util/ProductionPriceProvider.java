package com.code.aon.ui.composition.util;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.composition.Production;
import com.code.aon.composition.ProductionDetail;
import com.code.aon.composition.ProductionExpense;
import com.code.aon.composition.dao.ICompositionAlias;
import com.code.aon.ql.Criteria;

/**
 * Class used to calculate prices of production.
 * 
 * @author Consulting & Development.
 */
public class ProductionPriceProvider {

    /**
     * Returns the total price by expense.
     * 
     * @return double
     */
    public double getListTotal(ProductionExpense to) {
        double quantity = to.getQuantity();
        double price = to.getPrice();
        return round(quantity * price, PRICE_PRECISION);
    }

    /**
     * Returns the total of expenses of the production. 
     * 
     * @return double
     */
    public double getTotalExpenses(Production to) {
        double expenses = 0;
        try {
            IManagerBean expensesBean = BeanManager.getManagerBean(ProductionExpense.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(expensesBean.getFieldName(ICompositionAlias.PRODUCTION_EXPENSE_PRODUCTION_ID), to.getId());
            Iterator iterator = expensesBean.getList(criteria).iterator();
            while (iterator.hasNext()) {
                ProductionExpense expense = (ProductionExpense)iterator.next();
                expenses += getListTotal(expense);
            }
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error getting production expenses", e);
        }
        return expenses;
    }

    /**
     * Returns the total of details of the production. 
     * 
     * @return double
     */
    public double getTotalDetails(Production to) {
        double price = 0;
        try {
            IManagerBean detailsBean = BeanManager.getManagerBean(ProductionDetail.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(detailsBean.getFieldName(ICompositionAlias.PRODUCTION_DETAIL_PRODUCTION_ID), to.getId());
            Iterator iterator = detailsBean.getList(criteria).iterator();
            while (iterator.hasNext()) {
                ProductionDetail detail = (ProductionDetail)iterator.next();
                price += round(detail.getQuantity() * detail.getPrice(), PRICE_PRECISION);
            }
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error getting production details", e);
        }
        return price;
    }

    /**
     * Returns the total of purchase of the production. 
     * 
     * @return double
     */
    public double getTotalPurchase(Production to) {
        return round(getTotalDetails(to) + getTotalExpenses(to), PRICE_PRECISION);
    }

    /**
     * Returns the total of sale of the production. 
     * 
     * @return double
     */
    public double getTotalSale(Production to) {
        return round(to.getQuantity() * to.getPrice(), PRICE_PRECISION);
    }

    /**
     * Returns the profit of the production. 
     * 
     * @return double
     */
    public double getProfit(Production to) {
        return getTotalSale(to) - getTotalPurchase(to);
    }

    /**
     * Returns the purchase profit of the production. 
     * 
     * @return double
     */
    public double getPurchaseProfit(Production to) {
        double purchasePrice = getTotalPurchase(to);
        return (purchasePrice==0) ? 0 : round(100 * getProfit(to) / purchasePrice, 2);
    }

    /**
     * Returns the sale profit of the production. 
     * 
     * @return double
     */
    public double getSaleProfit(Production to) {
        double salePrice = getTotalSale(to);
        return (salePrice==0) ? 0 : round(100 * getProfit(to) / salePrice, 2);
    }

    /**
     * Returns the sale quantity of the production. 
     * 
     * @return double
     */
    public double getSaleQuantity(Production to) {
        double quantity = 0;
        try {
            IManagerBean detailsBean = BeanManager.getManagerBean(ProductionDetail.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(detailsBean.getFieldName(ICompositionAlias.PRODUCTION_DETAIL_PRODUCTION_ID), to.getId());
            Iterator iterator = detailsBean.getList(criteria).iterator();
            while (iterator.hasNext()) {
                ProductionDetail detail = (ProductionDetail)iterator.next();
                quantity += detail.getQuantity();
            }
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error getting production details", e);
        }
        return quantity;
    }

    /**
     * Returns the sale percentage of the production. 
     * 
     * @return double
     */
    public double getSalePercentage(Production to) {
        double masterQuantity = to.getQuantity();
        double childQuantity = getSaleQuantity(to);
        return (masterQuantity==0) ? 0 : round(100 * childQuantity / masterQuantity, 2);
    }

    /**
     * Returns the quantity percentage by detail.
     * 
     * @return double
     */
    public double getListPercentage(ProductionDetail to) {
        double masterQuantity = to.getProduction().getQuantity();
        double childQuantity = to.getQuantity();
        return (masterQuantity==0) ? 0 : round(100 * childQuantity / masterQuantity, PRICE_PRECISION);
    }

    /**
     * Returns the total price by detail.
     * 
     * @return double
     */
    public double getListTotal(ProductionDetail to) {
        double quantity = to.getQuantity();
        double price = to.getPrice();
        return round(quantity * price, PRICE_PRECISION);
    }

    /**
     * Rounds the <code>value</code> using the <code>precision</code> passed as a parameter.
     * 
     * @param value
     * @param precision
     * 
     * @return double
     */
    public double round(double value, int precision) {
        double decimal = Math.pow(10, precision);
        return Math.round(decimal*value) / decimal;
    }

    /** The PRICE PRECISION. */
    private int PRICE_PRECISION = 2;
    /** The LOGGER. */
    private final static Logger LOGGER = Logger.getLogger(ProductionPriceProvider.class.getName()); 

}
