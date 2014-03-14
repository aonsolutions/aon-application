package com.esferalia.aon.salary.bonus;

import com.esferalia.aon.salary.ISalaryItem;
import com.esferalia.aon.salary.enumeration.BonusType;

public interface IBonus extends ISalaryItem<BonusType> {
	public String getExpression();
}
