package com.esferalia.aon.gwt.fiscal.client.accounting;

import com.esferalia.aon.gwt.common.client.widget.AccountBox;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;

class BalancingAccountBox extends AccountBox {
	public BalancingAccountBox(final AccountEntryDetail aed) {
		super(AccountEntryModule.getCurrentDomainName(), AccountEntryModule
				.getCurrentDomain(), false);
		setValue(aed.getBalancingAccount(), aed.getBalancingAccountCode(),
				aed.getBalancingAccountDescription());
		addSelectionHandler(new SelectionHandler<Account>() {
			@Override
			public void onSelection(SelectionEvent<Account> event) {
				aed.setBalancingAccount(event.getSelectedItem().getId());
				aed.setBalancingAccountCode(event.getSelectedItem().getCode());
				aed.setBalancingAccountDescription(event.getSelectedItem().getDescription());
			}
		});
	}
}
