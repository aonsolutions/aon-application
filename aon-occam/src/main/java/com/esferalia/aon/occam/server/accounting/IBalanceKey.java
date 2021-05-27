package com.esferalia.aon.occam.server.accounting;

import com.esferalia.aon.occam.api.model.AccountBalanceLineStyle;

public interface IBalanceKey {

	int getLevel();
	AccountBalanceLineStyle getType();
	String getCode();
	String getPrefix();
	String getName();
	String getInitialExpression();
	String getComputeExpression();
	
}
