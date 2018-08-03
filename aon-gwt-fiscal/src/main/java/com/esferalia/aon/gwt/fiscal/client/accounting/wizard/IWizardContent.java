package com.esferalia.aon.gwt.fiscal.client.accounting.wizard;

import java.util.Date;

import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule.IContentAttachCallback;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.WizardContentBase.ISelectionCallback;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.IsWidget;

public interface IWizardContent extends IsWidget,Focusable {
	
	public AonConfiguration getConfiguration();
	public void attach(IContentAttachCallback contentCbk);
	public AccountEntry getMainEntry();
	public boolean isUpdatable();
	public String getNoUpdatableCause();
	public boolean isRemovable();
	public boolean isNew();
	
	public IAccountEntryWrapper getEntryWrapper();
	
	public void reset(AccountEntry moduleEntry, ISelectionCallback selectionCallback);
	public void select(Integer id,IAccountEntryWrapper t,ISelectionCallback cbk);
	public void save( final AsyncCallback<AccountEntry[]> callback);
	public void remove( final AsyncCallback<Void> callback);

	public void manageWidgets(boolean canRemove, boolean canEdit);
	

	public void entryDateChanged(Date entryDate);
	public void activityChanged(Integer activty);
	public void confidentialChanged(boolean confidential);
	
	default public boolean isStatusMsgEnabled() {
		return true;
	};
}
