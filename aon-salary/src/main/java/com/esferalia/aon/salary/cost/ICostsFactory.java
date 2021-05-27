package com.esferalia.aon.salary.cost;

import com.esferalia.aon.salary.SalaryException;

public interface ICostsFactory {

	boolean accept(ICostsFactoryContext ctx);
	Costs getCosts( ICostsFactoryContext ctx ) throws SalaryException;
	
}
