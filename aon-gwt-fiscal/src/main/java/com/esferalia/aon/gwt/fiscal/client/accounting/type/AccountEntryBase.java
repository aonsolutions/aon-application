package com.esferalia.aon.gwt.fiscal.client.accounting.type;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class AccountEntryBase implements IAccountEntryType {

	static FiscalServiceAsync fiscalService;
	
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

	private AccountEntry ae;

	public AccountEntryBase() {
		Date date = ae != null ? ae.getEntryDate() : new Date();
		setAccountEntry(new AccountEntry()
			.setDomain(getDomain())
			.setEntryDate(date)
			.setConfidential(false)
			.setEntryType(getAccountEntryType())
			.setDirty(false));
	}


	@Override
	public abstract AccountEntryType getAccountEntryType();

	@Override
	public AccountEntry getAccountEntry() {
		return this.ae;
	}

	@Override
	public void setAccountEntry(AccountEntry ae) {
		this.ae = ae;
	}

	@Override
	public boolean isPeriodActive() {
		return (ae.getPeriodStatus() == null || ae.getPeriodStatus().isActive());
	}

	@Override
	public LinkedList<AccountEntryDetail> getDetails() {
		return this.ae.getDetails();
	}

	@Override
	public int getSize() {
		int i = 0;
		for (AccountEntryDetail aed : getDetails()) {
			i = i + (aed.isDeleted() ? 0 : 1);
		}
		return i;
	}

	@Override
	public AccountEntryDetail getLast() {
		AccountEntryDetail aed = null;
		if (!getDetails().isEmpty()) {
			for (int i = (getDetails().size() - 1); i >= 0; i--) {
				aed = getDetails().get(i);
				if (!aed.isDeleted()) {
					break;
				}
			}
		}
		return aed;
	}

	@Override
	public boolean isManual() {
		return getAccountEntry().getEntryType().isManual();
	}
	
	@Override
	public boolean isUpdatable() {
		return (isPeriodActive() && isManual());
	}
	
	@Override
	public void save(final AsyncCallback<AccountEntry> callback) {
		getFiscalService().save(getDomainName(), getDomain(), ae, new AsyncCallbackWrapper<AccountEntry>(callback) {

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
	public boolean isNew() {
		return getAccountEntry() == null || getAccountEntry().getId() == null;
	}

	@Override
	public boolean isDirty() {
		if (getAccountEntry().getId() == null && getAccountEntry().getDetails().size() == 0) {
			return false;
		}
		return getAccountEntry().isDirty();
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
}
