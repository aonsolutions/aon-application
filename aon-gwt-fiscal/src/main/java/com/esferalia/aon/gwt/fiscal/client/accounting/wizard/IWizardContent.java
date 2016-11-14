package com.esferalia.aon.gwt.fiscal.client.accounting.wizard;

import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule.IAccountEntryModuleCallback;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule.IContentAttchCallback;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.WizardContentBase.ISelectionCallback;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;

public interface IWizardContent extends IsWidget {
	public void attach(IContentAttchCallback contentCbk);
	
	public void setFocus(boolean b);
	
	public AccountEntryType getAccountEntryType();
	
	public IAccountEntryModuleCallback getCallback();
	public void setCallback(IAccountEntryModuleCallback callback);

	public IAccountEntryWrapper getAccountEntryWrapper();
	
	public boolean isUpdatable();
	public boolean isNew();
	public boolean isDirty();
	
	public void reset();
	public void select(AccountEntry entry,ISelectionCallback cbk);
	public void save( final AsyncCallback<AccountEntry[]> callback);
	public void remove( final AsyncCallback<Void> callback);
	public void get(Integer id , final AsyncCallback<AccountEntry> callback);

	public void enableElements(boolean canRemove, boolean canEdit);
	
}
