package com.esferalia.aon.salary.bonus;

import com.esferalia.aon.salary.SalaryException;

public interface IBonusesFactory {

	boolean accept(IBonusesFactoryContext ctx);
	Bonuses getBonuses( IBonusesFactoryContext ctx ) throws SalaryException;
	
}
