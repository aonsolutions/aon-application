package com.code.aon.fiscal.mod131;

import com.code.aon.common.AonException;
import com.code.aon.config.enumeration.Administration;

public interface IMod131Calculator {
	
	boolean accept(int year, Administration administration);
	void calculate(Mod131 mod131) throws AonException;
	double getResult(Mod131 mod131);

}
