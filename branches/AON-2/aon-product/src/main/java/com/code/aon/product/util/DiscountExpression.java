package com.code.aon.product.util;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * Class that manages aritmetic expressions of discounts. 
 * 
 * @author Consulting & Development. Eugenio Castellano - 21-abr-2005
 * @since 1.0
 *  
 */
public class DiscountExpression implements Serializable, Comparable{
	
    /**
     * Aritmetic expression for discounts.
     */
    private String discountExpr;

    /**
     * All discounts.
     */
    private double[] discounts;

    /**
     * Regular expression to obtain the discounts included in the String.
     */
    private static Pattern PATTERN = Pattern.compile("\\+"); //$NON-NLS-1$

    /**
     * Separator for discounts in the String.
     */
    private static String SEPARATOR = "+"; //$NON-NLS-1$

    /**
     * Void constructor
     * Default expression is a with no discount.
     *
     */
    public DiscountExpression(){
    	discountExpr = "0.0";
    }
    
    /**
     * Contructor with an array with the discounts.
     * 
     * @param discounts
     *            All discounts.
     */
    public DiscountExpression(double[] discounts) {
        this.discounts = discounts;
        discountExpr = "";
        StringBuffer buf = new StringBuffer("");
        for (int i = 0; i < discounts.length; i++) {
            if (i > 0) {
                buf.append(SEPARATOR);
            }
            buf.append(discounts[i]);
        }
    }

    /**
     * Contructor with a discount expression parameter.
     * The format of this String must be <code>n+n+....</code> with <code>n</code>
     * as a valid number.
     * 
     * @param discountExpr the discount expression.
     */
    public DiscountExpression(String discountExpr) {
        this.discountExpr = discountExpr;

        String[] arr = PATTERN.split(discountExpr);
        discounts = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            String discount = arr[i];
            discounts[i] = Double.parseDouble(discount.trim());
        }
    }

    /**
     * Returns the string with the expression.
     * 
     * @return String the expression.
     */
    public String getDiscountExpr() {
        return discountExpr;
    }
    
    /**
     * Assigns the string with the expression.
     * 
     * @param discountExpr string with the expression.
     */
    public void setDiscountExpr(String discountExpr) {
        this.discountExpr = discountExpr;
        
        String[] arr = PATTERN.split(discountExpr);
        discounts = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            String discount = arr[i];
            discounts[i] = Double.parseDouble(discount.trim());
        }
    }

    /**
     * Returns an array with the discounts.
     * 
     * @return double[] the discounts.
     */
    public double[] getDiscounts() {
        return discounts;
    }

    /**
     * Compares this object with the one specified. .
     * 
     * @param object *
     *            The object to be compared.
     * @return int Returns a negative integer, zero or a positive integer if is less, the same o larger.
     * 
     * @see java.lang.Comparable#compareTo(Object)
     */
    public int compareTo(Object object) {
        return compareTo((DiscountExpression) object);
    }

    /**
     * Compares this object with the one specified. .
     * 
     * @param object *
     *            The object to be compared.
     * @return int Returns a negative integer, zero or a positive integer if is less, the same o larger.
     * 
     * @see java.lang.Comparable#compareTo(Object)
     */
    public int compareTo(DiscountExpression discountExpression) {
        return this.getDiscountExpr().compareTo(discountExpression.getDiscountExpr()); 
    }

}
