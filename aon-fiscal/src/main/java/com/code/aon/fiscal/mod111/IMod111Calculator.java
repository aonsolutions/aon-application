package com.code.aon.fiscal.mod111;

import com.code.aon.common.AonException;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.fiscal.enumeration.Mod111Key;

public interface IMod111Calculator {
	
	boolean accept(int year, Administration administration);
	void calculate(Mod111 mod111) throws AonException;
	
	Mod111Key getKeyForWorkReceivers();
	Mod111Key getKeyForWorkPerception();
	Mod111Key getKeyForWorkWitholding();

	Mod111Key getKeyForWorkInKindReceivers();
	Mod111Key getKeyForWorkInKindPerception();
	Mod111Key getKeyForWorkInKindWitholding();
	
	Mod111Key getKeyForInvoiceReceivers();
	Mod111Key getKeyForInvoicePerception();
	Mod111Key getKeyForInvoiceWitholding();

	Mod111Key getKeyForInvoiceInKindReceivers();
	Mod111Key getKeyForInvoiceInKindPerception();
	Mod111Key getKeyForInvoiceInKindWitholding();

	double getResult(Mod111 mod111);

}
