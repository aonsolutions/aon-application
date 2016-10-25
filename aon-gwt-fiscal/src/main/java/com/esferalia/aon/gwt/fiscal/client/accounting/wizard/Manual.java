package com.esferalia.aon.gwt.fiscal.client.accounting.wizard;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule.IAccountEntryModuleCallback;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryTable;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Manual extends WizardContentBase {
	
	private ScrollPanel tableContainer;
	private VerticalPanel tableInnerContainer;
	private AccountEntryTable table;
	
	public Manual(final IAccountEntryModuleCallback callback) {
		setCallback(callback);
		tableContainer = new ScrollPanel();
		initWidget(tableContainer);
	}
	
	@Override
	public void select(AccountEntry entry) {
		this.ae = entry;
		reset();
		table.paintTable();
		tableInnerContainer.add(table);
		tableContainer.setWidget(tableInnerContainer);
		callback.onBalance(entry);
	}
	
	@Override
	public AccountEntryType getAccountEntryType() {
		return AccountEntryType.MANUAL;
	}

	@Override
	public void setFocus(boolean b) {
		table.setFocus(b);
	}
	
	@Override
	public boolean isUpdatable() {
		return super.isUpdatable() && getAccountEntry().isManual();
	}
	
	@Override
	public void reset() {
		
		tableContainer.setStyleName(AON.AON_CSS.aonScrollArea());
		tableInnerContainer = new VerticalPanel();
		tableInnerContainer.addStyleName(AON.AON_CSS.aonWidthAll());
		
		table = new AccountEntryTable(this);
		table.addErrorHandler(new ErrorHandler() {

			@Override
			public void onError(ErrorEvent event) {
				callback.onError(event.getRelativeElement().getAttribute("ERROR"));
			}
			
		});
		table.addSelectionHandler(new SelectionHandler<Account>() {
			@Override
			public void onSelection(SelectionEvent<Account> event) {
				Account account = event.getSelectedItem();
				if (account != null) callback.onBalance(account);
			}
		});
		table.addValueChangeHandler(new ValueChangeHandler<AccountEntryDetail>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<AccountEntryDetail> event) {
				callback.onRefreshId();
			}
		});
	}
	
}
