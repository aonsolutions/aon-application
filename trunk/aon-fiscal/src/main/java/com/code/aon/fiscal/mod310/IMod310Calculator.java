package com.code.aon.fiscal.mod310;

import com.code.aon.common.AonException;
import com.code.aon.config.enumeration.Administration;

public interface IMod310Calculator {
	
	boolean accept(int year, Administration administration);
	void calculate(Mod310 mod310) throws AonException;
	double getResult(Mod310 mod310);

}
