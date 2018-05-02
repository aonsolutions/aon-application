package com.esferalia.aon.gwt.fiscal.client;

import com.esferalia.aon.gwt.common.client.ModuleCallback;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.google.gwt.event.shared.GwtEvent;

public class AccountEntrySelectionEvent extends GwtEvent<AccountEntrySelectionHandler> {

	private static Type<AccountEntrySelectionHandler> TYPE;

	public static <T> void fire(HasAccountEntrySelectionHandlers source, AccountEntry selectedItem, ModuleCallback<AccountEntry> callback) {
		if (TYPE != null) {
			AccountEntrySelectionEvent event = new AccountEntrySelectionEvent(selectedItem, callback);
			source.fireEvent(event);
		}
	}

	public static Type<AccountEntrySelectionHandler> getType() {
		if (TYPE == null) {
			TYPE = new Type<AccountEntrySelectionHandler>();
		}
		return TYPE;
	}

	private final AccountEntry selectedItem;
	private final ModuleCallback<AccountEntry> callback;

	protected AccountEntrySelectionEvent(AccountEntry selectedItem, ModuleCallback<AccountEntry> callback) {
		this.selectedItem = selectedItem;
		this.callback = callback;
	}

	@Override
	public final Type<AccountEntrySelectionHandler> getAssociatedType() {
		return TYPE;
	}

	public AccountEntry getSelectedItem() {
		return selectedItem;
	}

	public ModuleCallback<AccountEntry> getCallback() {
		return this.callback;
	}

	@Override
	protected void dispatch(AccountEntrySelectionHandler handler) {
		handler.onSelection(this);
	}
}
