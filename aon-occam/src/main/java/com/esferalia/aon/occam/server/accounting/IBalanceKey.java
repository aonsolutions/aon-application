package com.esferalia.aon.occam.server.accounting;

public interface IBalanceKey {

	int getLevel();
	boolean isLeaf();
	String getCode();
	String getPrefix();
	String getName();
	String getInitialExpression();
	String getComputeExpression();
	
}
