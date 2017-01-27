package com.esferalia.aon.gwt.fiscal.client.accounting.wizard;

import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule.IContentAttachCallback;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.WizardContentBase.ISelectionCallback;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.IsWidget;

public interface IWizardContent extends IsWidget,Focusable {
	
	public void attach(IContentAttachCallback contentCbk);
	public AccountEntry getMainEntry(); 
	public boolean isUpdatable();
	public boolean isNew();
	
	public IAccountEntryWrapper getEntryWrapper();
	
	public void reset(AccountEntry moduleEntry, ISelectionCallback selectionCallback);
	public void select(Integer id,IAccountEntryWrapper t,ISelectionCallback cbk);
	public void save( final AsyncCallback<AccountEntry[]> callback);
	public void remove( final AsyncCallback<Void> callback);

	public void manageWidgets(boolean canRemove, boolean canEdit);
	
//	public IAccountEntryWrapper create(AccountEntry base);
//	public boolean isNew();
//	public boolean isDirty();
//	public void get(Integer id , final AsyncCallback<AccountEntry> callback);
}
