package com.code.aon.ui.product.util;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.dao.CriteriaUtilities;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.product.Item;
import com.code.aon.product.Tax;
import com.code.aon.product.enumeration.TaxType;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.ql.Criteria;

/**
 * Class used to calculate prices.
 */
public class ItemPriceProvider {
	
    /** The tax provider. */
    private ItemCompanyTaxProvider taxProvider = new ItemCompanyTaxProvider();

    /**
     * Checks if a surcharge has to be applied.
     * 
     * @return true, if a surcharge has to be applied.
     */
    public boolean isSurcharge() {
        return taxProvider.isSurcharge();
    }
    
    /**
     * Gets the purchase price of an <code>Item</code>.
     * 
     * @param item the item
     * 
     * @return the purchase price
     */
    public double getPurchasePrice(Item item) {
        Session session = HibernateUtil.getSession();
        String stmt = "SELECT incomeDetail FROM IncomeDetail incomeDetail, Income income, Item item WHERE incomeDetail.income.id = income.id and incomeDetail.item.id = item.id";
        Criteria criteria = new Criteria();
        criteria.addEqualExpression("item.id", item.getId());
        String sentence = CriteriaUtilities.toSQLString(criteria, stmt);
        sentence = (new StringBuilder()).append(sentence).append("order by income.issueTime desc").toString();
        Query query = session.createQuery(sentence);
        List list = query.list();
        Iterator iter = list.iterator();
        if (iter.hasNext()) {
            ICalculable calculable = (ICalculable)iter.next();
            double price = calculable.getPrice();
            double discount = 0;
            double discounts[] = calculable.getDiscountExpression().getDiscounts();
            for(int i = 0; i < discounts.length; i++)
                discount += discounts[i];

            price *= 1 - discount / 100;
            return round(price, PRICE_PRECISION);
        }
        return item.getPurchasePrice();
        
    }

    /**
     * Gets the base price of an <code>Item</code>.
     * 
     * @param item the item
     * 
     * @return the base price
     */
    public double getBasePrice(Item item) {
    	return getCommonBasePrice(item, getPurchasePrice(item));
    }

    /**
     * Gets the real base price of an <code>Item</code>.
     * 
     * @param item the item
     * 
     * @return the real base price
     */
    public double getRealBasePrice(Item item) {
    	return getCommonBasePrice(item, item.getPurchasePrice());
    }

    private double getCommonBasePrice(Item item, double purchasePriceParam){
        double purchasePrice = (purchasePriceParam * (1 + item.getExpensesPercent() / 100)) + item.getExpensesFixed();
        double percent = 0;
        Iterator iter = taxProvider.getTaxList(item).iterator();
        while (iter.hasNext()) {
            Tax tax = (Tax)iter.next();
            percent += tax.getPercentage();
            if (isSurcharge()) {
                percent += tax.getSurcharge();
            }
        }
        return round(purchasePrice * (1 + percent / 100), BASE_PRECISION);
    }
    
    public double getInventoryBasePrice(Item item) {
        double purchasePrice = (item.getPurchasePrice() * (1 + item.getExpensesPercent() / 100)) + item.getExpensesFixed();
        double percent = 0;
        Iterator iter = taxProvider.getTaxList(item).iterator();
        while (iter.hasNext()) {
            Tax tax = (Tax)iter.next();
            if (!tax.getType().equals(TaxType.VAT) || isSurcharge()) {
                percent += tax.getPercentage();
                if (isSurcharge()) {
                    percent += tax.getSurcharge();
                }
            }
        }
        return round(purchasePrice * (1 + percent / 100), BASE_PRECISION);
    }

    /**
     * Gets the total price of an <code>Item</code>..
     * 
     * @param item the item
     * 
     * @return the total price
     */
    public double getTotalPrice(Item item) {
        double basePrice = getBasePrice(item);
        return round(basePrice + (basePrice * item.getProfitPercent() / 100), BASE_PRECISION);
    }

    /**
     * Gets the real total price of an <code>Item</code>.
     * 
     * @param item the item
     * 
     * @return the real total price
     */
    public double getRealTotalPrice(Item item) {
        return round(item.getPrice() * (1 + getTax(item) / 100), PRICE_PRECISION);
    }

    /**
     * Gets the taxable base of an <code>Item</code>.
     * 
     * @param item the item
     * 
     * @return the taxable base
     */
    public double getTaxableBase(Item item) {
        return round(getTotalPrice(item) / (1 + getTax(item) / 100), PRICE_PRECISION);
    }

    /**
     * Gets the profit percent of an <code>Item</code>.
     * 
     * @param item the item
     * 
     * @return the profit percent
     */
    public double getProfitPercent(Item item) {
        double basePrice = getBasePrice(item);
        double totalPrice = getRealTotalPrice(item);
        return (basePrice==0) ? 0 : round(((totalPrice - basePrice) * 100 / basePrice), BASE_PRECISION);
    }

    /**
     * Gets the profit percent2 of an <code>Item</code>.
     * 
     * @param item the item
     * 
     * @return the profit percent2
     */
    public double getProfitPercent2(Item item) {
        double basePrice = getBasePrice(item);
        double totalPrice = getRealTotalPrice(item);
        return (totalPrice==0) ? 0 : round(((totalPrice - basePrice) * 100 / totalPrice), BASE_PRECISION);
    }

    /**
     * Gets the real profit percent2 of an <code>Item</code>.
     * 
     * @param item the item
     * 
     * @return the real profit percent2
     */
    public double getRealProfitPercent2(Item item) {
        double basePrice = getRealBasePrice(item);
        double totalPrice = getRealTotalPrice(item);
        return (totalPrice==0) ? 0 : round(((totalPrice - basePrice) * 100 / totalPrice), BASE_PRECISION);
    }

    /**
     * Gets the tax of an <code>Item</code>.
     * 
     * @param item the item
     * 
     * @return the tax
     */
    public double getTax(Item item) {
        double percent = 0;
        List taxList = taxProvider.getTaxList(item);
        if (taxList != null) {
            Iterator iterator = taxList.iterator();
            while (iterator.hasNext()) {
                Tax tax = (Tax)iterator.next();
                percent += tax.getPercentage();
            }
            return round(percent, PRICE_PRECISION);
        }
        return 0;    	
    }
    
    /**
     * Gets the tax rate of an <code>Item</code>.
     * 
     * @param item the item
     * 
     * @return the tax rate
     */
    public double getTaxRate(Item item) {
        return round(getTax(item) * getTaxableBase(item) / 100, BASE_PRECISION);
    }

    /**
     * Gets the vat of an <code>Item</code>.
     * 
     * @param item the item
     * 
     * @return the vat
     */
    public double getVat(Item item) {
        double percent = 0;
        List taxList = taxProvider.getTaxList(item);
        if (taxList != null) {
            Iterator iterator = taxList.iterator();
            while (iterator.hasNext()) {
                Tax tax = (Tax)iterator.next();
                if (tax.getType().equals(TaxType.VAT))
                    percent +=  tax.getPercentage();
            }
            return round(percent, PRICE_PRECISION);
        }
        return 0;       
    }
    
    /**
     * Gets the vat rate of an <code>Item</code>.
     * 
     * @param item the item
     * 
     * @return the vat rate
     */
    public double getVatRate(Item item) {
        return round(getVat(item) * getTaxableBase(item) / 100, BASE_PRECISION);
    }

    /**
     * Gets the surcharge of an <code>Item</code>.
     * 
     * @param item the item
     * 
     * @return the surcharge
     */
    public double getSurcharge(Item item) {
        double percent = 0;
        List taxList = taxProvider.getTaxList(item);
        if (taxList != null) {
            Iterator iterator = taxList.iterator();
            while (iterator.hasNext()) {
                Tax tax = (Tax)iterator.next();
                if (tax.getType().equals(TaxType.VAT))
                    percent +=  tax.getSurcharge();
            }
            return round(percent, PRICE_PRECISION);
        }
        return 0;       
    }
    
    /**
     * Gets the surcharge rate of an <code>Item</code>.
     * 
     * @param item the item
     * 
     * @return the surcharge rate
     */
    public double getSurchargeRate(Item item) {
        if (isSurcharge()) {
            return round(getSurcharge(item) * getTaxableBase(item) / 100, BASE_PRECISION);
        }
        return 0;
    }

    /**
     * Gets the expenses percent rate of an <code>Item</code>.
     * 
     * @param item the item
     * 
     * @return the expenses percent rate
     */
    public double getExpensesRate(Item item) {
        return round(item.getPurchasePrice() * (item.getExpensesPercent() / 100) + item.getExpensesFixed(), BASE_PRECISION);
    }

    /**
     * Round the <code>value</code> using the <code>precision</code> passed as a parameter
     * 
     * @param value the value
     * @param precision the precision
     * 
     * @return the double
     */
    private double round(double value, int precision) {
        double decimal = Math.pow(10, precision);
        return Math.round(decimal*value) / decimal;
    }

    /** The BASE PRECISION. */
    private int BASE_PRECISION = 3;
    
    /** The PRICE PRECISION. */
    private int PRICE_PRECISION = 2;
}
