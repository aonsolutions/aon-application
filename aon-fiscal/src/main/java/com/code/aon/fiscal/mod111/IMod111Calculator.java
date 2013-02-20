package com.code.aon.fiscal.mod111;

import com.code.aon.common.AonException;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.fiscal.enumeration.Mod111Key;

public interface IMod111Calculator {
	
	boolean accept(int year, Administration administration);
	void calculate(Mod111 mod111) throws AonException;
	
	Mod111Key getKeyForReceivers();
	Mod111Key getKeyForPerception();
	Mod111Key getKeyForWitholding();

	Mod111Key getKeyForInKindReceivers();
	Mod111Key getKeyForInKindPerception();
	Mod111Key getKeyForInKindWitholding();
	
	double getResult(Mod111 mod111);

}
