package com.esferalia.aon.gwt.fiscal.client.accounting.wizard;

import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule.IAccountEntryModuleCallback;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule.IContentAttchCallback;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;

public abstract class WizardContentBase extends ResizeComposite implements RequiresResize, IWizardContent, IAccountEntryWrapper {

	static FiscalServiceAsync fiscalService;
	protected AccountEntry ae;
	protected IAccountEntryModuleCallback callback;
	
	public static interface ISelectionCallback {
		void onSucces();
		void onFailure();
	}
	
	private static FiscalServiceAsync getFiscalService() {
		if (fiscalService == null) {
			FiscalServiceAsync fiscalServiceRaw = GWT.create(FiscalService.class);
			fiscalService = new FiscalServiceAsyncDecorator(fiscalServiceRaw);
		}
		return fiscalService;
	}
	private static String getDomainName() {
		return AccountEntryModule.getCurrentDomainName();
	}
	private static int getDomain() {
		return AccountEntryModule.getCurrentDomain();
	}
	
	public IAccountEntryModuleCallback getCallback() {
		return callback;
	}

	public void setCallback(IAccountEntryModuleCallback callback) {
		this.callback = callback;
	}

	@Override
	public IAccountEntryWrapper getAccountEntryWrapper() {
		return this;
	}
	
	@Override
	public AccountEntry getAccountEntry() {
		return this.ae;
	}
	@Override
	public void setAccountEntry(AccountEntry entry) {
		this.ae = entry;
	}
	
	@Override
	public void save(final AsyncCallback<AccountEntry[]> callback) {
		getFiscalService().save(getDomainName(), getDomain(), ae, new AsyncCallback<AccountEntry>() {

			@Override
			public void onSuccess(AccountEntry result) {
				ae = result;
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
		if (ae.getId() != null) {
			getFiscalService().deleteAccountEntry(getDomainName(), getDomain(), ae.getId(),
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
	public void get(Integer id, final AsyncCallback<AccountEntry> callback) {
		getFiscalService().getAccountEntry(getDomainName(), getDomain(), id,
				new AsyncCallbackWrapper<AccountEntry>(callback) {

					@Override
					public void onSuccess(AccountEntry result) {
						ae = result;
						callback.onSuccess(result);
					}

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}
				});
	}

	@Override
	public boolean isNew() {
		return getAccountEntry() == null || getAccountEntry().getId() == null;
	}

	@Override
	public boolean isUpdatable() {
		return (getAccountEntry() == null || (getAccountEntry().isPeriodActive()));
	}

	@Override
	public boolean isDirty() {
//		if (getAccountEntry() == null || getAccountEntry().getId() == null || getAccountEntry().getDetails().size() == 0) {
//			return false;
//		}
		return getAccountEntry().isDirty();
	}

	@Override
	public void attach(IContentAttchCallback contentCbk) {
		callback.attach(this,contentCbk);
	}
	
	@Override
	public abstract AccountEntryType getAccountEntryType();
	
}
