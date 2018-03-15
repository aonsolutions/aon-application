package com.esferalia.aon.gwt.fiscal.client.accounting.wizard;

import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule.IAccountEntryModuleCallback;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule.IContentAttachCallback;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;

public abstract class WizardContentBase<T extends IAccountEntryWrapper> extends ResizeComposite implements RequiresResize, IWizardContent  {

	static FiscalServiceAsync fiscalService;
	private IAccountEntryModuleCallback callback;
	
	public static interface ISelectionCallback {
		void onSuccess();
		void onFailure();
	}
	
	protected static FiscalServiceAsync getFiscalService() {
		if (fiscalService == null) {
			FiscalServiceAsync fiscalServiceRaw = GWT.create(FiscalService.class);
			fiscalService = new FiscalServiceAsyncDecorator(fiscalServiceRaw);
		}
		return fiscalService;
	}
	
	public AonConfiguration getConfiguration() {
		return getCallback().getModule().getConfiguration();
	}
	public IAccountEntryModuleCallback getCallback() {
		return callback;
	}

	public void setCallback(IAccountEntryModuleCallback callback) {
		this.callback = callback;
	}

	public AccountEntry getAccountEntry() {
		return getWrapper().getAccountEntry();
	}
	public void setAccountEntry(AccountEntry entry) {
		getWrapper().setAccountEntry(entry);
	}
	
	@Override
	public void save(final AsyncCallback<AccountEntry[]> callback) {
		getFiscalService().save(getCallback().getCurrentDomainName(), getCallback().getCurrentDomainId(), getWrapper().getAccountEntry()
			, new AsyncCallback<AccountEntry>() {

			@Override
			public void onSuccess(AccountEntry result) {
				getWrapper().setAccountEntry(result);
				AccountEntry[] entries = new AccountEntry[]{result}; 	
				callback.onSuccess(entries);
			}

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

		});
	}

	@Override
	public void remove(final AsyncCallback<Void> callback) {
		// Si el ID del elemento no es null, estamos editando aun asiento
		// existente,
		// por lo que hay borrarlo de BD. En caso contrario, se borrar la
		// pantalla.
		// Es lo mismo que un reset().
		if (getWrapper().getAccountEntry().getId() != null) {
			getFiscalService().deleteAccountEntry(getCallback().getCurrentDomainName(), getCallback().getCurrentDomainId()
					, getWrapper().getAccountEntry().getId(),
					new AsyncCallbackWrapper<Void>(callback) {

						@Override
						public void onSuccess(Void result) {
							callback.onSuccess(result);
						}

						@Override
						public void onFailure(Throwable caught) {
							callback.onFailure(caught);
						}

					});
		} else {
			callback.onSuccess(null);
		}
	}

	@Override
	public boolean isNew() {
		return getMainEntry() == null || getMainEntry().getId() == null;
	}

	@Override
	public boolean isUpdatable() {
		return (getAccountEntry() == null || (getAccountEntry().isPeriodActive()));
	}
	
	@Override
	public boolean isRemovable() {
		return (!isNew() && isUpdatable());
	}
	
	@Override
	public AccountEntry getMainEntry() {
		return getAccountEntry();
	}

	@Override
	public void attach(IContentAttachCallback contentCbk) {
		getCallback().getModule().setWizardContent(this, contentCbk);
	}
	
	@Override
	public IAccountEntryWrapper getEntryWrapper() {
		return getWrapper();
	}
	
	public abstract T getWrapper();
	public abstract void setWrapper(T t);
	
}
