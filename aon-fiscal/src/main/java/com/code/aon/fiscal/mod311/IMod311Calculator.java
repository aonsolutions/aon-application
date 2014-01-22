package com.code.aon.fiscal.mod311;

import com.code.aon.common.AonException;
import com.code.aon.config.enumeration.Administration;

public interface IMod311Calculator {
	
	boolean accept(int year, Administration administration);
	void calculate(Mod311 mod311) throws AonException;
	double getResult(Mod311 mod311);

}
