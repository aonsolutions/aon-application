package com.esferalia.aon.payroll.calculator;

public interface IContractLeave {
	
	double getCgcBase();

	double getCgpBase();

	double getRegBase();

	int getDays(int start, int end );

}
