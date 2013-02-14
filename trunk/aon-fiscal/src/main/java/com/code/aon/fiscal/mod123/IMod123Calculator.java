package com.code.aon.fiscal.mod123;

import com.code.aon.common.AonException;
import com.code.aon.config.enumeration.Administration;

public interface IMod123Calculator {
	
	boolean accept(int year, Administration administration);
	void calculate(Mod123 mod123) throws AonException;

}
