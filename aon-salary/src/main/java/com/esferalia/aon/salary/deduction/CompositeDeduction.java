/**
 * 
 */
package com.esferalia.aon.salary.deduction;

import org.apache.commons.lang.StringUtils;

import com.esferalia.aon.salary.enumeration.DeductionType;

/**
 * @author rtrepiana
 *
 */
public class CompositeDeduction implements IDeduction {

	private IDeduction childs[];

	/**
	 * 
	 */
	public CompositeDeduction(IDeduction... childs) {
		this.childs = childs;
	}

	@Override
	public DeductionType getType() {
		for (int i = 1; i < childs.length; i++)
			if (!equals(childs[i - 1].getType(), childs[i].getType()))
				return null;
		return childs[0].getType();
	}

	@Override
	public String getName() {
		for (int i = 1; i < childs.length; i++)
			if (!StringUtils.equals(childs[i - 1].getName(),
					childs[i].getName()))
				return null;
		return childs[0].getName();
	}

	@Override
	public double getAmount() {
		double amount = 0.00;
		for (IDeduction child : childs)
			amount += child.getAmount();
		return amount;
	}

	@Override
	public String getDescription() {
		for (int i = 1; i < childs.length; i++)
			if (!StringUtils.equals(childs[i - 1].getDescription(),
					childs[i].getDescription()))
				return null;
		return childs[0].getDescription();
	}

	@Override
	public String getExpression() {
		for (int i = 1; i < childs.length; i++)
			if (!StringUtils.equals(childs[i - 1].getExpression(),
					childs[i].getExpression()))
				return null;
		return childs[0].getExpression();
	}

	private static boolean equals(Object x, Object y) {
		return (x == null && y == null) || (x != null && x.equals(y));
	}

}
