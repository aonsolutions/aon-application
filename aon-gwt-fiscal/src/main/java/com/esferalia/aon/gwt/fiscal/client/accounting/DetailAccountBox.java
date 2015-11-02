package com.esferalia.aon.gwt.fiscal.client.accounting;

import com.esferalia.aon.gwt.common.client.widget.AccountBox;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;

class DetailAccountBox extends AccountBox {
	
	public DetailAccountBox(final AccountEntryDetail aed) {
		super(AccountEntryModule.getCurrentDomainName(), AccountEntryModule
				.getCurrentDomain());
		setValue(aed.getAccount(), aed.getAccountCode(),
				aed.getAccountDescription());
		addSelectionHandler(new SelectionHandler<Account>() {
			@Override
			public void onSelection(SelectionEvent<Account> event) {
				aed.setAccount(event.getSelectedItem().getId());
				aed.setAccountCode(event.getSelectedItem().getCode());
				aed.setAccountDescription(event.getSelectedItem().getDescription());
			}
		});
	}
}
