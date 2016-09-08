package com.esferalia.aon.gwt.fiscal.client.accounting.type;

import com.esferalia.aon.occam.api.model.type.AccountEntryType;

public class AccountEntryManual extends AccountEntryBase {
	
	@Override
	public AccountEntryType getAccountEntryType() {
		return AccountEntryType.MANUAL;
	}

}
