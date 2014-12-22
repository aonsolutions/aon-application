package com.code.aon.product.strategy;

import java.util.Date;
import java.util.List;

import com.code.aon.product.util.DiscountExpression;
import com.code.aon.registry.Registry;

public interface ICalculableContainer {

	public Registry getRegistry();

	public Date getDate();

	public DiscountExpression getDiscountExpression();

	public List<?> getDetailList();

}
