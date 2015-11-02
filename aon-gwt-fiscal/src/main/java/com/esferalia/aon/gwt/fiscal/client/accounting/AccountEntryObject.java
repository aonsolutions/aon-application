package com.esferalia.aon.gwt.fiscal.client.accounting;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AccountEntryObject {
	
	private String domainName;
	private int domainId;
	private AccountEntry ae;
	
	public static AccountEntryObject newInstance(String domainName,int domainId) {
		return new AccountEntryObject(domainName,domainId); 
	}
	
	private AccountEntryObject(String domainName,int domainId) {
		this.domainName = domainName;
		this.domainId = domainId;
		
		Date date = ae!=null?ae.getEntryDate():new Date();
		setAccountEntry(new AccountEntry()
			.setDomain(this.domainId)
			.setEntryDate(date)
			.setConfidential(false)
			.setEntryType(AccountEntryType.MANUAL));
	}
	
	private static FiscalServiceAsync getFiscalService() {
		return AccountEntryModule.fiscalService;
	}
	public AccountEntryObject(AccountEntry ae) {
		this.ae = ae;
	}
	
	public AccountEntry getAccountEntry() {
		return this.ae;
	}
	public void setAccountEntry(AccountEntry ae) {
		this.ae = ae;
	}
	public boolean isPeriodActive() {
		return  (ae.getPeriodStatus() == null || ae.getPeriodStatus().isActive() );
	}
	public boolean isManual() {
		return ae.getEntryType().isManual();
	}
	public boolean isUpdatable() {
		return (isPeriodActive() && isManual());
	}

	public LinkedList<AccountEntryDetail> getDetails() {
		return this.ae.getDetails();
	}
	
	public int getSize() {
		int i = 0;
		for (AccountEntryDetail aed : getDetails()) {
			i = i + (aed.isDeleted()?0:1);
		}
		return i;
	}

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
	
	public void save( final AsyncCallback<AccountEntry> callback) {
		getFiscalService().save(this.domainName,this.domainId
				, ae,new AsyncCallbackWrapper<AccountEntry>(callback) {

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

	public void remove( final AsyncCallback<Void> callback) {
		// Si el ID del elemento no es null, estamos editando aun asiento existente, 
		// por lo que hay borrarlo de BD. En caso contrario, se borrar la pantalla.
		// Es lo mismo que un reset().
		if (ae.getId() != null) {
			getFiscalService().deleteAccountEntry(this.domainName,this.domainId
					, ae.getId(),new AsyncCallbackWrapper<Void>(callback) {

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
	public boolean isNew() {
		return getAccountEntry() == null || getAccountEntry().getId() == null;
	}
	
	public void get(Integer id , final AsyncCallback<AccountEntry> callback) {
		getFiscalService().getAccountEntry(this.domainName,this.domainId, id,
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
