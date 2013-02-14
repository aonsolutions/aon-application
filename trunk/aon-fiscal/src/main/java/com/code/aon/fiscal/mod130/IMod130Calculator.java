package com.code.aon.fiscal.mod130;

import com.code.aon.common.AonException;
import com.code.aon.config.enumeration.Administration;

public interface IMod130Calculator {
	
	boolean accept(int year, Administration administration);
	void calculate(Mod130 mod130) throws AonException;

}
