package com.code.aon.product.strategy;

import com.code.aon.company.WorkPlace;
import com.code.aon.product.Item;
import com.code.aon.product.util.DiscountExpression;

public interface ICalculable {
	
	public Item getItem();

	public DiscountExpression getDiscountExpression();

	public double getQuantity();

	public double getPrice();

	public double getTaxes();

	public WorkPlace getWorkPlace();

}
