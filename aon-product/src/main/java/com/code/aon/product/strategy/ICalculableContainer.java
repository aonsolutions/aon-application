package com.code.aon.product.strategy;

import java.util.Date;
import java.util.List;

import com.code.aon.product.util.DiscountExpression;

public interface ICalculableContainer {

	public List<?> getDetailList();

	public DiscountExpression getDiscountExpression();

	public Date getDate();

}
