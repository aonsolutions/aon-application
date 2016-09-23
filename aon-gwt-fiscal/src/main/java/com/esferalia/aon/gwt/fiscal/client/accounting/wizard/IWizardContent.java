package com.esferalia.aon.gwt.fiscal.client.accounting.wizard;

import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;

public interface IWizardContent extends IsWidget {
	
	public void setFocus(boolean b);
	
	public AccountEntryType getAccountEntryType();
	
	public AccountEntry getAccountEntry();
	public void setAccountEntry(AccountEntry accountEntry);
	public void paint();
	public void reset();
	
	public boolean isUpdatable();
	public boolean isNew();
	public boolean isDirty();
	
	public void select(AccountEntry entry);
	public void save( final AsyncCallback<AccountEntry> callback);
	public void remove( final AsyncCallback<Void> callback);
	public void get(Integer id , final AsyncCallback<AccountEntry> callback);
	
}
