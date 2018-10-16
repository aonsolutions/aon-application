package com.esferalia.aon.occam.server.accounting;

public interface IBalanceKey {

	int getLevel();
	String getCode();
	String getName();
	String getInitialExpression();
	String getComputeExpression();
	
}
