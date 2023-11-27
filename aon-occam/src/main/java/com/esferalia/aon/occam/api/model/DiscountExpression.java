package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

public class DiscountExpression implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Aritmetic expression for discounts.
	 */
	private String discountExpr;

	/**
	 * All discounts.
	 */
	private double[] discounts;

	/**
	 * Separator for discounts in the String.
	 */
	private static final String SEPARATOR = "+";

	/**
	 * Discount Expression default value.
	 */
	private static final String ZERO = "0.0";

	/**
	 * Void constructor Default expression is a with no discount.
	 *
	 */
	public DiscountExpression() {
		discountExpr = ZERO;
		discounts = new double[] { 0.0 };
	}

	/**
	 * Contructor with an array with the discounts.
	 * 
	 * @param discounts All discounts.
	 */
	public DiscountExpression(double[] discounts) {
		this.discounts = discounts;
		StringBuilder buf = new StringBuilder("");
		for (int i = 0; i < discounts.length; i++) {
			if (i > 0) {
				buf.append(SEPARATOR);
			}
			buf.append(discounts[i]);
		}
		setDiscountExpr(buf.toString());
	}
	
	/**
	 * Contructor with an array with the discounts.
	 * 
	 * @param discounts All discounts.
	 */
	public DiscountExpression(double discount) {
		discountExpr = Double.toString(discount);
		this.discounts = new double[] {discount};
	}

	/**
	 * Contructor with a discount expression parameter. The format of this String
	 * must be <code>n+n+....</code> with <code>n</code> as a valid number.
	 * 
	 * @param discountExpr the discount expression.
	 */
	public DiscountExpression(String discountExpr) {
		this.discountExpr = discountExpr == null || (discountExpr.length()) == 0
				? ZERO : discountExpr;

		String[] arr = getDiscountExpr().split(SEPARATOR);
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
		this.discountExpr = discountExpr == null || (discountExpr.length()) == 0
				? ZERO : discountExpr;

		String[] arr = getDiscountExpr().split(SEPARATOR);
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
	 * @param discountExpression The discount expression.
	 * @return int Returns a negative integer, zero or a positive integer if is
	 *         less, the same o larger.
	 * 
	 * @see java.lang.Comparable#compareTo(Object)
	 */
	public int compareTo(DiscountExpression discountExpression) {
		return this.getDiscountExpr().compareTo(discountExpression.getDiscountExpr());
	}
	
	public double getPercentage() {
		double amount = 100;
		double percentage = 0.0;
		for (double d : discounts) {
			double per = amount * d/100;
			percentage = percentage + per;
			amount = amount - per;
		}
		return percentage;
	}
	
}
