package com.esferalia.aon.occam.server.accounting;

import java.util.List;

import com.esferalia.aon.occam.api.model.accounting.IAccMiningKeyAccept;

public abstract class BalanceScript {

	public abstract  IAccMiningKeyAccept getAccepter();
	public abstract List<? extends IBalanceKey> getKeyList();
	
}
