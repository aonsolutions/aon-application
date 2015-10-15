package com.esferalia.aon.salary.bonus;

import java.io.Serializable;

import com.esferalia.aon.salary.ISalaryItem;
import com.esferalia.aon.salary.enumeration.BonusType;

public interface IBonus extends ISalaryItem<BonusType>, Serializable {
	public String getExpression();
}
