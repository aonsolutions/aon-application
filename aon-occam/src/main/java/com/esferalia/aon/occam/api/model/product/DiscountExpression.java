
package com.esferalia.aon.occam.api.model.product;

import java.io.Serializable;
import java.util.regex.Pattern;

public class DiscountExpression implements Serializable {
	
	private static final long serialVersionUID = -1987289760974488376L;
	
    private static final Pattern PATTERN = Pattern.compile("\\+");

    public static double[] getDiscounts(String discountExpr) {
        String[] arr = PATTERN.split(discountExpr);
        double[] discounts = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            String discount = arr[i];
            discounts[i] = Double.parseDouble(discount.trim());
        }
        return discounts;
    }

}
