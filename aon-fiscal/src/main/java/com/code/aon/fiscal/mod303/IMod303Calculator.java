package com.code.aon.fiscal.mod303;

import com.code.aon.common.AonException;
import com.code.aon.config.enumeration.Administration;

public interface IMod303Calculator {
	
	boolean accept(int year, Administration administration);
	void calculate(Mod303 mod303) throws AonException;
	double getResult(Mod303 mod303);

}
