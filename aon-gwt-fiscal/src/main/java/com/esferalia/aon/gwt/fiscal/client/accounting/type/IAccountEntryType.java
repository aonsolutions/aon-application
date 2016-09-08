package com.esferalia.aon.gwt.fiscal.client.accounting.type;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IAccountEntryType {
	
	public AccountEntryType getAccountEntryType();
	
	public AccountEntry getAccountEntry();
	public void setAccountEntry(AccountEntry ae);
	public LinkedList<AccountEntryDetail> getDetails();
	public int getSize();
	public AccountEntryDetail getLast();
	
	public boolean isPeriodActive();
	public boolean isManual();
	public boolean isUpdatable();
	public boolean isNew();
	public boolean isDirty();
	
	public void save( final AsyncCallback<AccountEntry> callback);
	public void remove( final AsyncCallback<Void> callback);
	public void get(Integer id , final AsyncCallback<AccountEntry> callback);
	
}
