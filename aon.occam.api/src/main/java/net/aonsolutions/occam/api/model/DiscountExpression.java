package net.aonsolutions.occam.api.model;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class DiscountExpression implements Serializable {

	private static final long serialVersionUID = 1L;

	private String discountExpr;
	private static final char SEPARATOR = '+';
	private static final String ZERO = "0.0";

	public DiscountExpression() {
		discountExpr = ZERO;
	}
	
	public String getDiscountExpr() {
		return discountExpr;
	}
	public DiscountExpression setDiscountExpr(String discountExpr) {
		this.discountExpr = AonStringUtils.isBlank(discountExpr) ? ZERO : discountExpr;
		this.discountExpr = this.discountExpr.replace(",",".");
		return this;
	}

	public double[] getDiscounts() {
		String[] arr = AonStringUtils.split(getDiscountExpr(), SEPARATOR);
		return AonCollectionUtils.stream(arr)
			.mapToDouble( d -> AonNumberUtils.todouble(d))
			.toArray();
	}

	public int compareTo(DiscountExpression discountExpression) {
		return this.getDiscountExpr().compareTo(discountExpression.getDiscountExpr());
	}
	
	public double getPercentage() {
		double amount = 100;
		double percentage = 0.0;
		for (double d : getDiscounts() ) {
			double per = amount * d/100;
			percentage = percentage + per;
			amount = amount - per;
		}
		return percentage;
	}

	public DiscountExpression setDiscount(double discount) {
		setDiscountExpr(AonNumberUtils.toString(discount));
		return null;
	}
	
}
