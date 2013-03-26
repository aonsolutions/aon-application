package com.code.aon.fiscal.mod115;

import com.code.aon.common.AonException;
import com.code.aon.config.enumeration.Administration;

public interface IMod115Calculator {
	
	boolean accept(int year, Administration administration);
	void calculate(Mod115 mod115) throws AonException;
	double getResult(Mod115 mod115);

}
