package com.esferalia.aon.occam.server.accounting;

import java.io.Serializable;
import java.util.List;

import com.esferalia.aon.occam.api.model.accounting.IAccMiningKeyAccept;

public abstract class BalanceScript implements Serializable {

	private static final long serialVersionUID = 6703644150354316745L;
	
	public abstract  IAccMiningKeyAccept getAccepter();
	public abstract List<? extends IBalanceKey> getKeyList();
	
}
